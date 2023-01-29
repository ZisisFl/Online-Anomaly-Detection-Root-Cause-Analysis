package anomaly_detection

/**
 * Base class for detector specs
 */
trait AbstractDetectorSpec extends Serializable {
  private val DEFAULT_TIMESTAMP = "timestamp";
  private val DEFAULT_METRIC = "value";

  private var _timestamp: String = DEFAULT_TIMESTAMP
  private var _metric: String = DEFAULT_METRIC

  //https://docs.scala-lang.org/style/naming-conventions.html#accessorsmutators
  def timestamp: String= _timestamp

  def timestamp_=(new_timestamp: String): AbstractDetectorSpec = {
    _timestamp=new_timestamp
    this
  }

  def metric: String = _metric

  def metric_=(new_metric: String): AbstractDetectorSpec = {
    _metric=new_metric
    this
  }
}
