package controllers

import models.{Post, Posts}
import org.joda.time.DateTime
import org.scalatest.mock.MockitoSugar

import scala.concurrent.Future
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import org.mockito.Mockito._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

class PostsControllerTest extends PlaySpec with Results with MockitoSugar {

  "Posts#list" should {
    "should be valid" in {
      val posts: Posts = mock[Posts]
      val now: DateTime = DateTime.now()
      when(posts.all()) thenReturn Future(Seq(
        Post(123, "test title", "test text", now),
        Post(432, "test title 2", "test text 2", now)
      ))
      val controller = new PostsController(posts)
      val postsJson: JsValue = contentAsJson(controller.list().apply(FakeRequest()))
      postsJson mustBe JsArray(Seq(
        JsObject(Seq(
          "id" -> JsNumber(123),
          "title" -> JsString("test title"),
          "text" -> JsString("test text"),
          "created" -> JsNumber(now.getMillis)
        )),
        JsObject(Seq(
          "id" -> JsNumber(432),
          "title" -> JsString("test title 2"),
          "text" -> JsString("test text 2"),
          "created" -> JsNumber(now.getMillis)
        ))
      ))
    }
  }
}