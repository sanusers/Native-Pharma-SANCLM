package saneforce.sanzen.activity.myresource.profile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;

import org.json.JSONArray;

import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityProfileViewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;


public class ProfileViewScreen extends AppCompatActivity {
    public ActivityProfileViewBinding activityProfileViewScreenBinding;
    MasterDataDao masterDataDao;
    private RoomDB roomDB;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityProfileViewScreenBinding = ActivityProfileViewBinding.inflate(getLayoutInflater());
        setContentView(activityProfileViewScreenBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();

//        MasterDataTable profile = masterDataDao.getMasterDataTableOrNew(Constants.PROFILE);
        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.PROFILE).getMasterSyncDataJsonArray();



        activityProfileViewScreenBinding.ivBack.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


    }

}
