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
        model1.setCustName(getString(R.string.location));
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.LOCATION_TRACK);
        model1.setPromoted(isGpsEnabled);
        list.add(model1);

        String taggedDcr = SharedPref.getGeoChk(requireContext());
        callstatus_model model2 = new callstatus_model();
        model2.setCustName(getString(R.string.near_me));
        model2.setChkflk("0");
        model2.setSpKey(SharedPref.GEO_CHK);
        model2.setPromoted(taggedDcr.equals("0"));
        list.add(model2);

        String deviceLock = SharedPref.getDeviceIdNeed(requireContext());
        callstatus_model model3 = new callstatus_model();
        model3.setCustName(getString(R.string.device_lock));
        model3.setChkflk("0");
        model3.setSpKey(SharedPref.DEVICE_ID_NEED);
        model3.setPromoted(deviceLock.equals("0"));
        list.add(model3);

        callstatus_model radius = new callstatus_model();
        radius.setCustName(getString(R.string.fencing_radius));
        radius.setChkflk("2");
        radius.setSpKey(SharedPref.DIS_RAD);
        radius.setMnth(SharedPref.getDisRad(requireContext()));
        list.add(radius);


        String tourPlan = SharedPref.getTpNeed(requireContext());
        callstatus_model model = new callstatus_model();
        model.setCustName(getString(R.string.tour_plan));
        model.setChkflk("0");
        model.setSpKey(SharedPref.TP_NEED);
        model.setPromoted(tourPlan.equals("0"));
        list.add(model);


        callstatus_model model4 = new callstatus_model();
        model4.setCustName(getString(R.string.tp_start_end_date));
        model4.setChkflk("2");
        model4.setSpKey(SharedPref.TP_START_DATE);
        model4.setTown_code(SharedPref.getTpStartDate(requireContext())); // From date
        model4.setTown_name(SharedPref.getTpEndDate(requireContext()));   // To date
        list.add(model4);

        String stp = SharedPref.getStpNeed(requireContext());
        callstatus_model stpneed = new callstatus_model();
        stpneed.setCustName(getString(R.string.standard_tour_plan));
        stpneed.setChkflk("0");
        stpneed.setSpKey(SharedPref.STP_NEED);
        stpneed.setPromoted(stp.equals("0"));
        list.add(stpneed);


        String  chemAdditional = SharedPref.getChemistAddition(requireContext());
        callstatus_model chmAdd = new callstatus_model();
        chmAdd.setCustName(getString(R.string.chemist_addition));
        chmAdd.setChkflk("0");
        chmAdd.setSpKey(SharedPref.ADD_CHM);
        chmAdd.setPromoted(chemAdditional.equals("0"));
        list.add(chmAdd);


        String  unlistAdditional = SharedPref.getUnlistAddition(requireContext());
        callstatus_model unlstAdd = new callstatus_model();
        unlstAdd.setCustName(getString(R.string.unlist_addition));
        unlstAdd.setChkflk("0");
        unlstAdd.setSpKey(SharedPref.ADD_UNLST);
        unlstAdd.setPromoted(unlistAdditional.equals("0"));
        list.add(unlstAdd);


        String  unlistapp = SharedPref.getUnlstDocAppNeed(requireContext());
        callstatus_model unlst = new callstatus_model();
        unlst.setCustName(getString(R.string.unlist_approval_need));
        unlst.setChkflk("0");
        unlst.setSpKey(SharedPref.ADD_UNLST);
        unlst.setPromoted(unlistapp.equals("0"));
        list.add(unlst);


        String  activity = SharedPref.getActivityNd(requireContext());
        callstatus_model act = new callstatus_model();
        act.setCustName(getString(R.string.activity));
        act.setChkflk("0");
        act.setSpKey(SharedPref.ACTIVITY_ND);
        act.setPromoted(activity.equals("0"));
        list.add(act);

        String  activitymand = SharedPref.getActivityMand(requireContext());
        callstatus_model actmand = new callstatus_model();
        actmand.setCustName(getString(R.string.activity_mandatory));
        actmand.setChkflk("0");
        actmand.setSpKey(SharedPref.ACTIVITY_MAND);
        actmand.setPromoted(activitymand.equals("0"));
        list.add(actmand);


        String  quiz = SharedPref.getQuizNeed(requireContext());
        callstatus_model quizNeed = new callstatus_model();
        quizNeed.setCustName(getString(R.string.quiz_need));
        quizNeed.setChkflk("0");
        quizNeed.setSpKey(SharedPref.QUIZ_NEED);
        quizNeed.setPromoted(quiz.equals("0"));
        list.add(quizNeed);

        String  quizMand = SharedPref.getQuizNeedMandt(requireContext());
        callstatus_model quizNdMand = new callstatus_model();
        quizNdMand.setCustName(getString(R.string.quiz_mandatory));
        quizNdMand.setChkflk("0");
        quizNdMand.setSpKey(SharedPref.QUIZ_NEED_MANDT);
        quizNdMand.setPromoted(quizMand.equals("0"));
        list.add(quizNdMand);

        String  survey = SharedPref.getSurveyNd(requireContext());
        callstatus_model sur = new callstatus_model();
        sur.setCustName(getString(R.string.survey));
        sur.setChkflk("0");
        sur.setSpKey(SharedPref.SURVEY_ND);
        sur.setPromoted(survey.equals("0"));
        list.add(sur);


        String dayCheckIn = SharedPref.getSrtNd(requireContext());
        callstatus_model checkIn = new callstatus_model();
        checkIn.setCustName(getString(R.string.day_check_in));
        checkIn.setChkflk("0");
        checkIn.setSpKey(SharedPref.SRT_ND);
        checkIn.setPromoted(dayCheckIn.equals("0"));
        list.add(checkIn);

        String geo_Tag = SharedPref.getGeotagImg(requireContext());
        callstatus_model image = new callstatus_model();
        image.setCustName(getString(R.string.geotag_event_capture));
        image.setChkflk("0");
        image.setSpKey(SharedPref.GEOTAG_IMG);
        image.setPromoted(geo_Tag.equals("0"));
        list.add(image);


        String profile = SharedPref.getProfilingNeed(requireContext());
        callstatus_model drProfile = new callstatus_model();
        drProfile.setCustName(getString(R.string.profile) + " ( "+ SharedPref.getDrCap(requireContext())+","+SharedPref.getChmCap(requireContext())+","+SharedPref.getStkCap(requireContext())+","+SharedPref.getUNLcap(requireContext())+ " )");
        drProfile.setChkflk("0");
        drProfile.setSpKey(SharedPref.PROFILING_NEED);
        drProfile.setPromoted(profile.equals("0"));
        list.add(drProfile);

        String  resetPassword = SharedPref.getResetPasswordNeed(requireContext());
        callstatus_model password = new callstatus_model();
        password.setCustName(getString(R.string.reset_password));
        password.setChkflk("0");
        password.setSpKey(SharedPref.RESET_PASSWORD_NEED);
        password.setPromoted(resetPassword.equals("0"));
        list.add(password);

        callstatus_model resetPass = new callstatus_model();
        resetPass.setCustName(getString(R.string.no_of_days_to_reset_password));
        resetPass.setChkflk("2");
        resetPass.setSpKey(SharedPref.RESET_PASSWORD_DAYS);
        resetPass.setWorkType(SharedPref.getResetPasswordDays(requireContext()));
        list.add(resetPass);


        String stay = SharedPref.getNightStay(requireContext());
        callstatus_model nightstay = new callstatus_model();
        nightstay.setCustName(getString(R.string.night_stay));
        nightstay.setChkflk("0");
        nightstay.setSpKey(SharedPref.NightStay);
        nightstay.setPromoted(stay.equals("0"));
        list.add(nightstay);

        String staynfw = SharedPref.getNsForNfwNeed(requireContext());
        callstatus_model nfw = new callstatus_model();
        nfw.setCustName(getString(R.string.night_stay_for_non_field_work));
        nfw.setChkflk("0");
        nfw.setSpKey(SharedPref.NS_FOR_NFW_NEED);
        nfw.setPromoted(staynfw.equals("0"));
        list.add(nfw);

        String nsNeed = SharedPref.getNsAllClusterNeed(requireContext());
        callstatus_model nsAllCluster = new callstatus_model();
        nsAllCluster.setCustName(getString(R.string.night_stay_all_cluster_need));
        nsAllCluster.setChkflk("0");
        nsAllCluster.setSpKey(SharedPref.NS_ALL_CLUSTER_NEED);
        nsAllCluster.setPromoted(nsNeed.equals("0"));
        list.add(nsAllCluster);








//        String  activityMyDayPan = SharedPref.getMydayplanNeed(requireContext());
//        callstatus_model actMyDailyPlan = new callstatus_model();
//        actMyDailyPlan.setCustName("DayPlan Based Common Activity");
//        actMyDailyPlan.setChkflk("0");
//        actMyDailyPlan.setSpKey(SharedPref.MYDAYPLAN_NEED);
//        actMyDailyPlan.setPromoted(activityMyDayPan.equals("0"));
//        list.add(actMyDailyPlan);





//        callstatus_model quizHead = new callstatus_model();
//        quizHead.setCustName("Quiz Heading");
//        quizHead.setChkflk("2");
//        quizHead.setSpKey(SharedPref.QUIZ_HEADING);
//        quizHead.setDcrname(SharedPref.getQuizHeading(requireContext()));
//        list.add(quizHead);







    }
}
