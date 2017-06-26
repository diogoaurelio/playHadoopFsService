package models

import java.io.IOException
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.hive.hcatalog.data.HCatRecord
import org.apache.hive.hcatalog.data.transfer.{DataTransferFactory, HCatReader, ReadEntity, ReaderContext}
import play.api.Logger

/**
  * Provides interface between API & HCatalog store
  */
class HCatalogueService(dbName: String, host: String) {

  def streamFromTable(tableName: String) = {
    val builder: ReadEntity.Builder = new ReadEntity.Builder()
    val entity: ReadEntity = builder
      .withDatabase(dbName)
      .withTable(tableName).build();

    val config: util.HashMap[String, String] = new util.HashMap()

    val reader: HCatReader = DataTransferFactory.getHCatReader(entity, config)
    val cntxt: ReaderContext = reader.prepareRead()
    Logger.info(s"Num of splits in context: ${cntxt.numSplits()}")

    for(split <- cntxt.numSplits()) {
      val splitReader:HCatReader = DataTransferFactory.getHCatReader(cntxt, split)
      val tablePages: util.Iterator[HCatRecord] = splitReader.read()
      Logger.info(s"ITR1: ${tablePages}")
      // TODO: get col names for schema building + build Map

      while (tablePages.hasNext()) {
        val records: HCatRecord = tablePages.next()
        Logger.info(s"Records: ${records}")
        try {
          val recordsIterator: util.Iterator[Object] = records.getAll.iterator()

          val page = new StringBuilder
          // iterate over full row
          while (recordsIterator.hasNext) {
            // TODO: implement sink
            val colRow = recordsIterator.next.toString
            page.append(colRow)
            if (recordsIterator.hasNext)
              page.append("\t")
            else
              page.append("\n")
          }
          // return page.toString

        } catch {
          case io: IOException => Logger.error(s"Failed to read from HCatalogue: ${io.getMessage}"); throw io
        }
      }
    }
  }
}

object HCatalogueService {

  lazy val defaultConf = "application.conf"

  /** Utility to load params from conf file */
  def getConfiguration(confFile: String = defaultConf): Config =
    ConfigFactory.load(confFile)

  def loadFromConfig(confFile: String = defaultConf): Seq[String] = {
    val conf = getConfiguration(confFile)
    val dbName = conf.getString("hcatalogue.db.name")
    val host = conf.getString("hcatalogue.db.host")
    Seq(dbName, host)
  }

  def apply(): HCatalogueService = {
    val Seq(dbName, host) = loadFromConfig()
    apply(dbName, host)
  }

  def apply(dbName: String, host: String): HCatalogueService =
    new HCatalogueService(dbName, host)

}
