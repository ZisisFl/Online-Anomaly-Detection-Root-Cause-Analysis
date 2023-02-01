package models

case class RCACandidate(
                    label: Boolean,
                    current: Double,
                    baseline: Double,
                    current_dimensions_breakdown: Map[Dimension, Double],
                    baseline_dimensions_breakdown: Map[Dimension, Double]
                  )
