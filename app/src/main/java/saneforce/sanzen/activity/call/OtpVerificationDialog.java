package saneforce.sanzen.activity.call;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class OtpVerificationDialog extends Dialog {
    private final Context context;
    private TextView tvContent,tvTitle;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private Button btnSubmit;
    private TextView tvResend, tvError, tvResendAttemptsLeft;
    private ImageView btnClose;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private ApiInterface api_interface;
    private static final long RESEND_TIMEOUT_MS = 60_000L;   // 60 seconds
    private static final long TICK_INTERVAL_MS = 1_000L;
    private static final int MAX_RESEND_ATTEMPTS = 3;
    private int resendAttemptsLeft = MAX_RESEND_ATTEMPTS;
    private final String customerName;
    private final String customerCode;
    private final String dcrType;
    private final String countryCode;
    private final String mobileNumber;
    private String otpUID;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isDismissing = false;
    private int productCount = 0, inputCount = 0;

    public interface OtpCallback {
        void onVerified();

        void onDismissed();
    }

    private final OtpCallback callback;

    public OtpVerificationDialog(@NonNull Context context, String customerName, String customerCode, String dcrType, int productCount, int inputCount, String countryCode, String mobileNumber, String otpUID, OtpCallback callback) {
        super(context);
        this.context = context;
        this.customerName = customerName;
        this.customerCode = customerCode;
        this.dcrType = dcrType;
        this.productCount = productCount;
        this.inputCount = inputCount;
        this.countryCode = countryCode;
        this.mobileNumber = mobileNumber;
        this.otpUID = otpUID;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_otp_verification);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        setCancelable(false);
        bindViews();
        setData();
        setupOtpNavigation();
        setupSubmitButton();
        setupResendButton();
        setupCloseButton();
        startResendTimer();
        updateAttemptsLabel();
    }

    private void bindViews() {
        tvTitle = findViewById(R.id.tvOTPTitle);
        tvContent = findViewById(R.id.tvOTPContent);
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvResend = findViewById(R.id.tvResend);
        tvResendAttemptsLeft = findViewById(R.id.tvResendAttemptsLeft);
        btnClose = findViewById(R.id.btnClose);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);

        etOtp1.requestFocus();
        showKeyboard(etOtp1);
    }

    private void setupCloseButton() {
        btnClose.setOnClickListener(v -> dismissAsCancelled());
    }

    private void showCloseButton() {
        btnClose.setVisibility(View.VISIBLE);
    }

    private void setData() {
       if(SharedPref.getProductOtpNeed(context).equalsIgnoreCase("0") && SharedPref.getInputOtpNeed(context).equalsIgnoreCase("1")){
           tvTitle.setText(SharedPref.getDocProductCaption(context) + " "+context.getString(R.string.otp_verification));
       } else if (SharedPref.getProductOtpNeed(context).equalsIgnoreCase("1") && SharedPref.getInputOtpNeed(context).equalsIgnoreCase("0")) {
           tvTitle.setText(SharedPref.getDocInputCaption(context) + " "+context.getString(R.string.otp_verification));
       } else if (SharedPref.getProductOtpNeed(context).equalsIgnoreCase("0") && SharedPref.getInputOtpNeed(context).equalsIgnoreCase("0")) {
           tvTitle.setText(SharedPref.getDocProductCaption(context)+ " / "+ SharedPref.getDocInputCaption(context) + " "+context.getString(R.string.otp_verification));
       }
        String last2Digit = "";
        if (mobileNumber.length() > 3) {
            last2Digit = mobileNumber.substring(mobileNumber.length() - 2);
        }
        if(!SharedPref.getDetDrCap(context).isEmpty()){
            tvContent.setText(String.format("One-Time Password sent to ********%s (%s)", last2Digit, SharedPref.getDetDrCap(context)+" "+customerName));
        }else{
            tvContent.setText(String.format("One-Time Password sent to ********%s (%s)", last2Digit, "Doctor"+" "+customerName));
        }
    }

    private void setupOtpNavigation() {
        EditText[] fields = {etOtp1, etOtp2, etOtp3, etOtp4};

        for (int i = 0; i < fields.length; i++) {
            final int index = i;
            final EditText current = fields[i];
            final EditText next = (i < fields.length - 1) ? fields[i + 1] : null;
            final EditText prev = (i > 0) ? fields[i - 1] : null;

            current.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int st, int c, int a) {
                }

                @Override
                public void onTextChanged(CharSequence s, int st, int b, int c) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    hideError();
                    if (s.length() == 1 && next != null) {
                        next.requestFocus();
                    }
                    if (index == 3 && s.length() == 1) {
                        hideKeyboard(current);
                    }
                }
            });

            current.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_DEL
                        && current.getText().length() == 0
                        && prev != null) {
                    prev.requestFocus();
                    prev.setText("");
                    return true;
                }
                return false;
            });
        }
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                String otp = getEnteredOtp();
                if (otp.length() < 4) {
                    showError("Please enter all 4 digits");
                    return;
                }
                verifyOTP(otp);
            }
        });
    }

    private String getEnteredOtp() {
        return etOtp1.getText().toString().trim()
                + etOtp2.getText().toString().trim()
                + etOtp3.getText().toString().trim()
                + etOtp4.getText().toString().trim();
    }

    private void setupResendButton() {
        tvResend.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (tvResend.isEnabled()) {
                    resendOtp();
                }
            }
        });
    }

    private void startResendTimer() {
        tvResend.setTextColor(context.getResources().getColor(R.color.dark_purple));
        tvResend.setEnabled(false);

        countDownTimer = new CountDownTimer(RESEND_TIMEOUT_MS, TICK_INTERVAL_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvResend.setText(String.format("Resend (%d)", seconds));
            }

            @Override
            public void onFinish() {
                showCloseButton();

                if (resendAttemptsLeft > 0) {
                    tvResend.setText("Resend");
                    setResendEnabled(true);
                    showCloseButton();
                } else {
                    tvResend.setText("No resends left");
                    setResendEnabled(false);
                    tvResend.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
//                tvResend.setText("Resend");
//                tvResend.setEnabled(true);
//                tvResend.setTextColor(context.getResources().getColor(R.color.dark_purple));
            }
        }.start();
    }

    private void setResendEnabled(boolean enabled) {
        tvResend.setEnabled(enabled);
        tvResend.setTextColor(enabled ? context.getResources().getColor(R.color.dark_purple) : context.getResources().getColor(R.color.text_grey));
    }

    private void updateAttemptsLabel() {
        if (resendAttemptsLeft > 0) {
            tvResendAttemptsLeft.setText(resendAttemptsLeft + " resend attempt"
                    + (resendAttemptsLeft == 1 ? "" : "s") + " remaining");
            tvResendAttemptsLeft.setTextColor(Color.parseColor("#9CA3AF"));
        } else {
            tvResendAttemptsLeft.setText("No resend attempts remaining");
            tvResendAttemptsLeft.setTextColor(Color.parseColor("#EF4444"));
        }
    }

    private void verifyOTP(String otp) {
        setLoading(true);

//        executor.execute(() -> {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject = CommonUtilsMethods.CommonJsonObjectParameter(context);
            jsonObject.addProperty("OTP_UID", otpUID);
            jsonObject.addProperty("OTP_Code", otp);
            jsonObject.addProperty("SF_Code", SharedPref.getSfCode(context));
            jsonObject.addProperty("Product_Count", productCount);
            jsonObject.addProperty("Input_Count", inputCount);

            Log.d("OTP", "verifyOTP: " + jsonObject.toString());

            if (UtilityClass.isNetworkAvailable(context)) {
                api_interface = RetrofitClient.getRetrofit(context, SharedPref.getAppUrl(context));
                Call<JsonElement> call = api_interface.getJsonElement(SharedPref.getAppUrl(context) + "api/" + SharedPref.getSenderId(context) + "/v3/OTP/Validate/", jsonObject);
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        Log.d("OTP", "onResponse: " + response.body());

                        try {
                            JSONObject jsonObject1 = new JSONObject(response.body().toString());
                            boolean success = jsonObject1.optBoolean("success");
                            String message = jsonObject1.optString("message");
//                                runOnUiThread(() -> {
                            setLoading(false);
                            if (success) {
                                Log.d("OTP", "onResponse verify : " + message);
                                CommonUtilsMethods.showToastMessage(context, message, true);
                                isDismissing = true;
                                if (countDownTimer != null) countDownTimer.cancel();
//                                        executor.shutdownNow();
                                dismiss();
                                if (callback != null) callback.onVerified();
                            } else {
                                Log.d("OTP", "onResponse verify : " + message);
                                CommonUtilsMethods.showToastMessage(context, message, true);
                                showError(message);
                                clearOtpFields();
                            }
//                                });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        t.printStackTrace();
//                            runOnUiThread(() -> {
                        setLoading(false);
                        CommonUtilsMethods.showToastMessage(context, "OTP verification failed. Please try again later!", true);
//                            });
                    }
                });
            } else {
//                    runOnUiThread(() -> {
                setLoading(false);
                CommonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network), true);
