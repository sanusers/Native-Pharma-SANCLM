package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static saneforce.sanzen.activity.call.dcrCallSelection.UnlistedDoctorAddition.unlistedadditionbinding;

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
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
//import saneforce.sanzen.databinding.FragmentCategoryBinding;
import saneforce.sanzen.databinding.FragmentClusterBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class ClusterFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentClusterBinding selectclusterSideBinding;
    public static String clusterName = "", clusterCode = "";
    int sel_clustercode=0;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ArrayList<String> list_name = new ArrayList<>();
    ArrayList<String> list_code = new ArrayList<>();
    ArrayAdapter<String> dataAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectclusterSideBinding = FragmentClusterBinding.inflate(inflater);
        View v = selectclusterSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();
        clusterName = "";
        clusterCode = "";
        sel_clustercode=0;
        selectclusterSideBinding.tvDummy.setOnClickListener(view -> {
        });


        selectclusterSideBinding.imgClose.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(selectclusterSideBinding.imgClose.getWindowToken(), 0);
            selectclusterSideBinding.searchList.setText("");
            unlistedadditionbinding.fragmentSelectCluster.setVisibility(View.GONE);
            UtilityClass.hideKeyboard(requireActivity());
        });

        selectclusterSideBinding.searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dataAdapter.getFilter().filter(editable.toString());
            }
        });

//        selectclusterSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
//            clusterCode = list_code.get(i);
//            clusterName = list_name.get(i);
//            selectclusterSideBinding.searchList.setText("");
//            sel_clustercode = Integer.parseInt(list_code.get(i));
//            SharedPref.setSelectedCluster(requireContext(),sel_clustercode);
//            unlistedadditionbinding.txtSelectTerritory.setText(selectclusterSideBinding.selectListView.getItemAtPosition(i).toString());
//            unlistedadditionbinding.fragmentSelectCluster.setVisibility(View.GONE);
//        });
        selectclusterSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedName = adapterView.getItemAtPosition(i).toString();
            int originalIndex = list_name.indexOf(selectedName);
            if (originalIndex != -1) {
                clusterCode = list_code.get(originalIndex);
                clusterName = selectedName;
                sel_clustercode = Integer.parseInt(clusterCode);
                selectclusterSideBinding.searchList.setText("");
                SharedPref.setSelectedCluster(requireContext(), sel_clustercode);
                unlistedadditionbinding.txtSelectTerritory.setText(clusterName);
                unlistedadditionbinding.fragmentSelectCluster.setVisibility(View.GONE);
            }
        });
        return v;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            if(SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")){
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            }
            else {
                Log.v("sleectedhq", SharedPref.getHq(requireContext()));
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getHq(requireContext())).getMasterSyncDataJsonArray();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("Name"));
                list_code.add(jsonObject.getString("Code"));
            }
        } catch (Exception ignored) {
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectclusterSideBinding.selectListView.setAdapter(dataAdapter);
    }

}
