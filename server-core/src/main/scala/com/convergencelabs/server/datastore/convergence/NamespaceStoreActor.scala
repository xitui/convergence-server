package com.convergencelabs.server.datastore.convergence

import java.util.UUID

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.convergencelabs.server.datastore.EntityNotFoundException
import com.convergencelabs.server.datastore.StoreActor
import com.convergencelabs.server.util.ExceptionUtils
import com.convergencelabs.server.db.DatabaseProvider

import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.util.Timeout
import com.convergencelabs.server.domain.Namespace
import com.convergencelabs.server.security.AuthorizationProfile
import com.convergencelabs.server.security.Permissions
import com.convergencelabs.server.domain.NamespaceUpdates

object NamespaceStoreActor {
  val RelativePath = "NamespaceStoreActor"

  def props(dbProvider: DatabaseProvider): Props = Props(new NamespaceStoreActor(dbProvider))

  case class CreateNamespace(requestor: String, namespaceId: String, displayName: String)
  case class UpdateNamespace(requestor: String, namespaceId: String, displayName: String)
  case class DeleteNamespace(requestor: String, namespaceId: String)
  case class GetAccessibleNamespaces(requestor: AuthorizationProfile, filter: Option[String], offset: Option[Int], limit: Option[Int])
  case class GetNamespace(namespaceId: String)
}

class NamespaceStoreActor private[datastore] (
  private[this] val dbProvider: DatabaseProvider)
  extends StoreActor with ActorLogging {

  import NamespaceStoreActor._
  import akka.pattern.ask

  private[this] val namespaceStore = new NamespaceStore(dbProvider)

  def receive: Receive = {
    case createRequest: CreateNamespace =>
      createNamespace(createRequest)
    case deleteRequest: DeleteNamespace =>
      deleteNamespace(deleteRequest)
    case updateRequest: UpdateNamespace =>
      updateNamespace(updateRequest)
    case getRequest: GetNamespace =>
      getNamespace(getRequest)
    case accessibleRequest: GetAccessibleNamespaces =>
      getAccessibleNamespaces(accessibleRequest)
    case message: Any =>
      unhandled(message)
  }

  def createNamespace(createRequest: CreateNamespace): Unit = {
    val CreateNamespace(requestor, namespaceId, displayName) = createRequest
    reply(namespaceStore.createNamespace(namespaceId, displayName, false))
  }

  def getNamespace(getRequest: GetNamespace): Unit = {
    val GetNamespace(namespaceId) = getRequest
    reply(namespaceStore.getNamespace(namespaceId))
  }

  def updateNamespace(request: UpdateNamespace): Unit = {
    val UpdateNamespace(requestor, namespaceId, displayName) = request
    reply(namespaceStore.updateNamespace(NamespaceUpdates(namespaceId, displayName)))
  }

  def deleteNamespace(deleteRequest: DeleteNamespace): Unit = {
    val DeleteNamespace(requestor, namespaceId) = deleteRequest
    log.debug("Delete Namespace: " + namespaceId)
    reply(namespaceStore.deleteNamespace(namespaceId))
  }

  def getAccessibleNamespaces(getRequest: GetAccessibleNamespaces): Unit = {
    val GetAccessibleNamespaces(authProfile, filter, offset, limit) = getRequest
    if (authProfile.hasGlobalPermission(Permissions.Global.ManageDomains)) {
      reply(namespaceStore.getAllNamespacesAndDomains())
    } else {
      reply(namespaceStore
        .getAccessibleNamespaces(authProfile.username)
        .flatMap(namespaces => namespaceStore.getNamespaceAndDomains(namespaces.map(_.id).toSet)))
    }
  }
}