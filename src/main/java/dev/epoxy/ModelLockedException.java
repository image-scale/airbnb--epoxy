package dev.epoxy;

public class ModelLockedException extends RuntimeException {

    private static final String EXPLANATION =
        "Model properties cannot be changed once the model has been added to a controller "
        + "and the build has completed. If you need to modify models, do so inside an "
        + "Interceptor callback.";

    public ModelLockedException(ItemModel<?> model, int position) {
        super("Position: " + position + " Model: " + model + "\n\n" + EXPLANATION);
    }

    public ModelLockedException(String detail) {
        super(detail + "\n\n" + EXPLANATION);
    }
}
