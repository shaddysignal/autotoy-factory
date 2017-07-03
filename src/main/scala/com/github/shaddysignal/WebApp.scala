package com.github.shaddysignal

import java.util.concurrent.{ExecutorService, Executors, ThreadFactory}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling
import akka.http.scaladsl.model.StatusCodes.PermanentRedirect
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import argonaut.{CodecJson, DecodeJson, EncodeJson}
import com.github.shaddysignal.factory.FactoryModule
import com.github.shaddysignal.model.{Car, Corpus, Electronics, Wheels}
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport
import org.slf4s.Logging

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class WebApp(implicit system: ActorSystem)
  extends HttpApp
    with ArgonautSupport
    with WebJarSupport
    with Logging {

  import argonaut.Argonaut._
  // load codecs
  import model.Car._

  implicit def seqCodec[T](implicit e: CodecJson[T]): CodecJson[Seq[T]] =
    CodecJson(
      seq => EncodeJson.VectorEncodeJson[T].apply(seq.toVector),
      cursor => DecodeJson.VectorDecodeJson[T].apply(cursor).map(_.toSeq)
    )
  implicit lazy val stateCodec: CodecJson[State] = CodecJson.derive[State]

  // Main module
  private val modules = new WebMainModule with FactoryModule {
    // What is that all for?
    val threadFactory = new ThreadFactory {
      private def wire[T <: Thread](thread: T): T = {
        thread.setName(s"executor-${thread.getId}")
        thread
      }

      override def newThread(r: Runnable): Thread = wire(new Thread(r))
    }

    val executorService: ExecutorService = Executors.newFixedThreadPool(32, threadFactory)

    implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
  }

  import modules._

  private val factories = Seq(corpusFactory, electronicsFactory, wheelsFactory)
  private var carDealership = List[Car]()

  override protected def routes: Route = pathPrefix("api") {
    path("socket") {
      import EventStreamMarshalling._

      complete {
        Source.tick(0.seconds, 1.seconds, NotUsed)
          .map(_ => produceState)
          .map(state => ServerSentEvent(state.asJson.nospaces))
          .keepAlive(1.second, () => ServerSentEvent.heartbeat)
      }
    } ~
      get {
        pathPrefix("produce") {
          path("car") {
            onSuccess(carFactory.produceCar()) { car =>
              carDealership = car :: carDealership
              complete(car)
            }
          }
        } ~
          path("state") {
            complete(produceState)
          }
      }
  } ~ pathEndOrSingleSlash {
    get(redirect("index.html", PermanentRedirect))
  } ~
    getFromResourceDirectory("static") ~
    webJars

  override protected def postHttpBinding(binding: Http.ServerBinding): Unit = {
    super.postHttpBinding(binding)

    factories.foreach(f => {
      Future { f.startProduction() }
      log.debug(s"Production started. $f")
    })
  }

  override protected def postServerShutdown(attempt: Try[Done], system: ActorSystem): Unit = {
    super.postServerShutdown(attempt, system)

    factories.foreach(_.stopProduction())
    executorService.shutdown()
  }

  private def produceState: State =
    State(carDealership, corpusFactory.getStorage, electronicsFactory.getStorage, wheelsFactory.getStorage)

  case class State(cars: Seq[Car], corpuses: Seq[Corpus], electronics: Seq[Electronics], wheels: Seq[Wheels])
}
