package saneforce.sanclm.activity.homeScreen.call.adapter.rcpa;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddCompSideView;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedCompList;

public class RCPAAddCompAdapter extends RecyclerView.Adapter<RCPAAddCompAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<RCPAAddCompSideView> RCPAAddCompSideViewArrayList;
    ArrayList<RCPAAddedCompList> rcpa_added_list;
    ArrayList<String> companyArrayList = new ArrayList<>();
    ArrayList<String> brandArrayList = new ArrayList<>();
    int position;
    CommonUtilsMethods commonUtilsMethods;

    public RCPAAddCompAdapter(Activity activity, Context context, ArrayList<RCPAAddCompSideView> RCPAAddCompSideViewArrayList, ArrayList<RCPAAddedCompList> rcpa_added_list) {
        this.activity = activity;
        this.context = context;
        this.RCPAAddCompSideViewArrayList = RCPAAddCompSideViewArrayList;
        this.rcpa_added_list = rcpa_added_list;
    }

    public RCPAAddCompAdapter(Context context, ArrayList<RCPAAddCompSideView> RCPAAddCompSideViewArrayList) {
        this.context = context;
        this.RCPAAddCompSideViewArrayList = RCPAAddCompSideViewArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_comp_rcpa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        position = holder.getAdapterPosition();
        AddSpinnerAdapter(holder.spin_company, holder.spin_brand, holder.tv_rate, holder.tv_value, position);
        Log.v("qyut", "--" + rcpa_added_list.get(pos).getQty());
        holder.tv_prd_name.setText(RCPAAddCompSideViewArrayList.get(pos).getPrd_name());
        holder.tv_value.setText(rcpa_added_list.get(pos).getValue());
        holder.tv_rate.setText(rcpa_added_list.get(pos).getRate());
        holder.edt_qty.setText(rcpa_added_list.get(pos).getQty());
        holder.ed_remarks.setText(rcpa_added_list.get(pos).getRemarks());

        holder.edt_qty.setTransformationMethod(null);
        commonUtilsMethods.setSpinText(holder.spin_company, rcpa_added_list.get(position).getComp_name());
        commonUtilsMethods.setSpinText(holder.spin_brand, rcpa_added_list.get(position).getComp_brand());

        holder.edt_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                rcpa_added_list.set(position, new RCPAAddedCompList(rcpa_added_list.get(position).getChem_names(), rcpa_added_list.get(position).getPrd_name(), rcpa_added_list.get(position).getComp_name(), rcpa_added_list.get(position).getComp_brand(), editable.toString(), rcpa_added_list.get(position).getRate()
                        , rcpa_added_list.get(position).getValue(), rcpa_added_list.get(position).getRemarks()));
            }
        });


        holder.ed_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                rcpa_added_list.set(position, new RCPAAddedCompList(rcpa_added_list.get(position).getChem_names(), rcpa_added_list.get(position).getPrd_name(), rcpa_added_list.get(position).getComp_name(), rcpa_added_list.get(position).getComp_brand(), rcpa_added_list.get(position).getQty(), rcpa_added_list.get(position).getRate()
                        , rcpa_added_list.get(position).getValue(), editable.toString()));
            }
        });

    }

    private void AddSpinnerAdapter(Spinner spin_company, Spinner spin_brand, TextView tv_rate, TextView tv_value, int position) {
        companyArrayList.clear();
        companyArrayList.add("Becon");
        companyArrayList.add("Paracetemol");
        companyArrayList.add("Calci Tab");
        companyArrayList.add("Loysta 900");
        companyArrayList.add("Jaromate");
        companyArrayList.add("Select");

        ArrayAdapter<String> dataAdaptercompany = new ArrayAdapter<>(context, R.layout.textview_bg_spinner, companyArrayList);
        dataAdaptercompany.setDropDownViewResource(R.layout.textview_bg_spinner);
        spin_company.setAdapter(dataAdaptercompany);

        spin_company.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rcpa_added_list.set(position, new RCPAAddedCompList(rcpa_added_list.get(position).getChem_names(), rcpa_added_list.get(position).getPrd_name(), spin_company.getSelectedItem().toString(), rcpa_added_list.get(position).getComp_brand(), rcpa_added_list.get(position).getQty(), rcpa_added_list.get(position).getRate()
                        , rcpa_added_list.get(position).getValue(), rcpa_added_list.get(position).getRemarks()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        brandArrayList.clear();
        brandArrayList.add("Amazon");
        brandArrayList.add("Flipkart");
        brandArrayList.add("Dolo 650");
        brandArrayList.add("Injective 500");
        brandArrayList.add("Odoform");
        brandArrayList.add("Herazite 80");
        brandArrayList.add("Select");

        spin_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (spin_brand.getSelectedItem().toString()) {
                    case "Select":
                        tv_rate.setText("");
                        tv_value.setText("");
                        break;
                    case "Amazon":
                        tv_rate.setText("30");
                        tv_value.setText("95.7");
                        break;
                    case "Flipkart":
                        tv_rate.setText("93");
                        tv_value.setText("8.7");
                        break;
                    case "Dolo 650":
                        tv_rate.setText("87");
                        tv_value.setText("5.4");
                        break;
                    case "Injective 500":
                        tv_rate.setText("56");
                        tv_value.setText("8.9");
                        break;
                    case "Odoform":
                        tv_rate.setText("9.50");
                        tv_value.setText("5.5");
                        break;
                    default:
                        tv_rate.setText("0");
                        tv_value.setText("0");
                        break;
                }
                rcpa_added_list.set(position, new RCPAAddedCompList(rcpa_added_list.get(position).getChem_names(), rcpa_added_list.get(position).getPrd_name(), rcpa_added_list.get(position).getComp_name(), spin_brand.getSelectedItem().toString(), rcpa_added_list.get(position).getQty(), tv_rate.getText().toString()
                        , tv_value.getText().toString(), rcpa_added_list.get(position).getRemarks()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> dataAdapterbrand = new ArrayAdapter<>(context, R.layout.textview_bg_spinner, brandArrayList);
        dataAdapterbrand.setDropDownViewResource(R.layout.textview_bg_spinner);
        spin_brand.setAdapter(dataAdapterbrand);
    }

    @Override
    public int getItemCount() {
        return RCPAAddCompSideViewArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_prd_name;
        Spinner spin_company, spin_brand;
        EditText edt_qty, ed_remarks;
        TextView tv_rate, tv_value;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_prd_name = itemView.findViewById(R.id.tv_prd_name);
            spin_company = itemView.findViewById(R.id.spin_comp_cmpny);
            spin_brand = itemView.findViewById(R.id.spin_comp_brand);
            ed_remarks = itemView.findViewById(R.id.ed_remarks);
            edt_qty = itemView.findViewById(R.id.ed_qty_comp);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_rate = itemView.findViewById(R.id.tv_rate);
        }
    }
}
