package saneforce.sanzen.activity.activityModule;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.TOP;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.IsFromDCR;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.activityModule.adapter.ActivityAdapter;
import saneforce.sanzen.activity.activityModule.adapter.ActvityList2Adapter;
import saneforce.sanzen.activity.activityModule.model.ActivityDetailsModelClass;
import saneforce.sanzen.activity.activityModule.model.ActivityModelClass;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.masterSync.MasterSyncItemModel;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataTable;
import saneforce.sanzen.roomdatabase.ActivityTableDetails.ActivityDetailsDataDao;
import saneforce.sanzen.roomdatabase.ActivityTableDetails.ActivityDetailsDataTable;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataTable;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DynamicActivity extends AppCompatActivity {
    ActivityBinding binding;
    private static final int PICK_FROM_GALLERY = 101;
    ApiInterface apiInterface;
    ArrayList<ActivityModelClass> ActivityList = new ArrayList<>();
    ArrayList<ActivityDetailsModelClass> ActivityDetailsList = new ArrayList<>();
    ArrayList<ActivityDetailsModelClass> ActivityViewItem = new ArrayList<>();
    String[] value, valfrom, valto;
    ActvityList2Adapter adapter1;
    List<String> SynqList = new ArrayList<>();
    private ProgressDialog syncProgressDialog;
    ActivityAdapter adapter;
    GPSTrack gpsTrack;
    Typeface fontregular, fontmedium;
    Uri uri;
    int StorageFlag = 0, chosenActivityPosition = -1;
    File file1;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private ActivityDetailsDataDao activityDetailsDataDao;
    private ActivityOfflineDataDao activityOfflineDataDao;
    private ActivityUploadDataDao activityUploadDataDao;
    public static boolean isEdited = false;
    private ActivityModelClass chosenActivityModelClass;
    private String activityDate, activityTime, selectedHQ = "", activityCap;
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB
    private TextView selectedTextFileUpload;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBinding.inflate(getLayoutInflater());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        gpsTrack = new GPSTrack(this);
        selectedHQ = SharedPref.getHqCode(this);
        binding.txthqName.setText(SharedPref.getHqName(DynamicActivity.this));
        activityCap = SharedPref.getActivityCap(this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        activityDetailsDataDao = roomDB.activityDetailsDataDao();
        activityOfflineDataDao = roomDB.activityOfflineDataDao();
        activityUploadDataDao = roomDB.activityUploadDataDao();
        commonUtilsMethods = new CommonUtilsMethods(this);
        apiInterface = RetrofitClient.getRetrofit(DynamicActivity.this, SharedPref.getCallApiUrl(DynamicActivity.this));
        fontmedium = ResourcesCompat.getFont(this, R.font.satoshi_medium);
        fontregular = ResourcesCompat.getFont(this, R.font.satoshi_regular);
        binding.title.setText(SharedPref.getActivityCap(this));
       // binding.listTitle.setText(String.format("List of %s", SharedPref.getActivityCap(this)));
        binding.listTitle.setText(getString(R.string.list_of)+ " " + SharedPref.getActivityCap(this));
        binding.namechooseActivity.setText(getString(R.string.choose)+ " " + SharedPref.getActivityCap(this));
        binding.tvContent.setText(getString(R.string.select_any)+ " " + activityCap + " " + getString(R.string.on_list_to_view_content));
       // binding.namechooseActivity.setText(String.format("Choose %s", SharedPref.getActivityCap(this)));
     //   binding.tvContent.setText(String.format("Select any %s on list  to view content", SharedPref.getActivityCap(DynamicActivity.this)));
//        binding.txthqName.setText(SharedPref.getHqName(DynamicActivity.this));
        binding.btnSubmit.setEnabled(false);

        syncProgressDialog = new ProgressDialog(DynamicActivity.this);
        syncProgressDialog.setMessage(DynamicActivity.this.getString(R.string.head_quarters_syncing));
        syncProgressDialog.setCancelable(false);
        syncProgressDialog.setIndeterminate(true);

        adapter = new ActivityAdapter(DynamicActivity.this, ActivityList, (classGroup, holder, position) -> {
            if (this.chosenActivityPosition != position && this.chosenActivityPosition != -1 && validateActivityData()) {
                activityChangeAlert(classGroup, position);
            } else {
                binding.namechooseActivity.setText(classGroup.getActivityName());
                binding.llActivityDetailsView.removeAllViews();
                chosenActivityModelClass = classGroup;
                chosenActivityPosition = position;
                getActivityDetails(classGroup);
            }
        });
        binding.skRecylerview.setLayoutManager(new LinearLayoutManager(this));
        binding.skRecylerview.setAdapter(adapter);

//        getActivity(SharedPref.getHqCode(DynamicActivity.this));
        getActivity();

//        binding.backArrow.setOnClickListener(v -> {
//            Dialog dialog = new Dialog(DynamicActivity.this);
//            dialog.setContentView(R.layout.dcr_cancel_alert);
//            dialog.setCancelable(false);
//            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.show();
//            TextView btn_yes = dialog.findViewById(R.id.btn_yes);
//            TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
//            TextView btn_no = dialog.findViewById(R.id.btn_no);
//            alertText.setText("Are you sure, you want to exit ?");
//            btn_yes.setOnClickListener(view12 -> {
//                dialog.dismiss();
//                finish();
//            });
//            btn_no.setOnClickListener(view12 -> {
//                dialog.dismiss();
//            });
//        });

        binding.backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (DynamicActivity.isEdited) {
                    Dialog dialog = new Dialog(DynamicActivity.this);
                    dialog.setContentView(R.layout.dcr_cancel_alert);
                    dialog.setCancelable(false);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                    TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
                    TextView btn_no = dialog.findViewById(R.id.btn_no);
                    alertText.setText(R.string.are_you_sure_you_want_to_exit);
                    btn_yes.setOnClickListener(new SafeClickListener() {
                        @Override
                        public void onSafeClick(View view) {
                            dialog.dismiss();
                            getOnBackPressedDispatcher().onBackPressed();
                            IsFromDCR = true;
                            HomeDashBoard.isDcrFrom = true;
                            Intent intent = new Intent(DynamicActivity.this, HomeDashBoard.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    btn_no.setOnClickListener(new SafeClickListener() {
                        @Override
                        public void onSafeClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    getOnBackPressedDispatcher().onBackPressed();
                    IsFromDCR = true;
                    HomeDashBoard.isDcrFrom = true;
                    Intent intent = new Intent(DynamicActivity.this, HomeDashBoard.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
            binding.rlheadquates.setVisibility(View.VISIBLE);
        } else {
            binding.rlheadquates.setVisibility(View.GONE);
        }

        binding.rlheadquates.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                showHQ();
            }
        });

        binding.btnSubmit.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                saveActivity();
            }
        });

        binding.btnClearall.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                showClearAlert();
            }
        });
    }

    private boolean validateActivityData() {
        boolean isDataEntered = false;
        for (int i = 0; i < ActivityViewItem.size(); i++) {
            ActivityDetailsModelClass activityDetailsModelClass = ActivityViewItem.get(i);
            if (activityDetailsModelClass.getAnswerTxt() != null && !activityDetailsModelClass.getAnswerTxt().isEmpty() && !activityDetailsModelClass.getControlId().equalsIgnoreCase("0") && !activityDetailsModelClass.getControlId().equalsIgnoreCase("17")) {
                isDataEntered = true;
                break;
            }
        }
        return isDataEntered;
    }

    private void activityChangeAlert(ActivityModelClass classGroup, int position) {
        Dialog dialog = new Dialog(DynamicActivity.this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        alertText.setText(String.format("%s Want to change %s.\nYour entered %s details will be cleared", DynamicActivity.this.getString(R.string.are_you_sure), SharedPref.getActivityCap(DynamicActivity.this), SharedPref.getActivityCap(DynamicActivity.this)));
        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                binding.namechooseActivity.setText(classGroup.getActivityName());
                binding.llActivityDetailsView.removeAllViews();
                chosenActivityModelClass = classGroup;
                chosenActivityPosition = position;
                getActivityDetails(classGroup);
                dialog.dismiss();
            }
        });
        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                adapter.changeRowIndex(chosenActivityPosition);
                dialog.dismiss();
            }
        });
    }

    private void showClearAlert() {
        Dialog dialog = new Dialog(DynamicActivity.this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        alertText.setText(DynamicActivity.this.getString(R.string.are_you_sure_you_want_to_clear));
        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                clearViews();
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

    private void clearViews() {
        binding.llActivityDetailsView.removeAllViews();
        chosenActivityModelClass = ActivityList.get(chosenActivityPosition);
        getActivityDetails(ActivityList.get(chosenActivityPosition));
    }

    public void getActivity() {
        binding.progressMain.setVisibility(View.VISIBLE);
        ActivityList.clear();
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.ACTIVITY).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String activityDesignation = jsonObject.optString("Activity_Desig"), activityFor = jsonObject.optString("Activity_For");
                String dsName = SharedPref.getDsName(DynamicActivity.this);

                if (activityDesignation.contains(dsName) && activityFor.contains("0")) {
                    binding.rlNoActivity.setVisibility(View.GONE);
                    binding.llMainLayout.setVisibility(View.VISIBLE);
                    binding.rlDetailsMain.setVisibility(View.VISIBLE);
                    boolean isAvailableOffline = activityDetailsDataDao.isActivityDataAvailable(jsonObject.getString("Activity_SlNo") + "_" + selectedHQ);
                    ActivityList.add(new ActivityModelClass(jsonObject.getString("Activity_SlNo"), jsonObject.getString("Activity_Name"), jsonObject.getString("Activity_For"), jsonObject.getString("Active_Flag"), jsonObject.getString("Activity_Desig"), jsonObject.getString("Activity_Available"), isAvailableOffline));
                    adapter.notifyDataSetChanged();
                }
            }
            if (ActivityList.isEmpty()) {
                adapter.notifyDataSetChanged();
                binding.rlDetailsMain.setVisibility(View.GONE);
                binding.rlNoActivity.setVisibility(View.VISIBLE);
                binding.tvNoActivity.setText(getString(R.string.no) + " " + activityCap);
                binding.llMainLayout.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no) + " " + activityCap);
            }
            binding.progressMain.setVisibility(View.GONE);
        } catch (Exception e) {
            binding.progressMain.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void getActivityDetails(ActivityModelClass activityModelClass) {
        binding.progrlessdetail.setVisibility(View.VISIBLE);
        ActivityDetailsList.clear();
        ActivityViewItem.clear();
        if (!activityDetailsDataDao.isActivityDataAvailable(activityModelClass.getSlNo() + "_" + selectedHQ)) {
            if (UtilityClass.isNetworkAvailable(this)) {
                callActivityDetailsAPI(activityModelClass);
            } else {
                commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
                chosenActivityModelClass = null;
                chosenActivityPosition = -1;
                this.adapter.changeRowIndex(-1);
                binding.rlNoData.setVisibility(View.VISIBLE);
                binding.rlDetailsMain.setVisibility(View.GONE);
                binding.btnSubmit.setVisibility(View.GONE);
                binding.progrlessdetail.setVisibility(View.GONE);
            }
        } else {
            try {
                ActivityDetailsDataTable activityDetailsDataTable = activityDetailsDataDao.getActivityDetailsByID(activityModelClass.getSlNo() + "_" + selectedHQ);
                JSONArray jsonArray = activityDetailsDataTable.getActivityDataJSONArray();
                if (jsonArray.length() > 0) {
                    binding.rlDataLayout.setVisibility(View.VISIBLE);
                    binding.btnSubmit.setVisibility(View.VISIBLE);
                    binding.rlNoData.setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String Control_Id = jsonObject1.getString("Control_Id");
                        String Field_Name = jsonObject1.getString("Field_Name");
                        String Creation_Id = jsonObject1.getString("Creation_Id");
                        String input = jsonObject1.getString("input");
                        String madantaory = jsonObject1.getString("Mandatory");
                        String Control_Para = jsonObject1.getString("Control_Para");
                        String Group_Creation_ID = jsonObject1.getString("Group_Creation_ID");
                        ActivityDetailsList.add(new ActivityDetailsModelClass(Field_Name, Control_Id, Creation_Id, input, madantaory, Control_Para, Group_Creation_ID, activityModelClass.getSlNo()));
                        String controlParam = Control_Para.toLowerCase();
                        if (SharedPref.getSfType(DynamicActivity.this).equalsIgnoreCase("2") && selectedHQ.equalsIgnoreCase(SharedPref.getSfCode(DynamicActivity.this)) && (controlParam.contains("doctor") || controlParam.contains("dr")
                                || controlParam.contains("chemist") || controlParam.contains("chm")
                                || controlParam.contains("stockist") || controlParam.contains("stock") || controlParam.contains("stk")
                                || controlParam.contains("unlisted") || controlParam.contains("un") || controlParam.contains("unlst")
                                || controlParam.contains("cluster")
                                || controlParam.contains("joint"))) {
                            chosenActivityModelClass = null;
                            chosenActivityPosition = -1;
                            this.adapter.changeRowIndex(-1);
                            binding.rlNoData.setVisibility(View.VISIBLE);
                            binding.rlDetailsMain.setVisibility(View.GONE);
                            binding.btnSubmit.setVisibility(View.GONE);
                            binding.progrlessdetail.setVisibility(View.GONE);
                            commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.select_headquater));
                            showHQ();
                            return;
                        }
                    }
                    int jjj = 0;
                    for (int i = 0; i < ActivityDetailsList.size(); i++) {
                        switch (ActivityDetailsList.get(i).getControlId()) {
                            case "0":
                                CreateLabelView(ActivityDetailsList.get(i), i);
                                break;
                            case "1":
                                CreateNameView(ActivityDetailsList.get(i), i);
                                break;
                            case "2":
                                CreateNumberView(ActivityDetailsList.get(i), i);
                                break;
                            case "3":
                                CreateMultipleLineViewText(ActivityDetailsList.get(i), i);
                                break;
                            case "4":
                                CreateSelectionDateView(ActivityDetailsList.get(i), i);
                                break;
                            case "5":
                                CreateFromAndToDateSelectionView(ActivityDetailsList.get(i), i);
                                break;
                            case "6":
                                CreateSelectionTimeView(ActivityDetailsList.get(i), i);
                                break;
                            case "7":
                                CreateFromAndToTimeSelectionView(ActivityDetailsList.get(i), i);
                                break;
                            case "8":
                            case "12":
                                CreateSingleListSelection(ActivityDetailsList.get(i), i);
                                break;
                            case "9":
                            case "13":
                                CreateMultipleListSelection(ActivityDetailsList.get(i), i);
                                break;
                            case "10":
                                adduploadfile(ActivityDetailsList.get(i), i);
                                break;
                            case "11":
                                addedittextnumericcurrency(ActivityDetailsList.get(i), i);
                                break;
                            case "14":
                                TableList(ActivityDetailsList.get(i), i);
                                break;
                            case "15":
                                CreateSelectionDateWithTimeView(ActivityDetailsList.get(i), i);
                                break;
                            case "16":
                                CreateFromAndToDateWithTimeSelectionView(ActivityDetailsList.get(i), i);
                                break;
                            case "17":
                                CreateLocationView(ActivityDetailsList.get(i), i);
                                break;
                            case "18":
                                addeditcurrencyconvertor(ActivityDetailsList.get(i), i);
                                break;
                            case "19":
                                digitalsign(ActivityDetailsList.get(i), i);
                                break;
                        }
                        jjj++;
                        if (ActivityDetailsList.size() == jjj) {
                            binding.btnSubmit.setEnabled(true);
                        }
                    }
                }
                if (!ActivityDetailsList.isEmpty()) {
                    binding.progrlessdetail.setVisibility(View.GONE);
                    binding.rlNoData.setVisibility(View.GONE);
                    binding.rlDetailsMain.setVisibility(View.VISIBLE);
                } else {
                    binding.rlNoData.setVisibility(View.VISIBLE);
                    binding.rlDetailsMain.setVisibility(View.GONE);
                    binding.btnSubmit.setVisibility(View.GONE);
                    binding.progrlessdetail.setVisibility(View.GONE);
                    commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no) + activityCap + getString(R.string.details));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callActivityDetailsAPI(ActivityModelClass activityModelClass) {
        binding.progressMain.setVisibility(View.VISIBLE);
        try {
            JSONObject object = CommonUtilsMethods.CommonObjectParameter(DynamicActivity.this);
            object.put("tableName", "getdynactivity_details");
            object.put("sfcode", SharedPref.getSfCode(this));
            object.put("division_code", SharedPref.getDivisionCode(this));
            object.put("Rsf", selectedHQ);
            object.put("slno", activityModelClass.getSlNo());
            Log.v("JsonObject  :", object.toString());
            Map<String, String> QueryParam = new HashMap<>();
            QueryParam.put("axn", "get/activity");

            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(DynamicActivity.this), QueryParam, object.toString());
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    Log.v("Response :", "" + response);
                    binding.progressMain.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            JsonElement jsonElement = response.body();
                            if (jsonElement != null) {
                                JSONArray jsonArray1 = new JSONArray(jsonElement.getAsJsonArray().toString());
                                activityDetailsDataDao.saveActivityDetailsData(new ActivityDetailsDataTable(activityModelClass.getSlNo() + "_" + selectedHQ, jsonArray1.toString(), "0"));
                                activityModelClass.setAvailableOffline(true);
                                adapter.notifyDataSetChanged();
                                getActivityDetails(activityModelClass);
                            }
                        } catch (Exception a) {
                            Log.e("Error", "----- " + a);
                            a.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    public void getActivity(String hqcode) {
        if (UtilityClass.isNetworkAvailable(this)) {
            isEdited = false;
            binding.progressMain.setVisibility(View.VISIBLE);
            try {
                JSONObject object = commonUtilsMethods.CommonObjectParameter(DynamicActivity.this);
                object.put("tableName", "getdynactivity");
                object.put("sfcode", SharedPref.getSfCode(this));
                object.put("division_code", SharedPref.getDivisionCode(this));
                object.put("Rsf", hqcode);
                Log.v("JsonObject  :", "" + object.toString());
                Map<String, String> QuaryParam = new HashMap<>();
                QuaryParam.put("axn", "get/activity");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(DynamicActivity.this), QuaryParam, object.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        binding.progressMain.setVisibility(View.GONE);
                        ActivityList.clear();
                        Log.v("Response API :", "" + response);
                        JSONArray jsonArray = new JSONArray();
                        if (response.isSuccessful()) {
                            try {
                                JsonElement jsonElement = response.body();
                                jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String[] Activity_Desig = jsonObject.getString("Activity_Desig").split(",\\s*");
                                        String[] ActivityFor = jsonObject.getString("Activity_For").split(",");
                                        List<String> ActivityForList = Arrays.asList(ActivityFor);
                                        List<String> DegList = Arrays.asList(Activity_Desig);

                                        if (DegList.contains(SharedPref.getDsName(DynamicActivity.this)) && ActivityForList.contains("0")) {
                                            binding.rlNoActivity.setVisibility(View.GONE);
                                            binding.llMainLayout.setVisibility(View.VISIBLE);
                                            binding.rlDetailsMain.setVisibility(View.VISIBLE);
                                            ActivityList.add(new ActivityModelClass(jsonObject.getString("Activity_SlNo"), jsonObject.getString("Activity_Name"), jsonObject.getString("Activity_For"), jsonObject.getString("Active_Flag"), jsonObject.getString("Activity_Desig"), jsonObject.getString("Activity_Available"), false));
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                if (ActivityList.size() <= 0) {
                                    adapter.notifyDataSetChanged();
                                    binding.rlDetailsMain.setVisibility(View.GONE);
                                    binding.rlNoActivity.setVisibility(View.VISIBLE);
                                    binding.llMainLayout.setVisibility(View.GONE);

                                    commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no) + activityCap);
                                }
                            } catch (Exception a) {
                                a.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        binding.progressMain.setVisibility(View.GONE);
                        binding.rlNoActivity.setVisibility(View.VISIBLE);
                        binding.llMainLayout.setVisibility(View.GONE);
                    }
                });

            } catch (Exception a) {
                a.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(this, getString(R.string.no_network));
        }
    }

    public void getActivityDetails1(ActivityModelClass Data) {
        if (UtilityClass.isNetworkAvailable(this)) {
            isEdited = false;
            binding.progrlessdetail.setVisibility(View.VISIBLE);
            try {
                JSONObject object = commonUtilsMethods.CommonObjectParameter(DynamicActivity.this);
                object.put("tableName", "getdynactivity_details");
                object.put("sfcode", SharedPref.getSfCode(this));
                object.put("division_code", SharedPref.getDivisionCode(this));
                object.put("Rsf", SharedPref.getHqCode(this));
                object.put("slno", Data.getSlNo());
                Log.v("JsonObject  :", "" + object.toString());
                Map<String, String> QuaryParam = new HashMap<>();
                QuaryParam.put("axn", "get/activity");

                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(DynamicActivity.this), QuaryParam, object.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        Log.v("Response :", "" + response);
                        JSONArray jsonArray = new JSONArray();
                        if (response.isSuccessful()) {
                            ActivityDetailsList.clear();
                            ActivityViewItem.clear();
                            try {
                                JsonElement jsonElement = response.body();
                                jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                if (jsonArray.length() > 0) {
                                    binding.rlDataLayout.setVisibility(View.VISIBLE);
                                    binding.btnSubmit.setVisibility(View.VISIBLE);
                                    binding.rlNoData.setVisibility(View.GONE);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String Control_Id = jsonObject1.getString("Control_Id");
                                        String Field_Name = jsonObject1.getString("Field_Name");
                                        String Creation_Id = jsonObject1.getString("Creation_Id");
                                        String input = jsonObject1.getString("input");
                                        String madantaory = jsonObject1.getString("Mandatory");
                                        String Control_Para = jsonObject1.getString("Control_Para");
                                        String Group_Creation_ID = jsonObject1.getString("Group_Creation_ID");
                                        ActivityDetailsList.add(new ActivityDetailsModelClass(Field_Name, Control_Id, Creation_Id, input, madantaory, Control_Para, Group_Creation_ID, Data.getSlNo()));
                                    }
                                    int jjj = 0;
                                    for (int i = 0; i < ActivityDetailsList.size(); i++) {
                                        switch (ActivityDetailsList.get(i).getControlId()) {
                                            case "0":
                                                CreateLabelView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "1":
                                                CreateNameView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "2":
                                                CreateNumberView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "3":
                                                CreateMultipleLineViewText(ActivityDetailsList.get(i), i);
                                                break;
                                            case "4":
                                                CreateSelectionDateView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "5":
                                                CreateFromAndToDateSelectionView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "6":
                                                CreateSelectionTimeView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "7":
                                                CreateFromAndToTimeSelectionView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "8":
                                            case "12":
                                                CreateSingleListSelection(ActivityDetailsList.get(i), i);
                                                break;
                                            case "9":
                                            case "13":
                                                CreateMultipleListSelection(ActivityDetailsList.get(i), i);
                                                break;
                                            case "10":
                                                adduploadfile(ActivityDetailsList.get(i), i);
                                                break;
                                            case "11":
                                                addedittextnumericcurrency(ActivityDetailsList.get(i), i);
                                                break;
                                            case "14":
                                                TableList(ActivityDetailsList.get(i), i);
                                                break;
                                            case "15":
                                                CreateSelectionDateWithTimeView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "16":
                                                CreateFromAndToDateWithTimeSelectionView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "17":
                                                CreateLocationView(ActivityDetailsList.get(i), i);
                                                break;
                                            case "18":
                                                addeditcurrencyconvertor(ActivityDetailsList.get(i), i);
                                                break;
                                            case "19":
                                                digitalsign(ActivityDetailsList.get(i), i);
                                                break;
                                        }
                                        jjj++;
                                        if (ActivityDetailsList.size() == jjj) {
                                            binding.btnSubmit.setEnabled(true);
                                        }
                                    }
                                }
                                if (!ActivityDetailsList.isEmpty()) {
                                    binding.progrlessdetail.setVisibility(View.GONE);
                                    binding.rlNoData.setVisibility(View.GONE);
                                    binding.rlDetailsMain.setVisibility(View.VISIBLE);
                                } else {
                                    binding.rlNoData.setVisibility(View.VISIBLE);
                                    binding.rlDetailsMain.setVisibility(View.GONE);
                                    binding.btnSubmit.setVisibility(View.GONE);
                                    binding.progrlessdetail.setVisibility(View.GONE);
                                    commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no) + activityCap + getString(R.string.details));
                                }
                            } catch (Exception a) {
                                commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no) + activityCap +  getString(R.string.details));
                                Log.e("Error", "----- " + a);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no) + activityCap +  getString(R.string.details));
                        binding.rlNoData.setVisibility(View.VISIBLE);
                        binding.rlDetailsMain.setVisibility(View.GONE);
                        binding.btnSubmit.setVisibility(View.GONE);
                        binding.progrlessdetail.setVisibility(View.GONE);
                    }
                });

            } catch (Exception a) {
                a.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no_network));
        }
    }

    @SuppressLint("ResourceType")
    public void CreateLabelView(ActivityDetailsModelClass List, int k) {
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textlabel = new TextView(this);
        textlabel.setText(List.getFieldName().toUpperCase());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textlabel.setBackgroundResource(R.drawable.backround_lite_gray_border);
        textlabel.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textlabel.setTextColor(getResources().getColor(R.color.text_dark));
        textlabel.setGravity(CENTER);
        textlabel.setLayoutParams(params);
        textlabel.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textlabel.setTypeface(fontmedium);
        textLinearLayout.addView(textlabel);
        textlabel.setId(k);
        binding.llActivityDetailsView.addView(textLinearLayout);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), List.getFieldName(), "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));
    }

    public void CreateNameView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        TextView txtLabelName = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            txtLabelName.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            txtLabelName.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        txtLabelName.setTextColor(getResources().getColor(R.color.text_dark));
        txtLabelName.setLayoutParams(params1);
        txtLabelName.setTypeface(fontmedium);
        txtLabelName.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinearLayout1.addView(txtLabelName);

        EditText textcharacter = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textcharacter.setBackgroundColor(Color.WHITE);
        textcharacter.setBackgroundResource(R.drawable.background_card_white_plan);
        textcharacter.setTextColor(getResources().getColor(R.color.text_dark));
        textcharacter.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textcharacter.setLayoutParams(params);
        textcharacter.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textcharacter.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        textLinearLayout1.addView(textcharacter);
        textcharacter.setInputType(InputType.TYPE_CLASS_TEXT);
        textcharacter.setId(k);
        textcharacter.setHint("Enter " + List.getFieldName());
        textcharacter.setCursorVisible(true);
        textcharacter.setClickable(true);
        if (!List.getControlPara().isEmpty() && !List.getControlPara().equalsIgnoreCase("0")) {
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(Integer.parseInt(List.getControlPara()));
            textcharacter.setFilters(fArray);
        }
        textcharacter.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(textcharacter)});

        // CreateName
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textcharacter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textcharacter.getText().toString());
            }
        });

        textcharacter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(view);
                    view.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("ResourceType")
    public void CreateNumberView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewdata.setTypeface(fontmedium);
        textLinearLayout1.addView(textviewdata);
        EditText textnumber = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textnumber.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        textnumber.setBackgroundColor(Color.WHITE);
        textnumber.setBackgroundResource(R.drawable.background_card_white_plan);
        textnumber.setTextColor(getResources().getColor(R.color.text_dark));
        textnumber.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textnumber.setLayoutParams(params);
        textnumber.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textnumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        textnumber.setId(k);
        textnumber.setHint("Enter " + List.getFieldName());
        textnumber.setClickable(false);
        textnumber.setCursorVisible(true);
        if (!List.getControlPara().isEmpty() && !List.getControlPara().equalsIgnoreCase("0")) {
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(Integer.parseInt(List.getControlPara()));
            textnumber.setFilters(fArray);
        }
        textLinearLayout1.addView(textnumber);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));
        textnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textnumber.getText().toString());
            }
        });
        textnumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(view);
                    view.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    public void CreateMultipleLineViewText(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textviewdata.setTypeface(fontmedium);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewdata.setTypeface(fontmedium);
        textLinearLayout1.addView(textviewdata);
        EditText textarea = new EditText(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textarea.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textarea.setTextColor(getResources().getColor(R.color.text_dark));
        textarea.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textarea.setGravity(TOP);
        textarea.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        textarea.setLayoutParams(params);
        textLinearLayout1.addView(textarea);
        textarea.setBackgroundColor(Color.WHITE);
        textarea.setBackgroundResource(R.drawable.background_card_white_plan);
        textarea.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        textarea.setSingleLine(false);
        textarea.setMinLines(5);
        textarea.setMaxLines(8);
        textarea.setId(k);
        textarea.setHint("Enter " + List.getFieldName());
        textarea.setCursorVisible(true);
        textarea.setClickable(true);
        if (!List.getControlPara().isEmpty() && !List.getControlPara().equalsIgnoreCase("0")) {
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(Integer.parseInt(List.getControlPara()));
            textarea.setFilters(fArray);
        }
        textarea.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(textarea)});

        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textarea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textarea.getText().toString());
            }
        });
        textarea.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(view);
                    view.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    public void CreateSelectionDateView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textviewdata.setTypeface(fontmedium);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewdata.setTypeface(fontmedium);
        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);
        textLinearLayout2.setId(k);

        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        params11.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        Drawable drawable = getDrawable(R.drawable.calendar_img);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));

        TextView textviewdate1 = new TextView(this);
        textviewdate1.setCompoundDrawables(drawable, null, null, null);
        textviewdate1.setBackgroundColor(Color.WHITE);
        textviewdate1.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewdate1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdate1.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdate1.setLayoutParams(params11);
        textviewdate1.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewdate1.setGravity(CENTER);
        textviewdate1.setHint("Select Date");
        textviewdate1.setHintTextColor(getResources().getColor(R.color.text_dark));
        textLinearLayout2.addView(textviewdate1);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textviewdate1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textviewdate1.getText().toString());
            }
        });

        textviewdate1.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DynamicActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        textviewdate1.setText(String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
                        commonFun();
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void CreateSelectionDateWithTimeView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        textviewdata.setTypeface(fontmedium);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewdata.setTypeface(fontmedium);

        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);
        textLinearLayout2.setId(k);

        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textviewdate1 = new TextView(this);
        params11.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        Drawable drawable = getDrawable(R.drawable.calendar_clock);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        textviewdate1.setCompoundDrawables(drawable, null, null, null);
        textviewdate1.setBackgroundColor(Color.WHITE);
        textviewdate1.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewdate1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdate1.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdate1.setLayoutParams(params11);
        textviewdate1.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewdate1.setGravity(CENTER);
        textviewdate1.setHint("Select Date and Time");
        textviewdate1.setHintTextColor(getResources().getColor(R.color.text_dark));
        textLinearLayout2.addView(textviewdate1);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textviewdate1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textviewdate1.getText().toString());
            }
        });

        textviewdate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(DynamicActivity.this, (view, year1, monthOfYear, dayOfMonth1) -> {
                    c.set(year1, monthOfYear, dayOfMonth1);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(DynamicActivity.this, (view1, hourOfDay, minute1) -> {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute1);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                        textviewdate1.setText(dateFormat.format(c.getTime()));
                        commonFun();
                    }, hour, minute, false);
                    timePickerDialog.show();
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
    }

    public void CreateFromAndToDateSelectionView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textviewdata.setTypeface(fontmedium);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
        LinearLayout textLinearLayout3 = new LinearLayout(this);
        textLinearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout3.setLayoutParams(params1);
        LinearLayout textLinearLayout4 = new LinearLayout(this);
        textLinearLayout4.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout4.setLayoutParams(params1);

        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));

        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);
        textLinearLayout2.addView(textLinearLayout3);
        textLinearLayout2.addView(textLinearLayout4);
        textLinearLayout3.setId(k + 23);

        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textviewfromdate = new TextView(this);

        Drawable drawable = getDrawable(R.drawable.calendar_img);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        textviewfromdate.setCompoundDrawables(drawable, null, null, null);
        textviewfromdate.setHint("Select From Date");
        textviewfromdate.setBackgroundColor(Color.WHITE);
        textviewfromdate.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewfromdate.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewfromdate.setTextColor(getResources().getColor(R.color.text_dark));
        textviewfromdate.setHintTextColor(getResources().getColor(R.color.text_dark));
        textviewfromdate.setLayoutParams(params11);
        textviewfromdate.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewfromdate.setGravity(CENTER);
        textLinearLayout3.addView(textviewfromdate);

        textviewfromdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textviewfromdate.getText().toString());
            }
        });

        textviewfromdate.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DynamicActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        textviewfromdate.setText(String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
                        commonFun();
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        textLinearLayout4.setId(k + 24);
        LinearLayout.LayoutParams params111 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textviewtodate = new TextView(this);
        textviewtodate.setCompoundDrawables(drawable, null, null, null);
        textviewtodate.setHint("Select To Date");
        textviewtodate.setBackgroundColor(Color.WHITE);
        textviewtodate.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewtodate.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewtodate.setTextColor(getResources().getColor(R.color.text_dark));
        textviewtodate.setHintTextColor(getResources().getColor(R.color.text_dark));
        textviewtodate.setLayoutParams(params111);
        textviewtodate.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewtodate.setGravity(CENTER);
        textLinearLayout4.addView(textviewtodate);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textviewtodate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt2(textviewtodate.getText().toString());
            }
        });

        textviewtodate.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                String fromdate = textviewfromdate.getText().toString();
                if (fromdate.equals("")) {
                    commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.select_from_date));
                } else {
                    String datee[] = fromdate.split("-");
                    final Calendar c = Calendar.getInstance();
                    int mYear = Integer.parseInt(datee[2]);
                    int mMonth = Integer.parseInt(datee[1]) - 1;
                    int mDay = Integer.parseInt(datee[0]);
                    SimpleDateFormat myFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                    Date dateBefore = null;
                    try {
                        dateBefore = myFormat1.parse(fromdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatePickerDialog datePickerDialog = new DatePickerDialog(DynamicActivity.this, (view1, year, monthOfYear, dayOfMonth) -> textviewtodate.setText(String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year)), mYear, mMonth, mDay);
                    commonFun();
                    datePickerDialog.getDatePicker().setMinDate(dateBefore.getTime());
                    datePickerDialog.show();
                }
            }
        });
    }

    public void CreateFromAndToDateWithTimeSelectionView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textviewdata.setTypeface(fontmedium);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
        LinearLayout textLinearLayout3 = new LinearLayout(this);
        textLinearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout3.setLayoutParams(params1);
        LinearLayout textLinearLayout4 = new LinearLayout(this);
        textLinearLayout4.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout4.setLayoutParams(params1);

        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));

        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);

        textLinearLayout2.addView(textLinearLayout3);
        textLinearLayout2.addView(textLinearLayout4);

        textLinearLayout3.setId(k + 23);

        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textviewfromdate = new TextView(this);

        Drawable drawable = getDrawable(R.drawable.calendar_clock);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        textviewfromdate.setCompoundDrawables(drawable, null, null, null);
        textviewfromdate.setHint("Select From DateWithTime");
        textviewfromdate.setHintTextColor(getResources().getColor(R.color.text_dark));
        textviewfromdate.setBackgroundColor(Color.WHITE);
        textviewfromdate.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewfromdate.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewfromdate.setTextColor(getResources().getColor(R.color.text_dark));
        textviewfromdate.setLayoutParams(params11);
        textviewfromdate.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewfromdate.setGravity(CENTER);
        textLinearLayout3.addView(textviewfromdate);

        textviewfromdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textviewfromdate.getText().toString());
            }
        });

        textviewfromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(DynamicActivity.this, (view, year1, monthOfYear, dayOfMonth1) -> {
                    c.set(year1, monthOfYear, dayOfMonth1);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(DynamicActivity.this, (view1, hourOfDay, minute1) -> {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute1);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                        textviewfromdate.setText(dateFormat.format(c.getTime()));

                    }, hour, minute, false);
                    timePickerDialog.show();
                    commonFun();
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        textLinearLayout4.setId(k + 24);
        LinearLayout.LayoutParams params111 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textviewtodate = new TextView(this);
        textviewtodate.setCompoundDrawables(drawable, null, null, null);
        textviewtodate.setHint("Select To DateWithTime");
        textviewtodate.setHintTextColor(getResources().getColor(R.color.text_dark));
        textviewtodate.setBackgroundColor(Color.WHITE);
        textviewtodate.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewtodate.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewtodate.setTextColor(getResources().getColor(R.color.text_dark));
        textviewtodate.setLayoutParams(params111);
        textviewtodate.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewtodate.setGravity(CENTER);
        textLinearLayout4.addView(textviewtodate);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textviewtodate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt2(textviewtodate.getText().toString());
            }
        });

        textviewtodate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                String fromdate = textviewfromdate.getText().toString();
                if (fromdate.equals("")) {
                    commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.select_from_date));
                } else {
                    String date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_30, TimeUtils.FORMAT_5, fromdate);
                    String Time = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_30, TimeUtils.FORMAT_29, fromdate);
                    String[] datas = Time.split(":");
                    final int mHour = Integer.parseInt(datas[0]);
                    final int mMinute = Integer.parseInt(datas[1]);
                    String datee[] = date.split("-");
                    final Calendar c = Calendar.getInstance();
                    int mYear = Integer.parseInt(datee[2]);
                    int mMonth = Integer.parseInt(datee[1]) - 1;
                    int mDay = Integer.parseInt(datee[0]);
                    SimpleDateFormat myFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                    Date dateBefore = null;
                    try {
                        dateBefore = myFormat1.parse(fromdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatePickerDialog datePickerDialog = new DatePickerDialog(DynamicActivity.this, (view, year1, monthOfYear, dayOfMonth1) -> {
                        c.set(year1, monthOfYear, dayOfMonth1);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(DynamicActivity.this, (view1, hourOfDay, minute1) -> {
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute1);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                            if (monthOfYear == mMonth && year1 == mYear && dayOfMonth1 == mDay) {
                                if (mHour < hourOfDay || mMinute < minute1) {
                                    textviewtodate.setText(dateFormat.format(c.getTime()));
                                } else {
                                    commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.please_select_as_after_from_time));
                                    textviewtodate.setText("");
                                }
                            } else {
                                textviewtodate.setText(dateFormat.format(c.getTime()));
                                commonFun();
                            }
                            commonFun();
                        }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(dateBefore.getTime());
                    datePickerDialog.show();
                }
            }
        });
    }

    public void CreateSelectionTimeView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        textviewdata.setTypeface(fontmedium);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);

        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));

        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);

        textLinearLayout2.setId(k);

        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textViewtime1 = new TextView(this);
        textViewtime1.setHint("Select Time");
        Drawable drawable = getDrawable(R.drawable.clock);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));

        textViewtime1.setCompoundDrawables(drawable, null, null, null);
        textViewtime1.setBackgroundColor(Color.WHITE);
        textViewtime1.setBackgroundResource(R.drawable.background_card_white_plan);
        textViewtime1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textViewtime1.setTextColor(getResources().getColor(R.color.text_dark));
        textViewtime1.setHintTextColor(getResources().getColor(R.color.text_dark));
        textViewtime1.setLayoutParams(params11);
        textViewtime1.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textViewtime1.setGravity(CENTER);

        textLinearLayout2.addView(textViewtime1);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textViewtime1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textViewtime1.getText().toString());
            }
        });

        textViewtime1.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(DynamicActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textViewtime1.setText(String.format("%02d:%02d", hourOfDay, minute));
                        commonFun();
                    }
                }, mHour, mMinute, true);
                timePickerDialog.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    timePickerDialog.getWindow().setDecorFitsSystemWindows(true);
                }
            }
        });
    }

    public void CreateFromAndToTimeSelectionView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        textviewdata.setTypeface(fontmedium);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);

        LinearLayout textLinearLayout3 = new LinearLayout(this);
        textLinearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout3.setLayoutParams(params1);
        LinearLayout textLinearLayout4 = new LinearLayout(this);
        textLinearLayout4.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout4.setLayoutParams(params1);
        textLinearLayout2.addView(textLinearLayout3);
        textLinearLayout2.addView(textLinearLayout4);

        textLinearLayout3.setId(k + 33);

        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textviewfromtime = new TextView(this);
        Drawable drawable = getDrawable(R.drawable.clock);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        textviewfromtime.setCompoundDrawables(drawable, null, null, null);
        textviewfromtime.setHint("Select From Time");
        textviewfromtime.setBackgroundColor(Color.WHITE);
        textviewfromtime.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewfromtime.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewfromtime.setTextColor(getResources().getColor(R.color.text_dark));
        textviewfromtime.setHintTextColor(getResources().getColor(R.color.text_dark));
        textviewfromtime.setLayoutParams(params11);
        textviewfromtime.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewfromtime.setGravity(CENTER);
        textLinearLayout3.addView(textviewfromtime);

        textviewfromtime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textviewfromtime.getText().toString());
            }
        });
        textLinearLayout4.setId(k + 34);

        textviewfromtime.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(DynamicActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textviewfromtime.setText(String.format("%02d:%02d", hourOfDay, minute));
                        commonFun();
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        LinearLayout.LayoutParams params111 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen._120sdp), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textviewtotime = new TextView(this);
        textviewtotime.setCompoundDrawables(drawable, null, null, null);
        textviewtotime.setHint("Select To Time");
        textviewtotime.setBackgroundColor(Color.WHITE);
        textviewtotime.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewtotime.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewtotime.setTextColor(getResources().getColor(R.color.text_dark));
        textviewtotime.setHintTextColor(getResources().getColor(R.color.text_dark));
        textviewtotime.setLayoutParams(params111);
        textviewtotime.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewtotime.setGravity(CENTER);
        textLinearLayout4.addView(textviewtotime);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textviewtotime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt2(textviewtotime.getText().toString());
            }
        });

        textviewtotime.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                String data = textviewfromtime.getText().toString();
                if (data.equalsIgnoreCase("")) {
                    commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.please_select_from_time));
                } else {
                    String[] datas = data.split(":");
                    final int mHour = Integer.parseInt(datas[0]);
                    final int mMinute = Integer.parseInt(datas[1]);

                    TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hour, int minute) {
                            if (mHour < hour || mMinute < minute) {
                                textviewtotime.setText(String.format("%02d:%02d", hour, minute));
                            } else {
                                commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.please_select_as_after_from_time));
                                textviewtotime.setText("");
                            }
                            commonFun();
                        }
                    };
                    final TimePickerDialog timePickerDialog = new TimePickerDialog(DynamicActivity.this, timePickerListener, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            }
        });
    }

    private void CreateSingleListSelection(ActivityDetailsModelClass List, int k) {
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        binding.llActivityDetailsView.addView(textLinearLayout);

        TextView textcombosingle = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textcombosingle.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textcombosingle.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textcombosingle.setTypeface(fontmedium);
        params.setMargins((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textcombosingle.setTextColor(getResources().getColor(R.color.text_dark));
        textcombosingle.setLayoutParams(params);
        textcombosingle.setTextSize((int) getResources().getDimension(R.dimen._5sdp));

        textLinearLayout.addView(textcombosingle);
        textcombosingle.setId(k);

        TextView singlecomboedittext = new TextView(this);
        singlecomboedittext.setBackgroundColor(Color.WHITE);

        singlecomboedittext.setBackgroundResource(R.drawable.background_card_white_plan);
        singlecomboedittext.setTextColor(getResources().getColor(R.color.text_dark));
        singlecomboedittext.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        singlecomboedittext.setLayoutParams(params);
        singlecomboedittext.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        singlecomboedittext.setVisibility(View.VISIBLE);
        Drawable drawable = getDrawable(R.drawable.right_arrow);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._5sdp));
        singlecomboedittext.setCompoundDrawables(null, null, drawable, null);
        singlecomboedittext.setHintTextColor(getResources().getColor(R.color.text_dark));
        singlecomboedittext.setHint("Select the " + List.getFieldName());
        textLinearLayout.addView(singlecomboedittext);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        singlecomboedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(singlecomboedittext.getText().toString());
            }
        });

        TextView Idview = new TextView(this);
        Idview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setCodes(Idview.getText().toString());
            }
        });
        singlecomboedittext.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ArrayList<ActivityModelClass> mlist = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(List.getInput());
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String[] data = jsonObject.toString().replaceAll("[{}]", "").split(",");
                            String[] mCode = data[0].split(":");
                            String[] mName = data[1].split(":");

                            mlist.add(new ActivityModelClass(mCode[1].replaceAll("\"", ""), mName[1].replaceAll("\"", ""), false));
                        }
                        ShowListPopup(singlecomboedittext, Idview, mlist, List.getFieldName(), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CreateMultipleListSelection(ActivityDetailsModelClass List, int k) {
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        binding.llActivityDetailsView.addView(textLinearLayout);

        TextView textcombomultiple = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textcombomultiple.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textcombomultiple.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textcombomultiple.setTypeface(fontmedium);
        params.setMargins((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textcombomultiple.setTextColor(getResources().getColor(R.color.text_dark));
        textcombomultiple.setLayoutParams(params);
        textcombomultiple.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinearLayout.addView(textcombomultiple);
        textcombomultiple.setId(k);
        textcombomultiple.setTypeface(fontmedium);

        TextView multicomboeditext = new TextView(this);
        multicomboeditext.setBackgroundColor(Color.WHITE);
        multicomboeditext.setBackgroundResource(R.drawable.background_card_white_plan);
        multicomboeditext.setTextColor(getResources().getColor(R.color.text_dark));
        multicomboeditext.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        multicomboeditext.setLayoutParams(params);
        multicomboeditext.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        multicomboeditext.setVisibility(View.VISIBLE);
        multicomboeditext.setHint("Select the " + List.getFieldName());
        multicomboeditext.setHintTextColor(getResources().getColor(R.color.text_dark));
        textLinearLayout.addView(multicomboeditext);
        Drawable drawable = getDrawable(R.drawable.right_arrow);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._5sdp));
        multicomboeditext.setCompoundDrawables(null, null, drawable, null);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        multicomboeditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(multicomboeditext.getText().toString());
            }
        });
        TextView TextCode = new TextView(this);

        TextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ActivityViewItem.get(k).setCodes(TextCode.getText().toString());
            }
        });
        multicomboeditext.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ArrayList<ActivityModelClass> mlist = new ArrayList<>();
                String[] selectedIds = TextCode.getText().toString().split(",");
                List<String> activityForList = Arrays.asList(selectedIds);
                try {
                    JSONArray jsonArray = new JSONArray(List.getInput());
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String[] data = jsonObject.toString().replaceAll("[{}]", "").split(",");
                            String[] mCode = data[0].split(":");
                            String[] mName = data[1].split(":");
                            Log.v("eeeswa", "" + mCode[1].replaceAll("\"", ""));
                            if (activityForList.contains(mCode[1].replaceAll("\"", ""))) {
                                mlist.add(new ActivityModelClass(mCode[1].replaceAll("\"", ""), mName[1].replaceAll("\"", ""), true));
                            } else {
                                mlist.add(new ActivityModelClass(mCode[1].replaceAll("\"", ""), mName[1].replaceAll("\"", ""), false));
                            }
                        }
                        ShowListPopup(multicomboeditext, TextCode, mlist, List.getFieldName(), true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void adduploadfile(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textviewdata.setTypeface(fontmedium);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinearLayout1.addView(textviewdata);
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textLinearLayout2.setLayoutParams(linearLayoutParams);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout1.addView(textLinearLayout2);

        TextView textfileupload = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
//        params.setMargins(1, 1, 1, 1);
        params.weight = 1;
        Drawable drawable = getDrawable(R.drawable.form);
        assert drawable != null;
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        drawable.setTint(getColor(R.color.dark_purple));
        textfileupload.setCompoundDrawables(drawable, null, null, null);
        textfileupload.setBackgroundColor(Color.WHITE);
        textfileupload.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen._4sdp));
