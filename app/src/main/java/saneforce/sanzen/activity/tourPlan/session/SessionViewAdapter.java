package saneforce.sanzen.activity.tourPlan.session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQHeaderModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQItemModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.storage.SharedPref;

public class SessionViewAdapter extends RecyclerView.Adapter<SessionViewAdapter.MyViewHolder> {
    public ModelClass inputDataModel = new ModelClass();
    public OneBuildModelClass inputDataModelOneBuild = new OneBuildModelClass();
    Context context;
    private boolean isMGR = false;

    public SessionViewAdapter() {
    }

    public SessionViewAdapter(ModelClass inputDataModel, Context context) {
        this.inputDataModel = inputDataModel;
        this.context = context;
        isMGR = SharedPref.getSfType(context).equals("2");
    }

    public SessionViewAdapter(OneBuildModelClass inputDataModelOneBuild, Context context) {
        this.inputDataModelOneBuild = inputDataModelOneBuild;
        this.context = context;
    }

    @NonNull
    @Override
    public SessionViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewAdapter.MyViewHolder holder, int position) {
        if (SharedPref.getClusterCap(context).isEmpty() || SharedPref.getClusterCap(context).equalsIgnoreCase(null)) {
            holder.clusterCapTV.setText("Cluster");
        } else {
            holder.clusterCapTV.setText(SharedPref.getClusterCap(context));
        }

        if (SharedPref.getDrCap(context).isEmpty() || SharedPref.getDrCap(context).equalsIgnoreCase(null)) {
            holder.drCapTV.setText("Doctor");
        } else {
            holder.drCapTV.setText(SharedPref.getDrCap(context));
        }

        if (SharedPref.getChmCap(context).isEmpty() || SharedPref.getChmCap(context).equalsIgnoreCase(null)) {
            holder.chemistCapTV.setText("Chemist");
        } else {
            holder.chemistCapTV.setText(SharedPref.getChmCap(context));
        }

        if (SharedPref.getStkCap(context).isEmpty() || SharedPref.getStkCap(context).equalsIgnoreCase(null)) {
            holder.stockiestCapTV.setText("Stockist");
        } else {
            holder.stockiestCapTV.setText(SharedPref.getStkCap(context));
        }

        if (SharedPref.getUNLcap(context).isEmpty() || SharedPref.getUNLcap(context).equalsIgnoreCase(null)) {
            holder.unListedDrCapTV.setText("Unlisted Doctor");
        } else {
            holder.unListedDrCapTV.setText(SharedPref.getUNLcap(context));
        }

        if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            holder.dataOneBuild = inputDataModelOneBuild.getSessionList().get(holder.getAbsoluteAdapterPosition());
            holder.clusterModelArrayOneBuild = new ArrayList<>(holder.dataOneBuild.getTerritories());
            holder.jcModelArrayOneBuild = new ArrayList<>(holder.dataOneBuild.getJointWorks());
            holder.listedDrModelArrayOneBuild = new ArrayList<>(holder.dataOneBuild.getDoctors());
            holder.chemistModelArrayOneBuild = new ArrayList<>(holder.dataOneBuild.getChemists());
            holder.stockiestModelArrayOneBuild = new ArrayList<>(holder.dataOneBuild.getStockists());
            holder.unListedDrModelArrayOneBuild = new ArrayList<>(holder.dataOneBuild.getUnlistedDoctors());
            holder.cipModelArrayOneBuild = new ArrayList<>(holder.dataOneBuild.getCip());
            holder.hospitalModelArrayOneBuild = new ArrayList<>(holder.dataOneBuild.getHospitals());

            holder.sessionNoTxt.setText(context.getString(R.string.session) + (position + 1));
            holder.workTypeTV.setText(holder.dataOneBuild.getWorkType().getName());

            if (!holder.dataOneBuild.getRemarks().isEmpty()) {
                holder.remarksTV.setText(holder.dataOneBuild.getRemarks());
            } else {
                holder.remarksLayout.setVisibility(View.GONE);
            }

            if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0")/* && SharedPref.getSfType(context).equalsIgnoreCase("1")*/) {
                if (holder.dataOneBuild.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                    holder.workDayLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.workDayLayout.setVisibility(View.GONE);
                }
                if (SharedPref.getSfType(context).equalsIgnoreCase("1")) {
                    holder.hqLayout.setVisibility(View.GONE);
                } else {
                    if (!holder.dataOneBuild.getHeadquarters().getName().isEmpty()) {
                        holder.hqLayout.setVisibility(View.VISIBLE);
                        holder.hqTV.setText(holder.dataOneBuild.getHeadquarters().getName());
                    } else {
                        holder.hqLayout.setVisibility(View.GONE);
                    }
                }
                if (holder.dataOneBuild.getSTPCode().isEmpty()) {
                    holder.workDayTV.setText(context.getString(R.string.select));
                } else {
                    holder.workDayTV.setText(holder.dataOneBuild.getSTPName());
                }
            } else {
                holder.workDayLayout.setVisibility(View.GONE);
                if (holder.dataOneBuild.getHeadquarters().getName().equals("")) {
                    holder.hqLayout.setVisibility(View.GONE);
                } else if (SharedPref.getSfType(context).equalsIgnoreCase("1")) {
                    holder.hqLayout.setVisibility(View.GONE);
                } else {
                    holder.hqTV.setText(holder.dataOneBuild.getHeadquarters().getName());
                }
            }

            //Cluster
            if (holder.dataOneBuild.getTerritories().size() > 0) {
                holder.clusterLayout.setVisibility(View.VISIBLE);
                StringBuilder clusterName = new StringBuilder();
                for (int i = 0; i < holder.clusterModelArrayOneBuild.size(); i++) {
                    if (clusterName.length() == 0) {
                        clusterName = new StringBuilder(holder.clusterModelArrayOneBuild.get(i).getName());
                    } else {
                        clusterName.append(",").append(holder.clusterModelArrayOneBuild.get(i).getName());
                    }
                }
                if (clusterName.length() > 0) {
                    holder.clusterTV.setText(clusterName);
                }
            } else {
                holder.clusterLayout.setVisibility(View.GONE);
            }

            //Joint Work
            if (holder.dataOneBuild.getJointWorks().size() > 0) {
                StringBuilder jcName = new StringBuilder();
                for (int i = 0; i < holder.jcModelArrayOneBuild.size(); i++) {
                    if (jcName.length() == 0) {
                        jcName = new StringBuilder(holder.jcModelArrayOneBuild.get(i).getName());
                    } else {
                        jcName.append(", ").append(holder.jcModelArrayOneBuild.get(i).getName());
                    }
                }
                if (jcName.length() > 0) {
                    holder.jcTV.setText(jcName);
                }
            } else {
                holder.jcLayout.setVisibility(View.GONE);
            }

            //Listed Dr
            if (holder.dataOneBuild.getDoctors().size() > 0) {
                StringBuilder drName = new StringBuilder();
                for (int i = 0; i < holder.listedDrModelArrayOneBuild.size(); i++) {
                    if (drName.length() == 0) {
                        drName = new StringBuilder(holder.listedDrModelArrayOneBuild.get(i).getName());
                    } else {
                        drName.append(", ").append(holder.listedDrModelArrayOneBuild.get(i).getName());
                    }
                }
                if (drName.length() > 0) {
                    holder.drTV.setText(drName);
                }
            } else {
                holder.drLayout.setVisibility(View.GONE);
            }

            //Chemist
            if (holder.dataOneBuild.getChemists().size() > 0) {
                StringBuilder chemistName = new StringBuilder();
                for (int i = 0; i < holder.chemistModelArrayOneBuild.size(); i++) {
                    if (chemistName.length() == 0) {
                        chemistName = new StringBuilder(holder.chemistModelArrayOneBuild.get(i).getName());
                    } else {
                        chemistName.append(", ").append(holder.chemistModelArrayOneBuild.get(i).getName());
                    }
                }
                if (chemistName.length() > 0) {
                    holder.chemistTV.setText(chemistName);
                }
            } else {
                holder.chemistLayout.setVisibility(View.GONE);
            }

            //Stockiest
            if (holder.dataOneBuild.getStockists().size() > 0) {
                StringBuilder stockiestName = new StringBuilder();
                for (int i = 0; i < holder.stockiestModelArrayOneBuild.size(); i++) {
                    if (stockiestName.length() == 0) {
                        stockiestName = new StringBuilder(holder.stockiestModelArrayOneBuild.get(i).getName());
                    } else {
                        stockiestName.append(", ").append(holder.stockiestModelArrayOneBuild.get(i).getName());
                    }
                }
                if (stockiestName.length() > 0) {
                    holder.stockiestTV.setText(stockiestName);
                }
            } else {
                holder.stockiestLayout.setVisibility(View.GONE);
            }

            //Unlisted Dr
            if (holder.dataOneBuild.getUnlistedDoctors().size() > 0) {
                StringBuilder unListedDrName = new StringBuilder();
                for (int i = 0; i < holder.unListedDrModelArrayOneBuild.size(); i++) {
                    if (unListedDrName.length() == 0) {
                        unListedDrName = new StringBuilder(holder.unListedDrModelArrayOneBuild.get(i).getName());
                    } else {
                        unListedDrName.append(", ").append(holder.unListedDrModelArrayOneBuild.get(i).getName());
                    }
                }
                if (unListedDrName.length() > 0) {
                    holder.unListedDrTV.setText(unListedDrName);
                }
            } else {
                holder.unListedDrLayout.setVisibility(View.GONE);
            }

            //Cip
            if (holder.dataOneBuild.getCip().size() > 0) {
                StringBuilder cipName = new StringBuilder();
                for (int i = 0; i < holder.cipModelArrayOneBuild.size(); i++) {
                    if (cipName.length() == 0) {
                        cipName = new StringBuilder(holder.cipModelArrayOneBuild.get(i).getName());
                    } else {
                        cipName.append(", ").append(holder.cipModelArrayOneBuild.get(i).getName());
                    }
                }
                if (cipName.length() > 0) {
                    holder.cipTV.setText(cipName);
                }
            } else {
                holder.cipLayout.setVisibility(View.GONE);
            }

            //Hospital
            if (holder.dataOneBuild.getHospitals().size() > 0) {
                StringBuilder hospName = new StringBuilder();
                for (int i = 0; i < holder.hospitalModelArrayOneBuild.size(); i++) {
                    if (hospName.length() == 0) {
                        hospName = new StringBuilder(holder.hospitalModelArrayOneBuild.get(i).getName());
                    } else {
                        hospName.append(", ").append(holder.hospitalModelArrayOneBuild.get(i).getName());
                    }
                }
                if (hospName.length() > 0) {
                    holder.hospTV.setText(hospName);
                }
            } else {
                holder.hospLayout.setVisibility(View.GONE);
            }
        } else {
            holder.data = inputDataModel.getSessionList().get(holder.getAbsoluteAdapterPosition());
            holder.clusterModelArray = new ArrayList<>(holder.data.getCluster());
            holder.jcModelArray = new ArrayList<>(holder.data.getJC());
            holder.listedDrModelArray = new ArrayList<>(holder.data.getListedDr());
            holder.chemistModelArray = new ArrayList<>(holder.data.getChemist());
            holder.stockiestModelArray = new ArrayList<>(holder.data.getStockiest());
            holder.unListedDrModelArray = new ArrayList<>(holder.data.getUnListedDr());
            holder.cipModelArray = new ArrayList<>(holder.data.getCip());
            holder.hospitalModelArray = new ArrayList<>(holder.data.getHospital());
            holder.clustersModelArray = new ArrayList<>(holder.data.getClusters());
            holder.jcsModelArray = new ArrayList<>(holder.data.getJCs());
            holder.listedDrsModelArray = new ArrayList<>(holder.data.getListedDrs());
            holder.chemistsModelArray = new ArrayList<>(holder.data.getChemists());
            holder.stockistsModelArray = new ArrayList<>(holder.data.getStockiests());
            holder.unListedDrsModelArray = new ArrayList<>(holder.data.getUnListedDrs());
            holder.cipsModelArray = new ArrayList<>(holder.data.getCips());
            holder.hospitalsModelArray = new ArrayList<>(holder.data.getHospitals());

            holder.sessionNoTxt.setText(context.getString(R.string.session) + (position + 1));
            holder.workTypeTV.setText(holder.data.getWorkType().getName());

            if (!holder.data.getRemarks().isEmpty()) {
                holder.remarksTV.setText(holder.data.getRemarks());
            } else {
                holder.remarksLayout.setVisibility(View.GONE);
            }

            if (SharedPref.getStpNeed(context).equalsIgnoreCase("0") && SharedPref.getStpBasedMtp(context).equalsIgnoreCase("0") && SharedPref.getSfType(context).equalsIgnoreCase("1")) {
                if (holder.data.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                    holder.workDayLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.workDayLayout.setVisibility(View.GONE);
                }
                holder.hqLayout.setVisibility(View.GONE);
                if (inputDataModel.getSTP_Code().isEmpty()) {
                    holder.workDayTV.setText(context.getString(R.string.select));
                } else {
                    holder.workDayTV.setText(inputDataModel.getSTP_Name());
                }
            } else {
                holder.workDayLayout.setVisibility(View.GONE);
                if (holder.data.getHQ().getName().isEmpty() && holder.data.getHQs().isEmpty()) {
                    holder.hqLayout.setVisibility(View.GONE);
                } else if (SharedPref.getSfType(context).equalsIgnoreCase("1")) {
                    holder.hqLayout.setVisibility(View.GONE);
                } else {
                    holder.hqLayout.setVisibility(View.VISIBLE);
                    if (!holder.data.getHQs().isEmpty()) {
                        StringBuilder hqName = new StringBuilder();
                        for (int i = 0; i < holder.data.getHQs().size(); i++) {
                            if (hqName.length() == 0) {
                                hqName = new StringBuilder(holder.data.getHQs().get(i).getName());
                            } else {
                                hqName.append(",").append(holder.data.getHQs().get(i).getName());
                            }
                        }
                        if (hqName.length() > 0) {
                            holder.hqTV.setText(hqName);
                        }
                    } else {
                        holder.hqTV.setText(holder.data.getHQ().getName());
                    }
                }
            }

            //Cluster
            if (!holder.data.getCluster().isEmpty() && !isMGR) {
                holder.clusterLayout.setVisibility(View.VISIBLE);
                StringBuilder clusterName = new StringBuilder();
                for (int i = 0; i < holder.clusterModelArray.size(); i++) {
                    if (clusterName.length() == 0) {
                        clusterName = new StringBuilder(holder.clusterModelArray.get(i).getName());
                    } else {
                        clusterName.append(",").append(holder.clusterModelArray.get(i).getName());
                    }
                }
                if (clusterName.length() > 0) {
                    holder.clusterTV.setText(clusterName);
                }
            } else if (!holder.data.getClusters().isEmpty() && isMGR) {
                holder.clusterLayout.setVisibility(View.VISIBLE);
                StringBuilder clusterName = new StringBuilder();
                for (int i = 0; i < holder.clustersModelArray.size(); i++) {
                    MultiHQHeaderModelClass multiHQHeaderModelClass = holder.clustersModelArray.get(i);
                    ArrayList<MultiHQItemModelClass> list = multiHQHeaderModelClass.getItemsList();
                    for (int j = 0; j < list.size(); j++) {
                        MultiHQItemModelClass multiHQItemModelClass = list.get(j);
                        if (clusterName.length() == 0) {
                            clusterName = new StringBuilder(multiHQItemModelClass.getName());
                        } else {
                            clusterName.append(", ").append(multiHQItemModelClass.getName());
                        }
                    }
                }
                if (clusterName.length() > 0) {
                    holder.clusterTV.setText(clusterName);
                } else {
                    holder.clusterLayout.setVisibility(View.GONE);
                }
            } else {
                holder.clusterLayout.setVisibility(View.GONE);
            }

            //Joint Work
            if (!holder.data.getJC().isEmpty() && !isMGR) {
                StringBuilder jcName = new StringBuilder();
                for (int i = 0; i < holder.jcModelArray.size(); i++) {
                    if (jcName.length() == 0) {
                        jcName = new StringBuilder(holder.jcModelArray.get(i).getName());
                    } else {
                        jcName.append(", ").append(holder.jcModelArray.get(i).getName());
                    }
                }
                if (jcName.length() > 0) {
                    holder.jcTV.setText(jcName);
                }
            } else if (!holder.data.getJCs().isEmpty() && isMGR) {
                holder.jcLayout.setVisibility(View.VISIBLE);
                StringBuilder jcName = new StringBuilder();
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
                if (jcName.length() > 0) {
                    holder.jcTV.setText(jcName);
                } else {
                    holder.jcLayout.setVisibility(View.GONE);
                }
            } else {
                holder.jcLayout.setVisibility(View.GONE);
            }

            //Listed Dr
            if (!holder.data.getListedDr().isEmpty() && !isMGR) {
                StringBuilder drName = new StringBuilder();
                for (int i = 0; i < holder.listedDrModelArray.size(); i++) {
                    if (drName.length() == 0) {
                        drName = new StringBuilder(holder.listedDrModelArray.get(i).getName());
                    } else {
                        drName.append(", ").append(holder.listedDrModelArray.get(i).getName());
                    }
                }
                if (drName.length() > 0) {
                    holder.drTV.setText(drName);
                }
            } else if (!holder.data.getListedDrs().isEmpty() && isMGR) {
                holder.drLayout.setVisibility(View.VISIBLE);
                StringBuilder drName = new StringBuilder();
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
                if (drName.length() > 0) {
                    holder.drTV.setText(drName);
                } else {
                    holder.drLayout.setVisibility(View.GONE);
                }
            } else {
                holder.drLayout.setVisibility(View.GONE);
            }

            //Chemist
            if (!holder.data.getChemist().isEmpty() && !isMGR) {
                StringBuilder chemistName = new StringBuilder();
                for (int i = 0; i < holder.chemistModelArray.size(); i++) {
                    if (chemistName.length() == 0) {
                        chemistName = new StringBuilder(holder.chemistModelArray.get(i).getName());
                    } else {
                        chemistName.append(", ").append(holder.chemistModelArray.get(i).getName());
                    }
                }
                if (chemistName.length() > 0) {
                    holder.chemistTV.setText(chemistName);
                }
            } else if (!holder.data.getChemists().isEmpty() && isMGR) {
                holder.chemistLayout.setVisibility(View.VISIBLE);
                StringBuilder chemistName = new StringBuilder();
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
                if (chemistName.length() > 0) {
                    holder.chemistTV.setText(chemistName);
                } else {
                    holder.chemistLayout.setVisibility(View.GONE);
                }
            } else {
                holder.chemistLayout.setVisibility(View.GONE);
            }

            //Stockiest
            if (!holder.data.getStockiest().isEmpty() && !isMGR) {
                StringBuilder stockiestName = new StringBuilder();
                for (int i = 0; i < holder.stockiestModelArray.size(); i++) {
                    if (stockiestName.length() == 0) {
                        stockiestName = new StringBuilder(holder.stockiestModelArray.get(i).getName());
                    } else {
                        stockiestName.append(", ").append(holder.stockiestModelArray.get(i).getName());
                    }
                }
                if (stockiestName.length() > 0) {
                    holder.stockiestTV.setText(stockiestName);
                }
            } else if (!holder.data.getStockiests().isEmpty() && isMGR) {
                holder.stockiestLayout.setVisibility(View.VISIBLE);
                StringBuilder stockiestName = new StringBuilder();
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
                if (stockiestName.length() > 0) {
                    holder.stockiestTV.setText(stockiestName);
                } else {
                    holder.stockiestLayout.setVisibility(View.GONE);
                }
            } else {
                holder.stockiestLayout.setVisibility(View.GONE);
            }

            //Unlisted Dr
            if (holder.data.getUnListedDr().size() > 0) {
                StringBuilder unListedDrName = new StringBuilder();
                for (int i = 0; i < holder.unListedDrModelArray.size(); i++) {
                    if (unListedDrName.length() == 0) {
                        unListedDrName = new StringBuilder(holder.unListedDrModelArray.get(i).getName());
                    } else {
                        unListedDrName.append(", ").append(holder.unListedDrModelArray.get(i).getName());
                    }
                }
                if (unListedDrName.length() > 0) {
                    holder.unListedDrTV.setText(unListedDrName);
                }
            } else {
                holder.unListedDrLayout.setVisibility(View.GONE);
            }

            //Cip
            if (holder.data.getCip().size() > 0) {
                StringBuilder cipName = new StringBuilder();
                for (int i = 0; i < holder.cipModelArray.size(); i++) {
                    if (cipName.length() == 0) {
                        cipName = new StringBuilder(holder.cipModelArray.get(i).getName());
                    } else {
                        cipName.append(", ").append(holder.cipModelArray.get(i).getName());
                    }
                }
                if (cipName.length() > 0) {
                    holder.cipTV.setText(cipName);
                }
            } else {
                holder.cipLayout.setVisibility(View.GONE);
            }

            //Hospital
            if (holder.data.getHospital().size() > 0) {
                StringBuilder hospName = new StringBuilder();
                for (int i = 0; i < holder.hospitalModelArray.size(); i++) {
                    if (hospName.length() == 0) {
                        hospName = new StringBuilder(holder.hospitalModelArray.get(i).getName());
                    } else {
                        hospName.append(", ").append(holder.hospitalModelArray.get(i).getName());
                    }
                }
                if (hospName.length() > 0) {
                    holder.hospTV.setText(hospName);
                }
            } else {
                holder.hospLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            return inputDataModelOneBuild.getSessionList().size();
        } else {
            return inputDataModel.getSessionList().size();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sessionNoTxt;
        public TextView workTypeTV, hqTV, clusterTV, jcTV, drTV, chemistTV, stockiestTV, unListedDrTV, cipTV, hospTV, remarksTV, workDayTV;
        public TextView clusterCapTV, drCapTV, chemistCapTV, stockiestCapTV, unListedDrCapTV, cipCapTV, hospCapTV;
        public LinearLayout workTypeLayout, hqLayout, clusterLayout, jcLayout, drLayout, chemistLayout, stockiestLayout, unListedDrLayout, cipLayout, hospLayout, remarksLayout, workDayLayout;
        public RelativeLayout sessionDelete;

        //Input data
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

        public ModelClass.SessionList data = new ModelClass.SessionList();

        //One Build Input data
        ArrayList<OneBuildModelClass.SessionList.SubClass> clusterModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> jcModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> listedDrModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> chemistModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> stockiestModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> unListedDrModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> cipModelArrayOneBuild;
        ArrayList<OneBuildModelClass.SessionList.SubClass> hospitalModelArrayOneBuild;
        public OneBuildModelClass.SessionList dataOneBuild = new OneBuildModelClass.SessionList();

        public MyViewHolder(@NonNull View itemView) {
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
            workDayLayout = itemView.findViewById(R.id.workDayLayout);

            workTypeTV = itemView.findViewById(R.id.workTypeField);
            hqTV = itemView.findViewById(R.id.hqField);
            clusterTV = itemView.findViewById(R.id.clusterField);
            jcTV = itemView.findViewById(R.id.jcField);
            drTV = itemView.findViewById(R.id.listedDrField);
            chemistTV = itemView.findViewById(R.id.chemistField);
            stockiestTV = itemView.findViewById(R.id.stockiestField);
            unListedDrTV = itemView.findViewById(R.id.unListedDrField);
            cipTV = itemView.findViewById(R.id.cipField);
            hospTV = itemView.findViewById(R.id.hospField);
            remarksTV = itemView.findViewById(R.id.remarksField);
            workDayTV = itemView.findViewById(R.id.workDayField);

            clusterCapTV = itemView.findViewById(R.id.clusterLayoutTxt);
            drCapTV = itemView.findViewById(R.id.listedDrCap);
            chemistCapTV = itemView.findViewById(R.id.chemistCap);
            stockiestCapTV = itemView.findViewById(R.id.stockistCap);
            unListedDrCapTV = itemView.findViewById(R.id.unListedDrCap);
            cipCapTV = itemView.findViewById(R.id.cipCap);
            hospCapTV = itemView.findViewById(R.id.hospitalCap);
        }

    }

}
