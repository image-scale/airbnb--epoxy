package dev.epoxy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseListAdapter {

    private final ViewTypeRegistry viewTypeRegistry = new ViewTypeRegistry();
    private final BoundHolderTracker boundHolders = new BoundHolderTracker();
    private final HolderState holderState = new HolderState();
    private ListChangeObserver observer;

    abstract List<? extends ItemModel<?>> getCurrentModels();

    public int getItemCount() {
        return getCurrentModels().size();
    }

    public long getItemId(int position) {
        return getCurrentModels().get(position).id();
    }

    public int getItemViewType(int position) {
        return viewTypeRegistry.getViewTypeAndRemember(getModelForPosition(position));
    }

    public ItemModel<?> getModelForPosition(int position) {
        return getCurrentModels().get(position);
    }

    public ListItemHolder createHolder(int viewType) {
        return new ListItemHolder(new Object(), viewType, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void bindHolder(ListItemHolder holder, int position) {
        bindHolder(holder, position, (ItemModel<?>) null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void bindHolder(ListItemHolder holder, int position, ItemModel<?> previousModel) {
        ItemModel model = getModelForPosition(position);
        holder.bind(model, previousModel, position);
        boundHolders.put(holder);

        if (previousModel == null) {
            holderState.restore(holder);
        }
    }

    public void unbindHolder(ListItemHolder holder) {
        holderState.save(holder);
        boundHolders.remove(holder);
        holder.unbind();
    }

    public void setObserver(ListChangeObserver observer) {
        this.observer = observer;
    }

    public ListChangeObserver getObserver() {
        return observer;
    }

    public Map<Long, Object> saveState() {
        for (ListItemHolder holder : boundHolders) {
            holderState.save(holder);
        }
        return holderState.getAll();
    }

    public void restoreState(Map<Long, Object> state) {
        if (state != null) {
            holderState.putAll(state);
        }
    }

    public BoundHolderTracker getBoundHolders() {
        return boundHolders;
    }

    public ViewTypeRegistry getViewTypeRegistry() {
        return viewTypeRegistry;
    }

    protected void dispatchInserted(int positionStart, int count) {
        if (observer != null) {
            observer.onItemsInserted(positionStart, count);
        }
    }

    protected void dispatchRemoved(int positionStart, int count) {
        if (observer != null) {
            observer.onItemsRemoved(positionStart, count);
        }
    }

    protected void dispatchMoved(int fromPosition, int toPosition) {
        if (observer != null) {
            observer.onItemMoved(fromPosition, toPosition);
        }
    }

    protected void dispatchChanged(int positionStart, int count, Object payload) {
        if (observer != null) {
            observer.onItemsChanged(positionStart, count, payload);
        }
    }

    protected void dispatchOperations(List<ChangeOperation> operations) {
        if (observer == null) return;
        for (ChangeOperation op : operations) {
            switch (op.type) {
                case ChangeOperation.ADD ->
                    observer.onItemsInserted(op.positionStart, op.itemCount);
                case ChangeOperation.REMOVE ->
                    observer.onItemsRemoved(op.positionStart, op.itemCount);
                case ChangeOperation.MOVE ->
                    observer.onItemMoved(op.positionStart, op.itemCount);
                case ChangeOperation.UPDATE ->
                    observer.onItemsChanged(op.positionStart, op.itemCount, op.payloads);
            }
        }
    }
}
