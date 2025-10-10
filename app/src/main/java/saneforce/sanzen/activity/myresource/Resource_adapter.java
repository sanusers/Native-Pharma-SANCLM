package saneforce.sanzen.activity.myresource;

import static saneforce.sanzen.activity.myresource.MyResource_Activity.Valcount;
import static saneforce.sanzen.activity.myresource.MyResource_Activity.et_Custsearch;
import static saneforce.sanzen.activity.myresource.MyResource_Activity.listresource;
import static saneforce.sanzen.activity.myresource.MyResource_Activity.search_list;

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
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.forms.weekoff.weekoff_viewscreen;
import saneforce.sanzen.activity.myresource.Categoryview.Cate_viewscreen;
import saneforce.sanzen.activity.myresource.Categoryview.DateSyncActivity;
import saneforce.sanzen.activity.myresource.StockBalanceview.StockBalanceScreen;
import saneforce.sanzen.activity.myresource.callstatusview.Callsstatus_screenview;
import saneforce.sanzen.activity.myresource.myresourcemodel.MyResourceInterface;
import saneforce.sanzen.activity.myresource.profile.ProfileViewScreen;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.utility.TimeUtils;

public class Resource_adapter extends RecyclerView.Adapter<Resource_adapter.ViewHolder> {
    public static String listedres;

    ArrayList<Resourcemodel_class> listeduser;

    Context context;

    HashSet<String> uniqueValues = new HashSet<>();
    HashMap<String, Integer> idCounts = new HashMap<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    String listed, pos_check = "";
    public static String rec_val = "";
    String synhqval1;
    MyResourceInterface myResourceInterface;

