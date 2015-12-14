package net.node3.demoprocessor

import java.io.{ ByteArrayInputStream, InputStream, OutputStream }

import akka.actor.{ Actor, Props }

import spray.http._
import spray.http.BodyPart
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.routing._

import net.node3.demoprocessor.models._

class DemoProcessorActor extends Actor with DemoProcessorService {
  def actorRefFactory = context
  def receive = runRoute(routes)
}

trait DemoProcessorService extends HttpService {
  import DemoProtocols.demoProcessResponseFormat

  val routes = path("process") {
    post {
      processDemo()
    }
  }

  def processDemo() = {
    respondWithMediaType(`application/json`) {
      entity(as[MultipartFormData]) { data =>
        complete {
          data.fields.headOption.map { file =>
            DemoProcessResponse(file.name.get, file.entity.data.length)
          }
        }
      }
    }
  }
}
