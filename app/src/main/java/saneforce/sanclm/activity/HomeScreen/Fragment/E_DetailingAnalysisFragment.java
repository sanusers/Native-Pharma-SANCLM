package saneforce.sanclm.activity.HomeScreen.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.HomeScreen.Adapters.DCR_CallCountAdapter;

public class E_DetailingAnalysisFragment extends Fragment {


    LinearLayout doc_layout, che_layout, stokiest_layout, unlistered_layout;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.e_detailing_analysis, container, false);



        doc_layout = v.findViewById(R.id.ll_doc_child);
        che_layout = v.findViewById(R.id.ll_che_child);
        stokiest_layout = v.findViewById(R.id.ll_stock_child);
        unlistered_layout = v.findViewById(R.id.ll_unli_child);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);



        int width = (int) ((((displayMetrics.widthPixels / 3) * 1.9) / 3)-10);
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        param1.setMargins(0, 5, 0, 10);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        param.setMargins(10, 5, 0, 10);
        doc_layout.setLayoutParams(param1);
        che_layout.setLayoutParams(param);
        stokiest_layout.setLayoutParams(param);
        unlistered_layout.setLayoutParams(param);





        return v;
    }
}
