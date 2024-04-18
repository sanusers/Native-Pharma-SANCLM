package saneforce.santrip.activity.myresource;

import static saneforce.santrip.activity.myresource.MyResource_Activity.Valcount;
import static saneforce.santrip.activity.myresource.MyResource_Activity.appRecyclerView;
import static saneforce.santrip.activity.myresource.MyResource_Activity.et_Custsearch;
import static saneforce.santrip.activity.myresource.MyResource_Activity.listresource;
import static saneforce.santrip.activity.myresource.MyResource_Activity.search_list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import saneforce.santrip.R;
import saneforce.santrip.activity.forms.weekoff.weekoff_viewscreen;
import saneforce.santrip.activity.myresource.Categoryview.Cate_viewscreen;
import saneforce.santrip.activity.myresource.callstatusview.Callsstatus_screenview;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.utility.TimeUtils;

public class Resource_adapter extends RecyclerView.Adapter<Resource_adapter.ViewHolder> {
    public static String listedres;

    ArrayList<Resourcemodel_class> listeduser;

    Context context;
    SQLite sqLite;
    HashSet<String> uniqueValues = new HashSet<>();
    HashMap<String, Integer> idCounts = new HashMap<>();

    String listed, pos_check = "";
    public static String rec_val = "";
    String synhqval1;

