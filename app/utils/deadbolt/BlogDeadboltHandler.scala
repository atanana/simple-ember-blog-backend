package utils.deadbolt

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import models.{Role, RoleNames, User}
import play.api.mvc.{Request, Result, Results}
import utils.SessionValues

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BlogDeadboltHandler extends DeadboltHandler {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future {None}

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future {None}

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] =
    Future {
      request.subject.orElse {
        request.session.get(SessionValues.USER_ID) match {
          case Some(userId) => Some(User("admin", List(Role(RoleNames.ADMIN_ROLE)), List()))
          case _ => None
        }
      }}

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    getSubject(request)
      .map(maybeSubject => maybeSubject
        .map(subject => Results.Forbidden)
        .getOrElse(Results.Unauthorized))
  }
}