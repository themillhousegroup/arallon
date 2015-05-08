package com.themillhousegroup.arallon.traits

import com.themillhousegroup.arallon._

trait Comparisons[T <: TimeZone] {
  this: TimeInZone[T] =>

  def isEqual(other: TimeInZone[_]): Boolean = {
    this.utc.isEqual(other.utc)
  }

  def isBefore(other: TimeInZone[_]): Boolean = {
    this.utc.isBefore(other.utc)
  }

  /** Inclusive (to the millisecond) at both ends */
  def isBetween(startInclusive: TimeInZone[_], endInclusive: TimeInZone[_]): Boolean = {
    (isAfter(startInclusive) || isEqual(startInclusive)) &&
      (isBefore(endInclusive) || isEqual(endInclusive))
  }

  def isAfter(other: TimeInZone[_]): Boolean = {
    this.utc.isAfter(other.utc)
  }
}
