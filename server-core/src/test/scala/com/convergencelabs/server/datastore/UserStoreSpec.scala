package com.convergencelabs.server.datastore

import java.time.Duration
import java.time.Instant

import org.scalatest.Finders
import org.scalatest.Matchers
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.WordSpecLike

import com.convergencelabs.server.datastore.domain.PersistenceStoreSpec
import com.convergencelabs.server.db.schema.DeltaCategory

class UserStoreSpec
    extends PersistenceStoreSpec[UserStore](DeltaCategory.Convergence)
    with WordSpecLike
    with Matchers {

  val username = "test"
  val displayName = "test user"
  val password = "password"
  
  val DummyToken = "myToken"
  val TestUser = User(username, "test@convergence.com", username, username, displayName)
  val tokenDurationMinutes = 5
  val tokenDuration = Duration.ofSeconds(5) // scalastyle:ignore magic.number

  def createStore(dbProvider: DatabaseProvider): UserStore = new UserStore(dbProvider, tokenDuration)

  "A UserStore" when {
    "querying a user" must {
      "correctly retreive user by username" in withPersistenceStore { store =>
        store.createUser(TestUser, password).get
        val queried = store.getUserByUsername(username)
        queried.success.get.value shouldBe TestUser
      }
    }

    "checking whether a user exists" must {
      "return true if the user exist" in withPersistenceStore { store =>
        store.createUser(TestUser, password).get
        store.userExists(TestUser.username).get shouldBe true
      }

      "return false if the user does not exist" in withPersistenceStore { store =>
        store.userExists("DoesNotExist").get shouldBe false
      }
    }

    "setting a users password" must {
      "correctly set the password" in withPersistenceStore { store =>
        val password = "newPasswordToSet"
        store.createUser(TestUser, password).get
        store.setUserPassword(username, password).success
        store.validateCredentials(username, password).success.get shouldBe defined
      }

      "return a failure if user does not exist" in withPersistenceStore { store =>
        store.setUserPassword("DoesNotExist", "doesn't matter").failed.get shouldBe a[IllegalArgumentException]
      }
    }

    "validating credentials" must {
      "return true and a username for a valid usename and password" in withPersistenceStore { store =>
        store.createUser(TestUser, password).get
        store.validateCredentials(username, password).success.get shouldBe defined
      }

      "return false and None for an valid username and invalid password" in withPersistenceStore { store =>
        store.createUser(TestUser, password).get
        store.validateCredentials(username, "wrong").success.value shouldBe None
      }

      "return false and None for an invalid username" in withPersistenceStore { store =>
        store.createUser(TestUser, password).get
        store.validateCredentials("no one", "p").success.value shouldBe None
      }
    }

    "validating tokens" must {
      "return true and a uid for a valid token" in withPersistenceStore { store =>
        store.createUser(TestUser, password).get
        store.createToken(username, DummyToken, Instant.now().plusSeconds(100)) // scalastyle:ignore magic.number
        store.validateToken(DummyToken).success.value shouldBe Some(username)
      }

      "return false and None for an expired token" in withPersistenceStore { store =>
        store.createUser(TestUser, password).get
        val expireTime = Instant.now().minusSeconds(1)
        store.createToken(username, DummyToken, expireTime)
        store.validateToken(DummyToken).success.value shouldBe None
      }

      "return false and None for an invalid token" in withPersistenceStore { store =>
        store.validateToken(DummyToken).success.value shouldBe None
      }
    }
  }
}