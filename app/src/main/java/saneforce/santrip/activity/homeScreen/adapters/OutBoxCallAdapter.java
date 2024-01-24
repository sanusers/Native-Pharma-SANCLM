package saneforce.santrip.activity.homeScreen.adapters;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.storage.SQLite;

public class OutBoxCallAdapter extends RecyclerView.Adapter<OutBoxCallAdapter.ViewHolder> {
    Context context;
    ArrayList<OutBoxCallList> outBoxCallLists;
    SQLite sqLite;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    CommonUtilsMethods commonUtilsMethods;


    public OutBoxCallAdapter(Context context, ArrayList<OutBoxCallList> outBoxCallLists) {
        this.context = context;
        this.outBoxCallLists = outBoxCallLists;
        sqLite = new SQLite(context);
        commonUtilsMethods = new CommonUtilsMethods(context);

    }

    @NonNull
    @Override
    public OutBoxCallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_child_view, parent, false);
        return new OutBoxCallAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutBoxCallAdapter.ViewHolder holder, int position) {

        String type = outBoxCallLists.get(position).getCusType();
        if (type.equalsIgnoreCase("1")) {
            holder.tvName.setText(String.format("%s (Doctor) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_dr_img);
        } else if (type.equalsIgnoreCase("2")) {
            holder.tvName.setText(String.format("%s (Chemist) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_chemist_img);
        } else if (type.equalsIgnoreCase("3")) {
            holder.tvName.setText(String.format("%s (Stockiest) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_stockist_img);
        } else if (type.equalsIgnoreCase("4")) {
            holder.tvName.setText(String.format("%s (UnDr) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_unlistdr_img);
        } else if (type.equalsIgnoreCase("5")) {
            holder.tvName.setText(String.format("%s (CIP) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_cip_img);
        } else if (type.equalsIgnoreCase("6")) {
            holder.tvName.setText(String.format("%s (HOS) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.tp_hospital_icon);
        }

        holder.tvInOut.setText(String.format("IN - %s OUT - %s", outBoxCallLists.get(position).getIn(), outBoxCallLists.get(position).getOut()));
        holder.tvStatus.setText(outBoxCallLists.get(position).getStatus());

        holder.tvMenu.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
            popup.inflate(R.menu.call_menu);
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menuEdit) {
                    Intent intent = new Intent(context, DCRCallActivity.class);
                    CallActivityCustDetails = new ArrayList<>();
                    CallActivityCustDetails.add(0, new CustList(outBoxCallLists.get(position).getCusName(), outBoxCallLists.get(position).getCusCode(), type, "", "", outBoxCallLists.get(position).getJsonData()));
                    intent.putExtra("isDetailedRequired", "false");
                    intent.putExtra("from_activity", "edit_local");
                    context.startActivity(intent);
                } else if (menuItem.getItemId() == R.id.menuDelete) {
                    sqLite.deleteOfflineCalls(outBoxCallLists.get(position).getCusCode(), outBoxCallLists.get(position).getCusName(), outBoxCallLists.get(position).getDates());
                    removeAt(position);
                }
                return true;
            });
            popup.show();
        });

    }

    @Override
    public int getItemCount() {
        return outBoxCallLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        outBoxCallLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, outBoxCallLists.size());
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(context, listDates);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInOut, tvStatus, tvMenu;
        ImageView imgPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textViewLabel1);
            tvInOut = itemView.findViewById(R.id.textViewLabel2);
            tvStatus = itemView.findViewById(R.id.tv_call_status);
            tvMenu = itemView.findViewById(R.id.optionview);
            imgPic = itemView.findViewById(R.id.profile_icon);
        }
    }
}
