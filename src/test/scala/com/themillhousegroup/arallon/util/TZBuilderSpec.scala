package com.themillhousegroup.arallon.util

import org.specs2.mutable.Specification

class TZBuilderSpec extends Specification {

  "TZBuilder" should {

    "have the most up-to-date list of timezones possible" in {
      TZBuilder.allTimeZoneStrings.size must beGreaterThanOrEqualTo(616) // If this fails, we know that we need to rebuild...
    }

    "be able to create a sequence of TimeZones" in {

      TZBuilder.rebuildTimeZones must not beEmpty

//			println(TZBuilder.rebuildTimeZones.mkString(""))

      TZBuilder.rebuildTimeZones.size must beGreaterThanOrEqualTo(545) // If this fails, we know that we need to rebuild...
    }
  }

}
