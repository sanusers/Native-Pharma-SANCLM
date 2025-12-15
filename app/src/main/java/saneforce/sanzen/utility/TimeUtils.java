package saneforce.sanzen.utility;

import android.annotation.SuppressLint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    public static final String FORMAT_19 = "dd MMM yyyy";
    public static final String FORMAT_20 = "E MMM dd HH:mm:ss z yyyy";
    public static final String FORMAT_21 = "yyyy-M-d";
    public static final String FORMAT_22 = "yyyy-MM-dd HH:mm:ss.SSSS";
    public static final String FORMAT_23 = "MMMM yyyy";
    public static final String FORMAT_24 = "yyyy-MM";
    public static final String FORMAT_25 = "MMMM";
    public static final String FORMAT_26 = "yyyy";
    public static final String FORMAT_27 = "MMMM d, yyyy";
    public static final String FORMAT_28 = "d";
    public static final String FORMAT_29 = "HH:mm";
    public static final String FORMAT_30 = "dd-MM-yyyy HH:mm";
    public static final String FORMAT_31 = "M";
    public static final String FORMAT_32 = "HH:mm:ss";
    public static final String FORMAT_33 = "hh:mm:ss a";
    public static final String FORMAT_34 = "d-M-yyyy";
    public static final String FORMAT_35 = "d-MMM";
    public static final String FORMAT_36 = "yyyy-MM-dd hh:mm a";
    public static final String FORMAT_37 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_38 = "d MMMM yyyy";
    public static final String FORMAT_39 = "dd-MM-yyyy hh:mm a";
    public static final String FORMAT_40 = "mm:ss";
    public static final String FORMAT_43 = "MMM dd, yyyy | EEEE";

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

    public static String GetConvertedDate(String currentFormat, String requiredFormat, String mDate) {
        SimpleDateFormat currentDateFormat = new SimpleDateFormat(currentFormat, Locale.ENGLISH);
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat(requiredFormat, Locale.ENGLISH);
        String outputDate = null;
        try {
            Date ConvertedDate = currentDateFormat.parse(mDate);
            outputDate = requiredDateFormat.format(Objects.requireNonNull(ConvertedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDate;
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

    public static String timeDifference(String startTime, String endTime) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_32);
            long differenceInMillis = simpleDateFormat.parse(endTime).getTime() - simpleDateFormat.parse(startTime).getTime();
            long mins = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis);
            long secs = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis)%60;
            return String.format("%02d:%02d",mins, secs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long timeDifferenceInMillis(String startTime, String endTime) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_1);
            return simpleDateFormat.parse(endTime).getTime() - simpleDateFormat.parse(startTime).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String addTime(String oldTime, String newTime) {
        try {
            if(!oldTime.isEmpty() && !newTime.isEmpty()) {
                LocalTime oldLocalTime = LocalTime.parse(oldTime);
                LocalTime newLocalTime = LocalTime.parse(newTime);
                LocalTime resultTime = oldLocalTime.plusHours(newLocalTime.getHour()).plusMinutes(newLocalTime.getMinute()).plusSeconds(newLocalTime.getSecond());
                return resultTime.format(DateTimeFormatter.ofPattern(FORMAT_32));
            }else if(!newTime.isEmpty()) return newTime;
            else if(!oldTime.isEmpty()) return oldTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String timeDurationHMS(String startTime, String endTime) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_32);
            long differenceInMillis = simpleDateFormat.parse(endTime).getTime() - simpleDateFormat.parse(startTime).getTime();
            long hrs = TimeUnit.MILLISECONDS.toHours(differenceInMillis);
            long mins = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis)%60;
            long secs = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis)%60;
            return String.format("%02d:%02d:%02d", hrs, mins, secs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "time";
    }
    public static String timeConverter(String time){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());

        try {
            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String multiplyTime(String time, String format, int noOfTimes) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat(format);
            Date dateTime = timeFormat.parse(time);

            if(dateTime != null) {
                long totalSeconds = dateTime.getTime() / 1000;
                long multipliedSeconds = totalSeconds * noOfTimes;

                Date multipliedTime = new Date(multipliedSeconds * 1000);

                return timeFormat.format(multipliedTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getMilliSeconds(String format, String time) {
        long millis = 0L;
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat(format);
            Date dateTime = timeFormat.parse(time);

            if(dateTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateTime);

                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);

                millis = ((hours * 3600) + (minutes * 60) + seconds) * 1000;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    public static String getMillisToFormattedTime(long millis, String format) {
        String time = "";
        int hours = (int) (millis / 1000) / 3600;
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        if(format.equalsIgnoreCase(FORMAT_32)) {
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if(format.equalsIgnoreCase(FORMAT_29)) {
            time = String.format("%02d:%02d", hours, minutes);
        } else if(format.equalsIgnoreCase(FORMAT_40)) {
            time = String.format("%02d:%02d", minutes, seconds);
        }
        return time;
    }

    public static boolean isBeforeOrToday(String date, String givenFormat) {
        String parsedDate = GetConvertedDate(givenFormat, FORMAT_4, date);
        LocalDate givenDate = LocalDate.parse(parsedDate);
        LocalDate today = LocalDate.now();
        return !givenDate.isAfter(today);
    }

    public static String formatFullDate(String day, String monthYear) {
        try {
            String input = day + " " + monthYear;
            SimpleDateFormat inputFormat = new SimpleDateFormat(FORMAT_17, Locale.getDefault());
            Date date = inputFormat.parse(input);
            SimpleDateFormat outputFormat = new SimpleDateFormat(FORMAT_43, Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getOrdinal(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1: return day + "st";
            case 2: return day + "nd";
            case 3: return day + "rd";
            default: return day + "th";
        }
    }

    public static SpannableString getSuperscriptOrdinalDate(String day) {
        int dayInt = Integer.parseInt(day);
        String ordinal = getOrdinal(dayInt);
        String fullText = dayInt + ordinal;
        SpannableString spannable = new SpannableString(fullText);
        int start = String.valueOf(dayInt).length();
        int end = start + ordinal.length();
        spannable.setSpan(new SuperscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.6f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static SpannableString getSuperscriptOrdinalDateWithMonth(String day, String monthYear) {
        int dayInt = Integer.parseInt(day);
        String ordinal = getOrdinal(dayInt);
        String[] parts = monthYear.split(" ");
        String shortMonth = parts[0].substring(0, 3).toLowerCase();
        String fullText = dayInt + ordinal + " " + shortMonth;
        SpannableString spannable = new SpannableString(fullText);
        int start = String.valueOf(dayInt).length();
        int end = start + ordinal.length();
        spannable.setSpan(new SuperscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.6f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static String formatShortDate(String day, String monthYear) {
        try {
            int dayInt = Integer.parseInt(day);
            String[] parts = monthYear.split(" ");
            String month = parts[0];
            String shortMonth = month.substring(0, 3).toLowerCase();
            return getOrdinal(dayInt) + " " + shortMonth;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}