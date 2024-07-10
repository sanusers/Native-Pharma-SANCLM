package saneforce.sanzen.activity.myresource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.LoginTableDetails.LoginDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;


public class Res_sidescreenAdapter extends RecyclerView.Adapter<Res_sidescreenAdapter.ViewHolder> {


    ArrayList<Resourcemodel_class> resList;
    ArrayList<Resourcemodel_class> FillteredList;
    ArrayList<String> resList1 = new ArrayList<>();

    Context context;
    String split_val, hqcode;
    String cdate2 = "";

    String Doc_geoneed, Che_geoneed, Stk_geoneed, Cip_geoneed, Ult_geoneed;

    ArrayList<Resourcemodel_class> L_cLasses = new ArrayList<>();

    HashSet<String> uniqueValues = new HashSet<>();
    ArrayList<String> duplicateValues = new ArrayList<>();


    private RoomDB roomDB;
    private LoginDataDao loginDataDao;
    private MasterDataDao masterDataDao;

    Resource_adapter resourceAdapter;
    private OnItemClickListener onItemClickListener;

    SidelistViewInterface sidelistViewInterface;

    public Res_sidescreenAdapter(Context context, ArrayList<Resourcemodel_class> resList, String split_val, String Hqcode, SidelistViewInterface sidelistViewInterface) {
        this.context = context;
        this.resList = resList;
        this.FillteredList = resList;
        this.split_val = split_val;
        this.hqcode = Hqcode;
        this.sidelistViewInterface = sidelistViewInterface;

        roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resource_list, parent, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @SuppressLint({"ResourceAsColor", "WrongConstant"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String count = String.valueOf((position + 1));
        final Resourcemodel_class app_adapt = FillteredList.get(position);
        System.out.println("leaveWorkTypes--->" + app_adapt.getWorkType());
        System.out.println("eligibility--->" + app_adapt.getEligible());
        Doc_geoneed = SharedPref.getGeotagNeed(context);
        Che_geoneed = SharedPref.getGeotagNeedChe(context);
        Stk_geoneed = SharedPref.getGeotagNeedStock(context);
        Cip_geoneed = SharedPref.getGeotagNeedCip(context);
        Ult_geoneed = SharedPref.getGeotagNeedUnlst(context);

        if (SharedPref.getGeoChk(context).equalsIgnoreCase("0")&& ((!app_adapt.getLatitude().isEmpty() || !app_adapt.getLongtitude().isEmpty()) && (((!app_adapt.getLatitude().equalsIgnoreCase("0.0") || !app_adapt.getLongtitude().equalsIgnoreCase("0.0")) || (!app_adapt.getLatitude().equalsIgnoreCase("0") || !app_adapt.getLongtitude().equalsIgnoreCase("0")))))) {
            holder.Res_View.setVisibility(View.VISIBLE);
        } else {
            holder.Res_View.setVisibility(View.GONE);
        }
        if (!split_val.equals("2")) {


            if (!app_adapt.getDcr_name().equals("") && !app_adapt.getDcr_name().equals("null")) {
                holder.Res_Name.setText(app_adapt.getDcr_name());
            }

            holder.Res_Name.setOnClickListener(view -> {
                MyResource_Activity.datalist = app_adapt.getDcr_code();

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(FillteredList.get(position));
                }
                MyResource_Activity.binding.drawerLayout.closeDrawer(Gravity.END);
            });
//adapter click to activity without intent or referec activity screen  in android java

            if (!app_adapt.getRes_custname().equals("") && !app_adapt.getRes_custname().equals("null")) {
                holder.Res_culter.setText(app_adapt.getRes_custname());
            } else {
                holder.Res_culter.setVisibility(View.GONE);
            }


            if (!app_adapt.getRes_custname().equals("") && !app_adapt.getRes_custname().equals("null")) {
                holder.Res_culter.setText(app_adapt.getRes_custname());
            } else {
                holder.Res_culter.setVisibility(View.GONE);
            }

            if (!app_adapt.getRes_Category().equals("") && !app_adapt.getRes_Category().equals("null")) {
                holder.Res_category.setText(app_adapt.getRes_Category());
            } else {
                holder.Res_category.setVisibility(View.GONE);
            }


            if (!app_adapt.getRes_Specialty().equals("") && !app_adapt.getRes_Specialty().equals("null")) {

                holder.Res_specialty.setText(app_adapt.getRes_Specialty());
            } else {
                holder.Res_specialty.setVisibility(View.GONE);
            }
            if (!app_adapt.getRes_rx().equals("") && !app_adapt.getRes_rx().equals("null")) {
                if (split_val.equals("2")) {
                    holder.Res_rx.setText(app_adapt.getRes_rx());
                } else {
                    holder.Res_rx.setText("-");
                }
            } else {
                holder.Res_rx.setText("");
            }


            if (app_adapt.getRes_Category().equals("") && app_adapt.getRes_rx().equals("") && app_adapt.getRes_Specialty().equals("")) {
                holder.Res_Table1.setVisibility(View.GONE);
                if (split_val.equals("1") || split_val.equals("2")) {
                    holder.Res_Edit.setVisibility(View.GONE);
//                    holder.Res_View.setVisibility(View.GONE);
                    holder.Res_Table2.setVisibility(View.GONE);
                }
            }

            if (split_val.equals("1") || split_val.equals("2")) {
                holder.Res_Edit.setVisibility(View.GONE);
                holder.Res_Table2.setVisibility(View.GONE);
            }
            if (split_val.equals("1") && !app_adapt.getLatitude().equals("") && !app_adapt.getLongtitude().equals("")) {
                holder.Res_Table1.setVisibility(View.VISIBLE);
                holder.Res_category.setVisibility(View.VISIBLE);

                holder.Res_category.setText("From: " + TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, app_adapt.getLatitude()));
                holder.Res_rx.setText("To: " + TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, app_adapt.getLongtitude()));
            }

            //        input filter_add
            if (split_val.equalsIgnoreCase("11")) {
                if (!app_adapt.getLatitude().equals("") && !app_adapt.getLongtitude().equals("")) {
                    holder.Res_Table1.setVisibility(View.VISIBLE);
                    holder.Res_category.setVisibility(View.VISIBLE);

                    holder.Res_category.setText("From: " + TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, app_adapt.getLatitude()));
                    holder.Res_rx.setText("To: " + TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, app_adapt.getLongtitude()));
                }
            }
            //workType
            if (app_adapt.getType().equals("workType")) {
                holder.Res_Name.setText(app_adapt.getWorkType());
                holder.Res_Table1.setVisibility(View.VISIBLE);
                holder.Res_category.setVisibility(View.VISIBLE);
                holder.Res_category.setText(app_adapt.getTP_DCR());
            }
            if (app_adapt.getType().equals("leaveStatus")) {
                holder.Res_category.setVisibility(View.VISIBLE);
                holder.Res_Table1.setVisibility(View.VISIBLE);
                holder.Res_Table2.setVisibility(View.VISIBLE);
                holder.available.setVisibility(View.VISIBLE);
                holder.Res_Name.setText("LeaveType :" + " " + app_adapt.getLeaveTypes());
                holder.Res_category.setText("Eligible :" + " " + app_adapt.getEligible());
                holder.available.setText("Available :" + " " + app_adapt.getAvailable());
                holder.Res_rx.setText("Taken :" + " " + app_adapt.getTaken());
            }

            holder.listcount.setText(count + " )");

            holder.Res_Edit.setOnClickListener(view -> {

                Intent intent = new Intent(context, Resource_profiling.class);
                intent.putExtra("Doc_name", app_adapt.getDcr_name());
                intent.putExtra("Doc_code", app_adapt.getDcr_code());
                intent.putExtra("Qual_values", app_adapt.getRes_Qualifiey());
                intent.putExtra("Spec_values", app_adapt.getRes_Specialty());
                intent.putExtra("cate_values", app_adapt.getRes_Category());
                intent.putExtra("cate_code", app_adapt.getRes_Categorycode());
                intent.putExtra("Spec_code", app_adapt.getRes_Specialtycode());
                intent.putExtra("ListedDrSex", app_adapt.getRes_listeddoc_sex());
                intent.putExtra("DOB", app_adapt.getRes_Dob());
                intent.putExtra("DOW", app_adapt.getRes_Dow());
                intent.putExtra("ADDRESS", app_adapt.getRes_adds());
                intent.putExtra("MOB", app_adapt.getRes_mob());
                intent.putExtra("PHN", app_adapt.getRes_phn());
                intent.putExtra("EMAIL", app_adapt.getRes_Email());
                intent.putExtra("Towncode", app_adapt.getTown_code());
                intent.putExtra("Town", app_adapt.getTown_name());
                intent.putExtra("PosDCRname", app_adapt.getPos_name());
                intent.putExtra("lat", app_adapt.getLatitude());
                intent.putExtra("long", app_adapt.getLongtitude());
                context.startActivity(intent);
            });


            holder.Res_View.setOnClickListener(v -> {
                Log.d("latlong", app_adapt.Latitude + "--" + app_adapt.Latitude + "--" + app_adapt.getRes_id() + "--" + app_adapt.getCustoum_name());
                if (app_adapt.Latitude.equals("") || app_adapt.Latitude.equals("null")) {

                } else {
                    sidelistViewInterface.OnCilckItem(hqcode, app_adapt.getDcr_code(), Resource_adapter.rec_val);
                }
            });


        } else {
            if (split_val.equals("2")) {
                holder.Res_Table1.setVisibility(View.GONE);
                holder.Res_Table2.setVisibility(View.GONE);
                holder.Res_Edit.setVisibility(View.GONE);
                holder.Res_visitcntl.setVisibility(View.VISIBLE);
                holder.tertry_list.setVisibility(View.VISIBLE);
                holder.end_line.setVisibility(View.VISIBLE);
                holder.topline.setVisibility(View.VISIBLE);
                holder.line_endshow.setVisibility(View.GONE);
                resList1.clear();
                int vcount = 0;

                try {
                    uniqueValues.clear();
                    duplicateValues.clear();
                    L_cLasses.clear();
                    JSONArray jsonvst_ctl = masterDataDao.getMasterDataTableOrNew(Constants.VISIT_CONTROL).getMasterSyncDataJsonArray();
//                   ==============================
                    String colorText1 = "<font color=\"#F1536E\">" + count + " )" + "</font>";
                    holder.listcount.setText(Html.fromHtml(colorText1));
                    String val = "-(" + app_adapt.getRes_Category() + ")";
                    String colorText = "<font color=\"#F1536E\">" + app_adapt.getDcr_name() + val + "</font>";
                    holder.Res_Name.setText(Html.fromHtml(colorText));

                    Log.d("vistdates", "vist_ctrlDate" + "--" + app_adapt.getCustoum_name() + "--" + app_adapt.getVisit_count() + "--" + app_adapt.cust_name + "--" + app_adapt.getLatitude());
                    for (int i = 0; i < jsonvst_ctl.length(); i++) {
                        JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);

                        if (app_adapt.getRes_custname().equals(jsonObject.getString("CustCode"))) {
                            if (app_adapt.getLatitude().equals(jsonObject.getString("Mnth"))) {
                                String custom_name = ((jsonObject.getString("CustName")));
                                String custom_code = ((jsonObject.getString("CustCode")));
                                String Dcr_dt = ((jsonObject.getString("Dcr_dt")));
                                String vist_ctrlDate = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, (jsonObject.getString("Dcr_dt"))));
                                vcount++;
                                L_cLasses.add(new Resourcemodel_class(custom_name, custom_code, vist_ctrlDate, Dcr_dt));

                                Collections.sort(L_cLasses, new Comparator<Resourcemodel_class>() {
                                    @Override
                                    public int compare(Resourcemodel_class lhs, Resourcemodel_class rhs) {
                                        return lhs.getMax_count().compareTo(rhs.getMax_count());
                                    }
                                });
                                Collections.reverse(L_cLasses);
                            }

                            resvisitctrl_adapter resvisitctrladapter = new resvisitctrl_adapter(context, L_cLasses);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                            holder.tertry_list.setLayoutManager(mLayoutManager);
                            holder.tertry_list.setItemAnimator(new DefaultItemAnimator());
                            holder.tertry_list.setAdapter(resvisitctrladapter);
                            resvisitctrladapter.notifyDataSetChanged();
                        }
                    }

                    holder.visit_dt.setText(vcount + "/" + app_adapt.getRes_id() + "-Visit");

                } catch (Exception a) {
                    a.printStackTrace();
                }
            }
        }
    }

    //    <Resourcemodel_class> L_cLasses
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Resourcemodel_class> filterdNames) {
        this.resList = filterdNames;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return FillteredList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Res_Name, Res_category, Res_specialty, Res_rx, Res_culter, listcount;
        public LinearLayout Res_Edit, Res_View, Res_Table1, Res_Table2, Click_Res, res_view, vistcntrl_view, Res_visitcntl, end_line, topline, line_endshow;

        public TextView visit_dt, available;  //cutom_name1,date_visit,cutom_name2,date_visit2,cutom_name3,date_visit3
        public RecyclerView tertry_list;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Click_Res = itemView.findViewById(R.id.Click_Res);
            Res_Name = itemView.findViewById(R.id.Res_Name);
            Res_category = itemView.findViewById(R.id.Res_category);
            Res_specialty = itemView.findViewById(R.id.Res_specialty);
            Res_rx = itemView.findViewById(R.id.Res_rx);
            Res_culter = itemView.findViewById(R.id.Res_culter);
            listcount = itemView.findViewById(R.id.listcount);


            Res_Edit = itemView.findViewById(R.id.Res_Edit);
            Res_View = itemView.findViewById(R.id.Res_View);
            Res_Table1 = itemView.findViewById(R.id.Res_Table1);
            Res_Table2 = itemView.findViewById(R.id.Res_Table2);
            res_view = itemView.findViewById(R.id.res_view);

            end_line = itemView.findViewById(R.id.end_line);
            topline = itemView.findViewById(R.id.topline);
            line_endshow = itemView.findViewById(R.id.line_endshow);

            tertry_list = itemView.findViewById(R.id.tertry_list);
            visit_dt = itemView.findViewById(R.id.visit_dt);
            Res_visitcntl = itemView.findViewById(R.id.Res_visitcntl);
            available = itemView.findViewById(R.id.textAvailablity);


        }
    }

    public interface OnItemClickListener {
        void onItemClick(Resourcemodel_class item);
    }
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();

                ArrayList<Resourcemodel_class> filtered = new ArrayList<>();
                for (Resourcemodel_class s : resList) {
                    if (s.getDcr_name().toLowerCase().contains(searchString.toLowerCase()) || s.getRes_custname().toLowerCase().contains(searchString.toLowerCase()) || s.getRes_Specialty().toLowerCase().contains(searchString.toLowerCase()) || s.getRes_Category().toLowerCase().contains(searchString.toLowerCase()) || s.getWorkType().toLowerCase().contains(searchString.toLowerCase()) || s.getLeaveTypes().toLowerCase().contains(searchString.toLowerCase())) {//getRes_Category
                        filtered.add(s);
                    }

                }
                FillteredList = filtered;
                FilterResults filterResults = new FilterResults();
                filterResults.values = FillteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                FillteredList = (ArrayList<Resourcemodel_class>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
