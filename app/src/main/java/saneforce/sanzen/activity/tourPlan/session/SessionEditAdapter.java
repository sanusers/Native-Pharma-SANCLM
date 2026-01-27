package saneforce.sanzen.activity.tourPlan.session;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.activity.tourPlan.model.EditModelClass;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQHeaderModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQItemModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.STPDaySorter;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class SessionEditAdapter extends RecyclerView.Adapter<SessionEditAdapter.MyViewHolder> {
    public static ModelClass inputDataArray = new ModelClass();
    public static OneBuildModelClass inputDataArrayOneBuild = new OneBuildModelClass();
    public int itemPosition;
    private Context context;

    ApiInterface apiInterface;
    SessionInterface sessionInterface;
    SessionInterfaceOneBuild sessionInterfaceOneBuild;
    SessionItemAdapter sessionItemAdapter = new SessionItemAdapter();
    private SessionMultiHQItemAdapter sessionMultiHQItemAdapter = new SessionMultiHQItemAdapter();
    String sfCode = "", division_code = "", sfType = "", designation = "", state_code = "", subdivision_code = "";
    int synccount = 0;
    String jwNeed = "", drNeed = "", chemistNeed = "", stockiestNeed = "", unListedDrNeed = "", cipNeed = "", hospNeed = "", FW_meetup_mandatory = "", holidayEditable = "", weeklyOffEditable = "", remarksNeed = "", planAllDr = "", visitFrequencyNeed = "", minimumGap = "";
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private static boolean isMGR = false;

    public SessionEditAdapter() {
    }

    public SessionEditAdapter(ModelClass inputDataArray, Context context, SessionInterface sessionInterface) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        SessionEditAdapter.inputDataArray = inputDataArray;
        this.context = context;
        this.sessionInterface = sessionInterface;
        roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();
//        sfType = SharedPref.getSfType(context);
        isMGR = SharedPref.getSfType(context).equalsIgnoreCase("2");

//        hq_code = SharedPref.getHqCode(context); // Selected HQ code in master sync ,it will be changed if any other HQ selected in Add Plan

        //Tour Plan setup
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.TP_SETUP).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                drNeed = jsonArray.getJSONObject(i).getString("DrNeed");
                chemistNeed = jsonArray.getJSONObject(i).getString("ChmNeed");
                jwNeed = jsonArray.getJSONObject(i).getString("JWNeed");
                stockiestNeed = jsonArray.getJSONObject(i).getString("StkNeed");
                unListedDrNeed = jsonArray.getJSONObject(i).getString("UnDrNeed");
                cipNeed = jsonArray.getJSONObject(i).getString("Cip_Need");
                hospNeed = jsonArray.getJSONObject(i).getString("HospNeed");
                FW_meetup_mandatory = jsonArray.getJSONObject(i).getString("FW_meetup_mandatory");
                holidayEditable = jsonArray.getJSONObject(i).getString("Holiday_Editable");
                weeklyOffEditable = jsonArray.getJSONObject(i).getString("Weeklyoff_Editable");
                remarksNeed = jsonArray.getJSONObject(i).optString("tp_objective_mandatory");
                planAllDr = jsonArray.getJSONObject(i).optString("Plan_All_Drs", "1");
                visitFrequencyNeed = jsonArray.getJSONObject(i).optString("visit_freq_need", "1");
                minimumGap = jsonArray.getJSONObject(i).optString("min_gap_need", "0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SessionEditAdapter(Context context, OneBuildModelClass inputDataArrayOneBuild, SessionInterfaceOneBuild sessionInterfaceOneBuild) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        this.context = context;
        SessionEditAdapter.inputDataArrayOneBuild = inputDataArrayOneBuild;
        this.sessionInterfaceOneBuild = sessionInterfaceOneBuild;
        roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();
        sfType = SharedPref.getSfType(context);

        //Tp Setup
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.TP_SETUP).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                drNeed = jsonArray.getJSONObject(i).getString("DrNeed");
                chemistNeed = jsonArray.getJSONObject(i).getString("ChmNeed");
                jwNeed = jsonArray.getJSONObject(i).getString("JWNeed");
                stockiestNeed = jsonArray.getJSONObject(i).getString("StkNeed");
                unListedDrNeed = jsonArray.getJSONObject(i).getString("UnDrNeed");
                cipNeed = jsonArray.getJSONObject(i).getString("Cip_Need");
                hospNeed = jsonArray.getJSONObject(i).getString("HospNeed");
                FW_meetup_mandatory = jsonArray.getJSONObject(i).getString("FW_meetup_mandatory");
                holidayEditable = jsonArray.getJSONObject(i).getString("Holiday_Editable");
                weeklyOffEditable = jsonArray.getJSONObject(i).getString("Weeklyoff_Editable");
                remarksNeed = jsonArray.getJSONObject(i).optString("tp_objective_mandatory");
                planAllDr = jsonArray.getJSONObject(i).optString("Plan_All_Drs", "1");
                visitFrequencyNeed = jsonArray.getJSONObject(i).optString("visit_freq_need", "1");
                minimumGap = jsonArray.getJSONObject(i).optString("min_gap_need", "0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSelectedCount(MyViewHolder holder, ArrayList<EditModelClass> arrayList, boolean selectState, TextView selectedNameTxtView, TextView countTxt) {

        if (!selectState) { // if its false we should show the text as "Selected" with count or just "Select" .if its true we need to show the selected item name in TextView.
            int count = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).isChecked())
                    count++;
            }

            if (count > 0) {
                selectedNameTxtView.setText(R.string.selected);
                countTxt.setVisibility(View.VISIBLE);
                countTxt.setText(String.valueOf(count));
            } else {
                selectedNameTxtView.setText(R.string.select);
                countTxt.setVisibility(View.GONE);
            }
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
            holder.fieldSelected = true;
        } else {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).isChecked()) {
                    if (text.length() == 0)
                        text = new StringBuilder(arrayList.get(i).getName());
                    else
                        text.append(",").append(arrayList.get(i).getName());
                }
            }
            if (text.length() == 0) {
                selectedNameTxtView.setText(R.string.select);
            } else {
                selectedNameTxtView.setText(text);
            }
            countTxt.setVisibility(View.GONE);
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
            holder.fieldSelected = false;

            if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                List<OneBuildModelClass.SessionList.SubClass> subClassListOneBuild = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).isChecked()) {
                        OneBuildModelClass.SessionList.SubClass subClassOneBuild = new OneBuildModelClass.SessionList.SubClass(arrayList.get(i).getName(), arrayList.get(i).getCode());
                        subClassListOneBuild.add(subClassOneBuild);
                    }
                }

                if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getTerritories().clear();
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setTerritories(subClassListOneBuild);
                } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getJointWorks().clear();
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setJointWorks(subClassListOneBuild);
                } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getDoctors().clear();
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setDoctors(subClassListOneBuild);
                } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemists().clear();
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemists(subClassListOneBuild);
                } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getStockists().clear();
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setStockists(subClassListOneBuild);
                } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnlistedDoctors().clear();
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnlistedDoctors(subClassListOneBuild);
                } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassListOneBuild);
                } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospitals().clear();
                    inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospitals(subClassListOneBuild);
                }
            } else {
                List<ModelClass.SessionList.SubClass> subClassList = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).isChecked()) {
                        ModelClass.SessionList.SubClass subClass = new ModelClass.SessionList.SubClass(arrayList.get(i).getName(), arrayList.get(i).getCode());
                        subClassList.add(subClass);
                    }
                }

                //replace the new/modified data to the input data of this adapter class
                if (holder.hqLayout.getVisibility() == View.VISIBLE && isMGR) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHQs().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHQs(subClassList);
                } else if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCluster().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCluster(subClassList);
                } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getJC().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setJC(subClassList);
                } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDr().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDr(subClassList);
                } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemist().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemist(subClassList);
                } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getStockiest().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setStockiest(subClassList);
                } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDr().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDr(subClassList);
                } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassList);
                } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospital().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospital(subClassList);
                }
            }
        }
    }

    public void setSelectedCountMGR(MyViewHolder holder, ArrayList<MultiHQHeaderModelClass> arrayList, boolean selectState, TextView selectedNameTxtView, TextView countTxt) {
        if (!selectState) {
            int count = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                MultiHQHeaderModelClass dataHeader = arrayList.get(i);
                if (dataHeader != null) {
                    ArrayList<MultiHQItemModelClass> dataList = dataHeader.getItemsList();
                    for (int j = 0; j < dataList.size(); j++) {
                        MultiHQItemModelClass data = dataHeader.getItemsList().get(j);
                        if (data != null && data.isChecked()) {
                            count++;
                        }
                    }
                }
            }

            if (count > 0) {
                selectedNameTxtView.setText(R.string.selected);
                countTxt.setVisibility(View.VISIBLE);
                countTxt.setText(String.valueOf(count));
            } else {
                selectedNameTxtView.setText(R.string.select);
                countTxt.setVisibility(View.GONE);
            }
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
            holder.fieldSelected = true;
        } else {
            StringBuilder text = new StringBuilder();
            ArrayList<MultiHQHeaderModelClass> resultDataHeaderList = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                MultiHQHeaderModelClass dataHeader = new MultiHQHeaderModelClass(arrayList.get(i));
                ArrayList<MultiHQItemModelClass> resultDataItemList = new ArrayList<>();
                ArrayList<MultiHQItemModelClass> dataList = dataHeader.getItemsList();
                for (int j = 0; j < dataList.size(); j++) {
                    MultiHQItemModelClass data = dataHeader.getItemsList().get(j);
                    if (data != null && data.isChecked()) {
                        resultDataItemList.add(data);
                        if (text.length() == 0) {
                            text = new StringBuilder(data.getName());
                        } else {
                            text.append(",").append(data.getName());
                        }
                    }
                }
                dataHeader.setItemsList(resultDataItemList);
                resultDataHeaderList.add(dataHeader);
            }
            if (text.length() == 0) {
                selectedNameTxtView.setText(R.string.select);
            } else {
                selectedNameTxtView.setText(text);
            }
            countTxt.setVisibility(View.GONE);
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
            holder.fieldSelected = false;

            //replace the new/modified data to the input data of this adapter class
            if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getClusters().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setClusters(resultDataHeaderList);
            } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getJCs().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setJCs(resultDataHeaderList);
            } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDrs().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDrs(resultDataHeaderList);
            } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemists().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemists(resultDataHeaderList);
            } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getStockiests().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setStockiests(resultDataHeaderList);
            } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDrs().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDrs(resultDataHeaderList);
            } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCips().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCips(resultDataHeaderList);
            } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospitals().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospitals(resultDataHeaderList);
            }
        }
    }

    @NonNull
    @Override
    public SessionEditAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_edit_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull SessionEditAdapter.MyViewHolder holder, int position) {
        if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            holder.progress_hq.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            holder.remarks.setImeOptions(EditorInfo.IME_ACTION_DONE);
            holder.remarks.setRawInputType(InputType.TYPE_CLASS_TEXT);
            holder.sessionDataOneBuild = inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition());
            holder.clusterModelArrayOneBuild = new ArrayList<>(holder.sessionDataOneBuild.getTerritories());
            holder.jcModelArrayOneBuild = new ArrayList<>(holder.sessionDataOneBuild.getJointWorks());
            holder.listedDrModelArrayOneBuild = new ArrayList<>(holder.sessionDataOneBuild.getDoctors());
            holder.chemistModelArrayOneBuild = new ArrayList<>(holder.sessionDataOneBuild.getChemists());
            holder.stockListModelArrayOneBuild = new ArrayList<>(holder.sessionDataOneBuild.getStockists());
            holder.unListedDrModelArrayOneBuild = new ArrayList<>(holder.sessionDataOneBuild.getUnlistedDoctors());
            holder.cipModelArrayOneBuild = new ArrayList<>(holder.sessionDataOneBuild.getCip());
            holder.hospitalModelArrayOneBuild = new ArrayList<>(holder.sessionDataOneBuild.getHospitals());
            designation = SharedPref.getDesig(context);
            if (holder.sessionDataOneBuild.getVisible()) {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            holder.sessionNoTxt.setText(R.string.session + (position + 1));
            if (holder.getAbsoluteAdapterPosition() == 0) { //No need to show delete icon if there is only one session
                if (inputDataArrayOneBuild.getSessionList().size() > 1)
                    holder.sessionDelete.setVisibility(View.VISIBLE);
                else
                    holder.sessionDelete.setVisibility(View.GONE);
            }
            //Work Day
            if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && sfType.equalsIgnoreCase("1")) {
                holder.workDayLayout.setVisibility(View.VISIBLE);
                if (inputDataArrayOneBuild.getSTP_Code().isEmpty()) {
                    holder.workDayField.setText(R.string.select);
                } else {
                    holder.workDayField.setText(inputDataArray.getSTP_Name());
                }
            } else {
                holder.workDayLayout.setVisibility(View.GONE);
            }
            //work Type
            if (holder.sessionDataOneBuild.getWorkType().getName().equals("")) {
                holder.workTypeField.setText(R.string.select);
            } else {
                holder.workTypeField.setText(holder.sessionDataOneBuild.getWorkType().getName());
            }

            if (holder.sessionDataOneBuild.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) { // Y - yes
                holder.hqNeed = "0"; // 0 - Yes
                holder.clusterNeed = "0";
            } else if (holder.sessionDataOneBuild.getWorkType().getTerrSlFlg().equalsIgnoreCase("N")) {
                holder.hqNeed = "1"; // 1 - No
                holder.clusterNeed = "1";
            }
            worktypeBasedUiOneBuild(holder, holder.sessionDataOneBuild, true);
            //HQ
            switch (designation) {
                case "MR":
                    holder.hqLayout.setVisibility(View.GONE);
                    if (holder.sessionDataOneBuild.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                        holder.sessionDataOneBuild.getHeadquarters().setName(SharedPref.getHqName(context));
                        holder.sessionDataOneBuild.getHeadquarters().setCode(SharedPref.getHqCode(context));
                        holder.hqField.setText(holder.sessionDataOneBuild.getHeadquarters().getName());
                        holder.selectedHq = holder.sessionDataOneBuild.getHeadquarters().getCode();
                    }
                    break;
                case "MGR":
                    if (holder.sessionDataOneBuild.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                        if (holder.sessionDataOneBuild.getHeadquarters().getName().equals("")) {
                            holder.hqField.setText(R.string.select);
                        } else {
                            holder.hqField.setText(holder.sessionDataOneBuild.getHeadquarters().getName());
                            holder.selectedHq = holder.sessionDataOneBuild.getHeadquarters().getCode();
                        }
                        if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && sfType.equalsIgnoreCase("1")) {
                            holder.hqLayout.setVisibility(View.GONE);
                        }
                    }
            }


            if (SharedPref.getWrkAreaName(context).isEmpty() || SharedPref.getWrkAreaName(context).equalsIgnoreCase(null)) {
                holder.textCluster.setText("Cluster");
            } else {
                holder.textCluster.setText(SharedPref.getWrkAreaName(context));
            }

            if (!holder.selectedHq.equals("")) {
                getDataFromLocal(holder, holder.selectedHq);
            }

            //Cluster
            StringBuilder clusterName = new StringBuilder();
            for (int i = 0; i < holder.clusterModelArrayOneBuild.size(); i++) {
                if (clusterName.length() == 0) {
                    clusterName = new StringBuilder(holder.clusterModelArrayOneBuild.get(i).getName());
                } else {
                    clusterName.append(", ").append(holder.clusterModelArrayOneBuild.get(i).getName());
                }
            }
            if (clusterName.length() > 0) {
                holder.clusterField.setText(clusterName);
            }
            prepareInputDataOneBuild(holder.clusterModelArrayOneBuild, holder.clusterArray);

            holder.selectedClusterCode.clear();
            for (int i = 0; i < holder.clusterModelArrayOneBuild.size(); i++) {
                holder.selectedClusterCode.add(holder.clusterModelArrayOneBuild.get(i).getCode());
            }

            //Joint Work
            StringBuilder jcName = new StringBuilder();
            if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0")) {
                holder.jcLayout.setVisibility(View.GONE);
            } else {
//            holder.jcLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < holder.jcModelArrayOneBuild.size(); i++) {
                    if (jcName.length() == 0) {
                        jcName = new StringBuilder(holder.jcModelArrayOneBuild.get(i).getName());
                    } else {
                        jcName.append(", ").append(holder.jcModelArrayOneBuild.get(i).getName());
                    }
                }
                if (jcName.length() > 0) {
                    holder.jcField.setText(jcName);
                }
                prepareInputDataOneBuild(holder.jcModelArrayOneBuild, holder.jointCallArray);
            }

            //Dr
            StringBuilder drName = new StringBuilder();

            for (int i = 0; i < holder.listedDrModelArrayOneBuild.size(); i++) {
                if (drName.length() == 0) {
                    drName = new StringBuilder(holder.listedDrModelArrayOneBuild.get(i).getName());
                } else {
                    drName.append(", ").append(holder.listedDrModelArrayOneBuild.get(i).getName());
                }
            }
            if (drName.length() > 0) {
                holder.drField.setText(drName);
            } else {
                holder.drField.setText(R.string.select);
            }
            prepareInputDataOneBuild(holder.listedDrModelArrayOneBuild, holder.listedDrArray);

            StringBuilder chemistName = new StringBuilder();
            for (int i = 0; i < holder.chemistModelArrayOneBuild.size(); i++) {
                if (chemistName.length() == 0) {
                    chemistName = new StringBuilder(holder.chemistModelArrayOneBuild.get(i).getName());
                } else {
                    chemistName.append(", ").append(holder.chemistModelArrayOneBuild.get(i).getName());
                }
            }
            if (chemistName.length() > 0) {
                holder.chemistField.setText(chemistName);
            } else {
                holder.chemistField.setText(R.string.select);
            }
            prepareInputDataOneBuild(holder.chemistModelArrayOneBuild, holder.chemistArray);

            //Stockiest
            StringBuilder stockiestName = new StringBuilder();
            for (int i = 0; i < holder.stockListModelArrayOneBuild.size(); i++) {
                if (stockiestName.length() == 0) {
                    stockiestName = new StringBuilder(holder.stockListModelArrayOneBuild.get(i).getName());
                } else {
                    stockiestName.append(", ").append(holder.stockListModelArrayOneBuild.get(i).getName());
                }
            }
            if (stockiestName.length() > 0) {
                holder.stockiestField.setText(stockiestName);
            }
            prepareInputDataOneBuild(holder.stockListModelArrayOneBuild, holder.stockiestArray);


            //UnListed Doctor
            StringBuilder unListedDrName = new StringBuilder();
            for (int i = 0; i < holder.unListedDrModelArrayOneBuild.size(); i++) {
                if (unListedDrName.length() == 0) {
                    unListedDrName = new StringBuilder(holder.unListedDrModelArrayOneBuild.get(i).getName());
                } else {
                    unListedDrName.append(", ").append(holder.unListedDrModelArrayOneBuild.get(i).getName());
                }

            }
            if (unListedDrName.length() > 0) {
                holder.unListedDrField.setText(unListedDrName);
            }
            prepareInputDataOneBuild(holder.unListedDrModelArrayOneBuild, holder.unListedDrArray);

            //Cip
            StringBuilder cipName = new StringBuilder();
            for (int i = 0; i < holder.cipModelArrayOneBuild.size(); i++) {
                if (cipName.length() == 0) {
                    cipName = new StringBuilder(holder.cipModelArrayOneBuild.get(i).getName());
                } else {
                    cipName.append(",").append(holder.cipModelArrayOneBuild.get(i).getName());
                }

            }
            if (cipName.length() > 0) {
                holder.cipField.setText(cipName);
            }
            prepareInputDataOneBuild(holder.cipModelArrayOneBuild, holder.cipArray);

            //Hospital
            StringBuilder hospName = new StringBuilder();
            for (int i = 0; i < holder.hospitalModelArrayOneBuild.size(); i++) {
                if (hospName.length() == 0) {
                    hospName = new StringBuilder(holder.hospitalModelArrayOneBuild.get(i).getName());
                } else {
                    hospName.append(", ").append(holder.hospitalModelArrayOneBuild.get(i).getName());
                }
            }
            if (hospName.length() > 0) {
                holder.hospField.setText(hospName);
            }
            prepareInputDataOneBuild(holder.hospitalModelArrayOneBuild, holder.hospArray);
            holder.remarks.setText(holder.sessionDataOneBuild.getRemarks());

            holder.searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        holder.searchClearIcon.setVisibility(View.VISIBLE);
                    } else {
                        holder.searchClearIcon.setVisibility(View.GONE);
                    }
                    sessionItemAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    sessionItemAdapter.getFilter().filter(editable.toString());
                }
            });

            holder.searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE) {
                        UtilityClass.hideKeyboard((Activity) context);
                        return true;
                    }
                    return false;
                }
            });

            holder.searchClearIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                }
            });

            holder.workTypeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);
                    if (!holder.fieldSelected) {
                        if (holder.workTypeArray.size() == 0) {
                            holder.workTypeArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray());
                        }
                        ArrayList<EditModelClass> filteredArray = new ArrayList<>();
                        for (int i = 0; i < holder.workTypeArray.size(); i++) {
                            if (holder.workTypeArray.get(i).getTP_DCR().contains("T")) {
                                filteredArray.add(holder.workTypeArray.get(i));
                            }
                        }
                        holder.sessionItemAdapterArray = filteredArray;
                        populateSessionItemAdapterOneBuild(holder, false, true, false);
                        holder.fieldSelected = true;
                        onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.WORK_TYPE);
                    } else {
                        changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, true);
                        holder.fieldSelected = false;
                        onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");  // change it
                    }
                    TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
                }
            });

            if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && sfType.equalsIgnoreCase("1")) {
                holder.workDayLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemPosition = holder.getLayoutPosition();
                        holder.relativeLayout.setSelected(false);
                        if (!holder.fieldSelected) {
                            ArrayList<EditModelClass> workDayArray = new ArrayList<>();
                            if (holder.workDayArray.isEmpty()) {
                                try {
                                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STP_SETUP).getMasterSyncDataJsonArray();
                                    if (jsonArray != null && jsonArray.length() > 0) {
                                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                                        String[] dayIDs = CommonUtilsMethods.removeLastComma(jsonObject.optString("Plan_SName")).split("/");
                                        String[] dayCaptions = CommonUtilsMethods.removeLastComma(jsonObject.optString("Plan_Name")).split("/");
                                        for (int index = 0; index < dayIDs.length; index++) {
                                            if (!dayIDs[index].isEmpty()) {
                                                workDayArray.add(new EditModelClass(dayIDs[index], dayCaptions[index], false));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                new STPDaySorter().sortDaysTP(workDayArray);
                                holder.workDayArray = workDayArray;
                            }
                            holder.sessionItemAdapterArray = holder.workDayArray;
                            populateSessionItemAdapterOneBuild(holder, false, false, false);
                            holder.fieldSelected = true;
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.WORK_DAY);
                        } else {
                            changeUIState(holder, holder.workDayLayout, holder.workDayArrow, true);
                            holder.fieldSelected = false;
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");  // change it
                        }
                        TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
                    }
                });
            }
            holder.hqLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    System.out.println("hqLayoutListener--->");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);
                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else {
                        if (!holder.fieldSelected) {
                            System.out.println("hqLayoutListener3--->");
                            System.out.println("hqLayoutListener5--->" + convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray()).size());

                            if (holder.hqArray.size() == 0) {

                                holder.hqArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray());
                            }
                            holder.sessionItemAdapterArray = holder.hqArray;
                            populateSessionItemAdapterOneBuild(holder, false, true, false);
                            holder.fieldSelected = true;
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.SUBORDINATE);
                        } else {
                            System.out.println("hqLayoutListener4--->");
                            changeUIState(holder, holder.hqLayout, holder.hqArrow, true);
                            holder.fieldSelected = false;
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");   // change
                        }
                    }
                    TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
                }
            });

            if (!(SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && sfType.equalsIgnoreCase("1"))) {
                holder.clusterLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.searchET.setText("");
                        itemPosition = holder.getLayoutPosition();
                        holder.relativeLayout.setSelected(false);
                        if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                        } else {
                            if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                            } else {
                                if (!holder.fieldSelected) {
                                    if (holder.clusterArray.size() == 0) {
                                        holder.clusterArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + holder.selectedHq).getMasterSyncDataJsonArray());
                                        TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        setSelectedCount(holder, holder.clusterArray, false, holder.clusterField, holder.clusterCount);
                                    }
                                    holder.fieldSelected = true;
                                    holder.sessionItemAdapterArray = holder.clusterArray;
                                    populateSessionItemAdapterOneBuild(holder, true, true, false);
                                    onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.CLUSTER);  // change
                                } else {
                                    holder.fieldSelected = false;
                                    setSelectedCount(holder, holder.clusterArray, true, holder.clusterField, holder.clusterCount);
                                    changeUIState(holder, holder.clusterLayout, holder.clusterArrow, true);
                                    onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");       // change
                                }
                            }
                        }
                    }
                });
            }

            holder.jcLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_cluster));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.jointCallArray.size() == 0) {
                                holder.jointCallArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.jointCallArray, false, holder.jcField, holder.jcCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = holder.jointCallArray;
                            populateSessionItemAdapterOneBuild(holder, true, true, false);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.JOINT_WORK);   //change
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.jointCallArray, true, holder.jcField, holder.jcCount);
                            changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, ""); //change
                        }
                    }

                }
            });
            holder.drLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_cluster));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.listedDrArray.size() == 0) {
                                holder.listedDrArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.listedDrArray, false, holder.drField, holder.drCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = filterJsonArray(holder, holder.listedDrArray);
                            populateSessionItemAdapterOneBuild(holder, true, true, true);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.DOCTOR_MAS);  //change
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.listedDrArray, true, holder.drField, holder.drCount);
                            changeUIState(holder, holder.drLayout, holder.drArrow, true);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");//change
                        }
                    }
                }
            });

            holder.chemistLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_cluster));
                    } else {

                        if (!holder.fieldSelected) {
                            if (holder.chemistArray.size() == 0) {
                                holder.chemistArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.chemistArray, false, holder.chemistField, holder.chemistCount);
                            }

                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = filterJsonArray(holder, holder.chemistArray);
                            populateSessionItemAdapterOneBuild(holder, true, true, false);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.CHEMIST_MAS);   //change
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.chemistArray, true, holder.chemistField, holder.chemistCount);
                            changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");//change
                        }
                    }
                }
            });

            holder.stockiestLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);
                    holder.searchET.setText("");
                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_cluster));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.stockiestArray.size() == 0) {
                                holder.stockiestArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_MAS + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.stockiestArray, false, holder.stockiestField, holder.stockiestCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = holder.stockiestArray;
                            populateSessionItemAdapterOneBuild(holder, true, true, false);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.STOCKIEST_MAS);  //change
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.stockiestArray, true, holder.stockiestField, holder.stockiestCount);
                            changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");  //change
                        }

                    }
                }
            });

            holder.unListedDrLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_cluster));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.unListedDrArray.size() == 0) {
                                holder.unListedDrArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR_MAS + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.unListedDrArray, false, holder.unListedDrField, holder.unListedDrCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = filterJsonArray(holder, holder.unListedDrArray);
                            populateSessionItemAdapterOneBuild(holder, true, true, false);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.UNLISTED_DOCTOR_MAS);   //change
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.unListedDrArray, true, holder.unListedDrField, holder.unListedDrCount);
                            changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, true);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");//change
                        }

                    }
                }
            });

            holder.cipLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_cluster));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.cipArray.size() == 0) {
                                holder.cipArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CIP + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.cipArray, false, holder.cipField, holder.cipCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = filterJsonArray(holder, holder.cipArray);
                            populateSessionItemAdapterOneBuild(holder, true, true, false);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.CIP);  //change
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.cipArray, true, holder.cipField, holder.cipCount);
                            changeUIState(holder, holder.cipLayout, holder.cipArrow, true);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, ""); //change
                        }
                    }
                }
            });

            holder.hospLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_cluster));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.hospArray.size() == 0) {
                                holder.hospArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.hospArray, false, holder.hospField, holder.hospCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = filterJsonArray(holder, holder.hospArray);
                            populateSessionItemAdapterOneBuild(holder, true, true, false);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), false, Constants.HOSPITAL); //change
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.hospArray, true, holder.hospField, holder.hospCount);
                            changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
                            onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, ""); //change
                        }
                    }
                }
            });

            holder.remarks.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent event) {
                    if (holder.remarks.hasFocus()) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                        }
                    }
                    return false;
                }
            });

            holder.remarks.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(holder.remarks, 300)});
            holder.remarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    holder.sessionDataOneBuild.setRemarks(holder.remarks.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

           /* holder.remarks.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        holder.sessionDataOneBuild.setRemarks(holder.remarks.getText().toString());
                    }
                    return false;
                }
            });

            holder.remarks.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        holder.sessionDataOneBuild.setRemarks(holder.remarks.getText().toString());
                    }
                }
            });*/

            holder.sessionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionInterfaceOneBuild.deleteClickedOneBuild(inputDataArrayOneBuild, holder.getAbsoluteAdapterPosition());
                }
            });

        } else {
  /*          holder.progress_hq.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            holder.remarks.setImeOptions(EditorInfo.IME_ACTION_DONE);
            holder.remarks.setRawInputType(InputType.TYPE_CLASS_TEXT);
            holder.sessionData = inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition());
            holder.clusterModelArray = new ArrayList<>(holder.sessionData.getCluster());
            holder.jcModelArray = new ArrayList<>(holder.sessionData.getJC());
            holder.listedDrModelArray = new ArrayList<>(holder.sessionData.getListedDr());
            holder.chemistModelArray = new ArrayList<>(holder.sessionData.getChemist());
            holder.stockiestModelArray = new ArrayList<>(holder.sessionData.getStockiest());
            holder.unListedDrModelArray = new ArrayList<>(holder.sessionData.getUnListedDr());
            holder.cipModelArray = new ArrayList<>(holder.sessionData.getCip());
            holder.hospitalModelArray = new ArrayList<>(holder.sessionData.getHospital());
            designation = SharedPref.getDesig(context);
            if (holder.sessionData.getVisible()) {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }*/
            holder.progress_hq.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            holder.remarks.setImeOptions(EditorInfo.IME_ACTION_DONE);
            holder.remarks.setRawInputType(InputType.TYPE_CLASS_TEXT);
            holder.sessionData = inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition());
            holder.hqModelArray = new ArrayList<>(holder.sessionData.getHQs());
            holder.clusterModelArray = new ArrayList<>(holder.sessionData.getCluster());
            holder.jcModelArray = new ArrayList<>(holder.sessionData.getJC());
            holder.listedDrModelArray = new ArrayList<>(holder.sessionData.getListedDr());
            holder.chemistModelArray = new ArrayList<>(holder.sessionData.getChemist());
            holder.stockiestModelArray = new ArrayList<>(holder.sessionData.getStockiest());
            holder.unListedDrModelArray = new ArrayList<>(holder.sessionData.getUnListedDr());
            holder.cipModelArray = new ArrayList<>(holder.sessionData.getCip());
            holder.hospitalModelArray = new ArrayList<>(holder.sessionData.getHospital());
            holder.clustersModelArray = new ArrayList<>(holder.sessionData.getClusters());
            holder.jcsModelArray = new ArrayList<>(holder.sessionData.getJCs());
            holder.listedDrsModelArray = new ArrayList<>(holder.sessionData.getListedDrs());
            holder.chemistsModelArray = new ArrayList<>(holder.sessionData.getChemists());
            holder.stockistsModelArray = new ArrayList<>(holder.sessionData.getStockiests());
            holder.unListedDrsModelArray = new ArrayList<>(holder.sessionData.getUnListedDrs());
            holder.cipsModelArray = new ArrayList<>(holder.sessionData.getCips());
            holder.hospitalsModelArray = new ArrayList<>(holder.sessionData.getHospitals());
            designation = SharedPref.getDesig(context);
            if (holder.sessionData.getVisible()) {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            holder.sessionNoTxt.setText(R.string.session + (position + 1));
            if (holder.getAbsoluteAdapterPosition() == 0) { //No need to show delete icon if there is only one session
                if (inputDataArray.getSessionList().size() > 1 && holder.getAbsoluteAdapterPosition() != 0)
                    holder.sessionDelete.setVisibility(View.VISIBLE);
                else
                    holder.sessionDelete.setVisibility(View.GONE);

            }

            //Work Day
            if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && !isMGR) {
                holder.workDayLayout.setVisibility(View.VISIBLE);
                if (inputDataArray.getSTP_Code().isEmpty() || !holder.sessionData.getWorkType().getFWFlg().equalsIgnoreCase("F") || holder.sessionData.getCluster() == null || holder.sessionData.getCluster().isEmpty()) {
                    holder.workDayField.setText(R.string.select);
                } else {
                    holder.workDayField.setText(inputDataArray.getSTP_Name());
                }
            } else {
                holder.workDayLayout.setVisibility(View.GONE);
            }

            //work Type
            if (holder.sessionData.getWorkType().getName().equals("")) {
                holder.workTypeField.setText(R.string.select);
            } else {
                holder.workTypeField.setText(holder.sessionData.getWorkType().getName());
            }

            if (holder.sessionData.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) { // Y - yes
                holder.hqNeed = "0"; // 0 - Yes
                holder.clusterNeed = "0";
            } else if (holder.sessionData.getWorkType().getTerrSlFlg().equalsIgnoreCase("N")) {
                holder.hqNeed = "1"; // 1 - No
                holder.clusterNeed = "1";
            }
            workTypeBasedUI(holder, holder.sessionData, true);

            //HQ
