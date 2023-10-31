package saneforce.sanclm.activity.homeScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Locale;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.SlideDownloaderAlertBox.SlideDownloaderAlertBox;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.activity.changepassword.Change_passwordActivity;
import saneforce.sanclm.activity.forms.Forms_activity;
import saneforce.sanclm.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.sanclm.activity.homeScreen.adapters.CustomViewPager;
import saneforce.sanclm.activity.homeScreen.adapters.ViewpagetAdapter;
import saneforce.sanclm.activity.leave.Leave_Application;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.activity.homeScreen.adapters.CustomPagerAdapter;
import saneforce.sanclm.activity.homeScreen.adapters.CustomViewPager;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.activity.homeScreen.adapters.ViewpagetAdapter;
import saneforce.sanclm.activity.leave.Leave_Application;
import saneforce.sanclm.activity.masterSync.MasterSyncActivity;
import saneforce.sanclm.activity.myresource.MyResource_Activity;
import saneforce.sanclm.activity.presentation.CreatePresentation;
import saneforce.sanclm.activity.presentation.Presentation;
import saneforce.sanclm.activity.tourPlan.TourPlanActivity;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;


public class HomeDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static ViewPager2 viewPager;
    public static CustomViewPager viewPager1;
    public static int DeviceWith;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView imageView;
    private DrawerLayout drawerLayout;
    TabLayout tabLayout;
    ViewpagetAdapter viewpagetAdapter;
    NavigationView navigationView;
    LinearLayout pre_layout, slide_layout, report_layout, anlas_layout;
    ImageView masterSync, img_account, img_notofication;
    CommonUtilsMethods commonUtilsMethods;
    LocationManager locationManager;
    SQLite sqLite;

    RelativeLayout rl_quick_link;
    private DrawerLayout drawerLayout1;
    LoginResponse loginResponse;
    Dialog dialog, dialog1;
    private int passwordNotVisible = 1, passwordNotVisible1 = 1;
    String valid_no = "";
    LinearLayout user_view;

    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, true);
            }
        } else {
            CommonUtilsMethods.RequestGPSPermission(HomeDashBoard.this);
        }
//        commonUtilsMethods.FullScreencall();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dash_board);
        sqLite = new SQLite(HomeDashBoard.this);
        sqLite.getWritableDatabase();
        dialog = new Dialog(this);
        dialog1=new Dialog(this);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        commonUtilsMethods = new CommonUtilsMethods(this);
//        commonUtilsMethods.FullScreencall();
        pre_layout = findViewById(R.id.ll_presentation);
        slide_layout = findViewById(R.id.ll_slide);
        report_layout = findViewById(R.id.ll_report);
        anlas_layout = findViewById(R.id.ll_analys);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        imageView = findViewById(R.id.back_arrow);
        viewPager = findViewById(R.id.view_pager);
        viewPager1 = findViewById(R.id.view_pager1);
        tabLayout = findViewById(R.id.tablelayout);
        navigationView = findViewById(R.id.nav_view);
        masterSync = findViewById(R.id.img_sync);

        img_account = findViewById(R.id.img_account);
        img_notofication = findViewById(R.id.img_notofication);
        user_view = findViewById(R.id.user_view);
        rl_quick_link = findViewById(R.id.rl_quick_link);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DeviceWith = displayMetrics.widthPixels;

        Log.e("test","fcm token : " + SharedPref.getFcmToken(HomeDashBoard.this));

        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.width = DeviceWith/3;
        navigationView.setLayoutParams(layoutParams);

        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        imageView.setBackgroundResource(R.drawable.bars_sort_img);

        viewpagetAdapter = new ViewpagetAdapter(this, 1);
        viewPager.setAdapter(viewpagetAdapter);

        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager1.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(""));
        mediator.attach();
        setupCustomTab(tabLayout, 0, "WorkPlan", false);
        setupCustomTab(tabLayout, 1, "Calls", false);
        setupCustomTab(tabLayout, 2, "Outbox", true);


        // Listener
        ViewTreeObserver vto = rl_quick_link.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int getlayout  = rl_quick_link.getMeasuredWidth();
                int width = (int) (getlayout/ 3-8);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
                param.setMargins(0, 5, 10, 0);
                pre_layout.setLayoutParams(param);
                slide_layout.setLayoutParams(param);
                report_layout.setLayoutParams(param);
                anlas_layout.setLayoutParams(param);


            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTitle = tab.getCustomView().findViewById(R.id.tablayname);
                tabTitle.setTextColor(getResources().getColor(R.color.text_dark));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTitle = tab.getCustomView().findViewById(R.id.tablayname);
                tabTitle.setTextColor(getResources().getColor(R.color.text_dark_65));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                imageView.setBackgroundResource(R.drawable.cross_img);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                imageView.setBackgroundResource(R.drawable.bars_sort_img);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

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

        pre_layout.setOnClickListener(v -> startActivity(new Intent(HomeDashBoard.this, Presentation.class)));

        masterSync.setOnClickListener(v -> startActivity(new Intent(HomeDashBoard.this, MasterSyncActivity.class)));
