package saneforce.sanzen.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.masterSync.MasterSyncActivity;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.WorkPlanEntriesNeeded;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataTable;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class SyncManager {
    private int doctorStatus = 0, doctorGeoStatus = 0, specialityStatus = 0, qualificationStatus = 0, categoryStatus = 0, chemistCategoryStatus = 0, departmentStatus = 0, classStatus = 0, feedbackStatus = 0, unlistedDrStatus = 0, chemistStatus = 0, stockiestStatus = 0, unlistedDrGeoStatus = 0, chemistGeoStatus = 0, stockiestGeoStatus = 0, hospitalStatus = 0, cipStatus = 0, inputStatus = 0, leaveStatus = 0, leaveStatusStatus = 0, tpSetupStatus = 0, tourPLanStatus = 0, stpSetupStatus = 0, standardTourPLanStatus = 0, clusterStatus = 0, callSyncStatus = 0, myDayPlanStatus = 0, visitControlStatus = 0, dateSyncStatus = 0, checkInStatus = 0, stockBalanceStatus = 0, calenderEventStaus = 0, productStatus = 0, proCatStatus = 0, brandStatus = 0, compProStatus = 0, mapCompPrdStatus = 0, activityStatus = 0, workTypeStatus = 0, holidayStatus = 0, weeklyOfStatus = 0, proSlideStatus = 0, proSpeSlideStatus = 0, brandSlideStatus = 0, therapticStatus = 0, welcomeStatus = 0, subordinateStatus = 0, subMgrStatus = 0, jWorkStatus = 0, QuizStatus = 0, SurveyStatus = 0, setupStatus = 0, profileStatus = 0;
    private final ArrayList<MasterSyncItemModel> doctorModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> stockiestModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> chemistModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> unlistedDrModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> hospitalModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> cipModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> inputModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> productModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> clusterModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> leaveModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> dcrModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> activityModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> workTypeModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> tpModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> slideModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> subordinateModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> otherModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> profileModelArray = new ArrayList<>();
    private final ArrayList<MasterSyncItemModel> setupModelArray = new ArrayList<>();
    private final Context context;
    private final String hqCode, type, monthYear;
    private final long id;
    private final ArrayList<MasterSyncItemModel> masterSyncAllModel = new ArrayList<>();
    private final MasterDataDao masterDataDao;
    private final TourPlanOfflineDataDao tourPlanOfflineDataDao;
    private final STPOfflineDataDao stpOfflineDataDao;
    private final NotificationDataDao notificationDataDao;
    private ApiInterface apiInterface;
    private int apiSuccessCount = 0;
    private LocalDate localDate;

    public SyncManager(Context context, String hqCode, String type, String monthYear, long id) {
        this.context = context;
        this.hqCode = hqCode;
        this.type = type;
        this.monthYear = monthYear;
        this.id = id;
        localDate = LocalDate.now();
        RoomDB db = RoomDB.getDatabase(context);
        masterDataDao = db.masterDataDao();
        tourPlanOfflineDataDao = db.tourPlanOfflineDataDao();
        stpOfflineDataDao = db.stpOfflineDataDao();
        notificationDataDao = db.notificationDataDao();
        notificationDataDao.changeNotificationSyncStatus((int) id, 2);
        getRequiredData();
        prepareArray();
    }

    private void getRequiredData() {
        doctorStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DOCTOR_MAS + hqCode);
        doctorGeoStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DOCTOR_GEO + hqCode);
        specialityStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SPECIALITY);
        qualificationStatus = masterDataDao.getMasterSyncStatusByKey(Constants.QUALIFICATION);
        categoryStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CATEGORY);
        departmentStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DEPARTMENT);
        classStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CLASS);
        feedbackStatus = masterDataDao.getMasterSyncStatusByKey(Constants.FEEDBACK);
        unlistedDrStatus = masterDataDao.getMasterSyncStatusByKey(Constants.UNLISTED_DOCTOR_MAS + hqCode);
        unlistedDrGeoStatus = masterDataDao.getMasterSyncStatusByKey(Constants.UNLISTED_DOCTOR_GEO + hqCode);
        chemistStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST_MAS + hqCode);
        chemistGeoStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST_GEO + hqCode);
        chemistCategoryStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CATEGORY_CHEMIST);
        stockiestStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STOCKIEST_MAS + hqCode);
        stockiestGeoStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STOCKIEST_GEO + hqCode);
        hospitalStatus = masterDataDao.getMasterSyncStatusByKey(Constants.HOSPITAL + hqCode);
        cipStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CIP + hqCode);
        inputStatus = masterDataDao.getMasterSyncStatusByKey(Constants.INPUT);
        leaveStatus = masterDataDao.getMasterSyncStatusByKey(Constants.LEAVE);
        leaveStatusStatus = masterDataDao.getMasterSyncStatusByKey(Constants.LEAVE_STATUS);
        callSyncStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CALL_SYNC);
        myDayPlanStatus = masterDataDao.getMasterSyncStatusByKey(Constants.WORK_PLAN);
        visitControlStatus = masterDataDao.getMasterSyncStatusByKey(Constants.VISIT_CONTROL);
        dateSyncStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DATE_SYNC);
        checkInStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHECK_IN);
        stockBalanceStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STOCK_BALANCE_MASTER);
        stockBalanceStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STOCK_BALANCE_MASTER);
        calenderEventStaus = masterDataDao.getMasterSyncStatusByKey(Constants.CALENDER_EVENT_STATUS);
        activityStatus = masterDataDao.getMasterSyncStatusByKey(Constants.ACTIVITY);
        workTypeStatus = masterDataDao.getMasterSyncStatusByKey(Constants.WORK_TYPE);
        holidayStatus = masterDataDao.getMasterSyncStatusByKey(Constants.HOLIDAY);
        weeklyOfStatus = masterDataDao.getMasterSyncStatusByKey(Constants.WEEKLY_OFF);
        tpSetupStatus = masterDataDao.getMasterSyncStatusByKey(Constants.TP_SETUP);
        tourPLanStatus = masterDataDao.getMasterSyncStatusByKey(Constants.TOUR_PLAN);
        stpSetupStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STP_SETUP);
        standardTourPLanStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STANDARD_TOUR_PLAN);
        productStatus = masterDataDao.getMasterSyncStatusByKey(Constants.PRODUCT);
        proCatStatus = masterDataDao.getMasterSyncStatusByKey(Constants.PRODUCT_CATEGORY);
        brandStatus = masterDataDao.getMasterSyncStatusByKey(Constants.BRAND);
        compProStatus = masterDataDao.getMasterSyncStatusByKey(Constants.COMPETITOR_PROD);
        mapCompPrdStatus = masterDataDao.getMasterSyncStatusByKey(Constants.MAPPED_COMPETITOR_PROD);
        clusterStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CLUSTER + hqCode);
        proSlideStatus = masterDataDao.getMasterSyncStatusByKey(Constants.PROD_SLIDE);
        proSpeSlideStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SPL_SLIDE);
        brandSlideStatus = masterDataDao.getMasterSyncStatusByKey(Constants.BRAND_SLIDE);
        therapticStatus = masterDataDao.getMasterSyncStatusByKey(Constants.THERAPTIC_SLIDE);
        welcomeStatus = masterDataDao.getMasterSyncStatusByKey(Constants.WELCOME_SLIDE);
        subordinateStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SUBORDINATE);
        subMgrStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SUBORDINATE_MGR);
        jWorkStatus = masterDataDao.getMasterSyncStatusByKey(Constants.JOINT_WORK + hqCode);
        QuizStatus = masterDataDao.getMasterSyncStatusByKey(Constants.QUIZ);
        SurveyStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SURVEY);
        profileStatus = masterDataDao.getMasterSyncStatusByKey(Constants.PROFILE);
        setupStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SETUP);
    }

    public void prepareArray() {
        //Listed Doctor
        if (SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
//            if (SharedPref.getGeotagNeed(context).equalsIgnoreCase("1")) {
//                MasterSyncItemModel doctorModel = new MasterSyncItemModel(SharedPref.getDrCap(context), Constants.DOCTOR, "getdoctors", Constants.DOCTOR + hqCode, doctorStatus, false);
            MasterSyncItemModel dr_mas = new MasterSyncItemModel(SharedPref.getDrCap(context), Constants.DOCTOR_MAS, "getdoctors_master", Constants.DOCTOR_MAS + hqCode, doctorStatus, false);
            MasterSyncItemModel geo = new MasterSyncItemModel(SharedPref.getDrCap(context) + " Geo", Constants.DOCTOR_MAS, "getdoctors_geo", Constants.DOCTOR_GEO + hqCode, doctorGeoStatus, false);
            MasterSyncItemModel spl = new MasterSyncItemModel(Constants.SPECIALITY, Constants.DOCTOR_MAS, "getspeciality", Constants.SPECIALITY, specialityStatus, false);
            MasterSyncItemModel ql = new MasterSyncItemModel(Constants.QUALIFICATION, Constants.DOCTOR_MAS, "getquali", Constants.QUALIFICATION, qualificationStatus, false);
            MasterSyncItemModel cat = new MasterSyncItemModel(Constants.CATEGORY, Constants.DOCTOR_MAS, "getcategorys", Constants.CATEGORY, categoryStatus, false);
//                    MasterSyncItemModel dep = new MasterSyncItemModel(Constants.DEPARTMENT, departmentCount, Constants.DOCTOR, "getdeparts", Constants.DEPARTMENT, departmentStatus, false);
            MasterSyncItemModel clas = new MasterSyncItemModel(Constants.CLASS, Constants.DOCTOR_MAS, "getclass", Constants.CLASS, classStatus, false);
//                doctorModelArray.add(doctorModel);
            doctorModelArray.add(dr_mas);
            doctorModelArray.add(geo);

            doctorModelArray.add(spl);
            doctorModelArray.add(ql);
            doctorModelArray.add(cat);
            //  doctorModelArray.add(dep);
            doctorModelArray.add(clas);
         /*   }else {
                MasterSyncItemModel doctorModel = new MasterSyncItemModel(SharedPref.getDrCap(context), Constants.DOCTOR, "getdoctors", Constants.DOCTOR + hqCode, doctorStatus, false);
                MasterSyncItemModel dr_mas = new MasterSyncItemModel(Constants.DOCTOR_MAS,Constants.DOCTOR,"getdoctors_master",Constants.DOCTOR_MAS,ListedDoctorMaster,false);
                MasterSyncItemModel geo = new MasterSyncItemModel(Constants.GEO, Constants.DOCTOR, "getdoctors_geo", Constants.GEO, ListedDoctorGeo, false);
                MasterSyncItemModel spl = new MasterSyncItemModel(Constants.SPECIALITY, Constants.DOCTOR, "getspeciality", Constants.SPECIALITY, specialityStatus, false);
                MasterSyncItemModel ql = new MasterSyncItemModel(Constants.QUALIFICATION, Constants.DOCTOR, "getquali", Constants.QUALIFICATION, qualificationStatus, false);
                MasterSyncItemModel cat = new MasterSyncItemModel(Constants.CATEGORY, Constants.DOCTOR, "getcategorys", Constants.CATEGORY, categoryStatus, false);
                //    MasterSyncItemModel dep = new MasterSyncItemModel(Constants.DEPARTMENT, departmentCount, Constants.DOCTOR, "getdeparts", Constants.DEPARTMENT, departmentStatus, false);
                MasterSyncItemModel clas = new MasterSyncItemModel(Constants.CLASS, Constants.DOCTOR, "getclass", Constants.CLASS, classStatus, false);
                doctorModelArray.add(doctorModel);
                doctorModelArray.add(dr_mas);
                doctorModelArray.add(geo);
                doctorModelArray.add(spl);
                doctorModelArray.add(ql);
                doctorModelArray.add(cat);
                //  doctorModelArray.add(dep);
                doctorModelArray.add(clas);
            }*/
        }

        //Chemist
        chemistModelArray.clear();
        if (SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
//            MasterSyncItemModel cheModel = new MasterSyncItemModel(SharedPref.getChmCap(context),  Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode, chemistStatus, false);
            MasterSyncItemModel cheMas = new MasterSyncItemModel(SharedPref.getChmCap(context), Constants.DOCTOR_MAS, "getchemist_master", Constants.CHEMIST_MAS + hqCode, chemistStatus, false);
            MasterSyncItemModel cheGeo = new MasterSyncItemModel(SharedPref.getChmCap(context) + " Geo", Constants.DOCTOR_MAS, "getchemist_geo", Constants.CHEMIST_GEO + hqCode, chemistGeoStatus, false);
            MasterSyncItemModel chemistCategory = new MasterSyncItemModel(Constants.CATEGORY, Constants.DOCTOR_MAS, "getchem_categorys", Constants.CATEGORY_CHEMIST, chemistCategoryStatus, false);
//            chemistModelArray.add(cheModel);
            chemistModelArray.add(cheMas);
            chemistModelArray.add(cheGeo);
            chemistModelArray.add(chemistCategory);
        }

        //Stockiest
        stockiestModelArray.clear();
        if (SharedPref.getStkNeed(context).equalsIgnoreCase("0")) {
//            MasterSyncItemModel stockModel = new MasterSyncItemModel(SharedPref.getStkCap(context),Constants.DOCTOR, "getstockist", Constants.STOCKIEST + hqCode, stockiestStatus, false);
            MasterSyncItemModel stock_mas = new MasterSyncItemModel(SharedPref.getStkCap(context), Constants.DOCTOR_MAS, "getstockist_master", Constants.STOCKIEST_MAS + hqCode, stockiestStatus, false);
            MasterSyncItemModel stock_geo = new MasterSyncItemModel(SharedPref.getStkCap(context) + " Geo", Constants.DOCTOR_MAS, "getstockist_geo", Constants.STOCKIEST_GEO + hqCode, stockiestGeoStatus, false);
//            stockiestModelArray.add(stockModel);
            stockiestModelArray.add(stock_mas);
            stockiestModelArray.add(stock_geo);
        }

        //Unlisted Dr
        unlistedDrModelArray.clear();
        if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) {
//            MasterSyncItemModel unListModel = new MasterSyncItemModel(SharedPref.getUNLcap(context),  Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, unlistedDrStatus, false);
            MasterSyncItemModel unList_mas = new MasterSyncItemModel(SharedPref.getUNLcap(context), Constants.DOCTOR_MAS, "getunlisteddr_master", Constants.UNLISTED_DOCTOR_MAS + hqCode, unlistedDrStatus, false);
            MasterSyncItemModel unList_geo = new MasterSyncItemModel(SharedPref.getUNLcap(context) + " Geo", Constants.DOCTOR_MAS, "getunlisteddr_geo", Constants.UNLISTED_DOCTOR_GEO + hqCode, unlistedDrGeoStatus, false);

//            unlistedDrModelArray.add(unListModel);
            unlistedDrModelArray.add(unList_mas);
            unlistedDrModelArray.add(unList_geo);
        }

        //Hospital
        hospitalModelArray.clear();
        if (SharedPref.getHospNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel hospModel = new MasterSyncItemModel(SharedPref.getHospCaption(context), Constants.DOCTOR_MAS, "gethospital", Constants.HOSPITAL + hqCode, hospitalStatus, false);
            hospitalModelArray.add(hospModel);
        }

        //CIP
        cipModelArray.clear();
        if (SharedPref.getCipNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel ciModel = new MasterSyncItemModel(SharedPref.getCipCaption(context), Constants.DOCTOR_MAS, "getcip", Constants.CIP + hqCode, cipStatus, false);
//            cipModelArray.add(ciModel);
        }

        //Cluster
        clusterModelArray.clear();
        MasterSyncItemModel cluster = new MasterSyncItemModel(SharedPref.getClusterCap(context), Constants.DOCTOR_MAS, "getterritory", Constants.CLUSTER + hqCode, clusterStatus, false);
        clusterModelArray.add(cluster);

        //Input
        inputModelArray.clear();
        MasterSyncItemModel inpModel = new MasterSyncItemModel(Constants.INPUT, Constants.PRODUCT, "getinputs", Constants.INPUT, inputStatus, false);
        inputModelArray.add(inpModel);

        //Product
        productModelArray.clear();
        MasterSyncItemModel proModel = new MasterSyncItemModel(Constants.PRODUCT, Constants.PRODUCT, "getproducts", Constants.PRODUCT, productStatus, false);
        //   MasterSyncItemModel proCatModel = new MasterSyncItemModel(Constants.PRODUCT_CATEGORY, proCatCount, Constants.PRODUCT, "", Constants.PRODUCT_CATEGORY, proCatStatus, false);
        MasterSyncItemModel brandModel = new MasterSyncItemModel(Constants.BRAND, Constants.PRODUCT, "getbrands", Constants.BRAND, brandStatus, false);
        productModelArray.add(proModel);
        //     productModelArray.add(proCatModel);
        productModelArray.add(brandModel);
        if (SharedPref.getRcpaNd(context).equalsIgnoreCase("0") || SharedPref.getChmRcpaNeed(context).equalsIgnoreCase("0")) {
            //     MasterSyncItemModel compProductModel = new MasterSyncItemModel(Constants.COMPETITOR_PROD, compProCount, Constants.PRODUCT, "getcompdet", Constants.COMPETITOR_PROD, compProStatus, false);
            MasterSyncItemModel mapCompPrdModel = new MasterSyncItemModel(Constants.MAPPED_COMPETITOR_PROD, "AdditionalDcr", "getmapcompdet", Constants.MAPPED_COMPETITOR_PROD, mapCompPrdStatus, false);
            //  productModelArray.add(compProductModel);
            productModelArray.add(mapCompPrdModel);
        }

        //Leave
        leaveModelArray.clear();
        MasterSyncItemModel leaveModel = new MasterSyncItemModel(Constants.LEAVE, "Leave", "getleavetype", Constants.LEAVE, leaveStatus, false);
        leaveModelArray.add(leaveModel);
        if (SharedPref.getLeaveEntitlementNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel leaveStatusModel = new MasterSyncItemModel(Constants.LEAVE_STATUS, "Leave", "getleavestatus", Constants.LEAVE_STATUS, leaveStatusStatus, false);
            leaveModelArray.add(leaveStatusModel);
        }

        //DCR
        dcrModelArray.clear();
        MasterSyncItemModel callSyncModel = new MasterSyncItemModel(Constants.CALL_SYNC, "Home", "gethome", Constants.CALL_SYNC, callSyncStatus, false);
        MasterSyncItemModel dateSyncModel = new MasterSyncItemModel(Constants.DATE_SYNC, "Home", "getdcrdate", Constants.DATE_SYNC, dateSyncStatus, false);
        MasterSyncItemModel myDayPlanModel = new MasterSyncItemModel(Constants.WORK_PLAN, Constants.DOCTOR_MAS, "gettodaydcr", Constants.WORK_PLAN, myDayPlanStatus, false);
        if (SharedPref.getSfType(context).equalsIgnoreCase("1") && SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            myDayPlanModel = new MasterSyncItemModel(Constants.WORK_PLAN, Constants.DOCTOR_MAS, "gettodaydcr", Constants.WORK_PLAN, myDayPlanStatus, false);
        } else {
            if (SharedPref.getSfType(context).equalsIgnoreCase("2") && SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                myDayPlanModel = new MasterSyncItemModel(Constants.WORK_PLAN, Constants.DOCTOR_MAS, "gettodaydcr", Constants.WORK_PLAN, myDayPlanStatus, false);
            } else {
                myDayPlanModel = new MasterSyncItemModel(Constants.WORK_PLAN, Constants.DOCTOR_MAS, "gettodaydcrmultihq", Constants.WORK_PLAN, myDayPlanStatus, false);
            }
        }

        //   MasterSyncItemModel EventCallSync = new MasterSyncItemModel("Status", -1, "AdditionalDcr", "gettodycalls", Constants.CALENDER_EVENT_STATUS, calenderEventStaus, false);
        dcrModelArray.add(callSyncModel);
        dcrModelArray.add(dateSyncModel);
        dcrModelArray.add(myDayPlanModel);
        if (SharedPref.getSrtNd(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel checkInModel = new MasterSyncItemModel(Constants.CHECK_IN, Constants.CHECK_IN, "getcheckin_zen", Constants.CHECK_IN, checkInStatus, false);
            dcrModelArray.add(checkInModel);
        }

        if (SharedPref.getSampleValidation(context).equalsIgnoreCase("1") || SharedPref.getInputValidation(context).equalsIgnoreCase("1")) {
            MasterSyncItemModel stockBalanceModel = new MasterSyncItemModel(Constants.STOCK_BALANCE, "AdditionalDcr", "getstockbalance", Constants.STOCK_BALANCE_MASTER, stockBalanceStatus, false);
            dcrModelArray.add(stockBalanceModel);
        }
        if (SharedPref.getVstNd(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel visitControlModel = new MasterSyncItemModel(Constants.VISIT_CONTROL, "AdditionalDcr", "getvisit_contro", Constants.VISIT_CONTROL, visitControlStatus, false);
            dcrModelArray.add(visitControlModel);
        }

        //Activity
        activityModelArray.clear();
        MasterSyncItemModel activity = new MasterSyncItemModel(Constants.ACTIVITY, Constants.ACTIVITY, "getdynactivity", Constants.ACTIVITY, activityStatus, false);
        activityModelArray.add(activity);

        //Work Type
        workTypeModelArray.clear();
        MasterSyncItemModel workType = new MasterSyncItemModel(Constants.WORK_TYPE, Constants.DOCTOR_MAS, "getworktype", Constants.WORK_TYPE, workTypeStatus, false);
        MasterSyncItemModel holiday = new MasterSyncItemModel(Constants.HOLIDAY, Constants.DOCTOR_MAS, "getholiday", Constants.HOLIDAY, holidayStatus, false);
        MasterSyncItemModel weeklyOff = new MasterSyncItemModel(Constants.WEEKLY_OFF, Constants.DOCTOR_MAS, "getweeklyoff", Constants.WEEKLY_OFF, weeklyOfStatus, false);
        workTypeModelArray.add(workType);
        workTypeModelArray.add(holiday);
        workTypeModelArray.add(weeklyOff);

        //Tour Plan
        tpModelArray.clear();
        boolean tpNeed = SharedPref.getTpNeed(context).equalsIgnoreCase("0"), stpNeed = SharedPref.getStpNeed(context).equalsIgnoreCase("0") && !SharedPref.getSfType(context).equalsIgnoreCase("2");
        if (tpNeed) {
            if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                MasterSyncItemModel tpSetup = new MasterSyncItemModel(Constants.TP_SETUP, Constants.SETUP, "gettpsetup", Constants.TP_SETUP, tpSetupStatus, false);
                MasterSyncItemModel tPlan = new MasterSyncItemModel(Constants.TOUR_PLAN, Constants.TOUR_PLAN, "gettp_onebuild", Constants.TOUR_PLAN, tourPLanStatus, false);
                tpModelArray.add(tpSetup);
                tpModelArray.add(tPlan);
            } else {
                MasterSyncItemModel tpSetup = new MasterSyncItemModel(Constants.TP_SETUP, Constants.SETUP, "gettpsetup", Constants.TP_SETUP, tpSetupStatus, false);
                MasterSyncItemModel tPlan = new MasterSyncItemModel(Constants.TOUR_PLAN, Constants.TOUR_PLAN, "getall_tp", Constants.TOUR_PLAN, tourPLanStatus, false);
                if (SharedPref.getSfType(context).equalsIgnoreCase("2")) {
                    tPlan = new MasterSyncItemModel(Constants.TOUR_PLAN, Constants.TOUR_PLAN, "getall_multitpnew", Constants.TOUR_PLAN, tourPLanStatus, false);
                }
                tpModelArray.add(tpSetup);
                tpModelArray.add(tPlan);
            }
        }
        if (stpNeed) {
            String stpCaption = SharedPref.getStpCaption(context), stpSetupCaption = Constants.STP_SETUP;
            if (!stpCaption.isEmpty()) {
                stpSetupCaption = stpCaption + " Setup";
            } else {
                stpCaption = Constants.STANDARD_TOUR_PLAN;
            }
            MasterSyncItemModel STPSetup = new MasterSyncItemModel(stpSetupCaption, Constants.STANDARD_TOUR_PLAN, "getstp_setup", Constants.STP_SETUP, stpSetupStatus, false);
            MasterSyncItemModel STPPlan = new MasterSyncItemModel(stpCaption, Constants.STANDARD_TOUR_PLAN, "getstp_details", Constants.STANDARD_TOUR_PLAN, standardTourPLanStatus, false);
            tpModelArray.add(STPSetup);
            tpModelArray.add(STPPlan);
        }

        //Slide
        slideModelArray.clear();
        MasterSyncItemModel proSlideModel = new MasterSyncItemModel(Constants.PROD_SLIDE, "Slide", "getprodslides", Constants.PROD_SLIDE, proSlideStatus, false);
        MasterSyncItemModel splSlideModel = new MasterSyncItemModel(Constants.SPL_SLIDE, "Slide", "getslidespeciality", Constants.SPL_SLIDE, proSpeSlideStatus, false);
        MasterSyncItemModel brandSlideModel = new MasterSyncItemModel(Constants.BRAND_SLIDE, "Slide", "getslidebrand", Constants.BRAND_SLIDE, brandSlideStatus, false);
        MasterSyncItemModel therapticSlideModel = new MasterSyncItemModel(Constants.THERAPTIC_SLIDE, "Slide", "gettheraptic", Constants.THERAPTIC_SLIDE, therapticStatus, false);
        MasterSyncItemModel welcomeSlideModel = new MasterSyncItemModel(Constants.WELCOME_SLIDE, "Slide", "getwelcomepage", Constants.WELCOME_SLIDE, welcomeStatus, false);
        slideModelArray.add(welcomeSlideModel);
        slideModelArray.add(proSlideModel);
        slideModelArray.add(splSlideModel);
        slideModelArray.add(brandSlideModel);
        if (SharedPref.getTherapticPresentationNeed(context).equalsIgnoreCase("0")) {
            slideModelArray.add(therapticSlideModel);
        }

        //Subordinate
        subordinateModelArray.clear();
        MasterSyncItemModel subModel = new MasterSyncItemModel("Hierarchy", Constants.SUBORDINATE, "getsubordinate", Constants.SUBORDINATE, subordinateStatus, false);
        // MasterSyncItemModel subMgrModel = new MasterSyncItemModel(Constants.SUBORDINATE_MGR, subMgrCount, Constants.SUBORDINATE, "getsubordinatemgr", Constants.SUBORDINATE_MGR, subMgrStatus, false);
        MasterSyncItemModel jWorkModel = new MasterSyncItemModel("Joint Work", Constants.SUBORDINATE, "getjointwork", Constants.JOINT_WORK + hqCode, jWorkStatus, false);
        subordinateModelArray.add(subModel);
        // subordinateModelArray.add(subMgrModel);
        subordinateModelArray.add(jWorkModel);

        //Other
        otherModelArray.clear();
        MasterSyncItemModel feedback = new MasterSyncItemModel(Constants.FEEDBACK, Constants.DOCTOR_MAS, "getdrfeedback", Constants.FEEDBACK, feedbackStatus, false);
        otherModelArray.add(feedback);
        //Profile
        profileModelArray.clear();
        MasterSyncItemModel profile = new MasterSyncItemModel(Constants.PROFILE, Constants.DOCTOR_MAS, "getuserdetails", Constants.PROFILE, profileStatus, false);
        profileModelArray.add(profile);

        if (SharedPref.getQuizNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel Quiz = new MasterSyncItemModel(Constants.QUIZ, "AdditionalDcr", "getquiz", Constants.QUIZ, QuizStatus, false);
            otherModelArray.add(Quiz);
        }
        if (SharedPref.getSurveyNd(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel Survey = new MasterSyncItemModel(Constants.SURVEY, Constants.SURVEY, "getsurveydetail", Constants.SURVEY, SurveyStatus, false);
            otherModelArray.add(Survey);
        }

        //Setup
        setupModelArray.clear();
        MasterSyncItemModel setupModel = new MasterSyncItemModel(Constants.SETUP, Constants.SETUP, "getsetups_edet", Constants.SETUP, setupStatus, false);
//        MasterSyncItemModel customSetupModel = new MasterSyncItemModel(Constants.CUSTOM_SETUP, customSetupCount, Constants.SETUP, "getcustomsetup", Constants.CUSTOM_SETUP, customSetupStatus, false);
        setupModelArray.add(setupModel);
//        setupModelArray.add(customSetupModel);
    }

    public void sync() {
        LinkedHashSet<MasterSyncItemModel> masterSyncItemModels = new LinkedHashSet<>();
        switch (type) {
            case "DR":
                masterSyncItemModels.add(doctorModelArray.get(0));
                if (doctorModelArray.size() > 1) {
                    masterSyncItemModels.add(doctorModelArray.get(1));
                }
                break;
            case "CH":
                masterSyncItemModels.add(chemistModelArray.get(0));
                if (chemistModelArray.size() > 1) {
                    masterSyncItemModels.add(chemistModelArray.get(1));
                }
                break;
            case "ST":
                masterSyncItemModels.add(stockiestModelArray.get(0));
                if (stockiestModelArray.size() > 1) {
                    masterSyncItemModels.add(stockiestModelArray.get(1));
                }
                break;
            case "UL":
                masterSyncItemModels.add(unlistedDrModelArray.get(0));
                if (unlistedDrModelArray.size() > 1) {
                    masterSyncItemModels.add(unlistedDrModelArray.get(1));
                }
                break;
            case "HOS":
                masterSyncItemModels.add(hospitalModelArray.get(0));
                if (hospitalModelArray.size() > 1) {
                    masterSyncItemModels.add(hospitalModelArray.get(1));
                }
                break;
            case "CIP":
                masterSyncItemModels.add(cipModelArray.get(0));
                if (cipModelArray.size() > 1) {
                    masterSyncItemModels.add(cipModelArray.get(1));
                }
                break;
            case "TM":
                masterSyncItemModels.add(clusterModelArray.get(0));
                break;
            case "WT":
                masterSyncItemModels.add(workTypeModelArray.get(0));
                break;
            case "HW":
                masterSyncItemModels.add(workTypeModelArray.get(1));
                masterSyncItemModels.add(workTypeModelArray.get(2));
                break;
            case "MI":
                masterSyncItemModels.add(dcrModelArray.get(1));
                break;
            case "PR":
                masterSyncItemModels.add(productModelArray.get(0));
                break;
            case "GIF":
                masterSyncItemModels.add(inputModelArray.get(0));
                break;
            case "SUB":
                masterSyncItemModels.add(subordinateModelArray.get(0));
                break;
            case "SE":
                masterSyncItemModels.add(setupModelArray.get(0));
                break;
            case "OTR":
                masterSyncItemModels.add(productModelArray.get(2));
                masterSyncItemModels.add(workTypeModelArray.get(1));
                masterSyncItemModels.add(workTypeModelArray.get(2));
                break;
            case "FSD":
                masterSyncItemModels.addAll(doctorModelArray);
                masterSyncItemModels.add(unlistedDrModelArray.get(0));
                masterSyncItemModels.add(clusterModelArray.get(0));
                break;
            case "AMS":
                masterSyncItemModels.addAll(doctorModelArray);
                masterSyncItemModels.addAll(chemistModelArray);
                masterSyncItemModels.addAll(stockiestModelArray);
                masterSyncItemModels.addAll(unlistedDrModelArray);
                masterSyncItemModels.addAll(hospitalModelArray);
                masterSyncItemModels.addAll(cipModelArray);
                masterSyncItemModels.addAll(clusterModelArray);
                masterSyncItemModels.addAll(inputModelArray);
                masterSyncItemModels.addAll(productModelArray);
                masterSyncItemModels.add(dcrModelArray.get(0));
                masterSyncItemModels.add(dcrModelArray.get(1));
                masterSyncItemModels.addAll(workTypeModelArray);
                masterSyncItemModels.addAll(subordinateModelArray);
                masterSyncItemModels.addAll(setupModelArray);
                break;
            case "DCR":
                masterSyncItemModels.addAll(dcrModelArray);
                break;
            case "TP":
//                masterSyncItemModels.add(tpModelArray.get(0));
//                masterSyncItemModels.add(tpModelArray.get(1));
                MasterSyncItemModel tpPlan = new MasterSyncItemModel(Constants.TOUR_PLAN, Constants.TOUR_PLAN, "gettpdetail_onebuild", Constants.TOUR_PLAN, tourPLanStatus, false);
                masterSyncItemModels.add(tpPlan);
                break;
            case "TPD":
                break;
            case "LE":
                masterSyncItemModels.addAll(leaveModelArray);
                break;
            case "STP":
                if (tpModelArray.size() > 2) {
                    masterSyncItemModels.add(tpModelArray.get(2));
                }
                if (tpModelArray.size() > 3) {
                    masterSyncItemModels.add(tpModelArray.get(3));
                }
                break;
        }
//        if (type.equalsIgnoreCase("DR")) {
//            masterSyncItemModels.add(doctorModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("CH")) {
//            masterSyncItemModels.add(chemistModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("ST")) {
//            masterSyncItemModels.add(stockiestModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("UL")) {
//            masterSyncItemModels.add(unlistedDrModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("HOS")) {
//            masterSyncItemModels.add(hospitalModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("CIP")) {
//            masterSyncItemModels.add(cipModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("TM")) {
//            masterSyncItemModels.add(clusterModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("WT")) {
//            masterSyncItemModels.add(workTypeModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("HW")) {
//            masterSyncItemModels.add(workTypeModelArray.get(1));
//            masterSyncItemModels.add(workTypeModelArray.get(2));
//        }
//        if (type.equalsIgnoreCase("MI")) {
//            masterSyncItemModels.add(dcrModelArray.get(1));
//        }
//        if (type.equalsIgnoreCase("PR")) {
//            masterSyncItemModels.add(productModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("GIF")) {
//            masterSyncItemModels.add(inputModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("SUB")) {
//            masterSyncItemModels.add(subordinateModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("SE")) {
//            masterSyncItemModels.add(setupModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("OTR")) {
//            masterSyncItemModels.add(productModelArray.get(2));
//            masterSyncItemModels.add(workTypeModelArray.get(1));
//            masterSyncItemModels.add(workTypeModelArray.get(2));
//        }
//        if (type.equalsIgnoreCase("FSD")) {
//            masterSyncItemModels.addAll(doctorModelArray);
//            masterSyncItemModels.add(unlistedDrModelArray.get(0));
//            masterSyncItemModels.add(clusterModelArray.get(0));
//        }
//        if (type.equalsIgnoreCase("AMS")) {
//            masterSyncItemModels.addAll(doctorModelArray);
//            masterSyncItemModels.addAll(chemistModelArray);
//            masterSyncItemModels.addAll(stockiestModelArray);
//            masterSyncItemModels.addAll(unlistedDrModelArray);
//            masterSyncItemModels.addAll(hospitalModelArray);
//            masterSyncItemModels.addAll(cipModelArray);
//            masterSyncItemModels.addAll(clusterModelArray);
//            masterSyncItemModels.addAll(inputModelArray);
//            masterSyncItemModels.addAll(productModelArray);
//            masterSyncItemModels.add(dcrModelArray.get(0));
//            masterSyncItemModels.add(dcrModelArray.get(1));
//            masterSyncItemModels.addAll(workTypeModelArray);
//            masterSyncItemModels.addAll(subordinateModelArray);
//            masterSyncItemModels.addAll(setupModelArray);
//        }
        masterSyncAllModel.addAll(masterSyncItemModels);

        if (masterSyncItemModels.isEmpty()) {
            new Handler().postDelayed(() -> {
                notificationDataDao.changeNotificationSyncStatus((int) id, 0);
                notificationDataDao.changeNotificationReadStatus((int) id, 1);
                NotificationDialog.dismissDialog();
                notifySyncCompleted();
            }, 2000);
        }

        apiSuccessCount = 0;
        for (int position = 0; position < masterSyncAllModel.size(); position++) {
            sync(masterSyncAllModel.get(position).getMasterOf(), masterSyncAllModel.get(position).getRemoteTableName(), masterSyncAllModel, position);
        }
    }

    private void sync(String masterOf, String remoteTableName, ArrayList<MasterSyncItemModel> masterSyncItemModels, int position) {
        try {
            apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
            jsonObject.put("tableName", remoteTableName);
            jsonObject.put("sfcode", SharedPref.getSfCode(context));
            jsonObject.put("division_code", SharedPref.getDivisionCode(context));
            jsonObject.put("hqCode", hqCode);
            jsonObject.put("Rsf", hqCode);
            jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
            apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
            switch (remoteTableName) {
                case "getholiday":
                case "getweeklyoff": {
                    jsonObject.put("year", Year.now().getValue());
                    break;
                }
                case "gettodaytpnew": {
                    jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                    break;
                }
                case "gettp_onebuild": {
                    if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                        jsonObject.put("tp_month", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_5, TimeUtils.FORMAT_8, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5)));
                        jsonObject.put("tp_year", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_5, TimeUtils.FORMAT_10, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5)));
                        break;
                    }
                }
                case "gettpdetail_onebuild": {
                    if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                        try {
                            String[] monthYearVal = monthYear.split("-");
                            if (monthYearVal.length > 1) {
                                localDate = TimeUtils.convertToLocalDate(monthYear, TimeUtils.FORMAT_43);
                            } else {
                                localDate = LocalDate.now();
                            }
                            jsonObject.put("Month", localDate.getMonthValue());
                            jsonObject.put("Year", localDate.getYear());
                        } catch (Exception e) {
                            e.printStackTrace();
                            localDate = LocalDate.now();
                            jsonObject.put("Month", localDate.getMonthValue());
                            jsonObject.put("Year", localDate.getYear());
                        }
                        break;
                    }
                }
                case "getall_tp":
                case "getall_multitpnew": {
                    jsonObject.put("tp_month", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_5, TimeUtils.FORMAT_8, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5)));
                    jsonObject.put("tp_year", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_5, TimeUtils.FORMAT_10, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5)));
                    break;
                }
                case "getquiz":
                case "getcheckin_zen":
                case "gettodaydcrmultihq":
                case "gettodaydcr": {
                    if (HomeDashBoard.selectedDate != null) {
                        jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.toString()));
                    } else {
                        WorkPlanEntriesNeeded.updateMyDayPlanEntryDates(context, false, new WorkPlanEntriesNeeded.SyncTaskStatus() {
                            @Override
                            public void datesFound() {
                                try {
                                    jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_1, SharedPref.getSelectedDateCal(context)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void noDatesFound() {
                                Log.e("Master Sync", "Get MyDayPlan Call and Date Sync failed!");
                            }
                        });
                    }
                }
            }

            Map<String, String> mapString = new HashMap<>();
            Log.e("API Object", "master sync obj : " + jsonObject);
            Call<JsonElement> call = null;
            if (masterOf.equalsIgnoreCase(Constants.DOCTOR_MAS)) {
                mapString.put("axn", "table/dcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.SUBORDINATE)) {
                mapString.put("axn", "table/subordinates");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.PRODUCT)) {
                mapString.put("axn", "table/products");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase("Leave")) {
                mapString.put("axn", "get/leave");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase("Home")) {
                mapString.put("axn", "home");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase("AdditionalDcr")) {
                mapString.put("axn", "table/additionaldcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase("Slide")) {
                mapString.put("axn", "table/slides");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.SETUP)) {
                mapString.put("axn", "table/setups");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.TOUR_PLAN)) {
                mapString.put("axn", "get/tp");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.STANDARD_TOUR_PLAN)) {
                mapString.put("axn", "get/stp");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.ACTIVITY)) {
                mapString.put("axn", "get/activity");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.SURVEY)) {
                mapString.put("axn", "get/survey");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            } else if (masterOf.equalsIgnoreCase(Constants.CHECK_IN)) {
                mapString.put("axn", "get/checKin");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }

            if (call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        masterSyncItemModels.get(position).setPBarVisibility(false);
                        ++apiSuccessCount;
                        Log.e("response :   ", remoteTableName + " : " + response.body().toString());
                        if (remoteTableName.equalsIgnoreCase("gettpdetail_onebuild")) {
                            if (response.body().isJsonArray()) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.body().getAsJsonArray().toString());
                                    if (jsonArray.length() > 0) {
                                        String status = jsonArray.getJSONObject(0).getString("Change_Status");
                                        String reason = jsonArray.getJSONObject(0).getString("Rejection_Reason");
                                        tourPlanOfflineDataDao.saveMonthlySyncStatusMaster(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, localDate.toString()), status, reason);

                                        TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDateTP(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, String.valueOf(localDate)));
                                        if (tourPlanOfflineDataTable != null) {
                                            status = tourPlanOfflineDataTable.getTpMonthSyncedOrEmpty();
                                            reason = tourPlanOfflineDataTable.getTpRejectionReasonOrEmpty();
                                        }
                                        switch (status) {
                                            case "2":
                                            case "3": {
                                                SetTpRangeStatus();
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonObject2 = new JSONObject();
                            if (response.isSuccessful()) {
                                Log.e("test", "response : " + masterOf + " -- " + remoteTableName + " : " + response.body().toString());
                                try {
                                    JsonElement jsonElement = response.body();
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                            if (!jsonObject2.has("success")) {
                                                jsonArray.put(jsonObject2);
                                                success = true;
                                            } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                                masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1); // only update sync status and no need to overwrite previously saved data when failed
                                                masterSyncItemModels.get(position).setSyncSuccess(1);
                                            }
                                        }

                                        if (success) {
                                            masterSyncItemModels.get(position).setCount(jsonArray.length());
                                            masterSyncItemModels.get(position).setSyncSuccess(2);
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString(), 2));
                                            if (masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.CALL_SYNC)) {
                                                CallDataRestClass.resetcallValues(context);
                                            } else if (masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.DATE_SYNC)) {
                                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC_DUP, jsonArray.toString(), 2));
                                            } else if (!hqCode.isEmpty() && masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.JOINT_WORK + hqCode)) {
                                                JSONObject jointWorkJsonObject = new JSONObject();
                                                jointWorkJsonObject.put("Code", SharedPref.getSfCode(context));
                                                jointWorkJsonObject.put("Name", Constants.INDEPENDENT);
                                                jointWorkJsonObject.put("SfName", Constants.INDEPENDENT);
                                                jointWorkJsonObject.put("Reporting_To_SF", "");
                                                jointWorkJsonObject.put("OwnDiv", "");
                                                jointWorkJsonObject.put("Division_Code", SharedPref.getDivisionCode(context));
                                                jointWorkJsonObject.put("SF_Status", "");
                                                jointWorkJsonObject.put("ActFlg", "");
                                                jointWorkJsonObject.put("UsrDfd_UserName", "");
                                                jointWorkJsonObject.put("DS_name", "");
                                                jointWorkJsonObject.put("sf_type", SharedPref.getSfType(context));
                                                jointWorkJsonObject.put("Desig", SharedPref.getDesig(context));
                                                jointWorkJsonObject.put("steps", "");

                                                JSONArray jointWorkJsonArray = new JSONArray();
                                                jointWorkJsonArray.put(jointWorkJsonObject);
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    jointWorkJsonObject = jsonArray.optJSONObject(i);
                                                    jointWorkJsonArray.put(jointWorkJsonObject);
                                                }
                                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.JOINT_WORK + hqCode, jointWorkJsonArray.toString(), 2));
                                            }
                                            if (masterOf.equalsIgnoreCase("AdditionalDcr") && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getstockbalance")) {
                                                if (jsonArray.length() > 0) {
                                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                    JSONArray stockBalanceArray = jsonObject1.getJSONArray("Sample_Stock");
                                                    JSONArray inputBalanceArray = jsonObject1.getJSONArray("Input_Stock");
                                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.STOCK_BALANCE, stockBalanceArray.toString(), 2));
                                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.INPUT_BALANCE, inputBalanceArray.toString(), 2));
                                                }
                                            }
