package com.mock.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn
import java.time.Instant

import scala.util.Random

object MockServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      path("nodes" / Segment / "usage") {id =>
        println(s"Request received in Mock Server for node id: $id")
        get {
          import java.util.concurrent.TimeUnit
          TimeUnit.SECONDS.sleep(Random.nextInt(6))
          Thread.sleep(Random.nextInt(6))
          complete(HttpEntity(ContentTypes.`text/csv(UTF-8)`, s"${Instant.now.toEpochMilli},${Random.nextInt(5)*1000},40"))
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}