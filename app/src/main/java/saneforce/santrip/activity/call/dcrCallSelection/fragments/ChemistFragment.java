package saneforce.santrip.activity.call.dcrCallSelection.fragments;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.santrip.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.santrip.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;


public class ChemistFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> cusListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close;
    Button btn_apply;
//    SQLite sqLite;
    JSONArray jsonArray;
    TextView tv_hqName,tvTerritory,tv_filter_count;
    CommonUtilsMethods commonUtilsMethods;

    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    String TerritoryCode;

    ListView lv_terr;

    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment", "---" + "chemist");
        View v = inflater.inflate(R.layout.fragment_chemist, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_filter_count = v.findViewById(R.id.tv_filter_count);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);

//        sqLite = new SQLite(getContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();

        SetupAdapter();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);

        iv_filter.setOnClickListener(view -> {
            CustomizeFiltered();
        });

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        return v;
    }


    private void SetupAdapter() {
        cusListArrayList.clear();
        try {
//            jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + DcrCallTabLayoutActivity.TodayPlanSfCode);
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            Log.v("CheCall", "-che_full_length-" + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (SharedPref.getGeotagNeedChe(context).equalsIgnoreCase("1")) {
                    if (!jsonObject.getString("lat").isEmpty() && !jsonObject.getString("long").isEmpty()) {
                        if (SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                            if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                    cusListArrayList = SaveData(jsonObject, i);
                                }
                            }
                        } else {
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                            if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                cusListArrayList = SaveData(jsonObject, i);
                            }
                        }
                    }
                } else {
                    if (SharedPref.getTpbasedDcr(context).equalsIgnoreCase("0")) {
                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).equalsIgnoreCase(jsonObject.getString("Town_Code"))) {
                            cusListArrayList = SaveData(jsonObject, i);
                        }
                    } else {
                        cusListArrayList = SaveData(jsonObject, i);
                    }
                }
            }

            int count = cusListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (cusListArrayList.get(i).getCode().equalsIgnoreCase(cusListArrayList.get(j).getCode())) {
                        cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(),"","", cusListArrayList.get(i).isClusterAvailable()));
                        cusListArrayList.remove(j--);
                        count--;
                    } else {
                        cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(),"","", cusListArrayList.get(i).isClusterAvailable()));
                    }
                }
            }

        } catch (Exception e) {
            Log.v("CheCall", "-che--error--" + e);
        }

        Log.v("CheCall", "-che--size--" + cusListArrayList.size());

        FilltercustArraList.clear();
        FilltercustArraList.addAll(cusListArrayList);
        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList,SharedPref.getChmSrtNd(requireContext()),"2");
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i) {
        try {
            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                cusListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "2", "Category", "", "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), "", "", jsonObject.getString("Chemists_Email"), jsonObject.getString("Chemists_Mobile"), jsonObject.getString("Chemists_Phone"), "", "", "","",false));
            } else {
                cusListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "2", "Category", "", "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), "", "", jsonObject.getString("Chemists_Email"), jsonObject.getString("Chemists_Mobile"), jsonObject.getString("Chemists_Phone"), "", "","","", true));

            }
        } catch (Exception e) {
            Log.v("CheCall", "--1111---" + e.toString());

        }
        return cusListArrayList;
    }


    private void filter(String text) {
        ArrayList<CustList> filteredNames = new ArrayList<>();
        for (CustList s : cusListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapterDCRCallSelection.filterList(filteredNames);
    }



    public  void CustomizeFiltered(){


        dialogFilter = new Dialog(requireContext());
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        dialogFilter.show();

        TerritoryCode="";
        TextView territory=dialogFilter.findViewById(R.id.constraint_territory);
        territory.setVisibility(View.VISIBLE);
        img_close = dialogFilter.findViewById(R.id.img_close);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        tvTerritory = dialogFilter.findViewById(R.id.constraint_territory);

        lv_terr = dialogFilter.findViewById(R.id.lv_territory);
        img_close.setOnClickListener(view12 -> dialogFilter.dismiss());

        btn_apply.setOnClickListener(view1 -> Filltered());

        tvTerritory.setOnClickListener(view -> {
            if (lv_terr.getVisibility() == View.VISIBLE) {
                lv_terr.setVisibility(View.GONE);

            } else {
                filterSelectionList.clear();
                try {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
//                    jsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode);
                    Log.v("jsonArray", "--" + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        filterSelectionList.add(new DCRFillteredModelClass(jsonObject.getString("Name"),jsonObject.getString("Name")));
                    }

                    FillteredAdapter arrayAdapter = new FillteredAdapter(requireContext(), filterSelectionList, clickedItem -> {
                        TerritoryCode = clickedItem.getCode();
                        tvTerritory.setText(clickedItem.getName());
                        lv_terr.setVisibility(View.GONE);

                    });
                    lv_terr.setAdapter(arrayAdapter);
                    lv_terr.setVisibility(View.VISIBLE);


                } catch (Exception ignored) {

                }

            }
        });


    }

    public void Filltered() {
        FilltercustArraList.clear();
        if (TerritoryCode.equalsIgnoreCase("")) {
            FilltercustArraList.addAll(cusListArrayList);
            tv_filter_count.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        } else {
            for (CustList mList : cusListArrayList) {
                if (mList.getTown_code().equalsIgnoreCase(TerritoryCode)) {
                    FilltercustArraList.add(mList);
                }
            }
            tv_filter_count.setText(String.valueOf(FilltercustArraList.size()));
        }

        adapterDCRCallSelection.notifyDataSetChanged();
        dialogFilter.dismiss();
    }

}