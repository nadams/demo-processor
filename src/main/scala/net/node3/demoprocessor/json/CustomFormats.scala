package net.node3.demoprocessor.json

import scala.util._

import java.time._

import spray.json._

object CustomFormats {
  implicit object ZonedDateTimeFormat extends JsonFormat[ZonedDateTime] {
    def write(x: ZonedDateTime) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => Try(ZonedDateTime.parse(x)) match {
        case Success(date) => date
        case Failure(ex) => {
          ex.printStackTrace
          error(x)
        }
      }
      case x => error(x)
    }

    private def error(x: AnyRef) = deserializationError("Expected ZonedDateTime as JsString, but got " + x)
  }

  implicit object InstantFormat extends JsonFormat[Instant] {
    def write(x: Instant) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => Try(Instant.parse(x)) match {
        case Success(date) => date
        case Failure(ex) => {
          ex.printStackTrace
          error(x)
        }
      }
      case x => error(x)
    }

    private def error(x: AnyRef) = deserializationError("Expected Instant as JsString, but got " + x)
  }
}

