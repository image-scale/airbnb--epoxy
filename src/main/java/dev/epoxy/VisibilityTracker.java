package dev.epoxy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VisibilityTracker {

    private final int viewportHeight;
    private final int viewportWidth;
    private Integer partialImpressionThreshold;
    private boolean onChangedEnabled = true;
    private final Map<Integer, VisibilityItem> trackedItems = new LinkedHashMap<>();

    public VisibilityTracker(int viewportHeight, int viewportWidth) {
        this.viewportHeight = viewportHeight;
        this.viewportWidth = viewportWidth;
    }

    public void setPartialImpressionThreshold(Integer threshold) {
        this.partialImpressionThreshold = threshold;
    }

    public Integer getPartialImpressionThreshold() {
        return partialImpressionThreshold;
    }

    public void setOnChangedEnabled(boolean enabled) {
        this.onChangedEnabled = enabled;
    }

    public boolean isOnChangedEnabled() {
        return onChangedEnabled;
    }

    @SuppressWarnings("unchecked")
    public void processVisibilityEvent(int adapterPosition, ItemModel<?> model, Object view,
                                       int height, int width,
                                       int visibleHeight, int visibleWidth) {
        VisibilityItem item = trackedItems.computeIfAbsent(adapterPosition, VisibilityItem::new);

        boolean measured = item.update(height, width, visibleHeight, visibleWidth,
                viewportHeight, viewportWidth);
        if (!measured) return;

        List<Integer> visibleEvents = item.handleVisible();
        List<Integer> focusEvents = item.handleFocus();
        List<Integer> fullEvents = item.handleFullImpression();

        for (int state : visibleEvents) {
            ((ItemModel<Object>) model).onVisibilityStateChanged(state, view);
        }
        for (int state : focusEvents) {
            ((ItemModel<Object>) model).onVisibilityStateChanged(state, view);
        }
        for (int state : fullEvents) {
            ((ItemModel<Object>) model).onVisibilityStateChanged(state, view);
        }

        if (partialImpressionThreshold != null) {
            List<Integer> partialEvents = item.handlePartialImpression(partialImpressionThreshold);
            for (int state : partialEvents) {
                ((ItemModel<Object>) model).onVisibilityStateChanged(state, view);
            }
        }

        boolean changed = item.handleChanged();
        if (changed && onChangedEnabled) {
            ((ItemModel<Object>) model).onVisibilityChanged(
                    item.getPercentVisibleHeight(),
                    item.getPercentVisibleWidth(),
                    item.getVisibleHeight(),
                    item.getVisibleWidth(),
                    view
            );
        }
    }

    public void onItemsInserted(int positionStart, int itemCount) {
        Map<Integer, VisibilityItem> updated = new LinkedHashMap<>();
        for (Map.Entry<Integer, VisibilityItem> entry : trackedItems.entrySet()) {
            int pos = entry.getKey();
            VisibilityItem vi = entry.getValue();
            if (pos >= positionStart) {
                vi.shiftBy(itemCount);
                updated.put(pos + itemCount, vi);
            } else {
                updated.put(pos, vi);
            }
        }
        trackedItems.clear();
        trackedItems.putAll(updated);
    }

    public void onItemsRemoved(int positionStart, int itemCount) {
        Map<Integer, VisibilityItem> updated = new LinkedHashMap<>();
        for (Map.Entry<Integer, VisibilityItem> entry : trackedItems.entrySet()) {
            int pos = entry.getKey();
            VisibilityItem vi = entry.getValue();
            if (pos >= positionStart && pos < positionStart + itemCount) {
                continue;
            }
            if (pos >= positionStart + itemCount) {
                vi.shiftBy(-itemCount);
                updated.put(pos - itemCount, vi);
            } else {
                updated.put(pos, vi);
            }
        }
        trackedItems.clear();
        trackedItems.putAll(updated);
    }

    public void clear() {
        trackedItems.clear();
    }

    public int getTrackedItemCount() {
        return trackedItems.size();
    }

    public VisibilityItem getTrackedItem(int adapterPosition) {
        return trackedItems.get(adapterPosition);
    }
}
