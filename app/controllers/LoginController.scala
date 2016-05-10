package controllers

import play.api.mvc.{Action, BodyParsers, Controller}
import utils.SessionValues

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController extends Controller {
  def login() = Action.async(BodyParsers.parse.json) { request =>
    Future(Ok.withSession(SessionValues.USER_ID -> "123"))
  }
}
