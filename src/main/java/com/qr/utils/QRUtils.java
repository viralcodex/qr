package com.qr.utils;

import com.constants.Constants;
import com.constants.ErrorCorrection;
import com.constants.Modes;

public class QRUtils {

    public static int getVersion(int dataLength, ErrorCorrection errorCorrection, Modes mode) {
        int[][] table = switch (mode) {
            case NUMERIC -> Constants.NUMERIC;
            case ALPHANUMERIC -> Constants.ALPHANUMERIC;
            case BYTE -> Constants.BYTE;
        };

        int errorIndex = errorCorrection.ordinal(); // index of the enum value

        for (int version = 0; version < table.length; version++) {
            if (dataLength <= table[version][errorIndex]) {
                return version + 1;
            }
        }
        return -1;
    }

    public static Modes getEncodingMode(String input) {
        if (input.matches("[0-9]+")) {
            return Modes.NUMERIC;
        } else if (input.matches("[A-Z0-9 $%*+\\-./:]+")) {
            return Modes.ALPHANUMERIC;
        } else {
            return Modes.BYTE;
        }
    }

    public static String getIndicatorCodes(int inputLength, Modes mode, int version) {
        // Mode indicator (4 bits)
        String modeIndicator = String.format("%4s", Integer.toBinaryString(mode.getIndicator()))
                .replace(' ', '0');

        // Character count indicator (variable bits based on version and mode)
        int charCountBits = mode.getCharCountBits(version);
        String lengthIndicator = String.format("%" + charCountBits + "s", Integer.toBinaryString(inputLength))
                .replace(' ', '0');

        return modeIndicator + lengthIndicator;
    }

    public static void printQR(int[][] qrCode) {
        String WHITE_BG = "\u001B[47m";
        String BLACK_BG = "\u001B[40m";
        String RESET = "\u001B[0m";

        //we need a 4 modules wide border around the QR code, so we print from -4 to length + 4
        for (int i = -4; i < qrCode.length + 4; i++) {
            for (int j = -4; j < qrCode[0].length + 4; j++) {
                if (!isValid(i, j, qrCode.length - 1, qrCode[0].length - 1)) {
                    System.out.print(WHITE_BG + "  " + RESET);
                } else {
                    if (qrCode[i][j] == 0) {
                        System.out.print(WHITE_BG + "  " + RESET);
                    } else {
                        System.out.print(BLACK_BG + "  " + RESET);
                    }
                }
            }
            System.out.println();
        }
    }

    private static boolean isValid(int row, int col, int n, int m)
    {
        return row >= 0 && row <= n && col >= 0 && col <= m;
    }
}
