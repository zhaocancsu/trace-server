package cn.migu.tracing.collector

object TraceKeys {
  //调用链id
  val TraceId = "traceid"
  //调用链业务名称
  val TraceName = "tracename"
  //请求id
  val SpanId = "spanid"
  //请求业务名称
  val SpanName = "spanname"
  //父请求id
  val SpanParentId = "spanparentid"
  //请求类型(cs,cr,ss,sr)
  val RequestType = "type"
  //请求返回状态
  val Status = "status"
  //请求发起host地址
  val Host = "host"
  //请求发起时间戳
  val TimeStamp = "timestamp"
  //请求注释
  val Annotation = "annotation"
}