//                    });
            }
        } catch (Exception e) {
//                runOnUiThread(() -> {
            setLoading(false);
            showError(context.getString(R.string.no_network));
//                });
        }
//        });
    }

//    private void verifyOtp(String otp) {
//        setLoading(true);
//
//        executor.execute(() -> {
//            try {
////                JSONObject payload = new JSONObject();
////                payload.put("mobile", mobileNumber);
////                payload.put("otp", otp);
////
////                JSONObject response = postJson(VERIFY_API_URL, payload);
////                boolean success = response != null && response.optBoolean("success", false);
//
//                runOnUiThread(() -> {
//                    setLoading(false);
////                    if (success) {
//                    Toast.makeText(context, "Verified successfully!", Toast.LENGTH_SHORT).show();
//                    if (callback != null) callback.onVerified();
//                    dismiss();

    /// /                    } else {
    /// /                        String msg = (response != null)
    /// /                                ? response.optString("message", "Invalid OTP. Please try again.")
    /// /                                : "Server error. Please try again.";
    /// /                        showError(msg);
    /// /                        clearOtpFields();
    /// /                    }
//                });
//
//            } catch (Exception e) {
//                runOnUiThread(() -> {
//                    setLoading(false);
//                    showError("Network error. Please check your connection.");
//                });
//            }
//        });
//    }
    private void resendOtp() {
        if (resendAttemptsLeft <= 0) return;

        resendAttemptsLeft--;
        updateAttemptsLabel();

        if (resendAttemptsLeft == 0) {
            setResendEnabled(false);
            tvResend.setText("No resends left");
            tvResend.setTextColor(context.getResources().getColor(R.color.text_grey));
        }

        setLoading(true);
        clearOtpFields();
        hideError();

//        executor.execute(() -> {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject = CommonUtilsMethods.CommonJsonObjectParameter(context);
                jsonObject.addProperty("SF_HQ_Code", DcrCallTabLayoutActivity.TodayPlanSfCode);
                jsonObject.addProperty("SF_HQ_Name", DcrCallTabLayoutActivity.TodayPlanSfName);
                jsonObject.addProperty("Activity_Date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                jsonObject.addProperty("Submission_Date", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                jsonObject.addProperty("DCR_Type", dcrType);
                jsonObject.addProperty("DCR_Code", customerCode);
                jsonObject.addProperty("DCR_Name", customerName);
                jsonObject.addProperty("Country_Code", countryCode);
                jsonObject.addProperty("Mobile_No", countryCode + mobileNumber);
                jsonObject.addProperty("Is_Resend", "1");
                jsonObject.addProperty("OTP_UID", otpUID);
                jsonObject.addProperty("Product_Count", productCount);
                jsonObject.addProperty("Input_Count", inputCount);

                Log.d("OTP", "requestOTP: " + jsonObject);

                if (UtilityClass.isNetworkAvailable(context)) {
                    api_interface = RetrofitClient.getRetrofit(context, SharedPref.getAppUrl(context));
                    Call<JsonElement> call = api_interface.getJsonElement(SharedPref.getAppUrl(context) + "api/" + SharedPref.getSenderId(context) + "/v3/OTP/Send/", jsonObject);
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            Log.d("OTP", "onResponse: " + response.body());
                            otpUID = "";

//                            runOnUiThread(() -> {
                                setLoading(false);
                                try {
                                    JSONObject jsonObject1 = new JSONObject(response.body().toString());
                                    boolean success = jsonObject1.optBoolean("success");
                                    String message = jsonObject1.optString("message");
                                    if (success) {
                                        JSONArray jsonArray = jsonObject1.optJSONArray("response");
                                        if (jsonArray.length() > 0) {
                                            JSONObject jsonObject2 = jsonArray.optJSONObject(0);
                                            otpUID = jsonObject2.optString("token");
                                            setLoading(false);
                                            Toast.makeText(context, "OTP resent successfully!", Toast.LENGTH_SHORT).show();
                                            if (resendAttemptsLeft > 0) {
                                                startResendTimer();
                                            }
                                            etOtp1.requestFocus();
                                            showKeyboard(etOtp1);
                                        } else {
                                            CommonUtilsMethods.showToastMessage(context, context.getString(R.string.something_went_wrong_please_try_again), true);
                                            resendAttemptsLeft++;
                                            updateAttemptsLabel();
                                        }
                                    } else {
                                        CommonUtilsMethods.showToastMessage(context, message, true);
                                        resendAttemptsLeft++;
                                        updateAttemptsLabel();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    resendAttemptsLeft++;
                                    updateAttemptsLabel();
                                }
//                            });
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            t.printStackTrace();
//                            runOnUiThread(() -> {
                                setLoading(false);
                                resendAttemptsLeft++;
                                updateAttemptsLabel();
                                CommonUtilsMethods.showToastMessage(context, "Failed to request OTP. Please try again later!", true);
//                            });
                        }
                    });
                } else {
//                    runOnUiThread(() -> {
                        setLoading(false);
                        CommonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network), true);
