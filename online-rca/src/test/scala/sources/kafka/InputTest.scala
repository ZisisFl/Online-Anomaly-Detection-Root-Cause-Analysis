package sources.kafka

import models.{Dimension, InputRecord, SaleRecord}
import config.AppConfig
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.{Configuration, MemorySize, TaskManagerOptions}
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class InputTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  val config = new Configuration()
  config.set(TaskManagerOptions.NETWORK_MEMORY_MAX,new MemorySize(256*1000000))
  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(16)
    .setNumberTaskManagers(1)
    .setConfiguration(config)
    .build)

  before {
    flinkCluster.before()
  }

  after {
    flinkCluster.after()
  }

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  // CONFIGURATION START
  val fromTime = "earliest"
  // CONFIGURATION END

  "everything" should "work" in {
    val inputStream: DataStream[InputRecord]= {
      val kafkaConsumer = KafkaConsumer("test1", "flink_job")

      val kafkaConsumerWithOffset = fromTime.toLowerCase match {
        case "earliest" => kafkaConsumer.setStartFromEarliest()
        case "latest" => kafkaConsumer.setStartFromLatest()
        case t => kafkaConsumer.setStartFromTimestamp(t.toLong)
      }
      env.addSource(kafkaConsumerWithOffset)
        .setParallelism(1)
        .map({
          record => InputRecord(
            id = "yo",
            timestamp = record.get("sale_at").toString,
            value = record.get("ws_ext_list_price").toString.toDouble,
            dimensions = Map(
              "ca_city" -> record.get("ca_city").toString,
              "ca_country"-> record.get("ca_country").toString
            )
          )
        })
    }
    inputStream
      .print()

    env.execute("Main Job")
  }
}
