package com.convergencelabs.server.datastore.convergence

import java.net.URLEncoder

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success

import com.convergencelabs.server.util.EmailUtilities
import com.convergencelabs.templates
import com.typesafe.config.Config

import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpMethods
import scala.xml.Utility
import com.convergencelabs.server.db.DatabaseProvider
import com.convergencelabs.server.datastore.StoreActor
import com.convergencelabs.server.util.ZohoUtility
import com.convergencelabs.server.datastore.convergence.ConvergenceUserManagerActor.CreateConvergenceUserRequest
import com.convergencelabs.server.datastore.EntityNotFoundException

object RegistrationActor {
  val RelativePath = "RegistrationActor"
  
  def props(dbProvider: DatabaseProvider, userManager: ActorRef): Props = Props(new RegistrationActor(dbProvider, userManager))

  case class RegisterUser(username: String, fname: String, lname: String, email: String, password: String, token: String)
  case class RequestRegistration(fname: String, lname: String, email: String, company: Option[String], title: Option[String], reason: String)
  case class ApproveRegistration(token: String)
  case class RejectRegistration(token: String)
  case class RegistrationInfoRequest(token: String)
  case class RegistrationInfo(fname: String, lname: String, email: String)
}

class RegistrationActor private[datastore] (dbProvider: DatabaseProvider, userManager: ActorRef) extends StoreActor with ActorLogging {

  import RegistrationActor._
  
  // FIXME: Read this from configuration
  private[this] implicit val requstTimeout = Timeout(15 seconds)
  private[this] implicit val exectionContext = context.dispatcher

  private[this] val smtpConfig: Config = context.system.settings.config.getConfig("convergence.smtp")

  private[this] val registrationBaseUrl = context.system.settings.config.getString("convergence.registration.registration-rest-base-url")
  private[this] val adminUiServerUrl = context.system.settings.config.getString("convergence.registration.admin-console-url")

  private[this] val zohoConfig = context.system.settings.config.getConfig("convergence.zoho")
  
  private[this] val zohoEnabled = context.system.settings.config.getBoolean("convergence.zoho.enabled")
  private[this] val zohoAuthToken = context.system.settings.config.getString("convergence.zoho.auth-token")
  private[this] val zohoUtil = new ZohoUtility(zohoAuthToken, context.system, context.dispatcher)

  private[this] val registrationStore = new RegistrationStore(dbProvider)

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  def receive: Receive = {
    case message: RegisterUser => registerUser(message)
    case message: RequestRegistration => requestRegistration(message)
    case message: ApproveRegistration => appoveRegistration(message)
    case message: RejectRegistration => rejectRegistration(message)
    case message: RegistrationInfoRequest => getRegistrationInfo(message)
    case message: Any => unhandled(message)
  }

  def registerUser(message: RegisterUser): Unit = {
    val origSender = sender
    val RegisterUser(username, fname, lname, email, password, token) = message
    registrationStore.isRegistrationApproved(email, token).map {
      case Some(true) => {
        val req = CreateConvergenceUserRequest(username, email, fname, lname, s"$fname $lname", password)
        (userManager ? req) onComplete {
          case Success(_) =>
            log.debug(s"User ${username} created, removing registration entry.")

            registrationStore.removeRegistration(token).recover {
              case cause: Exception =>
                log.error(cause, s"Could not remove registration request entry for: {$email}")
            }

            origSender ! Success(())

            val welcomeTxt = if (fname != null && fname.nonEmpty) s"${fname}, welcome" else "Welcome"
            val templateHtml = templates.email.html.accountCreated(username, welcomeTxt)
            val templateTxt = templates.email.txt.accountCreated(username, welcomeTxt)
            val newAccountEmail = EmailUtilities.createHtmlEmail(smtpConfig, templateHtml, templateTxt.toString())
            newAccountEmail.setSubject(s"${welcomeTxt} to Convergence!")
            newAccountEmail.addTo(email)
            newAccountEmail.send()

            Future {
              newAccountEmail.send()
            } onComplete {
              case Success(_) =>
                log.debug(s"Sent registration approval notification to: ${email}")
              case Failure(cause) =>
                log.error(cause, s"Could not send registration approval notification email for: {$email}")
            }

            if (zohoEnabled) {
              log.debug(s"Converting Zoho Lead to Contact for email: ${email}")

              zohoUtil.convertLead(email) onComplete {
                case Success(_) =>
                  log.debug(s"Zoho lead converted for email: ${email}")
                case Failure(cause) =>
                  log.error(cause, s"Could not convert Zoho Lead for email: {$email}")
              }
            }
            ()
          case Failure(cause) =>
            log.error(cause, s"Could not create user: ${username}")
            origSender ! akka.actor.Status.Failure(cause)
        }
      }
      case Some(false) =>
        origSender ! akka.actor.Status.Failure(new IllegalArgumentException("Registration not approved"))
      case None =>
        origSender ! akka.actor.Status.Failure(new EntityNotFoundException("Registration token not found"))
    }
  }

