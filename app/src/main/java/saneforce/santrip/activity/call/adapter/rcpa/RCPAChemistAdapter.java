package saneforce.santrip.activity.call.adapter.rcpa;

import static saneforce.santrip.activity.call.fragments.rcpa.RCPAFragment.ChemistSelectedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.fragments.rcpa.RCPAFragment;
import saneforce.santrip.activity.call.fragments.rcpa.RCPASelectCompSide;
import saneforce.santrip.activity.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.santrip.activity.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.santrip.activity.map.custSelection.CustList;

public class RCPAChemistAdapter extends RecyclerView.Adapter<RCPAChemistAdapter.ViewHolder> {
    public static RecyclerView rv_prd_list;
    public static ArrayList<CustList> chemistNames;
    public static ArrayList<RCPAAddedProdList> rcpaProdList;
    @SuppressLint("StaticFieldLeak")
    public static RCPAProductsAdapter rcpaProductsAdapter;
    Context context;
    Activity activity;
    ArrayList<RCPAAddedProdList> FilterPrdList = new ArrayList<>();
    ArrayList<RCPAAddedCompList> rcpaCompList;
    String ChemCode;
    ArrayList<Double> PrdQty = new ArrayList<>();

    public RCPAChemistAdapter(Activity activity,Context context, ArrayList<CustList> chemistNames, ArrayList<RCPAAddedProdList> rcpaProdArrayList, ArrayList<RCPAAddedCompList> rcpaCompList) {
        this.context = context;
        this.activity = activity;
        RCPAChemistAdapter.chemistNames = chemistNames;
        rcpaProdList = rcpaProdArrayList;
        this.rcpaCompList = rcpaCompList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_chemist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.chemist_name.setText(chemistNames.get(position).getName());
        holder.tv_total.setText(String.format("%s  %s", context.getResources().getString(R.string.total), chemistNames.get(position).getTotalRcpa()));
        rv_prd_list = holder.rv_prdList;

        FilterPrdList = new ArrayList<>();
        PrdQty = new ArrayList<>();
        for (int i = 0; i < rcpaProdList.size(); i++) {
            if (rcpaProdList.get(i).getChe_codes().equalsIgnoreCase(chemistNames.get(position).getCode())) {
                Log.v("chemist", rcpaProdList.get(i).getPrd_name() + "---" + rcpaProdList.get(i).getTotalPrdValue());
                FilterPrdList.add(new RCPAAddedProdList(rcpaProdList.get(i).getChem_names(), rcpaProdList.get(i).getChe_codes(), rcpaProdList.get(i).getPrd_name(), rcpaProdList.get(i).getPrd_code(), rcpaProdList.get(i).getQty(), rcpaProdList.get(i).getRate(), rcpaProdList.get(i).getValue(), rcpaProdList.get(i).getTotalPrdValue()));
                rcpaProductsAdapter = new RCPAProductsAdapter(activity, context, FilterPrdList, rcpaCompList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                rv_prd_list.setLayoutManager(mLayoutManager);
                rv_prd_list.setAdapter(rcpaProductsAdapter);
                PrdQty.add(Double.parseDouble(rcpaProdList.get(i).getValue()));
            }
        }


        holder.img_del.setOnClickListener(view -> {
            ChemCode = chemistNames.get(position).getCode();
            removeAt(position);
            for (int i = 0; i < RCPASelectCompSide.rcpa_comp_list.size(); i++) {
                if (RCPASelectCompSide.rcpa_comp_list.get(i).getChem_Code().equalsIgnoreCase(ChemCode)) {
                    RCPASelectCompSide.rcpa_comp_list.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < RCPAFragment.ProductSelectedList.size(); i++) {
                if (RCPAFragment.ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(ChemCode)) {
                    RCPAFragment.ProductSelectedList.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < ChemistSelectedList.size(); i++) {
                if (ChemistSelectedList.get(i).getCode().equalsIgnoreCase(ChemCode)) {
                    ChemistSelectedList.remove(i);
                    i--;
                }
            }
        });
    }

    public void removeAt(int position) {
        chemistNames.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, chemistNames.size());
    }

    @Override
    public int getItemCount() {
        return chemistNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView chemist_name, tv_total;
        RecyclerView rv_prdList;
        ImageView img_del;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chemist_name = itemView.findViewById(R.id.tv_chemist_name);
            rv_prdList = itemView.findViewById(R.id.rv_prd_names);
            img_del = itemView.findViewById(R.id.img_del_chem);
            tv_total = itemView.findViewById(R.id.tv_total);
        }
    }
}