//                    });
                }
//                JSONObject payload = new JSONObject();
//                payload.put("mobile", mobileNumber);
//
//                JSONObject response = postJson(RESEND_API_URL, payload);
//                boolean success = response != null && response.optBoolean("success", false);
//
//                runOnUiThread(() -> {
//                    setLoading(false);
//                    if (success) {
//                    Toast.makeText(context, "OTP resent successfully!", Toast.LENGTH_SHORT).show();
//                    startResendTimer(); // restart the 60s timer
//                    etOtp1.requestFocus();
//                    showKeyboard(etOtp1);
//                    } else {
//                        String msg = (response != null)
//                                ? response.optString("message", "Failed to resend OTP.")
//                                : "Server error. Please try again.";
//                        showError(msg);
//                    }
//                });
            } catch (Exception e) {
//                runOnUiThread(() -> {
                    setLoading(false);
                    showError(context.getString(R.string.no_network));
//                });
            }
//        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!loading);
        btnSubmit.setAlpha(loading ? 0.6f : 1f);
        etOtp1.setEnabled(!loading);
        etOtp2.setEnabled(!loading);
        etOtp3.setEnabled(!loading);
        etOtp4.setEnabled(!loading);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        shakeOtpBoxes();
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void clearOtpFields() {
        etOtp1.setText("");
        etOtp2.setText("");
        etOtp3.setText("");
        etOtp4.setText("");
        etOtp1.requestFocus();
    }

    private void shakeOtpBoxes() {
        android.view.animation.Animation shake = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.shake);
        etOtp1.startAnimation(shake);
        etOtp2.startAnimation(shake);
        etOtp3.startAnimation(shake);
        etOtp4.startAnimation(shake);
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void runOnUiThread(Runnable action) {
        if (getOwnerActivity() != null) {
            getOwnerActivity().runOnUiThread(action);
        }
    }

    private void dismissAsCancelled() {
        if (isDismissing) return;
        isDismissing = true;
        cleanup();
        if (callback != null) callback.onDismissed();
        super.dismiss();
    }

    private void cleanup() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        executor.shutdownNow();
    }

    @Override
    public void dismiss() {
        if (isDismissing) return;
        isDismissing = true;

        if (countDownTimer != null) countDownTimer.cancel();
        executor.shutdownNow();
        if (callback != null) callback.onDismissed();
        super.dismiss();
    }
}
