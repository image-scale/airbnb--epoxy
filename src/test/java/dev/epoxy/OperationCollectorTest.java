package dev.epoxy;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class OperationCollectorTest {

    @Test
    public void testSingleInsertion() {
        OperationCollector collector = new OperationCollector();
        collector.add(0);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(1, ops.size());
        assertEquals(ChangeOperation.ADD, ops.get(0).getType());
        assertEquals(0, ops.get(0).getPositionStart());
        assertEquals(1, ops.get(0).getItemCount());
    }

    @Test
    public void testInsertionBatch() {
        OperationCollector collector = new OperationCollector();
        collector.add(0);
        collector.add(1);
        collector.add(0);
        collector.add(1);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(1, ops.size());
        assertEquals(0, ops.get(0).getPositionStart());
        assertEquals(4, ops.get(0).getItemCount());
        assertEquals(4, collector.getNumInsertions());
    }

    @Test
    public void testInsertionMultipleBatches() {
        OperationCollector collector = new OperationCollector();
        collector.add(1);
        collector.add(3);
        collector.add(5);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(3, ops.size());
    }

    @Test
    public void testInsertionBatchRange() {
        OperationCollector collector = new OperationCollector();
        collector.add(0, 3);
        collector.add(3, 2);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(1, ops.size());
        assertEquals(0, ops.get(0).getPositionStart());
        assertEquals(5, ops.get(0).getItemCount());
    }

    @Test
    public void testSingleRemoval() {
        OperationCollector collector = new OperationCollector();
        collector.remove(2);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(1, ops.size());
        assertEquals(ChangeOperation.REMOVE, ops.get(0).getType());
        assertEquals(2, ops.get(0).getPositionStart());
        assertEquals(1, ops.get(0).getItemCount());
    }

    @Test
    public void testRemovalBatchSamePosition() {
        OperationCollector collector = new OperationCollector();
        collector.remove(3);
        collector.remove(3);
        collector.remove(3);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(1, ops.size());
        assertEquals(3, ops.get(0).getPositionStart());
        assertEquals(3, ops.get(0).getItemCount());
        assertEquals(3, collector.getNumRemovals());
    }

    @Test
    public void testRemovalBatchFromFront() {
        OperationCollector collector = new OperationCollector();
        collector.remove(3);
        collector.remove(2);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(1, ops.size());
        assertEquals(2, ops.get(0).getPositionStart());
        assertEquals(2, ops.get(0).getItemCount());
    }

    @Test
    public void testRemovalMultipleBatches() {
        OperationCollector collector = new OperationCollector();
        collector.remove(1);
        collector.remove(5);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(2, ops.size());
    }

    @Test
    public void testUpdateBatchAdjacent() {
        OperationCollector collector = new OperationCollector();
        TestItem m1 = new TestItem(1);
        TestItem m2 = new TestItem(2);
        TestItem m3 = new TestItem(3);
        collector.update(1, m1);
        collector.update(0, m2);
        collector.update(2, m3);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(1, ops.size());
        assertEquals(ChangeOperation.UPDATE, ops.get(0).getType());
        assertEquals(0, ops.get(0).getPositionStart());
        assertEquals(3, ops.get(0).getItemCount());
    }

    @Test
    public void testUpdateContainedPosition() {
        OperationCollector collector = new OperationCollector();
        TestItem m1 = new TestItem(1);
        TestItem m2 = new TestItem(2);
        collector.update(1, m1);
        collector.update(1, m2);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(1, ops.size());
        assertEquals(1, ops.get(0).getItemCount());
        assertEquals(2, ops.get(0).getPayloads().size());
    }

    @Test
    public void testUpdateMultipleBatches() {
        OperationCollector collector = new OperationCollector();
        collector.update(0, new TestItem(1));
        collector.update(5, new TestItem(2));
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(2, ops.size());
    }

    @Test
    public void testMovesNeverBatch() {
        OperationCollector collector = new OperationCollector();
        collector.move(0, 3);
        collector.move(1, 4);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(2, ops.size());
        assertEquals(ChangeOperation.MOVE, ops.get(0).getType());
        assertEquals(0, ops.get(0).getPositionStart());
        assertEquals(3, ops.get(0).getToPosition());
        assertEquals(ChangeOperation.MOVE, ops.get(1).getType());
        assertEquals(1, ops.get(1).getPositionStart());
        assertEquals(4, ops.get(1).getToPosition());
        assertEquals(2, collector.getNumMoves());
    }

    @Test
    public void testMoveBreaksBatching() {
        OperationCollector collector = new OperationCollector();
        collector.add(0);
        collector.move(2, 3);
        collector.add(0);
        List<ChangeOperation> ops = collector.getOperations();
        assertEquals(3, ops.size());
    }

    @Test
    public void testNumCounters() {
        OperationCollector collector = new OperationCollector();
        collector.add(0);
        collector.add(1);
        collector.remove(3);
        collector.move(2, 4);
        assertEquals(2, collector.getNumInsertions());
        assertEquals(1, collector.getNumRemovals());
        assertEquals(1, collector.getNumMoves());
    }

    @Test
    public void testReset() {
        OperationCollector collector = new OperationCollector();
        collector.add(0);
        collector.remove(1);
        collector.move(2, 3);
        collector.reset();
        assertEquals(0, collector.getOperations().size());
        assertEquals(0, collector.getNumInsertions());
        assertEquals(0, collector.getNumRemovals());
        assertEquals(0, collector.getNumMoves());
    }
}
