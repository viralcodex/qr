package com.qr;

import com.constants.Constants;
import com.constants.ErrorCorrection;
import com.constants.Modes;

import java.nio.charset.StandardCharsets;

import static com.qr.utils.QRUtils.getEncodingMode;
import static com.qr.utils.QRUtils.getIndicatorCodes;
import static com.qr.utils.QRUtils.getVersion;

public class QREncoder {

    public static String encode(String input, ErrorCorrection errorCorrectionLevel) {
        int inputLength = input.length();

        Modes mode = getEncodingMode(input);
        int version = getVersion(inputLength, errorCorrectionLevel, mode);

        if (version == -1) {
            throw new IllegalArgumentException("Data too long for QR code");
        }


        System.out.println("Mode: " + mode);
        System.out.println("Version: " + version);

        String indicators = getIndicatorCodes(inputLength, mode, version);
        String encodedString = encodeBasedOnMode(input, mode);

        int totalBitsAllowed = Constants.DATA_CODEWORDS[version - 1][errorCorrectionLevel.ordinal()] * 8; //bits = bytes * 8

        StringBuilder sb = new StringBuilder();

        int difference = totalBitsAllowed - encodedString.length();

        sb.append(indicators).append(encodedString).append("0".repeat(Math.min(difference, 4))); //only upto 4 0s to add

        if(sb.length() % 8 != 0)
        {
            sb.append("0".repeat(8 - sb.length() % 8));
        }

        boolean useFirst = true;
        while (sb.length() < totalBitsAllowed) {
            sb.append(useFirst ? "11101100" : "00010001"); // 236 and 17 (bitwise complement)
            useFirst = !useFirst;
        }

        // TODO: Implement encoding steps:
        // 1. Add mode indicator (4 bits) (done)
        // 2. Add character count indicator (done)
        // 3. Encode data based on mode (done)
        // 4. Add terminator and padding (done)
        // 5. Generate error correction codewords
        // 6. Structure final message
        // 7. Place modules in matrix
        // 8. Apply masking
        // 9. Add format and version info

        return "Encoded(" + input + ")";
    }

    private static String encodeBasedOnMode(String input, Modes mode) {
        return switch (mode) {
            case NUMERIC -> encodeNumeric(input);
            case ALPHANUMERIC -> encodeAlphaNumeric(input);
            case BYTE -> encodeByte(input);
        };
    }

    private static String encodeNumeric(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i += 3) {
            String group = input.substring(i, Math.min(i + 3, input.length()));
            sb.append(getNumberBytes(group));
        }
        return sb.toString();
    }

    private static String encodeAlphaNumeric(String input) {
        input = input.toUpperCase(); //can't encode lowercase letters, so convert to uppercase
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i += 2) {
            String group = input.substring(i, Math.min(i + 2, input.length()));
            if (group.length() == 2) {
                int value = getAlphanumericValue(group.charAt(0)) * 45 + getAlphanumericValue(group.charAt(1));
                sb.append(String.format("%11s", Integer.toBinaryString(value)).replace(' ', '0'));
            } else {
                int value = getAlphanumericValue(group.charAt(0));
                sb.append(String.format("%6s", Integer.toBinaryString(value)).replace(' ', '0'));
            }
        }
        return sb.toString();
    }

    private static int getAlphanumericValue(char c) {
        int index = Constants.ALPHANUMERIC_STRING.indexOf(c);
        if (index == -1) {
            throw new IllegalArgumentException("Invalid alphanumeric character: " + c);
        }
        return index;
    }

    private static String encodeByte(String input) {
       byte[] convertedInput = input.getBytes(StandardCharsets.UTF_8);
       StringBuilder sb = new StringBuilder();
        for (byte b : convertedInput) {
            sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        return sb.toString();
    }

    private static String getNumberBytes(String number)
    {
        int bits = switch (number.length()) {
            case 3 -> 10;
            case 2 -> 7;
            case 1 -> 4;
            default -> throw new IllegalArgumentException("Invalid group length");
        };
        return String.format("%" + bits + "s", Integer.toBinaryString(Integer.parseInt(number))).replace(' ', '0');
    }
}
