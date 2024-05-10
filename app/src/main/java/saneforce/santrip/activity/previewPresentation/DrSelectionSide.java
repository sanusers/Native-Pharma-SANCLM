package saneforce.santrip.activity.previewPresentation;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

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
import android.view.inputmethod.InputMethodManager;
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
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.activity.masterSync.MasterSyncItemModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.FragmentDrSelectionSideBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SharedPref;

public class DrSelectionSide extends Fragment {
    public static ArrayList<CustList> callDrListBrand = new ArrayList<>();
    public static ArrayList<CustList> callDrListSpeciality = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static FragmentDrSelectionSideBinding drSelectionSideBinding;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    ApiInterface apiInterface;
    JSONArray jsonArray;
    JSONObject jsonObject;
//    SQLite sqLite;
    SelectDoctorAdapter selectDoctorAdapter;

    String TodayPlanSfCode;
    String brands;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        drSelectionSideBinding = FragmentDrSelectionSideBinding.inflate(inflater);
        View v = drSelectionSideBinding.getRoot();
//        sqLite = new SQLite(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetDrAdapter();

        drSelectionSideBinding.tvDummy.setOnClickListener(view -> {
        });

        drSelectionSideBinding.imgClose.setOnClickListener(v1 -> {
            hideKeyboard();
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
                try {
                    filter(s.toString());
                } catch (Exception ignored) {

                }
            }
        });
        return v;
    }


    private void filter(String text) {
        ArrayList<CustList> filteredNames = new ArrayList<>();
        if (SelectedTab.equalsIgnoreCase("Spec")) {
            for (CustList s : callDrListBrand) {
                if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                    filteredNames.add(s);
                }
            }
        } else if (SelectedTab.equalsIgnoreCase("Matrix")) {
            for (CustList s : callDrListBrand) {
                if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredNames.add(s);
                }
            }
        }
        selectDoctorAdapter.filterList(filteredNames);
    }

    public void SetDrAdapter() {
        try {
            callDrListBrand.clear();
            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) {
                TodayPlanSfCode = SharedPref.getSfCode(requireContext());
            } else {
                if (SharedPref.getTodayDayPlanSfCode(requireContext()).isEmpty()) {
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
//                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
                    for (int i = 0; i < 1; i++) {
                        JSONObject jsonHQList = jsonArray.getJSONObject(0);
                        TodayPlanSfCode = jsonHQList.getString("id");
                    }
                } else {
                    TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(requireContext());
                }
            }

            if (!masterDataDao.getMasterSyncDataOfHQ(Constants.DOCTOR + TodayPlanSfCode)) {
//            if (!sqLite.getMasterSyncDataOfHQ(Constants.DOCTOR + TodayPlanSfCode)) {
                prepareMasterToSync(TodayPlanSfCode);
            } else {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + TodayPlanSfCode).getMasterSyncDataJsonArray();
//                jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + TodayPlanSfCode);
            }

          /*  if (jsonArray.length() == 0) {
                JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
                for (int i = 0; i < 1; i++) {
                    JSONObject jsonHQList = jsonArray.getJSONObject(0);
                    TodayPlanSfCode = jsonHQList.getString("id");
                }
            }*/

            /*if (jsonArray.length() == 0) {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_data_found) + " " + context.getString(R.string.do_master_sync));
            }*/

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if (!jsonObject.getString("MappProds").isEmpty() && jsonObject.getString("MappProds").contains("-") && !jsonObject.getString("MProd").isEmpty()) {
                    brands = getBrands(jsonObject.getString("MappProds"));
                    callDrListBrand.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), brands, jsonObject.getString("MProd"), true));
                } else {
                    callDrListBrand.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Specialty"), jsonObject.getString("SpecialtyCode"), "", "", false));
                }
            }
            selectDoctorAdapter = new SelectDoctorAdapter(requireContext(), callDrListBrand);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            drSelectionSideBinding.selectListView.setLayoutManager(mLayoutManager);
            drSelectionSideBinding.selectListView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
            drSelectionSideBinding.selectListView.setAdapter(selectDoctorAdapter);

        } catch (Exception e) {
            Log.v("dsds", "---error---" + e + "----" + SelectedTab);
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

        if (SharedPref.getDrNeed(requireContext()).equalsIgnoreCase("0")) {
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
                jsonObject.put("sfcode", SharedPref.getSfCode(context));
                jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", SharedPref.getSfType(context));
                jsonObject.put("Designation", SharedPref.getDesig(context));
                jsonObject.put("state_code", SharedPref.getStateCode(context));
                jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(context));

                Call<JsonElement> call = null;
                Map<String, String> mapString = new HashMap<>();
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Doctor")) {
                    mapString.put("axn", "table/dcrmasterdata");
                    call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                } else if (masterSyncItemModel.getMasterOf().equalsIgnoreCase("Subordinate")) {
                    mapString.put("axn", "table/subordinates");
                    call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
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
                                                masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
//                                                sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                            }
                                        }

                                        if (success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0));
//                                            sqLite.saveMasterSyncData(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0);
                                        }
                                    } else {
                                        masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
//                                        sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
//                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(drSelectionSideBinding.getRoot().getWindowToken(), 0);
    }

    public static class SelectDoctorAdapter extends RecyclerView.Adapter<SelectDoctorAdapter.ViewHolder> {
        Context context;
        ArrayList<CustList> callDrList;
//        SQLite sqLite;
        private RoomDB roomDB;
        private MasterDataDao masterDataDao;

        public SelectDoctorAdapter(Context context, ArrayList<CustList> callDrList) {
            this.context = context;
            this.callDrList = callDrList;
            roomDB = RoomDB.getDatabase(context);
            masterDataDao = roomDB.masterDataDao();
        }

        @NonNull
        @Override
        public SelectDoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_select_dr, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectDoctorAdapter.ViewHolder holder, int position) {
//            sqLite = new SQLite(context);
            holder.tvName.setText(callDrList.get(position).getName());

            if (SelectedTab.equalsIgnoreCase("Spec")) {
                holder.tvSpeciality.setVisibility(View.VISIBLE);
                holder.tvSpeciality.setText(callDrList.get(position).getSpecialist());
                holder.tvBrandAvailable.setVisibility(View.GONE);
            } else if (SelectedTab.equalsIgnoreCase("Matrix")) {
                holder.tvSpeciality.setVisibility(View.GONE);
                if (callDrList.get(position).isExtra()) {
                    holder.tvBrandAvailable.setVisibility(View.VISIBLE);
                } else {
                    holder.tvBrandAvailable.setVisibility(View.GONE);
                }
            }


            holder.tvName.setOnClickListener(v -> {
                if (SelectedTab.equalsIgnoreCase("Spec")) {
                    specialityPreviewBinding.tvSelectDoctor.setText(String.format("%s - %s", callDrList.get(position).getName(), callDrList.get(position).getSpecialist()));
                    getSelectedSpec(context, callDrList.get(position).getSpecialistCode(), callDrList.get(position).getSpecialist(), masterDataDao);
                } else if (SelectedTab.equalsIgnoreCase("Matrix")) {
                    brandMatrixBinding.tvSelectDoctor.setText(callDrList.get(position).getName());
                    getSelectedMatrix(context, callDrList.get(position).getMappedBrands(), callDrList.get(position).getMappedSlides(), masterDataDao);
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
            TextView tvName, tvSpeciality, tvBrandAvailable;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                tvSpeciality = itemView.findViewById(R.id.tv_speciality);
                tvBrandAvailable = itemView.findViewById(R.id.tv_brandAvailable);
            }
        }
    }
}
