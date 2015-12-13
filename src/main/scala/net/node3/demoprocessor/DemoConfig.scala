package net.node3.demoprocessor

import com.typesafe.config.ConfigFactory

case class DemoConfig(val zandronumBin: String)

object DemoConfig {
  val conf = ConfigFactory.load().getConfig("net.node3.demoprocessor")

  def apply(): DemoConfig = DemoConfig(
    conf.getString("zandronum-bin")
  )
}
