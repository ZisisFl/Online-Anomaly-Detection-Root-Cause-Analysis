package sources.kafka

import config.AppConfig
import models.{Dimension, InputRecord}
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sources.kafka.KafkaConsumer

class SampleInputTest extends AnyFlatSpec with Matchers  {
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  "everything" should "work" in {
    val fromTime = "earliest"

    val inputOrdersStream: DataStream[InputRecord] = {
      val kafkaConsumer = KafkaConsumer("test1", "flink_job")

      val kafkaConsumerWithOffset = fromTime.toLowerCase match {
        case "earliest" => kafkaConsumer.setStartFromEarliest()
        case "latest" => kafkaConsumer.setStartFromLatest()
        case t => kafkaConsumer.setStartFromTimestamp(t.toLong)
      }
      env.addSource(kafkaConsumerWithOffset)
        .setParallelism(1)
        .map(record =>
          InputRecord(
            id = "dwedw",
            timestamp = record.get("value").get("sale_at").textValue(),
            value = record.get("value").get("ws_ext_list_price").doubleValue(),
            dimensions = Map(
              "ca_city" -> record.get("value").get("ca_city").textValue(),
              "ca_country" -> record.get("value").get("ca_country").textValue()
            )
          )
        )
    }
    inputOrdersStream
      .print()

    //    println(inputOrdersStream.executeAndCollect(5))

    //    val morgan_town_count = inputOrdersStream
    //      .setParallelism(1)
    //      .map(record => record.dimensions.getOrElse("ca_city", "yo"))
    //      .filter(city => city=="Morgantown")
    //      .map((_,1))
    //      .keyBy(_._1)
    //      .window(TumblingEventTimeWindows.of(Time.seconds(5)))
    //      .sum(1)
    //
    //    println(morgan_town_count)

    env.execute()
  }
}
