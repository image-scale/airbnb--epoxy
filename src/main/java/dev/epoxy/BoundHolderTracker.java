package dev.epoxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BoundHolderTracker implements Iterable<ListItemHolder> {

    private final Map<Long, ListItemHolder> holders = new HashMap<>();

    public void put(ListItemHolder holder) {
        holders.put(holder.getModelId(), holder);
    }

    public void remove(ListItemHolder holder) {
        holders.remove(holder.getModelId());
    }

    public ListItemHolder get(long modelId) {
        return holders.get(modelId);
    }

    public ListItemHolder getByModel(ItemModel<?> model) {
        return holders.get(model.id());
    }

    public int size() {
        return holders.size();
    }

    @Override
    public Iterator<ListItemHolder> iterator() {
        return holders.values().iterator();
    }
}
