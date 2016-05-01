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
    posts.all().map(result => Ok(Json.toJson(result)))
  }

  def add() = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[PostPayload].fold(
      errors => Future(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      payload => posts.add(payload).map(id => Created(Json.obj("id" -> id)))
    )
  }

  def delete(id: Long) = Action.async {
    posts.delete(id).map(result => if (result > 0) Ok("ok") else BadRequest("not ok"))
  }

  def update(id: Long) = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[PostPayload].fold(
      errors => Future(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      payload => posts.update(new Post(id, payload)).map(result => if (result > 0) Ok("ok") else BadRequest("not ok"))
    )
  }
}
