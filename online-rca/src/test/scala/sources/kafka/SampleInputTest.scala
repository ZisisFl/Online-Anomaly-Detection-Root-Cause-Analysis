package sources.kafka

import config.AppConfig
import models.{Dimension, InputRecord}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SampleInputTest extends AnyFlatSpec with Matchers  {
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  "input stream" should "be created with InputRecordStream.createInputRecordStream method" in {
    val inputStream: DataStream[InputRecord] = InputRecordStream.createInputRecordStream(
      "test1",
      env,
      1,
      "earliest"
    )

    inputStream.print()

    env.execute()
  }

  "town count" should "work" in {
    val inputStream: DataStream[InputRecord] = InputRecordStream.createInputRecordStream(
      "test1",
      env,
      1,
      "earliest"
    )

    val morgan_town_count = inputStream
      .map(record => record.dimensions.getOrElse("ca_city", "default_town"))
      .filter(city => city=="Morgantown")
      .map((_,1))
//      .keyBy(_._1)
//      .window(TumblingEventTimeWindows.of(Time.seconds(5)))
//      .sum(1)

    morgan_town_count.print()

    env.execute()
  }

  "manual input stream" should "work" in {
    val fromTime = "earliest"

    val inputOrdersStream: DataStream[InputRecord] = {
      val kafkaConsumer = GenericJsonKafkaConsumer("test1", "flink_job")

      val kafkaConsumerWithOffset = fromTime.toLowerCase match {
        case "earliest" => kafkaConsumer.setStartFromEarliest()
        case "latest" => kafkaConsumer.setStartFromLatest()
        case t => kafkaConsumer.setStartFromTimestamp(t.toLong)
      }
      env.addSource(kafkaConsumerWithOffset)
        .setParallelism(1)
        .map(record =>
          InputRecord(
            timestamp = record.get("value").get("sale_at").textValue(),
            value = record.get("value").get("ws_ext_list_price").doubleValue(),
            dimensions = Map(
              "ca_city" -> Dimension("ca_city", record.get("value").get("ca_city").textValue()),
              "ca_country" -> Dimension("ca_country", record.get("value").get("ca_country").textValue())
            )
          )
        )
    }

    inputOrdersStream
      .print()

    env.execute()
  }
}
