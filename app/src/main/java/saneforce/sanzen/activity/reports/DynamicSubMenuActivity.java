package saneforce.sanzen.activity.reports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.adapter.DynamicAdapter;
import saneforce.sanzen.activity.reports.dayReport.adapter.DynamicSubMenuAdapter;
import saneforce.sanzen.activity.reports.dayReport.model.SubMenuModel;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DynamicSubMenuActivity extends AppCompatActivity {
    ArrayList<SubMenuModel> subMenuModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    DynamicSubMenuAdapter dynamicSubMenuAdapter;
    TextView toolbar_title;
    LinearLayout backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_submenu);

        toolbar_title = findViewById(R.id.toolbar_title);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(v -> {
            //show_exit_alert();
            finish();
        });

        String title = getIntent().getStringExtra("title");
        String menu_sub_details = getIntent().getStringExtra("menu_sub_details");

        toolbar_title.setText(title);
        dynamicSubMenuAdapter = new DynamicSubMenuAdapter( subMenuModelArrayList, this);

        prepare_menu_sub_details(menu_sub_details);
    }

    @SuppressLint({"Range", "NotifyDataSetChanged"})
    private void prepare_menu_sub_details(String menu_sub_details) {
        try{

//            String rSF;
//            if(!CustomerMeList.get(0).getDesigCode().equalsIgnoreCase("MR")){
//                rSF = SharedPref.getSfCode(this);
//            }else{
//                rSF = "-1";
//            }


            JSONArray jsonArray = new JSONArray(menu_sub_details);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject menuSubObject = jsonArray.getJSONObject(i);
                    String formatted_URL = SharedPref.getBaseWebUrl(DynamicSubMenuActivity.this) + "/" + menuSubObject.optString("OptionMenu_Page")+ "?";
//                    formatted_URL = formatted_URL + "sfcode=" + CustomerMeList.get(0).getSFCode() + "&" + "rSF=" + rSF + "&" + "div_code=" +
//                            CustomerMeList.get(0).getDivisionCode().replace(",", "") + "&" + "cMnth=" +
//                            TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_9) + "&" + "cYr=" + TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_12)+ "&doc_id=-1&IsDocView=0&cluster_code=-1";

                    SubMenuModel menuSubModel = new SubMenuModel(menuSubObject.optString("OptionMenu_Id"), menuSubObject.optString("OptionMenu_Name"), formatted_URL);
                    subMenuModelArrayList.add(menuSubModel);
                }
                dynamicSubMenuAdapter = new DynamicSubMenuAdapter(subMenuModelArrayList,DynamicSubMenuActivity.this);
                recyclerView.setAdapter(dynamicSubMenuAdapter);
                dynamicSubMenuAdapter.notifyDataSetChanged();
            } else {
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_record_found), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


}
