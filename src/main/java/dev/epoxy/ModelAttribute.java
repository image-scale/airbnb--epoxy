package dev.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ModelAttribute {

    enum Option {
        NoGetter,
        NoSetter,
        DoNotHash,
        IgnoreRequireHashCode,
        DoNotUseInToString
    }

    Option[] value() default {};
}
