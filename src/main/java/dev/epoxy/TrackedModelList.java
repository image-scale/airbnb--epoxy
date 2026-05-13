package dev.epoxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TrackedModelList extends ArrayList<ItemModel<?>> {

    public interface ListObserver {
        void onItemRangeInserted(int positionStart, int itemCount);
        void onItemRangeRemoved(int positionStart, int itemCount);
    }

    private ListObserver observer;
    private boolean notificationsPaused;

    public TrackedModelList() {
        super();
    }

    public TrackedModelList(int initialCapacity) {
        super(initialCapacity);
    }

    public void setObserver(ListObserver observer) {
        this.observer = observer;
    }

    public void pauseNotifications() {
        if (notificationsPaused) {
            throw new IllegalStateException("Notifications are already paused");
        }
        notificationsPaused = true;
    }

    public void resumeNotifications() {
        if (!notificationsPaused) {
            throw new IllegalStateException("Notifications are not paused");
        }
        notificationsPaused = false;
    }

    private void notifyInsertion(int position, int count) {
        if (!notificationsPaused && observer != null) {
            observer.onItemRangeInserted(position, count);
        }
    }

    private void notifyRemoval(int position, int count) {
        if (!notificationsPaused && observer != null) {
            observer.onItemRangeRemoved(position, count);
        }
    }

    void batchRemove(int fromIndex, int count) {
        for (int i = count - 1; i >= 0; i--) {
            super.remove(fromIndex + i);
        }
        notifyRemoval(fromIndex, count);
    }

    @Override
    public ItemModel<?> set(int index, ItemModel<?> element) {
        ItemModel<?> old = super.set(index, element);
        if (old.id() != element.id()) {
            notifyRemoval(index, 1);
            notifyInsertion(index, 1);
        }
        return old;
    }

    @Override
    public boolean add(ItemModel<?> element) {
        boolean result = super.add(element);
        notifyInsertion(size() - 1, 1);
        return result;
    }

    @Override
    public void add(int index, ItemModel<?> element) {
        super.add(index, element);
        notifyInsertion(index, 1);
    }

    @Override
    public boolean addAll(Collection<? extends ItemModel<?>> c) {
        int startIndex = size();
        boolean result = super.addAll(c);
        if (result) {
            notifyInsertion(startIndex, c.size());
        }
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends ItemModel<?>> c) {
        boolean result = super.addAll(index, c);
        if (result) {
            notifyInsertion(index, c.size());
        }
        return result;
    }

    @Override
    public ItemModel<?> remove(int index) {
        ItemModel<?> removed = super.remove(index);
        notifyRemoval(index, 1);
        return removed;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        int oldSize = size();
        if (oldSize > 0) {
            super.clear();
            notifyRemoval(0, oldSize);
        }
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) return;
        super.removeRange(fromIndex, toIndex);
        notifyRemoval(fromIndex, toIndex - fromIndex);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (int i = size() - 1; i >= 0; i--) {
            if (c.contains(get(i))) {
                remove(i);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (int i = size() - 1; i >= 0; i--) {
            if (!c.contains(get(i))) {
                remove(i);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public List<ItemModel<?>> subList(int fromIndex, int toIndex) {
        return new TrackedSubList(this, fromIndex, toIndex);
    }

    private static class TrackedSubList extends java.util.AbstractList<ItemModel<?>> {
        private final TrackedModelList parent;
        private final int offset;
        private int subSize;

        TrackedSubList(TrackedModelList parent, int from, int to) {
            this.parent = parent;
            this.offset = from;
            this.subSize = to - from;
        }

        @Override
        public ItemModel<?> get(int index) {
            Objects.checkIndex(index, subSize);
            return parent.get(offset + index);
        }

        @Override
        public int size() {
            return subSize;
        }

        @Override
        public ItemModel<?> remove(int index) {
            Objects.checkIndex(index, subSize);
            ItemModel<?> removed = parent.remove(offset + index);
            subSize--;
            return removed;
        }

        @Override
        public void clear() {
            if (subSize > 0) {
                parent.batchRemove(offset, subSize);
                subSize = 0;
            }
        }
    }
}
