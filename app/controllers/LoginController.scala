package controllers

import play.api.libs.json.{JsError, JsPath, Json, Reads}
import play.api.mvc.{Action, BodyParsers, Controller}
import utils.SessionValues

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.functional.syntax._

class LoginController extends Controller {

  case class LoginForm(login: String, password: String)

  implicit val loginFormReads: Reads[LoginForm] = (
    (JsPath \ "login").read[String] and
      (JsPath \ "password").read[String]
    ) (LoginForm.apply _)

  def loginResult(success: Boolean) = Ok(Json.obj("success" -> success))

  def login() = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[LoginForm].fold(
      errors => Future(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      login => Future(login match {
        case LoginForm("admin", "admin") => loginResult(true).withSession(SessionValues.USER_ID -> "123")
        case _ => loginResult(false)
      })
    )
  }
}
