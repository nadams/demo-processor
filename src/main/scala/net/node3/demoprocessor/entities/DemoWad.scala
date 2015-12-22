package net.node3.demoprocessor.entities

case class DemoWad(val id: Int, val filename: String, val checksum: String)

object DemoWad {
  import anorm._
  import anorm.SqlParser._

  val singleRowParser =
    int("id") ~
    str("filename") ~
    str("checksum") map flatten

  val multiRowParser = singleRowParser *

  def apply(x: (Int, String, String)): DemoWad = DemoWad(x._1, x._2, x._3)
}
