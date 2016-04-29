package controllers

import com.google.inject.Inject
import models.{Post, Posts}
import org.joda.time.DateTime
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.concurrent.ExecutionContext.Implicits.global

class PostsController @Inject()(posts: Posts) extends Controller {
  implicit val postsWrites: Writes[Post] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "title").write[String] and
      (JsPath \ "text").write[String] and
      (JsPath \ "created").write[DateTime]
    ) (unlift(Post.unapply))

  def list() = Action.async {
    posts.all().map(result => Ok(Json.toJson(result)))
  }
}
