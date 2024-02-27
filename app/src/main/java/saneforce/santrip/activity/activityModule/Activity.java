package saneforce.santrip.activity.activityModule;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.TOP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.homeScreen.fragment.worktype.WorkplanListAdapter;
import saneforce.santrip.activity.masterSync.MasterSyncActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.ActivityBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class Activity extends AppCompatActivity {

    ActivityBinding binding;

    LoginResponse loginResponse;
    SQLite sqLite;
    ApiInterface apiInterface;

    ArrayList<ActivityModelClass> ActivityList=new ArrayList<>();
    ArrayList<ActivityDetailsModelClass> ActivityDetailsList=new ArrayList<>();


    ArrayList<String> dialogtablemultipledatagd = new ArrayList<String>();
    ArrayList<String> dialogcombomultipledatagd = new ArrayList<String>();
    ArrayList<String> dialogtablesingledatagd = new ArrayList<String>();
    ArrayList<String> dialogcombosingledatagd = new ArrayList<String>();
    ArrayList<String> dialogtablemultipledataidgd = new ArrayList<String>();
    ArrayList<String> dialogcombomultipledataidgd = new ArrayList<String>();
    ArrayList<String> dialogtablesingledataidgd = new ArrayList<String>();
    ArrayList<String> dialogcombosingledataidgd = new ArrayList<String>();


    ArrayList<EditText> numericedittext = new ArrayList<EditText>();
    ArrayList<EditText> characteredittext = new ArrayList<EditText>();
    ArrayList<EditText> textareaedittext = new ArrayList<EditText>();
    ArrayList<EditText> currencyeditext = new ArrayList<EditText>();
    ArrayList<EditText> currencyconvertorFrom = new ArrayList<EditText>();
    ArrayList<EditText> currencyconvertorTo = new ArrayList<EditText>();

    ArrayList<EditText> numericedittextgd = new ArrayList<EditText>();
    ArrayList<EditText> characteredittextgd = new ArrayList<EditText>();
    ArrayList<EditText> textareaedittextgd = new ArrayList<EditText>();
    ArrayList<EditText> currencyeditextgd = new ArrayList<EditText>();

    ArrayList<TextView> multitableeditextarr = new ArrayList<TextView>();
    ArrayList<TextView> singletableeditextarr = new ArrayList<TextView>();
    ArrayList<TextView> multicomboeditextarr = new ArrayList<TextView>();
    ArrayList<TextView> singlecomboeditextarr = new ArrayList<TextView>();
    ArrayList<TextView> textviewdate = new ArrayList<TextView>();
    ArrayList<TextView> textviewtime = new ArrayList<TextView>();
    ArrayList<TextView> textviewdatefrom = new ArrayList<TextView>();
    ArrayList<TextView> textviewdateto = new ArrayList<TextView>();
    ArrayList<TextView> textviewtimefrom = new ArrayList<TextView>();
    ArrayList<TextView> textviewtimeto = new ArrayList<TextView>();
    ArrayList<TextView> textviewupload = new ArrayList<TextView>();

    ArrayList<TextView> multitableeditextarrgd = new ArrayList<TextView>();
    ArrayList<TextView> singletableeditextarrgd = new ArrayList<TextView>();
    ArrayList<TextView> multicomboeditextarrgd = new ArrayList<TextView>();
    ArrayList<TextView> singlecomboeditextarrgd = new ArrayList<TextView>();
    ArrayList<TextView> textviewdategd = new ArrayList<TextView>();
    ArrayList<TextView> textviewtimegd = new ArrayList<TextView>();
    ArrayList<TextView> textviewdatefromgd = new ArrayList<TextView>();
    ArrayList<TextView> textviewdatetogd = new ArrayList<TextView>();
    ArrayList<TextView> textviewtimefromgd = new ArrayList<TextView>();
    ArrayList<TextView> textviewtimetogd = new ArrayList<TextView>();
    ArrayList<TextView> textviewuploadgd = new ArrayList<TextView>();
    ArrayList<TextView> textviewlatlong = new ArrayList<TextView>();
    ArrayList<TextView> textviewaddress = new ArrayList<TextView>();


    ArrayList<JSONObject> HQList = new ArrayList<>();



    TextView textviewtablemultiple,textlabel,textcombosingle,textcombomultiple,textviewtablesingle,headertext,donetxt;
    EditText textarea,textnumber,textcharacter,textcurrency,txtcurconvert1,txtcurconvert2;
    TextView textviewdate1,textViewtime1,textviewfromdate,textviewtodate,textviewfromtime,textviewtotime,textfileupload;

    int multiarr=0,singarr=0,multicomarr=0,singcomarr=0,date1=0,datefrm=0,dateto=0,time1=0,timefrm=0,timeto=0,fileuload=0;

    TextView multitableedittext,singletableedittext,multicomboeditext,singlecomboedittext;

    ImageView goback,upload,refresh;

    String[] value,valfrom,valto;


    TextView textlatlong, textaddress;



    ActivityAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sqLite=new SQLite(this);
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

        adapter=new ActivityAdapter(getApplicationContext(), ActivityList, classGroup -> {
            binding.namechooseActivity.setText(classGroup.getActivityName());
            binding.llActivityDetailsView.removeAllViews();
            getActivityDetails(classGroup);
        });
        binding.skRecylerview.setLayoutManager(new LinearLayoutManager(this));
        binding.skRecylerview.setAdapter(adapter);

        getActivity();

        binding.backArrow.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());


        if(!loginResponse.getDesig().equalsIgnoreCase("MR")){
            binding.rlheadquates.setVisibility(View.VISIBLE);
        }else {
            binding.rlheadquates.setVisibility(View.GONE);
        }

        binding.rlheadquates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHQ();

            }
        });


    }


    public void showHQ() {

        try {
            JSONArray workTypeArray3 = sqLite.getMasterSyncDataByKey(Constants.SUBORDINATE);
            for (int i = 0; i < workTypeArray3.length(); i++) {
                JSONObject jsonObject = workTypeArray3.getJSONObject(i);
                HQList.add(jsonObject);
                ActvityHqAdapter adapter1=new ActvityHqAdapter(getApplicationContext(),HQList);
                binding.acRecyelerView.setLayoutManager(new LinearLayoutManager(this));
                binding.acRecyelerView.setAdapter(adapter1);
            }
        }catch (Exception a){}



        }






    public  void getActivity(){

       try {
           JSONObject object = new JSONObject();
           object.put("tableName", "getdynactivity");
           object.put("sfcode", loginResponse.getSF_Code());
           object.put("division_code", loginResponse.getDivision_Code());
           object.put("Rsf", SharedPref.getHqCode(this));
           object.put("Designation", loginResponse.getDesig());
           object.put("sf_type", loginResponse.getSf_type());
            object.put("state_code", loginResponse.getState_Code());
           object.put("subdivision_code", loginResponse.getSubdivision_code());
           Map<String, String> QuaryParam = new HashMap<>();
           QuaryParam.put("axn", "get/activity");
            Log.v("Response :",""+object.toString());
           Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), QuaryParam, object.toString());

          call.enqueue(new Callback<JsonElement>() {
              @Override
              public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                  Log.v("Response :",""+response);
                  JSONArray jsonArray = new JSONArray();
                  if(response.isSuccessful()){
                 try {
                     JsonElement jsonElement = response.body();
                     jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());

                     if(jsonArray.length()>0){
                         for(int i=0;i<jsonArray.length();i++){
                             JSONObject jsonObject= jsonArray.getJSONObject(i);
                             String[] Activity_Desig = jsonObject.getString("Activity_Desig").split(",\\s*");
                             List<String>  DegList= Arrays.asList(Activity_Desig);
                             if (DegList.contains(loginResponse.getDesig())) {
                                 ActivityList.add(new ActivityModelClass(jsonObject.getString("Activity_SlNo"), jsonObject.getString("Activity_Name"), jsonObject.getString("Activity_For"), jsonObject.getString("Active_Flag"), jsonObject.getString("Activity_Desig"), jsonObject.getString("Activity_Available")));
                             }
                         }

                     }else {
                         CommonUtilsMethods commonUtilsMethods=new CommonUtilsMethods(getApplicationContext());
                         commonUtilsMethods.ShowToast(getApplicationContext(),"No Activity",100);

                     }

                  adapter.notifyDataSetChanged();

                 }catch (Exception a){
                 }
                  }

              }

              @Override
              public void onFailure(Call<JsonElement> call, Throwable t) {

              }
          });


       }catch (Exception a){
           a.printStackTrace();
       }
    }



    public void getActivityDetails(ActivityModelClass Data) {

        try {
            JSONObject object = new JSONObject();
            object.put("tableName", "getdynactivity_details");
            object.put("sfcode", loginResponse.getSF_Code());
            object.put("division_code", loginResponse.getDivision_Code());
            object.put("Rsf", SharedPref.getHqCode(this));
            object.put("Designation", loginResponse.getDesig());
            object.put("sf_type", loginResponse.getSf_type());
            object.put("state_code", loginResponse.getState_Code());
            object.put("subdivision_code", loginResponse.getSubdivision_code());
            object.put("slno", Data.getSlNo());
            Map<String, String> QuaryParam = new HashMap<>();
            QuaryParam.put("axn", "get/activity");
            Log.v("Response :",""+object.toString());
            Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), QuaryParam, object.toString());
            call.enqueue(new Callback<JsonElement>() {
               @Override
               public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                   Log.v("Response :",""+response);
                   JSONArray jsonArray = new JSONArray();
                   if(response.isSuccessful()){
                       ActivityDetailsList.clear();

                       try {
                           JsonElement jsonElement = response.body();
                           jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                           if(jsonArray.length()>0){
                               for(int i=0;i<jsonArray.length();i++){
                                       JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                           String Field_Name = jsonObject1.getString("Field_Name");
                                           String Control_Id = jsonObject1.getString("Control_Id");
                                           String Creation_Id = jsonObject1.getString("Creation_Id");
                                           String input = jsonObject1.getString("input");
                                           String madantaory = jsonObject1.getString("Mandatory");
                                           String Control_Para = jsonObject1.getString("Control_Para");
                                           String Group_Creation_ID = jsonObject1.getString("Group_Creation_ID");
                                           ActivityDetailsList.add(new ActivityDetailsModelClass(Field_Name,Control_Id,Creation_Id,input,madantaory,Control_Para,Group_Creation_ID));

                               }
                               for (int i = 0; i < ActivityDetailsList.size(); i++) {

                                   if (ActivityDetailsList.get(i).getControlId().equals("addlabeltxtvw")) {
                                       addedittextcharacter(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("1")) {
                                       addedittextcharacter(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("2")) {
                                       addedittextnumeric(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("3")) {
                                       addedittextarea(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("4")) {
                                       adddate(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("5")) {
                                       adddaterange(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("6")) {
                                       addtime(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("7")) {
                                       addtimerange(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("8")) {
                                       addsinglecomboViews(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("9")) {
                                       addmultiplecomboViews(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("10")) {
                                       adduploadfile(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("11")) {
                                       addedittextnumericcurrency(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("12")) {
                                       addsingletableViews(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("13")) {
                                       addmultipletableViews(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("17")) {
                                       addlocationdata(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("18")) {
                                       addeditcurrencyconvertor(ActivityDetailsList.get(i), i);
                                   } else if (ActivityDetailsList.get(i).getControlId().equals("19")) {
                                       digitalsign(ActivityDetailsList.get(i), i);
                                   }





                               }
                           }else {
                               CommonUtilsMethods commonUtilsMethods=new CommonUtilsMethods(getApplicationContext());
                               commonUtilsMethods.ShowToast(getApplicationContext(),"No Activity Details",100);
                           }
                       }catch (Exception a){
                       }
                   }




               }

               @Override
               public void onFailure(Call<JsonElement> call, Throwable t) {

               }
           });

        }catch (Exception a){
            a.printStackTrace();
        }
    }



    @SuppressLint("ResourceType")
    public void addlabeltxtvw(ActivityDetailsModelClass List, int k){
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);

        textlabel = new TextView(this);
        textlabel.setText(List.getFieldName().toUpperCase());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textlabel.setBackgroundResource(R.drawable.backround_lite_gray_border);
        textlabel.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textlabel.setTextColor(getResources().getColor(R.color.text_dark));
        textlabel.setGravity(CENTER);
        textlabel.setLayoutParams(params);
        textlabel.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textLinearLayout.addView(textlabel);
        textlabel.setId(k);
        binding.llActivityDetailsView.addView(textLinearLayout);

    }


    public void addedittextcharacter(ActivityDetailsModelClass List, int k){

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));



        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textLinearLayout1.addView(textviewdata);

        textcharacter = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textcharacter.setBackgroundColor(Color.WHITE);
        textcharacter.setBackgroundResource(R.drawable.background_card_white_plan);
        textcharacter.setTextColor(getResources().getColor(R.color.text_dark));
        textcharacter.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        characteredittext.add(textcharacter);
        textcharacter.setLayoutParams(params);
        textcharacter.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));

        textLinearLayout1.addView(textcharacter);
        textcharacter.setInputType(InputType.TYPE_CLASS_TEXT);
        textcharacter.setId(k);
        textcharacter.setHint("Name");
        textcharacter.setCursorVisible(true);
        textcharacter.setClickable(true);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(Integer.parseInt(List.getControlPara()));
        textcharacter.setFilters(fArray);

    }

    @SuppressLint("ResourceType")
    public void addedittextnumeric(ActivityDetailsModelClass List, int k) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdata.setPadding((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textLinearLayout1.addView(textviewdata);
        textnumber = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));


        textnumber.setBackgroundColor(Color.WHITE);
        textnumber.setBackgroundResource(R.drawable.background_card_white_plan);
        textnumber.setTextColor(getResources().getColor(R.color.text_dark));
        textnumber.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textnumber.setLayoutParams(params);
        textnumber.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textnumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        textnumber.setId(k);
        textnumber.setHint("Number");
        textnumber.setClickable(false);
        textnumber.setCursorVisible(true);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(Integer.parseInt(List.getControlPara()));
        textnumber.setFilters(fArray);
        textLinearLayout1.addView(textnumber);
        numericedittext.add(textnumber);
    }




    public void addedittextarea(ActivityDetailsModelClass List, int k){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));

        textLinearLayout1.addView(textviewdata);
        textarea = new EditText(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textarea.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textarea.setTextColor(getResources().getColor(R.color.text_dark));
        textarea.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textarea.setGravity(TOP);
        textareaedittext.add(textarea);
        textarea.setLayoutParams(params);
        textLinearLayout1.addView(textarea);
        textarea.setBackgroundColor(Color.WHITE);
        textarea.setBackgroundResource(R.drawable.background_card_white_plan);
        textarea.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        textarea.setSingleLine(false);
        textarea.setMinLines(5);
        textarea.setMaxLines(8);
        textarea.setId(k);
        textarea.setHint("Multi line Text");
        textarea.setCursorVisible(true);
        textarea.setClickable(true);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(Integer.parseInt(List.getControlPara()));
        textarea.setFilters(fArray);


    }

    public void adddate(ActivityDetailsModelClass List, int k){

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));

        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);


        textLinearLayout2.setId(k);
        textLinearLayout2.setTag(date1);
        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen._100sdp),
                LinearLayout.LayoutParams.MATCH_PARENT);
        textviewdate1 = new TextView(this);
        textviewdate.add(textviewdate1);


        Drawable drawable = getDrawable(R.drawable.calendar_img);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        textviewdate1.setCompoundDrawables(drawable, null, null, null);


        textviewdate1.setBackgroundColor(Color.WHITE);
        textviewdate1.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewdate1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdate1.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdate1.setLayoutParams(params11);

        textviewdate1.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textviewdate1.setGravity(CENTER);
        textviewdate1.setText("Select Date");
        textLinearLayout2.addView(textviewdate1);
        date1++;
    }


    public void adddaterange(ActivityDetailsModelClass List, int k){

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
         binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

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

        textviewdata.	setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));

        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);



        textLinearLayout2.addView(textLinearLayout3);
        textLinearLayout2.addView(textLinearLayout4);


        textLinearLayout3.setId(k+23);
        textLinearLayout3.setTag(datefrm);

        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(com.intuit.sdp.R.dimen._100sdp),
                LinearLayout.LayoutParams.MATCH_PARENT);
        textviewfromdate = new TextView(this);

        Drawable drawable = getDrawable(R.drawable.calendar_img);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        textviewfromdate.setCompoundDrawables(drawable, null, null, null);
        textviewfromdate.setText("Select From Date");
        textviewfromdate.setBackgroundColor(Color.WHITE);
        textviewfromdate.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewfromdate.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewfromdate.setTextColor(getResources().getColor(R.color.text_dark));
        textviewfromdate.setLayoutParams(params11);
        textviewfromdate.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textviewfromdate.setGravity(CENTER);
        textviewdatefrom.add(textviewfromdate);
        textLinearLayout3.addView(textviewfromdate);
        textLinearLayout4.setId(k+24);
        textLinearLayout4.setTag(dateto);

        LinearLayout.LayoutParams params111 = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(com.intuit.sdp.R.dimen._100sdp),
                LinearLayout.LayoutParams.MATCH_PARENT);
        textviewtodate = new TextView(this);
        textviewtodate.setCompoundDrawables(drawable, null, null, null);
        textviewtodate.setText("Select To Date");
        textviewtodate.setBackgroundColor(Color.WHITE);
        textviewtodate.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewtodate.	setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewtodate.setTextColor(getResources().getColor(R.color.text_dark));

        textviewtodate.setLayoutParams(params111);
        textviewtodate.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textviewtodate.setGravity(CENTER);
        textviewdateto.add(textviewtodate);
        textLinearLayout4.addView(textviewtodate);
        dateto++;
        datefrm++;
    }
    public void addtime(ActivityDetailsModelClass List, int k){


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen._100sdp),
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);

        textviewdata.	setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));

        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));

        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);

        textLinearLayout2.setId(k);
        textLinearLayout2.setTag(time1);


        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen._100sdp),
                LinearLayout.LayoutParams.MATCH_PARENT);
        textViewtime1 = new TextView(this);
        textViewtime1.setText("Select Time");
        Drawable drawable = getDrawable(R.drawable.clock);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));

        textViewtime1.setCompoundDrawables(drawable, null, null, null);
        textViewtime1.setBackgroundColor(Color.WHITE);
        textViewtime1.setBackgroundResource(R.drawable.background_card_white_plan);
        textViewtime1.	setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textViewtime1.setTextColor(getResources().getColor(R.color.text_dark));
        textViewtime1.setLayoutParams(params11);
        textViewtime1.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textViewtime1.setGravity(CENTER);
        textviewtime.add(textViewtime1);
        textLinearLayout2.addView(textViewtime1);
        time1++;
    }
    public void addtimerange(ActivityDetailsModelClass List, int k){


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));;
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


        textLinearLayout3.setId(k+33);
        textLinearLayout3.setTag(timefrm);

        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(
                 (int) getResources().getDimension(R.dimen._100sdp),
                LinearLayout.LayoutParams.MATCH_PARENT);
        textviewfromtime = new TextView(this);
        Drawable drawable = getDrawable(R.drawable.clock);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        textviewfromtime.setCompoundDrawables(drawable, null, null, null);
        textviewfromtime.setText("Select From Time");
        textviewfromtime.setBackgroundColor(Color.WHITE);
        textviewfromtime.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewfromtime.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewfromtime.setTextColor(Color.BLACK);
        textviewfromtime.setLayoutParams(params11);
        textviewfromtime.setTextSize((int) getResources().getDimension(R.dimen._6sdp));;
        textviewfromtime.setGravity(CENTER);

        textviewtimefrom.add(textviewfromtime);
        textLinearLayout3.addView(textviewfromtime);
        textLinearLayout4.setId(k+34);
        textLinearLayout4.setTag(timeto);

        LinearLayout.LayoutParams params111 = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen._100sdp),
                LinearLayout.LayoutParams.MATCH_PARENT);
        textviewtotime = new TextView(this);
        textviewtotime.setCompoundDrawables(drawable, null, null, null);
        textviewtotime.setText("Select To Time");
        textviewtotime.setBackgroundColor(Color.WHITE);
        textviewtotime.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewtotime.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewtotime.setTextColor(Color.BLACK);
        textviewtotime.setLayoutParams(params111);
        textviewtotime.setTextSize((int) getResources().getDimension(R.dimen._6sdp));;
        textviewtotime.setGravity(CENTER);
        textviewtimeto.add(textviewtotime);
        textLinearLayout4.addView(textviewtotime);
        timeto++;
        timefrm++;
    }

    private void addsinglecomboViews(ActivityDetailsModelClass List, int k) {
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        binding.llActivityDetailsView.addView(textLinearLayout);

        textcombosingle = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textcombosingle.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textcombosingle.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textcombosingle.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textcombosingle.setTextColor(getResources().getColor(R.color.text_dark));
        textcombosingle.setLayoutParams(params);

        textcombosingle.setTag(singcomarr);
        textLinearLayout.addView(textcombosingle);
        textcombosingle.setId(k);

        singlecomboedittext = new TextView(this);
        singlecomboedittext.setBackgroundColor(Color.WHITE);
        singlecomboedittext.setBackgroundResource(R.drawable.background_card_white_plan);
        singlecomboedittext.setTextColor(getResources().getColor(R.color.text_dark));
        singlecomboedittext.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        singlecomboedittext.setLayoutParams(params);
        singlecomboedittext.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        singlecomboedittext.setVisibility(View.VISIBLE);
        Drawable drawable = getDrawable(R.drawable.right_arrow);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._6sdp));
        singlecomboedittext.setCompoundDrawables(null, null, drawable, null);
        singlecomboedittext.setHintTextColor(getResources().getColor(R.color.text_dark));
        singlecomboedittext.setHint("Select the " + List.getFieldName());
        textLinearLayout.addView(singlecomboedittext);
        singlecomboeditextarr.add(singlecomboedittext);
        singcomarr++;
    }



    private void addmultiplecomboViews(ActivityDetailsModelClass List, int k) {


        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        binding.llActivityDetailsView.addView(textLinearLayout);

        textcombomultiple = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textcombomultiple.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textcombomultiple.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textcombomultiple.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textcombomultiple.setTextColor(getResources().getColor(R.color.text_dark));
        textcombomultiple.setLayoutParams(params);
        textcombomultiple.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textLinearLayout.addView(textcombomultiple);
        textcombomultiple.setId(k);
        textcombomultiple.setTag(multicomarr);
        multicomboeditext = new TextView(this);

        multicomboeditext.setBackgroundColor(Color.WHITE);
        multicomboeditext.setBackgroundResource(R.drawable.background_card_white_plan);
        multicomboeditext.setTextColor(getResources().getColor(R.color.text_dark));
        multicomboeditext.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        multicomboeditext.setLayoutParams(params);
        multicomboeditext.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        multicomboeditext.setVisibility(View.VISIBLE);
        multicomboeditext.setHint("Select the "+List.getFieldName());
        textLinearLayout.addView(multicomboeditext);
        multicomboeditextarr.add(multicomboeditext);
        multicomarr++;

     }

    public void adduploadfile(ActivityDetailsModelClass List, int k){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins(0, (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textLinearLayout1.addView(textviewdata);
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout1.addView(textLinearLayout2);

        textfileupload = new TextView(this);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        Drawable drawable = getDrawable(R.drawable.form);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._10sdp), (int) getResources().getDimension(R.dimen._10sdp));
        textfileupload.setCompoundDrawables(drawable, null, null, null);        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textfileupload.setBackgroundColor(Color.WHITE);
        textfileupload.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen._4sdp));
        textfileupload.setBackgroundResource(R.drawable.background_card_white_plan);
        textfileupload.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textfileupload.setTextColor(getResources().getColor(R.color.text_dark));
        textfileupload.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textviewupload.add(textfileupload);
        textfileupload.setLayoutParams(params);
        textLinearLayout2.addView(textfileupload);
        textLinearLayout2.setId(k);
        textLinearLayout2.setTag(fileuload);
        textfileupload.setHint("File");
        fileuload++;

    }

    public void addedittextnumericcurrency(ActivityDetailsModelClass List, int k){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        textLinearLayout1.setClickable(true);
        textLinearLayout1.setFocusableInTouchMode(true);
        binding.llActivityDetailsView.addView(textLinearLayout1);

        TextView textviewdata = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        //textviewdata.setBackgroundResource(R.drawable.linearlayoutbgd);
        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textLinearLayout1.addView(textviewdata);

     //   textLinearLayout1.setBackgroundResource(R.drawable.linearlayoutbgd);

        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout1.addView(textLinearLayout2);
        TextView textviewdata1 = new TextView(this);
        textviewdata.setText(List.getFieldName());
        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params11.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewdata1.setBackgroundColor(Color.WHITE);
        textviewdata1.setBackgroundResource(R.drawable.background_card_white_plan);
        textviewdata1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata1.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata1.setLayoutParams(params11);
        textviewdata1.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textviewdata1.setText(List.getControlPara());

        textLinearLayout2.addView(textviewdata1);
        textcurrency = new EditText(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textcurrency.setBackgroundColor(Color.WHITE);
        textcurrency.setBackgroundResource(R.drawable.background_card_white_plan);
        textcurrency.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textcurrency.setTextColor(getResources().getColor(R.color.text_dark));
        textcurrency.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textcurrency.setLayoutParams(params);
        currencyeditext.add(textcurrency);

        textLinearLayout2.addView(textcurrency);
        textcurrency.setInputType(InputType.TYPE_CLASS_NUMBER);
        textcurrency.setId(k);
        textcurrency.setHint("Amount");
        textcurrency.setCursorVisible(true);
        textcurrency.setClickable(true);

    }

    private void addsingletableViews(ActivityDetailsModelClass List, int k) {
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        binding.llActivityDetailsView.addView(textLinearLayout);


        textviewtablesingle = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewtablesingle.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewtablesingle.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        textviewtablesingle.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewtablesingle.setTextColor(getResources().getColor(R.color.text_dark));
        textviewtablesingle.setLayoutParams(params);

        textLinearLayout.addView(textviewtablesingle);
        textviewtablesingle.setId(k);
        textviewtablesingle.setTag(singarr);

        singletableedittext = new TextView(this);
        singletableedittext.setBackgroundColor(Color.WHITE);
        singletableedittext.setBackgroundResource(R.drawable.background_card_white_plan);
        singletableedittext.setTextColor(getResources().getColor(R.color.text_dark));
        singletableedittext.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        singletableedittext.setLayoutParams(params);
        singletableedittext.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        singletableedittext.setVisibility(View.GONE);
        singletableedittext.setHint("Select the "+List.getFieldName());
        textLinearLayout.addView(singletableedittext);
        singletableeditextarr.add(singletableedittext);
        singarr++;
    }
    private void addmultipletableViews(@NonNull ActivityDetailsModelClass List, int k) {

        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        binding.llActivityDetailsView.addView(textLinearLayout);

        textviewtablemultiple = new TextView(this);
        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewtablemultiple.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewtablemultiple.setText(List.getFieldName());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewtablemultiple.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewtablemultiple.setTextColor(getResources().getColor(R.color.text_dark));
        textviewtablemultiple.setLayoutParams(params);
        textviewtablemultiple.setTextSize((int) getResources().getDimension(R.dimen._6sdp));

        textLinearLayout.addView(textviewtablemultiple);
        textviewtablemultiple.setId(k);
        multitableedittext = new TextView(this);
        multitableedittext.setBackgroundColor(Color.WHITE);
        multitableedittext.setBackgroundResource(R.drawable.background_card_white_plan);
        multitableedittext.setTextColor(getResources().getColor(R.color.text_dark));
        multitableedittext.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        multitableedittext.setLayoutParams(params);
        multitableedittext.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        multitableedittext.setVisibility(View.VISIBLE);
        Drawable drawable = getDrawable(R.drawable.right_arrow);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._6sdp));
        multitableedittext.setCompoundDrawables(null, null, drawable, null);
        multitableedittext.setHintTextColor(getResources().getColor(R.color.text_dark));
        multitableedittext.setHint("Select the "+List.getFieldName());
        textLinearLayout.addView(multitableedittext);
        multitableeditextarr.add(multitableedittext);

    }

    public void addlocationdata(ActivityDetailsModelClass List, int k){

        textviewlatlong.clear();
        textviewaddress.clear();
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout.LayoutParams paramsre = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsre.gravity= Gravity.RIGHT;
        paramsre.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout5 = new LinearLayout(this);
        textLinearLayout5.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout5.setLayoutParams(param);
        TextView textView=new TextView(this);

        textView.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textView.setTextColor(getResources().getColor(R.color.text_dark));
        textView.setLayoutParams(params1);
        textView.setTextSize((int) getResources().getDimension(R.dimen._6sdp));

        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textView.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textView.setText(List.getFieldName());
        }
        refresh = new ImageView(this);
        refresh.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        refresh.setLayoutParams(paramsre);
        refresh.setImageResource(R.drawable.refresh_1);
        textLinearLayout5.addView(textView);
        textLinearLayout5.addView(refresh);
        textLinearLayout1.addView(textLinearLayout5);

        textlatlong = new TextView(this);
