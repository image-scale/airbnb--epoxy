package dev.epoxy;

public final class VisibilityState {

    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 1;
    public static final int FOCUSED_VISIBLE = 2;
    public static final int UNFOCUSED_VISIBLE = 3;
    public static final int FULL_IMPRESSION_VISIBLE = 4;
    public static final int PARTIAL_IMPRESSION_VISIBLE = 5;
    public static final int PARTIAL_IMPRESSION_INVISIBLE = 6;

    private VisibilityState() {}
}
