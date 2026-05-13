package dev.epoxy;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnnotationDefinitionTest {

    @Test
    public void testModelAttributeOptionValues() {
        ModelAttribute.Option[] values = ModelAttribute.Option.values();
        assertEquals(5, values.length);
        assertNotNull(ModelAttribute.Option.NoGetter);
        assertNotNull(ModelAttribute.Option.NoSetter);
        assertNotNull(ModelAttribute.Option.DoNotHash);
        assertNotNull(ModelAttribute.Option.IgnoreRequireHashCode);
        assertNotNull(ModelAttribute.Option.DoNotUseInToString);
    }

    @Test
    public void testModelAttributeOptionOrdinals() {
        assertEquals(0, ModelAttribute.Option.NoGetter.ordinal());
        assertEquals(1, ModelAttribute.Option.NoSetter.ordinal());
        assertEquals(2, ModelAttribute.Option.DoNotHash.ordinal());
        assertEquals(3, ModelAttribute.Option.IgnoreRequireHashCode.ordinal());
        assertEquals(4, ModelAttribute.Option.DoNotUseInToString.ordinal());
    }

    @Test
    public void testViewModelSpecSizeValues() {
        ViewModelSpec.Size[] values = ViewModelSpec.Size.values();
        assertEquals(6, values.length);
        assertNotNull(ViewModelSpec.Size.NONE);
        assertNotNull(ViewModelSpec.Size.MANUAL);
        assertNotNull(ViewModelSpec.Size.WRAP_WIDTH_WRAP_HEIGHT);
        assertNotNull(ViewModelSpec.Size.WRAP_WIDTH_MATCH_HEIGHT);
        assertNotNull(ViewModelSpec.Size.MATCH_WIDTH_WRAP_HEIGHT);
        assertNotNull(ViewModelSpec.Size.MATCH_WIDTH_MATCH_HEIGHT);
    }

    @Test
    public void testViewModelSpecSizeOrdinals() {
        assertEquals(0, ViewModelSpec.Size.NONE.ordinal());
        assertEquals(1, ViewModelSpec.Size.MANUAL.ordinal());
        assertEquals(2, ViewModelSpec.Size.WRAP_WIDTH_WRAP_HEIGHT.ordinal());
        assertEquals(3, ViewModelSpec.Size.WRAP_WIDTH_MATCH_HEIGHT.ordinal());
        assertEquals(4, ViewModelSpec.Size.MATCH_WIDTH_WRAP_HEIGHT.ordinal());
        assertEquals(5, ViewModelSpec.Size.MATCH_WIDTH_MATCH_HEIGHT.ordinal());
    }

    @Test
    public void testModelPropertyOptionValues() {
        ModelProperty.Option[] values = ModelProperty.Option.values();
        assertEquals(4, values.length);
        assertNotNull(ModelProperty.Option.DoNotHash);
        assertNotNull(ModelProperty.Option.IgnoreRequireHashCode);
        assertNotNull(ModelProperty.Option.GenerateStringOverloads);
        assertNotNull(ModelProperty.Option.NullOnRecycle);
    }

    @Test
    public void testModelPropertyOptionOrdinals() {
        assertEquals(0, ModelProperty.Option.DoNotHash.ordinal());
        assertEquals(1, ModelProperty.Option.IgnoreRequireHashCode.ordinal());
        assertEquals(2, ModelProperty.Option.GenerateStringOverloads.ordinal());
        assertEquals(3, ModelProperty.Option.NullOnRecycle.ordinal());
    }

    @Test
    public void testPackageViewConfigOptionValues() {
        PackageViewConfig.Option[] values = PackageViewConfig.Option.values();
        assertEquals(3, values.length);
        assertNotNull(PackageViewConfig.Option.Default);
        assertNotNull(PackageViewConfig.Option.Enabled);
        assertNotNull(PackageViewConfig.Option.Disabled);
    }

    @Test
    public void testPackageViewConfigOptionOrdinals() {
        assertEquals(0, PackageViewConfig.Option.Default.ordinal());
        assertEquals(1, PackageViewConfig.Option.Enabled.ordinal());
        assertEquals(2, PackageViewConfig.Option.Disabled.ordinal());
    }

    @Test
    public void testPackageConfigConstants() {
        assertFalse(PackageConfig.REQUIRE_HASHCODE_DEFAULT);
        assertFalse(PackageConfig.REQUIRE_ABSTRACT_MODELS_DEFAULT);
        assertFalse(PackageConfig.IMPLICITLY_ADD_AUTO_MODELS_DEFAULT);
    }

    @Test
    public void testViewModelSpecSizeValueOf() {
        assertEquals(ViewModelSpec.Size.NONE, ViewModelSpec.Size.valueOf("NONE"));
        assertEquals(ViewModelSpec.Size.MANUAL, ViewModelSpec.Size.valueOf("MANUAL"));
        assertEquals(ViewModelSpec.Size.WRAP_WIDTH_WRAP_HEIGHT,
                ViewModelSpec.Size.valueOf("WRAP_WIDTH_WRAP_HEIGHT"));
        assertEquals(ViewModelSpec.Size.MATCH_WIDTH_MATCH_HEIGHT,
                ViewModelSpec.Size.valueOf("MATCH_WIDTH_MATCH_HEIGHT"));
    }

    @Test
    public void testModelAttributeOptionValueOf() {
        assertEquals(ModelAttribute.Option.NoGetter,
                ModelAttribute.Option.valueOf("NoGetter"));
        assertEquals(ModelAttribute.Option.DoNotHash,
                ModelAttribute.Option.valueOf("DoNotHash"));
    }

    @Test
    public void testModelPropertyOptionValueOf() {
        assertEquals(ModelProperty.Option.NullOnRecycle,
                ModelProperty.Option.valueOf("NullOnRecycle"));
        assertEquals(ModelProperty.Option.GenerateStringOverloads,
                ModelProperty.Option.valueOf("GenerateStringOverloads"));
    }

    @Test
    public void testPackageViewConfigOptionValueOf() {
        assertEquals(PackageViewConfig.Option.Default,
                PackageViewConfig.Option.valueOf("Default"));
        assertEquals(PackageViewConfig.Option.Enabled,
                PackageViewConfig.Option.valueOf("Enabled"));
        assertEquals(PackageViewConfig.Option.Disabled,
                PackageViewConfig.Option.valueOf("Disabled"));
    }

    @Test
    public void testAnnotationsCompileOnFields() {
        assertNotNull(AnnotatedFieldClass.class);
    }

    @Test
    public void testAnnotationsCompileOnMethods() {
        assertNotNull(AnnotatedMethodClass.class);
    }

    @Test
    public void testAnnotationsCompileOnTypes() {
        assertNotNull(AnnotatedTypeClass.class);
    }

    @Test
    public void testAnnotationsWithOptionsCompile() {
        assertNotNull(AnnotatedWithOptionsClass.class);
    }

    @Test
    public void testMarkerAnnotationsCompile() {
        assertNotNull(MarkerAnnotatedClass.class);
    }

    @Test
    public void testDataBindingAnnotationsCompile() {
        assertNotNull(DataBindingAnnotatedClass.class);
    }

    @Test
    public void testPackageConfigAnnotationCompiles() {
        assertNotNull(PackageConfigClass.class);
    }

    static class AnnotatedFieldClass {
        @ModelAttribute
        int field1;

        @ModelAttribute({ModelAttribute.Option.DoNotHash, ModelAttribute.Option.NoSetter})
        String field2;

        @AutoModel
        Object autoModelField;
    }

    static class AnnotatedMethodClass {
        @ModelProperty(defaultValue = "DEFAULT_TEXT", group = "textGroup")
        void setText(String text) {}

        @TextProperty(defaultRes = 0)
        void setTitle(CharSequence title) {}

        @CallbackProperty
        void setOnClick(Runnable listener) {}

        @AfterPropertiesSet
        void onPropsSet() {}

        @OnRecycled
        void onRecycle() {}

        @VisibilityChanged
        void onVisChange() {}

        @VisibilityStateChanged
        void onVisStateChange() {}
    }

    @ViewModelSpec(
        autoLayout = ViewModelSpec.Size.MATCH_WIDTH_WRAP_HEIGHT,
        saveViewState = true,
        fullSpan = false,
        baseModelClass = Void.class
    )
    static class AnnotatedTypeClass {}

    @GeneratedModelClass(layout = 42, useLayoutOverloads = true)
    static class AnnotatedWithOptionsClass {
        @ModelAttribute({ModelAttribute.Option.NoGetter, ModelAttribute.Option.DoNotUseInToString})
        int value;
    }

    static class MarkerAnnotatedClass {
        @CallbackProperty
        void setCallback(Runnable r) {}

        @AfterPropertiesSet
        void afterProps() {}

        @OnRecycled
        void recycled() {}

        @VisibilityChanged
        void visChanged() {}

        @VisibilityStateChanged
        void visStateChanged() {}
    }

    @DataBindingLayouts(value = {1, 2, 3}, enableDoNotHash = false)
    static class DataBindingAnnotatedClass {}

    @PackageConfig(
        requireHashCode = true,
        requireAbstractModels = true,
        implicitlyAddAutoModels = true
    )
    static class PackageConfigClass {}
}
