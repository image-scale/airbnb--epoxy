package dev.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface PackageConfig {

    boolean REQUIRE_HASHCODE_DEFAULT = false;
    boolean REQUIRE_ABSTRACT_MODELS_DEFAULT = false;
    boolean IMPLICITLY_ADD_AUTO_MODELS_DEFAULT = false;

    boolean requireHashCode() default REQUIRE_HASHCODE_DEFAULT;

    boolean requireAbstractModels() default REQUIRE_ABSTRACT_MODELS_DEFAULT;

    boolean implicitlyAddAutoModels() default IMPLICITLY_ADD_AUTO_MODELS_DEFAULT;
}
