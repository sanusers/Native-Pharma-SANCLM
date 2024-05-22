package saneforce.sanzen.activity.myresource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import saneforce.sanzen.R;


public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CustomModel> dataList;

    public CustomAdapter(Context context, ArrayList<CustomModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
//            holder.titleTextView = convertView.findViewById(R.id.titleTextView);
            holder.descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CustomModel model = dataList.get(position);

//        holder.titleTextView.setText(model.getTitle());
        holder.descriptionTextView.setText(model.getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
    }
}
