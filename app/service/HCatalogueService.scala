package service

import java.io.IOException
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.hive.hcatalog.data.HCatRecord
import org.apache.hive.hcatalog.data.transfer.{DataTransferFactory, HCatReader, ReadEntity, ReaderContext}
import org.apache.hive.hcatalog.common.HCatUtil
import org.apache.hive.hcatalog.data.schema.HCatSchema
import org.apache.hadoop.hive.ql.metadata.Table
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient
import org.apache.hadoop.mapreduce.Job
import org.apache.hive.hcatalog.mapreduce.{HCatInputFormat, InputJobInfo}
import play.api.Logger

/**
  * Provides interface between API & HCatalog store
  */
class HCatalogueService(dbName: String, host: String, port: Int) {

  private lazy val hiveSiteConfPath = this.getClass.getProtectionDomain.getCodeSource().getLocation().getPath() + "../../docker/hive-site.xml"
  private lazy val conf = {
    val conf = new Configuration()
    conf.addResource(new Path(hiveSiteConfPath))
    conf
  }
  lazy val hiveConf = HCatUtil.getHiveConf(conf)

  def getTableSchema(dbName: String, tableName: String): HCatSchema = {
    val client: HiveMetaStoreClient = HCatUtil.getHiveClient(hiveConf)
    val table: Table = HCatUtil.getTable(client, dbName, tableName)
    HCatUtil.getTableSchemaWithPtnCols(table)
  }

  /*
  def initJob(dbName: String, tableName: String): Unit = {
    val job: Job = new Job(conf, "HCatalogueService")
    HCatInputFormat.setInput(job, InputJobInfo.create(dbName,
      tableName, null))
    // initialize HCatOutputFormat

    job.setInputFormatClass(HCatInputFormat.class);
    job.setJarByClass(GroupByAge.class);
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
    job.setMapOutputKeyClass(IntWritable.class);
    job.setMapOutputValueClass(IntWritable.class);
    job.setOutputKeyClass(WritableComparable.class);
    job.setOutputValueClass(DefaultHCatRecord.class);
    HCatOutputFormat.setOutput(job, OutputJobInfo.create(dbName,
      outputTableName, null));
    HCatSchema s = HCatOutputFormat.getTableSchema(job);
    System.err.println("INFO: output schema explicitly set for writing:"
      + s);
    HCatOutputFormat.setSchema(job, s);
    job.setOutputFormatClass(HCatOutputFormat.class);
  }
  */

  def streamFromTable(tableName: String) = {

    //val schema = getTableSchema(dbName, tableName)

    val builder: ReadEntity.Builder = new ReadEntity.Builder()
    val entity: ReadEntity = builder
      .withDatabase(dbName)
      .withTable(tableName)
      .build()

    val config: util.HashMap[String, String] = new util.HashMap()
    config.put("hive.metastore.uris", s"thrift://${host}:9083") // 172.17.0.1
    config.put("javax.jdo.option.ConnectionUserName", "APP")
    config.put("javax.jdo.option.ConnectionPassword", "mine")
    config.put("javax.jdo.option.ConnectionURL", "jdbc:mysql://localhost:3306/metastore?create=true")
    config.put("javax.jdo.option.ConnectionDriverName", "com.mysql.jdbc.Driver")
    config.put("datanucleus.fixedDatastore", "true")
    config.put("datanucleus.autoCreateSchema", "false")

/*    val itr = hiveConf.iterator()
    while (itr.hasNext) {
      val next: util.Map.Entry[String, String] = itr.next()
      config.put(next.getKey, next.getValue)
    }*/

    val reader: HCatReader = DataTransferFactory.getHCatReader(entity, config)
    val cntxt: ReaderContext = reader.prepareRead()
    Logger.info(s"Num of splits in context: ${cntxt.numSplits}")

    for(split <- 0 to cntxt.numSplits) {
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

  def apply(dbName: String, host: String = "localhost", port: Int = 9083): HCatalogueService =
    new HCatalogueService(dbName, host, port)

}
