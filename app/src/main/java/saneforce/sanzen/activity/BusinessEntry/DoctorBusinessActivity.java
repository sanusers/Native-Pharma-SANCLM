package saneforce.sanzen.activity.BusinessEntry;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.BusinessEntry.ModelClass.DoctorBusinessModel;
import saneforce.sanzen.activity.BusinessEntry.ModelClass.ProductListModel;
import saneforce.sanzen.activity.BusinessEntry.adapter.AdapterDoctorBusinessProduct;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ActivityDoctorbusinessEntryBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DoctorBusinessActivity extends AppCompatActivity {
    public static ActivityDoctorbusinessEntryBinding doctorbusinessEntryBinding;
    CommonUtilsMethods commonUtilsMethods;
    public static ArrayList<DoctorBusinessModel> DocBusinessProductDetails;
    public static String selectedhq = "";
    String SfType = "", SfCode = "", SfName = "", DivCode = "", usersfcode = "";
    ApiInterface apiInterface;
    String activeflag = "";
    ProgressDialog progressDialog;
    MasterDataDao masterDataDao;
    JSONArray jsonArray;
    JSONObject jsonObject;
    Button btn_apply, btn_clear;
    TextView tv_add_condition;
    TextView tvSpec, tvCate, tvTerritory, tvClass;
    ListView lv_spec, lv_cate, lv_terr, lv_class;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    ArrayList<CustList> filteredNames = new ArrayList<>();
    AdapterDoctorBusinessProduct adaptdoctorproduct;
    private RoomDB roomDB;
    Dialog dialogFilter;
    ConstraintLayout constraintLayout;
    ImageView img_close, img_del;
    String sfcode = "", sfname = "", Rsf = "", selmonth = "", selyear = "";
    String specialityCode = "", categoryCode = "", territoryCode = "", classCode = "";
    String specialityName = "", categoryName = "", territoryName = "", className = "";
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorbusinessEntryBinding = ActivityDoctorbusinessEntryBinding.inflate(getLayoutInflater());
        setContentView(doctorbusinessEntryBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        SfType = SharedPref.getSfType(this);
        usersfcode = SharedPref.getSfCode(this);
        SfName = SharedPref.getSfName(this);
        DivCode = SharedPref.getDivisionCode(this);
        if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
            SfCode = SharedPref.getSfCode(this);
        } else {
            SfCode = SharedPref.getHqCode(this);
            doctorbusinessEntryBinding.edtFieldforce.setText(SfName);
            doctorbusinessEntryBinding.edtFieldforce.setEnabled(false);
            selectedhq = SfCode;
        }

        doctorbusinessEntryBinding.btnStartentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SfType.equalsIgnoreCase("2")) {
                    if (doctorbusinessEntryBinding.edtFieldforce.getText().toString().equalsIgnoreCase("")) {
                        doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
                        Toast.makeText(DoctorBusinessActivity.this, getString(R.string.select_fieldforce), Toast.LENGTH_LONG).show();
                    } else if (doctorbusinessEntryBinding.edtMonth.getText().toString().equalsIgnoreCase("")) {
                        doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
                        Toast.makeText(DoctorBusinessActivity.this, getString(R.string.select_month), Toast.LENGTH_LONG).show();
                    } else {
                        doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
                        JSONObject json = CommonUtilsMethods.CommonObjectParameter(DoctorBusinessActivity.this);
                        try {
                            json.put("tableName", "get_drproduct");
                            json.put("sfcode", SfCode);
                            json.put("division_code", DivCode);
                            json.put("Rsf", selectedhq);
                            json.put("Fdt", doctorbusinessEntryBinding.edtMonth.getText().toString());
                            String value = doctorbusinessEntryBinding.edtMonth.getText().toString(); // "May 2025"
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                                Date date = inputFormat.parse(value);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
                                int year = calendar.get(Calendar.YEAR);
                                json.put("bmonth", month);
                                json.put("byear", year);
                                Log.d("ParsedDate", "Month: " + month + ", Year: " + year);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            json.put("sf_type", SfType);
                            json.put("Designation", SharedPref.getDesig(DoctorBusinessActivity.this));
                            json.put("state_code", SharedPref.getStateCode(DoctorBusinessActivity.this));
                            json.put("subdivision_code", SharedPref.getSubdivisionCode(DoctorBusinessActivity.this));
                            getDoctorProduct(json.toString());
                        } catch (Exception e) {
                        }
                    }
                } else {
                    if (doctorbusinessEntryBinding.edtMonth.getText().toString().equalsIgnoreCase("")) {
                        doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
                        Toast.makeText(DoctorBusinessActivity.this, getString(R.string.select_month), Toast.LENGTH_LONG).show();
                    } else {
                        doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
                        doctorbusinessEntryBinding.searchLayout.setVisibility(View.VISIBLE);
                        doctorbusinessEntryBinding.list.setVisibility(View.VISIBLE);
                        JSONObject json = CommonUtilsMethods.CommonObjectParameter(DoctorBusinessActivity.this);
                        try {
                            json.put("tableName", "get_drproduct");
                            json.put("sfcode", SfCode);
                            json.put("division_code", DivCode);
                            json.put("Rsf", selectedhq);
                            json.put("Fdt", doctorbusinessEntryBinding.edtMonth.getText().toString());
                            String value = doctorbusinessEntryBinding.edtMonth.getText().toString(); // "May 2025"
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                                Date date = inputFormat.parse(value);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
                                int year = calendar.get(Calendar.YEAR);
                                json.put("bmonth", month);
                                json.put("byear", year);
                                Log.d("ParsedDate", "Month: " + month + ", Year: " + year);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            json.put("sf_type", SfType);
                            json.put("Designation", SharedPref.getDesig(DoctorBusinessActivity.this));
                            json.put("state_code", SharedPref.getStateCode(DoctorBusinessActivity.this));
                            json.put("subdivision_code", SharedPref.getSubdivisionCode(DoctorBusinessActivity.this));
                            getDoctorProduct(json.toString());
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });

        doctorbusinessEntryBinding.drBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCancel();
            }
        });

        doctorbusinessEntryBinding.edtMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomMonthYearPicker();
            }
        });

        doctorbusinessEntryBinding.txtMnth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(doctorbusinessEntryBinding.txtMnth.getWindowToken(), 0);
                }
                showCustomMonthYearPicker();
            }
        });

        doctorbusinessEntryBinding.edtFieldforce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(doctorbusinessEntryBinding.edtFieldforce.getWindowToken(), 0);
                }
                doctorbusinessEntryBinding.fragmentSelectHq.setVisibility(View.VISIBLE);
            }
        });

        doctorbusinessEntryBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doctorbusinessEntryBinding.btnSave.setEnabled(false);
                JSONObject json = CommonUtilsMethods.CommonObjectParameter(DoctorBusinessActivity.this);
                try {
                    json.put("tableName", "drproduct_submit");
                    if (SharedPref.getSfType(DoctorBusinessActivity.this).equalsIgnoreCase("2")) {
                        sfcode = SharedPref.getSfCode(DoctorBusinessActivity.this);
                        sfname = SharedPref.getSfName(DoctorBusinessActivity.this);
                        Rsf = SharedPref.getHqCode(DoctorBusinessActivity.this);
                    } else {
                        sfcode = SharedPref.getSfCode(DoctorBusinessActivity.this);
                        sfname = SharedPref.getSfName(DoctorBusinessActivity.this);
                        Rsf = sfcode;
                    }
                    String dateStr = doctorbusinessEntryBinding.edtMonth.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                    try {
                        Date date = sdf.parse(dateStr);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
                        int year = calendar.get(Calendar.YEAR);
                        selmonth = String.valueOf(month);
                        selyear = String.valueOf(year);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    json.put("sfcode", sfcode);
                    json.put("sfname", sfname);
                    json.put("Rsf", Rsf);
                    json.put("Trans_Year", selyear);
                    json.put("Trans_Month", selmonth);
                    json.put("division_code", SharedPref.getDivisionCode(DoctorBusinessActivity.this));
                    json.put("current_Dt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
                    Log.v("products_final", json.toString());
                    finalsubmit(json.toString());
                } catch (Exception e) {
                }
            }
        });

        doctorbusinessEntryBinding.searchCust.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        doctorbusinessEntryBinding.ivFilter.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                CustomizeFiltered();
            }
        });
    }

    public void finalsubmit(String val) {
        try {
            if (progressDialog == null) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(this);
                progressDialog = CommonUtilsMethods.createProgressDialog(this);
                progressDialog.show();
            } else {
                progressDialog.show();
            }

            if (isNetworkConnected()) {
                String baseUrl = SharedPref.getBaseWebUrl(this);
                String pathUrl = SharedPref.getPhpPathUrl(this);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                Log.e("test", "login url : " + baseUrl + replacedUrl);
                apiInterface = RetrofitClient.getRetrofit(this, baseUrl + replacedUrl);
                Log.d("save_obj", String.valueOf(val));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "submit/business_product");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(DoctorBusinessActivity.this), mapString, val);

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                progressDialog.dismiss();
                                try {
                                    assert response.body() != null;
                                    Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        if (jsonObject.getString("success").equalsIgnoreCase("true")) {
                                            doctorbusinessEntryBinding.layoutButtons.setVisibility(View.VISIBLE);
                                            doctorbusinessEntryBinding.btnSave.setVisibility(View.GONE);
                                        }
                                    } catch (Exception e) {
                                        Log.v("chkSamStk", "error---" + e);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                doctorbusinessEntryBinding.btnSave.setEnabled(true);
                                commonUtilsMethods.showToastMessage(DoctorBusinessActivity.this, getResources().getString(R.string.something_wrong));
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            doctorbusinessEntryBinding.btnSave.setEnabled(true);
                            commonUtilsMethods.showToastMessage(DoctorBusinessActivity.this, getResources().getString(R.string.no_network));
                            progressDialog.dismiss();
                        }
                    });
                }
            } else {
                doctorbusinessEntryBinding.btnSave.setEnabled(true);
                commonUtilsMethods.showToastMessage(DoctorBusinessActivity.this, getResources().getString(R.string.no_network));
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            doctorbusinessEntryBinding.btnSave.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }

    private void filter(String text) {
        filteredNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adaptdoctorproduct.filterList(filteredNames);
    }

    public void CustomizeFiltered() {
        dialogFilter = new Dialog(DoctorBusinessActivity.this);
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        dialogFilter.show();
        img_close = dialogFilter.findViewById(R.id.img_close);
        img_del = dialogFilter.findViewById(R.id.img_del);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        tv_add_condition = dialogFilter.findViewById(R.id.btn_add_condition);
        tvSpec = dialogFilter.findViewById(R.id.constraint_speciality);
        tvTerritory = dialogFilter.findViewById(R.id.constraint_territory);
        tvCate = dialogFilter.findViewById(R.id.constraint_category);
        tvClass = dialogFilter.findViewById(R.id.constraint_class);
        lv_spec = dialogFilter.findViewById(R.id.lv_spec);
        lv_cate = dialogFilter.findViewById(R.id.lv_category);
        lv_terr = dialogFilter.findViewById(R.id.lv_territory);
        lv_class = dialogFilter.findViewById(R.id.lv_class);
        tvSpec.setVisibility(View.VISIBLE);
        tvCate.setVisibility(View.VISIBLE);
        tv_add_condition.setVisibility(View.VISIBLE);
        img_del.setVisibility(View.GONE);

        if (!territoryCode.isEmpty()) {
            tvTerritory.setVisibility(View.VISIBLE);
            img_del.setVisibility(View.VISIBLE);
        } else {
            tvTerritory.setVisibility(View.GONE);
            img_del.setVisibility(View.GONE);
        }

        if (!classCode.isEmpty()) {
            if (territoryCode.isEmpty()) {
                tvTerritory.setVisibility(View.INVISIBLE);
            } else {
                tv_add_condition.setVisibility(View.GONE);
            }
            tvClass.setVisibility(View.VISIBLE);
        } else {
            tvClass.setVisibility(View.GONE);
        }

        constraintLayout = dialogFilter.findViewById(R.id.constraint_btns);
        img_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogFilter.dismiss();
            }
        });

        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                specialityCode = "";
                territoryCode = "";
                categoryCode = "";
                classCode = "";
                specialityName = "";
                territoryName = "";
                categoryName = "";
                className = "";
                tvSpec.setText("");
                tvTerritory.setText("");
                tvCate.setText("");
                tvClass.setText("");
                tvSpec.setHint(R.string.speciality);
                tvTerritory.setHint(R.string.territory);
                tvCate.setHint(R.string.category);
                tvClass.setHint(R.string.class_filter);
            }
        });

        tvSpec.setText(specialityName);
        tvTerritory.setText(territoryName);
        tvCate.setText(categoryName);
        tvClass.setText(className);

        tv_add_condition.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                    tvClass.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.GONE);
                } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE) {
                    tvTerritory.setVisibility(View.VISIBLE);
                    img_del.setVisibility(View.VISIBLE);
                }
            }
        });

        img_del.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (!classCode.isEmpty()) {
                    classCode = "";
                    className = "";
                } else if (!territoryCode.isEmpty()) {
                    territoryCode = "";
                    territoryName = "";
                }
                if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvClass.getVisibility() == View.INVISIBLE) {
                    tvTerritory.setVisibility(View.GONE);
                    img_del.setVisibility(View.GONE);
                    tvTerritory.setHint(R.string.territory);
                } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                    tvClass.setVisibility(View.INVISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    tvClass.setHint(R.string.class_filter);
                }
            }
        });

        tvSpec.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_class.setVisibility(View.GONE);
                lv_cate.setVisibility(View.GONE);
                lv_terr.setVisibility(View.GONE);
                if (lv_spec.getVisibility() == View.VISIBLE) {
                    lv_spec.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                } else {
                    getFilterList("Speciality");
                    FillteredAdapter arrayAdapter = new FillteredAdapter(DoctorBusinessActivity.this, filterSelectionList, clickedItem -> {
                        specialityCode = clickedItem.getCode();
                        specialityName = clickedItem.getName();
                        tvSpec.setText(clickedItem.getName());
                        lv_spec.setVisibility(View.GONE);
                        tv_add_condition.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_spec.setAdapter(arrayAdapter);
                    lv_spec.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        tvCate.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_class.setVisibility(View.GONE);
                lv_spec.setVisibility(View.GONE);
                lv_terr.setVisibility(View.GONE);
                if (lv_cate.getVisibility() == View.VISIBLE) {
                    lv_cate.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                } else {
                    getFilterList("Category");
                    FillteredAdapter arrayAdapter = new FillteredAdapter(DoctorBusinessActivity.this, filterSelectionList, clickedItem -> {
                        categoryCode = clickedItem.getCode();
                        categoryName = clickedItem.getName();
                        tvCate.setText(clickedItem.getName());
                        lv_cate.setVisibility(View.GONE);
                        tv_add_condition.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_cate.setAdapter(arrayAdapter);
                    lv_cate.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        tvTerritory.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_class.setVisibility(View.GONE);
                lv_cate.setVisibility(View.GONE);
                lv_spec.setVisibility(View.GONE);
                if (lv_terr.getVisibility() == View.VISIBLE) {
                    lv_terr.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                } else {
                    getFilterList("Territory");
                    FillteredAdapter arrayAdapter = new FillteredAdapter(DoctorBusinessActivity.this, filterSelectionList, clickedItem -> {
                        territoryCode = clickedItem.getCode();
                        territoryName = clickedItem.getName();
                        tvTerritory.setText(clickedItem.getName());
                        lv_terr.setVisibility(View.GONE);
                        tv_add_condition.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_terr.setAdapter(arrayAdapter);
                    lv_terr.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        tvClass.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                lv_spec.setVisibility(View.GONE);
                lv_cate.setVisibility(View.GONE);
                lv_terr.setVisibility(View.GONE);
                if (lv_class.getVisibility() == View.VISIBLE) {
                    lv_class.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                } else {
                    getFilterList("Class");
                    FillteredAdapter arrayAdapter = new FillteredAdapter(DoctorBusinessActivity.this, filterSelectionList, clickedItem -> {
                        classCode = clickedItem.getCode();
                        className = clickedItem.getName();
                        tvClass.setText(clickedItem.getName());
                        lv_class.setVisibility(View.GONE);
                        tv_add_condition.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                    });
                    lv_class.setAdapter(arrayAdapter);
                    lv_class.setVisibility(View.VISIBLE);
                    tv_add_condition.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_apply.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Filtered();
            }
        });

    }

    public void Filtered() {
        ArrayList<CustList> filterCusList = new ArrayList<>();
        if (!filteredNames.isEmpty()) {
            filterCusList.addAll(filteredNames);
        } else {
            filterCusList.addAll(custListArrayList);
        }
        FilltercustArraList.clear();
        if (specialityCode.equalsIgnoreCase("") && categoryCode.equalsIgnoreCase("") && territoryCode.equalsIgnoreCase("") && classCode.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(filterCusList);
            doctorbusinessEntryBinding.tvFilterCount.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        } else {
            for (CustList mList : filterCusList) {
                if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && categoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && territoryCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && territoryCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && specialityCode.isEmpty()
                        && classCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && categoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else if (mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                        && mList.getClassCode().equalsIgnoreCase(classCode)
                        && specialityCode.isEmpty()
                        && territoryCode.isEmpty()) {
                    FilltercustArraList.add(mList);
                } else {
                    if (mList.getSpecialistCode().equalsIgnoreCase(specialityCode)
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getCategoryCode().equalsIgnoreCase(categoryCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getTown_code().equalsIgnoreCase(territoryCode)
                            && specialityCode.isEmpty()
                            && categoryCode.isEmpty()
                            && classCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getClassCode().equalsIgnoreCase(classCode)
                            && specialityCode.isEmpty()
                            && territoryCode.isEmpty()
                            && categoryCode.isEmpty()) {
                        FilltercustArraList.add(mList);
                    }
                }
            }
            doctorbusinessEntryBinding.tvFilterCount.setText(String.valueOf(FilltercustArraList.size()));
        }

        if (FilltercustArraList.isEmpty()) {
            doctorbusinessEntryBinding.noDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getDrCap(DoctorBusinessActivity.this), getString(R.string.found)));
            doctorbusinessEntryBinding.noDoctor.setVisibility(View.VISIBLE);
            doctorbusinessEntryBinding.rvCustListSelection.setVisibility(View.GONE);
        } else {
            doctorbusinessEntryBinding.noDoctor.setVisibility(View.GONE);
            doctorbusinessEntryBinding.rvCustListSelection.setVisibility(View.VISIBLE);
            adaptdoctorproduct.filterList(FilltercustArraList);
        }
        dialogFilter.dismiss();
    }

    private void getFilterList(String requiredList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (requiredList.equalsIgnoreCase("Speciality")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Category")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Territory")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + selectedhq).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Class")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray();
            }
            filterSelectionList.clear();
            Log.v("jsonArray", "--" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"), jsonObject.getString("Code")));
            }
        } catch (Exception ignored) {
        }
    }

    public void commonFun() {
        try {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } catch (Exception e) {
        }
    }

    private void showCustomMonthYearPicker() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_month_year_picker, null);
        final NumberPicker monthPicker = dialogView.findViewById(R.id.month_picker);
        final NumberPicker yearPicker = dialogView.findViewById(R.id.year_picker);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, 1); // Start of current month
        // Generate last 3 valid months and their years
        ArrayList<String> validMonths = new ArrayList<>();
        ArrayList<Integer> validYears = new ArrayList<>();
        Calendar temp = (Calendar) now.clone();
        for (int i = 1; i <= 3; i++) {
            temp.add(Calendar.MONTH, -1);
            String monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(temp.getTime());
            int year = temp.get(Calendar.YEAR);
            validMonths.add(monthName);
            if (!validYears.contains(year)) {
                validYears.add(year); // Only add year once
            }
        }
        // Reverse for chronological order
        Collections.reverse(validMonths);
        Collections.reverse(validYears);
        // Convert to arrays
        String[] monthNames = validMonths.toArray(new String[0]);
        String[] yearStrings = validYears.stream().map(String::valueOf).toArray(String[]::new);
        // Set values to pickers
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(monthNames.length - 1);
        monthPicker.setDisplayedValues(monthNames);
        monthPicker.setWrapSelectorWheel(false);
        yearPicker.setMinValue(0);
        yearPicker.setMaxValue(yearStrings.length - 1);
        yearPicker.setDisplayedValues(yearStrings);
        yearPicker.setWrapSelectorWheel(false);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Month and Year")
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, which) -> {
                    String selectedMonth = monthNames[monthPicker.getValue()];
                    String selectedYear = yearStrings[yearPicker.getValue()];
                    String finalDate = selectedMonth + " " + selectedYear;
                    doctorbusinessEntryBinding.edtMonth.setText(finalDate);
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        // Resize dialog width
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.60), // 60% width
                             WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) DoctorBusinessActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void handleCancel() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void getDoctorProduct(String val) {
        try {
            if (progressDialog == null) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(this);
                progressDialog = CommonUtilsMethods.createProgressDialog(this);
                progressDialog.show();
            } else {
                progressDialog.show();
            }

            if (isNetworkConnected()) {
                String baseUrl = SharedPref.getBaseWebUrl(this);
                String pathUrl = SharedPref.getPhpPathUrl(this);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                Log.e("test", "login url : " + baseUrl + replacedUrl);
                apiInterface = RetrofitClient.getRetrofit(this, baseUrl + replacedUrl);
                Log.d("save_obj", String.valueOf(val));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "get/business_product");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(DoctorBusinessActivity.this), mapString, val);

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                progressDialog.dismiss();
                                try {
                                    assert response.body() != null;
                                    DocBusinessProductDetails = new ArrayList<>();
                                    Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                    try {
                                        JsonElement jsonElement = response.body();
                                        if (jsonElement != null) {
                                            JSONArray jsonArray1 = new JSONArray(jsonElement.getAsJsonArray().toString());
                                            if (jsonArray1.length() > 0) {
                                                for (int j = 0; j < jsonArray1.length(); j++) {
                                                    JSONObject jsdctrbusiness = jsonArray1.getJSONObject(j);
                                                    activeflag = jsdctrbusiness.getString("Active");
                                                    if (activeflag.equalsIgnoreCase("1")) {
                                                        doctorbusinessEntryBinding.btnSave.setVisibility(View.GONE);
                                                        doctorbusinessEntryBinding.layoutButtons.setVisibility(View.VISIBLE);
                                                    } else {
                                                        doctorbusinessEntryBinding.layoutButtons.setVisibility(View.VISIBLE);
                                                    }
                                                    DocBusinessProductDetails.add(0, new DoctorBusinessModel(jsdctrbusiness.getString("Head_No"), jsdctrbusiness.getString("ListedDrCode"), jsdctrbusiness.getString("ListedDr_Name"), jsdctrbusiness.getString("Active"), jsdctrbusiness.getString("Product_data")));
                                                }
                                            } else if (jsonArray1.length() == 0) {
                                                doctorbusinessEntryBinding.layoutButtons.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    } catch (Exception e) {
                                        Log.v("chkSamStk", "error---" + e);
                                    }
                                    SetupAdapter();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                doctorbusinessEntryBinding.layoutButtons.setVisibility(View.GONE);
                                doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
                                commonUtilsMethods.showToastMessage(DoctorBusinessActivity.this, getResources().getString(R.string.something_wrong));
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            doctorbusinessEntryBinding.layoutButtons.setVisibility(View.GONE);
                            doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
                            commonUtilsMethods.showToastMessage(DoctorBusinessActivity.this, getResources().getString(R.string.no_network));
                            progressDialog.dismiss();
                        }
                    });
                }
            } else {
                doctorbusinessEntryBinding.layoutButtons.setVisibility(View.GONE);
                doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
                commonUtilsMethods.showToastMessage(DoctorBusinessActivity.this, getResources().getString(R.string.no_network));
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            doctorbusinessEntryBinding.layoutButtons.setVisibility(View.GONE);
            doctorbusinessEntryBinding.btnStartentry.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }

    private void SetupAdapter() {
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + selectedhq).getMasterSyncDataJsonArray();
            if (jsonArray.length() == 0) {
                CommonUtilsMethods.showToastMessage(DoctorBusinessActivity.this, DoctorBusinessActivity.this.getString(R.string.no_data_found) + "  " + DoctorBusinessActivity.this.getString(R.string.do_master_sync));
            }
