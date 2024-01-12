package saneforce.santrip.commonClasses;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private final int min;
    private final int max;

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input)) return null;
        } catch (NumberFormatException ignored) {
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
