package saneforce.sanzen.activity.approvals.dcr.detailView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanzen.activity.approvals.dcr.detailView.adapter.AdapterCusSingleList;
import saneforce.sanzen.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanzen.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanzen.activity.approvals.tp.pojo.TpModelList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.ActivityDcrDetailViewBinding;
import saneforce.sanzen.storage.SharedPref;

public class DcrDetailViewActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public static ArrayList<DcrDetailModelList> dcrDetailModelLists;
    public static String SelectedCode;
    @SuppressLint("StaticFieldLeak")
    public static ActivityDcrDetailViewBinding dcrDetailViewBinding;
    AdapterCusSingleList adapterCusSingleList;
      public static String dcr_id ,Details_id,dcr_code;
    String hq_name, Cus_pob, Cus_jw, Cus_type, Cus_cluster, Cus_remark, Cus_feedback, Cus_modTime, Cus_visitTime,cut_name;

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
            cut_name = extra.getString("cut_name");
            hq_name = extra.getString("hq_name");
            Cus_cluster = extra.getString("cus_cluster");
            Cus_pob = extra.getString("cus_pob");
            Cus_jw = extra.getString("cus_jw");
            Cus_type = extra.getString("cus_type");
            Cus_remark = extra.getString("cus_remark");
            Cus_feedback = extra.getString("cus_fb");
            Cus_modTime = extra.getString("cus_mod_time");
            Cus_visitTime = extra.getString("cus_visit_time");
            dcr_id = extra.getString("dcr_id");
            Details_id = extra.getString("Details_id");
        }

        dcrDetailViewBinding.tagHeader.setText(hq_name);
        if (!cut_name.isEmpty()) dcrDetailViewBinding.tvName.setText(cut_name);
        if (!Cus_cluster.isEmpty()) dcrDetailViewBinding.tvClusterTop.setText(Cus_cluster);
        if (!Cus_pob.isEmpty()) dcrDetailViewBinding.tvPob.setText(Cus_pob);
        if (!Cus_jw.isEmpty()) dcrDetailViewBinding.tvJw.setText(Cus_jw.replace("$$",","));
        if (!Cus_remark.isEmpty()) dcrDetailViewBinding.tvOverallRemarks.setText(Cus_remark);
        if (!Cus_feedback.isEmpty()) dcrDetailViewBinding.tvOverallFeedback.setText(Cus_feedback);
        if (!Cus_visitTime.isEmpty()) dcrDetailViewBinding.tvVisitTime.setText(Cus_visitTime);
        if (!Cus_modTime.isEmpty()) dcrDetailViewBinding.tvModifiedTime.setText(Cus_modTime);
        String detailingNeed = "0";

        switch (Cus_type) {
            case "1":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_dr_img));
                if (SharedPref.getDocProductCaption(this).isEmpty() || SharedPref.getDocProductCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSamplePrd.setText("Sample Products");
                    dcrDetailViewBinding.tagPrdName.setText("Product Name");
                }else{
                    dcrDetailViewBinding.tagSamplePrd.setText(SharedPref.getDocProductCaption(this));
                    dcrDetailViewBinding.tagPrdName.setText(SharedPref.getDocProductCaption(this));
                }
                if ( SharedPref.getDrSmpQCap(this).isEmpty() ||  SharedPref.getDrSmpQCap(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSample.setText("Samples");
                }else {
                    dcrDetailViewBinding.tagSample.setText(( SharedPref.getDrSmpQCap(this)));
                }
                if (SharedPref.getDrRxQCap(this).isEmpty() || SharedPref.getDrRxQCap(this).isEmpty()){
                    dcrDetailViewBinding.tagRxQty.setText("RX Qty");
                }else {
                    dcrDetailViewBinding.tagRxQty.setText(SharedPref.getDrRxQCap(this));
                }
                if (SharedPref.getDocInputCaption(this).isEmpty() || SharedPref.getDocInputCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagInput.setText("Input");
                    dcrDetailViewBinding.tagInputNameMain.setText("Input Name");
                }else {
                    dcrDetailViewBinding.tagInput.setText(SharedPref.getDocInputCaption(this));
                    dcrDetailViewBinding.tagInputNameMain.setText(SharedPref.getDocInputCaption(this));
                }
                if (SharedPref.getRcpaQtyNeed(this).equals("0")){
                    dcrDetailViewBinding.tagRcpaPrd.setVisibility(View.VISIBLE);
                }else{
                    dcrDetailViewBinding.tagRcpaPrd.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getDocPobNeed(this).equals("0")){
                    dcrDetailViewBinding.tagPob.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.VISIBLE);
                }
                if (SharedPref.getDocJointworkNeed(this).equals("0")){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                if ( SharedPref.getDfNeed(this).isEmpty() || SharedPref.getDfNeed(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagOverallFeedback.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvOverallFeedback.setVisibility(View.INVISIBLE);
                }else{
                    dcrDetailViewBinding.tagOverallFeedback.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvOverallFeedback.setVisibility(View.VISIBLE);
                }
                break;
            case "2":
                detailingNeed = SharedPref.getCHMDetailingNeed(this);
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_chemist_img));
                if (SharedPref.getChmProductCaption(this).isEmpty() || SharedPref.getChmProductCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSamplePrd.setText("Sample Products");
                    dcrDetailViewBinding.tagPrdName.setText("Product Name");
                }else{
                    dcrDetailViewBinding.tagSamplePrd.setText(SharedPref.getChmProductCaption(this));
                    dcrDetailViewBinding.tagPrdName.setText(SharedPref.getChmProductCaption(this));
                }
                if ( SharedPref.getChmSmpCap(this).isEmpty() ||  SharedPref.getChmSmpCap(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSample.setText("Samples");
                }else {
                    dcrDetailViewBinding.tagSample.setText(( SharedPref.getChmSmpCap(this)));
                }
                if (SharedPref.getChmQCap(this).isEmpty() || SharedPref.getChmQCap(this).isEmpty()){
                    dcrDetailViewBinding.tagRxQty.setText("RX Qty");
                }else {
                    dcrDetailViewBinding.tagRxQty.setText(SharedPref.getChmQCap(this));
                }
                if (SharedPref.getChmInputCaption(this).isEmpty() || SharedPref.getChmInputCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagInput.setText("Input");
                    dcrDetailViewBinding.tagInputNameMain.setText("Input Name");
                }else {
                    dcrDetailViewBinding.tagInput.setText(SharedPref.getChmInputCaption(this));
                    dcrDetailViewBinding.tagInputNameMain.setText(SharedPref.getChmInputCaption(this));
                }
                if ( SharedPref.getChmPobNeed(this).equals("0")){
                    dcrDetailViewBinding.tagPob.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.VISIBLE);
                }
                if (SharedPref.getChmJointworkNeed(this).equals("0")){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getCfNeed(this).isEmpty() || SharedPref.getCfNeed(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }
                break;
            case "3":
                detailingNeed = SharedPref.getSTKDetailingNeed(this);
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_stockist_img));
                if (SharedPref.getStkProductCaption(this).isEmpty() || SharedPref.getStkProductCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSamplePrd.setText("Sample Products");
                    dcrDetailViewBinding.tagPrdName.setText("Product Name");
                }else{
                    dcrDetailViewBinding.tagSamplePrd.setText(SharedPref.getStkProductCaption(this));
                    dcrDetailViewBinding.tagPrdName.setText(SharedPref.getStkProductCaption(this));
                }
                if (SharedPref.getStkQCap(this).isEmpty() || SharedPref.getStkQCap(this).isEmpty()){
                    dcrDetailViewBinding.tagRxQty.setText("RX Qty");
                }else {
                    dcrDetailViewBinding.tagRxQty.setText(SharedPref.getStkQCap(this));
                }
                if (SharedPref.getStkInputCaption(this).isEmpty() || SharedPref.getStkInputCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagInput.setText("Input");
                    dcrDetailViewBinding.tagInputNameMain.setText("Input Name");
                }else {
                    dcrDetailViewBinding.tagInput.setText(SharedPref.getStkInputCaption(this));
                    dcrDetailViewBinding.tagInputNameMain.setText(SharedPref.getStkInputCaption(this));
                }
                if ( SharedPref.getStkPobNeed(this).equals("0")){
                    dcrDetailViewBinding.tagPob.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.VISIBLE);
                }
                if (SharedPref.getStkJointworkNeed(this).equals("0")){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getSfNeed(this).isEmpty() || SharedPref.getSfNeed(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }
                break;
            case "4":
                detailingNeed = SharedPref.getUNDRDetailingNeed(this);
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_unlistdr_img));
                if (SharedPref.getUlProductCaption(this).isEmpty() || SharedPref.getUlProductCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSamplePrd.setText("Sample Products");
                    dcrDetailViewBinding.tagPrdName.setText("Product Name");
                }else{
                    dcrDetailViewBinding.tagSamplePrd.setText(SharedPref.getUlProductCaption(this));
                    dcrDetailViewBinding.tagPrdName.setText(SharedPref.getUlProductCaption(this));
                }
                if ( SharedPref.getNlSmpQCap(this).isEmpty() ||  SharedPref.getNlSmpQCap(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSample.setText("Samples");
                }else {
                    dcrDetailViewBinding.tagSample.setText(( SharedPref.getNlSmpQCap(this)));
                }
                if (SharedPref.getNlRxQCap(this).isEmpty() || SharedPref.getNlRxQCap(this).isEmpty()){
                    dcrDetailViewBinding.tagRxQty.setText("RX Qty");
                }else {
                    dcrDetailViewBinding.tagRxQty.setText(SharedPref.getNlRxQCap(this));
                }
                if (SharedPref.getUlInputCaption(this).isEmpty() || SharedPref.getUlInputCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagInput.setText("Input");
                    dcrDetailViewBinding.tagInputNameMain.setText("Input Name");
                }else {
                    dcrDetailViewBinding.tagInput.setText(SharedPref.getUlInputCaption(this));
                    dcrDetailViewBinding.tagInputNameMain.setText(SharedPref.getUlInputCaption(this));
                }
                if ( SharedPref.getUnlistedDoctorPobNeed(this).equals("0")){
                    dcrDetailViewBinding.tagPob.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.VISIBLE);
                }
                if (SharedPref.getUlJointworkNeed(this).equals("0")){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getNfNeed(this).isEmpty() || SharedPref.getNfNeed(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }
                break;
        }
        if(detailingNeed.equalsIgnoreCase("0")){
            dcrDetailViewBinding.constraintMainSld.setVisibility(View.VISIBLE);
            dcrDetailViewBinding.viewDummySld.setVisibility(View.VISIBLE);
        }else {
            dcrDetailViewBinding.constraintMainSld.setVisibility(View.GONE);
            dcrDetailViewBinding.viewDummySld.setVisibility(View.GONE);
        }

        SetUpCusListAdapter();

        dcrDetailViewBinding.ivBack.setOnClickListener(view -> {
            finish();
        });

        dcrDetailViewBinding.btnBack.setOnClickListener(view -> {
            finish();
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
        adapterCusSingleList = new AdapterCusSingleList(DcrDetailViewActivity.this, dcrDetailModelLists, DcrDetailViewActivity.this,Cus_type);
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
        dcrDetailViewBinding.constraintTpListContent.setVisibility(View.VISIBLE);
        dcrDetailViewBinding.tvName.setText(dcrDetailModelList.getName());
        SelectedCode = dcrDetailModelList.getCode();
        dcr_id= dcrDetailModelList.getDct_id();
        Details_id= dcrDetailModelList.getDcr_detial_id();

        Log.v("Details_id",""+dcr_id+" "+Details_id);
        switch (dcrDetailModelList.getType()) {
            case "1":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_dr_img));
                if (SharedPref.getDocProductCaption(this).isEmpty() || SharedPref.getDocProductCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSamplePrd.setText("Sample Products");
                    dcrDetailViewBinding.tagPrdName.setText("Product Name");
                }else{
                    dcrDetailViewBinding.tagSamplePrd.setText(SharedPref.getDocProductCaption(this));
                    dcrDetailViewBinding.tagPrdName.setText(SharedPref.getDocProductCaption(this));
                }
                if ( SharedPref.getDrSmpQCap(this).isEmpty() ||  SharedPref.getDrSmpQCap(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSample.setText("Samples");
                }else {
                    dcrDetailViewBinding.tagSample.setText(( SharedPref.getDrSmpQCap(this)));
                }
                if (SharedPref.getDrRxQCap(this).isEmpty() || SharedPref.getDrRxQCap(this).isEmpty()){
                    dcrDetailViewBinding.tagRxQty.setText("RX Qty");
                }else {
                    dcrDetailViewBinding.tagRxQty.setText(SharedPref.getDrRxQCap(this));
                }
                if (SharedPref.getDocInputCaption(this).isEmpty() || SharedPref.getDocInputCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagInput.setText("Input");
                    dcrDetailViewBinding.tagInputNameMain.setText("Input Name");
                }else {
                    dcrDetailViewBinding.tagInput.setText(SharedPref.getDocInputCaption(this));
                    dcrDetailViewBinding.tagInputNameMain.setText(SharedPref.getDocInputCaption(this));
                }
                if (SharedPref.getRcpaQtyNeed(this).equals("0")){
                    dcrDetailViewBinding.tagRcpaPrd.setVisibility(View.VISIBLE);
                }else{
                    dcrDetailViewBinding.tagRcpaPrd.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getDocPobNeed(this).equals("0")){
                    dcrDetailViewBinding.tagPob.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagPob.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getDocJointworkNeed(this).equals("0")){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                if ( SharedPref.getDfNeed(this).isEmpty() || SharedPref.getDfNeed(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagOverallFeedback.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvOverallFeedback.setVisibility(View.INVISIBLE);
                }else{
                    dcrDetailViewBinding.tagOverallFeedback.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvOverallFeedback.setVisibility(View.VISIBLE);
                }
                break;
            case "2":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_chemist_img));
                if (SharedPref.getChmProductCaption(this).isEmpty() || SharedPref.getChmProductCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSamplePrd.setText("Sample Products");
                    dcrDetailViewBinding.tagPrdName.setText("Product Name");
                }else{
                    dcrDetailViewBinding.tagSamplePrd.setText(SharedPref.getChmProductCaption(this));
                    dcrDetailViewBinding.tagPrdName.setText(SharedPref.getChmProductCaption(this));
                }
                if ( SharedPref.getChmSmpCap(this).isEmpty() ||  SharedPref.getChmSmpCap(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSample.setText("Samples");
                }else {
                    dcrDetailViewBinding.tagSample.setText(( SharedPref.getChmSmpCap(this)));
                }
                if (SharedPref.getChmQCap(this).isEmpty() || SharedPref.getChmQCap(this).isEmpty()){
                    dcrDetailViewBinding.tagRxQty.setText("RX Qty");
                }else {
                    dcrDetailViewBinding.tagRxQty.setText(SharedPref.getChmQCap(this));
                }
                if (SharedPref.getChmInputCaption(this).isEmpty() || SharedPref.getChmInputCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagInput.setText("Input");
                    dcrDetailViewBinding.tagInputNameMain.setText("Input Name");
                }else {
                    dcrDetailViewBinding.tagInput.setText(SharedPref.getChmInputCaption(this));
                    dcrDetailViewBinding.tagInputNameMain.setText(SharedPref.getChmInputCaption(this));
                }
                if ( SharedPref.getChmPobNeed(this).equals("0")){
                    dcrDetailViewBinding.tagPob.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagPob.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getChmJointworkNeed(this).equals("0")){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getCfNeed(this).isEmpty() || SharedPref.getCfNeed(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                dcrDetailViewBinding.tagRcpaPrd.setVisibility(View.VISIBLE);
                break;
            case "3":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_stockist_img));
                if (SharedPref.getStkProductCaption(this).isEmpty() || SharedPref.getStkProductCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSamplePrd.setText("Sample Products");
                    dcrDetailViewBinding.tagPrdName.setText("Product Name");
                }else{
                    dcrDetailViewBinding.tagSamplePrd.setText(SharedPref.getStkProductCaption(this));
                    dcrDetailViewBinding.tagPrdName.setText(SharedPref.getStkProductCaption(this));
                }
                if (SharedPref.getStkQCap(this).isEmpty() || SharedPref.getStkQCap(this).isEmpty()){
                    dcrDetailViewBinding.tagRxQty.setText("RX Qty");
                }else {
                    dcrDetailViewBinding.tagRxQty.setText(SharedPref.getStkQCap(this));
                }
                if (SharedPref.getStkInputCaption(this).isEmpty() || SharedPref.getStkInputCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagInput.setText("Input");
                    dcrDetailViewBinding.tagInputNameMain.setText("Input Name");
                }else {
                    dcrDetailViewBinding.tagInput.setText(SharedPref.getStkInputCaption(this));
                    dcrDetailViewBinding.tagInputNameMain.setText(SharedPref.getStkInputCaption(this));
                }
                if ( SharedPref.getStkPobNeed(this).equals("0")){
                    dcrDetailViewBinding.tagPob.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagPob.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getStkJointworkNeed(this).equals("0")){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                dcrDetailViewBinding.tagRcpaPrd.setVisibility(View.VISIBLE);
                break;
            case "4":
                dcrDetailViewBinding.imgCust.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.map_unlistdr_img));
                if (SharedPref.getUlProductCaption(this).isEmpty() || SharedPref.getUlProductCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSamplePrd.setText("Sample Products");
                    dcrDetailViewBinding.tagPrdName.setText("Product Name");
                }else{
                    dcrDetailViewBinding.tagSamplePrd.setText(SharedPref.getUlProductCaption(this));
                    dcrDetailViewBinding.tagPrdName.setText(SharedPref.getUlProductCaption(this));
                }
                if ( SharedPref.getNlSmpQCap(this).isEmpty() ||  SharedPref.getNlSmpQCap(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagSample.setText("Samples");
                }else {
                    dcrDetailViewBinding.tagSample.setText(( SharedPref.getNlSmpQCap(this)));
                }
                if (SharedPref.getNlRxQCap(this).isEmpty() || SharedPref.getNlRxQCap(this).isEmpty()){
                    dcrDetailViewBinding.tagRxQty.setText("RX Qty");
                }else {
                    dcrDetailViewBinding.tagRxQty.setText(SharedPref.getNlRxQCap(this));
                }
                if (SharedPref.getUlInputCaption(this).isEmpty() || SharedPref.getUlInputCaption(this).equalsIgnoreCase(null)){
                    dcrDetailViewBinding.tagInput.setText("Input");
                    dcrDetailViewBinding.tagInputNameMain.setText("Input Name");
                }else {
                    dcrDetailViewBinding.tagInput.setText(SharedPref.getUlInputCaption(this));
                    dcrDetailViewBinding.tagInputNameMain.setText(SharedPref.getUlInputCaption(this));
                }
                if ( SharedPref.getUnlistedDoctorPobNeed(this).equals("0")){
                    dcrDetailViewBinding.tagPob.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagPob.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvPob.setVisibility(View.INVISIBLE);
                }
                if (SharedPref.getUlJointworkNeed(this).equals("0")){
                    dcrDetailViewBinding.tagJw.setVisibility(View.VISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.VISIBLE);
                }else {
                    dcrDetailViewBinding.tagJw.setVisibility(View.INVISIBLE);
                    dcrDetailViewBinding.tvJw.setVisibility(View.INVISIBLE);
                }
                dcrDetailViewBinding.tagRcpaPrd.setVisibility(View.VISIBLE);
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
            dcrDetailViewBinding.tvJw.setText(dcrDetailModelList.getJointWork().replace("$$",","));
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