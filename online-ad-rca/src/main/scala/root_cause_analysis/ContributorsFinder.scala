package root_cause_analysis

import models.{AnomalyEvent, RCAResult}
import org.apache.flink.streaming.api.scala.DataStream

trait ContributorsFinder extends Serializable {

  def runSearch(anomalyStream: DataStream[AnomalyEvent]): DataStream[RCAResult]
}
