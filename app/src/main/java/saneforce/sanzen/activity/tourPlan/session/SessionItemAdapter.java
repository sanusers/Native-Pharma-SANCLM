package saneforce.sanzen.activity.tourPlan.session;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.activity.tourPlan.model.DoctorVisitModel;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.tourPlan.model.EditModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.storage.SharedPref;

public class SessionItemAdapter extends RecyclerView.Adapter<SessionItemAdapter.MyViewHolder> implements Filterable {
    ArrayList<EditModelClass> arrayList = new ArrayList<>();
    ArrayList<EditModelClass> arrayForFilter = new ArrayList<>();
    ArrayList<EditModelClass> supportModelArray = new ArrayList<>();
    private boolean checkBoxVisibility = false, isHQ = false, isDr = false, visitFrequencyNeed = false;
    private int selectedHQCount = 0, minimumGap = 0;
    private ValueFilter valueFilter;
    SessionItemInterface sessionItemInterface;
    private Context context;
    private CommonUtilsMethods commonUtilsMethods;

    public SessionItemAdapter() {
    }

    public SessionItemAdapter(Context context, ArrayList<EditModelClass> arrayList, boolean checkBoxVisibility, boolean isHQ, boolean isDr, String visitFrequencyNeed, String minimumGap, SessionItemInterface sessionItemInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.arrayForFilter = arrayList;
        this.isHQ = isHQ;
        this.isDr = isDr;
        this.checkBoxVisibility = checkBoxVisibility;
        this.sessionItemInterface = sessionItemInterface;
        commonUtilsMethods = new CommonUtilsMethods(context);
        selectedHQCount = 0;
        this.visitFrequencyNeed = visitFrequencyNeed.equals("0");
        this.minimumGap = Integer.parseInt(minimumGap);
        for (EditModelClass hq : arrayList) {
            if (hq.isChecked()) {
                selectedHQCount++;
            }
        }
    }

