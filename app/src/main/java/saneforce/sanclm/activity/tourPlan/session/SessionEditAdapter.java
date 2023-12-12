package saneforce.sanclm.activity.tourPlan.session;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.activity.tourPlan.TourPlanActivity;
import saneforce.sanclm.activity.tourPlan.model.EditModelClass;
import saneforce.sanclm.activity.tourPlan.model.ModelClass;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class SessionEditAdapter extends RecyclerView.Adapter<SessionEditAdapter.MyViewHolder> {

    public static ModelClass inputDataArray = new ModelClass();
    Context context;
    SQLite sqLite;
    LoginResponse loginResponse;
    ApiInterface apiInterface;
    SessionInterface sessionInterface;
    SessionItemAdapter sessionItemAdapter = new SessionItemAdapter();
    String sfCode = "", division_code = "", sfType = "", designation = "", state_code = "", subdivision_code = "";
    String jwNeed = "", drNeed = "", chemistNeed = "", stockiestNeed = "", unListedDrNeed = "", cipNeed = "", hospNeed = "", FW_meetup_mandatory = "";
    static String drCap = "", chemistCap = "", stockiestCap = "", unDrCap = "", hospCap = "", cipCap = "";
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    public int itemPosition;

    public SessionEditAdapter() {
    }

    public SessionEditAdapter(ModelClass inputDataArray, Context context, SessionInterface sessionInterface) {
        SessionEditAdapter.inputDataArray = inputDataArray;
        this.context = context;
        this.sessionInterface = sessionInterface;

        sqLite = new SQLite(context);
        loginResponse = sqLite.getLoginData();

        sfCode = loginResponse.getSF_Code();
        division_code = loginResponse.getDivision_Code();
        subdivision_code = loginResponse.getSubdivision_code();
        designation = loginResponse.getDesig();
        state_code = loginResponse.getState_Code();
        sfType = loginResponse.getSf_type();
        drCap = loginResponse.getDrCap();
        chemistCap = loginResponse.getChmCap();
        stockiestCap = loginResponse.getStkCap();
        unDrCap = loginResponse.getNLCap();
        hospCap = loginResponse.getHosp_caption();
        cipCap = loginResponse.getCIP_Caption();
//        hq_code = SharedPref.getHqCode(context); // Selected HQ code in master sync ,it will be changed if any other HQ selected in Add Plan

        //Tour Plan setup
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.TP_SETUP);
            for (int i = 0; i<jsonArray.length(); i++) {
                drNeed = jsonArray.getJSONObject(i).getString("DrNeed");
                chemistNeed = jsonArray.getJSONObject(i).getString("ChmNeed");
                jwNeed = jsonArray.getJSONObject(i).getString("JWNeed");
                stockiestNeed = jsonArray.getJSONObject(i).getString("StkNeed");
                unListedDrNeed = jsonArray.getJSONObject(i).getString("UnDrNeed");
                cipNeed = jsonArray.getJSONObject(i).getString("Cip_Need");
                hospNeed = jsonArray.getJSONObject(i).getString("HospNeed");
                FW_meetup_mandatory = jsonArray.getJSONObject(i).getString("FW_meetup_mandatory");

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public SessionEditAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_edit_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull SessionEditAdapter.MyViewHolder holder, int position) {

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

        if (holder.sessionData.getVisible()) {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }else {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        holder.sessionNoTxt.setText("Session " + (position + 1));
        if (holder.getAbsoluteAdapterPosition() == 0) { //No need to show delete icon if there is only one session
            if (inputDataArray.getSessionList().size()>1)
                holder.sessionDelete.setVisibility(View.VISIBLE);
            else
                holder.sessionDelete.setVisibility(View.GONE);

        }

        //work Type
        if (holder.sessionData.getWorkType().getName().equals("")) {
            holder.workTypeField.setText("Select");
        }else {
            holder.workTypeField.setText(holder.sessionData.getWorkType().getName());
        }

        if (holder.sessionData.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")) { // Y - yes
            holder.hqNeed = "0"; // 0 - Yes
            holder.clusterNeed = "0";
        }else if (holder.sessionData.getWorkType().getTerrSlFlg().equalsIgnoreCase("N")) {
            holder.hqNeed = "1"; // 1 - No
            holder.clusterNeed = "1";
        }
        workTypeBasedUI(holder, holder.sessionData, true);

        //HQ
        if (holder.sessionData.getHQ().getName().equals("")) {
            holder.hqField.setText("Select");
        }else {
            holder.hqField.setText(holder.sessionData.getHQ().getName());
            holder.selectedHq = holder.sessionData.getHQ().getCode();
        }

        if (!holder.selectedHq.equals("")) {
            getDataFromLocal(holder, holder.selectedHq);
        }

        //Cluster
        StringBuilder clusterName = new StringBuilder();
        for (int i = 0; i<holder.clusterModelArray.size(); i++) {
            if (clusterName.length() == 0) {
                clusterName = new StringBuilder(holder.clusterModelArray.get(i).getName());
            }else {
                clusterName.append(", ").append(holder.clusterModelArray.get(i).getName());
            }
        }
        if (clusterName.length()>0) {
            holder.clusterField.setText(clusterName);
        }
        prepareInputData(holder.clusterModelArray, holder.clusterArray);

        holder.selectedClusterCode.clear();
        for (int i = 0; i<holder.clusterModelArray.size(); i++) {
            holder.selectedClusterCode.add(holder.clusterModelArray.get(i).getCode());
        }

        //Joint Work
        StringBuilder jcName = new StringBuilder();
        for (int i = 0; i<holder.jcModelArray.size(); i++) {
            if (jcName.length() == 0) {
                jcName = new StringBuilder(holder.jcModelArray.get(i).getName());
            }else {
                jcName.append(", ").append(holder.jcModelArray.get(i).getName());
            }
        }
        if (jcName.length()>0) {
            holder.jcField.setText(jcName);
        }
        prepareInputData(holder.jcModelArray, holder.jointCallArray);

        //Dr
        StringBuilder drName = new StringBuilder();
        for (int i = 0; i<holder.listedDrModelArray.size(); i++) {
            if (drName.length() == 0) {
                drName = new StringBuilder(holder.listedDrModelArray.get(i).getName());
            }else {
                drName.append(", ").append(holder.listedDrModelArray.get(i).getName());
            }
        }
        if (drName.length()>0) {
            holder.drField.setText(drName);
        }
        prepareInputData(holder.listedDrModelArray, holder.listedDrArray);

        //Chemist
        StringBuilder chemistName = new StringBuilder();
        for (int i = 0; i<holder.chemistModelArray.size(); i++) {
            if (chemistName.length() == 0) {
                chemistName = new StringBuilder(holder.chemistModelArray.get(i).getName());
            }else {
                chemistName.append(", ").append(holder.chemistModelArray.get(i).getName());
            }
        }
        if (chemistName.length()>0) {
            holder.chemistField.setText(chemistName);
        }
        prepareInputData(holder.chemistModelArray, holder.chemistArray);

        //Stockiest
        StringBuilder stockiestName = new StringBuilder();
        for (int i = 0; i<holder.stockiestModelArray.size(); i++) {
            if (stockiestName.length() == 0) {
                stockiestName = new StringBuilder(holder.stockiestModelArray.get(i).getName());
            }else {
                stockiestName.append(", ").append(holder.stockiestModelArray.get(i).getName());
            }
        }
        if (stockiestName.length()>0) {
            holder.stockiestField.setText(stockiestName);
        }
        prepareInputData(holder.stockiestModelArray, holder.stockiestArray);

        //UnListed Doctor
        StringBuilder unListedDrName = new StringBuilder();
        for (int i = 0; i<holder.unListedDrModelArray.size(); i++) {
            if (unListedDrName.length() == 0) {
                unListedDrName = new StringBuilder(holder.unListedDrModelArray.get(i).getName());
            }else {
                unListedDrName.append(", ").append(holder.unListedDrModelArray.get(i).getName());
            }

        }
        if (unListedDrName.length()>0) {
            holder.unListedDrField.setText(unListedDrName);
        }
        prepareInputData(holder.unListedDrModelArray, holder.unListedDrArray);

        //Cip
        StringBuilder cipName = new StringBuilder();
        for (int i = 0; i<holder.cipModelArray.size(); i++) {
            if (cipName.length() == 0) {
                cipName = new StringBuilder(holder.cipModelArray.get(i).getName());
            }else {
                cipName.append(",").append(holder.cipModelArray.get(i).getName());
            }

        }
        if (cipName.length()>0) {
            holder.cipField.setText(cipName);
        }
        prepareInputData(holder.cipModelArray, holder.cipArray);

        //Hospital
        StringBuilder hospName = new StringBuilder();
        for (int i = 0; i<holder.hospitalModelArray.size(); i++) {
            if (hospName.length() == 0) {
                hospName = new StringBuilder(holder.hospitalModelArray.get(i).getName());
            }else {
                jcName.append(", ").append(holder.hospitalModelArray.get(i).getName());
            }
        }
        if (hospName.length()>0) {
            holder.hospField.setText(hospName);
        }
        prepareInputData(holder.hospitalModelArray, holder.hospArray);
        holder.remarks.setText(holder.sessionData.getRemarks());

        holder.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0) {
                    holder.searchClearIcon.setVisibility(View.VISIBLE);
                }else {
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
                if(id == EditorInfo.IME_ACTION_DONE){
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
                    if (holder.workTypeArray.size()==0) {
                        holder.workTypeArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE));
                    }
                    ArrayList<EditModelClass> filteredArray = new ArrayList<>();
                    for (int i = 0; i<holder.workTypeArray.size(); i++) {
                        if (holder.workTypeArray.get(i).getTP_DCR().contains("T")) {
                            filteredArray.add(holder.workTypeArray.get(i));
                        }
                    }
                    holder.sessionItemAdapterArray = filteredArray;
                    populateSessionItemAdapter(holder, false);
                    holder.fieldSelected = true;
                    onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.WORK_TYPE);
                }else {
                    changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, true);
                    holder.fieldSelected = false;
                    onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                }
                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
            }
        });

        holder.hqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);
                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else {
                    if (!holder.fieldSelected) {
                        if (holder.hqArray.size()==0) {
                            holder.hqArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE));
                        }
                        holder.sessionItemAdapterArray = holder.hqArray;
                        populateSessionItemAdapter(holder,false);
                        holder.fieldSelected = true;
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.SUBORDINATE);
                    }else {
                        changeUIState(holder, holder.hqLayout, holder.hqArrow, true);
                        holder.fieldSelected = false;
                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                    }
                }
                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
            }
        });

        holder.clusterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);
                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else {
                    if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                        Toast.makeText(context, "Select Head Quarters", Toast.LENGTH_SHORT).show();
                    }else {
                        if (!holder.fieldSelected) {
                            if (holder.clusterArray.size()==0) {
                                holder.clusterArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.CLUSTER + holder.selectedHq));
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            }else {
                                setSelectedCount(holder, holder.clusterArray, false, holder.clusterField, holder.clusterCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = holder.clusterArray;
                            populateSessionItemAdapter(holder,true);
                            onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CLUSTER);
                        }else {
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.clusterArray, true, holder.clusterField, holder.clusterCount);
                            changeUIState(holder, holder.clusterLayout, holder.clusterArrow, true);
                            onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                        }
                    }
                }
            }
        });

        holder.jcLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                }else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                }else {
                    if (!holder.fieldSelected) {
                        if (holder.jointCallArray.size()==0) {
                            holder.jointCallArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK + holder.selectedHq));
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        }else {
                            setSelectedCount(holder, holder.jointCallArray, false, holder.jcField, holder.jcCount);
                        }
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray =holder.jointCallArray;
                        populateSessionItemAdapter(holder, true);
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.JOINT_WORK);
                    }else {
                        holder.fieldSelected = false;
                        setSelectedCount(holder, holder.jointCallArray, true, holder.jcField, holder.jcCount);
                        changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                    }
                }

            }
        });

        holder.drLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                }else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                }else {
                    if (!holder.fieldSelected) {
                        if (holder.listedDrArray.size()==0) {
                            holder.listedDrArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.DOCTOR + holder.selectedHq));
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        }else {
                            setSelectedCount(holder, holder.listedDrArray, false, holder.drField, holder.drCount);
                        }
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray =filterJsonArray(holder, holder.listedDrArray);
                        populateSessionItemAdapter(holder,true);
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.DOCTOR);
                    }else {
                        holder.fieldSelected = false;
                        setSelectedCount(holder, holder.listedDrArray, true, holder.drField, holder.drCount);
                        changeUIState(holder, holder.drLayout, holder.drArrow, true);
                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                    }
                }
            }
        });

        holder.chemistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                }else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                }else {

                    if (!holder.fieldSelected) {
                        if (holder.chemistArray.size()==0) {
                            holder.chemistArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + holder.selectedHq));
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        }else {
                            setSelectedCount(holder, holder.chemistArray, false, holder.chemistField, holder.chemistCount);
                        }

                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = filterJsonArray(holder, holder.chemistArray);
                        populateSessionItemAdapter(holder,true);
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CHEMIST);
                    }else {
                        holder.fieldSelected = false;
                        setSelectedCount(holder, holder.chemistArray, true, holder.chemistField, holder.chemistCount);
                        changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                    }
                }
            }
        });

        holder.stockiestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                }else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                }else {
                    if (!holder.fieldSelected) {
                        if (holder.stockiestArray.size()==0) {
                            holder.stockiestArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + holder.selectedHq));
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        }else {
                            setSelectedCount(holder, holder.stockiestArray, false, holder.stockiestField, holder.stockiestCount);
                        }
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray =holder.stockiestArray;
                        populateSessionItemAdapter(holder,true);
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.STOCKIEST);
                    }else {
                        holder.fieldSelected = false;
                        setSelectedCount(holder, holder.stockiestArray, true, holder.stockiestField, holder.stockiestCount);
                        changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                    }

                }
            }
        });

        holder.unListedDrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                }else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                }else {
                    if (!holder.fieldSelected) {
                        if (holder.unListedDrArray.size()==0) {
                            holder.unListedDrArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + holder.selectedHq));
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        }else {
                            setSelectedCount(holder, holder.unListedDrArray, false, holder.unListedDrField, holder.unListedDrCount);
                        }
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray =filterJsonArray(holder, holder.unListedDrArray);
                        populateSessionItemAdapter(holder,true);
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.UNLISTED_DOCTOR);
                    }else {
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
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                }else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                }else {
                    if (!holder.fieldSelected) {
                        if (holder.cipArray.size()==0) {
                            holder.cipArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + holder.selectedHq));
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        }else {
                            setSelectedCount(holder, holder.cipArray, false, holder.cipField, holder.cipCount);
                        }
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray =filterJsonArray(holder, holder.cipArray);
                        populateSessionItemAdapter(holder,true);
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.CIP);
                    }else {
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
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                }else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                }else {
                    if (!holder.fieldSelected) {
                        if (holder.hospArray.size()==0) {
                            holder.hospArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + holder.selectedHq));
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        }else {
                            setSelectedCount(holder, holder.hospArray, false, holder.hospField, holder.hospCount);
                        }
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = filterJsonArray(holder, holder.hospArray);
                        populateSessionItemAdapter(holder,true);
                        onEdit(holder.getAbsoluteAdapterPosition(), false, Constants.HOSPITAL);
                    }else {
                        holder.fieldSelected = false;
                        setSelectedCount(holder, holder.hospArray, true, holder.hospField, holder.hospCount);
                        changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
                        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
                    }
                }
            }
        });

        holder.remarks.setOnTouchListener((v, event) -> {
            if (holder.remarks.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });

        holder.remarks.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        });

        holder.sessionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionInterface.deleteClicked(inputDataArray, holder.getAbsoluteAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return inputDataArray.getSessionList().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView sessionNoTxt;
        public LinearLayout searchClearIcon;
        public TextView workTypeField, hqField, clusterField, jcField, drField, chemistField, stockiestField, unListedDrField, cipField, hospField;
        public TextView listedDrCapTV, cheCapTV, stockCapTV, unListedDrCapTV, hospCapTV, cipCapTV;
        public TextView clusterCount, jcCount, drCount, chemistCount, stockiestCount, unListedDrCount, cipCount, hospCount;
        public LinearLayout sessionDelete, workTypeLayout, hqLayout, clusterLayout, jcLayout, drLayout, chemistLayout, stockiestLayout, unListedDrLayout, cipLayout, hospLayout, remarksLayout;
        public ImageView workTypeArrow, hqArrow, clusterArrow, jcArrow, drArrow, chemistArrow, stockiestArrow, unListedDrArrow, cipArrow, hospArrow;
        EditText searchET, remarks;
        RelativeLayout relativeLayout;
        CardView parentCarView, listCardView;
        RecyclerView itemRecView;
        boolean fieldSelected = false;

        //Input data
        ArrayList<ModelClass.SessionList.SubClass> clusterModelArray;
        ArrayList<ModelClass.SessionList.SubClass> jcModelArray;
        ArrayList<ModelClass.SessionList.SubClass> listedDrModelArray;
        ArrayList<ModelClass.SessionList.SubClass> chemistModelArray;
        ArrayList<ModelClass.SessionList.SubClass> stockiestModelArray;
        ArrayList<ModelClass.SessionList.SubClass> unListedDrModelArray;
        ArrayList<ModelClass.SessionList.SubClass> cipModelArray;
        ArrayList<ModelClass.SessionList.SubClass> hospitalModelArray;

        public ModelClass.SessionList sessionData = new ModelClass.SessionList();

        // Data from sqLite storage
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
        public ArrayList<EditModelClass> sessionItemAdapterArray = new ArrayList<>();

        String hq_code = "", selectedHq = "", hqNeed = "", clusterNeed = "";
        ArrayList<String> selectedClusterCode = new ArrayList<>();

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

            listedDrCapTV = itemView.findViewById(R.id.listedDrCap);
            cheCapTV = itemView.findViewById(R.id.chemistCap);
            stockCapTV = itemView.findViewById(R.id.stockiestCap);
            unListedDrCapTV = itemView.findViewById(R.id.unListedDrCap);
            hospCapTV = itemView.findViewById(R.id.hospCap);
            cipCapTV = itemView.findViewById(R.id.cipCap);

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

            listedDrCapTV.setText(drCap);
            cheCapTV.setText(chemistCap);
            stockCapTV.setText(stockiestCap);
            unListedDrCapTV.setText(unDrCap);
            hospCapTV.setText(hospCap);
            cipCapTV.setText(cipCap);

        }
    }

    public void workTypeBasedUI(MyViewHolder holder, ModelClass.SessionList session, boolean bool) {

        String workType = session.getWorkType().getFWFlg();
        switch (workType){
            case "F":{
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

                if (unListedDrNeed.equalsIgnoreCase("0"))
                    holder.unListedDrLayout.setVisibility(View.VISIBLE);
                else
                    holder.unListedDrLayout.setVisibility(View.GONE);

                if (cipNeed.equalsIgnoreCase("0"))
                    holder.cipLayout.setVisibility(View.VISIBLE);
                else
                    holder.cipLayout.setVisibility(View.GONE);

                if (hospNeed.equalsIgnoreCase("0"))
                    holder.hospLayout.setVisibility(View.VISIBLE);
                else
                    holder.hospLayout.setVisibility(View.GONE);

                break;
            }
            case "W":
            case "H":
            case "L":{
                holder.hqLayout.setVisibility(View.GONE);
                holder.clusterLayout.setVisibility(View.GONE);
                holder.jcLayout.setVisibility(View.GONE);
                holder.drLayout.setVisibility(View.GONE);
                holder.chemistLayout.setVisibility(View.GONE);
                holder.stockiestLayout.setVisibility(View.GONE);
                holder.unListedDrLayout.setVisibility(View.GONE);
                holder.cipLayout.setVisibility(View.GONE);
                holder.hospLayout.setVisibility(View.GONE);
                break;
            }
            case "N":{
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
                }else {
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
                break;
            }
            default:{
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

                if (unListedDrNeed.equalsIgnoreCase("0"))
                    holder.unListedDrLayout.setVisibility(View.VISIBLE);
                else
                    holder.unListedDrLayout.setVisibility(View.GONE);

                if (cipNeed.equalsIgnoreCase("0"))
                    holder.cipLayout.setVisibility(View.VISIBLE);
                else
                    holder.cipLayout.setVisibility(View.GONE);

                if (hospNeed.equalsIgnoreCase("0"))
                    holder.hospLayout.setVisibility(View.VISIBLE);
                else
                    holder.hospLayout.setVisibility(View.GONE);
            }
        }

        if (bool) {
            switch (session.getLayoutVisible()){
                case Constants.WORK_TYPE:{
                    changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, false);
                    break;
                }
                case Constants.SUBORDINATE:{
                    changeUIState(holder, holder.hqLayout, holder.hqArrow, false);
                    break;
                }
                case Constants.CLUSTER:{
                    changeUIState(holder, holder.clusterLayout, holder.clusterArrow, false);
                    break;
                }
                case Constants.JOINT_WORK:{
                    changeUIState(holder, holder.jcLayout, holder.jcArrow, false);
                    break;
                }
                case Constants.DOCTOR:{
                    changeUIState(holder, holder.drLayout, holder.drArrow, false);
                    break;
                }
                case Constants.CHEMIST:{
                    changeUIState(holder, holder.chemistLayout, holder.chemistArrow, false);
                    break;
                }
                case Constants.STOCKIEST:{
                    changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, false);
                    break;
                }
                case Constants.UNLISTED_DOCTOR:{
                    changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, false);
                    break;
                }
                case Constants.CIP:{
                    changeUIState(holder, holder.cipLayout, holder.cipArrow, false);
                    break;
                }
                case Constants.HOSPITAL:{
                    changeUIState(holder, holder.hospLayout, holder.hospArrow, false);
                    break;
                }
            }
        }

    }

    public void getDataFromLocal(MyViewHolder holder, String hqCode) {

        if (!sqLite.getMasterSyncDataOfHQ(Constants.CLUSTER + hqCode)) {
            prepareMasterToSync(hqCode);
        }

        holder.clusterArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hqCode));
        holder.jointCallArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK + hqCode));
        holder.listedDrArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.DOCTOR + hqCode));
        holder.chemistArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + hqCode));
        holder.stockiestArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + hqCode));
        holder.unListedDrArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + hqCode));
        holder.cipArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + hqCode));
        holder.hospArray = convertJSONToModel(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + hqCode));

    }

    public ArrayList<EditModelClass> convertJSONToModel(JSONArray jsonArray){
        Type type = new TypeToken<ArrayList<EditModelClass>>(){}.getType();
        return new Gson().fromJson(String.valueOf(jsonArray), type);
    }

    public void prepareMasterToSync(String hqCode) {
        masterSyncArray.clear();
        MasterSyncItemModel doctorModel = new MasterSyncItemModel(Constants.DOCTOR, "getdoctors", Constants.DOCTOR + hqCode);
        MasterSyncItemModel cheModel = new MasterSyncItemModel(Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode);
        MasterSyncItemModel stockModel = new MasterSyncItemModel(Constants.DOCTOR, "getstockist", Constants.STOCKIEST + hqCode);
        MasterSyncItemModel unListModel = new MasterSyncItemModel(Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode);
        MasterSyncItemModel hospModel = new MasterSyncItemModel(Constants.DOCTOR, "gethospital", Constants.HOSPITAL + hqCode);
        MasterSyncItemModel ciModel = new MasterSyncItemModel(Constants.DOCTOR, "getcip", Constants.CIP + hqCode);
        MasterSyncItemModel cluster = new MasterSyncItemModel(Constants.DOCTOR, "getterritory", Constants.CLUSTER + hqCode);
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel(Constants.SUBORDINATE, "getjointwork", Constants.JOINT_WORK + hqCode);

        masterSyncArray.add(doctorModel);
        masterSyncArray.add(cheModel);
        masterSyncArray.add(stockModel);
        masterSyncArray.add(unListModel);
        masterSyncArray.add(hospModel);
        masterSyncArray.add(ciModel);
        masterSyncArray.add(cluster);
        masterSyncArray.add(jWorkModel);
        for (int i = 0; i<masterSyncArray.size(); i++) {
            sync(masterSyncArray.get(i), hqCode);
        }
    }

    public void sync(MasterSyncItemModel masterSyncItemModel, String hqCode) {

        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                String baseUrl = SharedPref.getBaseWebUrl(context);
                String pathUrl = SharedPref.getPhpPathUrl(context);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                apiInterface = RetrofitClient.getRetrofit(context, baseUrl + replacedUrl);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", masterSyncItemModel.getRemoteTableName());
                jsonObject.put("sfcode", sfCode);
                jsonObject.put("division_code", division_code);
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("sf_type", sfType);
                jsonObject.put("Designation", designation);
                jsonObject.put("state_code", state_code);
                jsonObject.put("subdivision_code", subdivision_code);

//                Log.e("test","master sync obj in TP : " + jsonObject);
                Call<JsonElement> call = null;
                if (masterSyncItemModel.getMasterOf().equalsIgnoreCase(Constants.DOCTOR)) {
                    call = apiInterface.getDrMaster(jsonObject.toString());
                }else if (masterSyncItemModel.getMasterOf().equalsIgnoreCase(Constants.SUBORDINATE)) {
                    call = apiInterface.getSubordinateMaster(jsonObject.toString());
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
                                    if (!Objects.requireNonNull(jsonElement).isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        }else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                            if (!jsonObject1.has("success")) { // json object with "success" : "fail" will be received only when api call is failed ,"success will not be received when api call is success
                                                jsonArray.put(jsonObject1);
                                                success = true;
                                            }else if (jsonObject1.has("success") && !jsonObject1.getBoolean("success")) {
                                                sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                            }
                                        }

                                        if (success) {
                                            sqLite.saveMasterSyncData(masterSyncItemModel.getLocalTableKeyName(), jsonArray.toString(), 0);
                                        }
                                    }else {
                                        sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(), 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(context, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    public void prepareInputData(ArrayList<ModelClass.SessionList.SubClass> modelClass, ArrayList<EditModelClass> arrayList) {

            if (modelClass.size()>0) {
                for (int i = 0; i<modelClass.size(); i++) {
                    for (int j = 0; j<arrayList.size(); j++) {
                        if (modelClass.get(i).getCode().equalsIgnoreCase(arrayList.get(j).getCode())) {
                            arrayList.get(j).setChecked(true);
                        }
                    }
                }
            }
    }

    public void populateSessionItemAdapter(MyViewHolder holder, boolean checkBoxNeed) {

        Collections.sort(holder.sessionItemAdapterArray, new Comparator<EditModelClass>() {
            @Override
            public int compare(EditModelClass editModelClass, EditModelClass t1) {
                return editModelClass.getName().compareTo(t1.getName());
            }
        });

        sessionItemAdapter = new SessionItemAdapter(holder.sessionItemAdapterArray, checkBoxNeed, new SessionItemInterface() {
            @SuppressLint("SetTextI18n")
            @Override
            public void itemClicked(ArrayList<EditModelClass> jsonArray, EditModelClass jsonObject) {
                if (holder.workTypeLayout.getVisibility() == View.VISIBLE) {
                    boolean workTypeRepeated = false;
                    if (inputDataArray.getSessionList().size()>1) {
                        for (int i = 0; i<inputDataArray.getSessionList().size(); i++) {
                            if (i != holder.getAbsoluteAdapterPosition()) {
                                if (inputDataArray.getSessionList().get(i).getWorkType().getFWFlg().equalsIgnoreCase(jsonObject.getFWFlg())) {
                                    switch (jsonObject.getFWFlg().toUpperCase()){
                                        case "W":
                                        case "H":{
                                            workTypeRepeated = true;
                                            Toast.makeText(context, "Work Type already been selected for session " + (i + 1), Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        case "N":{
                                            if (inputDataArray.getSessionList().get(i).getWorkType().getCode().equalsIgnoreCase(jsonObject.getCode())) {
                                                workTypeRepeated = true;
                                                Toast.makeText(context, "Work Type already been selected for session " + (i + 1), Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                        }
                                        case "F":{
                                            if (!sfType.equalsIgnoreCase("2")) {
                                                workTypeRepeated = true;
                                                Toast.makeText(context, "Work Type already been selected for session " + (i + 1), Toast.LENGTH_SHORT).show();
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
                        holder.sessionData.getWorkType().setName(jsonObject.getName());
                        holder.sessionData.getWorkType().setCode(jsonObject.getCode());
                        holder.sessionData.getWorkType().setFWFlg(jsonObject.getFWFlg());
                        holder.sessionData.getWorkType().setTerrSlFlg(jsonObject.getTerrSlFlg());

                        sessionInterface.fieldWorkSelected(inputDataArray, holder.getAbsoluteAdapterPosition());
                    }
                }
                else if (holder.hqLayout.getVisibility() == View.VISIBLE) {

                    boolean hqRepeated = false;
                    if (inputDataArray.getSessionList().size()>1) {
                        for (int i = 0; i<inputDataArray.getSessionList().size(); i++) {
                            ModelClass.SessionList modelClass = inputDataArray.getSessionList().get(i);
                            if (i != holder.getAbsoluteAdapterPosition()) {
                                if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase(inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getWorkType().getFWFlg())) {
                                    if (modelClass.getHQ().getCode().equalsIgnoreCase(jsonObject.getCode())) {
                                        hqRepeated = true;
                                        Toast.makeText(context, "HQ already been selected for the same work type for session " + (i + 1), Toast.LENGTH_SHORT).show();
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
                        }else {
                            sessionInterface.hqChanged(inputDataArray, itemPosition, false);
                        }
                    }
                }
                else if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }

                    if (count>0) {
                        holder.clusterField.setText("Selected");
                        holder.clusterCount.setVisibility(View.VISIBLE);
                        holder.clusterCount.setText(String.valueOf(count));
                    }else {
                        holder.clusterField.setText("Select");
                        holder.clusterCount.setVisibility(View.GONE);
                    }
                }
                else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }

                    if (count>0) {
                        holder.jcField.setText("Selected");
                        holder.jcCount.setVisibility(View.VISIBLE);
                        holder.jcCount.setText(String.valueOf(count));
                    }else {
                        holder.jcField.setText("Select");
                        holder.jcCount.setVisibility(View.GONE);
                    }
                }
                else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count>0) {
                        holder.drField.setText("Selected");
                        holder.drCount.setVisibility(View.VISIBLE);
                        holder.drCount.setText(String.valueOf(count));
                    }else {
                        holder.drField.setText("Select");
                        holder.drCount.setVisibility(View.GONE);
                    }
                }
                else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count>0) {
                        holder.chemistField.setText("Selected");
                        holder.chemistCount.setVisibility(View.VISIBLE);
                        holder.chemistCount.setText(String.valueOf(count));
                    }else {
                        holder.chemistField.setText("Select");
                        holder.chemistCount.setVisibility(View.GONE);
                    }
                }
                else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count>0) {
                        holder.stockiestField.setText("Selected");
                        holder.stockiestCount.setVisibility(View.VISIBLE);
                        holder.stockiestCount.setText(String.valueOf(count));
                    }else {
                        holder.stockiestField.setText("Select");
                        holder.stockiestCount.setVisibility(View.GONE);
                    }
                }
                else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count>0) {
                        holder.unListedDrField.setText("Selected");
                        holder.unListedDrCount.setVisibility(View.VISIBLE);
                        holder.unListedDrCount.setText(String.valueOf(count));
                    }else {
                        holder.unListedDrField.setText("Select");
                        holder.unListedDrCount.setVisibility(View.GONE);
                    }
                }
                else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count>0) {
                        holder.cipField.setText("Selected");
                        holder.cipCount.setVisibility(View.VISIBLE);
                        holder.cipCount.setText(String.valueOf(count));
                    }else {
                        holder.cipField.setText("Select");
                        holder.cipCount.setVisibility(View.GONE);
                    }
                }
                else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                    int count = 0;
                    for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                        if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                            count++;
                        }
                    }
                    if (count>0) {
                        holder.hospField.setText("Selected");
                        holder.hospCount.setVisibility(View.VISIBLE);
                        holder.hospCount.setText(String.valueOf(count));
                    }else {
                        holder.hospField.setText("Select");
                        holder.hospCount.setVisibility(View.GONE);
                    }
                }

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.itemRecView.setLayoutManager(layoutManager);
        holder.itemRecView.setAdapter(sessionItemAdapter);
        sessionItemAdapter.notifyDataSetChanged();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void changeUIState(MyViewHolder holder, LinearLayout linearLayout, ImageView imageView, boolean allLayoutVisible) {

        if (allLayoutVisible) {
            holder.workTypeLayout.setVisibility(View.VISIBLE);
            holder.remarksLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.down_arrow));
            holder.listCardView.setVisibility(View.GONE);
            TourPlanActivity.addSaveBtnLayout.setVisibility(View.VISIBLE);
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);

            workTypeBasedUI(holder, holder.sessionData, false);
        }else {
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
            holder.listCardView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.up_arrow));
            TourPlanActivity.addSaveBtnLayout.setVisibility(View.GONE);
