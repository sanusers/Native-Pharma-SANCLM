package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.DCRCallSelectionTabLayoutAdapter;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.ChemistFragment;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.ListedDoctorFragment;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.StockiestFragment;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments.UnlistedDoctorFragment;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.CallDcrSelectionBinding;
import saneforce.sanclm.storage.SharedPref;

public class DcrCallTabLayoutActivity extends AppCompatActivity {
    CallDcrSelectionBinding dcrSelectionBinding;
    CommonUtilsMethods commonUtilsMethods;
    DCRCallSelectionTabLayoutAdapter viewPagerAdapter;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrSelectionBinding = CallDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(dcrSelectionBinding.getRoot());
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.FullScreencall();

        viewPagerAdapter = new DCRCallSelectionTabLayoutAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new ListedDoctorFragment(), SharedPref.getCaptionDr(getApplicationContext()));

        if (SharedPref.getChemistNeed(getApplicationContext()).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ChemistFragment(), SharedPref.getCaptionChemist(getApplicationContext()));
        }
        if (SharedPref.getCipNeed(getApplicationContext()).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ChemistFragment(), SharedPref.getCaptionChemist(getApplicationContext()));
        }
        if (SharedPref.getStockistNeed(getApplicationContext()).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new StockiestFragment(), SharedPref.getCaptionStockist(getApplicationContext()));
        }
        if (SharedPref.getUndrNeed(getApplicationContext()).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new UnlistedDoctorFragment(), SharedPref.getCaptionUnDr(getApplicationContext()));
        }

        dcrSelectionBinding.viewPagerCallSelection.setAdapter(viewPagerAdapter);
        dcrSelectionBinding.tabLayoutCall.setupWithViewPager(dcrSelectionBinding.viewPagerCallSelection);

        dcrSelectionBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(DcrCallTabLayoutActivity.this, HomeDashBoard.class)));
    }
}