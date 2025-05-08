package saneforce.sanzen.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
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
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class SyncManager {
    private int doctorStatus = 0, specialityStatus = 0, qualificationStatus = 0, categoryStatus = 0, departmentStatus = 0, classStatus = 0, feedbackStatus = 0, unlistedDrStatus = 0, chemistStatus = 0, stockiestStatus = 0, hospitalStatus = 0, cipStatus = 0, inputStatus = 0, leaveStatus = 0, leaveStatusStatus = 0, tpSetupStatus = 0, tourPLanStatus = 0, stpSetupStatus = 0, standardTourPLanStatus = 0, clusterStatus = 0, callSyncStatus = 0, myDayPlanStatus = 0, visitControlStatus = 0, dateSyncStatus = 0, stockBalanceStatus = 0, calenderEventStaus = 0, productStatus = 0, proCatStatus = 0, brandStatus = 0, compProStatus = 0, mapCompPrdStatus = 0, activityStatus = 0, workTypeStatus = 0, holidayStatus = 0, weeklyOfStatus = 0, proSlideStatus = 0, proSpeSlideStatus = 0, brandSlideStatus = 0, therapticStatus = 0, welcomeStatus = 0, subordinateStatus = 0, subMgrStatus = 0, jWorkStatus = 0, QuizStatus = 0, SurveyStatus = 0, setupStatus = 0;
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
    private final ArrayList<MasterSyncItemModel> setupModelArray = new ArrayList<>();
    private final Context context;
    private final String hqCode, type;
    private final long id;
    private final ArrayList<MasterSyncItemModel> masterSyncAllModel = new ArrayList<>();
    private final MasterDataDao masterDataDao;
    private final NotificationDataDao notificationDataDao;
    private ApiInterface apiInterface;
    private int apiSuccessCount = 0;

    public SyncManager(Context context, String hqCode, String type, long id) {
        this.context = context;
        this.hqCode = hqCode;
        this.type = type;
        this.id = id;
        RoomDB db = RoomDB.getDatabase(context);
        masterDataDao = db.masterDataDao();
        notificationDataDao = db.notificationDataDao();
        getRequiredData();
        prepareArray();
    }

    private void getRequiredData() {
        doctorStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DOCTOR + hqCode);
        specialityStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SPECIALITY);
        qualificationStatus = masterDataDao.getMasterSyncStatusByKey(Constants.QUALIFICATION);
        categoryStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CATEGORY);
        departmentStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DEPARTMENT);
        classStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CLASS);
        feedbackStatus = masterDataDao.getMasterSyncStatusByKey(Constants.FEEDBACK);
        unlistedDrStatus = masterDataDao.getMasterSyncStatusByKey(Constants.UNLISTED_DOCTOR + hqCode);
        chemistStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CHEMIST + hqCode);
        stockiestStatus = masterDataDao.getMasterSyncStatusByKey(Constants.STOCKIEST + hqCode);
        hospitalStatus = masterDataDao.getMasterSyncStatusByKey(Constants.HOSPITAL + hqCode);
        cipStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CIP + hqCode);
        inputStatus = masterDataDao.getMasterSyncStatusByKey(Constants.INPUT);
        leaveStatus = masterDataDao.getMasterSyncStatusByKey(Constants.LEAVE);
        leaveStatusStatus = masterDataDao.getMasterSyncStatusByKey(Constants.LEAVE_STATUS);
        callSyncStatus = masterDataDao.getMasterSyncStatusByKey(Constants.CALL_SYNC);
        myDayPlanStatus = masterDataDao.getMasterSyncStatusByKey(Constants.WORK_PLAN);
        visitControlStatus = masterDataDao.getMasterSyncStatusByKey(Constants.VISIT_CONTROL);
        dateSyncStatus = masterDataDao.getMasterSyncStatusByKey(Constants.DATE_SYNC);
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
        setupStatus = masterDataDao.getMasterSyncStatusByKey(Constants.SETUP);
    }

    public void prepareArray() {

        //Listed Doctor
        doctorModelArray.clear();
        if(SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel doctorModel = new MasterSyncItemModel(SharedPref.getDrCap(context), Constants.DOCTOR, "getdoctors", Constants.DOCTOR + hqCode, doctorStatus, false);
            MasterSyncItemModel spl = new MasterSyncItemModel(Constants.SPECIALITY, Constants.DOCTOR, "getspeciality", Constants.SPECIALITY, specialityStatus, false);
            MasterSyncItemModel ql = new MasterSyncItemModel(Constants.QUALIFICATION, Constants.DOCTOR, "getquali", Constants.QUALIFICATION, qualificationStatus, false);
            MasterSyncItemModel cat = new MasterSyncItemModel(Constants.CATEGORY, Constants.DOCTOR, "getcategorys", Constants.CATEGORY, categoryStatus, false);
            //    MasterSyncItemModel dep = new MasterSyncItemModel(Constants.DEPARTMENT, departmentCount, Constants.DOCTOR, "getdeparts", Constants.DEPARTMENT, departmentStatus, false);
            MasterSyncItemModel clas = new MasterSyncItemModel(Constants.CLASS, Constants.DOCTOR, "getclass", Constants.CLASS, classStatus, false);
            doctorModelArray.add(doctorModel);
            doctorModelArray.add(spl);
            doctorModelArray.add(ql);
            doctorModelArray.add(cat);
            //  doctorModelArray.add(dep);
            doctorModelArray.add(clas);
        }

        //Chemist
        chemistModelArray.clear();
        if(SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel cheModel = new MasterSyncItemModel(SharedPref.getChmCap(context), Constants.DOCTOR, "getchemist", Constants.CHEMIST + hqCode, chemistStatus, false);
            MasterSyncItemModel chemistCategory = new MasterSyncItemModel(Constants.CATEGORY, Constants.DOCTOR, "getchem_categorys", Constants.CATEGORY_CHEMIST, categoryStatus, false);
            chemistModelArray.add(cheModel);
            chemistModelArray.add(chemistCategory);
        }

        //Stockiest
        stockiestModelArray.clear();
        if(SharedPref.getStkNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel stockModel = new MasterSyncItemModel(SharedPref.getStkCap(context), Constants.DOCTOR, "getstockist", Constants.STOCKIEST + hqCode, stockiestStatus, false);
            stockiestModelArray.add(stockModel);
        }

        //Unlisted Dr
        unlistedDrModelArray.clear();
        if(SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel unListModel = new MasterSyncItemModel(SharedPref.getUNLcap(context), Constants.DOCTOR, "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, unlistedDrStatus, false);
            unlistedDrModelArray.add(unListModel);
        }

        //Hospital
        hospitalModelArray.clear();
        if(SharedPref.getHospNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel hospModel = new MasterSyncItemModel(SharedPref.getHospCaption(context), Constants.DOCTOR, "gethospital", Constants.HOSPITAL + hqCode, hospitalStatus, false);
            hospitalModelArray.add(hospModel);
        }

        //CIP
        cipModelArray.clear();
        if(SharedPref.getCipNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel ciModel = new MasterSyncItemModel(SharedPref.getCipCaption(context), Constants.DOCTOR, "getcip", Constants.CIP + hqCode, cipStatus, false);
            cipModelArray.add(ciModel);
        }

        //Cluster
        clusterModelArray.clear();
        MasterSyncItemModel cluster = new MasterSyncItemModel(SharedPref.getClusterCap(context), Constants.DOCTOR, "getterritory", Constants.CLUSTER + hqCode, clusterStatus, false);
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
        if(SharedPref.getRcpaNd(context).equalsIgnoreCase("0") || SharedPref.getChmRcpaNeed(context).equalsIgnoreCase("0")) {
            //     MasterSyncItemModel compProductModel = new MasterSyncItemModel(Constants.COMPETITOR_PROD, compProCount, Constants.PRODUCT, "getcompdet", Constants.COMPETITOR_PROD, compProStatus, false);
            MasterSyncItemModel mapCompPrdModel = new MasterSyncItemModel(Constants.MAPPED_COMPETITOR_PROD, "AdditionalDcr", "getmapcompdet", Constants.MAPPED_COMPETITOR_PROD, mapCompPrdStatus, false);
            //  productModelArray.add(compProductModel);
            productModelArray.add(mapCompPrdModel);
        }

        //Leave
        leaveModelArray.clear();
        MasterSyncItemModel leaveModel = new MasterSyncItemModel(Constants.LEAVE, "Leave", "getleavetype", Constants.LEAVE, leaveStatus, false);
        leaveModelArray.add(leaveModel);
        if(SharedPref.getLeaveEntitlementNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel leaveStatusModel = new MasterSyncItemModel(Constants.LEAVE_STATUS, "Leave", "getleavestatus", Constants.LEAVE_STATUS, leaveStatusStatus, false);
            leaveModelArray.add(leaveStatusModel);
        }

        //DCR
        dcrModelArray.clear();
        MasterSyncItemModel callSyncModel = new MasterSyncItemModel(Constants.CALL_SYNC, "Home", "gethome", Constants.CALL_SYNC, callSyncStatus, false);
        MasterSyncItemModel dateSyncModel = new MasterSyncItemModel(Constants.DATE_SYNC, "Home", "getdcrdate", Constants.DATE_SYNC, dateSyncStatus, false);
        MasterSyncItemModel myDayPlanModel = new MasterSyncItemModel(Constants.WORK_PLAN, Constants.DOCTOR, "gettodaydcr", Constants.WORK_PLAN, myDayPlanStatus, false);

        //   MasterSyncItemModel EventCallSync = new MasterSyncItemModel("Status", -1, "AdditionalDcr", "gettodycalls", Constants.CALENDER_EVENT_STATUS, calenderEventStaus, false);
        dcrModelArray.add(callSyncModel);
        dcrModelArray.add(dateSyncModel);
        dcrModelArray.add(myDayPlanModel);


        if(SharedPref.getSampleValidation(context).equalsIgnoreCase("1") || SharedPref.getInputValidation(context).equalsIgnoreCase("1")) {
            MasterSyncItemModel stockBalanceModel = new MasterSyncItemModel(Constants.STOCK_BALANCE, "AdditionalDcr", "getstockbalance", Constants.STOCK_BALANCE_MASTER, stockBalanceStatus, false);
            dcrModelArray.add(stockBalanceModel);
        }
        if(SharedPref.getVstNd(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel visitControlModel = new MasterSyncItemModel(Constants.VISIT_CONTROL, "AdditionalDcr", "getvisit_contro", Constants.VISIT_CONTROL, visitControlStatus, false);
            dcrModelArray.add(visitControlModel);
        }

        //Activity
        activityModelArray.clear();
        MasterSyncItemModel activity = new MasterSyncItemModel(Constants.ACTIVITY, Constants.ACTIVITY, "getdynactivity", Constants.ACTIVITY, activityStatus, false);
        activityModelArray.add(activity);

        //Work Type
        workTypeModelArray.clear();
        MasterSyncItemModel workType = new MasterSyncItemModel(Constants.WORK_TYPE, Constants.DOCTOR, "getworktype", Constants.WORK_TYPE, workTypeStatus, false);
        MasterSyncItemModel holiday = new MasterSyncItemModel(Constants.HOLIDAY, Constants.DOCTOR, "getholiday", Constants.HOLIDAY, holidayStatus, false);
        MasterSyncItemModel weeklyOff = new MasterSyncItemModel(Constants.WEEKLY_OFF, Constants.DOCTOR, "getweeklyoff", Constants.WEEKLY_OFF, weeklyOfStatus, false);
        workTypeModelArray.add(workType);
        workTypeModelArray.add(holiday);
        workTypeModelArray.add(weeklyOff);

        //Tour Plan
        tpModelArray.clear();
        boolean tpNeed = SharedPref.getTpNeed(context).equalsIgnoreCase("0"), stpNeed = SharedPref.getStpNeed(context).equalsIgnoreCase("0") && !SharedPref.getSfType(context).equalsIgnoreCase("2");
        if(tpNeed) {
            MasterSyncItemModel tpSetup = new MasterSyncItemModel(Constants.TP_SETUP, Constants.SETUP, "gettpsetup", Constants.TP_SETUP, tpSetupStatus, false);
            MasterSyncItemModel tPlan = new MasterSyncItemModel(Constants.TOUR_PLAN, Constants.TOUR_PLAN, "getall_tp", Constants.TOUR_PLAN, tourPLanStatus, false);
            tpModelArray.add(tpSetup);
            tpModelArray.add(tPlan);
        }
        if(stpNeed) {
            String stpCaption = SharedPref.getStpCaption(context), stpSetupCaption = Constants.STP_SETUP;
            if(!stpCaption.isEmpty()) {
                stpSetupCaption = stpCaption + " Setup";
            }else {
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
        if(SharedPref.getTherapticPresentationNeed(context).equalsIgnoreCase("0")) {
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
        MasterSyncItemModel feedback = new MasterSyncItemModel(Constants.FEEDBACK, Constants.DOCTOR, "getdrfeedback", Constants.FEEDBACK, feedbackStatus, false);
        otherModelArray.add(feedback);
        if(SharedPref.getQuizNeed(context).equalsIgnoreCase("0")) {
            MasterSyncItemModel Quiz = new MasterSyncItemModel(Constants.QUIZ, "AdditionalDcr", "getquiz", Constants.QUIZ, QuizStatus, false);
            otherModelArray.add(Quiz);
        }
        if(SharedPref.getSurveyNd(context).equalsIgnoreCase("0")) {
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
        if(type.equalsIgnoreCase("DR")) {
            masterSyncItemModels.add(doctorModelArray.get(0));
        }
        if(type.equalsIgnoreCase("CH")) {
            masterSyncItemModels.add(chemistModelArray.get(0));
        }
        if(type.equalsIgnoreCase("ST")) {
            masterSyncItemModels.add(stockiestModelArray.get(0));
        }
        if(type.equalsIgnoreCase("UL")) {
            masterSyncItemModels.add(unlistedDrModelArray.get(0));
        }
        if(type.equalsIgnoreCase("HOS")) {
            masterSyncItemModels.add(hospitalModelArray.get(0));
        }
        if(type.equalsIgnoreCase("CIP")) {
            masterSyncItemModels.add(cipModelArray.get(0));
        }
        if(type.equalsIgnoreCase("TM")) {
            masterSyncItemModels.add(clusterModelArray.get(0));
        }
        if(type.equalsIgnoreCase("WT")) {
            masterSyncItemModels.add(workTypeModelArray.get(0));
        }
        if(type.equalsIgnoreCase("HW")) {
            masterSyncItemModels.add(workTypeModelArray.get(1));
            masterSyncItemModels.add(workTypeModelArray.get(2));
        }
        if(type.equalsIgnoreCase("MI")) {
            masterSyncItemModels.add(dcrModelArray.get(1));
        }
        if(type.equalsIgnoreCase("PR")) {
            masterSyncItemModels.add(productModelArray.get(0));
        }
        if(type.equalsIgnoreCase("GIF")) {
            masterSyncItemModels.add(inputModelArray.get(0));
        }
        if(type.equalsIgnoreCase("SUB")) {
            masterSyncItemModels.add(subordinateModelArray.get(0));
        }
        if(type.equalsIgnoreCase("SE")) {
            masterSyncItemModels.add(setupModelArray.get(0));
        }
        if(type.equalsIgnoreCase("FSD")) {
            masterSyncItemModels.addAll(doctorModelArray);
            masterSyncItemModels.add(unlistedDrModelArray.get(0));
            masterSyncItemModels.add(clusterModelArray.get(0));
        }
        if(type.equalsIgnoreCase("AMS")) {
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
        }
        masterSyncAllModel.addAll(masterSyncItemModels);

        apiSuccessCount = 0;
        for (int position = 0; position<masterSyncAllModel.size(); position++) {
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
            switch (remoteTableName){
                case "getholiday":
                case "getweeklyoff":{
                    jsonObject.put("year", Year.now().getValue());
                    break;
                }
                case "gettodaytpnew":{
                    jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                    break;
                }
                case "getall_tp":{
                    jsonObject.put("tp_month", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_5, TimeUtils.FORMAT_8, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5)));
                    jsonObject.put("tp_year", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_5, TimeUtils.FORMAT_10, TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_5)));
                    break;
                }
                case "getquiz":
                case "gettodaydcr":{
                    if(HomeDashBoard.selectedDate != null) {
                        jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.toString()));
                    }else {
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
            if(masterOf.equalsIgnoreCase(Constants.DOCTOR)) {
                mapString.put("axn", "table/dcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase(Constants.SUBORDINATE)) {
                mapString.put("axn", "table/subordinates");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase(Constants.PRODUCT)) {
                mapString.put("axn", "table/products");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase("Leave")) {
                mapString.put("axn", "get/leave");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase("Home")) {
                mapString.put("axn", "home");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase("AdditionalDcr")) {
                mapString.put("axn", "table/additionaldcrmasterdata");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase("Slide")) {
                mapString.put("axn", "table/slides");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase(Constants.SETUP)) {
                mapString.put("axn", "table/setups");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase(Constants.TOUR_PLAN)) {
                mapString.put("axn", "get/tp");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase(Constants.STANDARD_TOUR_PLAN)) {
                mapString.put("axn", "get/stp");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase(Constants.ACTIVITY)) {
                mapString.put("axn", "get/activity");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }else if(masterOf.equalsIgnoreCase(Constants.SURVEY)) {
                mapString.put("axn", "get/survey");
                call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            }

            if(call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        masterSyncItemModels.get(position).setPBarVisibility(false);
                        ++apiSuccessCount;
                        Log.e("response :   ", remoteTableName + " : " + response.body().toString());

                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject2 = new JSONObject();
                        if(response.isSuccessful()) {
                            Log.e("test", "response : " + masterOf + " -- " + remoteTableName + " : " + response.body().toString());
                            try {
                                JsonElement jsonElement = response.body();
                                if(!jsonElement.isJsonNull()) {
                                    if(jsonElement.isJsonArray()) {
                                        jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                        success = true;
                                    }else if(jsonElement.isJsonObject()) {
                                        jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                        if(!jsonObject2.has("success")) {
                                            // response as jsonObject with {"success" : "fail" } will be received only when there are unformed object passed or there are no data in back end.
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                        }else if(jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1); // only update sync status and no need to overwrite previously saved data when failed
                                            masterSyncItemModels.get(position).setSyncSuccess(1);
                                        }
                                    }

                                    if(success) {
                                        masterSyncItemModels.get(position).setCount(jsonArray.length());
                                        masterSyncItemModels.get(position).setSyncSuccess(2);
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(masterSyncItemModels.get(position).getLocalTableKeyName(), jsonArray.toString(), 2));
                                        if(masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.CALL_SYNC)) {
                                            CallDataRestClass.resetcallValues(context);
                                        }else if(masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.DATE_SYNC)) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC_DUP, jsonArray.toString(), 2));
                                        }else if(!hqCode.isEmpty() && masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.JOINT_WORK + hqCode)) {
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
                                            for (int i = 0; i<jsonArray.length(); i++) {
                                                jointWorkJsonObject = jsonArray.optJSONObject(i);
                                                jointWorkJsonArray.put(jointWorkJsonObject);
                                            }
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.JOINT_WORK + hqCode, jointWorkJsonArray.toString(), 2));
                                        }
                                        if(masterOf.equalsIgnoreCase("AdditionalDcr") && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getstockbalance")) {
                                            if(jsonArray.length()>0) {
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
                                        else if(masterOf.equalsIgnoreCase(Constants.SETUP) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getsetups_edet")) {
                                            if(jsonArray.length()>0) {
                                                SharedPref.InsertLogInData(context, jsonArray.getJSONObject(0));
                                            }
//                                        } else if(masterSyncItemModels.get(position).getLocalTableKeyName().equalsIgnoreCase(Constants.STANDARD_TOUR_PLAN)) {
//                                            stpOfflineDataDao.deleteAllData("0");
//                                            saveSTPDataToLocal();
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
                                }else {
                                    masterSyncItemModels.get(position).setSyncSuccess(1);
                                    masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1);
//                                    if (navigateFrom.equalsIgnoreCase("Login")) {
//                                        masterSyncAll(false);
//                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            if(masterOf.equalsIgnoreCase(Constants.TOUR_PLAN) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getall_tp")) {
                                SharedPref.setTpSyncStaus(context, false);
                            }
                            masterSyncItemModels.get(position).setSyncSuccess(1);
                            masterDataDao.saveMasterSyncStatus(masterSyncItemModels.get(position).getLocalTableKeyName(), 1);
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
                        if(apiSuccessCount == masterSyncAllModel.size()) {
                            notificationDataDao.changeNotificationSyncStatus((int) id, 0);
                            notificationDataDao.changeNotificationReadStatus((int) id, 1);
                            NotificationDialog.dismissDialog();
                            notifySyncCompleted();
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        if(masterOf.equalsIgnoreCase(Constants.TOUR_PLAN) && masterSyncItemModels.get(position).getRemoteTableName().equalsIgnoreCase("getall_tp")) {
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
                        if(apiSuccessCount == masterSyncAllModel.size()) {
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

    private void notifySyncCompleted() {
        Intent intent = new Intent("com.saneforce.SYNC_COMPLETED");
        intent.putExtra("type", type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
