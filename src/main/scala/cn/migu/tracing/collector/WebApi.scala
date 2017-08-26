package cn.migu.tracing.collector

import javax.net.ssl.SSLContext

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import cn.migu.tracing.akkaapp.{CommonRoutes, WebService}
import cn.migu.tracing.collector.CollectorSupervisor.{Collector, HandlerSucess}
import cn.migu.tracing.common.{ConfigParamsUtils, SSLContextFactory}
import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import spray.http._
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.io.ServerSSLEngineProvider
import spray.json.DefaultJsonProtocol._
import spray.routing.{HttpService, RequestContext, Route}

import scala.concurrent.ExecutionContext

/**
  * web api
  *
  * author  zhaocan
  * version  [版本号, 2017/5/3]
  * see  [相关类/方法]
  * since  [产品/模块版本]
  */
object WebApi {

  val StatusKey = "status"
  val ResultKey = "result"

  def badRequest(ctx: RequestContext, msg: String) {
    ctx.complete(StatusCodes.BadRequest, errMap(msg))
  }

  def notFound(ctx: RequestContext, msg: String) {
    ctx.complete(StatusCodes.NotFound, errMap(msg))
  }

  def errMap(errMsg: String): Map[String, String] = Map(StatusKey -> "error", ResultKey -> errMsg)

  def errMap(errStatus: String, errMsg: String): Map[String, String] = Map(StatusKey -> errStatus, ResultKey -> errMsg)

  def errMap(t: Throwable, status: String): Map[String, String] =
    Map(StatusKey -> status, ResultKey -> formatException(t))

  def successMap(msg: String): Map[String, String] = Map(StatusKey -> "ok", ResultKey -> msg)

  def formatException(t: Throwable): String =
    if (t.getCause != null) {
      Map("message" -> t.getMessage,
        "errorClass" -> t.getClass.getName,
        "cause" -> t.getCause.getMessage,
        "causingClass" -> t.getCause.getClass.getName,
        "stack" -> t.getCause.getStackTrace.map(_.toString).toSeq).toString()
    } else {
      Map("message" -> t.getMessage,
        "errorClass" -> t.getClass.getName,
        "stack" -> t.getStackTrace.map(_.toString).toSeq).toString()
    }
}

class WebApi(system: ActorSystem,
             config: Config,
             port: Int,
             manager: ActorRef) extends HttpService with CommonRoutes with CORSSupport {

  import WebApi._
  import scala.concurrent.duration._

  override def actorRefFactory: ActorSystem = system

  implicit val ec: ExecutionContext = system.dispatcher

  val contextTimeout = ConfigParamsUtils.getRequestTimeout(config)
  val bindAddress = config.getString("tracing.web.bind-address")

  //val reqLogger = LogFactory.getLog("INTFREQ")
  val logger = LoggerFactory.getLogger(getClass)

  val myRoutes = cors {
    serviceRoutes ~ healthzRoutes
  }

  /**
    * web server健康监测
    * GET /healthz  - return OK or error message
    */
  def healthzRoutes: Route = pathPrefix("healthz") {
    get { ctx =>
      ctx.complete("OK")
    }
  }

  /**
    * 接口处理
    * @return
    */
  def serviceRoutes: Route = pathPrefix("tracing") {
    post {
        entity(as[FormData]) { formData =>
          val seq = formData.fields
          val formDataMap = Map(seq map { a => a._1 -> a._2 }: _*)
          path(Segment) { (contextName) => {
            clientIP{ip =>
              respondWithMediaType(MediaTypes.`application/json`) { ctx =>
                contextName match {
                  case "span" =>
                    val future = (manager ? Collector(formDataMap))(contextTimeout.seconds)
                    future.map {
                      case HandlerSucess =>
                        ctx.complete(StatusCodes.OK, successMap("request finish"))
                    }
                  case _ =>
                    ctx.complete(StatusCodes.BadRequest,errMap(s"$contextName is illegal request"))
                }
              }
            }
            }}
        }
      }

  }

  def start() {

    implicit val sslContext: SSLContext = {
      SSLContextFactory.createContext(config.getConfig("spray.can.server"))
    }

    implicit def sslEngineProvider: ServerSSLEngineProvider = {
      ServerSSLEngineProvider { engine =>
        val protocols = config.getStringList("spray.can.server.enabledProtocols")
        engine.setEnabledProtocols(protocols.toArray(Array[String]()))
        engine
      }
    }

    logger.info("Starting browser web service...")
    WebService.start(myRoutes ~ commonRoutes, system, bindAddress, port)
  }

  override def timeoutRoute: Route = {
    complete(500, errMap("request timeout"))
  }



}


