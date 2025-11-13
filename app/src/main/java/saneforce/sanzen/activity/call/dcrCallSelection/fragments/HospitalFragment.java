package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class HospitalFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close;
    Button btn_apply;
    JSONArray jsonArray;
    TextView tv_hqName;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private DcrCallTabLayoutActivity.HQChangeListener hqChangeListener;

    public HospitalFragment() {
    }

    public HospitalFragment(DcrCallTabLayoutActivity.HQChangeListener hqChangeListener) {
        this.hqChangeListener = hqChangeListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("fragment", "---" + "hospital");
        View view = inflater.inflate(R.layout.fragment_hos, container, false);
        rv_list = view.findViewById(R.id.rv_cust_list_selection);
        ed_search = view.findViewById(R.id.search_cust);
        iv_filter = view.findViewById(R.id.iv_filter);
        tv_hqName = view.findViewById(R.id.tv_hq_name);
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        SetupAdapter();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);

        iv_filter.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {

                dialogFilter = new Dialog(requireContext());
                dialogFilter.setContentView(R.layout.popup_dcr_filter);
                Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogFilter.setCancelable(false);
                dialogFilter.show();

                img_close = dialogFilter.findViewById(R.id.img_close);
                btn_apply = dialogFilter.findViewById(R.id.btn_apply);

                img_close.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        dialogFilter.dismiss();
                    }
                });

                btn_apply.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        dialogFilter.dismiss();
                    }
                });
            }
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

        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
            tv_hqName.setOnClickListener(v -> {
                try {
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                    ArrayList<String> list = new ArrayList<>();

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (SharedPref.getMultiHQCode(requireContext()).contains(jsonObject.optString("id"))) {
                                list.add(jsonObject.getString("name"));
                            }
                        }
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
//                LayoutInflater inflater = requireActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_listview, null);
                    alertDialog.setView(dialogView);
                    TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
                    ListView listView = dialogView.findViewById(R.id.listView);
                    SearchView searchView = dialogView.findViewById(R.id.searchET);

                    headerTxt.setText(getResources().getText(R.string.select_hq));
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);
                    AlertDialog dialog = alertDialog.create();

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            adapter.getFilter().filter(s);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            adapter.getFilter().filter(s);
                            return false;
                        }
                    });

                    listView.setOnItemClickListener((adapterView, view1, position, l) -> {
                        String selectedHq = listView.getItemAtPosition(position).toString();
                        tv_hqName.setText(selectedHq);
                        String hqID = "", hqName = "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("name").equalsIgnoreCase(selectedHq)) {
                                    hqID = jsonObject.getString("id");
                                    hqName = jsonObject.getString("name");
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
//                        DcrCallTabLayoutActivity.prepareClusterList(requireActivity());
//                        SetupAdapter();
                        hqChangeListener.onHQChange(hqID, hqName);
                        dialog.dismiss();
                    });

                    alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());

                    dialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UtilityClass.hideKeyboard(requireActivity());

            });
        }

        return view;
    }

    public void SetupAdapter() {
        tv_hqName.setText(DcrCallTabLayoutActivity.TodayPlanSfName);
        custListArrayList.clear();
        try {
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + DcrCallTabLayoutActivity.TodayPlanSfCode).getMasterSyncDataJsonArray();
            Log.v("call", "-hos_full_length-" + jsonArray.length());

            List<String> todayPlannedClusters = Arrays.asList(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(SharedPref.getTodayDayPlanClusterCode(requireContext()))).split(","));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (todayPlannedClusters.contains(jsonObject.getString("Town_Code"))) {
                /*    if (CipGeoTag.equalsIgnoreCase("1")) {
                        if (!jsonObject.getString("Lat").isEmpty() && !jsonObject.getString("Long").isEmpty()) {
                            if (GeoTagApproval.equalsIgnoreCase("0")) {
                                Log.v("Cip", "--11-");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), lat, lng, distance);
                                if (distance[0] < limitKm * 1000.0) {
                                    if (jsonObject.getString("cust_status").equalsIgnoreCase("0")) {
                                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "5", jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                                    }
                                }
                            } else {
                                Log.v("Cip", "--22-");
                                float[] distance = new float[2];
                                Location.distanceBetween(Double.parseDouble(jsonObject.getString("Lat")), Double.parseDouble(jsonObject.getString("Long")), lat, lng, distance);
                                if (distance[0] < limitKm * 1000.0) {
                                    custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "5", jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                                }
                            }
                        }
                    } else {*/
                    if (SharedPref.getTpbasedDcr(requireContext()).equalsIgnoreCase("0")) {
                        Log.v("Hos", "--33-");
                        if (todayPlannedClusters.contains(jsonObject.getString("Town_Code"))) {
                            custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "6", jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                        }
                    } else {
                        Log.v("Hos", "--44-");
                        custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "6", jsonObject.getString("Category"), jsonObject.getString("Specialty"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
                    }
                }
                // }
            }

            int count = custListArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (custListArrayList.get(i).getCode().equalsIgnoreCase(custListArrayList.get(j).getCode())) {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification()));
                        custListArrayList.remove(j--);
                        count--;
                    } else {
                        custListArrayList.set(i, new CustList(custListArrayList.get(i).getName(), custListArrayList.get(i).getCode(), custListArrayList.get(i).getType(), custListArrayList.get(i).getCategory(), custListArrayList.get(i).getCategoryCode(), custListArrayList.get(i).getSpecialist(), custListArrayList.get(i).getTown_name(), custListArrayList.get(i).getTown_code(), custListArrayList.get(i).getTag(), custListArrayList.get(i).getMaxTag(), String.valueOf(i), custListArrayList.get(i).getLatitude(), custListArrayList.get(i).getLongitude(), custListArrayList.get(i).getAddress(), custListArrayList.get(i).getDob(), custListArrayList.get(i).getWedding_date(), custListArrayList.get(i).getEmail(), custListArrayList.get(i).getMobile(), custListArrayList.get(i).getPhone(), custListArrayList.get(i).getQualification()));
                    }
                }
            }

        } catch (Exception e) {
            Log.v("call", "-hos--error--" + e);
        }

        Log.v("call", "-hos--size--" + custListArrayList.size());
        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), custListArrayList, "1", false, "6");
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        Collections.sort(custListArrayList, Comparator.comparing(CustList::getTown_name));
    }

    private void filter(String text) {
        ArrayList<CustList> filteredNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getTown_name().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || s.getSpecialist().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapterDCRCallSelection.filterList(filteredNames);
    }
}