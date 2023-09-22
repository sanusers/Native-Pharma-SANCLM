package saneforce.sanclm.utility;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TimeUtils {
    private static final String TAG = TimeUtils.class.getSimpleName();
    public static final String FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_2 = "dd-MM-yyyy HH:mm:ss";
    public static final String FORMAT_3 = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_4 = "yyyy-MM-dd";
    public static final String FORMAT_5 = "dd-MM-yyyy";
    public static final String FORMAT_6 = "dd/MM/yyyy";
    public static final String FORMAT_7 = "dd";
    public static final String FORMAT_8 = "MM";
    public static final String FORMAT_9 = "MMM";
    public static final String FORMAT_10 = "yyyy";
    public static final String FORMAT_11 = "dd-MMM-yyyy";
    public static final String FORMAT_12 = "MMM dd, yyyy";
    public static final String FORMAT_13 = "01/MM/yyyy";
    public static final String FORMAT_14 = "yyyy-MM-01";
    public static final String FORMAT_15 = "yyyy-MM-dd 00:00:00.000";
    public static final String FORMAT_16 = "dd MMM yyyy hh:mm a";
    public static final String FORMAT_17 = "dd MMMM yyyy";
    public static final String FORMAT_18 = "MMM dd,yyyy";

//    public static final String FORMAT_16 = "yyyy,MM,dd";




    public static String getCurrentDateTime(String format) {
        long timestampMilliseconds = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return simpleDateFormat.format(new Date(timestampMilliseconds));
    }


    public static String GetCurrentTimeStamp(String mFormat) {
        String stringDate;
        long timestampMilliseconds = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mFormat, Locale.ENGLISH);
        stringDate = simpleDateFormat.format(new Date(timestampMilliseconds));
        return stringDate;
    }

    public static long GetTimeStamp(String mDate, String mFormat) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(mFormat, Locale.ENGLISH);
            date = sdf.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(date).getTime();
    }

    public static String GetCurrentDateTime(String format) {
        long timestampMilliseconds = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        String Str_Date = simpleDateFormat.format(new Date(timestampMilliseconds));
        Log.d(TAG, "GetCurrentDateTime: => " + Str_Date);
        return Str_Date;
    }

    public static String GetNextDateTime() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH) + 1;
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String Str_Date = day + "/" + (month + 1) + "/" + year;
        Log.d(TAG, "GetNextDateTime: => " + Str_Date);
        return Str_Date;
    }

    public static String GetConvertedDate(String CurrentFormat, String RequiredFormat, String mDate) {
        SimpleDateFormat CurrentDateFormat = new SimpleDateFormat(CurrentFormat, Locale.ENGLISH);
        SimpleDateFormat RequiredDateFormat = new SimpleDateFormat(RequiredFormat, Locale.ENGLISH);
        String OutputDate = null;
        try {
            Date ConvertedDate = CurrentDateFormat.parse(mDate);
            OutputDate = RequiredDateFormat.format(Objects.requireNonNull(ConvertedDate));
            Log.d(TAG, "Formatted Date : " + OutputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return OutputDate;
    }

    public static boolean GetIsBetweenDate(String mGivenDate, String mStartDate, String mEndDate) {
        Date D_GivenDate = null;
        Date D_StartDate = null;
        Date D_EndDate = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtils.FORMAT_15);
            D_GivenDate = dateFormat.parse(mGivenDate);
            D_StartDate = dateFormat.parse(mStartDate);
            D_EndDate = dateFormat.parse(mEndDate);
            if (D_GivenDate != null && D_StartDate != null && D_EndDate != null) {
                return D_GivenDate.after(D_StartDate) && D_GivenDate.before(D_EndDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}