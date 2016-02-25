package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class DayInZoneInstanceSpec extends Specification with LazyLogging {
  val now = new DateTime()
  val parisTZ = new Paris()
  val melbourneTZ = new Melbourne()

  val paris = new DayInZone[Paris](parisTZ, now, now)
  val melbourne = new DayInZone[Melbourne](melbourneTZ, now, now)

  "Strongly-typed day-in-zone instances" should {

    "permit creation of day in a zone" in {
      logger.info(s"paris is a   $paris")
      logger.info(s"mel is a $melbourne")

      paris must not be equalTo(melbourne)

      paris.timezone must not be equalTo(melbourne.timezone)
    }

    "Not permit silly programming errors" in {

      def somethingThatWorksInMelbourne(mel: DayInZone[Melbourne]): Boolean = {
        true
      }

      def somethingThatWorksAnywhere(any: DayInZone[_]): Boolean = {
        true
      }

      somethingThatWorksInMelbourne(melbourne) must beTrue

      // Won't compile. By design. Try it! :-)
      // somethingThatWorksInMelbourne(paris) must beTrue

      somethingThatWorksAnywhere(melbourne) must beTrue
      somethingThatWorksAnywhere(paris) must beTrue

    }

    "allow me to map from one timezone to another by type signature alone" in {

      val melbourne2 = paris.map[Melbourne]
      paris must not be equalTo(melbourne2)

      paris.timezone must not be equalTo(melbourne2.timezone)

      melbourne.timezone must be equalTo (melbourne2.timezone)
    }
  }
}
