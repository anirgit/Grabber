package com.solution.grabber

sealed trait Error extends Throwable {
  val message : String
}

case class ResponseTimeOut (message: String) extends Error
case class InvalidUsageData (message: String) extends Error
case class ResponseParseError (message: String) extends Error


