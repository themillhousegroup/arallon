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

  "Serialization (from object)" should {

    "Allow a TimeInZone[UTC] to be serialized to a String" in {
      val t = TimeInZone.fromUTCMillis(testTimeMillis)
      TimeInZoneSerializing.serialize(t) must beEqualTo(s"${testTimeMillis}Z:UTC")
    }

    "Allow a TimeInZone[TZ] to be serialized to a String" in {
      val t = TimeInZone.fromMillis(testTimeMillis, testZone)
      TimeInZoneSerializing.serialize(t) must beEqualTo(s"${testTimeMillis}Z:$testZone")
    }

    "Throw a RuntimeException if we can't read our own output" in {
      val t = TimeInZone.fromMillis(-1, testZone) // The negative sign is not expected when we read our output
      TimeInZoneSerializing.serialize(t) must throwA[RuntimeException]
    }
  }

  "Serialization (from instance)" should {

    "Allow a TimeInZone[UTC] to be serialized to a String" in {
      val t = TimeInZone.fromUTCMillis(testTimeMillis)
      t.serialize must beEqualTo(s"${testTimeMillis}Z:UTC")
    }

    "Allow a TimeInZone[TZ] to be serialized to a String" in {
      val t = TimeInZone.fromMillis(testTimeMillis, testZone)
      t.serialize must beEqualTo(s"${testTimeMillis}Z:$testZone")
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
