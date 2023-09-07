package saneforce.sanclm.activity.HomeScreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import saneforce.sanclm.Leave_Application;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.HomeScreen.Adapters.ViewpagetAdapter;
import saneforce.sanclm.activity.SplashScreen;
import saneforce.sanclm.activity.mastersync.MasterSyncActivity;


public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    ImageView imageView;
    public static ViewPager2 viewPager, viewPager1;
    TabLayout tabLayout;
    ViewpagetAdapter viewpagetAdapter;
    RecyclerView recyclerView;

    public ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

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

        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.syncState();


        String[] labels = new String[]{"WorkPlan", "Calls", "OutBox"};
        viewpagetAdapter = new ViewpagetAdapter(this, 1);
        viewPager.setAdapter(viewpagetAdapter);

        viewpagetAdapter = new ViewpagetAdapter(this, 2);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_leave_appln) {
            Intent l = new Intent(this.getApplicationContext(), Leave_Application.class);
            startActivity(l);
            finish();
            return true;
        }

        return true;
    }
}
