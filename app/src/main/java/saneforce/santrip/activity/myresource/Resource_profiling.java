package saneforce.santrip.activity.myresource;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.remaindercalls.cuslistadapter;
import saneforce.santrip.activity.remaindercalls.remainder_modelclass;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;

public class Resource_profiling extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    LinearLayout backArrow;
    public static ImageView close_sideview;
    private static EditText email1;
    public static EditText et_Custsearch;
    public static TextView headtext_id;
    CheckBox check1, check2;
    LinearLayout doc_qual, map_layout;
    ListView listView;
    ApiInterface api_interface;
    ProgressDialog progressDialog = null;
    public static cuslistadapter cuslistadap;
    public static RecyclerView app_recycler_view;
    public static DrawerLayout drawer_Layout12;
    public static TextView Qualification, Speciality, Category;
    public static String Qual_code = "", spec_code = "", cate_code = "";
    TextView Dcrname, submit_profil, clear_all;
    RelativeLayout tag_clk;
    public static String profil_dates = "";
    public static TextView dob_date, dow_date, Dr_addrs, Dr_addrs1, Dr_addrs2;
    String dob_dt, dow_dt;
    Date dateBefore;
    EditText main_address, RP_mobile, RP_phone, district, city;
    SQLite sqLite;
    ArrayList<remainder_modelclass> hq_view = new ArrayList<>();
    String qualif, cate, spec, dob, dow, address, mobile, phone, email, docname, docsex, doc_code, Dcr_posname, Lat, Long, Town_val;
    public static String profil_val = "";
    private GoogleMap mMap;
    GPSTrack gpsTrack;
    double str1, str2;
    ArrayList<Mapview_modelclass> listed_cust = new ArrayList<>();
    ArrayList<CustomModel> dataList = new ArrayList<>();
    String Doc_geoneed, Che_geoneed, Stk_geoneed, Cip_geoneed, Ult_geoneed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_profiling);

        backArrow = findViewById(R.id.backArrow);
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        email1 = findViewById(R.id.PR_email);
        dob_date = findViewById(R.id.dob_date);
        dow_date = findViewById(R.id.dow_date);
        main_address = findViewById(R.id.main_address);
        RP_mobile = findViewById(R.id.RP_mobile);
        RP_phone = findViewById(R.id.RP_phone);
        Dcrname = findViewById(R.id.Dctname);
        submit_profil = findViewById(R.id.submit_profil);
        district = findViewById(R.id.district);
        city = findViewById(R.id.city);
        clear_all = findViewById(R.id.clear_all);
        tag_clk = findViewById(R.id.tag_clk);
        drawer_Layout12 = findViewById(R.id.drawer_layout);
        Qualification = findViewById(R.id.Qualification);
        Speciality = findViewById(R.id.Speciality);
        Category = findViewById(R.id.Category);
        et_Custsearch = findViewById(R.id.et_Custsearch);
        app_recycler_view = findViewById(R.id.app_recycler_view);
        headtext_id = findViewById(R.id.headtext_id);
        close_sideview = findViewById(R.id.close_sideview);
        doc_qual = findViewById(R.id.doc_qual);
        map_layout = findViewById(R.id.map_layout);
        listView = findViewById(R.id.list_view);

        gpsTrack = new GPSTrack(this);
        app_recycler_view.setVisibility(View.VISIBLE);
        Bundle extra = getIntent().getExtras();
        sqLite = new SQLite(this);



        Doc_geoneed = SharedPref.getGeoNeed(this);
        Che_geoneed = SharedPref.getGeotagNeedChe(this);
        Stk_geoneed = SharedPref.getGeotagNeedStock(this);
        Cip_geoneed = SharedPref.getGeotagNeedCip(this);
        Ult_geoneed = SharedPref.getGeotagNeedUnlst(this);

        if (Doc_geoneed.equals("1") || Che_geoneed.equals("1") || Stk_geoneed.equals("") || Cip_geoneed.equals("1") || Ult_geoneed.equals("1")) {
            map_layout.setVisibility(View.VISIBLE);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

        if (!profil_dates.equals("")) {
            dob_date.setText(profil_dates);
        }

        if (extra != null) {
            qualif = extra.getString("Qual_values");
            spec = extra.getString("Spec_values");
            cate = extra.getString("cate_values");
            docname = extra.getString("Doc_name");
            doc_code = extra.getString("Doc_code");
            cate_code = extra.getString("cate_code");
            spec_code = extra.getString("Spec_code");
            Dcr_posname = extra.getString("PosDCRname");
            Lat = extra.getString("lat");
            Long = extra.getString("long");
            Town_val = extra.getString("Town");


            if (extra.getString("DOB").equals("")) {
                dob = "";
            } else {
                dob = extra.getString("DOB");
            }

            if (extra.getString("ListedDrSex").equals("") || extra.getString("ListedDrSex").equals("null")) {
                docsex = "";
            } else {
                docsex = extra.getString("ListedDrSex");
            }

            if (extra.getString("DOW").equals("")) {
                dow = "";
            } else {
                dow = extra.getString("DOW");
            }

            if (extra.getString("ADDRESS").equals("")) {
                address = "";
            } else {
                address = extra.getString("ADDRESS");
                Log.d("address", address);
            }


            if (extra.getString("MOB").equals("")) {
                mobile = "";
            } else {
                mobile = extra.getString("MOB");
            }


            if (extra.getString("PHN").equals("")) {
                phone = "";
            } else {
                phone = extra.getString("PHN");
            }

            if (extra.getString("EMAIL").equals("")) {
                email = "";
            } else {
                email = extra.getString("EMAIL");
            }
        }

        if (docsex.equals("M")) {
            check1.setChecked(true);
            docsex = "";
        }
        if (docsex.equals("F")) {
            check2.setChecked(true);
            docsex = "";
        }

        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    check2.setChecked(false); // disable checkbox
                }
            }
        });


        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    check1.setChecked(false); // disable checkbox
                }
            }
        });

        Qualification.setText(qualif);
        Speciality.setText(spec);
        Category.setText(cate);
        dob_date.setText(dob);
        dow_date.setText(dow);
        main_address.setText(address);
        RP_mobile.setText(mobile);
        RP_phone.setText(phone);
        email1.setText(email);
        Dcrname.setText(docname);


        dob_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance(Locale.getDefault());
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Resource_profiling.this,
                    //   R.style.CustomDatePickerDialogTheme,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        if ((monthOfYear + 1) < 10) {
                            if (dayOfMonth < 10) {
                                dob_dt = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
                            } else {
                                dob_dt = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }
                        } else {
                            if (dayOfMonth < 10) {
                                dob_dt = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
                            } else {
                                dob_dt = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }
                        }
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String inputDateStr = dob_dt;
                        try {
                            dob_date.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_11, TimeUtils.FORMAT_14, inputDateStr));
                            dateBefore = inputFormat.parse(dob_dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    },
                    mYear, mMonth, mDay
            );

            datePickerDialog.show();
        });

        dow_date.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance(Locale.getDefault());
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(Resource_profiling.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        if ((monthOfYear + 1) < 10) {
                            if (dayOfMonth < 10) {
                                dow_dt = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
                            } else {
                                dow_dt = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }
                        } else {
                            if (dayOfMonth < 10) {
                                dow_dt = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
                            } else {
                                dow_dt = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }
                        }
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String inputDateStr = dow_dt;
                        Date date = null;
                        try {
                            date = inputFormat.parse(inputDateStr);
                            dow_date.setText((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_11, TimeUtils.FORMAT_14, inputDateStr)));//
                            dateBefore = inputFormat.parse(dow_dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        });


        clear_all.setOnClickListener(view -> {
            Qualification.setText("");
            Speciality.setText("");
            Category.setText("");
            dob_date.setText("");
            dow_date.setText("");
            main_address.setText("");
            RP_mobile.setText("");
            RP_phone.setText("");
            email1.setText("");
            city.setText("");
            district.setText("");
        });

        tag_clk.setOnClickListener(view -> {
            Intent visit = new Intent(this, MyResource_mapview.class);
            visit.putExtra("type", doc_code);
            visit.putExtra("cust_name", "Res_doc");
            visit.putExtra("Dcr_name", docname);
            visit.putExtra("pos_name", Dcr_posname);//Town_name
            visit.putExtra("Town_loct", Town_val);
            startActivity(visit);
        });

        close_sideview.setOnClickListener(view -> {
            Resource_profiling.drawer_Layout12.closeDrawer(GravityCompat.END);
        });

        if (!Resource_adapter.rec_val.equals("D")) {
            doc_qual.setVisibility(View.GONE);
        }


        Qualification.setOnClickListener(view -> {
            headtext_id.setText("Qualification");
            sidelisted_data("Q");
            drawer_Layout12.openDrawer(GravityCompat.END);
        });
        Speciality.setOnClickListener(view -> {
            headtext_id.setText("Speciality");
            sidelisted_data("S");
            drawer_Layout12.openDrawer(GravityCompat.END);
        });
        Category.setOnClickListener(view -> {
            headtext_id.setText("Category");
            sidelisted_data("C");
            drawer_Layout12.openDrawer(GravityCompat.END);
        });

        submit_profil.setOnClickListener(view -> {
            String email = email1.getText().toString().trim();

            if (Qualification.getText().toString().equals("")) {
                Toast.makeText(this, "Select Qualification", Toast.LENGTH_SHORT).show();
            } else if (Speciality.getText().toString().equals("")) {
                Toast.makeText(this, "Select Speciality", Toast.LENGTH_SHORT).show();
            } else if (Category.getText().toString().equals("")) {
                Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
            } else if (!email.isEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "mail not valid ", Toast.LENGTH_SHORT).show();
                } else {
                    submit_profiling();
                }
            } else {
                submit_profiling();
            }
        });

        backArrow.setOnClickListener(view -> {
            finish();
        });


    }


    public void sidelisted_data(String pos) {
        try {
            hq_view.clear();
            JSONArray jsonspc_Doc = null;

            if (pos.equals("Q")) {
                jsonspc_Doc = sqLite.getMasterSyncDataByKey(Constants.QUALIFICATION);
            } else if (pos.equals("S")) {
                jsonspc_Doc = sqLite.getMasterSyncDataByKey(Constants.SPECIALITY);

            } else if (pos.equals("C")) {
                jsonspc_Doc = sqLite.getMasterSyncDataByKey(Constants.CATEGORY);
            }

            if (jsonspc_Doc.length() > 0) {
                for (int i = 0; i < jsonspc_Doc.length(); i++) {
                    JSONObject jsonObject = jsonspc_Doc.getJSONObject(i);
                    String Code = jsonObject.getString("Code");
                    String Name = jsonObject.getString("Name");
                    remainder_modelclass doc_VALUES = new remainder_modelclass(Code, Name, "1");
                    hq_view.add(doc_VALUES);
                }
                profil_val = pos;
                cuslistadap = new cuslistadapter(Resource_profiling.this, hq_view, pos);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                app_recycler_view.setLayoutManager(mLayoutManager);
                app_recycler_view.setItemAnimator(new DefaultItemAnimator());
                app_recycler_view.setAdapter(cuslistadap);
                cuslistadap.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submit_profiling() {
        try {
            String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
            String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
            String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
            api_interface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl + replacedUrl);
            progressDialog = CommonUtilsMethods.createProgressDialog(this);
            JSONObject jsonobj = new JSONObject();
            JSONArray jarr_addctrl = new JSONArray();


            jsonobj.put("tableName", "savedrprofile");
            jsonobj.put("AdditionalCtrls", "");
            jsonobj.put("ContactPerson", "");//docname,doc_code
            jsonobj.put("ListedDrCode", doc_code);
            jsonobj.put("ListedDr_Name", docname);
            jsonobj.put("ListedDr_Address1", main_address.getText().toString());
            jsonobj.put("ListedDr_Address2", "");
            jsonobj.put("ListedDr_Address3", "");
            jsonobj.put("Doc_Cat_Code", cate_code);
            jsonobj.put("Doc_Cat_ShortName", Category.getText().toString());
            jsonobj.put("Doc_Class_ShortName", "");

//            String[] dob_arr = refillingbinding.dobDate.getText().toString().split("/");
//            String[] dow_arr = refillingbinding.dowDate.getText().toString().split("/");

            jsonobj.put("ListedDr_DOB", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_14, TimeUtils.FORMAT_15, dob_date.getText().toString()));
            jsonobj.put("ListedDr_DOW", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_14, TimeUtils.FORMAT_15, dow_date.getText().toString()));
            jsonobj.put("ListedDr_Email", email1.getText().toString());
            jsonobj.put("ListedDr_Hospital", "");


            jsonobj.put("ListedDr_Mobile", RP_mobile.getText().toString());
            jsonobj.put("ListedDr_Phone", RP_phone.getText().toString());
            jsonobj.put("Doc_QuaCode", Qual_code);
            jsonobj.put("Doc_Qua_Name", Qualification.getText().toString());
            jsonobj.put("Doc_Special_Code", spec_code);
            jsonobj.put("Doc_Spec_ShortName", Speciality.getText().toString());
            jsonobj.put("Map_ListedDr_Products", "");
            jsonobj.put("ListedDr_Visit_Days", "");


            Log.d("prifiling", jsonobj.toString());

            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "table/dcrmasterdata");
            Call<JsonElement> call = null;
            call = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonobj.toString());






            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {

//                        syn_dcr_data();


                        Intent intent12 = new Intent(Resource_profiling.this, HomeDashBoard.class);
                        startActivity(intent12);

                        Toast.makeText(Resource_profiling.this, "Remaindercalls Add Successfully", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        gpsTrack = new GPSTrack(this);
        str1 = gpsTrack.getLatitude();
        str2 = gpsTrack.getLongitude();

        if (Lat.equals("") && Long.equals("")) {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
        } else {
            mMap.setMyLocationEnabled(true);
        }


        DCR_Doc();
    }


    @SuppressLint("PotentialBehaviorOverride")
    public void DCR_Doc() {
        mMap.clear();
        listed_cust.clear();
        dataList.clear();
        String latitude = "", longtitue = "", address = "";

        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(this));
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (doc_code.equals(jsonObject.getString("Code"))) {//
                        latitude = (jsonObject.getString("Lat"));
                        longtitue = (jsonObject.getString("Long"));
                        address = (jsonObject.getString("Addrs"));

                        String custname = (jsonObject.getString("Name"));
                        String townname = (jsonObject.getString("Town_Name"));
                        Mapview_modelclass vals = new Mapview_modelclass(latitude, longtitue, address, custname, townname, "");
                        listed_cust.add(vals);
                        Log.d("listsize", String.valueOf(listed_cust) + "---" + address);
                        if (address.equals("")) {
                            listView.setVisibility(View.GONE);
                        }
                        dataList.add(new CustomModel(address));

                        // Add more data as needed

                        CustomAdapter adapter = new CustomAdapter(this, dataList);
                        listView.setAdapter(adapter);

                    }
                }
                Log.d("list_size", dataList.toString() + "---" + dataList.size());
                if (dataList.size() == 0) {
                    listView.setVisibility(View.GONE);
                }

                for (int i = 0; i < listed_cust.size(); i++) {
                    if (listed_cust.get(i).getStrlat() != null && listed_cust.get(i).getStrlat().length() > 0) {
                        LatLng sydney = new LatLng(Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong()));
                        LatLng sydney1 = new LatLng(Double.parseDouble(listed_cust.get(0).getStrlat()), Double.parseDouble(listed_cust.get(0).getStrlong()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
                        LatLng location = new LatLng(Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong())); // Replace with your desired latitude and longitude
                        Log.e("loc_latlong", str1 + "--" + str2 + "--" + location);

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
//                        mMap.setMyLocationEnabled(true);


                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.marker);

//                        Bitmap b = BitmapDescriptorFactory.fromResource(R.drawable.marker);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 30, 30, true);

//


                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));


                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 10f);
                        mMap.moveCamera(cameraUpdate);

//                        if(latitude.equals("")&&longtitue.equals("")){
//                            mMap.setMyLocationEnabled(true);
//                        }else{
//                            mMap.setMyLocationEnabled(false);
//                            mMap.getUiSettings().setScrollGesturesEnabled(false);
//                            mMap.getUiSettings().setZoomControlsEnabled(false);
//                            mMap.getUiSettings().setZoomGesturesEnabled(false);
//                            mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
//                            mMap.getUiSettings().setCompassEnabled(false);
//                            mMap.getUiSettings().setRotateGesturesEnabled(false);
//                        }

                        mMap.setOnMarkerClickListener(this);


//                        Dr_addrs.setText(getAddress(Double.parseDouble(listed_cust.get(i).getStrlat()), Double.parseDouble(listed_cust.get(i).getStrlong())));

                        marker.setTag(listed_cust.get(i));
                        mMap.setOnMarkerClickListener(this);

                        Log.e("location_latlong", str1 + "--" + str2);


                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String getAddress(double la, double ln) {
        Geocoder geocoder;
        List<Address> addresses;
        String address = "";
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(la, ln, 1);
            if (addresses.size() == 0) {

            } else {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
}

