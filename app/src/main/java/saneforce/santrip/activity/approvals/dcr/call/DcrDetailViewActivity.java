package saneforce.santrip.activity.approvals.dcr.call;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.OnItemClickListenerApproval;
import saneforce.santrip.activity.approvals.dcr.call.adapter.AdapterCusSingleList;
import saneforce.santrip.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.santrip.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.santrip.activity.approvals.tp.pojo.TpModelList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.ActivityDcrDetailViewBinding;

public class DcrDetailViewActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public static ArrayList<DcrDetailModelList> dcrDetailModelLists;
    public static String SelectedCode;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrDetailViewBinding dcrDetailViewBinding;
    AdapterCusSingleList adapterCusSingleList;
    String hq_name, Cus_pob, Cus_jw, Cus_type, Cus_cluster, Cus_remark, Cus_feedback, Cus_modTime, Cus_visitTime;
    CommonUtilsMethods commonUtilsMethods;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrDetailViewBinding = ActivityDcrDetailViewBinding.inflate(getLayoutInflater());
        setContentView(dcrDetailViewBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            hq_name = extra.getString("hq_name");
            Cus_cluster = extra.getString("cus_cluster");
            Cus_pob = extra.getString("cus_pob");
            Cus_jw = extra.getString("cus_jw");
            Cus_type = extra.getString("cus_type");
            Cus_remark = extra.getString("cus_fb");
            Cus_feedback = extra.getString("cus_remark");
            Cus_modTime = extra.getString("cus_mod_time");
            Cus_visitTime = extra.getString("cus_visit_time");
        }

        dcrDetailViewBinding.tagHeader.setText(hq_name);
        if (!Cus_cluster.isEmpty()) dcrDetailViewBinding.tvClusterTop.setText(Cus_cluster);
        if (!Cus_pob.isEmpty()) dcrDetailViewBinding.tvPob.setText(Cus_pob);
        if (!Cus_jw.isEmpty()) dcrDetailViewBinding.tvJw.setText(Cus_jw);
        if (!Cus_remark.isEmpty()) dcrDetailViewBinding.tvOverallRemarks.setText(Cus_remark);
        if (!Cus_feedback.isEmpty()) dcrDetailViewBinding.tvOverallFeedback.setText(Cus_feedback);
        if (!Cus_visitTime.isEmpty()) dcrDetailViewBinding.tvVisitTime.setText(Cus_visitTime);
        if (!Cus_modTime.isEmpty()) dcrDetailViewBinding.tvModifiedTime.setText(Cus_modTime);

        switch (Cus_type) {
            case "1":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_dr_img));
                break;
            case "2":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_chemist_img));
                break;
            case "3":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_stockist_img));
                break;
            case "4":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_unlistdr_img));
                break;
        }

        SetUpCusListAdapter();

        dcrDetailViewBinding.ivBack.setOnClickListener(view -> {
            finish();   /*startActivity(new Intent(DcrDetailViewActivity.this, DcrCallApprovalActivity.class));*/
        });

        dcrDetailViewBinding.btnBack.setOnClickListener(view -> {
            finish();   /*startActivity(new Intent(DcrDetailViewActivity.this, DcrCallApprovalActivity.class));*/
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

    private void SetUpCusListAdapter() {
        adapterCusSingleList = new AdapterCusSingleList(DcrDetailViewActivity.this, dcrDetailModelLists, DcrDetailViewActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        dcrDetailViewBinding.rvDcrListNames.setLayoutManager(mLayoutManager);
        dcrDetailViewBinding.rvDcrListNames.setAdapter(adapterCusSingleList);
    }

    private void filter(String text) {
        ArrayList<DcrDetailModelList> filteredNames = new ArrayList<>();
        for (DcrDetailModelList s : dcrDetailModelLists) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapterCusSingleList.filterList(filteredNames);
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
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_dr_img));
                break;
            case "2":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_chemist_img));
                break;
            case "3":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_stockist_img));
                break;
            case "4":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_unlistdr_img));
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
    public void onItemClick(TpModelList tpModelLists,int pos) {

    }
}