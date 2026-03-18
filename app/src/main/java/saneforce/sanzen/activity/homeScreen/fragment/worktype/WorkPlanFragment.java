package saneforce.sanzen.activity.homeScreen.fragment.worktype;

import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.SetupOutBoxAdapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BulletSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.stp.model.STPModelList;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.fragment.CallsFragment;
import saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment;
import saneforce.sanzen.activity.homeScreen.modelClass.MultiHQClusterItem;
import saneforce.sanzen.activity.homeScreen.modelClass.MultiHQExpandItem;
import saneforce.sanzen.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQHeaderModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQItemModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.commonClasses.CheckInOutManager;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.STPDaySorter;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.commonClasses.WorkPlanEntriesNeeded;
import saneforce.sanzen.databinding.WorkplanFragmentBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataTable;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDataTable;
import saneforce.sanzen.roomdatabase.OutboxUtil;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataTable;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class WorkPlanFragment extends Fragment implements View.OnClickListener {
    public String chk_cluster = "", chk_hq = "";
    public static ArrayList<Multicheckclass_clust> listSelectedCluster = new ArrayList<>();
    public HashMap<String, ArrayList<MultiHQClusterItem>> mapSelectedCluster = new HashMap<>();
    public ArrayList<Multicheckclass_clust> listSelectedHQ = new ArrayList<>();
    public static String mTowncode1 = "", mTownname1 = "", mWTCode1 = "", mWTName1 = "", mFwFlg1 = "", mHQCode1 = "", mHQName1 = "", mRemarks1 = "", mTowncode2 = "", mTownname2 = "", mWTCode2 = "", mWTName2 = "", mFwFlg2 = "", mHQCode2 = "", mHQName2 = "", mHQCode = "", mTowncode = "", mTownname = "", mWTCode = "", mWTName = "", mFwFlg = "", mHQName = "", mFinalRemarks = "", mTerratiry1 = "", mTerratiry2 = "", dayStatus = "", tpWorkType = "", tpCluster = "", tpDoctor = "",tpChemists="", tpWorkType2 = "", tpCluster2 = "", tpDoctor2 = "",tpChemists2="", deviation = "0", remarks = "", tpApprovalStatus = "", workDayName = "", workDayCode = "", workDayName2 = "", workDayCode2 = "";
    @SuppressLint("StaticFieldLeak")
    public static WorkplanFragmentBinding binding;
    ProgressDialog progressDialog;
    String CheckInOutStatus, FinalSubmitStatus, hqCode = "", rejectedReason = "", deviationRejectedReason = "";
    JSONObject jsonObject = new JSONObject(), deviationJSONObject = new JSONObject();
    JSONObject deleteJsonObject = new JSONObject();
    public static JSONObject tpDataObj = null;
    ArrayList<JSONObject> workType_list1 = new ArrayList<>();
    public ArrayList<Multicheckclass_clust> multiple_cluster_list = new ArrayList<>();
    public ArrayList<MultiHQExpandItem> multiHQExpandItems = new ArrayList<>();
    public ArrayList<Multicheckclass_clust> multiple_hq_list = new ArrayList<>();
    ArrayList<JSONObject> HQList = new ArrayList<>();
    ArrayList<JSONObject> cluster = new ArrayList<>();
    ArrayList<JSONObject> stpModelList = new ArrayList<>();
    JSONObject SelectedWorkType;
    JSONObject SelectedHQ;
    JSONObject SelectedWorkDay;
    ApiInterface api_interface;
    List<String> SynqList = new ArrayList<>();
    String strClusterID = "", strClusterName = "", strHQCode = "", strHQName = "", previousWTCode1 = "", previousWTCode2 = "";
    String DayPlanCount = "1", IsFeildWorkFlag = "F0", EditSession = "", insMode = "0";
    CommonUtilsMethods commonUtilsMethods;
    double latitude, longitude;
    GPSTrack gpsTrack;
    Dialog dialogCheckOut, dialogCheckIn;
    TextView tvDateTimeAfter, tvLat, tvLong, tvAddress, tvHeading, tvName, tvDateTime;
    TextView tv_address_in, tv_dateTime_in, tvLatLong_in;
    TextView tv_address, tv_dateTime, tvLatLong;
    ImageView imgClose;
    Button btnCheckOut, btnCheckIn;
    String address;
    JSONObject jsonCheck;
    JSONObject finalSubmitJSONObject;
    boolean NeedClusterFlag1 = false, NeedClusterFlag2 = false;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao;
    private OfflineDaySubmitDao offlineDaySubmitDao;
    private TourPlanOfflineDataDao tourPlanOfflineDataDao;
    private STPOfflineDataDao stpOfflineDataDao;
    ArrayList<MasterSyncItemModel> masterSyncArray = new ArrayList<>();
    private ProgressDialog syncProgressDialog, finalSubmitDialog;
    private int syncCount = 0, requiredSyncCount = 0;
    private String CheckInOutNeed, STPNeed, STPBasedMTP, STPBasedDCR, TPNeed, TPMandatory, TPBasedDCR, TPDCRDeviation, TPDCRMGRApprNeed;
    public static boolean isFromTP = false;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static WorkPlanFragment workPlanFragment;
    private boolean isRefreshCalled = false;
    private long lastClickTime = 0;
    private Handler handler;
    private Runnable runnable;
    private int limit = 1;
    private OutboxUtil outboxUtil;
    private Handler dateHandler;
    private String status;

    private void checkDateChange() {
        if (!isAdded()) return;
        if (!(SharedPref.getDcrSequential(requireContext()).equalsIgnoreCase("0")
                && SharedPref.getSeqDlyCtrl(requireContext()).equalsIgnoreCase("1"))) {
            return;
        }
        String savedDate = SharedPref.getLastKnownDate(requireContext());
        String today = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4);
        if (!savedDate.isEmpty() && !savedDate.equals(today)) {
            SharedPref.setLastKnownDate(requireContext(), "");
            onDateChanged();
        }
    }

    private void onDateChanged() {
        if (!(SharedPref.getDcrSequential(requireContext()).equalsIgnoreCase("0")
                && SharedPref.getSeqDlyCtrl(requireContext()).equalsIgnoreCase("1"))) {
            return;
        } else {
            if (!SharedPref.getSeqDcrLockDays(requireContext()).equalsIgnoreCase("0")
                    && (!SharedPref.getGeotagNeed(requireContext()).equalsIgnoreCase("1")
                    && !SharedPref.getGeotagNeedChe(requireContext()).equalsIgnoreCase("1")
                    && !SharedPref.getGeotagNeedStock(requireContext()).equalsIgnoreCase("1")
                    && !SharedPref.getGeotagNeedUnlst(requireContext()).equalsIgnoreCase("1"))) {
                return;
            }
        }
        if (SharedPref.getIsWorkingToday(requireContext())) {
//        JSONArray callSync = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
//        for (int i = callSync.length() - 1; i >= 0; i--) {
//            JSONObject jsonObject = callSync.optJSONObject(i);
//            if (jsonObject.optString("Dcr_dt").equalsIgnoreCase(HomeDashBoard.selectedDate.toString())
//                    && jsonObject.optString("CustCode").equalsIgnoreCase("0")
//                    && jsonObject.optString("day_status").equalsIgnoreCase("0")) {
            if (finalSubmitDialog != null) {
//                finalSubmitDialog.show();
            }
            finalSubmit("Auto Submitted", true);
//                break;
//            }
//        }
        }
    }

    private void startDateWatcher() {
        if (!(SharedPref.getDcrSequential(requireContext()).equalsIgnoreCase("0")
                && SharedPref.getSeqDlyCtrl(requireContext()).equalsIgnoreCase("1"))) {
            return;
        }
        dateHandler = new Handler(Looper.getMainLooper());
        dateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkDateChange();
                dateHandler.postDelayed(this, 60 * 1000);
            }
        }, 60 * 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ACTIVITY_STATUS", "OnResume");
        checkDateChange();
//        if (!SharedPref.getCheckDateTodayPlan(requireContext()).equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()))) {
//            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, "[]", 0));
//        if(HomeDashBoard.selectedDate != null && !HomeDashBoard.selectedDate.toString().isEmpty()) {
//            if(UtilityClass.isNetworkAvailable(requireContext())) {
//                syncMyDayPlan();
//                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
//            }else {
//                setUpMyDayplan();
//            }
//        }
//        } else {
//            setUpMyDayplan();
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        startDateWatcher();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dateHandler != null) {
            dateHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeDashBoard) {
            syncProgressDialog = new ProgressDialog(context);
            syncProgressDialog.setMessage(context.getString(R.string.head_quarters_syncing));
            syncProgressDialog.setCancelable(false);
            syncProgressDialog.setIndeterminate(true);

            finalSubmitDialog = new ProgressDialog(context);
            finalSubmitDialog.setMessage(context.getString(R.string.final_submit));
            finalSubmitDialog.setCancelable(false);
            finalSubmitDialog.setIndeterminate(true);
//            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WorkplanFragmentBinding.inflate(inflater);
        View view = binding.getRoot();
        Log.d("ACTIVITY_STATUS", "oncreateview");
        outboxUtil = new OutboxUtil(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        offlineCheckInOutDataDao = roomDB.offlineCheckInOutDataDao();
        callOfflineWorkTypeDataDao = roomDB.callOfflineWorkTypeDataDao();
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
        tourPlanOfflineDataDao = roomDB.tourPlanOfflineDataDao();
        stpOfflineDataDao = roomDB.stpOfflineDataDao();
        gpsTrack = new GPSTrack(requireActivity());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        chk_cluster = "";
        deviationRejectedReason = "";
        tpDataObj = null;
        isRefreshCalled = false;
        hqCode = SharedPref.getHqCode(requireContext());
        if (SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
            status = "2";
        } else {
            status = "0";
        }

        if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")) {
            binding.btnSubmit.setText(requireContext().getString(R.string.final_submit_check_out));
        } else {
            binding.btnSubmit.setText(requireContext().getString(R.string.final_submit));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.progressHq1.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            binding.progressHq2.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
            binding.progressSumit1.setIndeterminateTintList(ColorStateList.valueOf(Color.BLACK));
        }

        api_interface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));

        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
            binding.rlheadquates1.setVisibility(View.VISIBLE);
            binding.rlheadquates2.setVisibility(View.VISIBLE);
        } else {
            binding.rlheadquates1.setVisibility(View.GONE);
            binding.rlheadquates2.setVisibility(View.GONE);
        }

        binding.btnSubmit.setOnClickListener(this);
        binding.rlworktype1.setOnClickListener(this);
        binding.rlcluster1.setOnClickListener(this);
        binding.rlheadquates1.setOnClickListener(this);
        binding.txtAddPlan.setOnClickListener(this);
        binding.txtSave.setOnClickListener(this);
        binding.rlworktype2.setOnClickListener(this);
        binding.rlcluster2.setOnClickListener(this);
        binding.rlheadquates2.setOnClickListener(this);
        binding.llDelete.setOnClickListener(this);
        binding.closeRejectedReason.setOnClickListener(this);
        binding.flSession1.setOnClickListener(this);
        binding.flSession2.setOnClickListener(this);
        binding.rlworkday1.setOnClickListener(this);
        binding.rlworkday2.setOnClickListener(this);
        binding.txtRefresh.setOnClickListener(this);

        if (binding.switchButton.isChecked()) {
            binding.switchButton.getThumbDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.white));
            binding.switchButton.getTrackDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.green_60));
        } else {
            binding.switchButton.getThumbDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.white));
            binding.switchButton.getTrackDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.mildRed));
        }

        binding.switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                enableEditSession1();
//                if(DayPlanCount.equalsIgnoreCase("2")) {
//                    enableEditSession2();
//                }
                binding.switchButton.getThumbDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.white));
                binding.switchButton.getTrackDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.green_60));
                if (UtilityClass.isNetworkAvailable(requireContext())) {
                    deviation = "1";
                } else {
                    deviation = "0";
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                    binding.switchButton.setChecked(false);
                    binding.switchButton.getThumbDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.white));
                    binding.switchButton.getTrackDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.mildRed));
                }
            } else {
                binding.switchButton.getThumbDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.white));
                binding.switchButton.getTrackDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.mildRed));
                deviation = "0";
                disableSession1();
                if (DayPlanCount.equalsIgnoreCase("2")) {
                    disableSession2();
                }
                setUpWorkPlan();
            }
        });

        boolean toSync = false;
        if (SharedPref.getWrkAreaName(requireContext()).isEmpty() || SharedPref.getWrkAreaName(requireContext()).equalsIgnoreCase(null)) {
            binding.txtCluster1.setHint(getString(R.string.select) + " " + SharedPref.getClusterCap(requireContext()));
        } else {
            binding.txtCluster1.setHint(getString(R.string.select) + " " + SharedPref.getWrkAreaName(requireContext()));
        }
        if (HomeDashBoard.selectedDate != null && !HomeDashBoard.selectedDate.toString().isEmpty()) {
            try {
                JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();
                if (workTypeArray.length() > 0) {
                    JSONObject FirstSeasonDayPlanObject = workTypeArray.getJSONObject(0);
                    String DayPlanDate1 = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_4, FirstSeasonDayPlanObject.getJSONObject("TPDt").getString("date"));
                    String CurrentDate = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                    if (!DayPlanDate1.equalsIgnoreCase(CurrentDate)) {
                        toSync = true;
                    }
                    Log.e("WorkPlanFragment", "onCreateView: " + DayPlanDate1 + " " + CurrentDate + " " + toSync);
                } else {
                    toSync = true;
                }
            } catch (Exception e) {
                Log.e("WorkPlanFragment", "onCreateView: " + e.getMessage());
                e.printStackTrace();
            }
        }

        getLocalData();
//        if (!SharedPref.getCheckDateTodayPlan(requireContext()).equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()))) {
//            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.MY_DAY_PLAN, "[]", 0));
        if (toSync) {
            if (UtilityClass.isNetworkAvailable(requireContext())) {
                binding.progress.setVisibility(View.VISIBLE);
                syncMyDayPlan(false);
                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
            } else {
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 1));
                setUpWorkPlan();
            }
        } else {
            setUpWorkPlan();
        }
//        } else {
//            setUpMyDayplan();
//        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = requireContext();
        workPlanFragment = this;
    }

    public void ShowWorkTypeAlert(TextView mTxtWorktype, RelativeLayout rlculster, RelativeLayout rlHQ) {
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.tvSearchheader.setText(requireContext().getString(R.string.worktype));
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.GONE);
        WorkplanListAdapter WT_ListAdapter = new WorkplanListAdapter(getActivity(), workType_list1, "1");
        HomeDashBoard.binding.llNav.wkListView.setAdapter(WT_ListAdapter);

        HomeDashBoard.binding.llNav.wkListView.setOnItemClickListener((parent, view, position, id) -> {
            HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
            UtilityClass.hideKeyboard(requireActivity());
            SelectedWorkType = WT_ListAdapter.getlisted().get(position);
            try {
                mTxtWorktype.setText(SelectedWorkType.getString("Name"));
                if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                    mFwFlg1 = SelectedWorkType.getString("FWFlg");
                    mWTCode1 = SelectedWorkType.getString("Code");
                    mWTName1 = SelectedWorkType.getString("Name");
                    mTerratiry1 = SelectedWorkType.getString("TerrSlFlg");
                    if (SelectedWorkType.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                        IsFeildWorkFlag = "F1";
                        NeedClusterFlag1 = true;
                        rlculster.setVisibility(View.VISIBLE);
                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                            rlHQ.setVisibility(View.VISIBLE);
                        }
                        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0")/* && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") */ && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0)/* ||
                                TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0)*/) {
                            binding.txtworkday1.setText("");
                            binding.txtCluster1.setText("");
                            binding.rlworkday1.setVisibility(View.VISIBLE);
                        } else {
                            mTowncode1 = "";
                            mTownname1 = "";
                            mHQCode1 = "";
                            mHQName1 = "";
                            chk_cluster = "";
                            binding.txtheadquaters1.setText("");
                            binding.txtCluster1.setText("");
                            workDayCode = "";
                            workDayName = "";
                            binding.rlworkday1.setVisibility(View.GONE);
                        }
                    } else {
                        NeedClusterFlag1 = false;
                        mTowncode1 = "";
                        mTownname1 = "";
                        mHQCode1 = "";
                        mHQName1 = "";
                        chk_cluster = "";
                        workDayCode = "";
                        workDayName = "";
                        rlculster.setVisibility(View.GONE);
                        rlHQ.setVisibility(View.GONE);
                        binding.rlworkday1.setVisibility(View.GONE);
                    }
                } else {
                    mFwFlg2 = SelectedWorkType.getString("FWFlg");
                    mWTCode2 = SelectedWorkType.getString("Code");
                    mWTName2 = SelectedWorkType.getString("Name");
                    mTerratiry2 = SelectedWorkType.getString("TerrSlFlg");
                    if (SelectedWorkType.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                        NeedClusterFlag2 = true;
                        IsFeildWorkFlag = "F2";
                        rlculster.setVisibility(View.VISIBLE);
                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                            rlHQ.setVisibility(View.VISIBLE);
                        }
                        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                            binding.txtworkday2.setText("");
                            binding.txtCluster2.setText("");
                            binding.rlworkday2.setVisibility(View.VISIBLE);
                            binding.rlcluster2.setEnabled(false);
                            binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
                        } else {
                            mTowncode2 = "";
                            mTownname2 = "";
                            mHQCode2 = "";
                            mHQName2 = "";
                            chk_cluster = "";
                            binding.txtheadquaters2.setText("");
                            binding.txtCluster2.setText("");
//                            workDayCode = "";
//                            workDayName = "";
                            binding.rlworkday2.setVisibility(View.GONE);
                        }
                    } else {
                        NeedClusterFlag2 = false;
                        mTowncode2 = "";
                        mTownname2 = "";
                        mHQCode2 = "";
                        mHQName2 = "";
                        chk_cluster = "";
//                        workDayCode = "";
//                        workDayName = "";
                        rlculster.setVisibility(View.GONE);
                        rlHQ.setVisibility(View.GONE);
                        binding.rlworkday2.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UtilityClass.hideKeyboard(requireActivity());
        });

        HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(requireActivity());
                WT_ListAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(requireActivity());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showMultiClusterAlert() {
        listSelectedCluster.clear();
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.GONE);
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        if (SharedPref.getWrkAreaName(requireContext()).isEmpty() || SharedPref.getWrkAreaName(requireContext()).equalsIgnoreCase(null)) {
            HomeDashBoard.binding.llNav.tvSearchheader.setText("Cluster");
        } else {
            HomeDashBoard.binding.llNav.tvSearchheader.setText(SharedPref.getWrkAreaName(requireContext()));

        }

        updateClusterList(DayPlanCount);
        MultiClusterAdapter multiClusterAdapter = new MultiClusterAdapter(getActivity(), multiple_cluster_list, new OnClusterClicklistener() {
            @Override
            public void classCampaignItem_addClass(Multicheckclass_clust classGroup) {
                listSelectedCluster.add(classGroup);
            }

            @Override
            public void classCampaignItem_removeClass(Multicheckclass_clust classGroup) {
                listSelectedCluster.add(classGroup);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        HomeDashBoard.binding.llNav.wkRecyelerView.setLayoutManager(linearLayoutManager);
        HomeDashBoard.binding.llNav.wkRecyelerView.setAdapter(multiClusterAdapter);

        HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(requireActivity());
                multiClusterAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        HomeDashBoard.binding.llNav.txtClDone.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
                UtilityClass.hideKeyboard(requireActivity());
                if (!listSelectedCluster.isEmpty()) {
                    String selectedUsers = "", selectedId = "";
                    strClusterName = "";
                    strClusterID = "";
                    for (Multicheckclass_clust multiCheckClassCluster : multiple_cluster_list) {
                        if (multiCheckClassCluster.isChecked()) {
                            selectedUsers = selectedUsers + multiCheckClassCluster.getStrname() + ",";
                            selectedId = selectedId + multiCheckClassCluster.getStrid() + ",";
                            strClusterID = selectedId;
                            strClusterName = selectedUsers;
                        }
                    }
                    if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                        mTowncode1 = strClusterID;
                        mTownname1 = strClusterName;
                        binding.txtCluster1.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(strClusterName.trim()).replaceAll(",", " , ")));
                        chk_cluster = mTowncode1;
                    } else {
                        mTowncode2 = strClusterID;
                        mTownname2 = strClusterName;
                        binding.txtCluster2.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(strClusterName.trim()).replaceAll(",", " , ")));
                        chk_cluster = mTowncode2;
                    }
                }
            }
        });

        HomeDashBoard.binding.llNav.cancelImg.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
