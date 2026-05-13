package dev.epoxy;

import java.util.Collections;
import java.util.List;

public class TouchHandler<T extends ItemModel<?>> {

    private final ListController controller;
    private final Class<T> targetModelClass;
    private final List<Class<? extends ItemModel<?>>> targetModelClasses;
    private final int dragDirections;
    private final int swipeDirections;
    private DragCallback<T> dragCallback;
    private SwipeCallback<T> swipeCallback;
    private T draggedModel;
    private T swipedModel;

    public TouchHandler(ListController controller, Class<T> targetModelClass,
                        int dragDirections, int swipeDirections) {
        this.controller = controller;
        this.targetModelClass = targetModelClass;
        this.targetModelClasses = null;
        this.dragDirections = dragDirections;
        this.swipeDirections = swipeDirections;
    }

    @SuppressWarnings("unchecked")
    public TouchHandler(ListController controller,
                        List<Class<? extends ItemModel<?>>> targetModelClasses,
                        int dragDirections, int swipeDirections) {
        this.controller = controller;
        this.targetModelClass = null;
        this.targetModelClasses = targetModelClasses;
        this.dragDirections = dragDirections;
        this.swipeDirections = swipeDirections;
    }

    public void setDragCallback(DragCallback<T> callback) {
        this.dragCallback = callback;
    }

    public void setSwipeCallback(SwipeCallback<T> callback) {
        this.swipeCallback = callback;
    }

    @SuppressWarnings("unchecked")
    public boolean isTouchableModel(ItemModel<?> model) {
        if (targetModelClass != null) {
            return targetModelClass.isInstance(model);
        }
        if (targetModelClasses != null) {
            for (Class<? extends ItemModel<?>> cls : targetModelClasses) {
                if (cls.isInstance(model)) return true;
            }
        }
        return false;
    }

    public boolean isDragEnabled(ItemModel<?> model) {
        if (!isTouchableModel(model)) return false;
        if (dragDirections == 0) return false;
        if (dragCallback instanceof TouchHelper.DragCallbacks<?> dc) {
            return dc.isDragEnabledForModel(model);
        }
        return true;
    }

    public boolean isSwipeEnabled(ItemModel<?> model) {
        if (!isTouchableModel(model)) return false;
        if (swipeDirections == 0) return false;
        if (swipeCallback instanceof TouchHelper.SwipeCallbacks<?> sc) {
            return sc.isSwipeEnabledForModel(model);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void handleDragStart(ItemModel<?> model, int position) {
        if (!isTouchableModel(model)) return;
        draggedModel = (T) model;
        if (dragCallback != null) {
            dragCallback.onDragStarted(draggedModel, position);
        }
    }

    @SuppressWarnings("unchecked")
    public void handleDragMove(int fromPosition, int toPosition) {
        if (controller != null) {
            controller.moveModel(fromPosition, toPosition);
        }
        if (dragCallback != null && draggedModel != null) {
            dragCallback.onModelMoved(fromPosition, toPosition, draggedModel);
        }
    }

    public void handleDragRelease() {
        if (dragCallback != null && draggedModel != null) {
            dragCallback.onDragReleased(draggedModel);
        }
    }

    public void handleDragClearView() {
        if (dragCallback != null && draggedModel != null) {
            dragCallback.clearView(draggedModel);
        }
        draggedModel = null;
    }

    @SuppressWarnings("unchecked")
    public void handleSwipeStart(ItemModel<?> model, int position) {
        if (!isTouchableModel(model)) return;
        swipedModel = (T) model;
        if (swipeCallback != null) {
            swipeCallback.onSwipeStarted(swipedModel, position);
        }
    }

    public void handleSwipeComplete(int position, int direction) {
        if (swipeCallback != null && swipedModel != null) {
            swipeCallback.onSwipeCompleted(swipedModel, position, direction);
        }
    }

    public void handleSwipeRelease() {
        if (swipeCallback != null && swipedModel != null) {
            swipeCallback.onSwipeReleased(swipedModel);
        }
    }

    public void handleSwipeClearView() {
        if (swipeCallback != null && swipedModel != null) {
            swipeCallback.clearView(swipedModel);
        }
        swipedModel = null;
    }

    public T getDraggedModel() {
        return draggedModel;
    }

    public T getSwipedModel() {
        return swipedModel;
    }

    public int getDragDirections() {
        return dragDirections;
    }

    public int getSwipeDirections() {
        return swipeDirections;
    }

    public ListController getController() {
        return controller;
    }
}
