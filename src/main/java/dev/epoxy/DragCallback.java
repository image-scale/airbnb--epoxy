package dev.epoxy;

public interface DragCallback<T extends ItemModel<?>> {

    void onDragStarted(T model, int adapterPosition);

    void onModelMoved(int fromPosition, int toPosition, T modelBeingMoved);

    void onDragReleased(T model);

    void clearView(T model);
}