//        switch (designation){
//            case "MR":
            if (!isMGR) {
                holder.hqLayout.setVisibility(View.GONE);
                holder.sessionData.getHQ().setName(SharedPref.getHqName(context));
                holder.sessionData.getHQ().setCode(SharedPref.getHqCode(context));
                holder.hqField.setText(holder.sessionData.getHQ().getName());
                holder.selectedHq = holder.sessionData.getHQ().getCode();
//                break;
            } else {
//            case "MGR":
                if (holder.sessionData.getHQs().isEmpty()) {
                    holder.hqField.setText(R.string.select);
                } else {
                    StringBuilder hqName = new StringBuilder(), hqcode = new StringBuilder();
                    for (int i = 0; i < holder.hqModelArray.size(); i++) {
                        if (hqName.length() == 0) {
                            hqName = new StringBuilder(holder.hqModelArray.get(i).getName());
                            hqcode = new StringBuilder(holder.hqModelArray.get(i).getCode());
                        } else {
                            hqName.append(", ").append(holder.hqModelArray.get(i).getName());
                            hqcode.append(", ").append(holder.hqModelArray.get(i).getCode());
                        }
                    }
                    if (hqName.length() > 0) {
                        holder.hqField.setText(hqName);
                    }
                    for (int i = 0; i < holder.sessionData.getHQs().size(); i++) {
                        if (holder.selectedHq.isEmpty()) {
                            holder.selectedHq = holder.sessionData.getHQs().get(i).getCode();
                        } else {
                            holder.selectedHq += ",";
                            holder.selectedHq += holder.sessionData.getHQs().get(i).getCode();
                        }
                    }
                    holder.hqArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray());
                    prepareInputData(holder.hqModelArray, holder.hqArray);

                    holder.selectedHQCode.clear();
                    for (int i = 0; i < holder.hqModelArray.size(); i++) {
                        holder.selectedHQCode.add(holder.hqModelArray.get(i).getCode());
                    }
                }
            }
//        }
//        if(SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && isMGR) {
//            holder.hqLayout.setVisibility(View.GONE);
//        }
            if (SharedPref.getWrkAreaName(context).isEmpty() || SharedPref.getWrkAreaName(context).equalsIgnoreCase(null)) {
                holder.textCluster.setText(SharedPref.getClusterCap(context));
            } else {
                holder.textCluster.setText(SharedPref.getWrkAreaName(context));
            }

            if (!holder.selectedHq.isEmpty()) {
                if (isMGR) {
                    for (String hqCode : holder.selectedHq.split(",")) {
                        getDataFromLocal(holder, hqCode);
                    }
                } else {
                    getDataFromLocal(holder, holder.selectedHq);
                }
            }

            //Cluster
            StringBuilder clusterName = new StringBuilder();
            if (isMGR) {
                holder.selectedClusterCodeMap.clear();
                for (int i = 0; i < holder.clustersModelArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.clustersModelArray.get(i);
                    ArrayList<MultiHQItemModelClass> list = multiHQHeaderModelClass.getItemsList();
                    ArrayList<String> selectedClusters = new ArrayList<>();
                    if (holder.selectedClusterCodeMap.containsKey(multiHQHeaderModelClass.getCode())) {
                        selectedClusters = holder.selectedClusterCodeMap.get(multiHQHeaderModelClass.getCode());
                    }
                    for (int j = 0; j < list.size(); j++) {
                        MultiHQItemModelClass multiHQItemModelClass = list.get(j);
                        if (clusterName.length() == 0) {
                            clusterName = new StringBuilder(multiHQItemModelClass.getName());
                        } else {
                            clusterName.append(", ").append(multiHQItemModelClass.getName());
                        }
                        selectedClusters.add(multiHQItemModelClass.getCode());
                    }
                    holder.selectedClusterCodeMap.put(multiHQHeaderModelClass.getCode(), selectedClusters);
                }
                prepareMGRInputData(holder.clustersModelArray, holder.mgrClusterArray);
            } else {
                for (int i = 0; i < holder.clusterModelArray.size(); i++) {
                    if (clusterName.length() == 0) {
                        clusterName = new StringBuilder(holder.clusterModelArray.get(i).getName());
                    } else {
                        clusterName.append(", ").append(holder.clusterModelArray.get(i).getName());
                    }
                }
                prepareInputData(holder.clusterModelArray, holder.clusterArray);
                holder.selectedClusterCode.clear();
                for (int i = 0; i < holder.clusterModelArray.size(); i++) {
                    holder.selectedClusterCode.add(holder.clusterModelArray.get(i).getCode());
                }
            }
            if (clusterName.length() > 0) {
                holder.clusterField.setText(clusterName);
            }

            //Joint Work
            StringBuilder jcName = new StringBuilder();
            if (isMGR) {
                for (int i = 0; i < holder.jcsModelArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.jcsModelArray.get(i);
                    ArrayList<MultiHQItemModelClass> list = multiHQHeaderModelClass.getItemsList();
                    for (int j = 0; j < list.size(); j++) {
                        MultiHQItemModelClass multiHQItemModelClass = list.get(j);
                        if (jcName.length() == 0) {
                            jcName = new StringBuilder(multiHQItemModelClass.getName());
                        } else {
                            jcName.append(", ").append(multiHQItemModelClass.getName());
                        }
                    }
                }
                prepareMGRJCData(holder.jcsModelArray, holder.mgrJointCallArray);
            } else {
                for (int i = 0; i < holder.jcModelArray.size(); i++) {
                    if (jcName.length() == 0) {
                        jcName = new StringBuilder(holder.jcModelArray.get(i).getName());
                    } else {
                        jcName.append(", ").append(holder.jcModelArray.get(i).getName());
                    }
                }
                prepareInputData(holder.jcModelArray, holder.jointCallArray);
            }
            if (jcName.length() > 0) {
                holder.jcField.setText(jcName);
            }
