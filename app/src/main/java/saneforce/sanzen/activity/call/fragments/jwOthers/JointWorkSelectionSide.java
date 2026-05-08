package saneforce.sanzen.activity.call.fragments.jwOthers;

import static saneforce.sanzen.activity.call.DCRCallActivity.TodayPlanSfCode;
import static saneforce.sanzen.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanzen.activity.call.fragments.jwOthers.JWOthersFragment.callAddedJointList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.jwOthers.AdapterCallJointWorkList;
import saneforce.sanzen.activity.call.adapter.jwOthers.JwAdapter;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.FragmentSelectJwSideBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.DCRDocDataTableDetails.DCRDocDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;
import saneforce.sanzen.utility.TimeUtils;

public class JointWorkSelectionSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectJwSideBinding selectJwSideBinding;
    public static ArrayList<CallCommonCheckedList> JwList;
    @SuppressLint("StaticFieldLeak")
    public static JwAdapter jwAdapter;
    JSONArray jsonArray;
    JSONObject jsonObject;
    AdapterCallJointWorkList adapterCallJointWorkList;
    CommonUtilsMethods commonUtilsMethods;
    //  public static ArrayList<CallsModalClass> TodayCallList = new ArrayList<>();
    public static ArrayList<modelClass> TodayCallList = new ArrayList<>();
    public static Context Mcontext;
    private static ApiInterface apiInterface;
    public static ProgressDialog progressDialog;
    public static boolean isNeedtoAdd;
    boolean isProgressNeed = false;

    public static String FwFlag;
    private boolean isMgrUser;
    private RoomDB roomDB;
    private DCRDocDataDao dcrDocDataDao;
    private static MasterDataDao masterDataDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectJwSideBinding = FragmentSelectJwSideBinding.inflate(inflater);
        View view = selectJwSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        dcrDocDataDao = roomDB.dcrDocDataDao();
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        checkUserRole();
        SetupAdapter();

        selectJwSideBinding.tvDummy.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
            }
        });

        selectJwSideBinding.btnOk.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                for (int j = 0; j < JwList.size(); j++) {
                    if (JwList.get(j).isCheckedItem()) {
                        callAddedJointList.add(new CallCommonCheckedList(JwList.get(j).getName(), JwList.get(j).getCode()));
                    }
                }

                int count = callAddedJointList.size();
                for (int i = 0; i < count; i++) {
                    for (int j = i + 1; j < count; j++) {
                        if (callAddedJointList.get(i).getCode().equalsIgnoreCase(callAddedJointList.get(j).getCode())) {
                            callAddedJointList.set(i, new CallCommonCheckedList(callAddedJointList.get(i).getName(), callAddedJointList.get(i).getCode()));
                            callAddedJointList.remove(j--);
                            count--;
                        } else {
                            callAddedJointList.set(i, new CallCommonCheckedList(callAddedJointList.get(i).getName(), callAddedJointList.get(i).getCode()));
                        }
                    }
                }
                selectJwSideBinding.searchJw.setText("");
                dcrCallBinding.fragmentSelectJwSide.setVisibility(View.GONE);
                UtilityClass.hideKeyboard(requireActivity());
                AssignRecyclerView(getActivity(), requireContext(), callAddedJointList, JwList);
            }
        });

        selectJwSideBinding.imgClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                SetupAdapter();
                selectJwSideBinding.searchJw.setText("");
                dcrCallBinding.fragmentSelectJwSide.setVisibility(View.GONE);
                UtilityClass.hideKeyboard(requireActivity());
            }
        });


        selectJwSideBinding.searchJw.addTextChangedListener(new TextWatcher() {
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
        return view;
    }


    private void checkUserRole() {
        String sfType = SharedPref.getSfType(requireContext()); // or this if in Activity
        isMgrUser = "2".equals(sfType);
    }


    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : JwList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        jwAdapter.filterList(filterdNames);
    }

    public void SetupAdapter() {
        JwList.clear();
        try {
            if (DCRCallActivity.save_valid.equals("1")) {
                jsonArray = dcrDocDataDao.getDCRDocData(DCRCallActivity.hqcode).getDCRDocDataJSONArray();

                Log.d("jw_data", jsonArray.toString() + "====" + TodayPlanSfCode);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    JwList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false));
                }
            } else {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + TodayPlanSfCode).getMasterSyncDataJsonArray();
                Log.d("jw_data", jsonArray.toString() + "====" + TodayPlanSfCode);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    JwList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false));
                }
            }
            for (int i = 0; i < JwList.size(); i++) {
                CallCommonCheckedList JwCallCommonCheckedList = JwList.get(i);
                for (CallCommonCheckedList callCommonCheckedList : callAddedJointList) {
                    if (callCommonCheckedList.getCode().equalsIgnoreCase(JwCallCommonCheckedList.getCode()) || callCommonCheckedList.getName().equalsIgnoreCase(JwCallCommonCheckedList.getName())) {
                        JwCallCommonCheckedList.setCheckedItem(true);
                        break;
                    }
                }
                JwList.set(i, JwCallCommonCheckedList);
            }

        } catch (Exception ignored) {
        }

        jwAdapter = new JwAdapter(getContext(), JwList, name -> {
            if (isMgrUser) {
                for (int i = 0; i < JwList.size(); i++) {
                    CallCommonCheckedList item = JwList.get(i);
                    if (item.getName().equalsIgnoreCase(name)) {
                        // Get sf_type from the original JSON array
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String sfType = jsonObject.optString("sf_type", "0"); // default 0
                            // Show popup only if sf_type == "1" and not independent
                            if ("1".equalsIgnoreCase(sfType)
                                    && !item.getCode().equalsIgnoreCase(SharedPref.getSfCode(getContext()))) {
                                showNamePopup(name);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break; // found the item, stop looping
                    }
                }
            } else {
                Log.d("JointWork", "MR user clicked, no popup shown for: " + name);
            }
        });

               // if( SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")){

//                JSONArray sftype = jsonArray.put("sf_type");
//                if (sftype.equals("1")) {
//                    showNamePopup(name);
//                }
//            } else {
//                Log.d("JointWork", "MR user clicked, no popup shown for: " + name);
//            }
//        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        selectJwSideBinding.rvJwList.setLayoutManager(mLayoutManager);
        selectJwSideBinding.rvJwList.setItemAnimator(new DefaultItemAnimator());
        selectJwSideBinding.rvJwList.addItemDecoration(new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));

        selectJwSideBinding.rvJwList.setAdapter(jwAdapter);
    }

    // Method to get doc names for a specific brand
    private ArrayList<String> getAllDocNames() {
        ArrayList<String> docNames = new ArrayList<>();

        for (modelClass call : TodayCallList) {

            String rawName = call.getDocName();
            if (rawName != null && !rawName.trim().isEmpty()) {
                String cleanName = rawName.split("---")[0].trim();
                docNames.add(cleanName);
            }
        }
        return docNames;
    }


