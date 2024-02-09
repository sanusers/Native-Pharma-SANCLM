package saneforce.santrip.activity.previewPresentation;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.Designation;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.DivCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.DrNeed;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SfCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SfType;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.StateCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SubDivisionCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SelectedTab;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.previewBinding;
import static saneforce.santrip.activity.previewPresentation.fragment.BrandMatrix.brandMatrixBinding;
import static saneforce.santrip.activity.previewPresentation.fragment.BrandMatrix.getSelectedMatrix;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.getSelectedSpec;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.specialityPreviewBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.FragmentDrSelectionSideBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class DrSelectionSide extends Fragment {
    public static ArrayList<CustList> callDrList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static FragmentDrSelectionSideBinding drSelectionSideBinding;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    ApiInterface apiInterface;
    JSONArray jsonArray;
    JSONObject jsonObject;
    SQLite sqLite;
    SelectDoctorAdapter selectDoctorAdapter;
    LoginResponse loginResponse;
    String TodayPlanSfCode;
    String brands;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        drSelectionSideBinding = FragmentDrSelectionSideBinding.inflate(inflater);
        View v = drSelectionSideBinding.getRoot();
        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetDrAdapter();

        drSelectionSideBinding.tvDummy.setOnClickListener(view -> {
        });

        drSelectionSideBinding.imgClose.setOnClickListener(v1 -> {
            drSelectionSideBinding.searchList.setText("");
            drSelectionSideBinding.selectListView.scrollToPosition(0);
            previewBinding.fragmentSelectDrSide.setVisibility(View.GONE);
        });


        drSelectionSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

        return v;
    }


    private void filter(String text) {
        ArrayList<CustList> filteredNames = new ArrayList<>();
        for (CustList s : callDrList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        selectDoctorAdapter.filterList(filteredNames);
    }

    private void SetDrAdapter() {
        try {
            loginResponse = new LoginResponse();
            loginResponse = sqLite.getLoginData();

            if (loginResponse.getSf_type().equalsIgnoreCase("1")) {
                TodayPlanSfCode = loginResponse.getSF_Code();
            } else {
                TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(requireContext());
            }

            if (!sqLite.getMasterSyncDataOfHQ(Constants.DOCTOR + TodayPlanSfCode)) {
                prepareMasterToSync(DcrCallTabLayoutActivity.TodayPlanSfCode);
            } else {
                jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + TodayPlanSfCode);
            }
            /*if (jsonArray.length() == 0) {
                commonUtilsMethods.ShowToast(context, context.getString(R.string.no_data_found) + " " + context.getString(R.string.do_master_sync), 100);
            }*/
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);

                if (!jsonObject.getString("MappProds").isEmpty() && jsonObject.getString("MappProds").contains("-")) {
                    brands = getBrands(jsonObject.getString("MappProds"));
                    callDrList.add(new CustList(jsonObject.getString("Name"),  jsonObject.getString("Specialty"),jsonObject.getString("SpecialtyCode"), brands, jsonObject.getString("MProd"), true));
                } else {
                    callDrList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), jsonObject.getString("MappProds"), jsonObject.getString("MProd"), true));
                }
            }


            selectDoctorAdapter = new SelectDoctorAdapter(requireContext(), callDrList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            drSelectionSideBinding.selectListView.setLayoutManager(mLayoutManager);
            drSelectionSideBinding.selectListView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
            drSelectionSideBinding.selectListView.setAdapter(selectDoctorAdapter);

        } catch (Exception e) {
            Log.v("dsds", "---error---" + e);
        }
    }

    public String getBrands(String mappProds) {
        String subString = "";
        String[] StrArray = mappProds.split(",");
        StringBuilder ss1 = new StringBuilder();

        for (String value : StrArray) {
            int iEnd = value.indexOf("-");
            if (iEnd != -1) {
                ss1.append(value.substring(0, iEnd));
                ss1.append(",");
            }
        }

        subString = ss1.toString();
        return subString;
    }

    public void prepareMasterToSync(String hqCode) {
        masterSyncArray.clear();

        if (DrNeed.equalsIgnoreCase("0")) {
            MasterSyncItemModel doctorModel = new MasterSyncItemModel("Doctor", "getdoctors", Constants.DOCTOR + hqCode);
            masterSyncArray.add(doctorModel);
        }
        for (int i = 0; i < masterSyncArray.size(); i++) {
            sync(masterSyncArray.get(i), hqCode);
        }
    }

    public void sync(MasterSyncItemModel masterSyncItemModel, String hqCode) {

        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", masterSyncItemModel.getRemoteTableName());
                jsonObject.put("sfcode", SfCode);
                jsonObject.put("division_code", DivCode);
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", SfType);
                jsonObject.put("Designation", Designation);
                jsonObject.put("state_code", StateCode);
                jsonObject.put("subdivision_code", SubDivisionCode);

                Call<JsonElement> call = null;
                Map<String, String> mapString = new HashMap<>();
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Doctor")) {
                    mapString.put("axn", "table/dcrmasterdata");
                    call  = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()),mapString,jsonObject.toString());
                } else if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Subordinate")) {
                    mapString.put("axn", "table/subordinates");
                    call  = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()),mapString,jsonObject.toString());
                }

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            if (response.isSuccessful()) {
                                try {
                                    JsonElement jsonElement = response.body();
                                    JSONArray jsonArray = new JSONArray();
                                    assert jsonElement != null;
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                            if (!jsonObject1.has("success")) { // json object with "success" : "fail" will be received only when api call is failed ,"success will not be received when api call is success
                                                jsonArray.put(jsonObject1);
                                                success = true;
                                            } else if (jsonObject1.has("success") && !jsonObject1.getBoolean("success")) {
                                                sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                            }
                                        }

                                        if (success) {
                                            sqLite.saveMasterSyncData(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0);
                                        }
                                    } else {
                                        sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.ShowToast(context, context.getString(R.string.no_network), 100);
        }
    }

    public static class SelectDoctorAdapter extends RecyclerView.Adapter<SelectDoctorAdapter.ViewHolder> {
        Context context;
        ArrayList<CustList> callDrList;
        SQLite sqLite;

        public SelectDoctorAdapter(Context context, ArrayList<CustList> callDrList) {
            this.context = context;
            this.callDrList = callDrList;
        }


        @NonNull
        @Override
        public SelectDoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectDoctorAdapter.ViewHolder holder, int position) {
            sqLite = new SQLite(context);
            holder.tvName.setText(callDrList.get(position).getName());

            holder.tvName.setOnClickListener(v -> {
                if (SelectedTab.equalsIgnoreCase("Spec")) {
                    specialityPreviewBinding.tvSelectDoctor.setText(callDrList.get(position).getName());
                    getSelectedSpec(context, sqLite, callDrList.get(position).getSpecialistCode(),callDrList.get(position).getSpecialist());
                } else if (SelectedTab.equalsIgnoreCase("Matrix")) {
                    brandMatrixBinding.tvSelectDoctor.setText(callDrList.get(position).getName());
                    getSelectedMatrix(context, sqLite, callDrList.get(position).getMappedBrands(), callDrList.get(position).getMappedSlides());
                }
                drSelectionSideBinding.searchList.setText("");
                drSelectionSideBinding.selectListView.scrollToPosition(0);
                previewBinding.fragmentSelectDrSide.setVisibility(View.GONE);
            });
        }

        @Override
        public int getItemCount() {
            return callDrList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CustList> filteredNames) {
            this.callDrList = filteredNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
            }
        }
    }
}