//            if(DayPlanCount.equalsIgnoreCase("1")){
//                if(binding.txtCluster1.getText().toString().isEmpty()){
//                    for (Multicheckclass_clust multicheckclassClust : multiple_cluster_list) {
//                        if(multicheckclassClust.isChecked()) multicheckclassClust.setChecked(false);
//                    }
//                }
//            }else {
//                if(binding.txtCluster2.getText().toString().isEmpty()){
//                    for (Multicheckclass_clust multicheckclassClust : multiple_cluster_list) {
//                        if(multicheckclassClust.isChecked()) multicheckclassClust.setChecked(false);
//                    }
//                }
//            }
                if ((DayPlanCount.equalsIgnoreCase("1") && mTowncode1.isEmpty()) || (DayPlanCount.equalsIgnoreCase("2") && mTowncode2.isEmpty())) {
                    chk_cluster = "";
                }
                HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showMultiHQClusterAlert(String session) {
        listSelectedCluster.clear();
        mapSelectedCluster.clear();
        multiple_cluster_list.clear();
        multiHQExpandItems.clear();
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.GONE);
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        if (SharedPref.getWrkAreaName(requireContext()).isEmpty()) {
            HomeDashBoard.binding.llNav.tvSearchheader.setText("Cluster");
        } else {
            HomeDashBoard.binding.llNav.tvSearchheader.setText(SharedPref.getWrkAreaName(requireContext()));
        }

        prepareMultiHQClusters();
        MultiHQClusterAdapter multiHQClusterAdapter = new MultiHQClusterAdapter(requireContext(), multiHQExpandItems, new MultiHQClusterAdapter.ClusterSelectListener() {
            @Override
            public void onClusterSelected(String hqCode, MultiHQClusterItem multiHQClusterItem) {
                ArrayList<MultiHQClusterItem> multiHQClusterItems = new ArrayList<>();
                if (mapSelectedCluster.containsKey(hqCode)) {
                    multiHQClusterItems = mapSelectedCluster.get(hqCode);
                    if (multiHQClusterItems != null && !multiHQClusterItems.contains(multiHQClusterItem)) {
                        multiHQClusterItems.add(multiHQClusterItem);
                    }
                } else {
                    multiHQClusterItems.add(multiHQClusterItem);
                }
                mapSelectedCluster.put(hqCode, multiHQClusterItems);
            }

            @Override
            public void onClusterUnSelected(String hqCode, MultiHQClusterItem multiHQClusterItem) {
                ArrayList<MultiHQClusterItem> multiHQClusterItems = new ArrayList<>();
                if (mapSelectedCluster.containsKey(hqCode)) {
                    multiHQClusterItems = mapSelectedCluster.get(hqCode);
                    multiHQClusterItems.remove(multiHQClusterItem);
                }
                mapSelectedCluster.put(hqCode, multiHQClusterItems);

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        HomeDashBoard.binding.llNav.wkRecyelerView.setLayoutManager(linearLayoutManager);
        HomeDashBoard.binding.llNav.wkRecyelerView.setAdapter(multiHQClusterAdapter);

        HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(requireActivity());
                multiHQClusterAdapter.filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        HomeDashBoard.binding.llNav.txtClDone.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && !SharedPref.getOneBuild(requireContext()).equals("0")) {
                    String hqName = "";
                    boolean isClusterNotSelected = false;
                    try {
                        String[] hqCodes = CommonUtilsMethods.removeLastComma(mHQCode1).split(",");
                        String[] hqNames = CommonUtilsMethods.removeLastComma(mHQName1).split(",");
                        if (session.equalsIgnoreCase("2")) {
                            hqCodes = CommonUtilsMethods.removeLastComma(mHQCode2).split(",");
                            hqNames = CommonUtilsMethods.removeLastComma(mHQName2).split(",");
                        }
                        for (int i = 0; i < hqCodes.length; i++) {
                            String HQCode = hqCodes[i];
                            if (!mapSelectedCluster.containsKey(HQCode) || mapSelectedCluster.get(HQCode) == null || mapSelectedCluster.get(HQCode).isEmpty()) {
                                isClusterNotSelected = true;
                                hqName = hqNames[i];
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (isClusterNotSelected) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_any) + SharedPref.getClusterCap(requireContext()) + " for " + hqName);
                        return;
                    }
                }
                HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
                UtilityClass.hideKeyboard(requireActivity());
                strClusterName = "";
                strClusterID = "";
                updateSelectedClusters();
            }
        });

        HomeDashBoard.binding.llNav.cancelImg.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if ((DayPlanCount.equalsIgnoreCase("1") && mTowncode1.isEmpty()) || (DayPlanCount.equalsIgnoreCase("2") && mTowncode2.isEmpty())) {
                    chk_cluster = "";
                }
                HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
            }
        });
    }

    private void updateSelectedClusters() {
        if (!mapSelectedCluster.isEmpty()) {
            String selectedUsers = "", selectedId = "";
            for (String key : mapSelectedCluster.keySet()) {
                ArrayList<MultiHQClusterItem> multiHQClusterItems = mapSelectedCluster.get(key);
                for (MultiHQClusterItem multiHQClusterItem : multiHQClusterItems) {
                    if (multiHQClusterItem.isChecked()) {
                        selectedUsers = selectedUsers + multiHQClusterItem.getName() + ",";
                        selectedId = selectedId + multiHQClusterItem.getCode() + ",";
                        strClusterID = selectedId;
                        strClusterName = selectedUsers;
                    }
                }
                selectedId = CommonUtilsMethods.removeLastComma(selectedId) + ",$";
                selectedUsers = CommonUtilsMethods.removeLastComma(selectedUsers) + ",$";
            }
            if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                mTowncode1 = strClusterID;
                mTownname1 = strClusterName;
                binding.txtCluster1.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(strClusterName.trim())));
                chk_cluster = mTowncode1;
            } else {
                mTowncode2 = strClusterID;
                mTownname2 = strClusterName;
                binding.txtCluster2.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(strClusterName.trim())));
                chk_cluster = mTowncode2;
            }
        } else {
            chk_cluster = "";
            if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                binding.txtCluster1.setText("");
                mTowncode1 = "";
                mTownname1 = "";
            } else {
                binding.txtCluster2.setText("");
                mTowncode2 = "";
                mTownname2 = "";
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void showHQ(TextView TextHQ, TextView TextCL, TextView TextWorkDay) {
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.tvSearchheader.setText("HeadQuarters");
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        updateHQList();
        WorkplanListAdapter HQ_ListAdapter = new WorkplanListAdapter(getActivity(), HQList, "3");
        HomeDashBoard.binding.llNav.wkListView.setAdapter(HQ_ListAdapter);

        HomeDashBoard.binding.llNav.wkListView.setOnItemClickListener((parent, view, position, id) -> {
            SelectedHQ = HQ_ListAdapter.getlisted().get(position);
            UtilityClass.hideKeyboard(requireActivity());
            HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
            try {
                hqCode = SelectedHQ.getString("id");
                Log.d("work plan", "showHQ: " + hqCode);
              /*  boolean docAvailability = masterDataDao.isDataAvailable(Constants.DOCTOR + hqCode),
                        chemAvailability = masterDataDao.isDataAvailable(Constants.CHEMIST + hqCode),
                        stkAvailability = masterDataDao.isDataAvailable(Constants.STOCKIEST + hqCode),
                        ulDocAvailability = masterDataDao.isDataAvailable(Constants.UNLISTED_DOCTOR + hqCode),
//                        hosAvailability = masterDataDao.isDataAvailable(Constants.HOSPITAL + hqCode),
//                        cipAvailability = masterDataDao.isDataAvailable(Constants.CIP + hqCode),
                        clusterAvailability = masterDataDao.isDataAvailable(Constants.CLUSTER + hqCode);*/
                boolean docAvailability = masterDataDao.isDataAvailable(Constants.DOCTOR_MAS + hqCode),
                        chemAvailability = masterDataDao.isDataAvailable(Constants.CHEMIST_MAS + hqCode),
                        stkAvailability = masterDataDao.isDataAvailable(Constants.STOCKIEST_MAS + hqCode),
                        ulDocAvailability = masterDataDao.isDataAvailable(Constants.UNLISTED_DOCTOR_MAS + hqCode),
//                        hosAvailability = masterDataDao.isDataAvailable(Constants.HOSPITAL + hqCode),
//                        cipAvailability = masterDataDao.isDataAvailable(Constants.CIP + hqCode),
                        clusterAvailability = masterDataDao.isDataAvailable(Constants.CLUSTER + hqCode);
                Log.e("Work plan", "showHQ: " + docAvailability + " " + chemAvailability + " " + stkAvailability + " " + ulDocAvailability + " " + clusterAvailability);

                if (SharedPref.getSfType(context).equalsIgnoreCase("2") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0")) {
                    if (UtilityClass.isNetworkAvailable(context)) {
                        MasterSyncItemModel STPSetup = new MasterSyncItemModel(Constants.STANDARD_TOUR_PLAN, "getstp_setup", Constants.STP_SETUP);
                        syncMaster(STPSetup.getMasterOf(), STPSetup.getRemoteTableName(), STPSetup.getLocalTableKeyName(), hqCode, false);
                    } else {
                        CommonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                    }
                }
//                if(docAvailability && chemAvailability && stkAvailability && ulDocAvailability && hosAvailability && cipAvailability && clusterAvailability){
                if (docAvailability && chemAvailability && stkAvailability && ulDocAvailability && clusterAvailability) {
                    TextCL.setText("");
                    TextWorkDay.setText("");
                    TextHQ.setText(SelectedHQ.getString("name"));
                    if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                        mHQCode1 = SelectedHQ.getString("id");
                        mHQName1 = SelectedHQ.getString("name");
                        binding.progressHq1.setVisibility(View.GONE);
                    } else {
                        mHQCode2 = SelectedHQ.getString("id");
                        mHQName2 = SelectedHQ.getString("name");
                        binding.progressHq2.setVisibility(View.GONE);
                    }
                    getDatabaseHeadQuarters(hqCode);
                } else if (UtilityClass.isNetworkAvailable(requireContext())) {
                    TextCL.setText("");
                    TextWorkDay.setText("");
                    TextHQ.setText(SelectedHQ.getString("name"));
                    if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                        mHQCode1 = SelectedHQ.getString("id");
                        mHQName1 = SelectedHQ.getString("name");
                        getData(SelectedHQ.getString("id"), false);
                    } else {
                        mHQCode2 = SelectedHQ.getString("id");
                        mHQName2 = SelectedHQ.getString("name");
                        getData(SelectedHQ.getString("id"), false);
                    }
                } else {
                    TextHQ.setText("");
                    TextWorkDay.setText("");
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(requireActivity());
                HQ_ListAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showHQMGR(TextView TextHQ, TextView TextCL, TextView TextWorkDay) {
        listSelectedHQ.clear();
        updateHQList();
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.tvSearchheader.setText("HeadQuarters");
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);

        MultiHQAdapter multiHQAdapter = new MultiHQAdapter(getActivity(), multiple_hq_list, new MultiHQAdapter.MultiHQSelectListener() {
            @Override
            public void onHQSelected(Multicheckclass_clust multicheckclassClust) {
                if (!listSelectedHQ.contains(multicheckclassClust)) {
                    listSelectedHQ.add(multicheckclassClust);
                }
            }

            @Override
            public void onHQUnSelected(Multicheckclass_clust multicheckclassClust) {
                listSelectedHQ.remove(multicheckclassClust);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        HomeDashBoard.binding.llNav.wkRecyelerView.setLayoutManager(linearLayoutManager);
        HomeDashBoard.binding.llNav.wkRecyelerView.setAdapter(multiHQAdapter);

        HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(requireActivity());
                multiHQAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        HomeDashBoard.binding.llNav.txtClDone.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
                UtilityClass.hideKeyboard(requireActivity());
                if (!listSelectedHQ.isEmpty()) {
                    String selectedHQNames = "", selectedHQCodes = "";
                    strHQName = "";
                    strHQCode = "";
                    for (Multicheckclass_clust multiCheckClassCluster : multiple_hq_list) {
                        if (multiCheckClassCluster.isChecked()) {
                            selectedHQNames = selectedHQNames + multiCheckClassCluster.getStrname() + ",";
                            selectedHQCodes = selectedHQCodes + multiCheckClassCluster.getStrid() + ",";
                            strHQCode = selectedHQCodes;
                            strHQName = selectedHQNames;
                        }
                    }
//                TextCL.setText("");
                    TextHQ.setText(CommonUtilsMethods.removeLastComma(strHQName).replaceAll(",", " , "));
                    if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                        mHQCode1 = strHQCode;
                        mHQName1 = strHQName;
//                    binding.txtCluster1.setText(CommonUtilsMethods.removeLastComma(strHQName.trim()));
                        chk_hq = mHQCode1;
                    } else {
                        mHQCode2 = strHQCode;
                        mHQName2 = strHQName;
//                    binding.txtCluster1.setText(CommonUtilsMethods.removeLastComma(strHQName.trim()));
                        chk_hq = mHQCode2;
                    }
                    listSelectedCluster.clear();
                    multiple_cluster_list.clear();
                    multiHQExpandItems.clear();
                    prepareMultiHQClusters();
                    updateSelectedClusters();
                    checkAndSyncMasters(CommonUtilsMethods.removeDollar(strHQCode), false);
                }
            }
        });

    }

    private void checkAndSyncMasters(String strHQCode, boolean shouldShowHQProgress) {
        for (String hqCode : CommonUtilsMethods.removeLastComma(strHQCode).split(",")) {
//            boolean docAvailability = masterDataDao.isDataAvailable(Constants.DOCTOR + hqCode),
//                    chemAvailability = masterDataDao.isDataAvailable(Constants.CHEMIST + hqCode),
//                    stkAvailability = masterDataDao.isDataAvailable(Constants.STOCKIEST + hqCode),
//                    ulDocAvailability = masterDataDao.isDataAvailable(Constants.UNLISTED_DOCTOR + hqCode),
////                        hosAvailability = masterDataDao.isDataAvailable(Constants.HOSPITAL + hqCode),
////                        cipAvailability = masterDataDao.isDataAvailable(Constants.CIP + hqCode),
//                    clusterAvailability = masterDataDao.isDataAvailable(Constants.CLUSTER + hqCode);
            boolean docAvailability = masterDataDao.isDataAvailable(Constants.DOCTOR_MAS + hqCode),
                    chemAvailability = masterDataDao.isDataAvailable(Constants.CHEMIST_MAS + hqCode),
                    stkAvailability = masterDataDao.isDataAvailable(Constants.STOCKIEST_MAS + hqCode),
                    ulDocAvailability = masterDataDao.isDataAvailable(Constants.UNLISTED_DOCTOR_MAS + hqCode),
//                        hosAvailability = masterDataDao.isDataAvailable(Constants.HOSPITAL + hqCode),
//                        cipAvailability = masterDataDao.isDataAvailable(Constants.CIP + hqCode),
                    clusterAvailability = masterDataDao.isDataAvailable(Constants.CLUSTER + hqCode),
                    subordinateAvailability = masterDataDao.isDataAvailable(Constants.JOINT_WORK + hqCode);
            Log.e("Work plan", hqCode + " - showHQ: " + docAvailability + " " + chemAvailability + " " + stkAvailability + " " + ulDocAvailability + " " + clusterAvailability);
            if (!docAvailability || !chemAvailability || !stkAvailability || !ulDocAvailability || !clusterAvailability || !subordinateAvailability) {
                getData(hqCode, shouldShowHQProgress);
            }
        }
    }

    void getLocalData() {
        workType_list1.clear();
        cluster.clear();
        multiple_cluster_list.clear();
        HQList.clear();
        stpModelList.clear();
        CheckInOutNeed = SharedPref.getSrtNd(requireContext());
        STPNeed = SharedPref.getStpNeed(requireContext());
        STPBasedMTP = SharedPref.getStpBasedMtp(requireContext());
        STPBasedDCR = SharedPref.getStpBasedDcr(requireContext());
        TPNeed = SharedPref.getTpNeed(requireContext());
        TPMandatory = SharedPref.getTpMandatoryNeed(requireContext());
        TPBasedDCR = SharedPref.getTpbasedDcr(requireContext());
        TPDCRDeviation = SharedPref.getTpdcrDeviation(requireContext());
        TPDCRMGRApprNeed = SharedPref.getTpdcrMgrappr(requireContext());

        try {
            updateWorkTypeList();

            updateClusterList("1");
//            JSONArray workTypeArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getHqCode(requireContext())).getMasterSyncDataJsonArray();
//            for (int i = 0; i < workTypeArray2.length(); i++) {
//                JSONObject Object1 = workTypeArray2.getJSONObject(i);
//
//                if (("," + chk_cluster + ",").contains("," + Object1.getString("Code") + ",")) {
//                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", true));
//                } else {
//                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", false));
//
//                }
//                cluster.add(Object1);
//            }

//            if(SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
//                JSONArray workTypeArray3 = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
//                for (int i = 0; i<workTypeArray3.length(); i++) {
//                    JSONObject jsonObject = workTypeArray3.getJSONObject(i);
//
//                    if(EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
//                        if(!(mHQCode2).equalsIgnoreCase(jsonObject.getString("id"))) {
//                            HQList.add(jsonObject);
//                        }
//                    }else {
//                        if(!(mHQCode1).equalsIgnoreCase(jsonObject.getString("id"))) {
//                            HQList.add(jsonObject);
//                        }
//                    }
//
//                }
//            }
            updateHQList();
            updateWorkDayList();
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    private void updateWorkDayList() {
        try {
            if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0)) {
                    List<STPOfflineDataTable> stpOfflineDataTableList = stpOfflineDataDao.getAllSTPData();
                    List<STPModelList> stpModelList = new ArrayList<>();
                    for (STPOfflineDataTable stpOfflineDataTable : stpOfflineDataTableList) {
                        stpModelList.add(new STPModelList(stpOfflineDataTable.getDayCaption(), stpOfflineDataTable.getDayID(), ""));
                    }
                    if (!SharedPref.getStpType(requireContext()).equalsIgnoreCase("1")) {
                        STPDaySorter.sortDays(stpModelList, STPModelList::getCode);
                    }
                    this.stpModelList = new ArrayList<>();
                    for (STPModelList stpModel : stpModelList) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", stpModel.getName());
                            jsonObject.put("code", stpModel.getCode());
                            this.stpModelList.add(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STP_SETUP).getMasterSyncDataJsonArray();
                    List<STPModelList> stpModelList = new ArrayList<>();
                    if (jsonArray != null && jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                        String[] dayIDs = CommonUtilsMethods.removeLastComma(jsonObject.optString("Plan_SName")).split("/");
                        String[] dayCaptions = CommonUtilsMethods.removeLastComma(jsonObject.optString("Plan_Name")).split("/");
                        for (int index = 0; index < dayIDs.length; index++) {
                            if (!dayIDs[index].isEmpty()) {
                                stpModelList.add(new STPModelList(dayCaptions[index], dayIDs[index], ""));
                            }
                        }
                    }
                    if (!SharedPref.getStpType(requireContext()).equalsIgnoreCase("1")) {
                        STPDaySorter.sortDays(stpModelList, STPModelList::getCode);
                    }
                    this.stpModelList = new ArrayList<>();
                    for (STPModelList stpModel : stpModelList) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", stpModel.getName());
                            jsonObject.put("code", stpModel.getCode());
                            this.stpModelList.add(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWorkTypeList() {
        try {
            workType_list1.clear();
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject object = workTypeArray.getJSONObject(i);
                if (object.getString("FWFlg").equalsIgnoreCase("L")) {
                    continue;
                }
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) {
                    if (EditSession.equalsIgnoreCase("1")) {
                        if (!(mWTCode2).equalsIgnoreCase(object.getString("Code"))) {
                            workType_list1.add(object);
                        }
                    } else if (EditSession.equalsIgnoreCase("2") || DayPlanCount.equalsIgnoreCase("2")) {
                        if (!(mWTCode1).equalsIgnoreCase(object.getString("Code"))) {
                            workType_list1.add(object);
                        }
                    } else if (DayPlanCount.equalsIgnoreCase("1")) {
                        if (!(mWTCode2).equalsIgnoreCase(object.getString("Code"))) {
                            workType_list1.add(object);
                        }
                    }
                } else {

                    if (SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                        if (EditSession.equalsIgnoreCase("1")) {
                            if (mWTCode2.equalsIgnoreCase(object.getString("Code"))) {
                                if (object.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                                    IsFeildWorkFlag = "F1";
                                    workType_list1.add(object);
                                }
                            } else {
                                workType_list1.add(object);
                            }
                        } else if (EditSession.equalsIgnoreCase("2") || DayPlanCount.equalsIgnoreCase("2")) {
                            if (mWTCode1.equalsIgnoreCase(object.getString("Code"))) {
                                if (object.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                                    IsFeildWorkFlag = "F2";
                                    workType_list1.add(object);
                                }
                            } else {
                                workType_list1.add(object);
                            }
                        } else if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                            if (mWTCode2.equalsIgnoreCase(object.getString("Code"))) {
                                if (object.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                                    IsFeildWorkFlag = "F1";
                                    workType_list1.add(object);
                                }
                            } else {
                                workType_list1.add(object);
                            }
                        }
                    } else {
                        if (EditSession.equalsIgnoreCase("1")) {
                            if (!mWTCode2.equalsIgnoreCase(object.getString("Code"))) {
                                if (object.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                                    IsFeildWorkFlag = "F1";
                                    workType_list1.add(object);
                                } else {
                                    workType_list1.add(object);
                                }
                            }
                        } else if (EditSession.equalsIgnoreCase("2") || DayPlanCount.equalsIgnoreCase("2")) {
                            if (!mWTCode1.equalsIgnoreCase(object.getString("Code"))) {
                                if (object.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                                    IsFeildWorkFlag = "F2";
                                    workType_list1.add(object);
                                } else {
                                    workType_list1.add(object);
                                }
                            }
                        } else if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                            if (!mWTCode2.equalsIgnoreCase(object.getString("Code"))) {
                                if (object.getString("TerrSlFlg").equalsIgnoreCase("Y")) {
                                    IsFeildWorkFlag = "F1";
                                    workType_list1.add(object);
                                } else {
                                    workType_list1.add(object);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Update WT", "updateWorkTypeList: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateHQList() {
        try {
            HQList.clear();
            multiple_hq_list.clear();
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && !SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                    if ((EditSession.equalsIgnoreCase("2") || DayPlanCount.equalsIgnoreCase("2")) && mHQCode2.contains(jsonObject.optString("Code"))) {
                        multiple_hq_list.add(new Multicheckclass_clust(jsonObject.optString("Code"), jsonObject.optString("name"), "", true));
                    } else if ((EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) && mHQCode1.contains(jsonObject.optString("Code"))) {
                        multiple_hq_list.add(new Multicheckclass_clust(jsonObject.optString("Code"), jsonObject.optString("name"), "", true));
                    } else {
                        multiple_hq_list.add(new Multicheckclass_clust(jsonObject.optString("Code"), jsonObject.optString("name"), "", false));
                    }
                } else {
                    if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                        if (mHQCode2 != null && !(mHQCode2).equalsIgnoreCase(jsonObject.getString("id"))) {
                            HQList.add(jsonObject);
                        }
                    } else {
                        if (mHQCode1 != null && !(mHQCode1).equalsIgnoreCase(jsonObject.getString("id"))) {
                            HQList.add(jsonObject);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateClusterList(String dayPlanCount) {
        try {
            String clusters = mTowncode1;
            if (dayPlanCount.equalsIgnoreCase("2")) clusters = mTowncode2;
            if (EditSession.equalsIgnoreCase("1")) {
                chk_cluster = mTowncode1;
                if (!mHQCode1.isEmpty()) {
                    hqCode = mHQCode1;
                }
            } else if (EditSession.equalsIgnoreCase("2")) {
                chk_cluster = mTowncode2;
                if (!mHQCode2.isEmpty()) {
                    hqCode = mHQCode2;
                }
            } else if (dayPlanCount.equalsIgnoreCase("1")) {
                chk_cluster = mTowncode1;
                if (!mHQCode1.isEmpty()) {
                    hqCode = mHQCode1;
                }
            } else {
                chk_cluster = mTowncode2;
                if (!mHQCode2.isEmpty()) {
                    hqCode = mHQCode2;
                }
            }
            multiple_cluster_list.clear();
            JSONArray workTypeArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray2.length(); i++) {
                JSONObject Object1 = workTypeArray2.getJSONObject(i);
                if (("," + chk_cluster + ",").contains("," + Object1.getString("Code") + ",")) {
                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", true));
                } else {
                    multiple_cluster_list.add(new Multicheckclass_clust(Object1.getString("Code"), Object1.getString("Name"), "", false));
                }
                cluster.add(Object1);
            }
        } catch (Exception e) {
            Log.e("Work plan", "updateClusterList: " + e.getMessage());
            e.printStackTrace();
        }
    }

//    void updateHQList() {
//        try {
//            HQList.clear();
//            if(SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
//                JSONArray workTypeArray3 = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
//                for (int i = 0; i<workTypeArray3.length(); i++) {
//                    JSONObject jsonObject = workTypeArray3.getJSONObject(i);
//                    if(EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
//                        if(mHQCode2 != null && !(mHQCode2).equalsIgnoreCase(jsonObject.getString("id"))) {
//                            HQList.add(jsonObject);
//                        }
//                    }else {
//                        if(mHQCode1 != null && !(mHQCode1).equalsIgnoreCase(jsonObject.getString("id"))) {
//                            HQList.add(jsonObject);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Log.e("Work plan", "updateHQList: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private String findHQName(String HQCode) {
        if (HQCode == null || HQCode.isEmpty()) {
            return "";
        }
        try {
            JSONArray hqArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            if (hqArray.length() > 0) {
                for (int i = 0; i < hqArray.length(); i++) {
                    JSONObject jsonObject = hqArray.optJSONObject(i);
                    if (HQCode.equalsIgnoreCase(jsonObject.optString("id"))) {
                        return jsonObject.optString("name");
                    }
                }
            } else {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.kindly_sync_subordinate));
            }
        } catch (Exception e) {
            Log.e("Work plan", "Find HQ : " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private String findClusterName(String clusterCode, String HQCode) {
        if (clusterCode == null || clusterCode.isEmpty()) {
            return "";
        }
        try {
            JSONArray clusterArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + HQCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < clusterArray.length(); i++) {
                JSONObject jsonObject = clusterArray.optJSONObject(i);
                if (clusterCode.equalsIgnoreCase(jsonObject.optString("Code"))) {
                    return jsonObject.optString("Name");
                }
            }
        } catch (Exception e) {
            Log.e("Work plan", "Find cluster : " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private String findWTName(String FWFlag) {
        if (FWFlag == null || FWFlag.isEmpty()) {
            return "";
        }
        try {
            JSONArray workTypeArray3 = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray3.length(); i++) {
                JSONObject jsonObject = workTypeArray3.optJSONObject(i);
                if (FWFlag.equalsIgnoreCase(jsonObject.optString("FWFlg"))) {
                    return jsonObject.optString("Name");
                }
            }
        } catch (Exception e) {
            Log.e("Work plan", "Find WT : " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 1000) {
            return; // Ignore fast click
        }
        lastClickTime = currentTime;
        try {
            switch (view.getId()) {
                case R.id.close_rejected_reason:
                    binding.rlRejReason.setVisibility(View.GONE);
                    binding.rejectedReason.setText("");
                    binding.rejectedReason.setVisibility(View.GONE);
                    break;

                case R.id.rlworktype1:
                    if (HomeDashBoard.binding.textDate.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_date));
                    } else {
                        updateWorkTypeList();
                        ShowWorkTypeAlert(binding.txtWorktype1, binding.rlcluster1, binding.rlheadquates1);
                    }
                    break;

                case R.id.rlcluster1:
                    if (binding.txtheadquaters1.getText().toString().equalsIgnoreCase("") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_hq));
                    } else if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                        if (binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                        } else {
                            showMultiClusterAlert();
                        }
                    } else {
                        showMultiHQClusterAlert("1");
                    }
                    break;

                case R.id.rlworktype2:
                    updateWorkTypeList();
                    ShowWorkTypeAlert(binding.txtWorktype2, binding.rlcluster2, binding.rlheadquates2);
                    break;

                case R.id.rlcluster2:
                    if (binding.txtheadquaters2.getText().toString().equalsIgnoreCase("") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_hq));
                    } else if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                        if (binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                        } else {
                            showMultiClusterAlert();
                        }
                    } else {
                        showMultiHQClusterAlert("2");
                    }
                    break;

                case R.id.rlheadquates1:
                    if (binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                    } else {
                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                            showHQ(binding.txtheadquaters1, binding.txtCluster1, binding.txtworkday1);
                        } else {
                            showHQMGR(binding.txtheadquaters1, binding.txtCluster1, binding.txtworkday1);
                        }
                    }
                    break;

                case R.id.rlheadquates2:
                    if (binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
                    } else {
                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                            showHQ(binding.txtheadquaters2, binding.txtCluster2, binding.txtworkday2);
                        } else {
                            showHQMGR(binding.txtheadquaters2, binding.txtCluster2, binding.txtworkday2);
                        }
                    }
                    break;

                case R.id.rlworkday1:
//                    if(binding.txtworkday1.getText().toString().equalsIgnoreCase("")) {
//                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_workday));
//                    }else {
                    updateWorkDayList();
                    showWorkDay(binding.txtworkday1, binding.txtCluster1);
//                    }
                    break;

                case R.id.rlworkday2:
//                    if(binding.txtworkday2.getText().toString().equalsIgnoreCase("")) {
//                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_workday));
//                    }else {
                    updateWorkDayList();
                    showWorkDay(binding.txtworkday2, binding.txtCluster1);
//                    }
                    break;

                case R.id.txtSave:
                    if (SharedPref.getApprovalManatoryStatus(requireContext()) && SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && SharedPref.getApprMandatoryNeed(requireActivity()).equalsIgnoreCase("0")) {
                        CommonAlertBox.ApprovalAlert(requireActivity());
                    } else if (SharedPref.getTpmanatoryStatus(requireContext()) && SharedPref.getTpMandatoryNeed(requireContext()).equalsIgnoreCase("0") && SharedPref.getTpNeed(requireContext()).equalsIgnoreCase("0")) {
                        CommonAlertBox.TpAlert(requireActivity());
                    } else {
                        onSaveClicked();
                        // ((HomeDashBoard) requireActivity()).showDoctorPlanPopup(tpDoctor);
                    }
                    break;

                case R.id.txtAddPlan:
                    if (isTPorSTPBased() && TPDCRDeviation.equalsIgnoreCase("0") && binding.llDeviation.getVisibility() == View.VISIBLE && !binding.switchButton.isChecked()) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.deviate_to_add_plan));
                    } else if (!EditSession.equalsIgnoreCase("1") && DayPlanCount.equals("1")) {
                        if (mFwFlg1 != null && mFwFlg1.equalsIgnoreCase("W")) {
                            String weekOffName = findWTName("W");
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.already) + weekOffName + getString(R.string.has_been_submitted));
                        } else if (mFwFlg1 != null && mFwFlg1.equalsIgnoreCase("H")) {
                            String holidayName = findWTName("H");
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.already) + holidayName + getString(R.string.has_been_submitted));
                        } else {
                            if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && TPDCRDeviation.equalsIgnoreCase("1")) {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.cannot_add_plan_in_tour_plan_based_dcr));
                            }
                            DayPlanCount = "2";
                            binding.llDelete.setVisibility(View.VISIBLE);
                            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                            binding.txtSave.setTextColor(getResources().getColor(R.color.black));
                            binding.txtSave.setEnabled(true);
                            binding.cardPlan2.setVisibility(View.VISIBLE);
                            if (SharedPref.getWrkAreaName(requireContext()).isEmpty()) {
                                binding.txtCluster2.setHint(getString(R.string.select) + " " + SharedPref.getClusterCap(requireContext()));
                            } else {
                                binding.txtCluster2.setHint(getString(R.string.select) + " " + SharedPref.getWrkAreaName(requireContext()));
                            }
                            getLocalData();
                            binding.txtworkday2.setText("");
                        }
                    } else {
                        if (binding.flSession1.getVisibility() == View.GONE) {
                            disableSession1();
                        } else if (binding.flSession2.getVisibility() == View.GONE && DayPlanCount.equalsIgnoreCase("2")) {
                            disableSession2();
                        }
                        if (binding.llDeviation.getVisibility() == View.VISIBLE && binding.switchButton.isChecked()) {
                            binding.switchButton.setChecked(false);
                        }
                        setUpWorkPlan();
                        binding.txtSave.setText(getString(R.string.save));
                        binding.txtAddPlan.setText(getString(R.string.add_plan));
                        if (DayPlanCount.equals("1")) {
                            if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && TPDCRDeviation.equalsIgnoreCase("1")) {
                                binding.txtAddPlan.setEnabled(false);
                                binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                            } else {
                                binding.txtAddPlan.setEnabled(true);
                                binding.txtAddPlan.setTextColor(getResources().getColor(R.color.black));
                            }
                        } else if (DayPlanCount.equals("2")) {
                            binding.txtAddPlan.setEnabled(false);
                            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                        }
                    }
                    break;

                case R.id.btn_submit:
                    if (SharedPref.getApprovalManatoryStatus(requireContext()) && SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && SharedPref.getApprMandatoryNeed(requireActivity()).equalsIgnoreCase("0")) {
                        CommonAlertBox.ApprovalAlert(requireActivity());
                    } else if (SharedPref.getTpmanatoryStatus(requireContext()) && SharedPref.getTpMandatoryNeed(requireContext()).equalsIgnoreCase("0") && SharedPref.getTpNeed(requireContext()).equalsIgnoreCase("0")) {
                        CommonAlertBox.TpAlert(requireActivity());
                    } else {
                        if (SharedPref.getGeoChk(requireContext()).equalsIgnoreCase("0")) {
                            if ((gpsTrack.getLatitude() != 0.0) || (gpsTrack.getLongitude() != 0.0)) {
                                submitMyDayPlan();
                            } else {
                                gpsTrack = new GPSTrack(requireActivity());
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_location_please_try_again));
                            }
                        } else {
                            submitMyDayPlan();
                        }
                    }
                    break;

                case R.id.ll_delete:
                    Dialog dialog = new Dialog(requireContext());
                    dialog.setContentView(R.layout.dcr_cancel_alert);
                    dialog.setCancelable(false);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                    TextView btn_no = dialog.findViewById(R.id.btn_no);
                    TextView title = dialog.findViewById(R.id.ed_alert_msg);
                    title.setText(R.string.are_you_sure_to_delete);
                    btn_yes.setOnClickListener(new SafeClickListener() {
                        @Override
                        public void onSafeClick(View view) {
                            dialog.dismiss();
                            binding.txtSave.setEnabled(false);
                            binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
                            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.black));
                            binding.txtWorktype2.setText("");
                            binding.txtCluster2.setText("");
                            binding.txtheadquaters2.setText("");
                            DayPlanCount = "1";
                            binding.cardPlan2.setVisibility(View.GONE);
                        }
                    });

                    btn_no.setOnClickListener(new SafeClickListener() {
                        @Override
                        public void onSafeClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    break;

                case R.id.fl_session1:
                    Log.d("Plan Edit", "onClick: Plan 1");
                    if (TPDCRDeviation.equalsIgnoreCase("0") && !binding.switchButton.isChecked() && binding.llDeviation.getVisibility() == View.VISIBLE && !deviation.equalsIgnoreCase("1")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.deviate_to_edit_work_plan));
                        break;
                    }
//                    else if(TPDCRDeviation.equalsIgnoreCase("0") && binding.llDeviation.getVisibility() == View.GONE && deviation.equalsIgnoreCase("1") && binding.flSession2.getVisibility() == View.VISIBLE) {
////                        commonUtilsMethods.showToastMessage(requireContext(), "Already Deviated");
//                        dialogEditOrDelete("1");
//                        break;
//                    }
                    if (!UtilityClass.isNetworkAvailable(requireContext())) {
                        if (DayPlanCount.equalsIgnoreCase("2")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network) + getString(R.string.no_network_edit_delete_workplan));
                        } else if (DayPlanCount.equalsIgnoreCase("1")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network) + getString(R.string.no_network_edit_workplan));
                        }
                        break;
                    }
                    if (dayStatus == null || dayStatus.isEmpty()) dayStatus = "0";
                    if (DayPlanCount.equalsIgnoreCase("1") || (DayPlanCount.equalsIgnoreCase("2") && binding.flSession2.getVisibility() == View.VISIBLE)) {
                        if (dayStatus.equalsIgnoreCase("2") || dayStatus.equalsIgnoreCase("3") || (binding.switchButton.isChecked())
                                || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && TPDCRDeviation.equalsIgnoreCase("0") && binding.llDeviation.getVisibility() == View.GONE && deviation.equalsIgnoreCase("1"))
                        ) {
                            dialogEditOrDelete("1");
                        } else {
                            if (binding.llDeviation.getVisibility() == View.VISIBLE) {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.deviate_to_edit_work_plan));
                            } else if (dayStatus.equalsIgnoreCase("0") && mFwFlg1.equalsIgnoreCase("F") && !(TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))) {
                                dialogFWEditConfirmation("1", mWTName1);
                            } else {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.rejection_re_entry_need));
                            }
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.please_update_session_2));
                    }
                    break;

                case R.id.fl_session2:
                    Log.d("Plan Edit", "onClick: Plan 2");
                    if (TPDCRDeviation.equalsIgnoreCase("0") && !binding.switchButton.isChecked() && binding.llDeviation.getVisibility() == View.VISIBLE && !deviation.equalsIgnoreCase("1")) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.deviate_to_edit_work_plan));
                        break;
                    }
