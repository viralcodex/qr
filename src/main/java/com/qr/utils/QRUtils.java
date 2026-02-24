package main.java.com.qr.utils;

import main.java.com.constants.ErrorCorrection;
import main.java.com.constants.Modes;

public class QRUtils {

    public static String encode(String input, ErrorCorrection errorCorrectionLevel) {
        Modes mode = getEncodingMode(input);

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
