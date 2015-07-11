package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class TimeZoneSpec extends Specification with LazyLogging {

  "TimeZones" should {
    "be unequal if referring to a different zone" in {
      val paris = new Paris()
      val london = new London()

      paris must not be equalTo(london)
    }

    "be equal if referring to the same zone" in {
      val p1 = new Paris()
      val p2 = new Paris()

      p1 must beEqualTo(p2)
    }

    "be equal even if constructed different ways" in {
      val p1 = new Paris()
      val p2 = TimeZone.apply("Europe/Paris")
      val p3 = TimeZone.apply("Europe/Paris")

      p1 must beEqualTo(p2)
      p2 must beEqualTo(p3)
    }

  }

  "TimeZone toString" should {
    "render UTC as simply 'UTC'" in {
      TimeZone.UTC.toString must beEqualTo("UTC")
    }
  }
}
