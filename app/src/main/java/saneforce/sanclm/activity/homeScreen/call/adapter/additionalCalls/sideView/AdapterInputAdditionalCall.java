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

import saneforce.sanclm.CommonClasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;

public class AdapterInputAdditionalCall extends RecyclerView.Adapter<AdapterInputAdditionalCall.ViewHolder> {
    Context context;
    ArrayList<AddInputAdditionalCall> addInputAdditionalCallArrayList;
    ArrayList<String> inputListSpinner = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;

    public AdapterInputAdditionalCall(Context context, ArrayList<AddInputAdditionalCall> addInputAdditionalCallArrayList) {
        this.context = context;
        this.addInputAdditionalCallArrayList = addInputAdditionalCallArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_input_additional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        inputListSpinner.clear();
        inputListSpinner.add("Pen");
        inputListSpinner.add("Marker");
        inputListSpinner.add("Key Chain");
        inputListSpinner.add("Keyboard");
        inputListSpinner.add("Watch");
        inputListSpinner.add("Horlicks");
        inputListSpinner.add("Umberlla");
        inputListSpinner.add("Lunch Box");
        inputListSpinner.add("Ball");
        inputListSpinner.add("Jacket");
        inputListSpinner.add("Bat");
        inputListSpinner.add("Toys");
        inputListSpinner.add("Plastic Bar");
        inputListSpinner.add("Select");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.textview_bg_spinner, inputListSpinner);
        dataAdapter.setDropDownViewResource(R.layout.textview_bg_spinner);
        holder.spin_input.setAdapter(dataAdapter);

        //  Log.v("yyy", addInputAdditionalCallArrayList.get(position).getInput_name() + "---" + addInputAdditionalCallArrayList.get(position).getStock() + "---" + addInputAdditionalCallArrayList.get(position).getInp_qty());
        commonUtilsMethods.setSpinText(holder.spin_input, addInputAdditionalCallArrayList.get(position).getInput_name());
        holder.txt_stock.setText(addInputAdditionalCallArrayList.get(position).getStock());
        holder.edt_inp_qty.setText(addInputAdditionalCallArrayList.get(position).getInp_qty());

        holder.spin_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addInputAdditionalCallArrayList.set(position, new AddInputAdditionalCall(addInputAdditionalCallArrayList.get(position).getCust_name(), holder.spin_input.getSelectedItem().toString(), holder.txt_stock.getText().toString(), holder.edt_inp_qty.getText().toString()));
                switch (holder.spin_input.getSelectedItem().toString()) {
                    case "Select":
                        holder.txt_stock.setText("");
                        break;
                    case "Pen":
                        holder.txt_stock.setText("10");
                        break;
                    case "Marker":
                        holder.txt_stock.setText("50");
                        break;
                    case "Toys":
                        holder.txt_stock.setText("110");
                        break;
                    case "Ball":
                        holder.txt_stock.setText("90");
                        break;
                    default:
                        holder.txt_stock.setText("0");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.edt_inp_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addInputAdditionalCallArrayList.set(position, new AddInputAdditionalCall(addInputAdditionalCallArrayList.get(position).getCust_name(), addInputAdditionalCallArrayList.get(position).getInput_name(), addInputAdditionalCallArrayList.get(position).getInp_qty(), holder.edt_inp_qty.getText().toString()));
            }
        });

        holder.img_del_input.setOnClickListener(view -> {
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return addInputAdditionalCallArrayList.size();
    }

    public void removeAt(int position) {
        addInputAdditionalCallArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, addInputAdditionalCallArrayList.size());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        Spinner spin_input;
        TextView txt_stock, txt_dummy_spin;
        ImageView img_del_input;
        EditText edt_inp_qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            spin_input = itemView.findViewById(R.id.spin_input_name);
            txt_stock = itemView.findViewById(R.id.tv_stock);
            img_del_input = itemView.findViewById(R.id.img_del_input);
            edt_inp_qty = itemView.findViewById(R.id.ed_input_qty);
            //  txt_dummy_spin = itemView.findViewById(R.id.txt_input_sel_dummy);
        }
    }
}
