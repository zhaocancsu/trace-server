package cn.migu.tracing.akkaapp

import java.util.concurrent.TimeUnit

import com.yammer.metrics.Metrics


/**
 * ActorMetrics提供下面的指标度量功能:
 * * message-handler.meter.{mean,m1,m5,m15} = 接收处理对象被调用的平均速率
 * * message-handler.duration.{mean,p75,p99,p999} = wrappedReceive处理的实时直方图
 *
 * NOTE: 传入消息的数量可以使用meter.count来跟踪
 */
trait ActorMetrics extends ActorStack {
  // Timer includes a histogram of wrappedReceive() duration as well as moving avg of rate of invocation
  val metricReceiveTimer = Metrics.newTimer(getClass, "message-handler",
                                            TimeUnit.MILLISECONDS, TimeUnit.SECONDS)

  override def receive: Receive = {
    case x =>
      val context = metricReceiveTimer.time()
      try {
        super.receive(x)
      } finally {
        context.stop()
      }
  }
}
