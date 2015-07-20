package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class TimeSpanInZoneInstanceSpec extends Specification with LazyLogging {
  val par = new Paris()
  val mel = new Melbourne()
  val now = new DateTime()
  val later = now.plusHours(1)
  val muchLater = now.plusHours(7)
  val paris = TimeSpanInZone[Paris](par, now, later)
  val melbourne = TimeSpanInZone[Melbourne](mel, now, later)

  "Strongly-typed timespan instances" should {

    "permit comparison of current times in a zone" in {
      val parisInMel = paris.map[Melbourne]

      logger.info(s"paris is a   $paris")
      logger.info(s"mel is a $melbourne")
      logger.info(s"pIM is a $parisInMel")

      paris must not be equalTo(melbourne)

      paris.timezone must not be equalTo(melbourne.timezone)

      paris.startUtcMillis must be equalTo (melbourne.startUtcMillis)
    }

    /* TODO
		"support checking whether a TimeInZone is inside the timespan" in {

		}
*/
    "support checking how another TimeSpanInZone compares with this one by comparing start instants - exact same tests" in {
      paris.compare(paris) must beEqualTo(0)
      paris.compare(paris.copy()) must beEqualTo(0)
      paris.compare(paris.copy(end = muchLater)) must beEqualTo(0)
      paris.compare(melbourne.map[Paris]) must beEqualTo(0)
    }

    "support checking how another TimeSpanInZone compares with this one by comparing start instants - later tests" in {
      paris.compare(paris.copy(start = later, end = muchLater)) must beEqualTo(-3600000)
    }

    "support checking how another TimeSpanInZone compares with this one by comparing start instants - earlier tests" in {
      val p2 = paris.copy(start = later, end = muchLater)
      p2.compare(paris) must beEqualTo(3600000)
    }

    "Not permit silly programming errors" in {
      def somethingThatWorksInMelbourne(mel: TimeSpanInZone[Melbourne]): Boolean = {
        true
      }

      def somethingThatWorksAnywhere(any: TimeSpanInZone[_]): Boolean = {
        true
      }

      somethingThatWorksInMelbourne(melbourne) must beTrue

      // Won't compile. By design. Try it! :-)
      // somethingThatWorksInMelbourne(paris) must beTrue

      somethingThatWorksAnywhere(melbourne) must beTrue
      somethingThatWorksAnywhere(paris) must beTrue

    }

    "allow me to map from one timezone to another by type signature alone" in {
      val mel2 = paris.map[Melbourne]
      paris must not be equalTo(mel2)

      paris.timezone must not be equalTo(mel2.timezone)

      paris.startUtcMillis must be equalTo (mel2.startUtcMillis)
    }

    "allow me to map from one timezone to another by Java timezone name" in {
      val mel3 = paris.map("Australia/Melbourne")
      paris must not be equalTo(mel3)

      paris.timezone must not be equalTo(mel3.timezone)

      paris.startUtcMillis must be equalTo (mel3.startUtcMillis)
    }

    "return 'this' if mapping from one timezone to itself" in {
      val mel4 = paris.map[Melbourne]
      paris must not be equalTo(mel4)

      val p1 = System.identityHashCode(paris)
      val paris2 = paris.map[Paris]
      val p2 = System.identityHashCode(paris2)

      p1 must beEqualTo(p2)
    }
  }
}