//        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        String lates = sharedpreferences.getString(lati, "0.0");
//        String longis = sharedpreferences.getString(longi, "0.0");
//        if (lates.equals("0.0") || longis.equals("0.0")) {
//            checkConnection();
//            Toast.makeText(getApplicationContext(),getResources().getString(R.string.loction_waiting),Toast.LENGTH_LONG).show();
//        }
//        else if (lates.equals("") || longis.equals("")){
//            checkConnection();
//            Toast.makeText(getApplicationContext(),getResources().getString(R.string.loction_waiting),Toast.LENGTH_LONG).show();
//        }else{
//            checkConnection();
////            Toast.makeText(Dynamiclisdocview.this,"Location Captured",Toast.LENGTH_LONG).show();
//        }
        textlatlong.setText("Lat :0.0"  +  " Long : 0.0");
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params2.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout3 = new LinearLayout(this);
        textLinearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout3.setLayoutParams(param);

        textlatlong.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textlatlong.setTextColor(getResources().getColor(R.color.text_dark));
        textlatlong.setLayoutParams(params2);
        textlatlong.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textviewlatlong.add(textlatlong);
        textLinearLayout1.addView(textlatlong);
        textLinearLayout1.addView(textLinearLayout3);

        Log.d("textlist",textviewlatlong.get(0).getText().toString().trim().replaceAll("\\s+", " "));

        textaddress = new TextView(this);
        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            addresses = geocoder.getFromLocation(Double.parseDouble(lates),Double.parseDouble(longis) , 1);