//        textfileupload.setBackgroundResource(R.drawable.background_card_white_plan);
//        textfileupload.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textfileupload.setTextColor(getResources().getColor(R.color.text_dark));
        textfileupload.setTextSize((int) getResources().getDimension(R.dimen._5sdp));

        textfileupload.setLayoutParams(params);
        textfileupload.setHint("Select " + List.getFieldName());
//        textLinearLayout2.addView(textfileupload);
        textLinearLayout2.setId(k);

        LinearLayout parentLayout = new LinearLayout(this);
        parentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        parentLayout.setOrientation(LinearLayout.HORIZONTAL);
        parentLayout.setBackgroundResource(R.drawable.background_card_white_plan);

        ImageView clearButton = new ImageView(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen._15sdp),
                (int) getResources().getDimension(R.dimen._15sdp)
        );
        buttonParams.gravity = Gravity.CENTER_VERTICAL;
        clearButton.setLayoutParams(buttonParams);
        clearButton.setPadding(0, (int) getResources().getDimension(R.dimen._2sdp), 0, (int) getResources().getDimension(R.dimen._2sdp));
        clearButton.setImageResource(R.drawable.close_icon);
        clearButton.setVisibility(View.GONE);

        parentLayout.addView(textfileupload);
        parentLayout.addView(clearButton);

        clearButton.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (textfileupload.getText() != null && !textfileupload.getText().toString().isEmpty()) {
                    removeFile(textfileupload.getText().toString());
                    textfileupload.setText("");
                }
            }
        });

        textLinearLayout2.addView(parentLayout);

        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textfileupload.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textfileupload.getText().toString());
                if (textfileupload.getText() != null && !textfileupload.getText().toString().isEmpty()) {
                    drawable.setTint(getColor(R.color.green_60));
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.GONE);
                    drawable.setTint(getColor(R.color.dark_purple));
                }
            }
        });

        textfileupload.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                selectedTextFileUpload = textfileupload;
