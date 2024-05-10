package saneforce.santrip.activity.myresource.Categoryview;

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

import java.util.ArrayList;

import saneforce.santrip.activity.myresource.callstatusview.callstatus_model;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.ActivityCateDoctorviewBinding;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
public class Cate_Doctorview extends Fragment {

    ArrayList<callstatus_model> cate_list = new ArrayList<>();
    ActivityCateDoctorviewBinding bindingDoccate;

    Category_adapter cate_adapt;


    RoomDB roomDB;
    MasterDataDao masterDataDao;
    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bindingDoccate = ActivityCateDoctorviewBinding.inflate(getLayoutInflater());
        View v = bindingDoccate.getRoot();



        roomDB= RoomDB.getDatabase(requireContext());
        masterDataDao=roomDB.masterDataDao();
        Doctview();

        return v;
    }

    public void Doctview() {
        try {
            JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CATEGORY));


            String docval = "";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!docval.equals(jsonObject.getString("Code"))) {
                        docval = jsonObject.getString("Code");

//                        "Name":"A",
//                                "Doc_Cat_Name":"A",

                        String Name = (jsonObject.getString("Name"));
                        String Doc_Cat_Name = (jsonObject.getString("Doc_Cat_Name"));

//                                                cate_list.add(new callstatus_model("", "", "", "", "", "",
//                                "", "", "", "", "", "", "", "", ""));
//


//                     ----------------
                        cate_list.add(new callstatus_model(Doc_Cat_Name, Name, "", "", "", "",
                                "", "", "", "", "", "", "", "", ""));


                    }
                }
                Log.d("jsonArray123", String.valueOf(cate_list.size()));
                cate_adapt = new Category_adapter(getActivity(), cate_list);
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                bindingDoccate.viewList.setLayoutManager(manager);
                bindingDoccate.viewList.setAdapter(cate_adapt);
                cate_adapt.notifyDataSetChanged();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}