package cn.migu.tracing

import akka.actor.{ActorSystem, Props}
import akka.routing.RoundRobinPool
import cn.migu.tracing.collector.{CollectorSupervisorActor, WebApi}
import cn.migu.tracing.logger.Logging
import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import org.slf4j.LoggerFactory

/**
 * server启动类
 *
 */
object Server extends Logging{

  def start(args: Array[String], makeSystem: Config => ActorSystem): Unit = {
    val config = ConfigFactory.load()
    val system = makeSystem(config)

    val port = config.getInt("tracing.web.port")

    val poolSize = config.getInt("tracing.actor-pool-size")

    val manager = system.actorOf(Props(classOf[CollectorSupervisorActor], config).withRouter(new RoundRobinPool(poolSize)), "trace-actor")

    new WebApi(system, config, port, manager).start()


  }

  def main(args: Array[String]) {
    val logger = LoggerFactory.getLogger(getClass)
    import scala.collection.JavaConverters._
    def makeSupervisorSystem(name: String)(config: Config): ActorSystem = {
      val configWithRole = config.withValue("akka.cluster.roles",
        ConfigValueFactory.fromIterable(List("supervisor").asJava))
      ActorSystem(name, configWithRole)
    }
    start(args, makeSupervisorSystem("TracingServer")(_))

    sys.addShutdownHook{
      logger.error("tracing server shutdown")
    }
  }

}
