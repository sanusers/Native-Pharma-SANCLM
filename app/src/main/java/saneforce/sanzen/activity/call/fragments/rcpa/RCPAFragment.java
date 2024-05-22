package saneforce.sanzen.activity.call.fragments.rcpa;

import static saneforce.sanzen.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanzen.activity.call.fragments.rcpa.RCPASelectChemSide.selectChemistSideBinding;
import static saneforce.sanzen.activity.call.fragments.rcpa.RCPASelectPrdSide.selectProductSideBinding;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.adapter.rcpa.RCPAChemistAdapter;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.FragmentRcpaBinding;


public class RCPAFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentRcpaBinding rcpaBinding;
    public static String PrdName, PrdCode, cheName, CheCode;
    public static ArrayList<CustList> ChemistSelectedList = new ArrayList<>();
    public static ArrayList<RCPAAddedProdList> ProductSelectedList = new ArrayList<>();
    double getQty;
    RCPAChemistAdapter rcpaChemistAdapter;
    CommonUtilsMethods commonUtilsMethods;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rcpaBinding = FragmentRcpaBinding.inflate(getLayoutInflater());
        View v = rcpaBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        if (ChemistSelectedList.size() > 0) {
            rcpaBinding.llNoRcpa.setVisibility(View.GONE);
            rcpaBinding.rvRcpaChemistList.setVisibility(View.VISIBLE);

            rcpaChemistAdapter = new RCPAChemistAdapter(requireActivity(), requireContext(), ChemistSelectedList, ProductSelectedList, RCPASelectCompSide.rcpa_comp_list);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rcpaBinding.rvRcpaChemistList.setLayoutManager(mLayoutManager);
            commonUtilsMethods.recycleTestWithoutDivider(rcpaBinding.rvRcpaChemistList);
            rcpaBinding.rvRcpaChemistList.setAdapter(rcpaChemistAdapter);
        }

        if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
            rcpaBinding.tvSelectChemist.setEnabled(true);
            rcpaBinding.tvSelectChemist.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.greater_than_purple, 0);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
            rcpaBinding.tvSelectChemist.setEnabled(false);
            rcpaBinding.tvSelectChemist.setText(DCRCallActivity.CallActivityCustDetails.get(0).getName());
            cheName = DCRCallActivity.CallActivityCustDetails.get(0).getName();
            CheCode = DCRCallActivity.CallActivityCustDetails.get(0).getCode();
        }

        rcpaBinding.btnAddRcpa.setOnClickListener(view -> {
            if (rcpaBinding.tvSelectChemist.getText().toString().isEmpty() || rcpaBinding.tvSelectChemist.getText().toString().equalsIgnoreCase("Select")) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_chemist));
            } else if (rcpaBinding.tvSelectProduct.getText().toString().isEmpty() || rcpaBinding.tvSelectProduct.getText().toString().equalsIgnoreCase("Select")) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_prd));
            } else if (Objects.requireNonNull(rcpaBinding.edQty.getText()).toString().equalsIgnoreCase("0")) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.enter_qty));
            } else {
                rcpaBinding.llNoRcpa.setVisibility(View.GONE);
                rcpaBinding.rvRcpaChemistList.setVisibility(View.VISIBLE);

                ProductSelectedList.add(new RCPAAddedProdList(cheName, CheCode, PrdName, PrdCode, rcpaBinding.edQty.getText().toString(), rcpaBinding.tvRate.getText().toString(), rcpaBinding.tvValue.getText().toString(), rcpaBinding.tvValue.getText().toString()));

                double getTotalValue = 0.0;
                ArrayList<Double> double_data = new ArrayList<>();
                if (ProductSelectedList.size() > 0) {
                    for (int i = 0; i < ProductSelectedList.size(); i++) {
                        if (ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(CheCode)) {
                            double_data.add(Double.parseDouble(ProductSelectedList.get(i).getTotalPrdValue()));
                        }
                    }
                } else {
                    ChemistSelectedList.add(new CustList(cheName, CheCode, rcpaBinding.tvValue.getText().toString(), ""));
                }

                if (double_data.size() > 0) {
                    for (int i = 0; i < double_data.size(); i++) {
                        getTotalValue = getTotalValue + double_data.get(i);
                    }
                }

                double valueRounded = Math.round(getTotalValue * 100D) / 100D;

                ChemistSelectedList.add(new CustList(cheName, CheCode, String.valueOf(valueRounded), ""));

                int count = ChemistSelectedList.size();
                for (int i = 0; i < count; i++) {
                    for (int j = i + 1; j < count; j++) {
                        if (ChemistSelectedList.get(i).getCode().equalsIgnoreCase(ChemistSelectedList.get(j).getCode())) {
                            String value = ChemistSelectedList.get(j).getTotalRcpa();
                            ChemistSelectedList.remove(j--);
                            ChemistSelectedList.set(i, new CustList(cheName, CheCode, value, ""));
                            count--;
                        }
                    }
                }

                if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
                    rcpaBinding.tvSelectChemist.setText(getResources().getString(R.string.select));
                }

                rcpaBinding.tvSelectProduct.setText(getResources().getString(R.string.select));
                rcpaBinding.edQty.setText("0");
                rcpaBinding.tvRate.setText("");
                rcpaBinding.tvValue.setText("");

                rcpaChemistAdapter = new RCPAChemistAdapter(requireActivity(), requireContext(), ChemistSelectedList, ProductSelectedList, RCPASelectCompSide.rcpa_comp_list);
                RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(getActivity());
                rcpaBinding.rvRcpaChemistList.setLayoutManager(mLayoutManagerChe);
                commonUtilsMethods.recycleTestWithoutDivider(rcpaBinding.rvRcpaChemistList);
                rcpaBinding.rvRcpaChemistList.setAdapter(rcpaChemistAdapter);
            }
        });

        rcpaBinding.edQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty() && !editable.toString().equalsIgnoreCase("0") && !rcpaBinding.tvRate.getText().toString().isEmpty()) {
                    getQty = Double.parseDouble(editable.toString()) * Double.parseDouble(rcpaBinding.tvRate.getText().toString());
                    double valueRounded = Math.round(getQty * 100D) / 100D;
                    rcpaBinding.tvValue.setText(String.valueOf(valueRounded));
                } else if (editable.toString().equalsIgnoreCase("0")) {
                    rcpaBinding.tvValue.setText("0");
                } else {
                    rcpaBinding.tvValue.setText(rcpaBinding.tvRate.getText().toString());
                }
            }
        });

        rcpaBinding.tvSelectChemist.setOnClickListener(view -> {
            dcrCallBinding.fragmentSelectChemistSide.setVisibility(View.VISIBLE);
            try {
                selectChemistSideBinding.searchList.setText("");
                selectChemistSideBinding.selectListView.scrollToPosition(0);
            } catch (Exception ignored) {
            }
        });


        rcpaBinding.tvSelectProduct.setOnClickListener(view -> {
            if (!rcpaBinding.tvSelectChemist.getText().toString().equalsIgnoreCase("Select") && !rcpaBinding.tvSelectChemist.getText().toString().isEmpty()) {
                dcrCallBinding.fragmentSelectProductSide.setVisibility(View.VISIBLE);
                try {
                    selectProductSideBinding.searchList.setText("");
                    selectProductSideBinding.selectListView.scrollToPosition(0);
                } catch (Exception ignored) {

                }

            } else {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_chemist));
            }

        });
        return v;
    }
}