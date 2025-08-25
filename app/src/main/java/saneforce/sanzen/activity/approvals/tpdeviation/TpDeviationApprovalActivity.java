package saneforce.sanzen.activity.approvals.tpdeviation;


import static saneforce.sanzen.activity.tourPlan.TourPlanActivity.prepareSessionListForAdapter;

import android.annotation.SuppressLint;
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
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQHeaderModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQItemModelClass;
import saneforce.sanzen.activity.tourPlan.session.SessionViewAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityTpDeviationApprovalBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class TpDeviationApprovalActivity extends AppCompatActivity {
    ActivityTpDeviationApprovalBinding tpDeviationApprovalBinding;
    ArrayList<TpDeviationModelList> tpDeviationModelLists = new ArrayList<>();
    TpDeviationAdapter tpDeviationAdapter;
    JSONObject jsonTpDeviation = new JSONObject();
    ApiInterface api_interface;

    ProgressDialog progressDialog = null;
    CommonUtilsMethods commonUtilsMethods;
    private MasterDataDao masterDataDao;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            tpDeviationApprovalBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tpDeviationApprovalBinding = ActivityTpDeviationApprovalBinding.inflate(getLayoutInflater());
        setContentView(tpDeviationApprovalBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        masterDataDao = RoomDB.getDatabase(TpDeviationApprovalActivity.this).masterDataDao();
        CallTpDeviationAPI();

        tpDeviationApprovalBinding.ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(TpDeviationApprovalActivity.this, ApprovalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        tpDeviationApprovalBinding.searchTpDeviation.addTextChangedListener(new TextWatcher() {
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

        tpDeviationApprovalBinding.tpDeviationApprovalNavigation.tpDrawerCloseIcon.setOnClickListener(view -> tpDeviationApprovalBinding.tpDeviationApprovalDrawer.closeDrawer(GravityCompat.END));
        tpDeviationApprovalBinding.tpDeviationApprovalNavigation.planDate.setVisibility(View.GONE);

        tpDeviationApprovalBinding.ivFilter.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(TpDeviationApprovalActivity.this, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, tpDeviationApprovalBinding.ivFilter, Gravity.END);
            popup.getMenu().add(1, 1, 1, "By Name      A - Z");
            popup.getMenu().add(2, 2, 2, "By Name      Z - A");
            popup.getMenu().add(3, 3, 3, "By Date      1 2 3");
            popup.getMenu().add(4, 4, 4, "By Date      3 2 1");
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case 1:
                        sortData(SortType.NAME_ASCENDING);
                        break;
                    case 2:
                        sortData(SortType.NAME_DESCENDING);
                        break;
                    case 3:
                        sortData(SortType.DATE_ASCENDING);
                        break;
                    case 4:
                        sortData(SortType.DATE_DESCENDING);
                        break;
                }
                tpDeviationAdapter.filterList(tpDeviationModelLists);
                return true;
            });
            popup.show();
        });
    }

    private ModelClass.SessionList.SubClass getHQData(String hqCode) {
        try {
            JSONArray HQMaster = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            for (int i = 0; i<HQMaster.length(); i++) {
                JSONObject jsonObject = HQMaster.optJSONObject(i);
                if(jsonObject.optString("Code").equalsIgnoreCase(hqCode)) {
                    return new ModelClass.SessionList.SubClass(jsonObject.optString("name"), hqCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelClass.SessionList.SubClass();
    }

    private void filter(String text) {
        ArrayList<TpDeviationModelList> filteredNames = new ArrayList<>();
        for (TpDeviationModelList s : tpDeviationModelLists) {
            if(s.getSfName().toLowerCase().contains(text.toLowerCase()) || s.getDate().contains(text.toLowerCase()) || s.getDeviationRemarks().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        tpDeviationAdapter.filterList(filteredNames);
    }

    private enum SortType {
        NAME_ASCENDING,
        NAME_DESCENDING,
        DATE_ASCENDING,
        DATE_DESCENDING;
    }

    private void sortData(SortType sortType) {
        switch (sortType) {
            case NAME_ASCENDING:
                Collections.sort(tpDeviationModelLists, Comparator.comparing(TpDeviationModelList::getSfName));
                break;
            case NAME_DESCENDING:
                Collections.sort(tpDeviationModelLists, Comparator.comparing(TpDeviationModelList::getSfName).reversed());
                break;
            case DATE_ASCENDING:
                Collections.sort(tpDeviationModelLists, Comparator.comparing(o -> (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_4, o.getDate()))));
                break;
            case DATE_DESCENDING:
                Collections.sort(tpDeviationModelLists, (o1, o2) -> (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_4, o2.getDate())).compareTo((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_4, o1.getDate()))));
                break;
        }
    }

    private void CallTpDeviationAPI() {
        progressDialog = CommonUtilsMethods.createProgressDialog(TpDeviationApprovalActivity.this);
        try {
            jsonTpDeviation = CommonUtilsMethods.CommonObjectParameter(TpDeviationApprovalActivity.this);
            jsonTpDeviation.put("tableName", "getdevappr");
            jsonTpDeviation.put("sfcode", SharedPref.getSfCode(this));
            jsonTpDeviation.put("division_code", SharedPref.getDivisionCode(this));
            jsonTpDeviation.put("Rsf", SharedPref.getHqCode(this));
            Log.v("json_get_tpDev_list", jsonTpDeviation.toString());
        } catch (Exception ignored) {

        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetTpDevList = api_interface.getJSONElement(SharedPref.getCallApiUrl(TpDeviationApprovalActivity.this), mapString, jsonTpDeviation.toString());
        callGetTpDevList.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                if(response.isSuccessful()) {
                    try {
                        progressDialog.dismiss();
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i<jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            tpDeviationModelLists.add(new TpDeviationModelList(json.getString("sf_name"), json.getString("sf_code"), json.getString("sl_no"), json.getString("missed_date"), json.getString("Deviation_Reason")));
                        }
                        sortData(SortType.NAME_ASCENDING);
                        tpDeviationAdapter = new TpDeviationAdapter(TpDeviationApprovalActivity.this, tpDeviationModelLists, viewPlanClickListener);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        tpDeviationApprovalBinding.rvTpDeviation.setLayoutManager(mLayoutManager);
                        tpDeviationApprovalBinding.rvTpDeviation.setAdapter(tpDeviationAdapter);
                    } catch (Exception ignored) {

                    }
                }else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(TpDeviationApprovalActivity.this, getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(TpDeviationApprovalActivity.this, getString(R.string.no_network));
            }
        });

    }

    public TpDeviationAdapter.ViewPlanClickListener viewPlanClickListener = this::viewActualPlan;

    public void viewActualPlan(TpDeviationModelList tpDeviationModelList) {
        progressDialog = CommonUtilsMethods.createProgressDialog(TpDeviationApprovalActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = CommonUtilsMethods.CommonObjectParameter(TpDeviationApprovalActivity.this);
            jsonObject.put("tableName", "getactual_plan");
            jsonObject.put("sfcode", tpDeviationModelList.getSfCode());
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", SharedPref.getHqCode(this));
            jsonObject.put("actual_date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_15, tpDeviationModelList.getDate()));
            Log.v("json_get_tpDev_list_plan", jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetTpDevList = api_interface.getJSONElement(SharedPref.getCallApiUrl(TpDeviationApprovalActivity.this), mapString, jsonObject.toString());
        callGetTpDevList.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                if(response.isSuccessful()) {
                    Log.i("TP Deviation actual plan", "onResponse: " + response.body());
                    progressDialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        prepareSessionData(jsonArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(TpDeviationApprovalActivity.this, getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(TpDeviationApprovalActivity.this, getString(R.string.no_network));
                t.printStackTrace();
            }
        });
    }

    private void prepareSessionData(JSONArray jsonArray) {
        if(jsonArray.length() > 0) {
            JSONObject jsonObject = jsonArray.optJSONObject(0);
            ModelClass.SessionList sessionList = prepareSessionListForAdapter();
            sessionList.setHQ(getHQData(jsonObject.optString("HQCodes")));

            ModelClass.SessionList.WorkType workType = new ModelClass.SessionList.WorkType(jsonObject.optString("FWFlg"), jsonObject.optString("WTName"), "", jsonObject.optString("WTCode"));
            sessionList.setWorkType(workType);

            List<ModelClass.SessionList.SubClass> clusterList = prepareList(jsonObject.optString("ClusterCode"), jsonObject.optString("ClusterName"));
            List<ModelClass.SessionList.SubClass> jwList = prepareList(jsonObject.optString("JWCodes"), jsonObject.optString("JWNames"));
            List<ModelClass.SessionList.SubClass> doctorList = prepareList(jsonObject.optString("Dr_Code"), jsonObject.optString("Dr_Name"));
            List<ModelClass.SessionList.SubClass> chemistList = prepareList(jsonObject.optString("Chem_Code"), jsonObject.optString("Chem_Name"));
            List<ModelClass.SessionList.SubClass> stockistList = prepareList(jsonObject.optString("Stockist_Code"), jsonObject.optString("Stockist_Name"));
            List<ModelClass.SessionList.SubClass> hqs = prepareList(jsonObject.optString("HQCodes"), jsonObject.optString("HQNames"));

            ArrayList<MultiHQHeaderModelClass> clusters = prepareList(hqs, jsonObject.optString("ClusterCode"), jsonObject.optString("ClusterName"));
            ArrayList<MultiHQHeaderModelClass> JCs = prepareList(hqs, jsonObject.optString("JWCodes"), jsonObject.optString("JWNames"));
            ArrayList<MultiHQHeaderModelClass> listedDrs = prepareList(hqs, jsonObject.optString("Dr_Code"), jsonObject.optString("Dr_Name"));
            ArrayList<MultiHQHeaderModelClass> chemists = prepareList(hqs, jsonObject.optString("Chem_Code"), jsonObject.optString("Chem_Name"));
            ArrayList<MultiHQHeaderModelClass> stockiests = prepareList(hqs, jsonObject.optString("Stockist_Code"), jsonObject.optString("Stockist_Name"));
            ArrayList<MultiHQHeaderModelClass> unListedDrs = new ArrayList<>();
            ArrayList<MultiHQHeaderModelClass> cips = new ArrayList<>();
            ArrayList<MultiHQHeaderModelClass> hospitals = new ArrayList<>();

            sessionList.setCluster(clusterList);
            sessionList.setJC(jwList);
            sessionList.setListedDr(doctorList);
            sessionList.setChemist(chemistList);
            sessionList.setStockiest(stockistList);
            sessionList.setHQs(hqs);
            sessionList.setClusters(clusters);
            sessionList.setJCs(JCs);
            sessionList.setListedDrs(listedDrs);
            sessionList.setChemists(chemists);
            sessionList.setStockiests(stockiests);
            sessionList.setUnListedDrs(unListedDrs);

            ArrayList<ModelClass.SessionList> sessionLists = new ArrayList<>();
            sessionLists.add(sessionList);

//            ModelClass modelClass = new ModelClass(jsonObject.optString("Day"), TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_19, jsonObject.optString("TPDt")), jsonObject.optString("tpday"), jsonObject.optString("Tour_Month"), jsonObject.optString("Tour_Year"), false, sessionLists);

            if(!jsonObject.optString("HQCodes2").isEmpty()) {
                sessionList = prepareSessionListForAdapter();
                sessionList.setHQ(getHQData(jsonObject.optString("HQCodes2")));

                workType = new ModelClass.SessionList.WorkType(jsonObject.optString("FWFlg2"), jsonObject.optString("WTName2"), "", jsonObject.optString("WTCode2"));
                sessionList.setWorkType(workType);

                clusterList = prepareList(jsonObject.optString("ClusterCode2"), jsonObject.optString("ClusterName2"));
                jwList = prepareList(jsonObject.optString("JWCodes2"), jsonObject.optString("JWNames2"));
                doctorList = prepareList(jsonObject.optString("Dr_two_code"), jsonObject.optString("Dr_two_name"));
                chemistList = prepareList(jsonObject.optString("Chem_two_code"), jsonObject.optString("Chem_two_name"));
                stockistList = prepareList(jsonObject.optString("Stockist_two_code"), jsonObject.optString("Stockist_two_name"));
                hqs = prepareList(jsonObject.optString("HQCodes2"), jsonObject.optString("HQNames2"));
                clusters = prepareList(hqs, jsonObject.optString("ClusterCode2"), jsonObject.optString("ClusterName2"));
                JCs = prepareList(hqs, jsonObject.optString("JWCodes2"), jsonObject.optString("JWNames2"));
                listedDrs = prepareList(hqs, jsonObject.optString("Dr_two_code"), jsonObject.optString("Dr_two_name"));
                chemists = prepareList(hqs, jsonObject.optString("Chem_two_code"), jsonObject.optString("Chem_two_name"));
                stockiests = prepareList(hqs, jsonObject.optString("Stockist_two_code"), jsonObject.optString("Stockist_two_name"));
                sessionList.setCluster(clusterList);
                sessionList.setJC(jwList);
                sessionList.setListedDr(doctorList);
                sessionList.setChemist(chemistList);
                sessionList.setStockiest(stockistList);
                sessionList.setHQs(hqs);
                sessionList.setClusters(clusters);
                sessionList.setJCs(JCs);
                sessionList.setListedDrs(listedDrs);
                sessionList.setChemists(chemists);
                sessionList.setStockiests(stockiests);

                sessionLists.add(sessionList);
            }
            if(!jsonObject.optString("HQCodes3").isEmpty()) {
                sessionList = prepareSessionListForAdapter();
                sessionList.setHQ(getHQData(jsonObject.optString("HQCodes3")));

                workType = new ModelClass.SessionList.WorkType(jsonObject.optString("FWFlg3"), jsonObject.optString("WTName3"), "", jsonObject.optString("WTCode3"));
                sessionList.setWorkType(workType);

                clusterList = prepareList(jsonObject.optString("ClusterCode3"), jsonObject.optString("ClusterName3"));
                jwList = prepareList(jsonObject.optString("JWCodes3"), jsonObject.optString("JWNames3"));
                doctorList = prepareList(jsonObject.optString("Dr_three_code"), jsonObject.optString("Dr_three_name"));
                chemistList = prepareList(jsonObject.optString("Chem_three_code"), jsonObject.optString("Chem_three_name"));
                stockistList = prepareList(jsonObject.optString("Stockist_three_code"), jsonObject.optString("Stockist_three_name"));
                hqs = prepareList(jsonObject.optString("HQCodes2"), jsonObject.optString("HQNames2"));
                clusters = prepareList(hqs, jsonObject.optString("ClusterCode3"), jsonObject.optString("ClusterName3"));
                JCs = prepareList(hqs, jsonObject.optString("JWCodes3"), jsonObject.optString("JWNames3"));
                listedDrs = prepareList(hqs, jsonObject.optString("Dr_three_code"), jsonObject.optString("Dr_three_name"));
                chemists = prepareList(hqs, jsonObject.optString("Chem_three_code"), jsonObject.optString("Chem_three_name"));
                stockiests = prepareList(hqs, jsonObject.optString("Stockist_Code"), jsonObject.optString("Stockist_three_name"));
                sessionList.setCluster(clusterList);
                sessionList.setJC(jwList);
                sessionList.setListedDr(doctorList);
                sessionList.setChemist(chemistList);
                sessionList.setStockiest(stockistList);
                sessionList.setHQs(hqs);
                sessionList.setClusters(clusters);
                sessionList.setJCs(JCs);
                sessionList.setListedDrs(listedDrs);
                sessionList.setChemists(chemists);
                sessionList.setStockiests(stockiests);

                sessionLists.add(sessionList);
            }

            ModelClass modelClass = new ModelClass(jsonObject.optString("Day"), TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_19, jsonObject.optString("TPDt")), jsonObject.optString("tpday"), jsonObject.optString("Tour_Month"), jsonObject.optString("Tour_Year"), false, sessionLists);
            populateSessionViewAdapter(modelClass);
        }
    }

    public void populateSessionViewAdapter(ModelClass modelClass) {
        tpDeviationApprovalBinding.tpDeviationApprovalNavigation.addEditViewTxt.setText(modelClass.getDate());
        tpDeviationApprovalBinding.tpDeviationApprovalDrawer.openDrawer(GravityCompat.END);
        SessionViewAdapter sessionViewAdapter = new SessionViewAdapter(modelClass, TpDeviationApprovalActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TpDeviationApprovalActivity.this);
        tpDeviationApprovalBinding.tpDeviationApprovalNavigation.tpSessionRecView.setLayoutManager(layoutManager);
        tpDeviationApprovalBinding.tpDeviationApprovalNavigation.tpSessionRecView.setAdapter(sessionViewAdapter);
        tpDeviationApprovalBinding.tpDeviationApprovalNavigation.addSaveLayout.setVisibility(View.GONE);
        tpDeviationApprovalBinding.tpDeviationApprovalNavigation.clrSaveBtnLayout.setVisibility(View.GONE);
        tpDeviationApprovalBinding.tpDeviationApprovalNavigation.editLayout.setVisibility(View.GONE);
    }

    private List<ModelClass.SessionList.SubClass> prepareList(String codes, String names) {
        List<ModelClass.SessionList.SubClass> list = new ArrayList<>();
        try {
            String[] codeArray = CommonUtilsMethods.removeLastComma(codes).split(",");
            String[] nameArray = CommonUtilsMethods.removeLastComma(names).split(",");
            for (int index = 0; index<codeArray.length; index++) {
                if(!(codeArray[index].isEmpty() || nameArray[index].isEmpty())) {
                    list.add(new ModelClass.SessionList.SubClass(nameArray[index], codeArray[index]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private ArrayList<MultiHQHeaderModelClass> prepareList(List<ModelClass.SessionList.SubClass> hqs, String Code, String Name) {
        String[] arrName = Name.split("\\$");
        String[] arrCode = Code.split("\\$");
        arrName = Arrays.stream(arrName)
                .filter(s -> s != null && !s.isEmpty())
                .toArray(String[]::new);
        arrCode = Arrays.stream(arrCode)
                .filter(s -> s != null && !s.isEmpty())
                .toArray(String[]::new);
        ArrayList<MultiHQHeaderModelClass> resultArray = new ArrayList<>();
        for (int i = 0; i<hqs.size(); i++) {
            ModelClass.SessionList.SubClass hq = hqs.get(i);
            if(arrCode.length > i) {
                try {
                    String[] names = arrName[i].split(",");
                    String[] codes = arrCode[i].split(",");
                    ArrayList<MultiHQItemModelClass> itemsList = new ArrayList<>();
                    MultiHQHeaderModelClass multiHQHeaderModelClass = new MultiHQHeaderModelClass(hq.getName(), hq.getCode(), itemsList, true);
                    for (int j = 0; j<codes.length; j++) {
                        MultiHQItemModelClass multiHQItemModelClass = new MultiHQItemModelClass(names[j], codes[j], hq.getCode(), "", "", true);
                        itemsList.add(multiHQItemModelClass);
                    }
                    multiHQHeaderModelClass.setItemsList(itemsList);
                    if(!itemsList.isEmpty()) {
                        resultArray.add(multiHQHeaderModelClass);
                    }
                } catch (Exception e) {
                    Log.d("TAG", "addExtraData: " + Name + Code);
                    Log.d("TAG", "addExtraData: " + Arrays.asList(arrName).toString() + Arrays.asList(arrCode).toString());
                    e.printStackTrace();
                }
            }
        }

        return resultArray;
    }


}