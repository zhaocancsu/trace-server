package cn.migu.tracing.database

import java.sql.Connection
import javax.sql.DataSource

import scalikejdbc.{ConnectionPool, ConnectionPoolFactory, ConnectionPoolSettings}

object C3P0ConnectionPoolFactory extends ConnectionPoolFactory {
  override def apply(url: String, user: String, password: String,
                     settings: ConnectionPoolSettings = ConnectionPoolSettings()) = {
    new C3P0ConnectionPool(url, user, password, settings)
  }
}

/**
  * c3p0 Connection Pool
  */
class C3P0ConnectionPool(
                          override val url: String,
                          override val user: String,
                          password: String,
                          override val settings: ConnectionPoolSettings = ConnectionPoolSettings())
  extends ConnectionPool(url, user, password, settings) {

  import com.mchange.v2.c3p0._
  private[this] val _dataSource = new ComboPooledDataSource
  _dataSource.setJdbcUrl(url)
  _dataSource.setUser(user)
  _dataSource.setPassword(password)
  _dataSource.setInitialPoolSize(settings.initialSize)
  _dataSource.setMaxPoolSize(settings.maxSize)
  _dataSource.setCheckoutTimeout(settings.connectionTimeoutMillis.toInt)
  _dataSource.setAcquireIncrement(10)
  _dataSource.setMaxIdleTime(30)
  _dataSource.setMinPoolSize(settings.initialSize)
  _dataSource.setAcquireRetryDelay(1000)
  _dataSource.setAcquireRetryAttempts(60)
  _dataSource.setBreakAfterAcquireFailure(false)
  _dataSource.setUnreturnedConnectionTimeout(25)
  _dataSource.setMaxIdleTimeExcessConnections(20)
  _dataSource.setMaxConnectionAge(20)
  _dataSource.setMaxStatements(0)


  override def dataSource: DataSource = _dataSource
  override def borrow(): Connection = dataSource.getConnection()
  override def numActive: Int = _dataSource.getNumBusyConnections(user, password)
  override def numIdle: Int = _dataSource.getNumIdleConnections(user, password)
  override def maxActive: Int = _dataSource.getMaxPoolSize
  override def maxIdle: Int = _dataSource.getMaxPoolSize
  override def close(): Unit = _dataSource.close()

}

/*implicit val factory = ConnectionPoolFactory
ConnectionPool.add('xxxx', url, user, password)*/
