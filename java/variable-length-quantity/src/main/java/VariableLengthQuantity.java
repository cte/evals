import java.util.List;

class VariableLengthQuantity {

    List<String> encode(List<Long> numbers) {
        List<String> encoded = new java.util.ArrayList<>();
        for (Long number : numbers) {
            if (number == 0) {
                encoded.add("0x0");
                continue;
            }
            java.util.List<Long> bytes = new java.util.ArrayList<>();
            long n = number;
            while (n > 0) {
                long byteVal = n & 0x7F;
                bytes.add(byteVal);
                n >>= 7;
            }
            for (int i = bytes.size() - 1; i >= 0; i--) {
                long b = bytes.get(i);
                if (i != 0) {
                    b |= 0x80;
                }
                encoded.add(String.format("0x%x", b));
            }
        }
        return encoded;
    }

    List<String> decode(List<Long> bytes) {
        List<String> decoded = new java.util.ArrayList<>();
        long value = 0;
        for (int i = 0; i < bytes.size(); i++) {
            long b = bytes.get(i);
            value = (value << 7) | (b & 0x7F);
            if ((b & 0x80) == 0) {
                decoded.add(String.format("0x%x", value));
                value = 0;
            } else if (i == bytes.size() - 1) {
                throw new IllegalArgumentException("Invalid variable-length quantity encoding");
            }
        }
        if (value != 0) {
            throw new IllegalArgumentException("Invalid variable-length quantity encoding");
        }
        return decoded;
    }
}
