package com.qr;

import java.util.Arrays;

import com.constants.Constants;
import com.constants.ErrorCorrection;

public class QRGenerator {
    private static int[][] modules;
    private static boolean[][] isFunction;
    private static int size = 0;
    private static int version = 1;
    private static String encodedString;

    private static ErrorCorrection ecLevel;
    private static int selectedMask = 0;

    public static int[][] generate(String input, ErrorCorrection errorCorrectionLevel) {
        String[] data;

        data = QREncoder.encode(input, errorCorrectionLevel);

        encodedString = data[0];
        version = Integer.parseInt(data[1]);
        ecLevel = errorCorrectionLevel;

        generate();

        return modules;
    }

    private static void generate() {
        size = 21 + (version - 1) * 4;

        modules = new int[size][size];
        isFunction = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            Arrays.fill(modules[i], -1);
            Arrays.fill(isFunction[i], false);
        }

        addFinders();
        addSeparators();

        if (version >= 2)
            addAlignmentPatterns();

        addTimingPatterns();
        addDarkModule();
        addFormatModules();

        if (version >= 7)
            addVersionModules();

        addDataBits();

        masking();

        addFormatInfo();

        if (version >= 7)
            addVersionInfo();
    }

    private static void setFunctionModule(int row, int col, int value) {
        modules[row][col] = value;
        isFunction[row][col] = true;
    }

    private static void reserveFunctionModule(int row, int col) {
        if (modules[row][col] == -1) {
            modules[row][col] = -2;
        }
        isFunction[row][col] = true;
    }

    private static void addFinders() {

        for (int i = 0; i < 7; i++) {
            // top row
            setFunctionModule(0, i, 1); // top left
            setFunctionModule(0, size - 7 + i, 1); // top right
            setFunctionModule(size - 7, i, 1); // bottom left

            // bottom row
            setFunctionModule(6, i, 1); // top left
            setFunctionModule(6, size - 7 + i, 1); // top right
            setFunctionModule(size - 1, i, 1); // bottom left

            // left column
            setFunctionModule(i, 0, 1); // top left
            setFunctionModule(i, size - 7, 1); // top right
            setFunctionModule(size - 7 + i, 0, 1); // bottom left

            // right column
            setFunctionModule(i, 6, 1); // top left
            setFunctionModule(i, size - 1, 1); // top right
            setFunctionModule(size - 7 + i, 6, 1); // bottom left
        }

        for (int i = 0; i < 5; i++) {
            // top row
            setFunctionModule(1, i + 1, 0); // top left
            setFunctionModule(1, size - 6 + i, 0); // top right
            setFunctionModule(size - 6, i + 1, 0); // bottom left

            // bottom row
            setFunctionModule(5, i + 1, 0); // top left
            setFunctionModule(5, size - 6 + i, 0); // top right
            setFunctionModule(size - 2, i + 1, 0); // bottom left

            // left column
            setFunctionModule(i + 1, 1, 0); // top left
            setFunctionModule(i + 1, size - 6, 0); // top right
            setFunctionModule(size - 6 + i, 1, 0); // bottom left

            // right column
            setFunctionModule(i + 1, 5, 0); // top left
            setFunctionModule(i + 1, size - 2, 0); // top right
            setFunctionModule(size - 6 + i, 5, 0); // bottom left
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                setFunctionModule(i + 2, j + 2, 1); // top left
                setFunctionModule(i + 2, size - 5 + j, 1); // top right
                setFunctionModule(size - 5 + i, j + 2, 1); // bottom left
            }
        }
    }

    private static void addSeparators() {
        for (int i = 0; i < 8; i++) {
            setFunctionModule(7, i, 0); // top left
            setFunctionModule(7, size - 8 + i, 0); // top right
            setFunctionModule(size - 8, i, 0); // bottom left

            setFunctionModule(i, 7, 0); // top left
            setFunctionModule(i, size - 8, 0); // top right
            setFunctionModule(size - 8 + i, 7, 0); // bottom left

        }
    }

    private static void addAlignmentPatterns() {
        int[] centers = Constants.ALIGNMENT_PATTERN_CENTERS[version - 1];

        for (int r : centers) {
            for (int c : centers) {
                if ((r == 6 && c == 6) ||
                        (r == 6 && c == centers[centers.length - 1]) ||
                        (r == centers[centers.length - 1] && c == 6)) {
                    continue;
                } // all of these overlap with finders
                placeAlignmentAt(r, c);
            }
        }
    }

    private static void placeAlignmentAt(int r, int c) {
        // top and bottom rows
        for (int i = c - 2; i <= c + 2; i++) {
            setFunctionModule(r - 2, i, 1);
            setFunctionModule(r + 2, i, 1);
        }
        // left and right columns
        for (int i = r - 2; i <= r + 2; i++) {
            setFunctionModule(i, c - 2, 1);
            setFunctionModule(i, c + 2, 1);
        }

        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                setFunctionModule(i, j, 0);
            }
        }
        setFunctionModule(r, c, 1);
    }

    private static void addTimingPatterns() {
        for (int i = 8; i < size - 8; i++) {
            if (modules[i][6] == -1)
                setFunctionModule(i, 6, (i % 2 == 0) ? 1 : 0);

            if (modules[6][i] == -1)
                setFunctionModule(6, i, (i % 2 == 0) ? 1 : 0);
        }
    }

    private static void addDarkModule() {
        setFunctionModule(4 * version + 9, 8, 1); // constant
    }

    // reserved bits below (format + version)
    private static void addFormatModules() {
        // Top-left copy (around row 8 / col 8)
        for (int c = 0; c <= 8; c++) {
            if (c == 6)
                continue; // timing intersection
            reserveFunctionModule(8, c);
        }
        for (int r = 0; r <= 8; r++) {
            if (r == 6)
                continue; // timing intersection
            reserveFunctionModule(r, 8);
        }

        // Top-right copy on row 8: last 8 cells
        for (int c = size - 8; c < size; c++) {
            reserveFunctionModule(8, c);
        }

        // Bottom-left copy on col 8: last 8 cells
        for (int r = size - 8; r < size; r++) {
            reserveFunctionModule(r, 8);
        }
    }

    private static void addVersionModules() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                reserveFunctionModule(j, size - 11 + i);
                reserveFunctionModule(size - 11 + i, j);
            }
        }
    }

    private static void addDataBits() {
        int bitIndex = 0; // which index we are in the data string
        int col = size - 1;
        boolean goingUp = true;
        while (col > 0) {
            if (col == 6)
                col--; // skip over vertical timing pattern

            for (int step = 0; step < size; step++) {
                int row = goingUp ? (size - 1 - step) : step;

                bitIndex = placeBits(row, col, bitIndex); // right module at the current step
                bitIndex = placeBits(row, col - 1, bitIndex); // left module at the current step
            }
            col -= 2;
            goingUp = !goingUp;
        }
    }

    private static int placeBits(int row, int col, int bitIndex) {
        if (isFunction[row][col] || modules[row][col] != -1)
            return bitIndex;

        int bit = 0;

        if (bitIndex < encodedString.length()) {
            bit = encodedString.charAt(bitIndex) - '0';
            bitIndex++;
        }

        modules[row][col] = bit;

        return bitIndex;
    }

    private static void masking() {
        int[] penalties = new int[8];
        int mask = 0;

        int[][] bestModules = null;
        int bestPenalty = Integer.MAX_VALUE;
        int bestMask = 0;

        while (mask < 8) {
            int[][] temp = copyModules();
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (!isFunction[row][col] && maskCondition(mask, row, col)) {
                        temp[row][col] ^= 1; // flip the bit
                    }
                }
            }
            // Score against a fully populated matrix
            placeFormatBits(temp, buildFormatBits(mask));
            if (version >= 7) {
                placeVersionBits(temp, buildVersionBits());
            }

            penalties[mask] = getPenalty(temp);

            if (penalties[mask] < bestPenalty) {

                bestPenalty = penalties[mask];
                bestMask = mask;
                bestModules = temp;
            }
            mask++;
        }

        selectedMask = bestMask;
        modules = bestModules;
    }

    private static boolean maskCondition(int index, int row, int col) {
        switch (index) {
            case 0:
                return (row + col) % 2 == 0;
            case 1:
                return row % 2 == 0;
            case 2:
                return col % 3 == 0;
            case 3:
                return (row + col) % 3 == 0;
            case 4:
                return ((row / 2) + (col / 3)) % 2 == 0;
            case 5:
                return ((row * col) % 2 + (row * col) % 3) == 0;
            case 6:
                return (((row * col) % 2 + (row * col) % 3) % 2) == 0;
            case 7:
                return (((row + col) % 2 + (row * col) % 3) % 2) == 0;
            default:
                return false;
        }
    }

    private static int getPenalty(int[][] copiedModules) {
        int totalPenalty = 0;

        // First Evaluation
        // col runs
        for (int col = 0; col < size; col++) {
            int runColor = copiedModules[0][col];
            int runLength = 1;
            for (int row = 1; row < size; row++) {
                if (copiedModules[row][col] == runColor) {
                    runLength++;
                } else {
                    if (runLength >= 5) {
                        totalPenalty += 3 + (runLength - 5); // +1 for every same color after 5 of same colors
                    }
                    runColor = copiedModules[row][col];
                    runLength = 1;
                }
            }
            // close trailing run
            if (runLength >= 5) {
                totalPenalty += 3 + (runLength - 5);
            }
        }

        // row runs
        for (int row = 0; row < size; row++) {
            int runColor = copiedModules[row][0];
            int runLength = 1;
            for (int col = 1; col < size; col++) {
                if (copiedModules[row][col] == runColor) {
                    runLength++;
                } else {
                    if (runLength >= 5) {
                        totalPenalty += 3 + (runLength - 5); // +1 for every same color after 5 of same colors
                    }
                    runColor = copiedModules[row][col];
                    runLength = 1;
                }
            }
            // close trailing run
            if (runLength >= 5) {
                totalPenalty += 3 + (runLength - 5);
            }
        }

        // Second Evaluation
        for (int row = 0; row < size - 1; row++) {
            for (int col = 0; col < size - 1; col++) {
                int color = copiedModules[row][col];
                if (copiedModules[row][col + 1] == color && copiedModules[row + 1][col] == color
                        && copiedModules[row + 1][col + 1] == color) {
                    totalPenalty += 3;
                }
            }
        }

        // Third Evaluation
        int[] p1 = { 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0 }; // 10111010000
        int[] p2 = { 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1 }; // 00001011101

        // row scan
        for (int row = 0; row < size; row++) {
            for (int col = 0; col <= size - 11; col++) {
                boolean isP1Found = true;
                boolean isP2Found = true;

                for (int k = 0; k < 11; k++) {
                    int color = copiedModules[row][col + k];

                    if (color != p1[k]) {
                        isP1Found = false;
                    }

                    if (color != p2[k]) {
                        isP2Found = false;
                    }

                    if (!isP1Found && !isP2Found) {
                        break;
                    }
                }

                if (isP1Found || isP2Found) {
                    totalPenalty += 40;
                }
            }
        }

        // column scan
        for (int col = 0; col < size; col++) {
            for (int row = 0; row <= size - 11; row++) {
                boolean isP1Found = true;
                boolean isP2Found = true;

                for (int k = 0; k < 11; k++) {
                    int color = copiedModules[row + k][col];

                    if (color != p1[k]) {
                        isP1Found = false;
                    }

                    if (color != p2[k]) {
                        isP2Found = false;
                    }

                    if (!isP1Found && !isP2Found) {
                        break;
                    }
                }

                if (isP1Found || isP2Found) {
                    totalPenalty += 40;
                }
            }
        }

        // Fourth Evaluation
        int dark = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (copiedModules[row][col] == 1) {
                    dark++;
                }
            }
        }

        int ratio = (dark * 100) / (size * size);

        int remainder = ratio % 5;

        int beforeMultiple = ratio - remainder, afterMultiple = ratio + (5 - remainder);

        int diffLower = Math.abs(afterMultiple - 50), diffUpper = Math.abs(beforeMultiple - 50);

        totalPenalty += Math.min(diffLower / 5, diffUpper / 5) * 10;

        return totalPenalty;
    }

    private static int[][] copyModules() {
        int[][] dst = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(modules[i], 0, dst[i], 0, size);
        }
        return dst;
    }

    private static void addFormatInfo() {
        placeFormatBits(modules, buildFormatBits(selectedMask));
    }

    private static int buildFormatBits(int mask) {
        int dataFiveBits = (getEcBits() << 3) | mask;
        int formatBits = buildBchCode(dataFiveBits, 10, 0b10100110111);
        return formatBits ^ 0b101010000010010;
    }

    private static int getEcBits() {
        switch (ecLevel) {
            case L:
                return 0b01;
            case M:
                return 0b00;
            case Q:
                return 0b11;
            case H:
                return 0b10;
            default:
                return 0b00;
        }
    }

    private static void placeFormatBits(int[][] target, int formatBits) {
        int bitIndex = 0;

        // ltr top left
        for (int col = 0; col < 9; col++) {
            if (target[8][col] == -2) {
                target[8][col] = getBit(formatBits, 14 - bitIndex++);
            }
        }

        // btt top left
        for (int row = 7; row >= 0; row--) {
            if (target[row][8] == -2) {
                target[row][8] = getBit(formatBits, 14 - bitIndex++);
            }
        }

        bitIndex = 0;

        // btt bottom left
        for (int row = size - 1; row >= size - 7; row--) {
            if (target[row][8] == -2) {
                target[row][8] = getBit(formatBits, 14 - bitIndex++);
            }
        }

        // ltr top right
        for (int col = size - 8; col < size; col++) {
            if (target[8][col] == -2) {
                target[8][col] = getBit(formatBits, 14 - bitIndex++);
            }
        }
    }

    private static void addVersionInfo() {
        placeVersionBits(modules, buildVersionBits());
    }

    private static int buildVersionBits() {
        int dataSixBits = version & 0x3F;
        return buildBchCode(dataSixBits, 12, 0b1111100100101);
    }

    private static void placeVersionBits(int[][] target, int versionBits) {
        int bitIndex = 0;

        for (int col = 0; col < 6; col++) {
            for (int row = 0; row < 3; row++) {
                target[size - 11 + row][col] = getBit(versionBits, bitIndex++);
            }
        }

        bitIndex = 0;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 3; col++) {
                target[row][size - 11 + col] = getBit(versionBits, bitIndex++);
            }
        }
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

    private static int getBit(int value, int index) {
        return (value >> index) & 1;
    }
}