//        drawerLayout.closeDrawer(Gravity.END);

        img_account.setOnClickListener(v -> {
            showPopup(img_account);
        });

    }


    @SuppressLint("WrongConstant")
    public void showPopup(ImageView viewed_img) {
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        commonUtilsMethods = new CommonUtilsMethods(this);
//        commonUtilsMethods.FullScreencall();

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.user_details, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(viewed_img, Gravity.RIGHT, 85, -148);

        TextView user_name = popupView.findViewById(R.id.user_name);
        TextView sf_name = popupView.findViewById(R.id.sf_name);
        TextView clut = popupView.findViewById(R.id.clut);
        LinearLayout l_click = popupView.findViewById(R.id.change_passwrd);
        LinearLayout user_logout = popupView.findViewById(R.id.user_logout);


        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData(true);

            l_click.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        l_click.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

            user_logout.setOnClickListener(v -> {
                startActivity(new Intent(HomeDashBoard.this, LoginActivity.class));
            });
            String username = loginResponse.getSF_Name();
            String user_roll = loginResponse.getDS_name();
            String hq_name = loginResponse.getHQName();
            user_name.setText(username);
            sf_name.setText(user_roll);
            clut.setText(hq_name);
        user_logout.setOnClickListener(v -> {
            startActivity(new Intent(HomeDashBoard.this, LoginActivity.class));
        });

        // TODO Auto-generated method stub
        l_click.setOnClickListener(v -> {
            popupWindow.dismiss();
            changepassword();
        });
        popupWindow.setFocusable(true);
        popupWindow.update();
        // TODO Auto-generated method stub
        l_click.setOnClickListener(v -> {
            popupWindow.dismiss();
            changepassword();
        });

        popupWindow.setFocusable(true);
        popupWindow.update();
    }


    @SuppressLint({"MissingInflatedId", "WrongConstant"})
    public void changepassword() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.FullScreencall();

        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData(true);

        dialog1.setContentView(R.layout.change_password);

        Window window1 = dialog1.getWindow();


        if (window1 != null) {
            WindowManager.LayoutParams layoutParams = window1.getAttributes();
            window1.setGravity(Gravity.CENTER);

            window1.setLayout(getResources().getDimensionPixelSize(R.dimen._210sdp), getResources().getDimensionPixelSize(R.dimen._220sdp));
//            window1.setLayout(500, 580);


            window1.setAttributes(layoutParams);
        }

