package com.themillhousegroup.arallon

import org.joda.time._
import com.themillhousegroup.arallon.util.ReflectionHelper
import com.themillhousegroup.arallon.traits._
import org.joda.time.format.DateTimeFormatter

import scala.reflect._
import scala.reflect.ClassTag

object TimeInZone {
  /** Uses the current JVM's timezone to return a strongly-typed instance */
  def now: TimeInZone[TimeZone] = {
    val z = DateTimeZone.getDefault.getID
    now(z)
  }

  /** This instant, expressed in the given TimeZone - i.e. the 'local' field will be appropriate */
  def now(javaTimeZoneName: String): TimeInZone[TimeZone] = {
    val tzInstance = TimeZone(javaTimeZoneName)
    val utc = new DateTime(DateTimeZone.UTC)
    new TimeInZone(tzInstance, utc)
  }

  /** When you just want whatever time it is right now, expressed in UTC */
  def nowUTC: TimeInZone[UTC] = {
    val utc = new DateTime(DateTimeZone.UTC)
    new TimeInZone(TimeZone.UTC, utc)
  }

  /** When all you have is millis. Implies that you are in UTC, so gives back a strong type to that effect */
  def fromUTCMillis(utcMillis: Long): TimeInZone[UTC] = {
    val utcTime = new DateTime(utcMillis, DateTimeZone.UTC)
    new TimeInZone(TimeZone.UTC, utcTime)
  }

  /** Take an instant expressed in millis (UTC) and return it as a TimeInZone in the specified zone */
  def fromMillis(utcMillis: Long, javaTimeZoneName: String): TimeInZone[TimeZone] = {
    val utc = new org.joda.time.DateTime(utcMillis, DateTimeZone.UTC)
    val tzInstance = TimeZone(javaTimeZoneName)
    new TimeInZone(tzInstance, utc)
  }

  def fromUTCTo[T <: TimeZone: ClassTag](utcTime: DateTime): TimeInZone[T] = {
    val t = classTag[T]
    val tzInstance: T = ReflectionHelper.construct(t, List())
    new TimeInZone(tzInstance, utcTime)
  }

  /** You have a Joda DateTime that is already in UTC, you just want the TimeInZone equivalent */
  def fromUTC(utcTime: DateTime): TimeInZone[UTC] = {
    new TimeInZone(TimeZone.UTC, utcTime)
  }

  /**
   * Returns "now" in the given timezone
   *
   * e.g. val nowInMelbourne = TimeInZone[Melbourne]
   */
  def apply[T <: TimeZone: ClassTag]: TimeInZone[T] = {
    val nowUTC = new DateTime(DateTimeZone.UTC)
    val t = classTag[T]
    val tzInstance: T = ReflectionHelper.construct(t, List())
    populateWithUtcTime(tzInstance, nowUTC).asInstanceOf[TimeInZone[T]]
  }

  /**
   * Returns a TimeInZone representing the specified instant in the given timezone
   *
   * e.g. val nowInMelbourne = TimeInZone[Melbourne]
   */
  def apply[T <: TimeZone: ClassTag](timeInThatZone: DateTime): TimeInZone[T] = {
    val t = classTag[T]
    val tzInstance: T = ReflectionHelper.construct(t, List())
    populateWithTime(tzInstance, timeInThatZone).asInstanceOf[TimeInZone[T]]
  }

  /**
   * Returns a TimeInZone representing the specified instant in the given timezone
   *
   * e.g. val nowInMelbourne = TimeInZone[Melbourne]
   */
  def apply(javaTimeZoneName: String, timeInThatZone: DateTime): TimeInZone[TimeZone] = {
    val tzInstance = TimeZone(javaTimeZoneName)
    populateWithTime(tzInstance, timeInThatZone)
  }

  private def populateWithTime(tzInstance: TimeZone, timeInThatZone: DateTime) = {
    val utc = timeInThatZone.withZoneRetainFields(tzInstance.zone).withZone(DateTimeZone.UTC)
    populateWithUtcTime(tzInstance, utc)
  }

  private def populateWithUtcTime(tzInstance: TimeZone, utcTime: DateTime) = {
    new TimeInZone(tzInstance, utcTime)
  }
}

final case class TimeInZone[TZ <: TimeZone](val timezone: TZ, val utc: DateTime) extends Ordered[TimeInZone[TZ]]
    with Comparisons[TZ] with Serializing[TZ] {
  val utcMillis: Long = utc.getMillis
  lazy val local: DateTime = utc.withZone(timezone.zone)
  lazy val asLocalDateTime: LocalDateTime = local.toLocalDateTime

  /**
   * Note this only works against other instances of TimeInZone[T].
   * TODO: Supply an Ordering[TimeInZone[_ <: Timezone] from the companion object
   */
  def compare(that: TimeInZone[TZ]): Int = (this.utcMillis - that.utcMillis).toInt

  /** Return a TimeInZone that represents the exact same instant, but in TimeZone B */
  def map[B <: TimeZone: ClassTag]: TimeInZone[B] = {
    val t = classTag[B]
    if (t.runtimeClass == timezone.getClass) {
      this.asInstanceOf[TimeInZone[B]]
    } else {
      TimeInZone.fromUTCTo[B](this.utc)
    }
  }

  /** Return a TimeInZone that represents the exact same instant, but in the TimeZone denoted by the given String */
  def map(javaTimeZoneName: String): TimeInZone[TimeZone] = {
    val tzInstance = TimeZone(javaTimeZoneName)
    new TimeInZone(tzInstance, utc)
  }

  /** Apply a transform to the underlying _local_ DateTime, resulting in a new instance in the same TimeZone */
  def transform(transformation: DateTime => DateTime): TimeInZone[TZ] = {
    val result = transformation(local)
    this.copy(utc = result.withZone(DateTimeZone.UTC))
  }

  override val toString: String = {
    s"TimeInZone[${timezone}] UTC: '$utc' UTCMillis: '$utcMillis' Local: '$local' LocalDT: '$asLocalDateTime'"
  }

  /** Much like Joda-Time's `toString(formatter` -
    * supply a formatter that will be called with the
    * underlying **UTC** Joda-Time instance
    * */
  def toString(formatter:DateTimeFormatter): String = {
    utc.toString(formatter)
  }
  
  /** Much like Joda-Time's `toString(formatter` -
    * supply a formatter that will be called with the
    * underlying **LOCAL** Joda-Time instance
    * */
  def toLocalString(formatter:DateTimeFormatter): String = {
    local.toString(formatter)
  }
}

