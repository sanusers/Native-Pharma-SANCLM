package saneforce.sanclm.activity.homeScreen.call.adapter.rcpa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.sanclm.activity.map.custSelection.CustList;

public class RCPAChemistAdapter extends RecyclerView.Adapter<RCPAChemistAdapter.ViewHolder> {
    public static RecyclerView rv_prd_list;
    public ArrayList<CustList> chemistNames;
    Context context;
    ArrayList<RCPAAddedProdList> rcpaProdList;
    ArrayList<RCPAAddedProdList> FilterPrdList = new ArrayList<>();
    ArrayList<RCPAAddedCompList> rcpa_add_list;
    RCPAProductsAdapter rcpaProductsAdapter;

    public RCPAChemistAdapter(Context context, ArrayList<CustList> chemistNames, ArrayList<RCPAAddedProdList> rcpaProdArrayList) {
        this.context = context;
        this.chemistNames = chemistNames;
        this.rcpaProdList = rcpaProdArrayList;
    }


 /*   public RCPAChemistAdapter(Context context, ArrayList<String> chemistNames, ArrayList<RCPAAddedProdList> rcpaAddedProdListArrayList, ArrayList<RCPAAddedCompList> rcpa_add_list) {
        this.context = context;
        RCPAChemistAdapter.chemistNames = chemistNames;
        this.rcpaAddedProdListArrayList = rcpaAddedProdListArrayList;
        this.rcpa_add_list = rcpa_add_list;
    }*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_chemist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.chemist_name.setText(chemistNames.get(position).getName());
        rv_prd_list = holder.rv_prdlist;
        FilterPrdList = new ArrayList<>();
        for (int i = 0; i < rcpaProdList.size(); i++) {
            if (rcpaProdList.get(i).getChe_codes().equalsIgnoreCase(chemistNames.get(position).getCode())) {
                FilterPrdList.add(new RCPAAddedProdList(rcpaProdList.get(i).getChem_names(), rcpaProdList.get(i).getChe_codes(), rcpaProdList.get(i).getPrd_name(), rcpaProdList.get(i).getPrd_code(), rcpaProdList.get(i).getQty(), rcpaProdList.get(i).getRate(), rcpaProdList.get(i).getValue()));
                rcpaProductsAdapter = new RCPAProductsAdapter(context, FilterPrdList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                rv_prd_list.setLayoutManager(mLayoutManager);
                rv_prd_list.setAdapter(rcpaProductsAdapter);
            }
        }



      /*  holder.img_del.setOnClickListener(view -> {
            removeAt(position);
            if (rcpaAddedProdListArrayList.size() > 0) {
                rcpaAddedProdListArrayList.remove(position);
            }
        });*/
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
        TextView chemist_name;
        RecyclerView rv_prdlist;
        ImageView img_del;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chemist_name = itemView.findViewById(R.id.tv_chemist_name);
            rv_prdlist = itemView.findViewById(R.id.rv_prd_names);
            img_del = itemView.findViewById(R.id.img_del_chem);
        }
    }
}
