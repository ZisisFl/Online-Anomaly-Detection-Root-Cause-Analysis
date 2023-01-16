package anomaly_detection.detectors

import anomaly_detection.AnomalyDetector

class ThresholdDetector extends AnomalyDetector[ThresholdDetectorSpec] {
  var spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

  override def runDetection(): Unit = {

  }
}
