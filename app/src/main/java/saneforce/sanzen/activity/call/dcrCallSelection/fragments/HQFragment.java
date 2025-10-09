package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static saneforce.sanzen.activity.call.dcrCallSelection.UnlistedDoctorAddition.unlistedadditionbinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.FragmentHqBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class HQFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentHqBinding selectHQSideBinding;
    public static String hqName = "", hqCode = "";
    int sel_hqcode = 0;
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
        selectHQSideBinding = FragmentHqBinding.inflate(inflater);
        View view = selectHQSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();
        unlistedadditionbinding.txtSelectTerritory.setText("");
        hqName = "";
        hqCode = "";
        sel_hqcode = 0;
        selectHQSideBinding.tvDummy.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {}
        });


        selectHQSideBinding.imgClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(selectHQSideBinding.imgClose.getWindowToken(), 0);
                selectHQSideBinding.searchList.setText("");
                unlistedadditionbinding.fragmentSelectHq.setVisibility(View.GONE);
                UtilityClass.hideKeyboard(requireActivity());
            }
        });

        selectHQSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

        /*selectHQSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
            hqCode = list_code.get(i);
            hqName = list_name.get(i);
            selectHQSideBinding.searchList.setText("");
            SharedPref.sethq(requireContext(),hqCode);
            loadFragment(new ClusterFragment());
            unlistedadditionbinding.txtSelectHq.setText(selectHQSideBinding.selectListView.getItemAtPosition(i).toString());
            unlistedadditionbinding.fragmentSelectHq.setVisibility(View.GONE);
        });*/
        selectHQSideBinding.selectListView.setOnItemClickListener((adapterView, v, i, l) -> {
            String selectedItem = (String) adapterView.getItemAtPosition(i);
            int originalIndex = list_name.indexOf(selectedItem);
            if(originalIndex != -1) {
                hqCode = list_code.get(originalIndex);
                hqName = list_name.get(originalIndex);
            }
            selectHQSideBinding.searchList.setText("");
            SharedPref.sethq(requireContext(), hqCode);
            loadFragment(new ClusterFragment());
            unlistedadditionbinding.txtSelectHq.setText(selectedItem);
            unlistedadditionbinding.txtSelectTerritory.setText("");
            unlistedadditionbinding.fragmentSelectHq.setVisibility(View.GONE);
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_select_cluster, fragment);  // Replace another fragment
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("name"));
                list_code.add(jsonObject.getString("id"));
            }
        } catch (Exception ignored) {
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectHQSideBinding.selectListView.setAdapter(dataAdapter);
    }

}
