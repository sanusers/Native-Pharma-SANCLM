package saneforce.santrip.activity.homeScreen.adapters;

import static saneforce.santrip.activity.homeScreen.HomeDashBoard.InputValidation;
import static saneforce.santrip.activity.homeScreen.HomeDashBoard.SampleValidation;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Chemist_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Doctor_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Stockiest_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.callAnalysisBinding;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.cip_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.hos_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.unlistered_list;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.adapter.product.CheckProductListAdapter;
import saneforce.santrip.activity.homeScreen.call.fragments.DetailedFragment;
import saneforce.santrip.activity.homeScreen.call.pojo.detailing.CallDetailingList;
import saneforce.santrip.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
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
                    UpdateInputSample(outBoxCallLists.get(position).getJsonData());
                    sqLite.deleteOfflineCalls(outBoxCallLists.get(position).getCusCode(), outBoxCallLists.get(position).getCusName(), outBoxCallLists.get(position).getDates());
                    try {
                        if (!outBoxCallLists.get(position).getStatus().equalsIgnoreCase("Duplicate Call")) {
                            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(outBoxCallLists.get(position).getDates()) && jsonObject.getString("CustCode").equalsIgnoreCase(outBoxCallLists.get(position).getCusCode())) {
                                    jsonArray.remove(i);
                                    break;
                                }
                            }
                            sqLite.saveMasterSyncData(Constants.DCR, jsonArray.toString(), 0);
                            sqLite.deleteLineChart(outBoxCallLists.get(position).getCusCode(), outBoxCallLists.get(position).getDates());

                            switch (outBoxCallLists.get(position).getCusType()) {
                                case "1":
                                    int doc_current_callcount = sqLite.getcurrentmonth_calls_count("1");
                                    callAnalysisBinding.txtDocCount.setText(String.format("%d / %d", doc_current_callcount, Doctor_list.length()));
                                    break;
                                case "2":
                                    int che_current_callcount = sqLite.getcurrentmonth_calls_count("2");
                                    callAnalysisBinding.txtCheCount.setText(String.format("%d / %d", che_current_callcount, Chemist_list.length()));
                                    break;
                                case "3":
                                    int stockiest_current_callcount = sqLite.getcurrentmonth_calls_count("3");
                                    callAnalysisBinding.txtStockCount.setText(String.format("%d / %d", stockiest_current_callcount, Stockiest_list.length()));
                                    break;
                                case "4":
                                    int unlistered_current_callcount = sqLite.getcurrentmonth_calls_count("4");
                                    callAnalysisBinding.txtUnlistCount.setText(String.format("%d / %d", unlistered_current_callcount, unlistered_list.length()));
                                    break;
                                case "5":
                                    int cip_current_callcount = sqLite.getcurrentmonth_calls_count("5");
                                    callAnalysisBinding.txtCipCount.setText(String.format("%d / %d", cip_current_callcount, cip_list.length()));
                                    break;
                                case "6":
                                    int hos_current_callcount = sqLite.getcurrentmonth_calls_count("6");
                                    callAnalysisBinding.txtHosCount.setText(String.format("%d / %d", hos_current_callcount, hos_list.length()));
                                    break;
                            }
                        }
                        // CallAnalysisFragment.SetcallDetailsInLineChart(sqLite, context);
                    } catch (Exception ignored) {

                    }
                    removeAt(position);
                }
                return true;
            });
            popup.show();
        });

    }

    private void UpdateInputSample(String jsonArray) {
        try {
            JSONObject json = new JSONObject(jsonArray);
            //Input
            if (InputValidation.equalsIgnoreCase("1")) {
                JSONArray jsonArrayInpStk = sqLite.getMasterSyncDataByKey(Constants.INPUT_BALANCE);
                JSONArray jsonInput = json.getJSONArray("Inputs");
                Log.v("input_wrk", String.valueOf(jsonInput));
                if (jsonInput.length() > 0) {
                    for (int i = 0; i < jsonInput.length(); i++) {
                        JSONObject jsIp = jsonInput.getJSONObject(i);
                        //InputStockChange
                        for (int j = 0; j < jsonArrayInpStk.length(); j++) {
                            JSONObject jsonObject = jsonArrayInpStk.getJSONObject(j);
                            Log.v("chkInpStk", jsIp.getString("Code") + "-----" + jsonObject.getString("Code"));
                            if (jsIp.getString("Code").equalsIgnoreCase(jsonObject.getString("Code"))) {
                                int EnterQty = Integer.parseInt(jsIp.getString("IQty"));
                                int BalanceStock = Integer.parseInt(jsonObject.getString("Balance_Stock"));
                                int FinalStock = EnterQty + BalanceStock;
                                jsonObject.remove("Balance_Stock");
                                jsonObject.put("Balance_Stock", FinalStock);
                                break;
                            }
                        }
                    }
                    sqLite.saveMasterSyncData(Constants.INPUT_BALANCE, jsonArrayInpStk.toString(), 0);
                }
            }

            //Sample
            if (SampleValidation.equalsIgnoreCase("1")) {
                JSONArray jsonArraySamStk = sqLite.getMasterSyncDataByKey(Constants.STOCK_BALANCE);
                JSONArray jsonPrdArray = new JSONArray(json.getString("Products"));
                Log.v("sample_wrk", String.valueOf(jsonPrdArray));
                if (jsonPrdArray.length() > 0) {
                    //InputStockChange
                    for (int i = 0; i < jsonPrdArray.length(); i++) {
                        JSONObject js = jsonPrdArray.getJSONObject(i);
                        if (js.getString("Group").equalsIgnoreCase("0")) {
                            for (int j = 0; j < jsonArraySamStk.length(); j++) {
                                JSONObject jsonObject = jsonArraySamStk.getJSONObject(j);
                                if (js.getString("Code").equalsIgnoreCase(jsonObject.getString("Code"))) {
                                    int EnterQty = Integer.parseInt(js.getString("SmpQty"));
                                    int BalanceStock = Integer.parseInt(jsonObject.getString("Balance_Stock"));
                                    int FinalStock = EnterQty + BalanceStock;
                                    jsonObject.remove("Balance_Stock");
                                    jsonObject.put("Balance_Stock", FinalStock);
                                    break;
                                }
                            }
                        }
                    }
                    sqLite.saveMasterSyncData(Constants.STOCK_BALANCE, jsonArraySamStk.toString(), 0);
                }
            }


        } catch (Exception e) {

        }
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
