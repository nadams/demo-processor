package net.node3.demoprocessor.data

import play.api.db.Database

import net.node3.demoprocessor.entities._

trait DemoRepository {
  def insert(demo: Demo): Demo
  def getByRenderId(renderId: String): Option[Demo]
}

class DemoRepositoryImpl(val db: Database) extends DemoRepository with DemoSchema {
  import java.sql.Connection

  import anorm._
  import anorm.SqlParser._

  def getByRenderId(renderId: String): Option[Demo] = db.withConnection { implicit connection =>
    SQL"""
      SELECT
        d.id AS id,
        d.render_id AS render_id,
        d.status AS status,
        d.created AS created,
        d.completed AS completed,
        dd.data AS data
      FROM #$demoSchema.demo AS d
        LEFT OUTER JOIN #$demoSchema.demo_data AS dd ON d.id = dd.id
      WHERE d.render_id = $renderId
    """
    .as(Demo.singleRowParser singleOpt)
    .map(Demo(_))
    .map { demo =>
      demo.copy(wads = SQL"""
          SELECT
            w.id AS id,
            w.filename AS filename,
            TRIM(trailing from w.checksum) AS checksum
          FROM #$demoSchema.demo_wad AS dw
            INNER JOIN #$demoSchema.wad AS w ON dw.wad_id = w.id
          WHERE dw.demo_id = ${demo.id}
        """
        .as(DemoWad.multiRowParser)
        .map(DemoWad(_))
      )
    }
  }

  def insert(demo: Demo): Demo = db.withTransaction { implicit conection =>
    var newDemo: Demo = demo.copy(id =
      SQL"""
        INSERT INTO #$demoSchema.demo (render_id, status, created)
        VALUES (${demo.renderId}, ${demo.status.id}, ${demo.created})
        RETURNING id
      """
      .as(scalar[Int] single)
    )

    newDemo = newDemo.copy(wads = insertWads(newDemo.wads))

    linkDemoWads(newDemo)

    newDemo.data.foreach { data =>
      SQL"""
        INSERT INTO #$demoSchema.demo_data(id, data)
        VALUES(${newDemo.id}, $data)
      """.execute
    }

    newDemo
  }

  private def linkDemoWads(demo: Demo)(implicit connection: Connection) =
    demo.wads.foreach { wad =>
      SQL"""
        INSERT INTO #$demoSchema.demo_wad (demo_id, wad_id)
        SELECT ${demo.id}, ${wad.id}
        WHERE NOT EXISTS (
          SELECT *
          FROM #$demoSchema.demo_wad
          WHERE demo_id = ${demo.id} AND wad_id = ${wad.id}
        )
      """.execute
    }

  private def insertWads(wads: Seq[DemoWad])(implicit connection: Connection): Seq[DemoWad] =
    wads.map { wad =>
      wad.copy(id =
        SQL"""
          INSERT INTO #$demoSchema.wad (filename, checksum)
          SELECT ${wad.filename}, ${wad.checksum}
          WHERE NOT EXISTS (
            SELECT *
            FROM #$demoSchema.wad
            WHERE filename = ${wad.filename} AND checksum = ${wad.checksum}
          )
          RETURNING id
        """
        .as(scalar[Int] singleOpt)
        .getOrElse {
          SQL"""
            SELECT id
            FROM #$demoSchema.wad
            WHERE filename = ${wad.filename} AND checksum = ${wad.checksum}
          """
          .as(scalar[Int] single)
        }
      )
    }
}
