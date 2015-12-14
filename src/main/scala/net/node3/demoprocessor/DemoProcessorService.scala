package net.node3.demoprocessor

import java.io.{ ByteArrayInputStream, InputStream, OutputStream }

import akka.actor.{ Actor, Props }

import spray.http._
import spray.http.BodyPart
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.routing._

import net.node3.demoprocessor.models._
import net.node3.demoprocessor.render._

class DemoProcessorActor extends Actor with DemoProcessorService {
  def actorRefFactory = context
  def receive = runRoute(routes)
}

trait DemoProcessorService extends HttpService {
  import spray.json._
  import spray.json.DefaultJsonProtocol._
  import DemoProtocols.demoProcessResponseFormat

  val routes = pathPrefix("process") {
    path(IntNumber) { id =>
      respondWithMediaType(`application/json`) {
        pathEnd {
          get {
            complete {
              Map("get" -> getRender(id))
            }
          } ~
          delete {
            complete {
              Map("delete" -> stopRender(id))
            }
          }
        }
      } ~
      path("zandronum") {
        post {
          processDemo(Engines.Zandronum)
        }
      }
    }
  }

  def processDemo(engine: Engines.Engine) = {
    entity(as[MultipartFormData]) { data =>
      complete {
        data.fields.headOption.map { file =>
          DemoProcessResponse(file.name.get, file.entity.data.length)
        }
      }
    }
  }

  def getRender(id: Int): Int = id
  def stopRender(id: Int): Int = id
}
