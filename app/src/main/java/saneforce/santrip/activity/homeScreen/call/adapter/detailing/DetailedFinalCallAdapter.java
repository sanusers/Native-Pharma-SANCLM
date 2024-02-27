package saneforce.santrip.activity.homeScreen.call.adapter.detailing;

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
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.pojo.detailing.CallDetailingList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class DetailedFinalCallAdapter extends RecyclerView.Adapter<DetailedFinalCallAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CallDetailingList> callDetailingLists;
    CommonUtilsMethods commonUtilsMethods;
    Dialog dialogFeedback;

    public DetailedFinalCallAdapter(Activity activity, Context context, ArrayList<CallDetailingList> callDetailingLists) {
        this.activity = activity;
        this.context = context;
        this.callDetailingLists = callDetailingLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_detailed_call, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_brand_name.setText(callDetailingLists.get(position).getBrandName());
        holder.tv_timeline.setText(String.format("%s - %s", callDetailingLists.get(position).getSt_end_time().substring(0, 8), callDetailingLists.get(position).getSt_end_time().substring(9, 17)));
        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(callDetailingLists.get(position).getRating())));
        holder.tv_brand_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, callDetailingLists.get(position).getBrandName()));

        if (!callDetailingLists.get(position).getFeedback().isEmpty()) {
            holder.img_feedback.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_feedback_red));
        }

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> callDetailingLists.set(holder.getAbsoluteAdapterPosition(), new CallDetailingList(callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getBrandName(),
                callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getBrandCode(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideName(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideType(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideUrl(),
                callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSt_end_time(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getStartTime(),
                Math.round(rating), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getFeedback(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getDate())));

        holder.img_feedback.setOnClickListener(v -> {
            dialogFeedback = new Dialog(context);
            dialogFeedback.setContentView(R.layout.popup_detailing_feedback);
            Objects.requireNonNull(dialogFeedback.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogFeedback.setCancelable(false);

            ImageView iv_close = dialogFeedback.findViewById(R.id.img_close);
            EditText ed_remark = dialogFeedback.findViewById(R.id.ed_remark);
            Button btn_clear = dialogFeedback.findViewById(R.id.btn_clear);
            Button btn_save = dialogFeedback.findViewById(R.id.btn_save);
            ed_remark.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_remark)});

            if (!callDetailingLists.get(holder.getBindingAdapterPosition()).getFeedback().isEmpty()) {
                ed_remark.setText(callDetailingLists.get(holder.getBindingAdapterPosition()).getFeedback());
            }

            iv_close.setOnClickListener(view1 -> {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
                ed_remark.setText("");
                dialogFeedback.dismiss();
            });


            btn_clear.setOnClickListener(view12 -> {
                ed_remark.setText("");
                ed_remark.setHint("Type your feedback");
            });


            btn_save.setOnClickListener(view13 -> {
                if (!TextUtils.isEmpty(ed_remark.getText().toString())) {
                    callDetailingLists.set(holder.getAbsoluteAdapterPosition(), new CallDetailingList(callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getBrandName(),
                            callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getBrandCode(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideName(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideType(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideUrl(),
                            callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSt_end_time(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getStartTime(),
                            callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getRating(), ed_remark.getText().toString(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getDate()));
                    dialogFeedback.dismiss();
                    holder.img_feedback.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_feedback_red));
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
                } else {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_enter_feedback));
                }
            });
            dialogFeedback.show();
        });

    }

    @Override
    public int getItemCount() {
        return callDetailingLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_brand_name, tv_timeline;
        RatingBar ratingBar;
        ImageView img_feedback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_brand_name = itemView.findViewById(R.id.tv_brand_name);
            tv_timeline = itemView.findViewById(R.id.tv_timeline);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            img_feedback = itemView.findViewById(R.id.img_feedback);
        }
    }
}
