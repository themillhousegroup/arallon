package com.themillhousegroup.arallon

import org.joda.time.DateTimeZone
import com.themillhousegroup.arallon.zones.UTC

sealed trait TimeZone {
  val name: String
  lazy val zone = DateTimeZone.forID(name)
}

abstract class TimeZoneAdapter(val name: String) extends TimeZone {
  val toStringName = name.split("/").last

  override def toString: String = s"$toStringName"
}

/** When there's no known match */
final class LocalTimeZoneAdapter(name: String) extends TimeZoneAdapter(name) {
  override def toString: String = s"Local ($toStringName)"
}

object TimeZone {

  val UTC = new UTC

  def apply(tzName: String): TimeZone = {
    tzName match {
      case UTC.name => UTC
      case _ => new LocalTimeZoneAdapter(tzName)
    }
  }
}

