package com.assessory.play


object UPickleParser {
/*
  // We support JSON up to 100kb
  val MAX_LENGTH = 100 * 1024

  def parser[A] = new BodyParser[A] {
    override def apply(request: RequestHeader): Iteratee[Array[Byte], Either[Result, A]] = {

      val asString = Traversable.takeUpTo[Array[Byte]](MAX_LENGTH).transform(Iteratee.consume[Array[Byte]]().map(c => new String(c, request.charset.getOrElse("ISO-8859-1"))))
        .flatMap(Iteratee.eofOrElse(Results.EntityTooLarge))

      for {
        either <- asString
        str <- either.right
      } yield upickle.read[A](str)
    }
  }
*/

}
