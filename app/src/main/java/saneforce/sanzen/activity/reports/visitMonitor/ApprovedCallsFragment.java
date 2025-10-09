package saneforce.sanzen.activity.reports.visitMonitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.reports.ReportsActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityVisitMonitorBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;
import saneforce.sanzen.utility.TimeUtils;

public class ApprovedCallsFragment extends Fragment {
    private RoomDB roomDB;
    MasterDataDao masterDataDao;

    private RecyclerView recyclerView;
    ApiInterface apiInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_approved_calls, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
//        ActivityVisitMonitorBinding.inflate(inflater.)



        return view;

    }

    public void getVisitData() {

        if (UtilityClass.isNetworkAvailable(requireContext())) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(requireContext(), status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
                        jsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
                        jsonObject.put("division_code",SharedPref.getDivisionCode(requireContext()));
                        jsonObject.put("Rsf",SharedPref.getSfCode(requireContext()));
                        jsonObject.put("month", TimeUtils.FORMAT_8);
                        jsonObject.put("year", TimeUtils.FORMAT_10);
                        jsonObject.put("tableName","getvisitmonitor_zen");

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/reports");

                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {

                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable throwable) {

                            }

                        });










                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            });

        }
    }
}
