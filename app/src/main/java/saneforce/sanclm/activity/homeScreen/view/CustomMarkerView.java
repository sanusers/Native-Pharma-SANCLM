package saneforce.sanclm.activity.homeScreen.view;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import saneforce.sanclm.R;
import saneforce.sanclm.storage.SQLite;

public class CustomMarkerView extends MarkerView implements CustomMarkerView1 {

    private TextView Total_Call_count,Call_count;
    private RelativeLayout layout ;

    SQLite sqLite;
    String cus;
    Context context;

    public CustomMarkerView(Context context, int layoutResource ,String custype) {
        super(context, layoutResource);

        this.context=context;
        this.cus=custype;
        Total_Call_count = (TextView) findViewById(R.id.text_total_call_count);
        Call_count = (TextView) findViewById(R.id.text_call_count);
        layout=findViewById(R.id.rl_popllayout);
        sqLite=new SQLite(context);

    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        Call_count.setText(String.valueOf(highlight.getY()).replace("."," "));
        Total_Call_count.setText(String.valueOf(sqLite.gettotalcallscount(cus)));

        if(cus.equalsIgnoreCase("1")){
            layout.setBackgroundColor(getResources().getColor(R.color.Green_45));
        }
        else if(cus.equalsIgnoreCase("2")){
            layout.setBackgroundColor(getResources().getColor(R.color.blue_45));
        }
        else if(cus.equalsIgnoreCase("3")){
            layout.setBackgroundColor(getResources().getColor(R.color.pink_45));
        }
        else if(cus.equalsIgnoreCase("4")){
            layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else if(cus.equalsIgnoreCase("5")){}
    }

    @Override
    public int getXOffset(float xpos) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }
}