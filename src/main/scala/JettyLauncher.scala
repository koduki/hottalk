// vim: set ts=4 sw=4 et:
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ DefaultServlet, ServletContextHandler }
import org.eclipse.jetty.webapp.WebAppContext

object JettyLauncher {
  def main(args: Array[String]) {
    val port = if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    context.setResourceBase("src/main/webapp")
    context.addServlet(classOf[cn.orz.pascal.hottalk.web.ResourceServlet], "/images/*")
    context.addServlet(classOf[cn.orz.pascal.hottalk.web.ResourceServlet], "/css/*")
    context.addServlet(classOf[cn.orz.pascal.hottalk.web.ResourceServlet], "/js/*")
    context.addServlet(classOf[cn.orz.pascal.hottalk.web.ResourceServlet], "/coffee/*")
    context.addServlet(classOf[cn.orz.pascal.hottalk.web.ResourceServlet], "/error/*")
    context.addServlet(classOf[cn.orz.pascal.hottalk.web.WebFront], "/*")

    server.setHandler(context)

    server.start
    server.join
  }

}
