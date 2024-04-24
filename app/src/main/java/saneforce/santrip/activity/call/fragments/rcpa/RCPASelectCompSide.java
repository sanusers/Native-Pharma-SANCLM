package saneforce.santrip.activity.call.fragments.rcpa;

import static saneforce.santrip.activity.call.DCRCallActivity.dcrCallBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.adapter.rcpa.RCPAChemistAdapter;
import saneforce.santrip.activity.call.DCRCallActivity;
import saneforce.santrip.activity.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.santrip.activity.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.FragmentAddRcpaSideBinding;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SQLite;

public class RCPASelectCompSide extends Fragment {
    public static ArrayList<RCPAAddedProdList> rcpaAddedProdListArrayList;
    public static ArrayList<RCPAAddedCompList> rcpa_comp_list;
    @SuppressLint("StaticFieldLeak")
    public static FragmentAddRcpaSideBinding rcpaSideBinding;
    public static ArrayList<RCPAAddedCompList> addCompList = new ArrayList<>();
    public static ArrayList<RCPAAddedCompList> addCompListDummy = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static AdapterCompetitorPrd adapterCompetitorPrd;
    @SuppressLint("StaticFieldLeak")
    public static RCPAChemistAdapter rcpaChemistAdapter;
    CommonUtilsMethods commonUtilsMethods;
    SQLite sqLite;
    double getTotalValue = 0, valueRounded;
    ArrayList<Double> CompQty = new ArrayList<>();
    JSONArray jsonArray;
    JSONObject jsonObject, jsonObject1;
    ArrayList<String> SelectedChk = new ArrayList<>();

    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @SuppressLint({"ResourceType", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rcpaSideBinding = FragmentAddRcpaSideBinding.inflate(getLayoutInflater());
        View v = rcpaSideBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        sqLite = new SQLite(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        SetupAdapter();

        if (DCRCallActivity.RcpaCompetitorAdd.equalsIgnoreCase("0")) {
            rcpaSideBinding.imgAddComp.setVisibility(View.VISIBLE);
        } else {
            rcpaSideBinding.imgAddComp.setVisibility(View.GONE);
        }

        rcpaSideBinding.tvDummy.setOnClickListener(view -> {
        });

        rcpaSideBinding.imgClose.setOnClickListener(view -> {
            if (rcpaSideBinding.constraintPreviewCompList.getVisibility() == View.VISIBLE) {
                dcrCallBinding.fragmentAddRcpaSide.setVisibility(View.GONE);
            } else {
                rcpaSideBinding.constraintPreviewCompList.setVisibility(View.VISIBLE);
                rcpaSideBinding.constraintAddCompList.setVisibility(View.GONE);
            }
        });

        rcpaSideBinding.searchCompPrd.addTextChangedListener(new TextWatcher() {
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


        rcpaSideBinding.btnClear.setOnClickListener(view -> {
            for (int i = 0; i < AdapterCompetitorPrd.CompetitorList.size(); i++) {
                AdapterCompetitorPrd.CompetitorList.set(i, new RCPAAddedCompList(AdapterCompetitorPrd.CompetitorList.get(i).getPrd_name(), AdapterCompetitorPrd.CompetitorList.get(i).getPrd_code(), AdapterCompetitorPrd.CompetitorList.get(i).getChem_names(), AdapterCompetitorPrd.CompetitorList.get(i).getChem_Code(), AdapterCompetitorPrd.CompetitorList.get(i).getComp_company_name(), AdapterCompetitorPrd.CompetitorList.get(i).getComp_company_code(), AdapterCompetitorPrd.CompetitorList.get(i).getComp_product(), AdapterCompetitorPrd.CompetitorList.get(i).getComp_product_code(), AdapterCompetitorPrd.CompetitorList.get(i).getRate(), false, AdapterCompetitorPrd.CompetitorList.get(i).getTotalPrdValue()));
            }
            adapterCompetitorPrd.notifyDataSetChanged();
        });

        rcpaSideBinding.btnSave.setOnClickListener(view -> {
            SelectedChk.clear();
            for (int i = 0; i < AdapterCompetitorPrd.CompetitorList.size(); i++) {
                if (AdapterCompetitorPrd.CompetitorList.get(i).isSelected()) {
                    SelectedChk.add(AdapterCompetitorPrd.CompetitorList.get(i).getChem_names());
                    rcpa_comp_list.add(new RCPAAddedCompList(AdapterCompetitorPrd.CompetitorList.get(i).getChem_names(), AdapterCompetitorPrd.CompetitorList.get(i).getChem_Code(), AdapterCompetitorPrd.CompetitorList.get(i).getPrd_name(), AdapterCompetitorPrd.CompetitorList.get(i).getPrd_code(), AdapterCompetitorPrd.CompetitorList.get(i).getComp_company_name(), AdapterCompetitorPrd.CompetitorList.get(i).getComp_company_code(), AdapterCompetitorPrd.CompetitorList.get(i).getComp_product(), AdapterCompetitorPrd.CompetitorList.get(i).getComp_product_code(), "1", AdapterCompetitorPrd.CompetitorList.get(i).getRate(), AdapterCompetitorPrd.CompetitorList.get(i).getRate(), "", AdapterCompetitorPrd.CompetitorList.get(i).getTotalPrdValue()));
                }
            }

            if (SelectedChk.size() > 0) {
                for (int j = 0; j < RCPAFragment.ProductSelectedList.size(); j++) {
                    CompQty = new ArrayList<>();
                    for (int i = 0; i < rcpa_comp_list.size(); i++) {
                        if (rcpa_comp_list.get(i).getChem_Code().equalsIgnoreCase(RCPAFragment.ProductSelectedList.get(j).getChe_codes()) && rcpa_comp_list.get(i).getPrd_code().equalsIgnoreCase(RCPAFragment.ProductSelectedList.get(j).getPrd_code())) {
                            if (!rcpa_comp_list.get(i).getValue().isEmpty()) {
                                getTotalValue = Double.parseDouble(RCPAFragment.ProductSelectedList.get(j).getValue());
                                CompQty.add(Double.parseDouble(rcpa_comp_list.get(i).getValue()));
                            }
                        }
                    }

                    if (CompQty.size() > 0) {
                        for (int i = 0; i < CompQty.size(); i++) {
                            getTotalValue = getTotalValue + CompQty.get(i);
                        }
                        valueRounded = Math.round(getTotalValue * 100D) / 100D;
                        RCPAFragment.ProductSelectedList.set(j, new RCPAAddedProdList(RCPAFragment.ProductSelectedList.get(j).getChem_names(), RCPAFragment.ProductSelectedList.get(j).getChe_codes(), RCPAFragment.ProductSelectedList.get(j).getPrd_name(), RCPAFragment.ProductSelectedList.get(j).getPrd_code(), RCPAFragment.ProductSelectedList.get(j).getQty(), RCPAFragment.ProductSelectedList.get(j).getRate(), RCPAFragment.ProductSelectedList.get(j).getValue(), String.valueOf(valueRounded)));
                    } else {
                        RCPAFragment.ProductSelectedList.set(j, new RCPAAddedProdList(RCPAFragment.ProductSelectedList.get(j).getChem_names(), RCPAFragment.ProductSelectedList.get(j).getChe_codes(), RCPAFragment.ProductSelectedList.get(j).getPrd_name(), RCPAFragment.ProductSelectedList.get(j).getPrd_code(), RCPAFragment.ProductSelectedList.get(j).getQty(), RCPAFragment.ProductSelectedList.get(j).getRate(), RCPAFragment.ProductSelectedList.get(j).getValue(), RCPAFragment.ProductSelectedList.get(j).getValue()));
                    }
                }


                for (int j = 0; j < RCPAFragment.ChemistSelectedList.size(); j++) {
                    CompQty = new ArrayList<>();
                    for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
                        if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(RCPAFragment.ChemistSelectedList.get(j).getCode())) {
                            if (!RCPAFragment.ProductSelectedList.get(i).getTotalPrdValue().isEmpty()) {
                                getTotalValue = 0.0;
                                CompQty.add(Double.parseDouble(RCPAFragment.ProductSelectedList.get(i).getTotalPrdValue()));
                            }
                        }
                    }

                    if (CompQty.size() > 0) {
                        for (int i = 0; i < CompQty.size(); i++) {
                            getTotalValue = getTotalValue + CompQty.get(i);
                        }
                        valueRounded = Math.round(getTotalValue * 100D) / 100D;
                        RCPAFragment.ChemistSelectedList.set(j, new CustList(RCPAFragment.ChemistSelectedList.get(j).getName(), RCPAFragment.ChemistSelectedList.get(j).getCode(), String.valueOf(valueRounded), ""));
                    }
                }

                rcpaChemistAdapter = new RCPAChemistAdapter(requireActivity(), requireContext(), RCPAFragment.ChemistSelectedList, RCPAFragment.ProductSelectedList, rcpa_comp_list);
                commonUtilsMethods.recycleTestWithoutDivider(RCPAFragment.rcpaBinding.rvRcpaChemistList);
                RCPAFragment.rcpaBinding.rvRcpaChemistList.setAdapter(rcpaChemistAdapter);
                rcpaChemistAdapter.notifyDataSetChanged();

                dcrCallBinding.fragmentAddRcpaSide.setVisibility(View.GONE);
            } else {
                commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.add_one_competitor));
            }
        });

        rcpaSideBinding.imgAddComp.setOnClickListener(view -> {
            rcpaSideBinding.constraintPreviewCompList.setVisibility(View.GONE);
            rcpaSideBinding.constraintAddCompList.setVisibility(View.VISIBLE);
        });

        rcpaSideBinding.btnClearAddComp.setOnClickListener(view -> {
            rcpaSideBinding.edCompPrd.setText("");
            rcpaSideBinding.edCompPrd.setHint(requireContext().getString(R.string.enter_name));
            rcpaSideBinding.edCompCompany.setText("");
            rcpaSideBinding.edCompCompany.setHint(requireContext().getString(R.string.ent_company_name));
        });

        rcpaSideBinding.btnAdd.setOnClickListener(view -> {
            boolean isMatched = false;
            if (Objects.requireNonNull(rcpaSideBinding.edCompPrd.getText()).toString().isEmpty()) {
                 commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.enter_prd));
            } else if (Objects.requireNonNull(rcpaSideBinding.edCompCompany.getText()).toString().isEmpty()) {
                 commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.enter_company));
            } else {
                try {
                    if (masterDataDao.getMasterSyncDataOfHQ(Constants.LOCAL_MAPPED_COMPETITOR_PROD)) {
//                    if (sqLite.getMasterSyncDataOfHQ(Constants.LOCAL_MAPPED_COMPETITOR_PROD)) {
                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LOCAL_MAPPED_COMPETITOR_PROD).getMasterSyncDataJsonArray();
//                        jsonArray = sqLite.getMasterSyncDataByKey(Constants.LOCAL_MAPPED_COMPETITOR_PROD);
                        jsonObject = new JSONObject();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            String CompCompanyName = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompCompanyName");
                            String CompPrdName = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompPrdName");
                            if (RCPAFragment.PrdCode.equalsIgnoreCase(String.valueOf(jsonObject.getInt("Our_prd_code"))) && CompPrdName.equalsIgnoreCase(rcpaSideBinding.edCompPrd.getText().toString()) && CompCompanyName.equalsIgnoreCase(rcpaSideBinding.edCompCompany.getText().toString())) {
                                isMatched = true;
                                break;
                            }
                        }
                        if (!isMatched) {
                            jsonObject1 = jsonArray.getJSONObject(jsonArray.length() - 1);
                            String CompPrdCode = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompPrdCode");
                            int count = Integer.parseInt(CompPrdCode);
                            int finalCount = --count;
                            jsonObject = new JSONObject();
                            jsonObject.put("Our_prd_code", Integer.parseInt(addCompListDummy.get(0).getPrd_code()));
                            jsonObject.put("Our_prd_name", addCompListDummy.get(0).getPrd_name());
                            jsonObject.put("Competitor_Prd_bulk", finalCount + "#" + rcpaSideBinding.edCompPrd.getText().toString() + "~" + finalCount + "$" + rcpaSideBinding.edCompCompany.getText().toString() + "/");
                            jsonArray.put(jsonObject);
//                            sqLite.saveMasterSyncData(Constants.LOCAL_MAPPED_COMPETITOR_PROD, jsonArray.toString(), 0);
                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.LOCAL_MAPPED_COMPETITOR_PROD, jsonArray.toString(), 0));

                            addCompList.add(new RCPAAddedCompList(addCompListDummy.get(0).getPrd_name(), addCompListDummy.get(0).getPrd_code(), addCompListDummy.get(0).getChem_names(), addCompListDummy.get(0).getChem_Code(), rcpaSideBinding.edCompCompany.getText().toString(), String.valueOf(finalCount), rcpaSideBinding.edCompPrd.getText().toString(), String.valueOf(finalCount), addCompListDummy.get(0).getRate(), false, addCompListDummy.get(0).getTotalPrdValue()));

                             commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.competitor_added));

                            rcpaSideBinding.constraintPreviewCompList.setVisibility(View.VISIBLE);
                            rcpaSideBinding.constraintAddCompList.setVisibility(View.GONE);
                            rcpaSideBinding.edCompPrd.setText("");
                            rcpaSideBinding.edCompPrd.setHint(requireContext().getString(R.string.enter_name));
                            rcpaSideBinding.edCompCompany.setText("");
                            rcpaSideBinding.edCompCompany.setHint(requireContext().getString(R.string.ent_company_name));

                            adapterCompetitorPrd = new AdapterCompetitorPrd(requireContext(), addCompList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            rcpaSideBinding.rvCompPrdList.setLayoutManager(mLayoutManager);
                            rcpaSideBinding.rvCompPrdList.setItemAnimator(new DefaultItemAnimator());
                            rcpaSideBinding.rvCompPrdList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
                            rcpaSideBinding.rvCompPrdList.setAdapter(adapterCompetitorPrd);
                            adapterCompetitorPrd.notifyDataSetChanged();
                        } else {
                             commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.prd_comp_available));
                        }
                    } else {
                        jsonArray = new JSONArray();
                        jsonObject = new JSONObject();
                        jsonObject.put("Our_prd_code", Integer.parseInt(RCPAFragment.PrdCode));
                        jsonObject.put("Our_prd_name", RCPAFragment.PrdName);
                        jsonObject.put("Competitor_Prd_bulk", "-1#" + rcpaSideBinding.edCompPrd.getText().toString() + "~" + "-1" + "$" + rcpaSideBinding.edCompCompany.getText().toString() + "/");
                        jsonArray.put(jsonObject);
//                        sqLite.saveMasterSyncData(Constants.LOCAL_MAPPED_COMPETITOR_PROD, jsonArray.toString(), 0);
                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.LOCAL_MAPPED_COMPETITOR_PROD, jsonArray.toString(), 0));

                        addCompList.add(new RCPAAddedCompList(addCompListDummy.get(0).getPrd_name(), addCompListDummy.get(0).getPrd_code(), addCompListDummy.get(0).getChem_names(), addCompListDummy.get(0).getChem_Code(), rcpaSideBinding.edCompCompany.getText().toString(), "-1", rcpaSideBinding.edCompPrd.getText().toString(), "-1", addCompListDummy.get(0).getRate(), false, addCompListDummy.get(0).getTotalPrdValue()));

                         commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.competitor_added));

                        rcpaSideBinding.constraintPreviewCompList.setVisibility(View.VISIBLE);
                        rcpaSideBinding.constraintAddCompList.setVisibility(View.GONE);
                        rcpaSideBinding.edCompPrd.setText("");
                        rcpaSideBinding.edCompPrd.setHint(requireContext().getString(R.string.enter_name));
                        rcpaSideBinding.edCompCompany.setText("");
                        rcpaSideBinding.edCompCompany.setHint(requireContext().getString(R.string.ent_company_name));

                        adapterCompetitorPrd = new AdapterCompetitorPrd(requireContext(), addCompList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        rcpaSideBinding.rvCompPrdList.setLayoutManager(mLayoutManager);
                        rcpaSideBinding.rvCompPrdList.setItemAnimator(new DefaultItemAnimator());
                        rcpaSideBinding.rvCompPrdList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
                        rcpaSideBinding.rvCompPrdList.setAdapter(adapterCompetitorPrd);
                        adapterCompetitorPrd.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.v("add_competitor", "error" + e);
                }
            }
        });
        return v;
    }

    private void filter(String text) {
        ArrayList<RCPAAddedCompList> filteredNames = new ArrayList<>();
        for (RCPAAddedCompList s : addCompList) {
            if (s.getComp_product().toLowerCase().contains(text.toLowerCase()) || s.getComp_company_name().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapterCompetitorPrd.filterList(filteredNames);
    }

    public String extractValues(String s, String data) {
        if (TextUtils.isEmpty(s)) return "";
        String[] ArrQty = s.split("/");
        StringBuilder ss1 = new StringBuilder();
        for (String value : ArrQty) {
            if (data.equalsIgnoreCase("CompPrdCode")) {
                ss1.append(value.substring(0, value.indexOf("#"))).append(",");
            } else if (data.equalsIgnoreCase("CompPrdName")) {
                ss1.append(value.substring(value.indexOf("#") + 1)).append(",");
                int index = ss1.indexOf("~");
                ss1 = new StringBuilder(ss1.substring(0, index) + ",");
            } else if (data.equalsIgnoreCase("CompCompanyCode")) {
                ss1.append(value.substring(value.indexOf("~") + 1)).append(",");
                int index = ss1.indexOf("$");
                ss1 = new StringBuilder(ss1.substring(0, index) + ",");
            } else if (data.equalsIgnoreCase("CompCompanyName")) {
                ss1.append(value.substring(value.indexOf("$") + 1)).append(",");
            }
        }
        String finalValue;
        finalValue = ss1.substring(0, ss1.length() - 1);
        return finalValue;
    }

    private void SetupAdapter() {
        adapterCompetitorPrd = new AdapterCompetitorPrd(requireContext(), addCompList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rcpaSideBinding.rvCompPrdList.setLayoutManager(mLayoutManager);
        rcpaSideBinding.rvCompPrdList.setItemAnimator(new DefaultItemAnimator());
        rcpaSideBinding.rvCompPrdList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        rcpaSideBinding.rvCompPrdList.setAdapter(adapterCompetitorPrd);
    }


    public static class AdapterCompetitorPrd extends RecyclerView.Adapter<AdapterCompetitorPrd.ViewHolder> {
        public static ArrayList<RCPAAddedCompList> CompetitorList;
        final Context context;
        CommonUtilsMethods commonUtilsMethods;

        public AdapterCompetitorPrd(Context context, ArrayList<RCPAAddedCompList> competitorList) {
            this.context = context;
            CompetitorList = competitorList;
            commonUtilsMethods = new CommonUtilsMethods(context);
        }

        @NonNull
        @Override
        public AdapterCompetitorPrd.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_comp, parent, false);
            return new AdapterCompetitorPrd.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterCompetitorPrd.ViewHolder holder, int position) {
            holder.tv_name.setText(CompetitorList.get(position).getComp_product());
            holder.tv_brand.setText(CompetitorList.get(position).getComp_company_name());

            if (CompetitorList.get(position).isSelected()) {
                holder.checkBox.setChecked(true);
                holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.cheked_txt_color));
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
            } else {
                holder.checkBox.setChecked(false);
                holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.bg_txt_color));
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
            }

            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (holder.checkBox.isPressed()) {
                    if (holder.checkBox.isChecked()) {
                        ArrayList<String> dummyArray = new ArrayList<>();
                        for (int i = 0; i < rcpa_comp_list.size(); i++) {
                            if (CompetitorList.get(position).getComp_product_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_product_code()) && CompetitorList.get(position).getComp_company_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_company_code()) && CompetitorList.get(position).getPrd_code().equalsIgnoreCase(rcpa_comp_list.get(i).getPrd_code()) && CompetitorList.get(position).getChem_Code().equalsIgnoreCase(rcpa_comp_list.get(i).getChem_Code())) {
                                dummyArray.add(CompetitorList.get(position).getComp_product_code());

                            }
                        }

                        if (dummyArray.size() == 0) {
                            holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.cheked_txt_color));
                            holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
                            CompetitorList.get(position).setSelected(true);
                        } else {
                             commonUtilsMethods.showToastMessage(context,context.getString(R.string.already_sel_prd));
                            holder.checkBox.setChecked(false);
                        }

                    } else {
                        holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.bg_txt_color));
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
                        CompetitorList.get(position).setSelected(false);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return CompetitorList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<RCPAAddedCompList> filteredNames) {
            CompetitorList = filteredNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tv_name;
            final TextView tv_brand;
            final CheckBox checkBox;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_data_name);
                checkBox = itemView.findViewById(R.id.chk_box);
                tv_brand = itemView.findViewById(R.id.tv_brand_name);
            }
        }
    }
}
