package saneforce.sanzen.activity.myresource.StockBalanceview;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.badge.BadgeDrawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.forms.weekoff.forms_viewpager;
import saneforce.sanzen.activity.myresource.Categoryview.Cate_Chemistview;
import saneforce.sanzen.activity.myresource.Categoryview.Cate_Doctorview;
import saneforce.sanzen.activity.myresource.Categoryview.Category_adapter;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityStockbalanceviewBinding;
import saneforce.sanzen.databinding.ActivityUnlistedadditionBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class StockBalanceScreen extends AppCompatActivity {
public ActivityStockbalanceviewBinding activityStockbalanceviewBinding;
    CommonUtilsMethods commonUtilsMethods;
    MasterDataDao masterDataDao;
    ArrayList<StockModelClass> stockcount=new ArrayList<>();
    ArrayList<StockInputModel> inputcount=new ArrayList<>();
    private RoomDB roomDB;
    forms_viewpager formsviewpager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityStockbalanceviewBinding = ActivityStockbalanceviewBinding.inflate(getLayoutInflater());
        setContentView(activityStockbalanceviewBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();


        StockProductBalance productstock = new StockProductBalance();
        StockInputBalance inputstock = new StockInputBalance();

        activityStockbalanceviewBinding.tabLayout.setupWithViewPager(activityStockbalanceviewBinding.viewPager);
         formsviewpager = new forms_viewpager(getSupportFragmentManager(), 0);
        if (SharedPref.getSampleValidation(this).equalsIgnoreCase("1")){
            formsviewpager.addFragment(productstock,"Product");
        }
        if (SharedPref.getInputValidation(this).equalsIgnoreCase("1")){
            formsviewpager.addFragment(inputstock, "Input");
        }
        activityStockbalanceviewBinding.viewPager.setAdapter(formsviewpager);        //set the icons
        activityStockbalanceviewBinding.tabLayout.getTabAt(0);
        activityStockbalanceviewBinding.tabLayout.getTabAt(1);
        BadgeDrawable badgeDrawable = Objects.requireNonNull(activityStockbalanceviewBinding.tabLayout.getTabAt(0)).getOrCreateBadge();
        badgeDrawable.setVisible(false);
        activityStockbalanceviewBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
