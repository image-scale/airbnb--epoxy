package dev.epoxy;

import java.util.Arrays;
import java.util.List;

public abstract class TouchHelper {

    public static DragBuilder initDragging(ListController controller) {
        return new DragBuilder(controller);
    }

    public static SwipeBuilder initSwiping() {
        return new SwipeBuilder();
    }

    public static class DragBuilder {
        private final ListController controller;

        DragBuilder(ListController controller) {
            this.controller = controller;
        }

        public DragBuilder2 forVerticalList() {
            return new DragBuilder2(controller, TouchDirection.VERTICAL);
        }

        public DragBuilder2 forHorizontalList() {
            return new DragBuilder2(controller, TouchDirection.HORIZONTAL);
        }

        public DragBuilder2 forGrid() {
            return new DragBuilder2(controller, TouchDirection.ALL);
        }

        public DragBuilder2 withDirections(int directionFlags) {
            return new DragBuilder2(controller, directionFlags);
        }
    }

    public static class DragBuilder2 {
        private final ListController controller;
        private final int directions;

        DragBuilder2(ListController controller, int directions) {
            this.controller = controller;
            this.directions = directions;
        }

        @SuppressWarnings("unchecked")
        public <U extends ItemModel<?>> DragBuilder3<U> withTarget(Class<U> targetModelClass) {
            return new DragBuilder3<>(controller, directions, targetModelClass, null);
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        public final DragBuilder3<ItemModel<?>> withTargets(
                Class<? extends ItemModel<?>>... targetModelClasses) {
            return new DragBuilder3<>(controller, directions, null,
                    Arrays.asList(targetModelClasses));
        }

        @SuppressWarnings("unchecked")
        public DragBuilder3<ItemModel<?>> forAllModels() {
            return new DragBuilder3<>(controller, directions,
                    (Class<ItemModel<?>>) (Class<?>) ItemModel.class, null);
        }
    }

    public static class DragBuilder3<U extends ItemModel<?>> {
        private final ListController controller;
        private final int directions;
        private final Class<U> targetClass;
        private final List<Class<? extends ItemModel<?>>> targetClasses;

        DragBuilder3(ListController controller, int directions,
                     Class<U> targetClass,
                     List<Class<? extends ItemModel<?>>> targetClasses) {
            this.controller = controller;
            this.directions = directions;
            this.targetClass = targetClass;
            this.targetClasses = targetClasses;
        }

        public TouchHandler<U> andCallbacks(DragCallbacks<U> callbacks) {
            TouchHandler<U> handler;
            if (targetClass != null) {
                handler = new TouchHandler<>(controller, targetClass, directions, 0);
            } else {
                handler = new TouchHandler<>(controller, targetClasses, directions, 0);
            }
            handler.setDragCallback(callbacks);
            return handler;
        }
    }

    public static class SwipeBuilder {
        SwipeBuilder() {}

        public SwipeBuilder2 left() {
            return new SwipeBuilder2(TouchDirection.LEFT);
        }

        public SwipeBuilder2 right() {
            return new SwipeBuilder2(TouchDirection.RIGHT);
        }

        public SwipeBuilder2 leftAndRight() {
            return new SwipeBuilder2(TouchDirection.HORIZONTAL);
        }

        public SwipeBuilder2 withDirections(int directionFlags) {
            return new SwipeBuilder2(directionFlags);
        }
    }

    public static class SwipeBuilder2 {
        private final int directions;

        SwipeBuilder2(int directions) {
            this.directions = directions;
        }

        @SuppressWarnings("unchecked")
        public <U extends ItemModel<?>> SwipeBuilder3<U> withTarget(Class<U> targetModelClass) {
            return new SwipeBuilder3<>(directions, targetModelClass, null);
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        public final SwipeBuilder3<ItemModel<?>> withTargets(
                Class<? extends ItemModel<?>>... targetModelClasses) {
            return new SwipeBuilder3<>(directions, null, Arrays.asList(targetModelClasses));
        }

        @SuppressWarnings("unchecked")
        public SwipeBuilder3<ItemModel<?>> forAllModels() {
            return new SwipeBuilder3<>(directions,
                    (Class<ItemModel<?>>) (Class<?>) ItemModel.class, null);
        }
    }

    public static class SwipeBuilder3<U extends ItemModel<?>> {
        private final int directions;
        private final Class<U> targetClass;
        private final List<Class<? extends ItemModel<?>>> targetClasses;

        SwipeBuilder3(int directions, Class<U> targetClass,
                      List<Class<? extends ItemModel<?>>> targetClasses) {
            this.directions = directions;
            this.targetClass = targetClass;
            this.targetClasses = targetClasses;
        }

        public TouchHandler<U> andCallbacks(SwipeCallbacks<U> callbacks) {
            TouchHandler<U> handler;
            if (targetClass != null) {
                handler = new TouchHandler<>(null, targetClass, 0, directions);
            } else {
                handler = new TouchHandler<>(null, targetClasses, 0, directions);
            }
            handler.setSwipeCallback(callbacks);
            return handler;
        }
    }

    public abstract static class DragCallbacks<T extends ItemModel<?>>
            implements DragCallback<T> {

        @Override
        public void onDragStarted(T model, int adapterPosition) {}

        @Override
        public abstract void onModelMoved(int fromPosition, int toPosition, T modelBeingMoved);

        @Override
        public void onDragReleased(T model) {}

        @Override
        public void clearView(T model) {}

        public boolean isDragEnabledForModel(ItemModel<?> model) {
            return true;
        }
    }

    public abstract static class SwipeCallbacks<T extends ItemModel<?>>
            implements SwipeCallback<T> {

        @Override
        public void onSwipeStarted(T model, int adapterPosition) {}

        @Override
        public abstract void onSwipeCompleted(T model, int position, int direction);

        @Override
        public void onSwipeReleased(T model) {}

        @Override
        public void clearView(T model) {}

        public boolean isSwipeEnabledForModel(ItemModel<?> model) {
            return true;
        }
    }
}
