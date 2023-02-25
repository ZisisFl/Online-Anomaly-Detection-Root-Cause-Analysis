package root_cause_analysis

object HierarchicalContributorsCost {
  private final val EPSILON = 0.00001

  def compute(baselineValue: Double, currentValue: Double, parentRatio: Double, contribution: Double): Double = {
    /**
     * According to the implementation of fillEmptyValuesAndGetError in
     * thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/ChangeRatioCostFunction.java
     * In case that parentRatio is NaN or 0 set parentRatio to 1
     */
    val checkedParentRatio = if (parentRatio == 0 || parentRatio.isNaN) {
      1d
    }
    else {
      parentRatio
    }

    if (baselineValue != 0 && currentValue != 0) {
      error(baselineValue, currentValue, checkedParentRatio, contribution)
    }
    else if (baselineValue == 0) {
      errorWithEmptyBaseline(currentValue, checkedParentRatio)
    }
    else if (currentValue == 0) {
      errorWithEmptyCurrent(baselineValue, checkedParentRatio)
    }
    else 0 // baselineValue and currentValue are zeros. Set cost to zero so the node will be naturally aggregated to its parent
  }
  /**
   * Similar to error function implementation found in
   * thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/BalancedCostFunction.java
   * @param baselineValue
   * @param currentValue
   * @param parentRatio
   * @param contribution
   * @return cost
   */
  private def error(baselineValue: Double, currentValue: Double, parentRatio: Double, contribution: Double): Double = {
    val expectedBaselineValue = parentRatio * baselineValue
    val expectedRatio = currentValue / expectedBaselineValue
    val weightedExpectedRatio = (expectedRatio - 1) * contribution + 1
    val logExpRatio = Math.log(weightedExpectedRatio)
    (currentValue - expectedBaselineValue) * logExpRatio
  }

  /**
   * Similar to errorWithEmptyBaseline function implementation found in
   * thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/BalancedCostFunction.java
   * @param currentValue
   * @param parentRatio
   * @return cost
   */
  def errorWithEmptyBaseline(currentValue: Double, parentRatio: Double): Double = {
    val logExpRatio = Math.log(parentRatio match {
      case x if x > 1 => 2 - parentRatio
      case _ => parentRatio
    })
    currentValue * logExpRatio
  }

  /**
   * Similar to errorWithEmptyCurrent function implementation found in
   * thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/BalancedCostFunction.java
   * @param baselineValue
   * @param parentRatio
   * @return cost
   */
  def errorWithEmptyCurrent(baselineValue: Double, parentRatio: Double): Double = {
    val logExpRatio = Math.log(parentRatio match {
      case x if x < 1 => 2 - parentRatio
      case _ => parentRatio
    })
    -baselineValue * logExpRatio
  }

  /**
   * Used for hierarchical contributors algorithm according to implementation found
   * thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/BalancedCostFunction.java
   * Contribution in an additive (AdditiveCubeNode RatioCubeNode)
   *
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
    else contribution
  }

  def computeChangeRatio(baseline: Double, current: Double): Double = {
    if (baseline != 0d) {
      current / baseline
    }
    else {
      Double.NaN
    }
  }
}
