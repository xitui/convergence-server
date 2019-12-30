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

package com.convergencelabs.convergence.server.api.realtime

import com.convergencelabs.convergence.proto.activity._
import com.convergencelabs.convergence.proto.core._
import com.convergencelabs.convergence.proto.chat._
import com.convergencelabs.convergence.proto.identity._
import com.convergencelabs.convergence.proto.model._
import com.convergencelabs.convergence.proto.presence._
import com.convergencelabs.convergence.proto.ConvergenceMessage._
import scalapb.GeneratedMessage

/**
 * A helper class that wraps and unwraps the ConvergenceMessage Body
 * which is the container for all protocol messages.
 */
object ConvergenceMessageBodyUtils {
  /**
   * Extracts the protocol buffer messages from the ConvergenceMessage
   * Body.
   *
   * @param body The body which may be one of many messages.
   * @return The extract message, or None if it was an empty message.
   */
  def fromBody(body: Body): Option[GeneratedMessage] = {
    Option(body match {
      case Body.Error(message) => message
      case Body.Ok(message) => message
      case Body.Ping(message) => message
      case Body.Pong(message) => message
      case Body.HandshakeRequest(message) => message
      case Body.HandshakeResponse(message) => message
      case Body.AuthenticationRequest(message) => message
      case Body.AuthenticationResponse(message) => message
      case Body.IdentityCacheUpdate(message) => message

      // Model
      case Body.OpenRealTimeModelRequest(message) => message
      case Body.OpenRealTimeModelResponse(message) => message
      case Body.CloseRealTimeModelRequest(message) => message
      case Body.CloseRealTimeModelResponse(message) => message
      case Body.CreateRealTimeModelRequest(message) => message
      case Body.CreateRealTimeModelResponse(message) => message
      case Body.DeleteRealtimeModelRequest(message) => message
      case Body.DeleteRealtimeModelResponse(message) => message
      case Body.ForceCloseRealTimeModel(message) => message
      case Body.RemoteClientOpenedModel(message) => message
      case Body.RemoteClientClosedModel(message) => message
      case Body.ModelAutoCreateConfigRequest(message) => message
      case Body.ModelAutoCreateConfigResponse(message) => message
      case Body.RemoteOperation(message) => message
      case Body.OperationSubmission(message) => message
      case Body.OperationAck(message) => message
      case Body.ShareReference(message) => message
      case Body.SetReference(message) => message
      case Body.ClearReference(message) => message
      case Body.UnshareReference(message) => message
      case Body.ReferenceShared(message) => message
      case Body.ReferenceSet(message) => message
      case Body.ReferenceCleared(message) => message
      case Body.ReferenceUnshared(message) => message
      case Body.ModelsQueryRequest(message) => message
      case Body.ModelsQueryResponse(message) => message
      case Body.GetModelPermissionsRequest(message) => message
      case Body.GetModelPermissionsResponse(message) => message
      case Body.SetModelPermissionsRequest(message) => message
      case Body.SetModelPermissionsResponse(message) => message
      case Body.ModelPermissionsChanged(message) => message
      case Body.ModelResyncRequest(message) => message
      case Body.ModelResyncResponse(message) => message
      case Body.ModelResyncCompleteRequest(message) => message
      case Body.ModelResyncCompleteResponse(message) => message
      case Body.RemoteClientResyncStarted(message) => message
      case Body.RemoteClientResyncCompleted(message) => message
      case Body.ModelOfflineSubscriptionChange(message) => message
      case Body.ModelOfflineUpdated(message) => message

      case Body.HistoricalDataRequest(message) => message
      case Body.HistoricalDataResponse(message) => message
      case Body.HistoricalOperationsRequest(message) => message
      case Body.HistoricalOperationsResponse(message) => message

      // Identity
      case Body.UsersGetRequest(message) => message
      case Body.UserSearchRequest(message) => message
      case Body.UserListResponse(message) => message
      case Body.UserGroupsRequest(message) => message
      case Body.UserGroupsResponse(message) => message
      case Body.UserGroupsForUsersRequest(message) => message
      case Body.UserGroupsForUsersResponse(message) => message

      // Activity
      case Body.ActivityParticipantsRequest(message) => message
      case Body.ActivityParticipantsResponse(message) => message
      case Body.ActivityJoinRequest(message) => message
      case Body.ActivityJoinResponse(message) => message
      case Body.ActivityLeaveRequest(message) => message
      case Body.ActivitySessionJoined(message) => message
      case Body.ActivitySessionLeft(message) => message
      case Body.ActivityUpdateState(message) => message
      case Body.ActivityStateUpdated(message) => message

      // Presence
      case Body.PresenceSetState(message) => message
      case Body.PresenceClearState(message) => message
      case Body.PresenceRemoveState(message) => message
      case Body.PresenceStateSet(message) => message
      case Body.PresenceStateCleared(message) => message
      case Body.PresenceStateRemoved(message) => message
      case Body.PresenceRequest(message) => message
      case Body.PresenceResponse(message) => message
      case Body.PresenceSubscribeRequest(message) => message
      case Body.PresenceSubscribeResponse(message) => message
      case Body.PresenceUnsubscribe(message) => message
      case Body.PresenceAvailabilityChanged(message) => message

      // Chat
      case Body.CreateChatRequest(message) => message
      case Body.CreateChatResponse(message) => message
      case Body.RemoveChatRequest(message) => message
      case Body.RemoveChatResponse(message) => message
      case Body.GetChatsRequest(message) => message
      case Body.GetChatsResponse(message) => message
      case Body.ChatsExistRequest(message) => message
      case Body.ChatsExistResponse(message) => message
      case Body.GetDirectChatsRequest(message) => message
      case Body.GetDirectChatsResponse(message) => message
      case Body.GetJoinedChatsRequest(message) => message
      case Body.GetJoinedChatsResponse(message) => message
      case Body.JoinChatRequest(message) => message
      case Body.JoinChatResponse(message) => message
      case Body.UserJoinedChat(message) => message
      case Body.LeaveChatRequest(message) => message
      case Body.LeaveChatResponse(message) => message
      case Body.UserLeftChat(message) => message
      case Body.AddUserToChatChannelRequest(message) => message
      case Body.AddUserToChatChannelResponse(message) => message
      case Body.UserAddedToChatChannel(message) => message
      case Body.RemoveUserFromChatChannelRequest(message) => message
      case Body.RemoveUserFromChatChannelResponse(message) => message
      case Body.UserRemovedFromChatChannel(message) => message
      case Body.SetChatNameRequest(message) => message
      case Body.SetChatNameResponse(message) => message
      case Body.ChatNameChanged(message) => message
      case Body.SetChatTopicRequest(message) => message
      case Body.SetChatTopicResponse(message) => message
      case Body.ChatTopicChanged(message) => message
      case Body.MarkChatEventsSeenRequest(message) => message
      case Body.MarkChatEventsSeenResponse(message) => message
      case Body.ChatEventsMarkedSeen(message) => message
      case Body.PublishChatMessageRequest(message) => message
      case Body.PublishChatMessageResponse(message) => message
      case Body.RemoteChatMessage(message) => message
      case Body.GetChatHistoryRequest(message) => message
      case Body.GetChatHistoryResponse(message) => message
      case Body.ChatRemoved(message) => message

      // Permissions
      case Body.GetClientPermissionsRequest(message) => message
      case Body.GetClientPermissionsResponse(message) => message
      case Body.AddPermissionsRequest(message) => message
      case Body.AddPermissionsResponse(message) => message
      case Body.RemovePermissionsRequest(message) => message
      case Body.RemovePermissionsResponse(message) => message
      case Body.SetPermissionsRequest(message) => message
      case Body.SetPermissionsResponse(message) => message
      case Body.GetWorldPermissionsRequest(message) => message
      case Body.GetWorldPermissionsResponse(message) => message
      case Body.GetAllUserPermissionsRequest(message) => message
      case Body.GetAllUserPermissionsResponse(message) => message
      case Body.GetUserPermissionsRequest(message) => message
      case Body.GetUserPermissionsResponse(message) => message
      case Body.GetAllGroupPermissionsRequest(message) => message
      case Body.GetAllGroupPermissionsResponse(message) => message
      case Body.GetGroupPermissionsRequest(message) => message
      case Body.GetGroupPermissionsResponse(message) => message
      case Body.Empty => null
    })
  }

