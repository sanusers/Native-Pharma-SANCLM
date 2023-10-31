package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrcallBinding;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment.jwothersBinding;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.FragmentSelectFbSideBinding;
import saneforce.sanclm.storage.SQLite;

public class FeedbackSelectionSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectFbSideBinding selectFbSideBinding;
    public static String feedbackName = "", feedbackCode = "";
    SQLite sqLite;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ArrayList<String> list_name = new ArrayList<>();
    ArrayList<String> list_code = new ArrayList<>();
    ArrayAdapter<String> dataAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectFbSideBinding = FragmentSelectFbSideBinding.inflate(inflater);
        View v = selectFbSideBinding.getRoot();
        sqLite = new SQLite(getContext());

        SetupAdapter();

        selectFbSideBinding.tvDummy.setOnClickListener(view -> {
        });


        selectFbSideBinding.imgClose.setOnClickListener(view -> {
            selectFbSideBinding.searchList.setText("");
            dcrcallBinding.fragmentSelectFbSide.setVisibility(View.GONE);
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
            jwothersBinding.tvFeedback.setText(selectFbSideBinding.selectListView.getItemAtPosition(i).toString());
            dcrcallBinding.fragmentSelectFbSide.setVisibility(View.GONE);
        });
        return v;
    }


    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.FEEDBACK);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("name"));
                list_code.add(jsonObject.getString("id"));
            }
        } catch (Exception e) {

        }
        dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview_items, list_name);
        selectFbSideBinding.selectListView.setAdapter(dataAdapter);
    }

}