//            if (addresses.size()==0){
//
//            }else {
//                String address = addresses.get(0).getAddressLine(0);
//                Log.v("totally_printing_add", address);
//                textaddress.setText(address);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params3.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout4 = new LinearLayout(this);
        textLinearLayout4.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout4.setLayoutParams(param);

        textaddress.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textaddress.setTextColor(getResources().getColor(R.color.text_dark));
        textaddress.setLayoutParams(params3);
        textaddress.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textviewaddress.add(textaddress);
        Log.d("textlistadd",textviewaddress.get(0).getText().toString());
        ImageView datecalender = new ImageView(this);
        datecalender.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        datecalender.setLayoutParams(params1);
        textLinearLayout4.addView(datecalender);
        textLinearLayout4.addView(textaddress);
        textLinearLayout1.addView(textLinearLayout4);

    }


    public void addeditcurrencyconvertor(ActivityDetailsModelClass List, int k){


        if (!List.getControlPara().equals("")){
            value = List.getControlPara().split("[/]");
            valfrom = value[0].split("[-]");
            valto = value[1].split("[-]");
            Log.d("testpara",value[0]+"---"+value[1]);
            Log.d("testpara",valfrom[0]+"---"+valfrom[1]);
            Log.d("testpara",valto[0]+"---"+valto[1]);
        }

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

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

        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textLinear.addView(textviewdata);

        TextView textviewdat = new TextView(this);

        LinearLayout.LayoutParams params113 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params113.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdat.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdat.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdat.setLayoutParams(params113);
        textviewdat.setText("( "+List.getControlPara()+" )");
        textviewdat.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textLinear.addView(textviewdat);


        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout1.addView(textLinearLayout2);

        TextView textviewdata1 = new TextView(this);
        textviewdata.setText(List.getFieldName());
        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params11.width=120;
        params11.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdata1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata1.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata1.setLayoutParams(params11);
        textviewdata1.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
//        textviewdata1.setText(control_para.get(k));
        textviewdata1.setText(valfrom[0]);
        textLinearLayout2.addView(textviewdata1);
        txtcurconvert1 = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        txtcurconvert1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        txtcurconvert1.setTextColor(getResources().getColor(R.color.text_dark));
        txtcurconvert1.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        txtcurconvert1.setLayoutParams(params);
        currencyconvertorFrom.add(txtcurconvert1);
        textLinearLayout2.addView(txtcurconvert1);
        txtcurconvert1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtcurconvert1.setId(k);
        txtcurconvert1.setHint("Amount");
//        txtcurconvert1.addTextChangedListener(new CustomTextWatcher(txtcurconvert1));

        LinearLayout textLinearLayout3 = new LinearLayout(this);
        textLinearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout1.addView(textLinearLayout3);
        TextView textviewdata12 = new TextView(this);
        textviewdata.setText(List.getFieldName());
        LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params12.width=120;
        params12.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        textviewdata12.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata12.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata12.setLayoutParams(params12);
        textviewdata12.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
//        textviewdata12.setText(control_para.get(k));
        textviewdata12.setText(valto[0]);
        textLinearLayout3.addView(textviewdata12);
        txtcurconvert2 = new EditText(this);
        LinearLayout.LayoutParams params13 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params13.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));

        txtcurconvert2.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        txtcurconvert2.setTextColor(getResources().getColor(R.color.text_dark));
        txtcurconvert2.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        txtcurconvert2.setLayoutParams(params13);
        currencyconvertorTo.add(txtcurconvert2);
        textLinearLayout3.addView(txtcurconvert2);
        txtcurconvert2.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtcurconvert2.setFocusable(false);
        txtcurconvert2.setClickable(false);
        txtcurconvert2.setEnabled(false);
        txtcurconvert2.setId(k);
        txtcurconvert2.setHint("Amount");
