package com.convergencelabs.server.frontend.realtime

import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JNumber
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JValue
import com.convergencelabs.server.domain.model.OpenModelMetaData
import com.convergencelabs.server.ProtocolConfiguration
import com.convergencelabs.server.domain.model.data.ObjectValue
import com.convergencelabs.server.domain.PresenceServiceActor.UserPresence
import com.convergencelabs.server.domain.model.ModelMetaData
import com.convergencelabs.server.domain.model.ModelOperation

// scalastyle:off number.of.types

///////////////////////////////////////////////////////////////////////////////
// Base Classes
///////////////////////////////////////////////////////////////////////////////
sealed trait ProtocolMessage

sealed trait IncomingProtocolMessage extends ProtocolMessage
sealed trait IncomingProtocolNormalMessage extends IncomingProtocolMessage
sealed trait IncomingProtocolRequestMessage extends IncomingProtocolMessage
sealed trait IncomingProtocolResponseMessage extends IncomingProtocolMessage

sealed trait OutgoingProtocolMessage extends ProtocolMessage
sealed trait OutgoingProtocolNormalMessage extends OutgoingProtocolMessage
sealed trait OutgoingProtocolRequestMessage extends OutgoingProtocolMessage
sealed trait OutgoingProtocolResponseMessage extends OutgoingProtocolMessage

case class PingMessage() extends ProtocolMessage
case class PongMessage() extends ProtocolMessage

///////////////////////////////////////////////////////////////////////////////
// Client Messages
///////////////////////////////////////////////////////////////////////////////

case class ErrorMessage(c: String, m: String, d: Map[String, Any])
  extends OutgoingProtocolResponseMessage
  with OutgoingProtocolNormalMessage
  with IncomingProtocolNormalMessage
  with IncomingProtocolResponseMessage

// Handshaking
case class HandshakeRequestMessage(r: scala.Boolean, k: Option[String]) extends IncomingProtocolRequestMessage

case class HandshakeResponseMessage(
  s: scala.Boolean, // success
  e: Option[ErrorData], // error
  r: Option[scala.Boolean], // retryOk
  c: Option[ProtocolConfigData]) extends OutgoingProtocolResponseMessage

case class ProtocolConfigData(
  h: scala.Boolean // heartbeat enabled
  )

case class ErrorData(
  c: String, // code
  d: String // details
  )

// Authentication Messages
sealed trait AuthenticationRequestMessage extends IncomingProtocolRequestMessage
case class PasswordAuthRequestMessage(u: String, p: String) extends AuthenticationRequestMessage
case class TokenAuthRequestMessage(k: String) extends AuthenticationRequestMessage
case class ReconnectTokenAuthRequestMessage(r: String) extends AuthenticationRequestMessage
case class AnonymousAuthRequestMessage(d: Option[String]) extends AuthenticationRequestMessage

case class AuthenticationResponseMessage(s: Boolean, n: Option[String], e: Option[String], p: Option[Map[String, Any]]) extends OutgoingProtocolResponseMessage

///////////////////////////////////////////////////////////////////////////////
// Model Messages
///////////////////////////////////////////////////////////////////////////////

sealed trait IncomingModelNormalMessage extends IncomingProtocolNormalMessage
case class OperationSubmissionMessage(r: String, s: Long, v: Long, o: OperationData) extends IncomingModelNormalMessage

sealed trait IncomingModelRequestMessage extends IncomingProtocolRequestMessage
case class OpenRealtimeModelRequestMessage(m: Option[String], a: Option[Integer]) extends IncomingModelRequestMessage
case class CloseRealtimeModelRequestMessage(r: String) extends IncomingModelRequestMessage
case class CreateRealtimeModelRequestMessage(
  c: String,
  m: Option[String],
  d: ObjectValue,
  v: Option[Boolean],
  w: Option[ModelPermissionsData],
  u: Option[Map[String, ModelPermissionsData]]) extends IncomingModelRequestMessage
case class DeleteRealtimeModelRequestMessage(m: String) extends IncomingModelRequestMessage

case class GetModelPermissionsRequestMessage(m: String) extends IncomingModelRequestMessage
case class SetModelPermissionsRequestMessage(
  m: String,
  s: Option[Boolean],
  w: Option[ModelPermissionsData],
  a: Boolean,
  u: Map[String, Option[ModelPermissionsData]]) extends IncomingModelRequestMessage

case class ModelsQueryRequestMessage(q: String) extends IncomingModelRequestMessage

