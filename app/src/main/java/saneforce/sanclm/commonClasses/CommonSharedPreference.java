package saneforce.sanclm.commonClasses;

import android.content.Context;
import android.content.SharedPreferences;

import saneforce.sanclm.R;


public class CommonSharedPreference {
    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferencesFeedback;

    public CommonSharedPreference(Context context) {
        sharedpreferences = context.getSharedPreferences(context.getResources().getString(R.string.preference_name), Context.MODE_PRIVATE);
        sharedpreferencesFeedback = context.getSharedPreferences("feedback", Context.MODE_PRIVATE);
    }

    public void setValueToPreference(String mTAG, String mValue) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(mTAG, mValue);
        editor.apply();
    }

    public void setValueToPreference(String mTAG, int mValue) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(mTAG, mValue);
        editor.apply();
    }

    public void setValueToPreferenceFeed(String mTAG, String mValue) {
        SharedPreferences.Editor editor = sharedpreferencesFeedback.edit();
        editor.putString(mTAG, mValue);
        editor.apply();
    }

    public void setValueToPreferenceFeed(String mTAG, int mValue) {
        SharedPreferences.Editor editor = sharedpreferencesFeedback.edit();
        editor.putInt(mTAG, mValue);
        editor.apply();
    }

    public void setValueToPreference(String mTAG, Boolean mValue) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(mTAG, mValue);
        editor.apply();
    }

    public Boolean getBooleanValueFromPreference(String mTAG) {
        Boolean mResult = false;

        if (sharedpreferences.contains(mTAG)) {
            mResult = sharedpreferences.getBoolean(mTAG, false);
        }
        return mResult;
    }

    public String getValueFromPreference(String mTAG) {
        String mResult = "null";

        if (sharedpreferences.contains(mTAG)) {
            mResult = sharedpreferences.getString(mTAG, "null");
        }
        return mResult;
    }

    public int getValueFromPreference(String mTAG, int vv) {
        int mResult = 0;

        if (sharedpreferences.contains(mTAG)) {
            mResult = sharedpreferences.getInt(mTAG, 0);
        }
        return mResult;
    }

    public String getValueFromPreferenceFeed(String mTAG) {
        String mResult = "null";

        if (sharedpreferencesFeedback.contains(mTAG)) {
            mResult = sharedpreferencesFeedback.getString(mTAG, "null");
        }
        return mResult;
    }

    public int getValueFromPreferenceFeed(String mTAG, int vv) {
        int mResult = 0;

        if (sharedpreferencesFeedback.contains(mTAG)) {
            mResult = sharedpreferencesFeedback.getInt(mTAG, 0);
        }
        return mResult;
    }

    public void clearFeedShare() {
        SharedPreferences.Editor editor = sharedpreferencesFeedback.edit();
        editor.clear();
        editor.apply();
    }


    public boolean isTagAvailableInPreference(String mTAG) {
        return sharedpreferences.contains(mTAG);
    }

    public boolean removeTagAvailableInPreference(String mTAG) {
        return sharedpreferences.edit().remove(mTAG).commit();
    }

    public void getValueToPreference(String savedcalls, String s) {
    }
}