//        if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0")) {
//            holder.jcLayout.setVisibility(View.GONE);
//        } else {
//            holder.jcLayout.setVisibility(View.VISIBLE);
//            for (int i = 0; i < holder.jcModelArray.size(); i++) {
//                if (jcName.length() == 0) {
//                    jcName = new StringBuilder(holder.jcModelArray.get(i).getName());
//                } else {
//                    jcName.append(", ").append(holder.jcModelArray.get(i).getName());
//                }
//            }
//        prepareInputData(holder.jcModelArray, holder.jointCallArray);
//        if (jcName.length() > 0) {
//                holder.jcField.setText(jcName);
//            }
//        }

            //Dr
            StringBuilder drName = new StringBuilder();
            if (isMGR) {
                for (int i = 0; i < holder.listedDrsModelArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.listedDrsModelArray.get(i);
                    ArrayList<MultiHQItemModelClass> list = multiHQHeaderModelClass.getItemsList();
                    for (int j = 0; j < list.size(); j++) {
                        MultiHQItemModelClass multiHQItemModelClass = list.get(j);
                        if (drName.length() == 0) {
                            drName = new StringBuilder(multiHQItemModelClass.getName());
                        } else {
                            drName.append(", ").append(multiHQItemModelClass.getName());
                        }
                    }
                }
                prepareMGRInputData(holder.listedDrsModelArray, holder.mgrListedDrArray);
            } else {
                for (int i = 0; i < holder.listedDrModelArray.size(); i++) {
                    if (drName.length() == 0) {
                        drName = new StringBuilder(holder.listedDrModelArray.get(i).getName());
                    } else {
                        drName.append(", ").append(holder.listedDrModelArray.get(i).getName());
                    }
                }
                prepareInputData(holder.listedDrModelArray, holder.listedDrArray);
            }
            if (drName.length() > 0) {
                holder.drField.setText(drName);
            }

            //Chemist
            StringBuilder chemistName = new StringBuilder();
            if (isMGR) {
                for (int i = 0; i < holder.chemistsModelArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.chemistsModelArray.get(i);
                    ArrayList<MultiHQItemModelClass> list = multiHQHeaderModelClass.getItemsList();
                    for (int j = 0; j < list.size(); j++) {
                        MultiHQItemModelClass multiHQItemModelClass = list.get(j);
                        if (chemistName.length() == 0) {
                            chemistName = new StringBuilder(multiHQItemModelClass.getName());
                        } else {
                            chemistName.append(", ").append(multiHQItemModelClass.getName());
                        }
                    }
                }
                prepareMGRInputData(holder.chemistsModelArray, holder.mgrChemistArray);
            } else {
                for (int i = 0; i < holder.chemistModelArray.size(); i++) {
                    if (chemistName.length() == 0) {
                        chemistName = new StringBuilder(holder.chemistModelArray.get(i).getName());
                    } else {
                        chemistName.append(", ").append(holder.chemistModelArray.get(i).getName());
                    }
                }
                prepareInputData(holder.chemistModelArray, holder.chemistArray);
            }
            if (chemistName.length() > 0) {
                holder.chemistField.setText(chemistName);
            }

            //Stockiest
            StringBuilder stockiestName = new StringBuilder();
            if (isMGR) {
                for (int i = 0; i < holder.stockistsModelArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.stockistsModelArray.get(i);
                    ArrayList<MultiHQItemModelClass> list = multiHQHeaderModelClass.getItemsList();
                    for (int j = 0; j < list.size(); j++) {
                        MultiHQItemModelClass multiHQItemModelClass = list.get(j);
                        if (stockiestName.length() == 0) {
                            stockiestName = new StringBuilder(multiHQItemModelClass.getName());
                        } else {
                            stockiestName.append(", ").append(multiHQItemModelClass.getName());
                        }
                    }
                }
                prepareMGRInputData(holder.stockistsModelArray, holder.mgrStockiestArray);
            } else {
                for (int i = 0; i < holder.stockiestModelArray.size(); i++) {
                    if (stockiestName.length() == 0) {
                        stockiestName = new StringBuilder(holder.stockiestModelArray.get(i).getName());
                    } else {
                        stockiestName.append(", ").append(holder.stockiestModelArray.get(i).getName());
                    }
                }
                prepareInputData(holder.stockiestModelArray, holder.stockiestArray);
            }
            if (stockiestName.length() > 0) {
                holder.stockiestField.setText(stockiestName);
            }

            //UnListed Doctor
            StringBuilder unListedDrName = new StringBuilder();
            for (int i = 0; i < holder.unListedDrModelArray.size(); i++) {
                if (unListedDrName.length() == 0) {
                    unListedDrName = new StringBuilder(holder.unListedDrModelArray.get(i).getName());
                } else {
                    unListedDrName.append(", ").append(holder.unListedDrModelArray.get(i).getName());
                }

            }
            if (unListedDrName.length() > 0) {
                holder.unListedDrField.setText(unListedDrName);
            }
            prepareInputData(holder.unListedDrModelArray, holder.unListedDrArray);

            //Cip
            StringBuilder cipName = new StringBuilder();
            for (int i = 0; i < holder.cipModelArray.size(); i++) {
                if (cipName.length() == 0) {
                    cipName = new StringBuilder(holder.cipModelArray.get(i).getName());
                } else {
                    cipName.append(",").append(holder.cipModelArray.get(i).getName());
                }

            }
            if (cipName.length() > 0) {
                holder.cipField.setText(cipName);
            }
            prepareInputData(holder.cipModelArray, holder.cipArray);

            //Hospital
            StringBuilder hospName = new StringBuilder();
            for (int i = 0; i < holder.hospitalModelArray.size(); i++) {
                if (hospName.length() == 0) {
                    hospName = new StringBuilder(holder.hospitalModelArray.get(i).getName());
                } else {
                    jcName.append(", ").append(holder.hospitalModelArray.get(i).getName());
                }
            }
            if (hospName.length() > 0) {
                holder.hospField.setText(hospName);
            }
            prepareInputData(holder.hospitalModelArray, holder.hospArray);
            holder.remarks.setText(holder.sessionData.getRemarks());
            holder.searchET.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(holder.searchET, 100)});
            holder.searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        holder.searchClearIcon.setVisibility(View.VISIBLE);
                    } else {
                        holder.searchClearIcon.setVisibility(View.GONE);
                    }
                    if (isMGR && holder.hqLayout.getVisibility() != View.VISIBLE && holder.workTypeLayout.getVisibility() != View.VISIBLE && holder.workDayLayout.getVisibility() != View.VISIBLE) {
                        sessionMultiHQItemAdapter.filter(charSequence.toString());
                    } else {
                        sessionItemAdapter.getFilter().filter(charSequence);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (isMGR && holder.hqLayout.getVisibility() != View.VISIBLE && holder.workTypeLayout.getVisibility() != View.VISIBLE && holder.workDayLayout.getVisibility() != View.VISIBLE) {
                        sessionMultiHQItemAdapter.filter(editable.toString());
                    } else {
                        sessionItemAdapter.getFilter().filter(editable.toString());
                    }
                }
            });

            holder.searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE) {
                        UtilityClass.hideKeyboard((Activity) context);
                        return true;
                    }
                    return false;
                }
            });

            holder.searchClearIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                }
            });

            holder.workTypeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);
                    if (!holder.fieldSelected) {
                        if (holder.workTypeArray.size() == 0) {
                            holder.workTypeArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray());
                        }
                        ArrayList<EditModelClass> filteredArray = new ArrayList<>();
                        for (int i = 0; i < holder.workTypeArray.size(); i++) {
                            if (holder.workTypeArray.get(i).getTP_DCR().contains("T")) {
                                filteredArray.add(holder.workTypeArray.get(i));
                            }
                        }
                        holder.sessionItemAdapterArray = filteredArray;
                        populateSessionItemAdapter(holder, false, true, false, false);
                        holder.fieldSelected = true;
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.WORK_TYPE);
                    } else {
                        changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, true);
                        holder.fieldSelected = false;
                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                    }
                    TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
                }
            });

            if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && !isMGR) {
                holder.workDayLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemPosition = holder.getLayoutPosition();
                        holder.relativeLayout.setSelected(false);
                        if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                        } else {
                            if (!holder.fieldSelected) {
                                ArrayList<EditModelClass> workDayArray = new ArrayList<>();
                                if (holder.workDayArray.isEmpty()) {
                                    try {
                                        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STP_SETUP).getMasterSyncDataJsonArray();
                                        if (jsonArray != null && jsonArray.length() > 0) {
                                            JSONObject jsonObject = jsonArray.optJSONObject(0);
                                            String[] dayIDs = CommonUtilsMethods.removeLastComma(jsonObject.optString("Plan_SName")).split("/");
                                            String[] dayCaptions = CommonUtilsMethods.removeLastComma(jsonObject.optString("Plan_Name")).split("/");
                                            for (int index = 0; index < dayIDs.length; index++) {
                                                if (!dayIDs[index].isEmpty()) {
                                                    workDayArray.add(new EditModelClass(dayIDs[index], dayCaptions[index], false));
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    STPDaySorter.sortDays(workDayArray, EditModelClass::getCode);
                                    holder.workDayArray = workDayArray;
                                }
                                holder.sessionItemAdapterArray = holder.workDayArray;
                                populateSessionItemAdapter(holder, false, false, false, false);
                                holder.fieldSelected = true;
                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.WORK_DAY);
                            } else {
                                changeUIState(holder, holder.workDayLayout, holder.workDayArrow, true);
                                holder.fieldSelected = false;
                                onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                            }
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }

            holder.hqLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    if (isMGR) {
                        if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                        } else {
                            if (!holder.fieldSelected) {
                                if (holder.hqArray.isEmpty()) {
                                    holder.hqArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray());
                                    TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                } else {
                                    setSelectedCount(holder, holder.hqArray, false, holder.hqField, holder.hqCount);
                                }
                                holder.fieldSelected = true;
                                holder.sessionItemAdapterArray = holder.hqArray;
                                populateSessionItemAdapter(holder, true, true, true, false);
                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.SUBORDINATE);
                            } else {
                                holder.fieldSelected = false;
                                setSelectedCount(holder, holder.hqArray, true, holder.hqField, holder.hqCount);
                                changeUIState(holder, holder.hqLayout, holder.hqArrow, true);
                                onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                            }
                        }
                    } else {
                        if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                        } else {
                            if (!holder.fieldSelected) {
                                if (holder.hqArray.isEmpty()) {
                                    holder.hqArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray());
                                }
                                holder.fieldSelected = true;
                                holder.sessionItemAdapterArray = holder.hqArray;
                                populateSessionItemAdapter(holder, false, true, false, false);
                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.SUBORDINATE);
                            } else {
                                holder.fieldSelected = false;
                                changeUIState(holder, holder.hqLayout, holder.hqArrow, true);
                                onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                            }
                        }
                        TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
                    }
                    holder.relativeLayout.setSelected(false);
                }
            });

            if (!(SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && !isMGR)) {
                holder.clusterLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.searchET.setText("");
                        itemPosition = holder.getLayoutPosition();
                        holder.relativeLayout.setSelected(false);
                        if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                        } else {
                            if (isMGR && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                            } else {
                                if (isMGR) {
                                    if (!holder.fieldSelected) {
                                        if (holder.mgrClusterArray.isEmpty()) {
                                            holder.mgrClusterArray = prepareModelList(holder.selectedHq, Constants.CLUSTER);
                                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            setSelectedCountMGR(holder, holder.mgrClusterArray, false, holder.clusterField, holder.clusterCount);
                                        }
                                        holder.fieldSelected = true;
                                        holder.mgrSessionItemAdapterArray = holder.mgrClusterArray;
                                        populateSessionMultiHQItemAdapter(holder, SharedPref.getClusterCap(context));
                                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CLUSTER);
                                    } else {
                                        holder.fieldSelected = false;
                                        setSelectedCountMGR(holder, holder.mgrClusterArray, true, holder.clusterField, holder.clusterCount);
                                        changeUIState(holder, holder.clusterLayout, holder.clusterArrow, true);
                                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                                    }
                                } else {
                                    if (!holder.fieldSelected) {
                                        if (holder.clusterArray.size() == 0) {
                                            holder.clusterArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + holder.selectedHq).getMasterSyncDataJsonArray());
                                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            setSelectedCount(holder, holder.clusterArray, false, holder.clusterField, holder.clusterCount);
                                        }
                                        holder.fieldSelected = true;
                                        holder.sessionItemAdapterArray = holder.clusterArray;
                                        populateSessionItemAdapter(holder, true, true, false, false);
                                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CLUSTER);
                                    } else {
                                        holder.fieldSelected = false;
                                        setSelectedCount(holder, holder.clusterArray, true, holder.clusterField, holder.clusterCount);
                                        changeUIState(holder, holder.clusterLayout, holder.clusterArrow, true);
                                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                                    }
                                }
                            }
                        }
                    }
                });
            }

            holder.jcLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (isMGR && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select) + SharedPref.getClusterCap(context));
                    } else {
                        if (isMGR) {
                            if (!holder.fieldSelected) {
                                if (holder.mgrJointCallArray.isEmpty()) {
                                    holder.mgrJointCallArray = prepareModelList(holder.selectedHq, Constants.JOINT_WORK);
                                    TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                } else {
                                    setSelectedCountMGR(holder, holder.mgrJointCallArray, false, holder.jcField, holder.jcCount);
                                }
                                holder.fieldSelected = true;
                                holder.mgrSessionItemAdapterArray = filterMasterList(holder, holder.mgrJointCallArray, false);
                                populateSessionMultiHQItemAdapter(holder, "Joint Work");
                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.JOINT_WORK);
                            } else {
                                holder.fieldSelected = false;
                                setSelectedCountMGR(holder, holder.mgrJointCallArray, true, holder.jcField, holder.jcCount);
                                changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
                                onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                            }
                        } else {
                            if (!holder.fieldSelected) {
                                if (holder.jointCallArray.isEmpty()) {
                                    holder.jointCallArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + holder.selectedHq).getMasterSyncDataJsonArray());
                                    TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                } else {
                                    setSelectedCount(holder, holder.jointCallArray, false, holder.jcField, holder.jcCount);
                                }
                                holder.fieldSelected = true;
                                holder.sessionItemAdapterArray = holder.jointCallArray;
                                populateSessionItemAdapter(holder, true, true, false, false);
                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.JOINT_WORK);
                            } else {
                                holder.fieldSelected = false;
                                setSelectedCount(holder, holder.jointCallArray, true, holder.jcField, holder.jcCount);
                                changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
                                onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                            }
                        }
                    }

                }
            });

            if (!(SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && sfType.equalsIgnoreCase("1"))) {
                holder.drLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.searchET.setText("");
                        itemPosition = holder.getLayoutPosition();
                        holder.relativeLayout.setSelected(false);

                        if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                        } else if (isMGR && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                        } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select) + SharedPref.getClusterCap(context));
                        } else {
                            if (isMGR) {
                                if (!holder.fieldSelected) {
                                    if (holder.mgrListedDrArray.isEmpty()) {
//                                        holder.mgrListedDrArray = prepareModelList(holder.selectedHq, Constants.DOCTOR);
                                        holder.mgrListedDrArray = prepareModelList(holder.selectedHq, Constants.DOCTOR_MAS);
                                        TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        setSelectedCountMGR(holder, holder.mgrListedDrArray, false, holder.drField, holder.drCount);
                                    }
                                    holder.fieldSelected = true;
                                    holder.mgrSessionItemAdapterArray = filterMasterList(holder, holder.mgrListedDrArray, true);
                                    populateSessionMultiHQItemAdapter(holder, SharedPref.getDrCap(context));
//                                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.DOCTOR);
                                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.DOCTOR_MAS);
                                } else {
                                    holder.fieldSelected = false;
                                    setSelectedCountMGR(holder, holder.mgrListedDrArray, true, holder.drField, holder.drCount);
                                    changeUIState(holder, holder.drLayout, holder.drArrow, true);
                                    onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                                }
                            } else {
                                if (!holder.fieldSelected) {
                                    if (holder.listedDrArray.isEmpty()) {
//                                        holder.listedDrArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + holder.selectedHq).getMasterSyncDataJsonArray());
                                        holder.listedDrArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + holder.selectedHq).getMasterSyncDataJsonArray());
                                        TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        setSelectedCount(holder, holder.listedDrArray, false, holder.drField, holder.drCount);
                                    }
                                    holder.fieldSelected = true;
                                    holder.sessionItemAdapterArray = filterJsonArray(holder, holder.listedDrArray);
                                    populateSessionItemAdapter(holder, true, true, false, true);
