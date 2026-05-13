package dev.epoxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class DirectAdapter extends BaseListAdapter {

    protected final List<ItemModel<?>> models = new ArrayList<>();
    private final PlaceholderModel hiddenModel = new PlaceholderModel();
    private List<ItemModel<?>> lastDiffSnapshot = new ArrayList<>();
    private boolean diffingEnabled;

    @Override
    List<? extends ItemModel<?>> getCurrentModels() {
        return models;
    }

    @Override
    public ItemModel<?> getModelForPosition(int position) {
        ItemModel<?> model = models.get(position);
        return model.isVisible() ? model : hiddenModel;
    }

    protected void addModel(ItemModel<?> model) {
        models.add(model);
        dispatchInserted(models.size() - 1, 1);
        syncDiffSnapshot();
    }

    protected void addModels(ItemModel<?>... modelsToAdd) {
        int startIndex = models.size();
        models.addAll(Arrays.asList(modelsToAdd));
        dispatchInserted(startIndex, modelsToAdd.length);
        syncDiffSnapshot();
    }

    protected void addModels(Collection<? extends ItemModel<?>> modelsToAdd) {
        int startIndex = models.size();
        models.addAll(modelsToAdd);
        dispatchInserted(startIndex, modelsToAdd.size());
        syncDiffSnapshot();
    }

    protected void insertModelBefore(ItemModel<?> modelToInsert, ItemModel<?> modelToInsertBefore) {
        int index = models.indexOf(modelToInsertBefore);
        if (index < 0) {
            throw new InvalidUsageException(
                "Model to insert before is not in the adapter: " + modelToInsertBefore);
        }
        models.add(index, modelToInsert);
        dispatchInserted(index, 1);
        syncDiffSnapshot();
    }

    protected void insertModelAfter(ItemModel<?> modelToInsert, ItemModel<?> modelToInsertAfter) {
        int index = models.indexOf(modelToInsertAfter);
        if (index < 0) {
            throw new InvalidUsageException(
                "Model to insert after is not in the adapter: " + modelToInsertAfter);
        }
        models.add(index + 1, modelToInsert);
        dispatchInserted(index + 1, 1);
        syncDiffSnapshot();
    }

    protected void removeModel(ItemModel<?> model) {
        int index = models.indexOf(model);
        if (index < 0) {
            throw new InvalidUsageException(
                "Model is not in the adapter: " + model);
        }
        models.remove(index);
        dispatchRemoved(index, 1);
        syncDiffSnapshot();
    }

    protected void removeAllModels() {
        int count = models.size();
        if (count > 0) {
            models.clear();
            dispatchRemoved(0, count);
            syncDiffSnapshot();
        }
    }

    protected void removeAllAfterModel(ItemModel<?> model) {
        int index = models.indexOf(model);
        if (index < 0) {
            throw new InvalidUsageException(
                "Model is not in the adapter: " + model);
        }
        int removeFrom = index + 1;
        int removeCount = models.size() - removeFrom;
        if (removeCount > 0) {
            models.subList(removeFrom, models.size()).clear();
            dispatchRemoved(removeFrom, removeCount);
            syncDiffSnapshot();
        }
    }

    protected void showModel(ItemModel<?> model, boolean show) {
        model.show(show);
        int index = models.indexOf(model);
        if (index >= 0) {
            dispatchChanged(index, 1, null);
        }
    }

    protected void showModel(ItemModel<?> model) {
        showModel(model, true);
    }

    protected void hideModel(ItemModel<?> model) {
        showModel(model, false);
    }

    public void notifyModelsChanged() {
        List<ChangeOperation> ops = DiffCalculator.computeDiff(lastDiffSnapshot, models);
        dispatchOperations(ops);
        syncDiffSnapshot();
    }

    protected void notifyModelChanged(ItemModel<?> model) {
        int index = models.indexOf(model);
        if (index >= 0) {
            dispatchChanged(index, 1, null);
        }
    }

    public int getModelPosition(ItemModel<?> model) {
        return models.indexOf(model);
    }

    private void syncDiffSnapshot() {
        lastDiffSnapshot = new ArrayList<>(models);
    }
}
