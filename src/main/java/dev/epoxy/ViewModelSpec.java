package dev.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ViewModelSpec {

    enum Size {
        NONE,
        MANUAL,
        WRAP_WIDTH_WRAP_HEIGHT,
        WRAP_WIDTH_MATCH_HEIGHT,
        MATCH_WIDTH_WRAP_HEIGHT,
        MATCH_WIDTH_MATCH_HEIGHT
    }

    Size autoLayout() default Size.NONE;

    int defaultLayout() default 0;

    Class<?> baseModelClass() default Void.class;

    boolean saveViewState() default false;

    boolean fullSpan() default true;
}
