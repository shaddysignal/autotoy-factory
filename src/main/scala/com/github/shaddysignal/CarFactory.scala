package com.github.shaddysignal

import com.github.shaddysignal.factory.{CorpusFactory, ElectronicsFactory, WheelsFactory}
import com.github.shaddysignal.model.Car
import org.slf4s.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class CarFactory(electronicsFactory: ElectronicsFactory,
                 corpusFactory: CorpusFactory,
                 wheelsFactory: WheelsFactory)
                (implicit executionContext: ExecutionContext) extends Logging {
  import CarFactory._

  def produceCar(): Future[Car] = {
    log.info("Producing car")
    val electronicsFuture = electronicsFactory.deliverPackage
    val corpusFuture = corpusFactory.deliverPackage
    val wheelsFuture = wheelsFactory.deliverPackage

    // After it delivered, assembly time
    Thread.sleep(carTimeAssemblyInSec * 1000)

    for {
      electronics <- electronicsFuture
      corpus      <- corpusFuture
      wheels      <- wheelsFuture
    } yield Car(electronics.quality * 0.33 + corpus.quality * 0.33 + wheels.quality * 0.33)
  }
}

object CarFactory {
  lazy val carTimeAssemblyInSec = 5
}
