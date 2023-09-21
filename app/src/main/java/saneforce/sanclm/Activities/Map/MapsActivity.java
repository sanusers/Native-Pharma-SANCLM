package saneforce.sanclm.Activities.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.CommonClasses.CommonSharedPreference;
import saneforce.sanclm.CommonClasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.Activities.HomeScreen.HomeDashBoard;
import saneforce.sanclm.Activities.Map.CallSelection.DCRSelectionList;

public class MapsActivity extends AppCompatActivity {
    ImageView img_arrow_right, img_arrow_left, img_refresh, img_cur_loc;
    Button btn_tag;
    RecyclerView rv_list;
    View view_one, view_two;
    TextView tv_doctor, tv_chemist, tv_stockist, tv_undr, tv_tag_addr;
    ConstraintLayout constraintShowCustList;
    TaggingAdapter taggingAdapter;
    String from = "", tv_custName = "";
    Dialog dialogTagCust;
    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference mCommonSharedPrefrence;

    @Override
    public void onBackPressed() {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        commonUtilsMethods = new CommonUtilsMethods(this);
        mCommonSharedPrefrence = new CommonSharedPreference(this);

        commonUtilsMethods.FullScreencall();

        tv_doctor = findViewById(R.id.tag_tv_doctor);
        tv_chemist = findViewById(R.id.tag_tv_chemist);
        tv_stockist = findViewById(R.id.tag_tv_stockist);
        tv_undr = findViewById(R.id.tag_tv_undr);

        constraintShowCustList = findViewById(R.id.constraint_mid);

        img_arrow_right = findViewById(R.id.img_rv_right);
        img_arrow_left = findViewById(R.id.img_rv_left);
        img_cur_loc = findViewById(R.id.img_cur_loc);
        img_refresh = findViewById(R.id.img_refresh_map);
        tv_tag_addr = findViewById(R.id.tv_tagged_address);
        btn_tag = findViewById(R.id.btn_tag);
        rv_list = findViewById(R.id.rv_list);
        view_one = findViewById(R.id.view_one);
        view_two = findViewById(R.id.view_two);
        dummyAdapter();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            tv_custName = extra.getString("cust_name");
            from = extra.getString("from");
            if (!from.isEmpty()) {
                if (from.equalsIgnoreCase("tag_adapter")) {
                    btn_tag.setText("Tag");
                    tv_tag_addr.setVisibility(View.VISIBLE);
                    constraintShowCustList.setVisibility(View.GONE);
                    img_arrow_right.setVisibility(View.GONE);
                    rv_list.setVisibility(View.GONE);
                } else if (from.equalsIgnoreCase("cust_sel_list")) {
                    tv_tag_addr.setVisibility(View.GONE);
                    constraintShowCustList.setVisibility(View.VISIBLE);
                    img_arrow_right.setVisibility(View.VISIBLE);
                    rv_list.setVisibility(View.VISIBLE);
                }
            }
        }

        btn_tag.setOnClickListener(view -> {
            if (from.equalsIgnoreCase("tag_adapter")) {
                img_refresh.setVisibility(View.GONE);
                DisplayDialog();
            } else {
                Intent intent1 = new Intent(MapsActivity.this, HomeDashBoard.class);
                startActivity(intent1);
            }
        });

        img_arrow_right.setOnClickListener(view -> {
            rv_list.setVisibility(View.GONE);
            img_arrow_right.setVisibility(View.GONE);
            img_arrow_left.setVisibility(View.VISIBLE);
        });

        img_arrow_left.setOnClickListener(view -> {
            rv_list.setVisibility(View.VISIBLE);
            img_arrow_right.setVisibility(View.VISIBLE);
            img_arrow_left.setVisibility(View.GONE);
        });

        tv_doctor.setOnClickListener(view -> {
            tv_doctor.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            tv_chemist.setBackground(null);
            tv_stockist.setBackground(null);
            tv_undr.setBackground(null);
        });

        tv_chemist.setOnClickListener(view -> {
            tv_doctor.setBackground(null);
            tv_chemist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            tv_stockist.setBackground(null);
            tv_undr.setBackground(null);
        });

        tv_stockist.setOnClickListener(view -> {
            tv_doctor.setBackground(null);
            tv_chemist.setBackground(null);
            tv_stockist.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
            tv_undr.setBackground(null);
        });

        tv_undr.setOnClickListener(view -> {
            tv_doctor.setBackground(null);
            tv_chemist.setBackground(null);
            tv_stockist.setBackground(null);
            tv_undr.setBackground(getResources().getDrawable(R.drawable.bg_light_purple));
        });
    }

    private void dummyAdapter() {
        ArrayList<TaggedMapList> taggedMapListArrayList = new ArrayList<>();
        TaggedMapList taggedMapList = new TaggedMapList("Aasik", "23,B Kaja Thoppu, Thennur, Trichy - 620017");
        TaggedMapList taggedMapList1 = new TaggedMapList("Venkat", "67/b Maildamy Road, 3rd Street, Cuddalore - 627889");
        TaggedMapList taggedMapList2 = new TaggedMapList("Aravindh", "No,31, 3rd Indra Street, Kanu Nagar, Nesapakkam, Chennai - 600008");
        TaggedMapList taggedMapList3 = new TaggedMapList("Surya", "78/98 Aravindhan Nagar, Fork Road, 1st Street, Hyderabad - 987666");
        TaggedMapList taggedMapList4 = new TaggedMapList("Aathif Mirza", "No: 56/87, Walaja Street, Annai terasa Road, Ground Floor, Madurai - 650001");
        taggedMapListArrayList.add(taggedMapList);
        taggedMapListArrayList.add(taggedMapList1);
        taggedMapListArrayList.add(taggedMapList2);
        taggedMapListArrayList.add(taggedMapList3);
        taggedMapListArrayList.add(taggedMapList4);

        taggingAdapter = new TaggingAdapter(MapsActivity.this, taggedMapListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_list.setLayoutManager(mLayoutManager);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        rv_list.setAdapter(taggingAdapter);
    }

    private void DisplayDialog() {
        dialogTagCust = new Dialog(this);
        dialogTagCust.setContentView(R.layout.dialog_confirmtag_alert);
        dialogTagCust.setCancelable(false);
        dialogTagCust.show();

        Button btn_confirm = dialogTagCust.findViewById(R.id.btn_confirm);
        Button btn_cancel = dialogTagCust.findViewById(R.id.btn_cancel);
        TextView tv_cust_name = dialogTagCust.findViewById(R.id.txt_cust_name);

        tv_cust_name.setText(tv_custName);

        btn_confirm.setOnClickListener(view -> {
            dialogTagCust.dismiss();
            Intent intent1 = new Intent(MapsActivity.this, DCRSelectionList.class);
            startActivity(intent1);
        });

        btn_cancel.setOnClickListener(view -> {
            dialogTagCust.dismiss();
            btn_tag.setText("Tag");
            img_refresh.setVisibility(View.GONE);
            tv_tag_addr.setVisibility(View.VISIBLE);
            constraintShowCustList.setVisibility(View.GONE);
            img_arrow_right.setVisibility(View.GONE);
            rv_list.setVisibility(View.GONE);
        });
    }
}