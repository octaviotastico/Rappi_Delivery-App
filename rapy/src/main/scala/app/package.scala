package object app {
  import cask.Response
  import org.json4s.native.Serialization

  object JSONResponse {
    implicit val formats: org.json4s.DefaultFormats = org.json4s.DefaultFormats

    def apply[A](data: A, statusCode: Int = 200): Response =
      Response(Serialization.write(data), statusCode, headers = Seq("Content-type" -> "application/json"))
  }
}
