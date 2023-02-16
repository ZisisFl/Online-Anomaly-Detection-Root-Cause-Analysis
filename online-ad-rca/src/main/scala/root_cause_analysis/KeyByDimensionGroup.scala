package root_cause_analysis

import models.{AggregatedRecordsWBaseline, AnomalyEvent}
import utils.Types.DimensionGroup

object KeyByDimensionGroup extends Serializable {
  /**
   * Splits a AnomalyEvent record by dimensionGroup to produce a Seq of (DimensionGroup, AnomalyEvent)
   * @param record
   * @return
   */
  def keyByDimensionGroup(record: AnomalyEvent): Seq[(DimensionGroup, AnomalyEvent)] = {
    // get all dimension groups present in this record
    val dimensionGroups = record.aggregatedRecordsWBaseline.current_dimensions_breakdown.keySet.map(_.group) ++ record.aggregatedRecordsWBaseline.baseline_dimensions_breakdown.keySet.map(_.group)

    // create a AggregatedRecordsWBaseline record for each dimension group
    dimensionGroups.map(group => {
      val groupCurrentDimensionsBreakdown = record.aggregatedRecordsWBaseline.current_dimensions_breakdown.filterKeys(_.group == group)
      val groupBaselineDimensionsBreakdown = record.aggregatedRecordsWBaseline.baseline_dimensions_breakdown.filterKeys(_.group == group)
      val groupDimensionsHierarchies = record.aggregatedRecordsWBaseline.dimensions_hierarchy.filterKeys(_.group == group)

      (group,
        AnomalyEvent(
          record.anomalyId,
          record.detectedAt,
          record.epoch,
          AggregatedRecordsWBaseline(
            record.aggregatedRecordsWBaseline.current,
            record.aggregatedRecordsWBaseline.baseline,
            groupCurrentDimensionsBreakdown,
            groupBaselineDimensionsBreakdown,
            groupDimensionsHierarchies,
            record.aggregatedRecordsWBaseline.records_in_baseline_offset
          )
        )
      )
    }).toSeq
  }
}