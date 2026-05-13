package dev.epoxy;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class TypedControllerTest {

    @Test
    public void testTypedControllerSetData() {
        AtomicReference<String> received = new AtomicReference<>();
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {
                received.set(data);
                if (data != null) {
                    new TestItem(1, data.length()).addTo(this);
                }
            }
        };
        controller.setData("hello");
        assertEquals("hello", received.get());
        assertEquals(1, controller.getModelCount());
    }

    @Test
    public void testTypedControllerGetCurrentData() {
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {
                if (data != null) new TestItem(1).addTo(this);
            }
        };
        assertNull(controller.getCurrentData());
        controller.setData("test");
        assertEquals("test", controller.getCurrentData());
    }

    @Test(expected = InvalidUsageException.class)
    public void testTypedControllerRequestBuildThrowsOutsideSetData() {
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {}
        };
        controller.requestModelBuild();
    }

    @Test
    public void testTypedControllerMultipleSetDataCalls() {
        AtomicInteger buildCount = new AtomicInteger(0);
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {
                buildCount.incrementAndGet();
                if (data != null) new TestItem(1).addTo(this);
            }
        };
        controller.setData("a");
        controller.setData("b");
        controller.setData("c");
        assertEquals(3, buildCount.get());
    }

    @Test
    public void testTypedControllerAlwaysRebuildsEvenWithSameData() {
        AtomicInteger buildCount = new AtomicInteger(0);
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {
                buildCount.incrementAndGet();
                if (data != null) new TestItem(1).addTo(this);
            }
        };
        controller.setData("same");
        controller.setData("same");
        controller.setData("same");
        assertEquals(3, buildCount.get());
    }

    @Test
    public void testTypedControllerWithObserver() {
        List<String> events = new ArrayList<>();
        TypedController<Integer> controller = new TypedController<>() {
            @Override
            protected void buildModels(Integer data) {
                if (data != null) {
                    for (int i = 0; i < data; i++) {
                        new TestItem(i + 1).addTo(this);
                    }
                }
            }
        };
        controller.setObserver(new RecordingObserver(events));
        controller.setData(2);
        assertTrue(events.stream().anyMatch(e -> e.startsWith("INSERT")));
    }

    @Test
    public void testTypedControllerDiffsOnDataChange() {
        List<String> events = new ArrayList<>();
        AtomicReference<List<Integer>> data = new AtomicReference<>(List.of(1, 2));
        TypedController<List<Integer>> controller = new TypedController<>() {
            @Override
            protected void buildModels(List<Integer> items) {
                if (items != null) {
                    for (int id : items) new TestItem(id).addTo(this);
                }
            }
        };
        controller.setObserver(new RecordingObserver(events));
        controller.setData(data.get());
        events.clear();

        controller.setData(List.of(1, 2, 3));
        assertTrue(events.stream().anyMatch(e -> e.startsWith("INSERT")));
    }

    @Test
    public void testTypedControllerWithInterceptor() {
        AtomicInteger intercepted = new AtomicInteger(0);
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {
                if (data != null) new TestItem(1).addTo(this);
            }
        };
        controller.addInterceptor(models -> intercepted.incrementAndGet());
        controller.setData("test");
        assertEquals(1, intercepted.get());
    }

    @Test
    public void testTypedControllerWithBuildListener() {
        AtomicInteger listenerCalled = new AtomicInteger(0);
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {
                if (data != null) new TestItem(1).addTo(this);
            }
        };
        controller.addBuildListener(ops -> listenerCalled.incrementAndGet());
        controller.setData("test");
        assertEquals(1, listenerCalled.get());
    }

    @Test
    public void testTypedControllerMoveModel() {
        List<String> events = new ArrayList<>();
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {
                new TestItem(1).addTo(this);
                new TestItem(2).addTo(this);
                new TestItem(3).addTo(this);
            }
        };
        controller.setObserver(new RecordingObserver(events));
        controller.setData("init");
        events.clear();

        controller.moveModel(0, 2);
        assertTrue(events.stream().anyMatch(e -> e.startsWith("MOVE")));
    }

    @Test
    public void testTyped2ControllerSetData() {
        AtomicReference<String> r1 = new AtomicReference<>();
        AtomicReference<Integer> r2 = new AtomicReference<>();
        Typed2Controller<String, Integer> controller = new Typed2Controller<>() {
            @Override
            protected void buildModels(String data1, Integer data2) {
                r1.set(data1);
                r2.set(data2);
                if (data1 != null) new TestItem(1).addTo(this);
            }
        };
        controller.setData("hello", 42);
        assertEquals("hello", r1.get());
        assertEquals(Integer.valueOf(42), r2.get());
    }

    @Test(expected = InvalidUsageException.class)
    public void testTyped2ControllerRequestBuildThrows() {
        Typed2Controller<String, Integer> controller = new Typed2Controller<>() {
            @Override
            protected void buildModels(String data1, Integer data2) {}
        };
        controller.requestModelBuild();
    }

    @Test
    public void testTyped3ControllerSetData() {
        AtomicReference<String> r1 = new AtomicReference<>();
        AtomicReference<Integer> r2 = new AtomicReference<>();
        AtomicReference<Boolean> r3 = new AtomicReference<>();
        Typed3Controller<String, Integer, Boolean> controller = new Typed3Controller<>() {
            @Override
            protected void buildModels(String d1, Integer d2, Boolean d3) {
                r1.set(d1);
                r2.set(d2);
                r3.set(d3);
                if (d1 != null) new TestItem(1).addTo(this);
            }
        };
        controller.setData("x", 10, true);
        assertEquals("x", r1.get());
        assertEquals(Integer.valueOf(10), r2.get());
        assertEquals(Boolean.TRUE, r3.get());
    }

    @Test(expected = InvalidUsageException.class)
    public void testTyped3ControllerRequestBuildThrows() {
        Typed3Controller<String, Integer, Boolean> controller = new Typed3Controller<>() {
            @Override
            protected void buildModels(String d1, Integer d2, Boolean d3) {}
        };
        controller.requestModelBuild();
    }

    @Test
    public void testTyped4ControllerSetData() {
        AtomicReference<String> r1 = new AtomicReference<>();
        AtomicReference<Integer> r2 = new AtomicReference<>();
        AtomicReference<Boolean> r3 = new AtomicReference<>();
        AtomicReference<Double> r4 = new AtomicReference<>();
        Typed4Controller<String, Integer, Boolean, Double> controller = new Typed4Controller<>() {
            @Override
            protected void buildModels(String d1, Integer d2, Boolean d3, Double d4) {
                r1.set(d1);
                r2.set(d2);
                r3.set(d3);
                r4.set(d4);
                if (d1 != null) new TestItem(1).addTo(this);
            }
        };
        controller.setData("y", 20, false, 3.14);
        assertEquals("y", r1.get());
        assertEquals(Integer.valueOf(20), r2.get());
        assertEquals(Boolean.FALSE, r3.get());
        assertEquals(Double.valueOf(3.14), r4.get());
    }

    @Test(expected = InvalidUsageException.class)
    public void testTyped4ControllerRequestBuildThrows() {
        Typed4Controller<String, Integer, Boolean, Double> controller = new Typed4Controller<>() {
            @Override
            protected void buildModels(String d1, Integer d2, Boolean d3, Double d4) {}
        };
        controller.requestModelBuild();
    }

    @Test
    public void testSimpleControllerSetModels() {
        SimpleController controller = new SimpleController();
        List<TestItem> models = List.of(new TestItem(1), new TestItem(2), new TestItem(3));
        controller.setModels(models);

        assertEquals(3, controller.getModelCount());
    }

    @Test
    public void testSimpleControllerDiffsOnChange() {
        List<String> events = new ArrayList<>();
        SimpleController controller = new SimpleController();
        controller.setObserver(new RecordingObserver(events));

        controller.setModels(List.of(new TestItem(1), new TestItem(2)));
        events.clear();

        controller.setModels(List.of(new TestItem(1), new TestItem(2), new TestItem(3)));
        assertTrue(events.stream().anyMatch(e -> e.startsWith("INSERT")));
    }

    @Test(expected = InvalidUsageException.class)
    public void testSimpleControllerRequestBuildThrows() {
        SimpleController controller = new SimpleController();
        controller.requestModelBuild();
    }

    @Test
    public void testSimpleControllerEmptyList() {
        SimpleController controller = new SimpleController();
        controller.setModels(List.of(new TestItem(1), new TestItem(2)));
        assertEquals(2, controller.getModelCount());

        controller.setModels(List.of());
        assertEquals(0, controller.getModelCount());
    }

    @Test
    public void testTypedControllerWithControllerAdapter() {
        List<String> events = new ArrayList<>();
        TypedController<Integer> controller = new TypedController<>() {
            @Override
            protected void buildModels(Integer data) {
                if (data != null) {
                    for (int i = 0; i < data; i++) {
                        new TestItem(i + 1).addTo(this);
                    }
                }
            }
        };

        ControllerAdapter adapter = new ControllerAdapter(controller);
        adapter.setObserver(new RecordingObserver(events));
        controller.setData(3);

        assertEquals(3, adapter.getItemCount());
        assertTrue(events.stream().anyMatch(e -> e.startsWith("INSERT")));
    }

    @Test
    public void testTyped2ControllerMoveModel() {
        List<String> events = new ArrayList<>();
        Typed2Controller<String, Integer> controller = new Typed2Controller<>() {
            @Override
            protected void buildModels(String d1, Integer d2) {
                new TestItem(1).addTo(this);
                new TestItem(2).addTo(this);
            }
        };
        controller.setObserver(new RecordingObserver(events));
        controller.setData("x", 1);
        events.clear();

        controller.moveModel(0, 1);
        assertTrue(events.stream().anyMatch(e -> e.startsWith("MOVE")));
    }

    @Test
    public void testSimpleControllerMoveModel() {
        List<String> events = new ArrayList<>();
        SimpleController controller = new SimpleController();
        controller.setObserver(new RecordingObserver(events));
        controller.setModels(List.of(new TestItem(1), new TestItem(2)));
        events.clear();

        controller.moveModel(0, 1);
        assertTrue(events.stream().anyMatch(e -> e.startsWith("MOVE")));
    }

    @Test
    public void testTypedControllerNullDataBeforeFirstSet() {
        TypedController<String> controller = new TypedController<>() {
            @Override
            protected void buildModels(String data) {
                if (data != null) new TestItem(1).addTo(this);
            }
        };
        assertNull(controller.getCurrentData());
    }

    private static class RecordingObserver implements ListChangeObserver {
        final List<String> events;

        RecordingObserver(List<String> events) { this.events = events; }

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
