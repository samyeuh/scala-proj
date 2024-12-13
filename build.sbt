ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val scalaTestVersion = "3.2.19"

lazy val root = (project in file("."))
  .settings(
    name := "scala-proj",
    organization := "fr.efrei.fp.project",
    idePackagePrefix := Some("fr.efrei.fp.project"),
    libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
        "org.scalatest" %% "scalatest-flatspec" % scalaTestVersion % Test
    )
  )
