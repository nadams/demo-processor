package net.node3.demoprocessor.data

import play.api.db.PooledDatabase

object DB {
  import play.api.Configuration
  import net.node3.demoprocessor.config.DemoConfig

  private val conf = new Configuration(DemoConfig.conf.getConfig("db"))

  val db = new PooledDatabase("default", conf)
}
