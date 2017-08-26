package cn.migu.tracing.collector

import java.sql.Timestamp
import java.util.concurrent.Executors

import cn.migu.tracing.akkaapp.InstrumentedActor
import cn.migu.tracing.logger.Logging
import com.typesafe.config.Config
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.commons.logging.LogFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


object CollectorSupervisor {

  case class Collector(params: Map[String,String])

  case class Span(traceId: String, spanId: String, parentSpanId: String, traceName: String, spanName: String, reqType: Int, annotation: String, timestamp: Timestamp, point: String)

  // Errors/Responses
  case object HandlerSucess

  //case class HandlerException(t: Throwable)

}

class CollectorSupervisorActor(config: Config) extends InstrumentedActor with Logging {

  import CollectorSupervisor._

  val errorLogger = LogFactory.getLog("error")

  def wrappedReceive: Receive = {
    case Collector(params: Map[String,String]) => sender ! collector(params)
  }

  def collector(params: Map[String,String]): Any = {
    asynRequest(params)
    HandlerSucess
  }



  private implicit val ec = new ExecutionContext {
    val threadPool = Executors.newCachedThreadPool()

    def execute(runnable: Runnable) {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable) {
      throw t
    }
  }

  private[this] def asynRequest(params: Map[String,String]): Unit = {

    val f: Future[String] = Future {

      "success"
    }

    f.onComplete {
      case Success(resp) => {
        //info(s"$key:export game records sucess")
      }
      case Failure(e) => {
        error(s"failed", e)
      }
    }


  }


}