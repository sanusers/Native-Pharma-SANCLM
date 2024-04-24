package saneforce.santrip.activity.previewPresentation.fragment;

import static saneforce.santrip.activity.previewPresentation.PreviewActivity.previewBinding;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.getRequiredData;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.getSelectedSpec;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.FragmentSelectFbSideBinding;
import saneforce.santrip.response.SetupResponse;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SQLite;

public class SpecialitySelectionSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectFbSideBinding selectSpecialitySideBinding;
//    SQLite sqLite;
    JSONObject jsonObject;
    ArrayList<String> list_name = new ArrayList<>();
    ArrayList<String> list_code = new ArrayList<>();
    ArrayAdapter<String> dataAdapter;
    SetupResponse setUpResponse;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectSpecialitySideBinding = FragmentSelectFbSideBinding.inflate(inflater);
        View v = selectSpecialitySideBinding.getRoot();
//        sqLite = new SQLite(getContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();

        selectSpecialitySideBinding.tvTagHeader.setText(requireContext().getString(R.string.speciality_selection));
        selectSpecialitySideBinding.tvDummy.setOnClickListener(view -> {
        });


        selectSpecialitySideBinding.imgClose.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(selectSpecialitySideBinding.imgClose.getWindowToken(), 0);
            selectSpecialitySideBinding.searchList.setText("");
            previewBinding.btnFinishDet.setVisibility(View.VISIBLE);
            previewBinding.fragmentSelectSpecialistSide.setVisibility(View.GONE);
        });

        selectSpecialitySideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

        selectSpecialitySideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectSpecialitySideBinding.searchList.setText("");
            if (list_name.get(i).equalsIgnoreCase("All") && list_code.get(i).isEmpty()) {
                getRequiredData(requireContext(), list_name.get(i), masterDataDao);
            } else {
                getSelectedSpec(requireContext(), list_code.get(i), list_name.get(i), masterDataDao);
            }
            previewBinding.btnFinishDet.setVisibility(View.VISIBLE);
            previewBinding.fragmentSelectSpecialistSide.setVisibility(View.GONE);
        });
        return v;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            JSONArray jsonArray;
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SETUP).getMasterSyncDataJsonArray();
//            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);
                setUpResponse = new SetupResponse();
                Type typeSetup = new TypeToken<SetupResponse>() {
                }.getType();
                setUpResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);

                if (setUpResponse.getPresentationSpecFilter().equalsIgnoreCase("1")) {
                    list_code.add("");
                    list_name.add("All");
                }
            }

            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
//            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SPECIALITY);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("Doc_Special_Name"));
                list_code.add(jsonObject.getString("Code"));
            }
        } catch (Exception ignored) {
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectSpecialitySideBinding.selectListView.setAdapter(dataAdapter);
    }
}
