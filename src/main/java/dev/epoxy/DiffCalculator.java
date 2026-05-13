package dev.epoxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiffCalculator {

    public static List<ChangeOperation> computeDiff(
            List<? extends ItemModel<?>> oldModels,
            List<? extends ItemModel<?>> newModels) {

        List<ItemSnapshot> oldStates = new ArrayList<>(oldModels.size());
        Map<Long, ItemSnapshot> oldMap = new HashMap<>();
        for (int i = 0; i < oldModels.size(); i++) {
            ItemSnapshot s = ItemSnapshot.create(oldModels.get(i), i);
            oldStates.add(s);
            if (oldMap.put(s.id, s) != null) {
                throw new IllegalStateException("Duplicate ID in old list: " + s.id);
            }
        }

        List<ItemSnapshot> newStates = new ArrayList<>(newModels.size());
        Map<Long, ItemSnapshot> newMap = new HashMap<>();
        for (int i = 0; i < newModels.size(); i++) {
            ItemSnapshot s = ItemSnapshot.create(newModels.get(i), i);
            newStates.add(s);
            if (newMap.put(s.id, s) != null) {
                throw new IllegalStateException("Duplicate ID in new list: " + s.id);
            }
        }

        for (ItemSnapshot oldS : oldStates) {
            ItemSnapshot newS = newMap.get(oldS.id);
            if (newS != null) {
                oldS.pair = newS;
                newS.pair = oldS;
            }
        }

        OperationCollector collector = new OperationCollector();

        List<Long> working = new ArrayList<>(oldModels.size());
        for (ItemSnapshot s : oldStates) {
            working.add(s.id);
        }

        collectRemovals(oldStates, working, collector);
        collectInsertions(newStates, working, collector);
        collectMoves(newStates, working, collector);
        collectChanges(newStates, collector);

        return collector.getOperations();
    }

    private static void collectRemovals(List<ItemSnapshot> oldStates,
                                        List<Long> working,
                                        OperationCollector collector) {
        for (ItemSnapshot s : oldStates) {
            if (s.pair == null) {
                int pos = working.indexOf(s.id);
                collector.remove(pos);
                working.remove(pos);
            }
        }
    }

    private static void collectInsertions(List<ItemSnapshot> newStates,
                                          List<Long> working,
                                          OperationCollector collector) {
        for (int i = 0; i < newStates.size(); i++) {
            ItemSnapshot s = newStates.get(i);
            if (s.pair == null) {
                collector.add(i);
                working.add(i, s.id);
            }
        }
    }

    private static void collectMoves(List<ItemSnapshot> newStates,
                                     List<Long> working,
                                     OperationCollector collector) {
        for (int i = 0; i < newStates.size(); i++) {
            long targetId = newStates.get(i).id;
            if (!working.get(i).equals(targetId)) {
                int currentPos = working.indexOf(targetId);
                if (currentPos >= 0 && currentPos != i) {
                    collector.move(currentPos, i);
                    working.remove(currentPos);
                    working.add(i, targetId);
                }
            }
        }
    }

    private static void collectChanges(List<ItemSnapshot> newStates,
                                       OperationCollector collector) {
        for (int i = 0; i < newStates.size(); i++) {
            ItemSnapshot s = newStates.get(i);
            if (s.pair != null && s.contentHashCode != s.pair.contentHashCode) {
                collector.update(i, s.pair.model);
            }
        }
    }
}
