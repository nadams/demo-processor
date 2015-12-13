package net.node3.demoprocessor

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

class DemoProcessorActor extends Actor with DemoProcessorService {
  def actorRefFactory = context
  def receive = runRoute(routes)
}

trait DemoProcessorService extends HttpService {
  val config = DemoConfig()

  val routes =
    path("process") {
      post {
        respondWithMediaType(`application/json`) {
          complete {
            s"""
              { "test": "${config.zandronumBin}" }
            """
          }
        }
      }
    }
}