//                                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.DOCTOR);
                                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.DOCTOR_MAS);
                                } else {
                                    holder.fieldSelected = false;
                                    setSelectedCount(holder, holder.listedDrArray, true, holder.drField, holder.drCount);
                                    changeUIState(holder, holder.drLayout, holder.drArrow, true);
                                    onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                                }
                            }
                        }
                    }
                });
            }

            if (!(SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && sfType.equalsIgnoreCase("1"))) {
                holder.chemistLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.searchET.setText("");
                        itemPosition = holder.getLayoutPosition();
                        holder.relativeLayout.setSelected(false);

                        if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                        } else if (isMGR && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                        } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.select) + SharedPref.getClusterCap(context));
                        } else {
                            if (isMGR) {
                                if (!holder.fieldSelected) {
                                    if (holder.mgrChemistArray.isEmpty()) {
//                                        holder.mgrChemistArray = prepareModelList(holder.selectedHq, Constants.CHEMIST);
                                        holder.mgrChemistArray = prepareModelList(holder.selectedHq, Constants.CHEMIST_MAS);
                                        TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        setSelectedCountMGR(holder, holder.mgrChemistArray, false, holder.chemistField, holder.chemistCount);
                                    }
                                    holder.fieldSelected = true;
                                    holder.mgrSessionItemAdapterArray = filterMasterList(holder, holder.mgrChemistArray, true);
                                    populateSessionMultiHQItemAdapter(holder, SharedPref.getChmCap(context));
//                                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CHEMIST);
                                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CHEMIST_MAS);
                                } else {
                                    holder.fieldSelected = false;
                                    setSelectedCountMGR(holder, holder.mgrChemistArray, true, holder.chemistField, holder.chemistCount);
                                    changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
                                    onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                                }
                            } else {
                                if (!holder.fieldSelected) {
                                    if (holder.chemistArray.isEmpty()) {
//                                        holder.chemistArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + holder.selectedHq).getMasterSyncDataJsonArray());
                                        holder.chemistArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + holder.selectedHq).getMasterSyncDataJsonArray());
                                        TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        setSelectedCount(holder, holder.chemistArray, false, holder.chemistField, holder.chemistCount);
                                    }
                                    holder.fieldSelected = true;
                                    holder.sessionItemAdapterArray = filterJsonArray(holder, holder.chemistArray);
                                    populateSessionItemAdapter(holder, true, true, false, false);
//                                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CHEMIST);
                                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CHEMIST_MAS);
                                } else {
                                    holder.fieldSelected = false;
                                    setSelectedCount(holder, holder.chemistArray, true, holder.chemistField, holder.chemistCount);
                                    changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
                                    onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                                }
                            }
                        }
                    }
                });
            }

            holder.stockiestLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);
                    holder.searchET.setText("");
                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (isMGR && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select) + SharedPref.getClusterCap(context));
                    } else {
                        if (isMGR) {
                            if (!holder.fieldSelected) {
                                if (holder.mgrStockiestArray.isEmpty()) {
                                    holder.mgrStockiestArray = prepareModelList(holder.selectedHq, Constants.STOCKIEST_MAS);
//                                    holder.mgrStockiestArray = prepareModelList(holder.selectedHq, Constants.STOCKIEST_MAS);
                                    TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                } else {
                                    setSelectedCountMGR(holder, holder.mgrStockiestArray, false, holder.stockiestField, holder.stockiestCount);
                                }
                                holder.fieldSelected = true;
                                holder.mgrSessionItemAdapterArray = filterMasterList(holder, holder.mgrStockiestArray, false);
                                populateSessionMultiHQItemAdapter(holder, SharedPref.getStkCap(context));
//                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.STOCKIEST);
                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.STOCKIEST_MAS);
                            } else {
                                holder.fieldSelected = false;
                                setSelectedCountMGR(holder, holder.mgrStockiestArray, true, holder.stockiestField, holder.stockiestCount);
                                changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
                                onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                            }
                        } else {
                            if (!holder.fieldSelected) {
                                if (holder.stockiestArray.isEmpty()) {
//                                    holder.stockiestArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + holder.selectedHq).getMasterSyncDataJsonArray());
                                    holder.stockiestArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_MAS + holder.selectedHq).getMasterSyncDataJsonArray());
                                    TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                                } else {
                                    setSelectedCount(holder, holder.stockiestArray, false, holder.stockiestField, holder.stockiestCount);
                                }
                                holder.fieldSelected = true;
                                holder.sessionItemAdapterArray = holder.stockiestArray;
                                populateSessionItemAdapter(holder, true, true, false, false);
