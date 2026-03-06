package com.qr;

import java.util.Scanner;
import com.constants.ErrorCorrection;

public class QR {
    public static void main(String[] args) {
        System.out.println("QR Code Encoder/Decoder");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 'encode' to encode a string or 'decode' to decode a QR code:");
        String choice = scanner.nextLine();
        
        switch (choice.toLowerCase()) {
            case "encode":
                System.out.println("Enter the string to encode:");
                String input = scanner.nextLine();
                
                System.out.println("Choose the error correction level (L, M, Q, H):");
                String ecInput = scanner.nextLine().toUpperCase();
                ErrorCorrection ec;
                try {
                    ec = ErrorCorrection.valueOf(ecInput);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid level. Defaulting to 'M'.");
                    ec = ErrorCorrection.M;
                }
                
                int[][] qrCode = QRGenerator.generate(input, ec);
                System.out.println("Encoded QR Code: " + qrCode);
                break;
                
            case "decode":
                System.out.println("Enter the QR code to decode:");
                String qrInput = scanner.nextLine();
                String decodedString = QRDecoder.decode(qrInput);
                System.out.println("Decoded String: " + decodedString);
                break;
                
            default:
                System.out.println("Invalid choice. Please enter 'encode' or 'decode'.");
        }
        scanner.close();
    }
}