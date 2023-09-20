package saneforce.sanclm.activity.HomeScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.HomeScreen.Adapters.ViewpagetAdapter;


public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    ImageView imageView;
    public static ViewPager2 viewPager,viewPager1;
    TabLayout tabLayout;
    ViewpagetAdapter viewpagetAdapter;
    NavigationView navigationView;




    LinearLayout pre_layout, slide_layout, report_layout, anlas_layout;

    public ActionBarDrawerToggle actionBarDrawerToggle;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dash_board);

        pre_layout = findViewById(R.id.ll_presentation);
        slide_layout =findViewById(R.id.ll_slide);
        report_layout = findViewById(R.id.ll_report);
        anlas_layout = findViewById(R.id.ll_analys);



        drawerLayout = findViewById(R.id.my_drawer_layout);
        imageView = findViewById(R.id.back_arrow);
        viewPager = findViewById(R.id.view_pager);
        viewPager1 = findViewById(R.id.view_pager1);
        tabLayout = findViewById(R.id.tablelayout);


        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_from);





        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);





        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.syncState();


        viewpagetAdapter = new ViewpagetAdapter(this,1);
        viewPager.setAdapter(viewpagetAdapter);
        viewpagetAdapter = new ViewpagetAdapter(this,2);
        viewPager1.setAdapter(viewpagetAdapter);






        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int width = (int) ((((displayMetrics.widthPixels / 3) * 1.9) / 3)-10);
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        param1.setMargins(0, 5, 0, 0);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        param.setMargins(10, 5, 0, 0);
        pre_layout.setLayoutParams(param1);
        slide_layout.setLayoutParams(param);
        report_layout.setLayoutParams(param);
        anlas_layout.setLayoutParams(param);




        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @SuppressLint("ResourceType")
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){

                    case 0:
                        tab.setText("Work Plan");
                        break;
                    case 1:
                        tab.setText("Calls");
                        break;
                    case 2:
                        tab.setText("Outbox");

                        BadgeDrawable badgeDrawable=tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                        badgeDrawable.setVisible(true);
                        badgeDrawable.setHorizontalOffset(1);
                        badgeDrawable.setVerticalOffset(10);
                        badgeDrawable.setBadgeGravity(BadgeDrawable.TOP_END);
                        badgeDrawable.setNumber(10);
                        badgeDrawable.setBadgeTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));

                        break;

                }
            }
        });
        tabLayoutMediator.attach();

        imageView.setBackgroundResource(R.drawable.bars_sort_img);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    imageView.setBackgroundResource(R.drawable.bars_sort_img);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                    imageView.setBackgroundResource(R.drawable.cross_img);

                }
            }
        });

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.nav_leave_appln) {
            Toast.makeText(this, "Leave ", Toast.LENGTH_LONG).show();
            return true;
        }

        return true;
    }
}

