package saneforce.santrip.activity.call.fragments.jwOthers;

import static saneforce.santrip.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.santrip.activity.call.fragments.jwOthers.JWOthersFragment.jwOthersBinding;

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
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.FragmentSelectFbSideBinding;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;


public class FeedbackSelectionSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectFbSideBinding selectFbSideBinding;
    public static String feedbackName = "", feedbackCode = "";
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
        selectFbSideBinding = FragmentSelectFbSideBinding.inflate(inflater);
        View v = selectFbSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();

        selectFbSideBinding.tvDummy.setOnClickListener(view -> {
        });


        selectFbSideBinding.imgClose.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(selectFbSideBinding.imgClose.getWindowToken(), 0);
            selectFbSideBinding.searchList.setText("");
            dcrCallBinding.fragmentSelectFbSide.setVisibility(View.GONE);
        });

        selectFbSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

        selectFbSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
            feedbackCode = list_code.get(i);
            feedbackName = list_name.get(i);
            selectFbSideBinding.searchList.setText("");
            jwOthersBinding.tvFeedback.setText(selectFbSideBinding.selectListView.getItemAtPosition(i).toString());
            dcrCallBinding.fragmentSelectFbSide.setVisibility(View.GONE);
        });
        return v;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.FEEDBACK).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("name"));
                list_code.add(jsonObject.getString("id"));
            }
        } catch (Exception ignored) {
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectFbSideBinding.selectListView.setAdapter(dataAdapter);
    }

}
