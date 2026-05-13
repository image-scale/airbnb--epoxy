package dev.epoxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayloadWrapper {

    private final ItemModel<?> singleModel;
    private final Map<Long, ItemModel<?>> modelsById;

    public PayloadWrapper(ItemModel<?> model) {
        this.singleModel = model;
        this.modelsById = null;
    }

    public PayloadWrapper(List<? extends ItemModel<?>> models) {
        this.singleModel = null;
        this.modelsById = new HashMap<>();
        for (ItemModel<?> m : models) {
            modelsById.put(m.id(), m);
        }
    }

    public ItemModel<?> getModel(long modelId) {
        if (singleModel != null) {
            return singleModel.id() == modelId ? singleModel : null;
        }
        return modelsById != null ? modelsById.get(modelId) : null;
    }

    public static ItemModel<?> getModelFromPayloads(List<Object> payloads, long modelId) {
        if (payloads == null) return null;
        for (Object p : payloads) {
            if (p instanceof PayloadWrapper pw) {
                ItemModel<?> model = pw.getModel(modelId);
                if (model != null) return model;
            }
        }
        return null;
    }
}
