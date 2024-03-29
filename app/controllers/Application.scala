package controllers

import javax.inject.Inject

import akka.NotUsed
import akka.stream.scaladsl.{Source, _}
import akka.util._
import play.api.libs.EventSource
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

case class TweetInfo(searchQuery: String, message: String, author: String)

object TweetInfo {
  implicit val tweetInfoFormat = Json.format[TweetInfo]
}

class Application @Inject()(wSClient: WSClient)(implicit ec: ExecutionContext) extends Controller {

  lazy val framing = Framing.delimiter(ByteString("\n"), maximumFrameLength = 100, allowTruncation = true)

  def index = Action {
    //default search
    Redirect(routes.Application.liveTouits(List("java", "ruby")))
  }

  def liveTouits(queries: List[String]) = Action {
    Ok(views.html.index(queries))
  }

  private def prefixAndAuthor = {
    import java.util.Random
    val prefixes = List("Tweet about", "Just heard about", "I love")
    val authors = List("Bob", "Joe", "John")
    val rand = new Random()
    (prefixes(rand.nextInt(prefixes.length)), authors(rand.nextInt(authors.length)))
  }

  //fake twitter API
  def timeline(keyword: String) = Action {
    val source = Source.tick(initialDelay = 0.second, interval = 1.second, tick = "tick")
    Ok.chunked(source.map { tick =>
      val (prefix, author) = prefixAndAuthor
      Json.obj("message" -> s"$prefix $keyword", "author" -> author).toString + "\n"
    }.limit(100))
  }

  def mixedStream(queryString: String) = Action {
    val keywordSources = Source(queryString.split(",").toList)
    val responses = keywordSources.flatMapMerge(10, queryToSource)
    Ok.chunked(responses via EventSource.flow)
  }

  private def queryToSource(keyword: String) = {
    val request = wSClient
        //.url(routes.Application.timeline(keyword))
      .url(s"http://localhost:9000/timeline")
      .withQueryString("keyword" -> keyword)

    streamResponse(request)
      .via(framing)
      .map { byteString =>
        val json = Json.parse(byteString.utf8String)
        val tweetInfo = TweetInfo(keyword, (json \ "message").as[String], (json \ "author").as[String])
        //json
        Json.toJson(tweetInfo) //tweetInfo
      }
  }

  private def streamResponse(request: WSRequest) = Source.fromFuture(request.stream()).flatMapConcat(_.body)

}
