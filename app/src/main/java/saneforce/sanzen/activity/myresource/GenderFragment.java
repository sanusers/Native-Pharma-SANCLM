package saneforce.sanzen.activity.myresource;

import static saneforce.sanzen.activity.myresource.ProfilingActivity.activityProfilingBinding;

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
import saneforce.sanzen.databinding.FragmentCategoryBinding;
import saneforce.sanzen.databinding.FragmentGenderBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class GenderFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentGenderBinding selectgenderBinding;
    public static String genderName = "";
    //int sel_categorycode=0;
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
        selectgenderBinding = FragmentGenderBinding.inflate(inflater);
        View view = selectgenderBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();
        genderName = "";
        //sel_categorycode=0;
        selectgenderBinding.tvDummy.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {}
        });


        selectgenderBinding.imgClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(selectgenderBinding.imgClose.getWindowToken(), 0);
                selectgenderBinding.searchList.setText("");
                activityProfilingBinding.fragmentSelectGender.setVisibility(View.GONE);
                UtilityClass.hideKeyboard(requireActivity());
            }
        });

        selectgenderBinding.searchList.addTextChangedListener(new TextWatcher() {
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

        selectgenderBinding.selectListView.setOnItemClickListener((adapterView, v, i, l) -> {
            genderName = list_name.get(i);
            selectgenderBinding.searchList.setText("");
            ProfilingActivity.gender= genderName;
            activityProfilingBinding.txtSelectGender.setText(selectgenderBinding.selectListView.getItemAtPosition(i).toString());
            activityProfilingBinding.fragmentSelectGender.setVisibility(View.GONE);
        });
        return view;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            list_name.add("Male");
            list_name.add("Female");

        } catch (Exception ignored) {
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectgenderBinding.selectListView.setAdapter(dataAdapter);
    }

}