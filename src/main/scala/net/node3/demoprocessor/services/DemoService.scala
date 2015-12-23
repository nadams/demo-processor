package net.node3.demoprocessor.services

import scala.util.Try

import net.node3.demoprocessor.entities._
import net.node3.demoprocessor.data._

trait DemoService {
  def renderDemo(engine: Engines.Value, demo: Demo): Try[Option[Demo]]
  def getRender(id: String): Try[Option[Demo]]
  def stopRender(id: String): Try[Option[Demo]]
}

class DemoServiceImpl(val demoRepo: DemoRepository) extends DemoService {
  def renderDemo(engine: Engines.Value, demo: Demo) = Try {
    val newDemo = demoRepo.insert(demo)

    println(newDemo)
    Some(newDemo)
  }

  def getRender(id: String): Try[Option[Demo]] = Try(demoRepo.getByRenderId(id))

  def stopRender(id: String): Try[Option[Demo]] = getRender(id).map { demoTry =>
    demoTry.map { demo =>
      if(demo.status == DemoStatus.Rendering) {
        // cancel demo process
        demoRepo.update(demo.copy(status = DemoStatus.Cancelled))
      } else {
        demo
      }
    }
  }
}
