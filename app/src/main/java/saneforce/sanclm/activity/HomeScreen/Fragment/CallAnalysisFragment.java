package saneforce.sanclm.activity.HomeScreen.Fragment;



import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.HomeScreen.Adapters.DCR_CallCountAdapter;

public class CallAnalysisFragment extends Fragment {

RecyclerView recyclerView;
DCR_CallCountAdapter dcrCallCountAdapter;
RelativeLayout relativeLayout;




    ArrayList<String> list = new ArrayList<String>() {{
        add("Doctor Calls");
        add("Chemist Calls");
        add("Stockiest Calls");
        add("Unlisted Calls");

    }};    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.call_analysis_fagment, container, false);

        recyclerView = v.findViewById(R.id.recyelerview1);
        relativeLayout=v.findViewById(R.id.call_analysis_layout);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int width = ((displayMetrics.widthPixels/3)*2)/3-22;


        dcrCallCountAdapter = new DCR_CallCountAdapter(list,width,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dcrCallCountAdapter);


        return v;
    }
}