//                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.STOCKIEST);
                                onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.STOCKIEST_MAS);
                            } else {
                                holder.fieldSelected = false;
                                setSelectedCount(holder, holder.stockiestArray, true, holder.stockiestField, holder.stockiestCount);
                                changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
                                onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                            }
                        }
                    }
                }
            });

            holder.unListedDrLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (isMGR && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select) + SharedPref.getClusterCap(context));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.unListedDrArray.size() == 0) {
//                                holder.unListedDrArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + holder.selectedHq).getMasterSyncDataJsonArray());
                                holder.unListedDrArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR_MAS + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.unListedDrArray, false, holder.unListedDrField, holder.unListedDrCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = filterJsonArray(holder, holder.unListedDrArray);
                            populateSessionItemAdapter(holder, true, true, false, false);
//                            onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.UNLISTED_DOCTOR);
                            onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.UNLISTED_DOCTOR_MAS);
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.unListedDrArray, true, holder.unListedDrField, holder.unListedDrCount);
                            changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, true);
                            onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                        }

                    }
                }
            });

            holder.cipLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (isMGR && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select) + SharedPref.getClusterCap(context));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.cipArray.size() == 0) {
                                holder.cipArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CIP + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.cipArray, false, holder.cipField, holder.cipCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = filterJsonArray(holder, holder.cipArray);
                            populateSessionItemAdapter(holder, true, true, false, false);
                            onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CIP);
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.cipArray, true, holder.cipField, holder.cipCount);
                            changeUIState(holder, holder.cipLayout, holder.cipArrow, true);
                            onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                        }
                    }
                }
            });

            holder.hospLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.searchET.setText("");
                    itemPosition = holder.getLayoutPosition();
                    holder.relativeLayout.setSelected(false);

                    if (holder.workTypeField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_worktype));
                    } else if (isMGR && holder.hqField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_hq));
                    } else if (holder.clusterField.getText().toString().equalsIgnoreCase(context.getString(R.string.select))) {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select) + SharedPref.getClusterCap(context));
                    } else {
                        if (!holder.fieldSelected) {
                            if (holder.hospArray.size() == 0) {
                                holder.hospArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + holder.selectedHq).getMasterSyncDataJsonArray());
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                setSelectedCount(holder, holder.hospArray, false, holder.hospField, holder.hospCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = filterJsonArray(holder, holder.hospArray);
                            populateSessionItemAdapter(holder, true, true, false, false);
                            onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.HOSPITAL);
                        } else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.hospArray, true, holder.hospField, holder.hospCount);
                            changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
                            onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                        }
                    }
                }
            });

            holder.remarks.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent event) {
                    if (holder.remarks.hasFocus()) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                        }
                    }
                    return false;
                }
            });

            holder.remarks.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(holder.remarks, 300)});
            holder.remarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    holder.sessionData.setRemarks(holder.remarks.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

       /*     holder.remarks.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        holder.sessionData.setRemarks(holder.remarks.getText().toString());
                    }
                    return false;
                }
            });

            holder.remarks.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        holder.sessionData.setRemarks(holder.remarks.getText().toString());
                    }
                }
            });*/

            holder.sessionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionInterface.deleteClicked(inputDataArray, holder.getAbsoluteAdapterPosition());
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            return inputDataArrayOneBuild.getSessionList().size();
        } else {
            return inputDataArray.getSessionList().size();
        }
    }

    public void workTypeBasedUI(MyViewHolder holder, ModelClass.SessionList session, boolean bool) {

        String workType = session.getWorkType().getFWFlg();
        switch (workType) {
            case "F": {
                if (holder.hqNeed.equalsIgnoreCase("0"))
                    holder.hqLayout.setVisibility(View.VISIBLE);
                else if (holder.hqNeed.equalsIgnoreCase("1"))
                    holder.hqLayout.setVisibility(View.GONE);

                if (holder.clusterNeed.equalsIgnoreCase("0"))
                    holder.clusterLayout.setVisibility(View.VISIBLE);
                else if (holder.clusterNeed.equalsIgnoreCase("1"))
                    holder.clusterLayout.setVisibility(View.GONE);

                if (jwNeed.equalsIgnoreCase("0"))
                    holder.jcLayout.setVisibility(View.VISIBLE);
                else
                    holder.jcLayout.setVisibility(View.GONE);

                if (drNeed.equalsIgnoreCase("0"))
                    holder.drLayout.setVisibility(View.VISIBLE);
                else
                    holder.drLayout.setVisibility(View.GONE);

                if (chemistNeed.equalsIgnoreCase("0"))
                    holder.chemistLayout.setVisibility(View.VISIBLE);
                else
                    holder.chemistLayout.setVisibility(View.GONE);

                if (stockiestNeed.equalsIgnoreCase("0"))
                    holder.stockiestLayout.setVisibility(View.VISIBLE);
                else
                    holder.stockiestLayout.setVisibility(View.GONE);

//                if (unListedDrNeed.equalsIgnoreCase("0"))
//                    holder.unListedDrLayout.setVisibility(View.VISIBLE);
//                else
//                    holder.unListedDrLayout.setVisibility(View.GONE);

                if (cipNeed.equalsIgnoreCase("0"))
                    holder.cipLayout.setVisibility(View.VISIBLE);
                else
                    holder.cipLayout.setVisibility(View.GONE);

                if (hospNeed.equalsIgnoreCase("0"))
                    holder.hospLayout.setVisibility(View.VISIBLE);
                else
                    holder.hospLayout.setVisibility(View.GONE);

                if (SharedPref.getStpNeed(context).equalsIgnoreCase(SharedPref.getStpBasedMtp(context)) && SharedPref.getStpNeed(context).equalsIgnoreCase("0") && !isMGR) {
                    holder.workDayLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.workDayLayout.setVisibility(View.GONE);
                }
                break;
            }
            case "W":
            case "H":
            case "L": {
                holder.hqLayout.setVisibility(View.GONE);
                holder.clusterLayout.setVisibility(View.GONE);
                holder.jcLayout.setVisibility(View.GONE);
                holder.drLayout.setVisibility(View.GONE);
                holder.chemistLayout.setVisibility(View.GONE);
                holder.stockiestLayout.setVisibility(View.GONE);
                holder.unListedDrLayout.setVisibility(View.GONE);
                holder.cipLayout.setVisibility(View.GONE);
                holder.hospLayout.setVisibility(View.GONE);
                holder.workDayLayout.setVisibility(View.GONE);
                break;
            }
            case "N": {
                if (session.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                    if (holder.hqNeed.equalsIgnoreCase("0"))
                        holder.hqLayout.setVisibility(View.VISIBLE);
                    else if (holder.hqNeed.equalsIgnoreCase("1"))
                        holder.hqLayout.setVisibility(View.GONE);

                    if (holder.clusterNeed.equalsIgnoreCase("0"))
                        holder.clusterLayout.setVisibility(View.VISIBLE);
                    else if (holder.clusterNeed.equalsIgnoreCase("1"))
                        holder.clusterLayout.setVisibility(View.GONE);

                    if (jwNeed.equalsIgnoreCase("0"))
                        holder.jcLayout.setVisibility(View.VISIBLE);
                    else if (jwNeed.equalsIgnoreCase("1"))
                        holder.jcLayout.setVisibility(View.GONE);
                } else {
                    holder.hqLayout.setVisibility(View.GONE);
                    holder.clusterLayout.setVisibility(View.GONE);
                    holder.jcLayout.setVisibility(View.GONE);
                }

                holder.drLayout.setVisibility(View.GONE);
                holder.chemistLayout.setVisibility(View.GONE);
                holder.stockiestLayout.setVisibility(View.GONE);
                holder.unListedDrLayout.setVisibility(View.GONE);
                holder.cipLayout.setVisibility(View.GONE);
                holder.hospLayout.setVisibility(View.GONE);
                holder.workDayLayout.setVisibility(View.GONE);
                break;
            }
            default: {
                if (holder.hqNeed.equalsIgnoreCase("0"))
                    holder.hqLayout.setVisibility(View.VISIBLE);
                else if (holder.hqNeed.equalsIgnoreCase("1"))
                    holder.hqLayout.setVisibility(View.GONE);

                if (holder.clusterNeed.equalsIgnoreCase("0"))
                    holder.clusterLayout.setVisibility(View.VISIBLE);
                else if (holder.clusterNeed.equalsIgnoreCase("1"))
                    holder.clusterLayout.setVisibility(View.GONE);

                if (jwNeed.equalsIgnoreCase("0"))
                    holder.jcLayout.setVisibility(View.VISIBLE);
                else
                    holder.jcLayout.setVisibility(View.GONE);

                if (drNeed.equalsIgnoreCase("0"))
                    holder.drLayout.setVisibility(View.VISIBLE);
                else
                    holder.drLayout.setVisibility(View.GONE);

                if (chemistNeed.equalsIgnoreCase("0"))
                    holder.chemistLayout.setVisibility(View.VISIBLE);
                else
                    holder.chemistLayout.setVisibility(View.GONE);

                if (stockiestNeed.equalsIgnoreCase("0"))
                    holder.stockiestLayout.setVisibility(View.VISIBLE);
                else
                    holder.stockiestLayout.setVisibility(View.GONE);

//                if (unListedDrNeed.equalsIgnoreCase("0"))
//                    holder.unListedDrLayout.setVisibility(View.VISIBLE);
//                else
//                    holder.unListedDrLayout.setVisibility(View.GONE);

                if (cipNeed.equalsIgnoreCase("0"))
                    holder.cipLayout.setVisibility(View.VISIBLE);
                else
                    holder.cipLayout.setVisibility(View.GONE);

                if (hospNeed.equalsIgnoreCase("0"))
                    holder.hospLayout.setVisibility(View.VISIBLE);
                else
                    holder.hospLayout.setVisibility(View.GONE);

                if (SharedPref.getStpNeed(context).equalsIgnoreCase(SharedPref.getStpBasedMtp(context)) && SharedPref.getStpNeed(context).equalsIgnoreCase("0") && !isMGR) {
                    holder.workDayLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.workDayLayout.setVisibility(View.GONE);
                }
            }
        }

        if (bool) {
            switch (session.getLayoutVisible()) {
                case Constants.WORK_TYPE: {
                    changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, false);
                    break;
                }
                case Constants.SUBORDINATE: {
                    changeUIState(holder, holder.hqLayout, holder.hqArrow, false);
                    break;
                }
                case Constants.CLUSTER: {
                    changeUIState(holder, holder.clusterLayout, holder.clusterArrow, false);
                    break;
                }
                case Constants.JOINT_WORK: {
                    changeUIState(holder, holder.jcLayout, holder.jcArrow, false);
                    break;
                }
              /*  case Constants.DOCTOR: {
                    changeUIState(holder, holder.drLayout, holder.drArrow, false);
                    break;
                }
                case Constants.CHEMIST: {
                    changeUIState(holder, holder.chemistLayout, holder.chemistArrow, false);
                    break;
                }
                case Constants.STOCKIEST: {
                    changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, false);
                    break;
                }
                case Constants.UNLISTED_DOCTOR: {
                    changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, false);
                    break;
                }*/
                case Constants.DOCTOR_MAS: {
                    changeUIState(holder, holder.drLayout, holder.drArrow, false);
                    break;
                }

                case Constants.CHEMIST_MAS: {
                    changeUIState(holder, holder.chemistLayout, holder.chemistArrow, false);
                    break;
                }
             /*   case Constants.STOCKIEST:{
                    changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, false);
                    break;
                }*/
                case Constants.STOCKIEST_MAS: {
                    changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, false);
                    break;
                }
            /*    case Constants.UNLISTED_DOCTOR:{
                    changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, false);
                    break;
                }*/
                case Constants.UNLISTED_DOCTOR_MAS: {
                    changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, false);
                    break;
                }
                case Constants.CIP: {
                    changeUIState(holder, holder.cipLayout, holder.cipArrow, false);
                    break;
                }
                case Constants.HOSPITAL: {
                    changeUIState(holder, holder.hospLayout, holder.hospArrow, false);
                    break;
                }
                case Constants.WORK_DAY: {
                    changeUIState(holder, holder.workDayLayout, holder.workDayArrow, false);
                    break;
                }
            }
        }

    }

    public void worktypeBasedUiOneBuild(MyViewHolder holder, OneBuildModelClass.SessionList sessionOneBuild, boolean bool) {
        String worktype = sessionOneBuild.getWorkType().getFWFlg();
        switch (worktype) {
            case "F":
                if (holder.hqNeed.equalsIgnoreCase("0"))
                    holder.hqLayout.setVisibility(View.VISIBLE);
                else if (holder.hqNeed.equalsIgnoreCase("1"))
                    holder.hqLayout.setVisibility(View.GONE);

                if (holder.clusterNeed.equalsIgnoreCase("0"))
                    holder.clusterLayout.setVisibility(View.VISIBLE);
                else if (holder.clusterNeed.equalsIgnoreCase("1"))
                    holder.clusterLayout.setVisibility(View.GONE);

                if (jwNeed.equalsIgnoreCase("0"))
                    holder.jcLayout.setVisibility(View.VISIBLE);
                else
                    holder.jcLayout.setVisibility(View.GONE);

                if (drNeed.equalsIgnoreCase("0"))
                    holder.drLayout.setVisibility(View.VISIBLE);
                else
                    holder.drLayout.setVisibility(View.GONE);

                if (chemistNeed.equalsIgnoreCase("0"))
                    holder.chemistLayout.setVisibility(View.VISIBLE);
                else
                    holder.chemistLayout.setVisibility(View.GONE);

                if (stockiestNeed.equalsIgnoreCase("0"))
                    holder.stockiestLayout.setVisibility(View.VISIBLE);
                else
                    holder.stockiestLayout.setVisibility(View.GONE);

//                if (unListedDrNeed.equalsIgnoreCase("0"))
//                    holder.unListedDrLayout.setVisibility(View.VISIBLE);
//                else
//                    holder.unListedDrLayout.setVisibility(View.GONE);

                if (cipNeed.equalsIgnoreCase("0"))
                    holder.cipLayout.setVisibility(View.VISIBLE);
                else
                    holder.cipLayout.setVisibility(View.GONE);

                if (hospNeed.equalsIgnoreCase("0"))
                    holder.hospLayout.setVisibility(View.VISIBLE);
                else
                    holder.hospLayout.setVisibility(View.GONE);

                if (SharedPref.getStpNeed(context).equalsIgnoreCase(SharedPref.getStpBasedMtp(context)) && SharedPref.getStpNeed(context).equalsIgnoreCase("0") && sfType.equalsIgnoreCase("1")) {
                    holder.workDayLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.workDayLayout.setVisibility(View.GONE);
                }
                break;

            case "W":
            case "H":
            case "L": {
                holder.hqLayout.setVisibility(View.GONE);
                holder.clusterLayout.setVisibility(View.GONE);
                holder.jcLayout.setVisibility(View.GONE);
                holder.drLayout.setVisibility(View.GONE);
                holder.chemistLayout.setVisibility(View.GONE);
                holder.stockiestLayout.setVisibility(View.GONE);
                holder.unListedDrLayout.setVisibility(View.GONE);
                holder.cipLayout.setVisibility(View.GONE);
                holder.hospLayout.setVisibility(View.GONE);
                holder.workDayLayout.setVisibility(View.GONE);
                break;
            }

            case "N": {
                if (sessionOneBuild.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) {
                    if (holder.hqNeed.equalsIgnoreCase("0"))
                        holder.hqLayout.setVisibility(View.VISIBLE);
                    else if (holder.hqNeed.equalsIgnoreCase("1"))
                        holder.hqLayout.setVisibility(View.GONE);

                    if (holder.clusterNeed.equalsIgnoreCase("0"))
                        holder.clusterLayout.setVisibility(View.VISIBLE);
                    else if (holder.clusterNeed.equalsIgnoreCase("1"))
                        holder.clusterLayout.setVisibility(View.GONE);

                    if (jwNeed.equalsIgnoreCase("0"))
                        holder.jcLayout.setVisibility(View.VISIBLE);
                    else if (jwNeed.equalsIgnoreCase("1"))
                        holder.jcLayout.setVisibility(View.GONE);
                } else {
                    holder.hqLayout.setVisibility(View.GONE);
                    holder.clusterLayout.setVisibility(View.GONE);
                    holder.jcLayout.setVisibility(View.GONE);
                }

                holder.drLayout.setVisibility(View.GONE);
                holder.chemistLayout.setVisibility(View.GONE);
                holder.stockiestLayout.setVisibility(View.GONE);
                holder.unListedDrLayout.setVisibility(View.GONE);
                holder.cipLayout.setVisibility(View.GONE);
                holder.hospLayout.setVisibility(View.GONE);
                holder.workDayLayout.setVisibility(View.GONE);
                break;
            }

            default: {
                if (holder.hqNeed.equalsIgnoreCase("0"))
                    holder.hqLayout.setVisibility(View.VISIBLE);
                else if (holder.hqNeed.equalsIgnoreCase("1"))
                    holder.hqLayout.setVisibility(View.GONE);

                if (holder.clusterNeed.equalsIgnoreCase("0"))
                    holder.clusterLayout.setVisibility(View.VISIBLE);
                else if (holder.clusterNeed.equalsIgnoreCase("1"))
                    holder.clusterLayout.setVisibility(View.GONE);

                if (jwNeed.equalsIgnoreCase("0"))
                    holder.jcLayout.setVisibility(View.VISIBLE);
                else
                    holder.jcLayout.setVisibility(View.GONE);

                if (drNeed.equalsIgnoreCase("0"))
                    holder.drLayout.setVisibility(View.VISIBLE);
                else
                    holder.drLayout.setVisibility(View.GONE);

                if (chemistNeed.equalsIgnoreCase("0"))
                    holder.chemistLayout.setVisibility(View.VISIBLE);
                else
                    holder.chemistLayout.setVisibility(View.GONE);

                if (stockiestNeed.equalsIgnoreCase("0"))
                    holder.stockiestLayout.setVisibility(View.VISIBLE);
                else
                    holder.stockiestLayout.setVisibility(View.GONE);

//                if (unListedDrNeed.equalsIgnoreCase("0"))
//                    holder.unListedDrLayout.setVisibility(View.VISIBLE);
//                else
//                    holder.unListedDrLayout.setVisibility(View.GONE);

                if (cipNeed.equalsIgnoreCase("0"))
                    holder.cipLayout.setVisibility(View.VISIBLE);
                else
                    holder.cipLayout.setVisibility(View.GONE);

                if (hospNeed.equalsIgnoreCase("0"))
                    holder.hospLayout.setVisibility(View.VISIBLE);
                else
                    holder.hospLayout.setVisibility(View.GONE);

                if (SharedPref.getStpNeed(context).equalsIgnoreCase(SharedPref.getStpBasedMtp(context)) && SharedPref.getStpNeed(context).equalsIgnoreCase("0") && sfType.equalsIgnoreCase("1")) {
                    holder.workDayLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.workDayLayout.setVisibility(View.GONE);
                }
            }

        }
        if (bool) {
            switch (sessionOneBuild.getLayoutVisible()) {
                case Constants.WORK_TYPE: {
                    changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, false);
                    break;
                }
                case Constants.SUBORDINATE: {
                    changeUIState(holder, holder.hqLayout, holder.hqArrow, false);
                    break;
                }
                case Constants.CLUSTER: {
                    changeUIState(holder, holder.clusterLayout, holder.clusterArrow, false);
                    break;
                }
                case Constants.JOINT_WORK: {
                    changeUIState(holder, holder.jcLayout, holder.jcArrow, false);
                    break;
                }
                case Constants.DOCTOR_MAS: {
                    changeUIState(holder, holder.drLayout, holder.drArrow, false);
                    break;
                }
                case Constants.CHEMIST_MAS: {
                    changeUIState(holder, holder.chemistLayout, holder.chemistArrow, false);
                    break;
                }
                case Constants.STOCKIEST_MAS: {
                    changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, false);
                    break;
                }
                case Constants.UNLISTED_DOCTOR_MAS: {
                    changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, false);
                    break;
                }
                case Constants.CIP: {
                    changeUIState(holder, holder.cipLayout, holder.cipArrow, false);
                    break;
                }
                case Constants.HOSPITAL: {
                    changeUIState(holder, holder.hospLayout, holder.hospArrow, false);
                    break;
                }
                case Constants.WORK_DAY: {
                    changeUIState(holder, holder.workDayLayout, holder.workDayArrow, false);
                    break;
                }
            }
        }
    }

    public void getDataFromLocal(MyViewHolder holder, String hqCode) {
        if (!masterDataDao.getMasterSyncDataOfHQ(Constants.CLUSTER + hqCode)) {
            prepareMasterToSync(holder, hqCode);
        }

        holder.hqModelArray = new ArrayList<>();
        holder.clusterArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray());
        holder.jointCallArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + hqCode).getMasterSyncDataJsonArray());
        holder.listedDrArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + hqCode).getMasterSyncDataJsonArray());
        holder.chemistArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + hqCode).getMasterSyncDataJsonArray());
        holder.stockiestArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_MAS + hqCode).getMasterSyncDataJsonArray());
        holder.unListedDrArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR_MAS + hqCode).getMasterSyncDataJsonArray());
        holder.cipArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.CIP + hqCode).getMasterSyncDataJsonArray());
        holder.hospArray = convertJSONToModel(masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + hqCode).getMasterSyncDataJsonArray());

        if (SharedPref.getSfType(context).equalsIgnoreCase("2")) {
            holder.mgrClusterArray = prepareModelList(holder.selectedHq, Constants.CLUSTER);
            holder.mgrJointCallArray = prepareModelList(holder.selectedHq, Constants.JOINT_WORK);
            holder.mgrListedDrArray = prepareModelList(holder.selectedHq, Constants.DOCTOR_MAS);
            holder.mgrChemistArray = prepareModelList(holder.selectedHq, Constants.CHEMIST_MAS);
            holder.mgrStockiestArray = prepareModelList(holder.selectedHq, Constants.STOCKIEST_MAS);
        }
    }

    public ArrayList<EditModelClass> convertJSONToModel(JSONArray jsonArray) {
        ArrayList<String> IDs = new ArrayList<>();
        ArrayList<EditModelClass> MainList = new ArrayList<>();
        Type type = new TypeToken<ArrayList<EditModelClass>>() {
        }.getType();
        ArrayList<EditModelClass> Lister = new Gson().fromJson(String.valueOf(jsonArray), type);

        for (EditModelClass list : Lister) {
            if (!IDs.contains(list.getCode())) {
                IDs.add(list.getCode());
                if (list.getFWFlg() != null
                        && ((list.getFWFlg().equalsIgnoreCase("W") && weeklyOffEditable.equalsIgnoreCase("1"))
                        || (list.getFWFlg().equalsIgnoreCase("H") && holidayEditable.equalsIgnoreCase("1")))) {
                    continue;
                }
                MainList.add(list);
            }
        }

        return MainList;
    }

    public ArrayList<MultiHQHeaderModelClass> prepareModelList(String selectedHQs, String masterKey) {
        ArrayList<MultiHQHeaderModelClass> mainList = new ArrayList<>();
        JSONArray hqJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
        for (int i = 0; i < hqJsonArray.length(); i++) {
            JSONObject hqJsonObject = hqJsonArray.optJSONObject(i);
            if (selectedHQs.contains(hqJsonObject.optString("Code"))) {
                String hqCode = hqJsonObject.optString("Code"), hqName = hqJsonObject.optString("name");
                JSONArray masterJsonArray = masterDataDao.getMasterDataTableOrNew(masterKey + hqCode).getMasterSyncDataJsonArray();
                ArrayList<MultiHQItemModelClass> itemsList = new ArrayList<>();
                ArrayList<String> IDs = new ArrayList<>();
                for (int j = 0; j < masterJsonArray.length(); j++) {
                    JSONObject jsonObject = masterJsonArray.optJSONObject(j);
                    String name = "", code = "", clusterCode = "", clusterName = "";
                    if (jsonObject.has("Name")) {
                        name = jsonObject.optString("Name");
                    } else if (jsonObject.has("name")) {
                        name = jsonObject.optString("name");
                    }
                    if (jsonObject.has("Code")) {
                        code = jsonObject.optString("Code");
                    } else if (jsonObject.has("code")) {
                        code = jsonObject.optString("code");
                    }
                    if (jsonObject.has("Town_Code")) {
                        clusterCode = jsonObject.optString("Town_Code");
                    } else if (jsonObject.has("town_code")) {
                        clusterCode = jsonObject.optString("town_code");
                    }
                    if (jsonObject.has("Town_Name")) {
                        clusterName = jsonObject.optString("Town_Name");
                    } else if (jsonObject.has("town_name")) {
                        clusterName = jsonObject.optString("town_name");
                    }
                    if (!IDs.contains(code)) {
                        IDs.add(code);
                        itemsList.add(new MultiHQItemModelClass(name, code, hqCode, clusterCode, clusterName, false));
                    }
                }
                itemsList.sort((i1, i2) -> {
                    int clusterCompare = i1.getClusterName().compareToIgnoreCase(i2.getClusterName());
                    if (clusterCompare != 0) {
                        return clusterCompare;
                    }
                    boolean o1IsIndependent = Constants.INDEPENDENT.equalsIgnoreCase(i1.getName());
                    boolean o2IsIndependent = Constants.INDEPENDENT.equalsIgnoreCase(i2.getName());
                    if (o1IsIndependent && !o2IsIndependent) return -1;
                    if (!o1IsIndependent && o2IsIndependent) return 1;
//                    if (i1.getName().equalsIgnoreCase(Constants.INDEPENDENT)) return -1;
//                    if (i2.getName().equalsIgnoreCase(Constants.INDEPENDENT)) return 1;
                    return i1.getName().compareToIgnoreCase(i2.getName());
                });
                mainList.add(new MultiHQHeaderModelClass(hqName, hqCode, itemsList, true));
            }
        }
        return mainList;
    }

    public void prepareMasterToSync(MyViewHolder holder, String hqCode) {
        holder.progress_hq.setVisibility(View.VISIBLE);
        synccount = 0;
        masterSyncArray.clear();
//        MasterSyncItemModel doctorModel = new MasterSyncItemModel(Constants.DOCTOR, "getdoctors", Constants.DOCTOR + hqCode);
//        MasterSyncItemModel cheModel = new MasterSyncItemModel(Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode);
//        MasterSyncItemModel stockModel = new MasterSyncItemModel(Constants.DOCTOR, "getstockist", Constants.STOCKIEST + hqCode);
//        MasterSyncItemModel unListModel = new MasterSyncItemModel(Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode);
//        MasterSyncItemModel hospModel = new MasterSyncItemModel(Constants.DOCTOR, "gethospital", Constants.HOSPITAL + hqCode);
//        MasterSyncItemModel ciModel = new MasterSyncItemModel(Constants.DOCTOR, "getcip", Constants.CIP + hqCode);
//        MasterSyncItemModel cluster = new MasterSyncItemModel(Constants.DOCTOR, "getterritory", Constants.CLUSTER + hqCode);
        MasterSyncItemModel doctorModel = new MasterSyncItemModel(Constants.DOCTOR_MAS, "getdoctors_master", Constants.DOCTOR_MAS + hqCode);
        MasterSyncItemModel cheModel = new MasterSyncItemModel(Constants.DOCTOR_MAS, "getchemist_master", Constants.CHEMIST_MAS + hqCode);
        MasterSyncItemModel stockModel = new MasterSyncItemModel(Constants.DOCTOR_MAS, "getstockist_master", Constants.STOCKIEST_MAS + hqCode);
        MasterSyncItemModel unListModel = new MasterSyncItemModel(Constants.DOCTOR_MAS, "getunlisteddr_master", Constants.UNLISTED_DOCTOR_MAS + hqCode);
        MasterSyncItemModel cluster = new MasterSyncItemModel(Constants.DOCTOR_MAS, "getterritory", Constants.CLUSTER + hqCode);
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel(Constants.SUBORDINATE, "getjointwork", Constants.JOINT_WORK + hqCode);

        masterSyncArray.add(doctorModel);
        masterSyncArray.add(cheModel);
        masterSyncArray.add(stockModel);
        masterSyncArray.add(unListModel);
//        masterSyncArray.add(hospModel);
//        masterSyncArray.add(ciModel);
        masterSyncArray.add(cluster);
        masterSyncArray.add(jWorkModel);
        for (int i = 0; i < masterSyncArray.size(); i++) {
            sync(masterSyncArray.get(i), hqCode, holder);
        }
    }

    public void sync(MasterSyncItemModel masterSyncItemModel, String hqCode, MyViewHolder holder) {

        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                String baseUrl = SharedPref.getBaseWebUrl(context);
                String pathUrl = SharedPref.getPhpPathUrl(context);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                apiInterface = RetrofitClient.getRetrofit(context, baseUrl + replacedUrl);

                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
                jsonObject.put("tableName", masterSyncItemModel.getRemoteTableName());
                jsonObject.put("sfcode", SharedPref.getSfCode(context));
                jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                jsonObject.put("Rsf", hqCode);


//                Log.e("test","master sync obj in TP : " + jsonObject);
                Call<JsonElement> call = null;
                Map<String, String> mapString = new HashMap<>();
//                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase(Constants.DOCTOR)) {
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase(Constants.DOCTOR_MAS)) {
                    mapString.put("axn", "table/dcrmasterdata");
                    call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                } else if (masterSyncItemModel.getMasterOf().equalsIgnoreCase(Constants.SUBORDINATE)) {
                    mapString.put("axn", "table/subordinates");
                    call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                }

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                            boolean success = false;
                            if (response.isSuccessful()) {
//                                Log.e("test","response : " + masterSyncItemModel.getRemoteTableName() +" : " + response.body().toString());
                                try {
                                    JsonElement jsonElement = response.body();
                                    JSONArray jsonArray = new JSONArray();
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                            if (!jsonObject1.has("success")) { // json object with "success" : "fail" will be received only when api call is failed ,"success will not be received when api call is success
                                                jsonArray.put(jsonObject1);
                                                success = true;
                                            } else if (jsonObject1.has("success") && !jsonObject1.getBoolean("success")) {
                                                masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                            }
                                        }

                                        if (success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 2));

                                            if (masterSyncItemModel.getLocalTableKeyName().equalsIgnoreCase(Constants.JOINT_WORK + hqCode)) {
                                                JSONObject jointWorkJsonObject = new JSONObject();
                                                jointWorkJsonObject.put("Code", SharedPref.getSfCode(context));
                                                jointWorkJsonObject.put("Name", Constants.INDEPENDENT);
                                                jointWorkJsonObject.put("SfName", Constants.INDEPENDENT);
                                                jointWorkJsonObject.put("Reporting_To_SF", "");
                                                jointWorkJsonObject.put("OwnDiv", "");
                                                jointWorkJsonObject.put("Division_Code", SharedPref.getDivisionCode(context));
                                                jointWorkJsonObject.put("SF_Status", "");
                                                jointWorkJsonObject.put("ActFlg", "");
                                                jointWorkJsonObject.put("UsrDfd_UserName", "");
                                                jointWorkJsonObject.put("DS_name", "");
                                                jointWorkJsonObject.put("sf_type", SharedPref.getSfType(context));
                                                jointWorkJsonObject.put("Desig", SharedPref.getDesig(context));
                                                jointWorkJsonObject.put("steps", "");

                                                JSONArray jointWorkJsonArray = new JSONArray();
                                                jointWorkJsonArray.put(jointWorkJsonObject);
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    jointWorkJsonObject = jsonArray.optJSONObject(i);
                                                    jointWorkJsonArray.put(jointWorkJsonObject);
                                                }
                                                masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModel.getLocalTableKeyName(), jointWorkJsonArray.toString(), 2));
                                            }
                                        }
                                    } else {
                                        masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            synccount++;
                            if (synccount == masterSyncArray.size()) {
                                holder.progress_hq.setVisibility(View.GONE);
                            }

                            if (SharedPref.getSfType(context).equalsIgnoreCase("2") && !SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                                holder.mgrClusterArray = prepareModelList(holder.selectedHq, Constants.CLUSTER);
                                holder.mgrJointCallArray = prepareModelList(holder.selectedHq, Constants.JOINT_WORK);
//            holder.mgrListedDrArray = prepareModelList(holder.selectedHq, Constants.DOCTOR);
//            holder.mgrChemistArray = prepareModelList(holder.selectedHq, Constants.CHEMIST);
//            holder.mgrStockiestArray = prepareModelList(holder.selectedHq, Constants.STOCKIEST);
                                holder.mgrListedDrArray = prepareModelList(holder.selectedHq, Constants.DOCTOR_MAS);
                                holder.mgrChemistArray = prepareModelList(holder.selectedHq, Constants.CHEMIST_MAS);
                                holder.mgrStockiestArray = prepareModelList(holder.selectedHq, Constants.STOCKIEST_MAS);

                                prepareMGRInputData(holder.clustersModelArray, holder.mgrClusterArray);
                                prepareMGRJCData(holder.jcsModelArray, holder.mgrJointCallArray);
                                prepareMGRInputData(holder.listedDrsModelArray, holder.mgrListedDrArray);
                                prepareMGRInputData(holder.chemistsModelArray, holder.mgrChemistArray);
                                prepareMGRInputData(holder.stockistsModelArray, holder.mgrStockiestArray);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            masterDataDao.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                            synccount++;
                            if (synccount == masterSyncArray.size()) {
                                holder.progress_hq.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                holder.progress_hq.setVisibility(View.GONE);
                e.printStackTrace();
            }
        } else {
            holder.progress_hq.setVisibility(View.GONE);
            commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
        }
    }

    public void prepareInputData(ArrayList<ModelClass.SessionList.SubClass> modelClass, ArrayList<EditModelClass> arrayList) {
        if (modelClass.size() > 0) {
            for (int i = 0; i < modelClass.size(); i++) {
                for (int j = 0; j < arrayList.size(); j++) {
                    if (modelClass.get(i).getCode().equalsIgnoreCase(arrayList.get(j).getCode())) {
                        arrayList.get(j).setChecked(true);
                    }
                }
            }
        }
    }

    public void prepareInputDataOneBuild(ArrayList<OneBuildModelClass.SessionList.SubClass> oneBuildModelClass, ArrayList<EditModelClass> arrayList) {
        if (oneBuildModelClass.size() > 0) {
            for (int i = 0; i < oneBuildModelClass.size(); i++) {
                for (int j = 0; j < arrayList.size(); j++) {
                    if (oneBuildModelClass.get(i).getCode().equalsIgnoreCase(arrayList.get(j).getCode())) {
                        arrayList.get(j).setChecked(true);
                    }
                }
            }
        }
    }

    private void prepareMGRInputData(ArrayList<MultiHQHeaderModelClass> modelArray, ArrayList<MultiHQHeaderModelClass> mgrArray) {
        if (!modelArray.isEmpty()) {
            Map<String, MultiHQItemModelClass> mgrArrayMap = mgrArray.stream()
                    .flatMap(parent -> parent.getItemsList().stream())
                    .collect(Collectors.toMap(
                            MultiHQItemModelClass::getCode,
                            child -> child,
                            (existing, replacement) -> existing // keep the first one
                    ));

            modelArray.stream()
                    .flatMap(parent -> parent.getItemsList().stream())
                    .forEach(child -> {
                        MultiHQItemModelClass match = mgrArrayMap.get(child.getCode());
                        if (match != null) {
                            match.setChecked(true);
                        }
                    });
            Log.d("TAG", "prepareMGRInputData: " + modelArray);
        }
    }

    private void prepareMGRJCData(ArrayList<MultiHQHeaderModelClass> modelArray, ArrayList<MultiHQHeaderModelClass> mgrArray) {
        if (!modelArray.isEmpty()) {
            Map<String, List<String>> selectedData = new HashMap<>();
            for (MultiHQHeaderModelClass multiHQHeaderModelClass : modelArray) {
                ArrayList<MultiHQItemModelClass> multiHQItemModelClassList = multiHQHeaderModelClass.getItemsList();
                List<String> selectedJC = new ArrayList<>();
                for (MultiHQItemModelClass multiHQItemModelClass : multiHQItemModelClassList) {
                    selectedJC.add(multiHQItemModelClass.getCode());
                }
                selectedData.put(multiHQHeaderModelClass.getCode(), selectedJC);
            }
            for (MultiHQHeaderModelClass multiHQHeaderModelClass : mgrArray) {
                if (selectedData.containsKey(multiHQHeaderModelClass.getCode())) {
                    for (MultiHQItemModelClass multiHQItemModelClass : multiHQHeaderModelClass.getItemsList()) {
                        if (selectedData.get(multiHQHeaderModelClass.getCode()).contains(multiHQItemModelClass.getCode())) {
                            multiHQItemModelClass.setChecked(true);
                        }
                    }
                }
            }
            Log.d("TAG", "prepareMGRJCData: " + modelArray);
        }
    }

    public void populateSessionItemAdapter(MyViewHolder holder, boolean checkBoxNeed, boolean isSortNeeded, boolean isHQ, boolean isDr) {
        if (isSortNeeded) {
            Collections.sort(holder.sessionItemAdapterArray, new Comparator<EditModelClass>() {
                @Override
                public int compare(EditModelClass editModelClass, EditModelClass t1) {
                    if (editModelClass.getName().equals(Constants.INDEPENDENT)) return -1;
                    if (t1.getName().equals(Constants.INDEPENDENT)) return 1;
                    return editModelClass.getName().compareTo(t1.getName());
                }
            });
        }
        sessionItemAdapter = new SessionItemAdapter(context, holder.sessionItemAdapterArray, checkBoxNeed, isHQ, isDr, visitFrequencyNeed, minimumGap, new SessionItemInterface() {
            @Override
            public void itemClicked(ArrayList<EditModelClass> jsonArray, EditModelClass jsonObject) {
                if (holder.workTypeLayout.getVisibility() == View.VISIBLE) {
                    boolean workTypeRepeated = false;
                    if (inputDataArray.getSessionList().size() > 1) {
                        for (int i = 0; i < inputDataArray.getSessionList().size(); i++) {
                            if (i != holder.getAbsoluteAdapterPosition()) {
                                if (inputDataArray.getSessionList().get(i).getWorkType().getCode().equalsIgnoreCase(jsonObject.getCode())) {
                                    switch (jsonObject.getFWFlg().toUpperCase()) {
                                        case "W":
                                        case "H": {
                                            workTypeRepeated = true;
                                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.wt_already_selected) + (i + 1));
                                            break;
                                        }
                                        case "N": {
                                            if (inputDataArray.getSessionList().get(i).getWorkType().getCode().equalsIgnoreCase(jsonObject.getCode())) {
                                                workTypeRepeated = true;
                                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.wt_already_selected) + (i + 1));
                                                break;
                                            }
                                        }
                                        case "F": {
//                                            if(!isMGR) {
                                            workTypeRepeated = true;
                                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.wt_already_selected) + (i + 1));
                                            break;
//                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!workTypeRepeated) {
                        holder.workTypeField.setText(jsonObject.getName());
                        holder.sessionData.getWorkType().setName(jsonObject.getName());
                        holder.sessionData.getWorkType().setCode(jsonObject.getCode());
                        holder.sessionData.getWorkType().setFWFlg(jsonObject.getFWFlg());
                        holder.sessionData.getWorkType().setTerrSlFlg(jsonObject.getTerrSlFlg());

                        sessionInterface.fieldWorkSelected(inputDataArray, holder.getAbsoluteAdapterPosition());
                    }
                } else if (holder.hqLayout.getVisibility() == View.VISIBLE) {
                    if (isMGR) {
                        int count = 0;
                        for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                            if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                                count++;
                            }
                        }
                        if (count > 0) {
                            holder.hqField.setText(R.string.selected);
                            holder.hqCount.setVisibility(View.VISIBLE);
                            holder.hqCount.setText(String.valueOf(count));
                        } else {
                            holder.hqField.setText(R.string.select);
                            holder.hqCount.setVisibility(View.GONE);
                        }
                    } else {
                        boolean hqRepeated = false;
                        if (inputDataArray.getSessionList().size() > 1) {
                            for (int i = 0; i < inputDataArray.getSessionList().size(); i++) {
                                ModelClass.SessionList modelClass = inputDataArray.getSessionList().get(i);
                                if (i != holder.getAbsoluteAdapterPosition()) {
                                    if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase(inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getWorkType().getFWFlg())) {
                                        if (modelClass.getHQ().getCode().equalsIgnoreCase(jsonObject.getCode())) {
                                            hqRepeated = true;
                                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.hq_already_selected) + (i + 1));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (!hqRepeated) {
                            holder.sessionData.getHQ().setName(jsonObject.getName());
                            holder.sessionData.getHQ().setCode(jsonObject.getCode());
                            holder.hq_code = jsonObject.getCode();
                            if (!holder.selectedHq.equalsIgnoreCase(holder.hq_code)) {
                                holder.selectedHq = holder.hq_code;
                                sessionInterface.hqChanged(inputDataArray, itemPosition, true);
                            } else {
                                sessionInterface.hqChanged(inputDataArray, itemPosition, false);
                            }
                        }
                    }
                } else if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }

                    if (count > 0) {
                        holder.clusterField.setText(R.string.selected);
                        holder.clusterCount.setVisibility(View.VISIBLE);
                        holder.clusterCount.setText(String.valueOf(count));
                    } else {
                        holder.clusterField.setText(R.string.select);
                        holder.clusterCount.setVisibility(View.GONE);
                    }
                } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.jcField.setText(R.string.selected);
                        holder.jcCount.setVisibility(View.VISIBLE);
                        holder.jcCount.setText(String.valueOf(count));
                    } else {
                        holder.jcField.setText(R.string.select);
                        holder.jcCount.setVisibility(View.GONE);
                    }
                } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.drField.setText(R.string.selected);
                        holder.drCount.setVisibility(View.VISIBLE);
                        holder.drCount.setText(String.valueOf(count));
                    } else {
                        holder.drField.setText(R.string.select);
                        holder.drCount.setVisibility(View.GONE);
                    }
                } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.chemistField.setText(R.string.selected);
                        holder.chemistCount.setVisibility(View.VISIBLE);
                        holder.chemistCount.setText(String.valueOf(count));
                    } else {
                        holder.chemistField.setText(R.string.select);
                        holder.chemistCount.setVisibility(View.GONE);
                    }
                } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.stockiestField.setText(R.string.selected);
                        holder.stockiestCount.setVisibility(View.VISIBLE);
                        holder.stockiestCount.setText(String.valueOf(count));
                    } else {
                        holder.stockiestField.setText(R.string.select);
                        holder.stockiestCount.setVisibility(View.GONE);
                    }
                } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.unListedDrField.setText(R.string.selected);
                        holder.unListedDrCount.setVisibility(View.VISIBLE);
                        holder.unListedDrCount.setText(String.valueOf(count));
                    } else {
                        holder.unListedDrField.setText(R.string.select);
                        holder.unListedDrCount.setVisibility(View.GONE);
                    }
                } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.cipField.setText(R.string.selected);
                        holder.cipCount.setVisibility(View.VISIBLE);
                        holder.cipCount.setText(String.valueOf(count));
                    } else {
                        holder.cipField.setText(R.string.select);
                        holder.cipCount.setVisibility(View.GONE);
                    }
                } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.hospField.setText(R.string.selected);
                        holder.hospCount.setVisibility(View.VISIBLE);
                        holder.hospCount.setText(String.valueOf(count));
                    } else {
                        holder.hospField.setText(R.string.select);
                        holder.hospCount.setVisibility(View.GONE);
                    }
                } else if (holder.workDayLayout.getVisibility() == View.VISIBLE) {
                    holder.workDayField.setText(jsonObject.getName());
                    inputDataArray.setSTP_Name(jsonObject.getName());
                    inputDataArray.setSTP_Code(jsonObject.getCode());
                    sessionInterface.workDayChanged(inputDataArray, itemPosition);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.itemRecView.setLayoutManager(layoutManager);
        holder.itemRecView.setAdapter(sessionItemAdapter);
        sessionItemAdapter.notifyDataSetChanged();

    }

    public void populateSessionItemAdapterOneBuild(MyViewHolder holder, boolean checkBoxNeed, boolean idSortNeeded, boolean isDr) {
        if (idSortNeeded) {
            Collections.sort(holder.sessionItemAdapterArray, new Comparator<EditModelClass>() {
                @Override
                public int compare(EditModelClass editModelClass, EditModelClass t1) {
                    if (editModelClass.getName().equals(Constants.INDEPENDENT)) return -1;
                    if (t1.getName().equals(Constants.INDEPENDENT)) return 1;
                    return editModelClass.getName().compareTo(t1.getName());
                }
            });
        }
        sessionItemAdapter = new SessionItemAdapter(context, holder.sessionItemAdapterArray, checkBoxNeed, false, isDr, visitFrequencyNeed, minimumGap, new SessionItemInterface() {
            @Override
            public void itemClicked(ArrayList<EditModelClass> jsonArray, EditModelClass jsonObject) {
                if (holder.workTypeLayout.getVisibility() == View.VISIBLE) {
                    boolean workTypeRepeated = false;
                    if (inputDataArrayOneBuild.getSessionList().size() > 1) {
                        for (int i = 0; i < inputDataArrayOneBuild.getSessionList().size(); i++) {
                            if (i != holder.getAbsoluteAdapterPosition()) {
                                if (inputDataArrayOneBuild.getSessionList().get(i).getWorkType().getCode().equalsIgnoreCase(jsonObject.getCode())) {
                                    switch (jsonObject.getFWFlg().toUpperCase()) {
                                        case "W":
                                        case "H": {
                                            workTypeRepeated = true;
                                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.wt_already_selected) + (i + 1));
                                            break;
                                        }
                                        case "N": {
                                            if (inputDataArrayOneBuild.getSessionList().get(i).getWorkType().getCode().equalsIgnoreCase(jsonObject.getCode())) {
                                                workTypeRepeated = true;
                                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.wt_already_selected) + (i + 1));
                                                break;
                                            }
                                        }
                                        case "F": {
                                            if (!sfType.equalsIgnoreCase("2")) {
                                                workTypeRepeated = true;
                                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.wt_already_selected) + (i + 1));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!workTypeRepeated) {
                        holder.workTypeField.setText(jsonObject.getName());
                        holder.sessionDataOneBuild.getWorkType().setName(jsonObject.getName());
                        holder.sessionDataOneBuild.getWorkType().setCode(jsonObject.getCode());
                        holder.sessionDataOneBuild.getWorkType().setFWFlg(jsonObject.getFWFlg());
                        holder.sessionDataOneBuild.getWorkType().setTerrSlFlg(jsonObject.getTerrSlFlg());

                        sessionInterfaceOneBuild.fieldWorkSelectedOneBuild(inputDataArrayOneBuild, holder.getAbsoluteAdapterPosition());
                    }

                } else if (holder.hqLayout.getVisibility() == View.VISIBLE) {
                    boolean hqRepeated = false;
                    if (inputDataArrayOneBuild.getSessionList().size() > 1) {
                        for (int i = 0; i < inputDataArrayOneBuild.getSessionList().size(); i++) {
                            OneBuildModelClass.SessionList oneBuildModelClass = inputDataArrayOneBuild.getSessionList().get(i);
                            if (i != holder.getAbsoluteAdapterPosition()) {
                                if (oneBuildModelClass.getWorkType().getFWFlg().equalsIgnoreCase(inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getWorkType().getFWFlg())) {
                                    if (oneBuildModelClass.getHeadquarters().getCode().equalsIgnoreCase(jsonObject.getCode())) {
                                        hqRepeated = true;
                                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.hq_already_selected) + (i + 1));
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (!hqRepeated) {
                        holder.sessionDataOneBuild.getHeadquarters().setName(jsonObject.getName());
                        holder.sessionDataOneBuild.getHeadquarters().setCode(jsonObject.getCode());

                        holder.hq_code = jsonObject.getCode();
                        if (!holder.selectedHq.equalsIgnoreCase(holder.hq_code)) {
                            holder.selectedHq = holder.hq_code;
                            sessionInterfaceOneBuild.hqChangedOneBuild(inputDataArrayOneBuild, itemPosition, true);
                        } else {
                            sessionInterfaceOneBuild.hqChangedOneBuild(inputDataArrayOneBuild, itemPosition, false);
                        }
                    }
                } else if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }

                    if (count > 0) {
                        holder.clusterField.setText(R.string.selected);
                        holder.clusterCount.setVisibility(View.VISIBLE);
                        holder.clusterCount.setText(String.valueOf(count));
                    } else {
                        holder.clusterField.setText(R.string.select);
                        holder.clusterCount.setVisibility(View.GONE);
                    }
                } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }

                    if (count > 0) {
                        holder.jcField.setText(R.string.selected);
                        holder.jcCount.setVisibility(View.VISIBLE);
                        holder.jcCount.setText(String.valueOf(count));
                    } else {
                        holder.jcField.setText(R.string.select);
                        holder.jcCount.setVisibility(View.GONE);
                    }
                } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.drField.setText(R.string.selected);
                        holder.drCount.setVisibility(View.VISIBLE);
                        holder.drCount.setText(String.valueOf(count));
                    } else {
                        holder.drField.setText(R.string.select);
                        holder.drCount.setVisibility(View.GONE);
                    }
                } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.chemistField.setText(R.string.selected);
                        holder.chemistCount.setVisibility(View.VISIBLE);
                        holder.chemistCount.setText(String.valueOf(count));
                    } else {
                        holder.chemistField.setText(R.string.select);
                        holder.chemistCount.setVisibility(View.GONE);
                    }
                } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.stockiestField.setText(R.string.selected);
                        holder.stockiestCount.setVisibility(View.VISIBLE);
                        holder.stockiestCount.setText(String.valueOf(count));
                    } else {
                        holder.stockiestField.setText(R.string.select);
                        holder.stockiestCount.setVisibility(View.GONE);
                    }
                } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.unListedDrField.setText(R.string.selected);
                        holder.unListedDrCount.setVisibility(View.VISIBLE);
                        holder.unListedDrCount.setText(String.valueOf(count));
                    } else {
                        holder.unListedDrField.setText(R.string.select);
                        holder.unListedDrCount.setVisibility(View.GONE);
                    }
                } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.cipField.setText(R.string.selected);
                        holder.cipCount.setVisibility(View.VISIBLE);
                        holder.cipCount.setText(String.valueOf(count));
                    } else {
                        holder.cipField.setText(R.string.select);
                        holder.cipCount.setVisibility(View.GONE);
                    }
                } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.hospField.setText(R.string.selected);
                        holder.hospCount.setVisibility(View.VISIBLE);
                        holder.hospCount.setText(String.valueOf(count));
                    } else {
                        holder.hospField.setText(R.string.select);
                        holder.hospCount.setVisibility(View.GONE);
                    }
                } else if (holder.workDayLayout.getVisibility() == View.VISIBLE) {
                    holder.workDayField.setText(jsonObject.getName());
                    inputDataArrayOneBuild.setSTP_Name(jsonObject.getName());
                    inputDataArrayOneBuild.setSTP_Code(jsonObject.getCode());
                    sessionInterfaceOneBuild.workDayChangedOneBuild(inputDataArrayOneBuild, itemPosition);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.itemRecView.setLayoutManager(layoutManager);
        holder.itemRecView.setAdapter(sessionItemAdapter);
        sessionItemAdapter.notifyDataSetChanged();

    }

    public void populateSessionMultiHQItemAdapter(MyViewHolder holder, String caption) {
        //            @Override
//            public void onItemUnSelected(String hqCode, MultiHQItemModelClass multiHQItemModelClass) {
//
//            }
        sessionMultiHQItemAdapter = new SessionMultiHQItemAdapter(context, holder.mgrSessionItemAdapterArray, caption, (hqCode, multiHQItemModelClass) -> {
            int count = 0;
            for (int i = 0; i < holder.mgrSessionItemAdapterArray.size(); i++) {
                MultiHQHeaderModelClass dataHeader = holder.mgrSessionItemAdapterArray.get(i);
                if (dataHeader != null) {
                    ArrayList<MultiHQItemModelClass> dataList = dataHeader.getItemsList();
                    for (int j = 0; j < dataList.size(); j++) {
                        MultiHQItemModelClass data = dataHeader.getItemsList().get(j);
                        if (data != null && data.isChecked()) {
                            count++;
                        }
                    }
                }
            }
            if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                if (count > 0) {
                    holder.clusterField.setText(R.string.selected);
                    holder.clusterCount.setVisibility(View.VISIBLE);
                    holder.clusterCount.setText(String.valueOf(count));
                } else {
                    holder.clusterField.setText(R.string.select);
                    holder.clusterCount.setVisibility(View.GONE);
                }
            } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                if (count > 0) {
                    holder.jcField.setText(R.string.selected);
                    holder.jcCount.setVisibility(View.VISIBLE);
                    holder.jcCount.setText(String.valueOf(count));
                } else {
                    holder.jcField.setText(R.string.select);
                    holder.jcCount.setVisibility(View.GONE);
                }
            } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                if (count > 0) {
                    holder.drField.setText(R.string.selected);
                    holder.drCount.setVisibility(View.VISIBLE);
                    holder.drCount.setText(String.valueOf(count));
                } else {
                    holder.drField.setText(R.string.select);
                    holder.drCount.setVisibility(View.GONE);
                }
            } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                if (count > 0) {
                    holder.chemistField.setText(R.string.selected);
                    holder.chemistCount.setVisibility(View.VISIBLE);
                    holder.chemistCount.setText(String.valueOf(count));
                } else {
                    holder.chemistField.setText(R.string.select);
                    holder.chemistCount.setVisibility(View.GONE);
                }

            } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                if (count > 0) {
                    holder.stockiestField.setText(R.string.selected);
                    holder.stockiestCount.setVisibility(View.VISIBLE);
                    holder.stockiestCount.setText(String.valueOf(count));
                } else {
                    holder.stockiestField.setText(R.string.select);
                    holder.stockiestCount.setVisibility(View.GONE);
                }
