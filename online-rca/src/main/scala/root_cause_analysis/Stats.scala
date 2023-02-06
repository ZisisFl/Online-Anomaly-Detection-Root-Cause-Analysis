package root_cause_analysis

object Stats {
  private final val EPSILON = 0.00001

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

  /**
   * Used for hierarchical contributors algorithm according to implementation found
   * thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/BalancedCostFunction.java
   * Contribution in an additive (AdditiveCubeNode RatioCubeNode)
   * @param baselineSize
   * @param currentSize
   * @param baselineTotalSize
   * @param currentTotalSize
   * @return
   */
  def computeContribution(
                           baselineSize: Double,
                           currentSize: Double,
                           baselineTotalSize: Double,
                           currentTotalSize: Double
                         ): Double = {

    val contribution = (baselineSize + currentSize) / (baselineTotalSize + currentTotalSize)

    if (Math.abs(0d - contribution) < EPSILON) 0d
    else roundUp(contribution)
  }

  private def roundUp(value: Double): Double = {
    Math.round(value * 10000d) / 100000d
  }
}
