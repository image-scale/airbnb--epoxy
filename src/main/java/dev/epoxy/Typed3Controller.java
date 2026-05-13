package dev.epoxy;

public abstract class Typed3Controller<T, U, V> extends ListController {

    private T data1;
    private U data2;
    private V data3;
    private boolean allowBuildRequests;

    public void setData(T data1, U data2, V data3) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        allowBuildRequests = true;
        requestModelBuild();
        allowBuildRequests = false;
    }

    @Override
    public final void requestModelBuild() {
        if (!allowBuildRequests) {
            throw new InvalidUsageException(
                "Cannot call requestModelBuild directly on a Typed3Controller. "
                + "Use setData() instead.");
        }
        super.requestModelBuild();
    }

    @Override
    protected final void buildModels() {
        buildModels(data1, data2, data3);
    }

    protected abstract void buildModels(T data1, U data2, V data3);

    @Override
    public void moveModel(int fromPosition, int toPosition) {
        allowBuildRequests = true;
        super.moveModel(fromPosition, toPosition);
        allowBuildRequests = false;
    }
}
