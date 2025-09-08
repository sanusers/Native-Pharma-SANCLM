package saneforce.sanzen.activity.reports.visitMonitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.ReportPagerAdapter;

public class ReportPagerFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public static ReportPagerFragment newInstance(List<String> monthFilteredData) {
        ReportPagerFragment fragment = new ReportPagerFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("monthData", new ArrayList<>(monthFilteredData));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_pager, container, false);
        viewPager = view.findViewById(R.id.visit_view_pager);


        if (getArguments() != null) {
            List<String> monthData = getArguments().getStringArrayList("monthData");
            ReportPagerAdapter pagerAdapter = new ReportPagerAdapter(this, monthData);
            viewPager.setAdapter(pagerAdapter);

       /*     new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("Doctor");
                        break;
                    case 1:
                        tab.setText("Chemist");
                        break;
                    case 2:
                        tab.setText("Stockist");
                        break;
                    case 3:
                        tab.setText("Unlisted");
                        break;
                }
            }).attach();*/
        }

        return view;
    }
}
