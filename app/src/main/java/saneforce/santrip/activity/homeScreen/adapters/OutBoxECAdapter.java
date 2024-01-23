package saneforce.santrip.activity.homeScreen.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;

public class OutBoxECAdapter extends RecyclerView.Adapter<OutBoxECAdapter.ViewHolder> {
    Context context;
    ArrayList<EcModelClass> ecModelClasses;

    public OutBoxECAdapter(Context context, ArrayList<EcModelClass> ecModelClasses) {
        this.context = context;
        this.ecModelClasses = ecModelClasses;
    }

    @NonNull
    @Override
    public OutBoxECAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_ec_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutBoxECAdapter.ViewHolder holder, int position) {
        Log.v("outBoxCall","---" + ecModelClasses.get(position).getName());
        holder.tvImageName.setText(ecModelClasses.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return ecModelClasses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvImageName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImageName = itemView.findViewById(R.id.tvImageName);
        }
    }
}
