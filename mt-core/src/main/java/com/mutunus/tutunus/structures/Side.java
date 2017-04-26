package com.mutunus.tutunus.structures;

public enum Side {
    LONG(-1) {
        @Override
        public Side revert() {
            return SHORT;
        }

        @Override
        public boolean isLong() {
            return true;
        }
    },
    SHORT(1) {
        @Override
        public Side revert() {
            return LONG;
        }

        @Override
        public boolean isLong() {
            return false;
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
    public abstract boolean isLong();
    public boolean isShort() {return !isLong();}

    public int getSizeSigned(int size) {
        size = Math.abs(size);
        if (this == LONG) {
            return size;
        }
        return -1 * size;
    }

}