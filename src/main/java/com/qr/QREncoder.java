package com.qr;

import com.constants.Constants;
import com.constants.ErrorCorrection;
import com.constants.Modes;
import com.qr.utils.GaloisField;

import java.nio.charset.StandardCharsets;

import static com.constants.Constants.EC_CODEWORDS_PER_BLOCK;
import static com.constants.Constants.GF_EXP;
import static com.constants.Constants.GROUP1_BLOCKS;
import static com.constants.Constants.GROUP1_DATA_CODEWORDS;
import static com.constants.Constants.GROUP2_BLOCKS;
import static com.constants.Constants.GROUP2_DATA_CODEWORDS;
import static com.qr.utils.QRUtils.getEncodingMode;
import static com.qr.utils.QRUtils.getIndicatorCodes;
import static com.qr.utils.QRUtils.getVersion;

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

public class QREncoder
{   
    static {
        GaloisField.initGaloisTables();
    }

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

        //padding the end as the QR spec mentions
        String paddedEncodedString = getPaddedEncodedString(version, errorCorrectionLevel, encodedString, indicators);

        //Error Correction Coding
        String errorCorrectionEncodedString = getErrorCorrectionCodedString(version, errorCorrectionLevel, paddedEncodedString);

        String finalEncodedString = errorCorrectionEncodedString + "0".repeat(getRemainderBits(version)); // add remainder bits if needed
        
