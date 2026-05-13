package dev.epoxy;

import java.util.ArrayList;
import java.util.List;

public class ModelGroupHolder {

    private final List<ListItemHolder> childHolders = new ArrayList<>();
    private ModelGroup boundGroup;

    void bindGroupIfNeeded(ModelGroup group) {
        if (group == boundGroup) return;

        List<? extends ItemModel<?>> models = group.getChildModels();

        while (childHolders.size() > models.size()) {
            ListItemHolder removed = childHolders.remove(childHolders.size() - 1);
            removed.unbind();
        }

        ViewTypeRegistry registry = new ViewTypeRegistry();
        for (int i = 0; i < models.size(); i++) {
            ItemModel<?> model = models.get(i);
            int viewType = registry.getViewType(model);

            if (i < childHolders.size()) {
                if (childHolders.get(i).getViewType() != viewType) {
                    childHolders.get(i).unbind();
                    childHolders.set(i, createChildHolder(model, viewType));
                }
            } else {
                childHolders.add(createChildHolder(model, viewType));
            }
        }

        boundGroup = group;
    }

    void unbindGroup() {
        for (ListItemHolder holder : childHolders) {
            holder.unbind();
        }
        childHolders.clear();
        boundGroup = null;
    }

    public ListItemHolder getChildHolder(int index) {
        return childHolders.get(index);
    }

    public int getChildCount() {
        return childHolders.size();
    }

    ModelGroup getBoundGroup() {
        return boundGroup;
    }

    private ListItemHolder createChildHolder(ItemModel<?> model, int viewType) {
        Object view = model.createView();
        return new ListItemHolder(view, viewType, model.shouldSaveViewState());
    }
}
