package saneforce.sanclm.activity.homeScreen.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.modelClass.GroupModelClass;

public class OutBoxAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inflater;
    Context contxt;

    List<GroupModelClass>  list=new ArrayList<>();

    public OutBoxAdapter(Context context,List<GroupModelClass>  list) {

        this.list=list;
        this.inflater = LayoutInflater.from(context);
        this.contxt = context;
    }
    @Override
    public int getGroupCount() {
        return 3;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 3;
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
        return 3;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 3;
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

        ImageView isexpandStatus=view.findViewById(R.id.txt_expand_status);

        if(isExpanded){
            isexpandStatus.setImageResource(R.drawable.top_vector);
        }else {
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


        TextView  DocName = view.findViewById(R.id.textViewLabel1);
        TextView  datetime= view.findViewById(R.id.textViewLabel2);
        CircleImageView imageView= view.findViewById(R.id.profile_icon);


        DocName.setText(list.get(groupPosition).getChildItems().get(childPosition).getDocName());
        datetime.setText(list.get(groupPosition).getChildItems().get(childPosition).getCallsDateTime());
        String value=list.get(groupPosition).getChildItems().get(childPosition).getDocNameID();
        if (value.equalsIgnoreCase("D")) {

            imageView.setImageResource(R.drawable.doctor_img);
        } else if (value.equalsIgnoreCase("C")) {

            imageView.setImageResource(R.drawable.chemist_img);

        } else if (value.equalsIgnoreCase("cip")) {
            imageView.setImageResource(R.drawable.cip_img);
        }


        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}