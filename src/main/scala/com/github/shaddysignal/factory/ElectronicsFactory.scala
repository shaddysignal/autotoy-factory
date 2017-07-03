package com.github.shaddysignal.factory

import com.github.shaddysignal.model.Electronics

import scala.concurrent.ExecutionContext

class ElectronicsFactory(implicit executionContext: ExecutionContext)
  extends Factory[Electronics](() => Electronics(math.random()), 5, 20)(executionContext)