        return finalEncodedString;
    }

    private static String getPaddedEncodedString(int version, ErrorCorrection errorCorrectionLevel, String encodedString, String indicators) {
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
        return sb.toString();
    }

    private static String getErrorCorrectionCodedString(int version, ErrorCorrection errorCorrectionLevel, String paddedEncodedString)
    {  
        int[] ecBlockInfo = getECBlockInfo(version, errorCorrectionLevel.ordinal());

        //split the paddedEncodedString into data codewords based on the block info

        int[][] dataBlocks = getDataBlocks(paddedEncodedString, ecBlockInfo);
        
        int[] generatorPolynomial = getGeneratorPolynomial(ecBlockInfo[0]);
        
        int[][] errorCorrectionCodewords = new int[dataBlocks.length][];

        for(int i = 0; i < dataBlocks.length; i++)
        {
            errorCorrectionCodewords[i] = getErrorCorrectionCodewords(dataBlocks[i], generatorPolynomial, ecBlockInfo[0]);
        }

        //interleave data codewords and error correction codewords according to the QR spec and return as a string of bits
        
        StringBuilder sb = new StringBuilder();
        int maxDataCodewords = Math.max(ecBlockInfo[2], ecBlockInfo[4]);
        
        /**
         * we know the number of data codewords in each block from the block info, 
         * so we loop through the data blocks and take the codewords at the same index (if it exists) and append to the final string. 
         * We do this for all indices up to the maximum number of data codewords in any block. 
         * This way we are interleaving the codewords from each block together. 
         * After interleaving all the data codewords, we then interleave the error correction codewords in the same way.
        */
        for(int col = 0; col< maxDataCodewords; col++)
        {
            for(int block = 0; block < dataBlocks.length; block++)
            {
                if(col < dataBlocks[block].length)
                {
                    sb.append(String.format("%8s", Integer.toBinaryString(dataBlocks[block][col] & 0xFF)).replace(' ', '0'));
                }
            }
        }
       
        
        for(int col = 0; col < ecBlockInfo[0]; col++)
        {
            for(int block = 0; block < errorCorrectionCodewords.length; block++)
            {
                if(col < errorCorrectionCodewords[block].length)
                {
                    sb.append(String.format("%8s", Integer.toBinaryString(errorCorrectionCodewords[block][col] & 0xFF)).replace(' ', '0'));
                }
            }
        }

        return sb.toString();
    }

    private static int getRemainderBits(int version) {
        return switch (version) {
            case 2, 3, 4, 5, 6 -> 7;
            case 14, 15, 16, 17, 18, 19, 20, 28, 29, 30, 31, 32, 33, 34 -> 3;
            case 21, 22, 23, 24, 25, 26, 27 -> 4;
            default -> 0; // 1, 7-13, 35-40
        };
    }

    private static int[] getECBlockInfo(int version, int ecLevel) {
        int idx = version - 1;
        return new int[] {
                EC_CODEWORDS_PER_BLOCK[idx][ecLevel],
                GROUP1_BLOCKS[idx][ecLevel],
                GROUP1_DATA_CODEWORDS[idx][ecLevel],
                GROUP2_BLOCKS[idx][ecLevel],
                GROUP2_DATA_CODEWORDS[idx][ecLevel]
        };
    }

    private static int[][] getDataBlocks(String paddedEncodedString, int[] ecBlockInfo) {
        int group1Blocks = ecBlockInfo[1];
        int group1DataCodewords = ecBlockInfo[2];
        int group2Blocks = ecBlockInfo[3];
        int group2DataCodewords = ecBlockInfo[4];

        int[][] dataBlocks = new int[group1Blocks + group2Blocks][];
        int bitOffset = 0;
        for(int i = 0; i < group1Blocks; i++)
        {
            dataBlocks[i] = new int[group1DataCodewords];
            for(int j = 0; j < group1DataCodewords; j++)
            {
                dataBlocks[i][j] = Integer.parseInt(paddedEncodedString.substring(bitOffset, bitOffset + 8), 2);
                bitOffset += 8;
            }
        }

        for(int i = 0; i < group2Blocks; i++)
        {
            dataBlocks[group1Blocks + i] = new int[group2DataCodewords];
        
            for(int j = 0; j < group2DataCodewords; j++)
            {
                dataBlocks[group1Blocks + i][j] = Integer.parseInt(paddedEncodedString.substring(bitOffset, bitOffset + 8), 2);
                bitOffset += 8;
            }
        }

        if(bitOffset != paddedEncodedString.length())
        {
            throw new IllegalStateException("Bit offset does not match padded encoded string length");
        }

        return dataBlocks;
    }

    private static int[] getGeneratorPolynomial(int degree)
    {
        int[] generatorPolynomial = {1}; // start with the polynomial "1"


        for(int i = 0; i < degree; i++)
        {
            int[] term = {1, GF_EXP[i]}; // (x - α^i) => 1*x^1 + GF_EXP[i]*x^0
            generatorPolynomial = polynomialMultiply(generatorPolynomial, term); // multiply the current generator polynomial by the new term to get the updated generator polynomial
        }

        return generatorPolynomial;
    }

    private static int[] polynomialMultiply(int[] poly1, int[] poly2)
    {
        int[] result = new int[poly1.length + poly2.length - 1];

        for(int i = 0; i < poly1.length; i++)
        {
            for (int j = 0; j < poly2.length; j++)
            {
                int product = GaloisField.gfMultiply(poly1[i], poly2[j]);
                result[i + j] = GaloisField.gfAdd(result[i + j], product);
            }
        }

        return result;
    }

    private static String encodeBasedOnMode(String input, Modes mode) {
        return switch (mode) {
            case NUMERIC -> encodeNumeric(input);
            case ALPHANUMERIC -> encodeAlphaNumeric(input);
            case BYTE -> encodeByte(input);
        };
    }

    /** the process is we take 
     * 1. the data codewords for a block (from the padded encoded string)
     * 2. treat that as the coefficients of a message polynomial
     * 3. divide that message polynomial by the generator polynomial using polynomial long division in GF(256)
     * 4. the remainder from that division is the error correction codewords for that block
     * 5. repeat for each block
     */
    private static int[] getErrorCorrectionCodewords(int[] dataBlocks, int[] generatorPolynomial, int ecCodewordsPerBlock)
    {
        int dataLength = dataBlocks.length;
        int[] workingPolynomial = new int[dataLength + ecCodewordsPerBlock]; // the message polynomial [data block codewords + error correction codewords]
        
        System.arraycopy(dataBlocks, 0, workingPolynomial, 0, dataBlocks.length);

        for(int i = 0; i < dataLength; i++)
        {
            int leadTerm = workingPolynomial[i]; // the term we want to eliminate (make 0)
            if(leadTerm != 0)
            {
                // multiply the generator polynomial by the lead term and subtract (XOR) from the working polynomial
                for(int j = 0; j < generatorPolynomial.length; j++)
                {
                    int product = GaloisField.gfMultiply(leadTerm, generatorPolynomial[j]);
                    workingPolynomial[i + j] = GaloisField.gfAdd(workingPolynomial[i + j], product); // XOR for subtraction in GF(256)
                }
            }
        }

        int[] errorCorrectionCodewords = new int[ecCodewordsPerBlock];

        // the last ecCodewordsPerBlock terms of the working polynomial are the error correction codewords after the division process
        System.arraycopy(workingPolynomial, dataLength, errorCorrectionCodewords, 0, ecCodewordsPerBlock);

        return errorCorrectionCodewords;
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
