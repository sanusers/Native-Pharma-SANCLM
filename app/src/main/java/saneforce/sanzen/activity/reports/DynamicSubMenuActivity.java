package saneforce.sanzen.activity.reports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.reports.dayReport.model.SubMenuModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DynamicSubMenuActivity extends AppCompatActivity {
    ArrayList<SubMenuModel> subMenuModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    DynamicSubMenuAdapter dynamicSubMenuAdapter;
    TextView toolbar_title;
    LinearLayout backArrow;
    String rSF, SF_Code,divCode;
    CommonUtilsMethods commonUtilsMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_submenu);
        commonUtilsMethods = new CommonUtilsMethods(this);
        toolbar_title = findViewById(R.id.toolbar_title);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {

                finish();
            }
        });

        String title = getIntent().getStringExtra("title");
        String menu_sub_details = getIntent().getStringExtra("menu_sub_details");

        toolbar_title.setText(title);
        dynamicSubMenuAdapter = new DynamicSubMenuAdapter( subMenuModelArrayList, this);
        SF_Code = SharedPref.getSfCode(this);
        divCode = SharedPref.getDivisionCode(this);
        rSF     = SharedPref.getSfCode(this);
        if(UtilityClass.isNetworkAvailable(this)) {
            prepare_menu_sub_details(menu_sub_details);
        }else{
            commonUtilsMethods.showToastMessage(DynamicSubMenuActivity.this, getString(R.string.no_network));
        }
    }

//    @SuppressLint({"Range", "NotifyDataSetChanged"})
//    private void prepare_menu_sub_details(String menu_sub_details) {
//        try {
//            JSONArray jsonArray = new JSONArray(menu_sub_details);
//            if (jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject menuSubObject = jsonArray.getJSONObject(i);
//                    String date = CommonUtilsMethods.getCurrentInstance("yyyy-mm-dd");
//                    String mnth;
//                    if (date.substring(5, 6).equalsIgnoreCase("0"))
//                        mnth = date.substring(6, 7);
//                    else
//                        mnth = date.substring(5, 7);
//                    String formatted_URL =SharedPref.getTagImageUrl(this) + "/" + menuSubObject.optString("OptionMenu_Page")+ "?";
//                    formatted_URL = formatted_URL + "sfcode=" + SF_Code + "&" + "rSF=" + rSF + "&" + "div_code=" +
//                            divCode.replace(",", "") + "&" + "cMnth=" +
//                            mnth + "&" + "cYr=" + date.substring(0, 4)+ "&doc_id=-1&IsDocView=0&cluster_code=-1";
//
//                    SubMenuModel menuSubModel = new SubMenuModel(menuSubObject.optString("OptionMenu_Id"), menuSubObject.optString("OptionMenu_Name"), formatted_URL);
//                    subMenuModelArrayList.add(menuSubModel);
//                }
//                dynamicSubMenuAdapter = new DynamicSubMenuAdapter(subMenuModelArrayList,this);
//                recyclerView.setAdapter(dynamicSubMenuAdapter);
//                dynamicSubMenuAdapter.notifyDataSetChanged();
//            } else {
//                commonUtilsMethods.showToastMessage(DynamicSubMenuActivity.this,"No Record Found");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
@SuppressLint({"Range", "NotifyDataSetChanged"})
private void prepare_menu_sub_details(String menu_sub_details) {
    try {
        if (menu_sub_details == null || menu_sub_details.trim().equals("") || menu_sub_details.trim().equalsIgnoreCase("null")) {
            commonUtilsMethods.showToastMessage(DynamicSubMenuActivity.this, "No submenu data available");
            return;
        }

        JSONArray jsonArray = new JSONArray(menu_sub_details);
        if (jsonArray.length() > 0) {
            subMenuModelArrayList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject menuSubObject = jsonArray.getJSONObject(i);
                String year = TimeUtils.FORMAT_10;
                String month = TimeUtils.FORMAT_8;

                String formatted_URL = SharedPref.getTagImageUrl(this) + "/" + menuSubObject.optString("OptionMenu_Page") + "?";
                formatted_URL += "sfcode=" + SF_Code + "&rSF=" + rSF + "&div_code=" + divCode.replace(",", "") +
                        "&cMnth=" + month + "&cYr=" + year +
                        "&doc_id=-1&IsDocView=0&cluster_code=-1";

                SubMenuModel menuSubModel = new SubMenuModel(
                        menuSubObject.optString("OptionMenu_Id"),
                        menuSubObject.optString("OptionMenu_Name"),
                        formatted_URL
                );
                subMenuModelArrayList.add(menuSubModel);
            }

            dynamicSubMenuAdapter = new DynamicSubMenuAdapter(subMenuModelArrayList, this);
            recyclerView.setAdapter(dynamicSubMenuAdapter);
            dynamicSubMenuAdapter.notifyDataSetChanged();
        } else {
            commonUtilsMethods.showToastMessage(DynamicSubMenuActivity.this, "No Record Found");
        }

    } catch (JSONException e) {
        e.printStackTrace();
        commonUtilsMethods.showToastMessage(DynamicSubMenuActivity.this, "Invalid submenu data format");
    }
}


}
