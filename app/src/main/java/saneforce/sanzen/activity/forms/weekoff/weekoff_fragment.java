package saneforce.sanzen.activity.forms.weekoff;

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
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class weekoff_fragment extends Fragment {
    RecyclerView weekofflist;
    ArrayList<fromsmodelclass> listvalue = new ArrayList<>();
    weekoff_adapter weekoffadapter;
    CommonUtilsMethods commonUtilsMethods;
    String[] backcolr={"#EAFAF1","#E8F5FC","#FCE8EC","#FCF8E8","#FCF6E8","#E8F9FC","#FCEDFB","#FAFAED","#EEE3F7","#FEF8F8","#F2ECF9","#F5F9E9","#FBFBE7"};
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_weekoff_fragment, container, false);
        weekofflist = v.findViewById(R.id.weekooflist);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        leave_avalabledetails();
        return v;
    }

    public void leave_avalabledetails() {
        try {
            JSONArray jsonstock = masterDataDao.getMasterDataTableOrNew(Constants.WEEKLY_OFF).getMasterSyncDataJsonArray();

            for (int i = 0; i < jsonstock.length(); i++) {

                JSONObject jsonObject = jsonstock.getJSONObject(i);

                String weekoff = (jsonObject.getString("Holiday_Mode"));//Leave_Name
                String colr=backcolr[i];
                Log.d("weekoff", weekoff+"--"+colr+"--"+i);
                String[] weekLyOffList = weekoff.split(",");
                if(weekLyOffList.length > 0) {
                    for (String weekOffData: weekLyOffList) {
                        if(!weekOffData.isEmpty()) {
                            fromsmodelclass list = new fromsmodelclass(weekOffData,colr);
                            listvalue.add(list);
                        }
                    }
                } else {
                    fromsmodelclass list = new fromsmodelclass(weekoff, colr);
                    listvalue.add(list);
                }

                weekoffadapter = new weekoff_adapter(listvalue, getActivity());
                weekofflist.setAdapter(weekoffadapter);
                weekofflist.setLayoutManager(new LinearLayoutManager(getActivity()));
                weekoffadapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}