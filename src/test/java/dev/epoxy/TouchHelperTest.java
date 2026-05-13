package dev.epoxy;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TouchHelperTest {

    static class SpecialItem extends TestItem {
        SpecialItem(long id) { super(id, 0); }
        SpecialItem(long id, int val) { super(id, val); }
    }

    static class OtherItem extends ItemModel<Object> {
        OtherItem(long id) { id(id); }
        @Override public int getDefaultLayoutId() { return 0; }
    }

    private ListController createController(List<ItemModel<?>> models) {
        return new ListController() {
            @Override
            protected void buildModels() {
                for (ItemModel<?> m : models) add(m);
            }
        };
    }

    // --- TouchDirection ---

    @Test
    public void touchDirectionDefinesBitFlags() {
        assertEquals(1, TouchDirection.UP);
        assertEquals(2, TouchDirection.DOWN);
        assertEquals(4, TouchDirection.LEFT);
        assertEquals(8, TouchDirection.RIGHT);
    }

    @Test
    public void touchDirectionVerticalCombinesUpDown() {
        assertEquals(TouchDirection.UP | TouchDirection.DOWN, TouchDirection.VERTICAL);
    }

    @Test
    public void touchDirectionHorizontalCombinesLeftRight() {
        assertEquals(TouchDirection.LEFT | TouchDirection.RIGHT, TouchDirection.HORIZONTAL);
    }

    @Test
    public void touchDirectionAllCombinesAllFour() {
        assertEquals(TouchDirection.UP | TouchDirection.DOWN | TouchDirection.LEFT | TouchDirection.RIGHT,
                TouchDirection.ALL);
    }

    // --- DragCallback interface ---

    @Test
    public void dragCallbackDefinesAllMethods() {
        List<String> called = new ArrayList<>();
        DragCallback<TestItem> cb = new DragCallback<>() {
            @Override public void onDragStarted(TestItem model, int pos) { called.add("started"); }
            @Override public void onModelMoved(int from, int to, TestItem m) { called.add("moved"); }
            @Override public void onDragReleased(TestItem model) { called.add("released"); }
            @Override public void clearView(TestItem model) { called.add("clear"); }
        };
        TestItem item = new TestItem(1);
        cb.onDragStarted(item, 0);
        cb.onModelMoved(0, 1, item);
        cb.onDragReleased(item);
        cb.clearView(item);
        assertEquals(List.of("started", "moved", "released", "clear"), called);
    }

    // --- SwipeCallback interface ---

    @Test
    public void swipeCallbackDefinesAllMethods() {
        List<String> called = new ArrayList<>();
        SwipeCallback<TestItem> cb = new SwipeCallback<>() {
            @Override public void onSwipeStarted(TestItem model, int pos) { called.add("started"); }
            @Override public void onSwipeCompleted(TestItem m, int pos, int dir) { called.add("completed"); }
            @Override public void onSwipeReleased(TestItem model) { called.add("released"); }
            @Override public void clearView(TestItem model) { called.add("clear"); }
        };
        TestItem item = new TestItem(1);
        cb.onSwipeStarted(item, 0);
        cb.onSwipeCompleted(item, 0, TouchDirection.LEFT);
        cb.onSwipeReleased(item);
        cb.clearView(item);
        assertEquals(List.of("started", "completed", "released", "clear"), called);
    }

    // --- TouchHandler construction ---

    @Test
    public void touchHandlerTakesControllerAndTargetClass() {
        ListController ctrl = createController(List.of());
        TouchHandler<TestItem> handler = new TouchHandler<>(ctrl, TestItem.class,
                TouchDirection.VERTICAL, 0);
        assertSame(ctrl, handler.getController());
        assertEquals(TouchDirection.VERTICAL, handler.getDragDirections());
        assertEquals(0, handler.getSwipeDirections());
    }

    // --- isDragEnabled / isSwipeEnabled ---

    @Test
    public void isDragEnabledReturnsTrueForMatchingModel() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                TouchDirection.VERTICAL, 0);
        handler.setDragCallback(new TouchHelper.DragCallbacks<>() {
            @Override public void onModelMoved(int f, int t, TestItem m) {}
        });
        assertTrue(handler.isDragEnabled(new TestItem(1)));
    }

    @Test
    public void isDragEnabledReturnsFalseForNonMatchingModel() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                TouchDirection.VERTICAL, 0);
        handler.setDragCallback(new TouchHelper.DragCallbacks<>() {
            @Override public void onModelMoved(int f, int t, TestItem m) {}
        });
        assertFalse(handler.isDragEnabled(new OtherItem(1)));
    }

    @Test
    public void isDragEnabledReturnsFalseWhenNoDragDirections() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class, 0, 0);
        assertFalse(handler.isDragEnabled(new TestItem(1)));
    }

    @Test
    public void isSwipeEnabledReturnsTrueForMatchingModel() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                0, TouchDirection.LEFT);
        handler.setSwipeCallback(new TouchHelper.SwipeCallbacks<>() {
            @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
        });
        assertTrue(handler.isSwipeEnabled(new TestItem(1)));
    }

    @Test
    public void isSwipeEnabledReturnsFalseForNonMatchingModel() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                0, TouchDirection.LEFT);
        assertFalse(handler.isSwipeEnabled(new OtherItem(1)));
    }

    @Test
    public void isSwipeEnabledReturnsFalseWhenNoSwipeDirections() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class, 0, 0);
        assertFalse(handler.isSwipeEnabled(new TestItem(1)));
    }

    // --- Subclass filtering ---

    @Test
    public void touchHandlerFiltersByTargetClassIncludingSubclasses() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                TouchDirection.VERTICAL, 0);
        handler.setDragCallback(new TouchHelper.DragCallbacks<>() {
            @Override public void onModelMoved(int f, int t, TestItem m) {}
        });
        assertTrue(handler.isDragEnabled(new SpecialItem(1)));
    }

    // --- handleDragMove ---

    @Test
    public void handleDragMoveCallsControllerMoveModelAndDispatchesCallback() {
        List<ItemModel<?>> models = new ArrayList<>();
        models.add(new TestItem(1));
        models.add(new TestItem(2));
        models.add(new TestItem(3));
        ListController ctrl = createController(models);
        ctrl.requestModelBuild();

        List<String> events = new ArrayList<>();
        TouchHandler<TestItem> handler = new TouchHandler<>(ctrl, TestItem.class,
                TouchDirection.VERTICAL, 0);
        handler.setDragCallback(new TouchHelper.DragCallbacks<>() {
            @Override
            public void onModelMoved(int from, int to, TestItem model) {
                events.add("moved:" + from + "->" + to);
            }
        });

        handler.handleDragStart(ctrl.getModel(0), 0);
        handler.handleDragMove(0, 2);

        assertEquals(List.of("moved:0->2"), events);
        assertEquals(2, ctrl.getModel(0).id());
    }

    // --- Drag lifecycle ---

    @Test
    public void touchHandlerDispatchesDragLifecycleEvents() {
        List<String> events = new ArrayList<>();
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                TouchDirection.VERTICAL, 0);
        handler.setDragCallback(new DragCallback<>() {
            @Override public void onDragStarted(TestItem m, int p) { events.add("start:" + m.id()); }
            @Override public void onModelMoved(int f, int t, TestItem m) { events.add("move"); }
            @Override public void onDragReleased(TestItem m) { events.add("release:" + m.id()); }
            @Override public void clearView(TestItem m) { events.add("clear:" + m.id()); }
        });

        TestItem item = new TestItem(42);
        handler.handleDragStart(item, 0);
        handler.handleDragRelease();
        handler.handleDragClearView();

        assertEquals(List.of("start:42", "release:42", "clear:42"), events);
    }

    // --- Swipe lifecycle ---

    @Test
    public void touchHandlerDispatchesSwipeLifecycleEvents() {
        List<String> events = new ArrayList<>();
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                0, TouchDirection.LEFT);
        handler.setSwipeCallback(new SwipeCallback<>() {
            @Override public void onSwipeStarted(TestItem m, int p) { events.add("start:" + m.id()); }
            @Override public void onSwipeCompleted(TestItem m, int p, int d) { events.add("complete:" + d); }
            @Override public void onSwipeReleased(TestItem m) { events.add("release"); }
            @Override public void clearView(TestItem m) { events.add("clear"); }
        });

        TestItem item = new TestItem(10);
        handler.handleSwipeStart(item, 0);
        handler.handleSwipeComplete(0, TouchDirection.LEFT);
        handler.handleSwipeRelease();
        handler.handleSwipeClearView();

        assertEquals(List.of("start:10", "complete:" + TouchDirection.LEFT, "release", "clear"), events);
    }

    // --- TouchHelper drag builder chain ---

    @Test
    public void initDraggingReturnsDragBuilder() {
        ListController ctrl = createController(List.of());
        TouchHelper.DragBuilder builder = TouchHelper.initDragging(ctrl);
        assertNotNull(builder);
    }

    @Test
    public void dragBuilderSetsControllerReference() {
        ListController ctrl = createController(List.of());
        TouchHandler<TestItem> handler = TouchHelper.initDragging(ctrl)
                .forVerticalList()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, TestItem m) {}
                });
        assertSame(ctrl, handler.getController());
    }

    @Test
    public void dragBuilderSupportsForVerticalList() {
        ListController ctrl = createController(List.of());
        TouchHandler<TestItem> handler = TouchHelper.initDragging(ctrl)
                .forVerticalList()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, TestItem m) {}
                });
        assertEquals(TouchDirection.VERTICAL, handler.getDragDirections());
    }

    @Test
    public void dragBuilderSupportsForHorizontalList() {
        ListController ctrl = createController(List.of());
        TouchHandler<TestItem> handler = TouchHelper.initDragging(ctrl)
                .forHorizontalList()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, TestItem m) {}
                });
        assertEquals(TouchDirection.HORIZONTAL, handler.getDragDirections());
    }

    @Test
    public void dragBuilderSupportsForGrid() {
        ListController ctrl = createController(List.of());
        TouchHandler<TestItem> handler = TouchHelper.initDragging(ctrl)
                .forGrid()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, TestItem m) {}
                });
        assertEquals(TouchDirection.ALL, handler.getDragDirections());
    }

    @Test
    public void dragBuilderSupportsWithTarget() {
        ListController ctrl = createController(List.of());
        TouchHandler<TestItem> handler = TouchHelper.initDragging(ctrl)
                .forVerticalList()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, TestItem m) {}
                });
        assertTrue(handler.isTouchableModel(new TestItem(1)));
        assertFalse(handler.isTouchableModel(new OtherItem(1)));
    }

    @Test
    public void dragBuilderSupportsForAllModels() {
        ListController ctrl = createController(List.of());
        TouchHandler<ItemModel<?>> handler = TouchHelper.initDragging(ctrl)
                .forVerticalList()
                .forAllModels()
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, ItemModel<?> m) {}
                });
        assertTrue(handler.isTouchableModel(new TestItem(1)));
        assertTrue(handler.isTouchableModel(new OtherItem(2)));
    }

    @Test
    public void dragBuilderAndCallbacksReturnsTouchHandler() {
        ListController ctrl = createController(List.of());
        TouchHandler<TestItem> handler = TouchHelper.initDragging(ctrl)
                .forVerticalList()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, TestItem m) {}
                });
        assertNotNull(handler);
        assertTrue(handler instanceof TouchHandler);
    }

    // --- TouchHelper swipe builder chain ---

    @Test
    public void initSwipingReturnsSwipeBuilder() {
        TouchHelper.SwipeBuilder builder = TouchHelper.initSwiping();
        assertNotNull(builder);
    }

    @Test
    public void swipeBuilderSupportsLeft() {
        TouchHandler<TestItem> handler = TouchHelper.initSwiping()
                .left()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.SwipeCallbacks<>() {
                    @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
                });
        assertEquals(TouchDirection.LEFT, handler.getSwipeDirections());
    }

    @Test
    public void swipeBuilderSupportsRight() {
        TouchHandler<TestItem> handler = TouchHelper.initSwiping()
                .right()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.SwipeCallbacks<>() {
                    @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
                });
        assertEquals(TouchDirection.RIGHT, handler.getSwipeDirections());
    }

    @Test
    public void swipeBuilderSupportsLeftAndRight() {
        TouchHandler<TestItem> handler = TouchHelper.initSwiping()
                .leftAndRight()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.SwipeCallbacks<>() {
                    @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
                });
        assertEquals(TouchDirection.HORIZONTAL, handler.getSwipeDirections());
    }

    @Test
    public void swipeBuilderSupportsWithTarget() {
        TouchHandler<TestItem> handler = TouchHelper.initSwiping()
                .left()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.SwipeCallbacks<>() {
                    @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
                });
        assertTrue(handler.isTouchableModel(new TestItem(1)));
        assertFalse(handler.isTouchableModel(new OtherItem(1)));
    }

    @Test
    public void swipeBuilderAndCallbacksReturnsTouchHandler() {
        TouchHandler<TestItem> handler = TouchHelper.initSwiping()
                .left()
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.SwipeCallbacks<>() {
                    @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
                });
        assertNotNull(handler);
    }

    // --- DragCallbacks defaults ---

    @Test
    public void dragCallbacksProvideNoOpDefaultsExceptOnModelMoved() {
        List<String> events = new ArrayList<>();
        TouchHelper.DragCallbacks<TestItem> callbacks = new TouchHelper.DragCallbacks<>() {
            @Override
            public void onModelMoved(int from, int to, TestItem model) {
                events.add("moved");
            }
        };
        TestItem item = new TestItem(1);
        callbacks.onDragStarted(item, 0);
        callbacks.onDragReleased(item);
        callbacks.clearView(item);
        assertTrue(events.isEmpty());
        callbacks.onModelMoved(0, 1, item);
        assertEquals(1, events.size());
    }

    @Test
    public void dragCallbacksIsDragEnabledDefaultsTrue() {
        TouchHelper.DragCallbacks<TestItem> callbacks = new TouchHelper.DragCallbacks<>() {
            @Override public void onModelMoved(int f, int t, TestItem m) {}
        };
        assertTrue(callbacks.isDragEnabledForModel(new TestItem(1)));
    }

    // --- SwipeCallbacks defaults ---

    @Test
    public void swipeCallbacksProvideNoOpDefaultsExceptOnSwipeCompleted() {
        List<String> events = new ArrayList<>();
        TouchHelper.SwipeCallbacks<TestItem> callbacks = new TouchHelper.SwipeCallbacks<>() {
            @Override
            public void onSwipeCompleted(TestItem model, int position, int direction) {
                events.add("completed");
            }
        };
        TestItem item = new TestItem(1);
        callbacks.onSwipeStarted(item, 0);
        callbacks.onSwipeReleased(item);
        callbacks.clearView(item);
        assertTrue(events.isEmpty());
        callbacks.onSwipeCompleted(item, 0, TouchDirection.LEFT);
        assertEquals(1, events.size());
    }

    @Test
    public void swipeCallbacksIsSwipeEnabledDefaultsTrue() {
        TouchHelper.SwipeCallbacks<TestItem> callbacks = new TouchHelper.SwipeCallbacks<>() {
            @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
        };
        assertTrue(callbacks.isSwipeEnabledForModel(new TestItem(1)));
    }

    // --- Tracked models ---

    @Test
    public void touchHandlerTracksDraggedModel() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                TouchDirection.VERTICAL, 0);
        handler.setDragCallback(new TouchHelper.DragCallbacks<>() {
            @Override public void onModelMoved(int f, int t, TestItem m) {}
        });

        assertNull(handler.getDraggedModel());
        TestItem item = new TestItem(5);
        handler.handleDragStart(item, 0);
        assertSame(item, handler.getDraggedModel());
        handler.handleDragClearView();
        assertNull(handler.getDraggedModel());
    }

    @Test
    public void touchHandlerTracksSwipedModel() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                0, TouchDirection.LEFT);
        handler.setSwipeCallback(new TouchHelper.SwipeCallbacks<>() {
            @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
        });

        assertNull(handler.getSwipedModel());
        TestItem item = new TestItem(7);
        handler.handleSwipeStart(item, 0);
        assertSame(item, handler.getSwipedModel());
        handler.handleSwipeClearView();
        assertNull(handler.getSwipedModel());
    }

    // --- Multiple target classes ---

    @Test
    public void touchHandlerSupportsMultipleTargetClasses() {
        @SuppressWarnings("unchecked")
        TouchHandler<ItemModel<?>> handler = TouchHelper.initDragging(createController(List.of()))
                .forVerticalList()
                .withTargets(TestItem.class, OtherItem.class)
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, ItemModel<?> m) {}
                });
        assertTrue(handler.isTouchableModel(new TestItem(1)));
        assertTrue(handler.isTouchableModel(new OtherItem(2)));
        assertTrue(handler.isTouchableModel(new SpecialItem(3)));
    }

    @Test
    public void swipeWithMultipleTargetClasses() {
        @SuppressWarnings("unchecked")
        TouchHandler<ItemModel<?>> handler = TouchHelper.initSwiping()
                .leftAndRight()
                .withTargets(TestItem.class, OtherItem.class)
                .andCallbacks(new TouchHelper.SwipeCallbacks<>() {
                    @Override public void onSwipeCompleted(ItemModel<?> m, int p, int d) {}
                });
        assertTrue(handler.isTouchableModel(new TestItem(1)));
        assertTrue(handler.isTouchableModel(new OtherItem(2)));
    }

    // --- withDirections ---

    @Test
    public void dragBuilderSupportsCustomDirections() {
        ListController ctrl = createController(List.of());
        TouchHandler<TestItem> handler = TouchHelper.initDragging(ctrl)
                .withDirections(TouchDirection.UP | TouchDirection.LEFT)
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.DragCallbacks<>() {
                    @Override public void onModelMoved(int f, int t, TestItem m) {}
                });
        assertEquals(TouchDirection.UP | TouchDirection.LEFT, handler.getDragDirections());
    }

    @Test
    public void swipeBuilderSupportsCustomDirections() {
        TouchHandler<TestItem> handler = TouchHelper.initSwiping()
                .withDirections(TouchDirection.UP | TouchDirection.DOWN)
                .withTarget(TestItem.class)
                .andCallbacks(new TouchHelper.SwipeCallbacks<>() {
                    @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
                });
        assertEquals(TouchDirection.UP | TouchDirection.DOWN, handler.getSwipeDirections());
    }

    // --- isDragEnabled via DragCallbacks control ---

    @Test
    public void isDragEnabledRespectsDragCallbacksOverride() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                TouchDirection.VERTICAL, 0);
        handler.setDragCallback(new TouchHelper.DragCallbacks<>() {
            @Override public void onModelMoved(int f, int t, TestItem m) {}
            @Override public boolean isDragEnabledForModel(ItemModel<?> model) {
                return ((TestItem) model).value == 42;
            }
        });
        TestItem enabled = new TestItem(1, 42);
        TestItem disabled = new TestItem(2, 0);
        assertTrue(handler.isDragEnabled(enabled));
        assertFalse(handler.isDragEnabled(disabled));
    }

    @Test
    public void isSwipeEnabledRespectsSwipeCallbacksOverride() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                0, TouchDirection.LEFT);
        handler.setSwipeCallback(new TouchHelper.SwipeCallbacks<>() {
            @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
            @Override public boolean isSwipeEnabledForModel(ItemModel<?> model) {
                return ((TestItem) model).value == 99;
            }
        });
        TestItem enabled = new TestItem(1, 99);
        TestItem disabled = new TestItem(2, 0);
        assertTrue(handler.isSwipeEnabled(enabled));
        assertFalse(handler.isSwipeEnabled(disabled));
    }

    // --- Drag ignores non-touchable models ---

    @Test
    public void handleDragStartIgnoresNonTouchableModel() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                TouchDirection.VERTICAL, 0);
        List<String> events = new ArrayList<>();
        handler.setDragCallback(new DragCallback<>() {
            @Override public void onDragStarted(TestItem m, int p) { events.add("start"); }
            @Override public void onModelMoved(int f, int t, TestItem m) {}
            @Override public void onDragReleased(TestItem m) {}
            @Override public void clearView(TestItem m) {}
        });
        handler.handleDragStart(new OtherItem(1), 0);
        assertTrue(events.isEmpty());
        assertNull(handler.getDraggedModel());
    }

    @Test
    public void handleSwipeStartIgnoresNonTouchableModel() {
        TouchHandler<TestItem> handler = new TouchHandler<>(null, TestItem.class,
                0, TouchDirection.LEFT);
        List<String> events = new ArrayList<>();
        handler.setSwipeCallback(new SwipeCallback<>() {
            @Override public void onSwipeStarted(TestItem m, int p) { events.add("start"); }
            @Override public void onSwipeCompleted(TestItem m, int p, int d) {}
            @Override public void onSwipeReleased(TestItem m) {}
            @Override public void clearView(TestItem m) {}
        });
        handler.handleSwipeStart(new OtherItem(1), 0);
        assertTrue(events.isEmpty());
        assertNull(handler.getSwipedModel());
    }

    // --- Swipe forAllModels ---

    @Test
    public void swipeBuilderSupportsForAllModels() {
        TouchHandler<ItemModel<?>> handler = TouchHelper.initSwiping()
                .left()
                .forAllModels()
                .andCallbacks(new TouchHelper.SwipeCallbacks<>() {
                    @Override public void onSwipeCompleted(ItemModel<?> m, int p, int d) {}
                });
        assertTrue(handler.isTouchableModel(new TestItem(1)));
        assertTrue(handler.isTouchableModel(new OtherItem(2)));
    }
}
