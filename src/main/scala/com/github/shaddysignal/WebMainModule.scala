package com.github.shaddysignal

import com.github.shaddysignal.factory.{CorpusFactory, ElectronicsFactory, WheelsFactory}

import scala.concurrent.ExecutionContext

trait WebMainModule {
  import com.softwaremill.macwire._

  lazy val carFactory: CarFactory = wire[CarFactory]

  def electronicsFactory: ElectronicsFactory
  def corpusFactory: CorpusFactory
  def wheelsFactory: WheelsFactory

  implicit def executionContext: ExecutionContext
}
