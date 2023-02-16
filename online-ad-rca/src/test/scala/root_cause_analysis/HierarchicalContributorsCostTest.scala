package root_cause_analysis

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HierarchicalContributorsCostTest extends AnyFlatSpec with Matchers {

  "errorWithEmptyBaseline function" should "result to infinity" in {
    val currentValue = 5d
    val parentRatio = 0d

    val cost = HierarchicalContributorsCost.errorWithEmptyBaseline(currentValue, parentRatio)
    print(cost)
  }

  "errorWithEmptyBaseline function" should "result to 0" in {
    val currentValue = 5d
    val parentRatio = 1d

    val cost = HierarchicalContributorsCost.errorWithEmptyBaseline(currentValue, parentRatio)
    print(cost)
  }
}
