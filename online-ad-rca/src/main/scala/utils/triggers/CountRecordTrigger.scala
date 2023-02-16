package utils.triggers

import models.AggregatedRecords
import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow

class CountRecordTrigger(size: Int) extends Trigger[AggregatedRecords, GlobalWindow] {
  private var count = 0

  override def onElement(element: AggregatedRecords, timestamp: Long, window: GlobalWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    count +=1

    if (count == size) {
      count = 0
      TriggerResult.FIRE_AND_PURGE
    }
    else {
      TriggerResult.CONTINUE
    }
  }

  override def onProcessingTime(time: Long, window: GlobalWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    TriggerResult.CONTINUE
  }

  override def onEventTime(time: Long, window: GlobalWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    TriggerResult.CONTINUE
  }

  override def clear(window: GlobalWindow, ctx: Trigger.TriggerContext): Unit = {
    count = 0
  }
}
