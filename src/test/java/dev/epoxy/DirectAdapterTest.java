package dev.epoxy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DirectAdapterTest {

    @Before
    public void setUp() {
        ViewTypeRegistry.resetForTesting();
    }

    @After
    public void tearDown() {
        ViewTypeRegistry.resetForTesting();
    }

    @Test
    public void testAddModel() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        TestItem item = new TestItem(1);
        adapter.addModel(item);

        assertEquals(1, adapter.getItemCount());
        assertEquals(1, observer.events.size());
        assertEquals("INSERT(0,1)", observer.events.get(0));
    }

    @Test
    public void testAddMultipleModels() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        adapter.addModel(new TestItem(1));
        adapter.addModel(new TestItem(2));
        adapter.addModel(new TestItem(3));

        assertEquals(3, adapter.getItemCount());
        assertEquals(3, observer.events.size());
    }

    @Test
    public void testAddModelsVarargs() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        adapter.addModels(new TestItem(1), new TestItem(2), new TestItem(3));

        assertEquals(3, adapter.getItemCount());
        assertEquals(1, observer.events.size());
        assertEquals("INSERT(0,3)", observer.events.get(0));
    }

    @Test
    public void testRemoveModel() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        TestItem item = new TestItem(1);
        adapter.addModel(item);
        observer.events.clear();

        adapter.removeModel(item);

        assertEquals(0, adapter.getItemCount());
        assertEquals(1, observer.events.size());
        assertEquals("REMOVE(0,1)", observer.events.get(0));
    }

    @Test
    public void testRemoveAllModels() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        adapter.addModel(new TestItem(1));
        adapter.addModel(new TestItem(2));
        adapter.addModel(new TestItem(3));
        observer.events.clear();

        adapter.removeAllModels();

        assertEquals(0, adapter.getItemCount());
        assertEquals(1, observer.events.size());
        assertEquals("REMOVE(0,3)", observer.events.get(0));
    }

    @Test
    public void testRemoveAllModelsEmpty() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        adapter.removeAllModels();
        assertEquals(0, observer.events.size());
    }

    @Test
    public void testInsertModelBefore() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        TestItem item1 = new TestItem(1);
        TestItem item2 = new TestItem(2);
        adapter.addModel(item1);
        adapter.addModel(item2);
        observer.events.clear();

        TestItem item3 = new TestItem(3);
        adapter.insertModelBefore(item3, item2);

        assertEquals(3, adapter.getItemCount());
        assertSame(item1, adapter.getCurrentModels().get(0));
        assertSame(item3, adapter.getCurrentModels().get(1));
        assertSame(item2, adapter.getCurrentModels().get(2));
        assertEquals("INSERT(1,1)", observer.events.get(0));
    }

    @Test
    public void testInsertModelAfter() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        TestItem item1 = new TestItem(1);
        TestItem item2 = new TestItem(2);
        adapter.addModel(item1);
        adapter.addModel(item2);
        observer.events.clear();

        TestItem item3 = new TestItem(3);
        adapter.insertModelAfter(item3, item1);

        assertEquals(3, adapter.getItemCount());
        assertSame(item1, adapter.getCurrentModels().get(0));
        assertSame(item3, adapter.getCurrentModels().get(1));
        assertSame(item2, adapter.getCurrentModels().get(2));
        assertEquals("INSERT(1,1)", observer.events.get(0));
    }

    @Test(expected = InvalidUsageException.class)
    public void testInsertModelBeforeNotFound() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.insertModelBefore(new TestItem(1), new TestItem(2));
    }

    @Test(expected = InvalidUsageException.class)
    public void testInsertModelAfterNotFound() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.insertModelAfter(new TestItem(1), new TestItem(2));
    }

    @Test(expected = InvalidUsageException.class)
    public void testRemoveModelNotFound() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.removeModel(new TestItem(1));
    }

    @Test
    public void testGetItemId() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.addModel(new TestItem(42));
        assertEquals(42, adapter.getItemId(0));
    }

    @Test
    public void testGetItemViewType() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.addModel(new TestItem(1));
        int viewType = adapter.getItemViewType(0);
        assertNotEquals(0, viewType);
    }

    @Test
    public void testShowHideModel() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        TestItem item = new TestItem(1, 10);
        adapter.addModel(item);
        observer.events.clear();

        adapter.hideModel(item);
        assertFalse(item.isVisible());
        assertEquals(1, observer.events.size());
        assertEquals("CHANGE(0,1)", observer.events.get(0));
    }

    @Test
    public void testHiddenModelReturnsPlaceholder() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        TestItem item = new TestItem(1, 10);
        adapter.addModel(item);

        adapter.hideModel(item);

        assertTrue(adapter.getModelForPosition(0) instanceof PlaceholderModel);
        assertEquals(1, adapter.getItemId(0));
    }

    @Test
    public void testVisibleModelReturnsSelf() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        TestItem item = new TestItem(1, 10);
        adapter.addModel(item);

        assertSame(item, adapter.getModelForPosition(0));
    }

    @Test
    public void testShowModelAfterHide() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        TestItem item = new TestItem(1, 10);
        adapter.addModel(item);

        adapter.hideModel(item);
        assertTrue(adapter.getModelForPosition(0) instanceof PlaceholderModel);

        adapter.showModel(item);
        assertTrue(item.isVisible());
        assertSame(item, adapter.getModelForPosition(0));
    }

    @Test
    public void testGetItemIdPreservedWhenHidden() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        TestItem item = new TestItem(42, 10);
        adapter.addModel(item);

        adapter.hideModel(item);
        assertEquals(42, adapter.getItemId(0));
    }

    @Test
    public void testHiddenModelPlaceholderHasZeroSpan() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        TestItem item = new TestItem(1);
        adapter.addModel(item);
        adapter.hideModel(item);

        ItemModel<?> placeholder = adapter.getModelForPosition(0);
        assertEquals(0, placeholder.getSpanSize(12, 0, 1));
    }

    @Test
    public void testNotifyModelsChanged() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();

        TestItem item1 = new TestItem(1, 10);
        TestItem item2 = new TestItem(2, 20);
        adapter.addModel(item1);
        adapter.addModel(item2);

        RecordingObserver observer = new RecordingObserver();
        adapter.setObserver(observer);

        adapter.getModels().set(0, new TestItem(1, 99));
        adapter.notifyModelsChanged();

        assertTrue(observer.events.stream().anyMatch(e -> e.startsWith("CHANGE")));
    }

    @Test
    public void testNotifyModelsChangedWithInsertions() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();

        TestItem item1 = new TestItem(1, 10);
        adapter.addModel(item1);

        RecordingObserver observer = new RecordingObserver();
        adapter.setObserver(observer);

        adapter.getModels().add(new TestItem(2, 20));
        adapter.notifyModelsChanged();

        assertTrue(observer.events.stream().anyMatch(e -> e.startsWith("INSERT")));
    }

    @Test
    public void testNotifyModelsChangedWithRemovals() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();

        adapter.addModel(new TestItem(1));
        adapter.addModel(new TestItem(2));

        RecordingObserver observer = new RecordingObserver();
        adapter.setObserver(observer);

        adapter.getModels().remove(0);
        adapter.notifyModelsChanged();

        assertTrue(observer.events.stream().anyMatch(e -> e.startsWith("REMOVE")));
    }

    @Test
    public void testBindAndUnbindHolder() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        BindTrackingItem item = new BindTrackingItem(1);
        adapter.addModel(item);

        ListItemHolder holder = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder, 0);

        assertTrue(item.bound);
        assertSame(item, holder.getModel());
        assertEquals(1, adapter.getBoundHolders().size());

        adapter.unbindHolder(holder);

        assertTrue(item.unbound);
        assertNull(holder.getModel());
        assertEquals(0, adapter.getBoundHolders().size());
    }

    @Test
    public void testBindWithPreviousModel() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        BindTrackingItem item = new BindTrackingItem(1);
        adapter.addModel(item);

        ListItemHolder holder = adapter.createHolder(adapter.getItemViewType(0));
        BindTrackingItem previousItem = new BindTrackingItem(1);
        adapter.bindHolder(holder, 0, previousItem);

        assertTrue(item.boundWithPrevious);
        assertSame(previousItem, item.previousModel);
    }

    @Test
    public void testBoundHolderTracking() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.addModel(new TestItem(42));
        adapter.addModel(new TestItem(43));

        ListItemHolder holder1 = adapter.createHolder(adapter.getItemViewType(0));
        ListItemHolder holder2 = adapter.createHolder(adapter.getItemViewType(1));

        adapter.bindHolder(holder1, 0);
        adapter.bindHolder(holder2, 1);

        assertEquals(2, adapter.getBoundHolders().size());
        assertSame(holder1, adapter.getBoundHolders().get(42));
        assertSame(holder2, adapter.getBoundHolders().get(43));
    }

    @Test
    public void testSaveAndRestoreState() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        StatefulItem item = new StatefulItem(1);
        adapter.addModel(item);

        ListItemHolder holder = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder, 0);
        holder.setState("saved-data");

        adapter.unbindHolder(holder);

        ListItemHolder holder2 = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder2, 0);

        assertEquals("saved-data", holder2.getState());
    }

    @Test
    public void testStateNotSavedForNonStatefulModel() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        TestItem item = new TestItem(1);
        adapter.addModel(item);

        ListItemHolder holder = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder, 0);
        holder.setState("should-not-save");

        adapter.unbindHolder(holder);

        ListItemHolder holder2 = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder2, 0);

        assertNull(holder2.getState());
    }

    @Test
    public void testSaveStateExport() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        StatefulItem item = new StatefulItem(1);
        adapter.addModel(item);

        ListItemHolder holder = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder, 0);
        holder.setState("persisted-state");

        java.util.Map<Long, Object> state = adapter.saveState();
        assertTrue(state.containsKey(1L));
        assertEquals("persisted-state", state.get(1L));
    }

    @Test
    public void testRestoreStateImport() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        StatefulItem item = new StatefulItem(1);
        adapter.addModel(item);

        java.util.Map<Long, Object> state = new java.util.HashMap<>();
        state.put(1L, "restored-state");
        adapter.restoreState(state);

        ListItemHolder holder = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder, 0);

        assertEquals("restored-state", holder.getState());
    }

    @Test
    public void testGetModelPosition() {
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        TestItem item1 = new TestItem(1);
        TestItem item2 = new TestItem(2);
        TestItem item3 = new TestItem(3);
        adapter.addModel(item1);
        adapter.addModel(item2);
        adapter.addModel(item3);

        assertEquals(0, adapter.getModelPosition(item1));
        assertEquals(1, adapter.getModelPosition(item2));
        assertEquals(2, adapter.getModelPosition(item3));
    }

    @Test
    public void testRemoveAllAfterModel() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        TestItem item1 = new TestItem(1);
        TestItem item2 = new TestItem(2);
        TestItem item3 = new TestItem(3);
        adapter.addModel(item1);
        adapter.addModel(item2);
        adapter.addModel(item3);
        observer.events.clear();

        adapter.removeAllAfterModel(item1);

        assertEquals(1, adapter.getItemCount());
        assertSame(item1, adapter.getCurrentModels().get(0));
        assertEquals("REMOVE(1,2)", observer.events.get(0));
    }

    @Test
    public void testNotifyModelChanged() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        TestItem item = new TestItem(1, 10);
        adapter.addModel(item);
        observer.events.clear();

        item.value = 99;
        adapter.notifyModelChanged(item);

        assertEquals(1, observer.events.size());
        assertEquals("CHANGE(0,1)", observer.events.get(0));
    }

    @Test
    public void testAddModelsCollection() {
        RecordingObserver observer = new RecordingObserver();
        SimpleDirectAdapter adapter = new SimpleDirectAdapter();
        adapter.setObserver(observer);

        List<TestItem> items = List.of(new TestItem(1), new TestItem(2));
        adapter.addModels(items);

        assertEquals(2, adapter.getItemCount());
        assertEquals("INSERT(0,2)", observer.events.get(0));
    }

    private static class BindTrackingItem extends ItemModel<Object> {
        boolean bound;
        boolean unbound;
        boolean boundWithPrevious;
        ItemModel<?> previousModel;

        BindTrackingItem(long id) {
            id(id);
        }

        @Override
        public int getDefaultLayoutId() { return 0; }

        @Override
        public void bind(Object view) { bound = true; }

        @Override
        public void bind(Object view, ItemModel<?> previousModel) {
            boundWithPrevious = true;
            this.previousModel = previousModel;
        }

        @Override
        public void unbind(Object view) { unbound = true; }
    }

    private static class StatefulItem extends ItemModel<Object> {
        StatefulItem(long id) { id(id); }

        @Override
        public int getDefaultLayoutId() { return 0; }

        @Override
        public boolean shouldSaveViewState() { return true; }
    }

    private static class RecordingObserver implements ListChangeObserver {
        final List<String> events = new ArrayList<>();

        @Override
        public void onItemsInserted(int positionStart, int count) {
            events.add("INSERT(" + positionStart + "," + count + ")");
        }

        @Override
        public void onItemsRemoved(int positionStart, int count) {
            events.add("REMOVE(" + positionStart + "," + count + ")");
        }

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            events.add("MOVE(" + fromPosition + "," + toPosition + ")");
        }

        @Override
        public void onItemsChanged(int positionStart, int count, Object payload) {
            events.add("CHANGE(" + positionStart + "," + count + ")");
        }
    }
}
