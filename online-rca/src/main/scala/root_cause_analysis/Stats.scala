package root_cause_analysis

class Stats {

  def computeValueChangePercentage(baseline: Double, current: Double): Double = {
    if (baseline != 0d) {
      val percentageChange = ((current - baseline) / baseline) * 100d
      roundUp(percentageChange)
    }
    else {
      Double.NaN
    }
  }

  def computeContributionChangePercentage(
                                           baseline: Double,
                                           current: Double,
                                           baselineTotal: Double,
                                           currentTotal: Double): Double = {

    if (currentTotal != 0d && baselineTotal != 0d) {
      val contributionChange = ((current / currentTotal) - (baseline / baselineTotal)) * 100d
      roundUp(contributionChange)
    }
    else {
      Double.NaN
    }
  }

  def computeContributionToOverallChangePercentage(
                                                    baseline: Double,
                                                    current: Double,
                                                    baselineTotal: Double,
                                                    currentTotal: Double
                                                  ): Double = {
    if (baselineTotal != 0d) {
      val contributionToOverallChange = ((current - baseline) / Math.abs(currentTotal - baselineTotal)) * 100d
      roundUp(contributionToOverallChange)
    }
    else {
      Double.NaN
    }
  }

  def roundUp(value: Double): Double = {
    Math.round(value * 10000d) / 100000d
  }

}
