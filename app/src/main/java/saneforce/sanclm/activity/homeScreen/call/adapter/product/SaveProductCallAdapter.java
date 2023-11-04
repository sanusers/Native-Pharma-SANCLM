package saneforce.sanclm.activity.homeScreen.call.adapter.product;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.SampleValidation;
import static saneforce.sanclm.activity.homeScreen.call.fragments.ProductFragment.productsBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class SaveProductCallAdapter extends RecyclerView.Adapter<SaveProductCallAdapter.ViewHolder> {
    public static int pos;
    static Boolean isTouched = false;
    Context context;
    Activity activity;
    ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    ArrayList<SaveCallProductList> productListArrayList;
    CommonUtilsMethods commonUtilsMethods;
    CallProductListAdapter callProductListAdapter;


    public SaveProductCallAdapter(Activity activity, Context context, ArrayList<SaveCallProductList> productListArrayList, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        this.activity = activity;
        this.context = context;
        this.productListArrayList = productListArrayList;
        this.callCommonCheckedListArrayList = callCommonCheckedListArrayList;
    }

    public static int getPosition() {
        return pos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_prod_call, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_prd_name.setText(productListArrayList.get(position).getName());
        holder.ed_samplesQty.setText(productListArrayList.get(position).getSample_qty());
        holder.ed_rxQty.setText(productListArrayList.get(position).getRx_qty());
        holder.ed_rcpaQty.setText(productListArrayList.get(position).getRcpa_qty());
        holder.switch_prompt.setChecked(productListArrayList.get(position).getPromoted().equalsIgnoreCase("0"));

        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1"))
                    holder.ed_samplesQty.setVisibility(View.VISIBLE);
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1"))
                    holder.ed_rxQty.setVisibility(View.VISIBLE);
                break;
            case "2":
            case "3":
            case "4":
                if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("0"))
                    holder.ed_samplesQty.setVisibility(View.VISIBLE);
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0"))
                    holder.ed_rxQty.setVisibility(View.VISIBLE);
                break;
            default:
                holder.ed_samplesQty.setVisibility(View.GONE);
                holder.ed_rxQty.setVisibility(View.GONE);
        }

       /* Queries    	  Qty	          Sample	          Sale	          Sale/Sample
        Selected	  Available	        ok	               ok	             ok
                      Not Available	   not ok	           ok	             ok
        Typing	       Available	     ok	               ok	            not ok
                      Not Available	    not ok	           ok	            not ok*/


        if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sample")) {
            holder.ed_rxQty.setEnabled(false);
            holder.ed_rxQty.setText("0");
        }

        if (SampleValidation.equalsIgnoreCase("0")) {
            holder.tv_stocks.setVisibility(View.VISIBLE);
            holder.tv_stocks.setText(productListArrayList.get(position).getBalance_sam_stk());
            if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale") || productListArrayList.get(position).getCategory().equalsIgnoreCase("Sample")) {
                holder.ed_samplesQty.setEnabled(true);
            } else if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                if (Integer.parseInt(productListArrayList.get(position).getSam_stk()) > 0) {
                    holder.ed_samplesQty.setEnabled(true);
                } else {
                    holder.ed_samplesQty.setEnabled(false);
                    holder.ed_samplesQty.setText("0");
                    // holder.ed_samplesQty.setBackground(context.getResources().getDrawable(R.drawable.bg_light_grey_disable));
                }
            }
        } else {
            holder.tv_stocks.setVisibility(View.GONE);
        }

        holder.switch_prompt.setOnTouchListener((view, motionEvent) -> {
            isTouched = true;
            return false;
        });

        holder.switch_prompt.setOnCheckedChangeListener((compoundButton, b) -> {
            if (isTouched) {
                isTouched = false;
                if (b) {
                    productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getCategory(), productListArrayList.get(holder.getAdapterPosition()).getBalance_sam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), "0", productListArrayList.get(holder.getAdapterPosition()).isCliked()));
                } else {
                    productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getCategory(), productListArrayList.get(holder.getAdapterPosition()).getBalance_sam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), "1", productListArrayList.get(holder.getAdapterPosition()).isCliked()));
                }
            }
        });

        holder.tv_prd_name.setOnClickListener(view -> {
            commonUtilsMethods.displayPopupWindow(activity, context, view, productListArrayList.get(position).getName());
        });

        holder.ed_samplesQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (SampleValidation.equalsIgnoreCase("0")) {
                        if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale")) {
                            productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getCategory(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
                        } else if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sample") || productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                            holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", productListArrayList.get(position).getSam_stk())});
                            if (!editable.toString().isEmpty()) {
                                int final_value = Integer.parseInt(productListArrayList.get(position).getSam_stk()) - Integer.parseInt(editable.toString());
                                holder.tv_stocks.setText(String.valueOf(final_value));
                                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getCategory(), String.valueOf(final_value), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
                            } else {
                                holder.tv_stocks.setText(productListArrayList.get(position).getSam_stk());
                                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getCategory(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
                            }
                        }
                    } else {
                        productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getCategory(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
                    }
                } catch (Exception e) {

                }
            }
        });

        holder.ed_rxQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getCategory(), productListArrayList.get(holder.getAdapterPosition()).getBalance_sam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
            }
        });

        holder.ed_rcpaQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getCategory(), productListArrayList.get(holder.getAdapterPosition()).getBalance_sam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSam_stk(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
            }
        });

        if (CallProductListAdapter.isCheckedPrd && !CallProductListAdapter.UnSelectedPrdCode.isEmpty()) {
            for (int i = 0; i < productListArrayList.size(); i++) {
                if (CallProductListAdapter.UnSelectedPrdCode.equalsIgnoreCase(productListArrayList.get(position).getCode())) {
                    new CountDownTimer(200, 200) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            pos = position;
                            removeAt(position);
                            CallProductListAdapter.isCheckedPrd = false;
                            // mCommonsharedpreference.setValueToPreference("checked_prd", false);
                        }
                    }.start();
                    break;
                }
            }
        }

        holder.img_del_prd.setOnClickListener(view -> {
            for (int j = 0; j < callCommonCheckedListArrayList.size(); j++) {
                if (callCommonCheckedListArrayList.get(j).getName().equalsIgnoreCase(productListArrayList.get(position).getName())) {
                    callCommonCheckedListArrayList.set(j, new CallCommonCheckedList(callCommonCheckedListArrayList.get(j).getName(), callCommonCheckedListArrayList.get(j).getCode(), callCommonCheckedListArrayList.get(j).getStock_balance(), false, callCommonCheckedListArrayList.get(j).getCategory(), callCommonCheckedListArrayList.get(j).getCategoryExtra()));
                }
            }

            callProductListAdapter = new CallProductListAdapter(activity, context, callCommonCheckedListArrayList, productListArrayList);
            commonUtilsMethods.recycleTestWithDivider(productsBinding.rvCheckDataList);
            productsBinding.rvCheckDataList.setAdapter(callProductListAdapter);
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return productListArrayList.size();
    }

    public void removeAt(int position) {
        productListArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productListArrayList.size());
    }

    public class InputFilterMinMax implements InputFilter {

        private final int min;
        private final int max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input)) return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_prd_name, tv_stocks;
        EditText ed_samplesQty, ed_rxQty, ed_rcpaQty;
        ImageView img_del_prd;
        SwitchCompat switch_prompt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_prd_name = itemView.findViewById(R.id.tv_prd_name);
            tv_stocks = itemView.findViewById(R.id.tv_stock);
            ed_samplesQty = itemView.findViewById(R.id.ed_samples);
            ed_rxQty = itemView.findViewById(R.id.ed_rx_qty);
            ed_rcpaQty = itemView.findViewById(R.id.ed_rcpa);
            img_del_prd = itemView.findViewById(R.id.img_del_prd);
            switch_prompt = itemView.findViewById(R.id.switch_promoted);
        }
    }
}
