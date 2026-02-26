package com.qr.utils;

import com.constants.ErrorCorrection;
import com.constants.Modes;
import org.junit.jupiter.api.Test;

import static com.qr.utils.QRCapacity.getVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QRTest {

    @Test
    void testVersionForNumericString() {
        // V1 capacity: L=41, M=34, Q=27, H=17
        assertEquals(1, getVersion(5, ErrorCorrection.L, Modes.NUMERIC));
        assertEquals(1, getVersion(17, ErrorCorrection.H, Modes.NUMERIC));
        
        // V2 capacity: L=77, M=63, Q=48, H=34
        assertEquals(2, getVersion(50, ErrorCorrection.L, Modes.NUMERIC));
        assertEquals(2, getVersion(18, ErrorCorrection.H, Modes.NUMERIC));
        
        // V3 capacity: H=58
        assertEquals(3, getVersion(50, ErrorCorrection.H, Modes.NUMERIC));
    }

    @Test
    void testVersionForAlphanumericString() {
        // V1 capacity: L=25, M=20, Q=16, H=10
        assertEquals(1, getVersion(5, ErrorCorrection.L, Modes.ALPHANUMERIC));
        assertEquals(1, getVersion(10, ErrorCorrection.H, Modes.ALPHANUMERIC));
        
        // V2 capacity: M=38
        assertEquals(2, getVersion(30, ErrorCorrection.M, Modes.ALPHANUMERIC));
        
        // V5 capacity: Q=87
        assertEquals(5, getVersion(80, ErrorCorrection.Q, Modes.ALPHANUMERIC));
    }

    @Test
    void testVersionForByteString() {
        // V1 capacity: L=17, M=14, Q=11, H=7
        assertEquals(1, getVersion(5, ErrorCorrection.L, Modes.BYTE));
        assertEquals(1, getVersion(7, ErrorCorrection.H, Modes.BYTE));
        
        // V2 capacity: M=26
        assertEquals(2, getVersion(20, ErrorCorrection.M, Modes.BYTE));
        
        // V6 capacity: H=58
        assertEquals(6, getVersion(50, ErrorCorrection.H, Modes.BYTE));
    }

    @Test
    void testVersionEdgeCases() {
        // Exactly at capacity boundary (BYTE V1 L=17)
        assertEquals(1, getVersion(17, ErrorCorrection.L, Modes.BYTE));
        assertEquals(2, getVersion(18, ErrorCorrection.L, Modes.BYTE));
        
        // Single character
        assertEquals(1, getVersion(1, ErrorCorrection.H, Modes.NUMERIC));
        
        // Data too large for any version returns -1
        assertEquals(-1, getVersion(1000, ErrorCorrection.H, Modes.BYTE));
    }
}
