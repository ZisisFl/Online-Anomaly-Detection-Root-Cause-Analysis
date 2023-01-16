package anomaly_detection.detectors

import anomaly_detection.AbstractDetectorSpec

class ThresholdDetectorSpec extends AbstractDetectorSpec{
  private var _min_value: Double = 0.0f
  private var _max_value: Double = 0.0f

  def min_value = _min_value

  def min_value_=(min_value: Double): Unit = {
    _min_value=min_value
  }

  def max_value = _max_value

  def max_value_=(max_value: Double): Unit = {
    _max_value=max_value
  }
}
