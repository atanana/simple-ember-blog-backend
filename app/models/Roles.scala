package models

import be.objectify.deadbolt.scala.models.{Role => DeadboltRole}

case class Role(name: String) extends DeadboltRole

object RoleNames {
  val ADMIN_ROLE = "admin"
}