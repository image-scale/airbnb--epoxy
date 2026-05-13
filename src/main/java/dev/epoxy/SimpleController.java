package dev.epoxy;

import java.util.Collections;
import java.util.List;

public class SimpleController extends ListController {

    private List<? extends ItemModel<?>> currentModels = Collections.emptyList();
    private boolean insideSetModels;

    public void setModels(List<? extends ItemModel<?>> models) {
        this.currentModels = models;
        insideSetModels = true;
        requestModelBuild();
        insideSetModels = false;
    }

    @Override
    public void requestModelBuild() {
        if (!insideSetModels) {
            throw new InvalidUsageException(
                "Cannot call requestModelBuild directly on a SimpleController. "
                + "Use setModels() instead.");
        }
        super.requestModelBuild();
    }

    @Override
    protected final void buildModels() {
        for (ItemModel<?> model : currentModels) {
            model.addTo(this);
        }
    }

    @Override
    public void moveModel(int fromPosition, int toPosition) {
        insideSetModels = true;
        super.moveModel(fromPosition, toPosition);
        insideSetModels = false;
    }
}
