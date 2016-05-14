package controllers

import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.SessionValues

import scala.concurrent.Future

class LoginControllerTest extends PlaySpec with Results with BeforeAndAfter with OneAppPerSuite {
  var controller: LoginController = _

  before {
    controller = new LoginController()
  }

  "Login#login" should {
    "should write valid login data" in {
      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.obj("login" -> "admin", "password" -> "admin"))
      val resultFuture: Future[Result] = controller.login().apply(request)
      status(resultFuture) mustBe OK
      contentAsJson(resultFuture) mustBe Json.obj("success" -> true)
      await(resultFuture).session(request).get(SessionValues.USER_ID) mustBe Some("123")
    }

    "should return an error on incorrect json" in {
      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.obj())
      val resultFuture: Future[Result] = controller.login().apply(request)
      status(resultFuture) mustBe BAD_REQUEST
      await(resultFuture).session(request).get(SessionValues.USER_ID) mustBe None
    }

    "should return an error on incorrect credentials" in {
      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.obj("login" -> "not admin", "password" -> "not admin"))
      val resultFuture: Future[Result] = controller.login().apply(request)
      status(resultFuture) mustBe OK
      contentAsJson(resultFuture) mustBe Json.obj("success" -> false)
      await(resultFuture).session(request).get(SessionValues.USER_ID) mustBe None
    }
  }
}