//                    else if(TPDCRDeviation.equalsIgnoreCase("0") && binding.llDeviation.getVisibility() == View.GONE && deviation.equalsIgnoreCase("1")) {
////                        commonUtilsMethods.showToastMessage(requireContext(), "Already Deviated");
//                        dialogEditOrDelete("2");
//                        break;
//                    }
//                    if(!binding.switchButton.isChecked()) {
//                        commonUtilsMethods.showToastMessage(requireContext(), "Deviate to edit WorkPlan");
//                        break;
//                    }
                    if (!UtilityClass.isNetworkAvailable(requireContext())) {
                        if (DayPlanCount.equalsIgnoreCase("2")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network) + getString(R.string.no_network_edit_delete_workplan));
                        } else if (DayPlanCount.equalsIgnoreCase("1")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network) + getString(R.string.no_network_edit_workplan));
                        }
                        break;
                    }
                    if (dayStatus == null || dayStatus.isEmpty()) dayStatus = "0";
                    if (binding.flSession1.getVisibility() == View.VISIBLE) {
                        if (dayStatus.equalsIgnoreCase("2") || dayStatus.equalsIgnoreCase("3") || (binding.switchButton.isChecked())
                                || (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && TPDCRDeviation.equalsIgnoreCase("0") && binding.llDeviation.getVisibility() == View.GONE && deviation.equalsIgnoreCase("1"))
                        ) {
                            dialogEditOrDelete("2");
                        } else {
                            if (binding.llDeviation.getVisibility() == View.VISIBLE) {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.deviate_to_edit_work_plan));
                            } else if (dayStatus.equalsIgnoreCase("0") && mFwFlg2.equalsIgnoreCase("F") && !(TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))) {
                                dialogFWEditConfirmation("2", mWTName2);
                            } else {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.rejection_re_entry_need));
                            }
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.please_update_session_1));
                    }
                    break;

                case R.id.txt_refresh:
                    if (UtilityClass.isNetworkAvailable(requireContext())) {
                        refresh(true);
                    } else {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("WorkPlan Fragment", "onClick: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refresh(boolean isToSetupWorkPlan) {
        try {
            JSONObject refreshJsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
            refreshJsonObject.put("devDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
            refreshJsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
            refreshJsonObject.put("dcr_sequential", SharedPref.getDcrSequential(requireContext()));
            Log.d("Work Plan", "refresh: " + refreshJsonObject);

            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "devstatus/dayplan");
            Call<JsonElement> callDevStatus = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, refreshJsonObject.toString());
            callDevStatus.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("devtp", response.body().toString());
                        try {
                            String JsonValue = "";
                            if ((!response.body().toString().equalsIgnoreCase("")) || (!response.body().toString().equalsIgnoreCase("null")) || (!response.body().toString().isEmpty())) {
                                JsonValue = response.body().toString();
                            } else {
                                JsonValue = "[]";
                            }
                            JSONArray jsonArray = new JSONArray(JsonValue);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String status = jsonObject.getString("Status");
//                                String reason = "";
                                deviationRejectedReason = "";
                                if (jsonObject.has("RejectedReason")) {
                                    deviationRejectedReason = jsonObject.getString("RejectedReason");
                                }
                                SharedPref.setTpDcrDeviationApprStatus(requireContext(), status);
                                if (!status.equalsIgnoreCase("3")) {
//                                    binding.deviationRejectedReasonTxt.setText(reason);
                                    if (status.equalsIgnoreCase("2") && !deviationRejectedReason.isEmpty()) {
                                        binding.rlRejReason.setVisibility(View.VISIBLE);
                                        binding.rejectedReason.setText(deviationRejectedReason);
                                        binding.rejectedReason.setVisibility(View.VISIBLE);
                                        binding.switchButton.setChecked(false);
//                                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 2));
                                        deviation = "1";
                                        SharedPref.setTpDcrDeviatedDate(requireContext(), "");
                                        syncMyDayPlan(true);

//                                        Type type = new TypeToken<ModelClass>() {
//                                        }.getType();
//                                        ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
//                                        StringBuilder clusterName = new StringBuilder(), clusterCode = new StringBuilder(), listedDr = new StringBuilder();
//                                        if(modelClass.getSessionList() != null && !modelClass.getSessionList().isEmpty()) {
//                                            for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(0).getListedDr()) {
//                                                listedDr.append(subClass.getCode());
//                                                listedDr.append(",");
//                                            }
//                                            for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(0).getCluster()) {
//                                                clusterCode.append(subClass.getCode());
//                                                clusterCode.append(",");
//                                                clusterName.append(subClass.getName());
//                                                clusterName.append(",");
//                                            }
//                                            JSONObject obj = new JSONObject();
//                                            JSONObject obj2 = new JSONObject();
//                                            obj.put("SFCode", SharedPref.getSfCode(requireContext()));
//                                            JSONObject TPDtFisrstSeasonObject = new JSONObject();
//                                            TPDtFisrstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
//                                            obj.put("TPDt", TPDtFisrstSeasonObject);
//                                            obj.put("WT", modelClass.getSessionList().get(0).getWorkType().getCode());
//                                            obj.put("WTNm", modelClass.getSessionList().get(0).getWorkType().getName());
//                                            obj.put("FWFlg", modelClass.getSessionList().get(0).getWorkType().getFWFlg());
//                                            obj.put("SFMem", modelClass.getSessionList().get(0).getHQ().getCode());
//                                            obj.put("HQNm", modelClass.getSessionList().get(0).getHQ().getName());
//                                            obj.put("Pl", clusterCode.toString());
//                                            obj.put("PlNm", clusterName.toString());
//                                            obj.put("Rem", "");
//                                            obj.put("TpVwFlg", "0");
//                                            obj.put("TP_Doctor", listedDr.toString());
//                                            obj.put("TP_cluster", clusterCode.toString());
//                                            obj.put("TP_worktype", modelClass.getSessionList().get(0).getWorkType().getCode());
//                                            if(TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) {
//                                                obj.put("Others_Code", modelClass.getSTP_Code());
//                                                obj.put("Others_Name", modelClass.getSTP_Name());
//                                            }else{
//                                                obj.put("Others_Code", "");
//                                                obj.put("Others_Name", "");
//                                            }
//                                            obj.put("isFromTP", true);
//
//                                            jsonArray = new JSONArray();
//                                            jsonArray.put(obj);
//
//                                            if(modelClass.getSessionList().size()>1) {
//                                                for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(1).getListedDr()) {
//                                                    listedDr.append(subClass.getCode());
//                                                    listedDr.append(",");
//                                                }
//                                                for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(1).getCluster()) {
//                                                    clusterCode.append(subClass.getCode());
//                                                    clusterCode.append(",");
//                                                    clusterName.append(subClass.getName());
//                                                    clusterName.append(",");
//                                                }
//                                                obj2.put("SFCode", SharedPref.getSfCode(requireContext()));
//                                                JSONObject TPDtSecondSeasonObject = new JSONObject();
//                                                TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
//                                                obj2.put("TPDt", TPDtSecondSeasonObject);
//                                                obj2.put("WT", modelClass.getSessionList().get(1).getWorkType().getCode());
//                                                obj2.put("WTNm", modelClass.getSessionList().get(1).getWorkType().getName());
//                                                obj2.put("FWFlg", modelClass.getSessionList().get(1).getWorkType().getFWFlg());
//                                                obj2.put("SFMem", modelClass.getSessionList().get(1).getHQ().getCode());
//                                                obj2.put("HQNm", modelClass.getSessionList().get(1).getHQ().getName());
//                                                obj2.put("Pl", clusterCode.toString());
//                                                obj2.put("PlNm", clusterName.toString());
//                                                obj2.put("Rem", "");
//                                                obj2.put("TpVwFlg", "0");
//                                                obj2.put("TP_Doctor", listedDr.toString());
//                                                obj2.put("TP_cluster", clusterCode.toString());
//                                                obj2.put("TP_worktype", modelClass.getSessionList().get(1).getWorkType().getCode());
//                                                if(TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) {
//                                                    obj.put("Others_Code", modelClass.getSTP_Code());
//                                                    obj.put("Others_Name", modelClass.getSTP_Name());
//                                                }else{
//                                                    obj.put("Others_Code", "");
//                                                    obj.put("Others_Name", "");
//                                                }
//                                                obj.put("isFromTP", true);
//                                                jsonArray.put(obj2);
//                                            }
//                                            isFromTP = true;
//                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonArray.toString(), 2));
//                                        }
//                                        setUpWorkPlan();
                                    } else if (status.equalsIgnoreCase("4")) {
                                        deviationRejectedReason = "";
                                        binding.rlRejReason.setVisibility(View.GONE);
                                        binding.rejectedReason.setText("");
                                        binding.rejectedReason.setVisibility(View.GONE);
                                        binding.switchButton.setChecked(true);
                                        binding.llDeviation.setVisibility(View.GONE);
                                        JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();
                                        for (int i = 0; i < jsonArray1.length(); i++) {
                                            JSONObject jsonObject1 = jsonArray1.optJSONObject(i);
                                            jsonObject1.put("isFromTP", true);
                                            jsonObject1.put("TpVwFlg", "1");
                                            jsonArray1.put(i, jsonObject1);
                                        }
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonArray1.toString(), 2));
                                        isFromTP = true;
                                    } else {
                                        deviationRejectedReason = "";
                                        binding.rlRejReason.setVisibility(View.GONE);
                                        binding.rejectedReason.setText("");
                                        binding.rejectedReason.setVisibility(View.GONE);
                                        isFromTP = false;
                                    }
                                    if (isToSetupWorkPlan) {
                                        setUpWorkPlan();
                                    }
                                } else {
                                    if (isToSetupWorkPlan) {
                                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.deviation_not_approved));
                                    }
                                }
                            } else {
                                binding.deviationLock.setVisibility(View.VISIBLE);       // Temp change from visible to gone
                                binding.rlWorkPlanMain.setVisibility(View.GONE);   // Temp change from gone to visible
                                previousWTCode1 = "";
                                previousWTCode2 = "";
                                SharedPref.setTpDcrDeviationApprStatus(requireContext(), "");
//                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                            }

                            if (TPDCRMGRApprNeed.equalsIgnoreCase("0") && SharedPref.getTpdcrDeviationApprStatus(requireContext()).equalsIgnoreCase("3")) {
                                binding.deviationLock.setVisibility(View.VISIBLE);
                                binding.rlWorkPlanMain.setVisibility(View.GONE);
                                previousWTCode1 = "";
                                previousWTCode2 = "";
                            } else {
                                binding.rlWorkPlanMain.setVisibility(View.VISIBLE);
                                binding.deviationLock.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWorkDay(TextView txtWorkDay, TextView txtCluster) {
        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        HomeDashBoard.binding.llNav.txtClDone.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkRecyelerView.setVisibility(View.GONE);
        HomeDashBoard.binding.llNav.wkListView.setVisibility(View.VISIBLE);
        HomeDashBoard.binding.llNav.etSearch.setText("");
        HomeDashBoard.binding.llNav.tvSearchheader.setText("Work Day");

        HomeDashBoard.binding.drMainlayout.openDrawer(GravityCompat.END);
        WorkplanListAdapter WD_ListAdapter = new WorkplanListAdapter(getActivity(), stpModelList, "3");
        HomeDashBoard.binding.llNav.wkListView.setAdapter(WD_ListAdapter);

        HomeDashBoard.binding.llNav.wkListView.setOnItemClickListener((parent, view, position, id) -> {
            SelectedWorkDay = WD_ListAdapter.getlisted().get(position);
            UtilityClass.hideKeyboard(requireActivity());
            HomeDashBoard.binding.drMainlayout.closeDrawer(GravityCompat.END);
            try {
                Log.d("work Day", "showWD: " + SelectedWorkDay);
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) {
                    STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(SelectedWorkDay.optString("code"), SharedPref.getSfCode(requireContext()));
                    txtWorkDay.setText(stpOfflineDataTable.getDayCaption());
                    strClusterName = stpOfflineDataTable.getClusterName();
                    strClusterID = stpOfflineDataTable.getClusterCode();
                    if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                        mTowncode1 = strClusterID;
                        mTownname1 = strClusterName;
                        workDayCode = stpOfflineDataTable.getDayID();
                        workDayName = stpOfflineDataTable.getDayCaption();
                        tpDoctor = stpOfflineDataTable.getDoctorCode();
                        tpChemists=stpOfflineDataTable.getChemistCode();
                        binding.txtCluster1.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(strClusterName.trim()).replaceAll(",", " , ")));
                        chk_cluster = mTowncode1;
                    } else {
                        mTowncode2 = strClusterID;
                        mTownname2 = strClusterName;
                        workDayCode2 = stpOfflineDataTable.getDayID();
                        workDayName2 = stpOfflineDataTable.getDayCaption();
                        tpDoctor2 = stpOfflineDataTable.getDoctorCode();
                        tpChemists2=stpOfflineDataTable.getChemistCode();
                        binding.txtCluster2.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(strClusterName.trim()).replaceAll(",", " , ")));
                        chk_cluster = mTowncode2;
                    }
                } else {
                    String hqCode = "";
                    if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                        hqCode = mHQCode1;
                    } else {
                        hqCode = mHQCode2;
                    }
                    getSTPMGR(hqCode, SelectedWorkDay.optString("code"), txtWorkDay);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        HomeDashBoard.binding.llNav.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                if (searchString.isEmpty()) UtilityClass.hideKeyboard(requireActivity());
                WD_ListAdapter.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getSTPMGR(String hqCode, String dayOfWeek, TextView txtWorkDay) {
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            try {
                binding.progress.setVisibility(View.VISIBLE);
                api_interface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
                jsonObject.put("tableName", "getstp_details_mgr");
                jsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
                jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
                jsonObject.put("Rsf", hqCode);
                jsonObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                jsonObject.put("workday", dayOfWeek);

                Log.v("STP", "--json-- " + jsonObject);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "get/stp");
                Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();

                        if (response.isSuccessful()) {
                            Log.e("test STP MGR", "response : " + Objects.requireNonNull(response.body()));
                            try {
                                JsonElement jsonElement = response.body();
                                if (!jsonElement.isJsonNull()) {
                                    if (jsonElement.isJsonArray()) {
                                        JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                        jsonArray = new JSONArray(jsonArray1.toString());
                                        success = true;
                                    } else if (jsonElement.isJsonObject()) {
                                        JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                        JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                        if (!jsonObject2.has("success")) {
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                        } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(Constants.STANDARD_TOUR_PLAN, 1);
                                        }
                                    }

                                    if (success) {
                                        if (jsonArray.length() == 0) {
                                            CommonUtilsMethods.showToastMessage(requireContext(), "No STP plan available");
                                        } else {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.STANDARD_TOUR_PLAN, jsonArray.toString(), 2));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                        stpOfflineDataDao.deleteAllData("0");
                            if (jsonArray.length() == 0) {
                                CommonUtilsMethods.showToastMessage(requireContext(), "No STP plan available");
                            } else {
                                saveSTPDataToLocal(hqCode);

                                STPOfflineDataTable stpOfflineDataTable = stpOfflineDataDao.getSTPDataOfDayOrNew(SelectedWorkDay.optString("code"), hqCode);
                                txtWorkDay.setText(stpOfflineDataTable.getDayCaption());
                                strClusterName = stpOfflineDataTable.getClusterName();
                                strClusterID = stpOfflineDataTable.getClusterCode();
                                if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
                                    mTowncode1 = strClusterID;
                                    mTownname1 = strClusterName;
                                    workDayCode = stpOfflineDataTable.getDayID();
                                    workDayName = stpOfflineDataTable.getDayCaption();
                                    tpDoctor = stpOfflineDataTable.getDoctorCode();
                                    tpChemists= stpOfflineDataTable.getChemistCode();
                                    tpWorkType = mWTCode1;
                                    tpCluster = stpOfflineDataTable.getClusterCode();
                                    binding.txtCluster1.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(strClusterName.trim()).replaceAll(",", " , ")));
                                    chk_cluster = mTowncode1;
                                } else {
                                    mTowncode2 = strClusterID;
                                    mTownname2 = strClusterName;
                                    workDayCode2 = stpOfflineDataTable.getDayID();
                                    workDayName2 = stpOfflineDataTable.getDayCaption();
                                    tpWorkType2 = mWTCode2;
                                    tpCluster2 = stpOfflineDataTable.getClusterCode();
                                    tpDoctor2 = stpOfflineDataTable.getDoctorCode();
                                    tpChemists2=stpOfflineDataTable.getChemistCode();
                                    binding.txtCluster2.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(strClusterName.trim()).replaceAll(",", " , ")));
                                    chk_cluster = mTowncode2;
                                }
                            }
                        }
                        binding.progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        Log.e("STP", "onFailure: ");
                        binding.progress.setVisibility(View.GONE);
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSTPDataToLocal(String hqCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray();
            JSONArray jsonDoc_mas = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getSfCode(requireContext())).getMasterSyncDataJsonArray();
            JSONArray jsonChm_mas = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + SharedPref.getSfCode(requireContext())).getMasterSyncDataJsonArray();
            JSONArray jsonCluster = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + SharedPref.getSfCode(requireContext())).getMasterSyncDataJsonArray();
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
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
                        Log.d("STP master data", "saveSTPDataToLocal1: " + jsonObject);

                        JSONObject jsonSave = new JSONObject();
                        jsonSave = CommonUtilsMethods.CommonObjectParameter(requireContext());
                        jsonSave.put("sfcode", hqCode);
                        jsonSave.put("DivCode", SharedPref.getDivisionCode(requireContext()));
                        jsonSave.put("Rsf", SharedPref.getHqCode(requireContext()));
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
                        jsonSave.put("Class_Name", doctorClass);
                        jsonSave.put("Plan_Name", dayCaption);
                        jsonSave.put("Plan_SName", dayID);
                        jsonSave.put("Plan_Code", dayPlanCode);
                        jsonSave.put("StpFlag", activeFlag);
                        jsonSave.put("tableName", "save_stp");
                        jsonSave.put("ReqDt", dateTime);
                        Log.d("STP save data", "saveSTPDataToLocal2: " + jsonSave);
                        int stpFlag = 3;
                        try {
                            stpFlag = Integer.parseInt(activeFlag);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        stpOfflineDataDao.saveSTPData(new STPOfflineDataTable(dayID, hqCode, dayCaption, clusterCode, clusterName, doctorCode, doctorName, chemistCode, chemistName, doctorSpeciality, doctorCategory, doctorClass, doctorCategoryCode, jsonObject.toString(), stpFlag, "0"));
                    } else {
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

                        JSONObject jsonSave = new JSONObject();
                        jsonSave = CommonUtilsMethods.CommonObjectParameter(requireContext());
                        jsonSave.put("sfcode", hqCode);
                        jsonSave.put("DivCode", SharedPref.getDivisionCode(requireContext()));
                        jsonSave.put("Rsf", SharedPref.getHqCode(requireContext()));
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
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void showDeviationAlert() {
        Dialog dialogRemarks = new Dialog(requireActivity());
        dialogRemarks.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogRemarks.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogRemarks.setCancelable(false);
        ImageView iv_close = dialogRemarks.findViewById(R.id.img_close);
        EditText ed_remarks = dialogRemarks.findViewById(R.id.ed_remark);
        ed_remarks.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_remarks, 300)});
        TextView heading = dialogRemarks.findViewById(R.id.tv_head);
        TextView content = dialogRemarks.findViewById(R.id.content);
        Button btn_clear = dialogRemarks.findViewById(R.id.btn_clear);
        Button btn_save = dialogRemarks.findViewById(R.id.btn_save);
        heading.setText(R.string.deviation_remarks);
        btn_save.setText(requireContext().getString(R.string.yes));
        btn_clear.setText(requireContext().getString(R.string.no));
        content.setVisibility(View.INVISIBLE);
        ed_remarks.setVisibility(View.VISIBLE);

        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (UtilityClass.isNetworkAvailable(requireContext())) {
                    remarks = ed_remarks.getText().toString().trim();
                    if (!remarks.isEmpty() && remarks.length() > 2) {
                        dialogRemarks.dismiss();
                        remarks = remarks.replaceAll("'", "");
                        Log.e("Deviation Remarks", "Deviation remark : " + remarks);
                        submitDeviation();
//                saveOrUpdateWorkPlan();
                    } else if (remarks.isEmpty()) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.please_enter_the_remarks));
                    } else {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.remarks_must_contain_at_least_3_characters));
                    }
                } else {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                }
            }
        });

        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogRemarks.dismiss();
            }
        });

        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogRemarks.dismiss();
            }
        });

        dialogRemarks.show();
    }

    private void submitDeviation() {
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            createDeviationJSON();
            deviationSubmitAPI();
        } else {
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
        }
    }

    private void createDeviationJSON() {
        try {
            if (DayPlanCount.equalsIgnoreCase("1")) {
                mHQCode = mHQCode1;
                mTowncode = mTowncode1;
                mHQName = mHQName1;
                mFwFlg = mFwFlg1;
            } else {
                if (IsFeildWorkFlag.equalsIgnoreCase("F1")) {
                    mHQCode = mHQCode1;
                    mTowncode = mTowncode1;
                    mHQName = mHQName1;

                } else if (IsFeildWorkFlag.equalsIgnoreCase("F2")) {
                    mHQCode = mHQCode2;
                    mTowncode = mTowncode1 + "," + mTowncode2;
                    mHQName = mHQName2;
                } else {
                    mHQCode = "";
                    mTowncode = "";
                    mHQName = "";
                }
            }

            deviationJSONObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
            deviationJSONObject.put("tableName", "deviate");
            deviationJSONObject.put("sfcode", SharedPref.getSfCode(requireContext()));
            deviationJSONObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                deviationJSONObject.put("Rsf", mHQCode1);
                deviationJSONObject.put("Rsf2", mHQCode2);
                deviationJSONObject.put("HQName", mHQName1);
                deviationJSONObject.put("HQName2", mHQName2);
            } else {
                deviationJSONObject.put("Rsf", SharedPref.getSfCode(requireContext()));
                deviationJSONObject.put("HQName", "");
            }

            deviationJSONObject.put("town_code", mTowncode1);
            deviationJSONObject.put("Town_name", mTownname1);
            deviationJSONObject.put("WT_code", mWTCode1);
            deviationJSONObject.put("WTName", mWTName1);
            deviationJSONObject.put("FwFlg", mFwFlg1);

            deviationJSONObject.put("town_code2", mTowncode2);
            deviationJSONObject.put("Town_name2", mTownname2);
            deviationJSONObject.put("WT_code2", mWTCode2);
            deviationJSONObject.put("WTName2", mWTName2);
            deviationJSONObject.put("FwFlg2", mFwFlg2);

            deviationJSONObject.put("location", gpsTrack.getLatitude() + ":" + gpsTrack.getLongitude());
            deviationJSONObject.put("address", CommonUtilsMethods.gettingAddress(getActivity(), gpsTrack.getLatitude(), gpsTrack.getLongitude(), false));
            deviationJSONObject.put("InsMode", deviation);   // previously it was 0
            deviationJSONObject.put("SubmittedDate", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
            deviationJSONObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
            deviationJSONObject.put("TpVwFlg", deviation);
            deviationJSONObject.put("day_flag", "0");
            deviationJSONObject.put("TP_cluster", tpCluster);
            deviationJSONObject.put("TP_worktype", tpWorkType);
            deviationJSONObject.put("TP_Doctor", tpDoctor);
            deviationJSONObject.put("TP_Chemists",tpChemists);
            deviationJSONObject.put("Others_Code", workDayCode);
            deviationJSONObject.put("Others_Name", workDayName);
            deviationJSONObject.put("TP_cluster2", tpCluster2);
            deviationJSONObject.put("TP_worktype2", tpWorkType2);
            deviationJSONObject.put("TP_Doctor2", tpDoctor2);
            deviationJSONObject.put("TP_Chemists2",tpChemists2);
            deviationJSONObject.put("Others_Code2", workDayCode2);
            deviationJSONObject.put("Others_Name2", workDayName2);
            if (TPDCRMGRApprNeed.equalsIgnoreCase("0")) {
                deviationJSONObject.put("deviation_req", "3");
            } else {
                deviationJSONObject.put("deviation_req", "4");
            }
            deviationJSONObject.put("deviate_reason", remarks);
            isFromTP = false;

            Log.e("Deviate JSON", "CreateJson: " + deviationJSONObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deviationSubmitAPI() {
        try {
            binding.progress.setVisibility(View.VISIBLE);
            Log.e("deviate:Object", deviationJSONObject.toString());

            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "deviate/dayplan");
            Call<JsonElement> saveMyDayPlan = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, deviationJSONObject.toString());

            saveMyDayPlan.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.d("todayCallList:Code", response.code() + " - " + response);
                    binding.progress.setVisibility(View.GONE);
                    EditSession = "";
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                SharedPref.setTpDcrDeviatedDate(requireContext(), HomeDashBoard.selectedDate.toString());
                                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                                if (DayPlanCount.equalsIgnoreCase("1")) {
                                    if (mFwFlg1.equalsIgnoreCase("F") || mFwFlg1.equalsIgnoreCase("A"))
                                        HomeDashBoard.binding.viewPager.setCurrentItem(1);
                                } else if (DayPlanCount.equalsIgnoreCase("2")) {
                                    if (mFwFlg2.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("A"))
                                        HomeDashBoard.binding.viewPager.setCurrentItem(1);
                                }
                                commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
                                if (TPDCRMGRApprNeed.equalsIgnoreCase("0")) {
                                    SharedPref.setTpDcrDeviationApprStatus(requireContext(), "3");
                                } else {
                                    saveOrUpdateWorkPlan(false);
                                }
                                updateLocalWPData();
                            } else {
                                setUpWorkPlan();
                                commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
                            }
                        } catch (Exception e) {
                            Log.e("Workplan", "onResponse: " + e.getMessage());
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    binding.progress.setVisibility(View.GONE);
                    EditSession = "";
                    setUpWorkPlan();
                    Log.e("VALUES", String.valueOf(t));
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            binding.progress.setVisibility(View.GONE);
            EditSession = "";
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
        }
    }

    private void onSaveClicked() {
//        if(((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
//                || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")))
//                && binding.switchButton.isChecked()) {
//                if(UtilityClass.isNetworkAvailable(requireContext())) {
//                    showDeviationAlert();
//                }else {
//                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
//                }
//        } else {
        if (SharedPref.getGeoChk(requireContext()).equalsIgnoreCase("0")) {
            if ((gpsTrack.getLatitude() != 0.0) || (gpsTrack.getLongitude() != 0.0)) {
                saveOrUpdateWorkPlan(true);
            } else {
                gpsTrack = new GPSTrack(requireActivity());
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_location_please_try_again));
            }
        } else {
            saveOrUpdateWorkPlan(true);
        }
