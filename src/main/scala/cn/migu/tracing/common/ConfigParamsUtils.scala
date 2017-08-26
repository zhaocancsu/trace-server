package cn.migu.tracing.common

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config

import scala.util.Try

/**
  * 配置读取类
  *
  * author  zhaocan
  * version  [版本号, 2017/5/3]
  * see  [相关类/方法]
  * since  [产品/模块版本]
  */
object ConfigParamsUtils {
  def getRequestTimeout(config: Config): Int = {
    Try(config.getDuration("tracing.web.request-timeout",
      TimeUnit.MILLISECONDS).toInt / 1000).getOrElse(15)
  }
}
