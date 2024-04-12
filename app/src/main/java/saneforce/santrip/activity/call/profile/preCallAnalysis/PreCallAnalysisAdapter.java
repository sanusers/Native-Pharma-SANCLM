package saneforce.santrip.activity.call.profile.preCallAnalysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.DCRCallActivity;

public class PreCallAnalysisAdapter extends RecyclerView.Adapter<PreCallAnalysisAdapter.Viewholder> {
    ArrayList<PreCallAnalysisModelClass> list;
    Context context;

    public PreCallAnalysisAdapter(ArrayList<PreCallAnalysisModelClass> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PreCallAnalysisAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.precall_table_item, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreCallAnalysisAdapter.Viewholder holder, int position) {

        PreCallAnalysisModelClass mlist = list.get(position);
        holder.txt_productname.setText(mlist.getProductname());
        holder.txt_sample.setText(mlist.getSample());
        holder.txt_rx.setText(mlist.getRx());
        holder.txt_rcpa.setText(mlist.getRcpa());
        holder.txt_feedback.setText(mlist.getFeedBack());

        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (PreCallAnalysisFragment.PrdSamNeed.equalsIgnoreCase("1")) {
                    holder.txt_sample.setVisibility(View.VISIBLE);
                    holder.view_sample.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_sample.setVisibility(View.GONE);
                    holder.view_sample.setVisibility(View.GONE);
                }
                if (PreCallAnalysisFragment.PrdRxNeed.equalsIgnoreCase("1")) {
                    holder.txt_rx.setVisibility(View.VISIBLE);
                    holder.view_rx.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_rx.setVisibility(View.GONE);
                    holder.view_rx.setVisibility(View.GONE);
                }

                if (PreCallAnalysisFragment.RCPANeed.equalsIgnoreCase("1")) {
                    holder.txt_rcpa.setVisibility(View.VISIBLE);
                    holder.view_rcpa.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_rcpa.setVisibility(View.GONE);
                    holder.view_rcpa.setVisibility(View.GONE);
                }
             break;
            case "2":
                if (PreCallAnalysisFragment.PrdSamNeed.equalsIgnoreCase("1")) {
                    holder.txt_sample.setVisibility(View.VISIBLE);
                    holder.view_sample.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_sample.setVisibility(View.GONE);
                    holder.view_sample.setVisibility(View.GONE);
                }
                if (PreCallAnalysisFragment.PrdRxNeed.equalsIgnoreCase("0")) {
                    holder.txt_rx.setVisibility(View.VISIBLE);
                    holder.view_rx.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_rx.setVisibility(View.GONE);
                    holder.view_rx.setVisibility(View.GONE);
                }

                if (PreCallAnalysisFragment.RCPANeed.equalsIgnoreCase("1")) {
                    holder.txt_rcpa.setVisibility(View.VISIBLE);
                    holder.view_rcpa.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_rcpa.setVisibility(View.GONE);
                    holder.view_rcpa.setVisibility(View.GONE);
                }
             break;
            case "3":
            case "4":
            case "5":
            case "6":
                if (PreCallAnalysisFragment.PrdSamNeed.equalsIgnoreCase("0")) {
                    holder.txt_sample.setVisibility(View.VISIBLE);
                    holder.view_sample.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_sample.setVisibility(View.GONE);
                    holder.view_sample.setVisibility(View.GONE);
                }
                if (PreCallAnalysisFragment.PrdRxNeed.equalsIgnoreCase("0")) {
                    holder.txt_rx.setVisibility(View.VISIBLE);
                    holder.view_rx.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_rx.setVisibility(View.GONE);
                    holder.view_rx.setVisibility(View.GONE);
                }

                if (PreCallAnalysisFragment.RCPANeed.equalsIgnoreCase("0")) {
                    holder.txt_rcpa.setVisibility(View.VISIBLE);
                    holder.view_rcpa.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_rcpa.setVisibility(View.GONE);
                    holder.view_rcpa.setVisibility(View.GONE);
                }
                break;
        }


    /*    if (PreCallAnalysisFragment.PrdSamNeed.equalsIgnoreCase("0")) {
            holder.txt_sample.setVisibility(View.VISIBLE);
            holder.view_sample.setVisibility(View.VISIBLE);
        } else {
            holder.txt_sample.setVisibility(View.GONE);
            holder.view_sample.setVisibility(View.GONE);
        }

        if (PreCallAnalysisFragment.PrdRxNeed.equalsIgnoreCase("0")) {
            holder.txt_rx.setVisibility(View.VISIBLE);
            holder.view_rx.setVisibility(View.VISIBLE);
        } else {
            holder.txt_rx.setVisibility(View.GONE);
            holder.view_rx.setVisibility(View.GONE);
        }

        if (PreCallAnalysisFragment.RCPANeed.equalsIgnoreCase("1")) {
            holder.txt_rcpa.setVisibility(View.VISIBLE);
            holder.view_rcpa.setVisibility(View.VISIBLE);
        } else {
            holder.txt_rx.setVisibility(View.GONE);
            holder.view_rcpa.setVisibility(View.GONE);
        }*/


        if (list.size() - 1 == position) {
            holder.view.setVisibility(View.VISIBLE);
        } else {
            holder.view.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        TextView txt_productname, txt_sample, txt_rx, txt_rcpa, txt_feedback;
        View view, view_sample, view_rx, view_rcpa;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            txt_productname = itemView.findViewById(R.id.prod_name);
            txt_sample = itemView.findViewById(R.id.sample_qty);
            txt_rx = itemView.findViewById(R.id.rx_qty);
            txt_rcpa = itemView.findViewById(R.id.rcpa_qty);
            txt_feedback = itemView.findViewById(R.id.feedback);
            view_sample = itemView.findViewById(R.id.dummy_sample);
            view_rx = itemView.findViewById(R.id.dummy_rx);
            view_rcpa = itemView.findViewById(R.id.dummy_rcpa);
            view = itemView.findViewById(R.id.last_line);
        }
    }
}
