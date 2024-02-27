package saneforce.santrip.activity.homeScreen.call.adapter.product;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SamQtyRestrictValue;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SamQtyRestriction;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SampleValidation;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.StockSample;
import static saneforce.santrip.activity.homeScreen.call.fragments.ProductFragment.productsBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.InputFilterMinMax;

public class FinalProductCallAdapter extends RecyclerView.Adapter<FinalProductCallAdapter.ViewHolder> {
    static Boolean isTouched = false;
    Context context;
    Activity activity;
    ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    ArrayList<SaveCallProductList> productListArrayList;
    CommonUtilsMethods commonUtilsMethods;
    CheckProductListAdapter checkProductListAdapter;
    String finalValue;


    public FinalProductCallAdapter(Activity activity, Context context, ArrayList<SaveCallProductList> productListArrayList, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        this.activity = activity;
        this.context = context;
        this.productListArrayList = productListArrayList;
        this.callCommonCheckedListArrayList = callCommonCheckedListArrayList;
        commonUtilsMethods = new CommonUtilsMethods(context);
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
                if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1"))
                    holder.ed_samplesQty.setVisibility(View.VISIBLE);
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0"))
                    holder.ed_rxQty.setVisibility(View.VISIBLE);
                break;
            case "3":
            case "4":
            case "5":
            case "6":
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
            //  holder.ed_rxQty.setEnabled(false);
            holder.ed_rxQty.setInputType(InputType.TYPE_NULL);
            holder.ed_rxQty.setShowSoftInputOnFocus(false);
            holder.ed_rxQty.setShowSoftInputOnFocus(false);
            holder.ed_rxQty.setCursorVisible(false);
            holder.ed_rxQty.setFocusableInTouchMode(false);
            holder.ed_rxQty.setFocusable(false);
            holder.ed_rxQty.setText("0");
        } else if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale")) {
            //  holder.ed_samplesQty.setEnabled(false);
            holder.ed_samplesQty.setInputType(InputType.TYPE_NULL);
            holder.ed_samplesQty.setShowSoftInputOnFocus(false);
            holder.ed_samplesQty.setShowSoftInputOnFocus(false);
            holder.ed_samplesQty.setCursorVisible(false);
            holder.ed_samplesQty.setFocusableInTouchMode(false);
            holder.ed_samplesQty.setFocusable(false);
            holder.ed_samplesQty.setText("0");
        }

        if (SampleValidation.equalsIgnoreCase("1")) {
            holder.tv_stocks.setVisibility(View.VISIBLE);
            holder.tv_stocks.setText(productListArrayList.get(position).getBalance_sam_stk());
            if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sample")) {
                holder.ed_samplesQty.setEnabled(true);
            } else if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                if (Integer.parseInt(productListArrayList.get(position).getLast_stock()) > 0) {
                    holder.ed_samplesQty.setEnabled(true);
                } else {
                    //  holder.ed_samplesQty.setEnabled(false);
                    holder.ed_samplesQty.setInputType(InputType.TYPE_NULL);
                    holder.ed_samplesQty.setShowSoftInputOnFocus(false);
                    holder.ed_samplesQty.setShowSoftInputOnFocus(false);
                    holder.ed_samplesQty.setCursorVisible(false);
                    holder.ed_samplesQty.setFocusableInTouchMode(false);
                    holder.ed_samplesQty.setFocusable(false);
                    holder.ed_samplesQty.setText("0");
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
                    productListArrayList.set(holder.getBindingAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getBindingAdapterPosition()).getName(), productListArrayList.get(holder.getBindingAdapterPosition()).getCode(), productListArrayList.get(holder.getBindingAdapterPosition()).getCategory(), productListArrayList.get(holder.getBindingAdapterPosition()).getBalance_sam_stk(), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), productListArrayList.get(holder.getBindingAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getRcpa_qty(), "0", productListArrayList.get(holder.getBindingAdapterPosition()).isClicked()));
                } else {
                    productListArrayList.set(holder.getBindingAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getBindingAdapterPosition()).getName(), productListArrayList.get(holder.getBindingAdapterPosition()).getCode(), productListArrayList.get(holder.getBindingAdapterPosition()).getCategory(), productListArrayList.get(holder.getBindingAdapterPosition()).getBalance_sam_stk(), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), productListArrayList.get(holder.getBindingAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getRcpa_qty(), "1", productListArrayList.get(holder.getBindingAdapterPosition()).isClicked()));
                }
            }
        });

        holder.tv_prd_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, productListArrayList.get(position).getName()));

        holder.ed_samplesQty.setOnClickListener(view -> {
            if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale")) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.ed_samplesQty.getWindowToken(), 0);
                holder.ed_samplesQty.setShowSoftInputOnFocus(false);
                holder.ed_samplesQty.setCursorVisible(false);
                holder.ed_samplesQty.setFocusableInTouchMode(false);
                holder.ed_samplesQty.setFocusable(false);
            }
        });


        holder.ed_samplesQty.setOnTouchListener((v, event) -> {
            if (SampleValidation.equalsIgnoreCase("1")) {
                if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sample") || productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                    if (SamQtyRestriction.equalsIgnoreCase("0")) {
                        //  Log.v("asdasds", (Integer.parseInt(SamQtyRestrictValue) >= Integer.parseInt(productListArrayList.get(position).getLast_stock())) + "----" + SamQtyRestrictValue + "----" + productListArrayList.get(position).getLast_stock());
                        if (Integer.parseInt(SamQtyRestrictValue) >= Integer.parseInt(productListArrayList.get(position).getLast_stock())) {
                            finalValue = productListArrayList.get(position).getLast_stock();
                            holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", productListArrayList.get(position).getLast_stock())});
                        } else {
                            finalValue = SamQtyRestrictValue;
                            holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", SamQtyRestrictValue)});
                        }
                    } else {
                        finalValue = productListArrayList.get(position).getLast_stock();
                        holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", productListArrayList.get(position).getLast_stock())});
                    }
                }
            } else {
                if (SamQtyRestriction.equalsIgnoreCase("0")) {
                    finalValue = SamQtyRestrictValue;
                    holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", SamQtyRestrictValue)});
                }
            }
            return false;
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
                    if (SampleValidation.equalsIgnoreCase("1")) {
                        if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sample") || productListArrayList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                            holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", finalValue)});
                          /*  if (SamQtyRestriction.equalsIgnoreCase("0")) {
                                Log.v("asdasds", SamQtyRestrictValue + "----" + productListArrayList.get(position).getLast_stock());
                                if (Integer.parseInt(SamQtyRestrictValue) >= Integer.parseInt(productListArrayList.get(position).getLast_stock())) {
                                    Log.v("asdasds", "true");
                                    holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", productListArrayList.get(position).getLast_stock())});
                                } else {
                                    Log.v("asdasds", "false");
                                    holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", SamQtyRestrictValue)});
                                }
                            } else {
                                holder.ed_samplesQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", productListArrayList.get(position).getLast_stock())});
                            }*/
                            if (!editable.toString().isEmpty()) {
                                int final_value = Integer.parseInt(productListArrayList.get(position).getLast_stock()) - Integer.parseInt(editable.toString());
                                holder.tv_stocks.setText(String.valueOf(final_value));
                                productListArrayList.set(holder.getBindingAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getBindingAdapterPosition()).getName(), productListArrayList.get(holder.getBindingAdapterPosition()).getCode(), productListArrayList.get(holder.getBindingAdapterPosition()).getCategory(), String.valueOf(final_value), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), editable.toString(), productListArrayList.get(holder.getBindingAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getPromoted(), productListArrayList.get(holder.getBindingAdapterPosition()).isClicked()));
                                for (int i = 0; i < StockSample.size(); i++) {
                                    if (StockSample.get(i).getStockCode().equalsIgnoreCase(productListArrayList.get(position).getCode())) {
                                        StockSample.set(i, new CallCommonCheckedList(StockSample.get(i).getStockCode(), StockSample.get(i).getActualStock(), String.valueOf(final_value)));
                                    }
                                }
                            } else {
                                holder.tv_stocks.setText(productListArrayList.get(position).getLast_stock());
                                productListArrayList.set(holder.getBindingAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getBindingAdapterPosition()).getName(), productListArrayList.get(holder.getBindingAdapterPosition()).getCode(), productListArrayList.get(holder.getBindingAdapterPosition()).getCategory(), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), editable.toString(), productListArrayList.get(holder.getBindingAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getPromoted(), productListArrayList.get(holder.getBindingAdapterPosition()).isClicked()));
                                for (int i = 0; i < StockSample.size(); i++) {
                                    if (StockSample.get(i).getStockCode().equalsIgnoreCase(productListArrayList.get(position).getCode())) {
                                        StockSample.set(i, new CallCommonCheckedList(StockSample.get(i).getStockCode(), StockSample.get(i).getActualStock(), productListArrayList.get(position).getLast_stock()));
                                    }
                                }
                            }
                        }
                    } else {
                        productListArrayList.set(holder.getBindingAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getBindingAdapterPosition()).getName(), productListArrayList.get(holder.getBindingAdapterPosition()).getCode(), productListArrayList.get(holder.getBindingAdapterPosition()).getCategory(), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), editable.toString(), productListArrayList.get(holder.getBindingAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getPromoted(), productListArrayList.get(holder.getBindingAdapterPosition()).isClicked()));
                    }
                } catch (Exception ignored) {
                }
            }
        });


        holder.ed_rxQty.setOnClickListener(view -> {
            if (productListArrayList.get(position).getCategory().equalsIgnoreCase("Sample")) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.ed_rxQty.getWindowToken(), 0);
                holder.ed_rxQty.setShowSoftInputOnFocus(false);
                holder.ed_rxQty.setCursorVisible(false);
                holder.ed_rxQty.setFocusableInTouchMode(false);
                holder.ed_rxQty.setFocusable(false);
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
                productListArrayList.set(holder.getBindingAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getBindingAdapterPosition()).getName(), productListArrayList.get(holder.getBindingAdapterPosition()).getCode(), productListArrayList.get(holder.getBindingAdapterPosition()).getCategory(), productListArrayList.get(holder.getBindingAdapterPosition()).getBalance_sam_stk(), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), productListArrayList.get(holder.getBindingAdapterPosition()).getSample_qty(), editable.toString(), productListArrayList.get(holder.getBindingAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getPromoted(), productListArrayList.get(holder.getBindingAdapterPosition()).isClicked()));
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
                productListArrayList.set(holder.getBindingAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getBindingAdapterPosition()).getName(), productListArrayList.get(holder.getBindingAdapterPosition()).getCode(), productListArrayList.get(holder.getBindingAdapterPosition()).getCategory(), productListArrayList.get(holder.getBindingAdapterPosition()).getBalance_sam_stk(), productListArrayList.get(holder.getBindingAdapterPosition()).getLast_stock(), productListArrayList.get(holder.getBindingAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getBindingAdapterPosition()).getRx_qty(), editable.toString(), productListArrayList.get(holder.getBindingAdapterPosition()).getPromoted(), productListArrayList.get(holder.getBindingAdapterPosition()).isClicked()));
            }
        });

        if (CheckProductListAdapter.isCheckedPrd && !CheckProductListAdapter.UnSelectedPrdCode.isEmpty()) {
            for (int i = 0; i < productListArrayList.size(); i++) {
                if (CheckProductListAdapter.UnSelectedPrdCode.equalsIgnoreCase(productListArrayList.get(position).getCode())) {
                    new CountDownTimer(80, 80) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            try {
                                for (int i = 0; i < StockSample.size(); i++) {
                                    int currentBalance;
                                    if (StockSample.get(i).getStockCode().equalsIgnoreCase(productListArrayList.get(position).getCode())) {
                                        if (productListArrayList.get(position).getSample_qty().equalsIgnoreCase("0") || productListArrayList.get(position).getSample_qty().isEmpty()) {
                                            currentBalance = Integer.parseInt(StockSample.get(i).getCurrentStock());
                                        } else {
                                            currentBalance = Integer.parseInt(StockSample.get(i).getCurrentStock()) + Integer.parseInt(productListArrayList.get(position).getSample_qty());
                                        }
                                        StockSample.set(i, new CallCommonCheckedList(StockSample.get(i).getStockCode(), StockSample.get(i).getActualStock(), String.valueOf(currentBalance)));
                                    }
                                }
                                removeAt(position);
                                CheckProductListAdapter.isCheckedPrd = false;
                            } catch (Exception ignored) {
                            }
                        }
                    }.start();
                    break;
                }
            }
        }

        holder.img_del_prd.setOnClickListener(view -> {
            try {
                for (int j = 0; j < callCommonCheckedListArrayList.size(); j++) {
                    if (callCommonCheckedListArrayList.get(j).getCode().equalsIgnoreCase(productListArrayList.get(position).getCode())) {
                        callCommonCheckedListArrayList.set(j, new CallCommonCheckedList(callCommonCheckedListArrayList.get(j).getName(), callCommonCheckedListArrayList.get(j).getCode(), callCommonCheckedListArrayList.get(j).getStock_balance(), false, callCommonCheckedListArrayList.get(j).getCategory(), callCommonCheckedListArrayList.get(j).getCategoryExtra()));
                        break;
                    }
                }

                for (int i = 0; i < StockSample.size(); i++) {
                    int currentBalance;
                    if (StockSample.get(i).getStockCode().equalsIgnoreCase(productListArrayList.get(position).getCode())) {
                        if (productListArrayList.get(position).getSample_qty().equalsIgnoreCase("0") || productListArrayList.get(position).getSample_qty().isEmpty()) {
                            currentBalance = Integer.parseInt(StockSample.get(i).getCurrentStock());
                        } else {
                            currentBalance = Integer.parseInt(StockSample.get(i).getCurrentStock()) + Integer.parseInt(productListArrayList.get(position).getSample_qty());
                        }
                        StockSample.set(i, new CallCommonCheckedList(StockSample.get(i).getStockCode(), StockSample.get(i).getActualStock(), String.valueOf(currentBalance)));
                    }
                }

                checkProductListAdapter = new CheckProductListAdapter(activity, context, callCommonCheckedListArrayList, productListArrayList);
                commonUtilsMethods.recycleTestWithDivider(productsBinding.rvCheckDataList);
                productsBinding.rvCheckDataList.setAdapter(checkProductListAdapter);
                removeAt(position);
            } catch (Exception ignored) {
            }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
            ed_rcpaQty = itemView.findViewById(R.id.tv_rcpa);
            img_del_prd = itemView.findViewById(R.id.img_del_prd);
            switch_prompt = itemView.findViewById(R.id.switch_promoted);
        }
    }
}
