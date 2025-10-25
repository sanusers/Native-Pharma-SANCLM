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

public class birthdaywishes_fragment extends Fragment {

    private RecyclerView birthList;
    private ConstraintLayout constraintNoData;
    private ArrayList<birthdayModel> birthdayList = new ArrayList<>();
    private birthdayAdapter birthdayAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

//    private final String[] backColors = {
//            "#FCE8EC", "#FCF8E8", "#FCF6E8", "#E8F9FC",
//            "#FCEDFB", "#FAFAED", "#EEE3F7", "#FEF8F8"
//    };

    //public birthdaywishes_fragment() { }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_birthdaywishes, container, false);

        birthList = view.findViewById(R.id.birthList);
        constraintNoData = view.findViewById(R.id.constraint_no_data);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        loadBirthdayData();
        return view;
    }

//    private void loadBirthdayData() {
//        try {
//            JSONArray doctorJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS).getMasterSyncDataJsonArray();
//
//            birthdayList.clear();
//
//            for (int i = 0; i < doctorJsonArray.length(); i++) {
//                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
//                String doctorName = doctorObj.getString("Name");
//                String birthDate = doctorObj.optString("Birthday", "");
//
//                if (!birthDate.isEmpty()) {
//                    String color = backColors[i % backColors.length];
//                    birthdayList.add(new birthdayModel(doctorName, birthDate, color));
//                }
//            }
//
//            // Setup RecyclerView
//            birthdayAdapter = new birthdayAdapter(birthdayList, getActivity());
//            birthdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            birthdayRecyclerView.setAdapter(birthdayAdapter);
//            birthdayAdapter.notifyDataSetChanged();
//
//            // Show "No Data" if list is empty
//            if (birthdayList.isEmpty()) {
//                birthdayRecyclerView.setVisibility(View.GONE);
//                constraintNoData.setVisibility(View.VISIBLE);
//            } else {
//                birthdayRecyclerView.setVisibility(View.VISIBLE);
//                constraintNoData.setVisibility(View.GONE);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("BirthdayFragment", "Error loading doctor birthdays", e);
//            birthdayRecyclerView.setVisibility(View.GONE);
//            constraintNoData.setVisibility(View.VISIBLE);
//        }
//    }
private void loadBirthdayData() {
    try {
        JSONArray doctorJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS+ SharedPref.getHqCode(requireContext())).getMasterSyncDataJsonArray();
        Log.d("BirthdayFragment", "Raw JSON from DB: " + doctorJsonArray.toString());
        birthdayList.clear();

        for (int i = 0; i < doctorJsonArray.length(); i++) {
            JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
            String doctorName = doctorObj.getString("Name");

            // Check DctrDOB
            JSONObject dobObject = doctorObj.optJSONObject("DctrDOB");
            String birthDate = "";
            if (dobObject != null) {
                String dobDateStr = dobObject.optString("date", "");
                // Show only if not empty
                if (!dobDateStr.isEmpty()) {
                    birthDate = dobDateStr.split(" ")[0];  // Take only YYYY-MM-DD
                }
            }

            // For testing: include even if birthDate is 1900-01-01
           // String color = backColors[i % backColors.length];
            birthdayList.add(new birthdayModel(doctorName, birthDate));
        }
        Log.d("BirthdayFragment", "Birthday list size: " + birthdayList.size());

        // Setup RecyclerView
        birthdayAdapter = new birthdayAdapter(birthdayList, getActivity());
        birthList.setLayoutManager(new LinearLayoutManager(getActivity()));
        birthList.setAdapter(birthdayAdapter);
        birthdayAdapter.notifyDataSetChanged();

        // Show No Data if list empty
        if (birthdayList.isEmpty()) {
            birthList.setVisibility(View.GONE);
            constraintNoData.setVisibility(View.VISIBLE);
        } else {
            birthList.setVisibility(View.VISIBLE);
            constraintNoData.setVisibility(View.GONE);
        }

    } catch (Exception e) {
        e.printStackTrace();
        birthList.setVisibility(View.GONE);
        constraintNoData.setVisibility(View.VISIBLE);
    }
}


}
