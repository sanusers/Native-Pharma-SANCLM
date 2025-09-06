/*
package saneforce.sanzen.activity.reports.visitMonitor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityVisitMonitorBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;


public class VisitMonitorActivity extends AppCompatActivity  {
    ActivityVisitMonitorBinding binding;
    LinearLayout backArrow;
    TextView title, note,self,live;
    EditText search;
    FrameLayout doctor_fragment_container;

    DoctorFragment doctorFragment;

    MasterDataDao masterDataDao;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityVisitMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = binding.title;
        note  = binding.tvNote;
        self  = binding.self;
        live  = binding.live;
        backArrow = binding.backArrow;
        backArrow.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        live.setOnClickListener(view -> {
        });

        RoomDB roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();

        try {
            JSONArray jsonArray_call = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            JSONArray jsonArray_date = new JSONArray(masterDataDao.getDataByKey(Constants.DATE_SYNC));

            Set<String> rejectedDates = new HashSet<>();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < jsonArray_date.length(); i++) {
                JSONObject dateObj = jsonArray_date.getJSONObject(i);
                String flg = dateObj.optString("flg", "");
                if ("0".equals(flg)) {
                    continue;
                }
                String fullDate = dateObj.getJSONObject("dt").getString("date");
                Date parsedDate = inputFormat.parse(fullDate);
                String formattedDate = outputFormat.format(parsedDate);
                rejectedDates.add(formattedDate);
            }

            JSONArray filteredCalls = new JSONArray();
            for (int i = 0; i < jsonArray_call.length(); i++) {
                JSONObject callObj = jsonArray_call.getJSONObject(i);
                String callDate = callObj.getString("Dcr_dt");
                if (!rejectedDates.contains(callDate)) {
                    filteredCalls.put(callObj);
                }
            }
            System.out.println(filteredCalls);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
*/


package saneforce.sanzen.activity.reports.visitMonitor;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityVisitMonitorBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class VisitMonitorActivity extends AppCompatActivity  {
    ActivityVisitMonitorBinding binding;
    LinearLayout backArrow;
    TextView title, note,self,live;
    EditText search;
    FrameLayout doctor_fragment_container; // Ensure this is present in your XML

    DoctorFragment doctorFragment;
    MasterDataDao masterDataDao;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityVisitMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = binding.title;
        note  = binding.tvNote;
        self  = binding.self;
        live  = binding.live;
        search = binding.searchCust;
        backArrow = binding.backArrow;
        doctor_fragment_container = binding.doctorFragmentContainer;

        backArrow.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        live.setOnClickListener(view -> {
            // Your logic for the 'live' button click
        });
        if(SharedPref.getSfType(VisitMonitorActivity.this).equalsIgnoreCase("2")){
            search.setVisibility(View.VISIBLE);
        }else{
            search.setVisibility(View.GONE);
        }

        // Add the DoctorFragment to the FrameLayout
        if (savedInstanceState == null) {
            doctorFragment = new DoctorFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.doctor_fragment_container, doctorFragment);
            fragmentTransaction.commit();
        }

        RoomDB roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();

        try {
            JSONArray jsonArray_call = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            JSONArray jsonArray_date = new JSONArray(masterDataDao.getDataByKey(Constants.DATE_SYNC));

            Set<String> rejectedDates = new HashSet<>();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < jsonArray_date.length(); i++) {
                JSONObject dateObj = jsonArray_date.getJSONObject(i);
                String flg = dateObj.optString("flg", "");
                if ("0".equals(flg)) {
                    continue;
                }
                String fullDate = dateObj.getJSONObject("dt").getString("date");
                Date parsedDate = inputFormat.parse(fullDate);
                String formattedDate = outputFormat.format(parsedDate);
                rejectedDates.add(formattedDate);
            }

            JSONArray filteredCalls = new JSONArray();
            for (int i = 0; i < jsonArray_call.length(); i++) {
                JSONObject callObj = jsonArray_call.getJSONObject(i);
                String callDate = callObj.getString("Dcr_dt");
                if (!rejectedDates.contains(callDate)) {
                    filteredCalls.put(callObj);
                }
            }
            System.out.println(filteredCalls);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
