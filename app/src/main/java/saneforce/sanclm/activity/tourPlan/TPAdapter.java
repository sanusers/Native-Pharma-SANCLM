package saneforce.sanclm.activity.tourPlan;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.masterSync.MasterSyncItemModel;
import saneforce.sanclm.activity.tourPlan.session.SessionInterface;
import saneforce.sanclm.activity.tourPlan.session.SessionItemAdapter;
import saneforce.sanclm.activity.tourPlan.session.SessionItemInterface;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class TPAdapter extends RecyclerView.Adapter<TPAdapter.MyViewHolder> {


    public static ModelClass arrayList = new ModelClass();
    Context context;
    public View view;
    SQLite sqLite;
    ApiInterface apiInterface;
    SessionInterface sessionInterface;
    public SessionItemAdapter sessionItemAdapter = new SessionItemAdapter();
    String sfCode = "",division_code = "",sfType = "",designation = "",state_code ="",subdivision_code = "";
    static String hqNeed = "",clusterNeed = "",jwNeed = "",drNeed = "",chemistNeed = "", stockiestNeed = "",cipNeed = "" , hospNeed = "";
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    int itemPosition ;
    boolean onEdit = false;

    public TPAdapter () {
    }

    public TPAdapter (ModelClass arrayList, Context context, SessionInterface sessionInterface) {
        this.arrayList = arrayList;
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
                cipNeed = jsonArray.getJSONObject(i).getString("Cip_Need");
                hospNeed = jsonArray.getJSONObject(i).getString("HospNeed");
            }
        }catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

    @NonNull
    @Override
    public TPAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_edit_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull TPAdapter.MyViewHolder holder, int position) {

//        try {
            holder.viewHolder = holder;
            holder.data = arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition());
            holder.clusterModelArray = new ArrayList<>(holder.data.getCluster());
            holder.jcModelArray = new ArrayList<>(holder.data.getJC());
            holder.listedDrModelArray = new ArrayList<>(holder.data.getListedDr());
            holder.chemistModelArray = new ArrayList<>(holder.data.getChemist());
            holder.stockiestModelArray = new ArrayList<>(holder.data.getStockiest());
            holder.unListedDrModelArray = new ArrayList<>(holder.data.getUnListedDr());
            holder.cipModelArray = new ArrayList<>(holder.data.getCip());
            holder.hospitalModelArray = new ArrayList<>(holder.data.getHospital());

            if (holder.data.getVisible()){
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else{
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            holder.sessionNoTxt.setText("Session " +  (position + 1));
            if(holder.getAbsoluteAdapterPosition() == 0){
                holder.sessionDelete.setVisibility(View.GONE);
            }

            //work Type
            if (holder.data.getWorkType().getName().equals("")){
                holder.workTypeField.setText("Select");
            }else{
                holder.workTypeField.setText(holder.data.getWorkType().getName());
            }

            if (!holder.data.getWorkType().getFWFlg().equals("")){
                if (holder.data.getWorkType().getFWFlg().equalsIgnoreCase("F")){
                    if (holder.data.getWorkType().getTerrSlFlg().equalsIgnoreCase("Y")){ // Y - yes
                        hqNeed = "0"; // 0 - Yes
                        clusterNeed = "0";
                    }else{
                        hqNeed = "1"; // 1 - No
                        clusterNeed = "1";
                    }
                    nonFieldWork(false,holder,false);
                }else{
                    nonFieldWork(true,holder,false);
                }
            }

            //HQ
            if (holder.data.getHQ().getName().equals("")){
                holder.hqField.setText("Select");
            }else {
                holder.hqField.setText(holder.data.getHQ().getName());
                holder.selectedHq = holder.data.getHQ().getCode();
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
                    clusterName.append(",").append(holder.clusterModelArray.get(i).getName());
                }
            }
            if (clusterName.length() > 0){
                holder.clusterField.setText(clusterName);
            }
            prepareInputData(holder.clusterModelArray,holder.clusterJsonArray,holder.clusterJsonArray1);

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
                    jcName.append(",").append(holder.jcModelArray.get(i).getName());
                }

            }
            if (jcName.length() > 0){
                holder.jcField.setText(jcName);
            }
            prepareInputData(holder.jcModelArray,holder.jointCallJsonArray,holder.jointCallJsonArray1);

            //Dr
            StringBuilder drName = new StringBuilder();
            for (int i = 0; i<holder.listedDrModelArray.size(); i++){
                if (drName.length() == 0){
                    drName = new StringBuilder(holder.listedDrModelArray.get(i).getName());
                }else{
                    drName.append(",").append(holder.listedDrModelArray.get(i).getName());
                }

            }
            if (drName.length() > 0){
                holder.drField.setText(drName);
            }
            prepareInputData(holder.listedDrModelArray,holder.listedDrJsonArray,holder.listedDrJsonArray1);

            //Chemist
            StringBuilder chemistName = new StringBuilder();
            for (int i=0;i<holder.chemistModelArray.size();i++){
                if (chemistName.length() == 0){
                    chemistName = new StringBuilder(holder.chemistModelArray.get(i).getName());
                }else{
                    chemistName.append(",").append(holder.chemistModelArray.get(i).getName());
                }

            }
            if (chemistName.length() > 0){
                holder.chemistField.setText(chemistName);
            }
            prepareInputData(holder.chemistModelArray,holder.chemistJsonArray,holder.chemistJsonArray1);

            //Stockiest
            StringBuilder stockiestName = new StringBuilder();
            for (int i=0;i<holder.stockiestModelArray.size();i++){
                if (stockiestName.length() == 0){
                    stockiestName = new StringBuilder(holder.stockiestModelArray.get(i).getName());
                }else{
                    stockiestName.append(",").append(holder.stockiestModelArray.get(i).getName());
                }

            }
            if (stockiestName.length() > 0){
                holder.stockiestField.setText(stockiestName);
            }
            prepareInputData(holder.stockiestModelArray,holder.stockiestJsonArray,holder.stockiestJsonArray1);


            //UnListed Doctor
            StringBuilder unListedDrName = new StringBuilder();
            for (int i=0;i<holder.unListedDrModelArray.size();i++){
                if (unListedDrName.length() == 0){
                    unListedDrName = new StringBuilder(holder.unListedDrModelArray.get(i).getName());
                }else{
                    unListedDrName.append(",").append(holder.unListedDrModelArray.get(i).getName());
                }

            }
            if (unListedDrName.length() > 0){
                holder.unListedDrField.setText(unListedDrName);
            }
            prepareInputData(holder.unListedDrModelArray,holder.unListedDrJsonArray,holder.unListedDrJsonArray1);

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
            prepareInputData(holder.cipModelArray,holder.cipJsonArray,holder.cipJsonArray1);

            //Hospital
            StringBuilder hospName = new StringBuilder();
            for (int i=0;i<holder.hospitalModelArray.size();i++){
                if (hospName.length() == 0){
                    hospName = new StringBuilder(holder.hospitalModelArray.get(i).getName());
                }else{
                    jcName.append(",").append(holder.hospitalModelArray.get(i).getName());
                }
            }
            if (hospName.length() > 0){
                holder.hospField.setText(hospName);
            }
            prepareInputData(holder.hospitalModelArray,holder.hospJsonArray,holder.hospJsonArray1);

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
                if (!holder.fieldSelected){
                    if (holder.workTypeJsonArray.length() <= 0){
                        holder.workTypeJsonArray = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
                        addCheckBox(holder.workTypeJsonArray);
                    }
                    populateSessionAdapter(holder,holder.workTypeJsonArray, false);
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
                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                }else{
                    if (!holder.fieldSelected){
                        if (holder.hqJsonArray.length() <= 0){
                            holder.hqJsonArray = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
                            addCheckBox(holder.hqJsonArray);
                        }
                        populateSessionAdapter(holder,holder.hqJsonArray, false);
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
                if (holder.workTypeField.getText().toString().equalsIgnoreCase("Select")){
                    Toast.makeText(context, "Select Work Type", Toast.LENGTH_SHORT).show();
                } else{
                    if (sfType.equalsIgnoreCase("2") && holder.hqField.getText().toString().equalsIgnoreCase("Select")){
                        Toast.makeText(context, "Select Head Quarters", Toast.LENGTH_SHORT).show();
                    } else{
                        if (!holder.fieldSelected){
                            if (holder.clusterJsonArray.length() <= 0){
                                holder.clusterJsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + holder.selectedHq );
                                holder.clusterJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + holder.selectedHq );
                                addCheckBox(holder.clusterJsonArray);
                                addCheckBox(holder.clusterJsonArray1);
                                TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
                            } else{
                                setSelectedCount(holder, holder.clusterJsonArray, false, holder.clusterField, holder.clusterCount);
                            }
                            holder.fieldSelected = true;
//                            selectCheckBox(holder.clusterModelArray,holder.clusterJsonArray,holder.clusterJsonArray1,true);
                            populateSessionAdapter(holder,holder.clusterJsonArray, true);
                            changeUIState(holder, holder.clusterLayout, holder.clusterArrow, false);
                            edit(holder.getAbsoluteAdapterPosition(),false);
                        } else{
                            holder.fieldSelected = false;
                            holder.clusterJsonArray = new JSONArray();
                            selectCheckBox(holder.clusterModelArray,holder.clusterJsonArray,holder.clusterJsonArray1,false);
                            setSelectedCount(holder, holder.clusterJsonArray, true, holder.clusterField, holder.clusterCount);
                            changeUIState(holder, holder.clusterLayout, holder.clusterArrow, true);
                            edit(holder.getAbsoluteAdapterPosition(),true);
                        }
                    }
                }
            }
        });

        holder.sessionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sessionInterface.deleteClicked(arrayList, holder.getAbsoluteAdapterPosition());
            }
        });


