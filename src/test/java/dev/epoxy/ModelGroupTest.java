package dev.epoxy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ModelGroupTest {

    @Before
    public void setUp() {
        ViewTypeRegistry.resetForTesting();
    }

    @After
    public void tearDown() {
        ViewTypeRegistry.resetForTesting();
    }

    @Test
    public void testBasicConstruction() {
        TestItem child1 = new TestItem(1, 10);
        TestItem child2 = new TestItem(2, 20);
        ModelGroup group = new ModelGroup(100, child1, child2);

        assertEquals(100, group.getDefaultLayoutId());
        assertEquals(2, group.getChildModels().size());
    }

    @Test
    public void testDefaultIdFromFirstChild() {
        TestItem child1 = new TestItem(42, 10);
        TestItem child2 = new TestItem(43, 20);
        ModelGroup group = new ModelGroup(100, child1, child2);

        assertEquals(42, group.id());
    }

    @Test
    public void testExplicitIdOverride() {
        TestItem child1 = new TestItem(1, 10);
        ModelGroup group = new ModelGroup(100, child1);
        group.id(999);

        assertEquals(999, group.id());
    }

    @Test(expected = InvalidUsageException.class)
    public void testEmptyCollectionThrows() {
        new ModelGroup(100, List.of());
    }

    @Test
    public void testCollectionConstructor() {
        List<TestItem> children = List.of(new TestItem(1), new TestItem(2), new TestItem(3));
        ModelGroup group = new ModelGroup(100, children);

        assertEquals(3, group.getChildModels().size());
    }

    @Test
    public void testChildModelsUnmodifiable() {
        ModelGroup group = new ModelGroup(100, new TestItem(1));
        try {
            group.getChildModels().add(new TestItem(2));
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testAddModel() {
        ModelGroup group = new ModelGroup(100) {};
        group.addModel(new TestItem(1));
        group.addModel(new TestItem(2));

        assertEquals(2, group.getChildModels().size());
    }

    @Test
    public void testBindDelegatesToChildren() {
        BindTrackingItem child1 = new BindTrackingItem(1);
        BindTrackingItem child2 = new BindTrackingItem(2);
        ModelGroup group = new ModelGroup(100, child1, child2);

        ModelGroupHolder holder = new ModelGroupHolder();
        group.bind(holder);

        assertTrue(child1.bound);
        assertTrue(child2.bound);
    }

    @Test
    public void testUnbindDelegatesToChildren() {
        BindTrackingItem child1 = new BindTrackingItem(1);
        BindTrackingItem child2 = new BindTrackingItem(2);
        ModelGroup group = new ModelGroup(100, child1, child2);

        ModelGroupHolder holder = new ModelGroupHolder();
        group.bind(holder);
        group.unbind(holder);

        assertTrue(child1.unbound);
        assertTrue(child2.unbound);
        assertEquals(0, holder.getChildCount());
    }

    @Test
    public void testBindWithPreviousModelIncrementalBinding() {
        BindTrackingItem child1 = new BindTrackingItem(1);
        BindTrackingItem child2 = new BindTrackingItem(2);
        ModelGroup previousGroup = new ModelGroup(100,
                new BindTrackingItem(1), new BindTrackingItem(2));

        ModelGroup newGroup = new ModelGroup(100, child1, child2);

        ModelGroupHolder holder = new ModelGroupHolder();
        previousGroup.bind(holder);
        newGroup.bind(holder, previousGroup);

        assertTrue(child1.boundWithPrevious);
        assertTrue(child2.boundWithPrevious);
    }

    @Test
    public void testBindWithPreviousFreshBindForMismatchedIds() {
        BindTrackingItem child1 = new BindTrackingItem(1);
        ModelGroup previousGroup = new ModelGroup(100, new BindTrackingItem(99));
        ModelGroup newGroup = new ModelGroup(100, child1);

        ModelGroupHolder holder = new ModelGroupHolder();
        previousGroup.bind(holder);
        newGroup.bind(holder, previousGroup);

        assertTrue(child1.bound);
        assertFalse(child1.boundWithPrevious);
    }

    @Test
    public void testBindWithMoreChildrenThanPrevious() {
        BindTrackingItem child1 = new BindTrackingItem(1);
        BindTrackingItem child2 = new BindTrackingItem(2);
        BindTrackingItem child3 = new BindTrackingItem(3);
        ModelGroup previousGroup = new ModelGroup(100,
                new BindTrackingItem(1));
        ModelGroup newGroup = new ModelGroup(100, child1, child2, child3);

        ModelGroupHolder holder = new ModelGroupHolder();
        previousGroup.bind(holder);
        assertEquals(1, holder.getChildCount());

        newGroup.bind(holder, previousGroup);
        assertEquals(3, holder.getChildCount());
        assertTrue(child1.boundWithPrevious);
        assertTrue(child2.bound);
        assertTrue(child3.bound);
    }

    @Test
    public void testBindWithFewerChildrenThanPrevious() {
        BindTrackingItem child1 = new BindTrackingItem(1);
        ModelGroup previousGroup = new ModelGroup(100,
                new BindTrackingItem(1), new BindTrackingItem(2), new BindTrackingItem(3));
        ModelGroup newGroup = new ModelGroup(100, child1);

        ModelGroupHolder holder = new ModelGroupHolder();
        previousGroup.bind(holder);
        assertEquals(3, holder.getChildCount());

        newGroup.bind(holder, previousGroup);
        assertEquals(1, holder.getChildCount());
        assertTrue(child1.boundWithPrevious);
    }

    @Test
    public void testEqualsIncludesChildren() {
        ModelGroup group1 = new ModelGroup(100,
                new TestItem(1, 10), new TestItem(2, 20));
        ModelGroup group2 = new ModelGroup(100,
                new TestItem(1, 10), new TestItem(2, 20));

        assertEquals(group1, group2);
    }

    @Test
    public void testNotEqualsWhenChildDiffers() {
        ModelGroup group1 = new ModelGroup(100,
                new TestItem(1, 10), new TestItem(2, 20));
        ModelGroup group2 = new ModelGroup(100,
                new TestItem(1, 10), new TestItem(2, 99));

        assertNotEquals(group1, group2);
    }

    @Test
    public void testNotEqualsWhenChildCountDiffers() {
        ModelGroup group1 = new ModelGroup(100, new TestItem(1, 10));
        ModelGroup group2 = new ModelGroup(100,
                new TestItem(1, 10), new TestItem(2, 20));

        assertNotEquals(group1, group2);
    }

    @Test
    public void testHashCodeIncludesChildren() {
        ModelGroup group1 = new ModelGroup(100,
                new TestItem(1, 10), new TestItem(2, 20));
        ModelGroup group2 = new ModelGroup(100,
                new TestItem(1, 10), new TestItem(2, 20));

        assertEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    public void testHashCodeDiffersWhenChildDiffers() {
        ModelGroup group1 = new ModelGroup(100,
                new TestItem(1, 10));
        ModelGroup group2 = new ModelGroup(100,
                new TestItem(1, 99));

        assertNotEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    public void testSpanSizeDelegatesToFirstChild() {
        SpannedItem child = new SpannedItem(1, 3);
        ModelGroup group = new ModelGroup(100, child, new TestItem(2));

        assertEquals(3, group.getSpanSize(12, 0, 1));
    }

    @Test
    public void testShouldSaveViewStateTrueWhenChildHasIt() {
        StatefulItem child = new StatefulItem(1);
        ModelGroup group = new ModelGroup(100, new TestItem(2), child);

        assertTrue(group.shouldSaveViewState());
    }

    @Test
    public void testShouldSaveViewStateFalseWhenNoChildHasIt() {
        ModelGroup group = new ModelGroup(100, new TestItem(1), new TestItem(2));

        assertFalse(group.shouldSaveViewState());
    }

    @Test
    public void testCreateViewReturnsModelGroupHolder() {
        ModelGroup group = new ModelGroup(100, new TestItem(1));
        Object view = group.createView();

        assertTrue(view instanceof ModelGroupHolder);
    }

    @Test
    public void testItemModelCreateViewReturnsObject() {
        TestItem item = new TestItem(1);
        Object view = item.createView();

        assertNotNull(view);
    }

    @Test
    public void testModelGroupHolderChildCount() {
        ModelGroup group = new ModelGroup(100,
                new TestItem(1), new TestItem(2), new TestItem(3));

        ModelGroupHolder holder = new ModelGroupHolder();
        group.bind(holder);

        assertEquals(3, holder.getChildCount());
    }

    @Test
    public void testModelGroupHolderGetChildHolder() {
        ModelGroup group = new ModelGroup(100, new TestItem(1));

        ModelGroupHolder holder = new ModelGroupHolder();
        group.bind(holder);

        ListItemHolder childHolder = holder.getChildHolder(0);
        assertNotNull(childHolder);
        assertNotNull(childHolder.getModel());
    }

    @Test
    public void testHolderReusesOnSameViewType() {
        TestItem child1 = new TestItem(1, 10);
        TestItem child2 = new TestItem(1, 20);
        ModelGroup group1 = new ModelGroup(100, child1);
        ModelGroup group2 = new ModelGroup(100, child2);

        ModelGroupHolder holder = new ModelGroupHolder();
        group1.bind(holder);
        ListItemHolder firstChildHolder = holder.getChildHolder(0);

        group2.bind(holder);
        ListItemHolder secondChildHolder = holder.getChildHolder(0);

        assertSame(firstChildHolder, secondChildHolder);
    }

    @Test
    public void testHolderRecyclesExcess() {
        ModelGroup group1 = new ModelGroup(100,
                new TestItem(1), new TestItem(2), new TestItem(3));
        ModelGroup group2 = new ModelGroup(100, new TestItem(1));

        ModelGroupHolder holder = new ModelGroupHolder();
        group1.bind(holder);
        assertEquals(3, holder.getChildCount());

        group2.bind(holder);
        assertEquals(1, holder.getChildCount());
    }

    @Test
    public void testGroupWithControllerDiffing() {
        AtomicBoolean includeExtra = new AtomicBoolean(false);
        java.util.List<String> events = new java.util.ArrayList<>();

        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                TestItem child1 = new TestItem(1, 10);
                if (includeExtra.get()) {
                    TestItem child2 = new TestItem(2, 20);
                    new ModelGroup(100, child1, child2).id(100).addTo(this);
                } else {
                    new ModelGroup(100, child1).id(100).addTo(this);
                }
            }
        };

        controller.setObserver(new ListChangeObserver() {
            @Override
            public void onItemsInserted(int positionStart, int count) {
                events.add("INSERT");
            }

            @Override
            public void onItemsRemoved(int positionStart, int count) {
                events.add("REMOVE");
            }

            @Override
            public void onItemMoved(int from, int to) {
                events.add("MOVE");
            }

            @Override
            public void onItemsChanged(int positionStart, int count, Object payload) {
                events.add("CHANGE");
            }
        });

        controller.requestModelBuild();
        events.clear();

        includeExtra.set(true);
        controller.requestModelBuild();

        assertTrue(events.contains("CHANGE"));
    }

    @Test
    public void testGroupReportsNoChangeWhenIdentical() {
        java.util.List<String> events = new java.util.ArrayList<>();

        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new ModelGroup(100,
                        new TestItem(1, 10),
                        new TestItem(2, 20)).id(100).addTo(this);
            }
        };

        controller.setObserver(new ListChangeObserver() {
            @Override
            public void onItemsInserted(int positionStart, int count) {
                events.add("INSERT");
            }

            @Override
            public void onItemsRemoved(int positionStart, int count) {
                events.add("REMOVE");
            }

            @Override
            public void onItemMoved(int from, int to) {
                events.add("MOVE");
            }

            @Override
            public void onItemsChanged(int positionStart, int count, Object payload) {
                events.add("CHANGE");
            }
        });

        controller.requestModelBuild();
        events.clear();

        controller.requestModelBuild();

        assertFalse(events.contains("CHANGE"));
    }

    @Test
    public void testChildVisibilityTracked() {
        TestItem visibleChild = new TestItem(1);
        TestItem hiddenChild = new TestItem(2);
        hiddenChild.hide();

        assertFalse(hiddenChild.isVisible());
        assertTrue(visibleChild.isVisible());
    }

    @Test
    public void testUnbindGroupClearsState() {
        ModelGroup group = new ModelGroup(100, new TestItem(1));
        ModelGroupHolder holder = new ModelGroupHolder();
        group.bind(holder);
        assertEquals(1, holder.getChildCount());

        holder.unbindGroup();
        assertEquals(0, holder.getChildCount());
        assertNull(holder.getBoundGroup());
    }

    private static class BindTrackingItem extends ItemModel<Object> {
        boolean bound;
        boolean unbound;
        boolean boundWithPrevious;

        BindTrackingItem(long id) { id(id); }

        @Override
        public int getDefaultLayoutId() { return 0; }

        @Override
        public void bind(Object view) { bound = true; }

        @Override
        public void bind(Object view, ItemModel<?> previousModel) {
            boundWithPrevious = true;
        }

        @Override
        public void unbind(Object view) { unbound = true; }
    }

    private static class SpannedItem extends ItemModel<Object> {
        private final int spanSize;

        SpannedItem(long id, int spanSize) {
            id(id);
            this.spanSize = spanSize;
        }

        @Override
        public int getDefaultLayoutId() { return 0; }

        @Override
        public int getSpanSize(int totalSpanCount, int position, int itemCount) {
            return spanSize;
        }
    }

    private static class StatefulItem extends ItemModel<Object> {
        StatefulItem(long id) { id(id); }

        @Override
        public int getDefaultLayoutId() { return 0; }

        @Override
        public boolean shouldSaveViewState() { return true; }
    }
}
