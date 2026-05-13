package dev.epoxy;

import java.util.List;

public final class ControllerAdapter extends BaseListAdapter implements ListChangeObserver {

    private final ListController controller;

    public ControllerAdapter(ListController controller) {
        this.controller = controller;
        controller.setObserver(this);
    }

    @Override
    List<? extends ItemModel<?>> getCurrentModels() {
        return controller.getCurrentModels();
    }

    public ListController getController() {
        return controller;
    }

    @Override
    public void onItemsInserted(int positionStart, int count) {
        dispatchInserted(positionStart, count);
    }

    @Override
    public void onItemsRemoved(int positionStart, int count) {
        dispatchRemoved(positionStart, count);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        dispatchMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemsChanged(int positionStart, int count, Object payload) {
        dispatchChanged(positionStart, count, payload);
    }

    public void requestModelBuild() {
        controller.requestModelBuild();
    }
}
