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
import saneforce.sanzen.databinding.FragmentCategoryResourceBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class CategoryFragment extends Fragment {
    public FragmentCategoryResourceBinding selectcatSideBinding;
    public static String categoryName = "", categoryCode = "";
    int sel_categorycode=0;
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
        selectcatSideBinding = FragmentCategoryResourceBinding.inflate(inflater);
        View view = selectcatSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();
        categoryName = "";
        categoryCode = "";
        sel_categorycode=0;
        selectcatSideBinding.tvDummy.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {}
        });


        selectcatSideBinding.imgClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(selectcatSideBinding.imgClose.getWindowToken(), 0);
                selectcatSideBinding.searchList.setText("");
                activityProfilingBinding.fragmentSelectCat.setVisibility(View.GONE);
                UtilityClass.hideKeyboard(requireActivity());
            }
        });

        selectcatSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

        selectcatSideBinding.selectListView.setOnItemClickListener((adapterView, v, i, l) -> {
            categoryCode = list_code.get(i);
            categoryName = list_name.get(i);
            sel_categorycode= Integer.parseInt(list_code.get(i));
            selectcatSideBinding.searchList.setText("");
            SharedPref.setSelectedCategory(requireContext(),sel_categorycode);
            ProfilingActivity.cate_code= String.valueOf(sel_categorycode);
            activityProfilingBinding.txtSelectCategory.setText(selectcatSideBinding.selectListView.getItemAtPosition(i).toString());
            activityProfilingBinding.fragmentSelectCat.setVisibility(View.GONE);
        });
        return view;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("Name"));
                list_code.add(jsonObject.getString("Code"));
            }
        } catch (Exception ignored) {
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectcatSideBinding.selectListView.setAdapter(dataAdapter);
    }

}