package saneforce.sanclm.activity.homeScreen.call.adapter.rcpa;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrcallBinding;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragmentSide.adapterCompetitorPrd;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragmentSide.addCompList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragmentSide.rcpaSideBinding;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragmentSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.storage.SQLite;

public class RCPAProductsAdapter extends RecyclerView.Adapter<RCPAProductsAdapter.ViewHolder> {
    public static RecyclerView rv_added_comp_list;
    Context context;
    ArrayList<RCPAAddedCompList> rcpa_add_list;
    ArrayList<RCPAAddedCompList> rcpa_add_list_new = new ArrayList<>();
    ArrayList<RCPAAddedProdList> ProductList;
    RCPACompListAdapter rcpaCompListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    JSONArray jsonArray;
    SQLite sqLite;
    JSONObject jsonObject;

  /*  public RCPAProductsAdapter(Context context, ArrayList<RCPAAddedProdList> rcpaFilterProdArrayListArrayList, ArrayList<RCPAAddedCompList> rcpa_add_list) {
        this.context = context;
        this.rcpaFilterProdArrayListArrayList = rcpaFilterProdArrayListArrayList;
        this.rcpa_add_list = rcpa_add_list;
    }*/

    public RCPAProductsAdapter(Context context, ArrayList<RCPAAddedProdList> productList) {
        this.context = context;
        this.ProductList = productList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_prd, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        sqLite = new SQLite(context);
        // Log.v("rcpa","---che--" + rcpaAddedProdListArrayList.get(position).getChem_names() + "--prd---" + rcpaAddedProdListArrayList.get(position).getPrd_name());
        holder.prd_name.setText(ProductList.get(position).getPrd_name());
        holder.tv_qty.setText(ProductList.get(position).getQty());
        holder.tv_rate.setText(ProductList.get(position).getRate());
        holder.tv_value.setText(ProductList.get(position).getValue());

        rv_added_comp_list = holder.rv_added_comp_list;

        /*for (int i = 0; i < rcpa_add_list.size(); i++) {
            if (rcpa_add_list.get(i).getChem_names().equalsIgnoreCase(ProductList.get(position).getChem_names()) && rcpa_add_list.get(i).getPrd_name().equalsIgnoreCase(ProductList.get(position).getPrd_name())) {
                rcpa_add_list_new.add(new RCPAAddedCompList(rcpa_add_list.get(i).getChem_names(), rcpa_add_list.get(i).getPrd_name(), rcpa_add_list.get(i).getComp_name(), rcpa_add_list.get(i).getComp_brand(), rcpa_add_list.get(i).getQty(), rcpa_add_list.get(i).getRate(), rcpa_add_list.get(i).getValue(), rcpa_add_list.get(i).getRemarks()));
                rcpaCompListAdapter = new RCPACompListAdapter(context, rcpa_add_list_new);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                rv_added_comp_list.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
                rv_added_comp_list.setLayoutManager(mLayoutManager);
                rv_added_comp_list.setAdapter(rcpaCompListAdapter);
            }
        }*/

      /*  rcpaCompListAdapter = new RCPACompListAdapter(context, rcpa_add_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rv_added_comp_list.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rv_added_comp_list.setLayoutManager(mLayoutManager);
        rv_added_comp_list.setAdapter(rcpaCompListAdapter);*/

        holder.btn_add_comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addCompList = new ArrayList<>();
                    jsonArray = sqLite.getMasterSyncDataByKey(Constants.MAPPED_COMPETITOR_PROD);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        jsonObject = jsonArray.getJSONObject(i);
                        ArrayList<String> CompPrdNameList = new ArrayList<>();
                        ArrayList<String> CompPrdCodeList = new ArrayList<>();
                        ArrayList<String> CompBrandNameList = new ArrayList<>();
                        ArrayList<String> CompBrandCodeList = new ArrayList<>();

                        if (ProductList.get(holder.getAdapterPosition()).getPrd_code().equalsIgnoreCase(jsonObject.getString("Our_prd_code"))) {

                            String CompPrdName = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompPrdName");
                            String CompPrdCode = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompPrdCode");
                            String CompBrandName = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompBrandName");
                            String CompBrandCode = extractValues(jsonObject.getString("Competitor_Prd_bulk"), "CompBrandCode");
                            Log.v("comp_prd", CompPrdName + "---" + CompPrdCode + "---" + CompBrandName + "---" + CompBrandCode);

                            getValues(CompPrdName, CompPrdNameList);
                            getValues(CompPrdCode, CompPrdCodeList);
                            getValues(CompBrandName, CompBrandNameList);
                            getValues(CompBrandCode, CompBrandCodeList);
                        }
                        Log.v("comp_prd", CompPrdNameList.size() + "---" + CompPrdCodeList.size() + "---" + CompBrandNameList.size() + "---" + CompBrandCodeList.size());
                        for (int j = 0; j < CompPrdNameList.size(); j++) {
                            addCompList.add(new CallCommonCheckedList(CompPrdNameList.get(j)));
                        }
                    }

