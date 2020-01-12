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
  var nodesUsage: Map[String, Int] = Map.empty[String, Int]

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher



    val route =
      path("nodes" / Segment / "usage") {id =>
        println(s"Request received in Mock Server for node id: $id")
        get {
          Thread.sleep(Random.nextInt(6))
          val delta = Random.nextInt(1000)
          val usage = nodesUsage.get(id).fold(delta)(_ + delta)
          nodesUsage = nodesUsage + (id -> usage)
          complete(HttpEntity(ContentTypes.`text/csv(UTF-8)`, s"${Instant.now.toEpochMilli},$usage,40"))
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