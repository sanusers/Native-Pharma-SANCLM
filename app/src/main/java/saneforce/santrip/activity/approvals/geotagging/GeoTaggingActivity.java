package saneforce.santrip.activity.approvals.geotagging;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.ApprovalsActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.ActivityGeoTaggingBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;

import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;


public class GeoTaggingActivity extends AppCompatActivity {
    public static String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode;
    ActivityGeoTaggingBinding geoTaggingBinding;
    ArrayList<GeoTaggingModelList> geoTaggingModelLists = new ArrayList<>();
    ArrayList<GeoTaggingModelList> geoTaggingModelSort = new ArrayList<>();
    GeoTaggingAdapter geoTaggingAdapter;
    ApiInterface api_interface;
    JSONObject jsonGeoTagList = new JSONObject();
    ProgressDialog progressDialog = null;
    SQLite sqLite;
    CommonUtilsMethods commonUtilsMethods;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            geoTaggingBinding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geoTaggingBinding = ActivityGeoTaggingBinding.inflate(getLayoutInflater());
        setContentView(geoTaggingBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        sqLite = new SQLite(getApplicationContext());
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        CallGeoTagApi();

        geoTaggingBinding.ivFilter.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, geoTaggingBinding.ivFilter, Gravity.END);

            popup.getMenu().add(0, 0, 0, "All");
            if (SharedPref.getDrNeed(context).equalsIgnoreCase("0")) popup.getMenu().add(1, 1, 1, SharedPref.getDrCap(this));
            if (SharedPref.getChmNeed(context).equalsIgnoreCase("0")) popup.getMenu().add(2, 2, 2, SharedPref.getChmCap(context));
            if (SharedPref.getStkNeed(context).equalsIgnoreCase("0")) popup.getMenu().add(3, 3, 3, SharedPref.getStkCap(context));
            if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) popup.getMenu().add(4, 4, 4, SharedPref.getUNLcap(context));
            if (SharedPref.getHospNeed(context).equalsIgnoreCase("0")) popup.getMenu().add(5, 5, 5, SharedPref.getHospCaption(context));
            if (SharedPref.getCipNeed(context).equalsIgnoreCase("0")) popup.getMenu().add(6, 6, 6, SharedPref.getCipCaption(context));
            popup.getMenu().add(7, 7, 7, "By Name      A - Z");
            popup.getMenu().add(8, 8, 8, "By Name      Z - A");
            popup.getMenu().add(9, 9, 9, "By Date      Newer - Older");
            popup.getMenu().add(10, 10, 10, "By Date      Older - Newer");
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case 0:

                        SortTable("All");
                        break;
                    case 1:
                        SortTable("D");
                        break;
                    case 2:
                        SortTable("C");
                        break;
                    case 3:
                        SortTable("S");
                        break;
                    case 4:
                        SortTable("U");
                        break;
                    case 5:
                        SortTable("H");
                        break;
                    case 6:
                        SortTable("CIP");
                        break;
                    case 7:
                        SortTable("By Name      A - Z");
                        break;
                    case 8:
                        SortTable("By Name      Z - A");
                        break;
                    case 9:
                        SortTable("By Date      Newer - Older");
                        break;
                    case 10:
                        SortTable("By Date      Older - Newer");
                        break;
                }
                return true;
            });
            popup.show();
        });

        geoTaggingBinding.searchGeoTagging.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        geoTaggingBinding.ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(GeoTaggingActivity.this, ApprovalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void SortTable(String Mode) {

        geoTaggingModelSort.clear();
        for (int i = 0; i < geoTaggingModelLists.size(); i++) {
            if (Mode.equalsIgnoreCase("All") || Mode.equalsIgnoreCase("By Name      A - Z") || Mode.equalsIgnoreCase("By Name      Z - A") || Mode.equalsIgnoreCase("By Date      Newer - Older") || Mode.equalsIgnoreCase("By Date      Older - Newer")) {
                geoTaggingModelSort.add(new GeoTaggingModelList(geoTaggingModelLists.get(i).getName(), geoTaggingModelLists.get(i).getCode(), geoTaggingModelLists.get(i).getHqName(), geoTaggingModelLists.get(i).getHqCode(), geoTaggingModelLists.get(i).getCluster(), geoTaggingModelLists.get(i).getAddress(), geoTaggingModelLists.get(i).getLatitude(), geoTaggingModelLists.get(i).getLongitude(), geoTaggingModelLists.get(i).getCust_mode(), geoTaggingModelLists.get(i).getDate_time(), geoTaggingModelLists.get(i).getMapId()));
            } else {
                if (geoTaggingModelLists.get(i).getCust_mode().equalsIgnoreCase(Mode)) {
                    geoTaggingModelSort.add(new GeoTaggingModelList(geoTaggingModelLists.get(i).getName(), geoTaggingModelLists.get(i).getCode(), geoTaggingModelLists.get(i).getHqName(), geoTaggingModelLists.get(i).getHqCode(), geoTaggingModelLists.get(i).getCluster(), geoTaggingModelLists.get(i).getAddress(), geoTaggingModelLists.get(i).getLatitude(), geoTaggingModelLists.get(i).getLongitude(), geoTaggingModelLists.get(i).getCust_mode(), geoTaggingModelLists.get(i).getDate_time(), geoTaggingModelLists.get(i).getMapId()));
                }
            }
        }

        geoTaggingAdapter = new GeoTaggingAdapter(getApplicationContext(), geoTaggingModelSort);
        geoTaggingBinding.rvGeoTagging.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        geoTaggingBinding.rvGeoTagging.setAdapter(geoTaggingAdapter);

        switch (Mode) {
            case "By Name      A - Z":
                Log.v("iii", "0");
                Collections.sort(geoTaggingModelSort, Comparator.comparing(GeoTaggingModelList::getName));
                break;
            case "By Name      Z - A":
                Log.v("iii", "1");
                Collections.sort(geoTaggingModelSort, Collections.reverseOrder(new SortByName()));
                break;
            case "By Date      Newer - Older":
                Log.v("iii", "2");
                Collections.sort(geoTaggingModelSort, Comparator.comparing(GeoTaggingModelList::getDate_time));
                break;
            case "By Date      Older - Newer":
                Log.v("iii", "3");
                Collections.sort(geoTaggingModelSort, Collections.reverseOrder(new SortByDate()));
                break;

        }
       /* if (Mode.equalsIgnoreCase("By Name      A - Z")) {
            Collections.sort(geoTaggingModelSort, Comparator.comparing(GeoTaggingModelList::getName));
        } else if (Mode.equalsIgnoreCase("By Name      Z - A")) {
            Collections.sort(geoTaggingModelSort, Collections.reverseOrder(new SortByName()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                geoTaggingModelSort.forEach(fruit -> {
                Log.v("iii","---" + fruit.getHqName() + " " + fruit.getName() + " " +
                            fruit.getDate_time());
                });
            }
        } else if (Mode.equalsIgnoreCase("By Date      Newer - Older")) {
            Collections.sort(geoTaggingModelSort, Comparator.comparing(GeoTaggingModelList::getDate_time));
        } else if (Mode.equalsIgnoreCase("By Date      Older - Newer")) {
            Collections.sort(geoTaggingModelSort, Collections.reverseOrder(new SortByDate()));
        }*/
    }



    private void CallGeoTagApi() {
        progressDialog = CommonUtilsMethods.createProgressDialog(GeoTaggingActivity.this);
        try {
            jsonGeoTagList.put("tableName", "getgeoappr");
            jsonGeoTagList.put("sfcode", SharedPref.getSfCode(this));
            jsonGeoTagList.put("division_code", SharedPref.getDivisionCode(this));
            jsonGeoTagList.put("Rsf", SharedPref.getHqCode(this));
            jsonGeoTagList.put("sf_type", SharedPref.getSfType(this));
            jsonGeoTagList.put("Designation", SharedPref.getDesig(this));
            jsonGeoTagList.put("state_code", SharedPref.getStateCode(this));
            jsonGeoTagList.put("subdivision_code", SharedPref.getSubdivisionCode(this));
            Log.v("json_getGeoTag_list", jsonGeoTagList.toString());
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetGeoTagList = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonGeoTagList.toString());

        callGetGeoTagList.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Log.v("getGeoTag", "-response-" + response.body());
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            if (SharedPref.getDrNeed(context).equalsIgnoreCase("0") && json.getString("cus_mode").equalsIgnoreCase("D") || SharedPref.getChmNeed(context).equalsIgnoreCase("0") && json.getString("cus_mode").equalsIgnoreCase("C") || SharedPref.getStkNeed(context).equalsIgnoreCase("0") && json.getString("cus_mode").equalsIgnoreCase("S") || SharedPref.getUnlNeed(context).equalsIgnoreCase("0") && json.getString("cus_mode").equalsIgnoreCase("U") || SharedPref.getCipNeed(context).equalsIgnoreCase("0") && json.getString("cus_mode").equalsIgnoreCase("CIP") || SharedPref.getHospNeed(context).equalsIgnoreCase("0") && json.getString("cus_mode").equalsIgnoreCase("H")) {
                                geoTaggingModelLists.add(new GeoTaggingModelList(json.getString("cust_name"), json.getString("Cust_Code"), json.getString("Sf_Name"), json.getString("tagged_sfCode"), json.getString("tagged_cluster"), json.getString("addrs"), json.getString("lat"), json.getString("long"), json.getString("cus_mode"), json.getString("tagged_time"), json.getString("MapId")));
                                geoTaggingModelSort.add(new GeoTaggingModelList(json.getString("cust_name"), json.getString("Cust_Code"), json.getString("Sf_Name"), json.getString("tagged_sfCode"), json.getString("tagged_cluster"), json.getString("addrs"), json.getString("lat"), json.getString("long"), json.getString("cus_mode"), json.getString("tagged_time"), json.getString("MapId")));
                            }
                        }

                        geoTaggingAdapter = new GeoTaggingAdapter(getApplicationContext(), geoTaggingModelSort);
                        geoTaggingBinding.rvGeoTagging.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        geoTaggingBinding.rvGeoTagging.setAdapter(geoTaggingAdapter);
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.v("getGeoTag", "-error-" + e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void filter(String text) {
        ArrayList<GeoTaggingModelList> filteredNames = new ArrayList<>();
        for (GeoTaggingModelList s : geoTaggingModelSort) {
            if (s.getCust_mode().toLowerCase().contains(text.toLowerCase()) || s.getCluster().toLowerCase().contains(text.toLowerCase()) || s.getHqName().toLowerCase().contains(text.toLowerCase()) || s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
            if (text.equalsIgnoreCase("Doctor")) {
                if (s.getCust_mode().equalsIgnoreCase("D")) filteredNames.add(s);
            }
        }
        geoTaggingAdapter.filterList(filteredNames);
    }

    static class SortByName implements Comparator<GeoTaggingModelList> {
        @Override
        public int compare(GeoTaggingModelList a, GeoTaggingModelList b) {
            return a.getName().compareTo(b.getName());
        }
    }

    static class SortByDate implements Comparator<GeoTaggingModelList> {
        @Override
        public int compare(GeoTaggingModelList a, GeoTaggingModelList b) {
            return a.getDate_time().compareTo(b.getDate_time());
        }
    }
}