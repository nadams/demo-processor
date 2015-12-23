package net.node3.demoprocessor.data

import play.api.db.PooledDatabase

object DB {
  import play.api.Configuration
  import com.typesafe.config.ConfigFactory

  private val defaultdb = "default"

  val dbConf = ConfigFactory.load().getConfig(s"db.$defaultdb")

  val db = new PooledDatabase(defaultdb, new Configuration(dbConf))
}
