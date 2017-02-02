package uk.gov.homeoffice.web

import java.net.URL
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.ws.{WSClient, WSRequest}

/**
  * Convenient wrapper to Play's Web service client, which also makes life simpler when coding tests against a stubbed service @see WebServiceSpec
  * <pre>
  * Instantiate it by simply providing the URL that represents the webservice to interface with e.g.
  * val webService = WebService(URL("http://localhost:9100"))
  *
  * and then use as:
  * webService.endpoint("my-get-route").get()
  * webService.endpoint("my-post-route").post()
  *
  * noting that the underlying WSClient will have been created for you, along with its necessary actor system and materializer.
  *
  * Or, provide your own WSClient e.g.
  * implicit val system = ActorSystem()
  * implicit val materializer = ActorMaterializer()
  * val wsClient = AhcWSClient()
  *
  * val webService = WebService(URL("http://localhost:9100"), wsClient)
  * </pre>
  * @param host URL
  * @param wsClient WSClient
  */
class WebService(val host: URL, val wsClient: WSClient) {
  val endpoint: String => WSRequest = { url =>
    wsClient url s"$host/${url.dropWhile(_ == '/')}"
  }
}

object WebService {
  def apply(host: URL, wsClient: WSClient) = new WebService(host, wsClient)

  def apply(host: URL) = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val wsClient = AhcWSClient()

    sys addShutdownHook {
      system.terminate()
      wsClient.close()
    }

    new WebService(host, wsClient)
  }
}