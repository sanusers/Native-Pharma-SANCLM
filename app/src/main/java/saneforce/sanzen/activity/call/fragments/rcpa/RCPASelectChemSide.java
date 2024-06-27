package saneforce.sanzen.activity.call.fragments.rcpa;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.sanzen.activity.call.DCRCallActivity.dcrCallBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentSelectChemistSideBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class RCPASelectChemSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectChemistSideBinding selectChemistSideBinding;
    ArrayList<CustList> ChemFullList = new ArrayList<>();
    JSONArray jsonArray;
    JSONObject jsonObject;
    ChemistAdapter CheAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectChemistSideBinding = FragmentSelectChemistSideBinding.inflate(inflater);
        View v = selectChemistSideBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        AddChemistData();


        selectChemistSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectChemistSideBinding.imgClose.setOnClickListener(view -> dcrCallBinding.fragmentSelectChemistSide.setVisibility(View.GONE));

        selectChemistSideBinding.searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterChe(editable.toString());
            }
        });
        return v;
    }

    private void AddChemistData() {
        try {
            ChemFullList.clear();
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + DCRCallActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
//                if(SharedPref.getGeotagNeedChe(context).equalsIgnoreCase("1")) {
//                    if(!jsonObject.getString("lat").isEmpty() && !jsonObject.getString("long").isEmpty()) {
//                        if(SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
//                            float[] distance = new float[2];
//                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
//                            if(distance[0]<DcrCallTabLayoutActivity.limitKm * 1000.0) {
////                                if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
//                                ChemFullList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code")));
////                                }
//                            }
//                        }else {
//                            float[] distance = new float[2];
//                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
//                            if(distance[0]<DcrCallTabLayoutActivity.limitKm * 1000.0) {
//                                ChemFullList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code")));
//                            }
//                        }
//                    }
//                }else {
//                    if(SharedPref.getTpbasedDcr(context).equalsIgnoreCase("0")) {
//                        if(SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
//                            ChemFullList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code")));
//                        }
//                    }else {
//                        ChemFullList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code")));
//                    }
//                }
                ChemFullList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code")));
            }

            int count = ChemFullList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (ChemFullList.get(i).getCode().equalsIgnoreCase(ChemFullList.get(j).getCode())) {
                        ChemFullList.remove(j--);
                        count--;
                    }
                }
            }

            CheAdapter = new ChemistAdapter(requireContext(), ChemFullList);
            RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(getActivity());
            selectChemistSideBinding.selectListView.setLayoutManager(mLayoutManagerChe);
            selectChemistSideBinding.selectListView.setItemAnimator(new DefaultItemAnimator());
            selectChemistSideBinding.selectListView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
            selectChemistSideBinding.selectListView.setAdapter(CheAdapter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void filterChe(String text) {
        ArrayList<CustList> filteredNames = new ArrayList<>();
        for (CustList s : ChemFullList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        CheAdapter.filterList(filteredNames);
    }

    public static class ChemistAdapter extends RecyclerView.Adapter<ChemistAdapter.ViewHolder> {
        Context context;
        ArrayList<CustList> ChemList;

        public ChemistAdapter(Context context, ArrayList<CustList> chemList) {
            this.context = context;
            this.ChemList = chemList;
        }

        @NonNull
        @Override
        public ChemistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
            return new ChemistAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChemistAdapter.ViewHolder holder, int position) {
            holder.tv_name.setText(ChemList.get(position).getName());

            holder.tv_name.setOnClickListener(view -> {
                RCPAFragment.cheName = ChemList.get(holder.getBindingAdapterPosition()).getName();
                RCPAFragment.CheCode = ChemList.get(holder.getBindingAdapterPosition()).getCode();
                RCPAFragment.rcpaBinding.tvSelectChemist.setText(ChemList.get(holder.getBindingAdapterPosition()).getName());
                RCPAFragment.rcpaBinding.tvSelectProduct.setText(context.getResources().getString(R.string.select));
                RCPAFragment.rcpaBinding.edQty.setText("0");
                RCPAFragment.rcpaBinding.tvRate.setText("");
                RCPAFragment.rcpaBinding.tvValue.setText("");
                dcrCallBinding.fragmentSelectChemistSide.setVisibility(View.GONE);
            });
        }

        @Override
        public int getItemCount() {
            return ChemList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CustList> filteredNames) {
            this.ChemList = filteredNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
            }
        }
    }
}
