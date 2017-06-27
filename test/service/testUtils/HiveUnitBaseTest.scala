package service.testUtils

import java.io.File

import org.apache.hadoop.conf.Configuration
import org.apache.hive.hcatalog.data.schema.HCatSchema
import org.apache.hive.hcatalog.data.transfer.{DataTransferFactory, HCatReader, ReadEntity, ReaderContext}
import org.apache.hive.hcatalog.mapreduce.HCatInputFormat
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}
import service.SchemaUtils


trait HiveUnitBaseTest extends FlatSpecLike with BeforeAndAfterAll {

  lazy val DEFAUlT_HIVE_DB = "default"
  lazy val DEFAULT_DERBY_DB = "jdbc:derby:memory:TestingDB;create=true"
  lazy val JDBC_CONNECTION_STRING = "jdbc.connection.string"

  /** conf keys */
  lazy val DATA_FILE_PATH = "DATA_FILE_PATH"
  lazy val JDBC_OUTPUT_COLUMN_NAMES = "jdbc.output.column.names"
  lazy val JDBC_OUTPUT_COLUMN_TYPES = "jdbc.output.column.types"

  lazy val HCAT_KEY_BASE = "mapreduce.lib.hcat"
  lazy val HCAT_KEY_OUTPUT_SCHEMA = HCAT_KEY_BASE + ".output.schema"
  lazy val HCAT_KEY_JOB_INFO = HCAT_KEY_BASE + ".job.info"

  var testSuite: HiveTestSuite = _
  var hcatRecordReader: HCatReader = _
  var conf: Configuration = _
  var hcatInputSchema: HCatSchema = _

  def setup(): Unit = {
    testSuite = new HiveTestSuite
    testSuite.createTestCluster()
    conf = testSuite.getFS.getConf
  }

  def tearDown(): Unit = {
    testSuite.shutdownTestCluster()
  }

  override def beforeAll: Unit = {
    super.beforeAll()
    setup()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    tearDown()
  }

  def setupHiveServer(dataFile: String, hiveScript: String, tableName: String): Unit = {
    // load data into hive table
    val inputRawData: File = new File(dataFile)
    val inputRawDataAbsFilePath: String = inputRawData.getAbsolutePath
    val params = new java.util.HashMap[String, String]()
    params.put(DATA_FILE_PATH, inputRawDataAbsFilePath)
    // create table w data
    val results: java.util.List[String] = testSuite.executeScript(hiveScript, params)
    assert(results.size != 0)

    // set up database related settings
    val conf: Configuration = testSuite.getFS.getConf
    conf.set(JDBC_CONNECTION_STRING, DEFAULT_DERBY_DB)

    // set up column type mapping
    HCatInputFormat.setInput(conf, DEFAUlT_HIVE_DB, tableName)

    //HCatInputFormat.getTableSchema(conf)
    //HCatInputFormat.getDataColumns(conf) wtf
    hcatInputSchema = SchemaUtils.getTableSchema(conf)

    val anonFields = new java.util.HashSet[String](0)
    conf.setStrings(JDBC_OUTPUT_COLUMN_TYPES,
      SchemaUtils.getColumnTypesFromHcatSchema(hcatInputSchema, anonFields):_*)

    // set up hcatalog record reader
    val builder: ReadEntity.Builder = new ReadEntity.Builder
    val entity: ReadEntity = builder.withDatabase(DEFAUlT_HIVE_DB).withTable(tableName).build

    val config = new java.util.HashMap[String, String]()
    val masterReader: HCatReader = DataTransferFactory.getHCatReader(entity, config)
    val ctx: ReaderContext = masterReader.prepareRead

    hcatRecordReader = DataTransferFactory.getHCatReader(ctx, 0)
  }

}
