package saneforce.sanclm.activity.myresource;

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

import saneforce.sanclm.R;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class Resource_adapter extends RecyclerView.Adapter<Resource_adapter.ViewHolder> {
    ArrayList<Resourcemodel_class> listeduser;
    ArrayList<Resourcemodel_class> cust_list = new ArrayList<>();

    Context context;
    SQLite sqLite;
    public static String listedres;
    String key;
    public static RelativeLayout list_resource;
    String Cust_name;


    ArrayList<String> list = new ArrayList<>();
    public static Res_sidescreenAdapter  side_adapter;
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
            MyResource_Activity.et_Custsearch.getText().clear();
            MyResource_Activity.drawerLayout.setVisibility(View.VISIBLE);
            MyResource_Activity.headtext_id.setText(app_adapt.getListed_data());
            Log.d("list_postion", app_adapt.getListed_data());
            try {
                Log.d("list_postion_1", app_adapt.getListed_data());
                MyResource_Activity.Key="";
                MyResource_Activity.listresource.clear();
                MyResource_Activity.search_list.clear();
                Res_sidescreenAdapter appAdapter_0 = new Res_sidescreenAdapter(context, MyResource_Activity.listresource,"");
                MyResource_Activity.appRecyclerView.setAdapter(appAdapter_0);
                MyResource_Activity.appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                appAdapter_0.notifyDataSetChanged();
//                        }

                switch (app_adapt.getListed_data()) {

                    case ("Listed doctor"):
                        MyResource_Activity.Key= String.valueOf(sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(context)));
                        JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(context));

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

                                    MyResource_Activity.listresource.add(new Resourcemodel_class(custom_name, cluster, category, "Rx", specialty, Lat, Long, docval, MyResource_Activity.Key));

                                    Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, MyResource_Activity.listresource, "");
                                    MyResource_Activity.appRecyclerView.setAdapter(appAdapter);
                                    MyResource_Activity.appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter.notifyDataSetChanged();
                                }
                            }

                        }
                        break;
                    case ("Chemist"):
                        MyResource_Activity.Key= String.valueOf(sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(context)));
                        JSONArray jsonchemist = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(context));
                        String chmval = "";
                        if (jsonchemist.length() > 0) {
                            for (int i = 0; i < jsonchemist.length(); i++) {
                                JSONObject jsonObject = jsonchemist.getJSONObject(i);
                                if(!chmval.equals(jsonObject.getString("Code"))){
                                    chmval = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));

                                    MyResource_Activity.listresource.add(new Resourcemodel_class(custom_name, cluster, "", "", "",Lat,Long,chmval, MyResource_Activity.Key));
                                    Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, MyResource_Activity.listresource,"");

                                    MyResource_Activity.appRecyclerView.setAdapter(appAdapter);
                                    MyResource_Activity.appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case ("Stockiest"):
                        MyResource_Activity.Key= String.valueOf(sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(context)));
                        JSONArray jsonstock = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(context));
                        String strck_val = "";
                        if (jsonstock.length() > 0) {
                            for (int i = 0; i < jsonstock.length(); i++) {
                                JSONObject jsonObject = jsonstock.getJSONObject(i);
                                if(!strck_val.equals(jsonObject.getString("Code"))){
                                    strck_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));

                                    MyResource_Activity.listresource.add(new Resourcemodel_class(custom_name, cluster, "", "", "",Lat,Long,strck_val, MyResource_Activity.Key));

                                    Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, MyResource_Activity.listresource,"");
                                    MyResource_Activity.appRecyclerView.setAdapter(appAdapter);
                                    MyResource_Activity.appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;

                    case ("Unlisted doctor"):
                        MyResource_Activity.Key= String.valueOf(sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(context)));
                        JSONArray jsonunlisted = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(context));
                        String unlist_val = "";
                        if (jsonunlisted.length() > 0) {
                            for (int i = 0; i < jsonunlisted.length(); i++) {
                                JSONObject jsonObject = jsonunlisted.getJSONObject(i);

                                if(!unlist_val.equals(jsonObject.getString("Code"))){
                                    unlist_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String category = (jsonObject.getString("CategoryName"));
                                    String specialty = (jsonObject.getString("SpecialtyName"));
                                    String Lat = (jsonObject.getString("lat"));
                                    String Long = (jsonObject.getString("long"));

                                    MyResource_Activity.listresource.add(new Resourcemodel_class(custom_name, cluster, category, "", specialty,Lat,Long,unlist_val, MyResource_Activity.Key));

                                    Res_sidescreenAdapter appAdapter = new Res_sidescreenAdapter(context, MyResource_Activity.listresource,"");
                                    MyResource_Activity.appRecyclerView.setAdapter(appAdapter);
                                    MyResource_Activity.appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case ("Hospital"):


                        break;
                    case ("Cip"):


                        break;
                    case ("Input"):
                        MyResource_Activity.Key= String.valueOf(sqLite.getMasterSyncDataByKey(Constants.INPUT ));
                        JSONArray jsoninput = sqLite.getMasterSyncDataByKey(Constants.INPUT);
                        String input_val = "";
                        if (jsoninput.length() > 0) {
                            for (int i = 0; i < jsoninput.length(); i++) {
                                JSONObject jsonObject = jsoninput.getJSONObject(i);
                                if(!input_val.equals(jsonObject.getString("Code"))){
//                                input_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));


                                    MyResource_Activity.listresource.add(new Resourcemodel_class(custom_name, "", "", "", "","","","",""));

                                    Res_sidescreenAdapter appAdapter3 = new Res_sidescreenAdapter(context, MyResource_Activity.listresource,"1");
                                    MyResource_Activity.appRecyclerView.setAdapter(appAdapter3);
                                    MyResource_Activity.appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter3.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case ("Product"):
                        MyResource_Activity.Key= String.valueOf(sqLite.getMasterSyncDataByKey(Constants.PRODUCT ));

                        JSONArray jsonproduct = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
                        Log.d("listedpro", String.valueOf(jsonproduct));
                        String product_val = "";
                        if (jsonproduct.length() > 0) {
                            for (int i = 0; i < jsonproduct.length(); i++) {
                                JSONObject jsonObject = jsonproduct.getJSONObject(i);
                                if(!product_val.equals(jsonObject.getString("Code"))){
//                                product_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String product_type = (jsonObject.getString("Product_Mode"));


                                    MyResource_Activity.listresource.add(new Resourcemodel_class(custom_name, "", product_type, "", "","","","",""));

                                    Res_sidescreenAdapter appAdapter3 = new Res_sidescreenAdapter(context, MyResource_Activity.listresource,"1");
                                    MyResource_Activity.appRecyclerView.setAdapter(appAdapter3);
                                    MyResource_Activity.appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter3.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case ("Culster"):
                        MyResource_Activity.Key= String.valueOf(sqLite.getMasterSyncDataByKey(Constants.CLUSTER )+ SharedPref.getHqCode(context));
                        JSONArray jsonculst = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + SharedPref.getHqCode(context));
                        String culst_val = "";
                        if (jsonculst.length() > 0) {
                            for (int i = 0; i < jsonculst.length(); i++) {
                                JSONObject jsonObject = jsonculst.getJSONObject(i);
                                if(!culst_val.equals(jsonObject.getString("Code"))){
//                                culst_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    MyResource_Activity.listresource.add(new Resourcemodel_class(custom_name, "", "", "", "","","","",""));

                                    Res_sidescreenAdapter appAdapter3 = new Res_sidescreenAdapter(context, MyResource_Activity.listresource,"1");
                                    MyResource_Activity.appRecyclerView.setAdapter(appAdapter3);
                                    MyResource_Activity.appRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    appAdapter3.notifyDataSetChanged();
                                }
                            }
                        }
                        break;

                }
                if (MyResource_Activity.drawerLayout.isDrawerOpen(Gravity.END)) {
                    MyResource_Activity.drawerLayout.closeDrawer(Gravity.END);

                } else {
                    MyResource_Activity.drawerLayout.openDrawer(Gravity.END);

                }

                MyResource_Activity.search_list.addAll(MyResource_Activity.listresource);
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
        TextView usercount, username;
        public RelativeLayout list_resource;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usercount = itemView.findViewById(R.id.usercount);
            username = itemView.findViewById(R.id.username);
            list_resource = itemView.findViewById(R.id.list_resource);
        }
    }


}
