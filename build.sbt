name := "auto-toy-factory"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

organization := "com.github.shaddysignal"

libraryDependencies := {
  val akkaHttpVersion = "10.0.8"
  val akkaStreamVersion = "2.5.1"
  val akkaHttpJsonVersion = "1.17.0"
  val macWireVersion = "2.3.0"

  val webJarBootstrapVersion = "3.3.7-1"
  val webJarLocatorVersion = "0.32"

  val logbackVersion = "1.2.3"
  val slf4sVersion = "1.7.25"
  val akkaSlf4jVersion = "2.5.1"

  Seq(
    "com.typesafe.akka"         %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka"         %% "akka-stream"          % akkaStreamVersion,
    "de.heikoseeberger"         %% "akka-http-argonaut"   % akkaHttpJsonVersion,
    "com.softwaremill.macwire"  %% "macros"               % macWireVersion,
    "org.webjars"               %  "bootstrap"            % webJarBootstrapVersion,
    "org.webjars"               %  "webjars-locator"      % webJarLocatorVersion,
    "org.slf4s"                 %% "slf4s-api"            % slf4sVersion,
    "ch.qos.logback"            %  "logback-classic"      % logbackVersion,
    "com.typesafe.akka"         %% "akka-slf4j"           % akkaSlf4jVersion
  )
}