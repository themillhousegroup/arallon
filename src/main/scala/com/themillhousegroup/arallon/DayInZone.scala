package com.themillhousegroup.arallon

import org.joda.time._
import com.themillhousegroup.arallon.util.ReflectionHelper
import org.joda.time.format.{ DateTimeFormat, DateTimeFormatter }
import com.themillhousegroup.arallon.traits._
import scala.reflect._
import scala.reflect.ClassTag

/**
 * Represents a 24-hour period (from 00:00:00:000 to 23:59:59:999) in a certain timezone.
 * Initially the way to get one is to pass a TimeInZone[TZ] that is somewhere inside this day.
 */
object DayInZone {

  val jodaYMDFormat = "yyyy-MM-dd"
  val ymdFormatter: DateTimeFormatter = DateTimeFormat.forPattern(jodaYMDFormat)

  def expand[TZ <: TimeZone](t: TimeInZone[TZ]): DayInZone[TZ] = {
    val start = t.local.withTime(0, 0, 0, 0)
    val end = t.local.withTime(23, 59, 59, 999)
    new DayInZone(t.timezone, start, end)
  }
}

class DayInZone[TZ <: TimeZone](override val timezone: TZ,
    override val start: DateTime,
    override val end: DateTime) extends TimeSpanInZone[TZ](timezone, start, end) {

  override val toString: String = s"DayInZone[${timezone}] : ${start.toString(DayInZone.ymdFormatter)}"

}

