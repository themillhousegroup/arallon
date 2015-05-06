package com.themillhousegroup.arallon.util

import org.specs2.mutable.Specification

class TZBuilderSpec extends Specification {

  "TZBuilder" should {

    "have the most up-to-date list of timezones possible" in {
      TZBuilder.allTimeZoneStrings must haveSize(619) // If this fails, we know that we need to rebuild...
    }

    "be able to create a sequence of TimeZones" in {

      TZBuilder.rebuildTimeZones() must not beEmpty

      TZBuilder.rebuildTimeZones() must haveSize(547) // If this fails, we know that we need to rebuild...
    }
  }

}
