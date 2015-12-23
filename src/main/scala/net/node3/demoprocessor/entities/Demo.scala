package net.node3.demoprocessor.entities

import java.time._

case class Demo(
  val id: Int,
  val renderId: String,
  val status: DemoStatus.Value,
  val created: ZonedDateTime,
  val completed: Option[ZonedDateTime],
  val wads: Seq[DemoWad],
  val data: Option[Array[Byte]]
)

object Demo {
  import java.util.UUID

  private val utc = ZoneOffset.UTC

  def default = Demo(0, UUID.randomUUID.toString, DemoStatus.Created, ZonedDateTime.now(utc), None, Seq.empty, None)
}

object DemoStatus extends Enumeration {
  val Created = Value(1)
  val Rendering = Value(2)
  val Dead = Value(3)
  val Finished = Value(4)
}
