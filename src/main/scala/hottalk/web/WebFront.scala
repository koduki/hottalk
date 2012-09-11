package cn.orz.pascal.hottalk.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport
import cn.orz.pascal.hottalk.models._
import cn.orz.pascal.hottalk.config._
import cn.orz.pascal.commons.utils.LoggingSupport
import cn.orz.pascal.commons.utils.DateUtils._
import cn.orz.pascal.commons.utils.ConfigReader
import cn.orz.pascal.commons.utils.Serializer
import ch.qos.logback._
import org.slf4j._
import scala.actors.Futures._
import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import java.util.Date

class WebFront extends BasicServlet {
  //  val config = ConfigReader[MyConfig]("config.scala")

  get("/") {
    info("development mode is " + isDevelopmentMode)
    val events = EventDao.find(MongoDBObject()).toList
    ssp("index", "title" -> "Top:", "events" -> events)
  }

  get("/events/new") {
    ssp("new", "title" -> "Top:")
  }
  
    get("/events/:oid") {
    val oid = new ObjectId(params("oid"))
    val event = EventDao.findOneByID(oid) match { case Some(x) => x; case _ => null }
        ssp("show", "title" -> "Top:", "event" -> event)
    }

  post("/events/create") {
    val startdate = params("startdate").split(":").map(_.toInt)
    val enddate = params("enddate").split(":").map(_.toInt)
    val event = Event(
      title = params("title"),
      detail = params("detail"),
      minNumber = params("min_number").toInt,
      maxNumber = params("min_number").toInt,
      place = params("place"),
      startDatetime = time(startdate(0), startdate(1)),
      endDatetime = time(enddate(0), enddate(1)))
    EventDao.save(event)

    redirect("/")
  }

  notFound {
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
