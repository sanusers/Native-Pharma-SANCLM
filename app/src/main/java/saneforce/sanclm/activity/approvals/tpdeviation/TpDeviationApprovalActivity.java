package saneforce.sanclm.activity.approvals.tpdeviation;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.databinding.ActivityTpDeviationApprovalBinding;

public class TpDeviationApprovalActivity extends AppCompatActivity {
    ActivityTpDeviationApprovalBinding tpDeviationApprovalBinding;
    ArrayList<TpDeviationModelList> tpDeviationModelLists = new ArrayList<>();
    TpDeviationAdapter tpDeviationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tpDeviationApprovalBinding = ActivityTpDeviationApprovalBinding.inflate(getLayoutInflater());
        setContentView(tpDeviationApprovalBinding.getRoot());
        SetupAdapter();

        tpDeviationApprovalBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(TpDeviationApprovalActivity.this, ApprovalsActivity.class)));
    }

    private void SetupAdapter() {
        tpDeviationModelLists.add(new TpDeviationModelList("Field Work", "Andheri,Madurai,Theni", "Aravindh", "Field Work Planned in Theni", "Planned Changed due to rain"));
        tpDeviationModelLists.add(new TpDeviationModelList("Field Work", "Trichy,Chennai", "Samual", "Field Work Planned in Chennai", "Planned Changed due to Meeting"));

        tpDeviationAdapter = new TpDeviationAdapter(TpDeviationApprovalActivity.this, tpDeviationModelLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        tpDeviationApprovalBinding.rvTpDeviation.setLayoutManager(mLayoutManager);
        tpDeviationApprovalBinding.rvTpDeviation.setAdapter(tpDeviationAdapter);
    }
}