//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.setCancelable(false);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.popupMenuStyle;

        EditText old_password = dialog1.findViewById(R.id.old_pass);
        ImageView old_view = dialog1.findViewById(R.id.oldpas_icon);
        ImageView newpass_view = dialog1.findViewById(R.id.noepass_icon);
        EditText new_password = dialog1.findViewById(R.id.newpasswrd);
        EditText remain_password = dialog1.findViewById(R.id.repeatpass);
        LinearLayout update = dialog1.findViewById(R.id.update);
        ImageView cls_but = dialog1.findViewById(R.id.close);
        ImageView oldpas_icon = dialog1.findViewById(R.id.oldpas_icon);

        String password = loginResponse.getSF_Password();
        String password1 = old_password.getText().toString().trim().replaceAll("\\s", "");

        old_view.setOnClickListener(v -> {

            if (!old_password.getText().toString().equals("")) {

                if (passwordNotVisible == 1) {
                    old_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    old_view.setImageDrawable(getResources().getDrawable(R.drawable.eye_hide));
                    passwordNotVisible = 0;
                } else {
                    old_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    old_view.setImageDrawable(getResources().getDrawable(R.drawable.eye_visible));
                    passwordNotVisible = 1;
                }

                old_password.setSelection(old_password.length());
            } else {

            }

        });


        newpass_view.setOnClickListener(v -> {
            if (!new_password.getText().toString().equals("")) {
                if (passwordNotVisible1 == 1) {
                    new_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    remain_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    newpass_view.setImageDrawable(getResources().getDrawable(R.drawable.eye_hide));
                    passwordNotVisible1 = 0;
                } else {
                    new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    remain_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newpass_view.setImageDrawable(getResources().getDrawable(R.drawable.eye_visible));
                    passwordNotVisible1 = 1;
                }

                new_password.setSelection(new_password.length());

            } else {

            }
        });


        update.setOnClickListener(v -> {

            if (old_password.getText().toString().equals("")) {
                Toast.makeText(HomeDashBoard.this, "Please submit old password", Toast.LENGTH_SHORT).show();
            } else if (new_password.getText().toString().equals("")) {
                Toast.makeText(HomeDashBoard.this, "Please submit new password", Toast.LENGTH_SHORT).show();
            } else if (remain_password.getText().toString().equals("")) {
                Toast.makeText(HomeDashBoard.this, "Please submit repeat password", Toast.LENGTH_SHORT).show();
            } else {
                if (!password.equals(old_password.getText().toString())) {
                    Toast.makeText(HomeDashBoard.this, "Please Chack Old Password", Toast.LENGTH_SHORT).show();
                } else if (!new_password.getText().toString().equals(remain_password.getText().toString())) {
                    Toast.makeText(HomeDashBoard.this, "Please Chack not match Password", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonobj = new JSONObject();
                        jsonobj.put("Oldpassword", old_password.getText().toString());
                        jsonobj.put("Newpassword", new_password.getText().toString());
                        Log.d("passwrd_chng", String.valueOf(jsonobj));
                    } catch (Exception e) {

                    }


                    Toast.makeText(HomeDashBoard.this, "Password changed", Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                }
            }

        });


        cls_but.setOnClickListener(v -> {
            dialog1.dismiss();
        });

        dialog1.show();

    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_leave_appln) {
            startActivity(new Intent(HomeDashBoard.this, Leave_Application.class));
            return true;
        }

        if (id == R.id.nav_tour_plan) {
            startActivity(new Intent(HomeDashBoard.this, TourPlanActivity.class));
            return true;
        }
        if (id == R.id.nav_myresource) {

            startActivity(new Intent(HomeDashBoard.this, MyResource_Activity.class));
            return true;
        }

        if (id == R.id.nav_approvals) {
            startActivity(new Intent(HomeDashBoard.this, ApprovalsActivity.class));
            return true;
        }

        if (id == R.id.nav_forms) {
            startActivity(new Intent(HomeDashBoard.this, Forms_activity.class));
            return true;
        }

        if (id == R.id.nav_create_presentation) {
            SlideDownloaderAlertBox.openCustomDialog(HomeDashBoard.this,"2");
         //   startActivity(new Intent(HomeDashBoard.this, CreatePresentation.class));
            return true;
        }

        if (id == R.id.nav_nearme) {
            if (UtilityClass.isNetworkAvailable(HomeDashBoard.this)) {
                Intent intent = new Intent(HomeDashBoard.this, MapsActivity.class);
                intent.putExtra("from", "not_tagging");
                MapsActivity.SelectedTab = "D";
                if (SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this).equalsIgnoreCase("null")) {
                    MapsActivity.SelectedHqCode = "";
                    MapsActivity.SelectedHqName = "";
                } else {
                    MapsActivity.SelectedHqCode = SharedPref.getTodayDayPlanSfCode(HomeDashBoard.this);
                    MapsActivity.SelectedHqName = SharedPref.getTodayDayPlanSfName(HomeDashBoard.this);
                }

                startActivity(intent);
                return true;
            } else {
                Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }
    private void setupCustomTab(TabLayout tabLayout, int tabIndex, String tabTitleText, boolean isTabTitleInvisible) {
        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        if (tab != null) {
            View customView = LayoutInflater.from(this).inflate(R.layout.customtab_item, null);
            tab.setCustomView(customView);
            TextView tabTitle = customView.findViewById(R.id.tablayname);
            if (tabIndex == 0) {
                tabTitle.setTextColor(getResources().getColor(R.color.text_dark));
            }

            tabTitle.setText(tabTitleText);
            TextView tabTitleInvisible = customView.findViewById(R.id.tv_filter_count);
            tabTitleInvisible.setVisibility(isTabTitleInvisible ? View.VISIBLE : View.GONE);
        }
    }
}

