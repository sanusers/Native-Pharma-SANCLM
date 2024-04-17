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
import android.widget.ArrayAdapter;
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
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;


public class StockiestFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close;
    Button btn_apply;
    SQLite sqLite;
    JSONArray jsonArray;
    TextView tv_hqName,tvTerritory,tv_filter_count;
    CommonUtilsMethods commonUtilsMethods;

    String TerritoryCode;

    ListView lv_terr;
    ArrayList<CustList> FilltercustArraList = new ArrayList<>();
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment","---"+ "stockist");
        View v = inflater.inflate(R.layout.fragment_stockiest, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_filter_count = v.findViewById(R.id.tv_filter_count);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);

        sqLite = new SQLite(getContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
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
        custListArrayList.clear();
        try {
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + DcrCallTabLayoutActivity.TodayPlanSfCode);

            Log.v("STKCALL", "-stk_full_length-" + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (SharedPref.getGeotagNeedStock(context).equalsIgnoreCase("1")) {
                        if (!jsonObject.getString("lat").isEmpty() && !jsonObject.getString("long").isEmpty()) {
                            if (SharedPref.getGeotagApprovalNeed(context).equalsIgnoreCase("0")) {
                                Log.v("STKCALL", "--11-");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                        custListArrayList = SaveData(jsonObject,i);
                                    }
                                }
                            } else {
                                Log.v("STKCALL", "--22-");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
                                if (distance[0] < DcrCallTabLayoutActivity.limitKm * 1000.0) {
                                    custListArrayList = SaveData(jsonObject,i);                                }
                            }
                        }
                    } else {
                        if (SharedPref.getTpbasedDcr(context).equalsIgnoreCase("0")) {
                            Log.v("STKCALL", "--33-");
                            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).equalsIgnoreCase(jsonObject.getString("Town_Code"))) {
                                custListArrayList = SaveData(jsonObject,i);                            }
                        } else {
                            Log.v("STKCALL", "--44-");
                            custListArrayList = SaveData(jsonObject,i);                        }
                    }
                }

            int count = custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(),"","",custListArrayList.get(i).isClusterAvailable()));
                        custListArrayList.remove(j--);
                        count--;
                    } else {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification(), custListArrayList.get(i).getPriorityPrdCode(),"","",custListArrayList.get(i).isClusterAvailable()));
                    }
                }
            }

        } catch (Exception e) {
            Log.v("STKCALL", "-stk--error--" + e);
        }
        FilltercustArraList.clear();
        FilltercustArraList.addAll(custListArrayList);
        Log.v("STKCALL", "-stk--size--" + custListArrayList.size());
        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), FilltercustArraList, "1","3");
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);

        Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i) {
        try {
            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "3", "Category", "", "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), "", "", jsonObject.getString("Stockiest_Email"), jsonObject.getString("Stockiest_Mobile"), jsonObject.getString("Stockiest_Phone"), jsonObject.getString("Stockiest_Cont_Desig"), "","","",false));
            } else {
                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "3", "Category", "", "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), "", "", jsonObject.getString("Stockiest_Email"), jsonObject.getString("Stockiest_Mobile"), jsonObject.getString("Stockiest_Phone"), jsonObject.getString("Stockiest_Cont_Desig"), "","","",true));
            }
        } catch (Exception e) {
            Log.v("STKCALL", "--1111---" + e.toString());

        }
        return custListArrayList;
    }

    private void filter(String text) {
        ArrayList<CustList> filteredNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
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
                    jsonArray = sqLite.getMasterSyncDataByKey(Constants.CLUSTER + DcrCallTabLayoutActivity.TodayPlanSfCode);
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
            FilltercustArraList.addAll(custListArrayList);

            tv_filter_count.setText("0");
            Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
        } else {
            for (CustList mList : custListArrayList) {
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