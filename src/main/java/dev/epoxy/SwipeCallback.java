package dev.epoxy;

public interface SwipeCallback<T extends ItemModel<?>> {

    void onSwipeStarted(T model, int adapterPosition);

    void onSwipeCompleted(T model, int position, int direction);

    void onSwipeReleased(T model);

    void clearView(T model);
}
