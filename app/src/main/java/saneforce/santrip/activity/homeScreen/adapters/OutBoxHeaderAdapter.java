package saneforce.santrip.activity.homeScreen.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.fragment.OutboxFragment;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;

public class OutBoxHeaderAdapter extends RecyclerView.Adapter<OutBoxHeaderAdapter.listDataViewholider> {
    Context context;
    ArrayList<GroupModelClass> groupModelClasses;
    OutBoxContentAdapter outBoxContentAdapter;


    public OutBoxHeaderAdapter(Context context, ArrayList<GroupModelClass> groupModelClasses) {
        this.context = context;
        this.groupModelClasses = groupModelClasses;
    }

    @NonNull
    @Override
    public OutBoxHeaderAdapter.listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outbox_group_view, parent, false);
        return new OutBoxHeaderAdapter.listDataViewholider(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxHeaderAdapter.listDataViewholider holder, int position) {
        GroupModelClass groupModelClass = groupModelClasses.get(position);
        holder.tvDate.setText(groupModelClass.getGroupName());

        if (groupModelClass.isExpanded()) {
            holder.constraintContent.setVisibility(View.VISIBLE);
            outBoxContentAdapter = new OutBoxContentAdapter(context, groupModelClass.getChildItems());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            holder.rvContentList.setLayoutManager(mLayoutManager);
            holder.rvContentList.setAdapter(outBoxContentAdapter);
            holder.ivExpand.setImageResource(R.drawable.top_vector);
        } else {
            holder.constraintContent.setVisibility(View.GONE);
            holder.ivExpand.setImageResource(R.drawable.down_arrow);
        }


        holder.cardView.setOnClickListener(v -> {
            groupModelClass.setExpanded(Objects.equals(holder.ivExpand.getDrawable().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.down_arrow)).getConstantState()));
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return groupModelClasses.size();
    }

    public static class listDataViewholider extends RecyclerView.ViewHolder {
        TextView tvDate;
        ImageView ivSync, ivExpand;
        ConstraintLayout constraintContent;
        RecyclerView rvContentList;
        CardView cardView;

        public listDataViewholider(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.text_date);
            ivSync = itemView.findViewById(R.id.img_sync_all);
            ivExpand = itemView.findViewById(R.id.txt_expand_status);
            constraintContent = itemView.findViewById(R.id.constraint_rv);
            rvContentList = itemView.findViewById(R.id.rv_outbox_list);
            cardView = itemView.findViewById(R.id.card_view_top);
        }
    }
}