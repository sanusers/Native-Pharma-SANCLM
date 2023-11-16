package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.FilterDataList;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.ListedDoctorFragment;

public class PopupFilterAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> filterList;
    int selectedPos;

    public PopupFilterAdapter(Context context, ArrayList<String> filterList, int adapterPosition) {
        this.context = context;
        this.filterList = filterList;
        selectedPos = adapterPosition;
    }

    @Override
    public int getCount() {
        return filterList.size();
    }

    @Override
    public Object getItem(int i) {
        return filterList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.row_item_popup_filter, viewGroup, false);

        TextView txt_content = view.findViewById(R.id.txt_content);
        txt_content.setText(filterList.get(i));

        txt_content.setOnClickListener(view1 -> {
            AdapterFilterSelection.dialog.dismiss();
            ListedDoctorFragment.ArrayFilteredList.set(selectedPos, new FilterDataList(filterList.get(i), selectedPos));
            ListedDoctorFragment.adapterFilterSelection.notifyDataSetChanged();
            // AdapterFilterSelection.ViewHolder.tv_conditions.setText(filterList.get(i));
        });

        return view;
    }
}