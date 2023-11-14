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
import saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.adapter.AdapterCustSingleList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.databinding.ActivityDcrDetailViewBinding;

public class DcrDetailViewActivity extends AppCompatActivity {
    public static ArrayList<DcrDetailModelList> dcrDetailModelLists;
    public static String SelectedName;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrDetailViewBinding dcrDetailViewBinding;
    AdapterCustSingleList adapterCustSingleList;
    String hq_name, cust_pob, cust_jw, cust_type, cust_cluster, cust_remark, cust_feedback;

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
        }

        dcrDetailViewBinding.tagHeader.setText(hq_name);
        if (!cust_cluster.isEmpty()) dcrDetailViewBinding.tvClusterTop.setText(cust_cluster);
        if (!cust_pob.isEmpty()) dcrDetailViewBinding.tvPob.setText(cust_pob);
        if (!cust_jw.isEmpty()) dcrDetailViewBinding.tvJw.setText(cust_jw);
        if (!cust_remark.isEmpty()) dcrDetailViewBinding.tvOverallRemarks.setText(cust_remark);
        if (!cust_feedback.isEmpty()) dcrDetailViewBinding.tvOverallFeedback.setText(cust_feedback);

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
        adapterCustSingleList = new AdapterCustSingleList(DcrDetailViewActivity.this, dcrDetailModelLists);
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

}