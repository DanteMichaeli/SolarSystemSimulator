ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "untitled"
  )

libraryDependencies += "org.scalafx" %% "scalafx" % "18.0.1-R28"