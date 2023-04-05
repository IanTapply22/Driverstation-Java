package me.iantapply.utils;

public class MathUtils {

    /**
     * Calculates the new value for the axis by converting the range to
     * -127 to 128.
     * @param rawValue Raw value to calculate.
     * @return The new value within the new range.
     */
    public static int calculateAxisValue(double rawValue) {
        return (int) ((rawValue*127.5)-0.5);
    }
}
