package dev.epoxy;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class VisibilityTrackerTest {

    static class VisibilityTestModel extends ItemModel<Object> {
        final List<Integer> stateChanges = new ArrayList<>();
        final List<float[]> visibilityChanges = new ArrayList<>();

        VisibilityTestModel(long id) { id(id); }

        @Override
        public int getDefaultLayoutId() { return 0; }

        @Override
        public void onVisibilityStateChanged(int visibilityState, Object view) {
            stateChanges.add(visibilityState);
        }

        @Override
        public void onVisibilityChanged(float percentHeight, float percentWidth,
                                        int heightPx, int widthPx, Object view) {
            visibilityChanges.add(new float[]{percentHeight, percentWidth, heightPx, widthPx});
        }
    }

    // --- VisibilityState constants ---

    @Test
    public void visibilityStateDefinesVisible() {
        assertEquals(0, VisibilityState.VISIBLE);
    }

    @Test
    public void visibilityStateDefinesInvisible() {
        assertEquals(1, VisibilityState.INVISIBLE);
    }

    @Test
    public void visibilityStateDefinesFocusedVisible() {
        assertEquals(2, VisibilityState.FOCUSED_VISIBLE);
    }

    @Test
    public void visibilityStateDefinesUnfocusedVisible() {
        assertEquals(3, VisibilityState.UNFOCUSED_VISIBLE);
    }

    @Test
    public void visibilityStateDefinesFullImpressionVisible() {
        assertEquals(4, VisibilityState.FULL_IMPRESSION_VISIBLE);
    }

    @Test
    public void visibilityStateDefinesPartialImpressionVisible() {
        assertEquals(5, VisibilityState.PARTIAL_IMPRESSION_VISIBLE);
    }

    @Test
    public void visibilityStateDefinesPartialImpressionInvisible() {
        assertEquals(6, VisibilityState.PARTIAL_IMPRESSION_INVISIBLE);
    }

    // --- VisibilityItem.update ---

    @Test
    public void updateReturnsTrueWhenDimensionsPositive() {
        VisibilityItem item = new VisibilityItem(0);
        assertTrue(item.update(100, 50, 50, 50, 1000, 500));
    }

    @Test
    public void updateReturnsFalseWhenHeightZero() {
        VisibilityItem item = new VisibilityItem(0);
        assertFalse(item.update(0, 50, 0, 50, 1000, 500));
    }

    @Test
    public void updateReturnsFalseWhenWidthZero() {
        VisibilityItem item = new VisibilityItem(0);
        assertFalse(item.update(100, 0, 50, 0, 1000, 500));
    }

    // --- Percent calculations ---

    @Test
    public void calculatesPercentVisibleHeight() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(200, 100, 100, 100, 1000, 500);
        assertEquals(50f, item.getPercentVisibleHeight(), 0.01f);
    }

    @Test
    public void calculatesPercentVisibleWidth() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(200, 100, 200, 75, 1000, 500);
        assertEquals(75f, item.getPercentVisibleWidth(), 0.01f);
    }

    @Test
    public void percentReturnsZeroWhenDimensionIsZero() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(0, 0, 0, 0, 1000, 500);
        assertEquals(0f, item.getPercentVisibleHeight(), 0.01f);
        assertEquals(0f, item.getPercentVisibleWidth(), 0.01f);
    }

    // --- handleVisible ---

    @Test
    public void handleVisibleDispatchesVisibleWhenBecomingVisible() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 50, 50, 1000, 500);
        List<Integer> events = item.handleVisible();
        assertEquals(1, events.size());
        assertEquals(VisibilityState.VISIBLE, (int) events.get(0));
    }

    @Test
    public void handleVisibleDispatchesInvisibleWhenBecomingInvisible() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 50, 50, 1000, 500);
        item.handleVisible();

        item.update(100, 100, 0, 0, 1000, 500);
        List<Integer> events = item.handleVisible();
        assertEquals(1, events.size());
        assertEquals(VisibilityState.INVISIBLE, (int) events.get(0));
    }

    @Test
    public void handleVisibleNoEventWhenAlreadyVisible() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 50, 50, 1000, 500);
        item.handleVisible();

        item.update(100, 100, 60, 60, 1000, 500);
        List<Integer> events = item.handleVisible();
        assertTrue(events.isEmpty());
    }

    @Test
    public void handleVisibleNotVisibleWhenOnlyHeightVisible() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 50, 0, 1000, 500);
        List<Integer> events = item.handleVisible();
        assertTrue(events.isEmpty());
    }

    // --- handleFocus ---

    @Test
    public void handleFocusDispatchesFocusedWhenSmallItemFullyVisible() {
        VisibilityItem item = new VisibilityItem(0);
        // Small item (100x100) in large viewport (1000x500), fully visible
        item.update(100, 100, 100, 100, 1000, 500);
        List<Integer> events = item.handleFocus();
        assertEquals(1, events.size());
        assertEquals(VisibilityState.FOCUSED_VISIBLE, (int) events.get(0));
    }

    @Test
    public void handleFocusDispatchesUnfocusedWhenSmallItemPartiallyVisible() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 100, 100, 1000, 500);
        item.handleFocus();

        // Now only partially visible
        item.update(100, 100, 50, 100, 1000, 500);
        List<Integer> events = item.handleFocus();
        assertEquals(1, events.size());
        assertEquals(VisibilityState.UNFOCUSED_VISIBLE, (int) events.get(0));
    }

    @Test
    public void handleFocusLargeItemFocusedWhenOccupyingHalfViewport() {
        VisibilityItem item = new VisibilityItem(0);
        // Large item (800x500) viewport (1000x500), half viewport area = 250000
        // Visible: 600x500 = 300000 >= 250000
        item.update(800, 500, 600, 500, 1000, 500);
        List<Integer> events = item.handleFocus();
        assertEquals(1, events.size());
        assertEquals(VisibilityState.FOCUSED_VISIBLE, (int) events.get(0));
    }

    @Test
    public void handleFocusLargeItemNotFocusedWhenLessThanHalfViewport() {
        VisibilityItem item = new VisibilityItem(0);
        // Large item (800x500) viewport (1000x500), half viewport area = 250000
        // Visible: 400x500 = 200000 < 250000
        item.update(800, 500, 400, 500, 1000, 500);
        List<Integer> events = item.handleFocus();
        assertTrue(events.isEmpty());
    }

    // --- handleFullImpression ---

    @Test
    public void handleFullImpressionDispatchesWhenFullyVisible() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 100, 100, 1000, 500);
        List<Integer> events = item.handleFullImpression();
        assertEquals(1, events.size());
        assertEquals(VisibilityState.FULL_IMPRESSION_VISIBLE, (int) events.get(0));
    }

    @Test
    public void handleFullImpressionNoEventWhenPartiallyVisible() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 50, 100, 1000, 500);
        List<Integer> events = item.handleFullImpression();
        assertTrue(events.isEmpty());
    }

    @Test
    public void handleFullImpressionNoEventWhenLeavingFullVisibility() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 100, 100, 1000, 500);
        item.handleFullImpression();

        item.update(100, 100, 50, 100, 1000, 500);
        List<Integer> events = item.handleFullImpression();
        assertTrue(events.isEmpty());
    }

    // --- handlePartialImpression ---

    @Test
    public void handlePartialImpressionDispatchesWhenExceedingThreshold() {
        VisibilityItem item = new VisibilityItem(0);
        // 100x100 item, 60x100 visible = 60% area, threshold 50%
        item.update(100, 100, 60, 100, 1000, 500);
        List<Integer> events = item.handlePartialImpression(50);
        assertEquals(1, events.size());
        assertEquals(VisibilityState.PARTIAL_IMPRESSION_VISIBLE, (int) events.get(0));
    }

    @Test
    public void handlePartialImpressionDispatchesInvisibleWhenDroppingBelow() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 60, 100, 1000, 500);
        item.handlePartialImpression(50);

        item.update(100, 100, 30, 100, 1000, 500);
        List<Integer> events = item.handlePartialImpression(50);
        assertEquals(1, events.size());
        assertEquals(VisibilityState.PARTIAL_IMPRESSION_INVISIBLE, (int) events.get(0));
    }

    @Test
    public void isPartiallyVisibleWithThresholdZeroFallsBackToIsVisible() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 1, 1, 1000, 500);
        List<Integer> events = item.handlePartialImpression(0);
        assertEquals(1, events.size());
        assertEquals(VisibilityState.PARTIAL_IMPRESSION_VISIBLE, (int) events.get(0));
    }

    @Test
    public void partialImpressionThresholdZeroNotVisibleWhenNoPixels() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 0, 0, 1000, 500);
        List<Integer> events = item.handlePartialImpression(0);
        assertTrue(events.isEmpty());
    }

    // --- handleChanged deduplication ---

    @Test
    public void handleChangedReturnsTrueOnFirstCall() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 50, 50, 1000, 500);
        assertTrue(item.handleChanged());
    }

    @Test
    public void handleChangedReturnsFalseWhenUnchanged() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 50, 50, 1000, 500);
        item.handleChanged();

        item.update(100, 100, 50, 50, 1000, 500);
        assertFalse(item.handleChanged());
    }

    @Test
    public void handleChangedReturnsTrueWhenVisibleHeightChanges() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 50, 50, 1000, 500);
        item.handleChanged();

        item.update(100, 100, 60, 50, 1000, 500);
        assertTrue(item.handleChanged());
    }

    // --- reset ---

    @Test
    public void resetClearsTrackingState() {
        VisibilityItem item = new VisibilityItem(0);
        item.update(100, 100, 100, 100, 1000, 500);
        item.handleVisible();
        item.handleFocus();
        item.handleFullImpression();
        item.handleChanged();

        item.reset(5);
        assertEquals(5, item.getAdapterPosition());

        // After reset, becoming visible again should fire VISIBLE
        item.update(100, 100, 50, 50, 1000, 500);
        List<Integer> events = item.handleVisible();
        assertEquals(1, events.size());
        assertEquals(VisibilityState.VISIBLE, (int) events.get(0));

        // handleChanged should fire again
        assertTrue(item.handleChanged());
    }

    // --- shiftBy ---

    @Test
    public void shiftByAdjustsAdapterPosition() {
        VisibilityItem item = new VisibilityItem(3);
        assertEquals(3, item.getAdapterPosition());
        item.shiftBy(2);
        assertEquals(5, item.getAdapterPosition());
        item.shiftBy(-1);
        assertEquals(4, item.getAdapterPosition());
    }

    // --- VisibilityTracker ---

    @Test
    public void trackerDispatchesOnVisibilityStateChangedToModel() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(0, model, view, 100, 100, 50, 50);

        assertTrue(model.stateChanges.contains(VisibilityState.VISIBLE));
    }

    @Test
    public void trackerDispatchesOnVisibilityChangedToModel() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(0, model, view, 100, 100, 50, 50);

        assertEquals(1, model.visibilityChanges.size());
        float[] change = model.visibilityChanges.get(0);
        assertEquals(50f, change[0], 0.01f);
        assertEquals(50f, change[1], 0.01f);
        assertEquals(50f, change[2], 0.01f);
        assertEquals(50f, change[3], 0.01f);
    }

    @Test
    public void trackerOnChangedEnabledControlsVisibilityChangedDispatch() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        tracker.setOnChangedEnabled(false);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(0, model, view, 100, 100, 50, 50);

        assertTrue(model.stateChanges.contains(VisibilityState.VISIBLE));
        assertTrue(model.visibilityChanges.isEmpty());
    }

    @Test
    public void trackerPartialImpressionThresholdConfigurable() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        tracker.setPartialImpressionThreshold(50);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        // 70% visible area
        tracker.processVisibilityEvent(0, model, view, 100, 100, 70, 100);

        assertTrue(model.stateChanges.contains(VisibilityState.PARTIAL_IMPRESSION_VISIBLE));
    }

    @Test
    public void trackerNoPartialImpressionWithoutThreshold() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(0, model, view, 100, 100, 70, 100);

        assertFalse(model.stateChanges.contains(VisibilityState.PARTIAL_IMPRESSION_VISIBLE));
    }

    @Test
    public void trackerClearRemovesAllTrackedItems() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(0, model, view, 100, 100, 50, 50);
        assertEquals(1, tracker.getTrackedItemCount());

        tracker.clear();
        assertEquals(0, tracker.getTrackedItemCount());
    }

    @Test
    public void trackerShiftsPositionsOnInsert() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(2, model, view, 100, 100, 50, 50);
        assertNotNull(tracker.getTrackedItem(2));

        tracker.onItemsInserted(1, 3);
        assertNull(tracker.getTrackedItem(2));
        assertNotNull(tracker.getTrackedItem(5));
    }

    @Test
    public void trackerShiftsPositionsOnRemove() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(5, model, view, 100, 100, 50, 50);
        assertNotNull(tracker.getTrackedItem(5));

        tracker.onItemsRemoved(1, 2);
        assertNull(tracker.getTrackedItem(5));
        assertNotNull(tracker.getTrackedItem(3));
    }

    @Test
    public void trackerRemovesTrackedItemsInRemovedRange() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(2, model, view, 100, 100, 50, 50);
        tracker.onItemsRemoved(1, 3);
        assertEquals(0, tracker.getTrackedItemCount());
    }

    // --- Listener interfaces ---

    @Test
    public void onModelVisibilityStateChangedListenerReceivesEvents() {
        List<Integer> received = new ArrayList<>();
        OnModelVisibilityStateChangedListener<VisibilityTestModel, Object> listener =
                (model, view, state) -> received.add(state);

        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();
        listener.onVisibilityStateChanged(model, view, VisibilityState.VISIBLE);
        listener.onVisibilityStateChanged(model, view, VisibilityState.FOCUSED_VISIBLE);

        assertEquals(List.of(VisibilityState.VISIBLE, VisibilityState.FOCUSED_VISIBLE), received);
    }

    @Test
    public void onModelVisibilityChangedListenerReceivesEvents() {
        List<float[]> received = new ArrayList<>();
        OnModelVisibilityChangedListener<VisibilityTestModel, Object> listener =
                (model, view, ph, pw, hv, wv) -> received.add(new float[]{ph, pw, hv, wv});

        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();
        listener.onVisibilityChanged(model, view, 50f, 75f, 100, 150);

        assertEquals(1, received.size());
        assertEquals(50f, received.get(0)[0], 0.01f);
        assertEquals(75f, received.get(0)[1], 0.01f);
    }

    // --- ItemModel callbacks through tracker ---

    @Test
    public void itemModelVisibilityMethodsCalledDuringProcessing() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        // Fully visible small item
        tracker.processVisibilityEvent(0, model, view, 100, 100, 100, 100);

        assertTrue(model.stateChanges.contains(VisibilityState.VISIBLE));
        assertTrue(model.stateChanges.contains(VisibilityState.FOCUSED_VISIBLE));
        assertTrue(model.stateChanges.contains(VisibilityState.FULL_IMPRESSION_VISIBLE));

        assertEquals(1, model.visibilityChanges.size());
        float[] change = model.visibilityChanges.get(0);
        assertEquals(100f, change[0], 0.01f);
        assertEquals(100f, change[1], 0.01f);
    }

    @Test
    public void trackerHandlesTransitionToInvisible() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(0, model, view, 100, 100, 50, 50);
        model.stateChanges.clear();
        model.visibilityChanges.clear();

        tracker.processVisibilityEvent(0, model, view, 100, 100, 0, 0);
        assertTrue(model.stateChanges.contains(VisibilityState.INVISIBLE));
    }

    @Test
    public void trackerMultipleItemsTrackedIndependently() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model1 = new VisibilityTestModel(1);
        VisibilityTestModel model2 = new VisibilityTestModel(2);
        Object view = new Object();

        tracker.processVisibilityEvent(0, model1, view, 100, 100, 100, 100);
        tracker.processVisibilityEvent(1, model2, view, 100, 100, 30, 100);

        assertTrue(model1.stateChanges.contains(VisibilityState.FULL_IMPRESSION_VISIBLE));
        assertFalse(model2.stateChanges.contains(VisibilityState.FULL_IMPRESSION_VISIBLE));
        assertEquals(2, tracker.getTrackedItemCount());
    }

    @Test
    public void trackerDeduplicatesOnVisibilityChanged() {
        VisibilityTracker tracker = new VisibilityTracker(1000, 500);
        VisibilityTestModel model = new VisibilityTestModel(1);
        Object view = new Object();

        tracker.processVisibilityEvent(0, model, view, 100, 100, 50, 50);
        tracker.processVisibilityEvent(0, model, view, 100, 100, 50, 50);

        assertEquals(1, model.visibilityChanges.size());
    }
}
