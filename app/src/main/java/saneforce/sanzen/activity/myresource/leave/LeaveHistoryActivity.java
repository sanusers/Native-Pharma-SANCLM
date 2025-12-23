//package saneforce.sanzen.activity.myresource.leave;
//
//import android.os.Bundle;
//import android.widget.ImageView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import saneforce.sanzen.R;
//
//public class LeaveHistoryActivity extends AppCompatActivity {
//
//    ImageView ivBack;
//    RecyclerView rvLeaveHistory;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_leave_history);
//        ivBack = findViewById(R.id.iv_back);
//        rvLeaveHistory = findViewById(R.id.rvLeaveHistory);
//        ivBack.setOnClickListener(v -> finish());
//        rvLeaveHistory.setLayoutManager(new LinearLayoutManager(this));
//
//    }
//}
package saneforce.sanzen.activity.myresource.leave;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class LeaveHistoryActivity extends AppCompatActivity {

    RecyclerView rvLeaveHistory;
    ImageView ivBack;
    MasterDataDao masterDataDao;
    private RoomDB roomDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_history);
        ivBack = findViewById(R.id.iv_back);
        rvLeaveHistory = findViewById(R.id.rvLeaveHistory);
        ivBack.setOnClickListener(v -> finish());
        rvLeaveHistory.setLayoutManager(new LinearLayoutManager(this));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE).getMasterSyncDataJsonArray();
        List<LeaveHistoryModel> leaveList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    String leaveSName = obj.optString("Leave_SName", "");
                    String leaveName = obj.optString("Leave_Name", "");
                    String leaveType = leaveSName + " - " + leaveName;
                   // String status= obj.optString("Status","");

//                    String createdDate = "";
//                    JSONObject createdObj = obj.optJSONObject("Created_Date");
//                    if (createdObj != null) {
//                        createdDate = createdObj.optString("date", "");
//                    }


                    String createdDate = "";
                    JSONObject createdObj = obj.optJSONObject("Created_Date");
                    if (createdObj != null) {
                        String dateStr = createdObj.optString("date", "");


                        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.ENGLISH);


                        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
                        try {
                            Date date = apiFormat.parse(dateStr);
                            if (date != null) {
                                createdDate = displayFormat.format(date);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            createdDate = dateStr;
                        }
                    }


                    leaveList.add(new LeaveHistoryModel(leaveType, createdDate));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        LeaveHistoryAdapter adapter = new LeaveHistoryAdapter(this, leaveList);
        rvLeaveHistory.setAdapter(adapter);
    }
}
        // Dummy data
//        List<LeaveHistoryModel> list = new ArrayList<>();
//
//        list.add(new LeaveHistoryModel(
//                "05 Dec 2025",
//                "09 Dec 2025",
//                "SL - Sick Leave",
//                "Dec 4 2025 12:28PM",
//                "sick",
//                "sdadadsfsdc",
//                "Rejected",
//                5
//        ));
//
//        list.add(new LeaveHistoryModel(
//                "05 Dec 2025",
//                "09 Dec 2025",
//                "SL - Sick Leave",
//                "Dec 4 2025 12:42PM",
//                "bgsgtd", "", "Pending", 5
//        ));

        // 👉 Adapter USED HERE
//        LeaveHistoryAdapter adapter = new LeaveHistoryAdapter(this, list);
//
//        rvLeaveHistory.setAdapter(adapter);

