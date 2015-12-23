package net.node3.demoprocessor.models

import spray.json._
import spray.json.DefaultJsonProtocol._

object ErrorProtocol extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(Error)
}

case class Error(val message: String)
