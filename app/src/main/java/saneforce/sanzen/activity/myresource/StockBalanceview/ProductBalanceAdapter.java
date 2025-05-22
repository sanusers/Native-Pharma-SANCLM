package saneforce.sanzen.activity.myresource.StockBalanceview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.Categoryview.Category_adapter;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;

public class ProductBalanceAdapter  extends   RecyclerView.Adapter<ProductBalanceAdapter.ViewHolder> {
    Context context;
    ArrayList<StockModelClass> prdlist;

    public ProductBalanceAdapter(Context context, ArrayList<StockModelClass> prductlist) {
        this.context = context;
        this.prdlist = prductlist;
    }

    @NonNull
    @Override
    public ProductBalanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_view, parent, false);
        return new ProductBalanceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductBalanceAdapter.ViewHolder holder, int position) {
        final StockModelClass prd_adapt = prdlist.get(position);
        String count = String.valueOf((position + 1));
        holder.count.setText(count +")");
        holder.prdname.setText(prd_adapt.getName());
        holder.balance.setText("Available: "+prd_adapt.getBalance_Stock());

    }

    @Override
    public int getItemCount() {
        return prdlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView count,prdname,balance;

//        LinearLayout cs_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            count = (itemView).findViewById(R.id.count);
            prdname = (itemView).findViewById(R.id.prdname);
            balance = (itemView).findViewById(R.id.Balance);
        }
    }
}
