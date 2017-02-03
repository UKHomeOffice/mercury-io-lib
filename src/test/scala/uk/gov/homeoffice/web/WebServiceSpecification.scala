package uk.gov.homeoffice.web

import java.net.URL
import play.api.mvc.{Handler, RequestHeader}
import play.api.test.WsTestClient
import play.core.server.Server
import org.specs2.matcher.MatchResult
import org.specs2.mutable.SpecificationLike

trait WebServiceSpecification {
  this: SpecificationLike =>

  sequential
  isolated

  /**
    * Firstly, given a PartialFunction of RequestHeader => Handler acting has some endpoints to test against.
    * Secondly a function to run against the endpoints, where the function is given a WebService to execute a particular endpoint,
    * and the result of the test function gives appropriate assertions. For example:
    * <pre>
    *   """accept route starting with "/"""" in routes {
    *     case GET(p"/my-get-route") => Action {
    *       Ok
    *     } { webService =>
    *       webService.endpoint("/my-get-route").get() must beLike[WSResponse] {
    *         case response => response.status mustEqual OK
    *       }.await
    *     }
    * </pre>
    */
  val routes = (rs: PartialFunction[RequestHeader, Handler]) => (test: WebService => MatchResult[_]) =>
    Server.withRouter()(rs) { implicit port =>
      WsTestClient.withClient { wsClient =>
        test(WebService(new URL(s"http://localhost:$port"), wsClient))
      }
    }
}