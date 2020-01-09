package com.solution.grabber

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.implicits._
import org.slf4j.LoggerFactory

  object GrabberApp extends App {

    override def main(args: Array[String]): Unit = {
      implicit val system = ActorSystem("system")
      implicit val materializer = ActorMaterializer()
      implicit val executionContext = system.dispatcher
      val logger = LoggerFactory.getLogger(GrabberApp.getClass)

      logger.debug("Running app...")
      args.headOption.fold {
        logger.error("Please provide valid number of nodes")
        system.terminate().as(())
      } {
        numOfNodes => (1 to numOfNodes.toInt).toList.traverse(GrabberLogic.periodicTick).as(())
      }
    }

  }