    @NonNull
    @Override
    public SessionItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_listview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionItemAdapter.MyViewHolder holder, int position) {
        EditModelClass editModelClass = arrayList.get(holder.getAbsoluteAdapterPosition());
        if (!checkBoxVisibility) {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.textView.setText(editModelClass.getName());
        holder.checkBox.setChecked(editModelClass.isChecked());

        if (isDr && SharedPref.getSfType(context).equalsIgnoreCase("1") && visitFrequencyNeed) {
            holder.infoView.setVisibility(View.VISIBLE);
            holder.infoView.setOnClickListener(view -> {
                String code = arrayList.get(position).getCode();
                DoctorVisitModel doctorVisitModel = TourPlanActivity.doctorVisitMap.get(code);
                StringBuilder data = new StringBuilder();
                if (doctorVisitModel != null) {
                    String category = "Category : " + doctorVisitModel.getCategory();
                    String totalVisits = "Total Visits : " + doctorVisitModel.getTotalVisit();
                    String plannedVisits = "Planned Visits : " + doctorVisitModel.getPlannedVisit();
                    Set<String> plannedDatesList = doctorVisitModel.getPlannedDates();
                    List<String> sortedList = plannedDatesList.stream().map(Integer::valueOf).sorted().map(String::valueOf).toList();
                    String dates = sortedList.toString().replaceAll("\\[", "").replaceAll("\\]", "");
                    if (dates.isEmpty()) dates = "-";
                    String plannedDates = "Planned Dates : " + dates;
                    data.append(category);
                    data.append("\n");
                    data.append(totalVisits);
                    data.append("\n");
                    data.append(plannedVisits);
                    data.append("\n");
                    data.append(plannedDates);
                }
                if (data.toString().isEmpty()) {
                    data.append(context.getString(R.string.not_planned));
                }
                showDocDataPopUp(view, data.toString());
            });
        } else {
            holder.infoView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                int position = holder.getAbsoluteAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                EditModelClass clickedItem = arrayList.get(position);

                int independentPos = -1;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getName().equalsIgnoreCase(Constants.INDEPENDENT)) {
                        independentPos = i;
                        break;
                    }
                }

                boolean isNowChecked = !clickedItem.isChecked();
                clickedItem.setChecked(isNowChecked);
                notifyItemChanged(position);

                if (isNowChecked) {
                    if (clickedItem.getName().equalsIgnoreCase(Constants.INDEPENDENT)) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (i != position && arrayList.get(i).isChecked()) {
                                arrayList.get(i).setChecked(false);
                                notifyItemChanged(i); // update only changed rows
                            }
                        }
                    } else if (independentPos != -1 && arrayList.get(independentPos).isChecked()) {
                        arrayList.get(independentPos).setChecked(false);
                        notifyItemChanged(independentPos);
                    }
                }
                if (isHQ) {
                    if (isNowChecked) {
                        selectedHQCount++;
                        if (selectedHQCount > 5) {
                            commonUtilsMethods.showToastMessage(context,context.getString(R.string.cannot_select_more_than_5) + context.getString(R.string.headquarter));
                            selectedHQCount--;
                            clickedItem.setChecked(false);
                            notifyItemChanged(position);
                        }
                    } else {
                        selectedHQCount--;
                    }
                }

                try {
                    if (isDr && SharedPref.getSfType(context).equalsIgnoreCase("1") && (visitFrequencyNeed || minimumGap > 0)) {
                        String code = arrayList.get(position).getCode();
                        DoctorVisitModel doctorVisitModel = TourPlanActivity.doctorVisitMap.get(code);
                        if (doctorVisitModel != null) {
                            int totalVisits = doctorVisitModel.getTotalVisit();
                            int plannedVisits = doctorVisitModel.getPlannedVisit();
                            Set<String> plannedDatesList = doctorVisitModel.getPlannedDates();
                            Log.e("SIA", "onSafeClick: " + SessionEditAdapter.inputDataArrayOneBuild.getDayNo());
                            if (isNowChecked) {
                                if (minimumGap > 0) {
                                    boolean isValid = true;
                                    try {
                                        for (String strDate : plannedDatesList) {
                                            int afterDate = Integer.parseInt(strDate) + minimumGap, beforeDate = Integer.parseInt(strDate) - minimumGap;
                                            int chosenDate = Integer.parseInt(SessionEditAdapter.inputDataArrayOneBuild.getDayNo());
                                            if (!(chosenDate > afterDate || chosenDate < beforeDate)) {
                                                Log.e("SIA", "onSafeClick: " + beforeDate + " <- " + chosenDate + " -> " + afterDate);
                                                isValid = false;
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (!isValid) {
                                        CommonUtilsMethods.showToastMessage(context, "Cannot plan with minimum gap of " + minimumGap);
                                        clickedItem.setChecked(false);
                                        notifyItemChanged(position);
                                    } else {
                                        if (visitFrequencyNeed && plannedVisits >= totalVisits) {
                                            Log.e("SIA", "onSafeClick: " + totalVisits + " -> " + plannedVisits);
                                            CommonUtilsMethods.showToastMessage(context, "Visit Frequency already met");
                                            clickedItem.setChecked(false);
                                            notifyItemChanged(position);
                                        } else {
                                            plannedDatesList.add(SessionEditAdapter.inputDataArrayOneBuild.getDayNo());
                                            plannedVisits++;
                                            doctorVisitModel.setPlannedDates(plannedDatesList);
                                            doctorVisitModel.setPlannedVisit(plannedVisits);
                                            TourPlanActivity.doctorVisitMap.put(code, doctorVisitModel);
                                        }
                                    }
                                } else {
                                    if (plannedVisits >= totalVisits) {
                                        Log.e("SIA", "onSafeClick: " + totalVisits + " -> " + plannedVisits);
                                        CommonUtilsMethods.showToastMessage(context, "Visit Frequency already met");
                                        clickedItem.setChecked(false);
                                        notifyItemChanged(position);
                                    } else {
                                        plannedDatesList.add(SessionEditAdapter.inputDataArrayOneBuild.getDayNo());
                                        plannedVisits++;
                                        doctorVisitModel.setPlannedDates(plannedDatesList);
                                        doctorVisitModel.setPlannedVisit(plannedVisits);
                                        TourPlanActivity.doctorVisitMap.put(code, doctorVisitModel);
                                    }
                                }
                            } else {
                                plannedDatesList.remove(SessionEditAdapter.inputDataArrayOneBuild.getDayNo());
                                plannedVisits--;
                                doctorVisitModel.setPlannedDates(plannedDatesList);
                                doctorVisitModel.setPlannedVisit(plannedVisits);
                                TourPlanActivity.doctorVisitMap.put(code, doctorVisitModel);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sessionItemInterface.itemClicked(arrayList, clickedItem);
            }
        });
    }

    private void showDocDataPopUp(View view, String data) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.timeline_popup, null);
        TextView timelineTV = popupView.findViewById(R.id.timeline);
        timelineTV.setText(data);
        timelineTV.setVisibility(View.VISIBLE);
        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setOutsideTouchable(true);
        ImageView close = popupView.findViewById(R.id.img_close);
        RecyclerView timeLineRecyclerview = popupView.findViewById(R.id.timeline_recyclerview);
        TextView tv_head = popupView.findViewById(R.id.tv_head);
        View divider = popupView.findViewById(R.id.view_dummy);
        close.setVisibility(View.GONE);
        timeLineRecyclerview.setVisibility(View.GONE);
        tv_head.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = popupView.getMeasuredWidth();
        int height = popupView.getMeasuredHeight();
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - width + 25, location[1] - height + 5);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;
        ImageView infoView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.tp_item_checkbox);
            textView = itemView.findViewById(R.id.tp_item_text);
            infoView = itemView.findViewById(R.id.info);
        }
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();

            ArrayList<EditModelClass> filteredModelArray = new ArrayList<>();
            if (charSequence != null && charSequence.length() > 0) {
                supportModelArray = new ArrayList<>();
                for (int i = 0; i < arrayForFilter.size(); i++) {
                    if ((arrayForFilter.get(i).getName().toUpperCase()).contains(charSequence.toString().toUpperCase())) {
                        filteredModelArray.add(arrayForFilter.get(i));
                        supportModelArray.add(arrayForFilter.get(i));
                    }
                }
                results.count = filteredModelArray.size();
                results.values = filteredModelArray;
            } else {
                for (int i = 0; i < supportModelArray.size(); i++) {
                    if (supportModelArray.get(i).isChecked()) {
                        for (int j = 0; j < arrayForFilter.size(); j++) {
                            if (arrayForFilter.get(j).getCode().equalsIgnoreCase(supportModelArray.get(i).getCode())) {
                                arrayForFilter.get(j).setChecked(supportModelArray.get(i).isChecked());
                            }
                        }
                    }
                }
                results.count = arrayForFilter.size();
                results.values = arrayForFilter;
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<EditModelClass>) results.values;
            notifyDataSetChanged();
        }
    }
}
