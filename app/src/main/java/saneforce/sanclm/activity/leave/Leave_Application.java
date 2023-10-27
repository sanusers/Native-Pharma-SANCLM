package saneforce.sanclm.activity.leave;


import static saneforce.sanclm.commonClasses.UtilityClass.hideKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.ActivityLoginBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.TimeUtils;

public class Leave_Application extends AppCompatActivity {
    public static TextView Fromdate, Todate, l_days, Leave_Type, balance_days;
    TextView Head_Ltype, Head_fromdate, Head_todate, c_val, c_val1, c_val2, c_val_tol, c_val_tol_1, c_val_tol_2, txt_Attachement, headtext_id;
    EditText ed_Address, ed_Reason, et_Custsearch;

    ApiInterface apiInterface;
    ActivityLoginBinding binding;
    LinearLayout back_btn;
    LoginResponse loginResponse;

    ImageView close_sideview;
    SQLite sqLite;
    String navigateFrom = "";
    public static RecyclerView leave_details;
    ArrayAdapter<String> adapter;
    public static ArrayList<Leave_modelclass> List_LeaveDates = new ArrayList<>();
    public static ArrayList<Leave_modelclass> Chart_list = new ArrayList<>();
    public static ArrayList<Leave_modelclass> pchart_list = new ArrayList<>();
    ArrayList<String> leave_type = new ArrayList<>();
    public static ArrayList<String> ltypecount = new ArrayList<>();
    ArrayList<String> leave_typeid = new ArrayList<>();
    ArrayList<String> leave_typename = new ArrayList<>();
    ArrayList<String> listdate = new ArrayList<>();
    ArrayList<String> balance_count = new ArrayList<>();
    String Ltype_id, L_typename, L_count = "", Lshortname, avilable, leavety;
    int totalval = 0, val = 0;
    public static DrawerLayout l_sideview;
    public static ListView dailog_list;
    RecyclerView RL_piechart;
    Button submit_leave;
    ApiInterface apiService;
    String l_address = "", l_reason = "";
    Piechart_adapter piechart_adapter;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_leave_application);


        Fromdate = findViewById(R.id.et_from_date);
        Todate = findViewById(R.id.et_to_date);
        Head_Ltype = findViewById(R.id.Head_Ltype);
        Head_todate = findViewById(R.id.Head_todate);
        Head_fromdate = findViewById(R.id.Head_fromdate);
        back_btn = findViewById(R.id.backArrow);
        leave_details = findViewById(R.id.leave_details);
        l_days = findViewById(R.id.l_days);
        txt_Attachement = findViewById(R.id.txt_Attachement);
        Leave_Type = findViewById(R.id.Leave_Type);
        ed_Reason = findViewById(R.id.ed_Reason);
        ed_Address = findViewById(R.id.ed_Address);
        l_sideview = findViewById(R.id.leave_side);
        headtext_id = findViewById(R.id.headtext_id);
        dailog_list = findViewById(R.id.cutumdailog_list);
        close_sideview = findViewById(R.id.close_sideview);
        et_Custsearch = findViewById(R.id.et_Custsearch);
        balance_days = findViewById(R.id.balance_days);
        RL_piechart = findViewById(R.id.RL_piechart);
        submit_leave = findViewById(R.id.submit_leave);

        dailog_list.setVisibility(View.VISIBLE);
        l_sideview.closeDrawer(Gravity.END);

        sqLite = new SQLite(this);
        back_btn.setOnClickListener(v -> {
            Intent l = new Intent(Leave_Application.this, HomeDashBoard.class);
            startActivity(l);
        });
