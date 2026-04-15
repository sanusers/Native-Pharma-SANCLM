package saneforce.sanzen.activity.myresource.setupdetails;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.storage.SharedPref;

public class App_setup extends Fragment {

    RecyclerView recyclerView;
    setupDetailsAdapter adapter;
    ArrayList<callstatus_model> list = new ArrayList<>();


    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_app_setup, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(divider);

        loadFromSharedPreferences();

        adapter = new setupDetailsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadFromSharedPreferences() {
        list.clear();

        boolean isGpsEnabled = App_setup.isLocationEnabled(requireContext());

        callstatus_model model1 = new callstatus_model();
        model1.setCustName("Location");
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.LOCATION_TRACK);
        model1.setPromoted(isGpsEnabled);
        list.add(model1);

        String taggedDcr = SharedPref.getGeoChk(requireContext());
        callstatus_model model2 = new callstatus_model();
        model2.setCustName("Near Me");
        model2.setChkflk("0");
        model2.setSpKey(SharedPref.GEO_CHK);
        model2.setPromoted(taggedDcr.equals("0"));
        list.add(model2);

        String deviceLock = SharedPref.getDeviceIdNeed(requireContext());
        callstatus_model model3 = new callstatus_model();
        model3.setCustName("Device Lock");
        model3.setChkflk("0");
        model3.setSpKey(SharedPref.DEVICE_ID_NEED);
        model3.setPromoted(deviceLock.equals("0"));
        list.add(model3);

        callstatus_model radius = new callstatus_model();
        radius.setCustName("Fencing Radius");
        radius.setChkflk("2");
        radius.setSpKey(SharedPref.DIS_RAD);
        radius.setMnth(SharedPref.getDisRad(requireContext()));
        list.add(radius);

        String geo_Tag = SharedPref.getGeotagImg(requireContext());
        callstatus_model image = new callstatus_model();
        image.setCustName("GeoTag_Event_Capture");
        image.setChkflk("0");
        image.setSpKey(SharedPref.GEOTAG_IMG);
        image.setPromoted(geo_Tag.equals("0"));
        list.add(image);

        String dcrSeq = SharedPref.getDcrSequential(requireContext());
        callstatus_model Seq = new callstatus_model();
        Seq.setCustName("DCR_Sequential");
        Seq.setChkflk("0");
        Seq.setSpKey(SharedPref.DCR_SEQUENTIAL);
        Seq.setPromoted(dcrSeq.equals("0"));
        list.add(Seq);

        String dcr_lock = SharedPref.getDcrLockDays(requireContext());
        callstatus_model lock = new callstatus_model();
        lock.setCustName("DCR_Lock");
        lock.setChkflk("0");
        lock.setSpKey(SharedPref.DCR_LOCK_DAYS);
        lock.setPromoted(dcr_lock.equals("0"));
        list.add(lock);


        callstatus_model lockDays = new callstatus_model();
        lockDays.setCustName("DCR_Lock_Days");
        lockDays.setChkflk("2");
        lockDays.setSpKey(SharedPref.SEQ_DCR_LOCK_DAYS);
        lockDays.setMonth_name(SharedPref.getSeqDcrLockDays(requireContext()));
        list.add(lockDays);

        String delete = SharedPref.getEditCallDelNeed(requireContext());
        callstatus_model editdeletecall = new callstatus_model();
        editdeletecall.setCustName("Delete_Option");
        editdeletecall.setChkflk("0");
        editdeletecall.setSpKey(SharedPref.EDIT_CALL_DEL_NEED);
        editdeletecall.setPromoted(delete.equals("0"));
        list.add(editdeletecall);

        String profile = SharedPref.getProfilingNeed(requireContext());
        callstatus_model drProfile = new callstatus_model();
        drProfile.setCustName("Dr Profile");
        drProfile.setChkflk("0");
        drProfile.setSpKey(SharedPref.PROFILING_NEED);
        drProfile.setPromoted(profile.equals("0"));
        list.add(drProfile);

        String additional = SharedPref.getAdditionalCallNeed(requireContext());
        callstatus_model addCalls = new callstatus_model();
        addCalls.setCustName("Additional Call");
        addCalls.setChkflk("0");
        addCalls.setSpKey(SharedPref.ADDITIONAL_CALL_NEED);
        addCalls.setPromoted(additional.equals("0"));
        list.add(addCalls);


        String stay = SharedPref.getNightStay(requireContext());
        callstatus_model nightstay = new callstatus_model();
        nightstay.setCustName("Night Stay");
        nightstay.setChkflk("0");
        nightstay.setSpKey(SharedPref.NightStay);
        nightstay.setPromoted(stay.equals("0"));
        list.add(nightstay);


