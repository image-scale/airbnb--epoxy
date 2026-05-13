package dev.epoxy;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TrackedModelListTest {

    private TrackedModelList list;
    private RecordingObserver observer;

    @Before
    public void setUp() {
        list = new TrackedModelList();
        list.add(new TestItem(1));
        list.add(new TestItem(2));
        list.add(new TestItem(3));
        observer = new RecordingObserver();
        list.setObserver(observer);
    }

    @Test
    public void testAddFiresInsertion() {
        list.add(new TestItem(4));
        assertEquals(1, observer.events.size());
        assertEquals("INSERT(3,1)", observer.events.get(0));
    }

    @Test
    public void testAddAtIndexFiresInsertion() {
        list.add(1, new TestItem(4));
        assertEquals(1, observer.events.size());
        assertEquals("INSERT(1,1)", observer.events.get(0));
    }

    @Test
    public void testAddAllFiresInsertion() {
        list.addAll(Arrays.asList(new TestItem(4), new TestItem(5)));
        assertEquals(1, observer.events.size());
        assertEquals("INSERT(3,2)", observer.events.get(0));
    }

    @Test
    public void testAddAllAtIndexFiresInsertion() {
        list.addAll(1, Arrays.asList(new TestItem(4), new TestItem(5)));
        assertEquals(1, observer.events.size());
        assertEquals("INSERT(1,2)", observer.events.get(0));
    }

    @Test
    public void testRemoveByIndexFiresRemoval() {
        list.remove(1);
        assertEquals(1, observer.events.size());
        assertEquals("REMOVE(1,1)", observer.events.get(0));
    }

    @Test
    public void testRemoveByObjectFiresRemoval() {
        ItemModel<?> model = list.get(1);
        list.remove(model);
        assertEquals(1, observer.events.size());
        assertEquals("REMOVE(1,1)", observer.events.get(0));
    }

    @Test
    public void testRemoveNonExistentObjectNoNotification() {
        list.remove(new TestItem(999));
        assertEquals(0, observer.events.size());
    }

    @Test
    public void testSetDifferentIdFiresRemovalAndInsertion() {
        list.set(0, new TestItem(99));
        assertEquals(2, observer.events.size());
        assertEquals("REMOVE(0,1)", observer.events.get(0));
        assertEquals("INSERT(0,1)", observer.events.get(1));
    }

    @Test
    public void testSetSameIdNoNotification() {
        list.set(0, new TestItem(1));
        assertEquals(0, observer.events.size());
    }

    @Test
    public void testClearFiresRemoval() {
        list.clear();
        assertEquals(1, observer.events.size());
        assertEquals("REMOVE(0,3)", observer.events.get(0));
    }

    @Test
    public void testClearEmptyListNoNotification() {
        TrackedModelList emptyList = new TrackedModelList();
        emptyList.setObserver(observer);
        emptyList.clear();
        assertEquals(0, observer.events.size());
    }

    @Test
    public void testSubListClearFiresBatchRemoval() {
        list.subList(0, 2).clear();
        assertEquals(1, observer.events.size());
        assertEquals("REMOVE(0,2)", observer.events.get(0));
        assertEquals(1, list.size());
    }

    @Test
    public void testPauseResumeNotifications() {
        list.pauseNotifications();
        list.add(new TestItem(10));
        list.remove(0);
        assertEquals(0, observer.events.size());

        list.resumeNotifications();
        list.add(new TestItem(11));
        assertEquals(1, observer.events.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testDoublePauseThrows() {
        list.pauseNotifications();
        list.pauseNotifications();
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleResumeThrows() {
        list.resumeNotifications();
    }

    @Test
    public void testRemoveAllFiresNotifications() {
        List<ItemModel<?>> toRemove = new ArrayList<>();
        toRemove.add(list.get(0));
        toRemove.add(list.get(2));
        list.removeAll(toRemove);
        assertEquals(2, observer.events.size());
        assertEquals(1, list.size());
    }

    @Test
    public void testRetainAllFiresNotifications() {
        List<ItemModel<?>> toRetain = new ArrayList<>();
        toRetain.add(list.get(1));
        list.retainAll(toRetain);
        assertEquals(2, observer.events.size());
        assertEquals(1, list.size());
    }

    @Test
    public void testRemoveRangeFiresRemoval() {
        list.subList(1, 3).clear();
        assertEquals(1, observer.events.size());
        assertEquals("REMOVE(1,2)", observer.events.get(0));
    }

    private static class RecordingObserver implements TrackedModelList.ListObserver {
        final List<String> events = new ArrayList<>();

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            events.add("INSERT(" + positionStart + "," + itemCount + ")");
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            events.add("REMOVE(" + positionStart + "," + itemCount + ")");
        }
    }
}
