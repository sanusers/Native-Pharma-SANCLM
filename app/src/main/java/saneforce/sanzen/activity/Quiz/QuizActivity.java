package saneforce.sanzen.activity.Quiz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.Quiz.AssertDownloadAlert.AssertDownloadAdapter;
import saneforce.sanzen.activity.Quiz.AssertDownloadAlert.AssertDownloadService;
import saneforce.sanzen.activity.Quiz.AssertDownloadAlert.AssertDownloadViewModel;
import saneforce.sanzen.activity.Quiz.adapter.QuizCountAdapter;
import saneforce.sanzen.activity.Quiz.adapter.QuizQuestionAdapter;
import saneforce.sanzen.activity.Quiz.model.QuizModelClass;
import saneforce.sanzen.activity.Quiz.model.QuizOptionModelClass;
import saneforce.sanzen.activity.Quiz.model.QuizQuesNoModel;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GifView;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityQuizBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDao;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDataTable;
import saneforce.sanzen.roomdatabase.QuizOfflineTableDetails.QuizOfflineDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class QuizActivity extends AppCompatActivity {
    ActivityQuizBinding binding;
    JSONArray QuesttionjsonArray, AnswerjsonArray, quizTitleJsonArray, processUserJsonArray;
    ArrayList<QuizModelClass> mQuizList = new ArrayList<>();
    ArrayList<QuizOptionModelClass> sQuizMainAnswerList = new ArrayList<>();
    ArrayList<QuizOptionModelClass> optionList = new ArrayList<>();
    private final ArrayList<QuizQuesNoModel> quesNumberModelList = new ArrayList<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private QuizOfflineDataDao quizOfflineDataDao;
    private QuizAssertsDao quizAssertsDao;
    private int QuestionNumber = 0, noOfCorrectAnswers = 0;
    private boolean isShuffleAllowed = false, isPaused = false, isStarted = false, isQuizAvailable = false, isSubmitting = false, isBackPressed = false;
    private String noOfAttemptsAllowed = "0", timeLimit = "00:00:00", startTime = "", surveyID = "", quizCap = "", fileName = "", fileType = "";
    private CountDownTimer countDownTimer;
    QuizQuestionAdapter quizQuestionAdapter;
    QuizCountAdapter quizCountAdapter;
    private CommonUtilsMethods commonUtilsMethods;
    private long remainingTime;
    private JSONObject saveJsonObject;
    private ApiInterface apiInterface;
    public static ArrayList<String> assertsNames = new ArrayList<>();
    public static boolean isSingleAssertDownloadingStatus = false;
    private HashMap<String, String> quizStoredData = new HashMap<>();
    public static String SYNC_NEEDED = "Sync Needed";

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        quizCap = SharedPref.getQuizHeading(QuizActivity.this);
        binding.quizTitle.setText(quizCap);
        binding.welcomeTitle.setText(String.format("Welcome to %s Session", quizCap));
        commonUtilsMethods = new CommonUtilsMethods(QuizActivity.this);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        quizOfflineDataDao = roomDB.quizOfflineDataDao();
        quizAssertsDao = roomDB.quizAssertsDao();
        binding.startQuizBtn.setText(String.format("Start %s", quizCap));
       // binding.tvNoQuiz.setText(String.format("No %s Found", quizCap));
        //String quizCap = SharedPref.getQuizCap(this);
        binding.tvNoQuiz.setText(getString(R.string.no_item_found, quizCap));

        assertsNames.clear();
        isSingleAssertDownloadingStatus = false;
        boolean syncNeeded = false;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            syncNeeded = bundle.getBoolean(SYNC_NEEDED, false);
        }

        if(syncNeeded) {
            showSyncPopup();
        } else {
//        if(SharedPref.getQuizAttempts(QuizActivity.this)>0 && HomeDashBoard.selectedDate != null && SharedPref.getLastQuizSubmittedDate(QuizActivity.this).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
            getData();
//        }else {
//            callSyncAPI();
//        }
//        SharedPref.setQuizData(this, "");

            if(!SharedPref.getQuizData(this).isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(SharedPref.getQuizData(this));
                    String date = jsonObject.optString("date");
                    if(HomeDashBoard.selectedDate != null && date.equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                        String timeRemaining = jsonObject.optString("time_remaining");
                        long time = Long.parseLong(timeRemaining);
                        timeLimit = TimeUtils.getMillisToFormattedTime(time, TimeUtils.FORMAT_32);
                        JSONArray jsonArray = jsonObject.optJSONArray("Quiz_Results");
                        if(jsonArray != null && jsonArray.length()>0) {
                            JSONArray quizData = jsonArray.optJSONArray(0);
                            if(quizData != null && quizData.length()>0) {
                                quizStoredData = new HashMap<>();
                                for (int i = 0; i<quizData.length(); i++) {
                                    JSONObject data = quizData.optJSONObject(i);
                                    String questionID = data.optString("Question_Id");
                                    String inputID = data.optString("input_id");
                                    String inputText = data.optString("Input_Text");
                                    quizStoredData.put(questionID, inputText);
                                }
                            }
                        }

                        binding.rlStartQuiz.setVisibility(View.GONE);
                        binding.rlQuizMain.setVisibility(View.VISIBLE);
                        isStarted = true;
                        populateData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        binding.backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (SharedPref.getQuizNeedMandt(QuizActivity.this).equalsIgnoreCase("0")
                        && isQuizAvailable
                        && (!SharedPref.getLastQuizSubmittedDate(QuizActivity.this).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())
                        || SharedPref.getQuizAttempts(QuizActivity.this) > 0)) {
                    noBackAlert();
                } else {
                    if (isStarted) {
                        backAlert();
                    } else {
                        pauseTimer();
                        isStarted = false;
                        isBackPressed = true;
                        getOnBackPressedDispatcher().onBackPressed();
                        finish();
                    }
                }
            }
        });

        binding.btnskip.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (SharedPref.getQuizNeedMandt(QuizActivity.this).equalsIgnoreCase("0")
                        && isQuizAvailable
                        && (!SharedPref.getLastQuizSubmittedDate(QuizActivity.this).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())
                        || SharedPref.getQuizAttempts(QuizActivity.this) > 0)) {
                    noBackAlert();
                } else {
                    if (isStarted) {
                        backAlert();
                    } else {
                        pauseTimer();
                        isStarted = false;
                        isBackPressed = true;
                        getOnBackPressedDispatcher().onBackPressed();
                        finish();
                    }
                }
            }
        });

        binding.llDownloadAsserts.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                callDownloadAssertAPI();
            }
        });

        binding.btnpreview.setAlpha(0.5f);
        binding.btnpreview.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (QuestionNumber != 0) {
                    QuestionNumber = QuestionNumber - 1;
                    setQuestion(QuestionNumber);
                }
            }
        });

        binding.btnNext.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (QuestionNumber < mQuizList.size() - 1) {
                    QuestionNumber = QuestionNumber + 1;
                    setQuestion(QuestionNumber);
                }
            }
        });

        binding.startQuizBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                binding.rlStartQuiz.setVisibility(View.GONE);
                binding.rlQuizMain.setVisibility(View.VISIBLE);
                isStarted = true;
                populateData();
            }
        });

        binding.btnSave.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                validate();
            }
        });

    }

    private void callDownloadAssertAPI() {
        assertsNames.clear();
        isSingleAssertDownloadingStatus = false;
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Name", fileName);
            jsonArray.put(jsonObject);
            QuizAssertsDataTable quizAssertsDataTable = quizAssertsDao.getQuizAssertsDataByName(fileName);
            if(quizAssertsDataTable != null && quizAssertsDataTable.getDownloadingStatus().equalsIgnoreCase("3")) {
                if(!isFinishing()) {
                    isStarted = true;
                    viewDownloadedQuizAssert();
                }
            }else {
                try {
                    quizAssertsDao.deleteAllData();
                    File file = new File(this.getExternalFilesDir(null) + "/QuizAsserts");
                    if(file.exists()) {
                        cleanDirectory(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                insertQuizAsserts(jsonArray);
                quizAssertAlertBox(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSyncPopup() {
        Dialog dialogBackConfirmation = new Dialog(QuizActivity.this);
        dialogBackConfirmation.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogBackConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBackConfirmation.setCancelable(false);
        ImageView iv_close = dialogBackConfirmation.findViewById(R.id.img_close);
        EditText ed_remarks = dialogBackConfirmation.findViewById(R.id.ed_remark);
        TextView heading = dialogBackConfirmation.findViewById(R.id.tv_head);
        TextView content = dialogBackConfirmation.findViewById(R.id.content);
        Button btn_clear = dialogBackConfirmation.findViewById(R.id.btn_clear);
        Button btn_save = dialogBackConfirmation.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(R.string.try_again);
        btn_clear.setText(getString(R.string.close));
        iv_close.setVisibility(View.GONE);
        if(SharedPref.getDcrSequential(QuizActivity.this).equalsIgnoreCase("0")) {
            btn_clear.setVisibility(View.GONE);
        }else {
            btn_clear.setVisibility(View.VISIBLE);
        }
        content.setText(String.format("%s is Mandatory.\nKindly Sync by clicking \"%s\"", quizCap, getString(R.string.try_again)));
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                callSyncAPI();
                dialogBackConfirmation.dismiss();
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                SharedPref.setSelectedDateCal(QuizActivity.this, "");
                dialogBackConfirmation.dismiss();
                finish();
            }
        });
        dialogBackConfirmation.show();
    }

    public void insertQuizAsserts(JSONArray jsonArray) {
        try {
            List<String> mList = new ArrayList<>();
            List<String> nList = quizAssertsDao.getAllQuizAssertNames();
            if(jsonArray.length()>0) {
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String FilePath = jsonObject.optString("Name");
                    String name = jsonObject.optString("Name");
                    mList.add(name);
                    if(quizAssertsDao.getQuizAssertName(name) != null && quizAssertsDao.getQuizAssertName(name).equalsIgnoreCase(FilePath)) {
                        quizAssertsDao.insert(new QuizAssertsDataTable(FilePath, "", "1", "0", "1", String.valueOf(i)));
                    }else {
                        quizAssertsDao.saveQuizAssertData(new QuizAssertsDataTable(FilePath, "", "1", "0", "1", String.valueOf(i)));
                    }
                }
                if(!nList.isEmpty()) {
                    for (int j = 0; j<nList.size(); j++) {
                        if(!mList.contains(nList.get(j))) {
                            quizAssertsDao.deleteQuizAssertByName(nList.get(j));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("QuizActivity", "insertQuizAsserts: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void quizAssertAlertBox(boolean servesFlag) {
        quizAssertsDao.setChangeStatus("1", "0");
        assertsNames.clear();
        if(servesFlag) {
            SharedPref.putQuizAssertDownloadingStatus(QuizActivity.this, false);
            Intent intent = new Intent(QuizActivity.this, AssertDownloadService.class);
            stopService(intent);

            Intent startIntent = new Intent(getApplicationContext(), AssertDownloadService.class);
            startService(startIntent);
        }
        isSingleAssertDownloadingStatus = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.slide_downloader_alert_box, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyelerview123);
        TextView txt_alert_title = dialogView.findViewById(R.id.alert_title);
        TextView txt_downloadCount = dialogView.findViewById(R.id.txt_downloadcount);
        TextView txt_total = dialogView.findViewById(R.id.txt_totaldownloadcount);
        ImageView cancel_img = dialogView.findViewById(R.id.cancel_img);
        cancel_img.setVisibility(View.GONE);
        txt_alert_title.setText(quizCap + " Asserts Downloader");
        AssertDownloadAdapter adapter = new AssertDownloadAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        if(!isFinishing()) {
            dialog.show();
        }

        cancel_img.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });

        AssertDownloadViewModel assertDownloadViewModel = new ViewModelProvider(this).get(AssertDownloadViewModel.class);
        assertDownloadViewModel.getAllQuizAsserts().observe(this, quizAsserts -> {
            Collections.sort(quizAsserts, Comparator.comparingInt(s -> Integer.parseInt(s.getListAssertPosition())));
            adapter.setQuizAsserts(quizAsserts);
        });

        assertDownloadViewModel.getDownloadingCount().observe(this, integer -> txt_downloadCount.setText(integer + " / "));

        txt_total.setText(String.valueOf(quizAssertsDao.getTotalQuizAssertCount()));

        assertDownloadViewModel.getCountOfDownloadingProcessDone().observe(this, integer -> {
            if(integer == quizAssertsDao.getTotalQuizAssertCount() && integer != 0) {
                SharedPref.putQuizAssertDownloadingStatus(QuizActivity.this, true);
                if(isSingleAssertDownloadingStatus) {
                    isSingleAssertDownloadingStatus = false;
                    commonUtilsMethods.showToastMessage(this, quizCap + getString(R.string.asserts_updated), true);
                }else {
                    commonUtilsMethods.showToastMessage(this, quizCap + getString(R.string.asserts_downloading_completed), true);
                }
                dialog.dismiss();
                if(!isFinishing()) {
                    isStarted = true;
                    viewDownloadedQuizAssert();
                }
            }else {
                SharedPref.putQuizAssertDownloadingStatus(QuizActivity.this, false);
            }
        });
    }

    private void viewDownloadedQuizAssert() {
        Intent intent = new Intent(QuizActivity.this, QuizAssertViewActivity.class);
        intent.putExtra(QuizAssertViewActivity.FILE_NAME, fileName);
        intent.putExtra(QuizAssertViewActivity.FILE_TYPE, fileType);
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("SuspiciousIndentation")
        @Override
        public void onActivityResult(ActivityResult result) {
            try {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    binding.rlStartQuiz.setVisibility(View.GONE);
                    binding.rlQuizMain.setVisibility(View.VISIBLE);
                    populateData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    private void noBackAlert() {
        Dialog dialogBackConfirmation = new Dialog(QuizActivity.this);
        dialogBackConfirmation.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogBackConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBackConfirmation.setCancelable(false);
        ImageView iv_close = dialogBackConfirmation.findViewById(R.id.img_close);
        EditText ed_remarks = dialogBackConfirmation.findViewById(R.id.ed_remark);
        TextView heading = dialogBackConfirmation.findViewById(R.id.tv_head);
        TextView content = dialogBackConfirmation.findViewById(R.id.content);
        Button btn_clear = dialogBackConfirmation.findViewById(R.id.btn_clear);
        Button btn_save = dialogBackConfirmation.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(getString(R.string.ok));
        btn_clear.setText(getString(R.string.no));
        btn_clear.setVisibility(View.GONE);
        content.setText(quizCap + " is Mandatory. Cannot go back");
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogBackConfirmation.dismiss();
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogBackConfirmation.dismiss();
            }
        });
        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogBackConfirmation.dismiss();
            }
        });
        dialogBackConfirmation.show();
    }

    private void backAlert() {
        Dialog dialogBackConfirmation = new Dialog(QuizActivity.this);
        dialogBackConfirmation.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogBackConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBackConfirmation.setCancelable(false);
        ImageView iv_close = dialogBackConfirmation.findViewById(R.id.img_close);
        EditText ed_remarks = dialogBackConfirmation.findViewById(R.id.ed_remark);
        TextView heading = dialogBackConfirmation.findViewById(R.id.tv_head);
        TextView content = dialogBackConfirmation.findViewById(R.id.content);
        Button btn_clear = dialogBackConfirmation.findViewById(R.id.btn_clear);
        Button btn_save = dialogBackConfirmation.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_clear.setVisibility(View.GONE);
        btn_save.setText(getString(R.string.continuee));
//        btn_clear.setText(getString(R.string.no));
        content.setText(String.format("%s started, Cannot go back", quizCap));
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
//            pauseTimer();
//            isStarted = false;
                dialogBackConfirmation.dismiss();
//            getOnBackPressedDispatcher().onBackPressed();
//            finish();
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogBackConfirmation.dismiss();
            }
        });
        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogBackConfirmation.dismiss();
            }
        });
        dialogBackConfirmation.show();
    }

    private void validate() {
        if(sQuizMainAnswerList.size() == quesNumberModelList.size()) {
//            pauseTimer();
            if(UtilityClass.isNetworkAvailable(QuizActivity.this)) {
                showSubmitAlert();
                isStarted = false;
            }else {
                commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.no_network), true);
            }
        }else {
            try {
                List<Integer> unAttendedQuestions = IntStream.rangeClosed(1, quesNumberModelList.size())
                        .boxed()
                        .collect(Collectors.toList());
                for (QuizOptionModelClass quizOptionModelClass : sQuizMainAnswerList) {
                    if(unAttendedQuestions.contains(quizOptionModelClass.getQuestionId() + 1)) {
                        int index = unAttendedQuestions.indexOf(quizOptionModelClass.getQuestionId() + 1);
                        unAttendedQuestions.remove(index);
                    }
                }
                commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.please_complete) + Arrays.toString(unAttendedQuestions.toArray()).replaceAll("\\[", "").replaceAll("]", ""), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showSubmitAlert() {
        Dialog dialogOptionSelection = new Dialog(this);
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
        btn_save.setText(getString(R.string.submit));
        btn_clear.setText(getString(R.string.cancel));
        content.setText(getString(R.string.are_you_sure) + " Want to Submit");
        String remTime = TimeUtils.getMillisToFormattedTime(remainingTime, TimeUtils.FORMAT_32);
//        if(remainingTime > 0) {
//            content.setText(getString(R.string.are_you_sure) + " Want to Submit \nYou have Remaining Time : " + remTime);
//        }
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                submitQuiz();
                dialogOptionSelection.dismiss();
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogOptionSelection.dismiss();
//            resumeTimer();
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

    private void submitQuiz() {
        if(UtilityClass.isNetworkAvailable(QuizActivity.this)) {
            isSubmitting = true;
            pauseTimer();
            createJson();
            callSaveAPI();
//            setScoreView();
        }else {
            commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.no_network), true);
            showSubmitPopup();
        }
//        quizOfflineDataDao.insert(new QuizOfflineDataTable(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32), saveJsonObject.toString(), 0, Constants.WAITING_FOR_SYNC));
    }

    private void setScoreView() {
        int totalQuestions = quesNumberModelList.size();
        int badThreshold = (int) (totalQuestions * 0.34);
        int okThreshold = (int) (totalQuestions * 0.67);

//        binding.score.setText(String.format("%d out of %d", noOfCorrectAnswers, totalQuestions));
//        binding.rlScore.setVisibility(View.VISIBLE);
//        binding.rlQuizMain.setVisibility(View.GONE);

        Dialog quizResultDialog = new Dialog(QuizActivity.this);
        quizResultDialog.setContentView(R.layout.popup_quiz_result);
        Objects.requireNonNull(quizResultDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        quizResultDialog.setCancelable(false);
        ImageView iv_close = quizResultDialog.findViewById(R.id.close_img);
        TextView score = quizResultDialog.findViewById(R.id.tv_score);
        TextView total = quizResultDialog.findViewById(R.id.tv_total);
        Button retry = quizResultDialog.findViewById(R.id.retry);
        GifView gifView = quizResultDialog.findViewById(R.id.completed_anim);

        if(noOfCorrectAnswers<=badThreshold) {
            gifView.setGifResource(R.raw.poor);
        }else if(noOfCorrectAnswers<=okThreshold) {
            gifView.setGifResource(R.raw.medium);
        }else {
            gifView.setGifResource(R.raw.good);
        }

        score.setText(String.valueOf(noOfCorrectAnswers));
        total.setText(String.valueOf(totalQuestions));

        if(SharedPref.getQuizAttempts(QuizActivity.this) == 0) {
            iv_close.setVisibility(View.VISIBLE);
            retry.setVisibility(View.GONE);
        }else {
            iv_close.setVisibility(View.GONE);
            retry.setVisibility(View.VISIBLE);
        }

        retry.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                getData();
                quizResultDialog.dismiss();
            }
        });

        iv_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                try {
                    binding.rlStartQuiz.setVisibility(View.VISIBLE);
                    binding.rlQuizMain.setVisibility(View.GONE);
                    masterDataDao.updateData(Constants.QUIZ, "[]");
                    quizAssertsDao.deleteAllData();
                    File file = new File(QuizActivity.this.getExternalFilesDir(null) + "/QuizAsserts");
                    if (file.exists()) {
                        cleanDirectory(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                quizResultDialog.dismiss();
                finish();
            }
        });

        if(!isFinishing()) {
            quizResultDialog.show();
        }
    }

    private void createJson() {
        try {
            noOfCorrectAnswers = 0;
            saveJsonObject = CommonUtilsMethods.CommonObjectParameter(QuizActivity.this);
            saveJsonObject.put("tableName", "Quiz_Results");
            saveJsonObject.put("sfcode", SharedPref.getSfCode(this));
            saveJsonObject.put("division_code", SharedPref.getDivisionCode(QuizActivity.this));

            JSONArray jsonArray = new JSONArray(), quizResults = new JSONArray();
            for (int i = 0; i<sQuizMainAnswerList.size(); i++) {
                QuizOptionModelClass quizOptionModelClass = null;
                for (int j = 0; j<sQuizMainAnswerList.size(); j++) {
                    if(sQuizMainAnswerList.get(j).getQuestionId() == i) {
                        quizOptionModelClass = sQuizMainAnswerList.get(j);
                        break;
                    }
                }
                QuizModelClass quizModelClass = mQuizList.get(i);

                String correctAns = "0";
                if(quizOptionModelClass != null && quizOptionModelClass.getSelctionCode() != null && quizOptionModelClass.getSelctionCode().equalsIgnoreCase(quizModelClass.getAnswerCode())) {
                    Log.d("QUIZ", "createJson: correct Ans -> " + quizOptionModelClass.getSelctionCode() + " = " + quizOptionModelClass.getSelectedOption());
                    correctAns = "1";
                    noOfCorrectAnswers++;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("input_id", quizOptionModelClass.getSelctionCode());
                jsonObject.put("Input_Text", quizOptionModelClass.getSelectedOption());
                jsonObject.put("Question_Id", quizModelClass.getQuestionCode());
                jsonObject.put("Correct_Ans", correctAns);
                jsonArray.put(jsonObject);
            }
            quizResults.put(jsonArray);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("start", startTime);
            jsonObject.put("end", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
            jsonObject.put("NoOfAttempts", noOfAttemptsAllowed);
            jsonObject.put("survey_id", surveyID);
            jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            quizResults.put(jsonArray);

            jsonObject = new JSONObject();
            jsonObject.put("start", startTime);
            jsonObject.put("end", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1));
            jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            quizResults.put(jsonArray);

            saveJsonObject.put("Quiz_Results", quizResults);
            Log.v("Quiz json", "createJson: " + saveJsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callSyncAPI() {
        if(UtilityClass.isNetworkAvailable(QuizActivity.this)) {
            binding.tvProgressTitle.setText("Syncing Quiz...");
            binding.flProgress.setVisibility(View.VISIBLE);
            try {
                apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                jsonObject.put("tableName", "getquiz");
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_22, HomeDashBoard.selectedDate.toString()));
                Log.i("QUIZ", "quiz: json -- " + jsonObject);
                Map<String, String> qry = new HashMap<>();
                qry.put("axn", "table/additionaldcrmasterdata");
                Call<JsonElement> quiz = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), qry, jsonObject.toString());
                if(quiz != null) {
                    quiz.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> quiz, @NonNull Response<JsonElement> response) {
                            binding.flProgress.setVisibility(View.GONE);
                            if(response.isSuccessful()) {
                                Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                try {
                                    String responseData = response.body().toString();
                                    if(!responseData.equalsIgnoreCase("[]")) {
                                        JSONObject object = new JSONObject(responseData);
                                        JSONArray jsonArray = new JSONArray();
                                        jsonArray.put(object);
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.QUIZ, jsonArray.toString(), 2));
                                        SharedPref.setQuizAvailableDate(QuizActivity.this, HomeDashBoard.selectedDate.toString());
                                    }else {
                                        masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.QUIZ, "[]", 2));
                                    }
                                    getData();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showSyncPopup();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> quiz, @NonNull Throwable t) {
                            t.printStackTrace();
                            binding.flProgress.setVisibility(View.GONE);
                            commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.poor_connection), true);
                            showSyncPopup();
                        }
                    });
                }
            } catch (Exception e) {
                binding.flProgress.setVisibility(View.GONE);
                e.printStackTrace();
                showSyncPopup();
            }
        }else {
            commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.no_network), true);
            showSyncPopup();
        }
    }

    private void callSaveAPI() {
        if(UtilityClass.isNetworkAvailable(QuizActivity.this)) {
            binding.tvProgressTitle.setText("Submitting Quiz...");
            binding.flProgress.setVisibility(View.VISIBLE);
            try {
                apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
                Map<String, String> qry = new HashMap<>();
                qry.put("axn", "result/quiz");
                Call<JsonElement> quiz = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), qry, saveJsonObject.toString());
                if(quiz != null) {
                    quiz.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> quiz, @NonNull Response<JsonElement> response) {
                            binding.flProgress.setVisibility(View.GONE);
                            if(response.isSuccessful()) {
                                if(response.body() != null) {
                                    Log.e("test", "response : " + " : " + response.body());
                                }
                                commonUtilsMethods.showToastMessage(QuizActivity.this, quizCap + getString(R.string.submitted_successfully), true);
                                int attempts = SharedPref.getQuizAttempts(QuizActivity.this);
//                            if(attempts == 1) {
                                SharedPref.setLastQuizSubmittedDate(QuizActivity.this, HomeDashBoard.selectedDate.toString());
//                            }
                                if(attempts>0) {
                                    attempts--;
                                }
                                SharedPref.setQuizAttempts(QuizActivity.this, attempts);
                                setScoreView();
                                SharedPref.setQuizData(QuizActivity.this, "");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> quiz, @NonNull Throwable t) {
                            commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.poor_connection), true);
                            binding.flProgress.setVisibility(View.GONE);
                            showSubmitPopup();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                binding.flProgress.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.poor_connection), true);
                showSubmitPopup();
            }
        } else {
            binding.flProgress.setVisibility(View.GONE);
            commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.no_network), true);
            showSubmitPopup();
        }
    }

    private void showSubmitPopup() {
        Dialog dialogBackConfirmation = new Dialog(QuizActivity.this);
        dialogBackConfirmation.setContentView(R.layout.popup_remarks);
        Objects.requireNonNull(dialogBackConfirmation.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBackConfirmation.setCancelable(false);
        ImageView iv_close = dialogBackConfirmation.findViewById(R.id.img_close);
        EditText ed_remarks = dialogBackConfirmation.findViewById(R.id.ed_remark);
        TextView heading = dialogBackConfirmation.findViewById(R.id.tv_head);
        TextView content = dialogBackConfirmation.findViewById(R.id.content);
        Button btn_clear = dialogBackConfirmation.findViewById(R.id.btn_clear);
        Button btn_save = dialogBackConfirmation.findViewById(R.id.btn_save);
        heading.setText(R.string.alert);
        btn_save.setText(R.string.try_again);
        btn_clear.setText(getString(R.string.no));
        iv_close.setVisibility(View.GONE);
        btn_clear.setVisibility(View.GONE);
        content.setText(String.format("%s submit failed.\n\"%s\" to submit", quizCap, getString(R.string.try_again)));
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                callSaveAPI();
                dialogBackConfirmation.dismiss();
            }
        });
        btn_clear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogBackConfirmation.dismiss();
            }
        });
        dialogBackConfirmation.show();
    }

    private void getData() {
        binding.rlStartQuiz.setVisibility(View.VISIBLE);
        binding.rlQuizMain.setVisibility(View.GONE);
        mQuizList.clear();
        try {
            JSONArray quizdata = masterDataDao.getMasterDataTableOrNew(Constants.QUIZ).getMasterSyncDataJsonArray();
            if(quizdata.length()>0) {
                isQuizAvailable = true;
                binding.rlStartQuiz.setVisibility(View.VISIBLE);
                binding.constraintNoData.setVisibility(View.GONE);
                for (int i = 0; i<quizdata.length(); i++) {
                    JSONObject jsonObject = quizdata.getJSONObject(i);
                    String quizTitle = jsonObject.getString("quiztitle");
                    String processUser = jsonObject.getString("processUser");
                    String questionText = jsonObject.getString("questions");
                    String answerText = jsonObject.getString("answers");
                    quizTitleJsonArray = new JSONArray(quizTitle);
                    processUserJsonArray = new JSONArray(processUser);
                    QuesttionjsonArray = new JSONArray(questionText);
                    AnswerjsonArray = new JSONArray(answerText);
                }
                setupQuizWelcome();
            }else {
                isQuizAvailable = false;
                quizTitleJsonArray = null;
                processUserJsonArray = null;
                QuesttionjsonArray = null;
                AnswerjsonArray = null;
                binding.rlStartQuiz.setVisibility(View.GONE);
                binding.constraintNoData.setVisibility(View.VISIBLE);
            }
        } catch (Exception a) {
            Log.v("error", "----" + a.getMessage());
            a.printStackTrace();
        }
    }

    private void setupQuizWelcome() {
        int noOfQuestions = 0;
        if(QuesttionjsonArray != null) {
            noOfQuestions = QuesttionjsonArray.length();
        }
        try {
            JSONObject jsonObject = new JSONObject();
            if(processUserJsonArray != null) {
                jsonObject = processUserJsonArray.optJSONObject(0);
                if(jsonObject != null) {
                    noOfAttemptsAllowed = jsonObject.optString("NoOfAttempts");
                    String type = jsonObject.getString("type");
                    isShuffleAllowed = type.equalsIgnoreCase("Suffle");
                    timeLimit = jsonObject.optString("timelimit");
                    String[] timeSplit = timeLimit.split(":");
                    if(timeSplit.length == 2) {
                        timeLimit += ":00";
                    }
                    remainingTime = TimeUtils.getMilliSeconds(TimeUtils.FORMAT_32, timeLimit);
                }
            }
            if(quizTitleJsonArray != null) {
                jsonObject = quizTitleJsonArray.optJSONObject(0);
                if(jsonObject != null) {
                    surveyID = jsonObject.optString("survey_id");
                    fileName = jsonObject.optString("FileName");
                    fileType = jsonObject.optString("mimetype");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int numberOfAttempts = 0;
        try {
            numberOfAttempts = Integer.parseInt(noOfAttemptsAllowed);
            binding.noOfAttemptsAllowed.setText(String.valueOf(numberOfAttempts));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(SharedPref.getQuizAttempts(QuizActivity.this)<1) {
            SharedPref.setQuizAttempts(QuizActivity.this, numberOfAttempts);
        }else {
            noOfAttemptsAllowed = String.valueOf(SharedPref.getQuizAttempts(QuizActivity.this));
        }
        binding.noOfQuestions.setText(String.valueOf(noOfQuestions));
        binding.noOfAttemptsLeft.setText(noOfAttemptsAllowed);

        binding.totalTime.setText(timeLimit);
        if(fileName.isEmpty()) {
            binding.llDownloadAsserts.setVisibility(View.GONE);
        }else {
            binding.llDownloadAsserts.setVisibility(View.VISIBLE);
        }
    }

    private void populateData() {
        mQuizList.clear();
        sQuizMainAnswerList.clear();
        try {
            Map<String, ArrayList<JSONObject>> optionListMap = new HashMap<>();
            for (int i = 0; i<AnswerjsonArray.length(); i++) {
                JSONObject jsonObject = AnswerjsonArray.getJSONObject(i);
                String questionId = jsonObject.getString("Question_Id");

                if(optionListMap.containsKey(questionId)) {
                    optionListMap.get(questionId).add(jsonObject);
                }else {
                    ArrayList<JSONObject> newList = new ArrayList<>();
                    newList.add(jsonObject);
                    optionListMap.put(questionId, newList);
                }
            }

            if(QuesttionjsonArray.length()>0) {
                for (int j = 0; j<QuesttionjsonArray.length(); j++) {
                    String optionname = "", optionCode = "", answercode = "";
                    JSONObject jsonObject = QuesttionjsonArray.getJSONObject(j);
                    ArrayList<JSONObject> jsonObject1 = optionListMap.get(jsonObject.getString("Question_Id"));
                    if(jsonObject1 != null) {
                        for (JSONObject obj : jsonObject1) {
                            optionname = optionname + obj.getString("Input_Text") + "^^";
                            optionCode = optionCode + obj.getString("input_id") + "^^";
                            if(obj.getString("Correct_Ans").equalsIgnoreCase("1")) {
                                answercode = obj.getString("input_id");
                            }
                        }
                    }
                    mQuizList.add(new QuizModelClass(jsonObject.getString("Question_Text"), jsonObject.getString("Question_Id"), optionname, optionCode, answercode, "", ""));
                }
                if(isShuffleAllowed) {
                    shuffle(mQuizList);
                }
            }
            if(!mQuizList.isEmpty()) {
                startTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1);
                startTimer();
                populateQuestionNumber();
                setQuestion(0);
            }else {
                commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.sync) + quizCap + getString(R.string.from_master_sync), true);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shuffle(List<QuizModelClass> list) {
        Random random = new Random();
        for (int i = list.size() - 1; i>0; i--) {
            int j = random.nextInt(i + 1);
            QuizModelClass temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    public void setQuestion(int position) {

        if(QuestionNumber == 0) {
            binding.btnpreview.setAlpha(0.5f);
            binding.btnNext.setAlpha(1f);
        }else if(QuestionNumber == mQuizList.size() - 1) {
            binding.btnpreview.setAlpha(1f);
            binding.btnNext.setAlpha(0.5f);
        }else {
            binding.btnpreview.setAlpha(1f);
            binding.btnNext.setAlpha(1f);
        }

        if(position<mQuizList.size()) {
            binding.txtQuestonName.setText(mQuizList.get(position).getQuestionName());
            binding.txtQuestonNo.setText("Question : " + (position + 1));
            String[] optionSplit = mQuizList.get(position).getOption().split("\\^\\^");
            String[] optionSplitIds = mQuizList.get(position).getOptionCode().split("\\^\\^");

            quizCountAdapter.setSelected(position);

            optionList.clear();
            String SelctionName = "";

            if(!sQuizMainAnswerList.isEmpty()) {
                for (QuizOptionModelClass list : sQuizMainAnswerList) {
                    if(list.getQuestionId() == position) {
                        SelctionName = list.getSelectedOption();
                    }
                }
            }

            for (int i = 0; i<optionSplit.length; i++) {
                String optionName = optionSplit[i], optionCode = optionSplitIds[i], questionCode = mQuizList.get(position).getQuestionCode();
                if(!SelctionName.equalsIgnoreCase("") && SelctionName.equalsIgnoreCase(optionName)) {
                    optionList.add(new QuizOptionModelClass(true, optionName, optionCode));
                } else if(quizStoredData != null && quizStoredData.containsKey(questionCode)
                        && quizStoredData.get(questionCode) != null && quizStoredData.get(questionCode).equalsIgnoreCase(optionName)) {
                    optionList.add(new QuizOptionModelClass(true, optionName, optionCode));
                    sQuizMainAnswerList.add(new QuizOptionModelClass(position, optionSplit, optionSplitIds, optionName, optionCode));
                } else {
                    optionList.add(new QuizOptionModelClass(false, optionName, optionCode));
                }
            }

            quizQuestionAdapter = new QuizQuestionAdapter(optionList, new OptionChooseInterface() {
                @Override
                public void classSelceted(QuizOptionModelClass classGroup) {

                    for (int j = 0; j<sQuizMainAnswerList.size(); j++) {
                        if(sQuizMainAnswerList.get(j).getQuestionId() == position) {
                            sQuizMainAnswerList.remove(j);
                        }
                    }
                    sQuizMainAnswerList.add(new QuizOptionModelClass(position, optionSplit, optionSplitIds, classGroup.getOptionName(), classGroup.getOptionCode()));
                    optionList.clear();
                    for (int i = 0; i<optionSplit.length; i++) {
                        String optionName = optionSplit[i], optionCode = optionSplitIds[i];
                        if(!classGroup.getOptionName().equalsIgnoreCase("") && classGroup.getOptionName().equalsIgnoreCase(optionName)) {
                            optionList.add(new QuizOptionModelClass(true, optionName, optionCode));
                            quizCountAdapter.setFinished(position);
                        }else {
                            optionList.add(new QuizOptionModelClass(false, optionName, optionCode));
                        }
                    }
                    quizQuestionAdapter.notifyDataSetChanged();
                }

                @Override
                public void classUnselected(QuizOptionModelClass classGroup) {
                    for (int j = 0; j<sQuizMainAnswerList.size(); j++) {
                        if(sQuizMainAnswerList.get(j).getQuestionId() == position) {
                            sQuizMainAnswerList.remove(j);
                        }
                    }
                }
            });
            binding.recyelerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            binding.recyelerview.setAdapter(quizQuestionAdapter);
        }
    }

    private void populateQuestionNumber() {
        quesNumberModelList.clear();
        QuestionNumber = 0;
        binding.btnpreview.setAlpha(0.5f);
        binding.btnNext.setAlpha(1f);
        for (int i = 0; i<mQuizList.size(); i++) {
            String questionCode = mQuizList.get(i).getQuestionCode();
            if(i == 0) {
                if(quizStoredData != null && quizStoredData.containsKey(questionCode)){
                    quesNumberModelList.add(new QuizQuesNoModel(i + 1, true, true));
                }else {
                    quesNumberModelList.add(new QuizQuesNoModel(i + 1, true, false));
                }
            }else {
                if(quizStoredData != null && quizStoredData.containsKey(questionCode)){
                    quesNumberModelList.add(new QuizQuesNoModel(i + 1, false, true));
                } else {
                    quesNumberModelList.add(new QuizQuesNoModel(i + 1, false, false));
                }
            }
        }
        quizCountAdapter = new QuizCountAdapter(quesNumberModelList, this, questionClickListener);
        binding.skRecylerview.setLayoutManager(new GridLayoutManager(this, 5));
        binding.skRecylerview.setAdapter(quizCountAdapter);
    }

    private final QuizCountAdapter.QuestionClickListener questionClickListener = quizQuesNoModel -> {
        QuestionNumber = quizQuesNoModel.getQuestionNumber() - 1;
        setQuestion(quizQuesNoModel.getQuestionNumber() - 1);
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void startTimer() {
        try {
            countDownTimer = new CountDownTimer(TimeUtils.getMilliSeconds(TimeUtils.FORMAT_32, timeLimit), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String timeLeftFormatted = TimeUtils.getMillisToFormattedTime(millisUntilFinished, TimeUtils.FORMAT_32);
                    binding.txtRemainigtime.setText(timeLeftFormatted);
                    remainingTime = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    binding.txtRemainigtime.setText("Time's up!");
                    submitQuiz();
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            commonUtilsMethods.showToastMessage(QuizActivity.this, getString(R.string.please_try_after_sometime), true);
        }
    }

    private void pauseTimer() {
        isPaused = true;
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void resumeTimer() {
        isPaused = false;
        try {
            countDownTimer = new CountDownTimer(remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String timeLeftFormatted = TimeUtils.getMillisToFormattedTime(millisUntilFinished, TimeUtils.FORMAT_32);
                    binding.txtRemainigtime.setText(timeLeftFormatted);
                    remainingTime = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    binding.txtRemainigtime.setText("Time's up!");
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanDirectory(File directory) {
        try {
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            System.err.println("Error cleaning directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!isSubmitting && !isBackPressed && isStarted && HomeDashBoard.selectedDate != null) {
            try {
                createJson();
                saveJsonObject.put("date", HomeDashBoard.selectedDate.toString());
                saveJsonObject.put("time_remaining", remainingTime);
                SharedPref.setQuizData(this, saveJsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
