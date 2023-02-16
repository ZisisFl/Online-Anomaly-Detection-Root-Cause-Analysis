package anomaly_detection

/**
 * Base class for detector specs
 */
trait AbstractDetectorSpec extends Serializable {
  private val DEFAULT_aggregationWindowSize: Int = 300 // 300 seconds 5 minutes
  private val DEFAULT_aggregationWindowSlide: Int = 60 // 60 seconds 1 minute
  private val DEFAULT_elementsInBaselineOffsetWindow: Int = 10 // one element sets the current and the rest the baseline

  private var _aggregationWindowSize = DEFAULT_aggregationWindowSize
  private var _aggregationWindowSlide = DEFAULT_aggregationWindowSlide
  private var _elementsInBaselineOffsetWindow = DEFAULT_elementsInBaselineOffsetWindow

  //https://docs.scala-lang.org/style/naming-conventions.html#accessorsmutators
  def aggregationWindowSize: Int = _aggregationWindowSize

  def aggregationWindowSize_=(newAggregationWindowSize: Int): AbstractDetectorSpec = {
    _aggregationWindowSize=newAggregationWindowSize
    this
  }

  def aggregationWindowSlide: Int = _aggregationWindowSlide

  def aggregationWindowSlide_=(newAggregationWindowSlide: Int): AbstractDetectorSpec = {
    _aggregationWindowSlide = newAggregationWindowSlide
    this
  }

  def elementsInBaselineOffsetWindow: Int = _elementsInBaselineOffsetWindow

  def elementsInBaselineOffsetWindow_=(newElementsInBaselineOffsetWindow: Int): AbstractDetectorSpec = {
    _elementsInBaselineOffsetWindow = newElementsInBaselineOffsetWindow
    this
  }
}