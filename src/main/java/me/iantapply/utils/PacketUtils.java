package me.iantapply.utils;

public class PacketUtils {

    public static String printByteBits(byte rawByte) {
        return String.format("%8s", Integer.toBinaryString(rawByte & 0xFF)).replace(' ', '0');
    }
}
