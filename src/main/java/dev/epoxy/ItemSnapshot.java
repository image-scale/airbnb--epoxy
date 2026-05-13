package dev.epoxy;

class ItemSnapshot {

    long id;
    int contentHashCode;
    int position;
    ItemModel<?> model;
    ItemSnapshot pair;

    static ItemSnapshot create(ItemModel<?> model, int position) {
        ItemSnapshot snapshot = new ItemSnapshot();
        snapshot.id = model.id();
        snapshot.contentHashCode = model.hashCode();
        snapshot.position = position;
        snapshot.model = model;
        return snapshot;
    }
}
