package config

import com.typesafe.config._

object AppConfig {
  private val env = if (System.getenv("SCALA_ENV") == null) "dev" else System.getenv("SCALA_ENV")
  private val conf = ConfigFactory.load().getConfig(env)
  println(s"[CONFIG]:${conf}")

  object Kafka {
    final val BOOTSTRAP_SERVERS = conf.getString("kafka.bootstrap-servers")
    final val GROUP_ID = if (conf.hasPath("kafka.group-id")) conf.getString("kafka.group-id") else "flink-online-rca"
  }

  object Flink {
    final val HOST = conf.getString("flink.host")
    final val SESSIONWITHGAP = conf.getString("flink.session-with-gap")
  }
}
