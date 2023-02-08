package anomaly_detection

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import models.{AggregatedRecordsWBaseline, AnomalyEvent}

// https://docs.scala-lang.org/overviews/scala-book/abstract-classes.html
trait AnomalyDetector[T] extends AbstractDetectorSpec {

  def init(spec: T): Unit

  def runDetection(env: StreamExecutionEnvironment): DataStream[AnomalyEvent]
}
