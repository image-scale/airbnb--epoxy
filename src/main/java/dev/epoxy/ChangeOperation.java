package dev.epoxy;

import java.util.ArrayList;
import java.util.List;

public class ChangeOperation {

    public static final int ADD = 0;
    public static final int REMOVE = 1;
    public static final int UPDATE = 2;
    public static final int MOVE = 3;

    int type;
    int positionStart;
    int itemCount;
    List<ItemModel<?>> payloads;

    ChangeOperation(int type, int positionStart, int itemCount) {
        this.type = type;
        this.positionStart = positionStart;
        this.itemCount = itemCount;
    }

    public int getType() {
        return type;
    }

    public int getPositionStart() {
        return positionStart;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getToPosition() {
        if (type != MOVE) {
            throw new IllegalStateException("getToPosition() only valid for MOVE operations");
        }
        return itemCount;
    }

    int positionEnd() {
        return positionStart + itemCount;
    }

    boolean contains(int position) {
        return position >= positionStart && position < positionEnd();
    }

    void addPayload(ItemModel<?> model) {
        if (payloads == null) {
            payloads = new ArrayList<>(1);
        }
        payloads.add(model);
    }

    public List<ItemModel<?>> getPayloads() {
        return payloads;
    }

    @Override
    public String toString() {
        String[] names = {"ADD", "REMOVE", "UPDATE", "MOVE"};
        if (type == MOVE) {
            return names[type] + "(" + positionStart + "->" + itemCount + ")";
        }
        return names[type] + "(" + positionStart + "," + itemCount + ")";
    }
}
