name := """playHadoopFsService"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"
lazy val hiveVersion = "2.1.1" // "1.1.0-cdh5.10.1"
lazy val hadoopVersion = "2.7.2" //"2.6.0-cdh5.10.1"

libraryDependencies ++= Seq(
  ws
  /*,"com.github.sakserv" % "hadoop-mini-clusters-hdfs" % "0.1.11"
  ,"com.github.sakserv" % "hadoop-mini-clusters-yarn" % "0.1.11"
  ,"com.github.sakserv" % "hadoop-mini-clusters-zookeeper" % "0.1.11"
  ,"com.github.sakserv" % "hadoop-mini-clusters-hiveserver2" % "0.1.11"
  ,"com.github.sakserv" % "hadoop-mini-clusters-hivemetastore" % "0.1.11"*/

  ,"org.apache.logging.log4j" % "log4j-core" % "2.8.2"
  /*,"org.apache.logging.log4j" % "log4j-api" % "2.8.2"*/

  ,"org.apache.hadoop" % "hadoop-common" % hadoopVersion
  ,"org.apache.hadoop" % "hadoop-hdfs" % "2.8.1" //"2.8.1"
  //,"org.apache.hadoop" % "hadoop-core" % "0.20.2"
  ,"org.apache.hadoop" % "hadoop-client" % hadoopVersion
  //,"org.apache.hadoop" % "hadoop-client" % "2.7.3.2.5.3.0-37"
  ,"org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.8.1"


  //,"commons-daemon" % "commons-daemon" % "1.0.15"

  ,"org.apache.hive" % "hive-jdbc" % hiveVersion //"1.1.0-cdh5.10.1"
  ,"org.apache.hive" % "hive-contrib" % hiveVersion //"1.1.0-cdh5.10.1"
  ,"org.apache.hive.hcatalog" % "hive-hcatalog-core" % hiveVersion //"1.1.0-cdh5.10.1" //"2.1.1"
  //,"org.apache.hive.hcatalog" % "hive-hcatalog-streaming" % "1.1.0-cdh5.10.1" //"2.1.1"
  //,"org.apache.hadoop" % "hadoop-mapred" % "0.22.0"


  //,"org.apache.hive.hcatalog" % "hive-hcatalog-server-extensions" % hiveVersion //"1.1.0-cdh5.10.1" exclude("javax.jms", "jms") exclude("org.apache.hive.hcatalog", "hive-hcatalog-core") exclude("org.slf4j", "slf4j-log4j12")
  //,"org.apache.hive.hcatalog" % "hive-webhcat-java-client" % hiveVersion //"1.1.0-cdh5.10.1"

  //,"org.apache.hadoop" % "hadoop-mapreduce-client-core" % hadoopVersion exclude("org.slf4j", "slf4j-log4j12") exclude("org.slf4j", "slf4j-api")

  //,"com.inmobi.hive.test" % "hiveunit-mr2" % "1.2.5" % Test classifier("schedoscope") //exclude("org.slf4j", "slf4j-log4j12") exclude("org.slf4j", "slf4j-api")

  ,"org.apache.derby" % "derby" % "10.11.1.1" % Test

  ,"org.scalatest" %% "scalatest" % "3.0.1" % Test

)

routesGenerator := InjectedRoutesGenerator

PlayKeys.playOmnidoc := false

resolvers ++= Seq(
  "conjars" at "http://conjars.org/repo"
  ,"otto-bintray" at "https://dl.bintray.com/ottogroup/maven"
  ,"cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
  //,"bintray-ottogroup-maven" at "https://api.bintray.com/maven/ottogroup/maven/schedoscope"
  //,"clojars" at "https://clojars.org/repo"
  //,"plugins" at "http://repo.spring.io/plugins-release"
  //,"sonatype" at "http://oss.sonatype.org/content/groups/public/"
  ,"public.repo.hortonworks.com" at "http://repo.hortonworks.com/content/groups/public/"
)
