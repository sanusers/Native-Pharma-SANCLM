package saneforce.sanzen.activity.myresource.Categoryview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;


public class Category_adapter extends   RecyclerView.Adapter<Category_adapter.ViewHolder> {
    Context context;
    ArrayList<callstatus_model> listeduser;

    public Category_adapter(Context context, ArrayList<callstatus_model> listeduser) {
        this.context = context;
        this.listeduser = listeduser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final callstatus_model app_adapt = listeduser.get(position);
        String count = String.valueOf((position + 1));
        holder.count.setText(count +")");
        holder.Category.setText(app_adapt.getCustType());
        holder.Doc_name.setText(app_adapt.getCustCode());

    }

    @Override
    public int getItemCount() {
        return listeduser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView count,Doc_name,Category;

//        LinearLayout cs_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            count = (itemView).findViewById(R.id.count);
            Doc_name = (itemView).findViewById(R.id.Doc_name);
            Category = (itemView).findViewById(R.id.Category);


        }
    }
}
