package saneforce.sanzen.activity.homeScreen.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import saneforce.sanzen.R;
import saneforce.sanzen.roomdatabase.CallTableDetails.CallTableDao;

public class CustomMarkerView extends MarkerView {

    private TextView Total_Call_count, Avg_calls;
    private ImageView imageView;
    private RelativeLayout layout;

    String cus, linekey;
    Context context;

    CallTableDao callTableDao;

    public CustomMarkerView(Context context, int layoutResource, String custype,CallTableDao callTableDao,  String linekey) {


        super(context, layoutResource);
        this.linekey = linekey;
        this.callTableDao=callTableDao;
        this.context = context;
        this.cus = custype;
        imageView = findViewById(R.id.image_id);
        Total_Call_count = (TextView) findViewById(R.id.text_total_call_count);
        Avg_calls = (TextView) findViewById(R.id.text_call_count);
        layout = findViewById(R.id.rl_popllayout);

    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        int getfeildworkcount = 0;

        if (linekey.equalsIgnoreCase("3")) {

            if (highlight.getX() == 1.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"C1");

            } else if (highlight.getX() == 2.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"C2");

            } else if (highlight.getX() == 3.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"B1");

            } else if (highlight.getX() == 4.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"B2");
            } else if (highlight.getX() == 5.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"A1");

            } else if (highlight.getX() == 6.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"A2");

            }
        } else if (linekey.equalsIgnoreCase("2")) {

            if (highlight.getX() == 1.0) {

                getfeildworkcount = callTableDao.getFieldworkCount(cus,"B1");

            } else if (highlight.getX() == 2.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"B2");


            } else if (highlight.getX() == 3.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"A1");


            } else if (highlight.getX() == 4.0) {

                getfeildworkcount = callTableDao.getFieldworkCount(cus,"A2");

            }

        } else {

            if (highlight.getX() == 1.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"A1");


            } else if (highlight.getX() == 2.0) {
                getfeildworkcount = callTableDao.getFieldworkCount(cus,"A2");

            }
        }

        double avaragecalls;
        int getyvakue = (int) highlight.getY();
        if (getfeildworkcount != 0.0) {
            avaragecalls = Double.valueOf(getyvakue) / Double.valueOf(getfeildworkcount);
        } else {
            avaragecalls = 0.0;
        }
        String formattedAverage = String.format("%.1f", avaragecalls);
        if (formattedAverage.endsWith(".0")) {
            Avg_calls.setText(formattedAverage.substring(0,formattedAverage.length()-2));
        } else {
            Avg_calls.setText(formattedAverage);
        }
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