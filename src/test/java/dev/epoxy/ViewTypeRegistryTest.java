package dev.epoxy;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class ViewTypeRegistryTest {

    @After
    public void tearDown() {
        ViewTypeRegistry.resetForTesting();
    }

    @Test
    public void testLayoutBasedViewType() {
        ViewTypeRegistry registry = new ViewTypeRegistry();
        ItemModel<?> model = new LayoutTestItem(1, 42);
        assertEquals(42, registry.getViewType(model));
    }

    @Test
    public void testAutoAssignsNegativeViewType() {
        ViewTypeRegistry registry = new ViewTypeRegistry();
        ItemModel<?> model = new TestItem(1);
        int viewType = registry.getViewType(model);
        assertTrue("Auto view type should be negative", viewType < 0);
    }

    @Test
    public void testReusesViewTypeForSameClass() {
        ViewTypeRegistry registry = new ViewTypeRegistry();
        TestItem a = new TestItem(1);
        TestItem b = new TestItem(2);
        int typeA = registry.getViewType(a);
        int typeB = registry.getViewType(b);
        assertEquals(typeA, typeB);
    }

    @Test
    public void testDifferentClassesGetDifferentAutoTypes() {
        ViewTypeRegistry registry = new ViewTypeRegistry();
        TestItem a = new TestItem(1);
        LayoutTestItem b = new LayoutTestItem(2, 0);
        int typeA = registry.getViewType(a);
        int typeB = registry.getViewType(b);
        assertNotEquals(typeA, typeB);
    }

    @Test
    public void testSharedAcrossRegistries() {
        ViewTypeRegistry r1 = new ViewTypeRegistry();
        ViewTypeRegistry r2 = new ViewTypeRegistry();
        TestItem m1 = new TestItem(1);
        int type1 = r1.getViewType(m1);
        TestItem m2 = new TestItem(2);
        int type2 = r2.getViewType(m2);
        assertEquals(type1, type2);
    }

    @Test
    public void testRememberModel() {
        ViewTypeRegistry registry = new ViewTypeRegistry();
        TestItem model = new TestItem(1);
        registry.getViewTypeAndRemember(model);
        assertSame(model, registry.getLastRememberedModel());
    }

    @Test
    public void testClearRemembered() {
        ViewTypeRegistry registry = new ViewTypeRegistry();
        TestItem model = new TestItem(1);
        registry.getViewTypeAndRemember(model);
        registry.clearLastRemembered();
        assertNull(registry.getLastRememberedModel());
    }

    @Test
    public void testGetViewTypeForClass() {
        ViewTypeRegistry registry = new ViewTypeRegistry();
        TestItem model = new TestItem(1);
        int viewType = registry.getViewType(model);
        assertEquals(Integer.valueOf(viewType), ViewTypeRegistry.getViewTypeForClass(TestItem.class));
    }

    @Test
    public void testLayoutIdUsedDirectly() {
        ViewTypeRegistry registry = new ViewTypeRegistry();
        LayoutTestItem model = new LayoutTestItem(1, 100);
        assertEquals(100, registry.getViewType(model));
        LayoutTestItem model2 = new LayoutTestItem(2, 100);
        assertEquals(100, registry.getViewType(model2));
    }

    private static class LayoutTestItem extends ItemModel<Object> {
        private final int layoutId;

        LayoutTestItem(long id, int layoutId) {
            id(id);
            this.layoutId = layoutId;
        }

        @Override
        public int getDefaultLayoutId() {
            return layoutId;
        }
    }
}
