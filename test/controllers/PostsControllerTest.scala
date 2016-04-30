package controllers

import models.Posts.postsFormat
import models.{Post, Posts}
import org.joda.time.DateTime
import org.mockito.Matchers.{eq => mockEq}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostsControllerTest extends PlaySpec with Results with MockitoSugar {

  val now: DateTime = DateTime.now()

  "Posts#list" should {
    val posts: Posts = mock[Posts]
    val controller = new PostsController(posts)
    "should be valid" in {
      when(posts.all()) thenReturn Future(Seq(
        Post(123, "test title", "test text", now),
        Post(432, "test title 2", "test text 2", now)
      ))
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

  "Posts#add" should {
    "should create new post" in {
      val posts: Posts = mock[Posts]
      val controller = new PostsController(posts)
      val post: Post = Post(0, "test title", "test text", now)
      when(posts.add(mockEq(post))).thenReturn(Future(123))
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/posts").withBody(Json.toJson(post))
      val result: JsValue = contentAsJson(controller.add().apply(request))
      result mustBe JsObject(Seq("id" -> JsNumber(123)))
    }

    "should return and error on incorrect json" in {
      val controller = new PostsController(mock[Posts])
      val json: JsObject = Json.toJson(Post(0, "test title", "test text", now)).as[JsObject] - "id"
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/posts").withBody(json)
      val result: Future[Result] = controller.add().apply(request)
      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "error").toOption.isDefined mustBe true
    }
  }
}