package saneforce.santrip.activity.tourPlan.session;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.tourPlan.model.ModelClass;

public class SessionViewAdapter extends RecyclerView.Adapter<SessionViewAdapter.MyViewHolder> {

    public ModelClass inputDataModel = new ModelClass();


    public SessionViewAdapter () {
    }

    public SessionViewAdapter (ModelClass inputDataModel) {
        this.inputDataModel = inputDataModel;
    }

    @NonNull
    @Override
    public SessionViewAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull SessionViewAdapter.MyViewHolder holder, int position) {


        holder.data = inputDataModel.getSessionList().get(holder.getAbsoluteAdapterPosition());
        holder.clusterModelArray = new ArrayList<>(holder.data.getCluster());
        holder.jcModelArray = new ArrayList<>(holder.data.getJC());
        holder.listedDrModelArray = new ArrayList<>(holder.data.getListedDr());
        holder.chemistModelArray = new ArrayList<>(holder.data.getChemist());
        holder.stockiestModelArray = new ArrayList<>(holder.data.getStockiest());
        holder.unListedDrModelArray = new ArrayList<>(holder.data.getUnListedDr());
        holder.cipModelArray = new ArrayList<>(holder.data.getCip());
        holder.hospitalModelArray = new ArrayList<>(holder.data.getHospital());

        holder.sessionNoTxt.setText("Session " +  (position + 1));
       holder.workTypeTV.setText(holder.data.getWorkType().getName());

       if (!holder.data.getRemarks().isEmpty()) {
           holder.remarksTV.setText(holder.data.getRemarks());
       } else {
           holder.remarksLayout.setVisibility(View.GONE);
       }

       if (holder.data.getHQ().getName().equals("")){
           holder.hqLayout.setVisibility(View.GONE);
       } else{
           holder.hqTV.setText(holder.data.getHQ().getName());
       }

       //Cluster
       if (holder.data.getCluster().size() > 0){
           StringBuilder clusterName = new StringBuilder();
           for (int i=0;i<holder.clusterModelArray.size();i++){
               if (clusterName.length() == 0){
                   clusterName = new StringBuilder(holder.clusterModelArray.get(i).getName());
               }else{
                   clusterName.append(",").append(holder.clusterModelArray.get(i).getName());
               }
           }
           if (clusterName.length() > 0){
               holder.clusterTV.setText(clusterName);
           }
       } else {
           holder.clusterLayout.setVisibility(View.GONE);
       }

        //Joint Work
        if (holder.data.getJC().size() > 0){
            StringBuilder jcName = new StringBuilder();
            for (int i=0;i<holder.jcModelArray.size();i++){
                if (jcName.length() == 0){
                    jcName = new StringBuilder(holder.jcModelArray.get(i).getName());
                }else{
                    jcName.append(", ").append(holder.jcModelArray.get(i).getName());
                }
            }
            if (jcName.length() > 0){
                holder.jcTV.setText(jcName);
            }
        } else {
            holder.jcLayout.setVisibility(View.GONE);
        }

        //Listed Dr
        if (holder.data.getListedDr().size() > 0){
            StringBuilder drName = new StringBuilder();
            for (int i=0;i<holder.listedDrModelArray.size();i++){
                if (drName.length() == 0){
                    drName = new StringBuilder(holder.listedDrModelArray.get(i).getName());
                }else{
                    drName.append(", ").append(holder.listedDrModelArray.get(i).getName());
                }
            }
            if (drName.length() > 0){
                holder.drTV.setText(drName);
            }
        } else {
            holder.drLayout.setVisibility(View.GONE);
        }

        //Chemist
        if (holder.data.getChemist().size() > 0){
            StringBuilder chemistName = new StringBuilder();
            for (int i=0;i<holder.chemistModelArray.size();i++){
                if (chemistName.length() == 0){
                    chemistName = new StringBuilder(holder.chemistModelArray.get(i).getName());
                }else{
                    chemistName.append(", ").append(holder.chemistModelArray.get(i).getName());
                }
            }
            if (chemistName.length() > 0){
                holder.chemistTV.setText(chemistName);
            }
        } else {
            holder.chemistLayout.setVisibility(View.GONE);
        }

        //Stockiest
        if (holder.data.getStockiest().size() > 0){
            StringBuilder stockiestName = new StringBuilder();
            for (int i=0;i<holder.stockiestModelArray.size();i++){
                if (stockiestName.length() == 0){
                    stockiestName = new StringBuilder(holder.stockiestModelArray.get(i).getName());
                }else{
                    stockiestName.append(", ").append(holder.stockiestModelArray.get(i).getName());
                }
            }
            if (stockiestName.length() > 0){
                holder.stockiestTV.setText(stockiestName);
            }
        } else {
            holder.stockiestLayout.setVisibility(View.GONE);
        }

        //Unlisted Dr
        if (holder.data.getUnListedDr().size() > 0){
            StringBuilder unListedDrName = new StringBuilder();
            for (int i=0;i<holder.unListedDrModelArray.size();i++){
                if (unListedDrName.length() == 0){
                    unListedDrName = new StringBuilder(holder.unListedDrModelArray.get(i).getName());
                }else{
                    unListedDrName.append(", ").append(holder.unListedDrModelArray.get(i).getName());
                }
            }
            if (unListedDrName.length() > 0){
                holder.unListedDrTV.setText(unListedDrName);
            }
        } else {
            holder.unListedDrLayout.setVisibility(View.GONE);
        }

        //Cip
        if (holder.data.getCip().size() > 0){
            StringBuilder cipName = new StringBuilder();
            for (int i=0;i<holder.cipModelArray.size();i++){
                if (cipName.length() == 0){
                    cipName = new StringBuilder(holder.cipModelArray.get(i).getName());
                }else{
                    cipName.append(", ").append(holder.cipModelArray.get(i).getName());
                }
            }
            if (cipName.length() > 0){
                holder.cipTV.setText(cipName);
            }
        } else {
            holder.cipLayout.setVisibility(View.GONE);
        }

        //Hospital
        if (holder.data.getHospital().size() > 0){
            StringBuilder hospName = new StringBuilder();
            for (int i=0;i<holder.hospitalModelArray.size();i++){
                if (hospName.length() == 0){
                    hospName = new StringBuilder(holder.hospitalModelArray.get(i).getName());
                }else{
                    hospName.append(", ").append(holder.hospitalModelArray.get(i).getName());
                }
            }
            if (hospName.length() > 0){
                holder.hospTV.setText(hospName);
            }
        } else {
            holder.hospLayout.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount () {
        return inputDataModel.getSessionList().size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView sessionNoTxt;
        public TextView workTypeTV,hqTV,clusterTV,jcTV,drTV,chemistTV,stockiestTV,unListedDrTV,cipTV,hospTV,remarksTV;
        public LinearLayout sessionDelete,workTypeLayout,hqLayout,clusterLayout,jcLayout,drLayout,chemistLayout,stockiestLayout,unListedDrLayout,cipLayout,hospLayout,remarksLayout;

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


        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            sessionNoTxt = itemView.findViewById(R.id.sessionNo);
            sessionDelete = itemView.findViewById(R.id.sessionDelete);

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
            remarksLayout = itemView.findViewById(R.id.remarksLayout);

            workTypeTV = itemView.findViewById(R.id.workTypeField);
            hqTV = itemView.findViewById(R.id.hqField);
            clusterTV = itemView.findViewById(R.id.clusterField);
            jcTV = itemView.findViewById(R.id.jcField);
            drTV = itemView.findViewById(R.id.listedDrField);
            chemistTV = itemView.findViewById(R.id.chemistField);
            stockiestTV = itemView.findViewById(R.id.stockiestField);
            unListedDrTV= itemView.findViewById(R.id.unListedDrField);
            cipTV = itemView.findViewById(R.id.cipField);
            hospTV = itemView.findViewById(R.id.hospField);
            remarksTV = itemView.findViewById(R.id.remarksField);

        }

    }

}
