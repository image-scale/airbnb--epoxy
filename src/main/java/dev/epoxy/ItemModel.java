package dev.epoxy;

public abstract class ItemModel<T> {

    private static long idCounter = -1;

    private long modelId;
    private boolean hasDefaultId = true;
    private int layoutOverride;
    private boolean visible = true;
    boolean addedToAdapter;

    public ItemModel() {
        this.modelId = idCounter--;
    }

    public ItemModel(long id) {
        this.modelId = id;
        this.hasDefaultId = false;
    }

    public long id() {
        return modelId;
    }

    public ItemModel<T> id(long id) {
        if (addedToAdapter) {
            throw new InvalidUsageException(
                "Cannot change a model's ID after it has been added to an adapter.");
        }
        this.modelId = id;
        this.hasDefaultId = false;
        return this;
    }

    public ItemModel<T> id(long id1, long id2) {
        long combined = id1 * 31 + HashGenerator.hashLong(id2);
        return id(combined);
    }

    public ItemModel<T> id(Number... ids) {
        long result = 0;
        for (Number n : ids) {
            result = result * 31 + HashGenerator.hashLong(n.longValue());
        }
        return id(result);
    }

    public ItemModel<T> id(CharSequence key) {
        return id(HashGenerator.hashString(key));
    }

    public ItemModel<T> id(CharSequence key, long value) {
        long hash = HashGenerator.hashString(key) * 31 + HashGenerator.hashLong(value);
        return id(hash);
    }

    public boolean hasDefaultId() {
        return hasDefaultId;
    }

    public abstract int getDefaultLayoutId();

    public ItemModel<T> layout(int layoutId) {
        this.layoutOverride = layoutId;
        return this;
    }

    public int getLayoutId() {
        return layoutOverride != 0 ? layoutOverride : getDefaultLayoutId();
    }

    public int getViewType() {
        return getLayoutId();
    }

    public ItemModel<T> show() {
        this.visible = true;
        return this;
    }

    public ItemModel<T> show(boolean show) {
        this.visible = show;
        return this;
    }

    public ItemModel<T> hide() {
        this.visible = false;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public void bind(T view) {
    }

    public void bind(T view, ItemModel<?> previousModel) {
        bind(view);
    }

    public void unbind(T view) {
    }

    public void onVisibilityStateChanged(int visibilityState, T view) {
    }

    public void onVisibilityChanged(float percentHeight, float percentWidth,
                                    int heightPx, int widthPx, T view) {
    }

    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 1;
    }

    public boolean shouldSaveViewState() {
        return false;
    }

    public Object createView() {
        return new Object();
    }

    public void addTo(ListController controller) {
        controller.addInternal(this);
    }

    public void addIf(boolean condition, ListController controller) {
        if (condition) {
            addTo(controller);
        }
    }

    public ItemModel<T> reset() {
        this.layoutOverride = 0;
        this.visible = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemModel<?> that)) return false;
        return modelId == that.modelId
                && getViewType() == that.getViewType()
                && visible == that.visible;
    }

    @Override
    public int hashCode() {
        int result = (int) (modelId ^ (modelId >>> 32));
        result = result * 31 + getViewType();
        result = result * 31 + (visible ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + modelId + ", layout=" + getLayoutId() + "}";
    }
}
