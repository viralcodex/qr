# QR Code Encoder/Decoder

A QR code encoder and decoder built from scratch in Java without external libraries.

## Features

- **Encode** text strings into QR codes
- **Decode** QR codes back to text
- Multiple **encoding modes**:
  - Numeric (0-9)
  - Alphanumeric (A-Z, 0-9, and special characters)
  - Byte (UTF-8 text)
  - Kanji (planned)
- Configurable **error correction levels**:
  - `L` - Low (~7% recovery)
  - `M` - Medium (~15% recovery)
  - `Q` - Quartile (~25% recovery)
  - `H` - High (~30% recovery)

## Requirements

- Java 17 or higher
- Maven 3.x

## Build

```bash
mvn clean install
```

## Run

```bash
mvn exec:java
```

The interactive CLI will prompt you to:
1. Choose between encoding or decoding
2. Enter the input string
3. Select an error correction level (for encoding)

## Project Structure

```
src/main/java/com/
├── qr/
│   ├── QR.java           # Main entry point
│   └── utils/
│       └── QRUtils.java  # Encoding/decoding utilities
└── constants/
    ├── Constants.java    # Alphanumeric character set
    ├── ErrorCorrection.java  # Error correction levels enum
    └── Modes.java        # Encoding modes enum
```

## Usage Example

```
QR Code Encoder/Decoder
Enter 'encode' to encode a string or 'decode' to decode a QR code:
encode
Enter the string to encode:
HELLO WORLD
Choose the error correction level (L, M, Q, H):
M
Encoded QR Code: Encoded(HELLO WORLD)
```

## License

MIT
