package cn.migu.tracing.database

import java.sql.Timestamp

import cn.migu.tracing.collector.CollectorSupervisor.Span
import scalikejdbc._

/**
  *
  *
  * author  zhaocan
  * version  [版本号, 2017/8/23]
  * see  [相关类/方法]
  * since  [产品/模块版本]
  */
object SpanDao extends BaseDao {

  /*def queryTest(): Unit = {
    NamedDB('tracing) readOnly { implicit session => {
      val entities: List[Map[String, Any]] = sql"select * from unify.unify_job where code like '测试%'".map(_.toMap).list.apply()
      entities.foreach(e => {
        println(e("NAME"))
      })
    }
    }
  }*/
  def insertOne(span: Span): Unit ={
    NamedDB('tracing) autoCommit { implicit session => {
      sql"insert into unify.trace_span(trace_id,trace_name,span_id,span_name,span_parentid,type,host,timestamp,annotation) values (${span.traceId}, ${span.traceName}, ${span.spanId},${span.spanName},${span.parentSpanId},${span.reqType},${span.point},${span.timestamp},${span.annotation})".update.apply()
    }
    }
  }

  def main(args: Array[String]): Unit = {

    val spanObj = Span("1","11","","zhaocan","my",1 ,"测试",new Timestamp(System.currentTimeMillis()),"127.0.0.1")
    insertOne(spanObj)
    //queryTest
  }

}
