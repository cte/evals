import java.util.ArrayList;
import java.util.List;

class Series {
    private String seriesString;

    Series(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Input string cannot be null.");
        }
        if (string.isEmpty()) {
            throw new IllegalArgumentException("series cannot be empty");
        }
        this.seriesString = string;
    }

    List<String> slices(int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("slice length cannot be negative or zero");
        }
        if (num > this.seriesString.length()) {
            throw new IllegalArgumentException("slice length cannot be greater than series length");
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i <= this.seriesString.length() - num; i++) {
            result.add(this.seriesString.substring(i, i + num));
        }
        return result;
    }
}
