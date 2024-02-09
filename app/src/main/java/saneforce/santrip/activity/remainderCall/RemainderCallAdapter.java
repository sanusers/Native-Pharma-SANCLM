package saneforce.santrip.activity.remainderCall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class RemainderCallAdapter extends RecyclerView.Adapter<RemainderCallAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CustList> custListArrayList;
    CommonUtilsMethods commonUtilsMethods;
    Dialog dialogRemarks;

    public RemainderCallAdapter(Context context, Activity activity, ArrayList<CustList> custListArrayList) {
        this.context = context;
        this.activity = activity;
        this.custListArrayList = custListArrayList;
        commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public RemainderCallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_remainder_call, parent, false);
        return new RemainderCallAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemainderCallAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(custListArrayList.get(position).getName());
        holder.tv_category.setText(custListArrayList.get(position).getCategory());
        holder.tv_specialist.setText(custListArrayList.get(position).getSpecialist());
        holder.tv_area.setText(custListArrayList.get(position).getTown_name());

        holder.tv_name.setOnClickListener(view -> {
            commonUtilsMethods.displayPopupWindow(activity, context, view, custListArrayList.get(position).getName());
        });

        holder.constraint_main.setOnClickListener(v -> {
            dialogRemarks = new Dialog(context);
            dialogRemarks.setContentView(R.layout.popup_rcpa_remarks);
            Objects.requireNonNull(dialogRemarks.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogRemarks.setCancelable(false);

            ImageView iv_close = dialogRemarks.findViewById(R.id.img_close);
            TextView tvHead = dialogRemarks.findViewById(R.id.tv_head);
            tvHead.setText(custListArrayList.get(position).getName());
            EditText ed_remark = dialogRemarks.findViewById(R.id.ed_remark);
            Button btn_clear = dialogRemarks.findViewById(R.id.btn_clear);
            Button btn_save = dialogRemarks.findViewById(R.id.btn_save);
            ed_remark.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_remark)});

            iv_close.setOnClickListener(view1 -> {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
                ed_remark.setText("");
                dialogRemarks.dismiss();
            });

            btn_clear.setOnClickListener(view12 -> {
                ed_remark.setText("");
                ed_remark.setHint("Type your Remarks");
            });

            btn_save.setOnClickListener(view13 -> {
                if (!TextUtils.isEmpty(ed_remark.getText().toString())) {
                    dialogRemarks.dismiss();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
                } else {
                    commonUtilsMethods.ShowToast(context, context.getString(R.string.toast_enter_remarks), 100);
                }
            });
            dialogRemarks.show();
        });
    }

    @Override
    public int getItemCount() {
        return custListArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CustList> filteredNames) {
        this.custListArrayList = filteredNames;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_category, tv_specialist, tv_area;
        ConstraintLayout constraint_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.txt_cust_name);
            tv_category = itemView.findViewById(R.id.txt_cat);
            tv_specialist = itemView.findViewById(R.id.txt_specialist);
            tv_area = itemView.findViewById(R.id.txt_address);
            constraint_main = itemView.findViewById(R.id.constraint_main);
        }
    }
}