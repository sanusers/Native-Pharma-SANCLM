package saneforce.sanzen.activity.reports.dayReport.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.model.DayReportRcpaModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class ReoportRcpaAdapter extends RecyclerView.Adapter<ReoportRcpaAdapter.ViewHolder> {

    ArrayList<DayReportRcpaModelClass> rcpaList = new ArrayList<>();
    Context context;
    CommonUtilsMethods commonUtilsMethods;


    public ReoportRcpaAdapter(ArrayList<DayReportRcpaModelClass> rcpaList, Context context) {
        this.rcpaList = rcpaList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReoportRcpaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_rcpa_child_table_item, parent, false);
        return new ReoportRcpaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReoportRcpaAdapter.ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.prdName.setText(rcpaList.get(position).getOPName().length() > 16 ? rcpaList.get(position).getOPName().substring(0, 16) + "..." : rcpaList.get(position).getOPName());
        holder.prdqty.setText(rcpaList.get(position).getOPQty());
        holder.CompetitorName.setText(rcpaList.get(position).getChmName().length() > 16 ? rcpaList.get(position).getChmName().substring(0, 16) + "..." : rcpaList.get(position).getChmName());
        holder.CompetitorProductName.setText(rcpaList.get(position).getCompPName().length() > 16 ? rcpaList.get(position).getCompPName().substring(0, 16) + "..." : rcpaList.get(position).getCompPName());
        holder.ComprdQty.setText(rcpaList.get(position).getCPQty());
        holder.comName.setText(rcpaList.get(position).getCompName().length() > 16 ? rcpaList.get(position).getCompName().substring(0, 16) + "..." : rcpaList.get(position).getCompName());
        int opValue = Integer.parseInt(rcpaList.get(position).getOPValue());
        int cpValue = Integer.parseInt(rcpaList.get(position).getCPValue());
        int totValue = opValue+cpValue;





        if (!rcpaList.get(position).getCPRemarks().isEmpty()) {
            holder.img_remarks.setEnabled(true);
            holder.img_remarks.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_remarks_0));
        } else {
            holder.img_remarks.setEnabled(false);
            holder.img_remarks.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_remarks_1));
        }






        holder.prdName.setOnClickListener(v -> {
            popUp(v, rcpaList.get(position).getOPName());
        });
        holder.CompetitorName.setOnClickListener(v -> {
            popUp(v, rcpaList.get(position).getChmName());
        });
        holder.CompetitorProductName.setOnClickListener(v -> {
            popUp(v, rcpaList.get(position).getCompPName());
        });
        holder.comName.setOnClickListener(v -> {
            popUp(v, rcpaList.get(position).getCompName());
        });
        holder.img_remarks.setOnClickListener(v -> {
            popUp(v, rcpaList.get(position).getCPRemarks());
        });
        holder.infoView.setOnClickListener(v -> {
            ratePopUp(v,rcpaList.get(position).getOPRate(),rcpaList.get(position).getOPQty(),rcpaList.get(position).getOPRate(),rcpaList.get(position).getOPValue(),rcpaList.get(position).getCPQty(),rcpaList.get(position).getCPRate(),rcpaList.get(position).getCPValue(),totValue);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return rcpaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView prdName, prdqty, CompetitorName, CompetitorProductName, ComprdQty, comName;
        View infoView;
        ImageView img_remarks;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            prdName = itemView.findViewById(R.id.productname);
            prdqty = itemView.findViewById(R.id.productqty);
            CompetitorName = itemView.findViewById(R.id.competitorname);
            CompetitorProductName = itemView.findViewById(R.id.competitorproductname);
            ComprdQty = itemView.findViewById(R.id.competitorproductnameqty);
            comName = itemView.findViewById(R.id.chemistName);
            infoView = itemView.findViewById(R.id.infoView);
            img_remarks = itemView.findViewById(R.id.img_remarks);
            layout = itemView.findViewById(R.id.layOut);
        }
    }

    private void popUp(View v, String name) {
        PopupWindow popup = new PopupWindow(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.popup_text, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_name = layout.findViewById(R.id.tv_name);
        tv_name.setText(name);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(v);
    }



    private void ratePopUp(View v,String rate,String opQty,String opRate,String opValue,String cpQty,String cpRate,String cpValue,int totValue) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.dialog_rcpa_rate, null);
        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setOutsideTouchable(true);
        TextView textOpQty = popupView.findViewById(R.id.textOpQty);
        TextView txtOpRate = popupView.findViewById(R.id.txtOpRate);
        TextView textOpValue = popupView.findViewById(R.id.textOpValue);
        TextView textCompQty = popupView.findViewById(R.id.textCompQty);
        TextView txtCompRate = popupView.findViewById(R.id.txtCompRate);
        TextView textCompValue = popupView.findViewById(R.id.textCompValue);
        TextView textTotal = popupView.findViewById(R.id.textTotalValue);
        textOpQty.setText(opQty);
        txtOpRate.setText(opRate);
        textOpValue.setText(opValue);
        textCompQty.setText(cpQty);
        txtCompRate.setText(cpRate);
        textCompValue.setText(cpValue);
        textTotal.setText(String.valueOf(totValue));
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int infoViewWidth = v.getMeasuredWidth();
        int popupWidth = popupView.getMeasuredWidth();
        int xOffset = infoViewWidth - popupWidth;
        popupWindow.showAsDropDown(v, xOffset, 0);
    }

}
