package com.themillhousegroup.arallon

import org.joda.time._
import com.themillhousegroup.arallon.util.ReflectionHelper
import com.themillhousegroup.arallon.zones.UTC
import scala.reflect._
import scala.reflect.runtime.universe._
import scala.reflect.ClassTag

object TimeInZone {
  /** Uses the current JVM's timezone to return a strongly-typed instance */
  def now: TimeInZone[TimeZone] = {
    val z = DateTimeZone.getDefault.getID
    now(z)
  }

  def now(javaTimeZoneName: String): TimeInZone[TimeZone] = {
    val hereNow = new DateTime()
    val tzInstance = TimeZone(javaTimeZoneName)
    populateWithTime(tzInstance, hereNow)
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

  def fromUTC[T <: TimeZone: ClassTag](utcTime: DateTime): TimeInZone[T] = {
    val t = classTag[T]
    val tzInstance: T = ReflectionHelper.construct(t, List())
    new TimeInZone(tzInstance, utcTime)
  }

  def apply[T <: TimeZone: ClassTag]: TimeInZone[T] = {
    val nowUTC = new DateTime(DateTimeZone.UTC)
    apply[T](nowUTC)
  }

  def apply[T <: TimeZone: ClassTag](timeInThatZone: DateTime): TimeInZone[T] = {
    val t = classTag[T]
    val tzInstance: T = ReflectionHelper.construct(t, List())
    populateWithTime(tzInstance, timeInThatZone).asInstanceOf[TimeInZone[T]]
  }

  def apply(javaTimeZoneName: String, timeInThatZone: DateTime): TimeInZone[TimeZone] = {
    val tzInstance = TimeZone(javaTimeZoneName)
    populateWithTime(tzInstance, timeInThatZone)
  }

  private def populateWithTime(tzInstance: TimeZone, timeInThatZone: DateTime) = {
    val utc = timeInThatZone.withZoneRetainFields(tzInstance.zone).withZone(DateTimeZone.UTC)
    new TimeInZone(tzInstance, utc)
  }
}

case class TimeInZone[T <: TimeZone](val timezone: T, val utc: DateTime) extends Ordered[TimeInZone[T]] with traits.Comparisons[T] {
  val utcMillis: Long = utc.getMillis
  lazy val local: DateTime = utc.withZone(timezone.zone)
  lazy val asLocalDateTime: LocalDateTime = local.toLocalDateTime

  /**
   * Note this only works against other instances of TimeInZone[T].
   * TODO: Supply an Ordering[TimeInZone[_ <: Timezone] from the companion object
   */
  def compare(that: TimeInZone[T]): Int = (this.utcMillis - that.utcMillis).toInt

  /** Return a TimeInZone that represents the exact same instant, but in TimeZone B */
  def map[B <: TimeZone: ClassTag]: TimeInZone[B] = {
    TimeInZone.fromUTC[B](this.utc)
  }

  /** Apply a transform to the underlying _local_ DateTime, resulting in a new instance in the same TimeZone */
  def transform(transformation: DateTime => DateTime): TimeInZone[T] = {
    val result = transformation(local)
    this.copy(utc = result.withZone(DateTimeZone.UTC))
  }

  override def toString: String = {
    s"TimeInZone[${timezone}] UTC: '$utc' UTCMillis: '$utcMillis' Local: '$local' LocalDT: '$asLocalDateTime'"
  }

}

