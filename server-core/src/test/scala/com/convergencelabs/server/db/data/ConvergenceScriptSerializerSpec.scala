package com.convergencelabs.server.db.data

import org.scalatest.Matchers
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.OptionValues._
import org.scalatest.WordSpecLike
import java.time.Instant

class ConvergenceScriptSerializerSpec extends WordSpecLike with Matchers {

  "A ConvergenceScriptSerializerSpec" when {
    "deserializing a script" must {
      "corrrectly parse" in {
        val serializer = new ConvergenceScriptSerializer()
        val in = getClass.getResourceAsStream("/com/convergencelabs/server/db/data/convergence-with-domain.yaml")
        val value = serializer.deserialize(in).success.value

        val ConvergenceScript(users, domains) = value

        users.value shouldBe List(
          CreateConvergenceUser("imported", SetPassword("plaintext", "password"), "imported@example.com", Some("Imported"), Some("User"), Some("Imported User")))

        val CreateDomain(
          namespace,
          domainId,
          displayName,
          status,
          statusMessage,
          owner,
          dataImport) = domains.value(0)

        namespace shouldBe "imported"
        domainId shouldBe "code-editor"
        displayName shouldBe "Code Editor"
        status shouldBe "online"
        statusMessage shouldBe ""
        owner shouldBe "imported"

        println(dataImport.value)

        val DomainScript(config, jwtAuthKeys, domainUsers, collections, models) = dataImport.value
        config shouldBe SetDomainConfig(true, CreateJwtKeyPair("Public Key", "Private Key"))

        jwtAuthKeys.value shouldBe List(CreateJwtAuthKey("test-key", Some("a test key"), Instant.parse("2016-11-16T17:49:15.233Z"), "Public Key", true))

        domainUsers.value shouldBe List(
          CreateDomainUser("normal", "test1", Some("Test"), Some("One"), Some("Test One"), Some("test1@example.com"), Some(SetPassword("plaintext", "somePassword"))),
          CreateDomainUser("normal", "test2", Some("Test"), Some("Two"), Some("Test Two"), Some("test2@example.com"), Some(SetPassword("hash", "someHash"))))

        collections.value shouldBe List(CreateCollection("colleciton1", "Collection 1", false))

        models.value shouldBe List(
          CreateModel(
            "collection1",
            "someId",
            2L,
            Instant.parse("2016-11-16T17:49:15.233Z"),
            Instant.parse("2016-11-16T17:49:15.233Z"),
            CreateObjectValue(
              "vid1",
              Map("myString" -> CreateStringValue("vid2", "my string"))),
            List(
              CreateModelOperation(1L, Instant.parse("2016-11-16T17:49:15.233Z"), "test1", "84hf", CreateStringInsertOperation("vid2", false, 0, "!")),
              CreateModelOperation(2L, Instant.parse("2016-11-16T17:49:15.233Z"), "test1", "84hf", CreateStringInsertOperation("vid2", false, 1, "@"))),
            List(
              CreateModelSnapshot(
                1L,
                Instant.parse("2016-11-16T17:49:15.233Z"),
                CreateObjectValue(
                  "vid1",
                  Map("myString" -> CreateStringValue("vid2", "my string")))))))

      }
    }
  }
}