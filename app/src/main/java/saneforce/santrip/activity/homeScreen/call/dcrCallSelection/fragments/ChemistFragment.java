package saneforce.santrip.activity.homeScreen.call.dcrCallSelection.fragments;

import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.CheGeoTag;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.GeoTagApproval;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfName;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TpBasedDcr;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.lat;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.limitKm;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.lng;

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
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.Constants;
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
    SQLite sqLite;
    JSONArray jsonArray;
    TextView tv_hqName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment", "---" + "chemist");
        View v = inflater.inflate(R.layout.fragment_chemist, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        tv_hqName = v.findViewById(R.id.tv_hq_name);
        tv_hqName.setText(TodayPlanSfName);

        sqLite = new SQLite(getContext());

        SetupAdapter();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);

        iv_filter.setOnClickListener(view -> {
            dialogFilter = new Dialog(requireContext());
            dialogFilter.setContentView(R.layout.popup_dcr_filter);
            Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogFilter.setCancelable(false);
            dialogFilter.show();

            img_close = dialogFilter.findViewById(R.id.img_close);
            btn_apply = dialogFilter.findViewById(R.id.btn_apply);

            img_close.setOnClickListener(view12 -> dialogFilter.dismiss());

            btn_apply.setOnClickListener(view1 -> dialogFilter.dismiss());
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
            Log.v("CheCall", GeoTagApproval + "--" + CheGeoTag + "----" + TpBasedDcr);
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + DcrCallTabLayoutActivity.TodayPlanSfCode);
            Log.v("CheCall", "-che_full_length-" + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (CheGeoTag.equalsIgnoreCase("1")) {
                    if (!jsonObject.getString("lat").isEmpty() && !jsonObject.getString("long").isEmpty()) {
                        if (GeoTagApproval.equalsIgnoreCase("0")) {
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), lat, lng, distance);
                            if (distance[0] < limitKm * 1000.0) {
                                if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                    cusListArrayList = SaveData(jsonObject, i);
                                }
                            }
                        } else {
                            float[] distance = new float[2];
                            Location.distanceBetween(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("long")), lat, lng, distance);
                            if (distance[0] < limitKm * 1000.0) {
                                cusListArrayList = SaveData(jsonObject, i);
                            }
                        }
                    }
                } else {
                    if (TpBasedDcr.equalsIgnoreCase("0")) {
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
                        cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(), cusListArrayList.get(i).isClusterAvailable()));
                        cusListArrayList.remove(j--);
                        count--;
                    } else {
                        cusListArrayList.set(i, new CustList(cusListArrayList.get(i).getName(), cusListArrayList.get(i).getCode(), cusListArrayList.get(i).getType(), cusListArrayList.get(i).getCategory(), cusListArrayList.get(i).getCategoryCode(), cusListArrayList.get(i).getSpecialist(), cusListArrayList.get(i).getTown_name(), cusListArrayList.get(i).getTown_code(), cusListArrayList.get(i).getTag(), cusListArrayList.get(i).getMaxTag(), String.valueOf(i), cusListArrayList.get(i).getLatitude(), cusListArrayList.get(i).getLongitude(), cusListArrayList.get(i).getAddress(), cusListArrayList.get(i).getDob(), cusListArrayList.get(i).getWedding_date(), cusListArrayList.get(i).getEmail(), cusListArrayList.get(i).getMobile(), cusListArrayList.get(i).getPhone(), cusListArrayList.get(i).getQualification(), cusListArrayList.get(i).getPriorityPrdCode(), cusListArrayList.get(i).isClusterAvailable()));
                    }
                }
            }

        } catch (Exception e) {
            Log.v("CheCall", "-che--error--" + e);
        }

        Log.v("CheCall", "-che--size--" + cusListArrayList.size());
        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), cusListArrayList);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        Collections.sort(cusListArrayList, Comparator.comparing(CustList::getTown_name));
        Collections.sort(cusListArrayList, Comparator.comparing(CustList::isClusterAvailable));
    }

    private ArrayList<CustList> SaveData(JSONObject jsonObject, int i) {
        try {
            if (SharedPref.getTodayDayPlanClusterCode(requireContext()).contains(jsonObject.getString("Town_Code"))) {
                cusListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "2", "Category", "", "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), "", "", jsonObject.getString("Chemists_Email"), jsonObject.getString("Chemists_Mobile"), jsonObject.getString("Chemists_Phone"), "", "", false));
            } else {
                cusListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "2", "Category", "", "Specialty", jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i), jsonObject.getString("lat"), jsonObject.getString("long"), jsonObject.getString("addrs"), "", "", jsonObject.getString("Chemists_Email"), jsonObject.getString("Chemists_Mobile"), jsonObject.getString("Chemists_Phone"), "", "", true));

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
}