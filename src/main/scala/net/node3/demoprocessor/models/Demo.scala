package net.node3.demoprocessor.models

import spray.json._
import spray.json.DefaultJsonProtocol._

object DemoProtocols extends DefaultJsonProtocol {
  implicit val demoProcessResponseFormat = jsonFormat2(DemoProcessResponse.apply)
}

object DemoProcessResponse {
  val empty = DemoProcessResponse("", 0)
}

case class DemoProcessResponse(val name: String, val length: Long)

