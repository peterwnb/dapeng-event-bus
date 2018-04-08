organization := "com.today"

name := "event-bus"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.4"


resolvers ++= List("today nexus" at "http://nexus.today36524.com/repository/maven-public/")

publishTo := Some("today-snapshots" at "http://nexus.today36524.com/repository/maven-snapshots/")

credentials += Credentials("Sonatype Nexus Repository Manager", "nexus.today36524.com", "central-services", "E@Z.nrW3")

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.13",
  "org.springframework" % "spring-aop" % "4.3.5.RELEASE",
  "org.springframework" % "spring-aspects" % "4.3.5.RELEASE",
  "org.apache.kafka" % "kafka-clients" % "1.0.0",
  "com.github.wangzaixiang" %% "scala-sql" % "2.0.5",
  "com.github.dapeng" % "dapeng-utils" % "2.0.1-SNAPSHOT" excludeAll (
    ExclusionRule().withOrganization("io.netty").withName("netty-all")
    ),
  "com.github.wangzaixiang" %% "spray-json" % "1.3.4",
  "com.google.guava" % "guava" % "16.0.1",
  "com.alibaba.otter" % "canal.protocol" % "1.0.25" excludeAll(
    ExclusionRule().withOrganization("com.alibaba.otter").withName("canal.common"),
    ExclusionRule().withOrganization("commons-lang").withName("commons-lang")
  )
)

javacOptions ++= Seq("-encoding", "UTF-8")

//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

