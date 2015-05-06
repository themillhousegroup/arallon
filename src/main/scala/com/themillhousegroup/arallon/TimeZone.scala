package com.themillhousegroup.arallon

import org.joda.time.DateTimeZone

sealed trait TimeZone {
  val name: String
  lazy val zone = DateTimeZone.forID(name)
}

abstract class TimeZoneAdapter(val name: String) extends TimeZone {
  val toStringName = name.split("/").last

  override def toString: String = s"$toStringName"

}

// Refer: https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/

object UTC extends UTC
class UTC extends TimeZoneAdapter("UTC")
object Paris extends Paris
class Paris extends TimeZoneAdapter("Europe/Paris")
class EST extends TimeZoneAdapter("EST") // New York
class PST extends TimeZoneAdapter("PST") // San Francisco
object Melbourne extends Melbourne
class Melbourne extends TimeZoneAdapter("Australia/Melbourne")
class Local(override val name: String) extends TimeZoneAdapter(name)

object TimeZone {
  def apply(tzName: String): TimeZone = {
    tzName match {
      case UTC.name => UTC
      case Paris.name => Paris
      case Melbourne.name => Melbourne
      case _ => new Local(tzName)
    }
  }
}

