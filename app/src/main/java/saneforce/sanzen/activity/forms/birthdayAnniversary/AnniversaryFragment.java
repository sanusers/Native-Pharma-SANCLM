package saneforce.sanzen.activity.forms.birthdayAnniversary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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

    //private ListView anniversayList;
    private ListView listToday;
    private ListView listUpcoming;
    private ListView listBelated;
    private ConstraintLayout constraintNoData;
    TextView txtTodayNoData, txtUpcomingNoData, txtBelatedNoData;


    //private ArrayList<anniversaryModel> anniversaryList = new ArrayList<>();
    private ArrayList<anniversaryModel> todayList = new ArrayList<>();
    private ArrayList<anniversaryModel> upcomingList = new ArrayList<>();
    private ArrayList<anniversaryModel> belatedList = new ArrayList<>();

    //private anniversaryAdapter anniversaryAdapter;
//    private anniversaryAdapter adapterToday;
//    private anniversaryAdapter adapterUpcoming;
//    private anniversaryAdapter adapterBelated;

    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

//    private final String[] backColors = {
//            "#FCE8EC", "#FCF8E8", "#FCF6E8", "#E8F9FC",
//            "#FCEDFB", "#FAFAED", "#EEE3F7", "#FEF8F8"
//    };

//    public AnniversaryFragment() {
//    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_anniversary_fragment, container, false);

        //anniversayList = view.findViewById(R.id.anniversaryList);
        listToday = view.findViewById(R.id.list_today);
        listUpcoming = view.findViewById(R.id.list_upcoming);
        listBelated = view.findViewById(R.id.list_belated);
        constraintNoData = view.findViewById(R.id.constraint_no_data);
        txtTodayNoData = view.findViewById(R.id.txtTodayNoData);
        txtUpcomingNoData = view.findViewById(R.id.txtUpcomingNoData);
        txtBelatedNoData = view.findViewById(R.id.txtBelatedNoData);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        loadAnniversaryData();
        setListViewHeightBasedOnChildren(listToday);
        setListViewHeightBasedOnChildren(listUpcoming);
        setListViewHeightBasedOnChildren(listBelated);
        return view;
    }
    private void loadAnniversaryData() {
        try {
            JSONArray doctorJsonArray = masterDataDao
                    .getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext()))
                    .getMasterSyncDataJsonArray();

            todayList.clear();
            upcomingList.clear();
            belatedList.clear();

            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMM d", java.util.Locale.US);

            // ✅ Current day reference (normalized year = 2000)
            java.util.Calendar today = java.util.Calendar.getInstance();
            today.set(java.util.Calendar.YEAR, 2000);

            // ✅ ±3 day range
            java.util.Calendar start = (java.util.Calendar) today.clone();
            java.util.Calendar end = (java.util.Calendar) today.clone();
            start.add(java.util.Calendar.DATE, -3);
            end.add(java.util.Calendar.DATE, 3);

            for (int i = 0; i < doctorJsonArray.length(); i++) {
                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
                String doctorName = doctorObj.optString("Name");
                String Code = doctorObj.optString("Code");
                String territory = doctorObj.optString("Town_Name");
                String qualification = doctorObj.optString("DrDesig");
                String category = doctorObj.optString("Category");
                String speciality = doctorObj.optString("Specialty");
                String className = doctorObj.optString("Doc_Class_ShortName");

                JSONObject annObject = doctorObj.optJSONObject("DctrDOW");
                if (annObject == null) continue;

                String annDateStr = annObject.optString("date", "").trim();
                if (annDateStr.isEmpty() || annDateStr.startsWith("1900")) continue;

                try {
                    java.util.Date parsedDate = inputFormat.parse(annDateStr.split(" ")[0]);
                    java.util.Calendar ann = java.util.Calendar.getInstance();
                    ann.setTime(parsedDate);

                    // ✅ Normalize year for proper comparison
                    ann.set(java.util.Calendar.YEAR, 2000);

                    String displayDate = outputFormat.format(parsedDate);
                    anniversaryModel model = new anniversaryModel(
                            doctorName, displayDate, Code, territory,
                            qualification, category, speciality, className
                    );

                    // ✅ Check if Anniversary is within the ±3-day range
                    boolean inRange;
                    if (start.after(end)) {
                        // year wrap (e.g., Dec–Jan)
                        inRange = (!ann.before(start) || !ann.after(end));
                    } else {
                        inRange = (!ann.before(start) && !ann.after(end));
                    }

                    if (ann.get(java.util.Calendar.MONTH) == today.get(java.util.Calendar.MONTH)
                            && ann.get(java.util.Calendar.DAY_OF_MONTH) == today.get(java.util.Calendar.DAY_OF_MONTH)) {
                        todayList.add(model);
                    } else if (inRange && ann.after(today)) {
                        upcomingList.add(model);
                    } else if (inRange && ann.before(today)) {
                        belatedList.add(model);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.d("AnniversaryFragment", "Today: " + todayList.size() +
                    " | Upcoming: " + upcomingList.size() +
                    " | Belated: " + belatedList.size());

            anniversaryAdapter todayAdapter = new anniversaryAdapter(todayList, getActivity());
            anniversaryAdapter upcomingAdapter = new anniversaryAdapter(upcomingList, getActivity());
            anniversaryAdapter belatedAdapter = new anniversaryAdapter(belatedList, getActivity());

            listToday.setAdapter(todayAdapter);
            listUpcoming.setAdapter(upcomingAdapter);
            listBelated.setAdapter(belatedAdapter);

            todayAdapter.notifyDataSetChanged();
            upcomingAdapter.notifyDataSetChanged();
            belatedAdapter.notifyDataSetChanged();


            if (todayList.isEmpty()) {
                txtTodayNoData.setVisibility(View.VISIBLE);
            } else {
                txtTodayNoData.setVisibility(View.GONE);
            }

            if (upcomingList.isEmpty()) {
                txtUpcomingNoData.setVisibility(View.VISIBLE);
            } else {
                txtUpcomingNoData.setVisibility(View.GONE);
            }

            if (belatedList.isEmpty()) {
                txtBelatedNoData.setVisibility(View.VISIBLE);
            } else {
                txtBelatedNoData.setVisibility(View.GONE);
            }


            // ✅ Visibility handling
            if (todayList.isEmpty() && upcomingList.isEmpty() && belatedList.isEmpty()) {
                constraintNoData.setVisibility(View.VISIBLE);
                listToday.setVisibility(View.GONE);
                listUpcoming.setVisibility(View.GONE);
                listBelated.setVisibility(View.GONE);
            } else {
                constraintNoData.setVisibility(View.GONE);
                listToday.setVisibility(View.VISIBLE);
                listUpcoming.setVisibility(View.VISIBLE);
                listBelated.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            constraintNoData.setVisibility(View.VISIBLE);
            listToday.setVisibility(View.GONE);
            listUpcoming.setVisibility(View.GONE);
            listBelated.setVisibility(View.GONE);
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        // ✅ If list has no items, make height minimal (remove big blank space)
        if (listAdapter.getCount() == 0) {
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = 1; // minimum height
            listView.setLayoutParams(params);

            // also remove any margins
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) params).topMargin = 0;
                ((ViewGroup.MarginLayoutParams) params).bottomMargin = 0;
            }

            listView.requestLayout();
            return;
        }

        // ✅ Calculate height normally if items are present
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.UNSPECIFIED
            );
            totalHeight += listItem.getMeasuredHeight()-requireContext().getResources().getDimension(R.dimen._10sdp);
        }

       // int dividerTotal = listView.getDividerHeight() * Math.max(0, listAdapter.getCount() - 1);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = Math.max(0, totalHeight );
        listView.setLayoutParams(params);

        // ✅ Remove extra top/bottom space between sections
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).topMargin = 0;
            ((ViewGroup.MarginLayoutParams) params).bottomMargin = 0;
        }

        listView.setPadding(0, 0, 0, 0);
        listView.setClipToPadding(false);
        listView.requestLayout();
    }
}