//            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
        }
        UtilityClass.hideKeyboard((Activity) context);

    }

    @SuppressLint("SetTextI18n")
    public static void setSelectedCount(MyViewHolder holder, ArrayList<EditModelClass> arrayList, boolean selectState, TextView selectedNameTxtView, TextView countTxt) {

            if (!selectState) { // if its false we should show the text as "Selected" with count or just "Select" .if its true we need to show the selected item name in TextView.
                int count = 0;
                for (int i = 0; i<arrayList.size(); i++) {
                    if (arrayList.get(i).isChecked())
                        count++;
                }

                if (count>0) {
                    selectedNameTxtView.setText("Selected");
                    countTxt.setVisibility(View.VISIBLE);
                    countTxt.setText(String.valueOf(count));
                }else {
                    selectedNameTxtView.setText("Select");
                    countTxt.setVisibility(View.GONE);
                }
                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                holder.fieldSelected = true;
            }else {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i<arrayList.size(); i++) {
                    if (arrayList.get(i).isChecked()) {
                        if (text.length() == 0)
                            text = new StringBuilder(arrayList.get(i).getName());
                        else
                            text.append(",").append(arrayList.get(i).getName());
                    }
                }
                if (text.length() == 0) {
                    selectedNameTxtView.setText("Select");
                }else {
                    selectedNameTxtView.setText(text);
                }
                countTxt.setVisibility(View.GONE);
                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
                holder.fieldSelected = false;

                List<ModelClass.SessionList.SubClass> subClassList = new ArrayList<>();
                for (int i = 0; i<arrayList.size(); i++) {
                    if (arrayList.get(i).isChecked()) {
                        ModelClass.SessionList.SubClass subClass = new ModelClass.SessionList.SubClass(arrayList.get(i).getName(), arrayList.get(i).getCode());
                        subClassList.add(subClass);
                    }
                }

                //replace the new/modified data to the input data of this adapter class
                if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCluster().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCluster(subClassList);
                }else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getJC().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setJC(subClassList);
                }else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDr().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDr(subClassList);
                }else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemist().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemist(subClassList);
                }else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getStockiest().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setStockiest(subClassList);
                }else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDr().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDr(subClassList);
                }else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassList);
                }else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospital().clear();
                    inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospital(subClassList);
                }
            }
    }

    public ArrayList<EditModelClass> filterJsonArray(MyViewHolder holder, ArrayList<EditModelClass> arrayList1) { // Filters based on selected cluster
        ArrayList<EditModelClass> arrayList = new ArrayList<>();
            for (int i = 0; i<holder.selectedClusterCode.size(); i++) {
                for (int j = 0; j<arrayList1.size(); j++) {
                    if (holder.selectedClusterCode.get(i).equalsIgnoreCase(arrayList1.get(j).getTown_Code())) { // filtering based on selected Cluster code
                        arrayList.add(arrayList1.get(j));
                    }
                }
            }

        return arrayList;
    }

    public void onEdit(int position, boolean visibility, String layoutVisible) {
        // to hide other sessions and also other fields of same session while select corresponding field at a time of edit

        for (int i = 0; i<inputDataArray.getSessionList().size(); i++) {
            if (i != position)
                inputDataArray.getSessionList().get(i).setVisible(visibility); // set all other sessions visibility either true or false
            else
                inputDataArray.getSessionList().get(i).setLayoutVisible(layoutVisible); // to set which one need to be visible at a time while edit of a same session.ex: when user click on session1 cluster then session1 cluster will only be visible
        }
        notifyDataSetChanged();
        TourPlanActivity activity = (TourPlanActivity) context;
        activity.scrollToPosition(position, false);
    }

    public void clearCheckBox(MyViewHolder holder) {

        if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.clusterField, holder.clusterCount);
        }else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.jcField, holder.jcCount);
        }else if (holder.drLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.drField, holder.drCount);
        }else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.chemistField, holder.chemistCount);
        }else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.stockiestField, holder.stockiestCount);
        }else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.unListedDrField, holder.unListedDrCount);
        }else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.cipField, holder.cipCount);
        }else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder, holder.hospField, holder.hospCount);
        }
    }

    @SuppressLint("SetTextI18n")
    public void clearSelectedItem(MyViewHolder holder, TextView labelTxt, TextView countTxt) {
         // un check the all check boxes
        for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                holder.sessionItemAdapterArray.get(i).setChecked(false);
            }
            labelTxt.setText("Select");
            countTxt.setVisibility(View.GONE);
            sessionItemAdapter.notifyDataSetChanged();


    }

    public void saveCheckedItem(MyViewHolder holder) {

            if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                holder.selectedClusterCode.clear();
                for (int i = 0; i<holder.sessionItemAdapterArray.size(); i++) {
                    if (holder.sessionItemAdapterArray.get(i).isChecked()) {
                        holder.selectedClusterCode.add(holder.sessionItemAdapterArray.get(i).getCode());
                    }
                }

                clusterChanged(holder.selectedClusterCode, holder.listedDrArray, holder.drField, Constants.DOCTOR, holder);
                clusterChanged(holder.selectedClusterCode, holder.chemistArray, holder.chemistField, Constants.CHEMIST, holder);
                clusterChanged(holder.selectedClusterCode, holder.stockiestArray, holder.stockiestField, Constants.STOCKIEST, holder);
                clusterChanged(holder.selectedClusterCode, holder.unListedDrArray, holder.unListedDrField, Constants.UNLISTED_DOCTOR, holder);
                clusterChanged(holder.selectedClusterCode, holder.cipArray, holder.cipField, Constants.CIP, holder);
                clusterChanged(holder.selectedClusterCode, holder.hospArray, holder.hospField, Constants.HOSPITAL, holder);

                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.clusterField, holder.clusterCount);
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setLayoutVisible("");
                sessionInterface.clusterChanged(inputDataArray, holder.getLayoutPosition());
            }else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.jcField, holder.jcCount);
                changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
            }else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.drField, holder.drCount);
                changeUIState(holder, holder.drLayout, holder.drArrow, true);
            }else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.chemistField, holder.chemistCount);
                changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
            }else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.stockiestField, holder.stockiestCount);
                changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
            }else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.unListedDrField, holder.unListedDrCount);
                changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, true);
            }else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.cipField, holder.cipCount);
                changeUIState(holder, holder.cipLayout, holder.cipArrow, true);
            }else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.hospField, holder.hospCount);
                changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
            }

        onEdit(holder.getAbsoluteAdapterPosition(), true, "");
    }

    @SuppressLint("SetTextI18n")
    public void clusterChanged(ArrayList<String> clusterCodes, ArrayList<EditModelClass> arrayList, TextView label, String master, MyViewHolder holder) {

            for (int i = 0; i<arrayList.size(); i++) {
                if (arrayList.get(i).isChecked()) {
                    boolean matched = false;
                    for (int j = 0; j<clusterCodes.size(); j++) {
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
            for (int i = 0; i<arrayList.size(); i++) {
                if (arrayList.get(i).isChecked()) {
                    if (text.length() == 0) {
                        text = new StringBuilder(arrayList.get(i).getName());
                    }else {
                        text.append(",").append(arrayList.get(i).getName());
                    }
                }
            }
            if (text.length() == 0) {
                label.setText("Select");
            }else {
                label.setText(text);
            }

            List<ModelClass.SessionList.SubClass> subClassList = new ArrayList<>();
            for (int i = 0; i<arrayList.size(); i++) {
                if (arrayList.get(i).isChecked()) {
                    ModelClass.SessionList.SubClass subClass = new ModelClass.SessionList.SubClass(arrayList.get(i).getName(), arrayList.get(i).getCode());
                    subClassList.add(subClass);
                }
            }
            if (master.equalsIgnoreCase(Constants.DOCTOR)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDr().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDr(subClassList);
            }else if (master.equalsIgnoreCase(Constants.CHEMIST)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemist().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemist(subClassList);
            }else if (master.equalsIgnoreCase(Constants.UNLISTED_DOCTOR)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDr().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDr(subClassList);
            }else if (master.equalsIgnoreCase(Constants.CIP)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassList);
            }else if (master.equalsIgnoreCase(Constants.HOSPITAL)) {
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospital().clear();
                inputDataArray.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospital(subClassList);
            }

    }

}
