package com.themillhousegroup.arallon.traits

import org.specs2.mutable.Specification
import org.specs2.mock._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.themillhousegroup.arallon.zones._
import com.themillhousegroup.arallon.TimeInZone

class SerializingSpec extends Specification {

  val testTimeMillis = 432198765L
  val testZone = "Europe/Paris"

  "Serialization" should {

    "Allow a TimeInZone[UTC] to be serialized to a String" in {
      val t = TimeInZone.fromUTCMillis(testTimeMillis)
      TimeInZoneSerializing.serialize(t) must beEqualTo(s"${testTimeMillis}Z:UTC")
    }

    "Allow a TimeInZone[TZ] to be serialized to a String" in {
      val t = TimeInZone.fromMillis(testTimeMillis, testZone)
      TimeInZoneSerializing.serialize(t) must beEqualTo(s"${testTimeMillis}Z:$testZone")
    }
  }

  "Deserialization" should {

    "Allow a TimeInZone[UTC] to be deserialized from a String" in {
      TimeInZoneSerializing.deserialize(s"${testTimeMillis}Z:UTC") must beSome(TimeInZone.fromUTCMillis(testTimeMillis))
    }

    "Allow a TimeInZone[TZ] to be deserialized from a String" in {
      TimeInZoneSerializing.deserialize(s"${testTimeMillis}Z:$testZone") must beSome(TimeInZone.fromMillis(testTimeMillis, testZone))
    }
  }
}
