package saneforce.santrip.activity.myresource;

import static saneforce.santrip.activity.myresource.MyResource_Activity.Valcount;
import static saneforce.santrip.activity.myresource.MyResource_Activity.appRecyclerView;
import static saneforce.santrip.activity.myresource.MyResource_Activity.et_Custsearch;
import static saneforce.santrip.activity.myresource.MyResource_Activity.listresource;
import static saneforce.santrip.activity.myresource.MyResource_Activity.search_list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import saneforce.santrip.R;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;

public class Resource_adapter extends RecyclerView.Adapter<Resource_adapter.ViewHolder> {
    public static String listedres;
    public static RelativeLayout list_resource;
    public static Res_sidescreenAdapter side_adapter;
    ArrayList<Resourcemodel_class> listeduser;
    ArrayList<Resourcemodel_class> visituser;
    ArrayList<String> visit_list = new ArrayList<>();
    ArrayList<String> visitlist1 = new ArrayList<>();
    Context context;
    SQLite sqLite;
    HashSet<String> uniqueValues = new HashSet<>();
    HashMap<String, Integer> idCounts = new HashMap<>();
    ArrayList<String> count_list = new ArrayList<>();
    ArrayList<String> visitcountlist = new ArrayList<>();
    String listed;
    String key;
    String Cust_name;
    ArrayList<String> list = new ArrayList<>();
    String Doc, chm = "", stck = "", unlist = "";
    String po_count;

    public Resource_adapter(Context context, ArrayList<Resourcemodel_class> listeduser) {
        this.context = context;
        this.listeduser = listeduser;


    }

