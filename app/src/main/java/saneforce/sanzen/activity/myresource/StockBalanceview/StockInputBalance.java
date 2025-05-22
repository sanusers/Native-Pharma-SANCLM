package saneforce.sanzen.activity.myresource.StockBalanceview;

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

import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityCateDoctorviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class StockInputBalance  extends Fragment {
    ArrayList<StockInputModel> inputlist = new ArrayList<>();
    ActivityCateDoctorviewBinding bindingDoccate;
    InputBalanceAdapter inp_adapt;
    RoomDB roomDB;
    MasterDataDao masterDataDao;
    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bindingDoccate = ActivityCateDoctorviewBinding.inflate(getLayoutInflater());
        View v = bindingDoccate.getRoot();
        roomDB= RoomDB.getDatabase(requireContext());
        masterDataDao=roomDB.masterDataDao();
        Inputview();
        return v;
    }

    public void Inputview() {
        try {
            JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.INPUT_BALANCE));
            String inpval = "";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!inpval.equals(jsonObject.getString("Code"))) {
                        inpval = jsonObject.getString("Code");
                        String Name = (jsonObject.getString("Name"));
                        String balance = (jsonObject.getString("Balance_Stock"));
                        inputlist.add(new StockInputModel(inpval,Name,balance));
                    }
                }
                Log.d("jsonArray123", String.valueOf(inputlist.size()));
                inp_adapt = new InputBalanceAdapter(getActivity(), inputlist);
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                bindingDoccate.viewList.setLayoutManager(manager);
                bindingDoccate.viewList.setAdapter(inp_adapt);
                inp_adapt.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
