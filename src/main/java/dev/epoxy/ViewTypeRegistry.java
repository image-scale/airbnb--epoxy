package dev.epoxy;

import java.util.HashMap;
import java.util.Map;

public class ViewTypeRegistry {

    private static final Map<Class<?>, Integer> VIEW_TYPE_MAP = new HashMap<>();
    private static int nextAutoType = -1;

    private ItemModel<?> lastModelForLookup;

    public int getViewType(ItemModel<?> model) {
        int viewType = model.getViewType();
        if (viewType != 0) {
            VIEW_TYPE_MAP.putIfAbsent(model.getClass(), viewType);
            return viewType;
        }

        Integer cached = VIEW_TYPE_MAP.get(model.getClass());
        if (cached != null) {
            return cached;
        }

        int autoType = nextAutoType--;
        VIEW_TYPE_MAP.put(model.getClass(), autoType);
        return autoType;
    }

    public int getViewTypeAndRemember(ItemModel<?> model) {
        lastModelForLookup = model;
        return getViewType(model);
    }

    public ItemModel<?> getLastRememberedModel() {
        return lastModelForLookup;
    }

    public void clearLastRemembered() {
        lastModelForLookup = null;
    }

    public static Integer getViewTypeForClass(Class<?> modelClass) {
        return VIEW_TYPE_MAP.get(modelClass);
    }

    static void resetForTesting() {
        VIEW_TYPE_MAP.clear();
        nextAutoType = -1;
    }
}
