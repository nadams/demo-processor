package net.node3.demoprocessor.entities

import java.time._

case class Demo(
  val id: Int,
  val renderId: String,
  val status: DemoStatus.Value,
  val created: Instant,
  val completed: Option[Instant],
  val wads: Seq[DemoWad],
  val data: Option[Array[Byte]]
)

object Demo {
  import java.util.UUID

  import anorm._
  import anorm.SqlParser._

  val singleRowParser =
    int("id") ~
    str("render_id") ~
    int("status") ~
    get[Instant]("created") ~
    get[Option[Instant]]("completed") ~
    get[Option[Array[Byte]]]("data") map flatten

  val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Int, Instant, Option[Instant], Option[Array[Byte]])): Demo =
    Demo(x._1, x._2, DemoStatus(x._3), x._4, x._5, Seq.empty, x._6)

  def default = Demo(0, UUID.randomUUID.toString, DemoStatus.Created, Instant.now, None, Seq.empty, None)
}

object DemoStatus extends Enumeration {
  val Created = Value(1)
  val Rendering = Value(2)
  val Dead = Value(3)
  val Finished = Value(4)
  val Cancelled = Value(5)
}
