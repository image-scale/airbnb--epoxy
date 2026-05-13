package dev.epoxy;

public abstract class Typed4Controller<T, U, V, W> extends ListController {

    private T data1;
    private U data2;
    private V data3;
    private W data4;
    private boolean allowBuildRequests;

    public void setData(T data1, U data2, V data3, W data4) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.data4 = data4;
        allowBuildRequests = true;
        requestModelBuild();
        allowBuildRequests = false;
    }

    @Override
    public final void requestModelBuild() {
        if (!allowBuildRequests) {
            throw new InvalidUsageException(
                "Cannot call requestModelBuild directly on a Typed4Controller. "
                + "Use setData() instead.");
        }
        super.requestModelBuild();
    }

    @Override
    protected final void buildModels() {
        buildModels(data1, data2, data3, data4);
    }

    protected abstract void buildModels(T data1, U data2, V data3, W data4);

    @Override
    public void moveModel(int fromPosition, int toPosition) {
        allowBuildRequests = true;
        super.moveModel(fromPosition, toPosition);
        allowBuildRequests = false;
    }
}
