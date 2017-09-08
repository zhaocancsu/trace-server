package cn.migu.tracing.collector

import java.sql.Timestamp

import scala.util.control.NonFatal

case class Span(traceId: String, spanId: String, parentSpanId: String, traceName: String, spanName: String, reqType: String, annotation: String, timestamp: Timestamp, point: String,status: String)

object Span{
  def apply(values: Map[String,String]): Span = try{
    Span(values("traceId"),values("spanId"),values("spanParentId"),values("traceName"),values("spanName"),values("type"),values("annotation"),new Timestamp(values("timestamp").toLong),values("host"),values("status"))
  } catch{
    case NonFatal(ex) =>
      ex.printStackTrace()
      null
  }
}
