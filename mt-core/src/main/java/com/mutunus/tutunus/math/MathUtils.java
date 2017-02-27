package com.mutunus.tutunus.math;

public final class MathUtils {

    public static double computeChange(final double oldPrice, final double newPrice) {
        final double result = ((newPrice - oldPrice) / oldPrice) * 100.0D;
        return result;
    }

    public static double computeRange(final double d, final double e) {
        final double r = (e + d) / 2f;
        final double rd = (r - d) / d;
        final double rup = (e - r) / e;
        return (rd + rup) * 100d;
    }

    // public static String formatVolume(final long volume) {
    // if (volume < 1000) {
    // return Long.toString(volume);
    // }
    //
    // if (volume < 1000 * 1000) {
    // return Double.toString(MathUtils.round(volume / 1000.0)) + "K";
    // }
    // return Double.toString(MathUtils.round(volume / (1000.0 * 1000.0))) + "M";
    // }

    public static double percentage(final double base, final double value) {
        return 100.0 * value / base;
    }

}
