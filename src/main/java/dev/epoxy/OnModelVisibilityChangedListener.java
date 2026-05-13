package dev.epoxy;

public interface OnModelVisibilityChangedListener<T extends ItemModel<V>, V> {

    void onVisibilityChanged(T model, V view,
                             float percentVisibleHeight, float percentVisibleWidth,
                             int heightVisible, int widthVisible);
}
