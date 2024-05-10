package saneforce.santrip.activity.call.adapter.rcpa;

import static saneforce.santrip.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.santrip.activity.call.fragments.rcpa.RCPAFragment.ChemistSelectedList;
import static saneforce.santrip.activity.call.fragments.rcpa.RCPAFragment.ProductSelectedList;
import static saneforce.santrip.activity.call.fragments.rcpa.RCPAFragment.rcpaBinding;
import static saneforce.santrip.activity.call.fragments.rcpa.RCPASelectCompSide.adapterCompetitorPrd;
import static saneforce.santrip.activity.call.fragments.rcpa.RCPASelectCompSide.addCompList;
import static saneforce.santrip.activity.call.fragments.rcpa.RCPASelectCompSide.addCompListDummy;
import static saneforce.santrip.activity.call.fragments.rcpa.RCPASelectCompSide.rcpaSideBinding;
import static saneforce.santrip.activity.call.fragments.rcpa.RCPASelectCompSide.rcpa_comp_list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.DCRCallActivity;
import saneforce.santrip.activity.call.fragments.rcpa.RCPASelectCompSide;
import saneforce.santrip.activity.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.santrip.activity.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;


public class RCPAProductsAdapter extends RecyclerView.Adapter<RCPAProductsAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<RCPAAddedCompList> CompList;
    ArrayList<RCPAAddedCompList> CompListFilter = new ArrayList<>();
    ArrayList<RCPAAddedProdList> ProductList;
    RCPACompListAdapter rcpaCompListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    JSONArray jsonArray;
