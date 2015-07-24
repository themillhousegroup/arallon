package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class TimeSpanInZoneObjectSpec extends Specification with LazyLogging {
  val par = new Paris()
  val mel = new Melbourne()
  val now = new DateTime()
  val later = now.plusHours(1)
  val muchLater = now.plusHours(7)
  val paris = TimeSpanInZone[Paris](par, now, later)
  val melbourne = TimeSpanInZone[Melbourne](mel, now, later)
	val baseMillis = 1437720026000L

  "Strongly-typed timespan companion object" should {

    "allow creation of a UTC timespan using two Long values" in {
			val utcSpan = TimeSpanInZone.fromUTCMillis(baseMillis, baseMillis + 9000)
			utcSpan.timezone must beEqualTo(TimeZone.UTC)
    }
	}
}
