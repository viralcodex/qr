package com.qr;

import java.util.Arrays;

import com.constants.Constants;
import com.constants.ErrorCorrection;

public class QRGenerator {
    private static int[][] modules;
    private static int size = 0;
    private static int version = 1;

    public static int[][] generate(String input, ErrorCorrection errorCorrectionLevel) {

        String[] data = QREncoder.encode(input, errorCorrectionLevel);

        version = Integer.parseInt(data[1]);

        generate();
        
        //TODO: data placement

        return modules;
    }

    public static void generate() {
        size = 21 + (version - 1) * 4;

        modules = new int[size][size];

        for (int i = 0; i < size; i++) {
            Arrays.fill(modules[i], -1);
        }

        addFinders();
        addSeparators();

        if (version >= 2)
            addAlignmentPatterns();

        addTimingPatterns();
        addDarkModule();
        addFormatInfo();

        if(version >= 7)
            addVersionInfo();
    }

    private static void addFinders() {

        for (int i = 0; i < 7; i++) {
            // top row
            modules[0][i] = 1; // top left
            modules[0][size - 7 + i] = 1; // top right
            modules[size - 7][i] = 1; // bottom left

            // bottom row
            modules[6][i] = 1; // top left
            modules[6][size - 7 + i] = 1; // top right
            modules[size - 1][i] = 1; // bottom left

            // left column
            modules[i][0] = 1; // top left
            modules[i][size - 7] = 1; // top right
            modules[size - 7 + i][0] = 1; // bottom left

            // right column
            modules[i][6] = 1; // top left
            modules[i][size - 1] = 1; // top right
            modules[size - 7 + i][6] = 1; // bottom left
        }

        for (int i = 0; i < 5; i++) {
            // top row
            modules[1][i + 1] = 0; // top left
            modules[1][size - 6 + i] = 0; // top right
            modules[size - 6][i + 1] = 0; // bottom left

            // bottom row
            modules[5][i + 1] = 0; // top left
            modules[5][size - 6 + i] = 0; // top right
            modules[size - 2][i + 1] = 0; // bottom left

            // left column
            modules[i + 1][1] = 0; // top left
            modules[i + 1][size - 6] = 0; // top right
            modules[size - 6 + i][1] = 0; // bottom left

            // right column
            modules[i + 1][5] = 0; // top left
            modules[i + 1][size - 2] = 0; // top right
            modules[size - 6 + i][5] = 0; // bottom left
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                modules[i + 2][j + 2] = 1; // top left
                modules[i + 2][size - 5 + j] = 1; // top right
                modules[size - 5 + i][j + 2] = 1; // bottom left
            }
        }
    }

    private static void addSeparators() {
        for (int i = 0; i < 8; i++) {
            modules[7][i] = 0; // top left
            modules[7][size - 8 + i] = 0; // top right
            modules[size - 8][i] = 0; // bottom left

            modules[i][7] = 0; // top left
            modules[i][size - 8] = 0; // top right
            modules[size - 8 + i][7] = 0; // bottom left

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
            modules[r - 2][i] = 1;
            modules[r + 2][i] = 1;
        }
        // left and right columns
        for (int i = r - 2; i <= r + 2; i++) {
            modules[i][c - 2] = 1;
            modules[i][c + 2] = 1;
        }

        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                modules[i][j] = 0;
            }
        }
        modules[r][c] = 1;
    }

    private static void addTimingPatterns(){
        for(int i = 8; i < size - 8; i++)
        {
            if(modules[i][6] == -1)
                modules[i][6] = (i % 2 == 0) ? 1 : 0;

            if(modules[6][i] == -1)
                modules[6][i] = (i % 2 == 0) ? 1 : 0;
        }
    }

    private static void addDarkModule(){
        modules[4 * version + 9][8] = 1; //constant
    }

    //reserved bits below (format + version)
    private static void addFormatInfo(){
        // Top-left copy (around row 8 / col 8)
        for (int c = 0; c <= 8; c++) {
            if (c == 6)
                continue; // timing intersection
            if (modules[8][c] == -1)
                modules[8][c] = -2;
        }
        for (int r = 0; r <= 8; r++) {
            if (r == 6)
                continue; // timing intersection
            if (modules[r][8] == -1)
                modules[r][8] = -2;
        }

        // Top-right copy on row 8: last 8 cells
        for (int c = size - 8; c < size; c++) {
            if (modules[8][c] == -1)
                modules[8][c] = -2;
        }

        // Bottom-left copy on col 8: last 8 cells
        for (int r = size - 8; r < size; r++) {
            if (modules[r][8] == -1)
                modules[r][8] = -2;
        }
    }

    private static void addVersionInfo() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                modules[j][size - 11 + i] = -2;
                modules[size - 11 + i][j] = -2;
            }
        }
    }
}
