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
import saneforce.sanzen.commonClasses.SafeClickListener;

public class InputBalanceAdapter  extends   RecyclerView.Adapter<InputBalanceAdapter.ViewHolder> {
    Context context;
    ArrayList<StockInputModel> inputlist;

    public InputBalanceAdapter(Context context, ArrayList<StockInputModel> inputlist) {
        this.context = context;
        this.inputlist = inputlist;
    }

    @NonNull
    @Override
    public InputBalanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_view, parent, false);
        return new InputBalanceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputBalanceAdapter.ViewHolder holder, int position) {
        final StockInputModel inp_adapt = inputlist.get(position);
        String count = String.valueOf((position + 1));
        holder.count.setText(count +")");
        holder.prdname.setText(inp_adapt.getName());
        holder.balance.setText("Available: "+inp_adapt.getBalance_Stock());

    }

    @Override
    public int getItemCount() {
        return inputlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView count,prdname,balance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            count = (itemView).findViewById(R.id.count);
            prdname = (itemView).findViewById(R.id.prdname);
            balance = (itemView).findViewById(R.id.Balance);
        }
    }
}

