package dev.epoxy;

public class PlaceholderModel extends ItemModel<Object> {

    private static final int PLACEHOLDER_VIEW_TYPE = Integer.MIN_VALUE;

    public PlaceholderModel() {
        id(Long.MIN_VALUE / 2);
    }

    @Override
    public int getDefaultLayoutId() {
        return 0;
    }

    @Override
    public int getViewType() {
        return PLACEHOLDER_VIEW_TYPE;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 0;
    }

    @Override
    public void bind(Object view) {
    }

    @Override
    public void unbind(Object view) {
    }

    @Override
    public boolean shouldSaveViewState() {
        return false;
    }
}
