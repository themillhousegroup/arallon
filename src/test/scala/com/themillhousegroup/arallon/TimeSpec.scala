package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.specs2.mock._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class TimeSpec extends Specification with LazyLogging {

  "Strongly-typed time" should {
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

    "allow construction of a time from millis, which must be in UTC" in {
      val millis = 1430222400000L
      val someUTCTime: TimeInZone[UTC] = TimeInZone.fromUTCMillis(millis)

      logger.info(s"someUTCTime is a $someUTCTime")
      someUTCTime must not beNull

      val melTime = someUTCTime.map[Melbourne]
      logger.info(s"melTime: $melTime")

      melTime.utcMillis must beEqualTo(millis)
    }

    "allow construction of a UTC time representing 'now'" in {
      val nowUTCTime: TimeInZone[UTC] = TimeInZone.nowUTC

      logger.info(s"nowUTCTime is $nowUTCTime")

      nowUTCTime must not beNull

      val millis = nowUTCTime.utcMillis

      val newUTCTime: TimeInZone[UTC] = TimeInZone.fromUTCMillis(millis)

      newUTCTime must beEqualTo(nowUTCTime)
    }

    "allow construction of current time in a zone purely by type signature" in {
      val paris = TimeInZone[Paris]
      val melbourne = TimeInZone[Melbourne]

      //			logger.info(s"paris is a $paris")
      //			logger.info(s"mel is a $melbourne")

      paris must not be equalTo(melbourne)

      paris.timezone must not be equalTo(melbourne.timezone)
    }

    "permit comparison of current times in a zone" in {
      val now = new DateTime()
      val paris = TimeInZone[Paris](now)
      val melbourne = TimeInZone[Melbourne](now)
      val parisInMel = paris.map[Melbourne]

      logger.info(s"paris is a   $paris")
      logger.info(s"mel is a $melbourne")
      logger.info(s"pIM is a $parisInMel")

      paris must not be equalTo(melbourne)

      paris.timezone must not be equalTo(melbourne.timezone)

      paris.utc must not be equalTo(melbourne.utc)
    }

    "Not permit silly programming errors" in {
      val paris = TimeInZone[Paris]
      val melbourne = TimeInZone[Melbourne]

      def somethingThatWorksInMelbourne(mel: TimeInZone[Melbourne]): Boolean = {
        true
      }

      def somethingThatWorksAnywhere(any: TimeInZone[_]): Boolean = {
        true
      }

      somethingThatWorksInMelbourne(melbourne) must beTrue

      // Won't compile. By design. Try it! :-)
      // somethingThatWorksInMelbourne(paris) must beTrue

      somethingThatWorksAnywhere(melbourne) must beTrue
      somethingThatWorksAnywhere(paris) must beTrue

    }

    "allow me to map from one timezone to another" in {
      val paris = TimeInZone[Paris]

      val melbourne = paris.map[Melbourne]
      paris must not be equalTo(melbourne)

      paris.timezone must not be equalTo(melbourne.timezone)

      paris.utc must be equalTo (melbourne.utc)
    }

    "allow me to offset a time within a timezone" in {
      val paris = TimeInZone[Paris]

      val parisMillis = paris.utcMillis

      val laterThatDay = paris.transform(_.plusHours(3))
      paris must not be equalTo(laterThatDay)

      paris.timezone must beEqualTo(laterThatDay.timezone)

      paris.utc must not be equalTo(laterThatDay.utc)

      val laterMillis = laterThatDay.utcMillis

      laterMillis must beEqualTo(parisMillis + (3 * 60 * 60 * 1000))
    }

    "allow me to adjust a time within a timezone" in {
      val paris = TimeInZone[Paris]

      val laterThatDay = paris.transform(_.withTime(17, 0, 0, 0))
      paris must not be equalTo(laterThatDay)

      paris.timezone must beEqualTo(laterThatDay.timezone)

      paris.utc must not be equalTo(laterThatDay.utc)

      println(laterThatDay.utc.getHourOfDay)

      laterThatDay.utc.getHourOfDay must not be equalTo(17)
    }
  }
}
