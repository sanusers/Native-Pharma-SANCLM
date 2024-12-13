package saneforce.sanzen.activity.Quiz;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.Quiz.adapter.QuizCountAdapter;
import saneforce.sanzen.activity.Quiz.adapter.QuizQuestionAdapter;
import saneforce.sanzen.activity.Quiz.model.QuizModelClass;
import saneforce.sanzen.activity.Quiz.model.QuizOptionModelClass;
import saneforce.sanzen.activity.Quiz.model.QuizQuesNoModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityQuizBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.QuizOfflineTableDetails.QuizOfflineDataDao;
import saneforce.sanzen.roomdatabase.QuizOfflineTableDetails.QuizOfflineDataTable;
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
    private int QuestionNumber = 0;
    private boolean isShuffleAllowed = false, isPaused = false;
    private String noOfAttemptsAllowed = "0", timeLimit ="00:00:00", startTime = "", surveyID = "";
    private CountDownTimer countDownTimer;
    QuizQuestionAdapter quizQuestionAdapter;
    QuizCountAdapter quizCountAdapter;
    private CommonUtilsMethods commonUtilsMethods;
    private long remainingTime;
    private JSONObject saveJsonObject;

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

        commonUtilsMethods = new CommonUtilsMethods(QuizActivity.this);

        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        quizOfflineDataDao = roomDB.quizOfflineDataDao();
        getData();
        setupQuizWelcome();

        binding.backArrow.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnpreview.setAlpha(0.5f);
        binding.btnpreview.setOnClickListener(view -> {
            if (QuestionNumber != 0) {
                QuestionNumber = QuestionNumber - 1;
                setQuestion(QuestionNumber);
            }
            if(QuestionNumber == 0) {
                binding.btnpreview.setAlpha(0.5f);
                binding.btnNext.setAlpha(1f);
            } else {
                binding.btnpreview.setAlpha(1f);
                binding.btnNext.setAlpha(1f);
            }
        });

        binding.btnNext.setOnClickListener(view -> {
            if (QuestionNumber < mQuizList.size() - 1) {
                QuestionNumber = QuestionNumber + 1;
                setQuestion(QuestionNumber);
            }
            if(QuestionNumber == mQuizList.size() - 1) {
                binding.btnpreview.setAlpha(1f);
                binding.btnNext.setAlpha(0.5f);
            } else {
                binding.btnpreview.setAlpha(1f);
                binding.btnNext.setAlpha(1f);
            }
        });

        binding.startQuizBtn.setOnClickListener(view -> {
            binding.rlStartQuiz.setVisibility(View.GONE);
            binding.rlQuizMain.setVisibility(View.VISIBLE);
            populateData();
        });

        binding.btnSave.setOnClickListener(view -> {
            validated();
        });

    }

    private void validated() {
        if(sQuizMainAnswerList.size() == quesNumberModelList.size()) {
            pauseTimer();
            showSubmitAlert();
        }else{
            commonUtilsMethods.showToastMessage(QuizActivity.this, "Not Completed");
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
        if(remainingTime > 0) {
            content.setText(getString(R.string.are_you_sure) + " Want to Submit \nYou have Remaining Time : " + remTime);
        }
        content.setVisibility(View.VISIBLE);
        ed_remarks.setVisibility(View.INVISIBLE);
        btn_save.setOnClickListener(view -> {
            dialogOptionSelection.dismiss();
            submitQuiz();
        });
        btn_clear.setOnClickListener(view -> {
            dialogOptionSelection.dismiss();
            resumeTimer();
        });
        iv_close.setOnClickListener(view -> dialogOptionSelection.dismiss());
        dialogOptionSelection.show();
    }

    private void submitQuiz() {
        pauseTimer();
        createJson();
        quizOfflineDataDao.insert(new QuizOfflineDataTable(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4), TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32), saveJsonObject.toString(), 0, Constants.WAITING_FOR_SYNC));
        finish();
    }

    private void createJson() {
        try {
            saveJsonObject = CommonUtilsMethods.CommonObjectParameter(QuizActivity.this);
            saveJsonObject.put("tableName", "Quiz_Results");
            saveJsonObject.put("division_code", SharedPref.getDivisionCode(QuizActivity.this));

            JSONArray jsonArray = new JSONArray(), quizResults = new JSONArray();
            for (int i = 0; i<sQuizMainAnswerList.size(); i++) {
                QuizOptionModelClass quizOptionModelClass = null;
                for (int j = 0; j<sQuizMainAnswerList.size(); j++) {
                    if(sQuizMainAnswerList.get(j).getQuestionId() == i){
                        quizOptionModelClass = sQuizMainAnswerList.get(j);
                        break;
                    }
                }
                QuizModelClass quizModelClass = mQuizList.get(i);

                String correctAns = "0";
                if(quizOptionModelClass != null && quizOptionModelClass.getSelctionCode() != null && quizOptionModelClass.getSelctionCode().equalsIgnoreCase(quizModelClass.getAnswerCode())){
                    Log.d("QUIZ", "createJson: correct Ans -> " + quizOptionModelClass.getSelctionCode() + " = " + quizOptionModelClass.getSelectedOption());
                    correctAns = "1";
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

    private void getData() {
        mQuizList.clear();
        try {
            JSONArray quizdata = masterDataDao.getMasterDataTableOrNew(Constants.QUIZ).getMasterSyncDataJsonArray();
            if (quizdata.length() > 0) {
                for (int i = 0; i < quizdata.length(); i++) {
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
            } else {
                quizTitleJsonArray = null;
                processUserJsonArray = null;
                QuesttionjsonArray = null;
                AnswerjsonArray = null;
            }
        } catch (Exception a) {
            Log.v("error", "----" + a.getMessage());
            a.printStackTrace();
        }
    }

    private void setupQuizWelcome(){
        int noOfQuestions = 0;
        if(QuesttionjsonArray != null) {
            noOfQuestions = QuesttionjsonArray.length();
        }
        String filename = "";
        try{
            JSONObject jsonObject = processUserJsonArray.optJSONObject(0);
            noOfAttemptsAllowed = jsonObject.optString("NoOfAttempts");
            String type = jsonObject.getString("type");
            isShuffleAllowed = !type.equalsIgnoreCase("No Suffle");
            timeLimit = jsonObject.optString("timelimit");

            jsonObject = quizTitleJsonArray.optJSONObject(0);
            filename = jsonObject.optString("FileName");
            surveyID = jsonObject.optString("survey_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.noOfQuestions.setText(String.valueOf(noOfQuestions));
        binding.noOfAttemptsAllowed.setText(noOfAttemptsAllowed);
        binding.totalTime.setText(timeLimit);
        if(filename.isEmpty()) {
            binding.downloadAssertsBtn.setVisibility(View.GONE);
        } else {
            binding.downloadAssertsBtn.setVisibility(View.VISIBLE);
        }
    }

    private void populateData(){
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
                            optionname = optionname + obj.getString("Input_Text") + ",";
                            optionCode = optionCode + obj.getString("input_id") + ",";
                            if(obj.getString("Correct_Ans").equalsIgnoreCase("1")) {
                                answercode = obj.getString("input_id");
                            }
                        }
                    }
                    mQuizList.add(new QuizModelClass(jsonObject.getString("Question_Text"), jsonObject.getString("Question_Id"), optionname, optionCode, answercode, "", ""));
                }

            }
            if(!mQuizList.isEmpty()) {
                startTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1);
                startTimer();
                populateQuestionNumber();
                setQuestion(0);
            }else {
                commonUtilsMethods.showToastMessage(QuizActivity.this, "Sync Quiz from Master Sync");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setQuestion(int position) {
        binding.txtQuestonName.setText(mQuizList.get(position).getQuestionName());
        binding.txtQuestonNo.setText("Question : " + (position + 1));
        String[] optionSplit = mQuizList.get(position).getOption().split(",");
        String[] optionSplitIds = mQuizList.get(position).getOptionCode().split(",");

        quizCountAdapter.setSelected(position);

        optionList.clear();
        String SelctionName = "";

        if (!sQuizMainAnswerList.isEmpty()) {
            for (QuizOptionModelClass list : sQuizMainAnswerList) {
                if (list.getQuestionId() == position) {
                    SelctionName = list.getSelectedOption();
                }
            }
        }

        for (int i=0; i<optionSplit.length; i++) {
            String optionName = optionSplit[i], optionCode = optionSplitIds[i];
            if (!SelctionName.equalsIgnoreCase("") && SelctionName.equalsIgnoreCase(optionName)) {
                optionList.add(new QuizOptionModelClass(true, optionName, optionCode));
            } else {
                optionList.add(new QuizOptionModelClass(false, optionName, optionCode));
            }
        }
        quizQuestionAdapter = new QuizQuestionAdapter(optionList, new OptionChooseInterface() {
            @Override
            public void classSelceted(QuizOptionModelClass classGroup) {

                for (int j = 0; j < sQuizMainAnswerList.size(); j++) {
                    if (sQuizMainAnswerList.get(j).getQuestionId() == position) {
                        sQuizMainAnswerList.remove(j);
                    }
                }
                sQuizMainAnswerList.add(new QuizOptionModelClass(position, optionSplit, optionSplitIds, classGroup.getOptionName(), classGroup.getOptionCode()));
                optionList.clear();
                for (int i=0; i<optionSplit.length; i++) {
                    String optionName = optionSplit[i], optionCode = optionSplitIds[i];
                    if (!classGroup.getOptionName().equalsIgnoreCase("") && classGroup.getOptionName().equalsIgnoreCase(optionName)) {
                        optionList.add(new QuizOptionModelClass(true, optionName, optionCode));
                        quizCountAdapter.setFinished(position);
                    } else {
                        optionList.add(new QuizOptionModelClass(false, optionName, optionCode));
                    }
                }
                quizQuestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void classUnselected(QuizOptionModelClass classGroup) {
                for (int j = 0; j < sQuizMainAnswerList.size(); j++) {
                    if (sQuizMainAnswerList.get(j).getQuestionId() == position) {
                        sQuizMainAnswerList.remove(j);
                    }
                }
            }
        });
        binding.recyelerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyelerview.setAdapter(quizQuestionAdapter);

    }

    private void populateQuestionNumber() {
        for (int i = 0; i<mQuizList.size(); i++) {
            if(i== 0) {
                quesNumberModelList.add(new QuizQuesNoModel(i+1, true, false));
            }else {
                quesNumberModelList.add(new QuizQuesNoModel(i+1, false, false));
            }
        }
        quizCountAdapter = new QuizCountAdapter(quesNumberModelList, this);
        binding.skRecylerview.setLayoutManager(new GridLayoutManager(this, 5));
        binding.skRecylerview.setAdapter(quizCountAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void startTimer() {
        try {
            countDownTimer = new CountDownTimer(TimeUtils.getMilliSeconds(TimeUtils.FORMAT_29, timeLimit), 1000) {
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
            commonUtilsMethods.showToastMessage(QuizActivity.this, "Please try after sometime");
            finish();;
        }
    }


    private void pauseTimer() {
        isPaused = true;
        countDownTimer.cancel();
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


}
