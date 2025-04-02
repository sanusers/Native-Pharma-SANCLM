package saneforce.sanzen.activity.myresource;

//import static saneforce.sanzen.activity.call.dcrCallSelection.ChemistAddtion.chemistadditionbinding;

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
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.FragmentChemistqualificationBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class ChemistQualificationFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentChemistqualificationBinding selectchmqualiSideBinding;
    public static String qualiName = "", qualiCode = "";
    int sel_qualicode=0;
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
        selectchmqualiSideBinding = FragmentChemistqualificationBinding.inflate(inflater);
        View v = selectchmqualiSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();
        qualiName = "";
        qualiCode = "";
        sel_qualicode=0;
        selectchmqualiSideBinding.tvDummy.setOnClickListener(view -> {
        });


        selectchmqualiSideBinding.imgClose.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(selectchmqualiSideBinding.imgClose.getWindowToken(), 0);
            selectchmqualiSideBinding.searchList.setText("");
            //chemistadditionbinding.fragmentSelectChmquali.setVisibility(View.GONE);
            UtilityClass.hideKeyboard(requireActivity());
        });

        selectchmqualiSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

        selectchmqualiSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
            qualiCode = list_code.get(i);
            qualiName = list_name.get(i);
            sel_qualicode= Integer.parseInt(list_code.get(i));
            selectchmqualiSideBinding.searchList.setText("");
            SharedPref.setSelectedQualification(requireContext(),sel_qualicode);
            //chemistadditionbinding.txtSelectQua.setText(selectchmqualiSideBinding.selectListView.getItemAtPosition(i).toString());
            //chemistadditionbinding.fragmentSelectChmquali.setVisibility(View.GONE);
        });
        return v;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.QUALIFICATION).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("Name"));
                list_code.add(jsonObject.getString("Code"));
            }
        } catch (Exception ignored) {
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectchmqualiSideBinding.selectListView.setAdapter(dataAdapter);
    }

}