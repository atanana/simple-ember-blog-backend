package utils.deadbolt

import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import be.objectify.deadbolt.scala.cache.HandlerCache
import javax.inject.Singleton

@Singleton
class BlogHandlerCache extends HandlerCache {
  val defaultHandler: DeadboltHandler = new BlogDeadboltHandler

  override def apply(): DeadboltHandler = defaultHandler

  override def apply(handlerKey: HandlerKey): DeadboltHandler = defaultHandler
}