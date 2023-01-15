package config

import com.typesafe.config._
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object AppConfig {
  private val env = if (System.getenv("SCALA_ENV") == null) "dev" else System.getenv("SCALA_ENV")
  private val conf = ConfigFactory.load().getConfig(env)
  println(s"[CONFIG]:${conf}")

  def enableCheckpoints(env:StreamExecutionEnvironment): Unit={
    env.enableCheckpointing( 5 * 60000)

    // set mode to exactly-once (this is the default)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)

    // make sure 1000 ms of progress happen between checkpoints
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(5 * 60000)

    env.getCheckpointConfig.setCheckpointTimeout(4 * 60000)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    env.getCheckpointConfig.setTolerableCheckpointFailureNumber(500)
    env.getCheckpointConfig.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
  }

  object Kafka {
    final val BOOTSTRAP_SERVERS = conf.getString("kafka.bootstrap-servers")
    final val GROUP_ID = if (conf.hasPath("kafka.group-id")) conf.getString("kafka.group-id") else "flink-online-rca"
  }

  object Flink {
    final val HOST = conf.getString("flink.host")
    final val SESSIONWITHGAP = conf.getString("flink.session-with-gap")
  }
}
