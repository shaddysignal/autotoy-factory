package com.github.shaddysignal

import argonaut.CodecJson

package object model {
  import argonaut.Argonaut._

  case class Car(quality: Double)
  case class Electronics(quality: Double)
  case class Corpus(quality: Double)
  case class Wheels(quality: Double)

  object Car {
    implicit lazy val carCodec: CodecJson[Car] = CodecJson.derive[Car]
  }

  object Electronics {
    implicit lazy val electronicsCodec: CodecJson[Electronics] = CodecJson.derive[Electronics]
  }

  object Corpus {
    implicit lazy val corpusCodec: CodecJson[Corpus] = CodecJson.derive[Corpus]
  }

  object Wheels {
    implicit lazy val wheelsCodec: CodecJson[Wheels] = CodecJson.derive[Wheels]
  }
}
