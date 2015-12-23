package net.node3.demoprocessor.models

import java.time.Instant

import spray.json._
import spray.json.DefaultJsonProtocol._

import net.node3.demoprocessor.entities.Demo
import net.node3.demoprocessor.json.CustomFormats._

object DemoProtocol extends DefaultJsonProtocol {
  import WadProtocol.format

  implicit val formatDemo = jsonFormat5(DemoProcessResponse.apply)
}

object DemoProcessResponse {
  def toModel(entity: Demo) = DemoProcessResponse(
    entity.renderId,
    entity.status.toString,
    entity.created,
    entity.completed,
    entity.wads.map(Wad.toModel(_))
  )
}

case class DemoProcessResponse(
  val renderId: String,
  val status: String,
  val created: Instant,
  val completed: Option[Instant],
  val wads: Seq[Wad]
)

