package com.convergencelabs.server.frontend.rest

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.convergencelabs.server.datastore.DomainStoreActor.CreateDomainRequest
import com.convergencelabs.server.datastore.DomainStoreActor.UpdateDomainRequest
import com.convergencelabs.server.datastore.DomainStoreActor.DeleteDomainRequest
import com.convergencelabs.server.datastore.DomainStoreActor.GetDomainRequest
import com.convergencelabs.server.datastore.DomainStoreActor.ListDomainsRequest
import akka.http.scaladsl.server.directives.FutureDirectives.onSuccess
import com.convergencelabs.server.domain.DomainFqn

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive.addByNameNullaryApply
import akka.http.scaladsl.server.Directive.addDirectiveApply
import akka.http.scaladsl.server.Directives._
import akka.pattern._
import akka.util.Timeout
import com.convergencelabs.server.datastore.CreateResult
import com.convergencelabs.server.datastore.CreateSuccess
import com.convergencelabs.server.datastore.DuplicateValue
import com.convergencelabs.server.datastore.DeleteResult
import com.convergencelabs.server.datastore.DeleteSuccess
import com.convergencelabs.server.datastore.NotFound
import com.convergencelabs.server.domain.Domain
import com.convergencelabs.server.datastore.InvalidValue
import com.convergencelabs.server.domain.RestAuthnorizationActor.DomainAuthorization
import scala.util.Failure
import com.convergencelabs.server.domain.RestAuthnorizationActor.AuthorizationGranted
import scala.util.Success
import com.convergencelabs.server.domain.RestAuthnorizationActor.AuthorizationDenied
import com.convergencelabs.server.domain.RestAuthnorizationActor.AuthorizationResult
import com.convergencelabs.server.datastore.UpdateResult
import com.convergencelabs.server.datastore.UpdateSuccess
import com.convergencelabs.server.datastore.ConvergenceUserManagerActor.GetConvergenceUserProfile
import com.convergencelabs.server.User

case class CovergenceUserProfile(userId: String, username: String, email: String, firstName: String, lastName: String)
case class UserProfileResponse(profile: CovergenceUserProfile) extends AbstractSuccessResponse

class ProfileService(
  private[this] val executionContext: ExecutionContext,
  private[this] val convergenceUserActor: ActorRef,
  private[this] val defaultTimeout: Timeout)
    extends JsonSupport {

  implicit val ec = executionContext
  implicit val t = defaultTimeout

  val route = { username: String =>
    pathPrefix("profile") {
      pathEnd {
        get {
          complete(getProfile(username))
        }
      }
    }
  }

  def getProfile(userId: String): Future[RestResponse] = {
    (convergenceUserActor ? GetConvergenceUserProfile(userId)).mapTo[Option[User]].map {
      case Some(User(
        username: String,
        email: String,
        firstName: String,
        lastName: String)) =>
        (StatusCodes.OK, UserProfileResponse(CovergenceUserProfile(userId, username, email, firstName, lastName)))
      case None =>
        NotFoundError
    }
  }
}