//                if (!CheckStoragePermission()) {
//                    RequestStoragePermission();
//                } else {
                Open_Storage();
//                }
            }
        });
    }

    public void addedittextnumericcurrency(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        textviewdata.setTypeface(fontmedium);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        //textviewdata.setBackgroundResource(R.drawable.linearlayoutbgd);
        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinearLayout1.addView(textviewdata);
        //   textLinearLayout1.setBackgroundResource(R.drawable.linearlayoutbgd);

        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout1.addView(textLinearLayout2);
        TextView textviewdata1 = new TextView(this);
//        textviewdata.setText(List.getFieldName());
        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params11.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata1.setBackgroundColor(Color.WHITE);
        textviewdata1.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewdata1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata1.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata1.setLayoutParams(params11);
        textviewdata1.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textviewdata1.setText(List.getControlPara());

        textLinearLayout2.addView(textviewdata1);
        EditText textcurrency = new EditText(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textcurrency.setBackgroundColor(Color.WHITE);
        textcurrency.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        textcurrency.setBackgroundResource(R.drawable.background_card_white_plan);
        textcurrency.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textcurrency.setTextColor(getResources().getColor(R.color.text_dark));
        textcurrency.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textcurrency.setLayoutParams(params);

        textLinearLayout2.addView(textcurrency);
        textcurrency.setInputType(InputType.TYPE_CLASS_NUMBER);
        textcurrency.setId(k);
        textcurrency.setHint("Enter " + List.getFieldName());
        textcurrency.setCursorVisible(true);
        textcurrency.setClickable(true);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        textcurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEdited = true;
                ActivityViewItem.get(k).setAnswerTxt(textcurrency.getText().toString());
            }
        });
    }

    public void CreateLocationView(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout MainLayout = new LinearLayout(this);
        MainLayout.setOrientation(LinearLayout.VERTICAL);
        MainLayout.setLayoutParams(param);
        binding.llActivityDetailsView.addView(MainLayout);

        TextView LabelText = new TextView(this);
        LabelText.setLayoutParams(param);
        LabelText.setTextColor(getResources().getColor(R.color.text_dark));
        LabelText.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        LabelText.setText(List.getFieldName());
        LabelText.setTypeface(fontmedium);
        Drawable drawable = getDrawable(R.drawable.refresh);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        LabelText.setCompoundDrawables(null, null, drawable, null);
        MainLayout.addView(LabelText);

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param1.weight = 1;
        param1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        LinearLayout LatLongLayout = new LinearLayout(this);
        LatLongLayout.setOrientation(LinearLayout.HORIZONTAL);
        LatLongLayout.setLayoutParams(param);

        TextView LatText = new TextView(this);
        LatText.setLayoutParams(param1);
        LatText.setTextColor(getResources().getColor(R.color.text_dark));
        LatText.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        LatText.setHint("Lat :");
        LatText.setHintTextColor(getResources().getColor(R.color.text_dark));

        TextView LongText = new TextView(this);
        LongText.setLayoutParams(param1);
        LongText.setPadding((int) getResources().getDimension(R.dimen._4sdp), 0, (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        LongText.setTextColor(getResources().getColor(R.color.text_dark));
        LongText.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        LongText.setHint("Lat :");
        LatText.setHintTextColor(getResources().getColor(R.color.text_dark));

        TextView AddressText = new TextView(this);
        AddressText.setLayoutParams(param1);
        AddressText.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        AddressText.setTextColor(getResources().getColor(R.color.text_dark));
        AddressText.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        AddressText.setHint("Address");
        AddressText.setHintTextColor(getResources().getColor(R.color.text_dark));
        AddressText.setBackgroundColor(Color.WHITE);
        AddressText.setBackgroundResource(R.drawable.background_card_white_plan);
        AddressText.setTextColor(getResources().getColor(R.color.text_dark));
        LatLongLayout.addView(LatText);
        LatLongLayout.addView(LongText);

        MainLayout.addView(LatLongLayout);
        MainLayout.addView(AddressText);

        String address;
        gpsTrack = new GPSTrack(this);
        double latitude = gpsTrack.getLatitude();
        double longitude = gpsTrack.getLongitude();
        if (UtilityClass.isNetworkAvailable(DynamicActivity.this)) {
            address = CommonUtilsMethods.gettingAddress(this, latitude, longitude, false);
        } else {
            address = getString(R.string.no_address_found);
        }
        AddressText.setText(address);
        LatText.setText("Lat  :" + String.valueOf(latitude));
        LongText.setText("Long  :" + String.valueOf(longitude));

        if (latitude == 0.0 || longitude == 0.0)
            ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "" + longitude, address, List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));
        else
            ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "Lat  :" + latitude + " " + "Long  :" + longitude, address, List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));

        LabelText.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (CommonUtilsMethods.isLocationEnabled(getApplicationContext())) {
                    if (!CheckLocPermission()) {
                        RequestLocationPermission();
                    } else {
                        commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.wait_for_location));
                        String address;
                        gpsTrack = new GPSTrack(DynamicActivity.this);
                        double latitude = gpsTrack.getLatitude();
                        double longitude = gpsTrack.getLongitude();
                        address = CommonUtilsMethods.gettingAddress(DynamicActivity.this, latitude, longitude, false);

                        if (ActivityViewItem != null && ActivityViewItem.size() > k) {
                            ActivityViewItem.get(k).setAnswerTxt("Lat  :" + latitude + " " + "Long  :" + longitude);
                            ActivityViewItem.get(k).setAnswerTxt2(address);
                        }
                        LatText.setText("Lat  :" + String.valueOf(latitude));
                        LongText.setText("Long  :" + String.valueOf(longitude));
                    }
                } else {
                    CommonUtilsMethods.RequestGPSPermission(DynamicActivity.this);
                }
            }
        });
    }

    public void addeditcurrencyconvertor(ActivityDetailsModelClass List, int k) {
        if (!List.getControlPara().equals("")) {
            value = List.getControlPara().split("[/]");
            valfrom = value[0].split("[-]");
            valto = value[1].split("[-]");
            Log.d("testpara", value[0] + "---" + value[1]);
            Log.d("testpara", valfrom[0] + "---" + valfrom[1]);
            Log.d("testpara", valto[0] + "---" + valto[1]);
        }

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        LinearLayout textLinear = new LinearLayout(this);
        textLinear.setOrientation(LinearLayout.HORIZONTAL);
        textLinear.setLayoutParams(param);
        textLinearLayout1.addView(textLinear);

        TextView textviewdata = new TextView(this);

        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        textviewdata.setTypeface(fontmedium);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinear.addView(textviewdata);

        TextView textviewdat = new TextView(this);

        LinearLayout.LayoutParams params113 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params113.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdat.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdat.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdat.setLayoutParams(params113);
        textviewdat.setText("( " + List.getControlPara() + " )");
        textviewdat.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textLinear.addView(textviewdat);

        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout1.addView(textLinearLayout2);

        TextView textviewdata1 = new TextView(this);
        textviewdata.setText(List.getFieldName());
        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params11.width = 120;
        params11.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdata1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata1.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata1.setLayoutParams(params11);
        textviewdata1.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
//        textviewdata1.setText(control_para.get(k));
//        textviewdata1.setText(valfrom[0]);
        textLinearLayout2.addView(textviewdata1);
        EditText txtcurconvert1 = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        txtcurconvert1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        txtcurconvert1.setTextColor(getResources().getColor(R.color.text_dark));
        txtcurconvert1.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        txtcurconvert1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtcurconvert1.setLayoutParams(params);

        textLinearLayout2.addView(txtcurconvert1);
        txtcurconvert1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtcurconvert1.setId(k);
        txtcurconvert1.setHint("Enter " + List.getFieldName());

        LinearLayout textLinearLayout3 = new LinearLayout(this);
        textLinearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout1.addView(textLinearLayout3);
        TextView textviewdata12 = new TextView(this);
        textviewdata.setText(List.getFieldName());
        LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params12.width = 120;
        params12.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdata12.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata12.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata12.setLayoutParams(params12);
        textviewdata12.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
//        textviewdata12.setText(valto[0]);
        textLinearLayout3.addView(textviewdata12);
        EditText txtcurconvert2 = new EditText(this);
        LinearLayout.LayoutParams params13 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params13.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        txtcurconvert2.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        txtcurconvert2.setTextColor(getResources().getColor(R.color.text_dark));
        txtcurconvert2.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        txtcurconvert2.setLayoutParams(params13);
        txtcurconvert2.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        textLinearLayout3.addView(txtcurconvert2);
        txtcurconvert2.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtcurconvert2.setFocusable(false);
        txtcurconvert2.setClickable(false);
        txtcurconvert2.setEnabled(false);
        txtcurconvert2.setId(k);
        txtcurconvert2.setHint("Enter " + List.getFieldName());
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));
    }

    public void digitalsign(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);

        String firstChar = "<font color='#000000'>" + List.getFieldName() + "</font>";
        String firstChar2 = "<font color='#EE0000'><small><small><sup> ✶</sup></small></small></font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        textviewdata.setTypeface(fontmedium);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._5sdp));

        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);

        ImageView timeclock = new ImageView(this);
        timeclock.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        timeclock.setLayoutParams(params1);
        textLinearLayout2.addView(timeclock);
        textLinearLayout2.setId(k);
        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textViewtime1 = new TextView(this);
        textViewtime1.setText("Signature");
        textViewtime1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textViewtime1.setTextColor(Color.BLACK);
        textViewtime1.setLayoutParams(params11);
        textViewtime1.setTextSize((int) getResources().getDimension(R.dimen._5sdp));
        textViewtime1.setGravity(CENTER);

        textLinearLayout2.addView(textViewtime1);
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));
    }

    public void TableList(ActivityDetailsModelClass List, int k) {
        ActivityViewItem.add(new ActivityDetailsModelClass(k, List.getFieldName(), "", "", List.getControlId(), List.getCreationId(), List.getInput(), List.getMandatory(), List.getControlPara(), List.getGroupCreationId(), " ", List.getSlno()));
    }

    public void ShowListPopup(TextView NameView, TextView IdView, ArrayList<ActivityModelClass> List, String name, boolean isMultipleCheck) {
        if (isMultipleCheck) {
            binding.slideScreen.viewDummy1.setVisibility(View.VISIBLE);
            binding.slideScreen.txtClDone.setVisibility(View.VISIBLE);
        } else {
            binding.slideScreen.viewDummy1.setVisibility(View.GONE);
            binding.slideScreen.txtClDone.setVisibility(View.GONE);
        }

        List<String> mListName = new ArrayList<>();
        List<String> mListId = new ArrayList<>();
        binding.mainLayout.openDrawer(Gravity.RIGHT);
        binding.slideScreen.etSearch.setText("");
        binding.slideScreen.tvSearchheader.setText("Select " + name);
        binding.slideScreen.etSearch.setHint("Search " + name);
        adapter1 = new ActvityList2Adapter(DynamicActivity.this, List, IdView, isMultipleCheck, new CheckBoxInterface() {
            @Override
            public void Checked(ActivityModelClass activityModelClass) {
                if (isMultipleCheck) {
                    mListName.add(activityModelClass.getName());
                    mListId.add(activityModelClass.getCode());
//                    IdView.setText(activityModelClass.getCode());
                } else {
                    binding.mainLayout.closeDrawer(Gravity.RIGHT);
                    NameView.setText(activityModelClass.getName());
                    IdView.setText(activityModelClass.getCode());
                }
            }

            @Override
            public void UnChecked(ActivityModelClass activityModelClass) {
                mListName.remove(activityModelClass.getName());
                mListId.remove(activityModelClass.getCode());
            }
        });
        binding.slideScreen.acRecyelerView.setLayoutManager(new LinearLayoutManager(DynamicActivity.this));
        binding.slideScreen.acRecyelerView.setAdapter(adapter1);

        binding.slideScreen.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchString = charSequence.toString();
                adapter1.getFilter().filter(searchString);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.slideScreen.txtClDone.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (isMultipleCheck) {
                    String lids = "";
                    for (int i = 0; i < mListId.size(); i++) {
                        lids = lids + mListId.get(i) + ",";
                    }
                    NameView.setText(mListName.toString().replaceAll("[\\[\\]]", ""));
                    IdView.setText(lids);
                    binding.mainLayout.closeDrawer(Gravity.RIGHT);
                }
            }
        });

        binding.slideScreen.cancelImg.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                binding.mainLayout.closeDrawer(Gravity.RIGHT);
            }
        });
    }

    private void Open_Storage() {
        Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, 7);
    }

    @SuppressLint({"MissingSuperCall", "Range"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 7) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                try {
                    uri = data.getData();
                    try {
                        final int takeFlags = data.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String filename = getFileNameFromUri(uri);
                    if (filename == null) {
                        commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.please_select_correct_path));
                        return;
                    }
//                    if (filename.endsWith(".zip")) {
//                        commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.zip_not_supported));
//                        return;
//                    }
                    selectedTextFileUpload.setText(filename);
                    commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.file_accepted));uri = data.getData();

                    long fileSize = getFileSize(uri);

                    if (fileSize > MAX_FILE_SIZE) {
                        commonUtilsMethods.showToastMessage(DynamicActivity.this, "File size limit exceeded");
                        removeFile(selectedTextFileUpload.getText().toString());
                        selectedTextFileUpload.setText("");
                        return;
                    }
                    copyFileToAppDir(uri);
