package dev.epoxy;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DiffCalculatorTest {

    @Test
    public void testNoChange() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = models(1, 2, 3);
        List<ChangeOperation> ops = DiffCalculator.computeDiff(old, neu);
        assertEquals(0, ops.size());
    }

    @Test
    public void testSimpleInsert() {
        List<TestItem> old = models(1, 2);
        List<TestItem> neu = models(1, 2, 3);
        List<ChangeOperation> ops = DiffCalculator.computeDiff(old, neu);
        validateDiff(old, neu, ops);
        assertTrue(hasOpType(ops, ChangeOperation.ADD));
    }

    @Test
    public void testInsertAtStart() {
        List<TestItem> old = models(2, 3);
        List<TestItem> neu = models(1, 2, 3);
        validateDiff(old, neu);
    }

    @Test
    public void testInsertAtMiddle() {
        List<TestItem> old = models(1, 3);
        List<TestItem> neu = models(1, 2, 3);
        validateDiff(old, neu);
    }

    @Test
    public void testInsertAtEnd() {
        List<TestItem> old = models(1, 2);
        List<TestItem> neu = models(1, 2, 3);
        validateDiff(old, neu);
    }

    @Test
    public void testMultipleInsertions() {
        List<TestItem> old = models(1);
        List<TestItem> neu = models(10, 1, 20, 30);
        validateDiff(old, neu);
    }

    @Test
    public void testSimpleRemoval() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = models(1, 3);
        List<ChangeOperation> ops = DiffCalculator.computeDiff(old, neu);
        validateDiff(old, neu, ops);
        assertTrue(hasOpType(ops, ChangeOperation.REMOVE));
    }

    @Test
    public void testRemoveFromStart() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = models(2, 3);
        validateDiff(old, neu);
    }

    @Test
    public void testRemoveFromEnd() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = models(1, 2);
        validateDiff(old, neu);
    }

    @Test
    public void testRemoveAll() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = models();
        validateDiff(old, neu);
    }

    @Test
    public void testMultipleRemovals() {
        List<TestItem> old = models(1, 2, 3, 4, 5);
        List<TestItem> neu = models(2, 4);
        validateDiff(old, neu);
    }

    @Test
    public void testSimpleMove() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = models(3, 1, 2);
        validateDiff(old, neu);
    }

    @Test
    public void testSwapEnds() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = models(3, 2, 1);
        validateDiff(old, neu);
    }

    @Test
    public void testMoveFrontToEnd() {
        List<TestItem> old = models(1, 2, 3, 4);
        List<TestItem> neu = models(2, 3, 4, 1);
        validateDiff(old, neu);
    }

    @Test
    public void testMoveEndToFront() {
        List<TestItem> old = models(1, 2, 3, 4);
        List<TestItem> neu = models(4, 1, 2, 3);
        validateDiff(old, neu);
    }

    @Test
    public void testReverse() {
        List<TestItem> old = models(1, 2, 3, 4, 5);
        List<TestItem> neu = models(5, 4, 3, 2, 1);
        validateDiff(old, neu);
    }

    @Test
    public void testSimpleContentUpdate() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = new ArrayList<>();
        neu.add(new TestItem(1, 0));
        neu.add(new TestItem(2, 999));
        neu.add(new TestItem(3, 0));
        List<ChangeOperation> ops = DiffCalculator.computeDiff(old, neu);
        assertTrue(hasOpType(ops, ChangeOperation.UPDATE));
        validateDiff(old, neu, ops);
    }

    @Test
    public void testCombinedInsertRemoveMove() {
        List<TestItem> old = models(1, 2, 3, 4, 5);
        List<TestItem> neu = models(10, 3, 1, 5, 20);
        validateDiff(old, neu);
    }

    @Test
    public void testFromEmptyToNonEmpty() {
        List<TestItem> old = models();
        List<TestItem> neu = models(1, 2, 3);
        validateDiff(old, neu);
    }

    @Test
    public void testSingleElement() {
        List<TestItem> old = models(1);
        List<TestItem> neu = models(2);
        validateDiff(old, neu);
    }

    @Test
    public void testCompleteReplacement() {
        List<TestItem> old = models(1, 2, 3);
        List<TestItem> neu = models(4, 5, 6);
        validateDiff(old, neu);
    }

    @Test
    public void testAllPermutationsSize3() {
        testPermutationsForSize(3);
    }

    @Test
    public void testAllPermutationsSize4() {
        testPermutationsForSize(4);
    }

    @Test
    public void testAllPermutationsSize5() {
        testPermutationsForSize(5);
    }

    @Test
    public void testRandomCombinations() {
        int nextId = 10000;
        for (int seed = 0; seed < 500; seed++) {
            Random rng = new Random(seed);
            int oldSize = rng.nextInt(8) + 1;

            List<TestItem> old = new ArrayList<>();
            for (int i = 0; i < oldSize; i++) {
                old.add(new TestItem(nextId++, rng.nextInt()));
            }

            List<TestItem> neu = new ArrayList<>();
            for (TestItem m : old) {
                neu.add(new TestItem(m.id(), m.value));
            }

            int removals = rng.nextInt(neu.size() / 2 + 1);
            for (int i = 0; i < removals && !neu.isEmpty(); i++) {
                neu.remove(rng.nextInt(neu.size()));
            }

            int additions = rng.nextInt(4);
            for (int i = 0; i < additions; i++) {
                int pos = neu.isEmpty() ? 0 : rng.nextInt(neu.size() + 1);
                neu.add(pos, new TestItem(nextId++, rng.nextInt()));
            }

            for (TestItem item : neu) {
                if (rng.nextBoolean()) {
                    item.value = rng.nextInt();
                }
            }

            if (neu.size() > 1) {
                Collections.shuffle(neu, rng);
            }

            validateDiff(old, neu,
                "Failed at seed=" + seed + " old=" + old + " new=" + neu);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testDuplicateIdInNewListThrows() {
        List<TestItem> old = models(1);
        List<TestItem> neu = new ArrayList<>();
        neu.add(new TestItem(1));
        neu.add(new TestItem(1));
        DiffCalculator.computeDiff(old, neu);
    }

    private void testPermutationsForSize(int size) {
        List<TestItem> original = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            original.add(new TestItem(i, 0));
        }
        for (List<TestItem> perm : permutations(original)) {
            validateDiff(original, perm,
                "Failed permutation for size " + size + ": " + perm);
        }
    }

    private static List<TestItem> models(long... ids) {
        List<TestItem> list = new ArrayList<>();
        for (long id : ids) {
            list.add(new TestItem(id, 0));
        }
        return list;
    }

    private void validateDiff(List<TestItem> oldList, List<TestItem> newList) {
        List<ChangeOperation> ops = DiffCalculator.computeDiff(oldList, newList);
        validateDiff(oldList, newList, ops);
    }

    private void validateDiff(List<TestItem> oldList, List<TestItem> newList, String message) {
        List<ChangeOperation> ops = DiffCalculator.computeDiff(oldList, newList);
        validateDiff(oldList, newList, ops, message);
    }

    private void validateDiff(List<TestItem> oldList, List<TestItem> newList,
                              List<ChangeOperation> ops) {
        validateDiff(oldList, newList, ops, "");
    }

    private void validateDiff(List<TestItem> oldList, List<TestItem> newList,
                              List<ChangeOperation> ops, String message) {
        Set<Long> oldIds = new HashSet<>();
        for (TestItem m : oldList) oldIds.add(m.id());

        List<Long> working = new ArrayList<>();
        for (TestItem m : oldList) working.add(m.id());

        for (ChangeOperation op : ops) {
            switch (op.type) {
                case ChangeOperation.REMOVE:
                    for (int i = 0; i < op.itemCount; i++) {
                        assertTrue(message + " Remove position out of bounds: " + op,
                            op.positionStart < working.size());
                        working.remove(op.positionStart);
                    }
                    break;
                case ChangeOperation.ADD:
                    for (int i = 0; i < op.itemCount; i++) {
                        working.add(op.positionStart + i, Long.MIN_VALUE);
                    }
                    break;
                case ChangeOperation.MOVE:
                    Long moved = working.remove(op.positionStart);
                    working.add(op.getToPosition(), moved);
                    break;
                case ChangeOperation.UPDATE:
                    break;
            }
        }

        assertEquals(message + " Size mismatch. Ops: " + ops,
            newList.size(), working.size());

        for (int i = 0; i < newList.size(); i++) {
            long expected = newList.get(i).id();
            long actual = working.get(i);
            if (actual == Long.MIN_VALUE) {
                assertFalse(message + " Inserted item at " + i
                    + " should not exist in old list (id=" + expected + ")",
                    oldIds.contains(expected));
            } else {
                assertEquals(message + " ID mismatch at position " + i,
                    expected, actual);
            }
        }
    }

    private boolean hasOpType(List<ChangeOperation> ops, int type) {
        return ops.stream().anyMatch(op -> op.type == type);
    }

    private static <T> List<List<T>> permutations(List<T> list) {
        if (list.isEmpty()) {
            return Collections.singletonList(Collections.emptyList());
        }
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            List<T> remaining = new ArrayList<>(list);
            remaining.remove(i);
            for (List<T> perm : permutations(remaining)) {
                List<T> newPerm = new ArrayList<>();
                newPerm.add(element);
                newPerm.addAll(perm);
                result.add(newPerm);
            }
        }
        return result;
    }
}