case class AutoCreateModelConfigResponseMessage(c: String, d: Option[ObjectValue], v: Option[Boolean], w: Option[ModelPermissionsData], u: Option[Map[String, ModelPermissionsData]], e: Option[Boolean]) extends IncomingProtocolResponseMessage

case class PublishReferenceMessage(r: String, d: Option[String], k: String, c: Int, v: Option[List[Any]], s: Option[Long]) extends IncomingModelNormalMessage
case class UnpublishReferenceMessage(r: String, d: Option[String], k: String) extends IncomingModelNormalMessage
case class SetReferenceMessage(r: String, d: Option[String], k: String, c: Int, v: List[Any], s: Long) extends IncomingModelNormalMessage
case class ClearReferenceMessage(r: String, d: Option[String], k: String) extends IncomingModelNormalMessage

// Outgoing Model Messages
case class OpenRealtimeModelResponseMessage(r: String, mi: String, ci: String, p: String, v: Long, c: Long, m: Long, d: OpenModelData, a: ModelPermissionsData) extends OutgoingProtocolResponseMessage
case class OpenModelData(d: ObjectValue, s: Set[String], r: Set[ReferenceData])
case class ReferenceData(s: String, d: Option[String], k: String, c: Int, v: List[Any])

case class CloseRealTimeModelSuccessMessage() extends OutgoingProtocolResponseMessage
case class CreateRealtimeModelSuccessMessage(m: String) extends OutgoingProtocolResponseMessage
case class DeleteRealtimeModelSuccessMessage() extends OutgoingProtocolResponseMessage

case class SetModelPermissionsResponseMessage() extends OutgoingProtocolResponseMessage
case class GetModelPermissionsResponseMessage(o: Boolean, w: ModelPermissionsData, u: Map[String, ModelPermissionsData]) extends OutgoingProtocolResponseMessage
case class ModelPermissionsChangedMessage(r: String, p: ModelPermissionsData) extends OutgoingProtocolNormalMessage

case class ModelsQueryResponseMessage(r: List[ModelResult]) extends OutgoingProtocolResponseMessage
case class ModelResult(l: String, m: String, c: Long, d: Long, v: Long, a: JValue)

case class OperationAcknowledgementMessage(r: String, s: Long, v: Long, p: Long) extends OutgoingProtocolNormalMessage
case class RemoteOperationMessage(r: String, s: String, v: Long, p: Long, o: OperationData) extends OutgoingProtocolNormalMessage

case class RemoteClientClosedMessage(r: String, s: String) extends OutgoingProtocolNormalMessage
case class RemoteClientOpenedMessage(r: String, s: String) extends OutgoingProtocolNormalMessage
case class ModelForceCloseMessage(r: String, s: String) extends OutgoingProtocolNormalMessage

case class AutoCreateModelConfigRequestMessage(a: Integer) extends OutgoingProtocolRequestMessage

case class RemoteReferencePublishedMessage(r: String, s: String, d: Option[String], k: String, c: Int, v: Option[List[Any]]) extends OutgoingProtocolNormalMessage
case class RemoteReferenceUnpublishedMessage(r: String, s: String, d: Option[String], k: String) extends OutgoingProtocolNormalMessage
case class RemoteReferenceSetMessage(r: String, s: String, d: Option[String], k: String, c: Int, v: List[Any]) extends OutgoingProtocolNormalMessage
case class RemoteReferenceClearedMessage(r: String, s: String, d: Option[String], k: String) extends OutgoingProtocolNormalMessage

case class ModelPermissionsData(r: Boolean, w: Boolean, d: Boolean, m: Boolean)

///////////////////////////////////////////////////////////////////////////////
// Historical Model Messages
///////////////////////////////////////////////////////////////////////////////
sealed trait IncomingHistoricalModelRequestMessage extends IncomingProtocolRequestMessage
case class HistoricalDataRequestMessage(m: String) extends IncomingHistoricalModelRequestMessage
case class HistoricalOperationRequestMessage(m: String, f: Long, l: Long) extends IncomingHistoricalModelRequestMessage

case class HistoricalDataResponseMessage(i: String, d: ObjectValue, v: Long, c: Long, m: Long) extends OutgoingProtocolResponseMessage
case class HistoricalOperationsResponseMessage(o: List[ModelOperationData]) extends OutgoingProtocolResponseMessage

///////////////////////////////////////////////////////////////////////////////
// Identity Messages
///////////////////////////////////////////////////////////////////////////////

sealed trait IncomingIdentityMessage
case class UserLookUpMessage(f: Int, v: List[String]) extends IncomingProtocolRequestMessage with IncomingIdentityMessage
case class UserSearchMessage(f: List[Int], v: String, o: Option[Int], l: Option[Int], r: Option[Int], s: Option[Int])
  extends IncomingProtocolRequestMessage with IncomingIdentityMessage

