package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.ChemistSelectedList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.PrdCode;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.PrdName;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.ProductSelectedList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.rcpaBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
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
import android.widget.Toast;

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
import java.util.Objects;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.adapter.rcpa.RCPAChemistAdapter;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.FragmentAddRcpaSideBinding;
import saneforce.sanclm.storage.SQLite;

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

    @SuppressLint({"ResourceType", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rcpaSideBinding = FragmentAddRcpaSideBinding.inflate(getLayoutInflater());
        View v = rcpaSideBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        sqLite = new SQLite(requireContext());

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
                for (int j = 0; j < ProductSelectedList.size(); j++) {
                    CompQty = new ArrayList<>();
                    for (int i = 0; i < rcpa_comp_list.size(); i++) {
                        if (rcpa_comp_list.get(i).getChem_Code().equalsIgnoreCase(ProductSelectedList.get(j).getChe_codes()) && rcpa_comp_list.get(i).getPrd_code().equalsIgnoreCase(ProductSelectedList.get(j).getPrd_code())) {
                            if (!rcpa_comp_list.get(i).getValue().isEmpty()) {
                                getTotalValue = Double.parseDouble(ProductSelectedList.get(j).getValue());
                                CompQty.add(Double.parseDouble(rcpa_comp_list.get(i).getValue()));
                            }
                        }
                    }

                    if (CompQty.size() > 0) {
                        for (int i = 0; i < CompQty.size(); i++) {
                            getTotalValue = getTotalValue + CompQty.get(i);
                        }
                        valueRounded = Math.round(getTotalValue * 100D) / 100D;
                        ProductSelectedList.set(j, new RCPAAddedProdList(ProductSelectedList.get(j).getChem_names(), ProductSelectedList.get(j).getChe_codes(), ProductSelectedList.get(j).getPrd_name(), ProductSelectedList.get(j).getPrd_code(), ProductSelectedList.get(j).getQty(), ProductSelectedList.get(j).getRate(), ProductSelectedList.get(j).getValue(), String.valueOf(valueRounded)));
                    } else {
                        ProductSelectedList.set(j, new RCPAAddedProdList(ProductSelectedList.get(j).getChem_names(), ProductSelectedList.get(j).getChe_codes(), ProductSelectedList.get(j).getPrd_name(), ProductSelectedList.get(j).getPrd_code(), ProductSelectedList.get(j).getQty(), ProductSelectedList.get(j).getRate(), ProductSelectedList.get(j).getValue(), ProductSelectedList.get(j).getValue()));
                    }
                }


                for (int j = 0; j < ChemistSelectedList.size(); j++) {
                    CompQty = new ArrayList<>();
                    for (int i = 0; i < ProductSelectedList.size(); i++) {
                        if (ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(ChemistSelectedList.get(j).getCode())) {
                            if (!ProductSelectedList.get(i).getTotalPrdValue().isEmpty()) {
                                getTotalValue = 0.0;
                                CompQty.add(Double.parseDouble(ProductSelectedList.get(i).getTotalPrdValue()));
                            }
                        }
                    }

                    if (CompQty.size() > 0) {
                        for (int i = 0; i < CompQty.size(); i++) {
                            getTotalValue = getTotalValue + CompQty.get(i);
                        }
                        valueRounded = Math.round(getTotalValue * 100D) / 100D;
                        ChemistSelectedList.set(j, new CustList(ChemistSelectedList.get(j).getName(), ChemistSelectedList.get(j).getCode(), String.valueOf(valueRounded), ""));
                    }
                }

                rcpaChemistAdapter = new RCPAChemistAdapter(requireActivity(), requireContext(), ChemistSelectedList, ProductSelectedList, rcpa_comp_list);
                commonUtilsMethods.recycleTestWithoutDivider(rcpaBinding.rvRcpaChemistList);
                rcpaBinding.rvRcpaChemistList.setAdapter(rcpaChemistAdapter);
                rcpaChemistAdapter.notifyDataSetChanged();

                dcrCallBinding.fragmentAddRcpaSide.setVisibility(View.GONE);
            } else {
                Toast.makeText(requireContext(), "Add AtLeast One Competitor to Save", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "Enter Product", Toast.LENGTH_SHORT).show();
            } else if (Objects.requireNonNull(rcpaSideBinding.edCompCompany.getText()).toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter Company", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    jsonArray = sqLite.getMasterSyncDataByKey(Constants.LOCAL_MAPPED_COMPETITOR_PROD);
                    jsonObject = new JSONObject();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            String CompCompanyName = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompCompanyName");
                            String CompPrdName = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompPrdName");
                            if (PrdCode.equalsIgnoreCase(String.valueOf(jsonObject.getInt("Our_prd_code"))) && CompPrdName.equalsIgnoreCase(rcpaSideBinding.edCompPrd.getText().toString()) && CompCompanyName.equalsIgnoreCase(rcpaSideBinding.edCompCompany.getText().toString())) {
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
                            sqLite.saveMasterSyncData(Constants.LOCAL_MAPPED_COMPETITOR_PROD, jsonArray.toString(), 0);

                            addCompList.add(new RCPAAddedCompList(addCompListDummy.get(0).getPrd_name(), addCompListDummy.get(0).getPrd_code(), addCompListDummy.get(0).getChem_names(), addCompListDummy.get(0).getChem_Code(), rcpaSideBinding.edCompCompany.getText().toString(), String.valueOf(finalCount), rcpaSideBinding.edCompPrd.getText().toString(), String.valueOf(finalCount), addCompListDummy.get(0).getRate(), false, addCompListDummy.get(0).getTotalPrdValue()));

                            Toast.makeText(requireContext(), "Competitor Added Successfully", Toast.LENGTH_SHORT).show();

                            rcpaSideBinding.constraintPreviewCompList.setVisibility(View.VISIBLE);
                            rcpaSideBinding.constraintAddCompList.setVisibility(View.GONE);
                            rcpaSideBinding.imgClose.setImageDrawable(requireContext().getResources().getDrawable(R.drawable.img_close));
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
                            Toast.makeText(requireContext(), "This Product & Company Already Available!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        jsonObject.put("Our_prd_code", Integer.parseInt(PrdCode));
                        jsonObject.put("Our_prd_name", PrdName);
                        jsonObject.put("Competitor_Prd_bulk", "-1#" + rcpaSideBinding.edCompPrd.getText().toString() + "~" + "-1" + "$" + rcpaSideBinding.edCompCompany.getText().toString() + "/");
                        jsonArray.put(jsonObject);
                        sqLite.saveMasterSyncData(Constants.LOCAL_MAPPED_COMPETITOR_PROD, jsonArray.toString(), 0);

                        addCompList.add(new RCPAAddedCompList(addCompListDummy.get(0).getPrd_name(), addCompListDummy.get(0).getPrd_code(), addCompListDummy.get(0).getChem_names(), addCompListDummy.get(0).getChem_Code(), rcpaSideBinding.edCompCompany.getText().toString(), "-1", rcpaSideBinding.edCompPrd.getText().toString(), "-1", addCompListDummy.get(0).getRate(), false, addCompListDummy.get(0).getTotalPrdValue()));

                        Toast.makeText(requireContext(), "Competitor Added Successfully", Toast.LENGTH_SHORT).show();

                        rcpaSideBinding.constraintPreviewCompList.setVisibility(View.VISIBLE);
                        rcpaSideBinding.constraintAddCompList.setVisibility(View.GONE);
                        rcpaSideBinding.imgClose.setImageDrawable(requireContext().getResources().getDrawable(R.drawable.img_close));
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
                    Log.v("dfgdfgdf", "error" + e);
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
        String[] clstarrrayqty = s.split("/");
        StringBuilder ss1 = new StringBuilder();
        for (String value : clstarrrayqty) {
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

        public AdapterCompetitorPrd(Context context, ArrayList<RCPAAddedCompList> competitorList) {
            this.context = context;
            CompetitorList = competitorList;
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
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                }
            } else {
                holder.checkBox.setChecked(false);
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                }
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
                            holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                            }
                            CompetitorList.get(position).setSelected(true);
                        } else {
                            Toast.makeText(context, "Already Selected this Product", Toast.LENGTH_SHORT).show();
                            holder.checkBox.setChecked(false);
                        }

                    } else {
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                        }
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
