package saneforce.sanzen.activity.myresource.Categoryview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.icu.util.LocaleData;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.callstatusview.call_statusadapter;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.activity.myresource.myresourceadapter.DateSyncAdapter;
import saneforce.sanzen.activity.myresource.myresourcemodel.DateSyncModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityDateSyncBinding;
import saneforce.sanzen.databinding.ActivityMyResourceBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class DateSyncActivity extends AppCompatActivity {
    private ActivityDateSyncBinding dateSyncBinding;
    ArrayList<DateSyncModel> dateSyncModel = new ArrayList<>();
    DateSyncAdapter dateSyncAdapter;

    RoomDB roomDB;
    MasterDataDao masterDataDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateSyncBinding = ActivityDateSyncBinding.inflate(getLayoutInflater());
        setContentView(dateSyncBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        initialization();

    }
    private void initialization(){
        roomDB= RoomDB.getDatabase(this);
        masterDataDao=roomDB.masterDataDao();
        dateSyncData();
        onClickListener();
    }
    private void onClickListener(){
        dateSyncBinding.backArrow.setOnClickListener(v -> {finish();});
    }
    private void dateSyncData(){
        try {
            JSONArray dateSyncList = new JSONArray(masterDataDao.getDataByKey(Constants.DATE_SYNC));
            if (dateSyncList.length()>0){
                for (int bean =0; bean<dateSyncList.length();bean++){
                    JSONObject dateSyncObject = dateSyncList.getJSONObject(bean);
                    String sfCode = dateSyncObject.getString("Sf_Code");
                    String flg    = dateSyncObject.getString("flg");
                    String tbName  = dateSyncObject.getString("tbname");
                    String reason  = dateSyncObject.getString("reason");
                    JSONObject dateObject = dateSyncObject.getJSONObject("dt");
                    String date = dateObject.getString("date");

                    dateSyncModel.add(new DateSyncModel(sfCode,flg,tbName,reason,date));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        adapterSetUp();

    }
    private void adapterSetUp(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        dateSyncBinding.dateSyncRecyclerView.setLayoutManager(layoutManager);
        dateSyncAdapter = new DateSyncAdapter(this,dateSyncModel);
        dateSyncBinding.dateSyncRecyclerView.setAdapter(dateSyncAdapter);
    }
}