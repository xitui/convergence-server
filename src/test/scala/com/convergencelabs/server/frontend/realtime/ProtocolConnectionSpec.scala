package com.convergencelabs.server.frontend.realtime

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import java.util.concurrent.TimeUnit
import akka.actor.ActorSystem
import akka.testkit.{ TestProbe, TestKit }
import org.json4s.JsonAST.{ JObject, JString }
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{ read, write }
import org.json4s.NoTypeHints
import org.mockito.{ ArgumentCaptor, Mockito, Matchers }
import org.mockito.Mockito.{ verify, times }
import org.scalatest.mock.MockitoSugar
import org.scalatest.{ BeforeAndAfterAll, WordSpecLike }
import scala.concurrent.duration.FiniteDuration
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import com.convergencelabs.server.domain.DomainFqn
import com.convergencelabs.server.ProtocolConfiguration
import com.convergencelabs.server.frontend.realtime.proto.HandshakeRequestMessage
import com.convergencelabs.server.frontend.realtime.proto.OutgoingProtocolMessage
import scala.concurrent.Promise
import com.convergencelabs.server.frontend.realtime.proto.OutgoingProtocolResponseMessage
import akka.actor.ActorRef
import com.convergencelabs.server.domain.HandshakeRequest
import com.convergencelabs.server.domain.model.CloseRealtimeModelSuccess
import com.convergencelabs.server.domain.model.CloseRealtimeModelRequest
import com.convergencelabs.server.frontend.realtime.proto.CloseRealtimeModelRequestMessage
import akka.actor.Terminated
import com.convergencelabs.server.frontend.realtime.proto.HandshakeRequestMessage
import com.convergencelabs.server.frontend.realtime.proto.OpCode
import com.convergencelabs.server.frontend.realtime.proto.MessageEnvelope
import scala.concurrent.Await
import scala.util.Success
import org.scalatest.Assertions
import java.util.concurrent.LinkedBlockingDeque
import scala.PartialFunction
import com.convergencelabs.server.frontend.realtime.proto.HandshakeResponseMessage

@RunWith(classOf[JUnitRunner])
class ProtocolConnectionSpec(system: ActorSystem)
    extends TestKit(system)
    with WordSpecLike
    with BeforeAndAfterAll
    with MockitoSugar
    with Assertions {

  def this() = this(ActorSystem("ProtocolConnectionSpec"))

  implicit val formats = Serialization.formats(NoTypeHints)

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A ProtocolConnection" when {
    "receiving a request" must {
      "emit a request received event" in new TestFixture(system) {

        try {
          val receiver = new Receiver(connection)
          val message = HandshakeRequestMessage(false, None, None)
          val envelope = MessageEnvelope(OpCode.Request, Some(1L), Some(message))
          val json = envelope.toJson()
          socket.fireOnMessage(json)

          val RequestReceived(x, r) = receiver.expectEventClass(10 millis, classOf[RequestReceived])
          assert(message == x)

        } finally {
          connection.close()
        }
      }
    }

    "responding to a request" must {
      "send a correct reply envelope" in new TestFixture(system) {
        val receiver = new Receiver(connection)
        val message = HandshakeRequestMessage(false, None, None)
        val envelope = MessageEnvelope(OpCode.Request, Some(1L), Some(message))
        val json = envelope.toJson()
        socket.fireOnMessage(json)
        val RequestReceived(x, r) = receiver.expectEventClass(10 millis, classOf[RequestReceived])

        val response = HandshakeResponseMessage(true, None, Some("foo"), Some("bar"))
        r.success(response)

        var responseEnvelop = MessageEnvelope(OpCode.Reply, Some(1L), Some(response))
        Mockito.verify(socket, times(1)).send(responseEnvelop.toJson())
        connection.close()
      }
    }
  }

  class TestFixture(system: ActorSystem) {
    val protoConfig = ProtocolConfiguration(2L)
    val socket = Mockito.spy(new TestSocket())
    val connection = new ProtocolConnection(socket, protoConfig, false, system.scheduler, system.dispatcher)
  }

  class Receiver(connection: ProtocolConnection) {

    connection.eventHandler = receive

    private def receive: PartialFunction[ConnectionEvent, Unit] = {
      case x => queue.add(x)
    }

    private val queue = new LinkedBlockingDeque[ConnectionEvent]()

    def expectEventClass[C](max: FiniteDuration, c: Class[C]): C = expectEventClass_internal(max, c)

    private def expectEventClass_internal[C](max: FiniteDuration, c: Class[C]): C = {
      val o = receiveOne(max)
      assert(o ne null, s"timeout ($max) during expectMsgClass waiting for $c")
      assert(c isInstance o, s"expected $c, found ${o.getClass}")
      o.asInstanceOf[C]
    }

    def receiveOne(max: Duration): AnyRef = {
      val message =
        if (max == 0.seconds) {
          queue.pollFirst
        } else if (max.isFinite) {
          queue.pollFirst(max.length, max.unit)
        } else {
          queue.takeFirst
        }

      message
    }
  }

  class TestSocket() extends ConvergenceServerSocket {
    def send(message: String): Unit = {
    }

    var isOpen: Boolean = true

    def close(): Unit = {
    }

    def abort(reason: String): Unit = {
    }

    def dispose(): Unit = {
    }
  }
}