//        txtcurconvert1.addTextChangedListener(new CustomTextWatcher(txtcurconvert1,txtcurconvert2));
//        txtcurconvert1.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String qty=s.toString();
//                try {
//                    double input=Double.parseDouble(qty);
//                    Double rate= Double.valueOf(valto[1]);
//                    Double total=rate*input;
//                    String va = String.valueOf(total);
//                    Log.d("bbb",input+" -- "+rate+" == "+va);
////                    if( va.matches("^\\d+\\.\\d+") )
////                        txtcurconvert2.setText(String.format("%.4f",total));
////                    else
//                    txtcurconvert2.setText(String.valueOf(total));
////                    txtcurconvert2.setText(String.format("%.4f",total));
//                }catch (NumberFormatException e){
//                    txtcurconvert2.setText("");
//                }catch (Exception e){
//                    txtcurconvert2.setText("");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    public void digitalsign(ActivityDetailsModelClass List, int k){

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        param.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout1 = new LinearLayout(this);
        textLinearLayout1.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout1.setLayoutParams(param);
        binding.llActivityDetailsView.addView(textLinearLayout1);
        TextView textviewdata = new TextView(this);

        String firstChar = "<font color='#000000'>" + List.getFieldName() +"</font>";
        String firstChar2 = "<font color='#EE0000'> ✶</font>";

        if ((List.getMandatory().equals("1")) && (!List.getMandatory().equals(""))) {
            textviewdata.setText(Html.fromHtml(firstChar + firstChar2 ));
        } else {
            textviewdata.setText(List.getFieldName());
        }

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.setMargins((int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp), (int) getResources().getDimension(R.dimen._2sdp));
        LinearLayout textLinearLayout2 = new LinearLayout(this);
        textLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        textLinearLayout2.setLayoutParams(param);
