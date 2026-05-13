package dev.epoxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationCollector {

    private final List<ChangeOperation> operations = new ArrayList<>();
    private ChangeOperation lastOp;
    private int numInsertions;
    private int numRemovals;

    public void add(int position) {
        add(position, 1);
    }

    public void add(int position, int count) {
        if (lastOp != null && lastOp.type == ChangeOperation.ADD
                && position >= lastOp.positionStart
                && position <= lastOp.positionEnd()) {
            lastOp.itemCount += count;
            numInsertions += count;
            return;
        }
        ChangeOperation op = new ChangeOperation(ChangeOperation.ADD, position, count);
        operations.add(op);
        lastOp = op;
        numInsertions += count;
    }

    public void remove(int position) {
        remove(position, 1);
    }

    public void remove(int position, int count) {
        if (lastOp != null && lastOp.type == ChangeOperation.REMOVE) {
            if (position == lastOp.positionStart) {
                lastOp.itemCount += count;
                numRemovals += count;
                return;
            }
            if (position == lastOp.positionStart - count) {
                lastOp.positionStart = position;
                lastOp.itemCount += count;
                numRemovals += count;
                return;
            }
        }
        ChangeOperation op = new ChangeOperation(ChangeOperation.REMOVE, position, count);
        operations.add(op);
        lastOp = op;
        numRemovals += count;
    }

    public void update(int position, ItemModel<?> payload) {
        if (lastOp != null && lastOp.type == ChangeOperation.UPDATE) {
            if (position == lastOp.positionStart - 1) {
                lastOp.positionStart = position;
                lastOp.itemCount++;
                lastOp.addPayload(payload);
                return;
            }
            if (position == lastOp.positionEnd()) {
                lastOp.itemCount++;
                lastOp.addPayload(payload);
                return;
            }
            if (lastOp.contains(position)) {
                lastOp.addPayload(payload);
                return;
            }
        }
        ChangeOperation op = new ChangeOperation(ChangeOperation.UPDATE, position, 1);
        op.addPayload(payload);
        operations.add(op);
        lastOp = op;
    }

    public void move(int fromPosition, int toPosition) {
        ChangeOperation op = new ChangeOperation(ChangeOperation.MOVE, fromPosition, toPosition);
        operations.add(op);
        lastOp = null;
    }

    public List<ChangeOperation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    public int getNumInsertions() {
        return numInsertions;
    }

    public int getNumRemovals() {
        return numRemovals;
    }

    public int getNumMoves() {
        return (int) operations.stream()
                .filter(op -> op.type == ChangeOperation.MOVE)
                .count();
    }

    void reset() {
        operations.clear();
        lastOp = null;
        numInsertions = 0;
        numRemovals = 0;
    }
}
