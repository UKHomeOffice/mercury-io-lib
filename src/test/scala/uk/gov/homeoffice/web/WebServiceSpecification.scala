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

  def withRoutes(routes: PartialFunction[RequestHeader, Handler])(test: WebService => MatchResult[_]) = {
    Server.withRouter()(routes){ implicit port =>
      WsTestClient.withClient { wsClient =>
        test(WebService(new URL(s"http://localhost:$port"), wsClient))
      }
    }
  }
}