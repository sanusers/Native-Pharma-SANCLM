package saneforce.santrip.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;

public class E_DetailingAnalysisFragment extends Fragment {


    LinearLayout doc_layout, che_layout, stokiest_layout, unlistered_layout,ll_analyis_layout,ll_grap_layout;

    RelativeLayout e_detailing_layout;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.e_detailing_analysis, container, false);
        Log.v("fragment", "Edetailing");
        doc_layout = v.findViewById(R.id.ll_doc_child);
        che_layout = v.findViewById(R.id.ll_che_child);
        stokiest_layout = v.findViewById(R.id.ll_stock_child);
        unlistered_layout = v.findViewById(R.id.ll_unli_child);
        ll_analyis_layout = v.findViewById(R.id.ll_analyis_layout);
        ll_grap_layout = v.findViewById(R.id.ll_grap_layout);
        e_detailing_layout = v.findViewById(R.id.e_detailing_layout);


        ViewTreeObserver vto = e_detailing_layout.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int getlayout  = e_detailing_layout.getMeasuredWidth();


                int width = (int) (getlayout/ 3-8);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
                param.setMargins(0, 5, 10, 0);
                doc_layout.setLayoutParams(param);
                che_layout.setLayoutParams(param);
                stokiest_layout.setLayoutParams(param);
                unlistered_layout.setLayoutParams(param);


            }
        });


        ll_analyis_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                HomeDashBoard.binding.viewPager1.setScrollEnabled(false);
                return false;
            }
        });


        ll_grap_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HomeDashBoard.binding.viewPager1.setScrollEnabled(true);
                return false;
            }
        });


        return v;
    }
    public void onDestroyView() {

        super.onDestroyView();
    }

}
