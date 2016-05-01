package controllers

import models.Posts.postPayloadsFormat
import models.{Post, PostPayload, Posts}
import org.joda.time.DateTime
import org.mockito.Matchers.{eq => mockEq}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostsControllerTest extends PlaySpec with Results with MockitoSugar with BeforeAndAfter {

  val now: DateTime = DateTime.now()
  var posts: Posts = _
  var controller: PostsController = _

  before {
    posts = mock[Posts]
    controller = new PostsController(posts)
  }

  "Posts#list" should {
    "should be valid" in {
      when(posts.all()) thenReturn Future(Seq(
        Post(123, "test title", "test text", now),
        Post(432, "test title 2", "test text 2", now)
      ))
      val result: Future[Result] = controller.list().apply(FakeRequest())
      status(result) mustBe OK
      val postsJson: JsValue = contentAsJson(result)
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

  "Posts#show" should {
    "should show post" in {
      when(posts.find(123)).thenReturn(Future(Some(Post(123, "test title", "test text", now))))
      val result: Future[Result] = controller.show(123).apply(FakeRequest())
      status(result) mustBe OK
      contentAsJson(result) mustBe JsObject(Seq(
        "id" -> JsNumber(123),
        "title" -> JsString("test title"),
        "text" -> JsString("test text"),
        "created" -> JsNumber(now.getMillis)
      ))
    }
  }

  "Posts#add" should {
    "should create new post" in {
      val payload: PostPayload = PostPayload("test title", "test text", now)
      when(posts.add(mockEq(payload))).thenReturn(Future(123))
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/posts").withBody(Json.toJson(payload))
      val result: Future[Result] = controller.add().apply(request)
      status(result) mustBe CREATED
      val resultJson: JsValue = contentAsJson(result)
      resultJson mustBe JsObject(Seq("id" -> JsNumber(123)))
    }

    "should return an error on incorrect json" in {
      val json: JsObject = Json.toJson(PostPayload("test title", "test text", now)).as[JsObject] - "title"
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/posts").withBody(json)
      val result: Future[Result] = controller.add().apply(request)
      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "error").toOption.isDefined mustBe true
    }
  }

  "Posts#delete" should {
    "should delete post" in {
      when(posts.delete(123)).thenReturn(Future(1))
      val result: Future[Result] = controller.delete(123).apply(FakeRequest())
      status(result) mustBe OK
    }

    "should return an error on deleting post with wrong id" in {
      when(posts.delete(123)).thenReturn(Future(0))
      val result: Future[Result] = controller.delete(123).apply(FakeRequest())
      status(result) mustBe BAD_REQUEST
    }
  }

  "Posts#update" should {
    "should update post" in {
      val payload: PostPayload = PostPayload("test title", "test text", now)
      when(posts.update(new Post(123, payload))).thenReturn(Future(1))
      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.toJson(payload))
      status(controller.update(123).apply(request)) mustBe OK
    }

    "should return an error on updating post with wrong id" in {
      val payload: PostPayload = PostPayload("test title", "test text", now)
      when(posts.update(new Post(123, payload))).thenReturn(Future(0))
      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.toJson(payload))
      status(controller.update(123).apply(request)) mustBe BAD_REQUEST
    }

    "should return an error on updating post with incorrect json" in {
      val payload: PostPayload = PostPayload("test title", "test text", now)
      when(posts.update(new Post(123, payload))).thenReturn(Future(1))
      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.toJson(payload).as[JsObject] - "title")
      status(controller.update(123).apply(request)) mustBe BAD_REQUEST
    }
  }
}