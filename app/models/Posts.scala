package models

import com.google.inject.Inject
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import com.github.tototoshi.slick.MySQLJodaSupport._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Format, JsPath}
import slick.lifted.ProvenShape

import scala.concurrent.Future

case class PostPayload(title: String, text: String, created: DateTime)

case class Post(id: Long, title: String, text: String, created: DateTime) {
  def this(id:Long, payload:PostPayload) = this(id, payload.title, payload.text, payload.created)
}

class PostsTableDef(tag: Tag) extends Table[Post](tag, "post") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def title = column[String]("title", O.Length(255, varying = true))

  def text = column[String]("text")

  def created = column[DateTime]("created", O.Default(DateTime.now()))

  override def * : ProvenShape[Post] = (id, title, text, created) <>(Post.tupled, Post.unapply)
}

class Posts @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val posts = TableQuery[PostsTableDef]

  def add(payload: PostPayload): Future[Int] = {
    dbConfig.db.run(posts += new Post(0, payload))
  }

  def delete(id: Long) = {
    dbConfig.db.run(posts.filter(_.id === id).delete)
  }

  def find(id: Long) = {
    dbConfig.db.run(posts.filter(_.id === id).result.headOption)
  }

  def all(): Future[Seq[Post]] = {
    dbConfig.db.run(posts.result)
  }

  def update(post: Post) = {
    dbConfig.db.run(posts.filter(_.id === post.id).update(post))
  }
}

object Posts {
  implicit val postsFormat: Format[Post] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "title").format[String] and
      (JsPath \ "text").format[String] and
      (JsPath \ "created").format[DateTime]
    ) (Post.apply, unlift(Post.unapply))

  implicit val postPayloadsFormat: Format[PostPayload] = (
    (JsPath \ "title").format[String] and
      (JsPath \ "text").format[String] and
      (JsPath \ "created").format[DateTime]
    ) (PostPayload.apply, unlift(PostPayload.unapply))
}