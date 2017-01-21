package uk.gov.homeoffice.io

import scala.io.Codec
import scala.util.Try

/**
  * Resource that could be a URL, from classpath, or File for example, where the contents can be given "as" something else.
  * @param r The actual resource by a specific type such as URL
  * @param encoding Codec i.e. the encoding of the resource
  * @tparam R This is the actual type of the resource
  */
case class Resource[R](r: R, encoding: Codec = Codec.UTF8) {
  /**
    * Give this resource "as" something (i.e. some T)
    * @param ev Evidence that manages the reading of the resource "as" the required format
    * @tparam T The format of what to read this resource into such as generating JSON (JValue) from resource
    * @return Success of the required formatting or Failure encapsulting the exception
    */
  def as[T](implicit ev: Readable[R, T]): Try[T] = implicitly[Readable[R, T]].read(r)(encoding)
}