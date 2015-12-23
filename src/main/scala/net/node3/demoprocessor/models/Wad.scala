package net.node3.demoprocessor.models

import spray.json._
import spray.json.DefaultJsonProtocol._

import net.node3.demoprocessor.entities.DemoWad

object WadProtocol extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Wad.apply)
}

case class Wad(val filename: String, val checksum: String) {
  def toEntity() = DemoWad(0, filename, checksum)
}

object Wad {
  def toModel(wad: DemoWad) = Wad(wad.filename, wad.checksum)
}


