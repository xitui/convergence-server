package com.convergencelabs.server.api.rest.domain

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.server.Directive.{addByNameNullaryApply, addDirectiveApply}
import akka.http.scaladsl.server.Directives.{_enhanceRouteWithConcatenation, _string2NR, as, complete, delete, entity, get, parameters, path, pathEnd, pathPrefix, post, put}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.convergencelabs.server.api.rest._
import com.convergencelabs.server.datastore.domain.{ChatInfo, ChatMembership, ChatType}
import com.convergencelabs.server.domain.{DomainId, DomainUserId}
import com.convergencelabs.server.domain.chat.ChatLookupActor.{CreateChatRequest, CreateChatResponse, FindChatInfo, GetChatInfo}
import com.convergencelabs.server.domain.chat.ChatMessages.{ChatAlreadyExistsException, RemoveChatlRequest, SetChatNameRequest, SetChatTopicRequest}
import com.convergencelabs.server.domain.rest.RestDomainActor.DomainRestMessage
import com.convergencelabs.server.security.AuthorizationProfile
import grizzled.slf4j.Logging

import scala.concurrent.{ExecutionContext, Future}

object DomainChatService {
  case class ChatInfoData(chatId: String, chatType: String, membership: String, name: String, topic: String, members: Set[String])
  case class CreateChatData(chatId: String, chatType: String, membership: String, name: String, topic: String, members: Set[String])
  case class SetNameData(name: String)
  case class SetTopicData(topic: String)
}

class DomainChatService(
  private[this] val executionContext: ExecutionContext,
  private[this] val timeout:          Timeout,
  private[this] val domainRestActor:  ActorRef,
  private[this] val chatSharding:     ActorRef)
  extends DomainRestService(executionContext, timeout) with Logging {

  import DomainChatService._
  import akka.http.scaladsl.server.Directives.Segment
  import akka.pattern.ask

  def route(authProfile: AuthorizationProfile, domain: DomainId): Route = {
    pathPrefix("chats") {
      pathEnd {
        get {
          parameters("filter".?, "offset".as[Int].?, "limit".as[Int].?) { (filter, offset, limit) =>
            complete(getChats(domain, filter, offset, limit))
          }
        } ~ post {
          entity(as[CreateChatData]) { chatData =>
            complete(createChat(authProfile, domain, chatData))
          }
        }
      } ~ pathPrefix(Segment) { chatId =>
        pathEnd {
          get {
            complete(getChat(domain, chatId))
          } ~ delete {
            complete(deleteChat(authProfile, domain, chatId))
          }
        } ~ (path("name") & put) {
          entity(as[SetNameData]) { data =>
            complete(setName(authProfile, domain, chatId, data))
          }
        } ~ (path("topic") & put) {
          entity(as[SetTopicData]) { data =>
            complete(setTopic(authProfile, domain, chatId, data))
          }
        }
      }
    }
  }

  def getChats(domain: DomainId, filter: Option[String], offset: Option[Int], limit: Option[Int]): Future[RestResponse] = {
    val message = DomainRestMessage(domain, FindChatInfo(filter, offset, limit))
    (domainRestActor ? message).mapTo[List[ChatInfo]] map { chats =>
      okResponse(chats.map { chat =>
        val ChatInfo(id, chatType, created, membership, name, topic, lastEventNumber, lastEventTime, members) = chat
        ChatInfoData(
          id,
          chatType.toString.toLowerCase,
          membership.toString().toLowerCase(),
          name,
          topic,
          members.map(m => m.userId.username))
      })
    }
  }

  def getChat(domain: DomainId, chatId: String): Future[RestResponse] = {
    val message = DomainRestMessage(domain, GetChatInfo(chatId))
    (domainRestActor ? message).mapTo[ChatInfo] map { chat =>
      val ChatInfo(id, chatType, created, membership, name, topic, lastEventNumber, lastEventTime, members) = chat
      okResponse(ChatInfoData(
        id,
        chatType.toString.toLowerCase,
        membership.toString.toLowerCase(),
        name,
        topic,
        members.map(m => m.userId.username)))
    }
  }

  def createChat(authProfile: AuthorizationProfile, domain: DomainId, chatData: CreateChatData): Future[RestResponse] = {
    val CreateChatData(chatId, chatType, membership, name, topic, members) = chatData
    val request = CreateChatRequest(
      Some(chatId),
      DomainUserId.convergence(authProfile.username),
      ChatType.parse(chatType),
      ChatMembership.parse(membership),
      Some(name),
      Some(topic),
      members.map(DomainUserId.normal).toSet)
    val message = DomainRestMessage(domain, request)

    domainRestActor.ask(message)
      .mapTo[CreateChatResponse]
      .map(_ => CreatedResponse)
      .recover {
        case ChatAlreadyExistsException(chatId) =>
          duplicateResponse("chatId")

        case cause =>
          logger.error("could not create chat: " + message, cause)
          unknownErrorResponse(Some("An unexcpeected error occurred creating the chat"))
      }
  }

  def deleteChat(authProfile: AuthorizationProfile, domain: DomainId, chatId: String): Future[RestResponse] = {
    val message = RemoveChatlRequest(domain, chatId, DomainUserId.convergence(authProfile.username))
    (chatSharding ? message).mapTo[Unit] map { chats =>
      okResponse(chats)
    }
  }

  def setName(authProfile: AuthorizationProfile, domain: DomainId, chatId: String, data: SetNameData): Future[RestResponse] = {
    val SetNameData(name) = data
    val userId = DomainUserId.convergence(authProfile.username)
    val message = SetChatNameRequest(domain, chatId, userId, name)
    (chatSharding ? message).mapTo[Unit] map (_ => OkResponse)
  }

  def setTopic(authProfile: AuthorizationProfile, domain: DomainId, chatId: String, data: SetTopicData): Future[RestResponse] = {
    val SetTopicData(topic) = data
    val userId = DomainUserId.convergence(authProfile.username)
    val message = SetChatTopicRequest(domain, chatId, userId, topic)
    (chatSharding ? message).mapTo[Unit] map (_ => OkResponse)
  }
}
