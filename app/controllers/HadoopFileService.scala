package controllers

import akka.NotUsed
import com.google.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import akka.stream.scaladsl.{Source, _}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.Logger

class HadoopFileService @Inject()(wSClient: WSClient)(implicit ec: ExecutionContext) extends Controller {


/*  def index = Action {
    Ok.chunked()
  }*/

/*  def queryTable(tableName: String) = Action {
    val source: Source[Future[String], NotUsed] = Source.fromFuture(mockHCatalogQuery(tableName))
    Logger.info("")

  }*/


  private def mockHCatalogQuery(tableName: String): Iterator[String] = ???

  private def streamResponse(tableName: String) = {
    Source.fromIterator { () => mockHCatalogQuery(tableName) }
  }
}
