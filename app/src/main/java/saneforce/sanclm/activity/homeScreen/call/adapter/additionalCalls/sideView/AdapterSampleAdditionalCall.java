package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterSampleAdditionalCall extends RecyclerView.Adapter<AdapterSampleAdditionalCall.ViewHolder> {
    Context context;
    ArrayList<AddSampleAdditionalCall> addSampleAdditionalCallArrayList;
    ArrayList<String> sampleListSpinner = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;

    public AdapterSampleAdditionalCall(Context context, ArrayList<AddSampleAdditionalCall> addSampleAdditionalCallArrayList) {
        this.context = context;
        this.addSampleAdditionalCallArrayList = addSampleAdditionalCallArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_sample_additional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        sampleListSpinner.clear();
        sampleListSpinner.add("Paracemetol");
        sampleListSpinner.add("AtriFlam");
        sampleListSpinner.add("Insat");
        sampleListSpinner.add("Meff");
        sampleListSpinner.add("Sucraz");
        sampleListSpinner.add("Stanvit");
        sampleListSpinner.add("Calch 500");
        sampleListSpinner.add("Arizon 700");
        sampleListSpinner.add("Terracite");
        sampleListSpinner.add("Select");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.textview_bg_spinner, sampleListSpinner);
        dataAdapter.setDropDownViewResource(R.layout.textview_bg_spinner);
        holder.spin_sample.setAdapter(dataAdapter);

        //  Log.v("yyy", addInputAdditionalCallArrayList.get(position).getInput_name() + "---" + addInputAdditionalCallArrayList.get(position).getStock() + "---" + addInputAdditionalCallArrayList.get(position).getInp_qty());
        //  setSpinText(holder.spin_sample, addSampleAdditionalCallArrayList.get(position).getPrd_name());
        commonUtilsMethods.setSpinText(holder.spin_sample, addSampleAdditionalCallArrayList.get(position).getPrd_name());
        holder.txt_sam_stock.setText(addSampleAdditionalCallArrayList.get(position).getPrd_stock());
        holder.edt_sam_qty.setText(addSampleAdditionalCallArrayList.get(position).getSample_qty());

        holder.spin_sample.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addSampleAdditionalCallArrayList.set(position, new AddSampleAdditionalCall(addSampleAdditionalCallArrayList.get(position).getCust_name(), addSampleAdditionalCallArrayList.get(position).getCust_code(), holder.spin_sample.getSelectedItem().toString(), addSampleAdditionalCallArrayList.get(position).getPrd_stock(),addSampleAdditionalCallArrayList.get(position).getSample_qty()));
                switch (holder.spin_sample.getSelectedItem().toString()) {
                    case "Select":
                        holder.txt_sam_stock.setText("");
                        break;
                    case "Paracemetol":
                        holder.txt_sam_stock.setText("10");
                        break;
                    case "Sucraz":
                        holder.txt_sam_stock.setText("50");
                        break;
                    case "Stanvit":
                        holder.txt_sam_stock.setText("110");
                        break;
                    case "Meff":
                        holder.txt_sam_stock.setText("90");
                        break;
                    default:
                        holder.txt_sam_stock.setText("0");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.edt_sam_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addSampleAdditionalCallArrayList.set(position, new AddSampleAdditionalCall(addSampleAdditionalCallArrayList.get(position).getCust_name(), addSampleAdditionalCallArrayList.get(position).getCust_code(), addSampleAdditionalCallArrayList.get(position).getPrd_name(), addSampleAdditionalCallArrayList.get(position).getPrd_stock(), editable.toString()));
            }
        });


        holder.img_del_sample.setOnClickListener(view -> {
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return addSampleAdditionalCallArrayList.size();
    }

    public void removeAt(int position) {
        addSampleAdditionalCallArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, addSampleAdditionalCallArrayList.size());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        Spinner spin_sample;
        TextView txt_sam_stock;
        ImageView img_del_sample;
        EditText edt_sam_qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            spin_sample = itemView.findViewById(R.id.spin_sample_name);
            txt_sam_stock = itemView.findViewById(R.id.tv_sample_stock);
            img_del_sample = itemView.findViewById(R.id.img_del_sample);
            edt_sam_qty = itemView.findViewById(R.id.ed_sample_qty);
        }
    }
}
