package net.node3.demoprocessor.services

import scala.util.Try

import net.node3.demoprocessor.entities._
import net.node3.demoprocessor.data._

trait DemoService {
  def renderDemo(engine: Engines.Value, data: Array[Byte]): Try[Demo]
}

class DemoServiceImpl(val demoRepo: DemoRepository) extends DemoService {
  def renderDemo(engine: Engines.Value, data: Array[Byte]): Try[Demo] = ???
}
