package uk.gov.homeoffice.web

import java.net.URL
import play.api.libs.ws.{WSClient, WSRequest}

class WebService(val host: URL, val wsClient: WSClient) {
  val endpoint: String => WSRequest = { url =>
    wsClient url s"$host/${url.dropWhile(_ == '/')}"
  }
}

object WebService {
  def apply(host: URL, wsClient: WSClient) = new WebService(host, wsClient)
}