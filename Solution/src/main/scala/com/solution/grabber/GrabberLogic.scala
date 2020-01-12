package com.solution.grabber

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.HttpRequest
import akka.stream.{ActorAttributes, ActorMaterializer, Supervision}
import akka.stream.scaladsl.{Sink, Source}
import slick.lifted.TableQuery
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import org.slf4j.LoggerFactory

object GrabberLogic {

  val logger = LoggerFactory.getLogger(GrabberLogic.getClass)
  val usageInfo = TableQuery[UsageInfo]
  val db = Database.forConfig("test.db")

  val defaultSupervisor: Supervision.Decider = {
    case parseError: ResponseParseError =>
      logger.error(s"Parsing Error - ${parseError.message}")
      Supervision.stop
    case e: InvalidUsageData =>
      logger.error(s"Invalid Usage Error - ${e.message}")
      Supervision.resume
    case e: Throwable =>
      logger.error(s"Unexpected error $e occurred, recovering")
      Supervision.resume
  }

  def periodicTick(nodeId: Int)(implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext) = {
    val httpRequest = HttpRequest(uri = s"http://localhost:8080/nodes/$nodeId/usage")
    Source
      .tick(1.seconds, 5.seconds, httpRequest)
      .mapAsync(1)(Http().singleRequest(_))
      .flatMapConcat(response => response.entity.getDataBytes())
      .map(_.utf8String)
      .map(Usage.fromCSVLine(nodeId))
      .mapAsync(1)(_.fold(e => Future.failed(e), findAndInsert))
      .withAttributes(ActorAttributes.supervisionStrategy(defaultSupervisor))
      .runWith(Sink.ignore)
  }

  private[this] def findAndInsert(info: Usage) = {
    db.run(usageInfo.filter(_.nodeId === info.nodeId).map(_.kb).sum.result)
      .map(
        _.fold(
          {
            logger.warn(s"No existing usage found for nodeId: ${info.nodeId}")
            info
          }
        )
        (totalKBFromDB => {
          logger.debug(s"Found existing usage for nodeId: ${info.nodeId}")
          info.copy(kbUsed = info.kbUsed - totalKBFromDB)
        }
        )
      )
      .flatMap(simpleInsert)
  }

  private[this] def simpleInsert(info: Usage) = {
    logger.debug(s"Inserting Usage: $info")
    db.run(usageInfo += info)
  }

}
