package saneforce.sanclm.activity.approvals.dcr.dcrdetaillist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.adapter.AdapterCustSingleList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.activity.approvals.tp.TpModelList;
import saneforce.sanclm.databinding.ActivityDcrDetailViewBinding;

public class DcrDetailViewActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public static ArrayList<DcrDetailModelList> dcrDetailModelLists;
    public static String SelectedCode;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrDetailViewBinding dcrDetailViewBinding;
    AdapterCustSingleList adapterCustSingleList;
    String hq_name, cust_pob, cust_jw, cust_type, cust_cluster, cust_remark, cust_feedback, cust_modTime, cust_visitTime;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrDetailViewBinding = ActivityDcrDetailViewBinding.inflate(getLayoutInflater());
        setContentView(dcrDetailViewBinding.getRoot());

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            hq_name = extra.getString("hq_name");
            cust_cluster = extra.getString("cust_cluster");
            cust_pob = extra.getString("cust_pob");
            cust_jw = extra.getString("cust_jw");
            cust_type = extra.getString("cust_type");
            cust_remark = extra.getString("cust_fb");
            cust_feedback = extra.getString("cust_remark");
            cust_modTime = extra.getString("cust_mod_time");
            cust_visitTime = extra.getString("cust_visit_time");
        }

        dcrDetailViewBinding.tagHeader.setText(hq_name);
        if (!cust_cluster.isEmpty()) dcrDetailViewBinding.tvClusterTop.setText(cust_cluster);
        if (!cust_pob.isEmpty()) dcrDetailViewBinding.tvPob.setText(cust_pob);
        if (!cust_jw.isEmpty()) dcrDetailViewBinding.tvJw.setText(cust_jw);
        if (!cust_remark.isEmpty()) dcrDetailViewBinding.tvOverallRemarks.setText(cust_remark);
        if (!cust_feedback.isEmpty()) dcrDetailViewBinding.tvOverallFeedback.setText(cust_feedback);
        if (!cust_visitTime.isEmpty()) dcrDetailViewBinding.tvVisitTime.setText(cust_visitTime);
        if (!cust_modTime.isEmpty()) dcrDetailViewBinding.tvModifiedTime.setText(cust_modTime);

        switch (cust_type) {
            case "1":
                dcrDetailViewBinding.imgCust.setImageDrawable(getResources().getDrawable(R.drawable.map_dr_img));
                break;
            case "2":
                dcrDetailViewBinding.imgCust.setImageDrawable(getResources().getDrawable(R.drawable.map_chemist_img));
                break;
            case "3":
                dcrDetailViewBinding.imgCust.setImageDrawable(getResources().getDrawable(R.drawable.map_stockist_img));
                break;
            case "4":
                dcrDetailViewBinding.imgCust.setImageDrawable(getResources().getDrawable(R.drawable.map_unlistdr_img));
                break;
        }

        SetupCustListAdapter();

        dcrDetailViewBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();   /*startActivity(new Intent(DcrDetailViewActivity.this, DcrCallApprovalActivity.class));*/
            }
        });

        dcrDetailViewBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();   /*startActivity(new Intent(DcrDetailViewActivity.this, DcrCallApprovalActivity.class));*/
            }
        });

        dcrDetailViewBinding.searchDcr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void SetupCustListAdapter() {
        adapterCustSingleList = new AdapterCustSingleList(DcrDetailViewActivity.this, dcrDetailModelLists, DcrDetailViewActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        dcrDetailViewBinding.rvDcrListNames.setLayoutManager(mLayoutManager);
        dcrDetailViewBinding.rvDcrListNames.setAdapter(adapterCustSingleList);
    }

    private void filter(String text) {
        ArrayList<DcrDetailModelList> filterdNames = new ArrayList<>();
        for (DcrDetailModelList s : dcrDetailModelLists) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        adapterCustSingleList.filterList(filterdNames);
    }

    @Override
    public void onClick(DCRApprovalList dcrApprovalList, int pos) {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClickDcrDetail(DcrDetailModelList dcrDetailModelList) {
        dcrDetailViewBinding.tvName.setText(dcrDetailModelList.getName());
        SelectedCode = dcrDetailModelList.getCode();

        switch (dcrDetailModelList.getType()) {
            case "1":
                dcrDetailViewBinding.imgCust.setImageDrawable(getResources().getDrawable(R.drawable.map_dr_img));
                break;
            case "2":
                dcrDetailViewBinding.imgCust.setImageDrawable(getResources().getDrawable(R.drawable.map_chemist_img));
                break;
            case "3":
                dcrDetailViewBinding.imgCust.setImageDrawable(getResources().getDrawable(R.drawable.map_stockist_img));
                break;
            case "4":
                dcrDetailViewBinding.imgCust.setImageDrawable(getResources().getDrawable(R.drawable.map_unlistdr_img));
                break;
        }

        if (!dcrDetailModelList.getSdp_name().isEmpty()) {
            dcrDetailViewBinding.tvClusterTop.setText(dcrDetailModelList.getSdp_name());
        } else {
            dcrDetailViewBinding.tvClusterTop.setText(getResources().getText(R.string.no_cluster));
        }
        if (!dcrDetailModelList.getPob().isEmpty()) {
            dcrDetailViewBinding.tvPob.setText(dcrDetailModelList.getPob());
        } else {
            dcrDetailViewBinding.tvPob.setText("0");
        }
        if (!dcrDetailModelList.getJointWork().isEmpty()) {
            dcrDetailViewBinding.tvJw.setText(dcrDetailModelList.getJointWork());
        } else {
            dcrDetailViewBinding.tvJw.setText(getResources().getText(R.string.no_jw));
        }
        if (!dcrDetailModelList.getRemark().isEmpty()) {
            dcrDetailViewBinding.tvOverallRemarks.setText(dcrDetailModelList.getRemark());
        } else {
            dcrDetailViewBinding.tvOverallRemarks.setText(getResources().getText(R.string.no_remarks));
        }
        if (!dcrDetailModelList.getCall_feedback().isEmpty()) {
            dcrDetailViewBinding.tvOverallFeedback.setText(dcrDetailModelList.getCall_feedback());
        } else {
            dcrDetailViewBinding.tvOverallFeedback.setText(getResources().getText(R.string.no_feedback));
        }
        dcrDetailViewBinding.tvModifiedTime.setText(dcrDetailModelList.getModTime());
        dcrDetailViewBinding.tvVisitTime.setText(dcrDetailModelList.getVisitTime());
    }

    @Override
    public void onItemClick(TpModelList tpModelLists) {

    }
}