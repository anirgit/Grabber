package com.solution.grabber

import slick.jdbc.SQLiteProfile.api._
import org.slf4j.LoggerFactory
import cats.implicits._

class UsageInfo(tag: Tag) extends Table[Usage](tag, "usage_data") {
  def nodeId = column[Int]("node_id", O.PrimaryKey)
  def timestamp = column[String]("timestamp")
  def kb = column[Int]("kb")
  def * = (nodeId, timestamp, kb) <>
    ((Usage.apply _).tupled, Usage.unapply)
}

case class Usage(nodeId: Int, timestamp: String, kbUsed: Int)

object Usage {

  val logger = LoggerFactory.getLogger(Usage.getClass)

  def unapply(arg: Usage): Option[(Int, String, Int)] = Some(arg.nodeId, arg.timestamp, arg.kbUsed)

  def fromCSVLine(nodeId: Int)(line: String) =
    line.split(",").toList match {
      case Nil =>
        Left(InvalidUsageData(s"$nodeId node didn't return any usage info"))
      case time :: kb :: _ =>
        logger.debug(s"Received data from node: $nodeId for time: $time with usage: $kb")
        Either.catchNonFatal(kb.toInt)
            .leftMap(_ => ResponseParseError(s"Couldn't parse kb data from node $nodeId"))
            .map(kb => Usage(nodeId, time, kb))
      case _ =>
        Left(ResponseParseError(s"Couldn't parse response from node: $nodeId"))
    }
}