//        }
    }

    private void CheckInDate(boolean saveWorkPlan) {
        try {
            gpsTrack = new GPSTrack(requireActivity());
            latitude = gpsTrack.getLatitude();
            longitude = gpsTrack.getLongitude();
            if (UtilityClass.isNetworkAvailable(requireContext())) {
                address = CommonUtilsMethods.gettingAddress(requireActivity(), latitude, longitude, false);
            } else {
                address = getString(R.string.no_address_found);
            }
        } catch (Exception e) {
            address = getString(R.string.no_address_found);
            e.printStackTrace();
        }

        dialogCheckIn = new Dialog(requireActivity());
        dialogCheckIn.setContentView(R.layout.dialog_day_check_in);
        dialogCheckIn.setCancelable(false);
        if (dialogCheckIn.getWindow() != null) {
            dialogCheckIn.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        tvName = dialogCheckIn.findViewById(R.id.txt_cus_name);
        tvName.setText(String.format("%s%s", getResources().getString(R.string.hi), SharedPref.getSfName(requireContext())));

        tvDateTime = dialogCheckIn.findViewById(R.id.txt_date_time);
        tvDateTime.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));
        startClock(tvDateTime, dialogCheckIn, "Check-In");

        tvLat = dialogCheckIn.findViewById(R.id.txt_lat);
        tvLat.setText(String.valueOf(latitude));

        tvLong = dialogCheckIn.findViewById(R.id.txt_long);
        tvLong.setText(String.valueOf(longitude));

        tvAddress = dialogCheckIn.findViewById(R.id.txt_address);
        tvAddress.setText(address);

        imgClose = dialogCheckIn.findViewById(R.id.img_close);
        btnCheckIn = dialogCheckIn.findViewById(R.id.btn_checkin);

        imgClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                stopClock();
                dialogCheckIn.dismiss();
                SharedPref.setCheckTodayCheckInOut(requireContext(), "");
                SharedPref.setDayCheckInData(requireContext(), "");
            }
        });

        ProgressBar progressBar = dialogCheckIn.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        RelativeLayout refreshLocation = dialogCheckIn.findViewById(R.id.rl_refresh_location);
        refreshLocation.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                try {
                    btnCheckIn.setEnabled(false);
                    stopClock();
                    startClock(tvDateTime, dialogCheckIn, "Check-In");
                    progressBar.setVisibility(View.VISIBLE);
                    gpsTrack = new GPSTrack(requireActivity());
                    gpsTrack.setLocationChangeListener(location -> {
                        try {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            if (UtilityClass.isNetworkAvailable(requireContext())) {
                                address = CommonUtilsMethods.gettingAddress(requireActivity(), latitude, longitude, false);
                            } else {
                                address = getString(R.string.no_address_found);
                            }

                            tvLat.setText(String.valueOf(latitude));
                            tvLong.setText(String.valueOf(longitude));
                            tvAddress.setText(address);
                            progressBar.setVisibility(View.GONE);
                            btnCheckIn.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnCheckIn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                stopClock();
                SharedPref.setCheckInTime(requireContext(), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                jsonCheck = CommonUtilsMethods.CommonObjectParameter(requireContext());
                try {
                    jsonCheck.put("tableName", "saveCheckin_out");
                    jsonCheck.put("sfcode", SharedPref.getSfCode(requireContext()));
                    jsonCheck.put("division_code", SharedPref.getDivisionCode(requireContext()).replaceAll(",", ""));
                    jsonCheck.put("lat", latitude);
                    jsonCheck.put("long", longitude);
                    jsonCheck.put("address", address);
                    jsonCheck.put("update", "0");
                    jsonCheck.put("Check_In", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                    jsonCheck.put("Check_Out", "");
                    jsonCheck.put("DateTime", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                    jsonCheck.put("Activity_Dt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                    Log.v("CheckInOut", "--json--" + jsonCheck.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPref.setDayCheckInData(requireContext(), jsonCheck.toString());
                try {
                    JSONObject checkInObj = new JSONObject();
                    checkInObj.put("id", "");
                    checkInObj.put("status", "1");
                    checkInObj.put("Start_Lat", latitude);
                    checkInObj.put("Start_Long", longitude);
                    checkInObj.put("Start_addres", address);
                    JSONObject date = new JSONObject();
                    date.put("date", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
                    checkInObj.put("Start_Time", date);
                    date = new JSONObject();
                    date.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.toString()));
                    checkInObj.put("Activity_Date", date);
                    Log.v("CheckIn Object", checkInObj.toString());
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(checkInObj);
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CHECK_IN, jsonArray.toString(), 2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (UtilityClass.isNetworkAvailable(requireContext()) && !outboxUtil.isOutBoxNonSyncDataAvailable()) {
                    progressDialog = CommonUtilsMethods.createProgressDialog(requireContext());
                    CallCheckInAPI(saveWorkPlan);
                } else {
                    SharedPref.setCheckTodayCheckInOut(requireContext(), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
                    offlineCheckInOutDataDao.saveCheckIn(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), jsonCheck.toString());
                    SetupOutBoxAdapter(requireActivity(), requireContext());
                    dialogCheckIn.dismiss();
                    onSaveClicked();
//                CallDialogAfterCheckIn();
                }
            }
        });
        if (!requireActivity().isFinishing()) {
            dialogCheckIn.show();
        }
    }

    private void CallCheckInAPI(boolean saveWorkPlan) {
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/activity");
        Call<JsonElement> callCheckInOut = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonCheck.toString());
        callCheckInOut.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                Log.v("CheckInOut", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            CheckInOutStatus = obj.getString("msg");
                        }

                        if (CheckInOutStatus.equalsIgnoreCase("1")) {
                            dialogCheckIn.dismiss();
                            SharedPref.setCheckTodayCheckInOut(requireContext(), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
                            if (saveWorkPlan) {
                                onSaveClicked();
                            }
//                            CallDialogAfterCheckIn();
                        } else {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.toast_leave_posted));
                        }
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.contact_admin_in));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
            }
        });
    }

    private void submitMyDayPlan() {
        if (HomeDashBoard.selectedDate != null && !HomeDashBoard.selectedDate.toString().isEmpty()) {
            if (binding.rlworktype1.isEnabled()) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
            } else if (binding.cardPlan2.getVisibility() == View.VISIBLE && binding.rlworktype2.isEnabled() && !mWTName2.isEmpty()) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
            } else if (mWTName1.isEmpty()) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
            } else if (mTowncode1.isEmpty() && mFwFlg1.equalsIgnoreCase("F")) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_cluster));
            } else if ((mFwFlg1.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("F"))
                    && ((SharedPref.getLastCallDate(requireContext()).isEmpty()
                    || !SharedPref.getLastCallDate(requireContext()).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())))) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.should_have_a_call));
            } else if (isFromTP) {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
            } else if ((mFwFlg1.equalsIgnoreCase("L") || mFwFlg2.equalsIgnoreCase("L")) && !rejectedReason.isEmpty()) {
                String leaveName = findWTName("L");
                if (leaveName.isEmpty()) {
                    leaveName = "Leave";
                }
                commonUtilsMethods.showToastMessage(requireContext(), leaveName + getString(R.string.has_been_rejected_kindly_select_other_work_type_and_save_work_plan));
            } else {
//                SharedPref.setCheckTodayCheckInOut(requireContext(), HomeDashBoard.selectedDate.toString());
                if (CheckInOutManager.isCheckInAvailable(requireContext())) {
                    CallDialogCheckOut();
                } else {
                    remarksAlertBox();
                }
            }
        } else {
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.please_select_a_date));
        }
    }

    private void saveOrUpdateWorkPlan(boolean isSaveClicked) {
        if (EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
            handleSession("1", NeedClusterFlag1, binding.txtheadquaters1, binding.txtCluster1, binding.txtWorktype1, binding.txtworkday1, mFwFlg1, isSaveClicked);
        } else {
            handleSession("2", NeedClusterFlag2, binding.txtheadquaters2, binding.txtCluster2, binding.txtWorktype2, binding.txtworkday2, mFwFlg2, isSaveClicked);
        }
    }

    private void handleSession(String sessionId, boolean needClusterFlag, TextView txtHq, TextView txtCluster, TextView txtWorkType, TextView txtWorkDay, String FWFlag, boolean isSaveClicked) {
        boolean isSession1 = sessionId.equals("1");

        if (needClusterFlag) {
            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && isEmpty(txtHq)) {
                showToast(R.string.select_hq);
                return;
            }

            if (isEmpty(txtCluster)) {
                showToast(R.string.select_cluster);
                return;
            }

            try {
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && !SharedPref.getOneBuild(requireContext()).equals("0")) {
                    String[] hqCodes = CommonUtilsMethods.removeLastComma(mHQCode1).split(",");
                    String[] hqNames = CommonUtilsMethods.removeLastComma(mHQName1).split(",");
                    if (sessionId.equalsIgnoreCase("2")) {
                        hqCodes = CommonUtilsMethods.removeLastComma(mHQCode2).split(",");
                        hqNames = CommonUtilsMethods.removeLastComma(mHQName2).split(",");
                    }
                    String hqName = "";
                    boolean isClusterNotSelected = false;
                    for (int i = 0; i < hqCodes.length; i++) {
                        String HQCode = hqCodes[i];
                        if (!mapSelectedCluster.containsKey(HQCode) || mapSelectedCluster.get(HQCode) == null || mapSelectedCluster.get(HQCode).isEmpty()) {
                            isClusterNotSelected = true;
                            hqName = hqNames[i];
                            break;
                        }
                    }
                    if (isClusterNotSelected) {
                        showToast(getString(R.string.select_any) + SharedPref.getClusterCap(requireContext()) + "for " + hqName);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (isEmpty(txtWorkType)) {
            showToast(R.string.select_worktype);
            return;
        }

        if (FWFlag.equalsIgnoreCase("L")) {
            showToast(getString(R.string.apply_leave_in_the_leave_application));
            return;
        }

        if (isTPorSTPBased() && binding.switchButton.isChecked() && binding.llDeviation.getVisibility() == View.VISIBLE) {
            if (isEmpty(txtWorkDay) && FWFlag.equalsIgnoreCase("F")
                    && (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0")
                    && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0")
                    && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")
                    && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0))) {
                showToast(getString(R.string.select_work_day_for_session) + sessionId);
                return;
            }

            if (isSaveClicked) {
                if (UtilityClass.isNetworkAvailable(requireContext())) {
                    showDeviationAlert();
                } else {
                    showToast(R.string.no_network);
                }
                return;
            }
        }

        if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")
                && !CheckInOutManager.isCheckedIn(requireContext())
                && HomeDashBoard.selectedDate != null
                && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
            CheckInDate(true);
            return;
        }

        // All validation passed
        disableSession1();
        if (!isSession1) {
            disableSession2();
        }

        CreateJson();
        saveOrUpdateWP(sessionId);
    }

    private boolean isEmpty(TextView view) {
        return view.getText().toString().trim().isEmpty();
    }

    private void showToast(int resId) {
        commonUtilsMethods.showToastMessage(requireContext(), getString(resId));
    }

    private void showToast(String message) {
        commonUtilsMethods.showToastMessage(requireContext(), message);
    }

    private boolean isTPorSTPBased() {
        return (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
                || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") &&
                STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") &&
                TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"));
    }

//    private void saveOrUpdateWorkPlan() {
//        if(EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) {
//            if(NeedClusterFlag1) {
//                if(binding.txtheadquaters1.getText().toString().equalsIgnoreCase("") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
//                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_hq));
//                }else if(binding.txtCluster1.getText().toString().equalsIgnoreCase("")) {
//                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_cluster));
//                }else if(CheckInOutManager.isCheckedId(requireContext())) {
//                    CheckInOutDate(true);
//                }else if(((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
//                        || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")))
//                        && binding.switchButton.isChecked()) {
//                    if((STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))) {
//                        if((EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) && binding.txtworkday1.getText().toString().trim().isEmpty()) {
//                            commonUtilsMethods.showToastMessage(requireContext(), "Select Work Day for Session 1");
//                        }else if((EditSession.equalsIgnoreCase("2") || DayPlanCount.equalsIgnoreCase("2")) && binding.txtworkday2.getText().toString().trim().isEmpty()) {
//                            commonUtilsMethods.showToastMessage(requireContext(), "Select Work Day for Session 2");
//                        }
//                    } else {
//                        if(UtilityClass.isNetworkAvailable(requireContext())) {
//                            showDeviationAlert();
//                        }else {
//                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
//                        }
//                    }
//                }else {
//                    disableSession1();
//                    CreateJson();
//                    saveOrUpdateWP("1");
//                }
//            }else if(binding.txtWorktype1.getText().toString().equalsIgnoreCase("")) {
//                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
//            }else if(CheckInOutManager.isCheckedId(requireContext())) {
//                CheckInOutDate(true);
//            }else if(((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
//                    || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")))
//                    && binding.switchButton.isChecked()) {
//                if((STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))) {
//                    if((EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) && binding.txtworkday1.getText().toString().trim().isEmpty()) {
//                        commonUtilsMethods.showToastMessage(requireContext(), "Select Work Day for Session 1");
//                    }else if((EditSession.equalsIgnoreCase("2") || DayPlanCount.equalsIgnoreCase("2")) && binding.txtworkday2.getText().toString().trim().isEmpty()) {
//                        commonUtilsMethods.showToastMessage(requireContext(), "Select Work Day for Session 2");
//                    }
//                } else {
//                    if(UtilityClass.isNetworkAvailable(requireContext())) {
//                        showDeviationAlert();
//                    }else {
//                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
//                    }
//                }
//            }else {
//                disableSession1();
//                CreateJson();
//                saveOrUpdateWP("1");
//            }
//        }else {
//            if(NeedClusterFlag2) {
//                if(binding.txtheadquaters2.getText().toString().equalsIgnoreCase("") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
//                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_hq));
//                }else if(binding.txtCluster2.getText().toString().equalsIgnoreCase("")) {
//                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_cluster));
//                }else if(CheckInOutManager.isCheckedId(requireContext())) {
//                    CheckInOutDate(true);
//                }else if(((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
//                        || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")))
//                        && binding.switchButton.isChecked()) {
//                    if((STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))) {
//                        if((EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) && binding.txtworkday1.getText().toString().trim().isEmpty()) {
//                            commonUtilsMethods.showToastMessage(requireContext(), "Select Work Day for Session 1");
//                        }else if((EditSession.equalsIgnoreCase("2") || DayPlanCount.equalsIgnoreCase("2")) && binding.txtworkday2.getText().toString().trim().isEmpty()) {
//                            commonUtilsMethods.showToastMessage(requireContext(), "Select Work Day for Session 2");
//                        }
//                    } else {
//                        if(UtilityClass.isNetworkAvailable(requireContext())) {
//                            showDeviationAlert();
//                        }else {
//                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
//                        }
//                    }
//                }else {
//                    disableSession1();
//                    disableSession2();
//                    CreateJson();
//                    saveOrUpdateWP("2");
//                }
//            }else if(binding.txtWorktype2.getText().toString().equalsIgnoreCase("")) {
//                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_worktype));
//            }else if(CheckInOutManager.isCheckedId(requireContext())) {
//                CheckInOutDate(true);
//            }else if(((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))
//                    || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")))
//                    && binding.switchButton.isChecked()) {
//                if((STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0"))) {
//                    if((EditSession.equalsIgnoreCase("1") || DayPlanCount.equalsIgnoreCase("1")) && binding.txtworkday1.getText().toString().trim().isEmpty()) {
//                        commonUtilsMethods.showToastMessage(requireContext(), "Select Work Day for Session 1");
//                    }else if((EditSession.equalsIgnoreCase("2") || DayPlanCount.equalsIgnoreCase("2")) && binding.txtworkday2.getText().toString().trim().isEmpty()) {
//                        commonUtilsMethods.showToastMessage(requireContext(), "Select Work Day for Session 2");
//                    }
//                } else {
//                    if(UtilityClass.isNetworkAvailable(requireContext())) {
//                        showDeviationAlert();
//                    }else {
//                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
//                    }
//                }
//            }else {
//                disableSession1();
//                disableSession2();
//                CreateJson();
//                saveOrUpdateWP("2");
//            }
//        }
//    }

    private void saveOrUpdateWP(String sessionType) {
        if (binding.txtSave.getText().equals(getString(R.string.save))) {
//            if(mWTCode1.equalsIgnoreCase(previousWTCode1) && mWTCode2.equalsIgnoreCase(previousWTCode2) && !isFromTP) {
//                CallsFragment.syncCalls();
//                updateLocalWPData();
//                setUpWorkPlan();
//                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.work_plan_updated_successfully));
//            }else {
            if (!HomeDashBoard.binding.textDate.getText().toString().trim().isEmpty()
                    && HomeDashBoard.binding.textDate.getText().toString().trim().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_27))) {
                SharedPref.setIsWorkingToday(requireContext(), true);
                SharedPref.setLastKnownDate(requireContext(), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
            } else if (!SharedPref.getIsWorkingToday(requireContext())) {
                SharedPref.setIsWorkingToday(requireContext(), false);
            }
            if (UtilityClass.isNetworkAvailable(requireContext()) && !outboxUtil.isOutBoxNonSyncDataAvailable()) {
                workPlanSubmit("Save");
            } else {
                SaveWTLocal(sessionType);
            }
//            }
        } else if (binding.txtSave.getText().equals(getString(R.string.update))) {
            binding.txtSave.setText(getString(R.string.save));
            binding.txtAddPlan.setText(getString(R.string.add_plan));
            if (UtilityClass.isNetworkAvailable(requireContext())) {
//                updateWorkPlan();
//                deleteSession(sessionType, "Edit");
//                createDeleteJson(sessionType);
                if (mWTCode1.equalsIgnoreCase(previousWTCode1) && mWTCode2.equalsIgnoreCase(previousWTCode2) && !isFromTP) {
                    workPlanEditSubmit();
//                    CallsFragment.syncCalls();
//                    updateLocalWPData();
//                    setUpWorkPlan();
//                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.work_plan_updated_successfully));
                } else {
                    callDeleteWP(sessionType, "Edit");
                }
//                workPlanSubmit();
            } else {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network) + getString(R.string.no_network_update_wp));
                if (sessionType.equals("1")) {
                    enableEditSession1();
                } else if (sessionType.equals("2")) {
                    enableEditSession2();
                }
            }
        }
        if (!mFwFlg1.equalsIgnoreCase("F") && !mFwFlg2.equalsIgnoreCase("F")) {
            checkAndClearCalls();
        }
        DcrCallTabLayoutActivity.TodayPlanSfCode = "";
        DcrCallTabLayoutActivity.TodayPlanSfName = "";
    }

    private void SaveWTLocal(String isWhich) {
        try {
            SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode1);
            JSONArray MydayPlanDataList = new JSONArray();
            JSONObject firstSessionObject = new JSONObject();
            JSONObject secondSessionObject = new JSONObject();
            JSONObject jsonObjectwt = new JSONObject();

            if (isWhich.equalsIgnoreCase("1")) {
                callOfflineWorkTypeDataDao.insert(new CallOfflineWorkTypeDataTable(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), mWTName1, mWTCode1, jsonObject.toString(), "", 0));
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                    SharedPref.saveHq(requireContext(), mHQName1.split(",")[0], mHQCode1.split(",")[0]);
                    SharedPref.saveMultiHQ(requireContext(), mHQName1, mHQCode1);
                } else {
                    SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
                }
                String todayPlanClusterCode = mTowncode1;
                if (mFwFlg2.equalsIgnoreCase("F")) todayPlanClusterCode = mTowncode1 + ","+ mTowncode2;
                SharedPref.setTodayDayPlanClusterCode(requireContext(), todayPlanClusterCode);
                if (mFwFlg1.equalsIgnoreCase("F") || mFwFlg1.equalsIgnoreCase("A"))
                    HomeDashBoard.binding.viewPager.setCurrentItem(1);
            } else {
                callOfflineWorkTypeDataDao.insert(new CallOfflineWorkTypeDataTable(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), mWTName2, mWTCode2, jsonObject.toString(), "", 0));
                OutboxFragment.SetupOutBoxAdapter(requireActivity(), requireContext());
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                    SharedPref.saveHq(requireContext(), mHQName2.split(",")[0], mHQCode2.split(",")[0]);
                    SharedPref.saveMultiHQ(requireContext(), mHQName2, mHQCode2);
                } else {
                    SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
                }
                String todayPlanClusterCode = mTowncode2;
                if (mFwFlg1.equalsIgnoreCase("F")) todayPlanClusterCode = mTowncode1 + ","+ mTowncode2;
                SharedPref.setTodayDayPlanClusterCode(requireContext(), todayPlanClusterCode);
                if (mFwFlg2.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("A"))
                    HomeDashBoard.binding.viewPager.setCurrentItem(1);
            }

            boolean isTodayAvailable = false;
            JSONArray jsonData = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();

            if (jsonData.length() > 0) {
                for (int i = 0; i < jsonData.length(); i++) {
                    jsonObjectwt = jsonData.getJSONObject(i);
                    if (jsonObjectwt.getJSONObject("TPDt").getString("date").substring(0, 11).equalsIgnoreCase(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"))) {
                        isTodayAvailable = true;
                        break;
                    }
                }
            }

            if (isTodayAvailable) {
                jsonObjectwt = new JSONObject();
                jsonObjectwt.put("SFCode", SharedPref.getSfCode(requireContext()));
                JSONObject TPDtSecondSeasonObject = new JSONObject();
                TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                jsonObjectwt.put("TPDt", TPDtSecondSeasonObject);
                jsonObjectwt.put("WT", mWTCode2);
                jsonObjectwt.put("WTNm", mWTName2);
                jsonObjectwt.put("FWFlg", mFwFlg2);
                jsonObjectwt.put("SFMem", mHQCode2);
                jsonObjectwt.put("HQNm", mHQName2);
                jsonObjectwt.put("Pl", mTowncode2);
                jsonObjectwt.put("PlNm", mTownname2);
                jsonObjectwt.put("Rem", "");
                jsonObjectwt.put("TpVwFlg", deviation.equals("1") ? "1" : "2");
                if (mFwFlg2.equalsIgnoreCase("F")) {
                    jsonObjectwt.put("TP_Doctor", tpDoctor);
                    jsonObjectwt.put("TP_Chemists",tpChemists);
                    jsonObjectwt.put("TP_cluster", tpCluster);
                    jsonObjectwt.put("TP_worktype", tpWorkType);
                    jsonObjectwt.put("Others_Code", workDayCode);
                    jsonObjectwt.put("Others_Name", workDayName);
                }
                jsonData.put(secondSessionObject);
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonData.toString(), 2));
            } else {
                firstSessionObject.put("SFCode", SharedPref.getSfCode(requireContext()));
                JSONObject TPDtFisrstSeasonObject = new JSONObject();
                TPDtFisrstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                firstSessionObject.put("TPDt", TPDtFisrstSeasonObject);
                firstSessionObject.put("WT", mWTCode1);
                firstSessionObject.put("WTNm", mWTName1);
                firstSessionObject.put("FWFlg", mFwFlg1);
                firstSessionObject.put("SFMem", mHQCode1);
                firstSessionObject.put("HQNm", mHQName1);
                firstSessionObject.put("Pl", mTowncode1);
                firstSessionObject.put("PlNm", mTownname1);
                firstSessionObject.put("Rem", "");
                firstSessionObject.put("TpVwFlg", deviation.equals("1") ? "1" : "2");
                if (mFwFlg1.equalsIgnoreCase("F")) {
                    firstSessionObject.put("TP_Doctor", tpDoctor);
                    firstSessionObject.put("TP_Chemists",tpChemists);
                    firstSessionObject.put("TP_cluster", tpCluster);
                    firstSessionObject.put("TP_worktype", tpWorkType);
                    firstSessionObject.put("Others_Code", workDayCode);
                    firstSessionObject.put("Others_Name", workDayName);
                }
                MydayPlanDataList.put(firstSessionObject);

                secondSessionObject.put("SFCode", SharedPref.getSfCode(requireContext()));
                JSONObject TPDtSecondSeasonObject = new JSONObject();
                TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                secondSessionObject.put("TPDt", TPDtSecondSeasonObject);
                secondSessionObject.put("WT", mWTCode2);
                secondSessionObject.put("WTNm", mWTName2);
                secondSessionObject.put("FWFlg", mFwFlg2);
                secondSessionObject.put("SFMem", mHQCode2);
                secondSessionObject.put("HQNm", mHQName2);
                secondSessionObject.put("Pl", mTowncode2);
                secondSessionObject.put("PlNm", mTownname2);
                secondSessionObject.put("Rem", "");
                secondSessionObject.put("TpVwFlg", deviation.equals("1") ? "1" : "2");
                if (mFwFlg2.equalsIgnoreCase("F")) {
                    secondSessionObject.put("TP_Doctor", tpDoctor2);
                    secondSessionObject.put("TP_Chemists",tpChemists2);
                    secondSessionObject.put("TP_cluster", tpCluster2);
                    secondSessionObject.put("TP_worktype", tpWorkType2);
                    secondSessionObject.put("Others_Code", workDayCode2);
                    secondSessionObject.put("Others_Name", workDayName2);
                }
                MydayPlanDataList.put(secondSessionObject);
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, MydayPlanDataList.toString(), 2));
            }

            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
            boolean isCallSyncAvailable = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String custType = jsonObject.getString("CustType");
                String time = jsonObject.getString("Dcr_dt");
                if (custType.equalsIgnoreCase("0") && time.equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                    isCallSyncAvailable = true;
                    break;
                }
            }


            String FW_Indicator;
            String Workname;

            if (mFwFlg1.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("F")) {
                FW_Indicator = "F";
                Workname = "Field Work";
            } else {
                FW_Indicator = mFwFlg1;
                Workname = mWTName1;
            }
//month_name .Mnth  Yr
            if (!isCallSyncAvailable) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("CustCode", "");
                jsonObject.put("CustType", "0");
                jsonObject.put("Dcr_dt", HomeDashBoard.selectedDate.toString());
                jsonObject.put("month_name", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_9, HomeDashBoard.selectedDate.toString()));
                jsonObject.put("Mnth", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_31, HomeDashBoard.selectedDate.toString()));
                jsonObject.put("Yr", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_10, HomeDashBoard.selectedDate.toString()));
                jsonObject.put("vtm", CommonUtilsMethods.getCurrentInstance("hh:mm aa"));
                jsonObject.put("CustName", "");
                jsonObject.put("town_code", "");
                jsonObject.put("FW_Indicator", FW_Indicator);
                jsonObject.put("WorkType_Name", Workname);
                jsonObject.put("town_name", "");
                jsonObject.put("Dcr_flag", "0");
                jsonObject.put("SF_Code", "");
                jsonObject.put("Trans_SlNo", "");
                jsonObject.put("AMSLNo", "");
                jsonObject.put("day_status", "0");
                jsonArray.put(jsonObject);
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, jsonArray.toString(), 2));
            }
            SharedPref.setDayPlanStartedDate(requireContext(), HomeDashBoard.selectedDate.toString());
            OutboxFragment.SetupOutBoxAdapter(requireActivity(), requireContext());
            SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.save_wt_locally));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMultiHQClusters(String hqCodes) {
        StringBuilder clusterCodes = new StringBuilder();
        StringBuilder clusterNames = new StringBuilder();
        String[] hqs = CommonUtilsMethods.removeLastComma(hqCodes).split(",");
        for (String hq : hqs) {
            if (mapSelectedCluster.containsKey(hq) && mapSelectedCluster.get(hq) != null) {
                for (MultiHQClusterItem cluster : mapSelectedCluster.get(hq)) {
                    clusterCodes.append(cluster.getCode());
                    clusterNames.append(cluster.getName());
                    clusterCodes.append(",");
                    clusterNames.append(",");
                }
            }
            clusterCodes.append("$");
            clusterNames.append("$");
        }
        if (clusterCodes.toString().replace("$", "").isEmpty()) {
            return "";
        }
        return clusterCodes + "^^" + clusterNames;
    }

    private void CreateJson() {
        try {
            if (DayPlanCount.equalsIgnoreCase("1")) {
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && !SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                    String[] clusterData = getMultiHQClusters(mHQCode1).split("\\^\\^");
                    if (clusterData.length > 1) {
                        mTowncode1 = clusterData[0];
                        mTownname1 = clusterData[1];
                    }
                }
                mHQCode = mHQCode1;
                mTowncode = mTowncode1;
                mHQName = mHQName1;
                mFwFlg = mFwFlg1;
            } else {
                if (IsFeildWorkFlag.equalsIgnoreCase("F1")) {
                    if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && !SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                        String[] clusterData = getMultiHQClusters(mHQCode1).split("\\^\\^");
                        if (clusterData.length > 1) {
                            mTowncode1 = clusterData[0];
                            mTownname1 = clusterData[1];
                        }
                    }
                    mHQCode = mHQCode1;
                    mTowncode = mTowncode1;
                    mHQName = mHQName1;

                } else if (IsFeildWorkFlag.equalsIgnoreCase("F2")) {
                    if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && !SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                        String[] clusterData = getMultiHQClusters(mHQCode2).split("\\^\\^");
                        if (clusterData.length > 1) {
                            mTowncode2 = clusterData[0];
                            mTownname2 = clusterData[1];
                        }
                    }
                    mHQCode = mHQCode2;
                    mTowncode = mTowncode1 + "," + mTowncode2;
                    mHQName = mHQName2;
                } else {
                    mHQCode = "";
                    mTowncode = "";
                    mHQName = "";
                }
            }

            jsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
            jsonObject.put("tableName", "dayplan");
            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && !SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                jsonObject.put("tableName", "dayplanmultihq");
            }
            jsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
            jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                jsonObject.put("Rsf", mHQCode1);
                jsonObject.put("Rsf2", mHQCode2);
            } else {
                jsonObject.put("Rsf", SharedPref.getSfCode(requireContext()));
            }

            jsonObject.put("town_code", mTowncode1);
            jsonObject.put("Town_name", mTownname1);
            jsonObject.put("WT_code", mWTCode1);
            jsonObject.put("WTName", mWTName1);
            jsonObject.put("FwFlg", mFwFlg1);

            jsonObject.put("town_code2", mTowncode2);
            jsonObject.put("Town_name2", mTownname2);
            jsonObject.put("WT_code2", mWTCode2);
            jsonObject.put("WTName2", mWTName2);
            jsonObject.put("FwFlg2", mFwFlg2);

            String sfMem = "", sfMem2 = "", planName = "", planName2 = "", planCode = "", planCode2 = "";
            if (!mHQCode1.isEmpty() && mHQCode1.split(",").length > 0) {
                sfMem = mHQCode1.split(",")[0];
            }
            if (!mHQCode2.isEmpty() && mHQCode2.split(",").length > 0) {
                sfMem2 = mHQCode2.split(",")[0];
            }
            if (!mTownname1.isEmpty() && mTownname1.split("\\$").length > 0) {
                planName = mTownname1.split("\\$")[0];
            }
            if (!mTownname2.isEmpty() && mTownname2.split("\\$").length > 0) {
                planName2 = mTownname2.split("\\$")[0];
            }
            if (!mTowncode1.isEmpty() && mTowncode1.split("\\$").length > 0) {
                planCode = mTowncode1.split("\\$")[0];
            }
            if (!mTowncode2.isEmpty() && mTowncode2.split("\\$").length > 0) {
                planCode2 = mTowncode2.split("\\$")[0];
            }

            jsonObject.put("SfMem", sfMem);
            jsonObject.put("SfMem2", sfMem2);
            jsonObject.put("plan_code", planCode);
            jsonObject.put("plan_code2", planCode2);
            jsonObject.put("plan_name", planName);
            jsonObject.put("plan_name2", planName2);

            jsonObject.put("Remarks", mRemarks1);
            jsonObject.put("location", gpsTrack.getLatitude() + ":" + gpsTrack.getLongitude());
            jsonObject.put("address", CommonUtilsMethods.gettingAddress(getActivity(), gpsTrack.getLatitude(), gpsTrack.getLongitude(), false));
            if (SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                jsonObject.put("InsMode", deviation);
            } else {
                jsonObject.put("InsMode", insMode);
            }
            jsonObject.put("SubmittedDate", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
            jsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
            jsonObject.put("TpVwFlg", deviation);
            jsonObject.put("TP_cluster", tpCluster);
            jsonObject.put("TP_worktype", tpWorkType);
            jsonObject.put("TP_Doctor", tpDoctor);
            jsonObject.put("TP_Chemists",tpChemists);
            jsonObject.put("TP_cluster2", tpCluster2);
            jsonObject.put("TP_worktype2", tpWorkType2);
            jsonObject.put("TP_Doctor2", tpDoctor2);
            jsonObject.put("TP_Chemists2",tpChemists2);
            jsonObject.put("day_flag", "0");

            SharedPref.setTodayTPDoctor(requireContext(), tpDoctor);
            Log.e("TPDoctorSave", "Saved TP Doctor => " + tpDoctor);

            SharedPref.setTodayTPChemists(requireContext(),tpChemists);
            Log.e("TPChemistSave","Saved TP Chemist =>" + tpChemists);

            ((HomeDashBoard) requireActivity()).showDoctorPlanPopup(tpDoctor, isFromTP,tpChemists);

            jsonObject.put("Others_Code", workDayCode);
            jsonObject.put("Others_Name", workDayName);
            jsonObject.put("Others_Code2", workDayCode2);
            jsonObject.put("Others_Name2", workDayName2);
            isFromTP = false;

            Log.e("SAVE JSON", "CreateJson: " + jsonObject.toString());
            insMode = "0";

            String doc = jsonObject.getString("TP_Doctor");
            SharedPref.setTodayTPDoctor(requireContext(), doc);
            Log.d("TAG", doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallCheckOutAPI() {
        try {
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/activity");
            Call<JsonElement> callCheckInOut = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonCheck.toString());
            callCheckInOut.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    assert response.body() != null;
                    Log.v("CheckInOut", response.body() + "--" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                CheckInOutStatus = obj.getString("msg");
                            }

                            if (CheckInOutStatus.equalsIgnoreCase("1")) {
                                SharedPref.setCheckInTime(requireContext(), "");
                                SharedPref.setCheckTodayCheckInOut(requireContext(), "");
                                SharedPref.setDayCheckInData(requireContext(), "");
                                dialogCheckOut.dismiss();
                                CallFinalSubmitAPI();
//                            remarksAlertBox();
                            } else {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.toast_leave_posted));
                            }
                            progressDialog.dismiss();
                        } catch (Exception ignored) {
                            progressDialog.dismiss();
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.contact_admin_out));
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallFinalSubmitAPI() {
        try {
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/daysubmit");
            Call<JsonElement> callFinalSubmit = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, finalSubmitJSONObject.toString());
            callFinalSubmit.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    assert response.body() != null;
                    Log.v("FinalSubmit", response.body() + "--" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        try {
                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 2));
                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CHECK_IN, "[]", 2));