//L_count

        String colorText = "<font color=\"#85929e\">" + "Leave Date From" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";
        String colorText1 = "<font color=\"#85929e\">" + "Leave Date To" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";
        String colorText2 = "<font color=\"#85929e\">" + "Leave Type" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";

        Head_fromdate.setText(Html.fromHtml(colorText));
        Head_todate.setText(Html.fromHtml(colorText1));
        Head_Ltype.setText(Html.fromHtml(colorText2));

        close_sideview.setOnClickListener(v -> {
            l_sideview.closeDrawer(Gravity.END);
            hideKeyboard(Leave_Application.this);
        });
        leave_applydates();

        headtext_id.setText("Leave Type");
        Fromdate.setOnClickListener(v -> {
            Intent tp = new Intent(Leave_Application.this, CalendarActivity.class);
            tp.putExtra("selectefromdDate", "1");
            startActivity(tp);
        });


        Todate.setOnClickListener(v -> {
            if (!Fromdate.getText().toString().equals("")) {
                Intent tp = new Intent(Leave_Application.this, CalendarActivity.class);
                tp.putExtra("selectefromdDate", "2");
                startActivity(tp);
            } else {
                Toast.makeText(Leave_Application.this, "Select From Date", Toast.LENGTH_SHORT).show();
            }
        });


        Leave_Type.setOnClickListener(v -> {
            if (Fromdate.getText().toString().equals("")) {
                Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show();
            } else if (Todate.getText().toString().equals("")) {
                Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show();
            } else {
                showalert_leavetype();
            }
        });

        submit_leave.setOnClickListener(v -> {
            if (Fromdate.getText().toString().equals("")) {
                Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show();
            } else if (Todate.getText().toString().equals("")) {
                Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show();
            } else if (Leave_Type.getText().toString().equals("")) {
                Toast.makeText(this, "Select Leave Type", Toast.LENGTH_SHORT).show();
            } else {
                Submit();

            }

        });


        AvailableLeave();



    }

    public void leave_applydates(){
        ltypecount.clear();
        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.LEAVE);
            String days="";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dates = jsonObject.getString("Created_Date");
                    JSONObject jsonval=new JSONObject(dates);
                    if(days.equals("")){
                        days = (jsonval.getString("date"));
                        String dval= TimeUtils.GetConvertedDate(TimeUtils.FORMAT_22, TimeUtils.FORMAT_4, days);
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
        l_sideview.setVisibility(View.VISIBLE);
        l_sideview.openDrawer(Gravity.END);

        leave_type.clear();
        leave_typeid.clear();
        leave_typename.clear();

        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.LEAVE);
            Log.d("L-type", String.valueOf(jsonArray));
            String days="";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String Leavename = (jsonObject.getString("Leave_Name"));//Leave_Name
                    String L_code = (jsonObject.getString("Leave_code"));
                    String L_Sname = (jsonObject.getString("Leave_SName"));


                    String dates = jsonObject.getString("Created_Date");

                    JSONObject jsonval=new JSONObject(dates);
                    if(days.equals("")){
                        days = (jsonval.getString("date"));

                        String dval= TimeUtils.GetConvertedDate(TimeUtils.FORMAT_22, TimeUtils.FORMAT_4, days);

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
                String selectedFromList = dailog_list.getItemAtPosition(position).toString();
                for (int i = 0; i < leave_typename.size(); i++) {
                    if (selectedFromList.equals(leave_typename.get(i))) {
                        Leave_Type.setText(selectedFromList);
                        Ltype_id = leave_typeid.get(i);
                        L_typename = leave_typename.get(i);
                        Lshortname = leave_type.get(i);


                        try {
                            JSONArray jsonArray1 = sqLite.getMasterSyncDataByKey(Constants.LEAVE_STATUS);
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
                l_sideview.closeDrawer(Gravity.END);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leave_avalabledetails() {
        try {
            String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
            String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
            String replacedUrl = pathUrl.replaceAll("\\?.*", "/");

            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl + replacedUrl);

            sqLite = new SQLite(getApplicationContext());
            sqLite.getWritableDatabase();

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                navigateFrom = getIntent().getExtras().getString("Origin");
            }

            Cursor cursor = sqLite.getLoginData();
            loginResponse = new LoginResponse();
            String loginData = "";
            if (cursor.moveToNext()) {
                loginData = cursor.getString(0);
            }
            cursor.close();
            Type type = new TypeToken<LoginResponse>() {
            }.getType();
            loginResponse = new Gson().fromJson(loginData, type);


            String f_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, Fromdate.getText().toString()));
            String t_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, Todate.getText().toString()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", "getlvlvalid");
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("Fdt", f_date);
            jsonObject.put("Tdt", t_date);
            jsonObject.put("LTy", Lshortname);
            jsonObject.put("division_code", loginResponse.getDivision_Code());
            jsonObject.put("Rsf", SharedPref.getSfCode(this));
            jsonObject.put("sf_type", SharedPref.getSfType(this));
            jsonObject.put("Designation", loginResponse.getDesig());
            jsonObject.put("state_code", loginResponse.getState_Code());
            jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
            Log.d("JSonobj", String.valueOf(jsonObject));

            Call<JsonArray> call = null;
            call = apiInterface.getLeavesetdata(jsonObject.toString());
            if (call != null) {
                call.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {

                        if (response.isSuccessful()) {
                            Log.e("test", "response : " + " : " + response.body().toString());
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
                                        Fromdate.setText("");
                                        Todate.setText("");
                                        Leave_Type.setText("");

                                        Toast.makeText(Leave_Application.this, msg, Toast.LENGTH_SHORT).show();

                                    }


                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                        List_LeaveDates.clear();
                        Fromdate.setText("");
                        Todate.setText("");
                        Leave_Type.setText("");
                        balance_days.setText("");
                        List_LeaveDates.clear();

                        Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
                        LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
                        Leave_Application.leave_details.setLayoutManager(LayoutManagerpoc);
                        Leave_Application.leave_details.setItemAnimator(new DefaultItemAnimator());
                        Leave_Application.leave_details.setAdapter(l_details);
                        l_details.notifyDataSetChanged();

                        Toast.makeText(Leave_Application.this, "case failure", Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Leavedetails() {
        listdate.clear();
        List_LeaveDates.clear();
        DateFormat mFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        try {
            String F_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_19, Leave_Application.Fromdate.getText().toString()));
            String T_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_19, Leave_Application.Todate.getText().toString()));

            Date fromDate = mFormat.parse(F_date);
            Date toDate = mFormat.parse(T_date);
            List<Date> datesInRange = getDatesInRange(fromDate, toDate);
            for (Date date : datesInRange) {
                listdate.add((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_20, TimeUtils.FORMAT_12, String.valueOf(date))));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Leave_Application.l_days.setText(listdate.size() + " days " + L_typename);
        L_count = String.valueOf(listdate.size());
        totalval = Integer.parseInt((avilable));
        val = Integer.parseInt(L_count);
        Log.d("rem", totalval + "---" + val);
        int bal = totalval - val;

        if (bal == 0 || bal == (-bal)) {

        } else {
            if (leavety.equals("LOP")) {
                balance_days.setText("");
            } else {
                String balval = String.valueOf(bal);
                balance_days.setText(balval + " " + "days remaining");
            }
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
                Leave_Application.leave_details.setLayoutManager(LayoutManagerpoc);
                Leave_Application.leave_details.setItemAnimator(new DefaultItemAnimator());
                Leave_Application.leave_details.setAdapter(l_details);
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
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.LEAVE_STATUS);
            JSONArray jsonArray1 = sqLite.getMasterSyncDataByKey(Constants.LEAVE);
            Log.d("Leave_data", jsonArray + "--" + jsonArray1);

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


                        Collections.reverse(Chart_list);
                        Piechart_adapter chart_details = new Piechart_adapter(Leave_Application.this, Chart_list);

                        RL_piechart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        RL_piechart.setItemAnimator(new DefaultItemAnimator());
                        RL_piechart.setAdapter(chart_details);
                        chart_details.notifyDataSetChanged();

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
            sqLite = new SQLite(getApplicationContext());
            sqLite.getWritableDatabase();
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                navigateFrom = getIntent().getExtras().getString("Origin");
            }
            Cursor cursor = sqLite.getLoginData();
            loginResponse = new LoginResponse();
            String loginData = "";
            if (cursor.moveToNext()) {
                loginData = cursor.getString(0);
            }
            cursor.close();
            Type type = new TypeToken<LoginResponse>() {
            }.getType();
            loginResponse = new Gson().fromJson(loginData, type);
            String f_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, Fromdate.getText().toString()));
            String t_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, Todate.getText().toString()));

            String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            l_address = ed_Address.getText().toString();
            l_reason = ed_Reason.getText().toString();

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
                jsonobj.put("division_code", loginResponse.getDivision_Code());
                jsonobj.put("Rsf", SharedPref.getSfCode(this));
                jsonobj.put("sf_type", SharedPref.getSfType(this));
                jsonobj.put("Designation", loginResponse.getDesig());
                jsonobj.put("state_code", loginResponse.getState_Code());
                jsonobj.put("subdivision_code", loginResponse.getSubdivision_code());
                jsonobj.put("sf_emp_id", loginResponse.getSf_emp_id());
                jsonobj.put("leave_typ_code", Ltype_id);


                Log.d("save_obj", String.valueOf(jsonobj));

                Call<JsonArray> call = null;
                call = apiInterface.getleavesave(jsonobj.toString());

                if (call != null) {
                    call.enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {

                            if (response.isSuccessful()) {
                                Log.e("test", "response : " + " : " + response.body().toString());

                                Toast.makeText(Leave_Application.this, "Leave submit successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                            Toast.makeText(Leave_Application.this, "case failure1", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Fromdate.setText("");
            Todate.setText("");
            Leave_Type.setText("");
            ed_Address.getText().clear();
            ed_Reason.getText().clear();
            balance_days.setText("");
            List_LeaveDates.clear();

            Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
            LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
            Leave_Application.leave_details.setLayoutManager(LayoutManagerpoc);
            Leave_Application.leave_details.setItemAnimator(new DefaultItemAnimator());
            Leave_Application.leave_details.setAdapter(l_details);
            l_details.notifyDataSetChanged();


            Toast.makeText(this, "please check Internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Leave_Application.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}