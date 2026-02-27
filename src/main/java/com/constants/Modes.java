package com.constants;

public enum Modes {
    //                    indicator, V1-9, V10-26, V27-40
    NUMERIC(0b0001,       10, 12, 14),
    ALPHANUMERIC(0b0010,   9, 11, 13),
    BYTE(0b0100,           8, 16, 16);
    // KANJI(0b1000,        8, 10, 12)  // add later

    private final int indicator;
    private final int[] charCountBits; // character count indicator lengths

    Modes(int indicator, int bitsV1_9, int bitsV10_26, int bitsV27_40) {
        this.indicator = indicator;
        this.charCountBits = new int[] { bitsV1_9, bitsV10_26, bitsV27_40 };
    }

    public int getIndicator() {
        return indicator;
    }

    /**
     * Get character count indicator bit length for a given version.
     * @param version QR version (1-40)
     * @return Number of bits for character count indicator
     */
    public int getCharCountBits(int version) {
        if (version <= 9) {
            return charCountBits[0];
        } else if (version <= 26) {
            return charCountBits[1];
        } else {
            return charCountBits[2];
        }
    }
}
