ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "kitpo2",
    idePackagePrefix := Some("com.m8u.kitpo2")
  )