    @NonNull
    @Override
    public Resource_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_listed, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull Resource_adapter.ViewHolder holder, int position) {
        final Resourcemodel_class app_adapt = listeduser.get(position);
        sqLite = new SQLite(context);

        holder.username.setText(app_adapt.getListed_data());
        holder.usercount.setText(app_adapt.getListed_count());
        listedres = app_adapt.getListed_data();
        holder.list_resource.setOnClickListener(v -> {
            et_Custsearch.getText().clear();
            MyResource_Activity.drawerLayout.setVisibility(View.VISIBLE);
            MyResource_Activity.headtext_id.setText(app_adapt.getListed_data());
            Log.d("list_postion", app_adapt.getListed_data() + "####" + app_adapt.getVal_pos());

            try {
                Log.d("list_postion_1", app_adapt.getListed_data());
                MyResource_Activity.Key = "";
                listresource.clear();
                search_list.clear();
                Res_sidescreenAdapter appAdapter_0 = new Res_sidescreenAdapter(context, listresource, "");
                appRecyclerView.setAdapter(appAdapter_0);
                appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                appAdapter_0.notifyDataSetChanged();
//                        }


                switch (app_adapt.getVal_pos()) {


                    case ("1"):
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(context)));
                        JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(context));
                        Valcount = "";
                        String docval = "";
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (!docval.equals(jsonObject.getString("Code"))) {
                                    docval = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));

                                    String category = (jsonObject.getString("Category"));
                                    String specialty = (jsonObject.getString("Specialty"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.getString("Lat"));
                                    String Long = (jsonObject.getString("Long"));

                                    listresource.add(new Resourcemodel_class(custom_name, cluster, category, "Rx", specialty, Lat, Long, docval, MyResource_Activity.Key));

                                    Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, listresource, "");
                                    appRecyclerView.setAdapter(appAdapter);
                                    appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter.notifyDataSetChanged();
                                }
                            }

                        }
                        break;
                    case ("2"):
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(context)));
                        JSONArray jsonchemist = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(context));
                        String chmval = "";
                        Valcount = "";
                        if (jsonchemist.length() > 0) {
                            for (int i = 0; i < jsonchemist.length(); i++) {
                                JSONObject jsonObject = jsonchemist.getJSONObject(i);
                                if (!chmval.equals(jsonObject.getString("Code"))) {
                                    chmval = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));

                                    listresource.add(new Resourcemodel_class(custom_name, cluster, "", "", "", Lat, Long, chmval, MyResource_Activity.Key));
                                    Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, listresource, "");

                                    appRecyclerView.setAdapter(appAdapter);
                                    appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case ("3"):
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(context)));
                        JSONArray jsonstock = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(context));
                        String strck_val = "";
                        Valcount = "";
                        if (jsonstock.length() > 0) {
                            for (int i = 0; i < jsonstock.length(); i++) {
                                JSONObject jsonObject = jsonstock.getJSONObject(i);
                                if (!strck_val.equals(jsonObject.getString("Code"))) {
                                    strck_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));

                                    listresource.add(new Resourcemodel_class(custom_name, cluster, "", "", "", Lat, Long, strck_val, MyResource_Activity.Key));

                                    Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, listresource, "");
                                    appRecyclerView.setAdapter(appAdapter);
                                    appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;

                    case ("4"):
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(context)));
                        JSONArray jsonunlisted = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(context));
                        String unlist_val = "";
                        Valcount = "";
                        if (jsonunlisted.length() > 0) {
                            for (int i = 0; i < jsonunlisted.length(); i++) {
                                JSONObject jsonObject = jsonunlisted.getJSONObject(i);

                                if (!unlist_val.equals(jsonObject.getString("Code"))) {
                                    unlist_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String category = (jsonObject.getString("CategoryName"));
                                    String specialty = (jsonObject.getString("SpecialtyName"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));

                                    listresource.add(new Resourcemodel_class(custom_name, cluster, category, "", specialty, Lat, Long, unlist_val, MyResource_Activity.Key));

                                    Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, listresource, "");
                                    appRecyclerView.setAdapter(appAdapter);
                                    appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter.notifyDataSetChanged();


                                }
                            }
                        }
                        break;
                    case ("5"):


                        break;
                    case ("6"):


                        break;
                    case ("7"):
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.INPUT));
                        JSONArray jsoninput = sqLite.getMasterSyncDataByKey(Constants.INPUT);
                        String input_val = "";
                        Valcount = "1";
                        if (jsoninput.length() > 0) {
                            for (int i = 0; i < jsoninput.length(); i++) {
                                JSONObject jsonObject = jsoninput.getJSONObject(i);
                                if (!input_val.equals(jsonObject.getString("Code"))) {
//                                input_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));


                                    listresource.add(new Resourcemodel_class(custom_name, "", "", "", "", "", "", "", ""));

                                    Res_sidescreenAdapter appAdapter3 = new Res_sidescreenAdapter(context, listresource, "1");
                                    appRecyclerView.setAdapter(appAdapter3);
                                    appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter3.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case ("8"):
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.PRODUCT));
                        Valcount = "1";
                        JSONArray jsonproduct = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
                        Log.d("listedpro", String.valueOf(jsonproduct));
                        String product_val = "";
                        if (jsonproduct.length() > 0) {
                            for (int i = 0; i < jsonproduct.length(); i++) {
                                JSONObject jsonObject = jsonproduct.getJSONObject(i);
                                if (!product_val.equals(jsonObject.getString("Code"))) {
//                                product_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String product_type = (jsonObject.getString("Product_Mode"));


                                    listresource.add(new Resourcemodel_class(custom_name, "", product_type, "", "", "", "", "", ""));

                                    Res_sidescreenAdapter appAdapter3 = new Res_sidescreenAdapter(context, listresource, "1");
                                    appRecyclerView.setAdapter(appAdapter3);
                                    appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter3.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case ("9"):
                        MyResource_Activity.Key = sqLite.getMasterSyncDataByKey(Constants.CLUSTER) + SharedPref.getHqCode(context);
                        JSONArray jsonculst = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + SharedPref.getHqCode(context));
                        String culst_val = "";
                        Valcount = "";
                        if (jsonculst.length() > 0) {
                            for (int i = 0; i < jsonculst.length(); i++) {
                                JSONObject jsonObject = jsonculst.getJSONObject(i);
                                if (!culst_val.equals(jsonObject.getString("Code"))) {
//                                culst_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    listresource.add(new Resourcemodel_class(custom_name, "", "", "", "", "", "", "", ""));

                                    Res_sidescreenAdapter appAdapter3 = new Res_sidescreenAdapter(context, listresource, "1");
                                    appRecyclerView.setAdapter(appAdapter3);
                                    appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter3.notifyDataSetChanged();
                                }
                            }
                        }
                        break;

                    case ("10"):
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.VISIT_CONTROL));
                        JSONArray jsonvst_ctl = sqLite.getMasterSyncDataByKey(Constants.VISIT_CONTROL);
                        JSONArray jsonvst_Doc = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(context));
                        uniqueValues.clear();
                        idCounts.clear();
                        Valcount = "2";

                        listed = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));

                        if (jsonvst_ctl.length() > 0) {
                            for (int i = 0; i < jsonvst_ctl.length(); i++) {
                                JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);
                                for (int i1 = 0; i1 < jsonvst_Doc.length(); i1++) {
                                    JSONObject jsonObject1 = jsonvst_Doc.getJSONObject(i1);
                                    String docval1 = jsonObject1.getString("Code");


                                    if (docval1.equalsIgnoreCase(jsonObject.getString("CustCode")) && listed.equals(jsonObject.getString("Mnth"))) {
                                        if (uniqueValues.add(docval1)) {
                                            String max_vistcount = jsonObject1.getString("Tlvst");
                                            String custom_name = ((jsonObject.getString("CustName")));
                                            String custom_id = ((jsonObject.getString("CustCode")));
                                            String town_name = ((jsonObject.getString("town_name")));
                                            String Dcr_count = (jsonObject.getString("Dcr_flag"));
                                            String vist_ctrlDate = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, (jsonObject.getString("Dcr_dt"))));
                                            String Vist_Date = vist_ctrlDate;
                                            String result = custom_name;
                                            Log.d("doc_list", result);

                                            listresource.add(new Resourcemodel_class(custom_name, custom_id, town_name, Vist_Date, Dcr_count, listed, "", max_vistcount, ""));
                                            Res_sidescreenAdapter appAdapter3 = new Res_sidescreenAdapter(context, listresource, "2");
                                            appRecyclerView.setAdapter(appAdapter3);
                                            appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                            appAdapter3.notifyDataSetChanged();

                                        }
                                    }
                                }
                            }


                        }


                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + app_adapt.getListed_data());
                }
                if (MyResource_Activity.drawerLayout.isDrawerOpen(Gravity.END)) {
                    MyResource_Activity.drawerLayout.closeDrawer(Gravity.END);

                } else {
                    MyResource_Activity.drawerLayout.openDrawer(Gravity.END);

                }

                search_list.addAll(listresource);


//                doSearch();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    @Override
    public int getItemCount() {
        return listeduser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usercount, username;
        public RelativeLayout list_resource;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usercount = itemView.findViewById(R.id.usercount);
            username = itemView.findViewById(R.id.username);
            list_resource = itemView.findViewById(R.id.list_resource);

        }
    }


}