//    private void loadAnniversaryData() {
//        try {
//            JSONArray doctorJsonArray = masterDataDao
//                    .getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext()))
//                    .getMasterSyncDataJsonArray();
//
//            Log.d("AnniversaryFragment", "Raw JSON from DB: " + doctorJsonArray.toString());
//            anniversaryList.clear();
//
//            // ✅ Date setup
//            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
//            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMMM d", java.util.Locale.US);
//
//            java.util.Calendar today = java.util.Calendar.getInstance();
//            today.set(java.util.Calendar.YEAR, 2000);
//            java.util.Calendar start = (java.util.Calendar) today.clone();
//            java.util.Calendar end = (java.util.Calendar) today.clone();
//            start.add(java.util.Calendar.DATE, -3);
//            end.add(java.util.Calendar.DATE, 3);
//
//            // ✅ Loop through all doctors
//            for (int i = 0; i < doctorJsonArray.length(); i++) {
//                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
//                String doctorName = doctorObj.optString("Name");
//                String Code = doctorObj.optString("Code");
//                String territory = doctorObj.optString("Town_Name");
//                String qualification = doctorObj.optString("DrDesig");
//                String category = doctorObj.optString("Category");
//                String speciality = doctorObj.optString("Specialty");
//                String className = doctorObj.optString("Doc_Class_ShortName");
//
//                JSONObject annObject = doctorObj.optJSONObject("DctrDOW");
//                String anniversaryDate = "";
//
//                if (annObject != null) {
//                    String annDateStr = annObject.optString("date", "").trim();
//
//                    // ✅ Skip invalid or empty dates
//                    if (!annDateStr.isEmpty() && !annDateStr.startsWith("1900")) {
//                        try {
//                            java.util.Date date = inputFormat.parse(annDateStr.split(" ")[0]);
//                            java.util.Calendar ann = java.util.Calendar.getInstance();
//                            ann.setTime(date);
//                            ann.set(java.util.Calendar.YEAR, 2000);
//
//                            // ✅ Check within ±3 days (with Dec–Jan wrap)
//                            boolean inRange;
//                            if (start.after(end)) {
//                                // Handle year wrap (e.g. Dec 30–Jan 2)
//                                inRange = (!ann.before(start) || !ann.after(end));
//                            } else {
//                                inRange = (!ann.before(start) && !ann.after(end));
//                            }
//
//                            if (inRange) {
//                                anniversaryDate = outputFormat.format(date);
//                                anniversaryList.add(new anniversaryModel(
//                                        doctorName, anniversaryDate, Code, territory,
//                                        qualification, category, speciality, className));
//                            }
//
//                        } catch (Exception ignored) {
//                            // ignore parse errors safely
//                        }
//                    }
//                }
//            }
//
//            Log.d("AnniversaryFragment", "Filtered Anniversary list size: " + anniversaryList.size());
//
//            // ✅ Recycler setup
//            anniversaryAdapter = new anniversaryAdapter(anniversaryList, getActivity());
//           // anniversayList.setLayoutManager(new LinearLayoutManager(getActivity()));
//            anniversayList.setAdapter(anniversaryAdapter);
//            //anniversayList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//            anniversaryAdapter.notifyDataSetChanged();
//
//            // ✅ Handle No Data
//            if (anniversaryList.isEmpty()) {
//                anniversayList.setVisibility(View.GONE);
//                constraintNoData.setVisibility(View.VISIBLE);
//            } else {
//                anniversayList.setVisibility(View.VISIBLE);
//                constraintNoData.setVisibility(View.GONE);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            anniversayList.setVisibility(View.GONE);
//            constraintNoData.setVisibility(View.VISIBLE);
//        }
//    }
//    private void loadAnniversaryData() {
//        try {
//            JSONArray doctorJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext())).getMasterSyncDataJsonArray();
//
//            Log.d("AnniversaryFragment", "Raw JSON from DB: " + doctorJsonArray.toString());
//            anniversaryList.clear();
//
//            for (int i = 0; i < doctorJsonArray.length(); i++) {
//                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
//                String doctorName = doctorObj.optString("Name");
//                String Code = doctorObj.getString("Code");
//                String territory = doctorObj.getString("Town_Name");
//                String qualification = doctorObj.getString("DrDesig");
//                String category = doctorObj.getString("Category");
//                String speciality = doctorObj.getString("Specialty");
//                String className = doctorObj.getString("Doc_Class_ShortName");
//
//                // Extract Anniversary date (DctrDOW)
//                JSONObject annObject = doctorObj.optJSONObject("DctrDOW");
//                String anniversaryDate = "";
//               // Anniversary
//                if (annObject != null) {
//                    String annDateStr = annObject.optString("date", "");
//                    // Show only if not empty
//                    if (!annDateStr.isEmpty()) {
//                        try {
//                            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
//                            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMMM d", java.util.Locale.US);
//                            java.util.Date date = inputFormat.parse(annDateStr.split(" ")[0]);
//                            anniversaryDate = outputFormat.format(date);
//                        } catch (Exception e) {
//                            anniversaryDate = annDateStr.split(" ")[0]; // Only YYYY-MM-DD
//                        }
//                    }
//                }
//
////                String color = backColors[i % backColors.length];
//                anniversaryList.add(new anniversaryModel(doctorName, anniversaryDate,Code, territory, qualification, category,speciality,className));
//            }
//            Log.d("anniversaryFragment", "anniversary list size: " + anniversaryList.size());
//            // Setup RecyclerView
//            anniversaryAdapter = new anniversaryAdapter(anniversaryList, getActivity());
//            anniversayList.setLayoutManager(new LinearLayoutManager(getActivity()));
//            anniversayList.setAdapter(anniversaryAdapter);
//            anniversaryAdapter.notifyDataSetChanged();
//
//            // Show or hide "No Data"
//            if (anniversaryList.isEmpty()) {
//                anniversayList.setVisibility(View.GONE);
//                constraintNoData.setVisibility(View.VISIBLE);
//            } else {
//                anniversayList.setVisibility(View.VISIBLE);
//                constraintNoData.setVisibility(View.GONE);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            anniversayList.setVisibility(View.GONE);
//            constraintNoData.setVisibility(View.VISIBLE);
//        }
//    }
//}

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
