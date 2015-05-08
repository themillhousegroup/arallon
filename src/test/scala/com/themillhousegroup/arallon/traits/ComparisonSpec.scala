package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.specs2.mock._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class ComparisonSpec extends Specification with LazyLogging {

  "Time comparisons" should {
    "Correctly detect 'equal' situations - local TZ" in {
      val earlierHere = TimeInZone.fromUTCMillis(149666777L)
      val laterHere = TimeInZone.fromUTCMillis(159666777L)

      earlierHere.isEqual(laterHere) must beFalse

      laterHere.isEqual(earlierHere) must beFalse

      laterHere.isEqual(laterHere) must beTrue
    }

    "Correctly detect 'equal' situations - across TZ" in {
      val instant = new DateTime()
      val inParis = TimeInZone[Paris](instant)
      val inSydney = TimeInZone[Sydney](instant)
      val inSydney2 = TimeInZone[Sydney](instant)
      val inUTC = TimeInZone.fromUTCMillis(instant.getMillis)

      inParis.isEqual(inSydney) must beFalse // They don't refer to the same instant

      inSydney.isEqual(inSydney2) must beTrue

      inSydney.isEqual(inUTC) must beTrue
    }

    "Correctly detect 'before' situations - local TZ" in {
      val earlierHere = TimeInZone.fromUTCMillis(149666777L)
      val laterHere = TimeInZone.fromUTCMillis(159666777L)

      earlierHere.isBefore(laterHere) must beTrue

      laterHere.isBefore(earlierHere) must beFalse

      laterHere.isBefore(laterHere) must beFalse
    }

    "Correctly detect 'before' situations - across TZ" in {
      val instant = new DateTime()
      val inParis = TimeInZone[Paris](instant)
      val inSydney = TimeInZone[Sydney](instant)
      val inSydney2 = TimeInZone[Sydney](instant)
      val inUTC = TimeInZone.fromUTCMillis(instant.getMillis)

      inSydney.isBefore(inParis) must beTrue // Sydney gets to 9am Monday Jan 1, 2020 before Paris does 
      inParis.isBefore(inSydney) must beFalse

      inSydney.isBefore(inSydney2) must beFalse

      inSydney.isBefore(inUTC) must beFalse
    }

    "Correctly detect 'after' situations - local TZ" in {
      val earlierHere = TimeInZone.fromUTCMillis(149666777L)
      val laterHere = TimeInZone.fromUTCMillis(159666777L)

      earlierHere.isAfter(laterHere) must beFalse

      laterHere.isAfter(earlierHere) must beTrue

      laterHere.isAfter(laterHere) must beFalse
    }
  }
}
