package saneforce.sanzen.activity.homeScreen.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;

public class AdapterPopupSpinnerSelection extends BaseAdapter
{
    Context context;
    ArrayList<DCRFillteredModelClass> list=new ArrayList<>();
    ArrayList<DCRFillteredModelClass> mFilterresult=new ArrayList<>();
    boolean single=false;
    String a="";
    int cc=0;

    public AdapterPopupSpinnerSelection(Context context, ArrayList<DCRFillteredModelClass> list)
    {
        this.context = context;
        this.list = list;
        this.mFilterresult=list;
        single=false;
    }
    public AdapterPopupSpinnerSelection(Context context, ArrayList<DCRFillteredModelClass> list,String a)
    {
        this.context = context;
        this.list = list;
        this.mFilterresult=list;
        single=false;
        this.a=a;
    }
    @SuppressLint("SuspiciousIndentation")
    @Override
    public int getCount()
    {
        return list.size();

    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public Object getItem(int i)
    {
        return list.get(i);

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.row_item_textview, viewGroup, false);
        TextView textView = (TextView) view.findViewById(R.id.txt_name);
        try {
            textView.setText(list.get(i).getName());

        } catch (Exception e) {
        }
        return view;
    }
}
