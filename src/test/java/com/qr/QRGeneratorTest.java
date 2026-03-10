package com.qr;

import com.constants.ErrorCorrection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QRGeneratorTest {

    @Test
    void testVersion1MatrixIsBinary() {
        int[][] matrix = QRGenerator.generate("HELLO WORLD", ErrorCorrection.Q);
        assertEquals(21, matrix.length);
        assertEquals(21, matrix[0].length);
        assertBinaryOnly(matrix);
    }

    @Test
    void testFormatInfoCopiesMatchAndEncodeEcAndMask() {
        int[][] matrix = QRGenerator.generate("HELLO WORLD", ErrorCorrection.M);
        int size = matrix.length;

        int copyA = readFormatCopyA(matrix);
        int copyB = readFormatCopyB(matrix, size);

        assertEquals(copyA, copyB, "Both format info copies should match");

        boolean matchesAnyMask = false;
        for (int mask = 0; mask < 8; mask++) {
            if (copyA == expectedFormatBits(ErrorCorrection.M, mask)) {
                matchesAnyMask = true;
                break;
            }
        }
        assertTrue(matchesAnyMask, "Format info bits do not match expected EC level/mask encoding");
    }

    @Test
    void testVersionInfoCopiesMatchAndEncodeVersionNumberForV7Plus() {
        String input = "~".repeat(140);
        int[][] matrix = QRGenerator.generate(input, ErrorCorrection.Q);
        int size = matrix.length;
        int version = (size - 21) / 4 + 1;

        int copyA = readVersionCopyA(matrix, size);
        int copyB = readVersionCopyB(matrix, size);

        assertEquals(copyA, copyB, "Both version info copies should match");
        assertEquals(expectedVersionBits(version), copyA, "Version info bits should match BCH-encoded version");
    }

    @Test
    void testGenerateNullInputThrows() {
        assertThrows(NullPointerException.class, () -> QRGenerator.generate(null, ErrorCorrection.M));
    }

    private static void assertBinaryOnly(int[][] matrix) {
        for (int[] row : matrix) {
            for (int cell : row) {
                assertTrue(cell == 0 || cell == 1, "Matrix contains non-binary cell: " + cell);
            }
        }
    }

    private static int readFormatCopyA(int[][] matrix) {
        int[] bits = new int[15];
        int idx = 0;

        for (int col = 0; col < 9; col++) {
            if (col == 6) {
                continue;
            }
            bits[idx++] = matrix[8][col];
        }
        for (int row = 7; row >= 0; row--) {
            if (row == 6) {
                continue;
            }
            bits[idx++] = matrix[row][8];
        }

        return bitsToIntMsb(bits);
    }

    private static int readFormatCopyB(int[][] matrix, int size) {
        int[] bits = new int[15];
        int idx = 0;

        for (int row = size - 1; row >= size - 7; row--) {
            bits[idx++] = matrix[row][8];
        }
        for (int col = size - 8; col < size; col++) {
            bits[idx++] = matrix[8][col];
        }

        return bitsToIntMsb(bits);
    }

    private static int bitsToIntMsb(int[] bits) {
        int value = 0;
        for (int bit : bits) {
            value = (value << 1) | bit;
        }
        return value;
    }

    private static int readVersionCopyA(int[][] matrix, int size) {
        int[] bits = new int[18];
        int idx = 0;

        for (int col = 0; col < 6; col++) {
            for (int row = 0; row < 3; row++) {
                bits[idx++] = matrix[size - 11 + row][col];
            }
        }

        return bitsToInt(bits);
    }

    private static int readVersionCopyB(int[][] matrix, int size) {
        int[] bits = new int[18];
        int idx = 0;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 3; col++) {
                bits[idx++] = matrix[row][size - 11 + col];
            }
        }

        return bitsToInt(bits);
    }

    private static int expectedFormatBits(ErrorCorrection ec, int mask) {
        int ecBits = switch (ec) {
            case L -> 0b01;
            case M -> 0b00;
            case Q -> 0b11;
            case H -> 0b10;
        };
        int data = (ecBits << 3) | mask;
        return buildBchCode(data, 10, 0b10100110111) ^ 0b101010000010010;
    }

    private static int expectedVersionBits(int version) {
        return buildBchCode(version & 0x3F, 12, 0b1111100100101);
    }

    private static int buildBchCode(int dataBits, int ecBitCount, int generatorPolynomial) {
        int dataWithZeros = dataBits << ecBitCount;
        int remainder = dataWithZeros;

        while ((31 - Integer.numberOfLeadingZeros(remainder)) >= ecBitCount) {
            int shift = (31 - Integer.numberOfLeadingZeros(remainder))
                    - (31 - Integer.numberOfLeadingZeros(generatorPolynomial));
            remainder ^= (generatorPolynomial << shift);
        }

        int onlyEcBits = remainder;
        onlyEcBits &= ecBitCount == 10 ? 0x3FF : 0xFFF;
        return dataWithZeros | onlyEcBits;
    }

    private static int bitsToInt(int[] bits) {
        int value = 0;
        for (int i = 0; i < bits.length; i++) {
            value |= (bits[i] << i);
        }
        return value;
    }
}
