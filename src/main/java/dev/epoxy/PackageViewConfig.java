package dev.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface PackageViewConfig {

    enum Option {
        Default,
        Enabled,
        Disabled
    }

    Class<?> rClass();

    String defaultLayoutPattern() default "%s";

    Class<?> defaultBaseModelClass() default Void.class;

    boolean useLayoutOverloads() default false;

    String generatedModelSuffix() default "Model_";

    Option disableGenerateBuilderOverloads() default Option.Default;

    Option disableGenerateGetters() default Option.Default;

    Option disableGenerateReset() default Option.Default;
}
