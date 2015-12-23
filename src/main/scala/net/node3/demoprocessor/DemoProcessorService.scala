package net.node3.demoprocessor

import scala.util.{ Try, Success, Failure }

import java.io.{ ByteArrayInputStream, InputStream, OutputStream }

import akka.actor.{ Actor, Props }

import spray.http._
import spray.http.BodyPart
import spray.http.MediaTypes._
import spray.http.HttpHeaders._
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling._
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
            stopRender(id)
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
        demoAction(demoService.renderDemo(engine, demo))(StatusCodes.Accepted -> DemoProcessResponse.toModel(_))
      }
    }
  }

  def getRender(id: String) = complete {
    demoAction(demoService.getRender(id))(demoSuccess)
  }

  def stopRender(id: String) = complete {
    demoAction(demoService.stopRender(id))(demoSuccess)
  }

  def getRenderData(id: String) = respondWithMediaType(`application/octet-stream`) {
    respondWithHeader(`Content-Disposition`("attachment", Map("filename" -> "demo.cld"))) {
      complete {
        demoAction(demoService.getRender(id))(StatusCodes.OK -> _.data)
      }
    }
  }

  private def demoSuccess(demo: Demo): ToResponseMarshallable = {
    import DemoProtocol._

    StatusCodes.OK -> DemoProcessResponse.toModel(demo)
  }

  private def demoAction(in: Try[Option[Demo]])(action: Demo => ToResponseMarshallable): ToResponseMarshallable =
    in match {
      case Success(demo) => demo match {
        case Some(d) => action(d)
        case None => StatusCodes.NotFound -> Error("Demo not found")
      }
      case Failure(ex) => {
        ex.printStackTrace
        StatusCodes.InternalServerError -> "Could not get the requested render"
      }
    }
}
