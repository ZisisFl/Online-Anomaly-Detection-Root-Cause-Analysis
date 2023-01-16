package anomaly_detection

/**
 * Base class for detector specs
 */
trait AbstractDetectorSpec {
  private val DEFAULT_TIMESTAMP = "timestamp";
  private val DEFAULT_METRIC = "value";

  private var _timestamp: String = DEFAULT_TIMESTAMP
  private var _metric: String = DEFAULT_METRIC

  //https://docs.scala-lang.org/style/naming-conventions.html#accessorsmutators
  def timestamp(): String= _timestamp

  def timestamp_=(new_timestamp: String): Unit = {
    _timestamp=new_timestamp
  }

  def metric: String = _metric

  def metric_=(new_metric: String): Unit = {
    _metric=new_metric
  }

}
