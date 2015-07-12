package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class TimeInZoneInstanceSpec extends Specification with LazyLogging {

  "Strongly-typed time instances" should {

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

    "allow me to map from one timezone to another by type signature alone" in {
      val paris = TimeInZone[Paris]

      val melbourne = paris.map[Melbourne]
      paris must not be equalTo(melbourne)

      paris.timezone must not be equalTo(melbourne.timezone)

      paris.utc must be equalTo (melbourne.utc)
    }

    "allow me to map from one timezone to another by Java timezone name" in {
      val paris = TimeInZone[Paris]

      val melbourne = paris.map("Australia/Melbourne")
      paris must not be equalTo(melbourne)

      paris.timezone must not be equalTo(melbourne.timezone)

      paris.utc must be equalTo (melbourne.utc)
    }

    "return 'this' if mapping from one timezone to itself" in {
      val paris = TimeInZone[Paris]

      val melbourne = paris.map[Melbourne]
      paris must not be equalTo(melbourne)

      val p1 = System.identityHashCode(paris)
      val paris2 = paris.map[Paris]
      val p2 = System.identityHashCode(paris2)

      p1 must beEqualTo(p2)
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
