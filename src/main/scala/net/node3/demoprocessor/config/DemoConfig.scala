package net.node3.demoprocessor.config

case class DemoConfig(val zandronumBin: String)

object DemoConfig {
  import com.typesafe.config.ConfigFactory

  val conf = ConfigFactory.load().getConfig("net.node3.demoprocessor")

  def apply(): DemoConfig = DemoConfig(
    conf.getString("zandronum-bin")
  )
}