//                    String fullPath = getPathFromURI(DynamicActivity.this, uri);
//                    String[] parts = fullPath.split("/");
//                    String filenmae = parts[parts.length - 1];
//
//                    if (filenmae.endsWith(".zip")) {
//                        commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.zip_not_supported));
//                    } else {
//                        FilnameTet.setText(String.valueOf(filenmae));
//                        commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.file_accepted));
////                        File dir1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), "SAN_Images");
////                        if(!dir1.exists()) {
////                            dir1.mkdirs();
////                        }
//                        File file = null;
//                        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//                            file = new File(getApplicationContext().getExternalFilesDir(null) + "/ActivityUpload/");
//                        } else {
//                            Log.e("File Creation", "captureFile: No media mounted");
//                        }
//                        if (file != null && !file.exists()) {
//                            if (!file.mkdirs()) {
//                                Log.e("File Creation", "Directory Creation Failed.");
//                            }
//                        }
//                        copyFileOrDirectory(String.valueOf(fullPath), String.valueOf(file));
//                    }
                } catch (Exception ex) {
                    Log.v("Error", ex.toString());
                    commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.please_select_correct_path));
                    ex.printStackTrace();
                }
            } else {
                commonUtilsMethods.showToastMessage(DynamicActivity.this, DynamicActivity.this.getString(R.string.no_file_selected));
            }
            commonFun();
        }
    }

    private void copyFileToAppDir(Uri sourceUri) {
        try {
            File destDir = new File(getApplicationContext().getExternalFilesDir(null), "ActivityUpload");
            if (!destDir.exists()) destDir.mkdirs();
            String fileName = getFileNameFromUri(sourceUri);
            if (fileName == null) fileName = "file_" + System.currentTimeMillis();
            File destFile = new File(destDir, fileName);
            try (InputStream inputStream = getContentResolver().openInputStream(sourceUri);
                 OutputStream outputStream = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            Log.d("FileCopy", "Saved to: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        try {
            if ("content".equals(uri.getScheme())) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index != -1) {
                            result = cursor.getString(index);
                        }
                    }
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private long getFileSize(Uri uri) {
        long size = -1;
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    size = cursor.getLong(sizeIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public void copyFileOrDirectory(String srcDir, String dstDir) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());
            Log.d("string", src.getName());
            if (src.isDirectory()) {
                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                FileUtils.copy(in,out);
//            }
            destination.transferFrom(source, 0, source.size());
            // destination.write(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public void removeFile(String fileName) {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(getApplicationContext().getExternalFilesDir(null) + "/ActivityUpload/" + fileName);
        } else {
            Log.e("File Deletion", "captureFile: No media mounted");
        }
        if (file != null && !file.exists()) {
            Log.w("File Deletion", "No File Found" + file.getAbsolutePath());
        } else if (file != null && file.exists()) {
            if (file.delete()) {
                Log.d("FileDeleter", "File deleted: " + file.getAbsolutePath());
            } else {
                Log.e("FileDeleter", "File not deleted: " + file.getAbsolutePath());
            }
        }
    }

    public void saveActivity() {
//        if(UtilityClass.isNetworkAvailable(DynamicActivity.this)) {
        int conut = 0;
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < ActivityViewItem.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                ActivityDetailsModelClass List = ActivityViewItem.get(i);
                Date today = new Date();
                String dateTime = TimeUtils.GetCurrentTimeStamp(TimeUtils.FORMAT_1);
                String dateToStr = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_4, HomeDashBoard.binding.textDate.getText().toString());
                String time = TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_32);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                String dateToStr1 = format1.format(today) + " 00:00:00";
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                jsonObject.put("act_date", dateToStr + " " + time);
                jsonObject.put("dcr_date", dateToStr1);
                jsonObject.put("update_time", dateTime);
                jsonObject.put("ModTime", "");
                jsonObject.put("slno", List.getSlno());
                jsonObject.put("ctrl_id", List.getControlId());
                jsonObject.put("creat_id", List.getCreationId());
                jsonObject.put("group_creat_id", List.getCreationId());
                jsonObject.put("WT", "0");
                jsonObject.put("Pl", "0");
                jsonObject.put("cus_code", "0");
                jsonObject.put("lat", gpsTrack.getLatitude());
                jsonObject.put("lng", gpsTrack.getLongitude());
                jsonObject.put("cusname", "Name");
                jsonObject.put("DataSF", SharedPref.getSfCode(this));
                jsonObject.put("type", "0");
                jsonObject.put("WT_code", "");
                jsonObject.put("WTName", "");
                jsonObject.put("FWFlg", "");
                jsonObject.put("town_code", "");
                jsonObject.put("town_name", "");
                jsonObject.put("Rsf", selectedHQ);
                jsonObject.put("sf_type", SharedPref.getSfType(this));
                jsonObject.put("Designation", SharedPref.getDesig(this));
                jsonObject.put("state_code", SharedPref.getStateCode(this));
                jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(this));

                if (List.getControlId().equalsIgnoreCase("5") || List.getControlId().equalsIgnoreCase("7") || List.getControlId().equalsIgnoreCase("16")) {
                    if (List.getMandatory().equalsIgnoreCase("1") && (List.getAnswerTxt().equalsIgnoreCase(""))) {
                        commonUtilsMethods.showToastMessage(DynamicActivity.this,getString(R.string.fill_the_from) + List.getFieldName());
                        break;
                    } else if (List.getMandatory().equalsIgnoreCase("1") && (List.getAnswerTxt2().equalsIgnoreCase(""))) {
                        commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.fill_the_to) + List.getFieldName());
                        break;
                    } else {
                        jsonObject.put("values", List.getAnswerTxt() + "," + List.getAnswerTxt2());
                        jsonObject.put("codes", List.getCodes());
                        Log.v("codes", List.getCodes());
                    }
                } else if (List.getControlId().equalsIgnoreCase("17")) {
                    if (List.getMandatory().equalsIgnoreCase("1") && List.getAnswerTxt().equalsIgnoreCase("")) {
                        commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.choose_the)+ List.getFieldName() + "");
                        break;
                    } else {
                        jsonObject.put("values", List.getAnswerTxt() + "$" + List.getAnswerTxt2());
                        jsonObject.put("codes", List.getCodes());
                    }
                } else {
                    if (List.getMandatory().equalsIgnoreCase("1") && List.getAnswerTxt().equalsIgnoreCase("")) {
                            commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.fill_the) + List.getFieldName() + "");
                        break;
                    } else {
                        jsonObject.put("values", List.getAnswerTxt());
                        jsonObject.put("codes", List.getCodes());
                        Log.v("codes", List.getCodes());
                    }
                }
                jsonArray.put(jsonObject);
                conut++;
            }

            if (conut == ActivityViewItem.size()) {
                isEdited = false;
                activityDate = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                activityTime = CommonUtilsMethods.getCurrentInstance("HH:mm:ss");
                binding.progressSubmit.setVisibility(View.VISIBLE);
                JSONObject MainObject = commonUtilsMethods.CommonObjectParameter(DynamicActivity.this);
                MainObject.put("tableName", "savedcract");
                MainObject.put("val", jsonArray);
                MainObject.put("division_code", SharedPref.getDivisionCode(DynamicActivity.this));
                Log.v("JsonObject  :", "" + MainObject.toString());
                Long id = activityOfflineDataDao.saveActivityOfflineData(new ActivityOfflineDataTable(chosenActivityModelClass.getSlNo(), chosenActivityModelClass.getActivityName(), activityDate, activityTime, MainObject.toString(), 0, Constants.WAITING_FOR_SYNC));
                Map<String, String> QryParam = new HashMap<>();
                QryParam.put("axn", "save/activity");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(DynamicActivity.this), QryParam, MainObject.toString());
                TaggedImage(id);
