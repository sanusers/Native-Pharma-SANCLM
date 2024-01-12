package saneforce.santrip.activity.tourPlan.session;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.tourPlan.model.EditModelClass;

public class SessionItemAdapter extends RecyclerView.Adapter<SessionItemAdapter.MyViewHolder> implements Filterable {

    ArrayList<EditModelClass> arrayList = new ArrayList<>();
    ArrayList<EditModelClass> arrayForFilter = new ArrayList<>();
    ArrayList<EditModelClass> supportModelArray = new ArrayList<>();
    private boolean checkBoxVisibility = false;
    private ValueFilter valueFilter;
    SessionItemInterface sessionItemInterface;
    public SessionItemAdapter (){

    }

    public SessionItemAdapter(ArrayList<EditModelClass> arrayList, boolean checkBoxVisibility, SessionItemInterface sessionItemInterface) {
        this.arrayList = arrayList;
        this.arrayForFilter = arrayList;
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
        EditModelClass editModelClass = arrayList.get(holder.getAbsoluteAdapterPosition());

        if (!checkBoxVisibility){
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.textView.setText(editModelClass.getName());
        holder.checkBox.setChecked(editModelClass.isChecked());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                if (!holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(true);
                    arrayList.get(holder.getAbsoluteAdapterPosition()).setChecked(true);
                }else{
                    holder.checkBox.setChecked(false);
                    arrayList.get(holder.getAbsoluteAdapterPosition()).setChecked(false);
                }
                sessionItemInterface.itemClicked(arrayList, arrayList.get(holder.getAbsoluteAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount () {
        return arrayList.size();
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

            ArrayList<EditModelClass> filteredModelArray = new ArrayList<>();
            if(charSequence!=null && charSequence.length() > 0){
                supportModelArray = new ArrayList<>();
                for(int i = 0; i< arrayForFilter.size(); i++){
                    if((arrayForFilter.get(i).getName().toUpperCase()).contains(charSequence.toString().toUpperCase())) {
                        filteredModelArray.add(arrayForFilter.get(i));
                        supportModelArray.add(arrayForFilter.get(i));
                    }
                }
                results.count=filteredModelArray.size();
                results.values=filteredModelArray;
            }else{
                for (int i=0;i<supportModelArray.size();i++){
                    if (supportModelArray.get(i).isChecked()){
                        for (int j=0;j<arrayForFilter.size();j++){
                            if (arrayForFilter.get(j).getCode().equalsIgnoreCase(supportModelArray.get(i).getCode())){
                                arrayForFilter.get(j).setChecked(supportModelArray.get(i).isChecked());
                            }
                        }
                    }
                }
                results.count=arrayForFilter.size();
                results.values=arrayForFilter;
            }

            return results;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<EditModelClass>) results.values;
            notifyDataSetChanged();
        }
    }



}
