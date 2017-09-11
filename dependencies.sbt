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

transitiveClassifiers := Seq("sources")

val `play-version` = "2.5.12"
val `json4s-version` = "3.2.11"
val `scalactic-version` = "3.0.1"
val `mercury-test-lib-version` = "1.4.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws" % `play-version`,
  "commons-codec" % "commons-codec" % "1.10",
  "com.typesafe" % "config" % "1.3.1",
  "org.scalactic" %% "scalactic" % `scalactic-version`,
  "org.json4s" %% "json4s-native" % `json4s-version`,
  "org.json4s" %% "json4s-jackson" % `json4s-version`,
  "org.json4s" %% "json4s-ext" % `json4s-version`,
  "org.json4s" %% "json4s-mongo" % `json4s-version`,
  "com.github.fge" % "json-schema-validator" % "2.2.6",
  "org.scala-lang.modules" %% "scala-pickling" % "0.10.1",
  "com.lihaoyi" %% "pprint" % "0.4.4",
  "com.github.nscala-time" %% "nscala-time" % "2.16.0",
  "org.clapper" %% "grizzled-slf4j" % "1.3.0",
  "com.github.UKHomeOffice" %% "mercury-test-lib" % `mercury-test-lib-version`
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-server" % `play-version` % Test,
  "com.typesafe.play" %% "play-test" % `play-version` % Test,
  "com.github.UKHomeOffice" %% "mercury-test-lib" % `mercury-test-lib-version` % Test classifier "tests"
)
