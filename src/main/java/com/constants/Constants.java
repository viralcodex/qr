package com.constants;

public class Constants {
    public static final String ALPHANUMERIC_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";

    //columns: [L, M, Q, H]
    public static final int[][] NUMERIC = {
            {   41,    34,    27,    17 },  // V1
            {   77,    63,    48,    34 },  // V2
            {  127,   101,    77,    58 },  // V3
            {  187,   149,   111,    82 },  // V4
            {  255,   202,   144,   106 },  // V5
            {  322,   255,   178,   139 },  // V6
            {  370,   293,   207,   154 },  // V7
            {  461,   365,   259,   202 },  // V8
            {  552,   432,   312,   235 },  // V9
            {  652,   513,   364,   288 },  // V10
    };

    public static final int[][] ALPHANUMERIC = {
            {   25,    20,    16,    10 },  // V1
            {   47,    38,    29,    20 },  // V2
            {   77,    61,    47,    35 },  // V3
            {  114,    90,    67,    50 },  // V4
            {  154,   122,    87,    64 },  // V5
            {  195,   154,   108,    84 },  // V6
            {  224,   178,   125,    93 },  // V7
            {  279,   221,   157,   122 },  // V8
            {  335,   262,   189,   143 },  // V9
            {  395,   311,   221,   174 },  // V10
    };

    public static final int[][] BYTE = {
            {   17,    14,    11,     7 },  // V1
            {   32,    26,    20,    14 },  // V2
            {   53,    42,    32,    24 },  // V3
            {   78,    62,    46,    34 },  // V4
            {  106,    84,    60,    44 },  // V5
            {  134,   106,    74,    58 },  // V6
            {  154,   122,    86,    64 },  // V7
            {  192,   152,   108,    84 },  // V8
            {  230,   180,   130,    98 },  // V9
            {  271,   213,   151,   119 },  // V10
    };

    // Total number of data codewords per version and EC level
    // columns: [L, M, Q, H]
    // Multiply by 8 to get total bits
    public static final int[][] DATA_CODEWORDS = {
            {   19,    16,    13,     9 },  // V1
            {   34,    28,    22,    16 },  // V2
            {   55,    44,    34,    26 },  // V3
            {   80,    64,    48,    36 },  // V4
            {  108,    86,    62,    46 },  // V5
            {  136,   108,    76,    60 },  // V6
            {  156,   124,    88,    66 },  // V7
            {  194,   154,   110,    86 },  // V8
            {  232,   182,   132,   100 },  // V9
            {  274,   216,   154,   122 },  // V10
    };

    // Each entry: [EC codewords per block, Group1 blocks, Group1 size, Group2 blocks, Group2 size]
    // columns: [L, M, Q, H]

    // Number of error correction codewords per block
    // columns: [L, M, Q, H]
    public static final int[][] EC_CODEWORDS_PER_BLOCK = {
            {  7, 10, 13, 17 },  // V1
            { 10, 16, 22, 28 },  // V2
            { 15, 26, 18, 22 },  // V3
            { 20, 18, 26, 16 },  // V4
            { 26, 24, 18, 22 },  // V5
            { 18, 16, 24, 28 },  // V6
            { 20, 18, 18, 26 },  // V7
            { 24, 22, 22, 26 },  // V8
            { 30, 22, 20, 24 },  // V9
            { 18, 26, 24, 28 },  // V10
    };

    // Number of blocks in Group 1
    // columns: [L, M, Q, H]
    public static final int[][] GROUP1_BLOCKS = {
            { 1, 1, 1, 1 },  // V1
            { 1, 1, 1, 1 },  // V2
            { 1, 1, 2, 2 },  // V3 ]
            { 1, 2, 2, 4 },  // V4
            { 1, 2, 2, 2 },  // V5
            { 2, 4, 4, 4 },  // V6
            { 2, 4, 2, 4 },  // V7
            { 2, 2, 4, 4 },  // V8
            { 2, 3, 4, 4 },  // V9
            { 2, 4, 6, 6 },  // V10
    };

    // Number of data codewords in each of Group 1's blocks
    // columns: [L, M, Q, H]
    public static final int[][] GROUP1_DATA_CODEWORDS = {
            { 19, 16, 13, 9 },  // V1
            { 34, 28, 22, 16 },  // V2
            { 55, 44, 17, 13 },  // V3
            { 80, 32, 24, 9 },  // V4
            { 108, 43, 15, 11 },  // V5
            { 68, 27, 19, 15 },  // V6
            { 78, 31, 14, 13 },  // V7
            { 97, 38, 18, 14 },  // V8
            { 116, 36, 16, 12 },  // V9
            { 68, 43, 19, 15 },  // V10
    };

    // Number of blocks in Group 2 (0 if no Group 2)
    // columns: [L, M, Q, H]
    public static final int[][] GROUP2_BLOCKS = {
            { 0, 0, 0, 0 },  // V1
            { 0, 0, 0, 0 },  // V2
            { 0, 0, 0, 0 },  // V3
            { 0, 0, 0, 0 },  // V4
            { 0, 0, 2, 2 },  // V5
            { 0, 0, 0, 0 },  // V6
            { 0, 0, 4, 1 },  // V7
            { 0, 2, 2, 2 },  // V8
            { 0, 2, 4, 4 },  // V9
            { 2, 1, 2, 2 },  // V10
    };

    // Number of data codewords in each of Group 2's blocks (0 if no Group 2)
    // columns: [L, M, Q, H]
    public static final int[][] GROUP2_DATA_CODEWORDS = {
            { 0, 0, 0, 0 },  // V1
            { 0, 0, 0, 0 },  // V2
            { 0, 0, 0, 0 },  // V3
            { 0, 0, 0, 0 },  // V4
            { 0, 0, 16, 12 },  // V5
            { 0, 0, 0, 0 },  // V6
            { 0, 0, 15, 14 },  // V7
            { 0, 39, 19, 15 },  // V8
            { 0, 37, 17, 13 },  // V9
            { 69, 44, 20, 16 },  // V10
    };

    // Galois Field parameters for GF(256) with primitive polynomial x^8 + x^4 + x^3 + x^2 + 1

    public static int[] GF_EXP = new int[512]; // 512 to avoid modulus during multiplication
    public static int[] GF_LOG = new int[256]; // Initialize Galois Field log table

    public static final int PRIMITIVE_POLYNOMIAL = 0x11d; //285 in decimal, x^8 + x^4 + x^3 + x^2 + 1
}
