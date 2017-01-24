package uk.gov.homeoffice.json

import java.net.URL
import scala.io.{Codec, Source}
import scala.util.Try
import play.api.libs.json.Json._
import play.api.libs.json._
import com.google.json.JsonSanitizer._
import org.json4s.JValue
import org.json4s.JsonDSL._
import uk.gov.homeoffice.io.IO

/**
  * Singleton to Json resource functionality, if you do not want to mixin the associated trait
  */
object Json extends Json

/**
  * Read JSON resource - Handle both Json4s and Play JSON
  */
trait Json extends IO {
  val jValue: JsValue => JValue = js => {
    import org.json4s.jackson.JsonMethods._

    parse(stringify(js))
  }

  val jsValue: JValue => JsValue = jv => {
    import org.json4s.jackson.JsonMethods.{compact, render}

    parse(compact(render(jv)))
  }

  implicit val jValueFormat = new Format[JValue] {
    def reads(jsValue: JsValue) = JsSuccess(jValue(jsValue))

    def writes(jValue: JValue) = jsValue(jValue)
  }

  def flatten(jv: JValue): JsObject = flatten(jsValue(jv))

  def flatten(js: JsValue): JsObject = {
    def flatten(js: JsValue, prefix: String): Seq[JsValue] = {
      def concat(prefix: String, key: String): String = if (prefix.nonEmpty) s"$prefix.$key" else key

      js.as[JsObject].fieldSet.toSeq.flatMap { case (k, v) =>
        v match {
          case JsBoolean(x) => Seq(obj(concat(prefix, k) -> x))
          case JsNumber(x) => Seq(obj(concat(prefix, k) -> x))
          case JsString(x) => Seq(obj(concat(prefix, k) -> x))
          case JsArray(seq) => seq.zipWithIndex.flatMap { case (x, i) =>
            x match {
              case _: JsObject | JsArray(_) => flatten (x, concat(prefix, s"$k[$i]"))
              case JsBoolean(_) => Seq(obj(concat(prefix, k) -> x))
              case JsNumber(_) => Seq(obj(concat(prefix, k) -> x))
              case JsString(_) => Seq(obj(concat(prefix, k) -> x))
              case _ => Seq(obj(concat(prefix, k) -> JsNull))
            }
          }
          case x: JsObject => flatten(x, concat(prefix, k))
          case _ => Seq(obj(concat(prefix, k) -> JsNull))
        }
      }
    }

    flatten(js, "").foldLeft(JsObject(Nil))(_ ++ _.as[JsObject])
  }

  /**
    * Use this method instead "parse" exposed by JSON4s, as this one will use Google's "sanitize" of a given String before generating a JValue.
    * It is assumed that the given string starts with { and ends with }
    * @param s String The text representing JSON to be generated to a JValue
    * @return Try[JValue] As the parsing may fail a Success of JValue or Failure is generated
    */
  def toJson(s: String): Try[JValue] = Try {
    import org.json4s.jackson.JsonMethods._

    val string = s.replaceAll("[\n\r]", "").trim

    if (string.startsWith("{") && string.endsWith("}")) parse(sanitize(string))
    else throw new Exception(s"The given string does not start with { and/or end with }, as JSON should be: $string")
  }

  /**
    * Acquire JSON from given URL e.g.
    * fromClasspath(path("/test.json"))
    * @param url URL to resource to read
    * @param encoding Codec of resource which defaults to UTF-8
    * @param adapt Optional function to adapt the content - Note that it is the String that is read in, that is adapted before generating JSON
    * @return Try[JValue] Success of JValue or Failure
    */
  @deprecated(message = "Use uk.gov.homeoffice.io.Resource", since = "11th March 2016")
  def jsonFromUrlContent(url: URL, encoding: Codec = Codec.UTF8)(implicit adapt: String => String = s => s): Try[JValue] = {
    import org.json4s.jackson.JsonMethods._

    urlContentToString(url, encoding)(adapt) map {
      parse(_)
    }
  }

  /**
    * Acquire JSON from class path e.g.
    * jsonFromClasspath(path("/test.json"))
    * @param classpath String class path of resource
    * @param encoding Codec of resource which defaults to UTF-8
    * @param adapt Optional function to adapt the content - Note that it is the String that is read in, that is adapted before generating JSON
    * @return Try[JValue] Success of JValue or Failure
    */
  @deprecated(message = "Use uk.gov.homeoffice.io.Resource", since = "11th March 2016")
  def jsonFromClasspath(classpath: String, encoding: Codec = Codec.UTF8)(implicit adapt: String => String = s => s): Try[JValue] =
    urlFromClasspath(classpath) flatMap { jsonFromUrlContent(_, encoding)(adapt) }

  /**
    * Acquire JSON from file path e.g.
    * jsonFromFilepath(path("src/test/test.json"))
    * @param filepath String file path of resource
    * @param encoding Codec of resource which defaults to UTF-8
    * @param adapt Optional function to adapt the content - Note that it is the String that is read in, that is adapted before generating JSON
    * @return Try[JValue] Success of JValue or Failure
    */
  @deprecated(message = "Use uk.gov.homeoffice.io.Resource", since = "11th March 2016")
  def jsonFromFilepath(filepath: String, encoding: Codec = Codec.UTF8)(implicit adapt: String => String = s => s): Try[JValue] = Try {
    import org.json4s.jackson.JsonMethods._

    parse(adapt(Source.fromFile(filepath)(encoding).getLines().mkString))
  }

  /**
    * Generate JSON representation of a Throwable
    * @param t Throwable to be converted to JSON
    * @return JValue the JSON created from the given Throwable
    */
  def toJson(t: Throwable): JValue =
    "errorStackTrace" ->
      ("errorMessage" -> t.getMessage) ~
      ("stackTrace" -> t.getStackTrace.toList.map { st =>
        ("file" -> st.getFileName) ~
          ("class" -> st.getClassName) ~
          ("method" -> st.getMethodName) ~
          ("line" -> st.getLineNumber)
      })

  implicit class JFieldOps(jfield: (String, JValue)) {
    def merge(json: JValue) = (jfield: JValue) merge json
  }

  implicit class JValueOps(json: JValue) {
    /** In scope of the following functions */
    implicit val j = json

    def replace[V : JValuable](transformation: (JValue, V)) = JValuable replace transformation

    def transform[V : JValuable](transformation: (JValue, V => V)) = JValuable transform transformation
  }
}