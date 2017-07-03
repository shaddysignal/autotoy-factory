package com.github.shaddysignal

import akka.http.scaladsl.server.Route
import org.webjars.WebJarAssetLocator

import scala.util.{Failure, Success, Try}

trait WebJarSupport {
  import akka.http.scaladsl.server.Directives._

  val webJarLocator = new WebJarAssetLocator

  final def webJars: Route = {
    extractUnmatchedPath { path =>
      Try(webJarLocator.getFullPath(path.toString)) match {
        case Success(fullPath) =>
          getFromResource(fullPath)
        case Failure(_: IllegalArgumentException) =>
          reject
        case Failure(e) =>
          failWith(e)
      }
    }
  }
}

object WebJarSupport extends WebJarSupport
