package saneforce.sanzen.activity.leave;


import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanzen.commonClasses.UtilityClass.hideKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityLeaveApplicationBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;

import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class Leave_Application extends AppCompatActivity {
    TextView  headtext_id;
    EditText et_Custsearch;

    ApiInterface apiInterface;
    ImageView close_sideview;

    String navigateFrom = "";

    ArrayAdapter<String> adapter;
    public static ArrayList<Leave_modelclass> List_LeaveDates = new ArrayList<>();
    public static ArrayList<Leave_modelclass> Chart_list = new ArrayList<>();

    ArrayList<String> leave_type = new ArrayList<>();
    public static ArrayList<String> ltypecount = new ArrayList<>();
    ArrayList<String> leave_typeid = new ArrayList<>();
    ArrayList<String> leave_typename = new ArrayList<>();
    ArrayList<String> listdate = new ArrayList<>();

    String Ltype_id, L_typename, L_count = "", Lshortname, avilable, leavety;
    int totalval = 0, val = 0;

    public static ListView dailog_list;
    String l_address = "", l_reason = "",imageName = "",leaveAvailable;

    CommonUtilsMethods commonUtilsMethods;
   public static ActivityLeaveApplicationBinding leavebinding;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private String destinationFilePath;
    boolean isLeaveEntitlementRequested;


    //To Hide the bottomNavigation When popup

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            leavebinding.chartLayout.setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }



    @SuppressLint("WrongConstant")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leavebinding = ActivityLeaveApplicationBinding.inflate(getLayoutInflater());
        setContentView(leavebinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        isLeaveEntitlementRequested = SharedPref.getLeaveEntitlementNeed(this).equals("0");
        dailog_list = findViewById(R.id.cutumdailog_list);
        close_sideview = findViewById(R.id.close_sideview);
        headtext_id = findViewById(R.id.headtext_id);
        et_Custsearch = findViewById(R.id.et_Custsearch);
        dailog_list.setVisibility(View.VISIBLE);
        setVisibility();
        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray();
        for (int bean = 0;bean<jsonArray.length();bean++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(bean);
                System.out.println("jsonObject--->"+jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
//        updateLeaveStatusMasterSync1();
        leavebinding.edReason.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(leavebinding.edReason)});

//        l_sideview.closeDrawer(Gravity.RIGHT);
        leavebinding.leavebackArrow.setOnClickListener(v -> {
            //  onBackPressed();
            getOnBackPressedDispatcher().onBackPressed();
          /*  Intent l = new Intent(Leave_Application.this, HomeDashBoard.class);
            startActivity(l);*/
        });

        String colorText = "<font color=\"#85929e\">" + "Leave Date From" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";
        String colorText1 = "<font color=\"#85929e\">" + "Leave Date To" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";
        String colorText2 = "<font color=\"#85929e\">" + "Leave Type" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";

        leavebinding.HeadFromdate.setText(Html.fromHtml(colorText));
        leavebinding.HeadTodate.setText(Html.fromHtml(colorText1));
        leavebinding.HeadLtype.setText(Html.fromHtml(colorText2));

        close_sideview.setOnClickListener(v -> {
            leavebinding.leaveSide.closeDrawer(Gravity.END);
            hideKeyboard(Leave_Application.this);
        });
        leave_applydates();


        leavebinding.LeaveType.setText("Leave Type");
        leavebinding.etFromDate.setOnClickListener(v -> {
            Intent tp = new Intent(Leave_Application.this, CalendarActivity.class);
            tp.putExtra("selectefromdDate", "1");
            startActivity(tp);
        });


        leavebinding.etToDate.setOnClickListener(v -> {
            if (!leavebinding.etFromDate.getText().toString().equals("")) {
                Intent tp = new Intent(Leave_Application.this, CalendarActivity.class);
                tp.putExtra("selectefromdDate", "2");
                startActivity(tp);
            } else {
                commonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_from_date));
            }
        });


        leavebinding.LeaveType.setOnClickListener(v -> {
            if (leavebinding.etFromDate.getText().toString().equals("")) {
                Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show();
            } else if (leavebinding.etToDate.getText().toString().equals("")) {
                Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show();
            } else {
                if (UtilityClass.isNetworkAvailable(this)){
                    showalert_leavetype();
                }
                else{
                    commonUtilsMethods.showToastMessage(this,"Please Check Your Internet Connection");
                }
            }
        });

        leavebinding.submitLeave.setOnClickListener(v -> {
            if (leavebinding.etFromDate.getText().toString().equals("")) {
                Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show();
            } else if (leavebinding.etToDate.getText().toString().equals("")) {
                Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show();
            } else if ( leavebinding.LeaveType.getText().toString().equals("")) {
                Toast.makeText(this, "Select Leave Type", Toast.LENGTH_SHORT).show();
            } else {
                Submit();

            }

        });

        AvailableLeave();

    }

    public void leave_applydates() {
        ltypecount.clear();
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE).getMasterSyncDataJsonArray();
            String days = "";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dates = jsonObject.getString("Created_Date");
                    JSONObject jsonval = new JSONObject(dates);
                    if (days.equals("")) {
                        days = (jsonval.getString("date"));
                        String dval = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_22, TimeUtils.FORMAT_4, days);
                        ltypecount.add(dval);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("WrongConstant")
    public void showalert_leavetype() {
        et_Custsearch.getText().clear();
        leavebinding.leaveSide.setVisibility(View.VISIBLE);
        leavebinding.leaveSide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        leavebinding.leaveSide.openDrawer(Gravity.END);

        leave_type.clear();
        leave_typeid.clear();
        leave_typename.clear();

        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE).getMasterSyncDataJsonArray();
            Log.d("L-type", String.valueOf(jsonArray));
            String days = "";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String Leavename = (jsonObject.getString("Leave_Name"));//Leave_Name
                    String L_code = (jsonObject.getString("Leave_code"));
                    String L_Sname = (jsonObject.getString("Leave_SName"));


                    String dates = jsonObject.getString("Created_Date");

                    JSONObject jsonval = new JSONObject(dates);
                    if (days.equals("")) {
                        days = (jsonval.getString("date"));

                        String dval = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_22, TimeUtils.FORMAT_4, days);

                        ltypecount.add(dval);
                    }
                    leave_type.add(L_Sname);
                    leave_typeid.add(L_code);
                    leave_typename.add(Leavename);
                }
            }

            adapter = new ArrayAdapter<>(Leave_Application.this, R.layout.cutoum_layout, (List) leave_typename);
            dailog_list.setAdapter(adapter);

            et_Custsearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            dailog_list.setOnItemClickListener((arg0, arg1, position, arg3) -> {
                // TODO Auto-generated method stub
                if (UtilityClass.isNetworkAvailable(this)) {
                    String selectedFromList = dailog_list.getItemAtPosition(position).toString();
                    for (int i = 0; i < leave_typename.size(); i++) {
                        if (selectedFromList.equals(leave_typename.get(i))) {
                            leavebinding.LeaveType.setText(selectedFromList);
                            Ltype_id = leave_typeid.get(i);
                            L_typename = leave_typename.get(i);
                            Lshortname = leave_type.get(i);
                            try {
                                JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray();
                                for (int d = 0; d < jsonArray1.length(); d++) {
                                    JSONObject jsonobj1 = jsonArray1.getJSONObject(d);
                                    if (Ltype_id.equals(jsonobj1.getString("Leave_code"))) {
                                        avilable = (jsonobj1.getString("Avail"));
                                        leavety = (jsonobj1.getString("Leave_Type_Code"));

                                    }
                                }
                                Log.e("dates12", String.valueOf(ltypecount));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            leave_avalabledetails();

                        }
                    }
                    leavebinding.leaveSide.closeDrawer(Gravity.END);
                }else{
                    commonUtilsMethods.showToastMessage(this,"Please Check Your Internet Connection");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leave_avalabledetails() {
        try {
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                navigateFrom = getIntent().getExtras().getString("Origin");
            }



            String f_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, leavebinding.etFromDate.getText().toString()));
            String t_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, leavebinding.etToDate.getText().toString()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "getlvlvalid");
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("Fdt", f_date);
            jsonObject.put("Tdt", t_date);
            jsonObject.put("LTy", Lshortname);
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", SharedPref.getHqCode(this));
            jsonObject.put("sf_type", SharedPref.getSfType(this));
            jsonObject.put("Designation", SharedPref.getDesig(this));
            jsonObject.put("state_code",  SharedPref.getStateCode(this));
            jsonObject.put("subdivision_code",  SharedPref.getSubdivisionCode(this));
            jsonObject.put("versionNo",  getString(R.string.app_version));
            jsonObject.put("mod", Constants.APP_MODE);
            jsonObject.put("Device_version", Build.VERSION.RELEASE);
            jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonObject.put("AppName", getString(R.string.str_app_name));
            jsonObject.put("language", SharedPref.getSelectedLanguage(this));
            Log.d("JSonobj", String.valueOf(jsonObject));

            Map<String, String> qry = new HashMap<>();
            qry.put("axn", "get/leave");
            Call<JsonElement> call = null;
            call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()),qry, jsonObject.toString());
            if (call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                        if (response.isSuccessful()) {
                            Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                            try {
                                JSONArray jsonArray = new JSONArray(response.body().toString());
                                Log.d("responce1", String.valueOf(jsonArray));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String Leavename = (jsonObject.getString("Flg"));//Leave_Name
                                    String msg = (jsonObject.getString("Msg"));
                                    if ((jsonObject.getString("Msg").equals(""))) {
                                        Leavedetails();
                                    } else {
                                        List_LeaveDates.clear();
                                        leavebinding.etFromDate.setText("");
                                        leavebinding.etToDate.setText("");
                                        leavebinding.LeaveType.setText("");

                                        Toast.makeText(Leave_Application.this, msg, Toast.LENGTH_SHORT).show();

                                    }


                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        List_LeaveDates.clear();
                        leavebinding.etFromDate.setText("");
                        leavebinding.etToDate.setText("");
                        leavebinding.LeaveType.setText("");
                        leavebinding.balanceDays.setText("");
                        List_LeaveDates.clear();

                        Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
                        LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
                        Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
                        Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
                        Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
                        l_details.notifyDataSetChanged();

                        Toast.makeText(Leave_Application.this, "Poor Connection Please Check After Sometime", Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void Leavedetails() {
        listdate.clear();
        List_LeaveDates.clear();
        DateFormat mFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        try {
            String F_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_19, leavebinding.etFromDate.getText().toString()));
            String T_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_19, leavebinding.etToDate.getText().toString()));

            Date fromDate = mFormat.parse(F_date);
            Date toDate = mFormat.parse(T_date);
            List<Date> datesInRange = getDatesInRange(fromDate, toDate);
            for (Date date : datesInRange) {
                listdate.add((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_20, TimeUtils.FORMAT_12, String.valueOf(date))));
            }

            Leave_Application.leavebinding.lDays.setText(listdate.size() + " days " + L_typename);
            L_count = String.valueOf(listdate.size());
            System.out.println("totalValue-->"+avilable);
            if (isLeaveEntitlementRequested){
                totalval = Integer.parseInt(avilable);
                val = Integer.parseInt(L_count);
                Log.d("rem", totalval + "---" + val);
                int bal = totalval - val;

                if(bal == 0 || bal == (-bal)) {

                }else {
                    if(leavety.equals("LOP")) {
                        leavebinding.balanceDays.setText("");
                    }else {
                        String balval = String.valueOf(bal);
                        leavebinding.balanceDays.setText(balval + " " + "days remaining");
                    }
                }
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        try {
            JSONArray jsonarr = new JSONArray();
            for (int s = 0; s < listdate.size(); s++) {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("Date", listdate.get(s));//cip_head
                jsonarr.put(jsonobj);
            }
            if (jsonarr.length() > 0) {
                for (int j = 0; j < jsonarr.length(); j++) {
                    JSONObject jsonObject = jsonarr.getJSONObject(j);
                    String listed_date = jsonObject.getString("Date");
                    Leave_modelclass model = new Leave_modelclass(listed_date, "5");//,ref_source_id
                    List_LeaveDates.add(model);
                }
                Leavedetails_adapter l_details = new Leavedetails_adapter(this, List_LeaveDates);
                LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
                Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
                Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
                Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
                l_details.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Date> getDatesInRange(Date fromDate, Date toDate) {
        List<Date> dateList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        while (calendar.getTime().before(toDate) || calendar.getTime().equals(toDate)) {
            dateList.add(calendar.getTime());

            // Increment the date by one day
            calendar.add(Calendar.DATE, 1);
        }

        return dateList;
    }

    public void AvailableLeave() {
        try {
            Chart_list.clear();
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray();
            JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE).getMasterSyncDataJsonArray();
            String lstatus = (jsonArray.get(0).toString());
            if (lstatus.equals(Constants.NO_DATA_AVAILABLE)) {
                leavebinding.chartLayout.setVisibility(View.GONE);
                leavebinding.mtcard.setVisibility(View.VISIBLE);
            } else {
//                Log.d("Leave_data", jsonArray + "--" + jsonArray1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    for (int i1 = 0; i1 < jsonArray1.length(); i1++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i1);
                        String Ltype = (jsonObject1.getString("Leave_Name"));
                        if (jsonObject.getString("Leave_code").equals(jsonObject1.getString("Leave_code"))) {
                            Leave_modelclass tackleave = new Leave_modelclass(jsonObject1.getString("Leave_Name"), (jsonObject.getString("Elig")),
                                    (jsonObject.getString("Taken")), (jsonObject.getString("Avail")),
                                    jsonObject.getString("Leave_Type_Code"));

//                            leave_modelclass tackleave=new leave_modelclass(jsonObject1.getString("Leave_Name"),"6","5","10");
                            Chart_list.add(tackleave);


//                        Collections.reverse(Chart_list);
                            Piechart_adapter chart_details = new Piechart_adapter(Leave_Application.this, Chart_list);
                            leavebinding.RLPiechart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                            leavebinding.RLPiechart.setItemAnimator(new DefaultItemAnimator());
                            leavebinding.RLPiechart.setAdapter(chart_details);
                            chart_details.notifyDataSetChanged();

                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Submit() {
        if (isNetworkConnected()) {
            String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
            String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
            String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
            Log.e("test", "login url : " + baseUrl + replacedUrl);
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl + replacedUrl);
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                navigateFrom = getIntent().getExtras().getString("Origin");
            }


            String f_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, leavebinding.etFromDate.getText().toString()));
            String t_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, leavebinding.etToDate.getText().toString()));

            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            l_address = leavebinding.edAddress.getText().toString();
            l_reason = leavebinding.edReason.getText().toString();

//   {
//
//    "tableName":"saveleave",
//    "sfcode":"MR0026",
//    "FDate":"2023-7-6",
//    "TDate":"2023-7-6",
//    "LeaveType":"CL",
//    "NOD":"1",
//    "LvOnAdd":"",
//    "LvRem":"test",
//    "division_code":"8,",
//    "Rsf":"MR0026",
//    "sf_type":"1",
//    "Designation":"TBM",
//    "state_code":"28",
//    "subdivision_code":"62,",
//    "sf_emp_id":"give emp id here",
//    "leave_typ_code":"give leavetype code here"
//
//}

            try {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("tableName", "saveleave");
                jsonobj.put("sfcode", SharedPref.getSfCode(this));
                jsonobj.put("FDate", f_date);
                jsonobj.put("TDate", t_date);
                jsonobj.put("LeaveType", Lshortname);
                jsonobj.put("NOD", L_count);
                jsonobj.put("LvOnAdd", l_address);
                jsonobj.put("LvRem", l_reason);
                jsonobj.put("division_code",  SharedPref.getDivisionCode(this));
                jsonobj.put("Rsf", SharedPref.getHqCode(this));
                jsonobj.put("sf_type", SharedPref.getSfType(this));
                jsonobj.put("Designation",  SharedPref.getDesig(this));
                jsonobj.put("state_code",  SharedPref.getStateCode(this));
                jsonobj.put("subdivision_code",  SharedPref.getSubdivisionCode(this));
                jsonobj.put("sf_emp_id", SharedPref.getSfEmpId(this));
                jsonobj.put("leave_typ_code", Ltype_id);
                jsonobj.put("versionNo",  getString(R.string.app_version));
                jsonobj.put("mod", Constants.APP_MODE);
                jsonobj.put("Device_version", Build.VERSION.RELEASE);
                jsonobj.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
                jsonobj.put("AppName", getString(R.string.str_app_name));
                jsonobj.put("language", SharedPref.getSelectedLanguage(this));
                Log.d("save_obj", String.valueOf(jsonobj));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "save/leavemodule");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonobj.toString());

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                commonUtilsMethods.showToastMessage(Leave_Application.this,"Leave Submitted Successfully");
                                if (isLeaveEntitlementRequested){
                                    updateLeaveStatusMasterSync();
                                }
                                finish();

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Toast.makeText(Leave_Application.this, "Poor Internet Connection Please Check After Sometime", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            leavebinding.etFromDate.setText("");
            leavebinding.etToDate.setText("");
            leavebinding.LeaveType.setText("");
            leavebinding.edAddress.getText().clear();
            leavebinding.edReason.getText().clear();
            leavebinding.balanceDays.setText("");
            List_LeaveDates.clear();

            Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
            LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
            Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
            Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
            Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
            l_details.notifyDataSetChanged();


            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Leave_Application.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    private void setVisibility(){
        if (isLeaveEntitlementRequested){
            leavebinding.chartLayout.setVisibility(View.VISIBLE);
        }
        else{
            leavebinding.chartLayout.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        timeZoneVerification();
    }

    private void timeZoneVerification() {
        boolean isAutoTimeZoneEnabled = commonUtilsMethods.isAutoTimeEnabled(context) && commonUtilsMethods.isTimeZoneAutomatic(context);
        if (!isAutoTimeZoneEnabled) {
            CommonUtilsMethods.showCustomDialog(this);
        }
    }
    private void updateLeaveStatusMasterSync() {
        JSONObject leaveStatusObject = new JSONObject();
        try {
            leaveStatusObject.put("tableName", "getleavestatus");
            leaveStatusObject.put("sfcode", SharedPref.getSfCode(this));
            leaveStatusObject.put("division_code", SharedPref.getDivisionCode(this));
            leaveStatusObject.put("Rsf", SharedPref.getHqCode(this));
            leaveStatusObject.put("sf_type", SharedPref.getSfType(this));
            leaveStatusObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            leaveStatusObject.put("Designation", SharedPref.getDesig(this));
            leaveStatusObject.put("state_code", SharedPref.getStateCode(this));
            leaveStatusObject.put("subdivision_code", SharedPref.getSubdivisionCode(this));
            System.out.println("leaveStatusObject--->"+leaveStatusObject);
        } catch (Exception ignored) {

        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/leave");
        Call<JsonElement> leaveStatus = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, leaveStatusObject.toString());

        leaveStatus.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonElement jsonElement = response.body();
                        JSONArray jsonArray = new JSONArray();
                        assert jsonElement != null;
                        if (!jsonElement.isJsonNull()) {
                            if (jsonElement.isJsonArray()) {
                                JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                jsonArray = new JSONArray(jsonArray1.toString());
                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.LEAVE_STATUS, jsonArray.toString(), 0));
                            } else if (jsonElement.isJsonObject()) {
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                if (!jsonObject1.has("success")) {
                                    jsonArray.put(jsonObject1);
                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.LEAVE_STATUS, jsonArray.toString(), 0));
                                }
                            }
                        }
                } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                Toast.makeText(Leave_Application.this, "Poor Internet Connection Please Check After Sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
