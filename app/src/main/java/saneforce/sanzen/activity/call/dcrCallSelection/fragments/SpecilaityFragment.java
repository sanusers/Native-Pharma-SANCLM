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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.FragmentQualificationBinding;
import saneforce.sanzen.databinding.FragmentSpecialityBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class SpecilaityFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSpecialityBinding selectspecSideBinding;
    public static String specName = "", specCode = "";
    int sel_speccode=0;
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
        selectspecSideBinding = FragmentSpecialityBinding.inflate(inflater);
        View view = selectspecSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();
        specName = "";
        specCode = "";
        sel_speccode=0;
        selectspecSideBinding.tvDummy.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {}
        });


        selectspecSideBinding.imgClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(selectspecSideBinding.imgClose.getWindowToken(), 0);
                selectspecSideBinding.searchList.setText("");
                unlistedadditionbinding.fragmentSelectSpeciality.setVisibility(View.GONE);
                UtilityClass.hideKeyboard(requireActivity());
            }
        });

        selectspecSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

//        selectspecSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
//            specCode = list_code.get(i);
//            specName = list_name.get(i);
//            sel_speccode = Integer.parseInt(list_code.get(i));
//            selectspecSideBinding.searchList.setText("");
//            SharedPref.setSelectedSpeciality(requireContext(),sel_speccode);
//            unlistedadditionbinding.txtSelectSpec.setText(selectspecSideBinding.selectListView.getItemAtPosition(i).toString());
//            unlistedadditionbinding.fragmentSelectSpeciality.setVisibility(View.GONE);
//        });

        selectspecSideBinding.selectListView.setOnItemClickListener((adapterView, v, i, l) -> {
            String selectedName = adapterView.getItemAtPosition(i).toString();
            int originalIndex = list_name.indexOf(selectedName);
            if (originalIndex != -1) {
                specCode = list_code.get(originalIndex);
                specName = selectedName;
                sel_speccode = Integer.parseInt(specCode);
                selectspecSideBinding.searchList.setText("");
                SharedPref.setSelectedSpeciality(requireContext(), sel_speccode);
                unlistedadditionbinding.txtSelectSpec.setText(specName);
                unlistedadditionbinding.fragmentSelectSpeciality.setVisibility(View.GONE);
            }
        });

        return view;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("Name"));
                list_code.add(jsonObject.getString("Code"));
            }
        } catch (Exception ignored) {
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectspecSideBinding.selectListView.setAdapter(dataAdapter);
    }

}