    public Resource_adapter(Context context, ArrayList<Resourcemodel_class> listeduser, String synhqval1, MyResourceInterface myResourceInterface) {
        this.context = context;
        this.listeduser = listeduser;
        this.synhqval1 = synhqval1;
        this.myResourceInterface = myResourceInterface;
        roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();
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

        holder.username.setText(app_adapt.getListed_data());
        holder.usercount.setText(app_adapt.getListed_count());
        listedres = app_adapt.getListed_data();
        holder.list_resource.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
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
                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.DOCTOR + synhqval1);
                            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + synhqval1).getMasterSyncDataJsonArray();
                            Valcount = "";
                            String docval = "";
                            pos_check = "";
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (!docval.equals(jsonObject.optString("Code"))) {
                                        docval = jsonObject.optString("Code");
                                        String custom_name = (jsonObject.optString("Name"));
                                        String category = (jsonObject.optString("Category"));
                                        String CategoryCode = (jsonObject.optString("CategoryCode"));
                                        String SpecialtyCode = (jsonObject.optString("SpecialtyCode"));
                                        String specialty = (jsonObject.optString("Specialty"));
                                        String cluster = (jsonObject.optString("Town_Name"));
                                        String Lat = (jsonObject.optString("Lat"));
                                        String Long = (jsonObject.optString("Long"));
                                        String Addrs = (jsonObject.optString("ResAddr"));
                                        String Mobile = (jsonObject.optString("Mobile"));
                                        String Phone = (jsonObject.optString("Phone"));
                                        JSONObject dobObject = jsonObject.getJSONObject("DctrDOB");
                                        String DOBDate = (dobObject.optString("date"));
                                        String DOB = DOBDate.split(" ")[0];
                                        JSONObject dowObject = jsonObject.getJSONObject("DctrDOW");
                                        String DOWDate = (dowObject.optString("date"));
                                        String DOW = DOWDate.split(" ")[0];
                                        String DrEmail = (jsonObject.optString("DrEmail"));
                                        String Qual = (jsonObject.optString("DrDesig"));
                                        String Qual_code = (jsonObject.optString("DocQuacode"));
                                        String ListedDr_Sex = (jsonObject.optString("ListedDr_Sex"));
                                        String Town_Code = (jsonObject.optString("Town_Code"));
                                        String Town_Name = (jsonObject.optString("Town_Name"));
                                        String Tag_count = (jsonObject.optString("GEOTagCnt"));
                                        String Max_count = (jsonObject.optString("MaxGeoMap"));
                                        String Class=(jsonObject.optString("Doc_Class_ShortName"));
                                        Log.e("dcr_doctorTown_Name", Town_Name);

                                        listresource.add(new Resourcemodel_class(docval, custom_name, cluster, category, CategoryCode, Class, SpecialtyCode, specialty, Lat, Long, docval, MyResource_Activity.Key, Qual, Addrs, DOB, DOW, Mobile, Phone, DrEmail, ListedDr_Sex, Town_Code, Town_Name, "D", "", "", "", "", "", "", "",Tag_count,Max_count,Qual_code));


                                    }
                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource,Valcount,synhqval1);
                            break;

                        case ("2"):
                            rec_val = "C";
                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.CHEMIST + synhqval1);
                            JSONArray jsonchemist = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + synhqval1).getMasterSyncDataJsonArray();
                            String chmval = "";
                            Valcount = "";
                            if (jsonchemist.length() > 0) {
                                for (int i = 0; i < jsonchemist.length(); i++) {
                                    JSONObject jsonObject = jsonchemist.getJSONObject(i);
                                    if (!chmval.equals(jsonObject.optString("Code"))) {
                                        chmval = jsonObject.optString("Code");
                                        String custom_name = (jsonObject.optString("Name"));
                                        String Code = (jsonObject.optString("Code"));
                                        String cluster = (jsonObject.optString("Town_Name"));
                                        String Catcode=(jsonObject.optString("Chm_cat"));
                                        String Lat = (jsonObject.optString("lat"));
                                        String Long = (jsonObject.optString("long"));
                                        String Tag_count = (jsonObject.optString("GEOTagCnt"));
                                        String Max_count = (jsonObject.optString("MaxGeoMap"));
                                        String Chemists_Mobile = (jsonObject.optString("Chemists_Mobile"));
                                        String Chemists_Phone = (jsonObject.optString("Chemists_Phone"));
                                        String Chemists_Email = (jsonObject.optString("Chemists_Email"));
                                        String addrs = (jsonObject.optString("Addr"));


                                        listresource.add(new Resourcemodel_class(Code, custom_name, cluster, "", Catcode, "", "", "", Lat, Long, chmval, MyResource_Activity.Key, "", addrs, "", "", Chemists_Mobile, Chemists_Phone, Chemists_Email, "", "", cluster, "C", "", "", "", "", "", "", "",Tag_count,Max_count,""));

                                    }
                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource,Valcount,synhqval1);
                            break;

                        case ("3"):
                            rec_val = "S";
                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.STOCKIEST + synhqval1);
                            JSONArray jsonstock = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + synhqval1).getMasterSyncDataJsonArray();
                            String strck_val = "";
                            Valcount = "";
                            if (jsonstock.length() > 0) {
                                for (int i = 0; i < jsonstock.length(); i++) {
                                    JSONObject jsonObject = jsonstock.getJSONObject(i);
                                    if (!strck_val.equals(jsonObject.optString("Code"))) {
                                        strck_val = jsonObject.optString("Code");
                                        String Code = (jsonObject.optString("Code"));
                                        String custom_name = (jsonObject.optString("Name"));
                                        String cluster = (jsonObject.optString("Town_Name"));
                                        String Lat = (jsonObject.optString("lat"));
                                        String Long = (jsonObject.optString("long"));
                                        String Tag_count = (jsonObject.optString("GEOTagCnt"));
                                        String Max_count = (jsonObject.optString("MaxGeoMap"));

                                        String Stockiest_Phone = (jsonObject.optString("Stockiest_Phone"));
                                        String Stockiest_Mobile = (jsonObject.optString("Stockiest_Mobile"));
                                        String Stockiest_Email = (jsonObject.optString("Stockiest_Email"));
                                        String Addr = (jsonObject.optString("Addr"));


                                        listresource.add(new Resourcemodel_class(Code, custom_name, cluster, "", "", "", "", "", Lat, Long, strck_val, MyResource_Activity.Key, "", Addr, "", "", Stockiest_Mobile, Stockiest_Phone, Stockiest_Email, "", "", "", "S", "", "", "", "", "", "", "",Tag_count,Max_count,""));


                                    }
                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource,Valcount,synhqval1);
                            break;

                        case ("4"):
                            rec_val = "U";
                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR + synhqval1);
                            JSONArray jsonunlisted = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + synhqval1).getMasterSyncDataJsonArray();
                            String unlist_val = "";
                            Valcount = "";
                            if (jsonunlisted.length() > 0) {
                                for (int i = 0; i < jsonunlisted.length(); i++) {
                                    JSONObject jsonObject = jsonunlisted.getJSONObject(i);

                                    if (!unlist_val.equals(jsonObject.optString("Code"))) {
                                        unlist_val = jsonObject.optString("Code");
                                        String Code = jsonObject.optString("Code");
                                        String custom_name = (jsonObject.optString("Name"));
                                        String cluster = (jsonObject.optString("Town_Name"));
                                        String category = (jsonObject.optString("CategoryName"));
                                        String specialty = (jsonObject.optString("SpecialtyName"));
                                        String Lat = (jsonObject.optString("lat"));
                                        String Long = (jsonObject.optString("long"));
                                        String quacode = (jsonObject.optString("Qual"));
//                                    String DOB = (jsonObject.optString("DOB"));
//                                    String DOW = (jsonObject.optString("DOW"));
                                        JSONObject dobObject = jsonObject.getJSONObject("UnlstDOB");
                                        String DOBDate = (dobObject.optString("date"));
                                        String DOB = DOBDate.split(" ")[0];
                                        JSONObject dowObject = jsonObject.getJSONObject("UnlstDOW");
                                        String DOWDate = (dowObject.optString("date"));
                                        String DOW = DOWDate.split(" ")[0];
                                        String Category = (jsonObject.optString("Category"));
                                        String Tag_count = (jsonObject.optString("GEOTagCnt"));
                                        String Max_count = (jsonObject.optString("MaxGeoMap"));

                                        String Specialty = (jsonObject.optString("Specialty"));
                                        String Qual = (jsonObject.optString("Doc_QuaName"));
                                        String Email = (jsonObject.optString("Email"));
                                        String Mobile = (jsonObject.optString("Mobile"));
                                        String Phone = (jsonObject.optString("Phone"));
                                        String addr = (jsonObject.optString("Addrs"));

                                        listresource.add(new Resourcemodel_class(Code, custom_name, cluster, category, Category, "", Specialty, specialty, Lat, Long, unlist_val, MyResource_Activity.Key, Qual, addr, DOB, DOW, Mobile, Phone, Email, "", "", cluster, "U", "", "", "", "", "", "", "", Tag_count,Max_count,quacode));
                                    }
                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource,Valcount,synhqval1);
                            break;

//                    /*    case ("1"):
//                            rec_val = "D";
//                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.DOCTOR + synhqval1);
//                            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + synhqval1).getMasterSyncDataJsonArray();
//                            Valcount = "";
//                            String docval = "";
//                            pos_check = "";
//                            if (jsonArray.length() > 0) {
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                    if (!docval.equals(jsonObject.getString("Code"))) {
//                                        docval = jsonObject.getString("Code");
//                                        String custom_name = (jsonObject.getString("Name"));
//                                        String category = (jsonObject.getString("Category"));
//                                        String CategoryCode = (jsonObject.getString("CategoryCode"));
//                                        String SpecialtyCode = (jsonObject.getString("SpecialtyCode"));
//                                        String specialty = (jsonObject.getString("Specialty"));
//                                        String cluster = (jsonObject.getString("Town_Name"));
//                                        String Lat = (jsonObject.getString("Lat"));
//                                        String Long = (jsonObject.getString("Long"));
//                                        String Addrs = (jsonObject.getString("ResAddr"));
//                                        String Mobile = (jsonObject.getString("Mobile"));
//                                        String Phone = (jsonObject.getString("Phone"));
//                                        JSONObject dobObject = jsonObject.getJSONObject("DctrDOB");
//                                        String DOBDate = (dobObject.getString("date"));
//                                        String DOB = DOBDate.split(" ")[0];
//                                        JSONObject dowObject = jsonObject.getJSONObject("DctrDOW");
//                                        String DOWDate = (dowObject.getString("date"));
//                                        String DOW = DOWDate.split(" ")[0];
//                                        String DrEmail = (jsonObject.getString("DrEmail"));
//                                        String Qual = (jsonObject.getString("DrDesig"));
//                                        String Qual_code = (jsonObject.getString("DocQuacode"));
//                                        String ListedDr_Sex = (jsonObject.getString("ListedDr_Sex"));
//                                        String Town_Code = (jsonObject.getString("Town_Code"));
//                                        String Town_Name = (jsonObject.getString("Town_Name"));
//                                        String Tag_count = (jsonObject.getString("GEOTagCnt"));
//                                        String Max_count = (jsonObject.getString("Geototal"));
//                                        String Class = (jsonObject.getString("Doc_Class_ShortName"));
//                                        Log.e("dcr_doctorTown_Name", Town_Name);
//
//                                        listresource.add(new Resourcemodel_class(docval, custom_name, cluster, category, CategoryCode, Class, SpecialtyCode, specialty, Lat, Long, docval, MyResource_Activity.Key, Qual, Addrs, DOB, DOW, Mobile, Phone, DrEmail, ListedDr_Sex, Town_Code, Town_Name, "D", "", "", "", "", "", "", "", Tag_count, Max_count, Qual_code));
//
//                                    }
//                                }
//                            }
//                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
//                            search_list.addAll(listresource);
//                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
//                            break;*/
                    /*case ("1"):
                        rec_val = "D";
                        MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.DOCTOR_MAS + synhqval1);
                        MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.DOCTOR_GEO + synhqval1);
                        JSONArray jsonArray_Mas = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + synhqval1).getMasterSyncDataJsonArray();
                        JSONArray jsonArray_Geo = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_GEO + synhqval1).getMasterSyncDataJsonArray();
                        Valcount = "";
                        String docgeo_val = "";
                        pos_check = "";

                        HashMap<String, JSONObject> docObj = new HashMap<>();
                        for (int i = 0; i < jsonArray_Mas.length(); i++) {
                            JSONObject jsonObject = jsonArray_Mas.getJSONObject(i);
                            String code = jsonObject.optString("Code");
                            if (!code.isEmpty()) {
                                docObj.put(code, jsonObject);
                            } else {
                                Log.d("Merge", "Skipping DOCTOR_MAS object with empty 'Code': " + jsonObject.toString());
                            }
                        }
                        for (int i = 0; i < jsonArray_Geo.length(); i++) {
                            JSONObject jsonObject_geo = jsonArray_Geo.getJSONObject(i);
                            String code = jsonObject_geo.optString("Code");
                            if (code.isEmpty()) {
                                Log.w("Merge", "Skipping GEO object with empty 'Code': " + jsonObject_geo.toString());
                                continue;
                            }
                            if (docObj.containsKey(code)) {
                                JSONObject existingObject = docObj.get(code);
                                jsonObject_geo.keys().forEachRemaining(key -> {
                                    System.out.println("Merging keys Res_Adp dr:" + key);
                                    try {
                                        assert existingObject != null;
                                        existingObject.put(key, jsonObject_geo.get(key));
                                    } catch (JSONException e) {
                                        Log.e("MergeError", "Error merging key: " + key);
                                    }
                                });
                            }
                        }
                        JSONArray jsonArray_master = new JSONArray(docObj.values());
                        if (jsonArray_master.length() > 0) {
                            for (int i = 0; i < jsonArray_master.length(); i++) {
                                JSONObject jsonObject = jsonArray_master.getJSONObject(i);
                                if (!docgeo_val.equals(jsonObject.getString("Code"))) {
                                    docgeo_val = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String category = (jsonObject.getString("Category"));
                                    String CategoryCode = (jsonObject.getString("CategoryCode"));
                                    String SpecialtyCode = (jsonObject.getString("SpecialtyCode"));
                                    String specialty = (jsonObject.getString("Specialty"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.optString("lat"));
                                    String Long = (jsonObject.optString("long"));
                                    String Addrs = (jsonObject.getString("ResAddr"));
                                    String Mobile = (jsonObject.getString("Mobile"));
                                    String Phone = (jsonObject.getString("Phone"));
                                    JSONObject dobObject = jsonObject.getJSONObject("DctrDOB");
                                    String DOBDate = (dobObject.getString("date"));
                                    String DOB = DOBDate.split(" ")[0];
                                    JSONObject dowObject = jsonObject.getJSONObject("DctrDOW");
                                    String DOWDate = (dowObject.optString("date"));
                                    String DOW = DOWDate.split(" ")[0];
                                    String DrEmail = (jsonObject.getString("DrEmail"));
                                    String Qual = (jsonObject.getString("DrDesig"));
                                    String Qual_code = (jsonObject.getString("DocQuacode"));
                                    String ListedDr_Sex = (jsonObject.getString("ListedDr_Sex"));
                                    String Town_Code = (jsonObject.getString("Town_Code"));
                                    String Town_Name = (jsonObject.getString("Town_Name"));
                                    String Tag_count = (jsonObject.optString("GEOTagedCnt"));
                                    String Max_count = (jsonObject.optString("Geototal"));
                                    String Class = (jsonObject.getString("Doc_Class_ShortName"));
//                                        Log.e("dcr_doctorTown_Name", Town_Name);
                                    listresource.add(new Resourcemodel_class(docgeo_val, custom_name, cluster, category, CategoryCode, Class, SpecialtyCode, specialty, Lat, Long, docgeo_val, MyResource_Activity.Key, Qual, Addrs, DOB, DOW, Mobile, Phone, DrEmail, ListedDr_Sex, Town_Code, Town_Name, "D", "", "", "", "", "", "", "", Tag_count, Max_count, Qual_code));
                                    Collections.sort(listresource, new Comparator<Resourcemodel_class>() {
                                        @Override
                                        public int compare(Resourcemodel_class o1, Resourcemodel_class o2) {
                                            return o1.getDcr_name().compareToIgnoreCase(o2.getDcr_name());
                                        }
                                    });
                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        search_list.addAll(listresource);
                        myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                        break;
*/
// /*                       case ("2"):
//                            rec_val = "C";
//                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.CHEMIST + synhqval1);
//                            JSONArray jsonchemist = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + synhqval1).getMasterSyncDataJsonArray();
//                            String chmval = "";
//                            Valcount = "";
//                            if (jsonchemist.length() > 0) {
//                                for (int i = 0; i < jsonchemist.length(); i++) {
//                                    JSONObject jsonObject = jsonchemist.getJSONObject(i);
//                                    if (!chmval.equals(jsonObject.getString("Code"))) {
//                                        chmval = jsonObject.getString("Code");
//                                        String custom_name = (jsonObject.getString("Name"));
//                                        String Code = (jsonObject.getString("Code"));
//                                        String cluster = (jsonObject.getString("Town_Name"));
//                                        String Catcode = (jsonObject.optString("Chm_cat"));
//                                        String Lat = (jsonObject.getString("lat"));
//                                        String Long = (jsonObject.getString("long"));
//                                        String Tag_count = (jsonObject.getString("GEOTagCnt"));
//                                        String Max_count = (jsonObject.getString("Geototal"));
//                                        String Chemists_Mobile = (jsonObject.getString("Chemists_Mobile"));
//                                        String Chemists_Phone = (jsonObject.getString("Chemists_Phone"));
//                                        String Chemists_Email = (jsonObject.getString("Chemists_Email"));
//                                        String addrs = (jsonObject.getString("Addr"));
//
//
//                                        listresource.add(new Resourcemodel_class(Code, custom_name, cluster, "", Catcode, "", "", "", Lat, Long, chmval, MyResource_Activity.Key, "", addrs, "", "", Chemists_Mobile, Chemists_Phone, Chemists_Email, "", "", cluster, "C", "", "", "", "", "", "", "", Tag_count, Max_count, ""));
//
//                                    }
//                                }
//                            }
//                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
//                            search_list.addAll(listresource);
//                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
//                            break;*/
                   /* case ("2"):
                        rec_val = "C";
                        MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.CHEMIST_MAS + synhqval1);
                        MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.CHEMIST_GEO + synhqval1);
                        JSONArray jsonchemist_mas = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + synhqval1).getMasterSyncDataJsonArray();
                        JSONArray jsonchemist_geo = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_GEO + synhqval1).getMasterSyncDataJsonArray();
                        String chm_masval = "";
                        Valcount = "";
                        HashMap<String, JSONObject> docObj_che = new HashMap<>();
                        for (int i = 0; i < jsonchemist_mas.length(); i++) {
                            JSONObject jsonObject = jsonchemist_mas.getJSONObject(i);
                            String code = jsonObject.optString("Code");
                            if (!code.isEmpty()) {
                                docObj_che.put(code, jsonObject);
                            } else {
                                Log.d("Merge", "Skipping DOCTOR_MAS object with empty 'Code': " + jsonObject.toString());
                            }
                        }
                        for (int i = 0; i < jsonchemist_geo.length(); i++) {
                            JSONObject jsonObject_geo = jsonchemist_geo.getJSONObject(i);
                            String code = jsonObject_geo.optString("Code");
                            if (code.isEmpty()) {
                                Log.w("Merge", "Skipping GEO object with empty 'Code': " + jsonObject_geo.toString());
                                continue;
                            }
                            if (docObj_che.containsKey(code)) {
                                JSONObject existingObject = docObj_che.get(code);
                                jsonObject_geo.keys().forEachRemaining(key -> {
                                    System.out.println("Merging keys Res_Adp Che:" + key);
                                    try {
                                        assert existingObject != null;
                                        existingObject.put(key, jsonObject_geo.get(key));
                                    } catch (JSONException e) {
                                        Log.e("MergeError", "Error merging key: " + key);
                                    }
                                });
                            }
                        }
                        JSONArray jsonChemist_master = new JSONArray(docObj_che.values());
                        if (jsonChemist_master.length() > 0) {
                            for (int i = 0; i < jsonChemist_master.length(); i++) {
                                JSONObject jsonObject = jsonChemist_master.getJSONObject(i);
                                if (!chm_masval.equals(jsonObject.getString("Code"))) {
                                    chm_masval = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String Code = (jsonObject.getString("Code"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Catcode = (jsonObject.getString("Chm_cat"));
                                    String Lat = (jsonObject.optString("lat"));
                                    String Long = (jsonObject.optString("long"));
                                    String Tag_count = (jsonObject.optString("GEOTagCnt"));
                                    String Max_count = (jsonObject.optString("Geototal"));
                                    String Chemists_Mobile = (jsonObject.getString("Chemists_Mobile"));
                                    String Chemists_Phone = (jsonObject.getString("Chemists_Phone"));
                                    String Chemists_Email = (jsonObject.getString("Chemists_Email"));
                                    String addrs = (jsonObject.optString("Addr"));
                                    listresource.add(new Resourcemodel_class(Code, custom_name, cluster, "", Catcode, "", "", "", Lat, Long, chm_masval, MyResource_Activity.Key, "", addrs, "", "", Chemists_Mobile, Chemists_Phone, Chemists_Email, "", "", cluster, "C", "", "", "", "", "", "", "", Tag_count, Max_count, ""));
                                    Collections.sort(listresource, new Comparator<Resourcemodel_class>() {
                                        @Override
                                        public int compare(Resourcemodel_class o1, Resourcemodel_class o2) {
                                            return o1.getDcr_name().compareToIgnoreCase(o2.getDcr_name());
                                        }
                                    });

                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        search_list.addAll(listresource);
                        myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                        break;
*/
//                    /*    case ("3"):
//                            rec_val = "S";
//                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.STOCKIEST + synhqval1);
//                            JSONArray jsonstock = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + synhqval1).getMasterSyncDataJsonArray();
//                            String strck_val = "";
//                            Valcount = "";
//                            if (jsonstock.length() > 0) {
//                                for (int i = 0; i < jsonstock.length(); i++) {
//                                    JSONObject jsonObject = jsonstock.getJSONObject(i);
//                                    if (!strck_val.equals(jsonObject.getString("Code"))) {
//                                        strck_val = jsonObject.getString("Code");
//                                        String Code = (jsonObject.getString("Code"));
//                                        String custom_name = (jsonObject.getString("Name"));
//                                        String cluster = (jsonObject.getString("Town_Name"));
//                                        String Lat = (jsonObject.getString("lat"));
//                                        String Long = (jsonObject.getString("long"));
//                                        String Tag_count = (jsonObject.getString("GEOTagedCnt"));
//                                        String Max_count = (jsonObject.getString("Geototal"));
//
//                                        String Stockiest_Phone = (jsonObject.getString("Stockiest_Phone"));
//                                        String Stockiest_Mobile = (jsonObject.getString("Stockiest_Mobile"));
//                                        String Stockiest_Email = (jsonObject.getString("Stockiest_Email"));
//                                        String Addr = (jsonObject.getString("Addr"));
//
//
//                                        listresource.add(new Resourcemodel_class(Code, custom_name, cluster, "", "", "", "", "", Lat, Long, strck_val, MyResource_Activity.Key, "", Addr, "", "", Stockiest_Mobile, Stockiest_Phone, Stockiest_Email, "", "", "", "S", "", "", "", "", "", "", "", Tag_count, Max_count, ""));
//
//
//                                    }
//                                }
//                            }
//                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
//                            search_list.addAll(listresource);
//                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
//                            break;*/
                  /*  case ("3"):
                        rec_val = "S";
                        MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.STOCKIEST_MAS + synhqval1);
                        MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.STOCKIEST_GEO + synhqval1);
                        JSONArray jsonStockiest_mas = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_MAS + synhqval1).getMasterSyncDataJsonArray();
                        JSONArray jsonStockiest_geo = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_GEO + synhqval1).getMasterSyncDataJsonArray();
                        String stk_masval = "";
                        Valcount = "";
                        HashMap<String, JSONObject> docObj_stk = new HashMap<>();
                        for (int i = 0; i < jsonStockiest_mas.length(); i++) {
                            JSONObject jsonObject = jsonStockiest_mas.getJSONObject(i);
                            String code = jsonObject.optString("Code");
                            if (!code.isEmpty()) {
                                docObj_stk.put(code, jsonObject);
                            } else {
                                Log.d("Merge", "Skipping DOCTOR_MAS object with empty 'Code': " + jsonObject.toString());
                            }
                        }
                        for (int i = 0; i < jsonStockiest_geo.length(); i++) {
                            JSONObject jsonObject_geo = jsonStockiest_geo.getJSONObject(i);
                            String code = jsonObject_geo.optString("Code");
                            if (code.isEmpty()) {
                                Log.w("Merge", "Skipping GEO object with empty 'Code': " + jsonObject_geo.toString());
                                continue;
                            }
                            if (docObj_stk.containsKey(code)) {
                                JSONObject existingObject = docObj_stk.get(code);
                                jsonObject_geo.keys().forEachRemaining(key -> {
                                    System.out.println("Merging keys Res_Adp Che:" + key);
                                    try {
                                        assert existingObject != null;
                                        existingObject.put(key, jsonObject_geo.get(key));
                                    } catch (JSONException e) {
                                        Log.e("MergeError", "Error merging key: " + key);
                                    }
                                });
                            }
                        }
                        JSONArray jsonStockiest_master = new JSONArray(docObj_stk.values());
                        if (jsonStockiest_master.length() > 0) {
                            for (int i = 0; i < jsonStockiest_master.length(); i++) {
                                JSONObject jsonObject = jsonStockiest_master.getJSONObject(i);
                                if (!stk_masval.equals(jsonObject.getString("Code"))) {
                                    stk_masval = jsonObject.getString("Code");
                                    String Code = (jsonObject.getString("Code"));
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String Lat = (jsonObject.optString("lat"));
                                    String Long = (jsonObject.optString("long"));
                                    String Tag_count = (jsonObject.optString("GEOTagedCnt"));
                                    String Max_count = (jsonObject.optString("Geototal"));

                                    String Stockiest_Phone = (jsonObject.getString("Stockiest_Phone"));
                                    String Stockiest_Mobile = (jsonObject.getString("Stockiest_Mobile"));
                                    String Stockiest_Email = (jsonObject.getString("Stockiest_Email"));
                                    String Addr = (jsonObject.optString("Addr"));


                                    listresource.add(new Resourcemodel_class(Code, custom_name, cluster, "", "", "", "", "", Lat, Long, stk_masval, MyResource_Activity.Key, "", Addr, "", "", Stockiest_Mobile, Stockiest_Phone, Stockiest_Email, "", "", "", "S", "", "", "", "", "", "", "", Tag_count, Max_count, ""));

                                    Collections.sort(listresource, new Comparator<Resourcemodel_class>() {
                                        @Override
                                        public int compare(Resourcemodel_class o1, Resourcemodel_class o2) {
                                            return o1.getDcr_name().compareToIgnoreCase(o2.getDcr_name());
                                        }
                                    });

                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        search_list.addAll(listresource);
                        myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                        break;*/

//                     /*   case ("4"):
//                            rec_val = "U";
//                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR + synhqval1);
//                            JSONArray jsonunlisted = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + synhqval1).getMasterSyncDataJsonArray();
//                            String unlist_val = "";
//                            Valcount = "";
//                            if (jsonunlisted.length() > 0) {
//                                for (int i = 0; i < jsonunlisted.length(); i++) {
//                                    JSONObject jsonObject = jsonunlisted.getJSONObject(i);
//
//                                    if (!unlist_val.equals(jsonObject.getString("Code"))) {
//                                        unlist_val = jsonObject.getString("Code");
//                                        String Code = jsonObject.getString("Code");
//                                        String custom_name = (jsonObject.getString("Name"));
//                                        String cluster = (jsonObject.getString("Town_Name"));
//                                        String category = (jsonObject.getString("CategoryName"));
//                                        String specialty = (jsonObject.getString("SpecialtyName"));
//                                        String Lat = (jsonObject.getString("lat"));
//                                        String Long = (jsonObject.getString("long"));
//                                        String quacode = (jsonObject.getString("Qual"));
////                                    String DOB = (jsonObject.getString("DOB"));
////                                    String DOW = (jsonObject.getString("DOW"));
//                                        JSONObject dobObject = jsonObject.getJSONObject("UnlstDOB");
//                                        String DOBDate = (dobObject.getString("date"));
//                                        String DOB = DOBDate.split(" ")[0];
//                                        JSONObject dowObject = jsonObject.getJSONObject("UnlstDOW");
//                                        String DOWDate = (dowObject.getString("date"));
//                                        String DOW = DOWDate.split(" ")[0];
//                                        String Category = (jsonObject.getString("Category"));
//                                        String Tag_count = (jsonObject.getString("GEOTagedCnt"));
//                                        String Max_count = (jsonObject.getString("Geototal"));
//
//                                        String Specialty = (jsonObject.getString("Specialty"));
//                                        String Qual = (jsonObject.getString("Doc_QuaName"));
//                                        String Email = (jsonObject.getString("Email"));
//                                        String Mobile = (jsonObject.getString("Mobile"));
//                                        String Phone = (jsonObject.getString("Phone"));
//                                        String addr = (jsonObject.getString("Addrs"));
//
//                                        listresource.add(new Resourcemodel_class(Code, custom_name, cluster, category, Category, "", Specialty, specialty, Lat, Long, unlist_val, MyResource_Activity.Key, Qual, addr, DOB, DOW, Mobile, Phone, Email, "", "", cluster, "U", "", "", "", "", "", "", "", Tag_count, Max_count, quacode));
//                                    }
//                                }
//                            }
//                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
//                            search_list.addAll(listresource);
//                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
//                            break;*/
                   /* case ("4"):
                        rec_val = "U";
                        MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR_MAS + synhqval1);
                        MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.UNLISTED_DOCTOR_GEO + synhqval1);
                        JSONArray jsonUnlisted_mas = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR_MAS + synhqval1).getMasterSyncDataJsonArray();
                        JSONArray jsonUnlisted_geo = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR_GEO + synhqval1).getMasterSyncDataJsonArray();
                        String unlistMas_val = "";
                        Valcount = "";
                        HashMap<String, JSONObject> docObj_Unld = new HashMap<>();
                        for (int i = 0; i < jsonUnlisted_mas.length(); i++) {
                            JSONObject jsonObject = jsonUnlisted_mas.getJSONObject(i);
                            String code = jsonObject.optString("Code");
                            if (!code.isEmpty()) {
                                docObj_Unld.put(code, jsonObject);
                            } else {
                                Log.d("Merge", "Skipping DOCTOR_MAS object with empty 'Code': " + jsonObject.toString());
                            }
                        }
                        for (int i = 0; i < jsonUnlisted_geo.length(); i++) {
                            JSONObject jsonObject_geo = jsonUnlisted_geo.getJSONObject(i);
                            String code = jsonObject_geo.optString("Code");
                            if (code.isEmpty()) {
                                Log.w("Merge", "Skipping GEO object with empty 'Code': " + jsonObject_geo.toString());
                                continue;
                            }
                            if (docObj_Unld.containsKey(code)) {
                                JSONObject existingObject = docObj_Unld.get(code);
                                jsonObject_geo.keys().forEachRemaining(key -> {
                                    System.out.println("Merging keys Res_Adp dr:" + key);
                                    try {
                                        assert existingObject != null;
                                        existingObject.put(key, jsonObject_geo.get(key));
                                    } catch (JSONException e) {
                                        Log.e("MergeError", "Error merging key: " + key);
                                    }
                                });
                            }
                        }

                        if (jsonUnlisted_mas.length() > 0) {
                            for (int u = 0; u < jsonUnlisted_mas.length(); u++) {
                                JSONObject jsonObject = jsonUnlisted_mas.getJSONObject(u);

                                if (!unlistMas_val.equals(jsonObject.getString("Code"))) {
                                    unlistMas_val = jsonObject.getString("Code");
                                    String Code = jsonObject.getString("Code");
                                    String custom_name = (jsonObject.getString("Name"));
                                    String cluster = (jsonObject.getString("Town_Name"));
                                    String category = (jsonObject.getString("CategoryName"));
                                    String specialty = (jsonObject.getString("SpecialtyName"));
                                    String Lat = (jsonObject.optString("lat"));
                                    String Long = (jsonObject.optString("long"));
                                    String quacode = (jsonObject.getString("Qual"));
//                                    String DOB = (jsonObject.getString("DOB"));
//                                    String DOW = (jsonObject.getString("DOW"));
                                    JSONObject dobObject = jsonObject.getJSONObject("UnlstDOB");
                                    String DOBDate = (dobObject.getString("date"));
                                    String DOB = DOBDate.split(" ")[0];
                                    JSONObject dowObject = jsonObject.getJSONObject("UnlstDOW");
                                    String DOWDate = (dowObject.getString("date"));
                                    String DOW = DOWDate.split(" ")[0];
                                    String Category = (jsonObject.getString("Category"));
                                    String Tag_count = (jsonObject.optString("GEOTagedCnt"));
                                    String Max_count = (jsonObject.optString("Geototal"));

                                    String Specialty = (jsonObject.getString("Specialty"));
                                    String Qual = (jsonObject.getString("Doc_QuaName"));
                                    String Email = (jsonObject.getString("Email"));
                                    String Mobile = (jsonObject.getString("Mobile"));
                                    String Phone = (jsonObject.getString("Phone"));
                                    String addr = (jsonObject.optString("Addrs"));

                                    listresource.add(new Resourcemodel_class(Code, custom_name, cluster, category, Category, "", Specialty, specialty, Lat, Long, unlistMas_val, MyResource_Activity.Key, Qual, addr, DOB, DOW, Mobile, Phone, Email, "", "", cluster, "U", "", "", "", "", "", "", "", Tag_count, Max_count, quacode));
                                }
                            }
                        }
                        MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                        search_list.addAll(listresource);
                        myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                        break;*/

                        case ("5"):


                            break;

                        case ("6"):


                            break;
                        case ("7"):
                            rec_val = "I";
                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.INPUT);
                            JSONArray jsoninput = masterDataDao.getMasterDataTableOrNew(Constants.INPUT).getMasterSyncDataJsonArray();
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

                                        listresource.add(new Resourcemodel_class("", custom_name, "", "", "", "", "", "", Frm_val[0], Toval[0], "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                                    }
                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                            break;

                        case ("8"):
                            rec_val = "P";
                            Valcount = "1";

                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.PRODUCT);
                            JSONArray jsonproduct = masterDataDao.getMasterDataTableOrNew(Constants.PRODUCT).getMasterSyncDataJsonArray();
                            Log.d("listedpro", String.valueOf(jsonproduct));
                            String product_val = "";
                            if (jsonproduct.length() > 0) {
                                for (int i = 0; i < jsonproduct.length(); i++) {
                                    JSONObject jsonObject = jsonproduct.getJSONObject(i);
                                    if (!product_val.equals(jsonObject.getString("Code")) && (!jsonObject.getString("Code").equals("-1"))) {
                                        String custom_name = (jsonObject.getString("Name"));
                                        String product_type = (jsonObject.getString("Product_Mode"));

                                        listresource.add(new Resourcemodel_class("", custom_name, "", product_type, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                                    }
                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                            break;

                        case ("9"):
                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.CLUSTER) + synhqval1;
                            JSONArray jsonculst = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + synhqval1).getMasterSyncDataJsonArray();
                            String culst_val = "";
                            Valcount = "";
                            Valcount = "1";
                            if (jsonculst.length() > 0) {
                                for (int i = 0; i < jsonculst.length(); i++) {
                                    JSONObject jsonObject = jsonculst.getJSONObject(i);
                                    if (!culst_val.equals(jsonObject.getString("Code"))) {
//                                culst_val = jsonObject.getString("Code");
                                        String custom_name = (jsonObject.getString("Name"));
                                        listresource.add(new Resourcemodel_class("", custom_name, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                                    }
                                }
                            }

                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                            break;

                        case ("10"):

                            MyResource_Activity.binding.drawerLayout.closeDrawer(Gravity.END);
                            Intent l = new Intent(context, weekoff_viewscreen.class);
                            context.startActivity(l);
                            MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
                            MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            break;

                        case ("11")://Cate_viewscreen

                            Intent l2 = new Intent(context, Cate_viewscreen.class);
                            context.startActivity(l2);
                            MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
                            MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                            break;

                        case ("12"):
                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.WORK_TYPE);
                            JSONArray jsonWorkType = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
                            String workType_al = "";
                            String TPDCR = "";
                            Valcount = "1";
                            if (jsonWorkType.length() > 0) {
                                for (int bean = 0; bean < jsonWorkType.length(); bean++) {
                                    JSONObject jsonObject = jsonWorkType.getJSONObject(bean);
                                    String workType = jsonObject.getString("Name");
                                    String tpDcr = jsonObject.getString("TP_DCR");
                                    if (tpDcr.equals("T")) {
                                        TPDCR = "TP";
                                    } else {
                                        TPDCR = "TP , DCR";
                                    }
//                                System.out.println("jsonObjectWorkType--->" + jsonObject);
                                    listresource.add(new Resourcemodel_class("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", workType, TPDCR, "workType", "", "", "", "", "", "", ""));
                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                            break;

                        case ("13"):
                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.LEAVE_STATUS);
                            JSONArray leaveStatus = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray();
                            if (leaveStatus.length() > 0) {
                                for (int bean = 0; bean < leaveStatus.length(); bean++) {
                                    JSONObject jsonObject = leaveStatus.getJSONObject(bean);
                                    String leaveType = jsonObject.getString("Leave_Type_Code");
                                    String eligible = jsonObject.getString("Elig");
                                    String available = jsonObject.getString("Avail");
                                    String taken = jsonObject.getString("Taken");
                                    int eligibleCount = Integer.parseInt(eligible);
                                    int availableCount = Integer.parseInt(available);
                                    int takenCount = Integer.parseInt(taken);
                                    if (availableCount < 0) {
                                        available = "0";
                                    }
                                    if (eligibleCount < takenCount) {
                                        taken = eligible;
                                    }
                                    listresource.add(new Resourcemodel_class("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "leaveStatus", leaveType, eligible, available, taken, "", "", ""));

                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                            break;

                        case ("14"):

                            MyResource_Activity.Key = masterDataDao.getDataByKey(Constants.VISIT_CONTROL);
                            JSONArray jsonvst_ctl = masterDataDao.getMasterDataTableOrNew(Constants.VISIT_CONTROL).getMasterSyncDataJsonArray();
//                            JSONArray jsonvst_Doc = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + synhqval1).getMasterSyncDataJsonArray();
//                        JSONArray jsonvst_Doc = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + synhqval1).getMasterSyncDataJsonArray();
                            JSONArray jsonvst_Doc = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + synhqval1).getMasterSyncDataJsonArray();

                            uniqueValues.clear();
                            idCounts.clear();
                            Valcount = "2";

                            listed = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_31, CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
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

                                                listresource.add(new Resourcemodel_class("", custom_name, custom_id, town_name, "", Vist_Date, "", Dcr_count, listed, "", max_vistcount, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));

                                            }
                                        }
                                    }
                                }
                            }
                            MyResource_Activity.binding.drawerLayout.openDrawer(Gravity.END);
                            search_list.addAll(listresource);
                            myResourceInterface.onclickItem(listresource, Valcount, synhqval1);
                            break;

                        case ("15"):
                            MyResource_Activity.binding.drawerLayout.closeDrawer(Gravity.END);
                            Intent l1 = new Intent(context, Callsstatus_screenview.class);
                            context.startActivity(l1);
                            MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
                            MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            break;

                        case ("16"):
                            MyResource_Activity.binding.drawerLayout.closeDrawer(Gravity.END);
                            Intent intent = new Intent(context, DateSyncActivity.class);
                            context.startActivity(intent);
                            MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
                            MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            break;
                        case ("17")://Stock Balance

                            Intent l3 = new Intent(context, StockBalanceScreen.class);
                            context.startActivity(l3);
                            MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
                            MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            break;
                        case ("18"):
                            Intent profile = new Intent(context, ProfileViewScreen.class);
                            context.startActivity(profile);
                            MyResource_Activity.binding.layoutScrn.closeDrawer(Gravity.END);
                            MyResource_Activity.binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + app_adapt.getListed_data());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
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
