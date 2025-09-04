package saneforce.sanzen.activity.reports;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import saneforce.sanzen.activity.reports.dayReport.adapter.DynamicSubMenuAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class VisitMonitorAdapter extends RecyclerView.Adapter<DynamicSubMenuAdapter.ViewHolder>{

    Context context;
    CommonUtilsMethods commonUtilsMethods;

    @NonNull
    @Override
    public DynamicSubMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicSubMenuAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
