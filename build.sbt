name := "taboola"

version := "0.1"

scalaVersion := "2.12.8"

val root = (project in file(".")).settings(libraryDependencies ++= Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.11.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.0"
))