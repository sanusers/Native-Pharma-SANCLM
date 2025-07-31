package saneforce.sanzen.activity.tourPlan.session;

import android.util.Log;
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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.model.EditModelClass;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.storage.SharedPref;

public class SessionItemAdapter extends RecyclerView.Adapter<SessionItemAdapter.MyViewHolder> implements Filterable {

    ArrayList<EditModelClass> arrayList = new ArrayList<>();
    ArrayList<EditModelClass> arrayForFilter = new ArrayList<>();
    ArrayList<EditModelClass> supportModelArray = new ArrayList<>();
    private boolean checkBoxVisibility = false;
    private ValueFilter valueFilter;
    SessionItemInterface sessionItemInterface;

    private int independentPosition = -1;
    public SessionItemAdapter() {
    }

    public SessionItemAdapter(ArrayList<EditModelClass> arrayList, boolean checkBoxVisibility, SessionItemInterface sessionItemInterface) {
        this.arrayList = arrayList;
        this.arrayForFilter = arrayList;
        this.checkBoxVisibility = checkBoxVisibility;
        this.sessionItemInterface = sessionItemInterface;
    }

    @NonNull
    @Override
    public SessionItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_session_listview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionItemAdapter.MyViewHolder holder, int position) {
        EditModelClass editModelClass = arrayList.get(holder.getAbsoluteAdapterPosition());
        if(editModelClass.getName().equalsIgnoreCase(Constants.INDEPENDENT)) {
            independentPosition = position;
        }
        if(!checkBoxVisibility) {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.textView.setText(editModelClass.getName());
        holder.checkBox.setChecked(editModelClass.isChecked());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("onClickListener--->");
                int position = holder.getAbsoluteAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                EditModelClass clickedItem = arrayList.get(position);

                int independentPos = -1;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getName().equalsIgnoreCase(Constants.INDEPENDENT)) {
                        independentPos = i;
                        break;
                    }
                }

                boolean isNowChecked = !clickedItem.isChecked();
                clickedItem.setChecked(isNowChecked);
                notifyItemChanged(position);

                if (isNowChecked) {
                    if (clickedItem.getName().equalsIgnoreCase(Constants.INDEPENDENT)) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (i != position && arrayList.get(i).isChecked()) {
                                arrayList.get(i).setChecked(false);
                                notifyItemChanged(i); // update only changed rows
                            }
                        }
                    } else if (independentPos != -1 && arrayList.get(independentPos).isChecked()) {
                        arrayList.get(independentPos).setChecked(false);
                        notifyItemChanged(independentPos);
                    }
                }
                System.out.println("onClickListene1r--->"+arrayList.size());

                sessionItemInterface.itemClicked(arrayList, clickedItem);
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
