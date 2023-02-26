package anomaly_detection.detectors

import aggregators.OffsetBaselineAggregator
import aggregators.metric_aggregators.SumAggregator
import config.AppConfig
import models.{AggregatedRecordsWBaseline, InputRecord}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.{GlobalWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.evictors.CountEvictor
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow
import org.scalatest.flatspec.AnyFlatSpec
import sources.kafka.InputRecordStreamBuilder

class TriggerAndEvictorsTest extends AnyFlatSpec{
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  "simulating countWindow functionality" should "work" in {
    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(env)

    val aggregatedRecordsWBaselineStream: DataStream[AggregatedRecordsWBaseline] = inputStream
      .assignAscendingTimestamps(_.epoch)
      .windowAll(TumblingEventTimeWindows.of(Time.seconds(30)))
      .aggregate(new SumAggregator)
      .windowAll(GlobalWindows.create())
      .evictor(CountEvictor.of[GlobalWindow](10))
      .trigger(CountTrigger.of[GlobalWindow](1))
      .aggregate(new OffsetBaselineAggregator)

    aggregatedRecordsWBaselineStream.print()

    //just for completeness how to use ElementEvictor and CountRecordTrigger
    //    val c_trigger = new CountRecordTrigger(10)
    //      .evictor(ElementEvictor.of[GlobalWindow](1, true))
    //      .trigger(new CountRecordTrigger(10))

    // creating a custom watermark
    //    val watermarkStrategy = WatermarkStrategy
    //      .forMonotonousTimestamps[InputRecord]()
    //      .withTimestampAssigner(new SerializableTimestampAssigner[InputRecord] {
    //        override def extractTimestamp(record: InputRecord, streamRecordTimestamp: Long): Long =
    //          record.epoch
    //      })
    // example with customer watermark and processAllWindowFunction
    //    val aggregatedRecordsStream: DataStream[AggregatedRecords] = inputStream
    //      .assignTimestampsAndWatermarks(watermarkStrategy)
    //      .windowAll(SlidingEventTimeWindows.of(Time.seconds(aggregationWindowSize), Time.seconds(aggregationWindowSlide)))
    //      .aggregate(new SumAggregator, new ProcessAllWindowFunction[AggregatedRecords, AggregatedRecords, TimeWindow] {
    //              override def process(context: Context, elements: Iterable[AggregatedRecords], out: Collector[AggregatedRecords]): Unit = {
    //                out.collect(elements.last)
    //              }
    //            }
    //    )
  }
}