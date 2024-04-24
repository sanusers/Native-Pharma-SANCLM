package saneforce.santrip.activity.myresource.callstatusview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.ActivityCallStatusSecondViewBinding;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.utility.TimeUtils;

public class Call_status_second_view extends Fragment {

    SQLite sqLite;

    call_statusadapter call_statusadap;
    ArrayList<callstatus_model> call_list = new ArrayList<>();
    ArrayList<String> dateflt = new ArrayList<>();
    ActivityCallStatusSecondViewBinding Callstatussecondview;
    String previousMonth;


    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Callstatussecondview = ActivityCallStatusSecondViewBinding.inflate(inflater);
        View v = Callstatussecondview.getRoot();


  /*  @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_call_status_second_view, container, false);*/

        sqLite = new SQLite(getActivity());





        Calendar c = Calendar.getInstance();

        // Go back one month
        c.add(Calendar.MONTH, -1);

        // Format the previous month in "MM" format
        SimpleDateFormat df = new SimpleDateFormat("M");
         previousMonth = df.format(c.getTime());

        System.out.println("Previous month in sec_MM format: " + previousMonth);

        callsview();

        return v;
    }

    public void callsview() {
        try {
            call_list.clear();
            String Dates_call = "", vALDATE = "";
            String CustType = "", FW_Indicator = "", Mnth = "", CustName = "", town_name = "", date_format = "", chckflk = "";
            String CustCode = "", Dcr_dt = "", month_name = "", town_code = "", Dcr_flag = "", SF_Code = "", Trans_SlNo = "", AMSLNo = "", val = "";

            JSONArray jsonvst_ctl = sqLite.getMasterSyncDataByKey(Constants.CALL_SYNC);
            JSONArray jsonvst_wrktype = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
            Log.d("callstatus", String.valueOf(jsonvst_ctl));
            if (jsonvst_ctl.length() > 0) {
                for (int i = 0; i < jsonvst_ctl.length(); i++) {
                    JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);
                    if (jsonObject.getString("Mnth").equals(previousMonth)) {
                        if (!Dates_call.equals(jsonObject.getString("Dcr_dt"))) {
                            date_format = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, jsonObject.getString("Dcr_dt")));
                            ;

                            chckflk = "1";

                            Dates_call = jsonObject.getString("Dcr_dt");
                            Dcr_dt = jsonObject.getString("Dcr_dt");

                            CustType = jsonObject.getString("CustType");
                            FW_Indicator = jsonObject.getString("FW_Indicator");
                            Mnth = jsonObject.getString("Mnth");
                            CustName = jsonObject.getString("CustName");
                            town_name = jsonObject.getString("town_name");


                            CustCode = jsonObject.getString("CustCode");

                            month_name = jsonObject.getString("month_name");
                            town_code = jsonObject.getString("town_code");
                            Dcr_flag = jsonObject.getString("Dcr_flag");
                            SF_Code = jsonObject.getString("SF_Code");
                            Trans_SlNo = jsonObject.getString("Trans_SlNo");
                            AMSLNo = jsonObject.getString("AMSLNo");


                        } else {
                            CustType = jsonObject.getString("CustType");
                            FW_Indicator = jsonObject.getString("FW_Indicator");
                            Mnth = jsonObject.getString("Mnth");
                            CustName = jsonObject.getString("CustName");
                            town_name = jsonObject.getString("town_name");
                            Dcr_dt = "";
                            chckflk = "";
                            date_format = "";


                            CustCode = jsonObject.getString("CustCode");

                            month_name = jsonObject.getString("month_name");
                            town_code = jsonObject.getString("town_code");
                            Dcr_flag = jsonObject.getString("Dcr_flag");
                            SF_Code = jsonObject.getString("SF_Code");
                            Trans_SlNo = jsonObject.getString("Trans_SlNo");
                            AMSLNo = jsonObject.getString("AMSLNo");

                        }
                        if (CustType.equals("0")) {
                            val = "";

                        } else if (CustType.equals("1")) {
                            val = ("Doctor");

                        } else if (CustType.equals("2")) {
                            val = ("Chemist");

                        } else if (CustType.equals("3")) {
                            val = ("Stockist");

                        } else if (CustType.equals("4")) {
                            val = ("Unlisted Doctor");

                        }


   /* for (int i1 = 0; i1 < jsonvst_wrktype.length(); i1++) {
        JSONObject jsonObject1 = jsonvst_wrktype.getJSONObject(i1);
        String fw_name=jsonObject1.getString("FWFlg");

        if(FW_Indicator.equals(fw_name)){
            val=jsonObject1.getString("Name");
            Log.d("frist_string", val);
        }
    }*/


                        call_list.add(new callstatus_model(CustCode, CustType, FW_Indicator, date_format, month_name, CustName, town_code, town_name, Dcr_flag, SF_Code, Trans_SlNo, AMSLNo,
                                Mnth, chckflk, val));//


                        call_statusadap = new call_statusadapter(getActivity(), call_list);
                        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                        manager.setOrientation(LinearLayoutManager.VERTICAL);
                        Callstatussecondview.viewList.setLayoutManager(manager);
                        Callstatussecondview.viewList.setAdapter(call_statusadap);
                        call_statusadap.notifyDataSetChanged();


//                        call_statusadap = new call_statusadapter(context, call_list);
//                        view_list.setItemAnimator(new DefaultItemAnimator());
//                        view_list.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
//                        view_list.setAdapter(call_statusadap);


//    "CustCode":"",
//    "CustType":"0",
//    "FW_Indicator":"N",
//    "Dcr_dt":"2024-01-01",
//    "month_name":"January",
//    "Mnth":1,
//    "Yr":2024,
//    "CustName":"",
//    "town_code":"",
//    "town_name":"",
//    "Dcr_flag":1,
//    "SF_Code":"MGR0941",
//    "Trans_SlNo":"DP3-697",
//    "AMSLNo":""
                    }
                }
            }
        } catch (Exception E) {
            E.printStackTrace();
        }


    }


}