//        textviewdata.setBackgroundResource(R.drawable.linearlayoutbgd);
        textviewdata.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textviewdata.setTextColor(getResources().getColor(R.color.text_dark));
        textviewdata.setLayoutParams(params1);
        textviewdata.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        //textviewdata.setTextAppearance(this,R.style.fontFordynamicPage);
        //setTextViewAttributes(textviewtablemultiple);
        textLinearLayout1.addView(textviewdata);
        textLinearLayout1.addView(textLinearLayout2);
//        textLinearLayout1.setBackgroundResource(R.drawable.linearlayoutbgd);

//        for (int i = 0; i <= fieldname.size()-1; i++) {
        ImageView timeclock = new ImageView(this);

        //textarea.setBackgroundResource(R.drawable.edittxtbgd);
        timeclock.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));

        timeclock.setLayoutParams(params1);
        // textnumber.setTextAppearance(this,R.style.fontFordynamicPage);
        //setTextViewAttributes(textviewtablemultiple);
        textLinearLayout2.addView(timeclock);
        //timeclock.setId(k);
        //timeclock.setTag(time1);
        textLinearLayout2.setId(k);
        textLinearLayout2.setTag(time1);
//        timeclock.setImageResource(R.drawable.edit);
//        textLinearLayout2.setOnClickListener(onclicklistnersign);
        LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        textViewtime1 = new TextView(this);
        textViewtime1.setText("Signature");
//        textViewtime1.setBackgroundResource(R.drawable.linearlayoutbgd);
        textViewtime1.setPadding((int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp), (int) getResources().getDimension(R.dimen._4sdp));
        textViewtime1.setTextColor(Color.BLACK);
        textViewtime1.setLayoutParams(params11);
        textViewtime1.setTextSize((int) getResources().getDimension(R.dimen._6sdp));
        textViewtime1.setGravity(CENTER);
        textviewtime.add(textViewtime1);
        textLinearLayout2.addView(textViewtime1);
        time1++;
    }



}