//                            MyDayPlanEntriesNeeded.syncCallAndDate(requireContext());
                            updateLocalData();
                            SharedPref.setIsWorkingToday(requireContext(), false);
                            if (finalSubmitDialog != null && finalSubmitDialog.isShowing()) {
                                finalSubmitDialog.dismiss();
                            }
                            SharedPref.setDayPlanStartedDate(requireContext(), "");
                            SharedPref.setLastCallDate(requireContext(), "");
                            SharedPref.setSelectedDateCal(requireContext(), "");
                            SharedPref.setCheckTodayCheckInOut(requireContext(), "");
                            SharedPref.setDayCheckInData(requireContext(), "");
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            previousWTCode1 = "";
                            previousWTCode2 = "";
//                            HomeDashBoard.canMoveNextDate = false;
                            FinalSubmitStatus = jsonObject.getString("Msg");
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.day_submitted_successfully));
                            WorkPlanEntriesNeeded.skipDates.clear();
                            progressDialog.dismiss();
                            HomeDashBoard.checkAndSetEntryDate(requireContext(), true);
                        } catch (Exception e) {
                            Log.e("WP", "onResponse: " + e.getMessage());
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.cannot_submit_work_plan));
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLocalData() {
        DayPlanCount = "1";
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
            boolean isDateFound = false;
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                String cusType = jsonObject.optString("CustType");
                String dayStatus = jsonObject.optString("day_status");
                String date = jsonObject.optString("Dcr_dt");
                if (cusType.equalsIgnoreCase("0")
                        && date.equalsIgnoreCase(HomeDashBoard.selectedDate.toString())
                        && !dayStatus.equalsIgnoreCase("1")) {
                    isDateFound = true;
                    jsonObject.put("day_status", "1");
                    jsonArray.put(index, jsonObject);
                    break;
                }
            }
            if (!isDateFound) {
                try {
                    String FWIndicator = "", WTName = "";
                    if (mFwFlg1.equalsIgnoreCase("F")) {
                        FWIndicator = mFwFlg1;
                        WTName = mWTName1;
                    } else if (mFwFlg2.equalsIgnoreCase("F")) {
                        FWIndicator = mFwFlg2;
                        WTName = mWTName2;
                    } else {
                        FWIndicator = mFwFlg1;
                        WTName = mWTName1;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("CustCode", "");
                    jsonObject.put("CustType", "0");
                    jsonObject.put("Dcr_dt", HomeDashBoard.selectedDate.toString());
                    jsonObject.put("month_name", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_9, HomeDashBoard.selectedDate.toString()));
                    jsonObject.put("Mnth", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_31, HomeDashBoard.selectedDate.toString()));
                    jsonObject.put("Yr", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_10, HomeDashBoard.selectedDate.toString()));
                    jsonObject.put("vtm", CommonUtilsMethods.getCurrentInstance("hh:mm aa"));
                    jsonObject.put("CustName", "");
                    jsonObject.put("town_code", "");
                    jsonObject.put("FW_Indicator", FWIndicator);
                    jsonObject.put("WorkType_Name", WTName);
                    jsonObject.put("town_name", "");
                    jsonObject.put("Dcr_flag", "0");
                    jsonObject.put("SF_Code", "");
                    jsonObject.put("Trans_SlNo", "");
                    jsonObject.put("AMSLNo", "");
                    jsonObject.put("day_status", "1");
                    jsonArray.put(jsonObject);
//                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, jsonArray.toString(), 2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, jsonArray.toString(), 2));
            jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC).getMasterSyncDataJsonArray();
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                String date = jsonObject.getJSONObject("dt").getString("date").substring(0, 10);
                if (date.equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                    jsonArray.remove(index);
                    index--;
//                    break;
                }
            }
            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, jsonArray.toString(), 2));
        } catch (Exception e) {
            Log.e("WorkPlan final submit", "updateLocalData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void CallDialogCheckOut() {
        try {
            gpsTrack = new GPSTrack(requireActivity());
            latitude = gpsTrack.getLatitude();
            longitude = gpsTrack.getLongitude();
            if (UtilityClass.isNetworkAvailable(requireContext())) {
                address = CommonUtilsMethods.gettingAddress(requireActivity(), latitude, longitude, false);
            } else {
                address = getString(R.string.no_address_found);
            }
        } catch (Exception e) {
            address = getString(R.string.no_address_found);
            e.printStackTrace();
        }

        dialogCheckOut = new Dialog(requireContext());
        dialogCheckOut.setContentView(R.layout.dialog_day_check_out);
        dialogCheckOut.setCancelable(false);
        if (dialogCheckOut.getWindow() != null) {
            dialogCheckOut.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHECK_IN).getMasterSyncDataJsonArray();
        if (jsonArray.length() > 0) {
            JSONObject checkInOutJsonObject = jsonArray.optJSONObject(0);
            if (checkInOutJsonObject != null) {
                tv_address_in = dialogCheckOut.findViewById(R.id.txt_address_in);
                tv_dateTime_in = dialogCheckOut.findViewById(R.id.txt_date_time_in);
                tvLatLong_in = dialogCheckOut.findViewById(R.id.txt_lat_lng_in);
                try {
                    tv_address_in.setText(checkInOutJsonObject.optString("Start_addres"));
                    JSONObject dateObj = checkInOutJsonObject.optJSONObject("Start_Time");
                    tv_dateTime_in.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_16, dateObj.optString("date")));
                    tvLatLong_in.setText(String.format(Locale.getDefault(), "%s , %s", checkInOutJsonObject.optString("Start_Lat"), checkInOutJsonObject.optString("Start_Long")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        btnCheckOut = dialogCheckOut.findViewById(R.id.btn_close);
        tv_address = dialogCheckOut.findViewById(R.id.txt_address);
        tv_dateTime = dialogCheckOut.findViewById(R.id.txt_date_time);
        tvLatLong = dialogCheckOut.findViewById(R.id.txt_lat_lng);
        ImageView imgClose = dialogCheckOut.findViewById(R.id.img_close);
        imgClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                stopClock();
                dialogCheckOut.dismiss();
            }
        });

        ProgressBar progressBar = dialogCheckOut.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        RelativeLayout refreshLocation = dialogCheckOut.findViewById(R.id.rl_refresh_location);
        refreshLocation.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                try {
                    btnCheckOut.setEnabled(false);
                    stopClock();
                    startClock(tv_dateTime, dialogCheckOut, "Check-Out");
                    progressBar.setVisibility(View.VISIBLE);
                    gpsTrack = new GPSTrack(requireActivity());
                    gpsTrack.setLocationChangeListener(location -> {
                        try {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            if (UtilityClass.isNetworkAvailable(requireContext())) {
                                address = CommonUtilsMethods.gettingAddress(requireActivity(), latitude, longitude, false);
                            } else {
                                address = getString(R.string.no_address_found);
                            }

                            tvLatLong.setText(String.format(Locale.getDefault(), "%f , %f", latitude, longitude));
                            tv_address.setText(address);
                            btnCheckOut.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        tvHeading.setText(getResources().getString(R.string.check_out));
        tv_dateTime.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));
        startClock(tv_dateTime, dialogCheckOut, "Check-Out");
        tvLatLong.setText(String.format(Locale.getDefault(), "%f , %f", latitude, longitude));
        tv_address.setText(address);

        btnCheckOut.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                try {
                    jsonCheck = CommonUtilsMethods.CommonObjectParameter(requireContext());
                    jsonCheck.put("tableName", "saveCheckin_out");
                    jsonCheck.put("sfcode", SharedPref.getSfCode(requireContext()));
                    jsonCheck.put("division_code", SharedPref.getDivisionCode(requireContext()));
                    jsonCheck.put("lat", latitude);
                    jsonCheck.put("long", longitude);
                    jsonCheck.put("address", address);
                    jsonCheck.put("update", "1");
                    jsonCheck.put("Check_In", SharedPref.getCheckInTime(requireContext()));
                    jsonCheck.put("Check_Out", CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_1));
                    jsonCheck.put("DateTime", CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_1));
                    jsonCheck.put("Activity_Dt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                    Log.v("CheckInOut", "--json--" + jsonCheck);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            if(UtilityClass.isNetworkAvailable(requireContext())) {
//                progressDialog = CommonUtilsMethods.createProgressDialog(requireContext());
//                CallCheckOutAPI();
//            }else {
//                SharedPref.setCheckInTime(requireContext(), "");
//                SharedPref.setCheckDateTodayPlan(requireContext(), "");
//                offlineCheckInOutDataDao.saveCheckOut(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), jsonCheck.toString());
                stopClock();
                dialogCheckOut.dismiss();
                remarksAlertBox();
//            }
            }
        });

        dialogCheckOut.show();
    }

    private void startClock(TextView textView, Dialog dialog, String text) {
        handler = new Handler();
        limit = 1;
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.getDefault());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (textView != null && dialog != null && dialog.isShowing()) {
                    Calendar calendar = Calendar.getInstance();
                    String currentTime = timeFormat.format(calendar.getTime());
                    textView.setText(currentTime);
                    handler.postDelayed(this, 1000);
                    limit++;
                    if (limit == 120) {
                        stopClock();
                        handleIdleTime(requireContext(), text);
                        dialog.dismiss();
                    }
                } else {
                    stopClock();
                }
            }
        };
        handler.post(runnable);
    }

    private void stopClock() {
        if (gpsTrack != null) {
            gpsTrack.setLocationChangeListener(null);
        }
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
    }

    private void handleIdleTime(Context context, String text) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
        TextView content = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        btn_no.setVisibility(View.GONE);
        btn_yes.setText(content.getResources().getString(R.string.ok));
        content.setText(content.getResources().getString(R.string.you_have_been_idle_for_2_minutes_kindly_re) + " " + text);

        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });

        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void workPlanSubmit(String option) {
        try {
            binding.progress.setVisibility(View.VISIBLE);
            Log.e("todayCallList:Object", jsonObject.toString());

            Map<String, String> mapString = new HashMap<>();
            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                mapString.put("axn", "edetsave/dayplan");
            } else {
                mapString.put("axn", "multihqsave/dayplan");
            }
            Call<JsonElement> saveMyDayPlan = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());

            saveMyDayPlan.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.d("todayCallList:Code", response.code() + " - " + response);
                    binding.progress.setVisibility(View.GONE);
                    EditSession = "";
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                                if (DayPlanCount.equalsIgnoreCase("1")) {
                                    if (mFwFlg1.equalsIgnoreCase("F") || mFwFlg1.equalsIgnoreCase("A"))
                                        HomeDashBoard.binding.viewPager.setCurrentItem(1);
                                } else if (DayPlanCount.equalsIgnoreCase("2")) {
                                    if (mFwFlg2.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("A"))
                                        HomeDashBoard.binding.viewPager.setCurrentItem(1);
                                }
                                commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
                                CallsFragment.syncCalls();
                                updateLocalWPData();
                                if (option.equalsIgnoreCase("Edit")) {
                                    syncMyDayPlan(false);
                                }
                            } else {
                                if (json.optBoolean("update")) {
                                    Dialog deviationConfirmation = new Dialog(requireActivity());
                                    deviationConfirmation.setContentView(R.layout.popup_remarks);
                                    Objects.requireNonNull(deviationConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    deviationConfirmation.setCancelable(false);
                                    ImageView iv_close = deviationConfirmation.findViewById(R.id.img_close);
                                    EditText ed_remarks = deviationConfirmation.findViewById(R.id.ed_remark);
                                    TextView heading = deviationConfirmation.findViewById(R.id.tv_head);
                                    TextView content = deviationConfirmation.findViewById(R.id.content);
                                    Button btn_clear = deviationConfirmation.findViewById(R.id.btn_clear);
                                    Button btn_save = deviationConfirmation.findViewById(R.id.btn_save);
                                    heading.setText(R.string.alert);
                                    btn_save.setText(requireContext().getString(R.string.ok));
                                    btn_clear.setText(requireContext().getString(R.string.cancel));
                                    content.setText(json.optString("Msg"));
                                    content.setVisibility(View.VISIBLE);
                                    btn_clear.setVisibility(View.INVISIBLE);
                                    ed_remarks.setVisibility(View.INVISIBLE);
                                    iv_close.setVisibility(View.GONE);
                                    btn_save.setOnClickListener(new SafeClickListener() {
                                        @Override
                                        public void onSafeClick(View view) {
                                            insMode = "1";
                                            deviation = "1";
                                            saveOrUpdateWorkPlan(false);
                                            deviationConfirmation.dismiss();
                                        }
                                    });
//                                    btn_clear.setOnClickListener(view -> {
//                                        disableSession1();
//                                        if(DayPlanCount.equalsIgnoreCase("2")) {
//                                            disableSession2();
//                                        }
//                                        enableSave();
//                                        setUpWorkPlan();
//                                        deviationConfirmation.dismiss();
//                                    });
//                                    iv_close.setOnClickListener(view -> deviationConfirmation.dismiss());
                                    deviationConfirmation.show();
                                } else {
                                    setUpWorkPlan();
                                    commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Workplan", "onResponse: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    binding.progress.setVisibility(View.GONE);
                    EditSession = "";
                    setUpWorkPlan();
                    Log.e("VALUES", String.valueOf(t));
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            binding.progress.setVisibility(View.GONE);
            EditSession = "";
        }
    }

    public void workPlanEditSubmit() {
        try {
            binding.progress.setVisibility(View.VISIBLE);
            jsonObject.put("tableName", "dayplan_edit");
            Log.e("todayCallList:Object", jsonObject.toString());
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "edit/dayplan");
            Call<JsonElement> saveMyDayPlan = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
            saveMyDayPlan.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.d("todayCallList:Code", response.code() + " - " + response);
                    binding.progress.setVisibility(View.GONE);
                    EditSession = "";
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                                if (DayPlanCount.equalsIgnoreCase("1")) {
                                    if (mFwFlg1.equalsIgnoreCase("F") || mFwFlg1.equalsIgnoreCase("A"))
                                        HomeDashBoard.binding.viewPager.setCurrentItem(1);
                                } else if (DayPlanCount.equalsIgnoreCase("2")) {
                                    if (mFwFlg2.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("A"))
                                        HomeDashBoard.binding.viewPager.setCurrentItem(1);
                                }
                                commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
                                CallsFragment.syncCalls();
                                updateLocalWPData();
//                                if (option.equalsIgnoreCase("Edit")) {
//                                    syncMyDayPlan(false);
//                                }
                            } else {
//                                if (json.optBoolean("update")) {
//                                    Dialog deviationConfirmation = new Dialog(requireActivity());
//                                    deviationConfirmation.setContentView(R.layout.popup_remarks);
//                                    Objects.requireNonNull(deviationConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                    deviationConfirmation.setCancelable(false);
//                                    ImageView iv_close = deviationConfirmation.findViewById(R.id.img_close);
//                                    EditText ed_remarks = deviationConfirmation.findViewById(R.id.ed_remark);
//                                    TextView heading = deviationConfirmation.findViewById(R.id.tv_head);
//                                    TextView content = deviationConfirmation.findViewById(R.id.content);
//                                    Button btn_clear = deviationConfirmation.findViewById(R.id.btn_clear);
//                                    Button btn_save = deviationConfirmation.findViewById(R.id.btn_save);
//                                    heading.setText(R.string.alert);
//                                    btn_save.setText(requireContext().getString(R.string.ok));
//                                    btn_clear.setText(requireContext().getString(R.string.cancel));
//                                    content.setText(json.optString("Msg"));
//                                    content.setVisibility(View.VISIBLE);
//                                    btn_clear.setVisibility(View.INVISIBLE);
//                                    ed_remarks.setVisibility(View.INVISIBLE);
//                                    iv_close.setVisibility(View.GONE);
//                                    btn_save.setOnClickListener(view -> {
//                                        insMode = "1";
//                                        saveOrUpdateWorkPlan(false);
//                                        deviationConfirmation.dismiss();
//                                    });
////                                    btn_clear.setOnClickListener(view -> {
////                                        disableSession1();
////                                        if(DayPlanCount.equalsIgnoreCase("2")) {
////                                            disableSession2();
////                                        }
////                                        enableSave();
////                                        setUpWorkPlan();
////                                        deviationConfirmation.dismiss();
////                                    });
////                                    iv_close.setOnClickListener(view -> deviationConfirmation.dismiss());
//                                    deviationConfirmation.show();
//                                }else {
                                setUpWorkPlan();
                                commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
//                                }
                            }
                        } catch (Exception e) {
                            Log.e("Workplan", "onResponse: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    binding.progress.setVisibility(View.GONE);
                    EditSession = "";
                    setUpWorkPlan();
                    Log.e("VALUES", String.valueOf(t));
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            binding.progress.setVisibility(View.GONE);
            EditSession = "";
        }
    }

    private void updateLocalWPData() {
        try {
            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                if (mFwFlg1.equalsIgnoreCase("F")) {
                    SharedPref.saveHq(requireContext(), mHQName1.split(",")[0], mHQCode1.split(",")[0]);
                    SharedPref.saveMultiHQ(requireContext(), mHQName1, mHQCode1);
                } else if (mFwFlg2.equalsIgnoreCase("F")) {
                    SharedPref.saveHq(requireContext(), mHQName2.split(",")[0], mHQCode2.split(",")[0]);
                    SharedPref.saveMultiHQ(requireContext(), mHQName2, mHQCode2);
                }
//                SharedPref.saveHq(requireContext(), mHQName.split(",")[0], mHQCode.split(",")[0]);
//                SharedPref.saveMultiHQ(requireContext(), mHQName, mHQCode);
            } else {
                SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
            }

            String todayPlanClusterCode = mTowncode1;
            if (mFwFlg2.equalsIgnoreCase("F")) todayPlanClusterCode = mTowncode1 + ","+ mTowncode2;
            SharedPref.setTodayDayPlanClusterCode(requireContext(), todayPlanClusterCode);
            SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), true, mFwFlg1.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("F"));

            JSONArray WorkPlanDataList = new JSONArray();
            JSONObject FirstSeasonObject = new JSONObject();
            JSONObject SecondSeasonObject = new JSONObject();

            FirstSeasonObject.put("SFCode", SharedPref.getSfCode(requireContext()));
            JSONObject TPDtFirstSeasonObject = new JSONObject();
            TPDtFirstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
            FirstSeasonObject.put("TPDt", TPDtFirstSeasonObject);
            FirstSeasonObject.put("WT", mWTCode1);
            FirstSeasonObject.put("WTNm", mWTName1);
            FirstSeasonObject.put("FWFlg", mFwFlg1);
            FirstSeasonObject.put("SFMem", mHQCode1);
            FirstSeasonObject.put("HQNm", mHQName1);
            FirstSeasonObject.put("Pl", mTowncode1);
            FirstSeasonObject.put("PlNm", mTownname1);
            FirstSeasonObject.put("Rem", "");
            FirstSeasonObject.put("TpVwFlg", deviation.equals("1") ? "1" : "2");
            if (mFwFlg1.equalsIgnoreCase("F")) {
                FirstSeasonObject.put("TP_Doctor", tpDoctor);
                FirstSeasonObject.put("TP_Chemists",tpChemists);
                FirstSeasonObject.put("TP_cluster", tpCluster);
                FirstSeasonObject.put("TP_worktype", tpWorkType);
                FirstSeasonObject.put("Others_Code", workDayCode);
                FirstSeasonObject.put("Others_Name", workDayName);
            }
            WorkPlanDataList.put(FirstSeasonObject);
            if (DayPlanCount.equalsIgnoreCase("2") || !mWTCode2.isEmpty()) {
                SecondSeasonObject.put("SFCode", SharedPref.getSfCode(requireContext()));
                JSONObject TPDtSecondSeasonObject = new JSONObject();
                TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                SecondSeasonObject.put("TPDt", TPDtSecondSeasonObject);
                SecondSeasonObject.put("WT", mWTCode2);
                SecondSeasonObject.put("WTNm", mWTName2);
                SecondSeasonObject.put("FWFlg", mFwFlg2);
                SecondSeasonObject.put("SFMem", mHQCode2);
                SecondSeasonObject.put("HQNm", mHQName2);
                SecondSeasonObject.put("Pl", mTowncode2);
                SecondSeasonObject.put("PlNm", mTownname2);
                SecondSeasonObject.put("Rem", "");
                SecondSeasonObject.put("TpVwFlg", deviation.equals("1") ? "1" : "2");
                if (mFwFlg2.equalsIgnoreCase("F")) {
                    SecondSeasonObject.put("TP_Doctor", tpDoctor2);
                    SecondSeasonObject.put("TP_Chemists",tpChemists2);
                    SecondSeasonObject.put("TP_cluster", tpCluster2);
                    SecondSeasonObject.put("TP_worktype", tpWorkType2);
                    SecondSeasonObject.put("Others_Code", workDayCode2);
                    SecondSeasonObject.put("Others_Name", workDayName2);
                }
                WorkPlanDataList.put(SecondSeasonObject);
            }
            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, WorkPlanDataList.toString(), 2));

            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
            boolean isCallSyncAvailable = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String custType = jsonObject.getString("CustType");
                String time = jsonObject.getString("Dcr_dt");
                if (custType.equalsIgnoreCase("0") && time.equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                    isCallSyncAvailable = true;
                    break;
                }
            }
            if (!isCallSyncAvailable) {

                String FW_Indicator;
                String Workname;

                if (mFwFlg1.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("F")) {
                    FW_Indicator = "F";
                    Workname = "Field Work";
                } else {
                    FW_Indicator = mFwFlg1;
                    Workname = mWTName1;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("CustCode", "");
                jsonObject.put("CustType", "0");
                jsonObject.put("Dcr_dt", HomeDashBoard.selectedDate.toString());
                jsonObject.put("month_name", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_9, HomeDashBoard.selectedDate.toString()));
                jsonObject.put("Mnth", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_31, HomeDashBoard.selectedDate.toString()));
                jsonObject.put("Yr", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_10, HomeDashBoard.selectedDate.toString()));
                jsonObject.put("vtm", CommonUtilsMethods.getCurrentInstance("hh:mm aa"));
                jsonObject.put("FW_Indicator", FW_Indicator);
                jsonObject.put("WorkType_Name", Workname);
                jsonObject.put("CustName", "");
                jsonObject.put("town_code", "");
                jsonObject.put("town_name", "");
                jsonObject.put("Dcr_flag", "");
                jsonObject.put("SF_Code", hqCode);
                jsonObject.put("Trans_SlNo", "");
                jsonObject.put("AMSLNo", "");
                jsonObject.put("day_status", "0");
                jsonArray.put(jsonObject);
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, jsonArray.toString(), 2));
            }

            try {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC).getMasterSyncDataJsonArray();
                boolean isDateFound = false;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject dateObj = jsonArray.optJSONObject(i);
                    String date = dateObj.optJSONObject("dt").optString("date");
                    if (date.equalsIgnoreCase(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.toString()))) {
                        isDateFound = true;
                        break;
                    }
                }
                if (!isDateFound) {
                    JSONObject rootObject = new JSONObject();
                    rootObject.put("Sf_Code", SharedPref.getSfCode(requireContext()));
                    rootObject.put("flg", "0");
                    rootObject.put("tbname", "dcr");
                    rootObject.put("reason", "");
                    JSONObject dtObject = new JSONObject();
                    dtObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.toString()));
                    dtObject.put("timezone_type", 3);
                    dtObject.put("timezone", "Asia/Kolkata");
                    rootObject.put("dt", dtObject);
                    jsonArray.put(rootObject);
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, jsonArray.toString(), 2));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPref.setDayPlanStartedDate(requireContext(), HomeDashBoard.selectedDate.toString());
            if ((TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")
                    || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0)))
                    && TPDCRDeviation.equalsIgnoreCase("0")) {
                if (deviation.equalsIgnoreCase("1")) {
                    binding.llDeviation.setVisibility(View.GONE);
                    binding.switchButton.setChecked(false);
                    setUpDeviationLock();
                } else {
//                    if (!isFromTP) {
                    TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, HomeDashBoard.selectedDate.toString()));
                    if (tourPlanOfflineDataTable != null) {
                        String status = tourPlanOfflineDataTable.getTpMonthSyncedOrEmpty();
                        if (status.equalsIgnoreCase("3")) {
                            binding.llDeviation.setVisibility(View.VISIBLE);
                        } else {
                            binding.llDeviation.setVisibility(View.GONE);
                        }
                    }
