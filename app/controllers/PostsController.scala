package controllers

import com.google.inject.Inject
import models.Posts.{postsFormat, postPayloadsFormat}
import models.{Post, PostPayload, Posts}
import play.api.libs.json.{Json, _}
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostsController @Inject()(posts: Posts) extends Controller {
  def list() = Action.async {
    posts.all().map(result => Ok(Json.obj("posts" -> Json.toJson(result).as[JsArray])))
  }

  def show(id: Long) = Action.async {
    posts.find(id).map(result => result.map(post => Ok(postJson(post))).getOrElse(NotFound("no post")))
  }

  def add() = Action.async(BodyParsers.parse.json) { request =>
    (request.body \ "post").validate[PostPayload].fold(
      errors => Future(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      payload => posts.add(payload).map(id => Created(postJson(new Post(id, payload))))
    )
  }

  def delete(id: Long) = Action.async {
    posts.delete(id).map(result => if (result > 0) NoContent else BadRequest("not ok"))
  }

  def update(id: Long) = Action.async(BodyParsers.parse.json) { request =>
    (request.body \ "post").validate[PostPayload].fold(
      errors => Future(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      payload => posts.update(new Post(id, payload)).map(result => if (result > 0) NoContent else BadRequest("not ok"))
    )
  }

  private def postJson(post: Post) = Json.obj("post" -> Json.toJson(post))
}
