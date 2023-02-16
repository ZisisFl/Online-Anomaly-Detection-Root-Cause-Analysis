package utils.evictors;

import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.streaming.api.windowing.evictors.Evictor;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.streaming.runtime.operators.windowing.TimestampedValue;

import java.util.Iterator;

@PublicEvolving
public class ElementEvictor<W extends Window> implements Evictor<Object, W> {
    private static final long serialVersionUID = 1L;
    private final long elementsToEvict;
    private final boolean doEvictAfter;

    private ElementEvictor(long elementsToEvict) {
        this.elementsToEvict = elementsToEvict;
        this.doEvictAfter = false;
    }

    private ElementEvictor(long elementsToEvict, boolean doEvictAfter) {
        this.elementsToEvict = elementsToEvict;
        this.doEvictAfter = doEvictAfter;
    }

    @Override
    public void evictBefore(
            Iterable<TimestampedValue<Object>> elements, int size, W window, EvictorContext ctx) {
        if (!doEvictAfter) {
            evict(elements);
        }
    }

    @Override
    public void evictAfter(
            Iterable<TimestampedValue<Object>> elements, int size, W window, EvictorContext ctx) {
        if (doEvictAfter) {
            evict(elements);
        }
    }

    private void evict(Iterable<TimestampedValue<Object>> elements) {
        int evictedCount = 0;
        for (Iterator<TimestampedValue<Object>> iterator = elements.iterator();
             iterator.hasNext(); ) {
            iterator.next();
            if (evictedCount == elementsToEvict) {
                break;
            } else {
                iterator.remove();
                evictedCount++;
            }
        }
    }

    public static <W extends Window> ElementEvictor<W> of(long elementsToEvict) {
        return new ElementEvictor<>(elementsToEvict);
    }

    /**
     * Creates a {@code CountEvictor} that keeps the given number of elements in the pane Eviction
     * is done before/after the window function based on the value of doEvictAfter.
     *
     * @param elementsToEvict The number of elements to keep in the pane.
     * @param doEvictAfter Whether to do eviction after the window function.
     */
    public static <W extends Window> ElementEvictor<W> of(long elementsToEvict, boolean doEvictAfter) {
        return new ElementEvictor<>(elementsToEvict, doEvictAfter);
    }
}

