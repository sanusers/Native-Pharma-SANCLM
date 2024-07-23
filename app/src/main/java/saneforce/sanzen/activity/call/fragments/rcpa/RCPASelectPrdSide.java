package saneforce.sanzen.activity.call.fragments.rcpa;

import static saneforce.sanzen.activity.call.DCRCallActivity.dcrCallBinding;

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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.FragmentSelectProductSideBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class RCPASelectPrdSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSelectProductSideBinding selectProductSideBinding;
    public static ArrayList<SaveCallProductList> PrdFullList;
    ProductAdapter PrdAdapter;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectProductSideBinding = FragmentSelectProductSideBinding.inflate(inflater);
        View v = selectProductSideBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        AddProductsData();

        selectProductSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectProductSideBinding.imgClose.setOnClickListener(view -> {
            dcrCallBinding.fragmentSelectProductSide.setVisibility(View.GONE);
            UtilityClass.hideKeyboard(requireActivity());
        });

        selectProductSideBinding.searchList.addTextChangedListener(new TextWatcher() {
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

    private void AddProductsData() {
        try {
            PrdAdapter = new ProductAdapter(requireContext(), PrdFullList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            selectProductSideBinding.selectListView.setLayoutManager(mLayoutManager);
            selectProductSideBinding.selectListView.setItemAnimator(new DefaultItemAnimator());
            selectProductSideBinding.selectListView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
            selectProductSideBinding.selectListView.setAdapter(PrdAdapter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void filterPrd(String text) {
        ArrayList<SaveCallProductList> filteredNames = new ArrayList<>();
        for (SaveCallProductList s : PrdFullList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        PrdAdapter.filterList(filteredNames);
    }

    public static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        Context context;
        ArrayList<SaveCallProductList> prdList;
        JSONArray jsonArray;
        JSONObject jsonObject;
        boolean isAvailableCompetitor;
        CommonUtilsMethods commonUtilsMethods;
        private RoomDB roomDB;
        private MasterDataDao masterDataDao;

        public ProductAdapter(Context context, ArrayList<SaveCallProductList> prdList) {
            this.context = context;
            this.prdList = prdList;
            commonUtilsMethods = new CommonUtilsMethods(context);
            roomDB = RoomDB.getDatabase(context);
            masterDataDao = roomDB.masterDataDao();
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

                ArrayList<String> dummyCheck = new ArrayList<>();
                if (DCRCallActivity.RcpaCompetitorAdd.equalsIgnoreCase("0")) {
                    try {
                        if (RCPAFragment.ProductSelectedList.size() > 0) {
                            for (int j = 0; j < RCPAFragment.ProductSelectedList.size(); j++) {
                                if (RCPAFragment.ProductSelectedList.get(j).getChe_codes().equalsIgnoreCase(RCPAFragment.CheCode) && RCPAFragment.ProductSelectedList.get(j).getPrd_code().equalsIgnoreCase(prdList.get(holder.getBindingAdapterPosition()).getCode())) {
                                    dummyCheck.add(RCPAFragment.CheCode);
                                }
                            }
                        }

                        if (dummyCheck.size() == 0) {
                            RCPAFragment.PrdName = prdList.get(holder.getBindingAdapterPosition()).getName();
                            RCPAFragment.PrdCode = prdList.get(holder.getBindingAdapterPosition()).getCode();
                            RCPAFragment.rcpaBinding.tvSelectProduct.setText(prdList.get(holder.getBindingAdapterPosition()).getName());
                            RCPAFragment.rcpaBinding.tvRate.setText(prdList.get(holder.getBindingAdapterPosition()).getRate());
                            RCPAFragment.rcpaBinding.edQty.setText("");
                            RCPAFragment.rcpaBinding.tvValue.setText(prdList.get(holder.getBindingAdapterPosition()).getRate());
                            dcrCallBinding.fragmentSelectProductSide.setVisibility(View.GONE);
                        } else {
                             commonUtilsMethods.showToastMessage(context,context.getString(R.string.already_sel_prd));
                        }
                    } catch (Exception ignored) {
                    }
                } else try {
                    jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.MAPPED_COMPETITOR_PROD).getMasterSyncDataJsonArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        if (prdList.get(holder.getBindingAdapterPosition()).getCode().equalsIgnoreCase(jsonObject.getString("Our_prd_code"))) {
                            isAvailableCompetitor = true;
                            break;
                        } else {
                            isAvailableCompetitor = false;
                        }
                    }

                    if (isAvailableCompetitor) {
                        if (RCPAFragment.ProductSelectedList.size() > 0) {
                            for (int j = 0; j < RCPAFragment.ProductSelectedList.size(); j++) {
                                if (RCPAFragment.ProductSelectedList.get(j).getChe_codes().equalsIgnoreCase(RCPAFragment.CheCode) && RCPAFragment.ProductSelectedList.get(j).getPrd_code().equalsIgnoreCase(prdList.get(holder.getBindingAdapterPosition()).getCode())) {
                                    dummyCheck.add(RCPAFragment.CheCode);
                                }
                            }
                        }
                    }

                    if (isAvailableCompetitor) {
                        if (dummyCheck.size() == 0) {
                            RCPAFragment.PrdName = prdList.get(holder.getBindingAdapterPosition()).getName();
                            RCPAFragment.PrdCode = prdList.get(holder.getBindingAdapterPosition()).getCode();
                            RCPAFragment.rcpaBinding.tvSelectProduct.setText(prdList.get(holder.getBindingAdapterPosition()).getName());
                            RCPAFragment.rcpaBinding.tvRate.setText(prdList.get(holder.getBindingAdapterPosition()).getRate());
                            RCPAFragment.rcpaBinding.edQty.setText("1");
                            RCPAFragment.rcpaBinding.tvValue.setText(prdList.get(holder.getBindingAdapterPosition()).getRate());
                            dcrCallBinding.fragmentSelectProductSide.setVisibility(View.GONE);
                        } else {
                             commonUtilsMethods.showToastMessage(context,context.getString(R.string.already_sel_prd));
                        }
                    } else {
                         commonUtilsMethods.showToastMessage(context,context.getString(R.string.no_comp_available));
                    }

                } catch (Exception e) {
                    Log.v("comp_prd", "--error--" + e);
                }
            });
        }

        @Override
        public int getItemCount() {
            return prdList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<SaveCallProductList> filteredNames) {
            this.prdList = filteredNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tv_name;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
            }
        }
    }
}