//        } catch (JSONException e){
//            throw new RuntimeException(e);
//        }


    }

    @Override
    public int getItemCount () {
        return arrayList.getSessionList().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView sessionNoTxt;
        public ImageView searchClearIcon;
        public TextView workTypeField,hqField,clusterField,jcField,drField,chemistField,stockiestField,unListedDrField,cipField,hospField;
        public TextView clusterCount,jcCount,drCount,chemistCount,stockiestCount,unListedDrCount,cipCount,hospCount;
        public LinearLayout sessionDelete,workTypeLayout,hqLayout,clusterLayout,jcLayout,drLayout,chemistLayout,stockiestLayout,unListedDrLayout,cipLayout,hospLayout;
        public ImageView workTypeArrow,hqArrow,clusterArrow,jcArrow,drArrow,chemistArrow,stockiestArrow,unListedDrArrow,cipArrow,hospArrow;
        EditText searchET;
        CardView listCardView;
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

        public ModelClass.SessionList data = new ModelClass.SessionList();
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

        public JSONArray clusterJsonArray1 = new JSONArray();
        public JSONArray jointCallJsonArray1 = new JSONArray();
        public JSONArray listedDrJsonArray1 = new JSONArray();
        public JSONArray chemistJsonArray1 = new JSONArray();
        public JSONArray stockiestJsonArray1 = new JSONArray();
        public JSONArray unListedDrJsonArray1 = new JSONArray();
        public JSONArray cipJsonArray1 = new JSONArray();
        public JSONArray hospJsonArray1 = new JSONArray();

        boolean nonFieldWorkSelected = false;
        String hq_code ="",selectedHq = "";
        ArrayList<String> selectedClusterCode = new ArrayList<>();

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
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

    public void nonFieldWork(boolean noFiledWorkSelected,MyViewHolder holder,boolean whileEdit){

        if (noFiledWorkSelected){
            holder.hqLayout.setVisibility(View.GONE);
            holder.clusterLayout.setVisibility(View.GONE);
            holder.jcLayout.setVisibility(View.GONE);
            holder.drLayout.setVisibility(View.GONE);
            holder.chemistLayout.setVisibility(View.GONE);
            holder.stockiestLayout.setVisibility(View.GONE);
            holder.unListedDrLayout.setVisibility(View.GONE);
            holder.cipLayout.setVisibility(View.GONE);
            holder.hospLayout.setVisibility(View.GONE);

            if (whileEdit){
                ModelClass.SessionList.SubClass hq = new ModelClass.SessionList.SubClass("","");
                ArrayList<ModelClass.SessionList.SubClass> clusterArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> jcArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> drArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> chemistArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> stockArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> unListedDrArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> cipArray = new ArrayList<>();
                ArrayList<ModelClass.SessionList.SubClass> hospArray = new ArrayList<>();

//                ModelClass modelClass =  new ModelClass(holder.data.getDate(),true,true,holder.data.getWorkType(), hq, clusterArray, jcArray, drArray, chemistArray, stockArray, unListedDrArray, cipArray, hospArray);
//                arrayList.remove(holder.getAbsoluteAdapterPosition());
//                arrayList.add(holder.getAbsoluteAdapterPosition(),modelClass);
            }
        }else {
            if (!onEdit){
                if (hqNeed.equals("0"))
                    holder.hqLayout.setVisibility(View.VISIBLE);

                if (clusterNeed.equals("0"))
                    holder.clusterLayout.setVisibility(View.VISIBLE);

                if (jwNeed.equals("0"))
                    holder.jcLayout.setVisibility(View.VISIBLE);

                if (drNeed.equals("0"))
                    holder.drLayout.setVisibility(View.VISIBLE);

                if (chemistNeed.equals("0"))
                    holder.chemistLayout.setVisibility(View.VISIBLE);

                if (stockiestNeed.equals("0"))
                    holder.stockiestLayout.setVisibility(View.VISIBLE);

                if (cipNeed.equals("0"))
                    holder.cipLayout.setVisibility(View.VISIBLE);

                if (hospNeed.equals("0"))
                    holder.hospLayout.setVisibility(View.VISIBLE);

                holder.unListedDrLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getDataFromLocal(MyViewHolder holder,String hqCode){

        holder.clusterJsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hqCode);
        if (holder.clusterJsonArray.length() <= 0){
            prepareMasterToSync(hqCode);
        }
        holder.clusterJsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hqCode);
        holder.jointCallJsonArray = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK );
        holder.listedDrJsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + hqCode);
        holder.chemistJsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + hqCode);
        holder.stockiestJsonArray = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + hqCode);
        holder.unListedDrJsonArray = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + hqCode);
        holder.cipJsonArray = sqLite.getMasterSyncDataByKey(Constants.CIP + hqCode);
        holder.hospJsonArray = sqLite.getMasterSyncDataByKey(Constants.HOSPITAL + hqCode);

        holder.clusterJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + hqCode);
        holder.jointCallJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.JOINT_WORK );
        holder.listedDrJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + hqCode);
        holder.chemistJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + hqCode);
        holder.stockiestJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + hqCode);
        holder.unListedDrJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + hqCode);
        holder.cipJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.CIP + hqCode);
        holder.hospJsonArray1 = sqLite.getMasterSyncDataByKey(Constants.HOSPITAL + hqCode);

        addCheckBox(holder.clusterJsonArray);
        addCheckBox(holder.jointCallJsonArray);
        addCheckBox(holder.listedDrJsonArray);
        addCheckBox(holder.chemistJsonArray);
        addCheckBox(holder.stockiestJsonArray);
        addCheckBox(holder.unListedDrJsonArray);
        addCheckBox(holder.cipJsonArray);
        addCheckBox(holder.hospJsonArray);

        addCheckBox(holder.clusterJsonArray1);
        addCheckBox(holder.jointCallJsonArray1);
        addCheckBox(holder.listedDrJsonArray1);
        addCheckBox(holder.chemistJsonArray1);
        addCheckBox(holder.stockiestJsonArray1);
        addCheckBox(holder.unListedDrJsonArray1);
        addCheckBox(holder.cipJsonArray1);
        addCheckBox(holder.hospJsonArray1);

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
        MasterSyncItemModel doctorModel = new MasterSyncItemModel("getdoctors", Constants.DOCTOR + hqCode);
        MasterSyncItemModel cheModel = new MasterSyncItemModel("getchemist",Constants.CHEMIST + hqCode);
        MasterSyncItemModel stockModel = new MasterSyncItemModel("getstockist",Constants.STOCKIEST + hqCode);
        MasterSyncItemModel unListModel = new MasterSyncItemModel("getunlisteddr",Constants.UNLISTED_DOCTOR + hqCode);
        MasterSyncItemModel hospModel = new MasterSyncItemModel("gethospital",Constants.HOSPITAL + hqCode);
        MasterSyncItemModel ciModel = new MasterSyncItemModel("getcip",Constants.CIP + hqCode);
        MasterSyncItemModel cluster = new MasterSyncItemModel("getterritory",Constants.CLUSTER + hqCode);
        masterSyncArray.add(doctorModel);
        masterSyncArray.add(cheModel);
        masterSyncArray.add(stockModel);
        masterSyncArray.add(unListModel);
        masterSyncArray.add(hospModel);
        masterSyncArray.add(ciModel);
        masterSyncArray.add(cluster);
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
                Call<JsonArray> call =  apiInterface.getDrMaster(jsonObject.toString());
                if (call != null){
                    call.enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse (@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {

                            if (response.isSuccessful()) {
//                                Log.e("test","response : " + masterSyncItemModel.getRemoteTableName() +" : " + response.body().toString());
                                try {
                                    JSONArray jsonArray = new JSONArray(response.body().toString());
                                    sqLite.saveMasterSyncData(masterSyncItemModel.getLocalTableKeyName(),jsonArray.toString());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        @Override
                        public void onFailure (@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                            Log.e("test", "failed : " + t);
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

    public void prepareInputData(ArrayList<ModelClass.SessionList.SubClass> modelClass,JSONArray jsonArray,JSONArray jsonArray1){

        try {
            if (modelClass.size() > 0){
                for (int i=0;i<modelClass.size();i++){
                    for (int j=0;j<jsonArray.length();j++){
                        if (modelClass.get(i).getCode().equalsIgnoreCase(jsonArray.getJSONObject(j).getString("Code"))){
                            jsonArray.getJSONObject(j).put("isChecked",true);
                            jsonArray1.getJSONObject(j).put("isChecked",true);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void populateSessionAdapter(MyViewHolder holder, JSONArray jsonArray, boolean checkBoxNeed){
        sessionItemAdapter = new SessionItemAdapter(jsonArray, context, checkBoxNeed, new SessionItemInterface() {
            @Override
            public void itemClicked (JSONArray jsonArray, JSONObject jsonObject) {
                try {
                    if (holder.workTypeLayout.getVisibility() == View.VISIBLE){
                        boolean workTypeRepeated= false;
                        if (arrayList.getSessionList().size() > 1){
                            for (int i=0;i<arrayList.getSessionList().size();i++){
                                if (i != holder.getAbsoluteAdapterPosition() && arrayList.getSessionList().get(i).getWorkType().getName().equalsIgnoreCase(jsonObject.getString("Name"))){
                                    if (!sfType.equalsIgnoreCase("2")){
                                        Toast.makeText(context, "Work Type already been selected for session " + (i + 1) , Toast.LENGTH_SHORT).show();
                                        workTypeRepeated = true;
                                    }
                                }
                            }
                        }

                        if (!workTypeRepeated){
                            holder.workTypeField.setText(jsonObject.getString("Name"));
                            holder.data.getWorkType().setName(jsonObject.getString("Name"));
                            holder.data.getWorkType().setCode(jsonObject.getString("Code"));
                            holder.data.getWorkType().setFWFlg(jsonObject.getString("FWFlg"));
                            holder.data.getWorkType().setTerrSlFlg(jsonObject.getString("TerrSlFlg"));

                            if (jsonObject.getString("FWFlg").equalsIgnoreCase("F")){
                                if (jsonObject.getString("TerrSlFlg").equalsIgnoreCase("Y")){ // Y - yes
                                    hqNeed = "0"; // 0 - Yes
                                    clusterNeed = "0";
                                }else{
                                    hqNeed = "1"; // 1 - No
                                    clusterNeed = "1";
                                }
//                                nonFieldWork(false,holder,false);
                            }else{
//                                nonFieldWork(true,holder,true);
                            }
                        }
                        changeUIState(holder, holder.workTypeLayout, holder.workTypeArrow, true);
                        holder.fieldSelected = false;
                    }
                    else if (holder.hqLayout.getVisibility() == View.VISIBLE){

                        holder.hq_code = jsonObject.getString("id");
                        if (!holder.selectedHq.equalsIgnoreCase(holder.hq_code)){
                            holder.selectedHq = holder.hq_code;
                            hqChanged(holder.hq_code,holder);
                        }
                        holder.data.getHQ().setName(jsonObject.getString("Name"));
                        holder.data.getHQ().setCode(jsonObject.getString("Code"));

                        holder.hqField.setText(jsonObject.getString("Name"));
                        changeUIState(holder, holder.hqLayout, holder.hqArrow, true);
                        holder.fieldSelected = false;
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
//                    else if (holder.jcLayout.getVisibility() == View.VISIBLE){
//                        int count= 0;
//                        for (int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            if (jsonObject1.getBoolean("isChecked")){
//                                count++;
//                            }
//                        }
//
//                        if (count > 0){
//                            holder.jcField.setText("Selected");
//                            holder.jcCount.setVisibility(View.VISIBLE);
//                            holder.jcCount.setText(String.valueOf(count));
//                        }else{
//                            holder.jcField.setText("Select");
//                            holder.jcCount.setVisibility(View.GONE);
//                        }
//                    }
//                    else if (holder.drLayout.getVisibility() == View.VISIBLE){
//                        int count= 0;
//                        for (int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            if (jsonObject1.getBoolean("isChecked")){
//                                count++;
//                            }
//                        }
//                        if (count > 0){
//                            holder.drField.setText("Selected");
//                            holder.drCount.setVisibility(View.VISIBLE);
//                            holder.drCount.setText(String.valueOf(count));
//                        }else{
//                            holder.drField.setText("Select");
//                            holder.drCount.setVisibility(View.GONE);
//                        }
//                    }
//                    else if (holder.chemistLayout.getVisibility() == View.VISIBLE){
//                        int count= 0;
//                        for (int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            if (jsonObject1.getBoolean("isChecked")){
//                                count++;
//                            }
//                        }
//                        if (count > 0){
//                            holder.chemistField.setText("Selected");
//                            holder.chemistCount.setVisibility(View.VISIBLE);
//                            holder.chemistCount.setText(String.valueOf(count));
//                        }else{
//                            holder.chemistField.setText("Select");
//                            holder.chemistCount.setVisibility(View.GONE);
//                        }
//                    }
//                    else if (holder.stockiestLayout.getVisibility() == View.VISIBLE){
//                        int count= 0;
//                        for (int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            if (jsonObject1.getBoolean("isChecked")){
//                                count++;
//                            }
//                        }
//                        if (count > 0){
//                            holder.stockiestField.setText("Selected");
//                            holder.stockiestCount.setVisibility(View.VISIBLE);
//                            holder.stockiestCount.setText(String.valueOf(count));
//                        }else{
//                            holder.stockiestField.setText("Select");
//                            holder.stockiestCount.setVisibility(View.GONE);
//                        }
//                    }
//                    else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE){
//                        int count= 0;
//                        for (int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            if (jsonObject1.getBoolean("isChecked")){
//                                count++;
//                            }
//                        }
//                        if (count > 0){
//                            holder.unListedDrField.setText("Selected");
//                            holder.unListedDrCount.setVisibility(View.VISIBLE);
//                            holder.unListedDrCount.setText(String.valueOf(count));
//                        }else{
//                            holder.unListedDrField.setText("Select");
//                            holder.unListedDrCount.setVisibility(View.GONE);
//                        }
//                    }
//                    else if (holder.cipLayout.getVisibility() == View.VISIBLE){
//                        int count= 0;
//                        for (int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            if (jsonObject1.getBoolean("isChecked")){
//                                count++;
//                            }
//                        }
//                        if (count > 0){
//                            holder.cipField.setText("Selected");
//                            holder.cipCount.setVisibility(View.VISIBLE);
//                            holder.cipCount.setText(String.valueOf(count));
//                        }else{
//                            holder.cipField.setText("Select");
//                            holder.cipCount.setVisibility(View.GONE);
//                        }
//                    }
//                    else if (holder.hospLayout.getVisibility() == View.VISIBLE){
//                        int count= 0;
//                        for (int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            if (jsonObject1.getBoolean("isChecked")){
//                                count++;
//                            }
//                        }
//                        if (count > 0){
//                            holder.hospField.setText("Selected");
//                            holder.hospCount.setVisibility(View.VISIBLE);
//                            holder.hospCount.setText(String.valueOf(count));
//                        }else{
//                            holder.hospField.setText("Select");
//                            holder.hospCount.setVisibility(View.GONE);
//                        }
//                    }
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
            holder.hqLayout.setVisibility(View.VISIBLE);
            holder.clusterLayout.setVisibility(View.VISIBLE);
            holder.jcLayout.setVisibility(View.VISIBLE);
            holder.drLayout.setVisibility(View.VISIBLE);
            holder.chemistLayout.setVisibility(View.VISIBLE);
            holder.stockiestLayout.setVisibility(View.VISIBLE);
            holder.unListedDrLayout.setVisibility(View.VISIBLE);
            holder.cipLayout.setVisibility(View.VISIBLE);
            holder.hospLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.down_arrow));
            holder.listCardView.setVisibility(View.GONE);
            TourPlanActivity.addSaveBtnLayout.setVisibility(View.VISIBLE);
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.GONE);

            if (!hqNeed.isEmpty()){
                if (hqNeed.equals("0")){
                    holder.hqLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.hqLayout.setVisibility(View.GONE);
                }
            }

            if (!clusterNeed.isEmpty()){
                if (clusterNeed.equals("0")){
                    holder.clusterLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.clusterLayout.setVisibility(View.GONE);
                }
            }
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
            holder.listCardView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.up_arrow));
            TourPlanActivity.addSaveBtnLayout.setVisibility(View.GONE);
            TourPlanActivity.clrSaveBtnLayout.setVisibility(View.VISIBLE);
        }

    }

    public void hqChanged(String hqCode, MyViewHolder holder){

        JSONArray jsonArray1 = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + hqCode); // Dr is part of master.If this master data not available for selected HQ then there will be no data for other masters as well
        if (jsonArray1.length() <= 0){
            prepareMasterToSync(hqCode); //if selected HQ master data not in table lets sync them
        }

        holder.clusterJsonArray = new JSONArray();
        holder.jointCallJsonArray = new JSONArray();
        holder.listedDrJsonArray = new JSONArray();
        holder.chemistJsonArray = new JSONArray();
        holder.stockiestJsonArray = new JSONArray();
        holder.cipJsonArray = new JSONArray();
        holder.hospJsonArray = new JSONArray();

        holder.clusterJsonArray1 = new JSONArray();
        holder.jointCallJsonArray1 = new JSONArray();
        holder.listedDrJsonArray1 = new JSONArray();
        holder.chemistJsonArray1 = new JSONArray();
        holder.stockiestJsonArray1 = new JSONArray();
        holder.cipJsonArray1 = new JSONArray();
        holder.hospJsonArray1 = new JSONArray();

        holder.hqField.setText("Select");
        holder.clusterField.setText("Select");
        holder.jcField.setText("Select");
        holder.drField.setText("Select");
        holder.chemistField.setText("Select");
        holder.stockiestField.setText("Select");
        holder.cipField.setText("Select");
        holder.hospField.setText("Select");

        getDataFromLocal(holder,hqCode);
    }

    public static void setSelectedCount(MyViewHolder holder, JSONArray jsonArray, boolean selectState, TextView selectedNameTxtView, TextView countTxt){

        try {
//            if (holder.clusterLayout.getVisibility() != View.VISIBLE && holder.jcLayout.getVisibility() != View.VISIBLE){
//                JSONArray filteredArray = new JSONArray();
//                for (int i=0;i<holder.selectedClusterCode.size();i++){
//                    for (int j=0;j<jsonArray.length();j++){
//                        if (holder.selectedClusterCode.get(i).equalsIgnoreCase(jsonArray.getJSONObject(j).getString("Town_Code"))) { // filtering based on selected Cluster code
//                            filteredArray.put(jsonArray.getJSONObject(j));
//                        }
//                    }
//                }
//                if (filteredArray.length() > 0){
//                    jsonArray = new JSONArray();
//                    jsonArray = filteredArray;
//                }
//            }

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
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCluster().clear();
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCluster(subClassList);
                }
                else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).getJC().clear();
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).setJC(subClassList);
                }
                else if (holder.drLayout.getVisibility() == View.VISIBLE) {
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).getListedDr().clear();
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).setListedDr(subClassList);
                }
                else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).getChemist().clear();
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).setChemist(subClassList);
                }
                else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).getStockiest().clear();
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).setStockiest(subClassList);
                }
                else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).getUnListedDr().clear();
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).setUnListedDr(subClassList);
                }
                else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).getCip().clear();
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).setCip(subClassList);
                }
                else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).getHospital().clear();
                    arrayList.getSessionList().get(holder.getAbsoluteAdapterPosition()).setHospital(subClassList);
                }
            }
        } catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

    public void selectCheckBox(ArrayList<ModelClass.SessionList.SubClass> modelClass,JSONArray jsonArray,JSONArray jsonArray1,boolean fromModel){

        try {
            for (int i=0;i<jsonArray1.length();i++){
                Object object = jsonArray1.get(i);
                if (object instanceof JSONObject){
                    JSONObject jsonObject = new JSONObject(((JSONObject) object).toString());
                    jsonArray.put(jsonObject);
                }else{
                    jsonArray.put(object);
                }
            }
        } catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public void edit(int position,boolean visible){

        onEdit = !visible;
        if (arrayList.getSessionList().size() > 1){
            for (int i=0;i<arrayList.getSessionList().size();i++){
                if (i != position){
                    arrayList.getSessionList().get(i).setVisible(visible);
                }
            }
            notifyDataSetChanged();
            TourPlanActivity activity = (TourPlanActivity) context;
            activity.scrollToPosition(position,false);
        }
    }

    public void clearCheckBox (MyViewHolder holder){

        if (holder.clusterLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder.clusterJsonArray, holder.clusterField, holder.clusterCount);
        } else if (holder.jcLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder.jointCallJsonArray, holder.jcField, holder.jcCount);
        } else if (holder.drLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder.listedDrJsonArray, holder.drField, holder.drCount);
        } else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder.chemistJsonArray, holder.chemistField, holder.chemistCount);
        } else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder.stockiestJsonArray, holder.stockiestField, holder.stockiestCount);
        } else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder.unListedDrJsonArray, holder.unListedDrField, holder.unListedDrCount);
        } else if (holder.cipLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder.cipJsonArray, holder.cipField, holder.cipCount);
        } else if (holder.hospLayout.getVisibility() == View.VISIBLE) {
            clearSelectedItem(holder.hospJsonArray, holder.hospField, holder.hospCount);
        }
    }

    public void clearSelectedItem (JSONArray jsonArray, TextView labelTxt, TextView countTxt){
        try {
            for (int i = 0; i< jsonArray.length(); i++){
                jsonArray.getJSONObject(i).put("isChecked", false);
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
                    for (int i = 0; i< holder.clusterJsonArray.length(); i++){
                        JSONObject jsonObject = holder.clusterJsonArray.getJSONObject(i);
                        if (jsonObject.getBoolean("isChecked")){
                            holder.selectedClusterCode.add(jsonObject.getString("Code"));
                        }
                    }
                    clusterChanged(holder.selectedClusterCode,holder.listedDrJsonArray,holder.drField);
                    clusterChanged(holder.selectedClusterCode,holder.chemistJsonArray,holder.chemistField);
                    clusterChanged(holder.selectedClusterCode,holder.stockiestJsonArray,holder.stockiestField);
                    clusterChanged(holder.selectedClusterCode,holder.unListedDrJsonArray,holder.unListedDrField);
                    clusterChanged(holder.selectedClusterCode,holder.cipJsonArray,holder.cipField);
                    clusterChanged(holder.selectedClusterCode,holder.hospJsonArray,holder.hospField);

                    holder.clusterJsonArray1 = new JSONArray();
                    for(int i=0;i<holder.clusterJsonArray.length();i++){
                        Object object = holder.clusterJsonArray.get(i);
                        if (object instanceof JSONObject){
                            JSONObject jsonObject1 = new JSONObject(((JSONObject) object).toString());
                            holder.clusterJsonArray1.put(jsonObject1);
                        } else {
                            holder.clusterJsonArray1.put(object);
                        }
                    }


                setSelectedCount(holder, holder.clusterJsonArray, true, holder.clusterField, holder.clusterCount);
                changeUIState(holder, holder.clusterLayout, holder.clusterArrow, true);
            }
            else if (holder.jcLayout.getVisibility() == View.VISIBLE) {

                    holder.jointCallJsonArray1 = new JSONArray();
                    for(int i=0;i<holder.jointCallJsonArray.length();i++){
                        Object object = holder.jointCallJsonArray.get(i);
                        if (object instanceof JSONObject){
                            JSONObject jsonObject1 = new JSONObject(((JSONObject) object).toString());
                            holder.jointCallJsonArray1.put(jsonObject1);
                        } else {
                            holder.jointCallJsonArray1.put(object);
                        }
                    }

                setSelectedCount(holder, holder.jointCallJsonArray, true, holder.jcField, holder.jcCount);
                changeUIState(holder, holder.jcLayout, holder.jcArrow, true);
            }
            else if (holder.drLayout.getVisibility() == View.VISIBLE) {

                    holder.listedDrJsonArray1 = new JSONArray();
                    for(int i=0;i<holder.listedDrJsonArray.length();i++){
                        Object object = holder.listedDrJsonArray.get(i);
                        if (object instanceof JSONObject){
                            JSONObject jsonObject1 = new JSONObject(((JSONObject) object).toString());
                            holder.listedDrJsonArray1.put(jsonObject1);
                        } else {
                            holder.listedDrJsonArray1.put(object);
                        }
                    }

                setSelectedCount(holder,holder.listedDrJsonArray, true, holder.drField, holder.drCount);
                changeUIState(holder, holder.drLayout, holder.drArrow, true);
            }
            else if (holder.chemistLayout.getVisibility() == View.VISIBLE) {

                    holder.chemistJsonArray1 = new JSONArray();
                    for(int i=0;i<holder.chemistJsonArray.length();i++){
                        Object object = holder.chemistJsonArray.get(i);
                        if (object instanceof JSONObject){
                            JSONObject jsonObject1 = new JSONObject(((JSONObject) object).toString());
                            holder.chemistJsonArray1.put(jsonObject1);
                        } else {
                            holder.chemistJsonArray1.put(object);
                        }
                    }

                setSelectedCount(holder, holder.chemistJsonArray, true, holder.chemistField, holder.chemistCount);
                changeUIState(holder, holder.chemistLayout, holder.chemistArrow, true);
            }
            else if (holder.stockiestLayout.getVisibility() == View.VISIBLE) {

                    holder.stockiestJsonArray1 = new JSONArray();
                    for(int i=0;i<holder.stockiestJsonArray.length();i++){
                        Object object = holder.stockiestJsonArray.get(i);
                        if (object instanceof JSONObject){
                            JSONObject jsonObject1 = new JSONObject(((JSONObject) object).toString());
                            holder.stockiestJsonArray1.put(jsonObject1);
                        } else {
                            holder.stockiestJsonArray1.put(object);
                        }
                    }

                setSelectedCount(holder, holder.stockiestJsonArray, true, holder.stockiestField, holder.stockiestCount);
                changeUIState(holder, holder.stockiestLayout, holder.stockiestArrow, true);
            }
            else if (holder.unListedDrLayout.getVisibility() == View.VISIBLE) {

                    holder.unListedDrJsonArray1 = new JSONArray();
                    for(int i=0;i<holder.unListedDrJsonArray.length();i++){
                        Object object = holder.unListedDrJsonArray.get(i);
                        if (object instanceof JSONObject){
                            JSONObject jsonObject1 = new JSONObject(((JSONObject) object).toString());
                            holder.unListedDrJsonArray1.put(jsonObject1);
                        } else {
                            holder.unListedDrJsonArray1.put(object);
                        }
                    }

                setSelectedCount(holder, holder.unListedDrJsonArray, true, holder.unListedDrField, holder.unListedDrCount);
                changeUIState(holder, holder.unListedDrLayout, holder.unListedDrArrow, true);
            }
            else if (holder.cipLayout.getVisibility() == View.VISIBLE) {

                    holder.cipJsonArray1 = new JSONArray();
                    for(int i=0;i<holder.cipJsonArray.length();i++){
                        Object object = holder.cipJsonArray.get(i);
                        if (object instanceof JSONObject){
                            JSONObject jsonObject1 = new JSONObject(((JSONObject) object).toString());
                            holder.cipJsonArray1.put(jsonObject1);
                        } else {
                            holder.cipJsonArray1.put(object);
                        }
                    }

                setSelectedCount(holder, holder.cipJsonArray, true, holder.cipField, holder.cipCount);
                changeUIState(holder, holder.cipLayout, holder.cipArrow, true);
            }
            else if (holder.hospLayout.getVisibility() == View.VISIBLE) {

                holder.hospJsonArray1 = new JSONArray();
                for(int i=0;i<holder.hospJsonArray.length();i++){
                    Object object = holder.hospJsonArray.get(i);
                    if (object instanceof JSONObject){
                        JSONObject jsonObject1 = new JSONObject(((JSONObject) object).toString());
                        holder.hospJsonArray1.put(jsonObject1);
                    } else {
                        holder.hospJsonArray1.put(object);
                    }
                }
                setSelectedCount(holder, holder.hospJsonArray, true, holder.hospField, holder.hospCount);
                changeUIState(holder, holder.hospLayout, holder.hospArrow, true);
            }


        } catch (JSONException e){
            throw new RuntimeException(e);
        }

        edit(holder.getAbsoluteAdapterPosition(), true);

    }

    public void clusterChanged(ArrayList<String> clusterCodes,JSONArray jsonArray,TextView label){

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

        } catch (JSONException e){
            throw new RuntimeException(e);
        }

    }


}
