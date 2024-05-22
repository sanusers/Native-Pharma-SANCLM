package saneforce.sanzen.activity.Quiz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityQuizBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;


public class QuizActivity extends AppCompatActivity {
    ActivityQuizBinding binding;
    JSONArray QuesttionjsonArray, AnswerjsonArray;
    ArrayList<QuizModelClass> mQuizList = new ArrayList<>();
    ArrayList<QuizOptionModelClass> sQuizMainAnswerList = new ArrayList<>();
    ArrayList<QuizOptionModelClass> opitonList = new ArrayList<>();
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    int QuestionNumber = 0;

    QuizQuestionAdapter quizQuestionAdapter;

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

        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        mQuizList.clear();
        try {

            JSONArray quizdata = masterDataDao.getMasterDataTableOrNew(Constants.QUIZ).getMasterSyncDataJsonArray();
            if (quizdata.length() > 0) {
                for (int i = 0; i < quizdata.length(); i++) {
                    JSONObject jsonObject = quizdata.getJSONObject(i);

                    String questionText = jsonObject.getString("questions");
                    String answerText = jsonObject.getString("answers");
                    QuesttionjsonArray = new JSONArray(questionText);
                    AnswerjsonArray = new JSONArray(answerText);
                }

                Map<String, ArrayList<JSONObject>> optionListMap = new HashMap<>();
                for (int i = 0; i < AnswerjsonArray.length(); i++) {
                    JSONObject jsonObject = AnswerjsonArray.getJSONObject(i);
                    String questionId = jsonObject.getString("Question_Id");

                    if (optionListMap.containsKey(questionId)) {
                        optionListMap.get(questionId).add(jsonObject);
                    } else {
                        ArrayList<JSONObject> newList = new ArrayList<>();
                        newList.add(jsonObject);
                        optionListMap.put(questionId, newList);
                    }
                }

                if (QuesttionjsonArray.length() > 0) {
                    for (int j = 0; j < QuesttionjsonArray.length(); j++) {
                        String optionname = "", optionCode = "", answercode = "";
                        JSONObject jsonObject = QuesttionjsonArray.getJSONObject(j);
                        ArrayList<JSONObject> jsonObject1 = optionListMap.get(jsonObject.getString("Question_Id"));
                        for (JSONObject obj : jsonObject1) {
                            optionname = optionname + obj.getString("Input_Text") + ",";
                            optionCode = optionCode + obj.getString("input_id") + ",";
                            if (obj.getString("Correct_Ans").equalsIgnoreCase("1")) {
                                answercode = obj.getString("input_id");
                            }
                        }
                        mQuizList.add(new QuizModelClass(jsonObject.getString("Question_Text"), jsonObject.getString("Question_Id"), optionname, optionCode, answercode, "", ""));
                    }

                }
                setQuestion(0);

            } else {
                QuesttionjsonArray = null;
                AnswerjsonArray = null;
            }
        } catch (Exception a) {
            Log.v("error", "----");

        }

        binding.backArrow.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnpreview.setOnClickListener(view -> {
            if (QuestionNumber != 0) {
                QuestionNumber = QuestionNumber - 1;
                setQuestion(QuestionNumber);
            }
        });

        binding.btnNext.setOnClickListener(view -> {
            if (QuestionNumber < mQuizList.size() - 1) {
                QuestionNumber = QuestionNumber + 1;
                setQuestion(QuestionNumber);
            }
        });

    }


    public void setQuestion(int position) {
        binding.txtQuestonName.setText(mQuizList.get(position).getQuestionName());
        binding.txtQuestonNo.setText("Question : " + (position + 1));
        String[] optionsplit = mQuizList.get(position).getOption().split(",");
        String[] optionsplitIds = mQuizList.get(position).getOptionCode().split(",");

        opitonList.clear();
        String SelctionName = "";

        if (sQuizMainAnswerList.size() > 0) {
            for (QuizOptionModelClass list : sQuizMainAnswerList) {
                if (list.getQuestionId() == position) {
                    SelctionName = list.getSelectedOption();
                }
            }
        }
        for (String option : optionsplit) {
            if (!SelctionName.equalsIgnoreCase("") && SelctionName.equalsIgnoreCase(option)) {
                opitonList.add(new QuizOptionModelClass(true, option));
            } else {
                opitonList.add(new QuizOptionModelClass(false, option));
            }

        }
        quizQuestionAdapter = new QuizQuestionAdapter(opitonList, new OptionChooseInterface() {
            @Override
            public void classSelceted(QuizOptionModelClass classGroup) {


                for (int j = 0; j < sQuizMainAnswerList.size(); j++) {
                    if (sQuizMainAnswerList.get(j).getQuestionId() == position) {
                        sQuizMainAnswerList.remove(j);
                    }
                }
                sQuizMainAnswerList.add(new QuizOptionModelClass(position, optionsplit, optionsplitIds, classGroup.getOptionName(), ""));
                opitonList.clear();
                for (String option : optionsplit) {
                    if (!classGroup.getOptionName().equalsIgnoreCase("") && classGroup.getOptionName().equalsIgnoreCase(option)) {
                        opitonList.add(new QuizOptionModelClass(true, option));
                    } else {
                        opitonList.add(new QuizOptionModelClass(false, option));
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

        QuizCountAapter quizCountAapter = new QuizCountAapter(mQuizList.size(), this);
        binding.skRecylerview.setLayoutManager(new GridLayoutManager(this, 5));
        binding.skRecylerview.setAdapter(quizCountAapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
