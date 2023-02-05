package anomaly_detection.detectors

import anomaly_detection.AbstractDetectorSpec

class ThresholdDetectorSpec extends AbstractDetectorSpec{
  private var _min: Double = 0.0f
  private var _max: Double = 0.0f

  def min:Double = _min

  def min_=(min_value: Double): ThresholdDetectorSpec = {
    _min=min_value
    this
  }

  def max: Double = _max

  def max_=(max_value: Double): ThresholdDetectorSpec = {
    _max=max_value
    this
  }
}
