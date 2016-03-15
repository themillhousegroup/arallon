package com.themillhousegroup.arallon.util

/**
 * Should you ever need to rebuild the list of timezones,
 * this will do it using the latest Java TZ data:
 */
object TZBuilder {

  val caseInsensitiveStringSort = (a: String, b: String) => String.CASE_INSENSITIVE_ORDER.compare(a, b) < 0

  lazy val allTimeZoneStrings = java.util.TimeZone.getAvailableIDs.toList

  lazy val sortedTimeZoneStrings = allTimeZoneStrings.sortWith(caseInsensitiveStringSort)

  val noForbiddenChars = (s: String) => !(s.contains("+") || s.contains("-"))

  def rebuildTimeZones(): List[String] = {

    val singleWordNames = sortedTimeZoneStrings.filter(noForbiddenChars).map { tz =>
      val last = tz.split('/').last
      last -> tz
    }.toMap

    singleWordNames.filter { case (k, v) => k != "UTC" }.map {
      case (k, v) =>
        k -> s"""final class $k extends NonUTCTimeZone("$v")\n"""
    }.values.toList.sortWith(caseInsensitiveStringSort)
  }
}
