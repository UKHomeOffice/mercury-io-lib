package uk.gov.homeoffice.web

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.mvc.Results._
import play.api.mvc.{Action, BodyParsers}
import play.api.routing.sird._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

class WebServiceSpec(implicit env: ExecutionEnv) extends Specification with BodyParsers with WebServiceSpecification {
  "Web service" should {
    """accept route starting with "/" and "get" subbed out""" in routes {
      case GET(p"/my-get-route") => Action {
        Ok("Good Get")
      }
    } { webService =>
      webService.endpoint("/my-get-route").get() must beLike[WSResponse] {
        case response =>
          response.status mustEqual OK
          response.body mustEqual "Good Get"
      }.await
    }

    """accept route not starting with "/" and "get" subbed out""" in routes {
      case GET(p"/my-get-route") => Action {
        Ok("Good Get")
      }
    } { webService =>
      webService.endpoint("my-get-route").get() must beLike[WSResponse] {
        case response =>
          response.status mustEqual OK
          response.body mustEqual "Good Get"
      }.await
    }

    """accept route starting with "/" and "post" subbed out""" in routes {
      case POST(p"/my-post-route") => Action(parse.json) { request =>
        Ok(Json.obj("Good Post" -> request.body))
      }
    } { webService =>
      webService.endpoint("/my-post-route").post(Json.parse("""{ "my": "json" }""")) must beLike[WSResponse] {
        case response =>
          response.status mustEqual OK
          response.json mustEqual Json.obj("Good Post" -> Json.obj("my" -> "json"))
      }.await
    }

    """accept route not starting with "/" and "post" subbed out""" in routes {
      case POST(p"/my-post-route") => Action(parse.json) { request =>
        Ok(Json.obj("Good Post" -> request.body))
      }
    } { webService =>
      webService.endpoint("my-post-route").post(Json.parse("""{ "my": "json" }""")) must beLike[WSResponse] {
        case response =>
          response.status mustEqual OK
          response.json mustEqual Json.obj("Good Post" -> Json.obj("my" -> "json"))
      }.await
    }
  }
}