//                    call.enqueue(new Callback<JsonElement>() {
//                        @Override
//                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                            if(response.code() == 200 || response.code() == 201) {
//                                commonUtilsMethods.showToastMessage(DynamicActivity.this, "Activity Submitted successfully");
                binding.progressSubmit.setVisibility(View.GONE);
//                                TaggedImage();
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
//
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<JsonElement> call, Throwable t) {
//                            commonUtilsMethods.showToastMessage(DynamicActivity.this, t.getMessage());
//                            binding.progressSubmit.setVisibility(View.GONE);
//                        }
//                    });
            }
        } catch (Exception a) {
            a.printStackTrace();
        }

//        }else {
//            commonUtilsMethods.showToastMessage(DynamicActivity.this, getResources().getString(R.string.no_network));
//
//        }
    }

    public void TaggedImage(Long id) {
        try {
            for (int i = 0; i < ActivityViewItem.size(); i++) {
                ActivityDetailsModelClass List = ActivityViewItem.get(i);
                if (List.getControlId().equalsIgnoreCase("10") && !List.getAnswerTxt().isEmpty()) {
                    binding.progressSubmit.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = new JSONObject();

                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateToStr = format.format(today);
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    String dateToStr1 = format1.format(today) + " 00:00:00";

                    jsonObject.put("sfcode", SharedPref.getSfCode(this));
                    jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                    jsonObject.put("act_date", dateToStr);
                    jsonObject.put("dcr_date", dateToStr1);
                    jsonObject.put("update_time", dateToStr);
                    jsonObject.put("ModTime", "");
                    jsonObject.put("slno", List.getSlno());
                    jsonObject.put("ctrl_id", List.getControlId());
                    jsonObject.put("creat_id", List.getCreationId());
                    jsonObject.put("group_creat_id", List.getCreationId());
                    jsonObject.put("WT", "0");
                    jsonObject.put("Pl", "0");
                    jsonObject.put("cus_code", "0");
                    jsonObject.put("lat", gpsTrack.getLatitude());
                    jsonObject.put("lng", gpsTrack.getLongitude());
                    jsonObject.put("cusname", "Name");
                    jsonObject.put("DataSF", SharedPref.getSfCode(this));
                    jsonObject.put("type", "0");
                    jsonObject.put("WT_code", "");
                    jsonObject.put("WTName", "");
                    jsonObject.put("FWFlg", "");
                    jsonObject.put("town_code", "");
                    jsonObject.put("town_name", "");
                    jsonObject.put("Rsf", SharedPref.getHqCode(DynamicActivity.this));
                    jsonObject.put("values", List.getAnswerTxt());
                    jsonObject.put("codes", List.getCodes());
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    JSONObject MainObject = commonUtilsMethods.CommonObjectParameter(DynamicActivity.this);
                    Log.v("JsonObject  :", "" + MainObject.toString());
                    MainObject.put("tableName", "savedcract");
                    MainObject.put("val", jsonArray);
                    MainObject.put("division_code", SharedPref.getDivisionCode(DynamicActivity.this));
                    ApiInterface apiInterface1 = RetrofitClient.getRetrofit(DynamicActivity.this, SharedPref.getTagApiImageUrl(DynamicActivity.this));
//                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), "SAN_Images");
                    File file = null;
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        file = new File(getApplicationContext().getExternalFilesDir(null) + "/ActivityUpload/");
                    } else {
                        Log.e("File Creation", "captureFile: No media mounted");
                    }
                    if (file != null && !file.exists()) {
                        if (!file.mkdirs()) {
                            Log.e("File Creation", "Directory Creation Failed.");
                        }
                    }
                    File destinationFile = new File(file, List.getAnswerTxt());
                    try {
                        if (!destinationFile.createNewFile()) {
                            Log.e("File Creation", "Destination File Creation Failed.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    file1 = new File(file.getAbsolutePath() + "/", List.getAnswerTxt());

                    activityUploadDataDao.saveActivityUploadData(new ActivityUploadDataTable(Integer.parseInt(String.valueOf(id)), chosenActivityModelClass.getSlNo(), chosenActivityModelClass.getActivityName(), activityDate, activityTime, List.getAnswerTxt(), destinationFile.getAbsolutePath(), MainObject.toString(), 0, Constants.WAITING_FOR_SYNC));
//                    MultipartBody.Part img = convertImg("ActivityFile", String.valueOf(file1));
//                    HashMap<String, RequestBody> values = field(MainObject.toString());
//                    Call<JsonObject> saveAttachement = apiInterface1.SaveImg(values, img);
//
//                    saveAttachement.enqueue(new Callback<JsonObject>() {
//                        @Override
//                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    binding.progressSubmit.setVisibility(View.GONE);
//                            if(response.isSuccessful()) {
//                                try {
//                                    assert response.body() != null;
//                                    JSONObject json = new JSONObject(response.body().toString());
//                                    Log.v("ImgUpload", json.toString());
//                                    json.getString("success");
//                                    commonUtilsMethods.showToastMessage(DynamicActivity.this, "ImageUpload SucessFully");
//                                } catch (Exception ignored) {
//
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<JsonObject> call, Throwable t) {
//                            binding.progressSubmit.setVisibility(View.GONE);
//                        }
//                    });
                }

            }
            commonUtilsMethods.showToastMessage(DynamicActivity.this, activityCap + getString(R.string.saved_successfully));
        } catch (Exception a) {
            a.printStackTrace();
        }

    }

    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(txt, MultipartBody.FORM);
    }

    public static String getPathFromURI(final Context Context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(Context, uri)) {
            if (isExternalStorageDocument(uri)) {
                Log.v("bv1", "------" + uri);
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                Log.v("bv2", "------" + uri);
                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                    return getDataColumn(Context, contentUri, null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Log.v("bv3", "------" + uri);
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(Context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.v("bv4", "------" + uri);
            return getDataColumn(Context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.v("bv5", "------" + uri);
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context Context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = Context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void commonFun() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public MultipartBody.Part convertImg(String tag, String path) {
        Log.d("path", tag + "-" + path);
        MultipartBody.Part yy = null;
        try {
            File file;
            if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(DynamicActivity.this).compressToFile(new File(path));
                Log.d("path", tag + "-" + path);
            } else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

            Log.d("path", String.valueOf(yy));
        } catch (Exception ignored) {
        }
        return yy;
    }

    void showHQ() {
        try {
            JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            JSONArray newJsonArray = new JSONArray();
            JSONObject independent = new JSONObject();
            independent.put("name", "Independent");
            independent.put("id", SharedPref.getSfCode(DynamicActivity.this));
            newJsonArray.put(independent);
            for (int i = 0; i < jsonArray1.length(); i++) {
                newJsonArray.put(jsonArray1.optJSONObject(i));
            }
            ArrayList<String> list = new ArrayList<>();
            if (newJsonArray.length() > 0) {
                for (int i = 0; i < newJsonArray.length(); i++) {
                    JSONObject jsonObject = newJsonArray.getJSONObject(i);
                    list.add(jsonObject.getString("name"));
                }
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DynamicActivity.this);
            LayoutInflater inflater = DynamicActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_listview, null);
            alertDialog.setView(dialogView);
            TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
            ListView listView = dialogView.findViewById(R.id.listView);
            SearchView searchView = dialogView.findViewById(R.id.searchET);

            searchView.setIconified(false);
            searchView.setIconifiedByDefault(false);
            searchView.setMaxWidth(Integer.MAX_VALUE);

            headerTxt.setText(getResources().getText(R.string.select_hq));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(DynamicActivity.this, android.R.layout.simple_list_item_1, new ArrayList<>(list));
            listView.setAdapter(adapter);
            AlertDialog dialog = alertDialog.create();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
//                    adapter.getFilter().filter(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    List<String> filtered = new ArrayList<>();
                    for (String item : list) {
                        if (item.toLowerCase().contains(s.toLowerCase())) {
                            filtered.add(item);
                        }
                    }

                    adapter.clear();
                    adapter.addAll(filtered);
                    adapter.notifyDataSetChanged();
                    return true;
                }
            });
            listView.setOnItemClickListener((adapterView, view1, position, l) -> {
                String selectedHq = listView.getItemAtPosition(position).toString();
                binding.txthqName.setText(selectedHq);
                for (int i = 0; i < newJsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = newJsonArray.getJSONObject(i);
                        if (jsonObject.optString("name").equalsIgnoreCase(selectedHq)) {
                            selectedHQ = jsonObject.optString("id");
                            binding.rlNoData.setVisibility(View.VISIBLE);
                            binding.rlDetailsMain.setVisibility(View.GONE);
                            binding.btnSubmit.setVisibility(View.GONE);
                            chosenActivityModelClass = null;
                            chosenActivityPosition = -1;
                            this.adapter.changeRowIndex(-1);
                            getActivity();
//                            getHQData(jsonObject.optString("id"));
//                            getActivity(jsonObject.getString("id"));
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                commonFun();
                dialog.dismiss();
            });

            alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getHQData(String hqCode) {
        try {
            Log.d("DynamicActivity", "showHQ: " + hqCode);
            boolean docAvailability = masterDataDao.isDataAvailable(Constants.DOCTOR_MAS + hqCode),
                    chemAvailability = masterDataDao.isDataAvailable(Constants.CHEMIST_MAS + hqCode),
                    stkAvailability = masterDataDao.isDataAvailable(Constants.STOCKIEST_MAS + hqCode),
                    ulDocAvailability = masterDataDao.isDataAvailable(Constants.UNLISTED_DOCTOR_MAS + hqCode),
//                        hosAvailability = masterDataDao.isDataAvailable(Constants.HOSPITAL + hqCode),
//                        cipAvailability = masterDataDao.isDataAvailable(Constants.CIP + hqCode),
                    clusterAvailability = masterDataDao.isDataAvailable(Constants.CLUSTER + hqCode);
            Log.e("DynamicActivity", "showHQ: " + docAvailability + " " + chemAvailability + " " + stkAvailability + " " + ulDocAvailability + " " + clusterAvailability);
//                if(docAvailability && chemAvailability && stkAvailability && ulDocAvailability && hosAvailability && cipAvailability && clusterAvailability){
            if (docAvailability && chemAvailability && stkAvailability && ulDocAvailability && clusterAvailability) {
                Log.d("DynamicActivity", "getHQData: Data Available");
            } else if (UtilityClass.isNetworkAvailable(DynamicActivity.this)) {
                getData(hqCode);
            } else {
                commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no_network));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData(String hqCode) {
        SynqList = SharedPref.getsyn_hqcode(DynamicActivity.this);
        SynqList.add(hqCode);
        SharedPref.setSyncHQ(DynamicActivity.this, SynqList);
        syncProgressDialog.show();
        List<MasterSyncItemModel> list = new ArrayList<>();
        list.add(new MasterSyncItemModel("Doctor", Constants.DOCTOR_MAS, "getdoctors_master", Constants.DOCTOR_MAS + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Chemist", Constants.DOCTOR_MAS, "getchemist_master", Constants.CHEMIST_MAS + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Stockiest", Constants.DOCTOR_MAS, "getstockist_master", Constants.STOCKIEST_MAS + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Unlisted Doctor", Constants.DOCTOR_MAS, "getunlisteddr_master", Constants.UNLISTED_DOCTOR_MAS + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("Hospital", 0, "Doctor", "gethospital", Constants.HOSPITAL + hqCode, 0, false));
//        list.add(new MasterSyncItemModel("CIP", 0, "Doctor", "getcip", Constants.CIP + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Cluster", Constants.DOCTOR_MAS, "getterritory", Constants.CLUSTER + hqCode, 0, false));
        list.add(new MasterSyncItemModel("Joint Work", Constants.SUBORDINATE, "getjointwork", Constants.JOINT_WORK + hqCode, 0, false));

        for (int i = 0; i < list.size(); i++) {
            syncMaster(list.get(i).getMasterOf(), list.get(i).getRemoteTableName(), list.get(i).getLocalTableKeyName(), hqCode);
        }
    }

    public void syncMaster(String masterFor, String remoteTableName, String LocalTableKeyName, String hqCode) {
        if (UtilityClass.isNetworkAvailable(DynamicActivity.this)) {
            try {
                String baseUrl = SharedPref.getBaseWebUrl(DynamicActivity.this);
                String pathUrl = SharedPref.getPhpPathUrl(DynamicActivity.this);
                String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
                apiInterface = RetrofitClient.getRetrofit(DynamicActivity.this, baseUrl + replacedUrl);

                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(DynamicActivity.this);
                jsonObject.put("tableName", remoteTableName);
                jsonObject.put("sfcode", SharedPref.getSfCode(DynamicActivity.this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(DynamicActivity.this));
                jsonObject.put("Rsf", hqCode);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/dcrmasterdata");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(DynamicActivity.this), mapString, jsonObject.toString());

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();

                            if (response.isSuccessful()) {
                                Log.e("test DA", "response : " + masterFor + " -- " + remoteTableName + " : " + Objects.requireNonNull(response.body()));
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
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            syncProgressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            syncProgressDialog.dismiss();
                            t.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                syncProgressDialog.dismiss();
                e.printStackTrace();
            }
        } else {
            commonUtilsMethods.showToastMessage(DynamicActivity.this, getString(R.string.no_network));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonAlertBox.CheckLocationStatus(DynamicActivity.this, gpsTrack);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

//    public boolean CheckStoragePermission() {
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            int image = ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES);
//            int video = ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO);
//            int audio = ContextCompat.checkSelfPermission(this, READ_MEDIA_AUDIO);
//            return image == PackageManager.PERMISSION_GRANTED && video == PackageManager.PERMISSION_GRANTED && audio == PackageManager.PERMISSION_GRANTED;
//        } else {
//            int Write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
//            int Read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
//            return Write == PackageManager.PERMISSION_GRANTED && Read == PackageManager.PERMISSION_GRANTED;
//        }
//    }
//
//    private void RequestStoragePermission() {
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if ((ActivityCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) || ActivityCompat.checkSelfPermission(this, READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_MEDIA_IMAGES)) {
//                    ActivityCompat.requestPermissions(this, new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO}, 101);
//                } else {
//                    ActivityCompat.requestPermissions(this, new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO}, 101);
//                }
//            }
//        } else {
//            if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                if ((ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE))) {
//                    ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 101);
//                } else {
//                    ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 101);
//                }
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean DontAskAgain = false;

        if (requestCode == 101) {
            for (String allowedPermissions : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, allowedPermissions)) {
                    StorageFlag++;
                } else if (PermissionChecker.checkCallingOrSelfPermission(this, allowedPermissions) != PermissionChecker.PERMISSION_GRANTED) {
                    DontAskAgain = true;
                    StorageFlag++;
                    break;
                } else {
                    Open_Storage();
                }
            }
            if ((DontAskAgain) && (StorageFlag > 1)) {
                CommonUtilsMethods.RequestGPSPermission(DynamicActivity.this, "Files");
            }
        }
        if (requestCode == 102) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            } else {
                // Permission denied, show a message to the user
                CommonUtilsMethods.RequestGPSPermission(DynamicActivity.this, "Location");
            }
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(DynamicActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void RequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(DynamicActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DynamicActivity.this, ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(DynamicActivity.this, new String[]{ACCESS_FINE_LOCATION}, 102);
            } else {
                ActivityCompat.requestPermissions(DynamicActivity.this, new String[]{ACCESS_FINE_LOCATION}, 102);
            }
        }
    }

    public boolean CheckLocPermission() {
        int FineLocation = ContextCompat.checkSelfPermission(DynamicActivity.this, ACCESS_FINE_LOCATION);
        int CoarseLocation = ContextCompat.checkSelfPermission(DynamicActivity.this, ACCESS_COARSE_LOCATION);
        return FineLocation == PackageManager.PERMISSION_GRANTED && CoarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    private void handleCancel() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        alertText.setText("");
        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
            }
        });
        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });
    }

}

