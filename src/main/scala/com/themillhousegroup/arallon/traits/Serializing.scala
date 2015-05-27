package com.themillhousegroup.arallon.traits

import com.themillhousegroup.arallon._

trait Serializing[TZ <: TimeZone] {
  this: TimeInZone[TZ] =>

  def serialize: String = TimeInZoneSerializing.serialize(this)
}

object TimeInZoneSerializing {
  // There seems to be no way to express this just once: :-(
  private val serializationFormat = """%d:%s"""
  private val deserializationRegex = """^(\d*)[:](.*)$""".r

  def serialize[TZ <: TimeZone](t: TimeInZone[TZ]): String = {
    val r = String.format(serializationFormat, t.utcMillis.asInstanceOf[Object], t.timezone.name)
    // Ensure we can read our own output
    if (deserialize(r) != t) {
      throw new RuntimeException(s"Couldn't read our own serialized output: $r")
    }
    r
  }

  def deserialize(s: String): Option[TimeInZone[TimeZone]] = {
    deserializationRegex.findFirstMatchIn(s).map { m =>
      val utcMillis = m.group(1).toLong
      val zoneString = m.group(2)
      TimeInZone.fromMillis(utcMillis, zoneString)
    }
  }
}
