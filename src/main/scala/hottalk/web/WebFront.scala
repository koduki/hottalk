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

class WebFront extends BasicServlet {
//  val config = ConfigReader[MyConfig]("config.scala")

  get("/") {
    info("development mode is " + isDevelopmentMode)

  //  val feeds = Books(config).getFeeds(Providers.bookWalker, 8) ++ Books(config).getFeeds(Providers.paburi, 8) ++ Books(config).getFeeds(Providers.eBookJapan, 8)
  //  val bookCount = BookDao.count()

    ssp("index","title" -> "Top:")
  }
  
  get("/new") {

  //  val feeds = Books(config).getFeeds(Providers.bookWalker, 8) ++ Books(config).getFeeds(Providers.paburi, 8) ++ Books(config).getFeeds(Providers.eBookJapan, 8)
  //  val bookCount = BookDao.count()

    ssp("new","title" -> "Top:")
  }
  notFound {
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
