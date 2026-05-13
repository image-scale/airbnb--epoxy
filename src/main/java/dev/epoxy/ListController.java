package dev.epoxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class ListController {

    public interface Interceptor {
        void intercept(List<ItemModel<?>> models);
    }

    public interface BuildListener {
        void onBuildFinished(List<ChangeOperation> diffResult);
    }

    private List<ItemModel<?>> currentModels = new ArrayList<>();
    private FrozenModelList modelsBeingBuilt;
    private final List<Interceptor> interceptors = new ArrayList<>();
    private final List<BuildListener> buildListeners = new ArrayList<>();
    private ListChangeObserver observer;
    private boolean filterDuplicates;
    private boolean building;
    private boolean hasBuiltModelsEver;

    private static boolean globalDuplicateFilteringDefault;

    public ListController() {
        this.filterDuplicates = globalDuplicateFilteringDefault;
    }

    protected abstract void buildModels();

    public void setObserver(ListChangeObserver observer) {
        this.observer = observer;
    }

    public void requestModelBuild() {
        if (building) {
            throw new InvalidUsageException(
                "Cannot call requestModelBuild from inside buildModels()");
        }
        executeBuild();
    }

    private void executeBuild() {
        building = true;
        modelsBeingBuilt = new FrozenModelList();

        try {
            buildModels();
        } catch (Exception e) {
            modelsBeingBuilt = null;
            building = false;
            throw e;
        }

        for (Interceptor interceptor : interceptors) {
            interceptor.intercept(modelsBeingBuilt);
        }

        if (filterDuplicates) {
            removeDuplicateIds(modelsBeingBuilt);
        }

        modelsBeingBuilt.freeze();

        List<ChangeOperation> ops = DiffCalculator.computeDiff(currentModels, modelsBeingBuilt);

        for (ItemModel<?> model : modelsBeingBuilt) {
            model.addedToAdapter = true;
        }

        currentModels = new ArrayList<>(modelsBeingBuilt);

        dispatchChanges(ops);

        List<BuildListener> listenersCopy = new ArrayList<>(buildListeners);
        for (BuildListener listener : listenersCopy) {
            listener.onBuildFinished(ops);
        }

        modelsBeingBuilt = null;
        building = false;
        hasBuiltModelsEver = true;
    }

    void addInternal(ItemModel<?> model) {
        if (modelsBeingBuilt == null) {
            throw new InvalidUsageException(
                "Cannot add models outside of buildModels().");
        }
        if (model.hasDefaultId()) {
            throw new InvalidUsageException(
                "Models must have an explicit ID set via id() before being added to a controller.");
        }
        if (!model.isVisible()) {
            throw new InvalidUsageException(
                "Hidden models cannot be added to a controller. "
                + "Use addIf() for conditional adding.");
        }
        modelsBeingBuilt.add(model);
    }

    public void add(ItemModel<?> model) {
        model.addTo(this);
    }

    public boolean isBuildingModels() {
        return building;
    }

    public boolean hasPendingModelBuild() {
        return building;
    }

    public int getModelCount() {
        return currentModels.size();
    }

    public ItemModel<?> getModel(int position) {
        return currentModels.get(position);
    }

    public List<ItemModel<?>> getCurrentModels() {
        return Collections.unmodifiableList(currentModels);
    }

    public void moveModel(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) return;
        if (currentModels.isEmpty()) {
            throw new InvalidUsageException("No models have been built yet.");
        }
        List<ItemModel<?>> models = new ArrayList<>(currentModels);
        ItemModel<?> model = models.remove(fromPosition);
        models.add(toPosition, model);
        currentModels = models;

        if (observer != null) {
            observer.onItemMoved(fromPosition, toPosition);
        }
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void removeInterceptor(Interceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public void addBuildListener(BuildListener listener) {
        buildListeners.add(listener);
    }

    public void removeBuildListener(BuildListener listener) {
        buildListeners.remove(listener);
    }

    public void setFilterDuplicates(boolean filter) {
        this.filterDuplicates = filter;
    }

    public boolean isFilterDuplicates() {
        return filterDuplicates;
    }

    public static void setGlobalDuplicateFilteringDefault(boolean filter) {
        globalDuplicateFilteringDefault = filter;
    }

    private void removeDuplicateIds(List<ItemModel<?>> models) {
        Set<Long> seenIds = new HashSet<>();
        Iterator<ItemModel<?>> it = models.iterator();
        while (it.hasNext()) {
            ItemModel<?> model = it.next();
            if (!seenIds.add(model.id())) {
                it.remove();
            }
        }
    }

    private void dispatchChanges(List<ChangeOperation> operations) {
        if (observer == null) return;
        for (ChangeOperation op : operations) {
            switch (op.type) {
                case ChangeOperation.ADD ->
                    observer.onItemsInserted(op.positionStart, op.itemCount);
                case ChangeOperation.REMOVE ->
                    observer.onItemsRemoved(op.positionStart, op.itemCount);
                case ChangeOperation.MOVE ->
                    observer.onItemMoved(op.positionStart, op.itemCount);
                case ChangeOperation.UPDATE ->
                    observer.onItemsChanged(op.positionStart, op.itemCount, op.payloads);
            }
        }
    }
}