//    public void showNamePopup(String name) {
//        CallTodayCallsAPI(requireContext(), apiInterface, isProgressNeed);
//        Context context = selectJwSideBinding.getRoot().getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View dialogView = inflater.inflate(R.layout.popup_jointwork, null);
//
//        TextView tvName = dialogView.findViewById(R.id.tv_popup_name);
//        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
//        ImageView imgClose = dialogView.findViewById(R.id.img_close);
//        Button btnOk = dialogView.findViewById(R.id.btn_ok);
//
//        tvName.setText(name);
//
//        ArrayList<String> docNames = getAllDocNames(); // your list of names
//        if (docNames.isEmpty()) {
//            docNames.add("No Customers");
//        }
//        PopupNameAdapter adapter = new PopupNameAdapter(context, docNames);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        adapter.selectedNames.clear();
//        recyclerView.setAdapter(adapter);
//        int clickedIndex = -1;
//        for (int i = 0; i < JwList.size(); i++) {
//            if (JwList.get(i).getName().equalsIgnoreCase(name)) {
//                clickedIndex = i;
//                break;
//            }
//        }
//        if (clickedIndex == -1) return;
//        int finalIndex = clickedIndex;
//        AlertDialog dialog = new AlertDialog.Builder(context)
//                .setView(dialogView)
//                .setCancelable(false)
//                .create();
//        dialog.setCanceledOnTouchOutside(false);
//
//        imgClose.setOnClickListener(v -> {
//            JwList.get(finalIndex).setCheckedItem(false);
//            if (jwAdapter != null) jwAdapter.notifyItemChanged(finalIndex);
//            dialog.dismiss();
//        });
//
//        btnOk.setOnClickListener(v -> {
//            JwList.get(finalIndex).setCheckedItem(true);
//            if (jwAdapter != null) jwAdapter.notifyItemChanged(finalIndex);
//            dialog.dismiss();
//        });
//
//        dialog.show();
//    }
//
//    public static void CallTodayCallsAPI(Context context, ApiInterface apiInterface, boolean isProgressNeed) {
//        Log.e("API_TEST", "CallTodayCallsAPI entered");
//
//        if (HomeDashBoard.selectedDate != null) {
//            if (UtilityClass.isNetworkAvailable(context)) {
//                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
//                apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
//                ApiInterface finalApiInterface1 = apiInterface;

    /// /                if (isProgressNeed)
    /// /                    progressDialog = CommonUtilsMethods.createProgressDialog(context);
