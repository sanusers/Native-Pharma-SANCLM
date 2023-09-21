package saneforce.sanclm.Activities.Call.Adapter.RCPA;

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
import saneforce.sanclm.Activities.Call.Pojo.RCPA.RCPAAddedCompList;
import saneforce.sanclm.Activities.Call.Pojo.RCPA.RCPAAddedProdList;

public class RCPAChemistAdapter extends RecyclerView.Adapter<RCPAChemistAdapter.ViewHolder> {
    public static RecyclerView rv_prd_list;
    public static ArrayList<String> chemistNames;
    Context context;
    ArrayList<RCPAAddedProdList> rcpaAddedProdListArrayList;
    ArrayList<RCPAAddedProdList> rcpaFilterProdArrayListArrayList = new ArrayList<>();
    ArrayList<RCPAAddedCompList> rcpa_add_list;
    RCPAProductsAdapter rcpaProductsAdapter;


    public RCPAChemistAdapter(Context context, ArrayList<String> chemistNames, ArrayList<RCPAAddedProdList> rcpaAddedProdListArrayList, ArrayList<RCPAAddedCompList> rcpa_add_list) {
        this.context = context;
        RCPAChemistAdapter.chemistNames = chemistNames;
        this.rcpaAddedProdListArrayList = rcpaAddedProdListArrayList;
        this.rcpa_add_list = rcpa_add_list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_chemist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        rv_prd_list = holder.rv_prdlist;
        rcpaFilterProdArrayListArrayList = new ArrayList<>();
      /*  int j = 0;
        dummyChemist.add(chemistNames.get(position));
        chemistNames.clear();
        for (String name : dummyChemist) {
            chemistNames.add(name);
            j++;
        }*/
        for (int i = 0; i < rcpaAddedProdListArrayList.size(); i++) {
            if (rcpaAddedProdListArrayList.get(i).getChem_names().equalsIgnoreCase(chemistNames.get(position))) {
                rcpaFilterProdArrayListArrayList.add(new RCPAAddedProdList(rcpaAddedProdListArrayList.get(i).getPrd_name(), rcpaAddedProdListArrayList.get(i).getQty(), rcpaAddedProdListArrayList.get(i).getRate(), rcpaAddedProdListArrayList.get(i).getValue(),rcpaAddedProdListArrayList.get(i).getChem_names()));
                rcpaProductsAdapter = new RCPAProductsAdapter(context, rcpaFilterProdArrayListArrayList, rcpa_add_list);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                rv_prd_list.setLayoutManager(mLayoutManager);
                rv_prd_list.setAdapter(rcpaProductsAdapter);
            }
        }

        holder.chemist_name.setText(chemistNames.get(position));

        holder.img_del.setOnClickListener(view -> {
            removeAt(position);
            if (rcpaAddedProdListArrayList.size() > 0) {
                rcpaAddedProdListArrayList.remove(position);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
