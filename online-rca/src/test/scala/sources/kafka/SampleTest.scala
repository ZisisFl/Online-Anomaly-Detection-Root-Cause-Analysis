package sources.kafka

import config.AppConfig
import models.{Dimension, InputRecord}
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import sources.kafka.KafkaConsumer

object SampleTest {
  def main(args: Array[String]) {
    // Parse program arguments
    val parameters = ParameterTool.fromArgs(args)

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setGlobalJobParameters(parameters)
    AppConfig.enableCheckpoints(env)

    val fromTime = "earliest"

    val inputOrdersStream: DataStream[InputRecord]= {
      val kafkaConsumer = KafkaConsumer("test2", "flink_job6")

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
            timestamp = "ferfe",//record.get("sale_at").toString,
            value = 5.0f,//record.get("ws_ext_list_price").toString.toDouble,
            dimensions = Map(
              "ca_city" -> "yolo",//record.get("ca_city").toString,
              "ca_country"-> "yolo"//record.get("ca_country").toString
            )
          )
        })
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
