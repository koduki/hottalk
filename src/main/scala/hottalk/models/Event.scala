package cn.orz.pascal.hottalk.models

// vim: set ts=2 sw=2 et:
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.MongoURI
import java.util.Date

case class Comment(val name: String, val message: String = "")
case class User(@Key("_id") id: String, val name: String, val imageUrl: String = "", val url: String = "")
case class Event(
  @Key("_id") id: ObjectId = new ObjectId,
  val title: String,
  val detail: String,
  val minNumber: Int,
  val maxNumber: Int,
  val place: String,
  val startDatetime: Date,
  val endDatetime: Date,
  val owner: User,
  val users: Set[User],
  val comments:List[Comment]) {
  def join(user: User): Event = {
    Event(this.id, this.title, this.detail, this.minNumber, this.maxNumber, this.place, this.startDatetime, this.endDatetime, this.owner, this.users + user, this.comments)
  }
  def comment(user:User, message:String) : Event = {
    val comments = List(Comment(name = user.name, message = message))
    Event(this.id, this.title, this.detail, this.minNumber, this.maxNumber, this.place, this.startDatetime, this.endDatetime, this.owner, this.users + user, comments ++ this.comments)
  }

}
object createMongoCoonnection {
  def apply() = {
    import com.novus.salat._
    import com.novus.salat.global._
    import com.novus.salat.annotations._
    import com.novus.salat.dao._
    import com.mongodb.casbah.Imports._
    import com.mongodb.casbah.MongoConnection
    import com.mongodb.casbah.MongoURI

    val uri = MongoURI("mongodb://heroku23:intex8080@alex.mongohq.com:10063/app7562613")
    val mongo = MongoConnection(uri)
    val db = mongo(uri.database)
    db.authenticate(uri.username, uri.password.foldLeft("")(_ + _.toString))
    db
  }
}
object EventDao extends SalatDAO[Event, ObjectId](collection = createMongoCoonnection()("events"))
