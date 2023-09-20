package saneforce.sanclm.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormat {

    public static final String FORMAT_1 = "dd MMM yyyy hh:mm a";

    public static String getCurrentDateTime(String format) {
        long timestampMilliseconds = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return simpleDateFormat.format(new Date(timestampMilliseconds));
    }
}
