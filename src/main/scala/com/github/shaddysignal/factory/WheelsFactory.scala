package com.github.shaddysignal.factory

import java.util.concurrent.atomic.AtomicInteger

import com.github.shaddysignal.model.Wheels

import scala.concurrent.ExecutionContext

class WheelsFactory(implicit executionContext: ExecutionContext)
  extends Factory[Wheels](() => Wheels(math.random()), 2, 50)(executionContext)
