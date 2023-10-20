package saneforce.sanclm.activity.tourPlan.session;

import android.content.Context;
import android.database.Cursor;
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
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.activity.tourPlan.ModelClass;
import saneforce.sanclm.activity.tourPlan.TourPlanActivity;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class SessionEditAdapter extends RecyclerView.Adapter<SessionEditAdapter.MyViewHolder> {

    public static ModelClass inputData = new ModelClass();
    Context context;
    SQLite sqLite;
    ApiInterface apiInterface;
    SessionInterface sessionInterface;
    public SessionItemAdapter sessionItemAdapter = new SessionItemAdapter();
    String sfCode = "",division_code = "",sfType = "",designation = "",state_code ="",subdivision_code = "";
    static String jwNeed = "",drNeed = "",chemistNeed = "", stockiestNeed = "",unListedDrNeed = "",cipNeed = "" , hospNeed = "",FW_meetup_mandatory = "";
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    public int itemPosition ;

    public SessionEditAdapter () {
    }

    public SessionEditAdapter (ModelClass inputData, Context context, SessionInterface sessionInterface) {
        this.inputData = inputData;
        this.context = context;
        this.sessionInterface = sessionInterface;

        sqLite = new SQLite(context);
        Cursor cursor = sqLite.getLoginData();
        LoginResponse loginResponse = new LoginResponse();
        String loginData = "";
        if (cursor.moveToNext()){
            loginData = cursor.getString(0);
        }
        cursor.close();
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        loginResponse = new Gson().fromJson(loginData, type);

        sfCode = loginResponse.getSF_Code();
        division_code = loginResponse.getDivision_Code();
        subdivision_code = loginResponse.getSubdivision_code();
        designation = loginResponse.getDesig();
        state_code = loginResponse.getState_Code();
        sfType = loginResponse.getSf_type();
//        hq_code = SharedPref.getHqCode(context); // Selected HQ code in master sync ,it will be changed if any other HQ selected in Add Plan

        //Tour Plan setup
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.TP_PLAN);
            for (int i=0;i<jsonArray.length();i++){
                drNeed = jsonArray.getJSONObject(i).getString("DrNeed");
                chemistNeed = jsonArray.getJSONObject(i).getString("ChmNeed");
                jwNeed = jsonArray.getJSONObject(i).getString("JWNeed");
                stockiestNeed = jsonArray.getJSONObject(i).getString("StkNeed");
                unListedDrNeed = jsonArray.getJSONObject(i).getString("UnDrNeed");
                cipNeed = jsonArray.getJSONObject(i).getString("Cip_Need");
                hospNeed = jsonArray.getJSONObject(i).getString("HospNeed");
                FW_meetup_mandatory = jsonArray.getJSONObject(i).getString("FW_meetup_mandatory");

            }
        }catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

    @NonNull
    @Override
    public SessionEditAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_edit_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull SessionEditAdapter.MyViewHolder holder, int position) {

        holder.remarks.setImeOptions(EditorInfo.IME_ACTION_DONE);
        holder.remarks.setRawInputType(InputType.TYPE_CLASS_TEXT);
        holder.viewHolder = holder;
        holder.sessionData = inputData.getSessionList().get(holder.getAbsoluteAdapterPosition());
        holder.clusterModelArray = new ArrayList<>(holder.sessionData.getCluster());
        holder.jcModelArray = new ArrayList<>(holder.sessionData.getJC());
        holder.listedDrModelArray = new ArrayList<>(holder.sessionData.getListedDr());
        holder.chemistModelArray = new ArrayList<>(holder.sessionData.getChemist());
        holder.stockiestModelArray = new ArrayList<>(holder.sessionData.getStockiest());
        holder.unListedDrModelArray = new ArrayList<>(holder.sessionData.getUnListedDr());
        holder.cipModelArray = new ArrayList<>(holder.sessionData.getCip());
        holder.hospitalModelArray = new ArrayList<>(holder.sessionData.getHospital());

        if (holder.sessionData.getVisible()){
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else{
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        holder.sessionNoTxt.setText("Session " +  (position + 1));
        if(holder.getAbsoluteAdapterPosition() == 0){
            if (inputData.getSessionList().size() > 1){
                holder.sessionDelete.setVisibility(View.VISIBLE);
            }else{
                holder.sessionDelete.setVisibility(View.GONE);
            }
        }

        //work Type
        if (holder.sessionData.getWorkType().getName().equals("")){
            holder.workTypeField.setText("Select");
        }else{
            holder.workTypeField.setText(holder.sessionData.getWorkType().getName());
        }

        if (holder.sessionData.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")){ // Y - yes
            holder.hqNeed = "0"; // 0 - Yes
            holder.clusterNeed = "0";
        }else if (holder.sessionData.getWorkType().getTerrSlFlg().equalsIgnoreCase("N")){
            holder.hqNeed = "1"; // 1 - No
            holder.clusterNeed = "1";
        }
        workTypeBasedUI(holder,holder.sessionData,true);

        //HQ
        if (holder.sessionData.getHQ().getName().equals("")){
            holder.hqField.setText("Select");
        }else {
            holder.hqField.setText(holder.sessionData.getHQ().getName());
            holder.selectedHq = holder.sessionData.getHQ().getCode();
        }

        if (!holder.selectedHq.equals("")){
            getDataFromLocal(holder,holder.selectedHq);
        }

        //Cluster
        StringBuilder clusterName = new StringBuilder();
        for (int i=0;i<holder.clusterModelArray.size();i++){
            if (clusterName.length() == 0){
                clusterName = new StringBuilder(holder.clusterModelArray.get(i).getName());
            }else{
                clusterName.append(", ").append(holder.clusterModelArray.get(i).getName());
            }
        }
        if (clusterName.length() > 0){
            holder.clusterField.setText(clusterName);
        }
        prepareInputData(holder.clusterModelArray,holder.clusterJsonArray);

        holder.selectedClusterCode.clear();
        for (int i=0;i<holder.clusterModelArray.size();i++){
            holder.selectedClusterCode.add(holder.clusterModelArray.get(i).getCode());
        }

        //Joint Work
        StringBuilder jcName = new StringBuilder();
        for (int i=0;i<holder.jcModelArray.size();i++){
            if (jcName.length() == 0){
                jcName = new StringBuilder(holder.jcModelArray.get(i).getName());
            }else{
                jcName.append(", ").append(holder.jcModelArray.get(i).getName());
            }
        }
        if (jcName.length() > 0){
            holder.jcField.setText(jcName);
        }
        prepareInputData(holder.jcModelArray,holder.jointCallJsonArray);

        //Dr
        StringBuilder drName = new StringBuilder();
        for (int i = 0; i<holder.listedDrModelArray.size(); i++){
            if (drName.length() == 0){
                drName = new StringBuilder(holder.listedDrModelArray.get(i).getName());
            }else{
                drName.append(", ").append(holder.listedDrModelArray.get(i).getName());
            }
        }
        if (drName.length() > 0){
            holder.drField.setText(drName);
        }
        prepareInputData(holder.listedDrModelArray,holder.listedDrJsonArray);

        //Chemist
        StringBuilder chemistName = new StringBuilder();
        for (int i=0;i<holder.chemistModelArray.size();i++){
            if (chemistName.length() == 0){
                chemistName = new StringBuilder(holder.chemistModelArray.get(i).getName());
            }else{
                chemistName.append(", ").append(holder.chemistModelArray.get(i).getName());
            }
        }
        if (chemistName.length() > 0){
            holder.chemistField.setText(chemistName);
        }
        prepareInputData(holder.chemistModelArray,holder.chemistJsonArray);

        //Stockiest
        StringBuilder stockiestName = new StringBuilder();
        for (int i=0;i<holder.stockiestModelArray.size();i++){
            if (stockiestName.length() == 0){
                stockiestName = new StringBuilder(holder.stockiestModelArray.get(i).getName());
            }else{
                stockiestName.append(", ").append(holder.stockiestModelArray.get(i).getName());
            }
        }
        if (stockiestName.length() > 0){
            holder.stockiestField.setText(stockiestName);
        }
        prepareInputData(holder.stockiestModelArray,holder.stockiestJsonArray);

        //UnListed Doctor
        StringBuilder unListedDrName = new StringBuilder();
        for (int i=0;i<holder.unListedDrModelArray.size();i++){
            if (unListedDrName.length() == 0){
                unListedDrName = new StringBuilder(holder.unListedDrModelArray.get(i).getName());
            }else{
                unListedDrName.append(", ").append(holder.unListedDrModelArray.get(i).getName());
            }

        }
        if (unListedDrName.length() > 0){
            holder.unListedDrField.setText(unListedDrName);
        }
        prepareInputData(holder.unListedDrModelArray,holder.unListedDrJsonArray);

        //Cip
        StringBuilder cipName = new StringBuilder();
        for (int i=0;i<holder.cipModelArray.size();i++){
            if (cipName.length() == 0){
                cipName = new StringBuilder(holder.cipModelArray.get(i).getName());
            }else{
                cipName.append(",").append(holder.cipModelArray.get(i).getName());
            }

        }
        if (cipName.length() > 0){
            holder.cipField.setText(cipName);
        }
        prepareInputData(holder.cipModelArray,holder.cipJsonArray);

        //Hospital
        StringBuilder hospName = new StringBuilder();
        for (int i=0;i<holder.hospitalModelArray.size();i++){
            if (hospName.length() == 0){
                hospName = new StringBuilder(holder.hospitalModelArray.get(i).getName());
            }else{
                jcName.append(", ").append(holder.hospitalModelArray.get(i).getName());
            }
        }
        if (hospName.length() > 0){
            holder.hospField.setText(hospName);
        }
        prepareInputData(holder.hospitalModelArray,holder.hospJsonArray);
        holder.remarks.setText(holder.sessionData.getRemarks());

        holder.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged (CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    holder.searchClearIcon.setVisibility(View.VISIBLE);
                }else{
                    holder.searchClearIcon.setVisibility(View.GONE);
                }
                sessionItemAdapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged (Editable editable) {
                sessionItemAdapter.getFilter().filter(editable.toString());
            }
        });

        holder.searchClearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                holder.searchET.getText().clear();
            }
        });

        holder.workTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);
                if (!holder.fieldSelected){
                    if (holder.workTypeJsonArray.length() <= 0){
                        holder.workTypeJsonArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
                        addCheckBox(holder.workTypeJsonArray);
                    }
                    JSONArray filteredArray = new JSONArray();
                    try {
                        for(int i=0;i<holder.workTypeJsonArray.length();i++){
                            if (holder.workTypeJsonArray.getJSONObject(i).getString("TP_DCR").contains("T")){
                                filteredArray.put(holder.workTypeJsonArray.getJSONObject(i));
                            }
                        }
                    }catch (JSONException e){
                        throw new RuntimeException(e);
                    }
                    holder.sessionItemAdapterArray = sortArray(filteredArray);
                    populateSessionAdapter(holder,holder.sessionItemAdapterArray, false);
                    changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, false);
                    holder.fieldSelected = true;
                }else{
                    changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, true);
                    holder.fieldSelected = false;
                }
                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
            }
        });

        holder.hqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);
                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else{
                    if (!holder.fieldSelected){
                        if (holder.hqJsonArray.length() <= 0){
                            holder.hqJsonArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
                            addCheckBox(holder.hqJsonArray);
                        }
                        holder.sessionItemAdapterArray = sortArray(holder.hqJsonArray);
                        populateSessionAdapter(holder,holder.sessionItemAdapterArray, false);
                        changeUIState(holder, holder.hqLayout, holder.hqArrow, false);
                        holder.fieldSelected = true;
                    }else{
                        changeUIState(holder, holder.hqLayout, holder.hqArrow, true);
                        holder.fieldSelected = false;
                    }
                }
                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
            }
        });

        holder.clusterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);
                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else{
                    if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")){
                        Toast.makeText(context, "Select Head Quarters", Toast.LENGTH_SHORT).show();
                    } else{
                        if (!holder.fieldSelected){
                            if (holder.clusterJsonArray.length() <= 0){
                                holder.clusterJsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + holder.selectedHq );
                                addCheckBox(holder.clusterJsonArray);
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else{
                                setSelectedCount(holder, holder.clusterJsonArray, false, holder.clusterField, holder.clusterCount);
                            }
                            holder.fieldSelected = true;
                            holder.sessionItemAdapterArray = prepareSessionAdapterArray(holder.clusterJsonArray,holder.sessionItemAdapterArray);
                            populateSessionAdapter(holder,holder.sessionItemAdapterArray, true);
                            edit(holder.getAbsoluteAdapterPosition(),false,Constants.CLUSTER);
                        } else{
                            holder.fieldSelected = false;
                            setSelectedCount(holder, holder.clusterJsonArray, true, holder.clusterField, holder.clusterCount);
                            changeUIState(holder, holder.clusterLayout, holder.clusterArrow, true);
                            edit(holder.getAbsoluteAdapterPosition(),true,"");
                        }
                    }
                }
            }
        });

        holder.jcLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                } else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                }else{
                    if (!holder.fieldSelected){
                        if (holder.jointCallJsonArray.length() <= 0){
                            holder.jointCallJsonArray = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK + holder.selectedHq);
                            addCheckBox(holder.jointCallJsonArray);
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        } else{
                            setSelectedCount(holder, holder.jointCallJsonArray, false, holder.jcField, holder.jcCount);
                        }
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = prepareSessionAdapterArray(holder.jointCallJsonArray,holder.sessionItemAdapterArray);
                        populateSessionAdapter(holder,holder.sessionItemAdapterArray, true);
                        edit(holder.getAbsoluteAdapterPosition(),false,Constants.JOINT_WORK);
                    } else{
                        holder.fieldSelected = false;
                        setSelectedCount(holder, holder.jointCallJsonArray, true, holder.jcField, holder.jcCount);
                        changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
                        edit(holder.getAbsoluteAdapterPosition(),true,"");
                    }
                }

            }
        });

        holder.drLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                } else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                } else {
                    if (!holder.fieldSelected){
                        if (holder.listedDrJsonArray.length() <= 0){
                            holder.listedDrJsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + holder.selectedHq);
                            addCheckBox(holder.listedDrJsonArray);
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        } else{
                            setSelectedCount(holder,holder.listedDrJsonArray,false,holder.drField,holder.drCount);
                        }
                        JSONArray filteredArray = filterJsonArray(holder,holder.listedDrJsonArray);
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = prepareSessionAdapterArray(filteredArray,holder.sessionItemAdapterArray);
                        populateSessionAdapter(holder,holder.sessionItemAdapterArray, true);
                        edit(holder.getAbsoluteAdapterPosition(),false,Constants.DOCTOR);
                    } else{
                        holder.fieldSelected = false;
                        setSelectedCount(holder,holder.listedDrJsonArray,true,holder.drField,holder.drCount);
                        changeUIState(holder, holder.drLayout, holder.drArrow, true);
                        edit(holder.getAbsoluteAdapterPosition(),true,"");
                    }
                }
            }
        });

        holder.chemistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                } else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                } else {

                    if (!holder.fieldSelected){
                        if (holder.chemistJsonArray.length() <= 0){
                            holder.chemistJsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + holder.selectedHq);
                            addCheckBox(holder.chemistJsonArray);
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        } else{
                            setSelectedCount(holder,holder.chemistJsonArray,false,holder.chemistField,holder.chemistCount);
                        }

                        JSONArray filteredArray = filterJsonArray(holder,holder.chemistJsonArray);
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = prepareSessionAdapterArray(filteredArray,holder.sessionItemAdapterArray);
                        populateSessionAdapter(holder,holder.sessionItemAdapterArray, true);
                        edit(holder.getAbsoluteAdapterPosition(),false,Constants.CHEMIST);
                    } else{
                        holder.fieldSelected = false;
                        setSelectedCount(holder,holder.chemistJsonArray,true,holder.chemistField,holder.chemistCount);
                        changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
                        edit(holder.getAbsoluteAdapterPosition(),true,"");
                    }
                }
            }
        });

        holder.stockiestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                } else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                } else {
                    if (!holder.fieldSelected){
                        if (holder.stockiestJsonArray.length() <= 0){
                            holder.stockiestJsonArray = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + holder.selectedHq);
                            addCheckBox(holder.stockiestJsonArray);
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        } else{
                            setSelectedCount(holder,holder.stockiestJsonArray,false,holder.stockiestField,holder.stockiestCount);
                        }
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = prepareSessionAdapterArray(holder.stockiestJsonArray,holder.sessionItemAdapterArray);
                        populateSessionAdapter(holder,holder.sessionItemAdapterArray, true);
                        edit(holder.getAbsoluteAdapterPosition(),false,Constants.STOCKIEST);
                    } else{
                        holder.fieldSelected = false;
                        setSelectedCount(holder,holder.stockiestJsonArray,true,holder.stockiestField,holder.stockiestCount);
                        changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
                        edit(holder.getAbsoluteAdapterPosition(),true,"");
                    }

                }
            }
        });

        holder.unListedDrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                } else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                } else {
                    if (!holder.fieldSelected){
                        if (holder.unListedDrJsonArray.length() <= 0){
                            holder.unListedDrJsonArray = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + holder.selectedHq);
                            addCheckBox(holder.unListedDrJsonArray);
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        } else{
                            setSelectedCount(holder,holder.unListedDrJsonArray,false,holder.unListedDrField,holder.unListedDrCount);
                        }
                        JSONArray filteredArray = filterJsonArray(holder,holder.unListedDrJsonArray);
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = prepareSessionAdapterArray(filteredArray,holder.sessionItemAdapterArray);
                        populateSessionAdapter(holder,holder.sessionItemAdapterArray, true);
                        edit(holder.getAbsoluteAdapterPosition(),false,Constants.UNLISTED_DOCTOR);
                    } else{
                        holder.fieldSelected = false;
                        setSelectedCount(holder,holder.unListedDrJsonArray,true,holder.unListedDrField,holder.unListedDrCount);
                        changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, true);
                        edit(holder.getAbsoluteAdapterPosition(),true,"");
                    }

                }
            }
        });

        holder.cipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                } else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                } else {
                    if (!holder.fieldSelected){
                        if (holder.cipJsonArray.length() <= 0){
                            holder.cipJsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + holder.selectedHq);
                            addCheckBox(holder.cipJsonArray);
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        } else{
                            setSelectedCount(holder,holder.cipJsonArray,false,holder.cipField,holder.cipCount);
                        }
                        JSONArray filteredArray = filterJsonArray(holder,holder.cipJsonArray);
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = prepareSessionAdapterArray(filteredArray,holder.sessionItemAdapterArray);
                        populateSessionAdapter(holder,holder.sessionItemAdapterArray, true);
                        edit(holder.getAbsoluteAdapterPosition(),false,Constants.CIP);
                    } else{
                        holder.fieldSelected = false;
                        setSelectedCount(holder,holder.cipJsonArray,true,holder.cipField,holder.cipCount);
                        changeUIState(holder, holder.cipLayout, holder.cipArrow, true);
                        edit(holder.getAbsoluteAdapterPosition(),true,"");
                    }
                }
            }
        });

        holder.hospLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                itemPosition = holder.getLayoutPosition();
                holder.relativeLayout.setSelected(false);

                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Head Quarter", Toast.LENGTH_SHORT).show();
                } else if (holder.clusterField.getText().toString().equalsIgnoreCase("Select")) {
                    Toast.makeText(context, "Select Clusters", Toast.LENGTH_SHORT).show();
                } else {
                    if (!holder.fieldSelected){
                        if (holder.hospJsonArray.length() <= 0){
                            holder.hospJsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + holder.selectedHq);
                            addCheckBox(holder.hospJsonArray);
                            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                        } else{
                            setSelectedCount(holder,holder.hospJsonArray,false,holder.hospField,holder.hospCount);
                        }
                        JSONArray filteredArray = filterJsonArray(holder,holder.hospJsonArray);
                        holder.fieldSelected = true;
                        holder.sessionItemAdapterArray = prepareSessionAdapterArray(filteredArray,holder.sessionItemAdapterArray);
                        populateSessionAdapter(holder,holder.sessionItemAdapterArray, true);
                        edit(holder.getAbsoluteAdapterPosition(),false,Constants.HOSPITAL);
                    } else{
                        holder.fieldSelected = false;
                        setSelectedCount(holder,holder.hospJsonArray,true,holder.hospField,holder.hospCount);
                        changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
                        edit(holder.getAbsoluteAdapterPosition(),true,"");
                    }
                }
            }
        });

        holder.remarks.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (holder.remarks.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                }
                return false;
            }
        });

        holder.remarks.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction (TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                   holder.sessionData.setRemarks(holder.remarks.getText().toString());
                }
                return false;
            }
        });

        holder.remarks.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange (View view, boolean b) {
                if (!b){
                    holder.sessionData.setRemarks(holder.remarks.getText().toString());
                }
            }
        });

        holder.sessionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sessionInterface.deleteClicked(inputData, holder.getAbsoluteAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount () {
        return inputData.getSessionList().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView sessionNoTxt;
        public ImageView searchClearIcon;
        public TextView workTypeField,hqField,clusterField,jcField,drField,chemistField,stockiestField,unListedDrField,cipField,hospField;
        public TextView clusterCount,jcCount,drCount,chemistCount,stockiestCount,unListedDrCount,cipCount,hospCount;
        public LinearLayout sessionDelete,workTypeLayout,hqLayout,clusterLayout,jcLayout,drLayout,chemistLayout,stockiestLayout,unListedDrLayout,cipLayout,hospLayout,remarksLayout;
        public ImageView workTypeArrow,hqArrow,clusterArrow,jcArrow,drArrow,chemistArrow,stockiestArrow,unListedDrArrow,cipArrow,hospArrow;
        EditText searchET,remarks;
        RelativeLayout relativeLayout;
        CardView parentCarView,listCardView;
        RecyclerView itemRecView;
        boolean fieldSelected = false;
        MyViewHolder viewHolder;


        //Input data
        ArrayList<ModelClass.SessionList.SubClass> clusterModelArray ;
        ArrayList<ModelClass.SessionList.SubClass> jcModelArray ;
        ArrayList<ModelClass.SessionList.SubClass> listedDrModelArray;
        ArrayList<ModelClass.SessionList.SubClass> chemistModelArray;
        ArrayList<ModelClass.SessionList.SubClass> stockiestModelArray ;
        ArrayList<ModelClass.SessionList.SubClass> unListedDrModelArray ;
        ArrayList<ModelClass.SessionList.SubClass> cipModelArray ;
        ArrayList<ModelClass.SessionList.SubClass> hospitalModelArray ;

        public ModelClass.SessionList sessionData = new ModelClass.SessionList();

        // Data from local storage
        public JSONArray workTypeJsonArray = new JSONArray();
        public JSONArray hqJsonArray = new JSONArray();
        public JSONArray clusterJsonArray = new JSONArray();
        public JSONArray jointCallJsonArray = new JSONArray();
        public JSONArray listedDrJsonArray = new JSONArray();
        public JSONArray chemistJsonArray = new JSONArray();
        public JSONArray stockiestJsonArray = new JSONArray();
        public JSONArray unListedDrJsonArray = new JSONArray();
        public JSONArray cipJsonArray = new JSONArray();
        public JSONArray hospJsonArray = new JSONArray();
        public JSONArray sessionItemAdapterArray = new JSONArray();

        String hq_code ="",selectedHq = "",hqNeed = "",clusterNeed = "";
        ArrayList<String> selectedClusterCode = new ArrayList<>();

        public MyViewHolder (@NonNull View itemView) {
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

        }
    }

    public void workTypeBasedUI (MyViewHolder holder,ModelClass.SessionList session,boolean bool){

        String workType = session.getWorkType().getFWFlg();

        switch (workType){
            case "F" : {
                if (holder.hqNeed.equalsIgnoreCase("0")){
                    holder.hqLayout.setVisibility(View.VISIBLE);
                }else if (holder.hqNeed.equalsIgnoreCase("1")){
                    holder.hqLayout.setVisibility(View.GONE);
                }

                if (holder.clusterNeed.equalsIgnoreCase("0")){
                    holder.clusterLayout.setVisibility(View.VISIBLE);
                }else if (holder.clusterNeed.equalsIgnoreCase("1")){
                    holder.clusterLayout.setVisibility(View.GONE);
                }

                if (jwNeed.equalsIgnoreCase("0")){
                    holder.jcLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.jcLayout.setVisibility(View.GONE);
                }

                if (drNeed.equalsIgnoreCase("0")){
                    holder.drLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.drLayout.setVisibility(View.GONE);
                }

                if (chemistNeed.equalsIgnoreCase("0")){
                    holder.chemistLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.chemistLayout.setVisibility(View.GONE);
                }

                if (stockiestNeed.equalsIgnoreCase("0")){
                    holder.stockiestLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.stockiestLayout.setVisibility(View.GONE);
                }

                if (unListedDrNeed.equalsIgnoreCase("0")){
                    holder.unListedDrLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.unListedDrLayout.setVisibility(View.GONE);
                }

                if (cipNeed.equalsIgnoreCase("0")){
                    holder.cipLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.cipLayout.setVisibility(View.GONE);
                }

                if (hospNeed.equalsIgnoreCase("0")){
                    holder.hospLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.hospLayout.setVisibility(View.GONE);
                }

                break;
            }
            case "W" :
            case "H" : {
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
            case "N" :{
                if (holder.hqNeed.equalsIgnoreCase("0")){
                    holder.hqLayout.setVisibility(View.VISIBLE);
                }else if (holder.hqNeed.equalsIgnoreCase("1")){
                    holder.hqLayout.setVisibility(View.GONE);
                }

                if (holder.clusterNeed.equalsIgnoreCase("0")){
                    holder.clusterLayout.setVisibility(View.VISIBLE);
                }else if (holder.clusterNeed.equalsIgnoreCase("1")){
                    holder.clusterLayout.setVisibility(View.GONE);
                }

                if (jwNeed.equalsIgnoreCase("0")){
                    holder.jcLayout.setVisibility(View.VISIBLE);
                }else{
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
            default: {
                if (holder.hqNeed.equalsIgnoreCase("0")){
                    holder.hqLayout.setVisibility(View.VISIBLE);
                }else if (holder.hqNeed.equalsIgnoreCase("1")){
                    holder.hqLayout.setVisibility(View.GONE);
                }

                if (holder.clusterNeed.equalsIgnoreCase("0")){
                    holder.clusterLayout.setVisibility(View.VISIBLE);
                }else if (holder.clusterNeed.equalsIgnoreCase("1")){
                    holder.clusterLayout.setVisibility(View.GONE);
                }

                if (jwNeed.equalsIgnoreCase("0")){
                    holder.jcLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.jcLayout.setVisibility(View.GONE);
                }

                if (drNeed.equalsIgnoreCase("0")){
                    holder.drLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.drLayout.setVisibility(View.GONE);
                }

                if (chemistNeed.equalsIgnoreCase("0")){
                    holder.chemistLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.chemistLayout.setVisibility(View.GONE);
                }

                if (stockiestNeed.equalsIgnoreCase("0")){
                    holder.stockiestLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.stockiestLayout.setVisibility(View.GONE);
                }

                if (unListedDrNeed.equalsIgnoreCase("0")){
                    holder.unListedDrLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.unListedDrLayout.setVisibility(View.GONE);
                }

                if (cipNeed.equalsIgnoreCase("0")){
                    holder.cipLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.cipLayout.setVisibility(View.GONE);
                }

                if (hospNeed.equalsIgnoreCase("0")){
                    holder.hospLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.hospLayout.setVisibility(View.GONE);
                }
            }
        }

        if (bool){
            switch (session.getLayoutVisible()){
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

    public void getDataFromLocal(MyViewHolder holder, String hqCode){

        holder.clusterJsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hqCode);
        if (holder.clusterJsonArray.length() <= 0){
            prepareMasterToSync(hqCode);
        }

        holder.clusterJsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hqCode);
        holder.jointCallJsonArray = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK + hqCode);
        holder.listedDrJsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + hqCode);
        holder.chemistJsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + hqCode);
        holder.stockiestJsonArray = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + hqCode);
        holder.unListedDrJsonArray = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + hqCode);
        holder.cipJsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + hqCode);
        holder.hospJsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + hqCode);

        addCheckBox(holder.clusterJsonArray);
        addCheckBox(holder.jointCallJsonArray);
        addCheckBox(holder.listedDrJsonArray);
        addCheckBox(holder.chemistJsonArray);
        addCheckBox(holder.stockiestJsonArray);
        addCheckBox(holder.unListedDrJsonArray);
        addCheckBox(holder.cipJsonArray);
        addCheckBox(holder.hospJsonArray);

    }

    public void addCheckBox(JSONArray jsonArray)  {

        try {
            for (int i=0;i<jsonArray.length();i++){
                jsonArray.getJSONObject(i).put("isChecked",false);
                if (jsonArray.getJSONObject(i).has("name")){
                    jsonArray.getJSONObject(i).put("Name",jsonArray.getJSONObject(i).getString("name"));
                    jsonArray.getJSONObject(i).remove("name");
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void prepareMasterToSync(String hqCode){
        masterSyncArray.clear();
        MasterSyncItemModel doctorModel = new MasterSyncItemModel("Doctor","getdoctors", Constants.DOCTOR + hqCode);
        MasterSyncItemModel cheModel = new MasterSyncItemModel("Doctor","getchemist",Constants.CHEMIST + hqCode);
        MasterSyncItemModel stockModel = new MasterSyncItemModel("Doctor","getstockist",Constants.STOCKIEST + hqCode);
        MasterSyncItemModel unListModel = new MasterSyncItemModel("Doctor","getunlisteddr",Constants.UNLISTED_DOCTOR + hqCode);
        MasterSyncItemModel hospModel = new MasterSyncItemModel("Doctor","gethospital",Constants.HOSPITAL + hqCode);
        MasterSyncItemModel ciModel = new MasterSyncItemModel("Doctor","getcip",Constants.CIP + hqCode);
        MasterSyncItemModel cluster = new MasterSyncItemModel("Doctor","getterritory",Constants.CLUSTER + hqCode);
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel("Subordinate","getjointwork",Constants.JOINT_WORK + hqCode);

        masterSyncArray.add(doctorModel);
        masterSyncArray.add(cheModel);
        masterSyncArray.add(stockModel);
        masterSyncArray.add(unListModel);
        masterSyncArray.add(hospModel);
        masterSyncArray.add(ciModel);
        masterSyncArray.add(cluster);
        masterSyncArray.add(jWorkModel);
        for (int i=0;i<masterSyncArray.size();i++){
            sync(masterSyncArray.get(i),hqCode);
        }
    }

    public void sync(MasterSyncItemModel masterSyncItemModel ,String hqCode)  {

        if (UtilityClass.isNetworkAvailable(context)){
            try {
                String baseUrl = SharedPref.getBaseWebUrl(context);
                String pathUrl = SharedPref.getPhpPathUrl(context);
                String replacedUrl = pathUrl.replaceAll("\\?.*","/");
                apiInterface = RetrofitClient.getRetrofit(context, baseUrl+replacedUrl);

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
                if (masterSyncItemModel.getMasterFor().equalsIgnoreCase("Doctor")){
                    call = apiInterface.getDrMaster(jsonObject.toString());
                } else if (masterSyncItemModel.getMasterFor().equalsIgnoreCase("Subordinate")) {
                    call = apiInterface.getSubordinateMaster(jsonObject.toString());
                }

                if (call != null){
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse (@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                            boolean success = false;
                            if (response.isSuccessful()) {
//                                Log.e("test","response : " + masterSyncItemModel.getRemoteTableName() +" : " + response.body().toString());
                                try {
                                    JsonElement jsonElement = response.body();
                                    JSONArray jsonArray = new JSONArray();
                                    if (!jsonElement.isJsonNull()){
                                        if (jsonElement.isJsonArray()){
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                            if (!jsonObject1.has("success")){ // json object with "success" : "fail" will be received only when api call is failed ,"success will not be received when api call is success
                                                jsonArray.put(jsonObject1);
                                                success = true;
                                            } else if (jsonObject1.has("success") && !jsonObject1.getBoolean("success")) {
                                                sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(),1);
                                            }
                                        }

                                        if (success){
                                            sqLite.saveMasterSyncData(masterSyncItemModel.getLocalTableKeyName(),jsonArray.toString(),0);
                                        }
                                    } else {
                                        sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(),1);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        @Override
                        public void onFailure (@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
                            sqLite.saveMasterSyncStatus(masterSyncItemModel.getLocalTableKeyName(),1);
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    public void prepareInputData(ArrayList<ModelClass.SessionList.SubClass> modelClass,JSONArray jsonArray){

        try {
            if (modelClass.size() > 0){
                for (int i=0;i<modelClass.size();i++){
                    for (int j=0;j<jsonArray.length();j++){
                        if (modelClass.get(i).getCode().equalsIgnoreCase(jsonArray.getJSONObject(j).getString("Code"))){
                            jsonArray.getJSONObject(j).put("isChecked",true);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray prepareSessionAdapterArray(JSONArray jsonArray,JSONArray jsonArray1){

         jsonArray1 = new JSONArray();
        try {
            for (int i=0;i<jsonArray.length();i++){
                Object object = jsonArray.get(i);
                if (object instanceof JSONObject){
                    JSONObject jsonObject = new JSONObject(((JSONObject) object).toString());
                    jsonArray1.put(jsonObject);
                }else{
                    jsonArray1.put(object);
                }
            }

        } catch (JSONException e){
            throw new RuntimeException(e);
        }
        return sortArray(jsonArray1);
    }

    public JSONArray sortArray(JSONArray jsonArray1){

        try {
            JsonArray jsonArray = new JsonParser().parse(String.valueOf(jsonArray1)).getAsJsonArray();
            List<JsonElement> list = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                list.add(element);
            }
            Collections.sort(list, new Comparator<JsonElement>() {
                @Override
                public int compare(JsonElement e1, JsonElement e2) {
                    String v1 = e1.getAsJsonObject().get("Name").getAsString();
                    String v2 = e2.getAsJsonObject().get("Name").getAsString();
                    return v1.compareTo(v2);
                }
            });

            // Convert the sorted List back into a JsonArray
            JsonArray sortedJsonArray = new JsonArray();
            for (JsonElement element : list) {
                sortedJsonArray.add(element);
            }

            return new JSONArray(sortedJsonArray.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateSessionAdapter(MyViewHolder holder, JSONArray jsonArray, boolean checkBoxNeed){

        sessionItemAdapter = new SessionItemAdapter(holder.sessionItemAdapterArray, checkBoxNeed, new SessionItemInterface() {
            @Override
            public void itemClicked (JSONArray jsonArray, JSONObject jsonObject) {
                try {
                    if (holder.workTypeLayout.getVisibility() == View.VISIBLE){
                        boolean workTypeRepeated= false;
                        if (inputData.getSessionList().size() > 1){
                            for (int i = 0; i< inputData.getSessionList().size(); i++){
                                if (i != holder.getAbsoluteAdapterPosition()){
                                    if (inputData.getSessionList().get(i).getWorkType().getFWFlg().equalsIgnoreCase(jsonObject.getString("FWFlg"))){
                                        switch (jsonObject.getString("FWFlg").toUpperCase()){
                                            case "W" :
                                            case "H" :{
                                                workTypeRepeated = true;
                                                Toast.makeText(context, "Work Type already been selected for session " + (i + 1) , Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                            case "F" :{
                                                if (!sfType.equalsIgnoreCase("2")){
                                                    workTypeRepeated = true;
                                                    Toast.makeText(context, "Work Type already been selected for session " + (i + 1) , Toast.LENGTH_SHORT).show();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!workTypeRepeated){
//                            String workType = holder.workTypeField.getText().toString();
//                            if (!workType.equalsIgnoreCase(jsonObject.getString("Name"))){
                                holder.workTypeField.setText(jsonObject.getString("Name"));
                                holder.sessionData.getWorkType().setName(jsonObject.getString("Name"));
                                holder.sessionData.getWorkType().setCode(jsonObject.getString("Code"));
                                holder.sessionData.getWorkType().setFWFlg(jsonObject.getString("FWFlg"));
                                holder.sessionData.getWorkType().setTerrSlFlg(jsonObject.getString("TerrSlFlg"));

                                sessionInterface.fieldWorkSelected(inputData, holder.getAbsoluteAdapterPosition());

//                            }else{
//                                Toast.makeText(context, "same work type", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }
                    else if (holder.hqLayout.getVisibility() == View.VISIBLE){

                        boolean hqRepeated = false;
                        if (inputData.getSessionList().size() > 1){
                            for (int i = 0; i< inputData.getSessionList().size(); i++){
                                ModelClass.SessionList modelClass = inputData.getSessionList().get(i);
                                if (i != holder.getAbsoluteAdapterPosition()){
                                    if (modelClass.getWorkType().getFWFlg().equalsIgnoreCase(inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getWorkType().getFWFlg())){
                                        if (modelClass.getHQ().getCode().equalsIgnoreCase(jsonObject.getString("Code"))){
                                            hqRepeated = true;
                                            Toast.makeText(context, "HQ already been selected for the same work type for session " + (i + 1) , Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (!hqRepeated){
                            holder.sessionData.getHQ().setName(jsonObject.getString("Name"));
                            holder.sessionData.getHQ().setCode(jsonObject.getString("Code"));

                            holder.hq_code = jsonObject.getString("id");
                            if (!holder.selectedHq.equalsIgnoreCase(holder.hq_code)){
                                holder.selectedHq = holder.hq_code;
                                sessionInterface.hqChanged(inputData, itemPosition, true);
                            }else{
                                sessionInterface.hqChanged(inputData, itemPosition, false);
                            }
                        }
                    }
                    else if (holder.clusterLayout.getVisibility() == View.VISIBLE){
                        int count= 0;
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getBoolean("isChecked")){
                                count++;
                            }
                        }

                        if (count > 0){
                            holder.clusterField.setText("Selected");
                            holder.clusterCount.setVisibility(View.VISIBLE);
                            holder.clusterCount.setText(String.valueOf(count));
                        }else{
                            holder.clusterField.setText("Select");
                            holder.clusterCount.setVisibility(View.GONE);
                        }
                    }
                    else if (holder.jcLayout.getVisibility() == View.VISIBLE){
                        int count= 0;
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getBoolean("isChecked")){
                                count++;
                            }
                        }

                        if (count > 0){
                            holder.jcField.setText("Selected");
                            holder.jcCount.setVisibility(View.VISIBLE);
                            holder.jcCount.setText(String.valueOf(count));
                        }else{
                            holder.jcField.setText("Select");
                            holder.jcCount.setVisibility(View.GONE);
                        }
                    }
                    else if (holder.drLayout.getVisibility() == View.VISIBLE){
                        int count= 0;
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getBoolean("isChecked")){
                                count++;
                            }
                        }
                        if (count > 0){
                            holder.drField.setText("Selected");
                            holder.drCount.setVisibility(View.VISIBLE);
                            holder.drCount.setText(String.valueOf(count));
                        }else{
                            holder.drField.setText("Select");
                            holder.drCount.setVisibility(View.GONE);
                        }
                    }
                    else if (holder.chemistLayout.getVisibility() == View.VISIBLE){
                        int count= 0;
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getBoolean("isChecked")){
                                count++;
                            }
                        }
                        if (count > 0){
                            holder.chemistField.setText("Selected");
                            holder.chemistCount.setVisibility(View.VISIBLE);
                            holder.chemistCount.setText(String.valueOf(count));
                        }else{
                            holder.chemistField.setText("Select");
                            holder.chemistCount.setVisibility(View.GONE);
                        }
                    }
                    else if (holder.stockiestLayout.getVisibility() == View.VISIBLE){
                        int count= 0;
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getBoolean("isChecked")){
                                count++;
                            }
                        }
                        if (count > 0){
                            holder.stockiestField.setText("Selected");
                            holder.stockiestCount.setVisibility(View.VISIBLE);
                            holder.stockiestCount.setText(String.valueOf(count));
                        }else{
                            holder.stockiestField.setText("Select");
                            holder.stockiestCount.setVisibility(View.GONE);
                        }
                    }
                    else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE){
                        int count= 0;
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getBoolean("isChecked")){
                                count++;
                            }
                        }
                        if (count > 0){
                            holder.unListedDrField.setText("Selected");
                            holder.unListedDrCount.setVisibility(View.VISIBLE);
                            holder.unListedDrCount.setText(String.valueOf(count));
                        }else{
                            holder.unListedDrField.setText("Select");
                            holder.unListedDrCount.setVisibility(View.GONE);
                        }
                    }
                    else if (holder.cipLayout.getVisibility() == View.VISIBLE){
                        int count= 0;
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getBoolean("isChecked")){
                                count++;
                            }
                        }
                        if (count > 0){
                            holder.cipField.setText("Selected");
                            holder.cipCount.setVisibility(View.VISIBLE);
                            holder.cipCount.setText(String.valueOf(count));
                        }else{
                            holder.cipField.setText("Select");
                            holder.cipCount.setVisibility(View.GONE);
                        }
                    }
                    else if (holder.hospLayout.getVisibility() == View.VISIBLE){
                        int count= 0;
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getBoolean("isChecked")){
                                count++;
                            }
                        }
                        if (count > 0){
                            holder.hospField.setText("Selected");
                            holder.hospCount.setVisibility(View.VISIBLE);
                            holder.hospCount.setText(String.valueOf(count));
                        }else{
                            holder.hospField.setText("Select");
                            holder.hospCount.setVisibility(View.GONE);
                        }
                    }

                } catch (JSONException e){
                    throw new RuntimeException(e);
                }

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.itemRecView.setLayoutManager(layoutManager);
        holder.itemRecView.setAdapter(sessionItemAdapter);
        sessionItemAdapter.notifyDataSetChanged();

    }

    public void changeUIState (MyViewHolder holder, LinearLayout linearLayout, ImageView imageView, boolean allLayoutVisible){

        if (allLayoutVisible){
            holder.workTypeLayout.setVisibility(View.VISIBLE);
            holder.remarksLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.down_arrow));
            holder.listCardView.setVisibility(View.GONE);
            TourPlanActivity.addSaveBtnLayout.setVisibility(View.VISIBLE);
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);

            workTypeBasedUI(holder,holder.sessionData,false);
        } else{
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
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
        }

    }

    public static void setSelectedCount(MyViewHolder holder, JSONArray jsonArray, boolean selectState, TextView selectedNameTxtView, TextView countTxt){

        try {
            if (!selectState){ // if its false we can show the text as "Selected" with count else just "Select" .if its true we need to show the selected item name in the label.
                int count= 0;
                for (int i = 0; i< jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getBoolean("isChecked")){
                        count++;
                    }
                }

                if (count > 0){
                    selectedNameTxtView.setText("Selected");
                    countTxt.setVisibility(View.VISIBLE);
                    countTxt.setText(String.valueOf(count));
                }else{
                    selectedNameTxtView.setText("Select");
                    countTxt.setVisibility(View.GONE);
                }
                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                holder.fieldSelected = true;
            }else{
                StringBuilder text = new StringBuilder();
                for (int i = 0; i< jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getBoolean("isChecked")){
                        if (text.length() == 0){
                            text = new StringBuilder(jsonObject.getString("Name"));
                        }else{
                            text.append(",").append(jsonObject.getString("Name"));
                        }
                    }
                }
                if (text.length() == 0){
                    selectedNameTxtView.setText("Select");
                }else{
                    selectedNameTxtView.setText(text);
                }
                countTxt.setVisibility(View.GONE);
                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);
                holder.fieldSelected = false;


                List<ModelClass.SessionList.SubClass> subClassList = new ArrayList<>();
                for (int i = 0; i< jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getBoolean("isChecked")){
                        ModelClass.SessionList.SubClass subClass = new ModelClass.SessionList.SubClass(jsonObject.getString("Name"),jsonObject.getString("Code"));
                        subClassList.add(subClass);
                    }
                }

                if (holder.clusterLayout.getVisibility() == View.VISIBLE){
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCluster().clear();
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCluster(subClassList);
                }
                else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getJC().clear();
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setJC(subClassList);
                }
                else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDr().clear();
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDr(subClassList);
                }
                else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemist().clear();
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemist(subClassList);
                }
                else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getStockiest().clear();
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setStockiest(subClassList);
                }
                else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDr().clear();
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDr(subClassList);
                }
                else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassList);
                }
                else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospital().clear();
                    inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospital(subClassList);
                }
            }
        } catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

    public JSONArray filterJsonArray(MyViewHolder holder, JSONArray jsonArray){ // Filters based on selected cluster
        JSONArray jsonArray1 = new JSONArray();
        try {
            for (int i=0;i<holder.selectedClusterCode.size();i++){
                for (int j=0;j<jsonArray.length();j++){
                    if (holder.selectedClusterCode.get(i).equalsIgnoreCase(jsonArray.getJSONObject(j).getString("Town_Code"))) { // filtering based on selected Cluster code
                        jsonArray1.put(jsonArray.get(j));
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonArray1;
    }

    public void edit(int position,boolean visibility,String layoutVisible){

//        if (arrayList.size() > 1){
            for (int i = 0; i< inputData.getSessionList().size(); i++){
                if (i != position){
                    inputData.getSessionList().get(i).setVisible(visibility);
                } else{
                    inputData.getSessionList().get(i).setLayoutVisible(layoutVisible);
                }
            }
            notifyDataSetChanged();
            TourPlanActivity activity = (TourPlanActivity) context;
            activity.scrollToPosition(position,false);
//        }
    }

    public void clearCheckBox (MyViewHolder holder){

        if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
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

    public void clearSelectedItem (MyViewHolder holder, TextView labelTxt, TextView countTxt){
        try {
            for (int i = 0; i< holder.sessionItemAdapterArray.length(); i++){
                holder.sessionItemAdapterArray.getJSONObject(i).put("isChecked", false);
            }
            labelTxt.setText("Select");
            countTxt.setVisibility(View.GONE);
            sessionItemAdapter.notifyDataSetChanged();
        }catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

    public void saveCheckedItem (MyViewHolder holder){

        try{
            if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
                holder.selectedClusterCode.clear();
                for (int i = 0; i< holder.sessionItemAdapterArray.length(); i++){
                    JSONObject jsonObject = holder.sessionItemAdapterArray.getJSONObject(i);
                    if (jsonObject.getBoolean("isChecked")){
                        holder.selectedClusterCode.add(jsonObject.getString("Code"));
                    }
                }

                clusterChanged(holder.selectedClusterCode,holder.listedDrJsonArray,holder.drField,Constants.DOCTOR,holder);
                clusterChanged(holder.selectedClusterCode,holder.chemistJsonArray,holder.chemistField,Constants.CHEMIST,holder);
                clusterChanged(holder.selectedClusterCode,holder.stockiestJsonArray,holder.stockiestField,Constants.STOCKIEST,holder);
                clusterChanged(holder.selectedClusterCode,holder.unListedDrJsonArray,holder.unListedDrField,Constants.UNLISTED_DOCTOR,holder);
                clusterChanged(holder.selectedClusterCode,holder.cipJsonArray,holder.cipField,Constants.CIP,holder);
                clusterChanged(holder.selectedClusterCode,holder.hospJsonArray,holder.hospField,Constants.HOSPITAL,holder);

                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.clusterField, holder.clusterCount);
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setLayoutVisible("");
                sessionInterface.clusterChanged(inputData, holder.getLayoutPosition());
            }
            else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.jcField, holder.jcCount);
                changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
            }
            else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder,holder.sessionItemAdapterArray, true, holder.drField, holder.drCount);
                changeUIState(holder, holder.drLayout, holder.drArrow, true);
            }
            else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.chemistField, holder.chemistCount);
                changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
            }
            else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.stockiestField, holder.stockiestCount);
                changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
            }
            else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.unListedDrField, holder.unListedDrCount);
                changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, true);
            }
            else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.cipField, holder.cipCount);
                changeUIState(holder, holder.cipLayout, holder.cipArrow, true);
            }
            else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                setSelectedCount(holder, holder.sessionItemAdapterArray, true, holder.hospField, holder.hospCount);
                changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
            }

        } catch (JSONException e){
            throw new RuntimeException(e);
        }

        edit(holder.getAbsoluteAdapterPosition(), true,"");

    }

    public void clusterChanged(ArrayList<String> clusterCodes,JSONArray jsonArray,TextView label,String master,MyViewHolder holder){

        try {
            for (int i=0;i<jsonArray.length();i++) {
                if (jsonArray.getJSONObject(i).getBoolean("isChecked")){
                    boolean matched = false;
                    for (int j = 0; j < clusterCodes.size(); j++) {
                        if (clusterCodes.get(j).equalsIgnoreCase(jsonArray.getJSONObject(i).getString("Town_Code"))) {
                            matched = true;
                        }
                    }
                    if (!matched){
                        jsonArray.getJSONObject(i).put("isChecked", false);
                    }
                }
            }

            StringBuilder text = new StringBuilder();
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getBoolean("isChecked")){
                    if (text.length() == 0){
                        text = new StringBuilder(jsonObject.getString("Name"));
                    }else{
                        text.append(",").append(jsonObject.getString("Name"));
                    }
                }
            }
            if (text.length() == 0){
                label.setText("Select");
            }else{
                label.setText(text);
            }

            List<ModelClass.SessionList.SubClass> subClassList = new ArrayList<>();
            for (int i = 0; i< jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getBoolean("isChecked")){
                    ModelClass.SessionList.SubClass subClass = new ModelClass.SessionList.SubClass(jsonObject.getString("Name"),jsonObject.getString("Code"));
                    subClassList.add(subClass);
                }
            }
            if (master.equalsIgnoreCase(Constants.DOCTOR)) {
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDr().clear();
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDr(subClassList);
            } else if (master.equalsIgnoreCase(Constants.CHEMIST)) {
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemist().clear();
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemist(subClassList);
            } else if (master.equalsIgnoreCase(Constants.UNLISTED_DOCTOR)) {
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDr().clear();
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDr(subClassList);
            } else if (master.equalsIgnoreCase(Constants.CIP)) {
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassList);
            } else if (master.equalsIgnoreCase(Constants.HOSPITAL)) {
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospital().clear();
                inputData.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospital(subClassList);
            }


        } catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

}
