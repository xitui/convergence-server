/*
 * Copyright (c) 2019 - Convergence Labs, Inc.
 *
 * This file is part of the Convergence Server, which is released under
 * the terms of the GNU General Public License version 3 (GPLv3). A copy
 * of the GPLv3 should have been provided along with this file, typically
 * located in the "LICENSE" file, which is part of this source code package.
 * Alternatively, see <https://www.gnu.org/licenses/gpl-3.0.html> for the
 * full text of the GPLv3 license, if it was not provided.
 */

package com.convergencelabs.convergence.server.api.rest.domain


import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, Scheduler}
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.server.Directive.{addByNameNullaryApply, addDirectiveApply}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.convergencelabs.convergence.server.api.rest._
import com.convergencelabs.convergence.server.backend.datastore.domain.jwt.CreateOrUpdateJwtAuthKey
import com.convergencelabs.convergence.server.backend.services.domain.jwt.JwtAuthKeyStoreActor._
import com.convergencelabs.convergence.server.backend.services.domain.rest.DomainRestActor
import com.convergencelabs.convergence.server.backend.services.domain.rest.DomainRestActor.DomainRestMessage
import com.convergencelabs.convergence.server.model.DomainId
import com.convergencelabs.convergence.server.security.AuthorizationProfile
import com.convergencelabs.convergence.server.util.{QueryLimit, QueryOffset}

import scala.concurrent.{ExecutionContext, Future}

private[domain] final class DomainKeyService(domainRestActor: ActorRef[DomainRestActor.Message],
                                             scheduler: Scheduler,
                                             executionContext: ExecutionContext,
                                             timeout: Timeout)
  extends AbstractDomainRestService(scheduler, executionContext, timeout) {

  import DomainKeyService._

  def route(authProfile: AuthorizationProfile, domain: DomainId): Route = {
    pathPrefix("jwtKeys") {
      pathEnd {
        get {
          complete(getKeys(domain))
        } ~ post {
          entity(as[CreateKeyData]) { key =>
            complete(createKey(domain, key))
          }
        }
      } ~ path(Segment) { keyId =>
        get {
          complete(getKey(domain, keyId))
        } ~ put {
          entity(as[UpdateInfo]) { key =>
            complete(updateKey(domain, keyId, key))
          }
        } ~ delete {
          complete(deleteKey(domain, keyId))
        }
      }
    }
  }

  private[this] def getKeys(domain: DomainId): Future[RestResponse] = {
    domainRestActor
      .ask[GetJwtAuthKeysResponse](r => DomainRestMessage(domain, GetJwtAuthKeysRequest(QueryOffset(), QueryLimit(), r)))
      .map(_.keys.fold(
        {
          case UnknownError() =>
            InternalServerError
        },
        okResponse(_)
      ))
  }

  private[this] def getKey(domain: DomainId, keyId: String): Future[RestResponse] = {
    domainRestActor
      .ask[GetJwtAuthKeyResponse](r => DomainRestMessage(domain, GetJwtAuthKeyRequest(keyId, r)))
      .map(_.key.fold(
        {
          case JwtAuthKeyNotFoundError() =>
            keyNotFound()
          case UnknownError() =>
            InternalServerError
        },
        okResponse(_)
      ))
  }

  private[this] def createKey(domain: DomainId, data: CreateKeyData): Future[RestResponse] = {
    val CreateKeyData(id, description, key, enabled) = data
    val create = CreateOrUpdateJwtAuthKey(id, description, key, enabled)
    domainRestActor
      .ask[CreateJwtAuthKeyResponse](
        r => DomainRestMessage(domain, CreateJwtAuthKeyRequest(create, r)))
      .map(_.response.fold(
        {
          case JwtAuthKeyExistsError() =>
            duplicateResponse("id")
          case UnknownError() =>
            InternalServerError
        },
        _ => CreatedResponse
      ))
  }

  private[this] def updateKey(domain: DomainId, keyId: String, update: UpdateInfo): Future[RestResponse] = {
    val UpdateInfo(description, key, enabled) = update
    val info = CreateOrUpdateJwtAuthKey(keyId, description, key, enabled)
    domainRestActor
      .ask[UpdateJwtAuthKeyResponse](
        r => DomainRestMessage(domain, UpdateJwtAuthKeyRequest(info, r)))
      .map(_.response.fold(
        {
          case JwtAuthKeyNotFoundError() =>
            keyNotFound()
          case UnknownError() =>
            InternalServerError
        },
        _ => OkResponse
      ))
  }

  private[this] def deleteKey(domain: DomainId, keyId: String): Future[RestResponse] = {
    domainRestActor
      .ask[DeleteJwtAuthKeyResponse](
        r => DomainRestMessage(domain, DeleteJwtAuthKeyRequest(keyId, r)))
      .map(_.response.fold(
        {
          case JwtAuthKeyNotFoundError() =>
            keyNotFound()
          case UnknownError() =>
            InternalServerError
        },
        _ => DeletedResponse
      ))
  }

  private def keyNotFound(): RestResponse = notFoundResponse("The specified key does not exist.")
}

object DomainKeyService {

  case class UpdateInfo(description: String, key: String, enabled: Boolean)

  case class CreateKeyData(id: String, description: String, key: String, enabled: Boolean)

}
