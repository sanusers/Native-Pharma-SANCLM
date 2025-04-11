package saneforce.sanzen.activity.survey;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.activity.survey.adapter.SurveyAdapter;
import saneforce.sanzen.activity.survey.adapter.SurveySideAdapter;
import saneforce.sanzen.activity.survey.model.SurveyDetailsModelClass;
import saneforce.sanzen.activity.survey.model.SurveyModelClass;
import saneforce.sanzen.activity.survey.model.SurveyOptionsModelClass;
import saneforce.sanzen.activity.survey.model.masterModel.ChemistModel;
import saneforce.sanzen.activity.survey.model.masterModel.DoctorModel;
import saneforce.sanzen.activity.survey.model.masterModel.HospitalModel;
import saneforce.sanzen.activity.survey.model.masterModel.StockistModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivitySurveyBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class SurveyActivity extends AppCompatActivity {
    private ActivitySurveyBinding surveyBinding;
    private ApiInterface apiInterface;
    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private ProgressDialog syncProgressDialog;
    private List<SurveyModelClass> surveyDataList = new ArrayList<>();
    private HashMap<String, List<SurveyDetailsModelClass>> surveyDetailsMap = new HashMap<>();
    private List<SurveyDetailsModelClass> surveyDetailsList = new ArrayList<>();
    private SurveyAdapter surveyAdapter;
    private SurveySideAdapter surveySideAdapter;
    private int chosenSurveyPosition = -1;
    private SurveyModelClass chosenSurveyModelClass;
    private boolean isEdited = false;
    private Typeface fontregular, fontmedium;
    private HashMap<String, DoctorModel> doctorModelHashMap;
    private HashMap<String, ChemistModel> chemistModelHashMap;
    private HashMap<String, StockistModel> stockistModelHashMap;
    private HashMap<String, HospitalModel> hospitalModelHashMap;
    private String selectedHQ = "", selectedCustomerType = "", selectedCustomerCode = "";
    private DoctorModel selectedDoctorModel;
    private ChemistModel selectedChemistModel;
    private StockistModel selectedStockistModel;
    private HospitalModel selectedHospitalModel;
    private String drCap = "", chmCap = "", stkCap = "", hosCap = "";
    private boolean drNeed = false, chmNeed = false, stkNeed = false, hosNeed = false;
    private HashMap<String, String> answerMap = new HashMap<>();
    private JSONObject saveJsonObject = new JSONObject();
    List<String> SynqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surveyBinding = ActivitySurveyBinding.inflate(getLayoutInflater());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(surveyBinding.getRoot());
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(this);
        apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
        fontmedium = ResourcesCompat.getFont(this, R.font.satoshi_medium);
        fontregular = ResourcesCompat.getFont(this, R.font.satoshi_regular);

        if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
            surveyBinding.llHqMain.setVisibility(View.VISIBLE);
        }else {
            surveyBinding.llHqMain.setVisibility(View.GONE);
        }
        selectedHQ = SharedPref.getHqCode(this);
        surveyBinding.tvHeadquarters.setText(SharedPref.getHqName(this));

        syncProgressDialog = new ProgressDialog(this);
        syncProgressDialog.setMessage(this.getString(R.string.head_quarters_syncing));
        syncProgressDialog.setCancelable(false);
        syncProgressDialog.setIndeterminate(true);

        surveyBinding.backArrow.setOnClickListener(v -> {
            if(isEdited) {
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dcr_cancel_alert);
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
                TextView btn_no = dialog.findViewById(R.id.btn_no);
                alertText.setText(R.string.are_you_sure_you_want_to_exit);
                btn_yes.setOnClickListener(view12 -> {
                    dialog.dismiss();
                    getOnBackPressedDispatcher().onBackPressed();
                });
                btn_no.setOnClickListener(view12 -> {
                    dialog.dismiss();
                });
            }else {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        surveyAdapter = new SurveyAdapter(this, surveyDataList, (surveyModelClass, position) -> {
            if(this.chosenSurveyPosition != position && this.chosenSurveyPosition != -1 && validateAnswerMap()) {
                activityChangeAlert(surveyModelClass, position);
            }else {
                surveyBinding.nameChooseSurvey.setText(surveyModelClass.getSurveyName());
                chosenSurveyModelClass = surveyModelClass;
                chosenSurveyPosition = position;
                clearAll();
                surveyBinding.rlSurveyDetails.setVisibility(View.VISIBLE);
                surveyBinding.tvSurveyName.setText(chosenSurveyModelClass.getSurveyName());
                surveyBinding.tvFromDate.setText(String.format("%s : %s", getString(R.string.from), chosenSurveyModelClass.getFromDate()));
                surveyBinding.tvToDate.setText(String.format("%s : %s", getString(R.string.to), chosenSurveyModelClass.getToDate()));
            }
        });
        surveyBinding.rvSurveyList.setLayoutManager(new LinearLayoutManager(this));
        surveyBinding.rvSurveyList.setAdapter(surveyAdapter);

        surveyBinding.tvSurveyName.setOnClickListener(view -> {
            if (chosenSurveyModelClass != null && chosenSurveyModelClass.getSurveyName() != null && !chosenSurveyModelClass.getSurveyName().isEmpty()) {
                commonUtilsMethods.displayPopupWindow(this, view, chosenSurveyModelClass.getSurveyName());
            }
        });

        getSurveyData();
        getRequiredData();
        getMasterData();

        surveyBinding.rlHeadquates.setOnClickListener(view -> {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            ArrayList<SurveyOptionsModelClass> mList = new ArrayList<>();
            if(jsonArray.length()>0) {
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    String name = jsonObject.optString("name");
                    String code = jsonObject.optString("Code");
                    mList.add(new SurveyOptionsModelClass(name, code, false));
                }
                ShowMasterListPopup(surveyBinding.tvHeadquarters, mList, getString(R.string.head_quarter));
            }
        });

        surveyBinding.rlCustomerType.setOnClickListener(view -> {
            if(SharedPref.getSfType(this).equalsIgnoreCase("2") && surveyBinding.tvHeadquarters.getText().toString().isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.select_head_quarter));
            }else {
                ArrayList<SurveyOptionsModelClass> mList = new ArrayList<>();
                try {
                    List<String> customerTypeList = new ArrayList<>();
                    if(drNeed) {
                        customerTypeList.add(drCap);
                    }
                    if(chmNeed) {
                        customerTypeList.add(chmCap);
                    }
//                if(stkNeed) {
//                    customerTypeList.add(stkCap);
//                }
//                if(hosNeed) {
//                    customerTypeList.add(hosCap);
//                }
                    if(!customerTypeList.isEmpty()) {
                        for (int i = 0; i<customerTypeList.size(); i++) {
                            mList.add(new SurveyOptionsModelClass(customerTypeList.get(i), String.valueOf(i + 1), false));
                        }
                        ShowMasterListPopup(surveyBinding.tvCustomerType, mList, getString(R.string.customer_type));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        surveyBinding.rlCustomer.setOnClickListener(view -> {
            if(surveyBinding.tvCustomerType.getText().toString().isEmpty()) {
                commonUtilsMethods.showToastMessage(this, getString(R.string.select_customer_type));
            }else {
                ArrayList<SurveyOptionsModelClass> mList = new ArrayList<>();
                try {
                    if(selectedCustomerType.equalsIgnoreCase(drCap)) {
                        for (DoctorModel doctorModel : doctorModelHashMap.values()) {
                            if(chosenSurveyModelClass != null
                                    && (chosenSurveyModelClass.getDrCat().toLowerCase().contains(doctorModel.getCategoryCode())
                                    || chosenSurveyModelClass.getDrCls().toLowerCase().contains(doctorModel.getClassCode())
                                    || chosenSurveyModelClass.getDrSpl().toLowerCase().contains(doctorModel.getSpecialtyCode()))) {
                                mList.add(new SurveyOptionsModelClass(doctorModel.getName(), doctorModel.getCode(), false));
                            }
                        }
                    }else if(selectedCustomerType.equalsIgnoreCase(chmCap)) {
                        for (ChemistModel chemistModel : chemistModelHashMap.values()) {
                            if(chosenSurveyModelClass != null
                                    && chosenSurveyModelClass.getChmCat().toLowerCase().contains(chemistModel.getCategoryCode())) {
                                mList.add(new SurveyOptionsModelClass(chemistModel.getName(), chemistModel.getCode(), false));
                            }
                        }
                    }
//                    else if(selectedCustomerType.equalsIgnoreCase(stkCap)) {
//                        for (StockistModel stockistModel : stockistModelHashMap.values()) {
//                            mList.add(new SurveyOptionsModelClass(stockistModel.getName(), stockistModel.getCode(), false));
//                        }
//                    }else if(selectedCustomerType.equalsIgnoreCase(hosCap)) {
//                        for (HospitalModel hospitalModel : hospitalModelHashMap.values()) {
//                            mList.add(new SurveyOptionsModelClass(hospitalModel.getName(), hospitalModel.getCode(), false));
//                        }
//                    }
                    Collections.sort(mList, Comparator.comparing(SurveyOptionsModelClass::getName));
                    ShowMasterListPopup(surveyBinding.tvCustomer, mList, selectedCustomerType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        surveyBinding.btnSubmit.setOnClickListener(view -> {
            if(UtilityClass.isNetworkAvailable(this)) {
                if(validateAndCreateJSON()) {
                    callSaveAPI();
                }
            }else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.please_check_your_internet_connection));
            }
        });

        surveyBinding.btnClearall.setOnClickListener(view -> showClearAlert());

    }

    private boolean validateAnswerMap() {
        for (String answer : answerMap.values()) {
            if(!answer.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void activityChangeAlert(SurveyModelClass classGroup, int position) {
        Dialog dialog = new Dialog(SurveyActivity.this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        alertText.setText(String.format("%s Want to change Survey.\nYour entered Survey details will be cleared", SurveyActivity.this.getString(R.string.are_you_sure)));
        btn_yes.setOnClickListener(view12 -> {
            chosenSurveyModelClass = classGroup;
            chosenSurveyPosition = position;
            clearAll();
            surveyBinding.rlSurveyDetails.setVisibility(View.VISIBLE);
            surveyBinding.tvSurveyName.setText(chosenSurveyModelClass.getSurveyName());
            surveyBinding.tvFromDate.setText(String.format("%s : %s", getString(R.string.from), chosenSurveyModelClass.getFromDate()));
            surveyBinding.tvToDate.setText(String.format("%s : %s", getString(R.string.to), chosenSurveyModelClass.getToDate()));
            dialog.dismiss();
        });
        btn_no.setOnClickListener(view12 -> {
            surveyAdapter.changeSelectedPosition(chosenSurveyPosition);
            dialog.dismiss();
        });
    }

    private void showClearAlert() {
        Dialog dialog = new Dialog(SurveyActivity.this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        alertText.setText(SurveyActivity.this.getString(R.string.are_you_sure_you_want_to_clear));
        btn_yes.setOnClickListener(view12 -> {
//            getSurveyDetails(chosenSurveyModelClass);
            clearAll();
            dialog.dismiss();
        });
        btn_no.setOnClickListener(view12 -> {
            dialog.dismiss();
        });
    }

    private void clearAll() {
        isEdited = false;
        surveyBinding.llSurveyDetailsView.removeAllViews();
        surveyBinding.tvHeadquarters.setText("");
        surveyBinding.tvCustomerType.setText("");
        surveyBinding.tvCustomer.setText("");
        surveyBinding.tvNoSurveyFound.setText(R.string.select_customer_type);
        surveyBinding.rlNoData.setVisibility(View.GONE);
        surveyBinding.rlDetailsMain.setVisibility(View.VISIBLE);
        surveyBinding.rlSurveyMain.setVisibility(View.GONE);
        surveyBinding.rlNoSurveyFound.setVisibility(View.VISIBLE);
    }

    private void callSaveAPI() {
        if(UtilityClass.isNetworkAvailable(SurveyActivity.this)) {
            surveyBinding.progressSubmit.setVisibility(View.VISIBLE);
            try {
                Log.d("Survey Activity", "callSaveAPI: " + saveJsonObject);
                apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
                Map<String, String> qry = new HashMap<>();
                qry.put("axn", "get/survey");
                Call<JsonElement> quiz = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), qry, saveJsonObject.toString());
                if(quiz != null) {
                    quiz.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> quiz, @NonNull Response<JsonElement> response) {
                            if(response.isSuccessful()) {
                                if(response.body() != null) {
                                    Log.e("test", "response : " + " : " + response.body());
                                }
                                clearAll();
                                commonUtilsMethods.showToastMessage(SurveyActivity.this, "Survey Submitted Successfully");
                            }
                            surveyBinding.progressSubmit.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> quiz, @NonNull Throwable t) {
                            commonUtilsMethods.showToastMessage(SurveyActivity.this, getString(R.string.poor_connection));
                            surveyBinding.progressSubmit.setVisibility(View.GONE);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                commonUtilsMethods.showToastMessage(this, getString(R.string.poor_connection));
                surveyBinding.progressSubmit.setVisibility(View.GONE);
            }
        }else {
            commonUtilsMethods.showToastMessage(this, getString(R.string.please_check_your_internet_connection));
        }
    }

    private boolean validateAndCreateJSON() {
        try {
            surveyDetailsList = surveyDetailsMap.get(chosenSurveyModelClass.getSurveyID());
            saveJsonObject = CommonUtilsMethods.CommonObjectParameter(this);
            saveJsonObject.put("sfcode", SharedPref.getSfCode(this));
            saveJsonObject.put("Rsf", selectedHQ);
            saveJsonObject.put("tableName", "savesurvey");
            JSONArray jsonArray = new JSONArray();
            String customerType = "", customerCode = "", surveyDate = CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_1), surveyID = chosenSurveyModelClass.getSurveyID();
            if(selectedCustomerType.equalsIgnoreCase(drCap)) {
                customerType = "D";
                customerCode = selectedDoctorModel.getCode();
            }else if(selectedCustomerType.equalsIgnoreCase(chmCap)) {
                customerType = "C";
                customerCode = selectedChemistModel.getCode();
            }else if(selectedCustomerType.equalsIgnoreCase(stkCap)) {
                customerType = "S";
                customerCode = selectedStockistModel.getCode();
            }else if(selectedCustomerType.equalsIgnoreCase(hosCap)) {
                customerType = "H";
                customerCode = selectedHospitalModel.getCode();
            }
            if(surveyDetailsList != null && !surveyDetailsList.isEmpty()) {
                for (SurveyDetailsModelClass surveyDetailsModelClass : surveyDetailsList) {
                    if(answerMap.containsKey(surveyDetailsModelClass.getQuestionID())) {
                        String answer = answerMap.get(surveyDetailsModelClass.getQuestionID());
                        if(surveyDetailsModelClass.getMandatory().equalsIgnoreCase("1") && (answer == null || answer.isEmpty())) {
                            if(surveyDetailsModelClass.getQuestionCodeID().equals("3") || surveyDetailsModelClass.getQuestionCodeID().equals("4")) {
                                commonUtilsMethods.showToastMessage(this, "Select option for " + surveyDetailsModelClass.getQuestion());
                            }else {
                                commonUtilsMethods.showToastMessage(this, "Fill " + surveyDetailsModelClass.getQuestion());
                            }
                            return false;
                        }else {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("CustType", customerType);
                            jsonObject.put("CustCode", customerCode);
                            jsonObject.put("SurveyDate", surveyDate);
                            jsonObject.put("Survey_Id", surveyID);
                            jsonObject.put("Question_Id", surveyDetailsModelClass.getQuestionID());
                            jsonObject.put("Answer", answer);
                            jsonArray.put(jsonObject);
                        }
                    }
                }
            }
            saveJsonObject.put("val", jsonArray);
            return true;
        } catch (Exception e) {
            commonUtilsMethods.showToastMessage(this, getString(R.string.please_try_again));
            e.printStackTrace();
        }
        return false;
    }

    private void getRequiredData() {
        drCap = SharedPref.getDrCap(this);
        chmCap = SharedPref.getChmCap(this);
        stkCap = SharedPref.getStkCap(this);
        hosCap = SharedPref.getHospCaption(this);
        drNeed = SharedPref.getDrNeed(this).equals("0");
        chmNeed = SharedPref.getChmNeed(this).equals("0");
        stkNeed = SharedPref.getStkNeed(this).equals("0");
        hosNeed = SharedPref.getHospNeed(this).equals("0");
    }

    private void getMasterData() {
        doctorModelHashMap = new HashMap<>();
        chemistModelHashMap = new HashMap<>();
        stockistModelHashMap = new HashMap<>();
        hospitalModelHashMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        if(drNeed) {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + selectedHQ).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String code = jsonObject.optString("Code");
                String name = jsonObject.optString("Name");
                String categoryCode = jsonObject.optString("CategoryCode");
                String specialtyCode = jsonObject.optString("SpecialtyCode");
                String classCode = jsonObject.optString("Doc_ClsCode");
                DoctorModel doctorModel = new DoctorModel(code, name, categoryCode, specialtyCode, classCode);
                doctorModelHashMap.put(code, doctorModel);
            }
        }
        if(chmNeed) {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + selectedHQ).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String code = jsonObject.optString("Code");
                String name = jsonObject.optString("Name");
                String categoryCode = jsonObject.optString("Chm_cat");
                ChemistModel chemistModel = new ChemistModel(code, name, categoryCode);
                chemistModelHashMap.put(code, chemistModel);
            }
        }
//        if(stkNeed) {
//            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + selectedHQ).getMasterSyncDataJsonArray();
//            for (int i = 0; i<jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.optJSONObject(i);
//                String code = jsonObject.optString("Code");
//                String name = jsonObject.optString("Name");
//                StockistModel stockistModel = new StockistModel(code, name);
//                stockistModelHashMap.put(code, stockistModel);
//            }
//        }
//        if(hosNeed) {
//            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + selectedHQ).getMasterSyncDataJsonArray();
//            for (int i = 0; i<jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.optJSONObject(i);
//                String code = jsonObject.optString("Code");
//                String name = jsonObject.optString("Name");
//                String categoryCode = jsonObject.optString("CategoryCode");
//                HospitalModel hospitalModel = new HospitalModel(code, name, categoryCode);
//                hospitalModelHashMap.put(code, hospitalModel);
//            }
//        }
    }

    private void getSurveyDetails(SurveyModelClass surveyModelClass) {
        surveyBinding.progrlessdetail.setVisibility(View.VISIBLE);
        surveyBinding.llSurveyDetailsView.removeAllViews();
        answerMap = new HashMap<>();
        try {
            surveyDetailsList = surveyDetailsMap.get(surveyModelClass.getSurveyID());
            if(surveyDetailsList != null && !surveyDetailsList.isEmpty()) {
                surveyBinding.rlSurveyMain.setVisibility(View.VISIBLE);
                surveyBinding.rlDataLayout.setVisibility(View.VISIBLE);
                surveyBinding.rlNoSurveyFound.setVisibility(View.GONE);
                surveyBinding.rlNoData.setVisibility(View.GONE);
                boolean isSurveyAvailable = false;
                for (int i = 0; i<surveyDetailsList.size(); i++) {
                    SurveyDetailsModelClass surveyDetailsModelClass = surveyDetailsList.get(i);
//                    boolean isToView = false;
//                    if(selectedCustomerType.equalsIgnoreCase(drCap)
//                            && surveyDetailsModelClass.getSurveyType().toLowerCase().contains("d")
//                            && (surveyDetailsModelClass.getDrCat().toLowerCase().contains(selectedDoctorModel.getCategoryCode())
//                            || surveyDetailsModelClass.getDrCls().toLowerCase().contains(selectedDoctorModel.getClassCode())
//                            || surveyDetailsModelClass.getDrSpl().toLowerCase().contains(selectedDoctorModel.getSpecialtyCode()))
//                    ) {
//                        isToView = true;
                    isSurveyAvailable = true;
//                    }else if(selectedCustomerType.equalsIgnoreCase(chmCap)
//                            && surveyDetailsModelClass.getSurveyType().toLowerCase().contains("c")
//                            && surveyDetailsModelClass.getChmCat().toLowerCase().contains(selectedChemistModel.getCategoryCode())
//                    ) {
//                        isToView = true;
//                        isSurveyAvailable = true;
//                    }
//                    else if(selectedCustomerType.equalsIgnoreCase(stkCap)) {
//                        StockistModel stockistModel = stockistModelHashMap.get(selectedCustomerCode);
//
//                    } else if(selectedCustomerType.equalsIgnoreCase(hosCap)) {
//                        HospitalModel hospitalModel = hospitalModelHashMap.get(selectedCustomerCode);
//
//                    }
//                    if(isToView) {
                    switch (surveyDetailsModelClass.getQuestionCodeID()){
                        case "1":
                            CreateNameView(surveyDetailsModelClass, i);
                            break;
                        case "2":
                            CreateNumberView(surveyDetailsModelClass, i);
                            break;
                        case "3":
                            CreateSingleListSelection(surveyDetailsModelClass, i);
                            break;
                        case "4":
                            CreateMultipleListSelection(surveyDetailsModelClass, i);
                            break;
                    }
//                    }
                    if(i + 1 == surveyDetailsList.size()) {
                        surveyBinding.btnSubmit.setVisibility(View.VISIBLE);
                    }
                }
                if(!isSurveyAvailable) {
                    surveyBinding.tvNoSurveyFound.setText("No Survey Found");
                    surveyBinding.rlNoSurveyFound.setVisibility(View.VISIBLE);
                    surveyBinding.rlSurveyMain.setVisibility(View.GONE);
                    surveyBinding.btnSubmit.setVisibility(View.GONE);
                    commonUtilsMethods.showToastMessage(this, "No Survey Found");
                }
            }else {
                surveyBinding.tvNoSurveyFound.setText("No Survey Found");
                surveyBinding.rlNoSurveyFound.setVisibility(View.VISIBLE);
                surveyBinding.rlSurveyMain.setVisibility(View.GONE);
                surveyBinding.btnSubmit.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(this, "No Survey Found");
            }
            surveyBinding.progrlessdetail.setVisibility(View.GONE);
        } catch (Exception e) {
            surveyBinding.progrlessdetail.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void getSurveyData() {
        surveyBinding.progressMain.setVisibility(View.VISIBLE);
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SURVEY).getMasterSyncDataJsonArray();
            surveyDataList = new ArrayList<>();
            surveyDetailsMap = new HashMap<>();
            if(jsonArray != null && jsonArray.length()>0) {
                for (int i = 0; i<jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String surveyID = jsonObject.optString("id");
                        String surveyName = jsonObject.optString("name");
                        String fromDate = jsonObject.optString("from_date");
                        String toDate = jsonObject.optString("to_date");
                        String drCat = jsonObject.optString("DrCat");
                        String drSpl = jsonObject.optString("DrSpl");
                        String drCls = jsonObject.optString("DrCls");
                        String hosCls = jsonObject.optString("HosCls");
                        String chmCat = jsonObject.optString("ChmCat");
                        String stkState = jsonObject.optString("Stkstate");
                        String stkHQ = jsonObject.optString("StkHQ");
                        String todayDate = TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_4);
                        LocalDate from = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                        LocalDate to = LocalDate.parse(toDate, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                        LocalDate today = LocalDate.parse(todayDate, DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                        if(!to.isBefore(today) && !from.isAfter(today)) {
//                            SurveyModelClass surveyModelClass = new SurveyModelClass(surveyID, surveyName, fromDate, toDate);
                            SurveyModelClass surveyModelClass = new SurveyModelClass(surveyID, surveyName, drCat, drSpl, drCls, hosCls, chmCat, stkState, stkHQ, fromDate, toDate);
                            surveyDataList.add(surveyModelClass);
                            JSONArray surveyDetailsJsonArray = jsonObject.optJSONArray("survey_for");
                            if(surveyDetailsJsonArray != null && surveyDetailsJsonArray.length()>0) {
                                for (int j = 0; j<surveyDetailsJsonArray.length(); j++) {
                                    JSONObject surveyDetailsJsonObject = surveyDetailsJsonArray.optJSONObject(j);
                                    String questionID = surveyDetailsJsonObject.optString("id");
                                    String surveyId = surveyDetailsJsonObject.optString("Survey");
//                                    String drCat = surveyDetailsJsonObject.optString("DrCat");
//                                    String drSpl = surveyDetailsJsonObject.optString("DrSpl");
//                                    String drCls = surveyDetailsJsonObject.optString("DrCls");
//                                    String hosCls = surveyDetailsJsonObject.optString("HosCls");
//                                    String chmCat = surveyDetailsJsonObject.optString("ChmCat");
//                                    String stkState = surveyDetailsJsonObject.optString("Stkstate");
//                                    String stkHQ = surveyDetailsJsonObject.optString("StkHQ");
                                    String surveyType = surveyDetailsJsonObject.optString("Stype");
                                    String questionCodeID = surveyDetailsJsonObject.optString("Qc_id");
                                    String questionType = surveyDetailsJsonObject.optString("Qtype");
                                    String answerLength = surveyDetailsJsonObject.optString("Qlength");
                                    String mandatory = surveyDetailsJsonObject.optString("Mandatory");
                                    String question = surveyDetailsJsonObject.optString("Qname");
                                    String answer = surveyDetailsJsonObject.optString("Qanswer");
                                    String activeFlag = surveyDetailsJsonObject.optString("Active_Flag");
//                                    SurveyDetailsModelClass surveyDetailsModelClass = new SurveyDetailsModelClass(questionID, surveyId, drCat, drSpl, drCls, hosCls, chmCat, stkState, stkHQ, surveyType, questionCodeID, questionType, answerLength, mandatory, question, answer, activeFlag);
                                    SurveyDetailsModelClass surveyDetailsModelClass = new SurveyDetailsModelClass(questionID, surveyId, surveyType, questionCodeID, questionType, answerLength, mandatory, question, answer, activeFlag);
                                    if(!surveyDetailsMap.containsKey(surveyId)) {
                                        surveyDetailsMap.put(surveyId, new ArrayList<>());
                                    }
                                    surveyDetailsMap.get(surveyId).add(surveyDetailsModelClass);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Survey Activity", "getSurveyData: details error");
                        e.printStackTrace();
                    }
                }
            }
            Log.w("Survey Activity", "getSurveyData: " + surveyDataList.toString());
            surveyAdapter.updateDataList(surveyDataList);
            if(surveyDataList.isEmpty()) {
                surveyBinding.rlDetailsMain.setVisibility(View.GONE);
                surveyBinding.rlNoSurvey.setVisibility(View.VISIBLE);
                surveyBinding.llMainLayout.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(this, "No Survey Found");
            }else {
                surveyBinding.rlNoSurvey.setVisibility(View.GONE);
                surveyBinding.llMainLayout.setVisibility(View.VISIBLE);
                surveyBinding.rlDetailsMain.setVisibility(View.VISIBLE);
            }
            surveyBinding.progressMain.setVisibility(View.GONE);
        } catch (Exception e) {
            surveyBinding.progressMain.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void CreateNameView(SurveyDetailsModelClass surveyDetailsModelClass, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        surveyBinding.llSurveyDetailsView.addView(textLinearLayout1);

        TextView txtLabelName = new TextView(this);
        String firstChar = "<font color='#000000'>" + surveyDetailsModelClass.getQuestion() + "</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if((surveyDetailsModelClass.getMandatory().equals("1")) && (!surveyDetailsModelClass.getMandatory().isEmpty())) {
            txtLabelName.setText(Html.fromHtml(firstChar + firstChar2));
        }else {
            txtLabelName.setText(surveyDetailsModelClass.getQuestion());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        txtLabelName.setTextColor(getResources().getColor(R.color.text_dark));
        txtLabelName.setLayoutParams(params1);
        txtLabelName.setTypeface(fontmedium);
        txtLabelName.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinearLayout1.addView(txtLabelName);

        EditText textcharacter = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textcharacter.setBackgroundColor(Color.WHITE);
        textcharacter.setBackgroundResource(R.drawable.background_card_white_plan);
        textcharacter.setTextColor(getResources().getColor(R.color.text_dark));
        textcharacter.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textcharacter.setLayoutParams(params);
        textcharacter.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textcharacter.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        textLinearLayout1.addView(textcharacter);
        textcharacter.setInputType(InputType.TYPE_CLASS_TEXT);
        textcharacter.setId(k);
        textcharacter.setHint("Type here...");
        textcharacter.setCursorVisible(true);
        textcharacter.setClickable(true);
        if(!surveyDetailsModelClass.getAnswerLength().isEmpty() && !surveyDetailsModelClass.getAnswerLength().equalsIgnoreCase("0")) {
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(Integer.parseInt(surveyDetailsModelClass.getAnswerLength()));
            textcharacter.setFilters(fArray);
        }

        answerMap.put(surveyDetailsModelClass.getQuestionID(), "");

        textcharacter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                answerMap.put(surveyDetailsModelClass.getQuestionID(), textcharacter.getText().toString());
            }
        });

        textcharacter.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(v);
                v.clearFocus();
                return true;
            }
            return false;
        });
    }

    @SuppressLint("ResourceType")
    public void CreateNumberView(SurveyDetailsModelClass surveyDetailsModelClass, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        surveyBinding.llSurveyDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + surveyDetailsModelClass.getQuestion() + "</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if((surveyDetailsModelClass.getMandatory().equals("1")) && (!surveyDetailsModelClass.getMandatory().isEmpty())) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        }else {
            textviewdata.setText(surveyDetailsModelClass.getQuestion());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewdata.setTypeface(fontmedium);
        textLinearLayout1.addView(textviewdata);
        EditText textnumber = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textnumber.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        textnumber.setBackgroundColor(Color.WHITE);
        textnumber.setBackgroundResource(R.drawable.background_card_white_plan);
        textnumber.setTextColor(getResources().getColor(R.color.text_dark));
        textnumber.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textnumber.setLayoutParams(params);
        textnumber.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textnumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        textnumber.setId(k);
        textnumber.setHint("Type here...");
        textnumber.setClickable(false);
        textnumber.setCursorVisible(true);
        if(!surveyDetailsModelClass.getAnswerLength().isEmpty() && !surveyDetailsModelClass.getAnswerLength().equalsIgnoreCase("0")) {
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(Integer.parseInt(surveyDetailsModelClass.getAnswerLength()));
            textnumber.setFilters(fArray);
        }
        textLinearLayout1.addView(textnumber);

        answerMap.put(surveyDetailsModelClass.getQuestionID(), "");

        textnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                answerMap.put(surveyDetailsModelClass.getQuestionID(), textnumber.getText().toString());
            }
        });
        textnumber.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(v);
                v.clearFocus();
                return true;
            }
            return false;
        });
    }

    private void CreateSingleListSelection(SurveyDetailsModelClass surveyDetailsModelClass, int k) {
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        surveyBinding.llSurveyDetailsView.addView(textLinearLayout);

        TextView textcombosingle = new TextView(this);
        String firstChar = "<font color='#000000'>" + surveyDetailsModelClass.getQuestion() + "</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if((surveyDetailsModelClass.getMandatory().equals("1")) && (!surveyDetailsModelClass.getMandatory().isEmpty())) {
            textcombosingle.setText(Html.fromHtml(firstChar + firstChar2));
        }else {
            textcombosingle.setText(surveyDetailsModelClass.getQuestion());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textcombosingle.setTypeface(fontmedium);
        params.setMargins((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textcombosingle.setTextColor(getResources().getColor(R.color.text_dark));
        textcombosingle.setLayoutParams(params);
        textcombosingle.setTextSize((int) getResources().getDimension(R.dimen._5sdp));

        textLinearLayout.addView(textcombosingle);
        textcombosingle.setId(k);

        TextView singlecomboedittext = new TextView(this);
        singlecomboedittext.setBackgroundColor(Color.WHITE);

        singlecomboedittext.setBackgroundResource(R.drawable.background_card_white_plan);
        singlecomboedittext.setTextColor(getResources().getColor(R.color.text_dark));
        singlecomboedittext.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        singlecomboedittext.setLayoutParams(params);
        singlecomboedittext.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        singlecomboedittext.setVisibility(View.VISIBLE);
        Drawable drawable = getDrawable(R.drawable.right_arrow);
        assert drawable != null;
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._5sdp));
        singlecomboedittext.setCompoundDrawables(null, null, drawable, null);
        singlecomboedittext.setHintTextColor(getResources().getColor(R.color.text_dark));
        singlecomboedittext.setHint("Select");
        textLinearLayout.addView(singlecomboedittext);

        answerMap.put(surveyDetailsModelClass.getQuestionID(), "");

        singlecomboedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                answerMap.put(surveyDetailsModelClass.getQuestionID(), singlecomboedittext.getText().toString());
            }
        });

        TextView Idview = new TextView(this);
        Idview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
            }
        });
        singlecomboedittext.setOnClickListener(view -> {
            ArrayList<SurveyOptionsModelClass> mList = new ArrayList<>();
            try {
                String[] answers = CommonUtilsMethods.removeLastComma(surveyDetailsModelClass.getAnswer()).split(",");
                if(answers.length>0) {
                    for (int i = 0; i<answers.length; i++) {
                        mList.add(new SurveyOptionsModelClass(answers[i], String.valueOf(i + 1), false));
                    }
                    ShowListPopup(singlecomboedittext, Idview, mList, "Option", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void CreateMultipleListSelection(SurveyDetailsModelClass surveyDetailsModelClass, int k) {
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        surveyBinding.llSurveyDetailsView.addView(textLinearLayout);

        TextView textcombomultiple = new TextView(this);
        String firstChar = "<font color='#000000'>" + surveyDetailsModelClass.getQuestion() + "</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if((surveyDetailsModelClass.getMandatory().equals("1")) && (!surveyDetailsModelClass.getMandatory().isEmpty())) {
            textcombomultiple.setText(Html.fromHtml(firstChar + firstChar2));
        }else {
            textcombomultiple.setText(surveyDetailsModelClass.getQuestion());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textcombomultiple.setTypeface(fontmedium);
        params.setMargins((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textcombomultiple.setTextColor(getResources().getColor(R.color.text_dark));
        textcombomultiple.setLayoutParams(params);
        textcombomultiple.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinearLayout.addView(textcombomultiple);
        textcombomultiple.setId(k);
        textcombomultiple.setTypeface(fontmedium);

        TextView multicomboeditext = new TextView(this);
        multicomboeditext.setBackgroundColor(Color.WHITE);
        multicomboeditext.setBackgroundResource(R.drawable.background_card_white_plan);
        multicomboeditext.setTextColor(getResources().getColor(R.color.text_dark));
        multicomboeditext.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        multicomboeditext.setLayoutParams(params);
        multicomboeditext.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        multicomboeditext.setVisibility(View.VISIBLE);
        multicomboeditext.setHint("Select");
        multicomboeditext.setHintTextColor(getResources().getColor(R.color.text_dark));
        textLinearLayout.addView(multicomboeditext);
        Drawable drawable = getDrawable(R.drawable.right_arrow);
        assert drawable != null;
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._5sdp));
        multicomboeditext.setCompoundDrawables(null, null, drawable, null);

        answerMap.put(surveyDetailsModelClass.getQuestionID(), "");

        multicomboeditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                answerMap.put(surveyDetailsModelClass.getQuestionID(), multicomboeditext.getText().toString());
            }
        });

        TextView TextCode = new TextView(this);
        TextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        multicomboeditext.setOnClickListener(view -> {
            ArrayList<SurveyOptionsModelClass> mList = new ArrayList<>();
            try {
                String[] selectedIds = CommonUtilsMethods.removeFirstComma(CommonUtilsMethods.removeLastComma(TextCode.getText().toString())).split(",");
                List<String> selectedIDList = Arrays.asList(selectedIds);
                String[] answers = CommonUtilsMethods.removeFirstComma(CommonUtilsMethods.removeLastComma(surveyDetailsModelClass.getAnswer())).split(",");
                if(answers.length>0) {
                    for (int i = 0; i<answers.length; i++) {
                        if(selectedIDList.contains(String.valueOf(i))) {
                            mList.add(new SurveyOptionsModelClass(answers[i], String.valueOf(i), true));
                        }else {
                            mList.add(new SurveyOptionsModelClass(answers[i], String.valueOf(i), false));
                        }
                    }
                    ShowListPopup(multicomboeditext, TextCode, mList, "Options", true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void ShowMasterListPopup(TextView NameView, ArrayList<SurveyOptionsModelClass> optionsList, String name) {
        surveyBinding.slideScreen.viewDummy1.setVisibility(View.GONE);
        surveyBinding.slideScreen.txtClDone.setVisibility(View.GONE);

        List<String> mListOptions = new ArrayList<>();
        List<String> mListId = new ArrayList<>();
        surveyBinding.mainLayout.openDrawer(GravityCompat.END);
        surveyBinding.slideScreen.etSearch.setText("");
        surveyBinding.slideScreen.tvSearchheader.setText("Select " + name);

        if(optionsList != null && !optionsList.isEmpty()) {
            surveyBinding.slideScreen.acRecyelerView.setVisibility(View.VISIBLE);
            surveyBinding.slideScreen.llSearchLayout.setVisibility(View.VISIBLE);
            surveyBinding.slideScreen.txtNoData.setVisibility(View.GONE);
            surveyBinding.slideScreen.etSearch.setHint("Search " + name);
            surveySideAdapter = new SurveySideAdapter(this, optionsList, false, new SurveySideAdapter.CheckBoxClickListener() {
                @Override
                public void onChecked(SurveyOptionsModelClass surveyOptionsModelClass) {
                    surveyBinding.mainLayout.closeDrawer(GravityCompat.END);
                    hideKeyboard(surveyBinding.getRoot());
                    NameView.setText(surveyOptionsModelClass.getName());

                    surveyBinding.llSurveyDetailsView.removeAllViews();
                    surveyBinding.rlNoData.setVisibility(View.GONE);
                    surveyBinding.rlDetailsMain.setVisibility(View.VISIBLE);
                    surveyBinding.rlSurveyMain.setVisibility(View.GONE);
                    surveyBinding.rlNoSurveyFound.setVisibility(View.VISIBLE);

                    if(name.equalsIgnoreCase(getString(R.string.customer_type))) {
                        selectedCustomerType = surveyOptionsModelClass.getName();
                        surveyBinding.tvCustomer.setText("");
                        surveyBinding.tvNoSurveyFound.setText(getString(R.string.select_customer));
                    }else if(name.equalsIgnoreCase(selectedCustomerType)) {
                        selectedCustomerCode = surveyOptionsModelClass.getId();
                        if(selectedCustomerType.equalsIgnoreCase(drCap)) {
                            selectedDoctorModel = doctorModelHashMap.get(selectedCustomerCode);
                        }else if(selectedCustomerType.equalsIgnoreCase(chmCap)) {
                            selectedChemistModel = chemistModelHashMap.get(selectedCustomerCode);
                        }else if(selectedCustomerType.equalsIgnoreCase(stkCap)) {
                            selectedStockistModel = stockistModelHashMap.get(selectedCustomerCode);
                        }else if(selectedCustomerType.equalsIgnoreCase(hosCap)) {
                            selectedHospitalModel = hospitalModelHashMap.get(selectedCustomerCode);
                        }
                        getSurveyDetails(chosenSurveyModelClass);
                    }else if(name.equalsIgnoreCase(getString(R.string.head_quarter))) {
                        selectedHQ = surveyOptionsModelClass.getId();
                        surveyBinding.tvCustomerType.setText("");
                        surveyBinding.tvCustomer.setText("");
                        surveyBinding.tvNoSurveyFound.setText(getString(R.string.select_customer_type));
                        getHQData(surveyOptionsModelClass.getId());
                    }
                }

                @Override
                public void onUnchecked(SurveyOptionsModelClass surveyOptionsModelClass) {
                    mListOptions.remove(surveyOptionsModelClass.getName());
                    mListId.remove(surveyOptionsModelClass.getId());
                }
            });
            surveyBinding.slideScreen.acRecyelerView.setLayoutManager(new LinearLayoutManager(this));
            surveyBinding.slideScreen.acRecyelerView.setAdapter(surveySideAdapter);

            surveyBinding.slideScreen.etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String searchString = charSequence.toString();
                    surveySideAdapter.getFilter().filter(searchString);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        } else {
            surveyBinding.slideScreen.acRecyelerView.setVisibility(View.GONE);
            surveyBinding.slideScreen.llSearchLayout.setVisibility(View.GONE);
            surveyBinding.slideScreen.txtNoData.setVisibility(View.VISIBLE);
            surveyBinding.slideScreen.txtNoData.setText(String.format("No %s available", selectedCustomerType));
        }

        surveyBinding.slideScreen.cancelImg.setOnClickListener(view -> {
            surveyBinding.mainLayout.closeDrawer(GravityCompat.END);
            hideKeyboard(surveyBinding.getRoot());
        });
    }

    public void ShowListPopup(TextView NameView, TextView IdView, ArrayList<SurveyOptionsModelClass> List, String name, boolean isMultipleCheck) {
        if(isMultipleCheck) {
            surveyBinding.slideScreen.viewDummy1.setVisibility(View.VISIBLE);
            surveyBinding.slideScreen.txtClDone.setVisibility(View.VISIBLE);
        }else {
            surveyBinding.slideScreen.viewDummy1.setVisibility(View.GONE);
            surveyBinding.slideScreen.txtClDone.setVisibility(View.GONE);
        }

        List<String> mListOptions = new ArrayList<>();
        List<String> mListId = new ArrayList<>();
        surveyBinding.mainLayout.openDrawer(GravityCompat.END);
        surveyBinding.slideScreen.etSearch.setText("");
        surveyBinding.slideScreen.tvSearchheader.setText("Select " + name);
        surveyBinding.slideScreen.etSearch.setHint("Search " + name);
        surveySideAdapter = new SurveySideAdapter(this, List, isMultipleCheck, new SurveySideAdapter.CheckBoxClickListener() {
            @Override
            public void onChecked(SurveyOptionsModelClass surveyOptionsModelClass) {
                if(isMultipleCheck) {
                    mListOptions.add(surveyOptionsModelClass.getName());
                    mListId.add(surveyOptionsModelClass.getId());
                }else {
                    surveyBinding.mainLayout.closeDrawer(GravityCompat.END);
                    hideKeyboard(surveyBinding.getRoot());
                    NameView.setText(surveyOptionsModelClass.getName());
                    IdView.setText(surveyOptionsModelClass.getId());
                }
            }

            @Override
            public void onUnchecked(SurveyOptionsModelClass surveyOptionsModelClass) {
                mListOptions.remove(surveyOptionsModelClass.getName());
                mListId.remove(surveyOptionsModelClass.getId());
            }
        });
        surveyBinding.slideScreen.acRecyelerView.setLayoutManager(new LinearLayoutManager(this));
        surveyBinding.slideScreen.acRecyelerView.setAdapter(surveySideAdapter);

        surveyBinding.slideScreen.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchString = charSequence.toString();
                surveySideAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        surveyBinding.slideScreen.txtClDone.setOnClickListener(view -> {
            if(isMultipleCheck) {
                StringBuilder lids = new StringBuilder();
                for (int i = 0; i<mListId.size(); i++) {
                    lids.append(",").append(mListId.get(i));
                }
                NameView.setText(mListOptions.toString().replaceAll("[\\[\\]]", ""));
                IdView.setText(lids.toString());
                hideKeyboard(surveyBinding.getRoot());
                surveyBinding.mainLayout.closeDrawer(GravityCompat.END);
            }
        });

        surveyBinding.slideScreen.cancelImg.setOnClickListener(view -> {
            surveyBinding.mainLayout.closeDrawer(GravityCompat.END);
            hideKeyboard(surveyBinding.getRoot());
        });
    }

    private void getHQData(String hqCode) {
        try {
            Log.d("SurveyActivity", "showHQ: " + hqCode);
            boolean docAvailability = masterDataDao.isDataAvailable(Constants.DOCTOR + hqCode),
                    chemAvailability = masterDataDao.isDataAvailable(Constants.CHEMIST + hqCode),
                    stkAvailability = masterDataDao.isDataAvailable(Constants.STOCKIEST + hqCode),
                    ulDocAvailability = masterDataDao.isDataAvailable(Constants.UNLISTED_DOCTOR + hqCode),
//                        hosAvailability = masterDataDao.isDataAvailable(Constants.HOSPITAL + hqCode),
//                        cipAvailability = masterDataDao.isDataAvailable(Constants.CIP + hqCode),
                    clusterAvailability = masterDataDao.isDataAvailable(Constants.CLUSTER + hqCode),
                    subordinateAvailability = masterDataDao.isDataAvailable(Constants.SUBORDINATE + hqCode);
            Log.e("SurveyActivity", "showHQ: " + docAvailability + " " + chemAvailability + " " + stkAvailability + " " + ulDocAvailability + " " + clusterAvailability + " " + subordinateAvailability);
//                if(docAvailability && chemAvailability && stkAvailability && ulDocAvailability && hosAvailability && cipAvailability && clusterAvailability){
            if(docAvailability && chemAvailability && stkAvailability && ulDocAvailability && clusterAvailability) {
                Log.d("SurveyActivity", "getHQData: Data Available");
                getMasterData();
            }else if(UtilityClass.isNetworkAvailable(SurveyActivity.this)) {
                getData(hqCode);
            }else {
                commonUtilsMethods.showToastMessage(SurveyActivity.this, getString(R.string.no_network));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData(String hqCode) {
        SynqList = SharedPref.getsyn_hqcode(SurveyActivity.this);
        SynqList.add(hqCode);
        SharedPref.setSyncHQ(SurveyActivity.this, SynqList);
        syncProgressDialog.show();
        List<MasterSyncItemModel> list = new ArrayList<>();
        list.add(new MasterSyncItemModel("Doctor", "Doctor", "getdoctors", Constants.DOCTOR + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Chemist", "Doctor", "getchemist", Constants.CHEMIST + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Stockiest", "Doctor", "getstockist", Constants.STOCKIEST + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Unlisted Doctor", "Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Hospital", 0, "Doctor", "gethospital", Constants.HOSPITAL + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("CIP", 0, "Doctor", "getcip", Constants.CIP + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Cluster", "Doctor", "getterritory", Constants.CLUSTER + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Joint Work", Constants.SUBORDINATE, "getjointwork", Constants.JOINT_WORK + hqCode, 0, false));

        for (int i = 0; i<list.size(); i++) {
            syncMaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hqCode);
        }
    }

    public void syncMaster(String masterFor, String remoteTableName, String LocalTableKeyName, String hqCode) {
        if(UtilityClass.isNetworkAvailable(SurveyActivity.this)) {
            try {
                String baseUrl = SharedPref.getBaseWebUrl(SurveyActivity.this);
                String pathUrl = SharedPref.getPhpPathUrl(SurveyActivity.this);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                apiInterface = RetrofitClient.getRetrofit(SurveyActivity.this, baseUrl + replacedUrl);

                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(SurveyActivity.this);
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode", SharedPref.getSfCode(SurveyActivity.this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(SurveyActivity.this));
                jsonObject.put("Rsf", hqCode);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(SurveyActivity.this), mapString, jsonObject.toString());

                if(call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();

                            if(response.isSuccessful()) {
                                Log.e("test", "response : " + masterFor + " -- " + remoteTableName + " : " + Objects.requireNonNull(response.body()));
                                try {
                                    JsonElement jsonElement = response.body();
                                    if(!jsonElement.isJsonNull()) {
                                        if(jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        }else if(jsonElement.isJsonObject()) {
                                            JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                            if(!jsonObject2.has("success")) {
                                                jsonArray.put(jsonObject2);
                                                success = true;
                                            }else if(jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                                masterDataDao.saveMasterSyncStatus(LocalTableKeyName, 1);
                                            }
                                        }

                                        if(success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(LocalTableKeyName, jsonArray.toString(), 2));
                                            getMasterData();
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            syncProgressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            syncProgressDialog.dismiss();
                            t.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                syncProgressDialog.dismiss();
                e.printStackTrace();
            }
        }else {
            commonUtilsMethods.showToastMessage(SurveyActivity.this, getString(R.string.no_network));
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}