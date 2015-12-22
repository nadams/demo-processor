package net.node3.demoprocessor.data

import play.api.db.Database

import net.node3.demoprocessor.entities._

trait DemoRepository {
  def insert(demo: Demo): Demo
}

class DemoRepositoryImpl(val db: Database) extends DemoRepository {
  import java.sql.Connection

  import anorm._
  import anorm.SqlParser._

  def insert(demo: Demo): Demo = db.withTransaction { implicit conection =>
    var newDemo: Demo = demo.copy(id =
      SQL"""
        INSERT INTO demo (process_id, status, created)
        VALUES (${demo.processId}, ${demo.status.id}, ${demo.created})
        RETURNING id
      """
      .as(scalar[Int] single)
    )

    newDemo = newDemo.copy(wads = insertWads(newDemo.wads))

    linkDemoWads(newDemo)

    newDemo
  }

  private def linkDemoWads(demo: Demo)(implicit connection: Connection) =
    demo.wads.foreach { wad =>
      SQL"""
        INSERT INTO demo_wad (demo_id, wad_id)
        SELECT ${demo.id}, ${wad.id}
        WHERE NOT EXISTS (
          SELECT *
          FROM demo_wad
          WHERE demo_id = ${demo.id} AND wad_id = ${wad.id}
        )
      """
    }

  private def insertWads(wads: Seq[DemoWad])(implicit connection: Connection): Seq[DemoWad] =
    wads.map { wad =>
      if(wad.id > 0) wad
      else
        wad.copy(id =
          SQL"""
            INSERT INTO wad (filename, checksum)
            VALUES(${wad.filename}, ${wad.checksum})
            RETURNING id
          """
          .as(scalar[Int] single)
        )
    }
}
