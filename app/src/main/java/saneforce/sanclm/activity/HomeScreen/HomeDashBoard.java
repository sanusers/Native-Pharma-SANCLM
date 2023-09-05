package saneforce.sanclm.activity.HomeScreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.HomeScreen.Adapters.ViewpagetAdapter;


public class HomeDashBoard extends AppCompatActivity {


    private DrawerLayout drawerLayout;
    ImageView imageView;
   public static ViewPager2 viewPager,viewPager1;
    TabLayout tabLayout;
    ViewpagetAdapter viewpagetAdapter;
    RecyclerView recyclerView ;

    public ActionBarDrawerToggle actionBarDrawerToggle;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dash_board);



        drawerLayout = findViewById(R.id.my_drawer_layout);
        imageView = findViewById(R.id.back_arrow);
        viewPager = findViewById(R.id.view_pager);
        viewPager1 = findViewById(R.id.view_pager1);
        tabLayout = findViewById(R.id.tab_layout);

        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);



        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.syncState();








        String[] labels = new String[]{"WorkPlan","Calls","OutBox"};
        viewpagetAdapter = new ViewpagetAdapter(this,1);
        viewPager.setAdapter(viewpagetAdapter);

        viewpagetAdapter = new ViewpagetAdapter(this,2);
        viewPager1.setAdapter(viewpagetAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(labels[position]);
        }).attach();

        imageView.setBackgroundResource(R.drawable.bars_sort);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    imageView.setBackgroundResource(R.drawable.bars_sort);

                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                    imageView.setBackgroundResource(R.drawable.cross);

                }
            }
        });

    }





    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
