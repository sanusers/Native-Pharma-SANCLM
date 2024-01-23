package saneforce.santrip.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.activity.homeScreen.adapters.OutBoxHeaderAdapter;
import saneforce.santrip.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.databinding.OutboxFragmentBinding;
import saneforce.santrip.storage.SQLite;


public class OutboxFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static OutboxFragmentBinding outBoxBinding;
    public static ArrayList<GroupModelClass> listDates = new ArrayList<>();
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SQLite sqLite;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        outBoxBinding = OutboxFragmentBinding.inflate(inflater, container, false);
        View v = outBoxBinding.getRoot();
        sqLite = new SQLite(requireContext());
        listDates = sqLite.getOutBoxDate();

        outBoxHeaderAdapter = new OutBoxHeaderAdapter(requireContext(), listDates);
        mLayoutManager = new LinearLayoutManager(requireContext());
        outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);

        outBoxBinding.clearCalls.setOnClickListener(v1 -> {
            sqLite.deleteOfflineCalls();
            listDates.clear();
            outBoxHeaderAdapter = new OutBoxHeaderAdapter(requireContext(), listDates);
            outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
            outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        });

        return v;
    }
}


   /* public static class OutBoxHeaderAdapter extends RecyclerView.Adapter<OutBoxHeaderAdapter.listDataViewholider> {
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
            return new listDataViewholider(view);
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
    }*/

   /* public static class OutBoxContentAdapter extends RecyclerView.Adapter<OutBoxContentAdapter.listDataViewholider> {
        Context context;
        ArrayList<ChildListModelClass> childListModelClasses;
        OfflineCallAdapter offlineCallAdapter;

        public OutBoxContentAdapter(Context context, ArrayList<ChildListModelClass> groupModelClasses) {
            this.context = context;
            this.childListModelClasses = groupModelClasses;
        }

        @NonNull
        @Override
        public OutBoxContentAdapter.listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outbox_content_view, parent, false);
            return new OutBoxContentAdapter.listDataViewholider(view);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull OutBoxContentAdapter.listDataViewholider holder, int position) {
            ChildListModelClass contentList = childListModelClasses.get(position);
            Log.v("outBox", "---" + contentList.getChildName() + "--count--" + childListModelClasses.size() + "----" + contentList.isAvailableList() + "---" + contentList.getCounts());
            holder.tvContentList.setText(contentList.getChildName());

            if (contentList.isAvailableList()) {
                holder.expandContentView.setEnabled(true);
                holder.img_expand_child.setVisibility(View.VISIBLE);
                holder.tvCount.setVisibility(View.VISIBLE);
                holder.tvCount.setText(String.valueOf(contentList.getOutBoxCallLists().size()));

                if (contentList.isExpanded() && contentList.getOutBoxCallLists().size() > 0) {
                    holder.constraintRv.setVisibility(View.VISIBLE);
                    offlineCallAdapter = new OfflineCallAdapter(context, contentList.getOutBoxCallLists());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    holder.rv_outbox_list.setLayoutManager(mLayoutManager);
                    holder.rv_outbox_list.setAdapter(offlineCallAdapter);
                    holder.tvContentList.setText(contentList.getChildName());
                    holder.img_expand_child.setImageResource(R.drawable.top_vector);
                } else {
                    holder.constraintRv.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }

            } else {
                holder.expandContentView.setEnabled(false);
                holder.constraintRv.setVisibility(View.GONE);
                holder.img_expand_child.setVisibility(View.GONE);
                holder.tvCount.setVisibility(View.GONE);
            }


            holder.expandContentView.setOnClickListener(v -> {
                contentList.setExpanded(Objects.equals(holder.img_expand_child.getDrawable().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.down_arrow)).getConstantState()));
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return childListModelClasses.size();
        }

        public static class listDataViewholider extends RecyclerView.ViewHolder {
            TextView tvContentList, tvCount;
            ImageView sync, img_expand_child;
            ConstraintLayout constraintRv;
            CardView expandContentView;
            RecyclerView rv_outbox_list;

            public listDataViewholider(@NonNull View view) {
                super(view);
                tvContentList = view.findViewById(R.id.textViewLabel1);
                tvCount = view.findViewById(R.id.textViewcount);
                sync = view.findViewById(R.id.img_sync);
                img_expand_child = view.findViewById(R.id.img_expand);
                expandContentView = view.findViewById(R.id.card_content_view);
                rv_outbox_list = view.findViewById(R.id.rv_outbox_list);
                constraintRv = view.findViewById(R.id.constraint_rv);
            }
        }
    }
}*/