package saneforce.sanzen.activity.homeScreen.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;


public class SalesAnalysisFragment extends Fragment  {

    TextView txt_month,txt_quaterly, txt_yearly;
    CommonUtilsMethods commonUtilsMethods;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sales_analysis, container, false);
        Log.v("fragment", "SalesAnalysis");
        txt_month=view.findViewById(R.id.text_month);
        txt_yearly=view.findViewById(R.id.text_yearly);
        txt_quaterly=view.findViewById(R.id.text_Quaterly);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        txt_yearly.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {

                txt_month.setTextColor(Color.parseColor("#4e4e5c"));
                txt_yearly.setTextColor(Color.WHITE);
                txt_quaterly.setTextColor(Color.parseColor("#4e4e5c"));


                txt_month.setBackgroundResource(R.drawable.cutom_backround_grey);
                txt_yearly.setBackgroundResource(R.drawable.custom_background_black);
                txt_quaterly.setBackgroundResource(R.drawable.cutom_backround_grey);
            }
        });
        txt_month.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                txt_month.setTextColor(Color.WHITE);
                txt_yearly.setTextColor(Color.parseColor("#4e4e5c"));
                txt_quaterly.setTextColor(Color.parseColor("#4e4e5c"));

                txt_month.setBackgroundResource(R.drawable.custom_background_black);
                txt_yearly.setBackgroundResource(R.drawable.cutom_backround_grey);
                txt_quaterly.setBackgroundResource(R.drawable.cutom_backround_grey);
            }
        });
        txt_quaterly.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                txt_month.setTextColor(Color.parseColor("#4e4e5c"));
                txt_yearly.setTextColor(Color.parseColor("#4e4e5c"));
                txt_quaterly.setTextColor(Color.WHITE);

                txt_quaterly.setBackgroundResource(R.drawable.custom_background_black);
                txt_yearly.setBackgroundResource(R.drawable.cutom_backround_grey);
                txt_month.setBackgroundResource(R.drawable.cutom_backround_grey);
            }
        });
        return view;
    }


    public void onDestroyView() {

        super.onDestroyView();
    }


}
