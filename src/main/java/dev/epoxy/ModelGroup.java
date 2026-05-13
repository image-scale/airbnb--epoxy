package dev.epoxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ModelGroup extends ItemModel<ModelGroupHolder> {

    private final List<ItemModel<?>> models;
    private final int groupLayoutId;

    public ModelGroup(int layoutId, Collection<? extends ItemModel<?>> models) {
        if (models.isEmpty()) {
            throw new InvalidUsageException("A ModelGroup must have at least one model.");
        }
        this.groupLayoutId = layoutId;
        this.models = new ArrayList<>(models);
        id(this.models.get(0).id());
    }

    public ModelGroup(int layoutId, ItemModel<?>... models) {
        this(layoutId, Arrays.asList(models));
    }

    protected ModelGroup() {
        this.models = new ArrayList<>();
        this.groupLayoutId = 0;
    }

    protected ModelGroup(int layoutId) {
        this.models = new ArrayList<>();
        this.groupLayoutId = layoutId;
    }

    protected void addModel(ItemModel<?> model) {
        models.add(model);
    }

    @Override
    public int getDefaultLayoutId() {
        return groupLayoutId;
    }

    @Override
    public Object createView() {
        return new ModelGroupHolder();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void bind(ModelGroupHolder holder) {
        holder.bindGroupIfNeeded(this);
        for (int i = 0; i < models.size(); i++) {
            ItemModel model = models.get(i);
            ListItemHolder childHolder = holder.getChildHolder(i);
            childHolder.bind(model, null, i);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void bind(ModelGroupHolder holder, ItemModel<?> previousModel) {
        holder.bindGroupIfNeeded(this);
        ModelGroup previousGroup = previousModel instanceof ModelGroup
                ? (ModelGroup) previousModel : null;

        for (int i = 0; i < models.size(); i++) {
            ItemModel model = models.get(i);
            ListItemHolder childHolder = holder.getChildHolder(i);
            ItemModel previous = null;

            if (previousGroup != null && i < previousGroup.models.size()
                    && previousGroup.models.get(i).id() == model.id()) {
                previous = previousGroup.models.get(i);
            }

            childHolder.bind(model, previous, i);
        }
    }

    @Override
    public void unbind(ModelGroupHolder holder) {
        holder.unbindGroup();
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        if (!models.isEmpty()) {
            return models.get(0).getSpanSize(totalSpanCount, position, itemCount);
        }
        return super.getSpanSize(totalSpanCount, position, itemCount);
    }

    @Override
    public boolean shouldSaveViewState() {
        for (ItemModel<?> model : models) {
            if (model.shouldSaveViewState()) {
                return true;
            }
        }
        return false;
    }

    public List<ItemModel<?>> getChildModels() {
        return Collections.unmodifiableList(models);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelGroup that)) return false;
        if (!super.equals(o)) return false;
        return models.equals(that.models);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + models.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ModelGroup{id=" + id() + ", children=" + models.size() + "}";
    }
}
