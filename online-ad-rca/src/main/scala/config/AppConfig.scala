package config

import com.typesafe.config._
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import scala.collection.JavaConverters.asScalaBufferConverter

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
  }

  object InputStream {
    private def constructDimensionHierarchiesMap():Map[String, String] = {
      AppConfig.InputStream.DIMENSION_NAMES
        .map(dim => (dim, DIMENSION_DEFINITIONS.getConfig(dim).getString("parent_dimension")))
        .groupBy(_._1)
        .mapValues(_.map(_._2).head)
    }

    /**
     * Given the DIMENSION_HIERARCHIES Create a map of type (dimension name -> level)
     * for each dimension. Root doesn't take part in the DIMENSION_HIERARCHIES but we consider
     * it to be level 0
     * @return
     */
    private def constructDimensionLevelsMap(): scala.collection.mutable.Map[String, Int] = {
      val dimensionLevels = scala.collection.mutable.Map[String, Int]()
      var notDefined = AppConfig.InputStream.DIMENSION_HIERARCHIES

      // this will loop at most, as many time as different levels exist
      while (notDefined.nonEmpty) {
        AppConfig.InputStream.DIMENSION_HIERARCHIES.foreach(x => {

          // if a dimension's parent is root then this dimension's level is 1
          if (x._2 == "root") {
            dimensionLevels(x._1) = 1
            notDefined = notDefined.-(x._1)
          }
          // if the level of the parent dimension is already know fetch it
          else if (dimensionLevels.isDefinedAt(x._2)) {
            dimensionLevels(x._1) = dimensionLevels(x._2) + 1
            notDefined = notDefined.-(x._1)
          }
          // otherwise move to next
        })
      }

      dimensionLevels
    }

    final val INPUT_TOPIC = conf.getString("input_stream.input_topic")
    final val TIMESTAMP_FIELD = conf.getString("input_stream.timestamp_field")
    final val VALUE_FIELD = conf.getString("input_stream.value_field")
    final val DIMENSION_NAMES = conf.getStringList("input_stream.dimensions.names").asScala.toList
    final val DIMENSION_DEFINITIONS = conf.getConfig("input_stream.dimensions.definitions")
    final val DIMENSION_HIERARCHIES = constructDimensionHierarchiesMap()
    final val DIMENSION_LEVELS = constructDimensionLevelsMap()
  }

  object AnomalyDetection {
    final val METHOD = conf.getString("anomaly_detection.method")
  }

  object RootCauseAnalysis {
    final val METHOD = conf.getString("root_cause_analysis.method")
    final val OUTPUT_TOPIC = "%s-out".format(InputStream.INPUT_TOPIC)//conf.getString("input_stream.output_topic")
    final val SUMMARY_SIZE = conf.getInt("root_cause_analysis.summary_size")
  }
}