//                                        else if (masterOf.equalsIgnoreCase(Constants.TOUR_PLAN) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getall_tp")) {
//                                            if(jsonArray.getJSONObject(0).toString().equalsIgnoreCase("[]")){
//                                                SharedPref.setTpSyncStaus(context,false);
//                                            }else {
//                                                SaveTourPlan(jsonArray.getJSONObject(0));
//                                                SharedPref.setTpSyncStaus(context,true);
//                                            }
//                                        } else if (masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.PROD_SLIDE)) {
//                                            if (jsonArray.length() > 0){
//                                                insertSlide(jsonArray);
//                                                if (!navigateFrom.equalsIgnoreCase("Login")) {
//                                                    SlideAlertbox(true);
//                                                }
//                                            }
//                                        } else if (masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.WELCOME_SLIDE)) {
//                                            if (jsonArray.length() > 0){
//                                                insertWelcomeSlide(jsonArray);
//                                                if (!navigateFrom.equalsIgnoreCase("Login")) {
//                                                    welcomeSlideAlertBox(true);
//                                                }
//                                            }
//                                        }
                                            else if (masterOf.equalsIgnoreCase(Constants.SETUP) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getsetups_edet")) {
                                                if (jsonArray.length() > 0) {
                                                    SharedPref.InsertLogInData(context, jsonArray.getJSONObject(0));
                                                }
                                        } else if(masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.STANDARD_TOUR_PLAN)) {
                                                if (SharedPref.getSfType(context).equalsIgnoreCase("1")) {
                                                    stpOfflineDataDao.deleteAllData("0");
                                                }
                                                saveSTPDataToLocal(hqCode);
//                                        } else if(masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.ACTIVITY)) {
//                                            activityDetailsDataDao.deleteAllData();
//                                            syncIndividualActivityDetails();
                                            }
