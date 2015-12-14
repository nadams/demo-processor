package net.node3.demoprocessor.render

import scala.concurrent.Future

import akka.actor._

trait DemoRenderer {
  def render(data: Array[Byte]) : Future[RenderResult]
}

class DemoRendererImpl(val enginePath: String) extends DemoRenderer with Actor {
  import context.dispatcher

  override def render(data: Array[Byte]): Future[RenderResult] = {
    Future {
      RenderResult(Seq.empty)
    }
  }

  def receive = {
    case x: Array[Byte] => render(x)
  }
}
