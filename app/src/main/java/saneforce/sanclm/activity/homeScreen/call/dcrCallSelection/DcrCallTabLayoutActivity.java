package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;


import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.databinding.CallDcrSelectionBinding;

public class DcrCallTabLayoutActivity extends AppCompatActivity {
    CallDcrSelectionBinding dcrSelectionBinding;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dcrSelectionBinding = CallDcrSelectionBinding.inflate(getLayoutInflater());
        setContentView(dcrSelectionBinding.getRoot());

        dcrSelectionBinding.tabLayoutCall.addTab(dcrSelectionBinding.tabLayoutCall.newTab().setText("Listed Doctor"));
        dcrSelectionBinding.tabLayoutCall.addTab(dcrSelectionBinding.tabLayoutCall.newTab().setText("Chemist"));
        dcrSelectionBinding.tabLayoutCall.addTab(dcrSelectionBinding.tabLayoutCall.newTab().setText("Stockiest"));
        dcrSelectionBinding.tabLayoutCall.addTab(dcrSelectionBinding.tabLayoutCall.newTab().setText("Unlisted Doctor"));

        DCRCallSelectionTabLayoutAdapter adapter = new DCRCallSelectionTabLayoutAdapter(this, getSupportFragmentManager(), dcrSelectionBinding.tabLayoutCall.getTabCount());
        dcrSelectionBinding.viewPagerCallSelection.setAdapter(adapter);
        dcrSelectionBinding.viewPagerCallSelection.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(dcrSelectionBinding.tabLayoutCall));

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dcrSelectionBinding.searchCust.getWindowToken(), 0);

        dcrSelectionBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DcrCallTabLayoutActivity.this, HomeDashBoard.class));
            }
        });

        dcrSelectionBinding.tabLayoutCall.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                dcrSelectionBinding.viewPagerCallSelection.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}