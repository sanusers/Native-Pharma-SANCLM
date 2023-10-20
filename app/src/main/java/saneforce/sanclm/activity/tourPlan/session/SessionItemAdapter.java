package saneforce.sanclm.activity.tourPlan.session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import saneforce.sanclm.R;

public class SessionItemAdapter extends RecyclerView.Adapter<SessionItemAdapter.MyViewHolder> implements Filterable {

    private JSONArray jsonArray = new JSONArray();
    private JSONArray forFilter ;
    private JSONArray supportArray = new JSONArray();
    JSONObject jsonObject = null;
    private boolean checkBoxVisibility = false;

    private ValueFilter valueFilter;
    SessionItemInterface sessionItemInterface;

    public SessionItemAdapter (){

    }

    public SessionItemAdapter (JSONArray arrayList, boolean checkBoxVisibility, SessionItemInterface sessionItemInterface) {
        this.jsonArray = arrayList;
        this.forFilter = arrayList;
        this.checkBoxVisibility = checkBoxVisibility;
        this.sessionItemInterface = sessionItemInterface;
    }

    @NonNull
    @Override
    public SessionItemAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_listview_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull SessionItemAdapter.MyViewHolder holder, int position) {
        String text = "";
        try {
            jsonObject = jsonArray.getJSONObject(holder.getAbsoluteAdapterPosition());
            text = jsonObject.getString("Name");
            holder.checkBox.setChecked(jsonObject.getBoolean("isChecked"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        if (!checkBoxVisibility){
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.textView.setText(text);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                try{
                    if (!holder.checkBox.isChecked()){
                        holder.checkBox.setChecked(true);
                        jsonArray.getJSONObject(holder.getAbsoluteAdapterPosition()).put("isChecked",true);
                    }else{
                        holder.checkBox.setChecked(false);
                        jsonArray.getJSONObject(holder.getAbsoluteAdapterPosition()).put("isChecked",false);
                    }
                    sessionItemInterface.itemClicked(jsonArray, jsonArray.getJSONObject(holder.getAbsoluteAdapterPosition()));
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    public int getItemCount () {
        return jsonArray.length();
    }

    @Override
    public Filter getFilter () {
        if(valueFilter==null) {
            valueFilter=new ValueFilter();
        }
        return valueFilter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView textView;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.tp_item_checkbox);
            textView = itemView.findViewById(R.id.tp_item_text);

        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results=new FilterResults();
            try {
                JSONArray filteredArray=new JSONArray();
                if(charSequence!=null && charSequence.length() > 0){
                    supportArray = new JSONArray();
                    for(int i = 0; i< jsonArray.length(); i++){
                        if((jsonArray.getJSONObject(i).getString("Name").toUpperCase()).contains(charSequence.toString().toUpperCase())) {
                            filteredArray.put(jsonArray.getJSONObject(i));
                            supportArray.put(jsonArray.getJSONObject(i));
                        }
                    }
                    results.count=filteredArray.length();
                    results.values=filteredArray;
                }else{
                    for (int i=0;i<supportArray.length();i++){
                        if (supportArray.getJSONObject(i).getBoolean("isChecked")){
                            for (int j=0;j<forFilter.length();j++){
                                if (forFilter.getJSONObject(j).getString("Code").equalsIgnoreCase(supportArray.getJSONObject(i).getString("Code"))){
                                    forFilter.getJSONObject(j).put("isChecked",supportArray.getJSONObject(i).getBoolean("isChecked"));
                                }
                            }
                        }
                    }
                    results.count=forFilter.length();
                    results.values=forFilter;
                }
            } catch (JSONException e){
                throw new RuntimeException(e);
            }

            return results;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            jsonArray = (JSONArray) results.values;
            notifyDataSetChanged();
        }
    }


}
