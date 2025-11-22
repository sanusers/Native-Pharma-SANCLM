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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.storage.SharedPref;

public class birthdaywishes_fragment extends Fragment {

    //private ListView birthList;
    private ListView listToday;
    private ListView listUpcoming;
    private ListView listBelated;
    private ConstraintLayout constraintNoData;
    TextView txtTodayNoData, txtUpcomingNoData, txtBelatedNoData;

    //private ArrayList<birthdayModel> birthdayList = new ArrayList<>();
    private ArrayList<birthdayModel> todayList = new ArrayList<>();
    private ArrayList<birthdayModel> upcomingList = new ArrayList<>();
    private ArrayList<birthdayModel> belatedList = new ArrayList<>();

    //private birthdayAdapter birthdayAdapter;
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

        // birthList = view.findViewById(R.id.birthList);
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
        loadBirthdayData();
        setListViewHeightBasedOnChildren(listToday);
        setListViewHeightBasedOnChildren(listUpcoming);
        setListViewHeightBasedOnChildren(listBelated);
        return view;
    }

    //    private void loadBirthdayData() {
//        try {
//            JSONArray doctorJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext())).getMasterSyncDataJsonArray();
//            Log.d("BirthdayFragment", "Raw JSON from DB: " + doctorJsonArray.toString());
//            //birthdayList.clear();
//            todayList.clear();
//            upcomingList.clear();
//            belatedList.clear();
//
//            // ✅ Added: prepare date formats and +3/-3 range
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
//            for (int i = 0; i < doctorJsonArray.length(); i++) {
//                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
//                String doctorName = doctorObj.getString("Name");
//                String Code = doctorObj.getString("Code");
//                String territory = doctorObj.getString("Town_Name");
//                String qualification = doctorObj.getString("DrDesig");
//                String category = doctorObj.getString("Category");
//                String speciality = doctorObj.getString("Specialty");
//                String className = doctorObj.getString("Doc_Class_ShortName");
//
//                JSONObject dobObject = doctorObj.optJSONObject("DctrDOB");
//                String birthDate = "";
//
//                if (dobObject != null) {
//                    String dobDateStr = dobObject.optString("date", "").trim();
//
//                    // ✅ Skip empty or invalid DOBs
//                    if (!dobDateStr.isEmpty() && !dobDateStr.startsWith("1900")) {
//                        try {
//                            java.util.Date date = inputFormat.parse(dobDateStr.split(" ")[0]);
//                            java.util.Calendar dob = java.util.Calendar.getInstance();
//                            dob.setTime(date);
//                            dob.set(java.util.Calendar.YEAR, 2000);
//
//                            // ✅ Date range check (+3 / -3)
//                            boolean inRange;
//                            if (start.after(end)) {
//                                // Year wrap case (e.g., Dec–Jan)
//                                inRange = (!dob.before(start) || !dob.after(end));
//                            } else {
//                                inRange = (!dob.before(start) && !dob.after(end));
//                            }
//
//                            if (inRange) {
//                                birthDate = outputFormat.format(date);
//                                birthdayList.add(new birthdayModel(
//                                        doctorName, birthDate, Code, territory,
//                                        qualification, category, speciality, className));
//                            }
//
//                        } catch (Exception ignored) {
//                            // ignore parse errors
//                        }
//                    }
//                }
//            }
//
//            Log.d("BirthdayFragment", "Filtered Birthday list size: " + birthdayList.size());
//
//            // Setup RecyclerView
//            birthdayAdapter = new birthdayAdapter(birthdayList, getActivity());
//            //birthList.setLayoutManager(new LinearLayoutManager(getActivity()));
//            birthList.setAdapter(birthdayAdapter);
//            //birthList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//            birthdayAdapter.notifyDataSetChanged();
//
//            if (birthdayList.isEmpty()) {
//                birthList.setVisibility(View.GONE);
//                constraintNoData.setVisibility(View.VISIBLE);
//            } else {
//                birthList.setVisibility(View.VISIBLE);
//                constraintNoData.setVisibility(View.GONE);
//            }
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            birthList.setVisibility(View.GONE);
//            constraintNoData.setVisibility(View.VISIBLE);
//        }
//    }
   /* private void loadBirthdayData() {
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
            end.add(java.util.Calendar.DATE, +3);

            for (int i = 0; i < doctorJsonArray.length(); i++) {
                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);
                String doctorName = doctorObj.optString("Name");
                String Code = doctorObj.optString("Code");
                String territory = doctorObj.optString("Town_Name");
                String qualification = doctorObj.optString("DrDesig");
                String category = doctorObj.optString("Category");
                String speciality = doctorObj.optString("Specialty");
                String className = doctorObj.optString("Doc_Class_ShortName");

                JSONObject dobObject = doctorObj.optJSONObject("DctrDOB");
                if (dobObject == null) continue;

                String dobDateStr = dobObject.optString("date", "").trim();
                if (dobDateStr.isEmpty() || dobDateStr.startsWith("1900")) continue;

                try {
                    java.util.Date parsedDate = inputFormat.parse(dobDateStr.split(" ")[0]);
                    java.util.Calendar dob = java.util.Calendar.getInstance();
                    dob.setTime(parsedDate);

                    // ✅ Normalize year for proper comparison
                    dob.set(java.util.Calendar.YEAR, 2000);

                    String displayDate = outputFormat.format(parsedDate);
                    birthdayModel model = new birthdayModel(
                            doctorName, displayDate, Code, territory,
                            qualification, category, speciality, className
                    );

                    // ✅ Check if DOB is within the ±3 day range
                    boolean inRange;
                    if (start.after(end)) {
                        // year wrap (e.g., Dec–Jan)
                        inRange = (!dob.before(start) || !dob.after(end));
                    } else {
                        inRange = (!dob.before(start) && !dob.after(end));
                    }

                    if (dob.get(java.util.Calendar.MONTH) == today.get(java.util.Calendar.MONTH)
                            && dob.get(java.util.Calendar.DAY_OF_MONTH) == today.get(java.util.Calendar.DAY_OF_MONTH)) {
                        todayList.add(model);
                    } else if (inRange && dob.after(today)) {
                        upcomingList.add(model);
                    } else if (inRange && dob.before(today)) {
                        belatedList.add(model);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.d("BirthdayFragment", "Today: " + todayList.size() +
                    " | Upcoming: " + upcomingList.size() +
                    " | Belated: " + belatedList.size());

            birthdayAdapter todayAdapter = new birthdayAdapter(todayList, getActivity());
            birthdayAdapter upcomingAdapter = new birthdayAdapter(upcomingList, getActivity());
            birthdayAdapter belatedAdapter = new birthdayAdapter(belatedList, getActivity());

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
*/

    private void loadBirthdayData() {
        try {
            JSONArray doctorJsonArray = masterDataDao
                    .getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(requireContext()))
                    .getMasterSyncDataJsonArray();

            todayList.clear();
            upcomingList.clear();
            belatedList.clear();

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d", Locale.US);

            // Current day (normalized)
            Calendar today = Calendar.getInstance();
            today.set(Calendar.YEAR, 2000);
            normalize(today);

            // ±3 day window
            Calendar start = (Calendar) today.clone();
            Calendar end = (Calendar) today.clone();
            start.add(Calendar.DATE, -3);
            end.add(Calendar.DATE, +3);

            normalize(start);
            normalize(end);

            for (int i = 0; i < doctorJsonArray.length(); i++) {
                JSONObject doctorObj = doctorJsonArray.getJSONObject(i);

                String doctorName = doctorObj.optString("Name");
                String Code = doctorObj.optString("Code");
                String territory = doctorObj.optString("Town_Name");
                String qualification = doctorObj.optString("DrDesig");
                String category = doctorObj.optString("Category");
                String speciality = doctorObj.optString("Specialty");
                String className = doctorObj.optString("Doc_Class_ShortName");

                JSONObject dobObject = doctorObj.optJSONObject("DctrDOB");
                if (dobObject == null) continue;

                String dobDateStr = dobObject.optString("date", "").trim();
                if (dobDateStr.isEmpty() || dobDateStr.startsWith("1900")) continue;

                try {
                    Date parsedDate = inputFormat.parse(dobDateStr.split(" ")[0]);
                    Calendar dob = Calendar.getInstance();
                    dob.setTime(parsedDate);
                    dob.set(Calendar.YEAR, 2000);
                    normalize(dob);

                    String displayDate = outputFormat.format(parsedDate);

                    birthdayModel model = new birthdayModel(
                            doctorName, displayDate, Code, territory,
                            qualification, category, speciality, className
                    );


                    boolean inRange;
                    if (start.after(end)) {
                        inRange = (!dob.before(start) || !dob.after(end));
                    } else {
                        inRange = (!dob.before(start) && !dob.after(end));
                    }

                    if (dob.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                            && dob.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                        todayList.add(model);
                    } else if (inRange && dob.after(today)) {
                        upcomingList.add(model);
                    } else if (inRange && dob.before(today)) {
                        belatedList.add(model);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.d("BirthdayFragment", "Today: " + todayList.size() +
                    " | Upcoming: " + upcomingList.size() +
                    " | Belated: " + belatedList.size());

            birthdayAdapter todayAdapter = new birthdayAdapter(todayList, getActivity());
            birthdayAdapter upcomingAdapter = new birthdayAdapter(upcomingList, getActivity());
            birthdayAdapter belatedAdapter = new birthdayAdapter(belatedList, getActivity());

            listToday.setAdapter(todayAdapter);
            listUpcoming.setAdapter(upcomingAdapter);
            listBelated.setAdapter(belatedAdapter);

            todayAdapter.notifyDataSetChanged();
            upcomingAdapter.notifyDataSetChanged();
            belatedAdapter.notifyDataSetChanged();

            txtTodayNoData.setVisibility(todayList.isEmpty() ? View.VISIBLE : View.GONE);
            txtUpcomingNoData.setVisibility(upcomingList.isEmpty() ? View.VISIBLE : View.GONE);
            txtBelatedNoData.setVisibility(belatedList.isEmpty() ? View.VISIBLE : View.GONE);

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


    private void normalize(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }


    // Added Lines
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
            totalHeight += listItem.getMeasuredHeight() - requireContext().getResources().getDimension(R.dimen._10sdp);
        }

        //int dividerTotal = listView.getDividerHeight() * Math.max(0, listAdapter.getCount() - 1);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = Math.max(0, totalHeight);
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