//                    } else {
//                        binding.llDeviation.setVisibility(View.GONE);
//                    }
                    binding.switchButton.setChecked(deviation.equalsIgnoreCase("1"));
                }
            } else {
                binding.llDeviation.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData(String hqCode, boolean shouldShowHQProgressDialog) {
        SynqList = SharedPref.getsyn_hqcode(requireContext());
        SynqList.add(hqCode);
        SharedPref.setSyncHQ(requireContext(), SynqList);
        if (shouldShowHQProgressDialog) {
            syncProgressDialog.show();
            syncCount = 0;
        }
        if (DayPlanCount.equalsIgnoreCase("1")) {
            binding.progressHq1.setVisibility(View.VISIBLE);
        } else {
            binding.progressHq2.setVisibility(View.VISIBLE);
        }
        this.hqCode = hqCode;

        List<MasterSyncItemModel> list = new ArrayList<>();
//        list.add(new MasterSyncItemModel("Doctor", "Doctor", "getdoctors", Constants.DOCTOR + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Chemist", "Doctor", "getchemist", Constants.CHEMIST + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Stockiest", "Doctor", "getstockist", Constants.STOCKIEST + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Unlisted Doctor", "Doctor", "getunlisteddr", Constants.UNLISTED_DOCTOR + hqCode, 0, false));

        list.add(new MasterSyncItemModel("Doctor", Constants.DOCTOR_MAS, "getdoctors_master", Constants.DOCTOR_MAS + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Chemist", Constants.DOCTOR_MAS, "getchemist_master", Constants.CHEMIST_MAS + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Stockiest", Constants.DOCTOR_MAS, "getstockist_master", Constants.STOCKIEST_MAS + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Unlisted Doctor", Constants.DOCTOR_MAS, "getunlisteddr_master", Constants.UNLISTED_DOCTOR_MAS + hqCode, 0, false));

//        list.add(new MasterSyncItemModel("Hospital", 0, "Doctor", "gethospital", Constants.HOSPITAL + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("CIP", 0, "Doctor", "getcip", Constants.CIP + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Cluster", "Doctor", "getterritory", Constants.CLUSTER + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Cluster", Constants.DOCTOR_MAS, "getterritory", Constants.CLUSTER + hqCode, 0, false));

        list.add(new MasterSyncItemModel("Joint Work", Constants.SUBORDINATE, "getjointwork", Constants.JOINT_WORK + hqCode, 0, false));


        for (int i = 0; i < list.size(); i++) {
            requiredSyncCount++;
            syncMaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hqCode, shouldShowHQProgressDialog);
        }
    }

    public void syncMaster(String masterFor, String remoteTableName, String LocalTableKeyName, String hqCode, boolean shouldShowHQProgressDialog) {
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            try {
                String baseUrl = SharedPref.getBaseWebUrl(requireContext());
                String pathUrl = SharedPref.getPhpPathUrl(requireContext());
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                api_interface = RetrofitClient.getRetrofit(getActivity(), baseUrl + replacedUrl);

                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
                jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
                jsonObject.put("Rsf", hqCode);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                if (masterFor.equalsIgnoreCase(Constants.SUBORDINATE)) {
                    mapString.put("axn", "table/subordinates");
                    call = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                } else if (masterFor.equalsIgnoreCase(Constants.STANDARD_TOUR_PLAN)) {
                    jsonObject.put("sfcode", hqCode);
                    mapString.put("axn", "get/stp");
                    call = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                }

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();

                            if (response.isSuccessful()) {
                                Log.e("test WPF", "response : " + masterFor + " -- " + remoteTableName + " : " + Objects.requireNonNull(response.body()));
                                try {
                                    JsonElement jsonElement = response.body();
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                            jsonArray = new JSONArray(jsonArray1.toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                            JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                            if (!jsonObject2.has("success")) {
                                                jsonArray.put(jsonObject2);
                                                success = true;
                                            } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                                masterDataDao.saveMasterSyncStatus(LocalTableKeyName, 1);
                                            }
                                        }

                                        if (success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(LocalTableKeyName, jsonArray.toString(), 2));

                                            if (LocalTableKeyName.startsWith(Constants.CLUSTER)) {
                                                if (DayPlanCount.equalsIgnoreCase("1")) {
                                                    binding.progressHq1.setVisibility(View.GONE);
                                                } else {
                                                    binding.progressHq2.setVisibility(View.GONE);
                                                }
                                                getDatabaseHeadQuarters(hqCode);
                                            }
                                            if (LocalTableKeyName.equalsIgnoreCase(Constants.JOINT_WORK + hqCode)) {
                                                JSONObject jointWorkJsonObject = new JSONObject();
                                                jointWorkJsonObject.put("Code", SharedPref.getSfCode(requireContext()));
                                                jointWorkJsonObject.put("Name", Constants.INDEPENDENT);
                                                jointWorkJsonObject.put("SfName", Constants.INDEPENDENT);
                                                jointWorkJsonObject.put("Reporting_To_SF", "");
                                                jointWorkJsonObject.put("OwnDiv", "");
                                                jointWorkJsonObject.put("Division_Code", SharedPref.getDivisionCode(requireContext()));
                                                jointWorkJsonObject.put("SF_Status", "");
                                                jointWorkJsonObject.put("ActFlg", "");
                                                jointWorkJsonObject.put("UsrDfd_UserName", "");
                                                jointWorkJsonObject.put("DS_name", "");
                                                jointWorkJsonObject.put("sf_type", SharedPref.getSfType(requireContext()));
                                                jointWorkJsonObject.put("Desig", SharedPref.getDesig(requireContext()));
                                                jointWorkJsonObject.put("steps", "");

                                                JSONArray jointWorkJsonArray = new JSONArray();
                                                jointWorkJsonArray.put(jointWorkJsonObject);
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    jointWorkJsonObject = jsonArray.optJSONObject(i);
                                                    jointWorkJsonArray.put(jointWorkJsonObject);
                                                }
                                                masterDataDao.saveMasterSyncData(new MasterDataTable(LocalTableKeyName, jointWorkJsonArray.toString(), 2));
                                            }
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (shouldShowHQProgressDialog) {
                                syncCount++;
                                if (syncCount == requiredSyncCount) {
                                    syncCount = 0;
                                    requiredSyncCount = 0;
                                    setUpWorkPlan();
                                    syncProgressDialog.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            if (shouldShowHQProgressDialog) {
                                syncCount++;
                                if (syncCount == requiredSyncCount) {
                                    syncCount = 0;
                                    requiredSyncCount = 0;
                                    setUpWorkPlan();
                                    syncProgressDialog.dismiss();
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                if (shouldShowHQProgressDialog) {
                    syncCount++;
                    if (syncCount == requiredSyncCount) {
                        syncCount = 0;
                        requiredSyncCount = 0;
                        setUpWorkPlan();
                        syncProgressDialog.dismiss();
                    }
                }
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
            syncCount = 0;
            requiredSyncCount = 0;
            syncProgressDialog.dismiss();
            if (DayPlanCount.equalsIgnoreCase("1")) {
                binding.progressHq1.setVisibility(View.GONE);
            } else {
                binding.progressHq2.setVisibility(View.GONE);
            }
        }
    }

    private void getDatabaseHeadQuarters(String hqCode) {
        cluster.clear();
        multiple_cluster_list.clear();
        try {
            JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + hqCode).getMasterSyncDataJsonArray();
            for (int i = 0; i < workTypeArray.length(); i++) {
                JSONObject jsonObject = workTypeArray.getJSONObject(i);
                multiple_cluster_list.add(new Multicheckclass_clust(jsonObject.getString("Code"), jsonObject.getString("Name"), "", false));

                cluster.add(jsonObject);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    private void prepareMultiHQClusters() {
        multiHQExpandItems.clear();
        mapSelectedCluster = new HashMap<>();
        try {
            String[] HQNames = mHQName1.split(",");
            String[] HQCodes = mHQCode1.split(",");
            String[] clusterSplit = mTowncode1.split("\\$");
            if (EditSession.equals("1")) {
                HQNames = mHQName1.split(",");
                HQCodes = mHQCode1.split(",");
                clusterSplit = mTowncode1.split("\\$");
            } else if (EditSession.equals("2")) {
                HQNames = mHQName2.split(",");
                HQCodes = mHQCode2.split(",");
                clusterSplit = mTowncode2.split("\\$");
            } else if (DayPlanCount.equals("1")) {
                HQNames = mHQName1.split(",");
                HQCodes = mHQCode1.split(",");
                clusterSplit = mTowncode1.split("\\$");
            } else if (DayPlanCount.equals("2")) {
                HQNames = mHQName2.split(",");
                HQCodes = mHQCode2.split(",");
                clusterSplit = mTowncode2.split("\\$");
            }
//            else {
//                clusterSplit = new String[0];
//            }
            ArrayList<String> clusters = new ArrayList<>();
            for (String cluster : clusterSplit) {
                String[] selectedClusters = cluster.split(",");
                clusters.addAll(Arrays.asList(selectedClusters));
            }
            for (int index = 0; index < HQCodes.length; index++) {
                ArrayList<MultiHQClusterItem> clusterList = new ArrayList<>();
                String HQName = HQNames[index], HQCode = HQCodes[index];
                JSONArray clusterArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER + HQCode).getMasterSyncDataJsonArray();
                for (int i = 0; i < clusterArray.length(); i++) {
                    JSONObject jsonObject = clusterArray.getJSONObject(i);
                    if (clusters.contains(jsonObject.optString("Code"))) {
                        MultiHQClusterItem multiHQClusterItem = new MultiHQClusterItem(jsonObject.getString("Name"), jsonObject.getString("Code"), HQCode, true);
                        clusterList.add(multiHQClusterItem);
                        try {
                            ArrayList<MultiHQClusterItem> multiHQClusterItems = new ArrayList<>();
                            if (mapSelectedCluster.containsKey(HQCode)) {
                                multiHQClusterItems = mapSelectedCluster.get(HQCode);
                                if (multiHQClusterItems != null && !multiHQClusterItems.contains(multiHQClusterItem)) {
                                    multiHQClusterItems.add(multiHQClusterItem);
                                }
                            } else {
                                multiHQClusterItems.add(multiHQClusterItem);
                            }
                            mapSelectedCluster.put(HQCode, multiHQClusterItems);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        clusterList.add(new MultiHQClusterItem(jsonObject.getString("Name"), jsonObject.getString("Code"), HQCode, false));
                    }
                }
                multiHQExpandItems.add(new MultiHQExpandItem(HQName, HQCode, clusterList, true));
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    public void setUpWorkPlan() {
        try {
            binding.progressWt1.setVisibility(View.VISIBLE);
            binding.txtSave.setText(getString(R.string.save));
            binding.txtAddPlan.setText(getString(R.string.add_plan));
            JSONArray workPlanData = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();
            JSONArray workTypeData = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
            JSONArray dateSync = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC).getMasterSyncDataJsonArray();
            JSONArray tpDataArray = new JSONArray();
            if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")
                    || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0))) {
                if (HomeDashBoard.selectedDate != null) {
                    try {
                        String monthYear = CommonUtilsMethods.setConvertDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, HomeDashBoard.selectedDate.toString());
                        TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(monthYear);
                        tpDataArray = tourPlanOfflineDataTable.getTpDataJSONArray();
                        tpApprovalStatus = tourPlanOfflineDataTable.getTpMonthSyncedOrEmpty();
                        String date = CommonUtilsMethods.setConvertDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_38, HomeDashBoard.selectedDate.toString());
                        if (tpDataArray.length() > 0 && tpApprovalStatus.equalsIgnoreCase("3")) {
                            for (int i = 0; i < tpDataArray.length(); i++) {
                                tpDataObj = tpDataArray.optJSONObject(i);
                                if (tpDataObj.optString("date").equalsIgnoreCase(date)) {
                                    break;
                                } else {
                                    tpDataObj = null;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        tpDataObj = null;
                    }
                }
            }
            SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), false, false);
            rejectedReason = "";
            String dateType = "";
            if (dateSync.length() > 0 && HomeDashBoard.selectedDate != null) {
                for (int i = 0; i < dateSync.length(); i++) {
                    JSONObject jsonObject = dateSync.getJSONObject(i);
                    String dateString = jsonObject.getJSONObject("dt").getString("date").substring(0, 10);
                    LocalDate date = LocalDate.parse(dateString);
                    LocalDate currentDate = HomeDashBoard.selectedDate;
                    if (date.isEqual(currentDate)) {
                        String flag = jsonObject.optString("flg");
                        String tbName = jsonObject.optString("tbname");
                        if (tbName.equalsIgnoreCase("missed")) {
                            dateType = "Missed";
                        } else if (tbName.equalsIgnoreCase("dcr") && flag.equalsIgnoreCase("2")) {
                            dateType = "Rejected";
                        } else if (tbName.equalsIgnoreCase("dcr") && flag.equalsIgnoreCase("3")) {
                            dateType = "Re-Entry";
                        } else if (flag.equalsIgnoreCase("0")) {
                            dateType = "Planning";
                        }
                        rejectedReason = jsonObject.optString("reason");
                        break;
                    }
                }
            }

            deviation = "0";
            if (!deviationRejectedReason.isEmpty()) {
                rejectedReason = deviationRejectedReason;
                deviation = "1";
            }

            if (!rejectedReason.isEmpty()) {
                binding.rlRejReason.setVisibility(View.VISIBLE);
                binding.rejectedReason.setText(rejectedReason);
                binding.rejectedReason.setVisibility(View.VISIBLE);
            } else {
                binding.rlRejReason.setVisibility(View.GONE);
                binding.rejectedReason.setText("");
                binding.rejectedReason.setVisibility(View.GONE);
            }

            mTowncode1 = "";
            mTownname1 = "";
            mWTCode1 = "";
            mWTName1 = "";
            mFwFlg1 = "";
            mHQCode1 = "";
            mHQName1 = "";
            mRemarks1 = "";
            mTowncode2 = "";
            mTownname2 = "";
            mWTCode2 = "";
            mWTName2 = "";
            mFwFlg2 = "";
            mHQCode2 = "";
            mHQName2 = "";
            mHQCode = "";
            mTowncode = "";
            mTownname = "";
            mWTCode = "";
            mWTName = "";
            mFwFlg = "";
            mHQName = "";
            mFinalRemarks = "";
            deviation = "";
            tpWorkType = "";
            tpCluster = "";
            tpDoctor = "";
            tpChemists="";
            workDayCode = "";
            workDayName = "";
            tpWorkType2 = "";
            tpCluster2 = "";
            tpDoctor2 = "";
            tpChemists2="";
            workDayCode2 = "";
            workDayName2 = "";
            isFromTP = false;
            DayPlanCount = "1";
            if (workPlanData.length() > 0) {
                if (HomeDashBoard.selectedDate != null) {
                    SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                    if (workPlanData.length() == 2 && !workPlanData.getJSONObject(1).getString("FWFlg").isEmpty()) {
                        DayPlanCount = "2";
                        binding.cardPlan2.setVisibility(View.VISIBLE);
                        binding.llDelete.setVisibility(View.GONE);
                    } else {
                        binding.cardPlan2.setVisibility(View.GONE);
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    JSONObject FirstSeasonDayPlanObject = workPlanData.getJSONObject(0);
                    String DayplanDate1 = FirstSeasonDayPlanObject.getJSONObject("TPDt").getString("date");
                    String CurrentDate = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));

                    if (!HomeDashBoard.binding.textDate.getText().toString().trim().isEmpty()
                            && HomeDashBoard.binding.textDate.getText().toString().trim().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_27))) {
                        SharedPref.setIsWorkingToday(requireContext(), true);
                        SharedPref.setLastKnownDate(requireContext(), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
                    } else if (!SharedPref.getIsWorkingToday(requireContext())) {
                        SharedPref.setIsWorkingToday(requireContext(), false);
                    }
                    Date FirstPlanDate = sdf.parse(DayplanDate1);
                    Date CurentDate = sdf.parse(CurrentDate);
                    String TerritoryFlag1 = "", TerritoryFlag2 = "";

                    if (Objects.requireNonNull(FirstPlanDate).equals(CurentDate)) {
                        mTowncode1 = FirstSeasonDayPlanObject.getString("Pl");
                        mTownname1 = FirstSeasonDayPlanObject.getString("PlNm");
                        mWTCode1 = FirstSeasonDayPlanObject.getString("WT");
                        mWTName1 = FirstSeasonDayPlanObject.getString("WTNm");
                        mFwFlg1 = FirstSeasonDayPlanObject.getString("FWFlg");
                        mHQCode1 = FirstSeasonDayPlanObject.getString("SFMem");
                        mHQName1 = FirstSeasonDayPlanObject.getString("HQNm");
                        mRemarks1 = FirstSeasonDayPlanObject.getString("Rem");
                        chk_cluster = FirstSeasonDayPlanObject.getString("Pl");
                        SharedPref.setDayPlanStartedDate(requireContext(), HomeDashBoard.selectedDate.toString());
                        DayPlanCount = "1";

                        if (workTypeData.length() > 0) {
                            for (int i = 0; i < workTypeData.length(); i++) {
                                JSONObject mJsonObject = workTypeData.getJSONObject(i);
                                if (mJsonObject.getString("Code").equalsIgnoreCase(mWTCode1)) {
                                    TerritoryFlag1 = mJsonObject.getString("TerrSlFlg");
                                }
                            }
                        }

//                        if(!HQList.isEmpty()) {
                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                            if (!FirstSeasonDayPlanObject.optString("TP_Hq").isEmpty() && !SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                                mTowncode1 = FirstSeasonDayPlanObject.optString("TP_cluster");
                                mHQCode1 = FirstSeasonDayPlanObject.optString("TP_Hq");
                                if (!mHQCode1.isEmpty()) {
                                    checkAndSyncMasters(CommonUtilsMethods.removeDollar(mHQCode1), true);
                                }
                                String[] hqSplit = CommonUtilsMethods.removeDollar(mHQCode1).split(",");
                                String[] clusterHQSplit = mTowncode1.split("\\$");
                                int index = 0;
                                StringBuilder hqName = new StringBuilder();
                                StringBuilder clusterName = new StringBuilder();
                                for (String hq : hqSplit) {
                                    hqName.append(findHQName(hq));
                                    hqName.append(",");
                                    String[] clusterSplit = clusterHQSplit[index++].split(",");
                                    for (String clusterCode : clusterSplit) {
                                        clusterName.append(findClusterName(clusterCode, hq));
                                        clusterName.append(",");
                                    }
                                    clusterName.append("$");
                                }
                                mHQName1 = hqName.toString();
                                mTownname1 = clusterName.toString();
                            } else if (mHQName1.isEmpty()) {
                                if (!mHQCode1.isEmpty()) {
                                    checkAndSyncMasters(CommonUtilsMethods.removeDollar(mHQCode1), true);
                                }
                                mHQName1 = findHQName(mHQCode1);
                            }
                        } else {
//                                for (JSONObject hqJsonObject : HQList) {
//                                    if((mHQCode1).equalsIgnoreCase(hqJsonObject.getString("id"))) {
//                                        mHQName1 = hqJsonObject.getString("name");
//                                        break;
//                                    }
//                                }
                            if (!mHQCode1.isEmpty()) {
                                checkAndSyncMasters(CommonUtilsMethods.removeDollar(mHQCode1), true);
                            }
                            mHQName1 = findHQName(mHQCode1);
                        }
//                        }

                        if (TerritoryFlag1.equalsIgnoreCase("N")) {
                            binding.rlheadquates1.setVisibility(View.GONE);
                            binding.rlcluster1.setVisibility(View.GONE);
                            binding.rlworkday1.setVisibility(View.GONE);
                            binding.txtWorktype1.setText(mWTName1);
                            binding.txtCluster1.setText("");
                            binding.txtheadquaters1.setText("");
                            NeedClusterFlag1 = false;
                            SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                                SharedPref.saveHq(requireContext(), mHQName1.split(",")[0], mHQCode1.split(",")[0]);
                                SharedPref.saveMultiHQ(requireContext(), mHQName1, mHQCode1);
                            } else {
                                SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
                            }
                        } else if (TerritoryFlag1.equalsIgnoreCase("Y")) {
                            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                                binding.rlheadquates1.setVisibility(View.VISIBLE);
                                SharedPref.saveHq(requireContext(), mHQName1.split(",")[0], mHQCode1.split(",")[0]);
                                SharedPref.saveMultiHQ(requireContext(), mHQName1, mHQCode1);
                            } else {
                                binding.rlheadquates1.setVisibility(View.GONE);
                                SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
                                if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                                    binding.rlworkday1.setVisibility(View.VISIBLE);
                                }
                            }

                            NeedClusterFlag1 = true;
                            binding.rlcluster1.setVisibility(View.VISIBLE);
                            binding.txtWorktype1.setText(mWTName1);
                            binding.txtCluster1.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(mTownname1).replaceAll(",", " , ")));
                            binding.txtheadquaters1.setText(CommonUtilsMethods.removeLastComma(mHQName1).replaceAll(",", " , "));
                            SharedPref.setTodayDayPlanClusterCode(requireContext(), mTowncode1);

                        }
                        disableSession1();
                        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")
                                || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0))) {
                            tpWorkType = FirstSeasonDayPlanObject.optString("TP_worktype");
                            tpCluster = FirstSeasonDayPlanObject.optString("TP_cluster");
                            tpDoctor = FirstSeasonDayPlanObject.optString("TP_Doctor");
                            tpChemists=FirstSeasonDayPlanObject.optString("TP_Chemists");
                            deviation = FirstSeasonDayPlanObject.optString("TpVwFlg");
                            workDayCode = FirstSeasonDayPlanObject.optString("Others_Code");
                            workDayName = FirstSeasonDayPlanObject.optString("Others_Name");
                            isFromTP = FirstSeasonDayPlanObject.optBoolean("isFromTP", false);
                            if (SharedPref.getTpDcrDeviatedDate(requireContext()).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                                deviation = "1";
                            }
                            Log.i("TP", "setUpWorkPlan: " + isFromTP);
                            if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) /*||
                                    TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && SharedPref.getSfType(requireContext()).equalsIgnoreCase("2") && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0)*/) {
                                binding.rlworkday1.setVisibility(View.VISIBLE);
                                binding.txtworkday1.setText(workDayName);

                                if (TerritoryFlag1.equalsIgnoreCase("N")) {
                                    binding.rlworkday1.setVisibility(View.GONE);
                                }
                            } else {
                                binding.rlworkday1.setVisibility(View.GONE);
                            }
                            if (isFromTP) {
                                enableSave();
                                SharedPref.setDayPlanStartedDate(requireContext(), "");
                            } else {
                                if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
                                    SharedPref.setCheckTodayCheckInOut(requireContext(), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
                                }
                                SharedPref.setDayPlanStartedDate(requireContext(), HomeDashBoard.selectedDate.toString());
                            }
                            if (TPDCRDeviation.equalsIgnoreCase("0")) {
                                if (deviation.equalsIgnoreCase("1")) {
                                    binding.llDeviation.setVisibility(View.GONE);
                                    binding.switchButton.setChecked(false);
                                    isRefreshCalled = false;
                                    setUpDeviationLock();
                                } else {
//                                    if (!isFromTP) {
                                    if (UtilityClass.isNetworkAvailable(requireContext()) && !isRefreshCalled) {
                                        isRefreshCalled = true;
                                        refresh(false);
                                    }
                                    TourPlanOfflineDataTable tourPlanOfflineDataTable = tourPlanOfflineDataDao.getTpDataOfMonthOrNew(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_23, HomeDashBoard.selectedDate.toString()));
                                    if (tourPlanOfflineDataTable != null) {
                                        String status = tourPlanOfflineDataTable.getTpMonthSyncedOrEmpty();
                                        if (status.equalsIgnoreCase("3")) {
                                            binding.llDeviation.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.llDeviation.setVisibility(View.GONE);
                                        }
                                    }
//                                    } else {
//                                        binding.llDeviation.setVisibility(View.GONE);
//                                    }
                                    binding.switchButton.setChecked(deviation.equalsIgnoreCase("1"));
                                }
                            } else {
                                binding.llDeviation.setVisibility(View.GONE);
                            }
                        } else {
                            if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
                                SharedPref.setCheckTodayCheckInOut(requireContext(), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4));
                            }
                        }
                        binding.flSession2.setVisibility(View.GONE);

                        String dateOnlyString = sdf.format(FirstPlanDate);
                        String selectedDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_27, dateOnlyString);
                        HomeDashBoard.binding.textDate.setText(selectedDate);
                    } else {
                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) {
                            SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
                        }
                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 2));
                        setUpWorkPlan();
//                    HomeDashBoard.binding.textDate.setText(CommonUtilsMethods.getCurrentInstance("MMMM d, yyyy"));
//                    SharedPref.saveHq(requireContext(), "", "");
//                    SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
//                    binding.txtWorktype1.setText("");
//                    binding.txtCluster1.setText("");
//                    binding.txtheadquaters1.setText("");
                    }

                    binding.llPlan2.setBackground(null);
                    binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlworktype2.setEnabled(true);
                    binding.rlcluster2.setEnabled(true);
                    binding.rlheadquates2.setEnabled(true);
                    binding.txtWorktype2.setText("");
                    binding.txtCluster2.setText("");
                    binding.txtheadquaters2.setText("");

                    if (workPlanData.length() >= 2 && !workPlanData.getJSONObject(1).getString("WT").isEmpty()) {
                        JSONObject SecondSeasonDayPlanObject = workPlanData.getJSONObject(1);
                        String TPDt2 = SecondSeasonDayPlanObject.getString("TPDt");
                        JSONObject jsonObject12 = new JSONObject(TPDt2);
                        String dayPlan_Date2 = jsonObject12.getString("date");
                        Date SecondPlanDate = sdf.parse(dayPlan_Date2);

                        if (Objects.requireNonNull(SecondPlanDate).equals(CurentDate)) {
                            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                            binding.txtAddPlan.setEnabled(false);
                            DayPlanCount = "2";
                            mTowncode2 = SecondSeasonDayPlanObject.getString("Pl");
                            mTownname2 = SecondSeasonDayPlanObject.getString("PlNm");
                            mWTCode2 = SecondSeasonDayPlanObject.getString("WT");
                            mWTName2 = SecondSeasonDayPlanObject.getString("WTNm");
                            mFwFlg2 = SecondSeasonDayPlanObject.getString("FWFlg");
                            mHQCode2 = SecondSeasonDayPlanObject.getString("SFMem");
                            mHQName2 = SecondSeasonDayPlanObject.getString("HQNm");
                            deviation = SecondSeasonDayPlanObject.optString("TpVwFlg");
                            chk_cluster = mTowncode2;
                            SharedPref.setDayPlanStartedDate(requireContext(), TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_4, HomeDashBoard.binding.textDate.getText().toString()));
                            //   mRemarks1 = SecondSeasonDayPlanObject.getString("Rem");

                            if (SharedPref.getTpDcrDeviatedDate(requireContext()).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                                deviation = "1";
                            }
                            if (workTypeData.length() > 0) {
                                for (int i = 0; i < workTypeData.length(); i++) {
                                    JSONObject mJsonObject = workTypeData.getJSONObject(i);
                                    if (mJsonObject.getString("Code").equalsIgnoreCase(mWTCode2)) {
                                        TerritoryFlag2 = mJsonObject.getString("TerrSlFlg");
                                    }
                                }
                            }

//                            if(!HQList.isEmpty()) {
                            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
//                                    String[] hqSplit = mHQCode2.split(",");
//                                    StringBuilder hqName = new StringBuilder();
//                                    for (String hq : hqSplit) {
//                                        for (JSONObject hqJsonObject : HQList) {
//                                            if((hq).equalsIgnoreCase(hqJsonObject.getString("id"))) {
//                                                hqName.append(findHQName(hq));
//                                                hqName.append(",");
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    mHQName2 = hqName.toString();
                                if (!SecondSeasonDayPlanObject.optString("TP_Hq").isEmpty() && !SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                                    mTowncode2 = SecondSeasonDayPlanObject.optString("TP_cluster");
                                    mHQCode2 = SecondSeasonDayPlanObject.optString("TP_Hq");
                                    if (!mHQCode2.isEmpty()) {
                                        checkAndSyncMasters(CommonUtilsMethods.removeDollar(mHQCode2), true);
                                    }
                                    String[] hqSplit = CommonUtilsMethods.removeDollar(mHQCode2).split(",");
                                    String[] clusterHQSplit = mTowncode2.split("\\$");
                                    int index = 0;
                                    StringBuilder hqName = new StringBuilder();
                                    StringBuilder clusterName = new StringBuilder();
                                    for (String hq : hqSplit) {
                                        hqName.append(findHQName(hq));
                                        hqName.append(",");
                                        String[] clusterSplit = clusterHQSplit[index++].split(",");
                                        for (String clusterCode : clusterSplit) {
                                            clusterName.append(findClusterName(clusterCode, hq));
                                            clusterName.append(",");
                                        }
                                        clusterName.append("$");
                                    }
                                    mHQName2 = hqName.toString();
                                    mTownname2 = clusterName.toString();
                                } else if (mHQName2.isEmpty()) {
                                    if (!mHQCode2.isEmpty()) {
                                        checkAndSyncMasters(CommonUtilsMethods.removeDollar(mHQCode2), true);
                                    }
                                    mHQName2 = findHQName(mHQCode2);
                                }
                            } else {
//                                    for (JSONObject hqJsonObject : HQList) {
//                                        if((mHQCode2).equalsIgnoreCase(hqJsonObject.getString("id"))) {
//                                            mHQName2 = hqJsonObject.getString("name");
//                                            break;
//                                        }
//                                    }
                                if (!mHQCode2.isEmpty()) {
                                    checkAndSyncMasters(CommonUtilsMethods.removeDollar(mHQCode2), true);
                                }
                                mHQName2 = findHQName(mHQCode2);
                            }
