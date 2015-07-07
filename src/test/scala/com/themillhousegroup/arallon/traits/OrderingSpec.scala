package com.themillhousegroup.arallon

import org.specs2.mutable.Specification
import org.specs2.mock._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.joda.time.{ DateTimeZone, DateTime }
import com.typesafe.scalalogging.slf4j._
import com.themillhousegroup.arallon.zones._

class OrderingSpec extends Specification with LazyLogging {

  case class TestObject(name: String, time: TimeInZone[UTC])
  val st = TimeInZone.fromUTCMillis(3333)
  val s = TestObject("start", st)
  val mt = TimeInZone.fromUTCMillis(6666)
  val m = TestObject("middle", mt)
  val et = TimeInZone.fromUTCMillis(9999)
  val e = TestObject("end", et)

  "Time ordering" should {
    "Allow a collection of objects to be sorted by their time field - via sortBy" in {
      List(e, s, m).sortBy(_.time) must be equalTo (List(s, m, e))
    }

    "Order a collection of objects with a time field ascendingly - via sortBy" in {
      val testList = List(e, m, s)

      val manuallySorted = testList.sortWith {
        case (o1, o2) =>
          o1.time.utcMillis < o2.time.utcMillis
      }
      testList.sortBy(_.time) must be equalTo (manuallySorted)
    }

    "Allow a collection of TimeInZone objects to be sorted - via sorted" in {
      List(st, et, mt).sorted must be equalTo (List(st, mt, et))
    }
  }
}
