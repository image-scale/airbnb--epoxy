package dev.epoxy;

public final class TouchDirection {

    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;

    public static final int VERTICAL = UP | DOWN;
    public static final int HORIZONTAL = LEFT | RIGHT;
    public static final int ALL = VERTICAL | HORIZONTAL;

    private TouchDirection() {}
}
