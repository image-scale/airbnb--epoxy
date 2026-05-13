package dev.epoxy;

import java.util.Collection;

public class FrozenModelList extends TrackedModelList {

    private boolean frozen;

    public FrozenModelList() {
        super();
    }

    public FrozenModelList(int initialCapacity) {
        super(initialCapacity);
    }

    public void freeze() {
        frozen = true;
    }

    public boolean isFrozen() {
        return frozen;
    }

    private void checkFrozen() {
        if (frozen) {
            throw new IllegalStateException(
                "Cannot modify a frozen model list. "
                + "This list has been submitted for diffing and cannot be changed.");
        }
    }

    @Override
    public boolean add(ItemModel<?> element) {
        checkFrozen();
        return super.add(element);
    }

    @Override
    public void add(int index, ItemModel<?> element) {
        checkFrozen();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends ItemModel<?>> c) {
        checkFrozen();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends ItemModel<?>> c) {
        checkFrozen();
        return super.addAll(index, c);
    }

    @Override
    public ItemModel<?> remove(int index) {
        checkFrozen();
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        checkFrozen();
        return super.remove(o);
    }

    @Override
    public ItemModel<?> set(int index, ItemModel<?> element) {
        checkFrozen();
        return super.set(index, element);
    }

    @Override
    public void clear() {
        checkFrozen();
        super.clear();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkFrozen();
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        checkFrozen();
        return super.retainAll(c);
    }
}
