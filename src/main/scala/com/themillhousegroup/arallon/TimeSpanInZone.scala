package com.themillhousegroup.arallon

import org.joda.time._
import com.themillhousegroup.arallon.util.ReflectionHelper
import com.themillhousegroup.arallon.traits._
import scala.reflect._
import scala.reflect.ClassTag

/** Not sure what object methods will be useful. Just doing some obvious ones to begin... */
object TimeSpanInZone {
  /** When all you have is millis. Implies that you are in UTC, so gives back a strong type to that effect */
  def fromUTCMillis(startUtcMillis: Long, endUtcMillis: Long): TimeSpanInZone[UTC] = {
    val startUtcTime = new DateTime(startUtcMillis, DateTimeZone.UTC)
    val endUtcTime = new DateTime(endUtcMillis, DateTimeZone.UTC)
    TimeSpanInZone(TimeZone.UTC, startUtcTime, endUtcTime)
  }
}

/**
 * Represents some finite period of time
 * with a start and an end,
 * that occurs within a given Timezone
 */
case class TimeSpanInZone[TZ <: TimeZone](val timezone: TZ, val start: DateTime, val end: DateTime) extends Ordered[TimeSpanInZone[TZ]] {

  lazy val startUtcMillis = start.getMillis
  lazy val startLocal: DateTime = start.withZone(timezone.zone)
  lazy val startAsLocalDateTime: LocalDateTime = startLocal.toLocalDateTime

  def compare(that: TimeSpanInZone[TZ]): Int = (this.startUtcMillis - that.startUtcMillis).toInt

  /**
   * Return a TimeSpanInZone that represents the same range of time, but in the TimeZone denoted by the given String.
   */
  def map[B <: TimeZone: ClassTag]: TimeSpanInZone[B] = {
    val t = classTag[B]
    if (t.runtimeClass == timezone.getClass) {
      this.asInstanceOf[TimeSpanInZone[B]]
    } else {
      val tzInstance: B = ReflectionHelper.construct(t, List())
      new TimeSpanInZone[B](tzInstance, start.withZone(tzInstance.zone), end.withZone(tzInstance.zone))
    }
  }

  /**
   * Return a TimeSpenInZone that represents the same range of time, but in the TimeZone denoted by the given String.
   */
  def map(javaTimeZoneName: String): TimeSpanInZone[TimeZone] = {
    val tzInstance = TimeZone(javaTimeZoneName)
    new TimeSpanInZone(tzInstance, start, end)
  }

  override val toString: String = {
    s"TimeSpanInZone[${timezone}] StartUTCMillis: '$startUtcMillis', AsLocalDT: '$startAsLocalDateTime'"
  }
}