    public Resource_adapter(Context context, ArrayList<Resourcemodel_class> listeduser, String synhqval1) {
        this.context = context;
        this.listeduser = listeduser;
        this.synhqval1 = synhqval1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_listed, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Resourcemodel_class app_adapt = listeduser.get(position);
        sqLite = new SQLite(context);

        holder.username.setText(app_adapt.getListed_data());
        holder.usercount.setText(app_adapt.getListed_count());
        listedres = app_adapt.getListed_data();
        holder.list_resource.setOnClickListener(v -> {
            et_Custsearch.getText().clear();
            MyResource_Activity.binding.drawerLayout.setVisibility(View.VISIBLE);
            MyResource_Activity.headtext_id.setText(app_adapt.getListed_data());
            Log.d("list_postion", app_adapt.getListed_data() + "####" + app_adapt.getVal_pos());

            try {
                Log.d("list_postion_1", app_adapt.getListed_data());
                MyResource_Activity.Key = "";
                listresource.clear();
                search_list.clear();


                switch (app_adapt.getVal_pos()) {


                    case ("1"):
                        rec_val = "D";
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.DOCTOR + synhqval1));
                        JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + synhqval1);
                        Valcount = "";
                        String docval = "";
                        pos_check = "";
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (!docval.equals(jsonObject.getString("Code"))) {
                                    docval = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String category = (jsonObject.getString("Category"));
                                    String CategoryCode = (jsonObject.getString("CategoryCode"));
                                    String SpecialtyCode = (jsonObject.getString("SpecialtyCode"));
                                    String specialty = (jsonObject.getString("Specialty"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.getString("Lat"));
                                    String Long = (jsonObject.getString("Long"));
                                    String Addrs = (jsonObject.getString("Addrs"));
                                    String Mobile = (jsonObject.getString("Mobile"));
                                    String Phone = (jsonObject.getString("Phone"));
                                    String DOB = (jsonObject.getString("DOB"));
                                    String DOW = (jsonObject.getString("DOW"));
                                    String DrEmail = (jsonObject.getString("DrEmail"));
                                    String Qual = (jsonObject.getString("DrDesig"));
                                    String ListedDr_Sex = (jsonObject.getString("ListedDr_Sex"));
                                    String Town_Code = (jsonObject.getString("Town_Code"));
                                    String Town_Name = (jsonObject.getString("Town_Name"));

                                    Log.e("dcr_doctorTown_Name", Town_Name);

                                    listresource.add(new Resourcemodel_class(docval, custom_name, cluster, category, CategoryCode, "Rx", SpecialtyCode, specialty, Lat, Long, docval, MyResource_Activity.Key, Qual,
                                            Addrs, DOB, DOW, Mobile, Phone, DrEmail, ListedDr_Sex, Town_Code, Town_Name, "D"));


                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        break;
                    case ("2"):
                        rec_val = "C";
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + synhqval1));
                        JSONArray jsonchemist = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + synhqval1);
                        String chmval = "";
                        Valcount = "";
                        if (jsonchemist.length() > 0) {
                            for (int i = 0; i < jsonchemist.length(); i++) {
                                JSONObject jsonObject = jsonchemist.getJSONObject(i);
                                if (!chmval.equals(jsonObject.getString("Code"))) {
                                    chmval = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String Code = (jsonObject.getString("Code"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));


                                    String Chemists_Mobile = (jsonObject.getString("Chemists_Mobile"));
                                    String Chemists_Phone = (jsonObject.getString("Chemists_Phone"));
                                    String Chemists_Email = (jsonObject.getString("Chemists_Email"));
                                    String addrs = (jsonObject.getString("addrs"));


                                    listresource.add(new Resourcemodel_class(Code, custom_name, cluster, "", "", "", "", "", Lat, Long, chmval,
                                            MyResource_Activity.Key, "", addrs, "", "", Chemists_Mobile, Chemists_Phone, Chemists_Email, "", "", cluster, "C"));

                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        break;
                    case ("3"):
                        rec_val = "S";
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + synhqval1));
                        JSONArray jsonstock = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + synhqval1);
                        String strck_val = "";
                        Valcount = "";
                        if (jsonstock.length() > 0) {
                            for (int i = 0; i < jsonstock.length(); i++) {
                                JSONObject jsonObject = jsonstock.getJSONObject(i);
                                if (!strck_val.equals(jsonObject.getString("Code"))) {
                                    strck_val = jsonObject.getString("Code");
                                    String Code = (jsonObject.getString("Code"));
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));


                                    String Stockiest_Phone = (jsonObject.getString("Stockiest_Phone"));
                                    String Stockiest_Mobile = (jsonObject.getString("Stockiest_Mobile"));
                                    String Stockiest_Email = (jsonObject.getString("Stockiest_Email"));
                                    String Addr = (jsonObject.getString("Addr"));


                                    listresource.add(new Resourcemodel_class(Code, custom_name, cluster, "", "", "", "", "", Lat, Long, strck_val,
                                            MyResource_Activity.Key, "", Addr, "", "", Stockiest_Mobile, Stockiest_Phone, Stockiest_Email, "", "", "", "S"));


                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        break;

                    case ("4"):
                        rec_val = "U";
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + synhqval1));
                        JSONArray jsonunlisted = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + synhqval1);
                        String unlist_val = "";
                        Valcount = "";
                        if (jsonunlisted.length() > 0) {
                            for (int i = 0; i < jsonunlisted.length(); i++) {
                                JSONObject jsonObject = jsonunlisted.getJSONObject(i);

                                if (!unlist_val.equals(jsonObject.getString("Code"))) {
                                    unlist_val = jsonObject.getString("Code");
                                    String Code = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String category = (jsonObject.getString("CategoryName"));
                                    String specialty = (jsonObject.getString("SpecialtyName"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));


                                    String Category = (jsonObject.getString("Category"));


                                    String Specialty = (jsonObject.getString("Specialty"));
                                    String Qual = (jsonObject.getString("Qual"));
                                    String Email = (jsonObject.getString("Email"));
                                    String Mobile = (jsonObject.getString("Mobile"));
                                    String Phone = (jsonObject.getString("Phone"));
                                    String addr = (jsonObject.getString("addr"));

                                    listresource.add(new Resourcemodel_class(Code, custom_name, cluster, category, Category, "", Specialty, specialty, Lat, Long, unlist_val,
                                            MyResource_Activity.Key, Qual, addr, "", "", Mobile, Phone, Email, "", "", cluster, "U"));
                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        break;
                    case ("5"):


                        break;
                    case ("6"):


                        break;
                    case ("7"):
                        rec_val = "I";
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.INPUT));
                        JSONArray jsoninput = sqLite.getMasterSyncDataByKey(Constants.INPUT);
                        String input_val = "";
                        Valcount = "1";

                        if (jsoninput.length() > 0) {
                            for (int i = 0; i < jsoninput.length(); i++) {
                                JSONObject jsonObject = jsoninput.getJSONObject(i);

                                if (!input_val.equals(jsonObject.getString("Code")) && (!jsonObject.getString("Code").equals("-1"))) {
                                    String custom_name = (jsonObject.getString("Name"));
                                    JSONObject jsonFDate = new JSONObject(jsonObject.getString("EffF"));
                                    JSONObject jsonTDate = new JSONObject(jsonObject.getString("EffT"));

                                    String frm_date = jsonFDate.getString("date");
                                    String[] Frm_val = frm_date.split(" ");
                                    String to_date = jsonTDate.getString("date");
                                    String[] Toval = to_date.split(" ");

                                    listresource.add(new Resourcemodel_class("", custom_name, "", "", "", "", "", "", Frm_val[0], Toval[0],
                                            "", "", "", "", "", "", "", "", "", "", "", "", ""));
                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        break;
                    case ("8"):
                        rec_val = "P";
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.PRODUCT));
                        Valcount = "1";

                        JSONArray jsonproduct = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
                        Log.d("listedpro", String.valueOf(jsonproduct));
                        String product_val = "";
                        if (jsonproduct.length() > 0) {
                            for (int i = 0; i < jsonproduct.length(); i++) {
                                JSONObject jsonObject = jsonproduct.getJSONObject(i);
                                if (!product_val.equals(jsonObject.getString("Code"))) {
                                    String custom_name = (jsonObject.getString("Name"));
                                    String product_type = (jsonObject.getString("Product_Mode"));

                                    listresource.add(new Resourcemodel_class("", custom_name, "", product_type, "", "", "", "", "", "",
                                            "", "", "", "", "", "", "", "", "", "", "", "", ""));
                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        break;
                    case ("9"):
                        MyResource_Activity.Key = sqLite.getMasterSyncDataByKey(Constants.CLUSTER) + synhqval1;
                        JSONArray jsonculst = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + synhqval1);
                        String culst_val = "";
                        Valcount = "";
                        Valcount = "1";
                        if (jsonculst.length() > 0) {
                            for (int i = 0; i < jsonculst.length(); i++) {
                                JSONObject jsonObject = jsonculst.getJSONObject(i);
                                if (!culst_val.equals(jsonObject.getString("Code"))) {
//                                culst_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    listresource.add(new Resourcemodel_class("", custom_name, "", "", "", "", "", "", "", "",
                                            "", "", "", "", "", "", "", "", "", "", "", "", ""));
                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        break;

                    case ("10"):
                        MyResource_Activity.Key = String.valueOf(sqLite.getMasterSyncDataByKey(Constants.VISIT_CONTROL));
                        JSONArray jsonvst_ctl = sqLite.getMasterSyncDataByKey(Constants.VISIT_CONTROL);
                        JSONArray jsonvst_Doc = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + synhqval1);
                        uniqueValues.clear();
                        idCounts.clear();
                        Valcount = "2";

                        listed = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                        pos_check = "2";
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

                                            listresource.add(new Resourcemodel_class("", custom_name, custom_id, town_name, "", Vist_Date, "", Dcr_count, listed, "",
                                                    max_vistcount, "", "", "", "", "", "", "", "", "", "", "", ""));


                                        }
                                    }
                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);

                        break;
                    case ("11"):
                        Intent l = new Intent(context, weekoff_viewscreen.class);
                        context.startActivity(l);

                        MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        break;

                    case ("12"):
                        MyResource_Activity.binding.drawerLayout.closeDrawer(Gravity.END);
                        Intent l1 = new Intent(context, Callsstatus_screenview.class);
                        context.startActivity(l1);
                        MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
                        MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        break;


                    case ("13")://Cate_viewscreen

                        Intent l2 = new Intent(context, Cate_viewscreen.class);
                        context.startActivity(l2);
                        MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
                        MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + app_adapt.getListed_data());
                }
//                MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
//                MyResource_Activity.binding.drawerLayout.closeDrawer(Gravity.END);

                search_list.addAll(listresource);



              /*  if (MyResource_Activity.drawerLayout.isDrawerOpen(Gravity.END)) {
                    MyResource_Activity.drawerLayout.closeDrawer(Gravity.END);

                } else {
                    MyResource_Activity.drawerLayout.openDrawer(Gravity.END);

                }*/
                MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, listresource, Valcount);
                appRecyclerView.setAdapter(appAdapter);
                appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                appAdapter.notifyDataSetChanged();

                search_list.addAll(listresource);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listeduser.size();
    }

    public void filterList(ArrayList<Resourcemodel_class> filterdNames) {
        this.listeduser = filterdNames;
        notifyDataSetChanged();
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

