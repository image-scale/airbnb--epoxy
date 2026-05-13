package dev.epoxy;

public interface OnModelVisibilityStateChangedListener<T extends ItemModel<V>, V> {

    void onVisibilityStateChanged(T model, V view, int visibilityState);
}
