package saneforce.sanclm.activity.approvals.geotagging;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import saneforce.sanclm.databinding.ActivityGeoTaggingBinding;


public class GeoTaggingActivity extends AppCompatActivity {
    ActivityGeoTaggingBinding geoTaggingBinding;
    ArrayList<GeoTaggingModelList> geoTaggingModelLists = new ArrayList<>();
    GeoTaggingAdapter geoTaggingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geoTaggingBinding = ActivityGeoTaggingBinding.inflate(getLayoutInflater());
        setContentView(geoTaggingBinding.getRoot());
        setUpAdapter();

        geoTaggingBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUpAdapter() {
        geoTaggingModelLists.add(new GeoTaggingModelList("Aravindh", "Chennai", "Tambaram,Chengalpattu,Kk Nagar", "No 31,Sri Venkatachala Street,3rd Street,Nesappakkam,Chennai - 620009"));
        geoTaggingModelLists.add(new GeoTaggingModelList("Surya", "Trichy", "Palakarai,Thennur,Thillai Nagar,Alwar Thoppu", "No 23,Quide Millath Street,Kaja Malai Nagar,Thennur,Trichy - 620017"));

        geoTaggingAdapter = new GeoTaggingAdapter(getApplicationContext(), geoTaggingModelLists);
        geoTaggingBinding.rvGeoTagging.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        geoTaggingBinding.rvGeoTagging.setAdapter(geoTaggingAdapter);
    }
}