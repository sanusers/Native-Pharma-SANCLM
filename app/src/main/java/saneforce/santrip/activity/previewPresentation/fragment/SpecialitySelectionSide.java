package saneforce.santrip.activity.previewPresentation.fragment;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.santrip.activity.homeScreen.call.fragments.JWOthersFragment.jwOthersBinding;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SpecialityCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SpecialityName;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.previewBinding;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.getRequiredData;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.getSelectedSpec;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.specialityPreviewBinding;

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

import saneforce.santrip.R;
import saneforce.santrip.activity.previewPresentation.PreviewActivity;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.FragmentSelectFbSideBinding;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;

public class SpecialitySelectionSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectFbSideBinding selectSpecialitySideBinding;
    SQLite sqLite;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ArrayList<String> list_name = new ArrayList<>();
    ArrayList<String> list_code = new ArrayList<>();
    ArrayAdapter<String> dataAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectSpecialitySideBinding = FragmentSelectFbSideBinding.inflate(inflater);
        View v = selectSpecialitySideBinding.getRoot();
        sqLite = new SQLite(getContext());

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
                getRequiredData(requireContext(),sqLite);
            } else {
                getSelectedSpec(requireContext(), sqLite, list_code.get(i), list_name.get(i));
            }
            previewBinding.btnFinishDet.setVisibility(View.VISIBLE);
            previewBinding.fragmentSelectSpecialistSide.setVisibility(View.GONE);
        });
        return v;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
       /* LoginResponse loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();
        if (loginResponse.getsp)*/

            list_code.add("");
            list_name.add("All");

        try {
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SPECIALITY);
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
