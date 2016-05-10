package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.google.inject.Inject
import models.Posts.{postPayloadsFormat, postsFormat}
import models._
import play.api.libs.json.{Json, _}
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostsController @Inject()(posts: Posts, actionBuilder: ActionBuilders) extends Controller {
  def list() = Action.async {
    posts.all().map(result => Ok(Json.obj("posts" -> Json.toJson(result).as[JsArray])))
  }

  def show(id: Long) = Action.async {
    posts.find(id).map(result => result.map(post => Ok(postJson(post))).getOrElse(NotFound("no post")))
  }

  def add() = adminAction()(BodyParsers.parse.json) { request =>
    (request.body \ "post").validate[PostPayload].fold(
      errors => Future(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      payload => posts.add(payload).map(id => Created(postJson(new Post(id, payload))))
    )
  }

  def adminAction() = actionBuilder.RestrictAction(RoleNames.ADMIN_ROLE).defaultHandler()

  def delete(id: Long) = adminAction() {
    posts.delete(id).map(result => if (result > 0) NoContent else BadRequest("not ok"))
  }

  def update(id: Long) = adminAction()(BodyParsers.parse.json) { request =>
    (request.body \ "post").validate[PostPayload].fold(
      errors => Future(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      payload => posts.update(new Post(id, payload)).map(result => if (result > 0) NoContent else BadRequest("not ok"))
    )
  }

  private def postJson(post: Post) = Json.obj("post" -> Json.toJson(post))
}
