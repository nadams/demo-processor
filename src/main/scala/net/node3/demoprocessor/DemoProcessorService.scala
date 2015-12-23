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

  import ErrorProtocol._

  def demoService: DemoService

  val routes = pathPrefix("render") {
    respondWithMediaType(`application/json`) {
      path("zandronum") {
        post {
          processDemo(Engines.Zandronum)
        }
      } ~
      path(Segment / "file") { id =>
        pathEnd {
          get {
            getRenderData(id)
          }
        }
      } ~
      path(Segment) { id =>
        pathEnd {
          get {
            getRender(id)
          } ~
          delete {
            complete {
              stopRender(id)
            }
          }
        }
      }
    }
  }

  def processDemo(engine: Engines.Value) = {
    entity(as[MultipartFormData]) { data =>
      complete {
        val demo = data.fields.foldLeft(Demo.default) { (acc, item) =>
          item.name.map { name =>
            if(name == "file") {
              acc.copy(data = Some(item.entity.data.toByteArray))
            } else if (name == "wads") {
              import WadProtocol._

              val wads = item.entity.data.asString.parseJson.convertTo[Seq[Wad]]
              acc.copy(wads = wads.map(_.toEntity))
            } else {
              acc
            }
          }.getOrElse(acc)
        }

        import DemoProtocol._

        demoService.renderDemo(engine, demo) match {
          case Success(demo) => StatusCodes.Accepted -> DemoProcessResponse.toModel(demo)
          case Failure(ex) => {
            ex.printStackTrace
            StatusCodes.InternalServerError -> "Could not render this demo"
          }
        }
      }
    }
  }

  def getRender(id: String) = complete {
    import DemoProtocol._

    demoService.getRender(id) match {
      case Success(demo) => demo match {
        case Some(d) => StatusCodes.OK -> DemoProcessResponse.toModel(d)
        case None => StatusCodes.NotFound -> Error("Demo not found")
      }
      case Failure(ex) => {
        ex.printStackTrace
        StatusCodes.InternalServerError -> "Could not get the requested render"
      }
    }
  }

  def stopRender(id: String) = ???

  def getRenderData(id: String) = respondWithMediaType(`application/octet-stream`) {
    complete {
      Array[Byte]()
    }
  }
}
