package saneforce.sanzen.activity.activityModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;

public class ActvityListAdapter extends RecyclerView.Adapter<ActvityListAdapter.Viewholder> {

    private Context context;
    private List<JSONObject> itemList;
    private List<JSONObject> filteredList;
    private LayoutInflater inflater;

    ChooseHeadQuaters chooseHeadQuaters;


    public ActvityListAdapter(Context context, List<JSONObject> itemList,ChooseHeadQuaters ChooseHeadQuaters) {
        this.context = context;
        this.itemList = itemList;
        this.filteredList = itemList;
        this.chooseHeadQuaters = ChooseHeadQuaters;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ActvityListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_text, parent, false);
        return new ActvityListAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActvityListAdapter.Viewholder holder, int position) {

        try {
            holder.txtHq.setText(filteredList.get(position).getString("name"));

            holder.txtHq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        chooseHeadQuaters.Choose(filteredList.get(position).getString("name"),filteredList.get(position).getString("Code"));
                    }catch (Exception ignore){}

                }
            });
        }catch (Exception ignore){
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView txtHq;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            txtHq=itemView.findViewById(R.id.itemTitle);
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                List<JSONObject> filtered = new ArrayList<>();
                for (JSONObject data : itemList) {
                    try {
                        if (data.getString("name").toLowerCase().contains(searchString)) {
                                filtered.add(data);
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

}
