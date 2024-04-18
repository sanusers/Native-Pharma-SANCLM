package saneforce.santrip.activity.call.dcrCallSelection.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


import saneforce.santrip.R;
import saneforce.santrip.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.santrip.activity.call.dcrCallSelection.FillteredInterfacce;

public class FillteredAdapter extends BaseAdapter {
    Context context;
    ArrayList<DCRFillteredModelClass> dataList= new ArrayList<>();

    FillteredInterfacce interfacce;

    public FillteredAdapter(Context context, ArrayList<DCRFillteredModelClass> dataList,FillteredInterfacce interfacce) {
        this.context = context;
        this.dataList = dataList;
        this.interfacce = interfacce;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.list_view_text, parent, false);

        LinearLayout linearLayout =itemView.findViewById(R.id.ListLayout);
        TextView textView = itemView.findViewById(R.id.itemTitle);
        textView.setText(dataList.get(i).getName());

        linearLayout.setOnClickListener(view -> {
            interfacce.ChooseValues(dataList.get(i));
        });

        return itemView;
    }
}
