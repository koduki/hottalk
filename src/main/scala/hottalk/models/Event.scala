package cn.orz.pascal.hottalk.models

// vim: set ts=2 sw=2 et:
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import java.util.Date

case class Event(
//  @Key("_id") id: ObjectId = new ObjectId,
  val title: String,
  val detail: String,
  val minNumber: Int,
  val maxNumber: Int,
  val place: String,
  val startDatetime: Date,
  val endDatetime: Date) {
}

object EventDao extends SalatDAO[Event, ObjectId](collection = MongoConnection()("test")("events"))
