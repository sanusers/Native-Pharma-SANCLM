package saneforce.sanzen.activity.myresource;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.MapsAddition;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityProfilingBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.LoginTableDetails.LoginDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class ProfilingActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static TextView Qualification, Speciality, Category;
    public static String Qual_code = "", spec_code = "", cate_code = "",SfType = "", SfCode = "", SfName = "", DivCode = "",Code,gender="";

    String qualif, cate, spec, dob, dow, address, mobile, phone="", email, docname, doc_code, Dcr_posname, Lat, Long, Town_val,town,fullobject="";
    ProgressDialog progressDialog;
    private GoogleMap mMap;
    private CommonUtilsMethods commonUtilsMethods;
    public static ActivityProfilingBinding activityProfilingBinding;
    private RoomDB roomDB;
    private LoginDataDao loginDataDao;
    private MasterDataDao masterDataDao;
    String CustValue,CustType,geotagcount,maxcount;
    int chemistStatus = 0,categoryStatus = 0;
    ArrayList<MasterSyncItemModel> arrayForAdapter = new ArrayList<>();
    ArrayList<String> Latitude= new ArrayList<>();
    ArrayList<String> Longitude= new ArrayList<>();
    ArrayList<String> Address= new ArrayList<>();

    ApiInterface apiInterface;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ArrayList<MasterSyncItemModel> doctorModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> stockiestModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> chemistModelArray = new ArrayList<>();
    ArrayList<MasterSyncItemModel> unlistedDrModelArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityProfilingBinding = ActivityProfilingBinding.inflate(getLayoutInflater());
        setContentView(activityProfilingBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            docname = extra.getString("Doc_name");
            Code = extra.getString("Doc_code");
            town = extra.getString("Town");
            qualif = extra.getString("Qual_values");
            spec = extra.getString("Spec_values");
            cate = extra.getString("cate_values");
            email = extra.getString("EMAIL");
            mobile = extra.getString("MOB");
            CustType = extra.getString("PosDCRname");
            fullobject = extra.getString("Doc_obj");
            if (CustType.equalsIgnoreCase("D")) {
                if (!fullobject.equalsIgnoreCase("")) {
                    try {
                        jsonArray = new JSONArray(fullobject);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            try {
                                if (jsonObject.getString("Code").equalsIgnoreCase(Code)) {
                                    Latitude.add(jsonObject.getString("Lat"));
                                    Longitude.add(jsonObject.getString("Long"));
                                    Address.add(jsonObject.getString("Addrs"));
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (CustType.equalsIgnoreCase("C")||CustType.equalsIgnoreCase("S")) {
                if (!fullobject.equalsIgnoreCase("")) {
                    try {
                        jsonArray = new JSONArray(fullobject);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            try {
                                if (jsonObject.getString("Code").equalsIgnoreCase(Code)) {
                                    Latitude.add(jsonObject.getString("lat"));
                                    Longitude.add(jsonObject.getString("long"));
                                    Address.add(jsonObject.getString("addrs"));
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (CustType.equalsIgnoreCase("U")) {
                if (!fullobject.equalsIgnoreCase("")) {
                    try {
                        jsonArray = new JSONArray(fullobject);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            try {
                                if (jsonObject.getString("Code").equalsIgnoreCase(Code)) {
                                    Latitude.add(jsonObject.getString("lat"));
                                    Longitude.add(jsonObject.getString("long"));
                                    Address.add(jsonObject.getString("addr"));
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (CustType.equalsIgnoreCase("D")||CustType.equalsIgnoreCase("U")) {
                cate_code = extra.getString("cate_code");
                spec_code = extra.getString("Spec_code");
                Qual_code= extra.getString("Qual_code");
                gender = extra.getString("ListedDrSex");
            }
            else{
                if (CustType.equalsIgnoreCase("C")){
                    cate_code = extra.getString("cate_code");
                    if(!cate_code.equalsIgnoreCase("")) {
                        cate=getChemistCategory(cate_code);
                    }
                   // cate = extra.getString("cate_values");
                }
            }
            if (extra.getString("PHN").equalsIgnoreCase("") || extra.getString("PHN").equalsIgnoreCase("null")) {
                phone = "";
            } else {
                phone = extra.getString("PHN");
            }
            if (extra.getString("DOB").equalsIgnoreCase("") || extra.getString("DOB").equalsIgnoreCase("null")) {
                dob = "";
            } else {
                dob = extra.getString("DOB");
            }
            if (extra.getString("DOW").equalsIgnoreCase("") || extra.getString("DOW").equalsIgnoreCase("null")) {
                dow = "";
            } else {
                dow = extra.getString("DOW");
            }
            address = extra.getString("ADDRESS");
            geotagcount = extra.getString("tagcount");
            maxcount = extra.getString("maxcount");

        }

        if (CustType.equalsIgnoreCase("D")) {
            activityProfilingBinding.layScroll.setVisibility(View.VISIBLE);
            activityProfilingBinding.layScroll1.setVisibility(View.GONE);
            if (SharedPref.getDrCap(this).isEmpty() || SharedPref.getDrCap(this) == null) {
                activityProfilingBinding.drtagname.setText(getResources().getString(R.string.txt_doctor) + " " + "Details");
            } else {
                activityProfilingBinding.drtagname.setText(SharedPref.getDrCap(this) + " " + "Details");
            }
            activityProfilingBinding.edtDctr.setText(docname);
            activityProfilingBinding.edtCluster.setText(town);
            activityProfilingBinding.edtDob.setText(dob);
            activityProfilingBinding.edtDow.setText(dow);
            activityProfilingBinding.txtSelectSpec.setText(spec);
            activityProfilingBinding.txtSelectCategory.setText(cate);
            activityProfilingBinding.txtSelectQua.setText(qualif);
            activityProfilingBinding.edtEmail.setText(email);
            activityProfilingBinding.edtMob.setText(mobile);
            activityProfilingBinding.edtPhone.setText(phone);
            activityProfilingBinding.edtAddr.setText(address);
            activityProfilingBinding.txtSelectGender.setText(gender);
            if (SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
                activityProfilingBinding.secondlayout.setVisibility(View.VISIBLE);
            } else {
                activityProfilingBinding.secondlayout.setVisibility(View.GONE);
            }
            activityProfilingBinding.txtGeocount.setText(geotagcount + "/" + maxcount);
            if (!geotagcount.equalsIgnoreCase("0")) {
                if (geotagcount.equalsIgnoreCase("1")) {
                    activityProfilingBinding.geotwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geothree.setVisibility(View.GONE);
                    activityProfilingBinding.geofour.setVisibility(View.GONE);
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                } else if (geotagcount.equalsIgnoreCase("2")) {
                    activityProfilingBinding.geotwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geothree.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geofour.setVisibility(View.GONE);
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                    String TagAddress1=Address.get(1);
                    activityProfilingBinding.tagaddress2.setText(TagAddress1);
                } else if (geotagcount.equalsIgnoreCase("3")) {
                    activityProfilingBinding.geotwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geothree.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geofour.setVisibility(View.VISIBLE);
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                    String TagAddress1=Address.get(1);
                    activityProfilingBinding.tagaddress2.setText(TagAddress1);
                    String TagAddress2=Address.get(2);
                    activityProfilingBinding.tagaddress3.setText(TagAddress2);
                }
            } else {
                activityProfilingBinding.notagdr.setVisibility(View.VISIBLE);
                activityProfilingBinding.geotwo.setVisibility(View.GONE);
                activityProfilingBinding.geothree.setVisibility(View.GONE);
                activityProfilingBinding.geofour.setVisibility(View.GONE);
            }
        } else if (CustType.equalsIgnoreCase("C")) {
            activityProfilingBinding.layScroll.setVisibility(View.GONE);
            activityProfilingBinding.layScroll1.setVisibility(View.VISIBLE);
            if (SharedPref.getChmCap(this).isEmpty() || SharedPref.getChmCap(this) == null) {
                activityProfilingBinding.drtagname.setText(getResources().getString(R.string.txt_chemist) + " " + "Details");
            } else {
                activityProfilingBinding.drtagname.setText(SharedPref.getChmCap(this) + " " + "Details");
            }
            activityProfilingBinding.edtChm.setText(docname);
            activityProfilingBinding.edtChmcluster.setText(town);
            activityProfilingBinding.edtChmdob.setText(dob);
            activityProfilingBinding.edtChmdow.setText(dow);
            activityProfilingBinding.txtSelectChmcat.setText(cate);
            activityProfilingBinding.edtChmemail.setText(email);
            activityProfilingBinding.edtChmmob.setText(mobile);
            activityProfilingBinding.edtChmphone.setText(phone);
            activityProfilingBinding.edtChmaddr.setText(address);
            activityProfilingBinding.txtChmgeocount.setText(geotagcount + "/" + maxcount);
            if (SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
                activityProfilingBinding.secondchmlayout.setVisibility(View.VISIBLE);
            } else {
                activityProfilingBinding.secondchmlayout.setVisibility(View.GONE);
            }
            activityProfilingBinding.txtChmgeocount.setText(geotagcount + "/" + maxcount);
            if (!geotagcount.equalsIgnoreCase("0")) {
                if (geotagcount.equalsIgnoreCase("1")) {
                    activityProfilingBinding.geochmtwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmthree.setVisibility(View.GONE);
                    activityProfilingBinding.geochmfour.setVisibility(View.GONE);
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.chmaddress1.setText(TagAddress);
                } else if (geotagcount.equalsIgnoreCase("2")) {
                    activityProfilingBinding.geochmtwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmthree.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmfour.setVisibility(View.GONE);
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.chmaddress1.setText(TagAddress);
                    String TagAddress1=Address.get(1);
                    activityProfilingBinding.chmaddress2.setText(TagAddress1);
                } else if (geotagcount.equalsIgnoreCase("3")) {
                    activityProfilingBinding.geochmtwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmthree.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmfour.setVisibility(View.VISIBLE);
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.chmaddress1.setText(TagAddress);
                    String TagAddress1=Address.get(1);
                    activityProfilingBinding.chmaddress2.setText(TagAddress1);
                    String TagAddress2=Address.get(2);
                    activityProfilingBinding.chmaddress3.setText(TagAddress2);
                }
            } else {
                activityProfilingBinding.notagchm.setVisibility(View.VISIBLE);
                activityProfilingBinding.geochmtwo.setVisibility(View.GONE);
                activityProfilingBinding.geochmthree.setVisibility(View.GONE);
                activityProfilingBinding.geochmfour.setVisibility(View.GONE);
            }
        } else if (CustType.equalsIgnoreCase("S")) {
            activityProfilingBinding.layScroll.setVisibility(View.GONE);
            activityProfilingBinding.layScroll1.setVisibility(View.VISIBLE);
            if (SharedPref.getStkCap(this).isEmpty() || SharedPref.getStkCap(this) == null) {
                activityProfilingBinding.drtagname.setText(getResources().getString(R.string.txt_stockist) + " " + "Details");
            } else {
                activityProfilingBinding.drtagname.setText(SharedPref.getStkCap(this) + " " + "Details");
            }
            activityProfilingBinding.edtChm.setText(docname);
            activityProfilingBinding.edtChmcluster.setText(town);
            activityProfilingBinding.edtChmdob.setText(dob);
            activityProfilingBinding.edtChmdow.setText(dow);
            activityProfilingBinding.txtSelectChmcat.setText(cate);
            activityProfilingBinding.edtChmemail.setText(email);
            activityProfilingBinding.edtChmmob.setText(mobile);
            activityProfilingBinding.edtChmphone.setText(phone);
            activityProfilingBinding.edtChmaddr.setText(address);
            activityProfilingBinding.txtChmgeocount.setText(geotagcount + "/" + maxcount);
            if (SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
                activityProfilingBinding.secondlayout.setVisibility(View.VISIBLE);
            } else {
                activityProfilingBinding.secondlayout.setVisibility(View.GONE);
            }
            if (!geotagcount.equalsIgnoreCase("0")) {
                if (geotagcount.equalsIgnoreCase("1")) {
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                    activityProfilingBinding.geochmtwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmthree.setVisibility(View.GONE);
                    activityProfilingBinding.geochmfour.setVisibility(View.GONE);
                } else if (geotagcount.equalsIgnoreCase("2")) {
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                    String TagAddress1=Address.get(1);
                    activityProfilingBinding.tagaddress2.setText(TagAddress1);
                    activityProfilingBinding.geochmtwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmthree.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmfour.setVisibility(View.GONE);
                } else if (geotagcount.equalsIgnoreCase("3")) {
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                    String TagAddress1=Address.get(1);
                    activityProfilingBinding.tagaddress2.setText(TagAddress1);
                    String TagAddress2=Address.get(2);
                    activityProfilingBinding.tagaddress3.setText(TagAddress2);
                    activityProfilingBinding.geochmtwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmthree.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geochmfour.setVisibility(View.VISIBLE);
                }
            } else {
                activityProfilingBinding.notagchm.setVisibility(View.VISIBLE);
                activityProfilingBinding.geochmtwo.setVisibility(View.GONE);
                activityProfilingBinding.geochmthree.setVisibility(View.GONE);
                activityProfilingBinding.geochmfour.setVisibility(View.GONE);
            }
        } else if (CustType.equalsIgnoreCase("U")) {
            activityProfilingBinding.layScroll.setVisibility(View.VISIBLE);
            activityProfilingBinding.layScroll1.setVisibility(View.GONE);
            activityProfilingBinding.three.setVisibility(View.GONE);
            // Adjust weights of layoutOne and layoutTwo
            //LinearLayout.LayoutParams paramsOne = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f);
            //LinearLayout.LayoutParams paramsTwo = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f);
            LinearLayout.LayoutParams paramsOne = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            LinearLayout.LayoutParams paramsTwo = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            paramsOne.setMargins(15, 0, 0, 0);
            paramsTwo.setMargins(15, 0, 15, 0);
            activityProfilingBinding.one.setLayoutParams(paramsOne);
            activityProfilingBinding.two.setLayoutParams(paramsTwo);
            if (SharedPref.getUNLcap(this).isEmpty() || SharedPref.getUNLcap(this) == null) {
                activityProfilingBinding.drtagname.setText(getResources().getString(R.string.txt_undr) + " " + "Details");
            } else {
                activityProfilingBinding.drtagname.setText(SharedPref.getUNLcap(this) + " " + "Details");
            }
            activityProfilingBinding.edtDctr.setText(docname);
            activityProfilingBinding.edtCluster.setText(town);
            activityProfilingBinding.edtDob.setText(dob);
            activityProfilingBinding.edtDow.setText(dow);
            activityProfilingBinding.txtSelectSpec.setText(spec);
            activityProfilingBinding.txtSelectCategory.setText(cate);
            activityProfilingBinding.txtSelectQua.setText(qualif);
            activityProfilingBinding.edtEmail.setText(email);
            activityProfilingBinding.edtMob.setText(mobile);
            activityProfilingBinding.edtPhone.setText(phone);
            activityProfilingBinding.edtAddr.setText(address);
            activityProfilingBinding.txtSelectGender.setText(gender);
            activityProfilingBinding.txtGeocount.setText(geotagcount + "/" + maxcount);
            if (SharedPref.getGeoChk(this).equalsIgnoreCase("0")) {
                activityProfilingBinding.secondlayout.setVisibility(View.VISIBLE);
            } else {
                activityProfilingBinding.secondlayout.setVisibility(View.GONE);
            }
            activityProfilingBinding.txtGeocount.setText(geotagcount + "/" + maxcount);
            if (!geotagcount.equalsIgnoreCase("0")) {
                if (geotagcount.equalsIgnoreCase("1")) {
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                    activityProfilingBinding.geotwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geothree.setVisibility(View.GONE);
                    activityProfilingBinding.geofour.setVisibility(View.GONE);
                } else if (geotagcount.equalsIgnoreCase("2")) {
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                    String TagAddress1=Address.get(1);
                    activityProfilingBinding.tagaddress2.setText(TagAddress1);
                    activityProfilingBinding.geotwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geothree.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geofour.setVisibility(View.GONE);
                } else if (geotagcount.equalsIgnoreCase("3")) {
                    String TagAddress=Address.get(0);
                    activityProfilingBinding.tagaddress1.setText(TagAddress);
                    String TagAddress1=Address.get(1);
                    activityProfilingBinding.tagaddress2.setText(TagAddress1);
                    String TagAddress2=Address.get(2);
                    activityProfilingBinding.tagaddress3.setText(TagAddress2);
                    activityProfilingBinding.geotwo.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geothree.setVisibility(View.VISIBLE);
                    activityProfilingBinding.geofour.setVisibility(View.VISIBLE);
                }
            } else {
                activityProfilingBinding.notagdr.setVisibility(View.VISIBLE);
                activityProfilingBinding.geotwo.setVisibility(View.GONE);
                activityProfilingBinding.geothree.setVisibility(View.GONE);
                activityProfilingBinding.geofour.setVisibility(View.GONE);
            }
        }


        activityProfilingBinding.adddrClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCancel();
            }
        });


        activityProfilingBinding.edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.edtDob.getWindowToken(), 0);
                }
                showDatePickerDialogforDOB();
            }
        });

        activityProfilingBinding.edtMob.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); // Max length 15

        activityProfilingBinding.edtMob.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.length() < 7) {
                    // Show error only if input is between 1 and 6 characters
                    activityProfilingBinding.edtMob.setError(getResources().getString(R.string.enter_valid_Mobile));
                } else {
                    // Remove error when field is empty or valid
                    activityProfilingBinding.edtMob.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        activityProfilingBinding.edtPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); // Max length 15

        activityProfilingBinding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.length() < 7) {
                    // Show error only if input is between 1 and 6 characters
                    activityProfilingBinding.edtPhone.setError(getResources().getString(R.string.enter_valid_phone));
                } else {
                    // Remove error when field is empty or valid
                    activityProfilingBinding.edtPhone.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        activityProfilingBinding.edtChmmob.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); // Max length 15

        activityProfilingBinding.edtChmmob.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.length() < 7) {
                    // Show error only if input is between 1 and 6 characters
                    activityProfilingBinding.edtChmmob.setError(getResources().getString(R.string.enter_valid_Mobile));
                } else {
                    // Remove error when field is empty or valid
                    activityProfilingBinding.edtChmmob.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        activityProfilingBinding.edtChmphone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)}); // Max length 15

        activityProfilingBinding.edtChmphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.length() < 7) {
                    // Show error only if input is between 1 and 6 characters
                    activityProfilingBinding.edtChmphone.setError(getResources().getString(R.string.enter_valid_phone));
                } else {
                    // Remove error when field is empty or valid
                    activityProfilingBinding.edtChmphone.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        activityProfilingBinding.edtDow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.edtDow.getWindowToken(), 0);
                }
                showDatePickerDialogforDOW();
            }
        });
        activityProfilingBinding.edtChmdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.edtChmdob.getWindowToken(), 0);
                }
                showDatePickerDialogforChmDOB();
            }
        });

        activityProfilingBinding.edtChmdow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.edtChmdow.getWindowToken(), 0);
                }
                showDatePickerDialogforChmDOW();
            }
        });


        activityProfilingBinding.txtViewonmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, MapView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Lat", Latitude.get(0));
                    intent.putExtra("Long", Longitude.get(0));
                    context.startActivity(intent);
                } else {
                    commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.no_network));
                }
            }
        });
        activityProfilingBinding.txtViewonmap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, MapView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Lat", Latitude.get(1));
                    intent.putExtra("Long", Longitude.get(1));
                    context.startActivity(intent);

                } else {
                    commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.no_network));
                }
            }

        });
        activityProfilingBinding.txtViewonmap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, MapView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Lat", Latitude.get(2));
                    intent.putExtra("Long", Longitude.get(2));
                    context.startActivity(intent);

                } else {
                    commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.no_network));
                }
            }

        });
        activityProfilingBinding.txtChmviewonmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, MapView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Lat", Latitude.get(0));
                    intent.putExtra("Long", Longitude.get(0));
                    context.startActivity(intent);

                } else {
                    commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.no_network));
                }
            }

        });
        activityProfilingBinding.txtChmviewonmap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, MapView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Lat", Latitude.get(1));
                    intent.putExtra("Long", Longitude.get(1));
                    context.startActivity(intent);

                } else {
                    commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.no_network));
                }
            }

        });
        activityProfilingBinding.txtChmviewonmap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilityClass.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, MapView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Lat", Latitude.get(2));
                    intent.putExtra("Long", Longitude.get(2));
                    context.startActivity(intent);

                } else {
                    commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.no_network));
                }
            }

        });
        activityProfilingBinding.txtSelectQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.txtSelectQua.getWindowToken(), 0);
                }
                activityProfilingBinding.fragmentSelectQuali.setVisibility(View.VISIBLE);
            }
        });

        activityProfilingBinding.txtSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.txtSelectCategory.getWindowToken(), 0);
                }
                activityProfilingBinding.fragmentSelectCat.setVisibility(View.VISIBLE);
            }
        });
        activityProfilingBinding.txtSelectGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.txtSelectGender.getWindowToken(), 0);
                }
                activityProfilingBinding.fragmentSelectGender.setVisibility(View.VISIBLE);
            }
        });



        activityProfilingBinding.txtSelectChmcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.txtSelectChmcat.getWindowToken(), 0);
                }
                activityProfilingBinding.fragmentSelectChmcat.setVisibility(View.VISIBLE);
            }
        });

        activityProfilingBinding.txtSelectSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activityProfilingBinding.txtSelectSpec.getWindowToken(), 0);
                }
                activityProfilingBinding.fragmentSelectSpeciality.setVisibility(View.VISIBLE);
            }
        });
        activityProfilingBinding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editable.toString();

                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    activityProfilingBinding.edtEmail.setError("Invalid email format");
                } else {
                    activityProfilingBinding.edtEmail.setError(null);  // Clear error if valid
                }
            }
        });
        activityProfilingBinding.edtChmemail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editable.toString();

                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    activityProfilingBinding.edtChmemail.setError("Invalid email format");
                } else {
                    activityProfilingBinding.edtChmemail.setError(null);  // Clear error if valid
                }
            }
        });
        activityProfilingBinding.btnSave.setOnClickListener(v -> {
            activityProfilingBinding.btnSave.setEnabled(false);
            JSONObject json =CommonUtilsMethods.CommonObjectParameter(this);
            try {

                SfName = SharedPref.getSfName(this);
                DivCode = SharedPref.getDivisionCode(this);
                json.put("tableName", "updateprofile");
                if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
                    SfCode = SharedPref.getHqCode(this);
                }
                else {
                    SfCode = SharedPref.getSfCode(this);
                }

                json.put("sfcode", SfCode);
                json.put("division_code", DivCode);
                json.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                json.put("DeviceID", SharedPref.getDeviceId(context));
                json.put("DrCode", Code);
                if(activityProfilingBinding.txtSelectGender.getText().toString().equalsIgnoreCase("Male")) {
                    json.put("DrGender", "M");
                }
                if(activityProfilingBinding.txtSelectGender.getText().toString().equalsIgnoreCase("Female")) {
                    json.put("DrGender", "F");
                }
                else{
                    json.put("DrGender", activityProfilingBinding.txtSelectGender.getText().toString());
                }
                json.put("DrQulCd", Qual_code);
                json.put("DrqulNm", activityProfilingBinding.txtSelectQua.getText().toString());
                json.put("DrSpcCd", spec_code);
                json.put("DrSpcNm", activityProfilingBinding.txtSelectSpec.getText().toString());
                json.put("DrCatCd", cate_code);
                json.put("DrCatNm", activityProfilingBinding.txtSelectCategory.getText().toString());
               if(CustType.equalsIgnoreCase("D")||CustType.equalsIgnoreCase("U")) {
                   json.put("DrAddr", activityProfilingBinding.edtAddr.getText().toString());
                   json.put("DrCatNm", activityProfilingBinding.txtSelectCategory.getText().toString());
                   json.put("DrPhone", activityProfilingBinding.edtPhone.getText().toString());
                   json.put("DrMob", activityProfilingBinding.edtMob.getText().toString());
                   json.put("DrEmail", activityProfilingBinding.edtEmail.getText().toString());
//                   if(!activityProfilingBinding.edtDob.getText().toString().equalsIgnoreCase(dob)) {
//                       json.put("DrDOB", activityProfilingBinding.edtDob.getText().toString()+ " 00:00:00");
//                   }
//                   else{
//                        json.put("DrDOB", "");
//                   }
//                   if(!activityProfilingBinding.edtDow.getText().toString().equalsIgnoreCase(dow)) {
//                       json.put("DrDOW", activityProfilingBinding.edtDow.getText().toString()+ " 00:00:00");
//                   }
//                   else{
//                       json.put("DrDOW", "");
//                   }
                   json.put("DrDOB", activityProfilingBinding.edtDob.getText().toString()+ " 00:00:00");
                   json.put("DrDOW", activityProfilingBinding.edtDow.getText().toString()+ " 00:00:00");
               }
               else{
                   json.put("DrAddr", activityProfilingBinding.edtChmaddr.getText().toString());
                   json.put("DrCatNm", activityProfilingBinding.txtSelectChmcat.getText().toString());
                   json.put("DrPhone", activityProfilingBinding.edtChmphone.getText().toString());
                   json.put("DrMob", activityProfilingBinding.edtChmmob.getText().toString());
                   json.put("DrEmail", activityProfilingBinding.edtChmemail.getText().toString());

                   if(!activityProfilingBinding.edtChmdob.getText().toString().equalsIgnoreCase(dob)) {
                       json.put("DrDOB", activityProfilingBinding.edtChmdob.getText().toString()+ " 00:00:00");
                   }
                   else{
                       // json.put("DrDOB", "");
                   }
                   if(!activityProfilingBinding.edtChmdow.getText().toString().equalsIgnoreCase(dow)) {
                       json.put("DrDOW", activityProfilingBinding.edtChmdow.getText().toString()+ " 00:00:00");
                   }
                   else{
                       //json.put("DrDOW", "");
                   }
               }
                json.put("key", SharedPref.getSaveLicenseSetting(context));
                json.put("DrType", CustType);
                //json.put("DrDOW", activityProfilingBinding.edtDow.getText().toString());
                Log.v("printing_add_dr", json.toString());
                activityProfilingBinding.btnSave.setEnabled(false);
                UpdateMaster(json.toString(),CustType);
            }
            catch (Exception e) {
                activityProfilingBinding.btnSave.setEnabled(true);
                e.printStackTrace();
            }
        });
    }
    public void UpdateMaster(String val,String Type) {
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
                mapString.put("axn", "save/masterdata");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, val);

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                //progressDialog.dismiss();
                                try {
                                    assert response.body() != null;
                                    JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                                    if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                        Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                        if (Type.equalsIgnoreCase("D")) {
                                            prepareArray(SfCode);
                                            //populateAdapter(doctorModelArray);
                                        }
                                        else if (Type.equalsIgnoreCase("C")){
                                            prepareArray(SfCode);
                                            //populateAdapter(chemistModelArray);
                                        }
                                        else if (Type.equalsIgnoreCase("S")) {
                                            prepareArray(SfCode);
                                            //populateAdapter(stockiestModelArray);
                                        }
                                        else if (Type.equalsIgnoreCase("U")) {
                                            prepareArray(SfCode);
                                            //populateAdapter(unlistedDrModelArray);
                                        }
//                                        SyncMaster(SfCode);
//                                        commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.updated_successfully));
                                    }
                                } catch (Exception e) {
                                }
                            }
                            else{
                                activityProfilingBinding.btnSave.setEnabled(true);
                                progressDialog.dismiss();
                                commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.something_wrong));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            activityProfilingBinding.btnSave.setEnabled(true);
                            progressDialog.dismiss();
                            commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.no_network));
                        }
                    });
                }
            }
            else {
                activityProfilingBinding.btnSave.setEnabled(true);
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.no_network));
            }

        }
        catch (Exception e) {
            activityProfilingBinding.btnSave.setEnabled(true);
            progressDialog.dismiss();
            throw new RuntimeException(e);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void populateAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels) {
        try{
            for (int i = 0; i < masterSyncItemModels.size(); i++) {
                MasterSyncItemModel item = masterSyncItemModels.get(i);
                sync(item.getMasterOf(), item.getRemoteTableName(), masterSyncItemModels, i);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void sync(String masterOf, String remoteTableName, ArrayList<MasterSyncItemModel> masterSyncItemModels, int position) {

        try {
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            JSONObject jsonObject =CommonUtilsMethods.CommonObjectParameter(this);
            jsonObject.put("tableName", remoteTableName);
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", SharedPref.getHqCode(this));
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getCallApiUrl(this));
            Map<String, String> mapString = new HashMap<>();
            Log.e("API Object", "master sync obj : " + jsonObject);
            Call<JsonElement> call = null;
            if (masterOf.equalsIgnoreCase(Constants.DOCTOR)) {
                mapString.put("axn", "table/dcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
            }
            if (call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        // masterSyncItemModels.get(position).setPBarVisibility(false);
                        Log.e("response :   ",  remoteTableName + " : " + response.body().toString());
                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject2=new JSONObject();
                        if (response.isSuccessful()) {
                            Log.e("test", "response : " + masterOf + " -- " + remoteTableName + " : " + response.body().toString());
                            try {
                                JsonElement jsonElement = response.body();
                                if (!jsonElement.isJsonNull()) {
                                    if (jsonElement.isJsonArray()) {
                                        jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                        success = true;
                                    } else if (jsonElement.isJsonObject()) {
                                        jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                        if (!jsonObject2.has("success")) {
                                            // response as jsonObject with {"success" : "fail" } will be received only when there are unformed object passed or there are no data in back end.
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                            finish();
                                        }
                                        else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1); // only update sync status and no need to overwrite previously saved data when failed
                                            masterSyncItemModels.get(position).setSyncSuccess(1);
                                        }
                                    }

                                    if (success) {
                                        progressDialog.dismiss();
                                        masterSyncItemModels.get(position).setCount(jsonArray.length());
                                        masterSyncItemModels.get(position).setSyncSuccess(2);
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString(), 2));
                                        //new Handler().postDelayed(() -> {
                                        commonUtilsMethods.showToastMessage(ProfilingActivity.this, getResources().getString(R.string.saved_successfully));
                                        MyResource_Activity.shouldRefresh = true; // Mark that refresh is needed
                                        finish();

                                        // }, 2000);
                                    }

                                    //SetupAdapter();
                                }

                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                        else{
                            progressDialog.dismiss();
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            }
        } catch (Exception e) {
            Log.v("masterCheck", "--error-" + e);
            progressDialog.dismiss();
            e.printStackTrace();
        }

    }
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
    private void handleCancel() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes=dialog.findViewById(R.id.btn_yes);
        TextView btn_no=dialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(view12 -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        btn_no.setOnClickListener(view12 -> {
            dialog.dismiss();
        });
    }
    private void showDatePickerDialogforDOB() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date in the calendar
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date as "1970-01-01 00:00:00"
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = formatter.format(calendar.getTime());

                    // Set the formatted date to the TextView
                    activityProfilingBinding.edtDob.setText(formattedDate);
                },
                year, month, day
        );

        // Restrict future date selection
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private void showDatePickerDialogforDOW() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date in the calendar
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date as "1970-01-01 00:00:00"
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = formatter.format(calendar.getTime());

                    // Set the formatted date to the TextView
                    activityProfilingBinding.edtDow.setText(formattedDate);
                },
                year, month, day
        );

        // Restrict future date selection
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) ProfilingActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public void prepareArray(String hqCode) {
        if (CustType.equalsIgnoreCase("D")) {
            doctorModelArray.clear();
            MasterSyncItemModel doctorModel = new MasterSyncItemModel(SharedPref.getDrCap(this), Constants.DOCTOR, "getdoctors", Constants.DOCTOR + hqCode, chemistStatus, false);
            // MasterSyncItemModel spl = new MasterSyncItemModel(Constants.SPECIALITY, Constants.DOCTOR, "getspeciality", Constants.SPECIALITY, chemistStatus, false);
            // MasterSyncItemModel ql = new MasterSyncItemModel(Constants.QUALIFICATION, Constants.DOCTOR, "getquali", Constants.QUALIFICATION, chemistStatus, false);
            // MasterSyncItemModel cat = new MasterSyncItemModel(Constants.CATEGORY, Constants.DOCTOR, "getcategorys", Constants.CATEGORY, categoryStatus, false);
            // MasterSyncItemModel clas = new MasterSyncItemModel(Constants.CLASS, Constants.DOCTOR, "getclass", Constants.CLASS, chemistStatus, false);
            doctorModelArray.add(doctorModel);
            arrayForAdapter.clear();
            arrayForAdapter.addAll(doctorModelArray);
            populateAdapter(arrayForAdapter);
        }
        else if (CustType.equalsIgnoreCase("C")) {
            chemistStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST + hqCode);
            categoryStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CATEGORY);
            MasterSyncItemModel cheModel = new MasterSyncItemModel(SharedPref.getChmCap(this),  Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode, chemistStatus, false);
            MasterSyncItemModel chemistCategory = new MasterSyncItemModel(Constants.CATEGORY,  Constants.DOCTOR, "getchem_categorys", Constants.CATEGORY_CHEMIST, categoryStatus, false);
            chemistModelArray.add(cheModel);
            chemistModelArray.add(chemistCategory);
            arrayForAdapter.clear();
            arrayForAdapter.addAll(chemistModelArray);
            populateAdapter(arrayForAdapter);
        }
        else if (CustType.equalsIgnoreCase("S")) {
            stockiestModelArray.clear();
            MasterSyncItemModel stockModel = new MasterSyncItemModel(SharedPref.getStkCap(this), Constants.DOCTOR, "getstockist", Constants.STOCKIEST + hqCode, chemistStatus, false);
            stockiestModelArray.add(stockModel);
            arrayForAdapter.clear();
            arrayForAdapter.addAll(stockiestModelArray);
            populateAdapter(arrayForAdapter);
        }
        else if (CustType.equalsIgnoreCase("U")) {
            unlistedDrModelArray.clear();
            MasterSyncItemModel unListModel = new MasterSyncItemModel(SharedPref.getUNLcap(this), Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, chemistStatus, false);
            unlistedDrModelArray.add(unListModel);
            arrayForAdapter.clear();
            arrayForAdapter.addAll(unlistedDrModelArray);
            populateAdapter(arrayForAdapter);
        }
    }
    private void showDatePickerDialogforChmDOB() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date in the calendar
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date as "1970-01-01 00:00:00"
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = formatter.format(calendar.getTime());

                    // Set the formatted date to the TextView
                    activityProfilingBinding.edtChmdob.setText(formattedDate);
                },
                year, month, day
        );

        // Restrict future date selection
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private void showDatePickerDialogforChmDOW() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date in the calendar
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date as "1970-01-01 00:00:00"
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = formatter.format(calendar.getTime());

                    // Set the formatted date to the TextView
                    activityProfilingBinding.edtChmdow.setText(formattedDate);
                },
                year, month, day
        );

        // Restrict future date selection
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private String getChemistCategory(String chmCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                String code = jsonObject.getString("Code");
                if(code.equalsIgnoreCase(chmCode))
                    return name;
            }
        } catch (Exception e) {
            Log.e("Chemist Call", "getChemistCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

}