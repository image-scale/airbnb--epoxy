package dev.epoxy;

public class ListItemHolder {

    private Object view;
    private ItemModel<?> model;
    private long modelId = Long.MIN_VALUE;
    private final int viewType;
    private final boolean shouldSaveState;
    private Object state;

    public ListItemHolder(Object view, int viewType, boolean shouldSaveState) {
        this.view = view;
        this.viewType = viewType;
        this.shouldSaveState = shouldSaveState;
    }

    public ListItemHolder(Object view, int viewType) {
        this(view, viewType, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void bind(ItemModel model, ItemModel previousModel, int position) {
        this.model = model;
        this.modelId = model.id();
        if (previousModel != null) {
            model.bind(view, previousModel);
        } else {
            model.bind(view);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void unbind() {
        if (model != null) {
            ((ItemModel) model).unbind(view);
            model = null;
        }
    }

    public Object getView() {
        return view;
    }

    public ItemModel<?> getModel() {
        return model;
    }

    public long getModelId() {
        return modelId;
    }

    public int getViewType() {
        return viewType;
    }

    public boolean shouldSaveState() {
        return shouldSaveState;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }
}
