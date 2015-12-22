package net.node3.demoprocessor.entities

import net.node3.demoprocessor.config.DemoConfig

object Engines extends Enumeration {
  private val conf = DemoConfig()

  val Zandronum = Value(1)

  class EnginePath(val engine: Engines.Value, val conf: DemoConfig) {
    def path(): String = engine match {
      case Zandronum => conf.zandronumBin
    }
  }

  implicit def path(engine: Engines.Value) = new EnginePath(engine, conf)
}
