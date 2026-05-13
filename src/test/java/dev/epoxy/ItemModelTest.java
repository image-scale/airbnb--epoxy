package dev.epoxy;

import org.junit.Test;

import static org.junit.Assert.*;

public class ItemModelTest {

    @Test
    public void testAutoDecrementedIds() {
        long prevId = Long.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            ItemModel<?> m = new ItemModel<Object>() {
                @Override
                public int getDefaultLayoutId() { return 0; }
            };
            assertTrue("ID should be negative", m.id() < 0);
            assertTrue("IDs should decrement", m.id() < prevId);
            prevId = m.id();
        }
    }

    @Test
    public void testExplicitIdViaLong() {
        TestItem m = new TestItem(1);
        m.id(42);
        assertEquals(42, m.id());
        assertFalse(m.hasDefaultId());
    }

    @Test
    public void testCharSequenceId() {
        TestItem m = new TestItem(1);
        m.id("test-key");
        long hash = HashGenerator.hashString("test-key");
        assertEquals(hash, m.id());
    }

    @Test
    public void testCompositeIdTwoLongs() {
        TestItem m1 = new TestItem(1);
        TestItem m2 = new TestItem(1);
        m1.id(10L, 20L);
        m2.id(20L, 10L);
        assertNotEquals(m1.id(), m2.id());
    }

    @Test
    public void testCompositeIdSameArgsSameResult() {
        TestItem m1 = new TestItem(1);
        TestItem m2 = new TestItem(1);
        m1.id(10L, 20L);
        m2.id(10L, 20L);
        assertEquals(m1.id(), m2.id());
    }

    @Test(expected = InvalidUsageException.class)
    public void testIdChangeAfterAddThrows() {
        TestItem m = new TestItem(42);
        m.addedToAdapter = true;
        m.id(99);
    }

    @Test
    public void testIdUnchangedAfterAddDoesNotThrow() {
        TestItem m = new TestItem(42);
        m.addedToAdapter = true;
        assertEquals(42, m.id());
    }

    @Test
    public void testEqualsWithSameIdAndValue() {
        TestItem m1 = new TestItem(42, 10);
        TestItem m2 = new TestItem(42, 10);
        assertEquals(m1, m2);
    }

    @Test
    public void testNotEqualsWithDifferentValues() {
        TestItem m1 = new TestItem(42, 10);
        TestItem m2 = new TestItem(42, 20);
        assertNotEquals(m1, m2);
    }

    @Test
    public void testNotEqualsWithDifferentIds() {
        TestItem m1 = new TestItem(1, 10);
        TestItem m2 = new TestItem(2, 10);
        assertNotEquals(m1, m2);
    }

    @Test
    public void testHashCodeDiffersWithDifferentValues() {
        TestItem m1 = new TestItem(42, 10);
        TestItem m2 = new TestItem(42, 20);
        assertNotEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    public void testShowHideToggle() {
        TestItem m = new TestItem(1);
        assertTrue(m.isVisible());

        m.hide();
        assertFalse(m.isVisible());

        m.show();
        assertTrue(m.isVisible());

        m.show(false);
        assertFalse(m.isVisible());

        m.show(true);
        assertTrue(m.isVisible());
    }

    @Test
    public void testVisibilityAffectsEquals() {
        TestItem m1 = new TestItem(42, 10);
        TestItem m2 = new TestItem(42, 10);
        assertEquals(m1, m2);

        m1.hide();
        assertNotEquals(m1, m2);
    }

    @Test
    public void testLayoutOverride() {
        TestItem m = new TestItem(1);
        assertEquals(0, m.getLayoutId());

        m.layout(42);
        assertEquals(42, m.getLayoutId());

        m.reset();
        assertEquals(0, m.getLayoutId());
    }

    @Test
    public void testDefaultIdFlag() {
        ItemModel<?> m = new ItemModel<Object>() {
            @Override
            public int getDefaultLayoutId() { return 0; }
        };
        assertTrue(m.hasDefaultId());

        m.id(42);
        assertFalse(m.hasDefaultId());
    }

    @Test
    public void testVarArgsId() {
        TestItem m1 = new TestItem(1);
        m1.id(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
        long id1 = m1.id();

        TestItem m2 = new TestItem(1);
        m2.id(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
        assertEquals(id1, m2.id());

        TestItem m3 = new TestItem(1);
        m3.id(Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(1));
        assertNotEquals(id1, m3.id());
    }

    @Test
    public void testCharSequenceWithLongId() {
        TestItem m1 = new TestItem(1);
        m1.id("prefix", 42L);
        TestItem m2 = new TestItem(1);
        m2.id("prefix", 42L);
        assertEquals(m1.id(), m2.id());

        TestItem m3 = new TestItem(1);
        m3.id("other", 42L);
        assertNotEquals(m1.id(), m3.id());
    }

    @Test
    public void testViewType() {
        TestItem m = new TestItem(1);
        assertEquals(0, m.getViewType());

        m.layout(99);
        assertEquals(99, m.getViewType());
    }

    @Test
    public void testGetSpanSizeDefault() {
        TestItem m = new TestItem(1);
        assertEquals(1, m.getSpanSize(4, 0, 10));
    }

    @Test
    public void testResetClearsLayoutAndVisibility() {
        TestItem m = new TestItem(1);
        m.layout(42);
        m.hide();
        assertFalse(m.isVisible());
        assertEquals(42, m.getLayoutId());

        m.reset();
        assertTrue(m.isVisible());
        assertEquals(0, m.getLayoutId());
    }
}
