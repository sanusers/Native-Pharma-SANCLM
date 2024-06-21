package saneforce.sanzen.activity.myresource.callstatusview;

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
import java.util.Comparator;

import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityCallStatusfristViewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class Call_statusfrist_view extends Fragment {

    call_statusadapter call_statusadap;
    ActivityCallStatusfristViewBinding Callstatusfristview;
    String previousMonth;
    ArrayList<callstatus_model> call_list = new ArrayList<>();


    RoomDB roomDB;
    MasterDataDao masterDataDao;
    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Callstatusfristview = ActivityCallStatusfristViewBinding.inflate(inflater);
        View v = Callstatusfristview.getRoot();
        roomDB=RoomDB.getDatabase(requireContext());
        masterDataDao=roomDB.masterDataDao();
        
        Calendar c = Calendar.getInstance();
        // Go back one month
        c.add(Calendar.MONTH, -2);
        // Format the previous month in "MM" format
        SimpleDateFormat df = new SimpleDateFormat("M");
         previousMonth = df.format(c.getTime());
         System.out.println("Previous month in MM format: " + previousMonth);
        callsview();
        return v;
    }

    public void callsview() {
        try {
            call_list.clear();
            String Dates_call = "", vALDATE = "";

            String CustType = "", FW_Indicator = "", Mnth = "", CustName = "", town_name = "", date_format = "", chckflk = "",workType= "",time = "";
            String CustCode = "", Dcr_dt = "", month_name = "", town_code = "", Dcr_flag = "", SF_Code = "", Trans_SlNo = "", AMSLNo = "", medicalPersonnel = "";

            JSONArray jsonvst_ctl = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            JSONArray jsonvst_wrktype = new JSONArray(masterDataDao.getDataByKey(Constants.WORK_TYPE));
            Log.d("callstatus", String.valueOf(jsonvst_ctl));
            if (jsonvst_ctl.length() > 0) {
                for (int i = 0; i < jsonvst_ctl.length(); i++) {
                    JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);
//    "CustCode":"559116",
//    "CustType":"1",
//    "FW_Indicator":"F",
//    "Dcr_dt":"2024-03-12",
//    "month_name":"March",
//    "Mnth":3,
//    "Yr":2024,
//    "CustName":"ADAM FARUKH",
//    "town_code":"122200",
//    "town_name":"Dharmapuri ( Vignesh MR 2 )",
//    "Dcr_flag":1,
//    "SF_Code":"MGR0941",
//    "Trans_SlNo":"DP3-760",
//    "AMSLNo":"DP3-620"
                    if (jsonObject.getString("Mnth").equals(previousMonth)) {
                        if (!Dates_call.equals(jsonObject.getString("Dcr_dt"))) {
                            date_format = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, jsonObject.getString("Dcr_dt")));

                            chckflk = "1";
                            Dates_call = jsonObject.getString("Dcr_dt");
                            Dcr_dt = jsonObject.getString("Dcr_dt");
                            CustType = jsonObject.getString("CustType");
                            FW_Indicator = jsonObject.getString("FW_Indicator");
                            workType = jsonObject.getString("WorkType_Name");
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
                            time = jsonObject.getString("vtm");
                        } else {
                            CustType = jsonObject.getString("CustType");
                            FW_Indicator = jsonObject.getString("FW_Indicator");
                            workType = jsonObject.getString("WorkType_Name");
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
                            time = jsonObject.getString("vtm");
                        }
                        if (CustType.equals("0")) {
                            medicalPersonnel = "";
                        } else if (CustType.equals("1")) {
                            if (SharedPref.getDrCap(requireContext()).isEmpty()|| SharedPref.getDrCap(requireContext())==null){
                                medicalPersonnel = "Doctor";
                            }else {
                                medicalPersonnel = SharedPref.getDrCap(requireContext());
                            }
                        } else if (CustType.equals("2")) {
                            if (SharedPref.getChmCap(requireContext()).isEmpty()|| SharedPref.getChmCap(requireContext())==null){
                                medicalPersonnel = "Chemist";
                            }else {
                                medicalPersonnel = SharedPref.getChmCap(requireContext());
                            }
                        } else if (CustType.equals("3")) {
                            if (SharedPref.getStkCap(requireContext()).isEmpty()|| SharedPref.getStkCap(requireContext())==null){
                                medicalPersonnel = "StockList";
                            }else {
                                medicalPersonnel = SharedPref.getStkCap(requireContext());
                            }

                        } else if (CustType.equals("4")) {
                            if (SharedPref.getUNLcap(requireContext()).isEmpty()|| SharedPref.getUNLcap(requireContext())==null){
                                medicalPersonnel = "UnListed Doctor";
                            }else {
                                medicalPersonnel = SharedPref.getUNLcap(requireContext());
                            }
                        }

                        call_list.add(new callstatus_model(CustCode, CustType, FW_Indicator, date_format, month_name, CustName, town_code, town_name, Dcr_flag, SF_Code, Trans_SlNo, AMSLNo,
                                Mnth, chckflk, medicalPersonnel,workType,time));

                        call_statusadap = new call_statusadapter(getActivity(), call_list);
                        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                        manager.setOrientation(LinearLayoutManager.VERTICAL);
                        Callstatusfristview.viewList.setLayoutManager(manager);
                        Callstatusfristview.viewList.setAdapter(call_statusadap);
                        call_statusadap.notifyDataSetChanged();

                    }
                }

                if(call_list.size()==0){
                    Callstatusfristview.viewList.setVisibility(View.GONE);
                    Callstatusfristview.NOData.setVisibility(View.VISIBLE);
                }else{
                    Callstatusfristview.viewList.setVisibility(View.VISIBLE);
                    Callstatusfristview.NOData.setVisibility(View.GONE);
                }
            }
        } catch (Exception E) {
            E.printStackTrace();
        }


    }


}