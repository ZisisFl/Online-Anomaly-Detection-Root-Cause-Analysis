package root_cause_analysis

object HierarchicalContributorsCost {
// DimNameValueCostEntry
  // the changeRatio between baseline and current value of parent node.

  def compute(baselineValue: Double, currentValue: Double, parentRatio: Double, contribution: Double): Double = {
    val checkedParentRation = parentRatio match {
      case 0 => 1d
      case _ => parentRatio
    }

    if (baselineValue != 0 && currentValue != 0) {
      error(baselineValue, currentValue, checkedParentRation, contribution)
    }
    else if (baselineValue == 0) {
      errorWithEmptyBaseline(currentValue, checkedParentRation)
    }
    else if (currentValue == 0) {
      errorWithEmptyCurrent(baselineValue, checkedParentRation)
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
}