  /**
   * Wraps the protocol buffer message into the Body such that
   * it can be added easily to the ConvergenceMessage.
   *
   * @param message The message to wrap in the Body.
   * @return A Body element containing the supplied message.
   */
  def toBody(message: GeneratedMessage): Body = {
    message match {
      case message: ErrorMessage => Body.Error(message)
      case message: PingMessage => Body.Ping(message)
      case message: PongMessage => Body.Pong(message)
      case message: HandshakeRequestMessage => Body.HandshakeRequest(message)
      case message: HandshakeResponseMessage => Body.HandshakeResponse(message)
      case message: AuthenticationRequestMessage => Body.AuthenticationRequest(message)
      case message: AuthenticationResponseMessage => Body.AuthenticationResponse(message)
      case message: IdentityCacheUpdateMessage => Body.IdentityCacheUpdate(message)
      case message: OkResponse => Body.Ok(message)

      // Model
      case message: OpenRealtimeModelRequestMessage => Body.OpenRealTimeModelRequest(message)
      case message: OpenRealtimeModelResponseMessage => Body.OpenRealTimeModelResponse(message)
      case message: CloseRealtimeModelRequestMessage => Body.CloseRealTimeModelRequest(message)
      case message: CloseRealTimeModelResponseMessage => Body.CloseRealTimeModelResponse(message)
      case message: CreateRealtimeModelRequestMessage => Body.CreateRealTimeModelRequest(message)
      case message: CreateRealtimeModelResponseMessage => Body.CreateRealTimeModelResponse(message)
      case message: DeleteRealtimeModelRequestMessage => Body.DeleteRealtimeModelRequest(message)
      case message: DeleteRealtimeModelResponseMessage => Body.DeleteRealtimeModelResponse(message)
      case message: ModelForceCloseMessage => Body.ForceCloseRealTimeModel(message)
      case message: RemoteClientOpenedMessage => Body.RemoteClientOpenedModel(message)
      case message: RemoteClientClosedMessage => Body.RemoteClientClosedModel(message)
      case message: AutoCreateModelConfigRequestMessage => Body.ModelAutoCreateConfigRequest(message)
      case message: AutoCreateModelConfigResponseMessage => Body.ModelAutoCreateConfigResponse(message)
      case message: RemoteOperationMessage => Body.RemoteOperation(message)
      case message: OperationSubmissionMessage => Body.OperationSubmission(message)
      case message: OperationAcknowledgementMessage => Body.OperationAck(message)
      case message: ShareReferenceMessage => Body.ShareReference(message)
      case message: SetReferenceMessage => Body.SetReference(message)
      case message: ClearReferenceMessage => Body.ClearReference(message)
      case message: UnshareReferenceMessage => Body.UnshareReference(message)
      case message: RemoteReferenceSharedMessage => Body.ReferenceShared(message)
      case message: RemoteReferenceSetMessage => Body.ReferenceSet(message)
      case message: RemoteReferenceClearedMessage => Body.ReferenceCleared(message)
      case message: RemoteReferenceUnsharedMessage => Body.ReferenceUnshared(message)
      case message: ModelsQueryRequestMessage => Body.ModelsQueryRequest(message)
      case message: ModelsQueryResponseMessage => Body.ModelsQueryResponse(message)
      case message: HistoricalDataRequestMessage => Body.HistoricalDataRequest(message)
      case message: HistoricalDataResponseMessage => Body.HistoricalDataResponse(message)
      case message: HistoricalOperationRequestMessage => Body.HistoricalOperationsRequest(message)
      case message: HistoricalOperationsResponseMessage => Body.HistoricalOperationsResponse(message)
      case message: GetModelPermissionsRequestMessage => Body.GetModelPermissionsRequest(message)
      case message: GetModelPermissionsResponseMessage => Body.GetModelPermissionsResponse(message)
      case message: SetModelPermissionsRequestMessage => Body.SetModelPermissionsRequest(message)
      case message: SetModelPermissionsResponseMessage => Body.SetModelPermissionsResponse(message)
      case message: ModelPermissionsChangedMessage => Body.ModelPermissionsChanged(message)
      case message: ModelResyncRequestMessage => Body.ModelResyncRequest(message)
      case message: ModelResyncResponseMessage => Body.ModelResyncResponse(message)
      case message: ModelResyncCompleteRequestMessage => Body.ModelResyncCompleteRequest(message)
      case message: ModelResyncCompleteResponseMessage => Body.ModelResyncCompleteResponse(message)
      case message: RemoteClientResyncStartedMessage => Body.RemoteClientResyncStarted(message)
      case message: RemoteClientResyncCompletedMessage => Body.RemoteClientResyncCompleted(message)
      case message: ModelOfflineSubscriptionChangeRequestMessage=> Body.ModelOfflineSubscriptionChange(message)
      case message: OfflineModelUpdatedMessage => Body.ModelOfflineUpdated(message)

      // Identity
      case message: GetUsersMessage => Body.UsersGetRequest(message)
      case message: UserSearchMessage => Body.UserSearchRequest(message)
      case message: UserListMessage => Body.UserListResponse(message)
      case message: UserGroupsRequestMessage => Body.UserGroupsRequest(message)
      case message: UserGroupsResponseMessage => Body.UserGroupsResponse(message)
      case message: UserGroupsForUsersRequestMessage => Body.UserGroupsForUsersRequest(message)
      case message: UserGroupsForUsersResponseMessage => Body.UserGroupsForUsersResponse(message)

      // Activity
      case message: ActivityParticipantsRequestMessage => Body.ActivityParticipantsRequest(message)
      case message: ActivityParticipantsResponseMessage => Body.ActivityParticipantsResponse(message)
      case message: ActivityJoinRequestMessage => Body.ActivityJoinRequest(message)
      case message: ActivityJoinResponseMessage => Body.ActivityJoinResponse(message)
      case message: ActivityLeaveMessage => Body.ActivityLeaveRequest(message)
      case message: ActivitySessionJoinedMessage => Body.ActivitySessionJoined(message)
      case message: ActivitySessionLeftMessage => Body.ActivitySessionLeft(message)
      case message: ActivityUpdateStateMessage => Body.ActivityUpdateState(message)
      case message: ActivityStateUpdatedMessage => Body.ActivityStateUpdated(message)

      // Presence
      case message: PresenceSetStateMessage => Body.PresenceSetState(message)
      case message: PresenceClearStateMessage => Body.PresenceClearState(message)
      case message: PresenceRemoveStateMessage => Body.PresenceRemoveState(message)
      case message: PresenceStateSetMessage => Body.PresenceStateSet(message)
      case message: PresenceStateClearedMessage => Body.PresenceStateCleared(message)
      case message: PresenceStateRemovedMessage => Body.PresenceStateRemoved(message)
      case message: PresenceRequestMessage => Body.PresenceRequest(message)
      case message: PresenceResponseMessage => Body.PresenceResponse(message)
      case message: SubscribePresenceRequestMessage => Body.PresenceSubscribeRequest(message)
      case message: SubscribePresenceResponseMessage => Body.PresenceSubscribeResponse(message)
      case message: UnsubscribePresenceMessage => Body.PresenceUnsubscribe(message)
      case message: PresenceAvailabilityChangedMessage => Body.PresenceAvailabilityChanged(message)

      // Chat
      case message: CreateChatRequestMessage => Body.CreateChatRequest(message)
      case message: CreateChatResponseMessage => Body.CreateChatResponse(message)
      case message: RemoveChatRequestMessage => Body.RemoveChatRequest(message)
      case message: RemoveChatResponseMessage => Body.RemoveChatResponse(message)
      case message: GetChatsRequestMessage => Body.GetChatsRequest(message)
      case message: GetChatsResponseMessage => Body.GetChatsResponse(message)
      case message: ChatsExistRequestMessage => Body.ChatsExistRequest(message)
      case message: ChatsExistResponseMessage => Body.ChatsExistResponse(message)
      case message: GetDirectChatsRequestMessage => Body.GetDirectChatsRequest(message)
      case message: GetDirectChatsResponseMessage => Body.GetDirectChatsResponse(message)
      case message: GetJoinedChatsRequestMessage => Body.GetJoinedChatsRequest(message)
      case message: GetJoinedChatsResponseMessage => Body.GetJoinedChatsResponse(message)
      case message: JoinChatRequestMessage => Body.JoinChatRequest(message)
      case message: JoinChatResponseMessage => Body.JoinChatResponse(message)
      case message: UserJoinedChatMessage => Body.UserJoinedChat(message)
      case message: LeaveChatRequestMessage => Body.LeaveChatRequest(message)
      case message: LeaveChatResponseMessage => Body.LeaveChatResponse(message)
      case message: UserLeftChatMessage => Body.UserLeftChat(message)
      case message: AddUserToChatChannelRequestMessage => Body.AddUserToChatChannelRequest(message)
      case message: AddUserToChatChannelResponseMessage => Body.AddUserToChatChannelResponse(message)
      case message: UserAddedToChatChannelMessage => Body.UserAddedToChatChannel(message)
      case message: RemoveUserFromChatChannelRequestMessage => Body.RemoveUserFromChatChannelRequest(message)
      case message: RemoveUserFromChatChannelResponseMessage => Body.RemoveUserFromChatChannelResponse(message)
      case message: UserRemovedFromChatChannelMessage => Body.UserRemovedFromChatChannel(message)
      case message: SetChatNameRequestMessage => Body.SetChatNameRequest(message)
      case message: SetChatNameResponseMessage => Body.SetChatNameResponse(message)
      case message: ChatNameSetMessage => Body.ChatNameChanged(message)
      case message: SetChatTopicRequestMessage => Body.SetChatTopicRequest(message)
      case message: SetChatTopicResponseMessage => Body.SetChatTopicResponse(message)
      case message: ChatTopicSetMessage => Body.ChatTopicChanged(message)
      case message: MarkChatEventsSeenRequestMessage => Body.MarkChatEventsSeenRequest(message)
      case message: MarkChatEventsSeenResponseMessage => Body.MarkChatEventsSeenResponse(message)
      case message: ChatEventsMarkedSeenMessage => Body.ChatEventsMarkedSeen(message)
      case message: PublishChatRequestMessage => Body.PublishChatMessageRequest(message)
      case message: PublishChatResponseMessage => Body.PublishChatMessageResponse(message)
      case message: RemoteChatMessageMessage => Body.RemoteChatMessage(message)
      case message: ChatHistoryRequestMessage => Body.GetChatHistoryRequest(message)
      case message: ChatHistoryResponseMessage => Body.GetChatHistoryResponse(message)
      case message: ChatRemovedMessage => Body.ChatRemoved(message)

      // Permissions
      case message: GetClientPermissionsRequestMessage => Body.GetClientPermissionsRequest(message)
      case message: GetClientPermissionsResponseMessage => Body.GetClientPermissionsResponse(message)
      case message: AddPermissionsRequestMessage => Body.AddPermissionsRequest(message)
      case message: AddPermissionsResponseMessage => Body.AddPermissionsResponse(message)
      case message: RemovePermissionsRequestMessage => Body.RemovePermissionsRequest(message)
      case message: RemovePermissionsResponseMessage => Body.RemovePermissionsResponse(message)
      case message: SetPermissionsRequestMessage => Body.SetPermissionsRequest(message)
      case message: SetPermissionsResponseMessage => Body.SetPermissionsResponse(message)
      case message: GetWorldPermissionsRequestMessage => Body.GetWorldPermissionsRequest(message)
      case message: GetWorldPermissionsResponseMessage => Body.GetWorldPermissionsResponse(message)
      case message: GetAllUserPermissionsRequestMessage => Body.GetAllUserPermissionsRequest(message)
      case message: GetAllUserPermissionsResponseMessage => Body.GetAllUserPermissionsResponse(message)
      case message: GetUserPermissionsRequestMessage => Body.GetUserPermissionsRequest(message)
      case message: GetUserPermissionsResponseMessage => Body.GetUserPermissionsResponse(message)
      case message: GetAllGroupPermissionsRequestMessage => Body.GetAllGroupPermissionsRequest(message)
      case message: GetAllGroupPermissionsResponseMessage => Body.GetAllGroupPermissionsResponse(message)
      case message: GetGroupPermissionsRequestMessage => Body.GetGroupPermissionsRequest(message)
      case message: GetGroupPermissionsResponseMessage => Body.GetGroupPermissionsResponse(message)
    }
  }
}