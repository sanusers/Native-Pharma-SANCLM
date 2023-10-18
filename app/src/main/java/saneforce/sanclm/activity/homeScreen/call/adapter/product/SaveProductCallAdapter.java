package saneforce.sanclm.activity.homeScreen.call.adapter.product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.fragments.ProductFragment;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class SaveProductCallAdapter extends RecyclerView.Adapter<SaveProductCallAdapter.ViewHolder> {
    public static int pos;
    Context context;
    Activity activity;
    ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    ArrayList<SaveCallProductList> productListArrayList;
    CommonSharedPreference mCommonsharedpreference;
    CommonUtilsMethods commonUtilsMethods;
    CallProductListAdapter callProductListAdapter;

    public SaveProductCallAdapter(Context context) {
        this.context = context;
    }

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mCommonsharedpreference = new CommonSharedPreference(context);
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_prd_name.setText(productListArrayList.get(position).getName());
        holder.tv_stocks.setText(productListArrayList.get(position).getStock());
        holder.ed_samplesQty.setText(productListArrayList.get(position).getSample_qty());
        holder.ed_rxQty.setText(productListArrayList.get(position).getRx_qty());
        holder.ed_rcpaQty.setText(productListArrayList.get(position).getRcpa_qty());

        holder.tv_prd_name.setOnClickListener(view -> {
            if (holder.tv_prd_name.getText().toString().length() > 12) {
                commonUtilsMethods.displayPopupWindow(activity, context, view, productListArrayList.get(position).getName());
            }
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
                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getStock(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
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
                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getStock(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).getRcpa_qty(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
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
                productListArrayList.set(holder.getAdapterPosition(), new SaveCallProductList(productListArrayList.get(holder.getAdapterPosition()).getName(), productListArrayList.get(holder.getAdapterPosition()).getStock(), productListArrayList.get(holder.getAdapterPosition()).getSample_qty(), productListArrayList.get(holder.getAdapterPosition()).getRx_qty(), editable.toString(), productListArrayList.get(holder.getAdapterPosition()).isCliked()));
            }
        });

        if (mCommonsharedpreference.getBooleanValueFromPreference("checked_prd") && !mCommonsharedpreference.getValueFromPreference("unselect_data_prd").isEmpty()) {
            for (int i = 0; i < productListArrayList.size(); i++) {
                if (mCommonsharedpreference.getValueFromPreference("unselect_data_prd").equalsIgnoreCase(productListArrayList.get(position).getName())) {
                    new CountDownTimer(200, 200) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            pos = position;
                            removeAt(position);
                            mCommonsharedpreference.setValueToPreference("checked_prd", false);
                        }
                    }.start();
                    break;
                }
            }
        }

        holder.img_del_prd.setOnClickListener(view -> {
            // Log.v("getpos", "----" + callProductListArrayList.size());
            for (int j = 0; j < callCommonCheckedListArrayList.size(); j++) {
                if (callCommonCheckedListArrayList.get(j).getName().equalsIgnoreCase(productListArrayList.get(position).getName())) {
                    callCommonCheckedListArrayList.set(j, new CallCommonCheckedList(callCommonCheckedListArrayList.get(j).getName(), callCommonCheckedListArrayList.get(j).getCode(), false, callCommonCheckedListArrayList.get(j).getCategory()));
                }
            }

            callProductListAdapter = new CallProductListAdapter(activity, context, callCommonCheckedListArrayList, productListArrayList);
            commonUtilsMethods.recycleTestWithDivider(ProductFragment.rv_list_data);
            ProductFragment.rv_list_data.setAdapter(callProductListAdapter);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_prd_name = itemView.findViewById(R.id.tv_prd_name);
            tv_stocks = itemView.findViewById(R.id.tv_stock);
            ed_samplesQty = itemView.findViewById(R.id.ed_samples);
            ed_rxQty = itemView.findViewById(R.id.ed_rx_qty);
            ed_rcpaQty = itemView.findViewById(R.id.ed_rcpa);
            img_del_prd = itemView.findViewById(R.id.img_del_prd);
        }
    }
}