//                                        JSONArray input = masterDataDao.getMasterDataTableOrNew(Constants.SETUP).getMasterSyncDataJsonArray();
//                                        for (int bean = 0; bean < input.length(); bean++) {
//                                            try {
//                                                JSONObject setUpObject = input.getJSONObject(bean);
//                                                String appAccess = setUpObject.getString("sanzen_edet");
//                                                if (!appAccess.equals("1")){
//                                                    CommonUtilsMethods.accessDialogBox(context);
//                                                }
//                                            } catch (JSONException e) {
//                                                throw new RuntimeException(e);
//                                            }
//                                        }
                                        }
                                    } else {
                                        masterSyncItemModels.get(position).setSyncSuccess(1);
                                        masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1);
//                                    if (navigateFrom.equalsIgnoreCase("Login")) {
//                                        masterSyncAll(false);
//                                    }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (masterOf.equalsIgnoreCase(Constants.TOUR_PLAN) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getall_tp")) {
                                    SharedPref.setTpSyncStaus(context, false);
                                }
                                masterSyncItemModels.get(position).setSyncSuccess(1);
                                masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1);
                            }
                        }
                        // when all the masters are synced and intent from Login Activity
//                        if (apiSuccessCount >= itemCount && navigateFrom.equalsIgnoreCase("Login")) {
//                            if (masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray().length() > 0) {
//                                SharedPref.putAutomassync(context, true);
//                                //    SharedPref.setSetUpClickedTab(context, "0");
//                                binding.backArrow.setVisibility(View.VISIBLE);
//                                binding.imgDownloading.setVisibility(View.VISIBLE);
//                                SlideAlertbox(true);
//                            } else if (masterDataDao.getMasterDataTableOrNew(Constants.WELCOME_SLIDE).getMasterSyncDataJsonArray().length() > 0) {
////                                SharedPref.putAutomassync(context, true);
//                                //    SharedPref.setSetUpClickedTab(context, "0");
//                                binding.backArrow.setVisibility(View.VISIBLE);
//                                binding.imgDownloading.setVisibility(View.VISIBLE);
//                                welcomeSlideAlertBox(true);
//                            } else {
//                                binding.imgDownloading.setVisibility(View.VISIBLE);
//                                binding.backArrow.setVisibility(View.VISIBLE);
//                                SharedPref.putAutomassync(context, true);
//                                //    SharedPref.setSetUpClickedTab(context, "0");
//                                Intent intent = new Intent(context, HomeDashBoard.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                            }
//                        }
                        Log.e("test", "success count : " + apiSuccessCount);
                        if (apiSuccessCount == masterSyncAllModel.size()) {
                            notificationDataDao.changeNotificationSyncStatus((int) id, 0);
                            notificationDataDao.changeNotificationReadStatus((int) id, 1);
                            NotificationDialog.dismissDialog();
                            notifySyncCompleted();
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        if (masterOf.equalsIgnoreCase(Constants.TOUR_PLAN) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getall_tp")) {
                            SharedPref.setTpSyncStaus(context, false);
                        }
                        Log.e("test", "failed : " + t);
                        ++apiSuccessCount;
                        Log.e("test", "success count at error : " + apiSuccessCount);
                        masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1);
                        masterSyncItemModels.get(position).setPBarVisibility(false);
                        masterSyncItemModels.get(position).setSyncSuccess(1);
