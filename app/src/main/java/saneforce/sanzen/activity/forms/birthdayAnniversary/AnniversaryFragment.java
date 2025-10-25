package saneforce.sanzen.activity.forms.birthdayAnniversary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.storage.SharedPref;

public class AnniversaryFragment extends Fragment {

    private RecyclerView anniversayList;
    private ConstraintLayout constraintNoData;
    private ArrayList<anniversaryModel> anniversaryList = new ArrayList<>();
    private anniversaryAdapter anniversaryAdapter;
    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

//    private final String[] backColors = {
//            "#FCE8EC", "#FCF8E8", "#FCF6E8", "#E8F9FC",
//            "#FCEDFB", "#FAFAED", "#EEE3F7", "#FEF8F8"
//    };

    public AnniversaryFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_anniversary_fragment, container, false);

        anniversayList = view.findViewById(R.id.anniversaryList);
        constraintNoData = view.findViewById(R.id.constraint_no_data);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();

        loadAnniversaryData();

        return view;
    }

    private void loadAnniversaryData() {
        try {
            JSONArray doctorJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext())).getMasterSyncDataJsonArray();

            Log.d("AnniversaryFragment", "Raw JSON from DB: " + doctorJsonArray.toString());
            anniversaryList.clear();

            for (int i = 0; i < doctorJsonArray.length(); i++) {
                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
                String doctorName = doctorObj.optString("Name");

                // Extract Anniversary date (DctrDOW)
                JSONObject annObject = doctorObj.optJSONObject("DctrDOW");
                String anniversaryDate = "";
               // Anniversary
                if (annObject != null) {
                    String annDateStr = annObject.optString("date", "");
                    if (!annDateStr.isEmpty()) {
                        anniversaryDate = annDateStr.split(" ")[0]; // Only YYYY-MM-DD
                    }
                }


//                String color = backColors[i % backColors.length];
                anniversaryList.add(new anniversaryModel(doctorName, anniversaryDate));
            }
            Log.d("BirthdayFragment", "Birthday list size: " + anniversaryList.size());
            // Setup RecyclerView
            anniversaryAdapter = new anniversaryAdapter(anniversaryList, getActivity());
            anniversayList.setLayoutManager(new LinearLayoutManager(getActivity()));
            anniversayList.setAdapter(anniversaryAdapter);
            anniversaryAdapter.notifyDataSetChanged();

            // Show or hide "No Data"
            if (anniversaryList.isEmpty()) {
                anniversayList.setVisibility(View.GONE);
                constraintNoData.setVisibility(View.VISIBLE);
            } else {
                anniversayList.setVisibility(View.VISIBLE);
                constraintNoData.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            anniversayList.setVisibility(View.GONE);
            constraintNoData.setVisibility(View.VISIBLE);
        }
    }
}

//package saneforce.sanzen.activity.forms.birthdayAnniversary;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//import saneforce.sanzen.R;
//import saneforce.sanzen.commonClasses.CommonUtilsMethods;
//import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
//import saneforce.sanzen.roomdatabase.RoomDB;
//import saneforce.sanzen.commonClasses.Constants;
//
//public class anniversary_fragment extends Fragment {
//
//    private RecyclerView anniversaryList;
//    private ConstraintLayout constraintNoData;
//    private ArrayList<birthdayModel> anniversaryList = new ArrayList<>();
//    private anniversaryAdapter anniversaryAdapter;
//    private CommonUtilsMethods commonUtilsMethods;
//    private RoomDB roomDB;
//    private MasterDataDao masterDataDao;
//
//    private final String[] backColors = {
//            "#FCE8EC", "#FCF8E8", "#FCF6E8", "#E8F9FC",
//            "#FCEDFB", "#FAFAED", "#EEE3F7", "#FEF8F8"
//    };
//
//    public anniversary_fragment() {
//    }
//
//    @SuppressLint("MissingInflatedId")
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_anniversary_fragment, container, false);
//
//        anniversaryList = view.findViewById(R.id.anniversaryList);
//        constraintNoData = view.findViewById(R.id.constraint_no_data);
//        commonUtilsMethods = new CommonUtilsMethods(requireContext());
//        commonUtilsMethods.setUpLanguage(requireContext());
//        roomDB = RoomDB.getDatabase(requireContext());
//        masterDataDao = roomDB.masterDataDao();
//
//        loadAnniversaryData();
//
//        return view;
//    }
//
//    private void loadAnniversaryData() {
//        try {
//            JSONArray doctorJsonArray = masterDataDao
//                    .getMasterDataTableOrNew(Constants.DOCTOR_MAS)
//                    .getMasterSyncDataJsonArray();
//
//            Log.d("AnniversaryFragment", "Raw JSON from DB: " + doctorJsonArray.toString());
//
//            anniversaryList.clear();
//
//            for (int i = 0; i < doctorJsonArray.length(); i++) {
//                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
//                String doctorName = doctorObj.optString("Name", "Unknown");
//
//                // Birthday
//                JSONObject dobObject = doctorObj.optJSONObject("DctrDOB");
//                String anniversaryDate = "";
//                if (dobObject != null) {
//                    String dowDateStr = dobObject.optString("date", "");
//                    if (!dowDateStr.isEmpty() && !dowDateStr.startsWith("1900")) {
//                        anniversaryDate = dowDateStr.split(" ")[0];
//                    }
//                }
//
//                // Anniversary
//                JSONObject dowObject = doctorObj.optJSONObject("DctrDOW");
//                String anniversaryDate = "";
//                if (dowObject != null) {
//                    String dowDateStr = dowObject.optString("date", "");
//                    if (!dowDateStr.isEmpty() && !dowDateStr.startsWith("1900")) {
//                        anniversaryDate = dowDateStr.split(" ")[0];
//                    }
//                }
//
//                // Skip doctors without valid anniversary
////                if (anniversaryDate.isEmpty()) continue;
////
////                String color = backColors[i % backColors.length];
////                anniversaryList.add(new birthdayModel(doctorName, birthDate, color, anniversaryDate));
//            }
//
//                // Setup RecyclerView
//                anniversaryAdapter = new anniversaryAdapter(anniversaryList, getActivity());
//            anniversaryList.setLayoutManager(new LinearLayoutManager(getActivity()));
//            anniversaryList.setAdapter(anniversaryAdapter);
//                anniversaryAdapter.notifyDataSetChanged();
//
//                if (anniversaryList.isEmpty()) {
//                    anniversaryList.setVisibility(View.GONE);
//                    constraintNoData.setVisibility(View.VISIBLE);
//                } else {
//                    anniversaryList.setVisibility(View.VISIBLE);
//                    constraintNoData.setVisibility(View.GONE);
//                }
//
//            } catch(Exception e){
//                e.printStackTrace();
//            anniversaryList.setVisibility(View.GONE);
//                constraintNoData.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