case class UserListMessage(u: List[DomainUserData]) extends OutgoingProtocolResponseMessage
case class DomainUserData(t: String, n: String, f: Option[String], l: Option[String], d: Option[String], e: Option[String])

case class UserGroupsRequestMessage(i: Option[List[String]]) extends IncomingProtocolRequestMessage with IncomingIdentityMessage
case class UserGroupsResponseMessage(g: List[UserGroupData]) extends OutgoingProtocolResponseMessage

case class UserGroupsForUsersRequestMessage(u: List[String]) extends IncomingProtocolRequestMessage with IncomingIdentityMessage
case class UserGroupsForUsersResponseMessage(g: Map[String, Set[String]]) extends OutgoingProtocolResponseMessage

case class UserGroupData(i: String, d: String, m: Set[String])

///////////////////////////////////////////////////////////////////////////////
// Activity Messages
///////////////////////////////////////////////////////////////////////////////

sealed trait IncomingActivityMessage
sealed trait IncomingActivityRequestMessage extends IncomingActivityMessage with IncomingProtocolRequestMessage
case class ActivityParticipantsRequestMessage(i: String) extends IncomingActivityRequestMessage
case class ActivityJoinMessage(i: String, s: Map[String, Any]) extends IncomingActivityRequestMessage

sealed trait IncomingActivityNormalMessage extends IncomingActivityMessage
case class ActivityLeaveMessage(i: String) extends IncomingProtocolNormalMessage with IncomingActivityNormalMessage
case class ActivitySetStateMessage(i: String, v: Map[String, Any]) extends IncomingProtocolNormalMessage with IncomingActivityNormalMessage
case class ActivityRemoveStateMessage(i: String, k: List[String]) extends IncomingProtocolNormalMessage with IncomingActivityNormalMessage
case class ActivityClearStateMessage(i: String) extends IncomingProtocolNormalMessage with IncomingActivityNormalMessage

case class ActivityJoinResponseMessage(s: Map[String, Map[String, Any]]) extends OutgoingProtocolResponseMessage
case class ActivityParticipantsResponseMessage(s: Map[String, Map[String, Any]]) extends OutgoingProtocolResponseMessage

case class ActivitySessionJoinedMessage(i: String, s: String, v: Map[String, Any]) extends OutgoingProtocolNormalMessage
case class ActivitySessionLeftMessage(i: String, s: String) extends OutgoingProtocolNormalMessage

case class ActivityRemoteStateSetMessage(i: String, s: String, v: Map[String, Any]) extends OutgoingProtocolNormalMessage
case class ActivityRemoteStateRemovedMessage(i: String, s: String, k: List[String]) extends OutgoingProtocolNormalMessage
case class ActivityRemoteStateClearedMessage(i: String, s: String) extends OutgoingProtocolNormalMessage

sealed trait IncomingPresenceMessage
sealed trait IncomingPresenceRequestMessage extends IncomingPresenceMessage with IncomingProtocolRequestMessage
case class PresenceRequestMessage(u: List[String]) extends IncomingPresenceRequestMessage
case class SubscribePresenceRequestMessage(u: List[String]) extends IncomingPresenceRequestMessage

case class PresenceResponseMessage(p: List[UserPresence]) extends OutgoingProtocolResponseMessage
case class SubscribePresenceResponseMessage(p: List[UserPresence]) extends OutgoingProtocolResponseMessage

sealed trait IncomingPresenceNormalMessage extends IncomingPresenceMessage with IncomingProtocolNormalMessage
case class PresenceSetStateMessage(s: Map[String, Any]) extends IncomingPresenceNormalMessage
case class PresenceRemoveStateMessage(k: List[String]) extends IncomingPresenceNormalMessage
case class PresenceClearStateMessage() extends IncomingPresenceNormalMessage
case class UnsubscribePresenceMessage(u: String) extends IncomingPresenceNormalMessage

case class PresenceStateSetMessage(u: String, s: Map[String, Any]) extends OutgoingProtocolNormalMessage
case class PresenceStateRemovedMessage(u: String, k: List[String]) extends OutgoingProtocolNormalMessage
case class PresenceStateClearedMessage(u: String) extends OutgoingProtocolNormalMessage
case class PresenceAvailabilityChangedMessage(u: String, a: Boolean) extends OutgoingProtocolNormalMessage

///////////////////////////////////////////////////////////////////////////////
// Chat Messages
///////////////////////////////////////////////////////////////////////////////

