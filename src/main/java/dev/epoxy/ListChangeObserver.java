package dev.epoxy;

public interface ListChangeObserver {

    void onItemsInserted(int positionStart, int count);

    void onItemsRemoved(int positionStart, int count);

    void onItemMoved(int fromPosition, int toPosition);

    void onItemsChanged(int positionStart, int count, Object payload);
}
