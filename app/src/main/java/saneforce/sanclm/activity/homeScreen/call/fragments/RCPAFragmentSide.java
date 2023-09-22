package saneforce.sanclm.activity.homeScreen.call.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import saneforce.sanclm.commonclasses.CommonSharedPreference;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.rcpa.RCPAAddCompAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.rcpa.RCPAChemistAdapter;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddCompSideView;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedProdList;

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
    Spinner spin_chemist, spin_prd;
    EditText edt_qty;
    ConstraintLayout constraint_main;
    String PrdName = "", chem_names = "";
    RCPAChemistAdapter rcpaChemistAdapter;
    CommonSharedPreference mCommonsharedPreference;
    ImageView img_close;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_rcpa_side, container, false);
        btn_save_rcpa = v.findViewById(R.id.btn_save_rcpa);
        tv_dummy = v.findViewById(R.id.tv_dummy);
        btn_add_competitor = v.findViewById(R.id.btn_add_conmpetitor);
        rv_comp_list = v.findViewById(R.id.rv_add_rcpa);
        spin_chemist = v.findViewById(R.id.spin_chemist_name);
        spin_prd = v.findViewById(R.id.spin_products_name);
        tv_rate = v.findViewById(R.id.tv_rate);
        tv_value = v.findViewById(R.id.tv_value);
        edt_qty = v.findViewById(R.id.ed_qty);
        img_close = v.findViewById(R.id.img_close);
        constraint_main = v.findViewById(R.id.constraint_main);
        mCommonsharedPreference = new CommonSharedPreference(getActivity());

        AddSpinnerData();

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
            DCRCallActivity.fragment_add_rcpa_side.setVisibility(View.GONE);
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DCRCallActivity.fragment_add_rcpa_side.setVisibility(View.GONE);
            }
        });
        btn_add_competitor.setOnClickListener(view -> {
            if (spin_chemist.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                Toast.makeText(getActivity(), "Select Chemist", Toast.LENGTH_SHORT).show();
            } else if (spin_prd.getSelectedItem().toString().equalsIgnoreCase("Select")) {
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

    private void AddSpinnerData() {
        ChemSpinnerList.clear();
        ChemSpinnerList.add("Select");
        ChemSpinnerList.add("Ganga Pharmaticals");
        ChemSpinnerList.add("Aasik Medicals");
        ChemSpinnerList.add("Venkateshwaranam Medist");
        ChemSpinnerList.add("Hari Medicals");
        ChemSpinnerList.add("Jaipur Chemicals");
        ChemSpinnerList.add("Kadhar Med Industries");
        ChemSpinnerList.add("Ravi Pharmacticals");

        ArrayAdapter<String> dataAdapterChem = new ArrayAdapter<>(getActivity(), R.layout.textview_bg_spinner, ChemSpinnerList);
        dataAdapterChem.setDropDownViewResource(R.layout.textview_bg_spinner);
        spin_chemist.setAdapter(dataAdapterChem);

        spin_chemist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });

        PrdSpinnerList.clear();
        PrdSpinnerList.add("Select");
        PrdSpinnerList.add("Paracemetol");
        PrdSpinnerList.add("Terracite");
        PrdSpinnerList.add("Calch 500");
        PrdSpinnerList.add("Stanvit");
        PrdSpinnerList.add("Sucraz");
        PrdSpinnerList.add("Meff");

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
        });

        ArrayAdapter<String> dataAdapterPrd = new ArrayAdapter<>(getActivity(), R.layout.textview_bg_spinner, PrdSpinnerList);
        dataAdapterPrd.setDropDownViewResource(R.layout.textview_bg_spinner);
        spin_prd.setAdapter(dataAdapterPrd);
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
