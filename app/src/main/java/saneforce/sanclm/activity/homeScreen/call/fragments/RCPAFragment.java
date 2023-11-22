package saneforce.sanclm.activity.homeScreen.call.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.rcpa.RCPAChemistAdapter;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.FragmentRcpaBinding;
import saneforce.sanclm.storage.SQLite;


public class RCPAFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentRcpaBinding rcpaBinding;
    public static String PrdName, PrdCode, cheName, CheCode;
    ArrayList<SaveCallProductList> PrdFullList = new ArrayList<>();
    ArrayList<CustList> ChemFullList = new ArrayList<>();
    ArrayList<CustList> ChemistSelectedList = new ArrayList<>();
    ArrayList<RCPAAddedProdList> ProductSelectedList = new ArrayList<>();
    SQLite sqLite;
    JSONArray jsonArray;
    JSONObject jsonObject;
    double getQty;
    ProductAdapter PrdAdapter;
    ChemistAdapter CheAdapter;
    RCPAChemistAdapter rcpaChemistAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rcpaBinding = FragmentRcpaBinding.inflate(getLayoutInflater());
        View v = rcpaBinding.getRoot();
        sqLite = new SQLite(requireContext());

        AddListViewData();

        rcpaBinding.btnAddRcpa.setOnClickListener(view -> {
            if (rcpaBinding.tvSelectChemist.getText().toString().isEmpty() || rcpaBinding.tvSelectChemist.getText().toString().equalsIgnoreCase("Select")) {
                Toast.makeText(requireContext(), "Select Chemist", Toast.LENGTH_SHORT).show();
            } else if (rcpaBinding.tvSelectProduct.getText().toString().isEmpty() || rcpaBinding.tvSelectProduct.getText().toString().equalsIgnoreCase("Select")) {
                Toast.makeText(requireContext(), "Select Product", Toast.LENGTH_SHORT).show();
            } else if (Objects.requireNonNull(rcpaBinding.edQty.getText()).toString().equalsIgnoreCase("0")) {
                Toast.makeText(requireContext(), "Enter Qty", Toast.LENGTH_SHORT).show();
            } else {
                rcpaBinding.llNoRcpa.setVisibility(View.GONE);
                rcpaBinding.rvRcpaChemistList.setVisibility(View.VISIBLE);

                ChemistSelectedList.add(new CustList(cheName, CheCode));
                ProductSelectedList.add(new RCPAAddedProdList(cheName, CheCode, PrdName, PrdCode, rcpaBinding.edQty.getText().toString(), rcpaBinding.tvRate.getText().toString(), rcpaBinding.tvValue.getText().toString()));

                rcpaChemistAdapter = new RCPAChemistAdapter(requireContext(), ChemistSelectedList, ProductSelectedList);
                RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(getActivity());
                rcpaBinding.rvRcpaChemistList.setLayoutManager(mLayoutManagerChe);
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
            if (rcpaBinding.listCvChemist.getVisibility() == View.VISIBLE) {
                rcpaBinding.listCvChemist.setVisibility(View.GONE);
                rcpaBinding.tvSelectChemist.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spin_down_arrow, 0);
            } else {
                rcpaBinding.tvSelectChemist.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arrow, 0);
                rcpaBinding.listCvChemist.setVisibility(View.VISIBLE);
            }
        });

        rcpaBinding.searchChemist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterChe(editable.toString());
            }
        });

        rcpaBinding.tvSelectProduct.setOnClickListener(view -> {
            if (!rcpaBinding.tvSelectChemist.getText().toString().equalsIgnoreCase("Select") && !rcpaBinding.tvSelectChemist.getText().toString().isEmpty()) {
                if (rcpaBinding.listCvProduct.getVisibility() == View.VISIBLE) {
                    rcpaBinding.tvSelectProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spin_down_arrow, 0);
                    rcpaBinding.listCvProduct.setVisibility(View.GONE);
                } else {
                    rcpaBinding.tvSelectProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arrow, 0);
                    rcpaBinding.listCvProduct.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(requireContext(), "Select Chemist", Toast.LENGTH_SHORT).show();
            }

        });

        rcpaBinding.searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterPrd(editable.toString());
            }
        });

        return v;
    }

    private void AddListViewData() {
        try {
            ChemFullList.clear();
            PrdFullList.clear();

            jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + DcrCallTabLayoutActivity.TodayPlanSfCode);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                ChemFullList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code")));
            }
            CheAdapter = new ChemistAdapter(requireContext(), ChemFullList);
            RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(getActivity());
            rcpaBinding.lvChemist.setLayoutManager(mLayoutManagerChe);
            rcpaBinding.lvChemist.setItemAnimator(new DefaultItemAnimator());
            rcpaBinding.lvChemist.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
            rcpaBinding.lvChemist.setAdapter(CheAdapter);

            jsonArray = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                PrdFullList.add(new SaveCallProductList(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("DRate")));
            }

            PrdAdapter = new ProductAdapter(requireContext(), PrdFullList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rcpaBinding.lvProduct.setLayoutManager(mLayoutManager);
            rcpaBinding.lvProduct.setItemAnimator(new DefaultItemAnimator());
            rcpaBinding.lvProduct.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
            rcpaBinding.lvProduct.setAdapter(PrdAdapter);

        } catch (Exception e) {
            Log.v("error", "--" + e);
        }
    }

    private void filterChe(String text) {
        ArrayList<CustList> filterdNames = new ArrayList<>();
        for (CustList s : ChemFullList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        CheAdapter.filterList(filterdNames);
    }

    private void filterPrd(String text) {
        ArrayList<SaveCallProductList> filterdNames = new ArrayList<>();
        for (SaveCallProductList s : PrdFullList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        PrdAdapter.filterList(filterdNames);
    }

    public static class ChemistAdapter extends RecyclerView.Adapter<ChemistAdapter.ViewHolder> {
        Context context;
        ArrayList<CustList> ChemList;

        public ChemistAdapter(Context context, ArrayList<CustList> chemList) {
            this.context = context;
            this.ChemList = chemList;
        }

        @NonNull
        @Override
        public ChemistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
                return new ChemistAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChemistAdapter.ViewHolder holder, int position) {
            Log.v("fsdfdf", "---" + ChemList.size());
            holder.tv_name.setText(ChemList.get(position).getName());

            holder.tv_name.setOnClickListener(view -> {
                RCPAFragment.cheName = ChemList.get(holder.getAdapterPosition()).getName();
                RCPAFragment.CheCode = ChemList.get(holder.getAdapterPosition()).getCode();
                rcpaBinding.tvSelectChemist.setText(ChemList.get(holder.getAdapterPosition()).getName());
                rcpaBinding.listCvChemist.setVisibility(View.GONE);
                rcpaBinding.tvSelectChemist.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spin_down_arrow, 0);
            });
        }

        @Override
        public int getItemCount() {
            return ChemList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CustList> filterdNames) {
            this.ChemList = filterdNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
            }
        }
    }

    public static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        Context context;
        ArrayList<SaveCallProductList> prdList;

        public ProductAdapter(Context context, ArrayList<SaveCallProductList> prdList) {
            this.context = context;
            this.prdList = prdList;
        }

        @NonNull
        @Override
        public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
            holder.tv_name.setText(prdList.get(position).getName());
            holder.tv_name.setOnClickListener(view -> {
                RCPAFragment.PrdName = prdList.get(holder.getAdapterPosition()).getName();
                RCPAFragment.PrdCode = prdList.get(holder.getAdapterPosition()).getCode();
                rcpaBinding.tvSelectProduct.setText(prdList.get(holder.getAdapterPosition()).getName());
                rcpaBinding.listCvProduct.setVisibility(View.GONE);
                rcpaBinding.tvSelectProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spin_down_arrow, 0);
                rcpaBinding.tvRate.setText(prdList.get(holder.getAdapterPosition()).getRate());
                rcpaBinding.edQty.setText("1");
                rcpaBinding.tvValue.setText(prdList.get(holder.getAdapterPosition()).getRate());
            });
        }

        @Override
        public int getItemCount() {
            return prdList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<SaveCallProductList> filterdNames) {
            this.prdList = filterdNames;
            notifyDataSetChanged();
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
            }
        }
    }
}