package service

//import com.github.sakserv.minicluster.impl.{HiveLocalMetaStore, HiveLocalServer2, YarnLocalCluster, ZookeeperLocalCluster}
import org.apache.hadoop.hive.conf.HiveConf
import org.apache.hadoop.conf.Configuration
import org.scalatest.FlatSpecLike
import service.testUtils.HiveUnitBaseTest


class HCatalogueServiceSpec extends FlatSpecLike {

  trait HCatalogueServiceTest {
    lazy val dbName = "testing"
    lazy val host = "localhost"
    lazy val service = HCatalogueService(dbName: String, host: String)
  }


  it should "start locally HCatalog" in new HCatalogueServiceTest {
    /*setupHiveServer("test/resources/test_map_data.txt",
      "test/resources/test_map.hql", "test_map")*/
    lazy val hivePort = 12348
    lazy val metastorePort = 12347 // 9083
    lazy val zookeeperPort = 11111
    lazy val metaStoreDerbyDbDir = "/tmp/metastore_db"
    lazy val hiveScratchDir = "/tmp/hive_scratch_dir"
    lazy val hiveWarehouseDir = "/tmp/warehouse_dir"

    service.streamFromTable(tableName = "test")

    /*
    val zookeeperLocalCluster: ZookeeperLocalCluster = new ZookeeperLocalCluster.Builder()
      .setPort(zookeeperPort)
      .setTempDir("embedded_zookeeper")
      .setZookeeperConnectionString("localhost:12345")
      .setMaxClientCnxns(60)
      .setElectionPort(20001)
      .setQuorumPort(20002)
      .setDeleteDataDirectoryOnClose(false)
      .setServerId(1)
      .setTickTime(2000)
      .build()

    zookeeperLocalCluster.start()
    println("Zookeeper launched")
    val hiveLocalMetaStore: HiveLocalMetaStore = new HiveLocalMetaStore.Builder()
      .setHiveMetastoreHostname("localhost")
      .setHiveMetastorePort(metastorePort)
      .setHiveMetastoreDerbyDbDir(metaStoreDerbyDbDir) // metastore_db
      .setHiveScratchDir(hiveScratchDir)
      .setHiveWarehouseDir(hiveWarehouseDir)
      .setHiveConf(new HiveConf())
      .build()

    /*hiveLocalMetaStore.start()
    println(s"hiveLocalMetaStore launched: " +
      s"host: ${hiveLocalMetaStore.getHiveMetastoreHostname} " +
      s"port: ${hiveLocalMetaStore.getHiveMetastorePort}, dir: " +
      s"${hiveLocalMetaStore.getHiveWarehouseDir}")*/


    val hiveLocalServer2: HiveLocalServer2 = new HiveLocalServer2.Builder()
      .setHiveServer2Hostname("localhost")
      .setHiveServer2Port(hivePort)
      .setHiveMetastoreHostname("localhost")
      .setHiveMetastorePort(metastorePort)
      .setHiveMetastoreDerbyDbDir(metaStoreDerbyDbDir)
      .setHiveScratchDir(hiveScratchDir)
      .setHiveWarehouseDir(hiveWarehouseDir)
      .setHiveConf(new HiveConf())
      .setZookeeperConnectionString(s"localhost:${zookeeperPort}")
      .build()

    hiveLocalServer2.start()
    */


  }

}
