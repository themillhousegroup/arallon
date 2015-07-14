package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.specs2.mock._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class TimeInZoneObjectSpec extends Specification with LazyLogging {

  "Strongly-typed time object" should {
    "allow construction of current time in a zone" in {
      val hereNow: TimeInZone[_ <: TimeZone] = TimeInZone.now

      logger.info(s"hereNow is a $hereNow")
      hereNow must not beNull
    }

    "allow construction of current time in an explicit zone" in {
      val gmtNow: TimeInZone[_ <: TimeZone] = TimeInZone.now("GMT")

      logger.info(s"gmtNow is a $gmtNow")
      gmtNow must not beNull
    }

    "allow construction of the correct current time in an explicit zone" in {
      val nycNow: TimeInZone[_ <: TimeZone] = TimeInZone.now("America/New_York")
      val parisNow: TimeInZone[_ <: TimeZone] = TimeInZone.now("Europe/Paris")

      logger.info(s"NYC: $nycNow")
      logger.info(s"PAR: $parisNow")

      nycNow must not be equalTo(parisNow)

      // But they still refer to the same instant (give or take a few):
      nycNow.utcMillis / 10000 must be equalTo (parisNow.utcMillis / 10000)

    }

    "allow construction of a time from millis, which must be in UTC" in {
      val millis = 1430222400000L
      val someUTCTime: TimeInZone[UTC] = TimeInZone.fromUTCMillis(millis)

      logger.info(s"someUTCTime is a $someUTCTime")
      someUTCTime must not beNull

      val melTime = someUTCTime.map[Melbourne]
      logger.info(s"melTime: $melTime")

      melTime.utcMillis must beEqualTo(millis)
    }

    "allow construction of a time from a Joda Time UTC instance" in {
      val millis = 1430222400000L
      val joda = new DateTime(millis)
      val someUTCTime: TimeInZone[UTC] = TimeInZone.fromUTC(joda)

      someUTCTime.utcMillis must beEqualTo(millis)
    }

    "allow construction of a UTC time representing 'now'" in {
      val nowUTCTime: TimeInZone[UTC] = TimeInZone.nowUTC

      logger.info(s"nowUTCTime is $nowUTCTime")

      nowUTCTime must not beNull

      val millis = nowUTCTime.utcMillis

      val newUTCTime: TimeInZone[UTC] = TimeInZone.fromUTCMillis(millis)

      newUTCTime must beEqualTo(nowUTCTime)
    }

    "allow construction of a time in a zone by zone ID and millis" in {
      val millis = 1430222400000L
      val newYork = TimeInZone.fromMillis(millis, "America/New_York")
      val losAngeles = TimeInZone.fromMillis(millis, "America/Los_Angeles")

      newYork must not be equalTo(losAngeles)

      newYork.timezone must not be equalTo(losAngeles.timezone)

      newYork.utcMillis must beEqualTo(losAngeles.utcMillis) // They are the same instant after all...
    }

    "allow construction of a time in a zone by zone ID and DT" in {
      val dt = new DateTime()
      val newYork = TimeInZone("America/New_York", dt)
      val losAngeles = TimeInZone("America/Los_Angeles", dt)

      newYork must not be equalTo(losAngeles)

      newYork.timezone must not be equalTo(losAngeles.timezone)

      newYork.utcMillis must not be equalTo(losAngeles.utcMillis)
    }

    "allow construction of current time in a zone purely by type signature" in {
      val paris = TimeInZone[Paris]
      val melbourne = TimeInZone[Melbourne]

      //			logger.info(s"paris is a $paris")
      //			logger.info(s"mel is a $melbourne")

      paris must not be equalTo(melbourne)

      paris.timezone must not be equalTo(melbourne.timezone)
    }
  }
}