//                NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
//                    if (status) {
//                        SharedPref.setTodayCallList(context, "");
//                        try {
//                            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
//                            jsonObject.put("tableName", "gettodycalls");
//                            jsonObject.put("sfcode", SharedPref.getHqCode(context));
//                            jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
//                            jsonObject.put("day_flag", "0");
//                            jsonObject.put("division_code", SharedPref.getDivisionCode(context));
//                            jsonObject.put("Rsf", SharedPref.getHqCode(context));
//                            Log.v("TodayCalls", "--json--" + jsonObject);
//
//                            Map<String, String> mapString = new HashMap<>();
//                            mapString.put("axn", "table/additionaldcrmasterdata");
//                            Call<JsonElement> getTodayCalls = finalApiInterface1.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
//                            Log.e("API_TEST", "API request fired");
//
//                            ApiInterface finalApiInterface = finalApiInterface1;
//                            getTodayCalls.enqueue(new Callback<JsonElement>() {
//                                @SuppressLint("NotifyDataSetChanged")
//                                @Override
//                                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
//                                    Log.e("API_TEST", "API success: " + response.body());
//
//                                    if (response.isSuccessful()) {
//                                        try {
//                                            assert response.body() != null;
//                                            SharedPref.setTodayCallList(context, response.body().toString());
//                                            JSONArray jsonArray = new JSONArray(response.body().toString());
//                                            SharedPref.setLastCallDate(context, "");
//                                            JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
//                                            JSONArray jsonArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
//                                            ArrayList<modelClass> TodayCallListOne = new ArrayList<>();
//                                            ArrayList<modelClass> TodayCallListTwo = new ArrayList<>();
//                                            TodayCallList.clear();
//                                            for (int i = 0; i < jsonArray.length(); i++) {
//                                                JSONObject json = jsonArray.getJSONObject(i);
//                                                SharedPref.setLastCallDate(context, HomeDashBoard.selectedDate.toString());
//                                                TodayCallList.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
//                                                TodayCallListTwo.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
//
//                                                if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
//                                                    for (int j = 0; j < jsonArray1.length(); j++) {
//                                                        JSONObject jsonObject = jsonArray1.getJSONObject(j);
//                                                        if (json.optString("DCRdt").substring(0, 10).equalsIgnoreCase(jsonObject.optString("Dcr_dt")) && jsonObject.optString("CustCode").equalsIgnoreCase(json.optString("CustCode"))) {
//                                                            TodayCallListOne.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
//                                                            jsonArray1.remove(j);
//                                                            break;
//                                                        }
//                                                    }
//                                                } else {
//                                                    for (int j = 0; j < jsonArray1.length(); j++) {
//                                                        JSONObject jsonObject = jsonArray1.getJSONObject(j);
//                                                        if (json.optString("vstTime").substring(0, 10).equalsIgnoreCase(jsonObject.optString("Dcr_dt")) && jsonObject.optString("CustCode").equalsIgnoreCase(json.optString("CustCode"))) {
//                                                            TodayCallListOne.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
//                                                            jsonArray1.remove(j);
//                                                            break;
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            if (jsonArray.length() > 0) {
//
//                                                JSONArray jsonArrayWt = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
//                                                for (int i = 0; i < jsonArrayWt.length(); i++) {
//                                                    JSONObject workTypeData = jsonArrayWt.getJSONObject(i);
//                                                    if (workTypeData.optString("FWFlg").equalsIgnoreCase("F")) {
//                                                        FwFlag = workTypeData.optString("FWFlg");
//                                                    }
//                                                }
//
//                                                if (TodayCallListTwo.size() != TodayCallListOne.size()) {
//                                                    for (int i = 0; i < TodayCallListTwo.size(); i++) {
//                                                        if (TodayCallListOne.size() > 0) {
//                                                            isNeedtoAdd = true;
//                                                            for (int j = 0; j < TodayCallListOne.size(); j++) {
//                                                                if (TodayCallListTwo.get(i).getDocCode().equalsIgnoreCase(TodayCallListOne.get(j).getDocCode())) {
//                                                                    TodayCallListTwo.remove(i);
//                                                                }
//                                                            }
//                                                        } else {
//                                                            isNeedtoAdd = false;
//                                                            SaveDCRData(context, TodayCallListTwo, i, jsonArray2);
//                                                        }
//                                                    }
//                                                    if (isNeedtoAdd && TodayCallListTwo.size() > 0) {
//                                                        for (int i = 0; i < TodayCallListTwo.size(); i++) {
//                                                            SaveDCRData(context, TodayCallListTwo, i, jsonArray2);
//                                                        }
//                                                    }
//                                                }
//
//                                                MasterDataTable data = new MasterDataTable();
//                                                data.setMasterKey(Constants.CALL_SYNC);
//                                                data.setMasterValues(jsonArray2.toString());
//                                                data.setSyncStatus(0);
//                                                MasterDataTable mNChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
//                                                if (mNChecked != null) {
//                                                    masterDataDao.updateData(Constants.CALL_SYNC, jsonArray2.toString());
//                                                } else {
//                                                    masterDataDao.insert(data);
//
//                                                }
//                                                CallDataRestClass.resetcallValues(context);
//                                            }
//
//                                            jwAdapter.notifyDataSetChanged();
//                                            if (isProgressNeed) progressDialog.dismiss();
//                                            SharedPref.setLastCallSyncDate(context, HomeDashBoard.selectedDate.toString());
//                                        } catch (Exception e) {
//                                            if (isProgressNeed) progressDialog.dismiss();
//                                            Log.v("TodayCalls", "--error--" + e);
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        if (isProgressNeed) progressDialog.dismiss();
//                                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
//
//                                    if (isProgressNeed) progressDialog.dismiss();
//                                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
//                                }
//                            });
//                        } catch (Exception e) {
//                            if (isProgressNeed) progressDialog.dismiss();
//                            Log.v("TodayCalls", "--error--2--" + e);
//                        }
//                    } else {
//                        if (isProgressNeed) {
//
//                            progressDialog.dismiss();
//                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.poor_connection));
//                        }
//                    }
//                });
//                networkStatusTask.execute();
//            } else {
//
//                getFromLocal(context, apiInterface);
//            }
//        }
//    }
    public void showNamePopup(String name) {
        CallTodayCallsAPI(requireContext(), apiInterface, isProgressNeed, new TodayCallsCallback() {

            @Override
            public void onDataReady() {
                Context context = selectJwSideBinding.getRoot().getContext();
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.popup_jointwork, null);

                TextView tvName = dialogView.findViewById(R.id.tv_popup_name);
                RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
                ImageView imgClose = dialogView.findViewById(R.id.img_close);
                Button btnOk = dialogView.findViewById(R.id.btn_ok);

                tvName.setText(name);

                ArrayList<String> docNames = getAllDocNames(); // now list is populated
                if (docNames.isEmpty()) {
                    docNames.add("No Customers");
                }


                PopupNameAdapter adapter = new PopupNameAdapter(context, docNames);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                adapter.selectedNames.clear();
                recyclerView.setAdapter(adapter);
                recyclerView.post(() -> {
                    if (recyclerView.getChildCount() > 0) {
                        View firstItem = recyclerView.getChildAt(0);
                        int itemHeight = firstItem.getHeight();
                        int maxVisible = 3; // 👈 limit to 3 items
                        recyclerView.getLayoutParams().height = itemHeight * maxVisible;
                        recyclerView.requestLayout();
                    }
                });

                int clickedIndex = -1;
                for (int i = 0; i < JwList.size(); i++) {
                    if (JwList.get(i).getName().equalsIgnoreCase(name)) {
                        clickedIndex = i;
                        break;
                    }
                }
                if (clickedIndex == -1) return;
                int finalIndex = clickedIndex;

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();
                dialog.setCanceledOnTouchOutside(false);

                imgClose.setOnClickListener(v -> {
                    JwList.get(finalIndex).setCheckedItem(false);
                    if (jwAdapter != null) jwAdapter.notifyItemChanged(finalIndex);
                    dialog.dismiss();
                });

                btnOk.setOnClickListener(v -> {
                    JwList.get(finalIndex).setCheckedItem(true);
                    if (jwAdapter != null) jwAdapter.notifyItemChanged(finalIndex);
                    dialog.dismiss();
                });

                dialog.show();
            }
        });
    }

    public interface TodayCallsCallback {
        void onDataReady();
    }

    public static void CallTodayCallsAPI(Context context, ApiInterface apiInterface, boolean isProgressNeed, TodayCallsCallback callback) {
        Log.e("API_TEST", "CallTodayCallsAPI entered");

        if (HomeDashBoard.selectedDate != null) {
            if (UtilityClass.isNetworkAvailable(context)) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
                apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                ApiInterface finalApiInterface1 = apiInterface;
//                if (isProgressNeed)
//                    progressDialog = CommonUtilsMethods.createProgressDialog(context);
                NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                    if (status) {
                        SharedPref.setTodayCallList(context, "");
                        try {
                            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
                            jsonObject.put("tableName", "gettodycalls");
                            jsonObject.put("sfcode", SharedPref.getHqCode(context));
                            jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
                            jsonObject.put("day_flag", "0");
                            jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                            jsonObject.put("Rsf", SharedPref.getHqCode(context));
                            Log.v("TodayCalls", "--json--" + jsonObject);

                            Map<String, String> mapString = new HashMap<>();
                            mapString.put("axn", "table/additionaldcrmasterdata");
                            Call<JsonElement> getTodayCalls = finalApiInterface1.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                            Log.e("API_TEST", "API request fired");

                            ApiInterface finalApiInterface = finalApiInterface1;
                            getTodayCalls.enqueue(new Callback<JsonElement>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                    Log.e("API_TEST", "API success: " + response.body());

                                    if (response.isSuccessful()) {
                                        try {
                                            assert response.body() != null;
                                            SharedPref.setTodayCallList(context, response.body().toString());
                                            JSONArray jsonArray = new JSONArray(response.body().toString());
                                            SharedPref.setLastCallDate(context, "");
                                            JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
                                            JSONArray jsonArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
                                            ArrayList<modelClass> TodayCallListOne = new ArrayList<>();
                                            ArrayList<modelClass> TodayCallListTwo = new ArrayList<>();
                                            TodayCallList.clear();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject json = jsonArray.getJSONObject(i);
                                                SharedPref.setLastCallDate(context, HomeDashBoard.selectedDate.toString());
                                                TodayCallList.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
                                                TodayCallListTwo.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));

                                                if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                                        JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                                        if (json.optString("DCRdt").substring(0, 10).equalsIgnoreCase(jsonObject.optString("Dcr_dt")) && jsonObject.optString("CustCode").equalsIgnoreCase(json.optString("CustCode"))) {
                                                            TodayCallListOne.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
                                                            jsonArray1.remove(j);
                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                                        JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                                        if (json.optString("vstTime").substring(0, 10).equalsIgnoreCase(jsonObject.optString("Dcr_dt")) && jsonObject.optString("CustCode").equalsIgnoreCase(json.optString("CustCode"))) {
                                                            TodayCallListOne.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
                                                            jsonArray1.remove(j);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            if (jsonArray.length() > 0) {

                                                JSONArray jsonArrayWt = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
                                                for (int i = 0; i < jsonArrayWt.length(); i++) {
                                                    JSONObject workTypeData = jsonArrayWt.getJSONObject(i);
                                                    if (workTypeData.optString("FWFlg").equalsIgnoreCase("F")) {
                                                        FwFlag = workTypeData.optString("FWFlg");
                                                    }
                                                }

                                                if (TodayCallListTwo.size() != TodayCallListOne.size()) {
                                                    for (int i = 0; i < TodayCallListTwo.size(); i++) {
                                                        if (TodayCallListOne.size() > 0) {
                                                            isNeedtoAdd = true;
                                                            for (int j = 0; j < TodayCallListOne.size(); j++) {
                                                                if (TodayCallListTwo.get(i).getDocCode().equalsIgnoreCase(TodayCallListOne.get(j).getDocCode())) {
                                                                    TodayCallListTwo.remove(i);
                                                                }
                                                            }
                                                        } else {
                                                            isNeedtoAdd = false;
                                                            SaveDCRData(context, TodayCallListTwo, i, jsonArray2);
                                                        }
                                                    }
                                                    if (isNeedtoAdd && TodayCallListTwo.size() > 0) {
                                                        for (int i = 0; i < TodayCallListTwo.size(); i++) {
                                                            SaveDCRData(context, TodayCallListTwo, i, jsonArray2);
                                                        }
                                                    }
                                                }

                                                MasterDataTable data = new MasterDataTable();
                                                data.setMasterKey(Constants.CALL_SYNC);
                                                data.setMasterValues(jsonArray2.toString());
                                                data.setSyncStatus(0);
                                                MasterDataTable mNChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
                                                if (mNChecked != null) {
                                                    masterDataDao.updateData(Constants.CALL_SYNC, jsonArray2.toString());
                                                } else {
                                                    masterDataDao.insert(data);

                                                }
                                                CallDataRestClass.resetcallValues(context);
                                            }

                                            jwAdapter.notifyDataSetChanged();
                                            if (isProgressNeed) progressDialog.dismiss();
                                            SharedPref.setLastCallSyncDate(context, HomeDashBoard.selectedDate.toString());
                                            if (callback != null) callback.onDataReady();
                                        } catch (Exception e) {
                                            if (isProgressNeed) progressDialog.dismiss();
                                            Log.v("TodayCalls", "--error--" + e);
                                            e.printStackTrace();
                                        }
                                    } else {
                                        if (isProgressNeed) progressDialog.dismiss();
                                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network), true);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                    if (isProgressNeed) progressDialog.dismiss();
                                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network), true);
                                }
                            });
                        } catch (Exception e) {
                            if (isProgressNeed) progressDialog.dismiss();
                            Log.v("TodayCalls", "--error--2--" + e);
                        }
                    } else {
                        if (isProgressNeed) {

                            progressDialog.dismiss();
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.poor_connection), true);
                        }
                    }
                });
                networkStatusTask.execute();
            } else {

                getFromLocal(context, apiInterface);
            }
        }
    }

    private static void getFromLocal(Context context, ApiInterface apiInterface) {
        try {
            TodayCallList.clear();
            String CheckDate = "";
            boolean isDataAvailable = false;
            if (!SharedPref.getTodayCallList(context).isEmpty()) {
                JSONArray jsonArray = new JSONArray(SharedPref.getTodayCallList(context));
                if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                    CheckDate = jsonArray.getJSONObject(0).optString("DCRdt").substring(0, 10);
                } else {
                    CheckDate = jsonArray.getJSONObject(0).optString("vstTime").substring(0, 10);
                }

                if (CheckDate.equalsIgnoreCase(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))))) {
                    isDataAvailable = true;
                }

                if (isDataAvailable) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        SharedPref.setLastCallDate(context, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                        TodayCallList.add(new modelClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
                    }
                }
            }
            //binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));


            jwAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {
        }
    }

    public static void SaveDCRData(Context context, ArrayList<modelClass> todayCallListTwo, int i, JSONArray jsonArray2) {
        try {
            SharedPref.setLastCallDate(context, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CustCode", todayCallListTwo.get(i).getDocCode());
            jsonObject.put("CustType", todayCallListTwo.get(i).getDocNameID());
            jsonObject.put("Dcr_dt", todayCallListTwo.get(i).getCallsDateTime().substring(0, 10));
            jsonObject.put("month_name", CommonUtilsMethods.getCurrentInstance("MMMM"));
            jsonObject.put("Mnth", CommonUtilsMethods.getCurrentInstance("M"));
            jsonObject.put("Yr", CommonUtilsMethods.getCurrentInstance("yyyy"));
            jsonObject.put("CustName", todayCallListTwo.get(i).getDocName());
            jsonObject.put("town_code", "");
            jsonObject.put("town_name", "");
            jsonObject.put("Dcr_flag", "");
            jsonObject.put("SF_Code", SharedPref.getSfCode(context));
            jsonObject.put("Trans_SlNo", todayCallListTwo.get(i).getTrans_Slno());
            jsonObject.put("FW_Indicator", FwFlag);
            jsonObject.put("WorkType_Name", "");
            jsonObject.put("AMSLNo", todayCallListTwo.get(i).getADetSLNo());
            jsonObject.put("versionNo", context.getString(R.string.app_version));
            jsonObject.put("mod", Constants.APP_MODE);
            jsonObject.put("Device_version", Build.VERSION.RELEASE);
            jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonObject.put("AppName", context.getString(R.string.str_app_name));
            jsonObject.put("language", SharedPref.getSelectedLanguage(context));
            jsonArray2.put(jsonObject);
        } catch (Exception ignored) {

        }
    }

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<CallCommonCheckedList> selectedJwList, ArrayList<CallCommonCheckedList> Jwlist) {
        adapterCallJointWorkList = new AdapterCallJointWorkList(context, activity, selectedJwList, Jwlist);
        //  commonUtilsMethods.recycleTestWithDivider(JWOthersFragment.jwothersBinding.rvJointwork);
        JWOthersFragment.jwOthersBinding.rvJointwork.setAdapter(adapterCallJointWorkList);
    }

  /*  public static class JwAdapter extends RecyclerView.Adapter<JwAdapter.ViewHolder> {
        public static ArrayList<CallCommonCheckedList> jwLists;
        Context context;
        Activity activity;

        public JwAdapter(Context context, Activity activity, ArrayList<CallCommonCheckedList> jwLists) {
            this.context = context;
            this.activity = activity;
            JwAdapter.jwLists = jwLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data_inp, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            for (int j = 0; j < JWOthersFragment.callAddedJointList.size(); j++) {
                if (JWOthersFragment.callAddedJointList.get(j).getCode().equalsIgnoreCase(jwLists.get(position).getCode())) {
                    jwLists.set(position, new CallCommonCheckedList(jwLists.get(position).getName(), jwLists.get(position).getCode(), true));
                }
            }

            holder.tv_name.setText(jwLists.get(position).getName());
            holder.checkBox.setChecked(jwLists.get(position).isCheckedItem());

            if (holder.checkBox.isChecked()) {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                }
            } else {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                }
            }


            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (holder.checkBox.isPressed()) {
                    if (holder.checkBox.isChecked()) {
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                        }
                        jwLists.get(position).setCheckedItem(true);
                    } else {
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                        }
                        jwLists.get(position).setCheckedItem(false);
                        for (int j = 0; j < JWOthersFragment.callAddedJointList.size(); j++) {
                            if (JWOthersFragment.callAddedJointList.get(j).getCode().equalsIgnoreCase(jwLists.get(position).getCode())) {
                                JWOthersFragment.callAddedJointList.remove(j);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return jwLists.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CallCommonCheckedList> filterdNames) {
            this.jwLists = filterdNames;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;
            CheckBox checkBox;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_data_name);
                checkBox = itemView.findViewById(R.id.chk_box);
            }
        }
    }*/
}
