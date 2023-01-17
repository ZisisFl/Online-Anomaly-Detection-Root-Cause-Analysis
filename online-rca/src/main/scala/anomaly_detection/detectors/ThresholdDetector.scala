package anomaly_detection.detectors

import anomaly_detection.AnomalyDetector
import models.InputRecord
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala.createTypeInformation
import sources.kafka.InputRecordStream

class ThresholdDetector extends AnomalyDetector[ThresholdDetectorSpec] {
  private var spec: ThresholdDetectorSpec = _
//  private var env: StreamExecutionEnvironment = _
  override def init(spec: ThresholdDetectorSpec): Unit = {
    this.spec = spec
//    this.env = env
  }

  override def runDetection(env: StreamExecutionEnvironment): Unit = {
    val inputStream: DataStream[InputRecord] = InputRecordStream.createInputRecordStream(
      "test1",
      env,
      1,
      "earliest"
    )

    inputStream
      .filter(record => valueTooLow(record.metric) || valueTooHigh(record.metric))
      .map(record => mapRecordToAnomaly(record))
      .print()

    env.execute()
  }

  private def valueTooHigh(value: Double): Boolean = {
    value > spec.max
  }

  private def valueTooLow(value: Double): Boolean = {
    value < spec.min
  }

  private def computeBaseline(): Unit = {

  }
}