sealed trait IncomingChatMessage
sealed trait IncomingChatRequestMessage extends IncomingChatMessage with IncomingProtocolRequestMessage
sealed trait IncomingChatNormalMessage extends IncomingChatMessage with IncomingProtocolNormalMessage

// Messages
case class PublishChatRequestMessage(i: String, m: String) extends IncomingChatRequestMessage
case class PublishChatResponseMessage() extends OutgoingProtocolResponseMessage

case class RemoteChatMessageMessage(i: String, e: Long, p: Long, s: String, m: String) extends OutgoingProtocolNormalMessage
    
// Create
case class CreateChatChannelRequestMessage(i: Option[String], e: String, p: String, n: Option[String], c: Option[String],
  m: Option[Set[String]]) extends IncomingChatRequestMessage
case class CreateChatChannelResponseMessage(i: String) extends OutgoingProtocolResponseMessage

// Remove
case class RemoveChatChannelRequestMessage(i: String) extends IncomingChatRequestMessage
case class RemoveChatChannelResponseMessage() extends OutgoingProtocolResponseMessage
case class ChatChannelRemovedMessage(i: String) extends OutgoingProtocolNormalMessage

// Join / Add
case class JoinChatChannelRequestMessage(i: String) extends IncomingChatRequestMessage
case class JoinChatChannelResponseMessage(c: ChatChannelInfoData) extends OutgoingProtocolResponseMessage
case class UserJoinedChatChannelMessage(i: String, n: Long, p: Long, u: String) extends OutgoingProtocolNormalMessage

case class AddUserToChatChannelRequestMessage(i: String, u: String) extends IncomingChatRequestMessage
case class AddUserToChatChannelResponseMessage() extends OutgoingProtocolResponseMessage
case class UserAddedToChatChannelMessage(i: String, n: Long, p: Long, u: String, b: String) extends OutgoingProtocolNormalMessage

case class ChatChannelJoinedMessage(i: String) extends OutgoingProtocolNormalMessage

// Leave / Remove
case class LeaveChatChannelRequestMessage(i: String) extends IncomingChatRequestMessage
case class LeaveChatChannelResponseMessage() extends OutgoingProtocolResponseMessage
case class UserLeftChatChannelMessage(i: String, n: Long, p: Long, u: String) extends OutgoingProtocolNormalMessage

case class RemoveUserFromChatChannelRequestMessage(i: String, u: String) extends IncomingChatRequestMessage
case class RemoveUserFromChatChannelResponseMessage() extends OutgoingProtocolResponseMessage
case class UserRemovedFromChatChannelMessage(i: String, n: Long, p: Long, u: String, b: String) extends OutgoingProtocolNormalMessage

case class ChatChannelLeftMessage(i: String) extends OutgoingProtocolNormalMessage

// Set Name
case class SetChatChannelNameRequestMessage(i: String, n: String) extends IncomingChatRequestMessage
case class SetChatChannelNameResponseMessage() extends OutgoingProtocolResponseMessage
case class ChatChannelNameSetMessage(i: String, e: Long, p: Long, u: String, n: String) extends OutgoingProtocolNormalMessage

// Set Topic
case class SetChatChannelTopicRequestMessage(i: String, c: String) extends IncomingChatRequestMessage
case class SetChatChannelTopicResponseMessage() extends OutgoingProtocolResponseMessage
case class ChatChannelTopicSetMessage(i: String, e: Long, p: Long, u: String, c: String) extends OutgoingProtocolNormalMessage

// Set Seen
case class MarkChatChannelEventsSeenRequestMessage(i: String, e: Long) extends IncomingChatRequestMessage
case class MarkChatChannelEventsSeenResponseMessage() extends OutgoingProtocolResponseMessage
case class ChatChannelEventsMarkedSeenMessage(i: String, e: Long) extends OutgoingProtocolNormalMessage

// Set Permissions

sealed trait IncomingPermissionsMessage {
  val p: Int
}
sealed trait IncomingPermissionsRequestMessage extends IncomingPermissionsMessage with IncomingProtocolRequestMessage

case class AddPermissionsRequestMessage(p: Int, i: String, w: Option[Set[String]], u: Option[Map[String, Set[String]]], g: Option[Map[String, Set[String]]]) extends IncomingPermissionsRequestMessage
case class AddPermissionsReponseMessage() extends OutgoingProtocolResponseMessage

