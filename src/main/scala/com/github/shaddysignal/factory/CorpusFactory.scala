package com.github.shaddysignal.factory

import com.github.shaddysignal.model.Corpus

import scala.concurrent.ExecutionContext

class CorpusFactory(implicit executionContext: ExecutionContext)
  extends Factory[Corpus](() => Corpus(math.random()), 12, 5)(executionContext)
