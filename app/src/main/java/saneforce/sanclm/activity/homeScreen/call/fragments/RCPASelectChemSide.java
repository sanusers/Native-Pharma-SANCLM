package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.rcpaBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.FragmentSelectChemistSideBinding;
import saneforce.sanclm.storage.SQLite;

public class RCPASelectChemSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectChemistSideBinding selectChemistSideBinding;
    ArrayList<CustList> ChemFullList = new ArrayList<>();
    SQLite sqLite;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ChemistAdapter CheAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectChemistSideBinding = FragmentSelectChemistSideBinding.inflate(inflater);
        View v = selectChemistSideBinding.getRoot();
        sqLite = new SQLite(getContext());
        AddChemistData();

        selectChemistSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectChemistSideBinding.imgClose.setOnClickListener(view -> dcrCallBinding.fragmentSelectChemistSide.setVisibility(View.GONE));

        selectChemistSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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
        return v;
    }

    private void AddChemistData() {
        try {
            ChemFullList.clear();

            jsonArray = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + DcrCallTabLayoutActivity.TodayPlanSfCode);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                ChemFullList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code")));
            }

            int count = ChemFullList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (ChemFullList.get(i).getCode().equalsIgnoreCase(ChemFullList.get(j).getCode())) {
                        ChemFullList.remove(j--);
                        count--;
                    }
                }
            }

            CheAdapter = new ChemistAdapter(requireContext(), ChemFullList);
            RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(getActivity());
            selectChemistSideBinding.selectListView.setLayoutManager(mLayoutManagerChe);
            selectChemistSideBinding.selectListView.setItemAnimator(new DefaultItemAnimator());
            selectChemistSideBinding.selectListView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
            selectChemistSideBinding.selectListView.setAdapter(CheAdapter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void filterChe(String text) {
        ArrayList<CustList> filteredNames = new ArrayList<>();
        for (CustList s : ChemFullList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        CheAdapter.filterList(filteredNames);
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
            holder.tv_name.setText(ChemList.get(position).getName());

            holder.tv_name.setOnClickListener(view -> {
                RCPAFragment.cheName = ChemList.get(holder.getBindingAdapterPosition()).getName();
                RCPAFragment.CheCode = ChemList.get(holder.getBindingAdapterPosition()).getCode();
                rcpaBinding.tvSelectChemist.setText(ChemList.get(holder.getBindingAdapterPosition()).getName());
                rcpaBinding.tvSelectProduct.setText(context.getResources().getString(R.string.select));
                rcpaBinding.edQty.setText("0");
                rcpaBinding.tvRate.setText("");
                rcpaBinding.tvValue.setText("");
                dcrCallBinding.fragmentSelectChemistSide.setVisibility(View.GONE);
            });
        }

        @Override
        public int getItemCount() {
            return ChemList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CustList> filteredNames) {
            this.ChemList = filteredNames;
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
