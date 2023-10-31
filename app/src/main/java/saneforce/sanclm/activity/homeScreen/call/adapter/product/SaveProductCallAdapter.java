package saneforce.sanclm.activity.homeScreen.call.adapter.product;

import static saneforce.sanclm.R.drawable.custom_switch_bg;
import static saneforce.sanclm.activity.homeScreen.call.fragments.ProductFragment.productsBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class SaveProductCallAdapter extends RecyclerView.Adapter<SaveProductCallAdapter.ViewHolder> {
    public static int pos;
    static Boolean isTouched = false;
    Context context;
    Activity activity;
    ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    ArrayList<SaveCallProductList> productListArrayList;
    CommonSharedPreference mCommonsharedpreference;
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
        mCommonsharedpreference = new CommonSharedPreference(context);
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_prd_name.setText(productListArrayList.get(position).getName());
        holder.ed_samplesQty.setText(productListArrayList.get(position).getSample_qty());
        holder.ed_rxQty.setText(productListArrayList.get(position).getRx_qty());
        holder.ed_rcpaQty.setText(productListArrayList.get(position).getRcpa_qty());
        holder.tv_stocks.setText(productListArrayList.get(position).getStock());

        holder.switch_prompt.setChecked(productListArrayList.get(position).getPromoted().equalsIgnoreCase("0"));
        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1")) {
                    holder.ed_samplesQty.setVisibility(View.VISIBLE);
                }
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1")) {
                    holder.ed_rxQty.setVisibility(View.VISIBLE);
                }
                break;
            case "2":
                if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("0")) {
                    holder.ed_samplesQty.setVisibility(View.VISIBLE);
                }
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
                    holder.ed_rxQty.setVisibility(View.VISIBLE);
                }
                break;
            case "3":
                holder.ed_samplesQty.setVisibility(View.VISIBLE);
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
                    holder.ed_rxQty.setVisibility(View.VISIBLE);
                }
                break;
            case "4":
                holder.ed_samplesQty.setVisibility(View.VISIBLE);
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
                    holder.ed_rxQty.setVisibility(View.VISIBLE);
                }
                break;
            default:
                holder.ed_samplesQty.setVisibility(View.GONE);
                holder.ed_rxQty.setVisibility(View.GONE);

        }
       /* if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1") && DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1")) {
            holder.ed_samplesQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2") && DCRCallActivity.PrdSamNeed.equalsIgnoreCase("0")) {
            holder.ed_samplesQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            holder.ed_samplesQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            holder.ed_samplesQty.setVisibility(View.VISIBLE);
        } else {
            holder.ed_samplesQty.setVisibility(View.GONE);
        }

        if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1") && DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1")) {
            holder.ed_rxQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2") && DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
            holder.ed_rxQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3") && DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
            holder.ed_rxQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4") && DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
            holder.ed_rxQty.setVisibility(View.VISIBLE);
        } else {
            holder.ed_rxQty.setVisibility(View.GONE);
        }*/

        /*if (DCRCallActivity.SampleValidation.equalsIgnoreCase("0")) {
            holder.tv_stocks.setVisibility(View.VISIBLE);
            holder.tv_stocks.setText(productListArrayList.get(position).getStock());
        } else {
            holder.tv_stocks.setVisibility(View.GONE);
        }
*/

        holder.switch_prompt.setOnTouchListener((view, motionEvent) -> {
            isTouched = true;
            return false;
        });

        holder.switch_prompt.setOnCheckedChangeListener((compoundButton, b) -> {
            if (isTouched) {
                isTouched = false;
                if (b) {
                    productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getStock(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), "0", productListArrayList.get(holder.getAdapterPosition()).isCliked()));
                } else {
                    productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getStock(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), "1", productListArrayList.get(holder.getAdapterPosition()).isCliked()));
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
                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getStock(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
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
                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getStock(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
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
                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getCode(), productListArrayList.get(holder.getAdapterPosition()).getStock(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getPromoted(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
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
                    callCommonCheckedListArrayList.set(j, new CallCommonCheckedList(callCommonCheckedListArrayList.get(j).getName(), callCommonCheckedListArrayList.get(j).getCode(), false, callCommonCheckedListArrayList.get(j).getCategory()));
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