//                }else if(holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
//                    if(count>0) {
//                        holder.unListedDrField.setText(R.string.selected);
//                        holder.unListedDrCount.setVisibility(View.VISIBLE);
//                        holder.unListedDrCount.setText(String.valueOf(count));
//                    }else {
//                        holder.unListedDrField.setText(R.string.select);
//                        holder.unListedDrCount.setVisibility(View.GONE);
//                    }
//                }else if(holder.cipLayout.getVisibility() == View.VISIBLE) {
//                    if(count>0) {
//                        holder.cipField.setText(R.string.selected);
//                        holder.cipCount.setVisibility(View.VISIBLE);
//                        holder.cipCount.setText(String.valueOf(count));
//                    }else {
//                        holder.cipField.setText(R.string.select);
//                        holder.cipCount.setVisibility(View.GONE);
//                    }
//                }else if(holder.hospLayout.getVisibility() == View.VISIBLE) {
//                    if(count>0) {
//                        holder.hospField.setText(R.string.selected);
//                        holder.hospCount.setVisibility(View.VISIBLE);
//                        holder.hospCount.setText(String.valueOf(count));
//                    }else {
//                        holder.hospField.setText(R.string.select);
//                        holder.hospCount.setVisibility(View.GONE);
//                    }
//                }else if(holder.workDayLayout.getVisibility() == View.VISIBLE) {
//                    holder.workDayField.setText(jsonObject.getName());
//                    inputDataArray.setSTP_Name(jsonObject.getName());
//                    inputDataArray.setSTP_Code(jsonObject.getCode());
//                    sessionInterface.workDayChanged(inputDataArray, itemPosition);
//                }

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.itemRecView.setLayoutManager(layoutManager);
        holder.itemRecView.setAdapter(sessionMultiHQItemAdapter);
        sessionMultiHQItemAdapter.notifyDataSetChanged();
    }

    public void changeUIState(MyViewHolder holder, RelativeLayout linearLayout, ImageView imageView, boolean allLayoutVisible) {

        if (allLayoutVisible) {
            holder.workTypeLayout.setVisibility(View.VISIBLE);
            holder.remarksLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.down_arrow));
            holder.listCardView.setVisibility(View.GONE);
            TourPlanActivity.addSaveBtnLayout.setVisibility(View.VISIBLE);
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
            if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                if (inputDataArrayOneBuild.getSessionList().size() > 1 && holder.getAbsoluteAdapterPosition() != 0) {
                    holder.sessionDelete.setVisibility(View.VISIBLE);
                }
            } else {
                if (inputDataArray.getSessionList().size() > 1 && holder.getAbsoluteAdapterPosition() != 0) {
                    holder.sessionDelete.setVisibility(View.VISIBLE);
                }
            }

            if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                worktypeBasedUiOneBuild(holder, holder.sessionDataOneBuild, false);
            } else {
                workTypeBasedUI(holder, holder.sessionData, false);
            }
        } else {
            holder.workTypeLayout.setVisibility(View.GONE);
            holder.hqLayout.setVisibility(View.GONE);
            holder.clusterLayout.setVisibility(View.GONE);
            holder.jcLayout.setVisibility(View.GONE);
            holder.drLayout.setVisibility(View.GONE);
            holder.chemistLayout.setVisibility(View.GONE);
            holder.stockiestLayout.setVisibility(View.GONE);
            holder.unListedDrLayout.setVisibility(View.GONE);
            holder.cipLayout.setVisibility(View.GONE);
            holder.hospLayout.setVisibility(View.GONE);
            holder.remarksLayout.setVisibility(View.GONE);
            holder.workDayLayout.setVisibility(View.GONE);
            holder.listCardView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.up_arrow));
            TourPlanActivity.addSaveBtnLayout.setVisibility(View.GONE);
            holder.sessionDelete.setVisibility(View.GONE);
