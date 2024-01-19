package saneforce.santrip.activity.homeScreen.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;

public class OutBoxAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inflater;
    Context contxt;

    List<GroupModelClass> list = new ArrayList<>();

    public OutBoxAdapter(Context context, List<GroupModelClass> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.contxt = context;
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getChildItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return list.size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return list.get(groupPosition).getChildItems().size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.outbox_group_view, parent, false);
        }

        TextView textDate = view.findViewById(R.id.text_date);

        textDate.setText(list.get(groupPosition).getGroupName());

        ImageView isexpandStatus = view.findViewById(R.id.txt_expand_status);

        if (isExpanded) {
            isexpandStatus.setImageResource(R.drawable.top_vector);
        } else {
            isexpandStatus.setImageResource(R.drawable.down_arrow);
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.outbox_child_view, parent, false);
        }


        TextView DocName = view.findViewById(R.id.textViewLabel1);
        TextView datetime = view.findViewById(R.id.textViewLabel2);
        CircleImageView imageView = view.findViewById(R.id.profile_icon);

        datetime.setText(list.get(groupPosition).getChildItems().get(childPosition).getCallsDateTime());
        String type = list.get(groupPosition).getChildItems().get(childPosition).getDocNameID();
        if (type.equalsIgnoreCase("1")) {
            DocName.setText(String.format("%s (Doctor) ", list.get(groupPosition).getChildItems().get(childPosition).getDocName()));
            imageView.setImageResource(R.drawable.map_dr_img);
        } else if (type.equalsIgnoreCase("2")) {
            DocName.setText(String.format("%s (Chemist) ", list.get(groupPosition).getChildItems().get(childPosition).getDocName()));
            imageView.setImageResource(R.drawable.map_chemist_img);
        } else if (type.equalsIgnoreCase("3")) {
            DocName.setText(String.format("%s (Stockiest) ", list.get(groupPosition).getChildItems().get(childPosition).getDocName()));
            imageView.setImageResource(R.drawable.map_stockist_img);
        } else if (type.equalsIgnoreCase("4")) {
            DocName.setText(String.format("%s (UnDr) ", list.get(groupPosition).getChildItems().get(childPosition).getDocName()));
            imageView.setImageResource(R.drawable.map_unlistdr_img);
        } else if (type.equalsIgnoreCase("5")) {
            DocName.setText(String.format("%s (CIP) ", list.get(groupPosition).getChildItems().get(childPosition).getDocName()));
            imageView.setImageResource(R.drawable.map_cip_img);
        } else if (type.equalsIgnoreCase("6")) {
            DocName.setText(String.format("%s (HOS) ", list.get(groupPosition).getChildItems().get(childPosition).getDocName()));
            imageView.setImageResource(R.drawable.tp_hospital_icon);
        }


        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}