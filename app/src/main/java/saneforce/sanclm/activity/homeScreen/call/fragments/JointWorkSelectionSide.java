package saneforce.sanclm.activity.homeScreen.call.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers.AdapterCallJointWorkList;
import saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers.JwAdapter;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.FragmentSelectJwSideBinding;
import saneforce.sanclm.storage.SQLite;

public class JointWorkSelectionSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectJwSideBinding selectJwSideBinding;
    public static ArrayList<CallCommonCheckedList> JwList;
    @SuppressLint("StaticFieldLeak")
    public static JwAdapter jwAdapter;
    SQLite sqLite;
    JSONArray jsonArray;
    JSONObject jsonObject;
    AdapterCallJointWorkList adapterCallJointWorkList;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectJwSideBinding = FragmentSelectJwSideBinding.inflate(inflater);
        View v = selectJwSideBinding.getRoot();
        sqLite = new SQLite(getContext());
        commonUtilsMethods = new CommonUtilsMethods(getActivity());

        SetupAdapter();

        selectJwSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectJwSideBinding.btnOk.setOnClickListener(view -> {
            for (int j = 0; j < JwList.size(); j++) {
                if (JwList.get(j).isCheckedItem()) {
                    JWOthersFragment.callAddedJointList.add(new CallCommonCheckedList(JwList.get(j).getName(), JwList.get(j).getCode()));
                }
            }

            int count = JWOthersFragment.callAddedJointList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (JWOthersFragment.callAddedJointList.get(i).getCode().equalsIgnoreCase(JWOthersFragment.callAddedJointList.get(j).getCode())) {
                        JWOthersFragment.callAddedJointList.set(i, new CallCommonCheckedList(JWOthersFragment.callAddedJointList.get(i).getName(), JWOthersFragment.callAddedJointList.get(i).getCode()));
                        JWOthersFragment.callAddedJointList.remove(j--);
                        count--;
                    } else {
                        JWOthersFragment.callAddedJointList.set(i, new CallCommonCheckedList(JWOthersFragment.callAddedJointList.get(i).getName(), JWOthersFragment.callAddedJointList.get(i).getCode()));
                    }
                }
            }
            selectJwSideBinding.searchJw.setText("");
            dcrCallBinding.fragmentSelectJwSide.setVisibility(View.GONE);
            AssignRecyclerView(getActivity(), context, JWOthersFragment.callAddedJointList, JwList);
        });

        selectJwSideBinding.imgClose.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(selectJwSideBinding.imgClose.getWindowToken(), 0);
            SetupAdapter();
            selectJwSideBinding.searchJw.setText("");
            dcrCallBinding.fragmentSelectJwSide.setVisibility(View.GONE);
        });


        selectJwSideBinding.searchJw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        return v;
    }


    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filteredNames = new ArrayList<>();
        for (CallCommonCheckedList s : JwList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        jwAdapter.filterList(filteredNames);
    }

    public void SetupAdapter() {
        JwList.clear();
        try {
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK + TodayPlanSfCode);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                JwList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jwAdapter = new JwAdapter(getContext(), JwList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        selectJwSideBinding.rvJwList.setLayoutManager(mLayoutManager);
        selectJwSideBinding.rvJwList.setItemAnimator(new DefaultItemAnimator());
        selectJwSideBinding.rvJwList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        selectJwSideBinding.rvJwList.setAdapter(jwAdapter);
    }

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<CallCommonCheckedList> selectedJwList, ArrayList<CallCommonCheckedList> JwList) {
        adapterCallJointWorkList = new AdapterCallJointWorkList(context, activity, selectedJwList, JwList);
        JWOthersFragment.jwOthersBinding.rvJointwork.setAdapter(adapterCallJointWorkList);
    }
}