//            Log.v("DrCall", "-dr_full_length-" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
//                      Log.v("DrCall", "333");
                    custListArrayList = SaveData(jsonObject, i);
                } catch (Exception e) {
                    Log.v("DrCall", "dr--error-1-" + e);
                }
            }
            int count = custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                        custListArrayList.remove(j--);
                        count--;
                    }
                }
            }
        } catch (Exception e) {
            Log.v("DrCall", "-dr--error-2-" + e);
        }
        if (custListArrayList.size() > 0) {
            doctorbusinessEntryBinding.searchLayout.setVisibility(View.VISIBLE);
            doctorbusinessEntryBinding.list.setVisibility(View.VISIBLE);
        }
        Log.v("call", "-dr--size--" + custListArrayList.size());
        FilltercustArraList.clear();
        FilltercustArraList.addAll(custListArrayList);

        if (FilltercustArraList.isEmpty()) {
            doctorbusinessEntryBinding.noDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getDrCap(DoctorBusinessActivity.this), getString(R.string.found)));
            doctorbusinessEntryBinding.noDoctor.setVisibility(View.VISIBLE);
            doctorbusinessEntryBinding.rvCustListSelection.setVisibility(View.GONE);
        } else {
            doctorbusinessEntryBinding.noDoctor.setVisibility(View.GONE);
            doctorbusinessEntryBinding.rvCustListSelection.setVisibility(View.VISIBLE);
            adaptdoctorproduct = new AdapterDoctorBusinessProduct(
                    DoctorBusinessActivity.this,
                    DoctorBusinessActivity.this,
                    FilltercustArraList,
                    DocBusinessProductDetails,
                    "1",
                    (doctor, position) -> {
                        boolean doctorFound = false;

                        if (DocBusinessProductDetails.size() > 0) {
                            for (int i = 0; i < DocBusinessProductDetails.size(); i++) {
                                if (DocBusinessProductDetails.get(i).getDrcode().equalsIgnoreCase(doctor.getCode())) {
                                    Intent intent = new Intent(DoctorBusinessActivity.this, DocBusinessProductList.class);
                                    intent.putExtra("doctorCode", doctor.getCode());
                                    intent.putExtra("position", position);
                                    intent.putExtra("doc_name", doctor.getName());
                                    intent.putExtra("Month", doctorbusinessEntryBinding.edtMonth.getText().toString());
                                    intent.putExtra("dr_code", doctor.getCode());
                                    intent.putExtra("cat_code", doctor.getCategoryCode());
                                    intent.putExtra("cat_name", doctor.getCategory());
                                    intent.putExtra("ter_code", doctor.getTown_code());
                                    intent.putExtra("ter_name", doctor.getTown_name());
                                    intent.putExtra("spec_code", doctor.getSpecialistCode());
                                    intent.putExtra("spec_name", doctor.getSpecialist());
                                    intent.putExtra("class_code", doctor.getClassCode());
                                    intent.putExtra("class_name", doctor.getclass());
                                    intent.putExtra("active_flag", activeflag);
                                    intent.putExtra("selectedhq", selectedhq);
                                    intent.putExtra("detailcode", DocBusinessProductDetails.get(i).getHeaderno());
                                    intent.putExtra("product_json", DocBusinessProductDetails.get(i).getJsonArray());
                                    startActivityForResult(intent, 1001);
                                    doctorFound = true;
                                    break;
                                }
                            }
                        }

                        // If doctor is not found in DocBusinessProductDetails, run this
                        if (!doctorFound) {
                            Intent intent = new Intent(DoctorBusinessActivity.this, DocBusinessProductList.class);
                            intent.putExtra("doctorCode", doctor.getCode());
                            intent.putExtra("position", position);
                            intent.putExtra("doc_name", doctor.getName());
                            intent.putExtra("Month", doctorbusinessEntryBinding.edtMonth.getText().toString());
                            intent.putExtra("dr_code", doctor.getCode());
                            intent.putExtra("cat_code", doctor.getCategoryCode());
                            intent.putExtra("cat_name", doctor.getCategory());
                            intent.putExtra("ter_code", doctor.getTown_code());
                            intent.putExtra("ter_name", doctor.getTown_name());
                            intent.putExtra("spec_code", doctor.getSpecialistCode());
                            intent.putExtra("spec_name", doctor.getSpecialist());
                            intent.putExtra("class_code", doctor.getClassCode());
                            intent.putExtra("class_name", doctor.getclass());
                            intent.putExtra("product_json", "");
                            intent.putExtra("active_flag", activeflag);
                            intent.putExtra("selectedhq", selectedhq);
                            intent.putExtra("detailcode", "0");
                            startActivityForResult(intent, 1001);
                        }
                    });
            doctorbusinessEntryBinding.rvCustListSelection.setItemAnimator(new DefaultItemAnimator());
            doctorbusinessEntryBinding.rvCustListSelection.setLayoutManager(new GridLayoutManager(DoctorBusinessActivity.this, 4, GridLayoutManager.VERTICAL, false));
            doctorbusinessEntryBinding.rvCustListSelection.setAdapter(adaptdoctorproduct);
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        }
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int index) {
        try {
            String code = jsonObject.getString("Code");

            CustList drList = new CustList(
                    jsonObject.getString("Name"),
                    code, "1",
                    jsonObject.getString("Category"),
                    jsonObject.getString("CategoryCode"),
                    jsonObject.getString("Specialty"),
                    jsonObject.getString("SpecialtyCode"),
                    jsonObject.getString("Town_Name"),
                    jsonObject.getString("Town_Code"),
//                    jsonObject.getString("GEOTagCnt"),
//                    jsonObject.getString("MaxGeoMap"),
                    String.valueOf(index),
//                    jsonObject.getString("Lat"),
//                    jsonObject.getString("Long"),
                    jsonObject.getString("HosAddr"),
                    jsonObject.getString("DOB"),
                    jsonObject.getString("DOW"),
                    jsonObject.getString("DrEmail"),
                    jsonObject.getString("Mobile"),
                    jsonObject.getString("Phone"),
                    jsonObject.getString("DrDesig"),
                    jsonObject.optString("Product_Code", ""),
                    "", // Will be replaced by calculated total
                    jsonObject.getString("MProd"),
                    jsonObject.getString("Tlvst"),
                    jsonObject.getString("Doc_Class_ShortName"),
                    jsonObject.getString("Doc_ClsCode"),
                    true,
                    "0");

            double totalValue = 0.0;
            for (DoctorBusinessModel doctorProduct : DocBusinessProductDetails) {
                if (doctorProduct.getDrcode().equalsIgnoreCase(code)) {
                    try {
                        String json = doctorProduct.getJsonArray();
                        Log.d("ProductJSON", "Doctor: " + code + ", JSON: " + json);
                        JSONArray productArray = new JSONArray(json);
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ProductListModel>>() {
                        }.getType();
                        List<ProductListModel> productList = gson.fromJson(productArray.toString(), listType);
                        for (ProductListModel product : productList) {
                            Log.d("ProductValue", "Doctor: " + code + ", Value: " + product.value);
                            if (product.value != null && !product.value.trim().isEmpty()) {
                                try {
                                    totalValue += Double.parseDouble(product.value);
                                } catch (NumberFormatException e) {
                                    Log.e("DrCall", "Invalid value for doctor " + code + ": " + product.value);
                                }
                            }
                        }
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            drList.setTotvalue(String.format(Locale.getDefault(), "%.2f", totalValue));
            custListArrayList.add(drList);
        } catch (Exception e) {
            Log.v("DrCall", "--SaveData-error-- " + e.toString());
        }
        return custListArrayList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            String updatedValue = data.getStringExtra("updatedValue");
            String headerno = data.getStringExtra("headerno");
            String flg = data.getStringExtra("activeflg");

            if (position >= 0 && position < custListArrayList.size()) {
                custListArrayList.get(position).setTotvalue(updatedValue);
                String updatedProductJson = data.getStringExtra("updatedProductJson");
                String doctorCode = custListArrayList.get(position).getCode();
                String doctorName = custListArrayList.get(position).getName();
                boolean found = false;
                for (int i = 0; i < DocBusinessProductDetails.size(); i++) {
                    if (DocBusinessProductDetails.get(i).getDrcode().equalsIgnoreCase(doctorCode)) {
                        DocBusinessProductDetails.get(i).setJsonArray(updatedProductJson);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    DoctorBusinessModel newEntry = new DoctorBusinessModel();
                    newEntry.setDrcode(doctorCode);
                    newEntry.setDrname(doctorName);
                    newEntry.setHeaderno(headerno);
                    newEntry.setActiveflag(flg);
                    newEntry.setJsonArray(updatedProductJson);
                    DocBusinessProductDetails.add(newEntry);
                }
                adaptdoctorproduct.notifyItemChanged(position);
            }
        }
    }
}
