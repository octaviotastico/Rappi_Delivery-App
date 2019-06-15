import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "rapy",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.lihaoyi" %% "cask" % "0.1.9",
    libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.0"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
