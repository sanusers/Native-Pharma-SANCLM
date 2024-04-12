package saneforce.santrip.activity.approvals.tp.adapter;

import static saneforce.santrip.activity.approvals.tp.TpApprovalActivity.SelectedDay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.tp.pojo.TpDetailedModel;

public class TpApprovalDetailedAdapter extends RecyclerView.Adapter<TpApprovalDetailedAdapter.ViewHolder> {
    Context context;
    ArrayList<TpDetailedModel> tpDetailedList;
    ArrayList<TpDetailedModel> tpClickedList = new ArrayList<>();
    TpClickViewDetailsAdapter tpClickViewDetailsAdapter;
    String wtName;

    public TpApprovalDetailedAdapter(Context context, ArrayList<TpDetailedModel> tpModelLists) {
        this.context = context;
        this.tpDetailedList = tpModelLists;
    }

    @NonNull
    @Override
    public TpApprovalDetailedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tp_detailed_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull TpApprovalDetailedAdapter.ViewHolder holder, int position) {
        holder.tvDayNo.setText(tpDetailedList.get(position).getDayNo());

        if (SelectedDay.equalsIgnoreCase(tpDetailedList.get(position).getDayNo())) {
            holder.rvViewDetailed.setVisibility(View.VISIBLE);
            holder.listArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.up_arrow));
            tpClickedList = new ArrayList<>();

            tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getClusterName(), tpDetailedList.get(position).getFWFlg(), tpDetailedList.get(position).getDayRemarks(), tpDetailedList.get(position).getDrName(), tpDetailedList.get(position).getChemistName(), tpDetailedList.get(position).getStockiestName(), tpDetailedList.get(position).getJwName()));

            if (!tpDetailedList.get(position).getWtName2().isEmpty())
                tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName2(), tpDetailedList.get(position).getClusterName2(), tpDetailedList.get(position).getFWFlg2(), tpDetailedList.get(position).getDayRemarks2(), tpDetailedList.get(position).getDrName2(), tpDetailedList.get(position).getChemistName2(), tpDetailedList.get(position).getStockiestName2(), tpDetailedList.get(position).getJwName2()));

            if (!tpDetailedList.get(position).getWtName3().isEmpty())
                tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName3(), tpDetailedList.get(position).getClusterName3(), tpDetailedList.get(position).getFWFlg3(), tpDetailedList.get(position).getDayRemarks3(), tpDetailedList.get(position).getDrName3(), tpDetailedList.get(position).getChemistName3(), tpDetailedList.get(position).getStockiestName3(), tpDetailedList.get(position).getJwName3()));

          /*  if (tpDetailedList.get(position).getWtName2().isEmpty()) {
                tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getClusterName(), tpDetailedList.get(position).getFWFlg(), tpDetailedList.get(position).getDayRemarks(), tpDetailedList.get(position).getDrName()));
            } else if (tpDetailedList.get(position).getWtName3().isEmpty()) {
                tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getClusterName(), tpDetailedList.get(position).getFWFlg(), tpDetailedList.get(position).getDayRemarks(), tpDetailedList.get(position).getDrName()));
                tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName2(), tpDetailedList.get(position).getClusterName2(), tpDetailedList.get(position).getFWFlg2(), tpDetailedList.get(position).getDayRemarks2(), tpDetailedList.get(position).getDrName2()));
            } else {
                tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getClusterName(), tpDetailedList.get(position).getFWFlg(), tpDetailedList.get(position).getDayRemarks(), tpDetailedList.get(position).getDrName()));
                tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName2(), tpDetailedList.get(position).getClusterName2(), tpDetailedList.get(position).getFWFlg2(), tpDetailedList.get(position).getDayRemarks2(), tpDetailedList.get(position).getDrName2()));
                tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName3(), tpDetailedList.get(position).getClusterName3(), tpDetailedList.get(position).getFWFlg3(), tpDetailedList.get(position).getDayRemarks3(), tpDetailedList.get(position).getDrName3()));
            }*/
            tpClickViewDetailsAdapter = new TpClickViewDetailsAdapter(context, tpClickedList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            holder.rvViewDetailed.setLayoutManager(mLayoutManager);
            holder.rvViewDetailed.setAdapter(tpClickViewDetailsAdapter);
        } else {
            holder.rvViewDetailed.setVisibility(View.GONE);
            holder.listArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drop_down));
        }

        wtName = tpDetailedList.get(position).getWtName();

        if (!tpDetailedList.get(position).getWtName2().isEmpty())
            wtName = wtName + " / " + tpDetailedList.get(position).getWtName2();

        if (!tpDetailedList.get(position).getWtName3().isEmpty())
            wtName = wtName + " / " + tpDetailedList.get(position).getWtName3();
        /*if (tpDetailedList.get(position).getWtName2().isEmpty()) {
            holder..setText(tpDetailedList.get(position).getWtName());
        } else if (tpDetailedList.get(position).getWtName3().isEmpty()) {
            holder.tvWtName.setText(String.format("%s,%s", tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getWtName2()));
        } else {
            holder.tvWtName.setText(String.format("%s,%s,%s", tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getWtName2(), tpDetailedList.get(position).getWtName3()));
        }*/
        holder.tvWtName.setText(wtName);


        holder.cardView.setOnClickListener(v -> {
            if (holder.cardView.isPressed()) {
                if (holder.rvViewDetailed.getVisibility() == View.VISIBLE) {
                    SelectedDay = "";
                } else {
                    SelectedDay = tpDetailedList.get(position).getDayNo();
                }
            /*    if (holder.rvViewDetailed.getVisibility() == View.VISIBLE) {
                    holder.rvViewDetailed.setVisibility(View.GONE);
                    holder.listArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drop_down));
                } else {*/
       /*         holder.rvViewDetailed.setVisibility(View.VISIBLE);
                holder.listArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.up_arrow));
                tpClickedList = new ArrayList<>();
                if (tpDetailedList.get(position).getWtName2().isEmpty()) {
                    tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getClusterName(), tpDetailedList.get(position).getFWFlg(), tpDetailedList.get(position).getDayRemarks(), tpDetailedList.get(position).getDrName()));
                } else if (tpDetailedList.get(position).getWtName3().isEmpty()) {
                    tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getClusterName(), tpDetailedList.get(position).getFWFlg(), tpDetailedList.get(position).getDayRemarks(), tpDetailedList.get(position).getDrName()));
                    tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName2(), tpDetailedList.get(position).getClusterName2(), tpDetailedList.get(position).getFWFlg2(), tpDetailedList.get(position).getDayRemarks2(), tpDetailedList.get(position).getDrName2()));
                } else {
                    tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName(), tpDetailedList.get(position).getClusterName(), tpDetailedList.get(position).getFWFlg(), tpDetailedList.get(position).getDayRemarks(), tpDetailedList.get(position).getDrName()));
                    tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName2(), tpDetailedList.get(position).getClusterName2(), tpDetailedList.get(position).getFWFlg2(), tpDetailedList.get(position).getDayRemarks2(), tpDetailedList.get(position).getDrName2()));
                    tpClickedList.add(new TpDetailedModel(tpDetailedList.get(position).getDayNo(), tpDetailedList.get(position).getWtName3(), tpDetailedList.get(position).getClusterName3(), tpDetailedList.get(position).getFWFlg3(), tpDetailedList.get(position).getDayRemarks3(), tpDetailedList.get(position).getDrName3()));
                }
                tpClickViewDetailsAdapter = new TpClickViewDetailsAdapter(context, tpClickedList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                holder.rvViewDetailed.setLayoutManager(mLayoutManager);
                holder.rvViewDetailed.setAdapter(tpClickViewDetailsAdapter);*/
                notifyDataSetChanged();
                //  }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tpDetailedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNo, tvWtName;
        CardView cardView;
        ImageView listArrow;
        RecyclerView rvViewDetailed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNo = itemView.findViewById(R.id.tv_dayNo);
            tvWtName = itemView.findViewById(R.id.tv_workTypes);
            listArrow = itemView.findViewById(R.id.listArrow);
            rvViewDetailed = itemView.findViewById(R.id.rv_tp_detailed_list);
            cardView = itemView.findViewById(R.id.card_view_top);
        }
    }
}

