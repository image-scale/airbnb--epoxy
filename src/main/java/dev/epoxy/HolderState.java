package dev.epoxy;

import java.util.HashMap;
import java.util.Map;

public class HolderState {

    private final Map<Long, Object> states = new HashMap<>();

    public void save(ListItemHolder holder) {
        if (holder.getModel() == null) return;
        if (!holder.getModel().shouldSaveViewState()) return;
        Object state = holder.getState();
        if (state != null) {
            states.put(holder.getModelId(), state);
        }
    }

    public void restore(ListItemHolder holder) {
        if (holder.getModel() == null) return;
        if (!holder.getModel().shouldSaveViewState()) return;
        Object state = states.get(holder.getModelId());
        if (state != null) {
            holder.setState(state);
        }
    }

    public boolean hasState(long modelId) {
        return states.containsKey(modelId);
    }

    public Map<Long, Object> getAll() {
        return new HashMap<>(states);
    }

    public void putAll(Map<Long, Object> saved) {
        states.putAll(saved);
    }

    public void clear() {
        states.clear();
    }
}
