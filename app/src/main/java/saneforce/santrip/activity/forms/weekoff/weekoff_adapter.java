package saneforce.santrip.activity.forms.weekoff;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;


public class weekoff_adapter extends RecyclerView.Adapter<weekoff_adapter.ViewHolder> {

    ArrayList<fromsmodelclass> resList;
    Context context;

    String[] val={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    public weekoff_adapter(ArrayList<fromsmodelclass> resList, Context context) {
        this.resList = resList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewed_weekoff, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final fromsmodelclass app_adapt = resList.get(position);
        holder.weekoffl.setBackgroundColor(Color.parseColor(app_adapt.getPos()));

            int count= Integer.parseInt(app_adapt.getValus());
            holder.days_name.setText(val[count]);
    }

    @Override
    public int getItemCount() {
        return resList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView days_name;

        ImageView imgscr;
        LinearLayout weekoffl,weekoff_backgrd;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            days_name=itemView.findViewById(R.id.days_name);
            imgscr=itemView.findViewById(R.id.imgscr);
            weekoffl=itemView.findViewById(R.id.weekoffl);
        }
    }
}
