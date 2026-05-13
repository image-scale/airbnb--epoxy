package dev.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ModelProperty {

    enum Option {
        DoNotHash,
        IgnoreRequireHashCode,
        GenerateStringOverloads,
        NullOnRecycle
    }

    Option[] options() default {};

    Option[] value() default {};

    String defaultValue() default "";

    String group() default "";
}
