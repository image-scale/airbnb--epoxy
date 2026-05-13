package dev.epoxy;

public final class HashGenerator {

    private HashGenerator() {}

    public static long hashLong(long value) {
        value ^= (value << 21);
        value ^= (value >>> 35);
        value ^= (value << 4);
        return value;
    }

    public static long hashString(CharSequence str) {
        if (str == null) {
            return 0;
        }
        long hash = 0xcbf29ce484222325L;
        long prime = 0x100000001b3L;
        for (int i = 0; i < str.length(); i++) {
            hash ^= str.charAt(i);
            hash *= prime;
        }
        return hash;
    }
}
