package saneforce.sanzen.activity.call.dcrCallSelection.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.FillteredInterfacce;

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
//        EditText editText = itemView.findViewById(R.id.search_cust);

        LinearLayout linearLayout =itemView.findViewById(R.id.ListLayout);
        TextView textView = itemView.findViewById(R.id.itemTitle);
        if(dataList.size() > i) {
            textView.setText(dataList.get(i).getName());
        }

        linearLayout.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (dataList.size() > i) {
                    interfacce.ChooseValues(dataList.get(i));
                }
            }
        });
/*        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });*/

        return itemView;

    }
    private void filter(String text) {
        dataList = new ArrayList<>();
        for (DCRFillteredModelClass s : dataList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getName().toLowerCase().contains(text.toLowerCase()) || s.getCode().toLowerCase().contains(text.toLowerCase())) {
                dataList.add(s);
            }
        }

    }
}
