package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrcallBinding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.rcpa.RCPAAddCompAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.rcpa.RCPAChemistAdapter;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddCompSideView;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.storage.SQLite;

public class RCPAFragmentSide extends Fragment {
    public static ArrayList<RCPAAddCompSideView> RCPAAddCompSideViewArrayList;
    public static ArrayList<RCPAAddedProdList> rcpaAddedProdListArrayList;
    public static ArrayList<RCPAAddedCompList> rcpa_Added_list;
    public static ArrayList<String> chemistNames;
    public static RecyclerView rv_comp_list;
    Button btn_save_rcpa;
    TextView tv_dummy, btn_add_competitor, tv_rate, tv_value;
    RCPAAddCompAdapter rcpaAddCompAdapter;
    ArrayList<String> ChemSpinnerList = new ArrayList<>();
    ArrayList<String> PrdSpinnerList = new ArrayList<>();
    EditText edt_qty;
    ConstraintLayout constraint_main;
    String PrdName = "", chem_names = "";
    RCPAChemistAdapter rcpaChemistAdapter;
    CommonSharedPreference mCommonsharedPreference;
    ImageView img_close;
    ListView lv_chemist, lv_prd;
    TextView tv_sel_chem, tv_sel_prd;
    SQLite sqLite;
    JSONArray jsonArray;
    JSONObject jsonObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_rcpa_side, container, false);
        btn_save_rcpa = v.findViewById(R.id.btn_save_rcpa);
        tv_dummy = v.findViewById(R.id.tv_dummy);
        btn_add_competitor = v.findViewById(R.id.btn_add_conmpetitor);
        rv_comp_list = v.findViewById(R.id.rv_add_rcpa);
        tv_rate = v.findViewById(R.id.tv_rate);
        tv_value = v.findViewById(R.id.tv_value);
        edt_qty = v.findViewById(R.id.ed_qty);
        img_close = v.findViewById(R.id.img_close);
        constraint_main = v.findViewById(R.id.constraint_main);
        lv_chemist = v.findViewById(R.id.lv_chemist);
        lv_prd = v.findViewById(R.id.lv_product);
        tv_sel_chem = v.findViewById(R.id.tv_select_chemist);
        tv_sel_prd = v.findViewById(R.id.tv_select_product);
        sqLite = new SQLite(getContext());
        mCommonsharedPreference = new CommonSharedPreference(getActivity());

        AddListViewData();
        tv_sel_chem.setOnClickListener(view -> {
            if (lv_chemist.getVisibility() == View.VISIBLE) {
                lv_chemist.setVisibility(View.GONE);
            } else {
                lv_chemist.setVisibility(View.VISIBLE);
            }
        });

        lv_chemist.setOnItemClickListener((adapterView, view, i, l) -> {
            tv_sel_chem.setText(lv_chemist.getItemAtPosition(i).toString());
            lv_chemist.setVisibility(View.GONE);
            chem_names = lv_chemist.getItemAtPosition(i).toString();
        });

        tv_sel_prd.setOnClickListener(view -> {
            if (lv_prd.getVisibility() == View.VISIBLE) {
                lv_prd.setVisibility(View.GONE);
            } else {
                lv_prd.setVisibility(View.VISIBLE);
            }
        });

        lv_prd.setOnItemClickListener((adapterView, view, i, l) -> {
            tv_sel_prd.setText(lv_prd.getItemAtPosition(i).toString());
            lv_prd.setVisibility(View.GONE);
            PrdName = lv_prd.getItemAtPosition(i).toString();
        });

        tv_dummy.setOnClickListener(view -> {
            HideKeyboard();
         /*   if (mCommonsharedPreference.getValueFromPreference("tab_pos_dcr").equalsIgnoreCase("3") || mCommonsharedPreference.getValueFromPreference("tab_pos_dcr").equalsIgnoreCase("4")) {
                HideKeyboard();
            }*/
        });


        btn_save_rcpa.setOnClickListener(view -> {
            HideKeyboard();
            boolean check_chem_name = false;

            for (int i = 0; i < chemistNames.size(); i++) {
                if (chemistNames.get(i).equalsIgnoreCase(chem_names)) {
                    check_chem_name = true;
                    break;
                }
            }

            if (!check_chem_name) {
                chemistNames.add(chem_names);
            }

            rcpaChemistAdapter = new RCPAChemistAdapter(getContext(), chemistNames, rcpaAddedProdListArrayList, rcpa_Added_list);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            RCPAFragment.rv_add_rcpa_list.setLayoutManager(mLayoutManager);
            RCPAFragment.rv_add_rcpa_list.setAdapter(rcpaChemistAdapter);
            dcrcallBinding.fragmentAddRcpaSide.setVisibility(View.GONE);
        });

        img_close.setOnClickListener(view -> dcrcallBinding.fragmentAddRcpaSide.setVisibility(View.GONE));

        btn_add_competitor.setOnClickListener(view -> {
            if (tv_sel_chem.getText().toString().equalsIgnoreCase("Select") || tv_sel_chem.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Select Chemist", Toast.LENGTH_SHORT).show();
            } else if (tv_sel_prd.getText().toString().equalsIgnoreCase("Select") || tv_sel_prd.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Select Product", Toast.LENGTH_SHORT).show();
            } else if (edt_qty.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Add Quantity", Toast.LENGTH_SHORT).show();
            } else {
                DummyAdapter();
                HideKeyboard();
            }
        });
        return v;
    }

    private void AddListViewData() {
        try {
            ChemSpinnerList.clear();
            PrdSpinnerList.clear();
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + DcrCallTabLayoutActivity.TodayPlanSfCode);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                ChemSpinnerList.add(jsonObject.getString("Name"));
            }

            ArrayAdapter<String> dataAdapterChem = new ArrayAdapter<>(getActivity(), R.layout.listview_items, ChemSpinnerList);
            lv_chemist.setAdapter(dataAdapterChem);

       /* spin_chemist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!chem_names.isEmpty()) {
                    if (!chem_names.equalsIgnoreCase(spin_chemist.getSelectedItem().toString())) {
                        RCPAAddCompSideViewArrayList.clear();
                        edt_qty.setText("");
                        tv_value.setText("");
                        tv_rate.setText("");
                    }
                }
                chem_names = spin_chemist.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                PrdSpinnerList.add(jsonObject.getString("Name"));
            }

/*
        spin_prd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!PrdName.isEmpty()) {
                    if (!PrdName.equalsIgnoreCase(spin_prd.getSelectedItem().toString())) {
                        edt_qty.setText("");
                        tv_value.setText("");
                        tv_rate.setText("");
                    }
                }
                PrdName = spin_prd.getSelectedItem().toString();
                switch (spin_prd.getSelectedItem().toString()) {
                    case "Select":
                        tv_rate.setText("");
                        tv_value.setText("");
                        break;
                    case "Paracemetol":
                        tv_rate.setText("30");
                        tv_value.setText("95.7");
                        break;
                    case "Terracite":
                        tv_rate.setText("93");
                        tv_value.setText("8.7");
                        break;
                    case "Calch 500":
                        tv_rate.setText("87");
                        tv_value.setText("5.4");
                        break;
                    case "Stanvit":
                        tv_rate.setText("56");
                        tv_value.setText("8.9");
                        break;
                    default:
                        tv_rate.setText("0");
                        tv_value.setText("0");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

            ArrayAdapter<String> dataAdapterPrd = new ArrayAdapter<>(getActivity(), R.layout.listview_items, PrdSpinnerList);
            lv_prd.setAdapter(dataAdapterPrd);
        } catch (Exception e) {

        }
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(constraint_main.getWindowToken(), 0);
    }

    private void DummyAdapter() {
        boolean check_prd_name = false;
        RCPAAddCompSideViewArrayList.add(0, new RCPAAddCompSideView(PrdName, edt_qty.getText().toString(), tv_rate.getText().toString(), tv_value.getText().toString()));

      /*  for (int i = 0; i < rcpaAddedProdListArrayList.size(); i++) {
            if (rcpaAddedProdListArrayList.get(i).getPrd_name().equalsIgnoreCase(PrdName)) {
                check_prd_name = true;
                break;
            }
        }

        if (!check_prd_name) {
            rcpaAddedProdListArrayList.add(0, new RCPAAddedProdList(PrdName, edt_qty.getText().toString(), tv_rate.getText().toString(), tv_value.getText().toString(), chem_names));
            rcpa_Added_list.add(0, new RCPAAddedCompList(chem_names, PrdName, "", "", "", "", "", ""));
        }*/

        rcpaAddedProdListArrayList.add(new RCPAAddedProdList(PrdName, edt_qty.getText().toString(), tv_rate.getText().toString(), tv_value.getText().toString(), chem_names));
        rcpa_Added_list.add(new RCPAAddedCompList(chem_names, PrdName, "", "", "", "", "", ""));

        rcpaAddCompAdapter = new RCPAAddCompAdapter(getActivity(), getContext(), RCPAAddCompSideViewArrayList, rcpa_Added_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_comp_list.setLayoutManager(mLayoutManager);
        rv_comp_list.setAdapter(rcpaAddCompAdapter);
    }
}