//                            }

                            if (TerritoryFlag2.equalsIgnoreCase("N")) {
                                binding.rlheadquates2.setVisibility(View.GONE);
                                binding.rlcluster2.setVisibility(View.GONE);
                                binding.txtWorktype2.setText(mWTName2);
                                binding.txtCluster2.setText("");
                                binding.txtheadquaters2.setText("");
                                NeedClusterFlag2 = false;
                                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                                    if (!mFwFlg1.equalsIgnoreCase("F")) {
                                        SharedPref.saveHq(requireContext(), mHQName2.split(",")[0], mHQCode2.split(",")[0]);
                                        SharedPref.saveMultiHQ(requireContext(), mHQName2, mHQCode2);
                                    }
                                } else {
                                    SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
                                }
                            } else if (TerritoryFlag2.equalsIgnoreCase("Y")) {
                                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                                    binding.rlheadquates2.setVisibility(View.VISIBLE);
                                    if (mFwFlg2.equalsIgnoreCase("F")) {
                                        SharedPref.saveHq(requireContext(), mHQName2.split(",")[0], mHQCode2.split(",")[0]);
                                        SharedPref.saveMultiHQ(requireContext(), mHQName2, mHQCode2);
                                    }
                                } else {
                                    binding.rlheadquates2.setVisibility(View.GONE);
                                    SharedPref.saveHq(requireContext(), SharedPref.getSfName(requireContext()), SharedPref.getSfCode(requireContext()));
                                }
                                binding.rlcluster2.setVisibility(View.VISIBLE);
                                binding.txtWorktype2.setText(mWTName2);
                                binding.txtCluster2.setText(CommonUtilsMethods.removeDollar(CommonUtilsMethods.removeLastComma(mTownname2).replaceAll(",", " , ")));
                                binding.txtheadquaters2.setText(CommonUtilsMethods.removeLastComma(mHQName2).replaceAll(",", " , "));
                                String todayPlanClusterCode = mTowncode2;
                                if (mFwFlg1.equalsIgnoreCase("F")) todayPlanClusterCode = mTowncode1 + ","+ mTowncode2;
                                SharedPref.setTodayDayPlanClusterCode(requireContext(), todayPlanClusterCode);
                            }
                            disableSession2();
                            if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")
                                    || (STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && (!stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0))) {
//                                if (SecondSeasonDayPlanObject.has("Others_Code") && !SecondSeasonDayPlanObject.optString("Others_Code", "").isEmpty()) {
                                if (mFwFlg2.equalsIgnoreCase("F")) {
                                    tpWorkType2 = SecondSeasonDayPlanObject.optString("TP_worktype");
                                    tpCluster2 = SecondSeasonDayPlanObject.optString("TP_cluster");
                                    tpDoctor2 = SecondSeasonDayPlanObject.optString("TP_Doctor");
                                    tpChemists2 = SecondSeasonDayPlanObject.optString("TP_Chemists");
                                }
                                deviation = SecondSeasonDayPlanObject.optString("TpVwFlg");
                                workDayCode2 = SecondSeasonDayPlanObject.optString("Others_Code");
                                workDayName2 = SecondSeasonDayPlanObject.optString("Others_Name");
//                                }+
//                                isFromTP = SecondSeasonDayPlanObject.optBoolean("isFromTP", false);
                                if (SharedPref.getTpDcrDeviatedDate(requireContext()).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                                    deviation = "1";
                                }
                                Log.i("TP", "setUpWorkPlan: " + isFromTP);
                                if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                                    binding.rlworkday2.setVisibility(View.VISIBLE);
                                    binding.txtworkday2.setText(workDayName2);

                                    if (TerritoryFlag2.equalsIgnoreCase("N")) {
                                        binding.rlworkday2.setVisibility(View.GONE);
                                    }
                                } else {
                                    binding.rlworkday2.setVisibility(View.GONE);
                                }
                            }
                            if (isFromTP) {
                                enableSave();
                                SharedPref.setDayPlanStartedDate(requireContext(), "");
                            } else {
                                SharedPref.setDayPlanStartedDate(requireContext(), HomeDashBoard.selectedDate.toString());
                            }
                        } else {
                            binding.cardPlan2.setVisibility(View.GONE);
                        }

                    } else {
                        binding.cardPlan2.setVisibility(View.GONE);
                    }

                    if (mFwFlg1.equalsIgnoreCase("F") || mFwFlg2.equalsIgnoreCase("F")) {
                        SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), true, true);
                    } else {
                        SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), true, false);
                    }

                    if (!mHQCode1.isEmpty()) {
                        checkAndSyncMasters(CommonUtilsMethods.removeDollar(mHQCode1), true);
//                        if(!masterDataDao.getMasterSyncDataOfHQ(Constants.DOCTOR + mHQCode1) || !(masterDataDao.isDataAvailable(Constants.DOCTOR + mHQCode1))) {
//                            getData(mHQCode1, true);
//                        }
                    }
                    if (!mHQCode2.isEmpty()) {
                        checkAndSyncMasters(CommonUtilsMethods.removeDollar(mHQCode2), true);
//                        if(!masterDataDao.getMasterSyncDataOfHQ(Constants.DOCTOR + mHQCode2) || !(masterDataDao.isDataAvailable(Constants.DOCTOR + mHQCode2))) {
//                            getData(mHQCode2, true);
//                        }
                    }
                } else {
                    SharedPref.setTpDcrDeviatedDate(requireContext(), "");
                    SharedPref.setDayPlanStartedDate(requireContext(), "");
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 2));
                    binding.txtWorktype1.setText("");
                    binding.txtCluster1.setText("");
                    binding.txtheadquaters1.setText("");
//                    HomeDashBoard.binding.textDate.setText("");
                    binding.txtWorktype2.setText("");
                    binding.txtCluster2.setText("");
                    binding.txtheadquaters2.setText("");
                    binding.cardPlan2.setVisibility(View.GONE);
                    DayPlanCount = "1";
//                    HomeDashBoard.binding.textDate.setText("");
//                    SharedPref.setSelectedDateCal(requireContext(), "");
                    if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                        SharedPref.saveHq(requireContext(), SharedPref.getHqName(requireContext()), "");
                        SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                    } else {
                        SharedPref.saveHq(requireContext(), SharedPref.getHqName(requireContext()), SharedPref.getSfCode(requireContext()));
                        SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                    }
                    SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), false, false);

                    binding.rlworktype1.setEnabled(true);
                    binding.rlcluster1.setEnabled(true);
                    binding.rlheadquates1.setEnabled(true);
                    binding.rlworktype2.setEnabled(true);
                    binding.rlcluster2.setEnabled(true);
                    binding.rlheadquates2.setEnabled(true);
                    binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                    binding.txtAddPlan.setEnabled(false);
                    binding.txtSave.setTextColor(getResources().getColor(R.color.black));
                    binding.txtSave.setEnabled(true);
                    binding.llPlan1.setBackground(null);
                    binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.llPlan2.setBackground(null);
                    binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                    binding.cardPlan2.setVisibility(View.GONE);
                    binding.llDeviation.setVisibility(View.GONE);
                }
            } else if (!dateType.isEmpty() && !dateType.equalsIgnoreCase("Missed") && !dateType.equalsIgnoreCase("Planning") && !UtilityClass.isNetworkAvailable(requireContext()) && !HomeDashBoard.binding.textDate.getText().toString().isEmpty()) {
                Log.e("TAG", "setUpWorkPlan: skip" + HomeDashBoard.selectedDate.toString());
                Dialog dialog = new Dialog(requireContext());
                dialog.setContentView(R.layout.dcr_cancel_alert);
                dialog.setCancelable(false);
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                if (!dialog.isShowing()) {
                    dialog.show();
                }
                TextView content = dialog.findViewById(R.id.ed_alert_msg);
                content.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._6sdp));
                TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                TextView btn_no = dialog.findViewById(R.id.btn_no);
                btn_yes.setText(requireContext().getResources().getString(R.string.proceed));
                btn_yes.setAllCaps(true);
                btn_no.setText(requireContext().getResources().getString(R.string.continuee));

                SpannableStringBuilder builder = new SpannableStringBuilder();

                String nextDate = "";
                if (WorkPlanEntriesNeeded.datesNeeded.size() > 1) {
                    nextDate = " (" + TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_12, List.copyOf(WorkPlanEntriesNeeded.datesNeeded).get(1)) + ")";
                }
                String[] items = {"No network to get 'Work Plan' for " + dateType + " date(" + HomeDashBoard.binding.textDate.getText().toString() + ")",
                        "Click '" + requireContext().getResources().getString(R.string.proceed).toUpperCase() + "' to work on Next date" + nextDate,
                        "Connect to network and click '" + requireContext().getResources().getString(R.string.continuee) + "' to continue selected date"};

                for (String item : items) {
                    SpannableString spannable = new SpannableString(item + "\n");
                    spannable.setSpan(new BulletSpan(20), 0, spannable.length(), 0);
                    builder.append(spannable);
                }
                content.setText(builder);

                btn_yes.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        WorkPlanEntriesNeeded.skipDates.add(HomeDashBoard.selectedDate.toString());
                        HomeDashBoard.checkAndSetEntryDate(requireContext(), true);
                        dialog.dismiss();
                    }
                });

                btn_no.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        if (UtilityClass.isNetworkAvailable(requireContext())) {
                            syncMyDayPlan(false);
                            dialog.dismiss();
                        } else {
                            commonUtilsMethods.showToastMessage(requireContext(), requireContext().getResources().getString(R.string.no_network));
                        }
                    }
                });
            } else if (!dateType.isEmpty() && !dateType.equalsIgnoreCase("Missed") && !dateType.equalsIgnoreCase("Planning") && UtilityClass.isNetworkAvailable(requireContext()) && !HomeDashBoard.binding.textDate.getText().toString().isEmpty()) {
                syncMyDayPlan(false);
            } else if (SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0") && tpDataObj != null && HomeDashBoard.selectedDate != null) {
                SharedPref.setTpDcrDeviatedDate(requireContext(), "");
                Type type = new TypeToken<OneBuildModelClass>() {
                }.getType();
                OneBuildModelClass oneBuildModelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                StringBuilder clusterName = new StringBuilder(), clusterCode = new StringBuilder(), listedDr = new StringBuilder(),listedChm=new StringBuilder();
                String stpCode = "", stpName = "";
                if (oneBuildModelClass.getSessionList() != null && !oneBuildModelClass.getSessionList().isEmpty()) {
                    stpCode = oneBuildModelClass.getSessionList().get(0).getSTPCode();
                    stpName = oneBuildModelClass.getSessionList().get(0).getSTPName();
                    if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                        for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(0).getDoctors()) {
                            listedDr.append(subClass.getCode());
                            listedDr.append(",");
                        }
                        for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(0).getChemists()) {
                            listedChm.append(subClass.getCode());
                            listedChm.append(",");
//                            listedChm.append(subClass.getName());
//                            listedChm.append(",");
                        }
                        for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(0).getTerritories()) {
                            clusterCode.append(subClass.getCode());
                            clusterCode.append(",");
                            clusterName.append(subClass.getName());
                            clusterName.append(",");
                        }
                    } /*else {
                        for (MultiHQHeaderModelClass header : oneBuildModelClass.getSessionList().get(0).getDoctors()) {
                            for (MultiHQItemModelClass data: header.getItemsList()) {
                                listedDr.append(data.getCode());
                                listedDr.append(",");
                            }
                            listedDr.append("$");
                        }
                        for (MultiHQHeaderModelClass header : oneBuildModelClass.getSessionList().get(0).getTerritories()) {
                            for (MultiHQItemModelClass data: header.getItemsList()) {
                                clusterCode.append(data.getCode());
                                clusterCode.append(",");
                                clusterName.append(data.getName());
                                clusterName.append(",");
                            }
                            clusterCode.append("$");
                            clusterName.append("$");
                        }
                    }*/
                    JSONObject obj = new JSONObject();
                    JSONObject obj2 = new JSONObject();
                    obj.put("SFCode", SharedPref.getSfCode(requireContext()));
                    JSONObject TPDtFisrstSeasonObject = new JSONObject();
                    TPDtFisrstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                    obj.put("TPDt", TPDtFisrstSeasonObject);
                    obj.put("WT", oneBuildModelClass.getSessionList().get(0).getWorkType().getCode());
                    obj.put("WTNm", oneBuildModelClass.getSessionList().get(0).getWorkType().getName());
                    obj.put("FWFlg", oneBuildModelClass.getSessionList().get(0).getWorkType().getFWFlg());
                    obj.put("SFMem", oneBuildModelClass.getSessionList().get(0).getHeadquarters().getCode());
                    obj.put("HQNm", oneBuildModelClass.getSessionList().get(0).getHeadquarters().getName());
                    obj.put("Pl", clusterCode.toString());
                    obj.put("PlNm", clusterName.toString());
                    obj.put("Rem", "");
                    obj.put("TpVwFlg", "0");
                    obj.put("TP_Doctor", listedDr.toString());
                    obj.put("TP_Chemists",listedChm.toString());
                    obj.put("TP_cluster", clusterCode.toString());
                    obj.put("TP_worktype", oneBuildModelClass.getSessionList().get(0).getWorkType().getCode());
                    if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                        obj.put("Others_Code", stpCode);
                        obj.put("Others_Name", stpName);
                    } else {
                        obj.put("Others_Code", "");
                        obj.put("Others_Name", "");
                    }
                    obj.put("isFromTP", true);

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(obj);

                    if (oneBuildModelClass.getSessionList().size() > 1) {
                        listedDr = new StringBuilder();
                        listedChm=new StringBuilder();
                        clusterCode = new StringBuilder();
                        clusterName = new StringBuilder();
                        String stpCode2 = "", stpName2 = "";
                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                            stpCode2 = oneBuildModelClass.getSessionList().get(1).getSTPCode();
                            stpName2 = oneBuildModelClass.getSessionList().get(1).getSTPName();
                            for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(1).getDoctors()) {
                                listedDr.append(subClass.getCode());
                                listedDr.append(",");
                            }
                            for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(1).getChemists()) {
                                listedChm.append(subClass.getCode());
                                listedChm.append(",");
                            }
                            for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(1).getTerritories()) {
                                clusterCode.append(subClass.getCode());
                                clusterCode.append(",");
                                clusterName.append(subClass.getName());
                                clusterName.append(",");
                            }
                        } /*else {
                            for (MultiHQHeaderModelClass header : oneBuildModelClass.getSessionList().get(1).getDoctors()) {
                                for (MultiHQItemModelClass data: header.getItemsList()) {
                                    listedDr.append(data.getCode());
                                    listedDr.append(",");
                                }
                                listedDr.append("$");
                            }
                            for (MultiHQHeaderModelClass header : oneBuildModelClass.getSessionList().get(1).getTerritories()) {
                                for (MultiHQItemModelClass data: header.getItemsList()) {
                                    clusterCode.append(data.getCode());
                                    clusterCode.append(",");
                                    clusterName.append(data.getName());
                                    clusterName.append(",");
                                }
                                clusterCode.append("$");
                                clusterName.append("$");
                            }
                        }*/
                        obj2.put("SFCode", SharedPref.getSfCode(requireContext()));
                        JSONObject TPDtSecondSeasonObject = new JSONObject();
                        TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                        obj2.put("TPDt", TPDtSecondSeasonObject);
                        obj2.put("WT", oneBuildModelClass.getSessionList().get(1).getWorkType().getCode());
                        obj2.put("WTNm", oneBuildModelClass.getSessionList().get(1).getWorkType().getName());
                        obj2.put("FWFlg", oneBuildModelClass.getSessionList().get(1).getWorkType().getFWFlg());
                        obj2.put("SFMem", oneBuildModelClass.getSessionList().get(1).getHeadquarters().getCode());
                        obj2.put("HQNm", oneBuildModelClass.getSessionList().get(1).getHeadquarters().getName());
                        obj2.put("Pl", clusterCode.toString());
                        obj2.put("PlNm", clusterName.toString());
                        obj2.put("Rem", "");
                        obj2.put("TpVwFlg", "0");
                        obj2.put("TP_Doctor", listedDr.toString());
                        obj2.put("TP_Chemists",listedChm.toString());
                        obj2.put("TP_cluster", clusterCode.toString());
                        obj2.put("TP_worktype", oneBuildModelClass.getSessionList().get(1).getWorkType().getCode());
                        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                            obj2.put("Others_Code", stpCode2);
                            obj2.put("Others_Name", stpName2);
                        } else {
                            obj2.put("Others_Code", "");
                            obj2.put("Others_Name", "");
                        }
                        obj2.put("isFromTP", true);
                        jsonArray.put(obj2);
                    }
                    isFromTP = true;
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonArray.toString(), 2));
                    setUpWorkPlan();
                }
            } else if (tpDataObj != null && HomeDashBoard.selectedDate != null) {
                SharedPref.setTpDcrDeviatedDate(requireContext(), "");
                Type type = new TypeToken<ModelClass>() {
                }.getType();
                ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                StringBuilder clusterName = new StringBuilder(), clusterCode = new StringBuilder(), listedDr = new StringBuilder();
                if (modelClass.getSessionList() != null && !modelClass.getSessionList().isEmpty()) {
                    if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                        for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(0).getListedDr()) {
                            listedDr.append(subClass.getCode());
                            listedDr.append(",");
                        }
                        for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(0).getCluster()) {
                            clusterCode.append(subClass.getCode());
                            clusterCode.append(",");
                            clusterName.append(subClass.getName());
                            clusterName.append(",");
                        }
                    } else {
                        for (MultiHQHeaderModelClass header : modelClass.getSessionList().get(0).getListedDrs()) {
                            for (MultiHQItemModelClass data : header.getItemsList()) {
                                listedDr.append(data.getCode());
                                listedDr.append(",");
                            }
                            listedDr.append("$");
                        }
                        for (MultiHQHeaderModelClass header : modelClass.getSessionList().get(0).getClusters()) {
                            for (MultiHQItemModelClass data : header.getItemsList()) {
                                clusterCode.append(data.getCode());
                                clusterCode.append(",");
                                clusterName.append(data.getName());
                                clusterName.append(",");
                            }
                            clusterCode.append("$");
                            clusterName.append("$");
                        }
                    }
                    JSONObject obj = new JSONObject();
                    JSONObject obj2 = new JSONObject();
                    obj.put("SFCode", SharedPref.getSfCode(requireContext()));
                    JSONObject TPDtFisrstSeasonObject = new JSONObject();
                    TPDtFisrstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                    obj.put("TPDt", TPDtFisrstSeasonObject);
                    obj.put("WT", modelClass.getSessionList().get(0).getWorkType().getCode());
                    obj.put("WTNm", modelClass.getSessionList().get(0).getWorkType().getName());
                    obj.put("FWFlg", modelClass.getSessionList().get(0).getWorkType().getFWFlg());
                    obj.put("SFMem", modelClass.getSessionList().get(0).getHQ().getCode());
                    obj.put("HQNm", modelClass.getSessionList().get(0).getHQ().getName());
                    obj.put("Pl", clusterCode.toString());
                    obj.put("PlNm", clusterName.toString());
                    obj.put("Rem", "");
                    obj.put("TpVwFlg", "0");
                    obj.put("TP_Doctor", listedDr.toString());
                    obj.put("TP_cluster", clusterCode.toString());
                    obj.put("TP_worktype", modelClass.getSessionList().get(0).getWorkType().getCode());
                    if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                        obj.put("Others_Code", modelClass.getSTP_Code());
                        obj.put("Others_Name", modelClass.getSTP_Name());
                    } else {
                        obj.put("Others_Code", "");
                        obj.put("Others_Name", "");
                    }
                    obj.put("isFromTP", true);

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(obj);

                    if (modelClass.getSessionList().size() > 1) {
                        listedDr = new StringBuilder();
                        clusterCode = new StringBuilder();
                        clusterName = new StringBuilder();
                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                            for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(1).getListedDr()) {
                                listedDr.append(subClass.getCode());
                                listedDr.append(",");
                            }
                            for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(1).getCluster()) {
                                clusterCode.append(subClass.getCode());
                                clusterCode.append(",");
                                clusterName.append(subClass.getName());
                                clusterName.append(",");
                            }
                        } else {
                            for (MultiHQHeaderModelClass header : modelClass.getSessionList().get(1).getListedDrs()) {
                                for (MultiHQItemModelClass data : header.getItemsList()) {
                                    listedDr.append(data.getCode());
                                    listedDr.append(",");
                                }
                                listedDr.append("$");
                            }
                            for (MultiHQHeaderModelClass header : modelClass.getSessionList().get(1).getClusters()) {
                                for (MultiHQItemModelClass data : header.getItemsList()) {
                                    clusterCode.append(data.getCode());
                                    clusterCode.append(",");
                                    clusterName.append(data.getName());
                                    clusterName.append(",");
                                }
                                clusterCode.append("$");
                                clusterName.append("$");
                            }
                        }
                        obj2.put("SFCode", SharedPref.getSfCode(requireContext()));
                        JSONObject TPDtSecondSeasonObject = new JSONObject();
                        TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                        obj2.put("TPDt", TPDtSecondSeasonObject);
                        obj2.put("WT", modelClass.getSessionList().get(1).getWorkType().getCode());
                        obj2.put("WTNm", modelClass.getSessionList().get(1).getWorkType().getName());
                        obj2.put("FWFlg", modelClass.getSessionList().get(1).getWorkType().getFWFlg());
                        obj2.put("SFMem", modelClass.getSessionList().get(1).getHQ().getCode());
                        obj2.put("HQNm", modelClass.getSessionList().get(1).getHQ().getName());
                        obj2.put("Pl", clusterCode.toString());
                        obj2.put("PlNm", clusterName.toString());
                        obj2.put("Rem", "");
                        obj2.put("TpVwFlg", "0");
                        obj2.put("TP_Doctor", listedDr.toString());
                        obj2.put("TP_cluster", clusterCode.toString());
                        obj2.put("TP_worktype", modelClass.getSessionList().get(1).getWorkType().getCode());
                        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                            obj.put("Others_Code", modelClass.getSTP_Code());
                            obj.put("Others_Name", modelClass.getSTP_Name());
                        } else {
                            obj.put("Others_Code", "");
                            obj.put("Others_Name", "");
                        }
                        obj.put("isFromTP", true);
                        jsonArray.put(obj2);
                    }
                    isFromTP = true;
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonArray.toString(), 2));
                    setUpWorkPlan();
                }
            } else {
                SharedPref.setTpDcrDeviatedDate(requireContext(), "");
                SharedPref.setDayPlanStartedDate(requireContext(), "");
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 2));
                binding.txtWorktype1.setText("");
                binding.txtCluster1.setText("");
                binding.txtheadquaters1.setText("");
//                    HomeDashBoard.binding.textDate.setText("");
                binding.txtWorktype2.setText("");
                binding.txtCluster2.setText("");
                binding.txtheadquaters2.setText("");
                binding.cardPlan2.setVisibility(View.GONE);
                DayPlanCount = "1";
