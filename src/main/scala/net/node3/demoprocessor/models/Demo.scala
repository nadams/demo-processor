package net.node3.demoprocessor.models

import spray.json._
import spray.json.DefaultJsonProtocol._

object DemoProtocols extends DefaultJsonProtocol {
  implicit val demoProcessResponseFormat = jsonFormat1(DemoProcessResponse.apply)
}

case class DemoProcessResponse(val renderId: String)

