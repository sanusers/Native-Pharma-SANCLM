package saneforce.santrip.activity.call.adapter.detailing;

import static saneforce.santrip.activity.call.DCRCallActivity.arrayStore;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.pojo.detailing.CallDetailingList;
import saneforce.santrip.activity.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.utility.TimeUtils;

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
        holder.tv_timeline.setText(callDetailingLists.get(position).getDuration());
        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(callDetailingLists.get(position).getRating())));
        holder.tv_brand_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, callDetailingLists.get(position).getBrandName()));

        if (!callDetailingLists.get(position).getFeedback().isEmpty()) {
            holder.img_feedback.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_feedback_red));
        }

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> callDetailingLists.set(holder.getAbsoluteAdapterPosition(), new CallDetailingList(callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getBrandName(),
                callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getBrandCode(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideName(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideType(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSlideUrl(),
                callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getSt_end_time(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getStartTime(),
                Math.round(rating), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getFeedback(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getDate(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getDuration())));

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
                            callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getRating(), ed_remark.getText().toString(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getDate(), callDetailingLists.get(holder.getAbsoluteAdapterPosition()).getDuration()));
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

        holder.info.setOnClickListener(view -> {
            TreeMap<String, String> timeline = new TreeMap<>();
            try {
                for(StoreImageTypeUrl storeImageTypeUrl : arrayStore) {
                    if(storeImageTypeUrl.getBrdCode().equalsIgnoreCase(callDetailingLists.get(position).getBrandCode())) {
                        JSONArray jsonArray = new JSONArray(storeImageTypeUrl.getRemTime());
                        if(jsonArray.length() > 0) {
                            for (int i = 0; i<jsonArray.length() - 1; i++) {
                                String startTime = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_32, TimeUtils.FORMAT_33, jsonArray.getJSONObject(i).getString("sT")),
                                        endTime = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_32, TimeUtils.FORMAT_33, jsonArray.getJSONObject(i).getString("eT"));
                                timeline.put(jsonArray.getJSONObject(i).getString("sT"), String.format("%s~%s~%s", storeImageTypeUrl.getSlideNam(), startTime, endTime));
                            }
                            String startTime = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_32, TimeUtils.FORMAT_33, jsonArray.getJSONObject(jsonArray.length() - 1).getString("sT")),
                                    endTime = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_32, TimeUtils.FORMAT_33, jsonArray.getJSONObject(jsonArray.length() - 1).getString("eT"));
                            timeline.put(jsonArray.getJSONObject(jsonArray.length() - 1).getString("sT"), String.format("%s~%s~%s", storeImageTypeUrl.getSlideNam(), startTime, endTime));
                        }
                    }
                }
//                for (String key: timeline.keySet()) {
//                    stringBuilder.append(timeline.get(key));
//                    if(!key.equalsIgnoreCase(timeline.lastKey())) stringBuilder.append("\n");
//                }
                showTimelinePopUp(view, new ArrayList<>(timeline.values()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }

    private boolean checkAddNewLine(StringBuilder stringBuilder){
        return !stringBuilder.toString().isEmpty() && !stringBuilder.toString().endsWith("\n");
    }

    private void showTimelinePopUp(View view, List<String> timeline) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.timeline_popup, null);
        TimelineAdapter timelineAdapter = new TimelineAdapter(context, timeline);
        RecyclerView timeLineRecyclerview = popupView.findViewById(R.id.timeline_recyclerview);
        int recyclerHeight = 150;
        if(timeline.size() < 4) recyclerHeight = WindowManager.LayoutParams.WRAP_CONTENT;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        ViewGroup.LayoutParams layoutParams = timeLineRecyclerview.getLayoutParams();
        layoutParams.height = recyclerHeight;
        timeLineRecyclerview.setLayoutParams(layoutParams);
        timeLineRecyclerview.setLayoutManager(layoutManager);
        timeLineRecyclerview.setAdapter(timelineAdapter);
        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.setOutsideTouchable(true);
        ImageView close = popupView.findViewById(R.id.img_close);
        close.setOnClickListener(closeView -> popupWindow.dismiss());
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = popupView.getMeasuredWidth();
        int height = popupView.getMeasuredHeight();
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - width + 25, location[1] - height + 5);
    }

    @Override
    public int getItemCount() {
        return callDetailingLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_brand_name, tv_timeline;
        RatingBar ratingBar;
        ImageView img_feedback, info;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_brand_name = itemView.findViewById(R.id.tv_brand_name);
            tv_timeline = itemView.findViewById(R.id.tv_timeline);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            img_feedback = itemView.findViewById(R.id.img_feedback);
            info = itemView.findViewById(R.id.tv_duration_info);
        }
    }
}
