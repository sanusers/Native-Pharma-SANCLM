package saneforce.sanclm.activity.presentation.playPreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanclm.databinding.ActivityPlaySlidePreviewBinding;

public class PlaySlidePreviewActivity extends AppCompatActivity {

    ActivityPlaySlidePreviewBinding binding;
    PlaySlidePagerAdapter itemsPagerAdapter;
    BottomPreviewAdapter bottomPreviewAdapter;
    ArrayList<BrandModelClass.Product> arrayList = new ArrayList<>();



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaySlidePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null){
         String data = bundle.getString("slideBundle");
            try {
                JSONArray jsonArray = new JSONArray(data);
                Type type = new TypeToken<ArrayList<BrandModelClass.Product>>() {
                }.getType();
                arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        binding.bottomLayout.setVisibility(View.GONE);
        populateViewPagerAdapter();
        populateBottomViewAdapter();

        binding.upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.upArrow.setVisibility(View.GONE);
                binding.bottomLayout.setVisibility(View.VISIBLE);
            }
        });

        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.bottomLayout.setVisibility(View.GONE);
                binding.upArrow.setVisibility(View.VISIBLE);
            }
        });


//        java.util.Timer timer = new java.util.Timer();
//        timer.scheduleAtFixedRate(new SlideTimer(), 2000, 5000);
//        binding.tabLayout.setupWithViewPager(binding.viewPager,true);

    }

    public void populateViewPagerAdapter(){
        itemsPagerAdapter = new PlaySlidePagerAdapter(this, arrayList);
        binding.viewPager.setAdapter(itemsPagerAdapter);

    }

    public void populateBottomViewAdapter(){

        bottomPreviewAdapter = new BottomPreviewAdapter(PlaySlidePreviewActivity.this,arrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaySlidePreviewActivity.this, LinearLayoutManager.HORIZONTAL,false);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(bottomPreviewAdapter);
        bottomPreviewAdapter.notifyDataSetChanged();

    }

    public class SlideTimer extends TimerTask {
        @Override
        public void run() {
            PlaySlidePreviewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (binding.viewPager.getCurrentItem() < arrayList.size()-1) {
                        binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem()+1);
                    } else
                        binding.viewPager.setCurrentItem(0);
                }
            });
        }
    }
}