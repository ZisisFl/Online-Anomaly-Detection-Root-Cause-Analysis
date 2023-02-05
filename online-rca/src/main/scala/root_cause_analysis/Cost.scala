package root_cause_analysis

object Cost {
  private final val MINIMUM_CONTRIBUTION_OF_INTEREST_PERCENTAGE = 3d

  /**
   * This is the implementation of the Balanced Simple cost function found in
   * thirdeye-plugins/thirdeye-contributors-simple/src/main/java/ai/startree/thirdeye/plugins/rca/contributors/simple/Cost.java
   * @param valueChangePercentage
   * @param contributionChangePercentage
   * @param contributionToOverallChangePercentage
   * @return
   */
  def compute(
               valueChangePercentage: Double,
               contributionChangePercentage: Double,
               contributionToOverallChangePercentage: Double
             ): Double = {

    if (Math.abs(contributionToOverallChangePercentage) < MINIMUM_CONTRIBUTION_OF_INTEREST_PERCENTAGE) 0d
    else Math.abs(contributionToOverallChangePercentage) + Math.abs(contributionChangePercentage)
  }
}