//    SQLite sqLite;
    JSONObject jsonObject;
    String ChemCode, PrdCode;
    double getTotalValue = 0, valueRounded;
    ArrayList<Double> CompQty = new ArrayList<>();
    RCPAChemistAdapter rcpaChemistAdapter;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    public RCPAProductsAdapter(Activity activity,Context context, ArrayList<RCPAAddedProdList> productList, ArrayList<RCPAAddedCompList> CompList) {
        this.activity = activity;
        this.context = context;
        this.ProductList = productList;
        this.CompList = CompList;
        roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_prd, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
//        sqLite = new SQLite(context);

        holder.prd_name.setText(ProductList.get(position).getPrd_name());
        holder.tv_qty.setText(ProductList.get(position).getQty());
        holder.tv_rate.setText(ProductList.get(position).getRate());
        holder.tv_value.setText(ProductList.get(position).getValue());
        holder.tv_total.setText(ProductList.get(position).getTotalPrdValue());

        holder.prd_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, holder.prd_name.getText().toString()));

        if (CompList.size() > 0) {
            holder.constraint_comp_list.setVisibility(View.VISIBLE);
        } else {
            holder.constraint_comp_list.setVisibility(View.GONE);
        }

        if (ProductList.size() > 1) {
            holder.img_delete.setVisibility(View.VISIBLE);
        } else {
            holder.img_delete.setVisibility(View.GONE);
        }


        CompListFilter = new ArrayList<>();
        for (int i = 0; i < CompList.size(); i++) {
            if (CompList.get(i).getChem_Code().equalsIgnoreCase(ProductList.get(position).getChe_codes()) && CompList.get(i).getPrd_code().equalsIgnoreCase(ProductList.get(position).getPrd_code())) {
                CompListFilter.add(new RCPAAddedCompList(CompList.get(i).getChem_names(), CompList.get(i).getChem_Code(), CompList.get(i).getPrd_name(), CompList.get(i).getPrd_code(), CompList.get(i).getComp_company_name(), CompList.get(i).getComp_company_code(), CompList.get(i).getComp_product(), CompList.get(i).getComp_product_code(), CompList.get(i).getQty(), CompList.get(i).getRate(), CompList.get(i).getValue(), CompList.get(i).getRemarks(), ProductList.get(position).getTotalPrdValue()));
            }

            rcpaCompListAdapter = new RCPACompListAdapter(activity,context, CompListFilter);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            ViewHolder.rv_added_comp_list.setLayoutManager(mLayoutManager);
            ViewHolder.rv_added_comp_list.setAdapter(rcpaCompListAdapter);
        }

        holder.btn_add_comp.setOnClickListener(view -> {
            try {
                addCompList = new ArrayList<>();
                if (DCRCallActivity.RcpaCompetitorAdd.equalsIgnoreCase("0")) {
                    AddCompetitorValues(holder.getBindingAdapterPosition(), Constants.MAPPED_COMPETITOR_PROD);
                    AddCompetitorValues(holder.getBindingAdapterPosition(), Constants.LOCAL_MAPPED_COMPETITOR_PROD);
                    addCompListDummy.clear();
                    addCompListDummy.add(new RCPAAddedCompList(ProductList.get(holder.getBindingAdapterPosition()).getPrd_name(), ProductList.get(holder.getBindingAdapterPosition()).getPrd_code(), ProductList.get(holder.getBindingAdapterPosition()).getChem_names(), ProductList.get(holder.getBindingAdapterPosition()).getChe_codes(), ProductList.get(holder.getBindingAdapterPosition()).getRate(), String.valueOf(valueRounded)));
                } else {
                    AddCompetitorValues(holder.getBindingAdapterPosition(), Constants.MAPPED_COMPETITOR_PROD);
                }

           /*     if (addCompList.size() == 0) {
                    rcpaSideBinding.constraintPreviewCompList.setVisibility(View.GONE);
                    rcpaSideBinding.constraintAddCompList.setVisibility(View.VISIBLE);
                } else {*/
                rcpaSideBinding.constraintPreviewCompList.setVisibility(View.VISIBLE);
                rcpaSideBinding.constraintAddCompList.setVisibility(View.GONE);
                //   }

                adapterCompetitorPrd = new RCPASelectCompSide.AdapterCompetitorPrd(context, addCompList);
                commonUtilsMethods.recycleTestWithDivider(rcpaSideBinding.rvCompPrdList);
                rcpaSideBinding.rvCompPrdList.setAdapter(adapterCompetitorPrd);
                adapterCompetitorPrd.notifyDataSetChanged();

            } catch (Exception e) {
                Log.v("comp_prd", "--error--2--" + e);
            }
            dcrCallBinding.fragmentAddRcpaSide.setVisibility(View.VISIBLE);
        });

        holder.img_delete.setOnClickListener(view -> {
            ChemCode = ProductList.get(holder.getBindingAdapterPosition()).getChe_codes();
            PrdCode = ProductList.get(holder.getBindingAdapterPosition()).getPrd_code();
            ProductList.remove(position);

            for (int i = 0; i < rcpa_comp_list.size(); i++) {
                if (rcpa_comp_list.get(i).getChem_Code().equalsIgnoreCase(ChemCode) && rcpa_comp_list.get(i).getPrd_code().equalsIgnoreCase(PrdCode)) {
                    rcpa_comp_list.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < ProductSelectedList.size(); i++) {
                if (ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(ChemCode) && ProductSelectedList.get(i).getPrd_code().equalsIgnoreCase(PrdCode)) {
                    ProductSelectedList.remove(i);
                    i--;
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

            rcpaChemistAdapter = new RCPAChemistAdapter(activity,context, ChemistSelectedList, ProductSelectedList, rcpa_comp_list);
            commonUtilsMethods.recycleTestWithoutDivider(rcpaBinding.rvRcpaChemistList);
            rcpaBinding.rvRcpaChemistList.setAdapter(rcpaChemistAdapter);
            rcpaChemistAdapter.notifyDataSetChanged();
        });
    }

    private void AddCompetitorValues(int adapterPosition, String ColumnName) {
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(ColumnName).getMasterSyncDataJsonArray();
//            jsonArray = sqLite.getMasterSyncDataByKey(ColumnName);
            for (int i = 0; i < jsonArray.length(); i++) {

                jsonObject = jsonArray.getJSONObject(i);
                ArrayList<String> CompCompanyNameList = new ArrayList<>();
                ArrayList<String> CompCompanyCodeList = new ArrayList<>();
                ArrayList<String> CompPrdNameList = new ArrayList<>();
                ArrayList<String> CompPrdCodeList = new ArrayList<>();

                if (ProductList.get(adapterPosition).getPrd_code().equalsIgnoreCase(jsonObject.getString("Our_prd_code"))) {
                    String CompCompanyName = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompCompanyName");
                    String CompCompanyCode = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompCompanyCode");
                    String CompPrdName = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompPrdName");
                    String CompPrdCode = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompPrdCode");
                    Log.v("comp_prd", CompCompanyName + "---" + CompCompanyCode + "---" + CompPrdName + "---" + CompPrdCode);

                    getValues(CompCompanyName, CompCompanyNameList);
                    getValues(CompCompanyCode, CompCompanyCodeList);
                    getValues(CompPrdName, CompPrdNameList);
                    getValues(CompPrdCode, CompPrdCodeList);
                }
                Log.v("comp_prd", CompCompanyNameList.size() + "---" + CompCompanyCodeList.size() + "---" + CompPrdNameList.size() + "---" + CompPrdCodeList.size());

                for (int j = 0; j < CompCompanyNameList.size(); j++) {
                    addCompList.add(new RCPAAddedCompList(ProductList.get(adapterPosition).getPrd_name(), ProductList.get(adapterPosition).getPrd_code(), ProductList.get(adapterPosition).getChem_names(), ProductList.get(adapterPosition).getChe_codes(), CompCompanyNameList.get(j), CompCompanyCodeList.get(j), CompPrdNameList.get(j), CompPrdCodeList.get(j), ProductList.get(adapterPosition).getRate(), false, String.valueOf(valueRounded)));
                }
            }
        } catch (Exception e) {
            Log.v("comp_prd", "--error--" + e);
        }
    }

    public void removeAt(int position) {
        ProductList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ProductList.size());
    }

    public String extractValues(String s, String data) {
        if (TextUtils.isEmpty(s)) return "";
        String[] StrArray = s.split("/");
        StringBuilder ss1 = new StringBuilder();
        for (String value : StrArray) {
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

    private void getValues(String names, ArrayList<String> addData) {
        String[] separated = names.split(",");
        addData.addAll(Arrays.asList(separated));
    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public static RecyclerView rv_added_comp_list;
        TextView tv_total;
        TextView prd_name, tv_qty, tv_rate, tv_value;
        ImageView img_delete;
        ImageView img_view;
        CardView card_list_view;
        ConstraintLayout constraint_comp_list;
        TextView btn_add_comp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prd_name = itemView.findViewById(R.id.tv_prd_name);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_total = itemView.findViewById(R.id.tv_total);
            img_delete = itemView.findViewById(R.id.img_del_prd);
            constraint_comp_list = itemView.findViewById(R.id.constraint_list_comp);
            rv_added_comp_list = itemView.findViewById(R.id.rv_comp_added_list);
            img_view = itemView.findViewById(R.id.img_view);
            card_list_view = itemView.findViewById(R.id.card_list);
            btn_add_comp = itemView.findViewById(R.id.btn_add_comp);
        }
    }
}
