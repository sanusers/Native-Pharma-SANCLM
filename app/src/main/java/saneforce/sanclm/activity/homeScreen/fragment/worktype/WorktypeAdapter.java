package saneforce.sanclm.activity.homeScreen.fragment.worktype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import saneforce.sanclm.R;

public class WorktypeAdapter extends BaseAdapter {

    private Context context;
    private List<JSONObject> itemList;
    private LayoutInflater inflater;

    private String type;
    public WorktypeAdapter(Context context, List<JSONObject> itemList, String type) {
        this.context = context;
        this.itemList = itemList;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return  itemList.size();
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
        try {
        if(type.equalsIgnoreCase("3")){
            itemTitle.setText(itemList.get(position).getString("name"));
        }else {
            itemTitle.setText(itemList.get(position).getString("Name"));

        } } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
