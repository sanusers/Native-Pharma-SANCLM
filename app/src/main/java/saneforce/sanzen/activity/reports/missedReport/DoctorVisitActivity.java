package saneforce.sanzen.activity.reports.missedReport;

import static saneforce.sanzen.activity.reports.ReportsActivity.binding;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityDoctorVisitBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;

public class DoctorVisitActivity extends AppCompatActivity {
    ActivityDoctorVisitBinding binding;
    DoctorVisitAdapter adapter;
    final List<DoctorVisitItem> doctorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorVisitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new DoctorVisitAdapter(this, doctorList);
        binding.recyclerDoctorVisit.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDoctorVisit.setAdapter(adapter);
        binding.imageBack.setOnClickListener(v -> finish());
        String doctorArrayString = getIntent().getStringExtra("doctor_array");
        if (doctorArrayString != null) {
            try {
                JSONArray jsonArray = new JSONArray(doctorArrayString);

                // Parse each JSON object into your DoctorVisitItem model
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    // Example assuming DoctorVisitItem has a constructor or setters:
                    DoctorVisitItem item = new DoctorVisitItem(obj.optString("ListedDr_Name"),obj.optString("territory_Name"),obj.optString("ListedDrCode"),obj.optString("Doc_QuaName"),obj.optString("Doc_Cat_SName"),obj.optString("Doc_Special_SName"),obj.optString("Doc_ClsSName"));

                    doctorList.add(item);
                }
                adapter.notifyDataSetChanged();
                binding.missedtittle.setText("Missed: " + doctorList.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}