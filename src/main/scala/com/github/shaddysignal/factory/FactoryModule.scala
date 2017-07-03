package com.github.shaddysignal.factory

import scala.concurrent.ExecutionContext

trait FactoryModule {
  import com.softwaremill.macwire._

  lazy val corpusFactory: CorpusFactory = wire[CorpusFactory]
  lazy val electronicsFactory: ElectronicsFactory = wire[ElectronicsFactory]
  lazy val wheelsFactory: WheelsFactory = wire[WheelsFactory]

  implicit def executionContext: ExecutionContext
}
