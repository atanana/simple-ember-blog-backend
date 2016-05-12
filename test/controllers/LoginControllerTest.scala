package controllers

import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.SessionValues

class LoginControllerTest extends PlaySpec with Results with BeforeAndAfter with OneAppPerSuite {
  var controller: LoginController = _

  before {
    controller = new LoginController()
  }

  "Login#login" should {
    "should write valid login data" in {
      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.obj())
      val result: Result = await(controller.login().apply(request))
      result.session(request).get(SessionValues.USER_ID) mustBe Some("123")
    }
  }
}
