package saneforce.sanclm.activity.homeScreen.fragment.worktype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanclm.R;

public class WorkplanListAdapter extends BaseAdapter {

    private Context context;
    private List<JSONObject> itemList;
    private List<JSONObject> filteredList;
    private LayoutInflater inflater;

    private String type;
    public WorkplanListAdapter(Context context, List<JSONObject> itemList, String type) {
        this.context = context;
        this.filteredList = itemList;
        this.itemList = itemList;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return  filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_view_text, parent, false);
        }TextView itemTitle = view.findViewById(R.id.itemTitle);
        CheckBox checkBox = view.findViewById(R.id.ch_cluster);
        try {
        if(type.equalsIgnoreCase("3")){
            itemTitle.setText(filteredList.get(position).getString("name"));
        }else {
            itemTitle.setText(filteredList.get(position).getString("Name"));

        }
            if(type.equalsIgnoreCase("2")){
                checkBox.setVisibility(View.VISIBLE);
            }else {
                checkBox.setVisibility(View.GONE);

            }





        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }




    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                List<JSONObject> filtered = new ArrayList<>();
                for (JSONObject data : itemList) {
                    try {
                        if(type.equalsIgnoreCase("3")){

                            if (data.getString("name").toLowerCase().contains(searchString)) {
                                filtered.add(data);
                            }

                        }else {
                            if (data.getString("Name").toLowerCase().contains(searchString)) {
                                filtered.add(data);
                            }

                        }

                    } catch (JSONException e) {
                      e.printStackTrace();
                    }

                }
                filteredList = filtered;
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<JSONObject>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public List<JSONObject> getlisted(){
        return filteredList;
    }
}
