package models

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}

case class User(identifier: String, roles: List[Role], permissions: List[Permission]) extends Subject;
