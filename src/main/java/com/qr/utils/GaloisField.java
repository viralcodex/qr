package com.qr.utils;

import static com.constants.Constants.GF_EXP;
import static com.constants.Constants.GF_LOG;
import static com.constants.Constants.PRIMITIVE_POLYNOMIAL;

public class GaloisField {

    public static void initGaloisTables(){
        int exponent = 1;
        for (int expIndex = 0; expIndex < 255; expIndex++) {

            GF_EXP[expIndex] = exponent; // store the value of alpha^i in GF(256)
            GF_LOG[exponent] = expIndex; // store the log base alpha of exponent in GF(256)

            exponent <<= 1; // multiply by 2 in GF(256)

            if (exponent >= 256) {
                exponent ^= PRIMITIVE_POLYNOMIAL; // reduce by primitive polynomial
            }
        }

        for(int value = 255; value < 512; value++) {
            GF_EXP[value] = GF_EXP[value - 255]; // to avoid modulus during multiplication
        }
    }

    //Addition in GF(256) is just XOR
    public static int gfAdd(int a, int b)
    {
        return a ^ b;
    }

    // Multiplication in GF(256) using log and exponent tables
    public static int gfMultiply(int a, int b)
    {
        if( a== 0 || b== 0) return 0;
        int logA = GF_LOG[a];
        int logB = GF_LOG[b];
        int logResult = (logA + logB);
        return GF_EXP[logResult];
    }

}
