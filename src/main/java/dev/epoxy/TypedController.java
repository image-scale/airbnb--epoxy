package dev.epoxy;

public abstract class TypedController<T> extends ListController {

    private T currentData;
    private boolean allowBuildRequests;

    public final void setData(T data) {
        this.currentData = data;
        allowBuildRequests = true;
        requestModelBuild();
        allowBuildRequests = false;
    }

    @Override
    public void requestModelBuild() {
        if (!allowBuildRequests) {
            throw new InvalidUsageException(
                "Cannot call requestModelBuild directly on a TypedController. "
                + "Use setData() instead.");
        }
        super.requestModelBuild();
    }

    @Override
    protected final void buildModels() {
        buildModels(currentData);
    }

    protected abstract void buildModels(T data);

    public T getCurrentData() {
        return currentData;
    }

    @Override
    public void moveModel(int fromPosition, int toPosition) {
        allowBuildRequests = true;
        super.moveModel(fromPosition, toPosition);
        allowBuildRequests = false;
    }
}
