package root_cause_analysis

class Stats {

  def computeValueChangePercentage(baseline: Double, current: Double): Unit = {
    if (baseline != 0d) {
      val percentageChange = ((current - baseline) / baseline) * 100d
      roundUp(percentageChange)
    }
    else {
      Double.NaN
    }
  }

  def roundUp(value: Double): Double = {
    Math.round(value * 10000d) / 100000d
  }

}
