package com.mutunus.tutunus.structures;

public enum Side {
    LONG(-1) {
        @Override
        public Side revert() {
            return SHORT;
        }
    },
    SHORT(1) {
        @Override
        public Side revert() {
            return LONG;
        }
    };

    public static int REVERT = -1;// TODO: wtf?

    private final int modifier;

    private Side(final int modifier) {
        this.modifier = modifier;
    }

    public int getModifier() {
        return modifier;
    }

    public abstract Side revert();

    public int getSizeSigned(int size) {
        size = Math.abs(size);
        if (this == LONG) {
            return size;
        }
        return -1 * size;
    }

}