  def requestRegistration(message: RequestRegistration): Unit = {
    val RequestRegistration(fname, lname, email, company, title, reason) = message
    log.debug(s"Processing registration request for email: ${email}")
    val result = registrationStore.addRegistration(fname, lname, email, reason) map { token =>
      val bodyContent = templates.email.internal.txt.registrationRequest(token, fname, lname, email, reason, registrationBaseUrl)
      val internalEmail = EmailUtilities.createTextEmail(smtpConfig, bodyContent.toString())
      internalEmail.setSubject(s"Registration Request from ${fname} ${lname}")
      val regEmail = smtpConfig.getString("new-registration-to-address")
      internalEmail.addTo(regEmail)

      Future {
        internalEmail.send()
      } onComplete {
        case Success(_) =>
          log.debug(s"Sent email internal registration notification for '${email}' to: ${regEmail}")
        case Failure(cause) =>
          log.error(cause, s"Could not send internal registration notification email for: {$email}")
      }

      if (zohoEnabled) {
        log.debug(s"Creating Zoho Lead for email: ${email}")
        val lead = ZohoUtility.Lead(fname, lname, email, company, title, reason)

        zohoUtil.createLead(lead) onComplete {
          case Success(_) =>
            log.debug(s"Zoho lead created for email: ${email}")
          case Failure(cause) =>
            log.error(cause, s"Could not create Zoho Lead for email: {$email}")
        }
      }
      ()
    } map { _ =>
      val template = templates.email.html.accountRequested(fname)
      val templateTxt = templates.email.txt.accountRequested(fname)
      val newRequestEmail = EmailUtilities.createHtmlEmail(smtpConfig, template, templateTxt.toString())
      newRequestEmail.setSubject(s"${fname}, your account request has been received")
      newRequestEmail.addTo(email)

      Future {
        newRequestEmail.send()
      } onComplete {
        case Success(_) =>
          log.debug(s"Sent email registration notification to: ${email}")
        case Failure(cause) =>
          log.error(cause, s"Could not send registration notification email to: {$email}")
      }
      ()
    }

    reply(result)
  }

  def appoveRegistration(message: ApproveRegistration): Unit = {
    val ApproveRegistration(token) = message

    val resp = for {
      info <- registrationStore.getRegistrationInfo(token) flatMap {
        case Some(info) => Success(info)
        case None => Failure(new IllegalArgumentException("Unable to lookup registration infromation"))
      }
      approve <- registrationStore.approveRegistration(token)
    } yield {
      val (firstName, lastName, email) = info

      val fnameEncoded = URLEncoder.encode(firstName, "UTF8")
      val lnameEncoded = URLEncoder.encode(lastName, "UTF8")
      val emailEncoded = URLEncoder.encode(email, "UTF8")
      val signupUrl = s"${adminUiServerUrl}signup/${token}"
      val introTxt = if (firstName != null && firstName.nonEmpty) s"${firstName}, good news!" else "Good news!"

      val templateHtml = templates.email.html.registrationApproved(signupUrl, introTxt)
      val templateTxt = templates.email.txt.registrationApproved(signupUrl, introTxt)

      val approvalEmail = EmailUtilities.createHtmlEmail(smtpConfig, templateHtml, templateTxt.toString());
      approvalEmail.setSubject(s"Your Convergence account request has been approved")
      approvalEmail.addTo(email)

      (Future {
        approvalEmail.send()
        log.debug(s"Sent registration approval email to: ${email}")
      }).failed.foreach {
        case cause: Exception =>
          log.error(cause, "Could not send registration approval message to: ${email}")
      }

      if (zohoEnabled) {
        log.debug(s"Approving Zoho Lead for email: ${email}")

        zohoUtil.approveLead(email) onComplete {
          case Success(_) =>
            log.debug(s"Zoho lead approved for email: ${email}")
          case Failure(cause) =>
            log.error(cause, s"Could not approve Zoho Lead for email: {$email}")
        }
      }
      ()
    }

    reply(resp)
  }

  def getRegistrationInfo(message: RegistrationInfoRequest): Unit = {
    val RegistrationInfoRequest(token) = message
    val result = registrationStore.getRegistrationInfo(token) map {
      _.map { registration =>
        val (firstName, lastName, email) = registration
        RegistrationInfo(firstName, lastName, email)
      }
    }
    reply(result)
  }

  def rejectRegistration(message: RejectRegistration): Unit = {
    val RejectRegistration(token) = message
    reply(registrationStore.removeRegistration(token))
  }
}