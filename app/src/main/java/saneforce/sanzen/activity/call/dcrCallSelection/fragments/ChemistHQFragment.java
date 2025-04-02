package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import static saneforce.sanzen.activity.call.dcrCallSelection.ChemistAddition.chemistadditionbinding;
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
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.FragmentChemisthqBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class ChemistHQFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentChemisthqBinding selectchmHQSideBinding;
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
        selectchmHQSideBinding = FragmentChemisthqBinding.inflate(inflater);
        View v = selectchmHQSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();
        chemistadditionbinding.txtSelectTerritory.setText("");
        hqName = "";
        hqCode = "";
        sel_hqcode = 0;
        selectchmHQSideBinding.tvDummy.setOnClickListener(view -> {
        });


        selectchmHQSideBinding.imgClose.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(selectchmHQSideBinding.imgClose.getWindowToken(), 0);
            selectchmHQSideBinding.searchList.setText("");
            chemistadditionbinding.fragmentSelectChemisthq.setVisibility(View.GONE);
            UtilityClass.hideKeyboard(requireActivity());
        });

        selectchmHQSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

        /*selectchmHQSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
            hqCode = list_code.get(i);
            hqName = list_name.get(i);
            selectchmHQSideBinding.searchList.setText("");
            SharedPref.sethq(requireContext(),hqCode);
            loadFragment(new ChemistClusterFragment());
            chemistadditionbinding.txtSelectHq.setText(selectchmHQSideBinding.selectListView.getItemAtPosition(i).toString());
            chemistadditionbinding.fragmentSelectChemisthq.setVisibility(View.GONE);
        });*/
        selectchmHQSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
// Get selected item from the filtered list
            String selectedItem = (String) adapterView.getItemAtPosition(i);
// Find the correct index in the original list
            int originalIndex = list_name.indexOf(selectedItem);
            if(originalIndex != -1) {
                hqCode = list_code.get(originalIndex);
                hqName = list_name.get(originalIndex);
            }
            selectchmHQSideBinding.searchList.setText("");
            SharedPref.sethq(requireContext(), hqCode);
            loadFragment(new ChemistClusterFragment());
            chemistadditionbinding.txtSelectHq.setText(selectedItem);
            chemistadditionbinding.txtSelectTerritory.setText("");
            chemistadditionbinding.fragmentSelectChemisthq.setVisibility(View.GONE);
        });
        return v;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_select_chmcluster, fragment);  // Replace another fragment
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
        selectchmHQSideBinding.selectListView.setAdapter(dataAdapter);
    }

}
