package saneforce.sanzen.activity.myresource.callstatusview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.activity.approvals.geotagging.GeoTaggingModelList;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityCallStatusThridViewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class Call_status_thrid_view extends Fragment {
    call_statusadapter call_statusadap;
    ArrayList<callstatus_model> call_list = new ArrayList<>();
    ArrayList<String> dateflt = new ArrayList<>();
ActivityCallStatusThridViewBinding Callstatusthridview;
    String previousMonth;

    RoomDB roomDB;
    MasterDataDao masterDataDao;
    String doctorName;

    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Callstatusthridview = ActivityCallStatusThridViewBinding.inflate(inflater);
        View v = Callstatusthridview.getRoot();


        roomDB= RoomDB.getDatabase(requireContext());
        masterDataDao=roomDB.masterDataDao();
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("M");
         previousMonth = df.format(c.getTime());

        System.out.println("Previous month in third MM format: " + previousMonth);


        setUp();
        callsview();

        return v;
    }

    public void callsview() {
        System.out.println("callsViewName--->" + doctorName);
        try {
            call_list.clear();
            String Dates_call = "", vALDATE = "";
            String CustType = "", FW_Indicator = "", Mnth = "", CustName = "", town_name = "", date_format = "", chckflk = "", workType;
            String CustCode = "", Dcr_dt = "", month_name = "", town_code = "", Dcr_flag = "", SF_Code = "", Trans_SlNo = "", AMSLNo = "", medicalPersonnel = "", time = "";

            JSONArray jsonvst_ctl = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            JSONArray jsonvst_wrktype = new JSONArray(masterDataDao.getDataByKey(Constants.WORK_TYPE));

            if (jsonvst_ctl.length() > 0) {
                for (int i = 0; i < jsonvst_ctl.length(); i++) {
                    JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);
                    if (jsonObject.getString("Mnth").equals(previousMonth)) {
                        if (!Dates_call.equals(jsonObject.getString("Dcr_dt"))) {
                            date_format = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, jsonObject.getString("Dcr_dt"));

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
                            if (SharedPref.getDrCap(requireContext()).isEmpty() || SharedPref.getDrCap(requireContext()) == null) {
                                medicalPersonnel = "Doctor";
                            } else {
                                medicalPersonnel = SharedPref.getDrCap(requireContext());
                            }
                        } else if (CustType.equals("2")) {
                            if (SharedPref.getChmCap(requireContext()).isEmpty() || SharedPref.getChmCap(requireContext()) == null) {
                                medicalPersonnel = "Chemist";
                            } else {
                                medicalPersonnel = SharedPref.getChmCap(requireContext());
                            }
                        } else if (CustType.equals("3")) {
                            if (SharedPref.getStkCap(requireContext()).isEmpty() || SharedPref.getStkCap(requireContext()) == null) {
                                medicalPersonnel = "StockList";
                            } else {
                                medicalPersonnel = SharedPref.getStkCap(requireContext());
                            }
                        } else if (CustType.equals("4")) {
                            if (SharedPref.getUNLcap(requireContext()).isEmpty() || SharedPref.getUNLcap(requireContext()) == null) {
                                medicalPersonnel = "UnListed Doctor";
                            } else {
                                medicalPersonnel = SharedPref.getUNLcap(requireContext());
                            }
                        }

                        String name = CustName;
                        call_list.add(new callstatus_model(CustCode, CustType, FW_Indicator, date_format, month_name, CustName, town_code, town_name, Dcr_flag, SF_Code, Trans_SlNo, AMSLNo, Mnth, chckflk, medicalPersonnel, workType, time));
                    }
                }

                call_statusadap = new call_statusadapter(getActivity(), call_list);
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                Callstatusthridview.viewList.setLayoutManager(manager);
                Callstatusthridview.viewList.setAdapter(call_statusadap);
                call_statusadap.notifyDataSetChanged();
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }
private void setUp()  {
    JSONArray jsonvst_ctl = null;
    try {
        jsonvst_ctl = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
        Map<String, List<String>> namesByDate = new HashMap<>();

        for (int i = 0; i < jsonvst_ctl.length(); i++) {
            JSONObject jsonObject = jsonvst_ctl.getJSONObject(i);
            String currentDcrDt = jsonObject.getString("Dcr_dt");
            String custName = jsonObject.getString("CustName");

            if (namesByDate.containsKey(currentDcrDt)) {
                namesByDate.get(currentDcrDt).add(custName);
            } else {
                List<String> namesList = new ArrayList<>();
                namesList.add(custName);
                namesByDate.put(currentDcrDt, namesList);
            }
        }

        for (Map.Entry<String, List<String>> entry : namesByDate.entrySet()) {
            String date = entry.getKey();
            List<String> namesList = entry.getValue();

            Collections.sort(namesList);

            System.out.println("Date: " + date);

            for (String name : namesList) {
                doctorName  = name;
                System.out.println("doctorName: " + doctorName);

            }
        }
    } catch (JSONException e) {
        throw new RuntimeException(e);
    }

}
}