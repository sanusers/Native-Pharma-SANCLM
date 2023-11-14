package saneforce.sanclm.activity.homeScreen.view;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import saneforce.sanclm.R;
import saneforce.sanclm.storage.SQLite;

public class CustomMarkerView extends MarkerView {

    private TextView Total_Call_count,Avg_calls;
    private ImageView imageView;
    private RelativeLayout layout ;
    SQLite sqLite;
    String cus,firstDateStr,fifteenthDateStr,enddate,firstDatepastmonth,fifteenthDatepastmonth,enddatepastmonth,firstDatecurrent,fifteenthDatecurrent,enddatecurrent,linekey;
    Context context;


    public CustomMarkerView(Context context, int layoutResource ,String custype,String firstDateStr, String fifteenthDateStr, String enddate,
                            String firstDatepastmonth, String fifteenthDatepastmonth, String enddatepastmonth,
                            String firstDatecurrent, String fifteenthDatecurrent, String enddatecurrent ,String linekey) {

        super(context, layoutResource);
        this.firstDateStr = firstDateStr;
        this.fifteenthDateStr = fifteenthDateStr;
        this.enddate = enddate;
        this.firstDatepastmonth = firstDatepastmonth;
        this.fifteenthDatepastmonth = fifteenthDatepastmonth;
        this.enddatepastmonth = enddatepastmonth;
        this.firstDatecurrent = firstDatecurrent;
        this.fifteenthDatecurrent = fifteenthDatecurrent;
        this.enddatecurrent = enddatecurrent;
        this.linekey=linekey;
        this.linekey = linekey;

        this.context = context;
        this.cus = custype;
        imageView = findViewById(R.id.image_id);
        Total_Call_count = (TextView) findViewById(R.id.text_total_call_count);
        Avg_calls = (TextView) findViewById(R.id.text_call_count);
        layout = findViewById(R.id.rl_popllayout);
        sqLite = new SQLite(context);


    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        int getfeildworkcount = 0;

        if (linekey.equalsIgnoreCase("3")) {

            if (highlight.getX() == 1.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDateStr, fifteenthDateStr);

            } else if (highlight.getX() == 2.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDateStr, enddate);

            } else if (highlight.getX() == 3.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDatepastmonth, fifteenthDatepastmonth);

            } else if (highlight.getX() == 4.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDatepastmonth, enddatepastmonth);
            } else if (highlight.getX() == 5.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDatecurrent, fifteenthDatecurrent);

            } else if (highlight.getX() == 6.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDatecurrent, enddatecurrent);

            }
        }
        else if (linekey.equalsIgnoreCase("2")) {

            if (highlight.getX() == 1.0) {

                getfeildworkcount = sqLite.getfeildworkcount(firstDatepastmonth, fifteenthDatepastmonth);

            } else if (highlight.getX() == 2.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDatepastmonth, fifteenthDatepastmonth);


            } else if (highlight.getX() == 3.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDatecurrent, fifteenthDatecurrent);


            } else if (highlight.getX() == 4.0) {

                getfeildworkcount = sqLite.getfeildworkcount(firstDatecurrent, enddatecurrent);

            }

        } else {
            if (highlight.getX() == 1.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDatecurrent, fifteenthDatecurrent);
            } else if (highlight.getX() == 2.0) {
                getfeildworkcount = sqLite.getfeildworkcount(firstDatecurrent, enddatecurrent);
            }
        }



        int getyvakue = (int) highlight.getY();
        int avaragecalls = Math.round(getyvakue / getfeildworkcount);
        Log.e("getcallcount",""+getfeildworkcount+ "     " +getyvakue+" "+avaragecalls);
        Avg_calls.setText(String.valueOf(avaragecalls));
        Total_Call_count.setText(String.valueOf(getyvakue));

        if (cus.equalsIgnoreCase("1")) {
            layout.setBackgroundResource(R.drawable.markview_backgrond_doctor);
            imageView.setImageResource(R.drawable.down_half_triangle_doctor);
        } else if (cus.equalsIgnoreCase("2")) {
            layout.setBackgroundResource(R.drawable.markview_backgrond_chemist);
            imageView.setImageResource(R.drawable.down_half_triangle_chemist);
        } else if (cus.equalsIgnoreCase("3")) {
            layout.setBackgroundResource(R.drawable.markview_backgrond_stockiest);
            imageView.setImageResource(R.drawable.down_half_triangle_stockiest);
        } else if (cus.equalsIgnoreCase("4")) {
            imageView.setImageResource(R.drawable.down_half_triangle_unlistered);
            layout.setBackgroundResource(R.drawable.markview_backgrond_unlisted);
        } else if (cus.equalsIgnoreCase("5")) {
            imageView.setImageResource(R.drawable.down_half_triangle_cip);
            layout.setBackgroundResource(R.drawable.markview_backgrond_cip);
        } else if (cus.equalsIgnoreCase("6")) {
            imageView.setImageResource(R.drawable.down_half_triangle_hospital);
            layout.setBackgroundResource(R.drawable.markview_backgrond_hospital);
        }
    }

    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }



}