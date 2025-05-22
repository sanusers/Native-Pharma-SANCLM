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
import saneforce.sanzen.activity.myresource.Categoryview.Category_adapter;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityCateDoctorviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class StockProductBalance extends Fragment {
    ArrayList<StockModelClass> prodlist = new ArrayList<>();
    ActivityCateDoctorviewBinding bindingDoccate;
    ProductBalanceAdapter prd_adapt;
    RoomDB roomDB;
    MasterDataDao masterDataDao;
    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bindingDoccate = ActivityCateDoctorviewBinding.inflate(getLayoutInflater());
        View v = bindingDoccate.getRoot();
        roomDB= RoomDB.getDatabase(requireContext());
        masterDataDao=roomDB.masterDataDao();
        Productview();
        return v;
    }

    public void Productview() {
        try {
            JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.STOCK_BALANCE));
            String prdval = "";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!prdval.equals(jsonObject.getString("Code"))) {
                        prdval = jsonObject.getString("Code");
                        String Name = (jsonObject.getString("Name"));
                        String pack = (jsonObject.getString("Pack"));
                        String balance = (jsonObject.getString("Balance_Stock"));
                        prodlist.add(new StockModelClass(prdval,Name,pack,balance));
                    }
                }
                Log.d("jsonArray123", String.valueOf(prodlist.size()));
                prd_adapt = new ProductBalanceAdapter(getActivity(), prodlist);
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                bindingDoccate.viewList.setLayoutManager(manager);
                bindingDoccate.viewList.setAdapter(prd_adapt);
                prd_adapt.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
