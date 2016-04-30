package controllers

import com.google.inject.Inject
import models.Posts.postsFormat
import models.{Post, Posts}
import play.api.libs.json.{Json, _}
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostsController @Inject()(posts: Posts) extends Controller {
  def list() = Action.async {
    posts.all().map(result => Ok(Json.toJson(result)))
  }

  def add() = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[Post].fold(
      errors => Future(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      post => posts.add(post).map(id => Created(Json.obj("id" -> id)))
    )
  }
}
