package com.qr.utils;

import com.constants.ErrorCorrection;
import com.constants.Modes;
import com.qr.utils.QRCapacity;

import static com.qr.utils.QRCapacity.getVersion;

public class QRUtils {

    public static String encode(String input, ErrorCorrection errorCorrectionLevel) {
        Modes mode = getEncodingMode(input);
        int version = getVersion(input.length(), errorCorrectionLevel, mode);

        System.out.println("Version: " + version);
        return "Encoded(" + input + ")";
    }

    public static String decode(String qrCode) {
        // Placeholder for decoding logic
        return "Decoded(" + qrCode + ")";
    }

    private static Modes getEncodingMode(String input) {
        if(input.matches("[0-9]+"))
        {
            return Modes.NUMERIC;
        }
        else if(input.matches("[A-Z0-9 $%*+\\-./:]+"))
        {
            return Modes.ALPHANUMERIC;
        }
        //kanji mode will be implemented later
        else
        {
            return Modes.BYTE;
        }
    }
}
