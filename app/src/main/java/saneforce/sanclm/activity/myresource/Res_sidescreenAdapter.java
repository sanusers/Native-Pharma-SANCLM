package saneforce.sanclm.activity.myresource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import saneforce.sanclm.R;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.utility.TimeUtils;

public class Res_sidescreenAdapter extends RecyclerView.Adapter<Res_sidescreenAdapter.ViewHolder> {

    public static View list_resource;
    ArrayList<Resourcemodel_class> resList;
    ArrayList<String> resList1 = new ArrayList<>();


    Map<String, Integer> valueCounts = new HashMap<>();
    Context context;
    String split_val = "";
    String cdate2 = "";
    SQLite sqLite;
    int countvalue = 0;

    ArrayList<Resourcemodel_class> L_cLasses = new ArrayList<>();

    HashSet<String> uniqueValues = new HashSet<>();
    ArrayList<String> duplicateValues = new ArrayList<>();


    public Res_sidescreenAdapter(Context context, ArrayList<Resourcemodel_class> resList, String split_val) {//
        this.context = context;
        this.resList = resList;
        this.split_val = split_val;
    }

    @NonNull
    @Override
    public Res_sidescreenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resource_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull Res_sidescreenAdapter.ViewHolder holder, int position) {
        String count = String.valueOf((position + 1));
        final Resourcemodel_class app_adapt = resList.get(position);
        sqLite = new SQLite(context);


        String countlis = String.valueOf(resList.get(position));

        Log.d("pos1", countlis);
        if (!split_val.equals("2")) {

            if (!app_adapt.getDcr_name().equals("") && !app_adapt.getDcr_name().equals("null")) {
                holder.Res_Name.setText(app_adapt.getDcr_name());
            }
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
                    holder.Res_View.setVisibility(View.GONE);

                    holder.Res_Table2.setVisibility(View.GONE);
                }

            }
            if (split_val.equals("1") || split_val.equals("2")) {
                holder.Res_Edit.setVisibility(View.GONE);
                holder.Res_View.setVisibility(View.GONE);

                holder.Res_Table2.setVisibility(View.GONE);
            }
            holder.listcount.setText(count + " )");

            holder.Res_View.setOnClickListener(v -> {
                Log.d("latlong", app_adapt.Latitude + "--" + app_adapt.Latitude + "--" + app_adapt.getRes_id() + "--" + app_adapt.getCustoum_name());
                if (app_adapt.Latitude.equals("") || app_adapt.Latitude.equals("null")) {

                } else {
                    Intent click = new Intent(context, MyResource_mapview.class);
                    click.putExtra("type", app_adapt.getRes_id());
                    click.putExtra("cust_name", app_adapt.getCustoum_name());
                    context.startActivity(click);
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
                    JSONArray jsonvst_ctl = sqLite.getMasterSyncDataByKey(Constants.VISIT_CONTROL);
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


    @Override
    public int getItemCount() {
        return resList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Res_Name, Res_category, Res_specialty, Res_rx, Res_culter, listcount;
        public LinearLayout Res_Edit, Res_View, Res_Table1, Res_Table2, Click_Res, res_view, vistcntrl_view, Res_visitcntl, end_line, topline, line_endshow;

        public TextView visit_dt;  //cutom_name1,date_visit,cutom_name2,date_visit2,cutom_name3,date_visit3
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

        }
    }


}
