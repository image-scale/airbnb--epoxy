package dev.epoxy;

import java.util.ArrayList;
import java.util.List;

public class VisibilityItem {

    private int adapterPosition = -1;
    private int height;
    private int width;
    private int visibleHeight;
    private int visibleWidth;
    private int viewportHeight;
    private int viewportWidth;

    private boolean visible;
    private boolean focusedVisible;
    private boolean fullyVisible;
    private boolean partiallyVisible;

    private int lastVisibleHeight = -1;
    private int lastVisibleWidth = -1;

    public VisibilityItem() {}

    public VisibilityItem(int adapterPosition) {
        reset(adapterPosition);
    }

    public boolean update(int height, int width, int visibleHeight, int visibleWidth,
                          int viewportHeight, int viewportWidth) {
        this.height = height;
        this.width = width;
        this.visibleHeight = visibleHeight;
        this.visibleWidth = visibleWidth;
        this.viewportHeight = viewportHeight;
        this.viewportWidth = viewportWidth;
        return height > 0 && width > 0;
    }

    public void reset(int newAdapterPosition) {
        this.adapterPosition = newAdapterPosition;
        this.visible = false;
        this.focusedVisible = false;
        this.fullyVisible = false;
        this.partiallyVisible = false;
        this.lastVisibleHeight = -1;
        this.lastVisibleWidth = -1;
    }

    public List<Integer> handleVisible() {
        List<Integer> events = new ArrayList<>();
        boolean previousVisible = visible;
        visible = isVisible();
        if (visible != previousVisible) {
            events.add(visible ? VisibilityState.VISIBLE : VisibilityState.INVISIBLE);
        }
        return events;
    }

    public List<Integer> handleFocus() {
        List<Integer> events = new ArrayList<>();
        boolean previousFocused = focusedVisible;
        focusedVisible = isInFocusVisible();
        if (focusedVisible != previousFocused) {
            events.add(focusedVisible ? VisibilityState.FOCUSED_VISIBLE
                    : VisibilityState.UNFOCUSED_VISIBLE);
        }
        return events;
    }

    public List<Integer> handleFullImpression() {
        List<Integer> events = new ArrayList<>();
        boolean previousFully = fullyVisible;
        fullyVisible = isFullyVisible();
        if (fullyVisible != previousFully) {
            if (fullyVisible) {
                events.add(VisibilityState.FULL_IMPRESSION_VISIBLE);
            }
        }
        return events;
    }

    public List<Integer> handlePartialImpression(int thresholdPercentage) {
        List<Integer> events = new ArrayList<>();
        boolean previousPartially = partiallyVisible;
        partiallyVisible = isPartiallyVisible(thresholdPercentage);
        if (partiallyVisible != previousPartially) {
            events.add(partiallyVisible ? VisibilityState.PARTIAL_IMPRESSION_VISIBLE
                    : VisibilityState.PARTIAL_IMPRESSION_INVISIBLE);
        }
        return events;
    }

    public boolean handleChanged() {
        if (visibleHeight != lastVisibleHeight || visibleWidth != lastVisibleWidth) {
            lastVisibleHeight = visibleHeight;
            lastVisibleWidth = visibleWidth;
            return true;
        }
        return false;
    }

    public float getPercentVisibleHeight() {
        if (height == 0) return 0f;
        return 100f / height * visibleHeight;
    }

    public float getPercentVisibleWidth() {
        if (width == 0) return 0f;
        return 100f / width * visibleWidth;
    }

    public int getVisibleHeight() {
        return visibleHeight;
    }

    public int getVisibleWidth() {
        return visibleWidth;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public void shiftBy(int offset) {
        adapterPosition += offset;
    }

    private boolean isVisible() {
        return visibleHeight > 0 && visibleWidth > 0;
    }

    private boolean isFullyVisible() {
        return visibleHeight == height && visibleWidth == width
                && visibleHeight > 0 && visibleWidth > 0;
    }

    private boolean isInFocusVisible() {
        if (!isVisible()) return false;
        long halfViewportArea = (long) viewportHeight * viewportWidth / 2;
        long totalArea = (long) height * width;
        long visibleArea = (long) visibleHeight * visibleWidth;
        if (totalArea >= halfViewportArea) {
            return visibleArea >= halfViewportArea;
        } else {
            return totalArea == visibleArea;
        }
    }

    private boolean isPartiallyVisible(int thresholdPercentage) {
        if (thresholdPercentage == 0) return isVisible();
        if (!isVisible()) return false;
        long totalArea = (long) height * width;
        long visibleArea = (long) visibleHeight * visibleWidth;
        float visiblePercentage = visibleArea / (float) totalArea * 100;
        return visiblePercentage >= thresholdPercentage;
    }
}
