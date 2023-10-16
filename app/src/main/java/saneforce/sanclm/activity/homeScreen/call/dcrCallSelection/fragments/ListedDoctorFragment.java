package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments;

import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.DrGeoTag;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.GeoTagApproval;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfName;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TpBasedDcr;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.laty;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.limitKm;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.lngy;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.FilterDataList;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.AdapterFilterSelection;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class ListedDoctorFragment extends Fragment {
    public static ListView filterList;
    public static ConstraintLayout constraintFilter;
    public static AdapterFilterSelection adapterFilterSelection;
    public static ArrayList<FilterDataList> ArrayFilteredList = new ArrayList<>();
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close, img_del;
    TextView tv_hqName, tv_add_condition, tv_filterCount;
    Button btn_apply, btn_clear;
    RecyclerView rv_filter;
    SQLite sqLite;
    JSONArray jsonArray;
    CommonUtilsMethods commonUtilsMethods;

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listed_doctor, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        filterList = v.findViewById(R.id.filter_list_view);
        constraintFilter = v.findViewById(R.id.constraint_filter_selection_list);
        tv_filterCount = v.findViewById(R.id.tv_filter_count);
        tv_hqName.setText(TodayPlanSfName);
        sqLite = new SQLite(getContext());
        commonUtilsMethods = new CommonUtilsMethods(getActivity());

        SetupAdapter();

        dialogFilter = new Dialog(getActivity());
        dialogFilter.setContentView(R.layout.popup_dcr_filter);
        dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        img_close = dialogFilter.findViewById(R.id.img_close);
        img_del = dialogFilter.findViewById(R.id.img_del);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        rv_filter = dialogFilter.findViewById(R.id.rv_conditions);
        tv_add_condition = dialogFilter.findViewById(R.id.btn_add_condition);

        ArrayFilteredList.clear();
        ArrayFilteredList.add(new FilterDataList("Speciality", 0));
        adapterFilterSelection = new AdapterFilterSelection(getContext(), ArrayFilteredList, custListArrayList);
        rv_filter.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        rv_filter.setAdapter(adapterFilterSelection);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);

        iv_filter.setOnClickListener(view -> {
            dialogFilter.show();
            tv_add_condition.setOnClickListener(view13 -> {
                int count = ArrayFilteredList.size();
                if (count == 1) {
                    ArrayFilteredList.add(new FilterDataList("Category", 1));
                } else if (count == 2) {
                    ArrayFilteredList.add(new FilterDataList("Qualification", 2));
                } else if (count == 3) {
                    ArrayFilteredList.add(new FilterDataList("Class", 3));
                } else if (count > 3) {
                    Toast.makeText(getContext(), "There is no Filter Available", Toast.LENGTH_SHORT).show();
                }

                adapterFilterSelection = new AdapterFilterSelection(getContext(), ArrayFilteredList, custListArrayList);
                rv_filter.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                rv_filter.setAdapter(adapterFilterSelection);
            });

            img_del.setOnClickListener(view14 -> {
                int count = ArrayFilteredList.size();
                ArrayFilteredList.remove(count - 1);
                adapterFilterSelection.notifyDataSetChanged();
            });


            btn_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayFilteredList.clear();
                    ArrayFilteredList.add(new FilterDataList("Speciality", 0));
                    adapterFilterSelection = new AdapterFilterSelection(getContext(), ArrayFilteredList, custListArrayList);
                    rv_filter.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                    rv_filter.setAdapter(adapterFilterSelection);
                }
            });

            img_close.setOnClickListener(view12 -> {
                dialogFilter.dismiss();
                commonUtilsMethods.FullScreencall();
            });

            btn_apply.setOnClickListener(view1 -> {
                dialogFilter.dismiss();
                int count = 0;
                for (int i = 0; i < ArrayFilteredList.size(); i++) {
                    if (ArrayFilteredList.get(i).getName().equalsIgnoreCase("Speciality") || ArrayFilteredList.get(i).getName().equalsIgnoreCase("Category") || ArrayFilteredList.get(i).getName().equalsIgnoreCase("Qualification") || ArrayFilteredList.get(i).getName().equalsIgnoreCase("Class")) {
                        // ArrayFilteredList.remove(i);
                    } else {
                        Log.v("ttrtr", ArrayFilteredList.get(i).getName());
                        filter(ArrayFilteredList.get(i).getName());
                        count++;
                    }

                }
                //  tv_filterCount.setText(String.valueOf(ArrayFilteredList.size()));
                tv_filterCount.setText(String.valueOf(count));
            });

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
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode);
            Log.v("jsonArray", "--" + jsonArray.length() + "---" + jsonArray);
            if (jsonArray.length() == 0) {
                if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                    // Toast.makeText(getActivity(), "No Data Available!", Toast.LENGTH_SHORT).show();
                    //  MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Doctor", "getdoctors", SfCode, SharedPref.getDivisionCode(TagCustSelectionList.this), selectedHqCode, SfType, SharedPref.getDesignationName(TagCustSelectionList.this), SharedPref.getStateCode(TagCustSelectionList.this), SharedPref.getSubdivCode(TagCustSelectionList.this));
                } else {
                    Toast.makeText(getActivity(), Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (DrGeoTag.equalsIgnoreCase("1")) {
                    if (!jsonObject.getString("Lat").isEmpty() && !jsonObject.getString("Long").isEmpty()) {
                        if (GeoTagApproval.equalsIgnoreCase("0")) {
                            Log.v("dfdfs", "--11-");
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), laty, lngy, distance);
                            if (distance[0] < limitKm * 1000.0) {
                                if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                                }
                            }
                        } else {
                            Log.v("dfdfs", "--22-");
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), laty, lngy, distance);
                            if (distance[0] < limitKm * 1000.0) {
                                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                            }
                        }
                    }
                } else {
                    if (TpBasedDcr.equalsIgnoreCase("0")) {
                        Log.v("dfdfs", "--33-");
                        if (SharedPref.getTodayDayPlanClusterCode(requireContext()).equalsIgnoreCase(jsonObject.getString("Town_Code"))) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                        }
                    } else {
                        Log.v("dfdfs", "--44-");
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "1", jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                    }
                }
            }

            int count = custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), "1", custListArrayList.get(i).getCategory(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i)));
                        custListArrayList.remove(j--);
                        count--;
                    } else {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), "1", custListArrayList.get(i).getCategory(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i)));
                    }
                }
            }

        } catch (Exception e) {
            Log.v("dfdfs", "---" + e);
        }

        Log.v("dfdfs", "---" + custListArrayList.size());
        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), custListArrayList);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        Collections.sort(custListArrayList, Comparator.comparing(CustList::getTown_name));
    }

    private void filter(String text) {
        ArrayList<CustList> filterdNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        adapterDCRCallSelection.filterList(filterdNames);
    }
}