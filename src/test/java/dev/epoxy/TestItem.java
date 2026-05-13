package dev.epoxy;

public class TestItem extends ItemModel<Object> {

    int value;
    boolean updated;

    public TestItem(long id, int value) {
        id(id);
        this.value = value;
    }

    public TestItem(long id) {
        this(id, 0);
    }

    @Override
    public int getDefaultLayoutId() {
        return 0;
    }

    public TestItem copy() {
        TestItem copy = new TestItem(id(), value);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestItem that)) return false;
        if (!super.equals(o)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + value;
        return result;
    }

    @Override
    public String toString() {
        return "TestItem{id=" + id() + ", val=" + value + "}";
    }
}