                    adapterCompetitorPrd = new RCPAFragmentSide.AdapterCompetitorPrd(context, addCompList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    rcpaSideBinding.rvCompPrdList.setLayoutManager(mLayoutManager);
                    rcpaSideBinding.rvCompPrdList.setItemAnimator(new DefaultItemAnimator());
                    rcpaSideBinding.rvCompPrdList.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
                    rcpaSideBinding.rvCompPrdList.setAdapter(adapterCompetitorPrd);

                } catch (Exception e) {
                    Log.v("comp_prd", "--error--" + e);
                }
                dcrcallBinding.fragmentAddRcpaSide.setVisibility(View.VISIBLE);
            }
        });

        holder.img_delete.setOnClickListener(view -> removeAt(holder.getAdapterPosition()));

      /*  holder.img_view.setOnClickListener(view -> {
            if (holder.card_list_view.getVisibility() == View.VISIBLE) {
                holder.card_list_view.setVisibility(View.GONE);
                holder.img_view.setImageDrawable(context.getResources().getDrawable(R.drawable.img_plus));
            } else {
                holder.card_list_view.setVisibility(View.VISIBLE);
                holder.img_view.setImageDrawable(context.getResources().getDrawable(R.drawable.img_minus));
            }
        });*/
    }

    public void removeAt(int position) {
        ProductList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ProductList.size());
    }

    public String extractValues(String s, String data) {
        if (TextUtils.isEmpty(s)) return "";
        String[] clstarrrayqty = s.split("/");
        StringBuilder ss1 = new StringBuilder();
        for (String value : clstarrrayqty) {
            if (data.equalsIgnoreCase("CompBrandCode")) {
                ss1.append(value.substring(0, value.indexOf("#"))).append(",");
            } else if (data.equalsIgnoreCase("CompBrandName")) {
                ss1.append(value.substring(value.indexOf("#") + 1)).append(",");
                int index = ss1.indexOf("~");
                ss1 = new StringBuilder(ss1.substring(0, index) + ",");
            } else if (data.equalsIgnoreCase("CompPrdCode")) {
                ss1.append(value.substring(value.indexOf("~") + 1)).append(",");
                int index = ss1.indexOf("$");
                ss1 = new StringBuilder(ss1.substring(0, index) + ",");
            } else if (data.equalsIgnoreCase("CompPrdName")) {
                ss1.append(value.substring(value.indexOf("$") + 1)).append(",");
            }
        }
        String finalValue = "";
        finalValue = ss1.substring(0, ss1.length() - 1);
        return finalValue;
    }

    private void getValues(String names, ArrayList<String> addDatas) {
        String[] separated = names.split(",");
        addDatas.addAll(Arrays.asList(separated));
    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView prd_name, tv_qty, tv_rate, tv_value;
        ImageView img_delete;
        RecyclerView rv_added_comp_list;
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
            img_delete = itemView.findViewById(R.id.img_del_prd);
            rv_added_comp_list = itemView.findViewById(R.id.rv_comp_added_list);
            img_view = itemView.findViewById(R.id.img_view);
            card_list_view = itemView.findViewById(R.id.card_list);
            btn_add_comp = itemView.findViewById(R.id.btn_add_comp);
        }
    }
}
