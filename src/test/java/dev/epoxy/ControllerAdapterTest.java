package dev.epoxy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ControllerAdapterTest {

    @Before
    public void setUp() {
        ViewTypeRegistry.resetForTesting();
    }

    @After
    public void tearDown() {
        ViewTypeRegistry.resetForTesting();
    }

    @Test
    public void testAdapterReceivesModelsFromController() {
        List<TestItem> items = new ArrayList<>();
        items.add(new TestItem(1));
        items.add(new TestItem(2));

        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                for (TestItem item : items) {
                    new TestItem(item.id(), item.value).addTo(this);
                }
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.requestModelBuild();

        assertEquals(2, adapter.getItemCount());
        assertEquals(1, adapter.getItemId(0));
        assertEquals(2, adapter.getItemId(1));
    }

    @Test
    public void testAdapterForwardsInsertions() {
        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.setObserver(observer);
        adapter.requestModelBuild();

        assertTrue(observer.events.stream().anyMatch(e -> e.startsWith("INSERT")));
    }

    @Test
    public void testAdapterForwardsRemovals() {
        AtomicBoolean includeSecond = new AtomicBoolean(true);
        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
                if (includeSecond.get()) {
                    new TestItem(2).addTo(this);
                }
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.setObserver(observer);
        adapter.requestModelBuild();
        observer.events.clear();

        includeSecond.set(false);
        adapter.requestModelBuild();

        assertTrue(observer.events.stream().anyMatch(e -> e.startsWith("REMOVE")));
    }

    @Test
    public void testAdapterForwardsMoves() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
                new TestItem(2).addTo(this);
                new TestItem(3).addTo(this);
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        RecordingObserver observer = new RecordingObserver();
        adapter.setObserver(observer);
        adapter.requestModelBuild();
        observer.events.clear();

        controller.moveModel(0, 2);

        assertTrue(observer.events.stream().anyMatch(e -> e.startsWith("MOVE")));
    }

    @Test
    public void testAdapterForwardsChanges() {
        AtomicBoolean updated = new AtomicBoolean(false);
        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1, updated.get() ? 99 : 10).addTo(this);
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.setObserver(observer);
        adapter.requestModelBuild();
        observer.events.clear();

        updated.set(true);
        adapter.requestModelBuild();

        assertTrue(observer.events.stream().anyMatch(e -> e.startsWith("CHANGE")));
    }

    @Test
    public void testAdapterReflectsControllerState() {
        AtomicBoolean includeThird = new AtomicBoolean(false);
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
                new TestItem(2).addTo(this);
                if (includeThird.get()) {
                    new TestItem(3).addTo(this);
                }
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.requestModelBuild();
        assertEquals(2, adapter.getItemCount());

        includeThird.set(true);
        adapter.requestModelBuild();
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testAdapterGetItemViewType() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.requestModelBuild();

        int viewType = adapter.getItemViewType(0);
        assertNotEquals(0, viewType);
    }

    @Test
    public void testAdapterBindAndUnbind() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new BindTrackingItem(1).addTo(this);
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.requestModelBuild();

        ListItemHolder holder = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder, 0);

        assertEquals(1, adapter.getBoundHolders().size());
        assertNotNull(holder.getModel());

        adapter.unbindHolder(holder);

        assertEquals(0, adapter.getBoundHolders().size());
        assertNull(holder.getModel());
    }

    @Test
    public void testGetController() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {}
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        assertSame(controller, adapter.getController());
    }

    @Test
    public void testAdapterSaveAndRestoreState() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new StatefulItem(1).addTo(this);
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.requestModelBuild();

        ListItemHolder holder = adapter.createHolder(adapter.getItemViewType(0));
        adapter.bindHolder(holder, 0);
        holder.setState("controller-state");

        java.util.Map<Long, Object> state = adapter.saveState();
        adapter.unbindHolder(holder);

        ControllerAdapter adapter2 = new ControllerAdapter(controller);
        adapter2.requestModelBuild();
        adapter2.restoreState(state);

        ListItemHolder holder2 = adapter2.createHolder(adapter2.getItemViewType(0));
        adapter2.bindHolder(holder2, 0);

        assertEquals("controller-state", holder2.getState());
    }

    @Test
    public void testMultipleRebuilds() {
        List<Integer> values = new ArrayList<>();
        values.add(10);

        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                for (int i = 0; i < values.size(); i++) {
                    new TestItem(i + 1, values.get(i)).addTo(this);
                }
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.setObserver(observer);
        adapter.requestModelBuild();
        assertEquals(1, adapter.getItemCount());

        values.add(20);
        adapter.requestModelBuild();
        assertEquals(2, adapter.getItemCount());

        values.add(30);
        adapter.requestModelBuild();
        assertEquals(3, adapter.getItemCount());
    }

    private static class BindTrackingItem extends ItemModel<Object> {
        BindTrackingItem(long id) { id(id); }

        @Override
        public int getDefaultLayoutId() { return 0; }
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