        String dayCheckIn = SharedPref.getSrtNd(requireContext());
        callstatus_model checkIn = new callstatus_model();
        checkIn.setCustName("CheckIn");
        checkIn.setChkflk("0");
        checkIn.setSpKey(SharedPref.SRT_ND);
        checkIn.setPromoted(dayCheckIn.equals("0"));
        list.add(checkIn);

        String  resetPassword = SharedPref.getResetPasswordNeed(requireContext());
        callstatus_model password = new callstatus_model();
        password.setCustName("Reset password");
        password.setChkflk("0");
        password.setSpKey(SharedPref.RESET_PASSWORD_NEED);
        password.setPromoted(resetPassword.equals("0"));
        list.add(password);

        callstatus_model resetPass = new callstatus_model();
        resetPass.setCustName("No Of Days To Reset Password");
        resetPass.setChkflk("2");
        resetPass.setSpKey(SharedPref.RESET_PASSWORD_DAYS);
        resetPass.setWorkType(SharedPref.getResetPasswordDays(requireContext()));
        list.add(resetPass);


        String  activity = SharedPref.getActivityNd(requireContext());
        callstatus_model act = new callstatus_model();
        act.setCustName("Activity");
        act.setChkflk("0");
        act.setSpKey(SharedPref.ACTIVITY_ND);
        act.setPromoted(activity.equals("0"));
        list.add(act);

        String  activitymand = SharedPref.getActivityMand(requireContext());
        callstatus_model actmand = new callstatus_model();
        actmand.setCustName("Activity Mandatory");
        actmand.setChkflk("0");
        actmand.setSpKey(SharedPref.ACTIVITY_MAND);
        actmand.setPromoted(activitymand.equals("0"));
        list.add(actmand);

//        String  activityMyDayPan = SharedPref.getMydayplanNeed(requireContext());
//        callstatus_model actMyDailyPlan = new callstatus_model();
//        actMyDailyPlan.setCustName("DayPlan Based Common Activity");
//        actMyDailyPlan.setChkflk("0");
//        actMyDailyPlan.setSpKey(SharedPref.MYDAYPLAN_NEED);
//        actMyDailyPlan.setPromoted(activityMyDayPan.equals("0"));
//        list.add(actMyDailyPlan);

        String  survey = SharedPref.getSurveyNd(requireContext());
        callstatus_model sur = new callstatus_model();
        sur.setCustName("Survey");
        sur.setChkflk("0");
        sur.setSpKey(SharedPref.SURVEY_ND);
        sur.setPromoted(survey.equals("0"));
        list.add(sur);


        String  quiz = SharedPref.getQuizNeed(requireContext());
        callstatus_model quizNeed = new callstatus_model();
        quizNeed.setCustName("Quiz");
        quizNeed.setChkflk("0");
        quizNeed.setSpKey(SharedPref.QUIZ_NEED);
        quizNeed.setPromoted(quiz.equals("0"));
        list.add(quizNeed);

        String  quizMand = SharedPref.getQuizNeedMandt(requireContext());
        callstatus_model quizNdMand = new callstatus_model();
        quizNdMand.setCustName("Quiz Need Mandatory");
        quizNdMand.setChkflk("0");
        quizNdMand.setSpKey(SharedPref.QUIZ_NEED_MANDT);
        quizNdMand.setPromoted(quizMand.equals("0"));
        list.add(quizNdMand);

        callstatus_model quizHead = new callstatus_model();
        quizHead.setCustName("Quiz Heading");
        quizHead.setChkflk("2");
        quizHead.setSpKey(SharedPref.QUIZ_HEADING);
        quizHead.setDcrname(SharedPref.getQuizHeading(requireContext()));
        list.add(quizHead);


        String  chemAdditional = SharedPref.getChemistAddition(requireContext());
        callstatus_model chmAdd = new callstatus_model();
        chmAdd.setCustName("Chemist Addition");
        chmAdd.setChkflk("0");
        chmAdd.setSpKey(SharedPref.ADD_CHM);
        chmAdd.setPromoted(chemAdditional.equals("0"));
        list.add(chmAdd);


        String  unlistAdditional = SharedPref.getUnlistAddition(requireContext());
        callstatus_model unlstAdd = new callstatus_model();
        unlstAdd.setCustName("Unlist Addition");
        unlstAdd.setChkflk("0");
        unlstAdd.setSpKey(SharedPref.ADD_UNLST);
        unlstAdd.setPromoted(unlistAdditional.equals("0"));
        list.add(unlstAdd);

        String tourPlan = SharedPref.getTpNeed(requireContext());
        callstatus_model model = new callstatus_model();
        model.setCustName("Tour Plan");
        model.setChkflk("0");
        model.setSpKey(SharedPref.TP_NEED);
        model.setPromoted(tourPlan.equals("0"));
        list.add(model);


    }
}
