package saneforce.sanclm.activity.homeScreen.call.adapter.rcpa;

import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.ChemistSelectedList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.ProductSelectedList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment.rcpaBinding;
import static saneforce.sanclm.activity.homeScreen.call.fragments.RCPASelectCompSide.rcpa_comp_list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedProdList;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.ListenerEditText;

public class RCPACompListAdapter extends RecyclerView.Adapter<RCPACompListAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<RCPAAddedCompList> CompetitorList;
    Dialog dialogRemarks;
    double getQty;
    double getPrdTotalStatic;
    double valueTotal, finalValuePrd;
    RCPAChemistAdapter rcpaChemistAdapter;
    CommonUtilsMethods commonUtilsMethods;
    double getTotalValue = 0, valueRounded;
    ArrayList<Double> CompQty = new ArrayList<>();


    public RCPACompListAdapter(Activity activity,Context context, ArrayList<RCPAAddedCompList> CompetitorList) {
        this.activity = activity;
        this.context = context;
        this.CompetitorList = CompetitorList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_comp_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_company.setText(CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product());
        holder.tv_brand.setText(CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name());
        holder.tv_comp_code.setText(CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code());
        holder.tv_brand_code.setText(CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code());
        holder.ed_qty.setText(CompetitorList.get(holder.getBindingAdapterPosition()).getQty());
        holder.tv_rate.setText(CompetitorList.get(holder.getBindingAdapterPosition()).getRate());
        holder.tv_value.setText(CompetitorList.get(holder.getBindingAdapterPosition()).getValue());

        getPrdTotalStatic = Double.parseDouble(CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue());

        if (!CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks().isEmpty()) {
            holder.img_remarks.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_remarks_0));
        } else {
            holder.img_remarks.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_remarks_1));
        }


        holder.tv_company.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, holder.tv_company.getText().toString()));

        holder.tv_brand.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, holder.tv_brand.getText().toString()));


        holder.ed_qty.setKeyImeChangeListener((keyCode, event) -> {
            if (keyCode == 4 && event.getAction() == KeyEvent.ACTION_DOWN) {
                updateTotalValues();
            }
        });

        holder.ed_qty.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.ed_qty.getWindowToken(), 0);
                updateTotalValues();
                return true;
            }
            return false;
        });

        holder.ed_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    valueTotal = 0.0;
                    finalValuePrd = 0.0;

                    if (!editable.toString().isEmpty() && !editable.toString().equalsIgnoreCase("0") && !holder.tv_rate.getText().toString().isEmpty()) {
                        getQty = Double.parseDouble(editable.toString()) * Double.parseDouble(holder.tv_rate.getText().toString());
                        double valueRounded = Math.round(getQty * 100D) / 100D;
                        holder.tv_value.setText(String.valueOf(valueRounded));

                        CompetitorList.set(holder.getBindingAdapterPosition(), new RCPAAddedCompList(CompetitorList.get(holder.getBindingAdapterPosition()).getChem_names(), CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code(), editable.toString(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), String.valueOf(valueRounded), CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks(), CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue()));
                        for (int i = 0; i < rcpa_comp_list.size(); i++) {
                            if (CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_product_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_company_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code().equalsIgnoreCase(rcpa_comp_list.get(i).getPrd_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code().equalsIgnoreCase(rcpa_comp_list.get(i).getChem_Code())) {
                                rcpa_comp_list.set(i, new RCPAAddedCompList(CompetitorList.get(holder.getBindingAdapterPosition()).getChem_names(), CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code(), editable.toString(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), String.valueOf(valueRounded), CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks(), CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue()));
                            }
                        }

                        Log.v("total", "---after---" + valueTotal + "---" + getPrdTotalStatic);
                        valueTotal = getPrdTotalStatic + getQty;
                        valueTotal = Math.round(valueTotal * 100D) / 100D;
                        finalValuePrd = valueTotal;
                        Log.v("total", "---after---" + valueRounded);

                    } else if (editable.toString().equalsIgnoreCase("0")) {
                        CompetitorList.set(holder.getBindingAdapterPosition(), new RCPAAddedCompList(CompetitorList.get(holder.getBindingAdapterPosition()).getChem_names(), CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code(), editable.toString(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), "0", CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks(), CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue()));
                        for (int i = 0; i < rcpa_comp_list.size(); i++) {
                            if (CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_product_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_company_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code().equalsIgnoreCase(rcpa_comp_list.get(i).getPrd_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code().equalsIgnoreCase(rcpa_comp_list.get(i).getChem_Code())) {
                                rcpa_comp_list.set(i, new RCPAAddedCompList(CompetitorList.get(holder.getBindingAdapterPosition()).getChem_names(), CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code(), editable.toString(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), "0", CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks(), CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue()));
                            }
                        }

                        holder.tv_value.setText("0");
                        finalValuePrd = getPrdTotalStatic;
                    } else {
                        CompetitorList.set(holder.getBindingAdapterPosition(), new RCPAAddedCompList(CompetitorList.get(holder.getBindingAdapterPosition()).getChem_names(), CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code(), editable.toString(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks(), CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue()));
                        for (int i = 0; i < rcpa_comp_list.size(); i++) {
                            if (CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_product_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_company_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code().equalsIgnoreCase(rcpa_comp_list.get(i).getPrd_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code().equalsIgnoreCase(rcpa_comp_list.get(i).getChem_Code())) {
                                rcpa_comp_list.set(i, new RCPAAddedCompList(CompetitorList.get(holder.getBindingAdapterPosition()).getChem_names(), CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code(), editable.toString(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks(), CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue()));
                            }
                        }

                        holder.tv_value.setText("");
                        finalValuePrd = getPrdTotalStatic;

                    }
                } catch (Exception e) {
                    Log.e("deleting_comb", "--refresh--" + e);
                }
            }
        });

        holder.img_remarks.setOnClickListener(view -> {
            dialogRemarks = new Dialog(context);
            dialogRemarks.setContentView(R.layout.popup_rcpa_remarks);
            Objects.requireNonNull(dialogRemarks.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogRemarks.setCancelable(false);

            ImageView iv_close = dialogRemarks.findViewById(R.id.img_close);
            EditText ed_remark = dialogRemarks.findViewById(R.id.ed_remark);
            Button btn_clear = dialogRemarks.findViewById(R.id.btn_clear);
            Button btn_save = dialogRemarks.findViewById(R.id.btn_save);
            ed_remark.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_remark)});

            if (!CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks().isEmpty()) {
                ed_remark.setText(CompetitorList.get(holder.getBindingAdapterPosition()).getRemarks());
            }

            iv_close.setOnClickListener(view1 -> {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ed_remark.getWindowToken(), 0);
                ed_remark.setText("");
                dialogRemarks.dismiss();
            });

            btn_clear.setOnClickListener(view12 -> {
                ed_remark.setText("");
                ed_remark.setHint("Type your Remarks");
            });

            btn_save.setOnClickListener(view13 -> {
                if (!TextUtils.isEmpty(ed_remark.getText().toString())) {
                    CompetitorList.set(holder.getBindingAdapterPosition(), new RCPAAddedCompList(CompetitorList.get(holder.getBindingAdapterPosition()).getChem_names(), CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getQty(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), CompetitorList.get(holder.getBindingAdapterPosition()).getValue(), ed_remark.getText().toString(), CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue()));
                    for (int i = 0; i < rcpa_comp_list.size(); i++) {
                        if (CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_product_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code().equalsIgnoreCase(rcpa_comp_list.get(i).getComp_company_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code().equalsIgnoreCase(rcpa_comp_list.get(i).getPrd_code()) && CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code().equalsIgnoreCase(rcpa_comp_list.get(i).getChem_Code())) {
                            rcpa_comp_list.set(i, new RCPAAddedCompList(CompetitorList.get(holder.getBindingAdapterPosition()).getChem_names(), CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_name(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product(), CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code(), CompetitorList.get(holder.getBindingAdapterPosition()).getQty(), CompetitorList.get(holder.getBindingAdapterPosition()).getRate(), CompetitorList.get(holder.getBindingAdapterPosition()).getValue(), ed_remark.getText().toString(), CompetitorList.get(holder.getBindingAdapterPosition()).getTotalPrdValue()));
                            break;
                        }
                    }
                    dialogRemarks.dismiss();
                    holder.img_remarks.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_remarks_0));
                } else {
                    Toast.makeText(context, context.getResources().getText(R.string.toast_enter_remarks), Toast.LENGTH_SHORT).show();
                }
            });
            dialogRemarks.show();
        });


        holder.img_del.setOnClickListener(view -> {
            try {
                Log.v("deleting_comb", holder.tv_comp_code.getText().toString() + "---" + holder.tv_brand_code.getText().toString() + "---" + CompetitorList.get(holder.getBindingAdapterPosition()).getComp_company_code() + "---" + CompetitorList.get(holder.getBindingAdapterPosition()).getComp_product_code());
                String BrandCode = holder.tv_brand_code.getText().toString();
                String CompanyCode = holder.tv_comp_code.getText().toString();
                String PrdCode = CompetitorList.get(holder.getBindingAdapterPosition()).getPrd_code();
                String CheCode = CompetitorList.get(holder.getBindingAdapterPosition()).getChem_Code();

                for (int i = 0; i < rcpa_comp_list.size(); i++) {
                    if (rcpa_comp_list.get(i).getComp_product_code().equalsIgnoreCase(BrandCode) && rcpa_comp_list.get(i).getComp_company_code().equalsIgnoreCase(CompanyCode) && rcpa_comp_list.get(i).getPrd_code().equalsIgnoreCase(PrdCode) && rcpa_comp_list.get(i).getChem_Code().equalsIgnoreCase(CheCode)) {
                        rcpa_comp_list.remove(i);
                        break;
                    }
                }

                updateTotalValues();

            } catch (Exception e) {
                Log.e("deleting_comb", "--remove--" + e);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateTotalValues() {
        for (int j = 0; j < ProductSelectedList.size(); j++) {
            CompQty = new ArrayList<>();
            for (int i = 0; i < rcpa_comp_list.size(); i++) {
                if (rcpa_comp_list.get(i).getChem_Code().equalsIgnoreCase(ProductSelectedList.get(j).getChe_codes()) && rcpa_comp_list.get(i).getPrd_code().equalsIgnoreCase(ProductSelectedList.get(j).getPrd_code())) {
                    if (!rcpa_comp_list.get(i).getValue().isEmpty()) {
                        getTotalValue = Double.parseDouble(ProductSelectedList.get(j).getValue());
                        CompQty.add(Double.parseDouble(rcpa_comp_list.get(i).getValue()));
                    }
                }
            }

            if (CompQty.size() > 0) {
                for (int i = 0; i < CompQty.size(); i++) {
                    getTotalValue = getTotalValue + CompQty.get(i);
                }
                valueRounded = Math.round(getTotalValue * 100D) / 100D;
                ProductSelectedList.set(j, new RCPAAddedProdList(ProductSelectedList.get(j).getChem_names(), ProductSelectedList.get(j).getChe_codes(), ProductSelectedList.get(j).getPrd_name(), ProductSelectedList.get(j).getPrd_code(), ProductSelectedList.get(j).getQty(), ProductSelectedList.get(j).getRate(), ProductSelectedList.get(j).getValue(), String.valueOf(valueRounded)));
            } else {
                ProductSelectedList.set(j, new RCPAAddedProdList(ProductSelectedList.get(j).getChem_names(), ProductSelectedList.get(j).getChe_codes(), ProductSelectedList.get(j).getPrd_name(), ProductSelectedList.get(j).getPrd_code(), ProductSelectedList.get(j).getQty(), ProductSelectedList.get(j).getRate(), ProductSelectedList.get(j).getValue(), ProductSelectedList.get(j).getValue()));
            }
        }


        for (int j = 0; j < ChemistSelectedList.size(); j++) {
            CompQty = new ArrayList<>();
            for (int i = 0; i < ProductSelectedList.size(); i++) {
                if (ProductSelectedList.get(i).getChe_codes().equalsIgnoreCase(ChemistSelectedList.get(j).getCode())) {
                    if (!ProductSelectedList.get(i).getTotalPrdValue().isEmpty()) {
                        getTotalValue = 0.0;
                        CompQty.add(Double.parseDouble(ProductSelectedList.get(i).getTotalPrdValue()));
                    }
                }
            }

            if (CompQty.size() > 0) {
                for (int i = 0; i < CompQty.size(); i++) {
                    getTotalValue = getTotalValue + CompQty.get(i);
                }
                valueRounded = Math.round(getTotalValue * 100D) / 100D;
                ChemistSelectedList.set(j, new CustList(ChemistSelectedList.get(j).getName(), ChemistSelectedList.get(j).getCode(), String.valueOf(valueRounded), ""));
            }
        }

        rcpaChemistAdapter = new RCPAChemistAdapter(activity,context, ChemistSelectedList, ProductSelectedList, rcpa_comp_list);
        commonUtilsMethods.recycleTestWithoutDivider(rcpaBinding.rvRcpaChemistList);
        rcpaBinding.rvRcpaChemistList.setAdapter(rcpaChemistAdapter);
        rcpaChemistAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return CompetitorList.size();
    }

    public void removeAt(int position) {
        CompetitorList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, CompetitorList.size());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_company, tv_brand, tv_rate, tv_value, tv_comp_code, tv_brand_code;
        ListenerEditText ed_qty;
        ConstraintLayout constraint_main;
        ImageView img_remarks, img_del;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_company = itemView.findViewById(R.id.tv_comp_cmpy);
            tv_brand = itemView.findViewById(R.id.tv_comp_brand);
            ed_qty = itemView.findViewById(R.id.tv_qty_comp);
            tv_rate = itemView.findViewById(R.id.tv_rate_comp);
            tv_value = itemView.findViewById(R.id.tv_value_comp);
            tv_comp_code = itemView.findViewById(R.id.tv_comp_code);
            tv_brand_code = itemView.findViewById(R.id.tv_brand_code);
            img_remarks = itemView.findViewById(R.id.img_remarks);
            img_del = itemView.findViewById(R.id.img_del);
            constraint_main = itemView.findViewById(R.id.constraint_list_comp);
        }
    }
}
