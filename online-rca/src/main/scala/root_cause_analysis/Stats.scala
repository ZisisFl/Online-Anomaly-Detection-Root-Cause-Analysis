package root_cause_analysis

/**
 * Methods implementations are in SummaryResponse in old ThirdEye and in Stats in startree ThirdEye
 * thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/summary/SummaryResponse.java
 * thirdeye-spi/src/main/java/ai/startree/thirdeye/spi/rca/Stats.java
 */
class Stats(
             baselineValue: Double,
             currentValue: Double,
             baselineTotal: Double,
             currentTotal: Double) {

  val valueChangePercentage = computeValueChangePercentage(
    baselineValue,
    currentValue
  )

  val contributionChangePercentage = computeContributionChangePercentage(
    baselineValue,
    currentValue,
    baselineTotal,
    currentTotal
  )

  val contributionToOverallChangePercentage = computeContributionToOverallChangePercentage(
    baselineValue,
    currentValue,
    baselineTotal,
    currentTotal
  )
  private def computeValueChangePercentage(baseline: Double, current: Double): Double = {
    if (baseline != 0d) {
      val percentageChange = ((current - baseline) / baseline) * 100d
      roundUp(percentageChange)
    }
    else {
      Double.NaN
    }
  }

  private def computeContributionChangePercentage(
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

  private def computeContributionToOverallChangePercentage(
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

  private def roundUp(value: Double): Double = {
    Math.round(value * 10000d) / 100000d
  }
}