//            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
        }
        UtilityClass.hideKeyboard((Activity) context);

    }

    public ArrayList<EditModelClass> filterJsonArray(MyViewHolder holder, ArrayList<EditModelClass> arrayList1) { // Filters based on selected cluster
        ArrayList<EditModelClass> arrayList = new ArrayList<>();
        for (int i = 0; i < holder.selectedClusterCode.size(); i++) {
            for (int j = 0; j < arrayList1.size(); j++) {
                if (holder.selectedClusterCode.get(i).equalsIgnoreCase(arrayList1.get(j).getTown_Code())) { // filtering based on selected Cluster code
                    arrayList.add(arrayList1.get(j));
                }
            }
        }

        return arrayList;
    }

    public ArrayList<MultiHQHeaderModelClass> filterMasterList(MyViewHolder holder, ArrayList<MultiHQHeaderModelClass> dataList, boolean isClusterCheckNeeded) { // Filters based on selected cluster
        ArrayList<MultiHQHeaderModelClass> resultHeaderList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            MultiHQHeaderModelClass multiHQHeaderModelClass = new MultiHQHeaderModelClass(dataList.get(i));
            ArrayList<MultiHQItemModelClass> multiHQItemModelClassArrayList = multiHQHeaderModelClass.getItemsList();
            ArrayList<MultiHQItemModelClass> resultItemList = new ArrayList<>();
            ArrayList<String> selectedClustersList = holder.selectedClusterCodeMap.get(multiHQHeaderModelClass.getCode());
            if (selectedClustersList != null && !selectedClustersList.isEmpty() && multiHQItemModelClassArrayList != null && !multiHQItemModelClassArrayList.isEmpty()) {
                for (int j = 0; j < multiHQItemModelClassArrayList.size(); j++) {
                    if (isClusterCheckNeeded) {
                        if (selectedClustersList.contains(multiHQItemModelClassArrayList.get(j).getClusterCode())) { // filtering based on selected Cluster code
                            resultItemList.add(multiHQItemModelClassArrayList.get(j));
                        }
                    } else {
                        resultItemList.add(multiHQItemModelClassArrayList.get(j));
                    }
                }
            }
            multiHQHeaderModelClass.setItemsList(resultItemList);
            resultHeaderList.add(multiHQHeaderModelClass);
        }
        return resultHeaderList;
    }

    public void onEdit(int position, boolean visibility, String layoutVisible) {
        // to hide other sessions and also other fields of same session while select corresponding field at a time of edit

        for (int i = 0; i < inputDataArray.getSessionList().size(); i++) {
            if (i != position)
                inputDataArray.getSessionList().get(i).setVisible(visibility); // set all other sessions visibility either true or false
            else
                inputDataArray.getSessionList().get(i).setLayoutVisible(layoutVisible); // to set which one need to be visible at a time while edit of a same session.ex: when user click on session1 cluster then session1 cluster will only be visible
        }
        notifyDataSetChanged();
        TourPlanActivity activity = (TourPlanActivity) context;
        activity.scrollToPosition(position, false);
    }

    public void onEditOneBuild(int position, boolean visibility, String layoutVisible) {
        for (int i = 0; i < inputDataArrayOneBuild.getSessionList().size(); i++) {
            if (i != position)
                inputDataArrayOneBuild.getSessionList().get(i).setVisible(visibility); // set all other sessions visibility either true or false
            else
                inputDataArrayOneBuild.getSessionList().get(i).setLayoutVisible(layoutVisible); // to set which one need to be visible at a time while edit of a same session.ex: when user click on session1 cluster then session1 cluster will only be visible
        }
        notifyDataSetChanged();
        TourPlanActivity activity = (TourPlanActivity) context;
        activity.scrollToPosition(position, false);
    }

    public void clearCheckBox(MyViewHolder holder) {
        if (holder.hqLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.hqField, holder.hqCount);
        } else if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.clusterField, holder.clusterCount);
        } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.jcField, holder.jcCount);
        } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.drField, holder.drCount);
        } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.chemistField, holder.chemistCount);
        } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.stockiestField, holder.stockiestCount);
        } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.unListedDrField, holder.unListedDrCount);
        } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.cipField, holder.cipCount);
        } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.hospField, holder.hospCount);
        }
    }

    public void clearSelectedItem(MyViewHolder holder, TextView labelTxt, TextView countTxt) {
        // un check the all check boxes
        for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
            holder.sessionItemAdapterArray.get(i).setChecked(false);
        }
        for (int i = 0; i < holder.mgrSessionItemAdapterArray.size(); i++) {
            for (int j = 0; j < holder.mgrSessionItemAdapterArray.get(i).getItemsList().size(); j++) {
                holder.mgrSessionItemAdapterArray.get(i).getItemsList().get(j).setChecked(false);
            }
        }
        labelTxt.setText(R.string.select);
        countTxt.setVisibility(View.GONE);
        sessionItemAdapter.notifyDataSetChanged();
        sessionMultiHQItemAdapter.notifyDataSetChanged();
    }

    public void saveCheckedItem(MyViewHolder holder) {
        try {
            if (holder.hqLayout.getVisibility() == View.VISIBLE) {
                holder.selectedHQCode.clear();
                for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                    if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                        holder.selectedHQCode.add(holder.sessionItemAdapterArray.get(i).getCode());
                    }
                }
                holder.selectedClusterCodeMap.clear();
                for (int i = 0; i < holder.mgrClusterArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.mgrClusterArray.get(i);
                    if (holder.selectedHQCode.contains(multiHQHeaderModelClass.getCode())) {
                        ArrayList<MultiHQItemModelClass> dataList = multiHQHeaderModelClass.getItemsList();
                        for (int j = 0; j < dataList.size(); j++) {
                            if (dataList.get(j).isChecked()) {
                                ArrayList<String> clusterCodes = new ArrayList<>();
                                if (holder.selectedClusterCodeMap.containsKey(multiHQHeaderModelClass.getCode())) {
                                    clusterCodes = holder.selectedClusterCodeMap.get(multiHQHeaderModelClass.getCode());
                                }
                                clusterCodes.add(dataList.get(j).getCode());
                                holder.selectedClusterCodeMap.put(multiHQHeaderModelClass.getCode(), clusterCodes);
                            } else {
                                dataList.remove(dataList.get(j));
                                j--;
                            }
                        }
                    } else {
                        holder.mgrClusterArray.remove(multiHQHeaderModelClass);
                        i--;
                    }
                }
                clusterChanged(holder.selectedClusterCode, holder.listedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
                clusterChanged(holder.selectedClusterCode, holder.chemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
                clusterChanged(holder.selectedClusterCode, holder.stockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
                clusterChanged(holder.selectedClusterCode, holder.unListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
                clusterChanged(holder.selectedClusterCode, holder.cipArray, holder.cipField, Constants.CIP, holder);
                clusterChanged(holder.selectedClusterCode, holder.hospArray, holder.hospField, Constants.HOSPITAL, holder);

                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrListedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrChemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrStockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrUnListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrCipArray, holder.cipField, Constants.CIP, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrHospArray, holder.hospField, Constants.HOSPITAL, holder);

                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getClusters().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setClusters(holder.mgrClusterArray);

                for (int i = 0; i < holder.mgrJointCallArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.mgrJointCallArray.get(i);
                    if (!holder.selectedHQCode.contains(multiHQHeaderModelClass.getCode())) {
                        holder.mgrJointCallArray.remove(multiHQHeaderModelClass);
                        i--;
                    } else {
                        ArrayList<MultiHQItemModelClass> selectedJCList = multiHQHeaderModelClass.getItemsList();
                        for (int j = 0; j < selectedJCList.size(); j++) {
                            MultiHQItemModelClass multiHQItemModelClass = selectedJCList.get(j);
                            if (!multiHQItemModelClass.isChecked()) {
                                selectedJCList.remove(multiHQItemModelClass);
                                j--;
                            }
                        }
                        multiHQHeaderModelClass.setItemsList(selectedJCList);
                    }
                }

                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getJCs().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setJCs(holder.mgrJointCallArray);

                for (int i = 0; i < holder.mgrStockiestArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.mgrStockiestArray.get(i);
                    if (!holder.selectedHQCode.contains(multiHQHeaderModelClass.getCode())) {
                        holder.mgrStockiestArray.remove(multiHQHeaderModelClass);
                        i--;
                    } else {
                        ArrayList<MultiHQItemModelClass> selectedStockiestList = multiHQHeaderModelClass.getItemsList();
                        for (int j = 0; j < selectedStockiestList.size(); j++) {
                            MultiHQItemModelClass multiHQItemModelClass = selectedStockiestList.get(j);
                            if (!multiHQItemModelClass.isChecked()) {
                                selectedStockiestList.remove(multiHQItemModelClass);
                                j--;
                            }
                        }
                        multiHQHeaderModelClass.setItemsList(selectedStockiestList);
                    }
                }

                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getStockiests().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setStockiests(holder.mgrStockiestArray);

                clusterChanged(holder.selectedClusterCode, holder.listedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
                clusterChanged(holder.selectedClusterCode, holder.chemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
                clusterChanged(holder.selectedClusterCode, holder.stockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
                clusterChanged(holder.selectedClusterCode, holder.unListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
                clusterChanged(holder.selectedClusterCode, holder.cipArray, holder.cipField, Constants.CIP, holder);
                clusterChanged(holder.selectedClusterCode, holder.hospArray, holder.hospField, Constants.HOSPITAL, holder);

                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrListedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrChemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
//                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrStockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrUnListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrCipArray, holder.cipField, Constants.CIP, holder);
                mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrHospArray, holder.hospField, Constants.HOSPITAL, holder);

                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.hqField, holder.hqCount);
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setLayoutVisible("");
                sessionInterface.hqChanged(inputDataArray, holder.getLayoutPosition(), true);
            } else if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                if (isMGR) {
                    HashMap<String, ArrayList<String>> selectedClusterCodeMap = holder.selectedClusterCodeMap;
                    holder.selectedClusterCodeMap.clear();
                    boolean isClusterNotSelectedForHQ = false;
                    String hqName = "";
                    for (int i = 0; i < holder.mgrSessionItemAdapterArray.size(); i++) {
                        MultiHQHeaderModelClass multiHQHeaderModelClass = holder.mgrSessionItemAdapterArray.get(i);
                        ArrayList<MultiHQItemModelClass> dataList = multiHQHeaderModelClass.getItemsList();
                        boolean isClusterNotSelected = false;
                        for (int j = 0; j < dataList.size(); j++) {
                            if (dataList.get(j).isChecked()) {
                                ArrayList<String> clusterCodes = new ArrayList<>();
                                if (holder.selectedClusterCodeMap.containsKey(multiHQHeaderModelClass.getCode())) {
                                    clusterCodes = holder.selectedClusterCodeMap.get(multiHQHeaderModelClass.getCode());
                                }
                                isClusterNotSelected = true;
                                clusterCodes.add(dataList.get(j).getCode());
                                holder.selectedClusterCodeMap.put(multiHQHeaderModelClass.getCode(), clusterCodes);
                            }
                        }
                        if (!isClusterNotSelected && !dataList.isEmpty()) {
                            isClusterNotSelectedForHQ = true;
                            hqName = multiHQHeaderModelClass.getName();
                            break;
                        }
                    }

                    if (isClusterNotSelectedForHQ) {
                        holder.selectedClusterCodeMap = selectedClusterCodeMap;
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.select_any) + SharedPref.getClusterCap(context) + " for " + hqName);
                        return;
                    } else {
                        mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrListedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
                        mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrChemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
                        mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrStockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
                        mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrUnListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
                        mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrCipArray, holder.cipField, Constants.CIP, holder);
                        mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrHospArray, holder.hospField, Constants.HOSPITAL, holder);

             /*       mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrListedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
                    mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrChemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
                    mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrStockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
                    mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrUnListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
                    mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrCipArray, holder.cipField, Constants.CIP, holder);
                    mgrClusterChanged(holder.selectedClusterCodeMap, holder.mgrHospArray, holder.hospField, Constants.HOSPITAL, holder);*/

                        setSelectedCountMGR(holder, holder.mgrSessionItemAdapterArray, true, holder.clusterField, holder.clusterCount);
                        inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setLayoutVisible("");
                        sessionInterface.clusterChanged(inputDataArray, holder.getLayoutPosition());
                    }
                } else {
                    holder.selectedClusterCode.clear();
                    for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            holder.selectedClusterCode.add(holder.sessionItemAdapterArray.get(i).getCode());
                        }
                    }

                    clusterChanged(holder.selectedClusterCode, holder.listedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
                    clusterChanged(holder.selectedClusterCode, holder.chemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
                    clusterChanged(holder.selectedClusterCode, holder.stockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
                    clusterChanged(holder.selectedClusterCode, holder.unListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
                    clusterChanged(holder.selectedClusterCode, holder.cipArray, holder.cipField, Constants.CIP, holder);
                    clusterChanged(holder.selectedClusterCode, holder.hospArray, holder.hospField, Constants.HOSPITAL, holder);

//                    clusterChanged(holder.selectedClusterCode, holder.listedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
//                    clusterChanged(holder.selectedClusterCode, holder.chemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
//                    clusterChanged(holder.selectedClusterCode, holder.stockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
//                    clusterChanged(holder.selectedClusterCode, holder.unListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
//                    clusterChanged(holder.selectedClusterCode, holder.cipArray, holder.cipField, Constants.CIP, holder);
//                    clusterChanged(holder.selectedClusterCode, holder.hospArray, holder.hospField, Constants.HOSPITAL, holder);

                    setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.clusterField, holder.clusterCount);
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setLayoutVisible("");
                    sessionInterface.clusterChanged(inputDataArray, holder.getLayoutPosition());
                }
            } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                if (isMGR) {
                    setSelectedCountMGR(holder, holder.mgrSessionItemAdapterArray, true, holder.jcField, holder.jcCount);
                    changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
                } else {
                    setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.jcField, holder.jcCount);
                    changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
                }
            } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                if (isMGR) {
                    setSelectedCountMGR(holder, holder.mgrSessionItemAdapterArray, true, holder.drField, holder.drCount);
                    changeUIState(holder, holder.drLayout, holder.drArrow, true);
                } else {
                    setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.drField, holder.drCount);
                    changeUIState(holder, holder.drLayout, holder.drArrow, true);
                }
            } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                if (isMGR) {
                    setSelectedCountMGR(holder, holder.mgrSessionItemAdapterArray, true, holder.chemistField, holder.chemistCount);
                    changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
                } else {
                    setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.chemistField, holder.chemistCount);
                    changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
                }
            } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                if (isMGR) {
                    setSelectedCountMGR(holder, holder.mgrSessionItemAdapterArray, true, holder.stockiestField, holder.stockiestCount);
                    changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
                } else {
                    setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.stockiestField, holder.stockiestCount);
                    changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
                }
            } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.unListedDrField, holder.unListedDrCount);
                changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, true);
            } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.cipField, holder.cipCount);
                changeUIState(holder, holder.cipLayout, holder.cipArrow, true);
            } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.hospField, holder.hospCount);
                changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
    }

    public void saveCheckedItemOneBuild(MyViewHolder holder) {
        if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
            holder.selectedClusterCode.clear();
            for (int i = 0; i < holder.sessionItemAdapterArray.size(); i++) {
                if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                    holder.selectedClusterCode.add(holder.sessionItemAdapterArray.get(i).getCode());
                }
            }

            clusterChanged(holder.selectedClusterCode, holder.listedDrArray, holder.drField, Constants.DOCTOR_MAS, holder);
            clusterChanged(holder.selectedClusterCode, holder.chemistArray, holder.chemistField, Constants.CHEMIST_MAS, holder);
            clusterChanged(holder.selectedClusterCode, holder.stockiestArray, holder.stockiestField, Constants.STOCKIEST_MAS, holder);
            clusterChanged(holder.selectedClusterCode, holder.unListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR_MAS, holder);
            clusterChanged(holder.selectedClusterCode, holder.cipArray, holder.cipField, Constants.CIP, holder);
            clusterChanged(holder.selectedClusterCode, holder.hospArray, holder.hospField, Constants.HOSPITAL, holder);

            setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.clusterField, holder.clusterCount);
            inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setLayoutVisible("");
            sessionInterfaceOneBuild.clusterChangedOneBuild(inputDataArrayOneBuild, holder.getLayoutPosition());
        } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
            setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.jcField, holder.jcCount);
            changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
        } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
            setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.drField, holder.drCount);
            changeUIState(holder, holder.drLayout, holder.drArrow, true);
        } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
            setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.chemistField, holder.chemistCount);
            changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
        } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
            setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.stockiestField, holder.stockiestCount);
            changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
        } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
            setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.unListedDrField, holder.unListedDrCount);
            changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, true);
        } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
            setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.cipField, holder.cipCount);
            changeUIState(holder, holder.cipLayout, holder.cipArrow, true);
        } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
            setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.hospField, holder.hospCount);
            changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
        }
        onEditOneBuild(holder.getAbsoluteAdapterPosition(), true, "");
    }

    public void clusterChanged(ArrayList<String> clusterCodes, ArrayList<EditModelClass> arrayList, TextView label, String master, MyViewHolder holder) {

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isChecked()) {
                boolean matched = false;
                for (int j = 0; j < clusterCodes.size(); j++) {
                    if (clusterCodes.get(j).equalsIgnoreCase(arrayList.get(i).getTown_Code())) {
                        matched = true;
                    }
                }
                if (!matched) {
                    arrayList.get(i).setChecked(false);
                }
            }
        }

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isChecked()) {
                if (text.length() == 0) {
                    text = new StringBuilder(arrayList.get(i).getName());
                } else {
                    text.append(",").append(arrayList.get(i).getName());
                }
            }
        }
        if (text.length() == 0) {
            label.setText(R.string.select);
        } else {
            label.setText(text);
        }
        if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            List<OneBuildModelClass.SessionList.SubClass> subClassListOneBuild = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).isChecked()) {
                    OneBuildModelClass.SessionList.SubClass subClassOneBuild = new OneBuildModelClass.SessionList.SubClass(arrayList.get(i).getName(), arrayList.get(i).getCode());
                    subClassListOneBuild.add(subClassOneBuild);
                }
            }
            if (master.equalsIgnoreCase(Constants.DOCTOR_MAS)) {
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getDoctors().clear();
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setDoctors(subClassListOneBuild);
            } else if (master.equalsIgnoreCase(Constants.CHEMIST_MAS)) {
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemists().clear();
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemists(subClassListOneBuild);
            } else if (master.equalsIgnoreCase(Constants.UNLISTED_DOCTOR_MAS)) {
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnlistedDoctors().clear();
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnlistedDoctors(subClassListOneBuild);
            } else if (master.equalsIgnoreCase(Constants.CIP)) {
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassListOneBuild);
            } else if (master.equalsIgnoreCase(Constants.HOSPITAL)) {
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospitals().clear();
                inputDataArrayOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospitals(subClassListOneBuild);
            }
        } else {

            List<ModelClass.SessionList.SubClass> subClassList = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).isChecked()) {
                    ModelClass.SessionList.SubClass subClass = new ModelClass.SessionList.SubClass(arrayList.get(i).getName(), arrayList.get(i).getCode());
                    subClassList.add(subClass);
                }
            }
            if (master.equalsIgnoreCase(Constants.DOCTOR_MAS)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDr().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDr(subClassList);
            } else if (master.equalsIgnoreCase(Constants.CHEMIST_MAS)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemist().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemist(subClassList);
            } else if (master.equalsIgnoreCase(Constants.UNLISTED_DOCTOR_MAS)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDr().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDr(subClassList);
            } else if (master.equalsIgnoreCase(Constants.CIP)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassList);
            } else if (master.equalsIgnoreCase(Constants.HOSPITAL)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospital().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospital(subClassList);
            }
        }
    }

    public void mgrClusterChanged(HashMap<String, ArrayList<String>> clusterCodesMap, ArrayList<MultiHQHeaderModelClass> multiHQHeaderModelClassArrayList, TextView label, String master, MyViewHolder holder) {
//        for (int i = 0; i<arrayList.size(); i++) {
//            if(arrayList.get(i).isChecked()) {
//                boolean matched = false;
//                for (int j = 0; j<clusterCodes.size(); j++) {
//                    if(clusterCodes.get(j).equalsIgnoreCase(arrayList.get(i).getTown_Code())) {
//                        matched = true;
//                    }
//                }
//
//                if(!matched) {
//                    arrayList.get(i).setChecked(false);
//                }
//            }
//        }
//
//        for (int i = 0; i<arrayList.size(); i++) {
//            if(arrayList.get(i).isChecked()) {
//                if(text.length() == 0) {
//                    text = new StringBuilder(arrayList.get(i).getName());
//                }else {
//                    text.append(",").append(arrayList.get(i).getName());
//                }
//            }
//        }
        StringBuilder text = new StringBuilder();
        ArrayList<MultiHQHeaderModelClass> resultDataHeaderList = new ArrayList<>();
        for (int i = 0; i < multiHQHeaderModelClassArrayList.size(); i++) {
            MultiHQHeaderModelClass dataHeader = new MultiHQHeaderModelClass(multiHQHeaderModelClassArrayList.get(i));
            ArrayList<MultiHQItemModelClass> resultDataItemList = new ArrayList<>();
            ArrayList<MultiHQItemModelClass> dataList = dataHeader.getItemsList();
            ArrayList<String> clusters = clusterCodesMap.get(dataHeader.getCode());
            if (clusters != null && !clusters.isEmpty()) {
                for (int j = 0; j < dataList.size(); j++) {
                    MultiHQItemModelClass data = dataHeader.getItemsList().get(j);
                    if (data != null && data.isChecked()) {
                        if (clusters.contains(data.getClusterCode())) {
                            if (text.length() == 0) {
                                text = new StringBuilder(data.getName());
                            } else {
                                text.append(",").append(data.getName());
                            }
                            resultDataItemList.add(data);
                        } else {
                            data.setChecked(false);
                        }
                    }
                }
            }
            if (!resultDataItemList.isEmpty()) {
                dataHeader.setItemsList(resultDataItemList);
                resultDataHeaderList.add(dataHeader);
            }
        }

        if (text.length() == 0) {
            label.setText(R.string.select);
        } else {
            label.setText(text);
        }

//        List<ModelClass.SessionList.SubClass> subClassList = new ArrayList<>();
//        for (int i = 0; i<arrayList.size(); i++) {
//            if(arrayList.get(i).isChecked()) {
//                ModelClass.SessionList.SubClass subClass = new ModelClass.SessionList.SubClass(arrayList.get(i).getName(), arrayList.get(i).getCode());
//                subClassList.add(subClass);
//            }
//        }
//        if (master.equalsIgnoreCase(Constants.DOCTOR)) {
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDrs().clear();
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDrs(resultDataHeaderList);
//        } else if (master.equalsIgnoreCase(Constants.CHEMIST)) {
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemists().clear();
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemists(resultDataHeaderList);
//        } else if (master.equalsIgnoreCase(Constants.UNLISTED_DOCTOR)) {
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDrs().clear();
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDrs(resultDataHeaderList);
//        } else if (master.equalsIgnoreCase(Constants.CIP)) {
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCips().clear();
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCips(resultDataHeaderList);
//        } else if (master.equalsIgnoreCase(Constants.HOSPITAL)) {
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospitals().clear();
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospitals(resultDataHeaderList);
//        }
        if (master.equalsIgnoreCase(Constants.DOCTOR_MAS)) {
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDrs().clear();
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDrs(resultDataHeaderList);
        } else if (master.equalsIgnoreCase(Constants.CHEMIST_MAS)) {
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemists().clear();
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemists(resultDataHeaderList);
//        } else if (master.equalsIgnoreCase(Constants.STOCKIEST_MAS)) {
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getStockiests().clear();
//            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setStockiests(resultDataHeaderList);
        } else if (master.equalsIgnoreCase(Constants.UNLISTED_DOCTOR_MAS)) {
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDrs().clear();
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDrs(resultDataHeaderList);
        } else if (master.equalsIgnoreCase(Constants.CIP)) {
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCips().clear();
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCips(resultDataHeaderList);
        } else if (master.equalsIgnoreCase(Constants.HOSPITAL)) {
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospitals().clear();
            inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospitals(resultDataHeaderList);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout searchClearIcon;
        public TextView workTypeField, hqField, clusterField, jcField, drField, chemistField, stockiestField, unListedDrField, cipField, hospField, workDayField;
        public TextView listedDrCapTV, cheCapTV, stockCapTV, unListedDrCapTV, hospCapTV, cipCapTV;
        public TextView hqCount, clusterCount, jcCount, drCount, chemistCount, stockiestCount, unListedDrCount, cipCount, hospCount, textCluster;
        public RelativeLayout sessionDelete, workTypeLayout, hqLayout, clusterLayout, jcLayout, drLayout, chemistLayout, stockiestLayout, unListedDrLayout, cipLayout, hospLayout, remarksLayout, workDayLayout;
        public ImageView workTypeArrow, hqArrow, clusterArrow, jcArrow, drArrow, chemistArrow, stockiestArrow, unListedDrArrow, cipArrow, hospArrow, workDayArrow;
        public ModelClass.SessionList sessionData = new ModelClass.SessionList();
        public OneBuildModelClass.SessionList sessionDataOneBuild = new OneBuildModelClass.SessionList();

        public ArrayList<EditModelClass> workTypeArray = new ArrayList<>();
        public ArrayList<EditModelClass> hqArray = new ArrayList<>();
        public ArrayList<EditModelClass> clusterArray = new ArrayList<>();
        public ArrayList<EditModelClass> jointCallArray = new ArrayList<>();
        public ArrayList<EditModelClass> listedDrArray = new ArrayList<>();
        public ArrayList<EditModelClass> chemistArray = new ArrayList<>();
        public ArrayList<EditModelClass> stockiestArray = new ArrayList<>();
        public ArrayList<EditModelClass> unListedDrArray = new ArrayList<>();
        public ArrayList<EditModelClass> cipArray = new ArrayList<>();
        public ArrayList<EditModelClass> hospArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrClusterArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrJointCallArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrListedDrArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrChemistArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrStockiestArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrUnListedDrArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrCipArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrHospArray = new ArrayList<>();
        public ArrayList<MultiHQHeaderModelClass> mgrSessionItemAdapterArray = new ArrayList<>();
        public ArrayList<EditModelClass> sessionItemAdapterArray = new ArrayList<>();
        public ArrayList<EditModelClass> workDayArray = new ArrayList<>();
        TextView sessionNoTxt;
        EditText searchET, remarks;
        RelativeLayout relativeLayout;
        CardView parentCarView, listCardView;
        RecyclerView itemRecView;
        boolean fieldSelected = false;
        //Input data from activity
        ArrayList<ModelClass.SessionList.SubClass> hqModelArray;
        ArrayList<ModelClass.SessionList.SubClass> clusterModelArray;
        ArrayList<ModelClass.SessionList.SubClass> jcModelArray;
        ArrayList<ModelClass.SessionList.SubClass> listedDrModelArray;
        ArrayList<ModelClass.SessionList.SubClass> chemistModelArray;
        ArrayList<ModelClass.SessionList.SubClass> stockiestModelArray;
        ArrayList<ModelClass.SessionList.SubClass> unListedDrModelArray;
        ArrayList<ModelClass.SessionList.SubClass> cipModelArray;
        ArrayList<ModelClass.SessionList.SubClass> hospitalModelArray;
        ArrayList<MultiHQHeaderModelClass> clustersModelArray;
        ArrayList<MultiHQHeaderModelClass> jcsModelArray;
        ArrayList<MultiHQHeaderModelClass> listedDrsModelArray;
        ArrayList<MultiHQHeaderModelClass> chemistsModelArray;
        ArrayList<MultiHQHeaderModelClass> stockistsModelArray;
        ArrayList<MultiHQHeaderModelClass> unListedDrsModelArray;
        ArrayList<MultiHQHeaderModelClass> cipsModelArray;
        ArrayList<MultiHQHeaderModelClass> hospitalsModelArray;

        ArrayList<OneBuildModelClass.SessionList.SubClass> hqModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> clusterModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> jcModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> listedDrModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> chemistModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> stockListModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> unListedDrModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> cipModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> hospitalModelArrayOneBuild;

        String hq_code = "", selectedHq = "", hqNeed = "", clusterNeed = "";
        ArrayList<String> selectedHQCode = new ArrayList<>();
        ArrayList<String> selectedClusterCode = new ArrayList<>();
        HashMap<String, ArrayList<String>> selectedClusterCodeMap = new HashMap<>();

        ProgressBar progress_hq;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            parentCarView = itemView.findViewById(R.id.cardView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            sessionNoTxt = itemView.findViewById(R.id.sessionNo);
            sessionDelete = itemView.findViewById(R.id.sessionDelete);
            searchET = itemView.findViewById(R.id.searchET);
            searchClearIcon = itemView.findViewById(R.id.searchClearIcon);

            workTypeLayout = itemView.findViewById(R.id.workTypeLayout);
            hqLayout = itemView.findViewById(R.id.hqLayout);
            clusterLayout = itemView.findViewById(R.id.clusterLayout);
            jcLayout = itemView.findViewById(R.id.jcLayout);
            drLayout = itemView.findViewById(R.id.listedDrLayout);
            chemistLayout = itemView.findViewById(R.id.chemistLayout);
            stockiestLayout = itemView.findViewById(R.id.stockiestLayout);
            unListedDrLayout = itemView.findViewById(R.id.unListedDrLayout);
            cipLayout = itemView.findViewById(R.id.cipLayout);
            hospLayout = itemView.findViewById(R.id.hospLayout);
            remarksLayout = itemView.findViewById(R.id.remarkLayout);
            remarks = itemView.findViewById(R.id.remarkET);
            progress_hq = itemView.findViewById(R.id.progress_hq);
            workDayLayout = itemView.findViewById(R.id.work_day_layout);

            workTypeArrow = itemView.findViewById(R.id.workTypeArrow);
            hqArrow = itemView.findViewById(R.id.hqArrow);
            clusterArrow = itemView.findViewById(R.id.clusterArrow);
            jcArrow = itemView.findViewById(R.id.jcArrow);
            drArrow = itemView.findViewById(R.id.listedDrArrow);
            chemistArrow = itemView.findViewById(R.id.chemistArrow);
            stockiestArrow = itemView.findViewById(R.id.stockiestArrow);
            unListedDrArrow = itemView.findViewById(R.id.unListedDrArrow);
            cipArrow = itemView.findViewById(R.id.cipArrow);
            hospArrow = itemView.findViewById(R.id.hospArrow);
            workDayArrow = itemView.findViewById(R.id.work_day_arrow);

            workTypeField = itemView.findViewById(R.id.workTypeField);
            hqField = itemView.findViewById(R.id.hqField);
            clusterField = itemView.findViewById(R.id.clusterField);
            jcField = itemView.findViewById(R.id.jcField);
            drField = itemView.findViewById(R.id.listedDrField);
            chemistField = itemView.findViewById(R.id.chemistField);
            stockiestField = itemView.findViewById(R.id.stockiestField);
            unListedDrField = itemView.findViewById(R.id.unListedDrField);
            cipField = itemView.findViewById(R.id.cipField);
            hospField = itemView.findViewById(R.id.hospField);
            workDayField = itemView.findViewById(R.id.work_day_field);

            listedDrCapTV = itemView.findViewById(R.id.listedDrCap);
            cheCapTV = itemView.findViewById(R.id.chemistCap);
            stockCapTV = itemView.findViewById(R.id.stockiestCap);
            unListedDrCapTV = itemView.findViewById(R.id.unListedDrCap);
            hospCapTV = itemView.findViewById(R.id.hospCap);
            cipCapTV = itemView.findViewById(R.id.cipCap);

            hqCount = itemView.findViewById(R.id.hqCount);
            clusterCount = itemView.findViewById(R.id.clusterCount);
            jcCount = itemView.findViewById(R.id.jcCount);
            drCount = itemView.findViewById(R.id.listedDrCount);
            chemistCount = itemView.findViewById(R.id.chemistCount);
            stockiestCount = itemView.findViewById(R.id.stockiestCount);
            unListedDrCount = itemView.findViewById(R.id.unListedDrCount);
            cipCount = itemView.findViewById(R.id.cipCount);
            hospCount = itemView.findViewById(R.id.hospCount);

            listCardView = itemView.findViewById(R.id.listCardView);
            itemRecView = itemView.findViewById(R.id.sessionItemRecView);
            textCluster = itemView.findViewById(R.id.textCluster);

        /*    listedDrCapTV.setText(SharedPref.getDrCap(context));
            cheCapTV.setText(SharedPref.getChmCap(context));
            stockCapTV.setText(SharedPref.getStkCap(context));
            unListedDrCapTV.setText(SharedPref.getUNLcap(context));
            hospCapTV.setText(SharedPref.getHospCaption(context));
            cipCapTV.setText(SharedPref.getCipCaption(context));*/

        }
    }

}