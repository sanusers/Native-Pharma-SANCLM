package saneforce.sanclm.Activities.HomeScreen.Fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.sanclm.R;


public class SalesAnalysisFragment extends Fragment {

    TextView txt_month,txt_quaterly, txt_yearly;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sales_analysis, container, false);

        txt_month=v.findViewById(R.id.text_month);
        txt_yearly=v.findViewById(R.id.text_yearly);
        txt_quaterly=v.findViewById(R.id.text_Quaterly);


        txt_yearly.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                txt_month.setTextColor(Color.parseColor("#5b5a67"));
                txt_yearly.setTextColor(Color.WHITE);
                txt_quaterly.setTextColor(Color.parseColor("#5b5a67"));


                txt_month.setBackgroundResource(R.drawable.cutom_backround_grey);
                txt_yearly.setBackgroundResource(R.drawable.custom_background_black);
                txt_quaterly.setBackgroundResource(R.drawable.cutom_backround_grey);
            }
        });
        txt_month.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                txt_month.setTextColor(Color.WHITE);
                txt_yearly.setTextColor(Color.parseColor("#5b5a67"));
                txt_quaterly.setTextColor(Color.parseColor("#5b5a67"));

                txt_month.setBackgroundResource(R.drawable.custom_background_black);
                txt_yearly.setBackgroundResource(R.drawable.cutom_backround_grey);
                txt_quaterly.setBackgroundResource(R.drawable.cutom_backround_grey);
            }
        });
        txt_quaterly.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                txt_month.setTextColor(Color.parseColor("#5b5a67"));
                txt_yearly.setTextColor(Color.parseColor("#5b5a67"));
                txt_quaterly.setTextColor(Color.WHITE);

                txt_quaterly.setBackgroundResource(R.drawable.custom_background_black);
                txt_yearly.setBackgroundResource(R.drawable.cutom_backround_grey);
                txt_month.setBackgroundResource(R.drawable.cutom_backround_grey);
            }
        });




        return v;
    }


}
