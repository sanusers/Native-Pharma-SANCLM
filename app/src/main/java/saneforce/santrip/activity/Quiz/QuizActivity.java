package saneforce.santrip.activity.Quiz;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.adapters.Callstatusadapter;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.ActivityHomeDashBoardBinding;
import saneforce.santrip.databinding.ActivityQuizBinding;
import saneforce.santrip.storage.SQLite;

public class QuizActivity  extends AppCompatActivity {
    ActivityQuizBinding binding;
    SQLite sqLite;
    JSONArray QuesttionjsonArray, AnswerjsonArray;
    ArrayList<QuizModelClass> mQuizList = new ArrayList<>();
    ArrayList<QuizOptionModelClass> sQuizMainAnswerList = new ArrayList<>();
    ArrayList<QuizOptionModelClass> opitonList = new ArrayList<>();


    int QuestionNumber = 0;

    QuizQuestionAdapter quizQuestionAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        sqLite = new SQLite(this);
        mQuizList.clear();
        try {
            JSONArray quizdata = sqLite.getMasterSyncDataByKey(Constants.QUIZ);
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
            throw new RuntimeException();

        }


        binding.btnpreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QuestionNumber != 0) {
                    QuestionNumber = QuestionNumber - 1;

                    setQuestion(QuestionNumber);
                }
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QuestionNumber < mQuizList.size() - 1) {
                    QuestionNumber = QuestionNumber + 1;
                    setQuestion(QuestionNumber);


                }
            }
        });

    }


    public void setQuestion(int position) {
        binding.txtQuestonName.setText(mQuizList.get(position).getQuestionName());
        binding.txtQuestonNo.setText("Question : " + String.valueOf(position + 1));
        String[] optionsplit = mQuizList.get(position).getOption().split(",");
        String[] optionsplitIds = mQuizList.get(position).getOptionCode().split(",");

        opitonList.clear();
        String SelctionName="";

        if(sQuizMainAnswerList.size()>0){
            for(QuizOptionModelClass list :sQuizMainAnswerList ){
                if(list.getQuestionId()==position){
                    SelctionName=list.getSelectedOption();
                }
        }
        }
        for (String option : optionsplit) {
            if(!SelctionName.equalsIgnoreCase("")&&SelctionName.equalsIgnoreCase(option)){
                opitonList.add(new QuizOptionModelClass(true,option));
            }else {
                opitonList.add(new QuizOptionModelClass(false,option));
            }

        }
        quizQuestionAdapter = new QuizQuestionAdapter(opitonList, new OptionChooseInterface() {
            @Override
            public void classSelceted(QuizOptionModelClass classGroup) {


                for (int j = 0; j < sQuizMainAnswerList.size(); j++) {
                    if( sQuizMainAnswerList.get(j).getQuestionId()==position){
                        sQuizMainAnswerList.remove(j);
                    }
                }
                sQuizMainAnswerList.add(new QuizOptionModelClass(position,optionsplit,optionsplitIds,classGroup.getOptionName(),""));
                opitonList.clear();
                for (String option : optionsplit) {
                    if(!classGroup.getOptionName().equalsIgnoreCase("")&&classGroup.getOptionName().equalsIgnoreCase(option)){
                        opitonList.add(new QuizOptionModelClass(true,option));
                    }else {
                        opitonList.add(new QuizOptionModelClass(false,option));
                    }
                }
                quizQuestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void classUnselected(QuizOptionModelClass classGroup) {
                for (int j = 0; j < sQuizMainAnswerList.size(); j++) {
                    if( sQuizMainAnswerList.get(j).getQuestionId()==position){
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


}
