package cn.migu.tracing.database

import com.typesafe.config.ConfigFactory
import scalikejdbc.{AutoSession, ConnectionPool, ConnectionPoolSettings}


trait BaseDao {
  val config = ConfigFactory.load()

  Class.forName(config.getString("tracing.database.driver"))
  implicit val session = AutoSession
  //ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

  val settings = ConnectionPoolSettings(
    initialSize = 5,
    maxSize = 200,
    connectionTimeoutMillis = 5000L,
    validationQuery = "select 1 from dual")

  implicit val factory = C3P0ConnectionPoolFactory
  val url = config.getString("tracing.database.url")
  val user = config.getString("tracing.database.user")
  val password = config.getString("tracing.database.password")
  ConnectionPool.add('tracing,url, user, password, settings)
}
