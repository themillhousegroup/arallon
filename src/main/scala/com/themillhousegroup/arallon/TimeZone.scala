package com.themillhousegroup.arallon

import org.joda.time.DateTimeZone

sealed trait TimeZone {
  val name: String
  lazy val zone = DateTimeZone.forID(name)
}

class UTC extends TimeZone {
  val name = "UTC"
  override lazy val zone = DateTimeZone.UTC
  override val toString: String = name
}

case class NonUTCTimeZone(val name: String) extends TimeZone {
  val toStringName = name.split("/").last

  override val toString: String = s"$toStringName"
}

object TimeZone {

  val UTC = new UTC

  def apply(tzName: String): TimeZone = {
    tzName match {
      case UTC.name => UTC
      case _ => new NonUTCTimeZone(tzName)
    }
  }
}

