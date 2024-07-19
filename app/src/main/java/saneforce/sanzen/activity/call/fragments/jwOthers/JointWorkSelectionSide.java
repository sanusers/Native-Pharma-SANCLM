package saneforce.sanzen.activity.call.fragments.jwOthers;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanzen.activity.call.DCRCallActivity.TodayPlanSfCode;
import static saneforce.sanzen.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanzen.activity.call.fragments.jwOthers.JWOthersFragment.callAddedJointList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.jwOthers.AdapterCallJointWorkList;
import saneforce.sanzen.activity.call.adapter.jwOthers.JwAdapter;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentSelectJwSideBinding;
import saneforce.sanzen.roomdatabase.DCRDocDataTableDetails.DCRDocDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;


public class JointWorkSelectionSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectJwSideBinding selectJwSideBinding;
    public static ArrayList<CallCommonCheckedList> JwList;
    @SuppressLint("StaticFieldLeak")
    public static JwAdapter jwAdapter;
    JSONArray jsonArray;
    JSONObject jsonObject;
    AdapterCallJointWorkList adapterCallJointWorkList;
    CommonUtilsMethods commonUtilsMethods;

    private RoomDB roomDB;
    private DCRDocDataDao dcrDocDataDao;
    private MasterDataDao masterDataDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectJwSideBinding = FragmentSelectJwSideBinding.inflate(inflater);
        View v = selectJwSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        dcrDocDataDao = roomDB.dcrDocDataDao();
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        SetupAdapter();

        selectJwSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectJwSideBinding.btnOk.setOnClickListener(view -> {
            for (int j = 0; j < JwList.size(); j++) {
                if (JwList.get(j).isCheckedItem()) {
                    callAddedJointList.add(new CallCommonCheckedList(JwList.get(j).getName(), JwList.get(j).getCode()));
                }
            }

            int count = callAddedJointList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (callAddedJointList.get(i).getCode().equalsIgnoreCase(callAddedJointList.get(j).getCode())) {
                        callAddedJointList.set(i, new CallCommonCheckedList(callAddedJointList.get(i).getName(), callAddedJointList.get(i).getCode()));
                        callAddedJointList.remove(j--);
                        count--;
                    } else {
                        callAddedJointList.set(i, new CallCommonCheckedList(callAddedJointList.get(i).getName(), callAddedJointList.get(i).getCode()));
                    }
                }
            }
            selectJwSideBinding.searchJw.setText("");
            dcrCallBinding.fragmentSelectJwSide.setVisibility(View.GONE);
            AssignRecyclerView(getActivity(), context, callAddedJointList, JwList);
        });

        selectJwSideBinding.imgClose.setOnClickListener(view -> {
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
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : JwList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        jwAdapter.filterList(filterdNames);
    }

    public void SetupAdapter() {
        JwList.clear();
        try {
            if(DCRCallActivity.save_valid.equals("1")){
                jsonArray = dcrDocDataDao.getDCRDocData(DCRCallActivity.hqcode).getDCRDocDataJSONArray();

                Log.d("jw_data",jsonArray.toString()+"===="+TodayPlanSfCode);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    JwList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false));
                }
            }else{
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + TodayPlanSfCode).getMasterSyncDataJsonArray();
                Log.d("jw_data",jsonArray.toString()+"===="+TodayPlanSfCode);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    JwList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false));
                }
            }
            for (int i = 0; i < JwList.size(); i++) {
                CallCommonCheckedList JwCallCommonCheckedList = JwList.get(i);
                for (CallCommonCheckedList callCommonCheckedList : callAddedJointList) {
                    if(callCommonCheckedList.getCode().equalsIgnoreCase(JwCallCommonCheckedList.getCode()) || callCommonCheckedList.getName().equalsIgnoreCase(JwCallCommonCheckedList.getName())) {
                        JwCallCommonCheckedList.setCheckedItem(true);
                        break;
                    }
                }
                JwList.set(i, JwCallCommonCheckedList);
            }

        } catch (Exception ignored) {
        }

        jwAdapter = new JwAdapter(getContext(), JwList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        selectJwSideBinding.rvJwList.setLayoutManager(mLayoutManager);
        selectJwSideBinding.rvJwList.setItemAnimator(new DefaultItemAnimator());
        selectJwSideBinding.rvJwList.addItemDecoration(new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));
        selectJwSideBinding.rvJwList.setAdapter(jwAdapter);
    }

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<CallCommonCheckedList> selectedJwList, ArrayList<CallCommonCheckedList> Jwlist) {
        adapterCallJointWorkList = new AdapterCallJointWorkList(context, activity, selectedJwList, Jwlist);
        //  commonUtilsMethods.recycleTestWithDivider(JWOthersFragment.jwothersBinding.rvJointwork);
        JWOthersFragment.jwOthersBinding.rvJointwork.setAdapter(adapterCallJointWorkList);
    }

  /*  public static class JwAdapter extends RecyclerView.Adapter<JwAdapter.ViewHolder> {
        public static ArrayList<CallCommonCheckedList> jwLists;
        Context context;
        Activity activity;

        public JwAdapter(Context context, Activity activity, ArrayList<CallCommonCheckedList> jwLists) {
            this.context = context;
            this.activity = activity;
            JwAdapter.jwLists = jwLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data_inp, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            for (int j = 0; j < JWOthersFragment.callAddedJointList.size(); j++) {
                if (JWOthersFragment.callAddedJointList.get(j).getCode().equalsIgnoreCase(jwLists.get(position).getCode())) {
                    jwLists.set(position, new CallCommonCheckedList(jwLists.get(position).getName(), jwLists.get(position).getCode(), true));
                }
            }

            holder.tv_name.setText(jwLists.get(position).getName());
            holder.checkBox.setChecked(jwLists.get(position).isCheckedItem());

            if (holder.checkBox.isChecked()) {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                }
            } else {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                }
            }


            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (holder.checkBox.isPressed()) {
                    if (holder.checkBox.isChecked()) {
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                        }
                        jwLists.get(position).setCheckedItem(true);
                    } else {
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                        }
                        jwLists.get(position).setCheckedItem(false);
                        for (int j = 0; j < JWOthersFragment.callAddedJointList.size(); j++) {
                            if (JWOthersFragment.callAddedJointList.get(j).getCode().equalsIgnoreCase(jwLists.get(position).getCode())) {
                                JWOthersFragment.callAddedJointList.remove(j);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return jwLists.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CallCommonCheckedList> filterdNames) {
            this.jwLists = filterdNames;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;
            CheckBox checkBox;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_data_name);
                checkBox = itemView.findViewById(R.id.chk_box);
            }
        }
    }*/
}
