package net.node3.demoprocessor.entities

import java.time.ZonedDateTime

case class Demo(
  val id: Int,
  val processId: String,
  val status: DemoStatus.Value,
  val created: ZonedDateTime,
  val completed: Option[ZonedDateTime],
  val wads: Seq[DemoWad]
)

object DemoStatus extends Enumeration {
  val Created = Value(1)
  val Rendering = Value(2)
  val Dead = Value(3)
  val Finished = Value(4)
}
