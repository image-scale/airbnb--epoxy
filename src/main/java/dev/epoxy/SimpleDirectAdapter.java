package dev.epoxy;

import java.util.Collection;
import java.util.List;

public class SimpleDirectAdapter extends DirectAdapter {

    @Override
    public void addModel(ItemModel<?> model) {
        super.addModel(model);
    }

    @Override
    public void addModels(ItemModel<?>... models) {
        super.addModels(models);
    }

    @Override
    public void addModels(Collection<? extends ItemModel<?>> models) {
        super.addModels(models);
    }

    @Override
    public void insertModelBefore(ItemModel<?> modelToInsert, ItemModel<?> modelToInsertBefore) {
        super.insertModelBefore(modelToInsert, modelToInsertBefore);
    }

    @Override
    public void insertModelAfter(ItemModel<?> modelToInsert, ItemModel<?> modelToInsertAfter) {
        super.insertModelAfter(modelToInsert, modelToInsertAfter);
    }

    @Override
    public void removeModel(ItemModel<?> model) {
        super.removeModel(model);
    }

    @Override
    public void removeAllModels() {
        super.removeAllModels();
    }

    @Override
    public void removeAllAfterModel(ItemModel<?> model) {
        super.removeAllAfterModel(model);
    }

    @Override
    public void showModel(ItemModel<?> model, boolean show) {
        super.showModel(model, show);
    }

    @Override
    public void showModel(ItemModel<?> model) {
        super.showModel(model);
    }

    @Override
    public void hideModel(ItemModel<?> model) {
        super.hideModel(model);
    }

    @Override
    public void notifyModelChanged(ItemModel<?> model) {
        super.notifyModelChanged(model);
    }

    public List<ItemModel<?>> getModels() {
        return models;
    }
}