//                        if (apiSuccessCount >= itemCount && navigateFrom.equalsIgnoreCase("Login")) {
//                            binding.backArrow.setVisibility(View.VISIBLE);
//                            SharedPref.putAutomassync(context, true);
//                            SharedPref.setSetUpClickedTab(context, 0);
//                            if (masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray().length() > 0) { // If product slide quantity is 0 then no need to display a dialog of Downloader
//                                binding.backArrow.setVisibility(View.VISIBLE);
//                                binding.imgDownloading.setVisibility(View.VISIBLE);
//                                SlideAlertbox(true);
//                            } else if (masterDataDao.getMasterDataTableOrNew(Constants.WELCOME_SLIDE).getMasterSyncDataJsonArray().length() > 0) { // If product slide quantity is 0 then no need to display a dialog of Downloader
//                                binding.backArrow.setVisibility(View.VISIBLE);
//                                binding.imgDownloading.setVisibility(View.VISIBLE);
//                                welcomeSlideAlertBox(true);
//                            } else {
//                                binding.backArrow.setVisibility(View.VISIBLE);
//                                binding.imgDownloading.setVisibility(View.VISIBLE);
//                                SharedPref.setSetUpClickedTab(context, 0);
//                                SharedPref.putAutomassync(context, true);
//                                Intent intent = new Intent(context, HomeDashBoard.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                            }
//                        }
                        if (apiSuccessCount == masterSyncAllModel.size()) {
                            notificationDataDao.changeNotificationSyncStatus((int) id, 2);
                            notificationDataDao.changeNotificationReadStatus((int) id, 0);
                            NotificationDialog.dismissDialog();
                            notifySyncCompleted();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.v("masterCheck", "--error-" + e);
            e.printStackTrace();
            notificationDataDao.changeNotificationSyncStatus((int) id, 2);
            notificationDataDao.changeNotificationReadStatus((int) id, 0);
            NotificationDialog.dismissDialog();
            notifySyncCompleted();
        }
    }

    public void SetTpRangeStatus() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        SimpleDateFormat date = new SimpleDateFormat("dd", Locale.ENGLISH);
        String mCurrDate = date.format(calendar.getTime());
        String currentDate = sdf.format(calendar.getTime());
        calendar.add(Calendar.MONTH, 1);
        String nextMonthDate = sdf.format(calendar.getTime());

        String tp_start = SharedPref.getTpStartDate(context);
        String tp_end = SharedPref.getTpEndDate(context);
        int Start_Date = Integer.parseInt(tp_start);
        int End_Date = Integer.parseInt(tp_end);
        int mCurrentDate = Integer.parseInt(mCurrDate);

        if (!tourPlanOfflineDataDao.getApprovalStatusByMonth(currentDate).equalsIgnoreCase("3")) {
            SharedPref.setTpStatus(context, true);
        } else if (!tourPlanOfflineDataDao.getApprovalStatusByMonth(nextMonthDate).equalsIgnoreCase("3") && (mCurrentDate >= Start_Date)) {
            SharedPref.setTpStatus(context, End_Date < mCurrentDate);
        } else {
            SharedPref.setTpStatus(context, false);
        }
    }

    private void saveSTPDataToLocal(String hqCode) {
        if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
                JSONArray jsonDoc_mas = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getSfCode(context)).getMasterSyncDataJsonArray();
                JSONArray jsonChm_mas = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + SharedPref.getSfCode(context)).getMasterSyncDataJsonArray();
                JSONArray jsonCluster = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getSfCode(context)).getMasterSyncDataJsonArray();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dayID = jsonObject.optString("Day_Plan_ShortName");
                        String dayCaption = jsonObject.optString("Day_Plan_Name");
                        String dayPlanCode = jsonObject.optString("Day_Plan_Code");
                        String clusterCode = jsonObject.optString("Patch_Code");
                        String clusterName = jsonObject.optString("Patch_Name");
                        String doctorCode = jsonObject.optString("Dr_Code");
                        String doctorName = jsonObject.optString("Dr_Name");
                        String chemistCode = jsonObject.optString("Chem_Code");
                        String chemistName = jsonObject.optString("Chem_Name");
                        String doctorSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Dr_Code")));
                        String chemistSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Chem_Code")));
                        String clusterSize = String.valueOf(getCountFromCommaString(jsonObject.optString("Patch_Code")));
                        String doctorSpeciality = jsonObject.optString("Speciality_Name");
                        String doctorCategory = jsonObject.optString("CategoryName");
                        String doctorCategoryCode = jsonObject.optString("CategoryCode");
                        String doctorClass = jsonObject.optString("Class_Name");
                        String dateTime = jsonObject.optString("Created_Date");
                        String activeFlag = jsonObject.optString("Active_Flag");
                        Log.d("STP master data", "saveSTPDataToLocal: " + jsonObject);

                        if ((activeFlag.equalsIgnoreCase("0") && !SharedPref.getOneBuild(context).equalsIgnoreCase("0"))
                                || (SharedPref.getOneBuild(context).equalsIgnoreCase("0") && activeFlag.equalsIgnoreCase("2"))) {
                            SharedPref.setStpStatus(context, "Approved");
                        }

                        JSONObject jsonSave = new JSONObject();
                        jsonSave = CommonUtilsMethods.CommonObjectParameter(context);
                        jsonSave.put("sfcode", hqCode);
                        jsonSave.put("DivCode", SharedPref.getDivisionCode(context));
                        jsonSave.put("Rsf", SharedPref.getHqCode(context));
                        jsonSave.put("town_code", clusterCode);
                        jsonSave.put("town_name", clusterName);
                        jsonSave.put("Doctor_Id", doctorCode);
                        jsonSave.put("Doctor_Name", doctorName);
                        jsonSave.put("Chemist_Id", chemistCode);
                        jsonSave.put("Chemist_Name", chemistName);
                        jsonSave.put("Planned_Territory_Count", clusterSize + " (" + jsonCluster.length() + ")");
                        jsonSave.put("Planned_Doctor_Count", doctorSize + " (" + jsonDoc_mas.length() + ")");
                        jsonSave.put("Planned_Chemist_Count", chemistSize + " (" + jsonChm_mas.length() + ")");
                        jsonSave.put("Planned_Hospital_Count", "0" + " (" + "0" + ")");
                        jsonSave.put("Speciality_Name", doctorSpeciality);
                        jsonSave.put("Category_Name", doctorCategory);
                        jsonSave.put("Category_Code", doctorCategoryCode);
                        jsonSave.put("Class_Name", doctorClass);
                        jsonSave.put("Plan_Name", dayCaption);
                        jsonSave.put("Plan_SName", dayID);
                        jsonSave.put("Plan_Code", dayPlanCode);
                        jsonSave.put("StpFlag", activeFlag);
                        jsonSave.put("tableName", "save_stp");
                        jsonSave.put("ReqDt", dateTime);
                        Log.d("STP save data", "saveSTPDataToLocal: " + jsonSave);
                        int stpFlag = 0;
                        try {
                            stpFlag = Integer.parseInt(activeFlag);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, hqCode, dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, jsonObject.toString(), stpFlag, "0"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dayID = jsonObject.optString("Day_Plan_ShortName");
                        String dayCaption = jsonObject.optString("Day_Plan_Name");
                        String dayPlanCode = jsonObject.optString("Day_Plan_Code");
                        String clusterCode = jsonObject.optString("Patch_Code");
                        String clusterName = jsonObject.optString("Patch_Name");
                        String doctorCode = jsonObject.optString("Dr_Code");
                        String doctorName = jsonObject.optString("Dr_Name");
                        String chemistCode = jsonObject.optString("Chem_Code");
                        String chemistName = jsonObject.optString("Chem_Name");
                        String dateTime = jsonObject.optString("Created_Date");
                        String activeFlag = jsonObject.optString("Active_Flag");
                        Log.d("STP master data", "saveSTPDataToLocal: " + jsonObject);

                        if ((activeFlag.equalsIgnoreCase("0") && !SharedPref.getOneBuild(context).equalsIgnoreCase("0"))
                                || (SharedPref.getOneBuild(context).equalsIgnoreCase("0") && activeFlag.equalsIgnoreCase("2"))) {
                            SharedPref.setStpStatus(context, "Approved");
                        }

                        JSONObject jsonSave = new JSONObject();
                        jsonSave = CommonUtilsMethods.CommonObjectParameter(context);
                        jsonSave.put("sfcode", hqCode);
                        jsonSave.put("DivCode", SharedPref.getDivisionCode(context));
                        jsonSave.put("Rsf", SharedPref.getHqCode(context));
                        jsonSave.put("town_code", clusterCode);
                        jsonSave.put("town_name", clusterName);
                        jsonSave.put("Doctor_Id", doctorCode);
                        jsonSave.put("Doctor_Name", doctorName);
                        jsonSave.put("Chemist_Id", chemistCode);
                        jsonSave.put("Chemist_Name", chemistName);
                        jsonSave.put("Plan_Name", dayCaption);
                        jsonSave.put("Plan_SName", dayID);
                        jsonSave.put("Plan_Code", dayPlanCode);
                        jsonSave.put("StpFlag", activeFlag);
                        jsonSave.put("tableName", "save_stp");
                        jsonSave.put("ReqDt", dateTime);
                        Log.d("STP save data", "saveSTPDataToLocal: " + jsonSave);
                        int stpFlag = 3;
                        try {
                            stpFlag = Integer.parseInt(activeFlag);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, hqCode, dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, jsonObject.toString(), stpFlag, "0"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getCountFromCommaString(String value) {
        if (value == null || value.trim().isEmpty()) return 0;

        int count = 0;
        for (String s : value.split(",")) {
            if (!s.trim().isEmpty()) count++;
        }
        return count;
    }

    private void notifySyncCompleted() {
        Intent intent = new Intent("com.saneforce.SYNC_COMPLETED");
        intent.putExtra("type", type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
