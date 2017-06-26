name := """hadoopFsService"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  ws
  ,"org.apache.hadoop" % "hadoop-client" % "2.7.1"
  ,"org.apache.hive.hcatalog" % "hive-hcatalog-streaming" % "2.1.1"
  ,"org.apache.hive.hcatalog" % "hive-hcatalog-core" % "2.1.1"
  ,"org.apache.hive.hcatalog" % "hive-webhcat-java-client" % "1.1.0-cdh5.10.1"
  ,"com.inmobi.hive.test" % "hiveunit-mr2" % "1.2.5" % Test
  ,"org.apache.derby" % "derby" % "10.11.1.1" % Test

)

routesGenerator := InjectedRoutesGenerator

PlayKeys.playOmnidoc := false

resolvers ++= Seq(
  "conjars" at "http://conjars.org/repo"
  ,"clojars" at "https://clojars.org/repo"
  ,"plugins" at "http://repo.spring.io/plugins-release"
  ,"sonatype" at "http://oss.sonatype.org/content/groups/public/"
)
