package net.node3.demoprocessor.render

object Engines extends Enumeration {
  import net.node3.demoprocessor.config.DemoConfig

  private val conf = DemoConfig()

  type Engine = Value

  val Zandronum = Value

  class EnginePath(val engine: Engine, val conf: DemoConfig) {
    def path(): String = engine match {
      case Zandronum => conf.zandronumBin
    }
  }

  implicit def path(engine: Engine) = new EnginePath(engine, conf)
}
