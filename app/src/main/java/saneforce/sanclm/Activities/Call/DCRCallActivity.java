package saneforce.sanclm.Activities.Call;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;

import saneforce.sanclm.Activities.Call.DcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.CommonClasses.CommonSharedPreference;
import saneforce.sanclm.CommonClasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.Activities.Call.Adapter.AdditionalCalls.CallAddCustListAdapter;
import saneforce.sanclm.Activities.Call.Adapter.AdditionalCalls.FinalSavedAdapter.SaveAdditionalCallAdapter;
import saneforce.sanclm.Activities.Call.Adapter.DCRCallTabLayoutAdapter;
import saneforce.sanclm.Activities.Call.Adapter.Input.CallInputListAdapter;
import saneforce.sanclm.Activities.Call.Adapter.Product.CallProductListAdapter;
import saneforce.sanclm.Activities.Call.Fragments.AdditionalCallDetailedSide;
import saneforce.sanclm.Activities.Call.Fragments.AdditionalCallFragment;
import saneforce.sanclm.Activities.Call.Fragments.InputFragment;
import saneforce.sanclm.Activities.Call.Fragments.JWOthersFragment;
import saneforce.sanclm.Activities.Call.Fragments.ProductFragment;
import saneforce.sanclm.Activities.Call.Fragments.RCPAFragmentSide;
import saneforce.sanclm.Activities.Call.Pojo.CallCommonCheckedList;
import saneforce.sanclm.Activities.Map.CustSelection.TagCustSelectionList;

public class DCRCallActivity extends AppCompatActivity {

    public static FragmentContainerView fragment_add_call_details_side, fragment_add_rcpa_side;
    public static ConstraintLayout constraint_dcr;
    TabLayout tabLayout;
    ViewPager viewPager;
    Button btn_cancel, btn_final_submit;
    ImageView img_back;
    TextView tv_cust_name;
    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference mCommonSharedPreference;
    int tab_pos = 0;
    String cust_name;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcrcall);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        btn_cancel = findViewById(R.id.btn_cancel);
        constraint_dcr = findViewById(R.id.constraint_dcr);
        btn_final_submit = findViewById(R.id.btn_final_submit);
        img_back = findViewById(R.id.iv_back);
        tv_cust_name = findViewById(R.id.tag_cust_name);

        commonUtilsMethods = new CommonUtilsMethods(this);
        mCommonSharedPreference = new CommonSharedPreference(this);

        commonUtilsMethods.FullScreencall();
        tabLayout.addTab(tabLayout.newTab().setText("Detailed"));
        tabLayout.addTab(tabLayout.newTab().setText("Product"));
        tabLayout.addTab(tabLayout.newTab().setText("Inputs"));
        tabLayout.addTab(tabLayout.newTab().setText("Additional Calls"));
        tabLayout.addTab(tabLayout.newTab().setText("RCPA"));
        tabLayout.addTab(tabLayout.newTab().setText("JFW/Others"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        AddProductsData();
        AddInputData();
        AddAdditionalCallData();
        AddRCPAData();
        AddJWData();

        DCRCallTabLayoutAdapter adapter = new DCRCallTabLayoutAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            cust_name = extra.getString("cust_name");
        }

        tv_cust_name.setText(cust_name);
        img_back.setOnClickListener(view -> {
            Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
            startActivity(intent);
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.v("getfrag", "----" + tab.getPosition());
                mCommonSharedPreference.setValueToPreference("tab_pos_dcr", String.valueOf(tab.getPosition()));
                tab_pos = tab.getPosition();
             /*   if (tab_pos == 0) {
                    fragment_detailed.setVisibility(View.VISIBLE);
                    fragment_products.setVisibility(View.GONE);
                } else if (tab_pos == 1) {
                    fragment_detailed.setVisibility(View.GONE);
                    fragment_products.setVisibility(View.VISIBLE);
                }*/
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void AddJWData() {
        JWOthersFragment.callCaptureImageLists = new ArrayList<>();
        JWOthersFragment.callAddedJointList = new ArrayList<>();
        JWOthersFragment.callAddedJointList.add(new CallCommonCheckedList("Joyal"));
        JWOthersFragment.callAddedJointList.add(new CallCommonCheckedList("Karthick"));
        JWOthersFragment.callAddedJointList.add(new CallCommonCheckedList("Siddiq"));
    }

    private void AddRCPAData() {
        RCPAFragmentSide.RCPAAddCompSideViewArrayList = new ArrayList<>();
        RCPAFragmentSide.rcpaAddedProdListArrayList = new ArrayList<>();
        RCPAFragmentSide.rcpa_Added_list = new ArrayList<>();
        RCPAFragmentSide.chemistNames = new ArrayList<>();
    }

    private void AddAdditionalCallData() {
        AdditionalCallFragment.custListArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.addInputAdditionalCallArrayList = new ArrayList<>();
        AdditionalCallDetailedSide.addSampleAdditionalCallArrayList = new ArrayList<>();
        CallAddCustListAdapter.saveAdditionalCallArrayList = new ArrayList<>();
        SaveAdditionalCallAdapter.nestedAddSampleCallDetails = new ArrayList<>();
        SaveAdditionalCallAdapter.nestedAddInputCallDetails = new ArrayList<>();

        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Aasik", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Aravindh", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Eman", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Rahamathullah", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Vignesh", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Joseph", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Philip", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Govindh", false));
    }

    private void AddInputData() {
        InputFragment.callCommonCheckedListArrayList = new ArrayList<>();
        CallInputListAdapter.saveCallInputListArrayList = new ArrayList<>();

        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Pen", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Marker", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Key Chain", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Keyboard", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Watch", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Horlicks", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Umberlla", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Lunch Box", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Ball", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Jacket", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Bat", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Toys", false));
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Plastic Bar", false));
    }

    private void AddProductsData() {

        ProductFragment.callCommonCheckedListArrayList = new ArrayList<>();
        CallProductListAdapter.saveCallProductListArrayList = new ArrayList<>();

        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("AtriFlam Tuesday data Para", false, "P1"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Insat", false, "SM"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Meff", false, "SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Sucraz", false, "P2"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Stanvit", false, "SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Calch 500", false, "SM/SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Arizon 700", false, "SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Terracite", false, "SM"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Paracemetol", false, "SM/SL"));

    }
}