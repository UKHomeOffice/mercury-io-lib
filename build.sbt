name := "io-scala-lib"

organization := "uk.gov.homeoffice"

scalaVersion := "2.11.8"

fork in run := true

fork in Test := true

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:reflectiveCalls",
  "-language:postfixOps",
  "-Yrangepos",
  "-Yrepl-sync"
)

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Kamon Repository" at "http://repo.kamon.io",
  "jitpack" at "https://jitpack.io",
  Resolver.bintrayRepo("hseeberger", "maven"),
  Resolver.bintrayRepo("findify", "maven")
)

val `json4s-version` = "3.5.0"
val `scalactic-version` = "3.0.1"
val `test-scala-lib-version` = "1.4.0"

libraryDependencies ++= Seq(
  "commons-codec" % "commons-codec" % "1.10",
  "com.typesafe" % "config" % "1.3.1" withSources(),
  "org.scalactic" %% "scalactic" % `scalactic-version` withSources(),
  "org.json4s" %% "json4s-native" % `json4s-version` withSources(),
  "org.json4s" %% "json4s-jackson" % `json4s-version` withSources(),
  "org.json4s" %% "json4s-ext" % `json4s-version` withSources(),
  "org.json4s" %% "json4s-mongo" % `json4s-version` withSources(),
  "com.github.fge" % "json-schema-validator" % "2.2.6" withSources(),
  "org.scala-lang.modules" %% "scala-pickling" % "0.10.1" withSources(),
  "com.lihaoyi" %% "pprint" % "0.4.4",
  "com.github.nscala-time" %% "nscala-time" % "2.16.0" withSources(),
  "org.clapper" %% "grizzled-slf4j" % "1.3.0",
  "com.github.UKHomeOffice" % "test-scala-lib" % `test-scala-lib-version` withSources()
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith ".java" => MergeStrategy.discard
  case _ => MergeStrategy.first
}