case class RemovePermissionsRequestMessage(p: Int, i: String, w: Option[Set[String]], u: Option[Map[String, Set[String]]], g: Option[Map[String, Set[String]]]) extends IncomingPermissionsRequestMessage
case class RemovePermissionsReponseMessage() extends OutgoingProtocolResponseMessage

case class SetPermissionsRequestMessage(p: Int, i: String, w: Option[Set[String]], u: Option[Map[String, Set[String]]], g: Option[Map[String, Set[String]]]) extends IncomingPermissionsRequestMessage
case class SetPermissionsReponseMessage() extends OutgoingProtocolResponseMessage

case class GetClientPermissionsRequestMessage(p: Int, i: String) extends IncomingPermissionsRequestMessage
case class GetClientPermissionsReponseMessage(p: Set[String]) extends OutgoingProtocolResponseMessage

case class GetWorldPermissionsRequestMessage(p: Int, i: String) extends IncomingPermissionsRequestMessage
case class GetWorldPermissionsReponseMessage(p: Set[String]) extends OutgoingProtocolResponseMessage

case class GetAllUserPermissionsRequestMessage(p: Int, i: String) extends IncomingPermissionsRequestMessage
case class GetAllUserPermissionsReponseMessage(u: Map[String, Set[String]]) extends OutgoingProtocolResponseMessage

case class GetAllGroupPermissionsRequestMessage(p: Int, i: String) extends IncomingPermissionsRequestMessage
case class GetAllGroupPermissionsReponseMessage(g: Map[String, Set[String]]) extends OutgoingProtocolResponseMessage

case class GetUserPermissionsRequestMessage(p: Int, i: String, u: String) extends IncomingPermissionsRequestMessage
case class GetUserPermissionsReponseMessage(p: Set[String]) extends OutgoingProtocolResponseMessage

case class GetGroupPermissionsRequestMessage(p: Int, i: String, u: String) extends IncomingPermissionsRequestMessage
case class GetGroupPermissionsReponseMessage(p: Set[String]) extends OutgoingProtocolResponseMessage


// Get, History, and Search
case class GetJoinedChatChannelsRequestMessage() extends IncomingChatRequestMessage
case class GetJoinedChatChannelsResponseMessage(c: List[ChatChannelInfoData]) extends OutgoingProtocolResponseMessage

case class GetChatChannelsRequestMessage(i: List[String]) extends IncomingChatRequestMessage
case class GetChatChannelsResponseMessage(c: List[ChatChannelInfoData]) extends OutgoingProtocolResponseMessage

case class GetDirectChannelsRequestMessage(u: List[Set[String]]) extends IncomingChatRequestMessage
case class GetDirectChannelsResponseMessage(c: List[ChatChannelInfoData]) extends OutgoingProtocolResponseMessage

case class ChatChannelHistoryRequestMessage(i: String, l: Option[Int], s: Option[Int], f: Option[Boolean], e: Option[List[Int]]) extends IncomingChatRequestMessage
case class ChatChannelHistoryResponseMessage(e: List[ChatChannelEventData]) extends OutgoingProtocolResponseMessage

case class ChatChannelsExistsRequestMessage(i: List[String]) extends IncomingChatRequestMessage
case class ChatChannelsExistsResponseMessage(e: List[Boolean]) extends OutgoingProtocolResponseMessage

case class ChatChannelInfoData(
  i: String,
  p: String,
  cm: String,
  n: String,
  o: String,
  c: Long,
  l: Long,
  ec: Long,
  ls: Long,
  m: Set[String])

sealed trait ChatChannelEventData {
  val i: String
  val n: Long
  val p: Long
  val u: String
}

case class ChatCreatedEventData(i: String, n: Long, p: Long, u: String, a: String, o: String, m: Set[String], e: Int) extends ChatChannelEventData
case class ChatMessageEventData(i: String, n: Long, p: Long, u: String, m: String, e: Int) extends ChatChannelEventData
case class ChatUserJoinedEventData(i: String, n: Long, p: Long, u: String, e: Int) extends ChatChannelEventData
case class ChatUserLeftEventData(i: String, n: Long, p: Long, u: String, e: Int) extends ChatChannelEventData
case class ChatUserAddedEventData(i: String, n: Long, p: Long, u: String, b: String, e: Int) extends ChatChannelEventData
case class ChatUserRemovedEventData(i: String, n: Long, p: Long, u: String, b: String, e: Int) extends ChatChannelEventData
case class ChatNameChangedEventData(i: String, n: Long, p: Long, u: String, a: String, e: Int) extends ChatChannelEventData
case class ChatTopicChangedEventData(i: String, n: Long, p: Long, u: String, o: String, e: Int) extends ChatChannelEventData
