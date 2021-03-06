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

  def isLogin() = {
    session.contains("user")
  }
  get("/") {
    info("development mode is " + isDevelopmentMode)
    val events = EventDao.find(MongoDBObject()).toList
    ssp("index", "title" -> "Top:", "events" -> events, "isLogin" -> isLogin)
  }

  get("/events/new") {
    if (!isLogin) {
      redirect("/autherror")
    }
    ssp("new", "title" -> "Top:", "isLogin" -> isLogin)
  }

  get("/event/:oid") {
    val oid = new ObjectId(params("oid"))
    val event = EventDao.findOneByID(oid) match { case Some(x) => x; case _ => null }
    val isDelete = if(isLogin){
      val self = session("user").asInstanceOf[User]
      (self.id == event.owner.id) || self.name == "Keiko Miwa"
    }else{
      false
    }

    ssp("show", "title" -> "Top:", "event" -> event, "isLogin" -> isLogin, "isDelete" -> isDelete)
  }
  get("/logout") {
    session -= "user"
    redirect("/")
  }
  get("/login") {
    import org.scribe.oauth._
    import org.scribe.builder.api._
    import org.scribe.builder._
    import org.scribe.model._

    val callbackUrl = "http://hottalk.herokuapp.com/facebook/callback"
    val service = new ServiceBuilder().provider((new FacebookApi).getClass).apiKey("361347227278534").apiSecret("817a2ff8fe2d99045406ad5ea33828a0").callback(callbackUrl).build
    val redirectUrl = service.getAuthorizationUrl(null)
    session("service") = service
    redirect(redirectUrl)
  }

  get("/facebook/callback") {
    import org.scribe.oauth._
    import org.scribe.builder.api._
    import org.scribe.builder._
    import org.scribe.model._
    val service = session("service").asInstanceOf[org.scribe.oauth.OAuthService]
    val code = params("code")

    val verifier = new Verifier(code)

    val token = service.getAccessToken(null, verifier)
    val request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me?fields=picture,name,link")
    service.signRequest(token, request)

    val response = request.send()

    import scala.util.parsing.json.JSON;
    val user = JSON.parseFull(response.getBody()) match { case Some(x) => x; case None => "" }
    val name = user.asInstanceOf[Map[String, String]]("name")
    val link = user.asInstanceOf[Map[String, String]]("link")
    val id = user.asInstanceOf[Map[String, String]]("id")
    val imageUrl = user.asInstanceOf[Map[String, Map[String, Map[String, String]]]]("picture")("data")("url")

    session("user") = User(id, name, imageUrl, link)
    println(User(id, name, imageUrl, link))

    redirect("/")
  }

  post("/events/create") {
    if (!isLogin) {
      redirect("/autherror")
    }
    val startdate = params("startdate").split(":").map(_.toInt)
    val event = Event(
      title = params("title"),
      detail = params("detail"),
      minNumber = params("min_number").toInt,
      maxNumber = params("min_number").toInt,
      place = params("place"),
      startDatetime = time(startdate(0), startdate(1)),
      endDatetime = time(startdate(0), startdate(1)),
      owner = session("user").asInstanceOf[User],
      users = Set(),
      comments = List())
    EventDao.save(event)

    redirect("/")
  }

  get("/autherror") {
    ssp("autherror", "title" -> "Error:", "isLogin" -> isLogin)
  }

  post("/events/join/:oid") {
    if (!isLogin) {
      redirect("/autherror")
    }
    val oid = new ObjectId(params("oid"))
    val event = EventDao.findOneByID(oid) match { case Some(x) => x; case _ => null }
    val user = session("user").asInstanceOf[User]
    EventDao.save(event.join(user))
    redirect("/event/" + oid)
  }
  post("/events/comment/:oid") {
    if (!isLogin) {
      redirect("/autherror")
    }
    val oid = new ObjectId(params("oid"))
    val message = params("message")
    val event = EventDao.findOneByID(oid) match { case Some(x) => x; case _ => null }
    val user = session("user").asInstanceOf[User]
    EventDao.save(event.comment(user, message))
    redirect("/event/" + oid)
  }
  get("/events/delete/:oid") {
    if (!isLogin) {
      redirect("/autherror")
    }
    val oid = new ObjectId(params("oid"))
    val event = EventDao.findOneByID(oid) match { case Some(x) => x; case _ => null }
    EventDao.remove(event)
    redirect("/")
  }
  notFound {
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
