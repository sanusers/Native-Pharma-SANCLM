package saneforce.sanclm.activity.homeScreen.call;

import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;
import static saneforce.sanclm.activity.profile.CustomerProfile.isDetailingRequired;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.call.adapter.DCRCallTabLayoutAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.CallAddCustListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.SaveAdditionalCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.input.CallInputListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.product.CallProductListAdapter;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.DetailedFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.InputFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.ProductFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragmentSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.ActivityDcrcallBinding;
import saneforce.sanclm.response.SetupResponse;
import saneforce.sanclm.storage.SQLite;

public class DCRCallActivity extends AppCompatActivity {


    public static ArrayList<CustList> CallActivityCustDetails;
    DCRCallTabLayoutAdapter viewPagerAdapter;
   @SuppressLint("StaticFieldLeak")
   public static ActivityDcrcallBinding dcrcallBinding;
    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference mCommonSharedPreference;
    SQLite sqLite;
    int tab_pos = 0;
    SetupResponse setUpResponse;
    String capDrPrd, capChemPrd, capStkPrd, capUndrPrd, capCipPrd, capDrInp, capChemInp, capStkInp, capUndrInp, drRCPANeed, ChemRCPANeed;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrcallBinding = ActivityDcrcallBinding.inflate(getLayoutInflater());
        setContentView(dcrcallBinding.getRoot());

        commonUtilsMethods = new CommonUtilsMethods(this);
        mCommonSharedPreference = new CommonSharedPreference(this);
        sqLite = new SQLite(this);
        commonUtilsMethods.FullScreencall();

        getRequiredData();

        viewPagerAdapter = new DCRCallTabLayoutAdapter(getSupportFragmentManager());

        if (isDetailingRequired) {
            viewPagerAdapter.add(new DetailedFragment(), "Detailed");
        }
        if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
            viewPagerAdapter.add(new ProductFragment(), capDrPrd);
            viewPagerAdapter.add(new InputFragment(), capDrInp);
            viewPagerAdapter.add(new RCPAFragment(), "RCPA");
            viewPagerAdapter.add(new AdditionalCallFragment(), "Additional Calls");
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
            viewPagerAdapter.add(new ProductFragment(), capChemPrd);
            viewPagerAdapter.add(new InputFragment(), capChemInp);
            viewPagerAdapter.add(new RCPAFragment(), "RCPA");
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            viewPagerAdapter.add(new ProductFragment(), capStkPrd);
            viewPagerAdapter.add(new InputFragment(), capStkInp);
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
            viewPagerAdapter.add(new ProductFragment(), capUndrPrd);
            viewPagerAdapter.add(new InputFragment(), capUndrInp);
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("5")) {
            viewPagerAdapter.add(new ProductFragment(), "Product");
            viewPagerAdapter.add(new InputFragment(), "Input");
            viewPagerAdapter.add(new JWOthersFragment(), "JFW/Others");
        }

        dcrcallBinding.viewPager.setAdapter(viewPagerAdapter);
        dcrcallBinding.tabLayout.setupWithViewPager(dcrcallBinding.viewPager);
        dcrcallBinding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        AddProductsData();
        AddInputData();
        AddAdditionalCallData();
        AddRCPAData();
        AddJWData();

        dcrcallBinding.tagCustName.setText(CallActivityCustDetails.get(0).getName());

        dcrcallBinding.ivBack.setOnClickListener(view -> {
            Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
            startActivity(intent);
        });

        dcrcallBinding.btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(DCRCallActivity.this, DcrCallTabLayoutActivity.class);
            startActivity(intent);
        });

        dcrcallBinding.btnFinalSubmit.setOnClickListener(view -> {
            Intent intent = new Intent(DCRCallActivity.this, HomeDashBoard.class);
            startActivity(intent);
        });
    }

    private void getRequiredData() {
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.SETUP);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject setupData = jsonArray.getJSONObject(0);
                setUpResponse = new SetupResponse();
                Type typeSetup = new TypeToken<SetupResponse>() {
                }.getType();
                setUpResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);

                if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
                    capDrPrd = setUpResponse.getCaptionDrPrd();
                    capDrInp = setUpResponse.getCaptionDrInp();
                    drRCPANeed = setUpResponse.getDrRcpaNeed();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
                    capChemPrd = setUpResponse.getCaptionChemistPrd();
                    capChemInp = setUpResponse.getCaptionChemistInp();
                    ChemRCPANeed = setUpResponse.getChemistRcpaNeed();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
                    capStkPrd = setUpResponse.getCaptionStokistPrd();
                    capStkInp = setUpResponse.getCaptionStokistInp();
                } else if (CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
                    capUndrPrd = setUpResponse.getCaptionUndrPrd();
                    capUndrInp = setUpResponse.getCaptionUndrInp();
                }
            }

        } catch (Exception e) {

        }
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

        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + TodayPlanSfCode);
            Log.v("length", jsonArray.length() + "---" + Constants.DOCTOR + TodayPlanSfCode);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), false));
            }

        } catch (Exception e) {

        }
/*
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Aasik", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Aravindh", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Eman", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Rahamathullah", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Vignesh", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Joseph", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Philip", false));
        AdditionalCallFragment.custListArrayList.add(new CallCommonCheckedList("Govindh", false));*/
    }

    private void AddInputData() {
        InputFragment.callCommonCheckedListArrayList = new ArrayList<>();
        CallInputListAdapter.saveCallInputListArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.INPUT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false));
            }

        } catch (Exception e) {

        }
        /*InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Pen", false));
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
        InputFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Plastic Bar", false));*/
    }

    private void AddProductsData() {
        ProductFragment.callCommonCheckedListArrayList = new ArrayList<>();
        CallProductListAdapter.saveCallProductListArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.PRODUCT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), false, jsonObject.getString("Product_Mode")));
            }

        } catch (Exception e) {

        }
       /* ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("AtriFlam Tuesday data Para", false, "P1"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Insat", false, "SM"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Meff", false, "SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Sucraz", false, "P2"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Stanvit", false, "SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Calch 500", false, "SM/SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Arizon 700", false, "SL"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Terracite", false, "SM"));
        ProductFragment.callCommonCheckedListArrayList.add(new CallCommonCheckedList("Paracemetol", false, "SM/SL"));*/
    }
}