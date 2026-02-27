package com.qr;

import com.constants.ErrorCorrection;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class QREncoderTest {

    // Use reflection to test private static methods
    private String invokeEncodeNumeric() throws Exception {
        Method method = QREncoder.class.getDeclaredMethod("encodeNumeric", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, "8675309");
    }

    private String invokeEncodeAlphaNumeric(String input) throws Exception {
        Method method = QREncoder.class.getDeclaredMethod("encodeAlphaNumeric", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, input);
    }

    private String invokeEncodeByte(String input) throws Exception {
        Method method = QREncoder.class.getDeclaredMethod("encodeByte", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, input);
    }

    private String invokeGetNumberBytes(String number) throws Exception {
        Method method = QREncoder.class.getDeclaredMethod("getNumberBytes", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, number);
    }

    // === NUMERIC ENCODING TESTS ===

    @Test
    void testNumericEncoding_ThreeDigits() throws Exception {
        // "123" -> 123 in 10 bits = 0001111011
        assertEquals("0001111011", invokeGetNumberBytes("123"));
    }

    @Test
    void testNumericEncoding_TwoDigits() throws Exception {
        // "45" -> 45 in 7 bits = 0101101
        assertEquals("0101101", invokeGetNumberBytes("45"));
    }

    @Test
    void testNumericEncoding_OneDigit() throws Exception {
        // "6" -> 6 in 4 bits = 0110
        assertEquals("0110", invokeGetNumberBytes("6"));
    }

    @Test
    void testNumericEncoding_FullString() throws Exception {
        // "8675309" -> "867" (10 bits) + "530" (10 bits) + "9" (4 bits)
        // 867 = 1101100011, 530 = 1000010010, 9 = 1001
        String result = invokeEncodeNumeric();
        assertEquals("1101100011" + "1000010010" + "1001", result);
    }

    @Test
    void testNumericEncoding_LeadingZeros() throws Exception {
        // "012" -> 12 in 10 bits = 0000001100
        assertEquals("0000001100", invokeGetNumberBytes("012"));
    }

    // === ALPHANUMERIC ENCODING TESTS ===

    @Test
    void testAlphanumericEncoding_HelloWorld() throws Exception {
        // From Thonky tutorial: "HELLO WORLD"
        // HE=45*17+14=779, LL=45*21+21=966, O =45*24+36=1116
        // WO=45*32+24=1464, RL=45*27+21=1236, D=13
        String result = invokeEncodeAlphaNumeric("HELLO WORLD");
        
        // HE -> 779 -> 01100001011
        // LL -> 966 -> 01111000110
        // O  -> 1116 -> 10001011100
        // WO -> 1464 -> 10110111000
        // RL -> 1236 -> 10011010100
        // D  -> 13 -> 001101
        String expected = "01100001011" + "01111000110" + "10001011100" + 
                          "10110111000" + "10011010100" + "001101";
        assertEquals(expected, result);
    }

    @Test
    void testAlphanumericEncoding_Pair() throws Exception {
        // "AC" -> A=10, C=12 -> 10*45+12 = 462 -> 00111001110 (11 bits)
        String result = invokeEncodeAlphaNumeric("AC");
        assertEquals("00111001110", result);
    }

    @Test
    void testAlphanumericEncoding_SingleChar() throws Exception {
        // "A" -> 10 -> 001010 (6 bits)
        String result = invokeEncodeAlphaNumeric("A");
        assertEquals("001010", result);
    }

    @Test
    void testAlphanumericEncoding_LowercaseConverted() throws Exception {
        // lowercase should be converted to uppercase
        String lower = invokeEncodeAlphaNumeric("ab");
        String upper = invokeEncodeAlphaNumeric("AB");
        assertEquals(upper, lower);
    }

    @Test
    void testAlphanumericEncoding_SpecialChars() throws Exception {
        // Space is valid (index 36), $ is index 37
        // "$ " -> 37*45+36 = 1701 -> 11010100101 (11 bits)
        String result = invokeEncodeAlphaNumeric("$ ");
        assertEquals("11010100101", result);
    }

    // === BYTE ENCODING TESTS ===

    @Test
    void testByteEncoding_Ascii() throws Exception {
        // "A" -> 65 -> 01000001
        String result = invokeEncodeByte("A");
        assertEquals("01000001", result);
    }

    @Test
    void testByteEncoding_Hello() throws Exception {
        // "Hello" -> H(72), e(101), l(108), l(108), o(111)
        String result = invokeEncodeByte("Hello");
        String expected = "01001000" + "01100101" + "01101100" + "01101100" + "01101111";
        assertEquals(expected, result);
    }

    @Test
    void testByteEncoding_NonAscii() throws Exception {
        // "é" in UTF-8 is 0xC3 0xA9 (195, 169)
        String result = invokeEncodeByte("é");
        assertEquals("11000011" + "10101001", result);
    }

    @Test
    void testByteEncoding_Emoji() throws Exception {
        // "😀" in UTF-8 is F0 9F 98 80 (240, 159, 152, 128)
        String result = invokeEncodeByte("😀");
        assertEquals("11110000" + "10011111" + "10011000" + "10000000", result);
    }

    // === INTEGRATION TESTS ===

    @Test
    void testEncode_DoesNotThrowForValidInput() {
        assertDoesNotThrow(() -> QREncoder.encode("HELLO WORLD", ErrorCorrection.Q));
    }

    @Test
    void testEncode_ThrowsForTooLongInput() {
        // Create a string longer than max capacity
        String tooLong = "A".repeat(5000);
        assertThrows(IllegalArgumentException.class, () -> 
            QREncoder.encode(tooLong, ErrorCorrection.H)
        );
    }

    @Test
    void testAlphanumericEncoding_InvalidChar() {
        // Characters like '#' are not in alphanumeric charset
        // This should throw when trying to encode
        assertThrows(Exception.class, () -> invokeEncodeAlphaNumeric("#"));
    }
}
