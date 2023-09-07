package saneforce.sanclm.activity.HomeScreen.Adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import saneforce.sanclm.R;

public class DCR_CallCountAdapter extends RecyclerView.Adapter<DCR_CallCountAdapter.listDataViewholider> {

    ArrayList<String> list = new ArrayList<>();
    public int with;
    Context context;
    Resources resources;

    public DCR_CallCountAdapter(ArrayList<String> list, int with, Context context) {

        this.with = with;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dcr_call_count_view_item, parent, false);
        return new listDataViewholider(view);

    }

    @Override
    public void onBindViewHolder(@NonNull listDataViewholider holder, int position) {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(with, ViewGroup.LayoutParams.MATCH_PARENT);

        param.setMargins(10, 10, 10, 10);
        holder.linearLayout.setLayoutParams(param);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(8);


        if (list.get(position).startsWith("Slide")) {
            gradientDrawable.setColor(Color.parseColor("#00c98e"));
            holder.txt_value2.setText("Slide Detailed to doctor& Chemist with Duration");
            holder.progress_bar.setVisibility(View.GONE);
            holder.txt_value.setVisibility(View.GONE);
            holder.txt_value2.setVisibility(View.VISIBLE);
            holder.txt_value3.setVisibility(View.GONE);


        } else if (list.get(position).startsWith("Brand")) {


            gradientDrawable.setColor(Color.parseColor("#41a1ef"));
            holder.txt_value2.setText("Brand Detailed to doctor& Chemist with Duration");
            holder.progress_bar.setVisibility(View.GONE);
            holder.txt_value.setVisibility(View.GONE);
            holder.txt_value2.setVisibility(View.VISIBLE);
            holder.txt_value3.setVisibility(View.GONE);

        } else if (list.get(position).startsWith("Specialty")) {

            gradientDrawable.setColor(Color.parseColor("#fd4f6e"));
            holder.txt_value2.setText("Specialty Detailed to doctor& Chemist with Duration");
            holder.progress_bar.setVisibility(View.GONE);
            holder.txt_value.setVisibility(View.GONE);
            holder.txt_value2.setVisibility(View.VISIBLE);
            holder.txt_value3.setVisibility(View.GONE);

        } else if (list.get(position).startsWith("2")) {

            gradientDrawable.setColor(Color.parseColor("#fd7f4e"));
            holder.txt_value.setText("13/88");
            holder.progress_bar.setVisibility(View.GONE);
            holder.txt_value.setVisibility(View.GONE);
            holder.txt_value2.setVisibility(View.VISIBLE);
            holder.txt_value3.setVisibility(View.GONE);
        }
        if (list.get(position).startsWith("Doctor")) {

            gradientDrawable.setColor(Color.parseColor("#00c98e"));
            holder.txt_value.setText("55/75");

            holder.progress_bar.setVisibility(View.VISIBLE);
            holder.txt_value.setVisibility(View.VISIBLE);
            holder.txt_value2.setVisibility(View.GONE);
            holder.txt_value3.setVisibility(View.VISIBLE);


        } else if (list.get(position).startsWith("Chemist")) {

            gradientDrawable.setColor(Color.parseColor("#41a1ef"));
            holder.txt_value.setText("55/75");
            holder.progress_bar.setVisibility(View.VISIBLE);
            holder.txt_value.setVisibility(View.VISIBLE);
            holder.txt_value2.setVisibility(View.GONE);
            holder.txt_value3.setVisibility(View.VISIBLE);

        } else if (list.get(position).startsWith("Stockiest")) {
            gradientDrawable.setColor(Color.parseColor("#fd4f6e"));
            holder.txt_value.setText("3/10");
            holder.progress_bar.setVisibility(View.VISIBLE);
            holder.txt_value.setVisibility(View.VISIBLE);
            holder.txt_value2.setVisibility(View.GONE);
            holder.txt_value3.setVisibility(View.VISIBLE);

        } else if (list.get(position).startsWith("Unlisted")) {
            gradientDrawable.setColor(Color.parseColor("#fd7f4e"));
            holder.txt_value.setText("13/88");
            holder.progress_bar.setVisibility(View.VISIBLE);
            holder.txt_value.setVisibility(View.VISIBLE);
            holder.txt_value2.setVisibility(View.GONE);
            holder.txt_value3.setVisibility(View.VISIBLE);
        }

        holder.linearLayout.setBackground(gradientDrawable);
        holder.txt_name.setText(list.get(position));


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class listDataViewholider extends RecyclerView.ViewHolder {

        RelativeLayout linearLayout;
        TextView txt_name, txt_value, txt_value2,txt_value3;
        ;
        ProgressBar progress_bar;

        public listDataViewholider(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.count_linear_layout);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_value = itemView.findViewById(R.id.txt_name1);
            txt_value2 = itemView.findViewById(R.id.txt_name2);
            txt_value3 = itemView.findViewById(R.id.progressValueTextView);
            progress_bar = itemView.findViewById(R.id.progress_bar);
        }
    }
}