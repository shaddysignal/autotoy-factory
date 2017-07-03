package com.github.shaddysignal.factory

import org.slf4s.Logging

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

abstract class Factory[T](produce: () => T, produceTimeInSec: Int, storageCapacity: Int)
                         (implicit executionContext: ExecutionContext) extends Logging {
  private val storage: mutable.Queue[T] = mutable.Queue()
  private var isFactoryStalling: Boolean = false
  private var isFactoryProducing: Boolean = false

  def startProduction(): Unit = {
    if (isFactoryProducing) return

    def productionProcess(): Unit = {
      log.debug("Production process")
      Thread.sleep(produceTimeInSec * 1000)
      storage.enqueue(produce())

      log.debug("Entering sync block(with notifyAll and wait)")
      this.synchronized {
        isFactoryStalling = false // Still working
        this.notifyAll() // notify all package deliverers

        while (isFactoryProducing && storage.size >= storageCapacity) {
          log.info("Produced to much, waiting")
          isFactoryStalling = true
          this.wait()
        }
      }

      if (isFactoryProducing)
        productionProcess()
    }

    log.info("Start production")
    isFactoryProducing = true
    productionProcess()
  }

  def deliverPackage: Future[T] = Future {
    log.info("Delivering package")

    log.debug("Entering sync block(with notify)")
    this.synchronized {
      if (isFactoryStalling && storage.size <= storageCapacity / 2) {
        this.notify() // notify factory to start producing
      }
    }

    log.debug("Entering sync block(with wait)")
    this.synchronized {
      while (storage.isEmpty) {
        this.wait() // wait for factory to produce
      }

      storage.dequeue() // In block to safely know that it is not empty
    }
  }

  def stopProduction(): Unit = this.synchronized {
    log.info("Stop production")

    isFactoryProducing = false
    this.notifyAll() // notify all to finish production processes
  }

  def getStorage: Seq[T] = storage.clone()
}