//                    HomeDashBoard.binding.textDate.setText("");
//                    SharedPref.setSelectedDateCal(requireContext(), "");
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                    SharedPref.saveHq(requireContext(), SharedPref.getHqName(requireContext()), "");
                    SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                } else {
                    SharedPref.saveHq(requireContext(), SharedPref.getHqName(requireContext()), SharedPref.getSfCode(requireContext()));
                    SharedPref.setTodayDayPlanClusterCode(requireContext(), "");
                }
                SharedPref.MydayPlanStausAndFeildWorkStatus(requireContext(), false, false);

                binding.rlworktype1.setEnabled(true);
                binding.rlcluster1.setEnabled(true);
                binding.rlheadquates1.setEnabled(true);
                binding.rlworktype2.setEnabled(true);
                binding.rlcluster2.setEnabled(true);
                binding.rlheadquates2.setEnabled(true);
                binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
                binding.txtAddPlan.setEnabled(false);
                binding.txtSave.setTextColor(getResources().getColor(R.color.black));
                binding.txtSave.setEnabled(true);
                binding.llPlan1.setBackground(null);
                binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.llPlan2.setBackground(null);
                binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.backround_text));
                binding.cardPlan2.setVisibility(View.GONE);
                binding.llDeviation.setVisibility(View.GONE);
            }

        } catch (Exception a) {
            a.printStackTrace();
        }
        if (!isFromTP && !binding.txtSave.isEnabled()) {
            previousWTCode1 = mWTCode1;
            previousWTCode2 = mWTCode2;
        }
        binding.progressWt1.setVisibility(View.GONE);
        if (!mFwFlg1.equalsIgnoreCase("F") && !mFwFlg2.equalsIgnoreCase("F")) {
            checkAndClearCalls();
        }
        prepareMultiHQClusters();
    }

    private void checkAndClearCalls() {
        try {
            if (!HomeDashBoard.binding.textDate.getText().toString().isEmpty()) {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
                JSONArray resultArray = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!(jsonObject.getString("Dcr_dt").equalsIgnoreCase(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_4, HomeDashBoard.binding.textDate.getText().toString())) && !jsonObject.optString("CustType").equalsIgnoreCase("0"))) {
                        resultArray.put(jsonArray.optJSONObject(i));
                    }
                }
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, resultArray.toString(), 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpDeviationLock() {
        if (UtilityClass.isNetworkAvailable(requireContext()) && (SharedPref.getTpdcrDeviationApprStatus(requireContext()).equalsIgnoreCase("2") || SharedPref.getTpdcrDeviationApprStatus(requireContext()).equalsIgnoreCase("3"))) {
            refresh(false);
        } else {
            if (TPDCRMGRApprNeed.equalsIgnoreCase("0") && SharedPref.getTpdcrDeviationApprStatus(requireContext()).equalsIgnoreCase("3")) {
                binding.deviationLock.setVisibility(View.VISIBLE);
                binding.rlWorkPlanMain.setVisibility(View.GONE);
            } else {
                binding.rlWorkPlanMain.setVisibility(View.VISIBLE);
                binding.deviationLock.setVisibility(View.GONE);
            }
        }
    }

    public void syncMyDayPlan(boolean isTPDeviated) {
        binding.progress.setVisibility(View.VISIBLE);
        if (HomeDashBoard.selectedDate != null && !HomeDashBoard.selectedDate.toString().isEmpty()) {
            try {
                api_interface = RetrofitClient.getRetrofit(getActivity(), SharedPref.getCallApiUrl(requireContext()));

                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
                if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                    jsonObject.put("tableName", "gettodaydcr");
                } else {
                    if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                        jsonObject.put("tableName", "gettodaydcrmultihq");
                    }
                }
                jsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
                jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
                jsonObject.put("Rsf", SharedPref.getHqCode(requireContext()));
                jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));

                Log.v("Mydayplan", "--json-- " + jsonObject);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Call<JsonElement> call = api_interface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                        boolean success = false;
                        JSONArray jsonArray = new JSONArray();
                        binding.progress.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            Log.e("test mydayplan", "response : " + Objects.requireNonNull(response.body()));
                            try {
                                JsonElement jsonElement = response.body();
                                if (!jsonElement.isJsonNull()) {
                                    if (jsonElement.isJsonArray()) {
                                        JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                        jsonArray = new JSONArray(jsonArray1.toString());
                                        success = true;
                                    } else if (jsonElement.isJsonObject()) {
                                        JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                                        JSONObject jsonObject2 = new JSONObject(jsonObject1.toString());
                                        if (!jsonObject2.has("success")) {
                                            jsonArray.put(jsonObject2);
                                            success = true;
                                        } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                            masterDataDao.saveMasterSyncStatus(Constants.WORK_PLAN, 1);
                                        }
                                    }

                                    if (success) {
                                        if (SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonArray.toString(), 2));
                                            if (jsonArray.toString().equalsIgnoreCase("[]") && isTPDeviated) {
                                                Type type = new TypeToken<OneBuildModelClass>() {
                                                }.getType();
                                                OneBuildModelClass oneBuildModelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                                                StringBuilder clusterName = new StringBuilder(), clusterCode = new StringBuilder(), listedDr = new StringBuilder();
                                                if (oneBuildModelClass.getSessionList() != null && !oneBuildModelClass.getSessionList().isEmpty()) {
                                                    for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(0).getDoctors()) {
                                                        listedDr.append(subClass.getCode());
                                                        listedDr.append(",");
                                                    }
                                                    for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(0).getTerritories()) {
                                                        clusterCode.append(subClass.getCode());
                                                        clusterCode.append(",");
                                                        clusterName.append(subClass.getName());
                                                        clusterName.append(",");
                                                    }
                                                    JSONObject obj = new JSONObject();
                                                    JSONObject obj2 = new JSONObject();
                                                    obj.put("SFCode", SharedPref.getSfCode(requireContext()));
                                                    JSONObject TPDtFisrstSeasonObject = new JSONObject();
                                                    TPDtFisrstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                                                    obj.put("TPDt", TPDtFisrstSeasonObject);
                                                    obj.put("WT", oneBuildModelClass.getSessionList().get(0).getWorkType().getCode());
                                                    obj.put("WTNm", oneBuildModelClass.getSessionList().get(0).getWorkType().getName());
                                                    obj.put("FWFlg", oneBuildModelClass.getSessionList().get(0).getWorkType().getFWFlg());
                                                    obj.put("SFMem", oneBuildModelClass.getSessionList().get(0).getHeadquarters().getCode());
                                                    obj.put("HQNm", oneBuildModelClass.getSessionList().get(0).getHeadquarters().getName());
                                                    obj.put("Pl", clusterCode.toString());
                                                    obj.put("PlNm", clusterName.toString());
                                                    obj.put("Rem", "");
                                                    obj.put("TpVwFlg", "0");
                                                    obj.put("TP_Doctor", listedDr.toString());
                                                    obj.put("TP_cluster", clusterCode.toString());
                                                    obj.put("TP_worktype", oneBuildModelClass.getSessionList().get(0).getWorkType().getCode());
                                                    if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                                                        obj.put("Others_Code", oneBuildModelClass.getSTP_Code());
                                                        obj.put("Others_Name", oneBuildModelClass.getSTP_Name());
                                                    } else {
                                                        obj.put("Others_Code", "");
                                                        obj.put("Others_Name", "");
                                                    }
                                                    obj.put("isFromTP", true);

                                                    jsonArray = new JSONArray();
                                                    jsonArray.put(obj);

                                                    if (oneBuildModelClass.getSessionList().size() > 1) {
                                                        for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(1).getDoctors()) {
                                                            listedDr.append(subClass.getCode());
                                                            listedDr.append(",");
                                                        }
                                                        for (OneBuildModelClass.SessionList.SubClass subClass : oneBuildModelClass.getSessionList().get(1).getTerritories()) {
                                                            clusterCode.append(subClass.getCode());
                                                            clusterCode.append(",");
                                                            clusterName.append(subClass.getName());
                                                            clusterName.append(",");
                                                        }
                                                        obj2.put("SFCode", SharedPref.getSfCode(requireContext()));
                                                        JSONObject TPDtSecondSeasonObject = new JSONObject();
                                                        TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                                                        obj2.put("TPDt", TPDtSecondSeasonObject);
                                                        obj2.put("WT", oneBuildModelClass.getSessionList().get(1).getWorkType().getCode());
                                                        obj2.put("WTNm", oneBuildModelClass.getSessionList().get(1).getWorkType().getName());
                                                        obj2.put("FWFlg", oneBuildModelClass.getSessionList().get(1).getWorkType().getFWFlg());
                                                        obj2.put("SFMem", oneBuildModelClass.getSessionList().get(1).getHeadquarters().getCode());
                                                        obj2.put("HQNm", oneBuildModelClass.getSessionList().get(1).getHeadquarters().getName());
                                                        obj2.put("Pl", clusterCode.toString());
                                                        obj2.put("PlNm", clusterName.toString());
                                                        obj2.put("Rem", "");
                                                        obj2.put("TpVwFlg", "0");
                                                        obj2.put("TP_Doctor", listedDr.toString());
                                                        obj2.put("TP_cluster", clusterCode.toString());
                                                        obj2.put("TP_worktype", oneBuildModelClass.getSessionList().get(1).getWorkType().getCode());
                                                        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                                                            obj.put("Others_Code", oneBuildModelClass.getSTP_Code());
                                                            obj.put("Others_Name", oneBuildModelClass.getSTP_Name());
                                                        } else {
                                                            obj.put("Others_Code", "");
                                                            obj.put("Others_Name", "");
                                                        }
                                                        obj.put("isFromTP", true);
                                                        jsonArray.put(obj2);
                                                    }
                                                    isFromTP = true;
                                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonArray.toString(), 2));
                                                }
                                            }
                                        } else {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonArray.toString(), 2));
                                            if (jsonArray.toString().equalsIgnoreCase("[]") && isTPDeviated) {
                                                Type type = new TypeToken<ModelClass>() {
                                                }.getType();
                                                ModelClass modelClass = new Gson().fromJson(String.valueOf(tpDataObj), type);
                                                StringBuilder clusterName = new StringBuilder(), clusterCode = new StringBuilder(), listedDr = new StringBuilder();
                                                if (modelClass.getSessionList() != null && !modelClass.getSessionList().isEmpty()) {
                                                    if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                                                        for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(0).getListedDr()) {
                                                            listedDr.append(subClass.getCode());
                                                            listedDr.append(",");
                                                        }
                                                        for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(0).getCluster()) {
                                                            clusterCode.append(subClass.getCode());
                                                            clusterCode.append(",");
                                                            clusterName.append(subClass.getName());
                                                            clusterName.append(",");
                                                        }
                                                    } else {
                                                        for (MultiHQHeaderModelClass header : modelClass.getSessionList().get(0).getListedDrs()) {
                                                            for (MultiHQItemModelClass data : header.getItemsList()) {
                                                                listedDr.append(data.getCode());
                                                                listedDr.append(",");
                                                            }
                                                            listedDr.append("$");
                                                        }
                                                        for (MultiHQHeaderModelClass header : modelClass.getSessionList().get(0).getClusters()) {
                                                            for (MultiHQItemModelClass data : header.getItemsList()) {
                                                                clusterCode.append(data.getCode());
                                                                clusterCode.append(",");
                                                                clusterName.append(data.getName());
                                                                clusterName.append(",");
                                                            }
                                                            clusterCode.append("$");
                                                            clusterName.append("$");
                                                        }
                                                    }
                                                    JSONObject obj = new JSONObject();
                                                    JSONObject obj2 = new JSONObject();
                                                    obj.put("SFCode", SharedPref.getSfCode(requireContext()));
                                                    JSONObject TPDtFisrstSeasonObject = new JSONObject();
                                                    TPDtFisrstSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                                                    obj.put("TPDt", TPDtFisrstSeasonObject);
                                                    obj.put("WT", modelClass.getSessionList().get(0).getWorkType().getCode());
                                                    obj.put("WTNm", modelClass.getSessionList().get(0).getWorkType().getName());
                                                    obj.put("FWFlg", modelClass.getSessionList().get(0).getWorkType().getFWFlg());
                                                    obj.put("SFMem", modelClass.getSessionList().get(0).getHQ().getCode());
                                                    obj.put("HQNm", modelClass.getSessionList().get(0).getHQ().getName());
                                                    obj.put("Pl", clusterCode.toString());
                                                    obj.put("PlNm", clusterName.toString());
                                                    obj.put("Rem", "");
                                                    obj.put("TpVwFlg", "0");
                                                    obj.put("TP_Doctor", listedDr.toString());
                                                    obj.put("TP_cluster", clusterCode.toString());
                                                    obj.put("TP_worktype", modelClass.getSessionList().get(0).getWorkType().getCode());
                                                    if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                                                        obj.put("Others_Code", modelClass.getSTP_Code());
                                                        obj.put("Others_Name", modelClass.getSTP_Name());
                                                    } else {
                                                        obj.put("Others_Code", "");
                                                        obj.put("Others_Name", "");
                                                    }
                                                    obj.put("isFromTP", true);

                                                    jsonArray = new JSONArray();
                                                    jsonArray.put(obj);

                                                    if (modelClass.getSessionList().size() > 1) {
                                                        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
                                                            for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(1).getListedDr()) {
                                                                listedDr.append(subClass.getCode());
                                                                listedDr.append(",");
                                                            }
                                                            for (ModelClass.SessionList.SubClass subClass : modelClass.getSessionList().get(1).getCluster()) {
                                                                clusterCode.append(subClass.getCode());
                                                                clusterCode.append(",");
                                                                clusterName.append(subClass.getName());
                                                                clusterName.append(",");
                                                            }
                                                        } else {
                                                            for (MultiHQHeaderModelClass header : modelClass.getSessionList().get(1).getListedDrs()) {
                                                                for (MultiHQItemModelClass data : header.getItemsList()) {
                                                                    listedDr.append(data.getCode());
                                                                    listedDr.append(",");
                                                                }
                                                                listedDr.append("$");
                                                            }
                                                            for (MultiHQHeaderModelClass header : modelClass.getSessionList().get(1).getClusters()) {
                                                                for (MultiHQItemModelClass data : header.getItemsList()) {
                                                                    clusterCode.append(data.getCode());
                                                                    clusterCode.append(",");
                                                                    clusterName.append(data.getName());
                                                                    clusterName.append(",");
                                                                }
                                                                clusterCode.append("$");
                                                                clusterName.append("$");
                                                            }
                                                        }
                                                        obj2.put("SFCode", SharedPref.getSfCode(requireContext()));
                                                        JSONObject TPDtSecondSeasonObject = new JSONObject();
                                                        TPDtSecondSeasonObject.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
                                                        obj2.put("TPDt", TPDtSecondSeasonObject);
                                                        obj2.put("WT", modelClass.getSessionList().get(1).getWorkType().getCode());
                                                        obj2.put("WTNm", modelClass.getSessionList().get(1).getWorkType().getName());
                                                        obj2.put("FWFlg", modelClass.getSessionList().get(1).getWorkType().getFWFlg());
                                                        obj2.put("SFMem", modelClass.getSessionList().get(1).getHQ().getCode());
                                                        obj2.put("HQNm", modelClass.getSessionList().get(1).getHQ().getName());
                                                        obj2.put("Pl", clusterCode.toString());
                                                        obj2.put("PlNm", clusterName.toString());
                                                        obj2.put("Rem", "");
                                                        obj2.put("TpVwFlg", "0");
                                                        obj2.put("TP_Doctor", listedDr.toString());
                                                        obj2.put("TP_cluster", clusterCode.toString());
                                                        obj2.put("TP_worktype", modelClass.getSessionList().get(1).getWorkType().getCode());
                                                        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
                                                            obj.put("Others_Code", modelClass.getSTP_Code());
                                                            obj.put("Others_Name", modelClass.getSTP_Name());
                                                        } else {
                                                            obj.put("Others_Code", "");
                                                            obj.put("Others_Name", "");
                                                        }
                                                        obj.put("isFromTP", true);
                                                        jsonArray.put(obj2);
                                                    }
                                                    isFromTP = true;
                                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, jsonArray.toString(), 2));
                                                }
                                            }
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            setUpWorkPlan();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        Log.e("Mydayplan", "onFailure: ");
                        binding.progress.setVisibility(View.GONE);
                        commonUtilsMethods.showToastMessage(requireContext(), requireContext().getString(R.string.please_sync_workplan));
                        t.printStackTrace();
                    }
                });

            } catch (Exception a) {
                binding.progress.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(requireContext(), requireContext().getString(R.string.please_sync_workplan));
                a.printStackTrace();
            }
        }
    }

    private void finalSubmit(String remark, boolean isAutoSubmit) {
        gpsTrack = new GPSTrack(requireActivity());
        latitude = gpsTrack.getLatitude();
        longitude = gpsTrack.getLongitude();
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            address = CommonUtilsMethods.gettingAddress(requireActivity(), latitude, longitude, false);
        } else {
            address = getString(R.string.no_address_found);
        }
        finalSubmitJSONObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
        try {
            finalSubmitJSONObject.put("tableName", "final_day");
            finalSubmitJSONObject.put("sfcode", SharedPref.getSfCode(requireContext()));
            finalSubmitJSONObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
            finalSubmitJSONObject.put("Rsf", SharedPref.getHqCode(requireContext()));
            finalSubmitJSONObject.put("Activity_Dt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_1, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
            finalSubmitJSONObject.put("current_Dt", CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_1));
            finalSubmitJSONObject.put("day_remarks", remark);
            finalSubmitJSONObject.put("location", latitude + ":" + longitude);
            finalSubmitJSONObject.put("address", address);

            Log.v("Final Submit", "--json-- " + finalSubmitJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (UtilityClass.isNetworkAvailable(requireContext()) && !outboxUtil.isOutBoxNonSyncDataAvailable()) {
            progressDialog = CommonUtilsMethods.createProgressDialog(requireContext());
            tpDataObj = null;
            if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0") && CheckInOutManager.isCheckInAvailable(requireContext())) {
                CallCheckOutAPI();
//                if(!SharedPref.getCheckInTime(requireContext()).isEmpty()) {
//                    CallCheckOutAPI();
//                }
            } else {
                CallFinalSubmitAPI();
            }
        } else {
            if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0") && jsonCheck != null) {
//            if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
                SharedPref.setCheckTodayCheckInOut(requireContext(), "");
                SharedPref.setDayCheckInData(requireContext(), "");
                SharedPref.setCheckInTime(requireContext(), "");
                SharedPref.setCheckDateTodayPlan(requireContext(), "");
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CHECK_IN, "[]", 2));
                offlineCheckInOutDataDao.saveCheckOut(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), jsonCheck.toString());
            }
            if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0")) {
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 0));
            }
            updateLocalData();
            if (offlineDaySubmitDao.getDaySubmit(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4))) != null) {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.already_submitted_work_plan));
            } else {
                offlineDaySubmitDao.insert(new OfflineDaySubmitDataTable(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), finalSubmitJSONObject.toString()));
            }
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.day_submit_saved_locally));
            WorkPlanEntriesNeeded.skipDates.clear();
//            SharedPref.setCheckTodayCheckInOut(requireContext(), "");
//            SharedPref.setCheckInTime(requireContext(), "");
//            SharedPref.setCheckDateTodayPlan(requireContext(), "");
            SharedPref.setLastCallDate(requireContext(), "");
            SharedPref.setSelectedDateCal(requireContext(), "");
            SharedPref.setDayPlanStartedDate(requireContext(), "");
            SharedPref.setCheckTodayCheckInOut(requireContext(), "");
            SharedPref.setDayCheckInData(requireContext(), "");
            previousWTCode1 = "";
            previousWTCode2 = "";
            SetupOutBoxAdapter(requireActivity(), requireContext());
//            if(SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")) {
//                if(!SharedPref.getCheckInTime(requireContext()).isEmpty()) {
//                    offlineCheckInOutDataDao.saveCheckOut(HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), jsonCheck.toString());
//                    CallDialogCheckOut();
//                }
//            }
            HomeDashBoard.checkAndSetEntryDate(requireContext(), true);
            SharedPref.setIsWorkingToday(requireContext(), false);
            if (finalSubmitDialog != null && finalSubmitDialog.isShowing()) {
                finalSubmitDialog.dismiss();
            }
        }
    }

    private void remarksAlertBox() {
        Dialog dialogRemarks = new Dialog(requireActivity());
        dialogRemarks.setContentView(R.layout.popup_remarks);
        if (dialogRemarks.getWindow() != null) {
            dialogRemarks.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialogRemarks.setCancelable(false);
        ImageView iv_close = dialogRemarks.findViewById(R.id.img_close);
        dialogAcknowledge(dialogRemarks);
        dialogRemarks.show();

        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogRemarks.dismiss();
            }
        });

    }

    private void dialogFinalSubmit(Dialog dialogRemarks) {
        EditText ed_remarks = dialogRemarks.findViewById(R.id.ed_remark);
        ed_remarks.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_remarks, 300)});
        TextView heading = dialogRemarks.findViewById(R.id.tv_head);
        TextView content = dialogRemarks.findViewById(R.id.content);
        Button btn_clear = dialogRemarks.findViewById(R.id.btn_clear);
        Button btn_save = dialogRemarks.findViewById(R.id.btn_save);
        btn_clear.setText(requireContext().getString(R.string.clear));
        btn_save.setText(requireContext().getString(R.string.save));
        content.setVisibility(View.GONE);
        heading.setText(requireContext().getString(R.string.remarks));
        ed_remarks.setVisibility(View.VISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                String remarks = ed_remarks.getText().toString().trim();
                if (!remarks.isEmpty() && remarks.length() > 2) {
                    dialogRemarks.dismiss();
                    remarks = remarks.replaceAll("'", "");
                    Log.e("Remarks", "remark : " + remarks);
                    finalSubmit(remarks, false);
                } else if (remarks.isEmpty()) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.please_enter_the_remarks));
                } else {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.remarks_must_contain_at_least_3_characters));
                }
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ed_remarks.setText("");
            }
        });
    }

    private void dialogAcknowledge(Dialog dialogRemarks) {
        EditText ed_remarks = dialogRemarks.findViewById(R.id.ed_remark);
        ed_remarks.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_remarks, 300)});
        TextView heading = dialogRemarks.findViewById(R.id.tv_head);
        TextView content = dialogRemarks.findViewById(R.id.content);
        Button btn_clear = dialogRemarks.findViewById(R.id.btn_clear);
        Button btn_save = dialogRemarks.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(requireContext().getString(R.string.yes));
        btn_clear.setText(requireContext().getString(R.string.no));
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogFinalSubmit(dialogRemarks);
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogRemarks.dismiss();
            }
        });
    }

    private void dialogEditOrDelete(String sessionType) {
        Dialog dialogOptionSelection = new Dialog(requireActivity());
        dialogOptionSelection.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogOptionSelection.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOptionSelection.setCancelable(false);
        ImageView iv_close = dialogOptionSelection.findViewById(R.id.img_close);
        EditText ed_remarks = dialogOptionSelection.findViewById(R.id.ed_remark);
        TextView heading = dialogOptionSelection.findViewById(R.id.tv_head);
        TextView content = dialogOptionSelection.findViewById(R.id.content);
        Button btn_clear = dialogOptionSelection.findViewById(R.id.btn_clear);
        Button btn_save = dialogOptionSelection.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(requireContext().getString(R.string.edit));
        if (DayPlanCount.equals("2")) {
            btn_clear.setText(requireContext().getString(R.string.delete));
            content.setText(String.format("%s %s", "Choose Edit/Delete session", sessionType));
        } else if (DayPlanCount.equals("1")) {
            content.setText(String.format("%s %s", "Choose Edit session", sessionType));
            btn_clear.setVisibility(View.GONE);
        }
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogEditOrDeleteConfirmation(sessionType, "Edit");
                dialogOptionSelection.dismiss();
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (isFromTP) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.save_work_plan_to_delete_session) + sessionType);
                    dialogOptionSelection.dismiss();
                } else {
                    dialogEditOrDeleteConfirmation(sessionType, "Delete");
                    dialogOptionSelection.dismiss();
                }
            }
        });
        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogOptionSelection.dismiss();
            }
        });
        dialogOptionSelection.show();
    }

    private void dialogEditOrDeleteConfirmation(String sessionType, String option) {
        Dialog dialogEditDeleteConfirmation = new Dialog(requireActivity());
        dialogEditDeleteConfirmation.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogEditDeleteConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEditDeleteConfirmation.setCancelable(false);
        ImageView iv_close = dialogEditDeleteConfirmation.findViewById(R.id.img_close);
        EditText ed_remarks = dialogEditDeleteConfirmation.findViewById(R.id.ed_remark);
        TextView heading = dialogEditDeleteConfirmation.findViewById(R.id.tv_head);
        TextView content = dialogEditDeleteConfirmation.findViewById(R.id.content);
        Button btn_clear = dialogEditDeleteConfirmation.findViewById(R.id.btn_clear);
        Button btn_save = dialogEditDeleteConfirmation.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(requireContext().getString(R.string.yes));
        btn_clear.setText(requireContext().getString(R.string.no));
        if (option.equalsIgnoreCase("Delete")
                && (
                (sessionType.equalsIgnoreCase("1") && mFwFlg1.equalsIgnoreCase("F"))
                        || (sessionType.equalsIgnoreCase("2") && mFwFlg2.equalsIgnoreCase("F"))
        )
        ) {
            content.setText(String.format("%s %s %s %s\n%s", getString(R.string.edit_delete_session), option, "session", sessionType, getString(R.string.deleting_field_work_with_calls)));
        } else {
            content.setText(String.format("%s %s %s %s", getString(R.string.edit_delete_session), option, "session", sessionType));
        }
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (option.equalsIgnoreCase("Edit")) {
                    if (sessionType.equals("1")) {
                        EditSession = "1";
                        enableEditSession1();
                        dialogEditDeleteConfirmation.dismiss();
                    } else {
                        EditSession = "2";
                        enableEditSession2();
                        dialogEditDeleteConfirmation.dismiss();
                    }
                    createDeleteJson(sessionType, "1");
                } else if (option.equalsIgnoreCase("Delete")) {
                    deleteSession(sessionType, "Delete");
                    dialogEditDeleteConfirmation.dismiss();
                }
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                EditSession = "";
                dialogEditDeleteConfirmation.dismiss();
            }
        });
        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogEditDeleteConfirmation.dismiss();
            }
        });
        dialogEditDeleteConfirmation.show();
    }

    private void dialogFWEditConfirmation(String sessionType, String workTypeName) {
        Dialog dialogEditDeleteConfirmation = new Dialog(requireActivity());
        dialogEditDeleteConfirmation.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogEditDeleteConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEditDeleteConfirmation.setCancelable(false);
        ImageView iv_close = dialogEditDeleteConfirmation.findViewById(R.id.img_close);
        EditText ed_remarks = dialogEditDeleteConfirmation.findViewById(R.id.ed_remark);
        TextView heading = dialogEditDeleteConfirmation.findViewById(R.id.tv_head);
        TextView content = dialogEditDeleteConfirmation.findViewById(R.id.content);
        Button btn_clear = dialogEditDeleteConfirmation.findViewById(R.id.btn_clear);
        Button btn_save = dialogEditDeleteConfirmation.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(requireContext().getString(R.string.yes));
        btn_clear.setText(requireContext().getString(R.string.no));
        content.setText(String.format("%s %s %s", getString(R.string.edit_delete_session), getString(R.string.edit), workTypeName));
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (sessionType.equals("1")) {
                    EditSession = "1";
                    enableEditFW1();
                    dialogEditDeleteConfirmation.dismiss();
                } else {
                    EditSession = "2";
                    enableEditFW2();
                    dialogEditDeleteConfirmation.dismiss();
                }
                createDeleteJson(sessionType, "1");
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                EditSession = "";
                dialogEditDeleteConfirmation.dismiss();
            }
        });
        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogEditDeleteConfirmation.dismiss();
            }
        });
        dialogEditDeleteConfirmation.show();
    }

    private void deleteSession(String sessionType, String option) {
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            createDeleteJson(sessionType, "0");
            callDeleteWP(sessionType, option);
        } else {
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network) + getString(R.string.no_network_delete_workplan));
        }
    }

    private void callDeleteWP(String sessionType, String option) {
        try {
            binding.progress.setVisibility(View.VISIBLE);
            Log.e("delete:Object", deleteJsonObject.toString());
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "delete/dayplan");
            Call<JsonElement> saveMyDayPlan = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, deleteJsonObject.toString());
            saveMyDayPlan.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    Log.d("delete:Code", response.code() + " - " + response);
                    binding.progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                            if (json.getString("success").equalsIgnoreCase("true")) {
                                SharedPref.setCheckDateTodayPlan(requireContext(), HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                                if (DayPlanCount.equalsIgnoreCase("2") && !option.equalsIgnoreCase("Edit")) {
                                    DayPlanCount = "1";
                                }
                                if (sessionType.equals("1") && mFwFlg1.equalsIgnoreCase("F")) {
                                    CallsFragment.CallTodayCallsAPI(requireContext(), api_interface, false);
                                } else if (sessionType.equals("2") && mFwFlg2.equalsIgnoreCase("F")) {
                                    CallsFragment.CallTodayCallsAPI(requireContext(), api_interface, false);
                                }
                            }
                            if (!option.equalsIgnoreCase("Edit")) {
//                                try {
//                                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
//                                    JSONArray newJsonArray = new JSONArray();
//                                    for (int i = 0; i<jsonArray.length(); i++) {
//                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                        String custType = jsonObject.getString("CustType");
//                                        String date = jsonObject.getString("Dcr_dt");
//                                        if(custType.equalsIgnoreCase("0") && date.equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
//                                            continue;
//                                        }else {
//                                            newJsonArray.put(jsonObject);
//                                        }
//                                    }
//                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, newJsonArray.toString(), 2));
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                                commonUtilsMethods.showToastMessage(requireContext(), json.getString("Msg"));
                                syncMyDayPlan(false);
                            } else {
                                workPlanSubmit(option);
                            }
                        } catch (Exception e) {
                            Log.e("Workplan", "onResponse: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    binding.progress.setVisibility(View.GONE);
                    setUpWorkPlan();
                    Log.e("VALUES", Arrays.toString(t.getStackTrace()));
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                }
            });
        } catch (Exception ignored) {
            binding.progress.setVisibility(View.GONE);
        }
    }

    private void createDeleteJson(String sessionType, String deletePlanFlag) {
        Log.d("Workplan delete", "createDeleteJson: " + sessionType);
        try {
            deleteJsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
            deleteJsonObject.put("tableName", "dayplan_delete");
            deleteJsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
            deleteJsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
            if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")) {
                deleteJsonObject.put("Rsf", mHQCode1);
                deleteJsonObject.put("Rsf2", mHQCode2);
            } else {
                deleteJsonObject.put("Rsf", SharedPref.getSfCode(requireContext()));
            }
            if (sessionType.equals("1")) {
                deleteJsonObject.put("town_code", mTowncode1);
                deleteJsonObject.put("Town_name", mTownname1);
                deleteJsonObject.put("WT_code", mWTCode1);
                deleteJsonObject.put("WTName", mWTName1);
                deleteJsonObject.put("FwFlg", mFwFlg1);
            } else {
                deleteJsonObject.put("town_code", mTowncode2);
                deleteJsonObject.put("Town_name", mTownname2);
                deleteJsonObject.put("WT_code", mWTCode2);
                deleteJsonObject.put("WTName", mWTName2);
                deleteJsonObject.put("FwFlg", mFwFlg2);
            }
            deleteJsonObject.put("location", gpsTrack.getLatitude() + ":" + gpsTrack.getLongitude());
            deleteJsonObject.put("address", CommonUtilsMethods.gettingAddress(getActivity(), gpsTrack.getLatitude(), gpsTrack.getLongitude(), false));
            deleteJsonObject.put("InsMode", "0");
            deleteJsonObject.put("delete_plan", deletePlanFlag);
            deleteJsonObject.put("SubmittedDate", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_37));
            deleteJsonObject.put("TPDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_15, HomeDashBoard.selectedDate.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableEditSession1() {
        binding.rlworktype1.setEnabled(true);
        binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.rlcluster1.setEnabled(true);
        binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.rlheadquates1.setEnabled(true);
        binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.rlworkday1.setEnabled(true);
        binding.rlworkday1.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.llPlan1.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.flSession1.setVisibility(View.GONE);
        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
            binding.rlcluster1.setEnabled(false);
            binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        }
        enableUpdate();
//        DayPlanCount = "1";
    }

    private void enableEditFW1() {
        binding.rlcluster1.setEnabled(true);
        binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.rlheadquates1.setEnabled(true);
        binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.llPlan1.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.flSession1.setVisibility(View.GONE);
        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
            binding.rlcluster1.setEnabled(false);
            binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        }
        enableUpdate();
//        DayPlanCount = "1";
    }

    private void enableEditSession2() {
        binding.rlworktype2.setEnabled(true);
        binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.rlcluster2.setEnabled(true);
        binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.rlheadquates2.setEnabled(true);
        binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.rlworkday2.setEnabled(true);
        binding.rlworkday2.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.llPlan2.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.flSession2.setVisibility(View.GONE);
        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
            binding.rlcluster2.setEnabled(false);
            binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        }
        enableUpdate();
//        DayPlanCount = "2";
    }

    private void enableEditFW2() {
        binding.rlcluster2.setEnabled(true);
        binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.rlheadquates2.setEnabled(true);
        binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.llPlan2.setBackground(getResources().getDrawable(R.drawable.backround_text));
        binding.flSession2.setVisibility(View.GONE);
        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && STPNeed.equalsIgnoreCase("0") && STPBasedMTP.equalsIgnoreCase("0") && STPBasedDCR.equalsIgnoreCase("0") && ((SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") && !stpOfflineDataDao.isNotApproved(status) && masterDataDao.getMasterDataTableOrNew(Constants.STANDARD_TOUR_PLAN).getMasterSyncDataJsonArray().length() > 0) || SharedPref.getSfType(requireContext()).equalsIgnoreCase("2"))) {
            binding.rlcluster2.setEnabled(false);
            binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        }
        enableUpdate();
//        DayPlanCount = "2";
    }

    private void disableSession1() {
        binding.llPlan1.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
        binding.rlcluster1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        binding.rlheadquates1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        if (mFwFlg1.equalsIgnoreCase("F")) {
            binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
        } else {
            binding.rlworktype1.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        }
        binding.rlworkday1.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
        binding.rlworktype1.setEnabled(false);
        binding.rlcluster1.setEnabled(false);
        binding.rlheadquates1.setEnabled(false);
        binding.rlworkday1.setEnabled(false);
        if (TPNeed.equalsIgnoreCase("0") && TPMandatory.equalsIgnoreCase("0") && TPBasedDCR.equalsIgnoreCase("0") && TPDCRDeviation.equalsIgnoreCase("1")) {
            binding.txtAddPlan.setEnabled(false);
            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
        } else {
            binding.txtAddPlan.setEnabled(true);
            binding.txtAddPlan.setTextColor(getResources().getColor(R.color.black));
        }
        binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
        binding.txtSave.setEnabled(false);
        binding.flSession1.setVisibility(View.VISIBLE);
        EditSession = "";
    }

    private void disableSession2() {
        binding.cardPlan2.setCardBackgroundColor(getResources().getColor(R.color.gray_45));
        binding.llPlan2.setBackground(getResources().getDrawable(R.drawable.background_button_border_black));
        binding.rlcluster2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        binding.rlheadquates2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        if (mFwFlg2.equalsIgnoreCase("F")) {
            binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
        } else {
            binding.rlworktype2.setBackground(getResources().getDrawable(R.drawable.background_card_white_plan));
        }
        binding.rlworkday2.setBackground(getResources().getDrawable(R.drawable.background_card_plan));
        binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
        binding.txtAddPlan.setEnabled(false);
        binding.rlworktype2.setEnabled(false);
        binding.rlcluster2.setEnabled(false);
        binding.rlheadquates2.setEnabled(false);
        binding.rlworkday2.setEnabled(false);
        binding.txtSave.setTextColor(getResources().getColor(R.color.gray_45));
        binding.txtSave.setEnabled(false);
        binding.llDelete.setVisibility(View.GONE);
        binding.flSession2.setVisibility(View.VISIBLE);
        EditSession = "";
    }

    private void enableUpdate() {
        binding.txtSave.setTextColor(getResources().getColor(R.color.black));
        binding.txtSave.setEnabled(true);
        binding.txtSave.setText(getString(R.string.update));
        binding.txtAddPlan.setText(getString(R.string.cancel));
        binding.txtAddPlan.setEnabled(true);
        binding.txtAddPlan.setTextColor(getResources().getColor(R.color.black));
    }

    private void enableSave() {
        binding.txtSave.setTextColor(getResources().getColor(R.color.black));
        binding.txtSave.setEnabled(true);
        binding.txtAddPlan.setTextColor(getResources().getColor(R.color.gray_45));
        binding.txtAddPlan.setEnabled(false);
    }
}
