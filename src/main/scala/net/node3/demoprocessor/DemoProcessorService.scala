package net.node3.demoprocessor

import scala.util.{ Try, Success, Failure }

import java.io.{ ByteArrayInputStream, InputStream, OutputStream }

import akka.actor.{ Actor, Props }

import spray.http._
import spray.http.BodyPart
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.routing._

import net.node3.demoprocessor.data._
import net.node3.demoprocessor.entities._
import net.node3.demoprocessor.models._
import net.node3.demoprocessor.render._
import net.node3.demoprocessor.services._

class DemoProcessorActor extends Actor with DemoProcessorService {
  def actorRefFactory = context
  def receive = runRoute(routes)
  val demoService = new DemoServiceImpl(new DemoRepositoryImpl(DB.db))
}

trait DemoProcessorService extends HttpService {
  import spray.json._
  import spray.json.DefaultJsonProtocol._
  import DemoProtocols.demoProcessResponseFormat

  def demoService: DemoService

  val routes = pathPrefix("process") {
    respondWithMediaType(`application/json`) {
      path(IntNumber) { id =>
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

  def processDemo(engine: Engines.Value) = {
    entity(as[MultipartFormData]) { data =>
      complete {
        import WadProtocol._

        val demo = data.fields.foldLeft(Demo.default) { (acc, item) =>
          item.name.map { name =>
            if(name == "file") {
              acc.copy(data = Some(item.entity.data.toByteArray))
            } else if (name == "wads") {
              val wads = item.entity.data.asString.parseJson.convertTo[Seq[Wad]]
              acc.copy(wads = wads.map(_.toEntity))
            } else {
              acc
            }
          }.getOrElse(acc)
        }

        demoService.renderDemo(engine, demo) match {
          case Success(demo) => DemoProcessResponse(demo.renderId)
          case Failure(ex) => {
            println(ex)
            ex.printStackTrace
            StatusCodes.InternalServerError -> "Could not render this demo"
          }
        }
      }
    }
  }

  def getRender(id: Int): Int = id
  def stopRender(id: Int): Int = id
}
