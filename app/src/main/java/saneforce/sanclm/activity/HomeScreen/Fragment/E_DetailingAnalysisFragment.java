package saneforce.sanclm.activity.HomeScreen.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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


    RecyclerView recyclerView;
    DCR_CallCountAdapter dcrCallCountAdapter1;
    RelativeLayout relativeLayout;


    ArrayList<String> list = new ArrayList<String>() {{
        add("Slide Analysis");
        add("Brand Analysis");
        add("Specialty Analysis");

    }};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.e_detailing_analysis, container, false);


        recyclerView = v.findViewById(R.id.recyelerview1);
        relativeLayout = v.findViewById(R.id.call_analysis_layout);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = ((displayMetrics.widthPixels/3)*2)/3-22;

        dcrCallCountAdapter1 = new DCR_CallCountAdapter(list, width, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dcrCallCountAdapter1);



        return v;
    }
}
