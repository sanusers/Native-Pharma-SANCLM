package saneforce.santrip.activity.presentation.createPresentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import io.reactivex.ObservableTransformer;
import saneforce.santrip.R;
import saneforce.santrip.activity.map.MapsActivity;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.activity.map.custSelection.TagCustSelectionList;
import saneforce.santrip.activity.presentation.createPresentation.brand.BrandNameAdapter;
import saneforce.santrip.activity.presentation.createPresentation.brand.BrandNameInterFace;
import saneforce.santrip.activity.presentation.createPresentation.selectedSlide.ItemDragListener;
import saneforce.santrip.activity.presentation.createPresentation.selectedSlide.ItemTouchHelperCallBack;
import saneforce.santrip.activity.presentation.createPresentation.selectedSlide.SelectedSlidesAdapter;
import saneforce.santrip.activity.presentation.createPresentation.slide.ImageSelectionInterface;
import saneforce.santrip.activity.presentation.createPresentation.slide.SlideImageAdapter;
import saneforce.santrip.activity.presentation.playPreview.PlaySlidePreviewActivity;
import saneforce.santrip.activity.presentation.presentation.PresentationActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.ActivityCreatePresentationBinding;
import saneforce.santrip.storage.SQLite;


public class CreatePresentationActivity extends AppCompatActivity {
    ActivityCreatePresentationBinding binding;
    BrandNameAdapter brandNameAdapter;
    SlideImageAdapter slideImageAdapter;
    SelectedSlidesAdapter selectedSlidesAdapter;
    SQLite sqLite;
    ArrayList<BrandModelClass> brandProductArrayList = new ArrayList<>();
    ArrayList<BrandModelClass.Product> selectedSlideArrayList = new ArrayList<>();
    ArrayList<BrandModelClass.Product> savedPresentation = new ArrayList<>();
    ArrayList<String> brandCodeList = new ArrayList<>();
    ImageSelectionInterface imageSelectionInterface;
    ItemTouchHelper itemTouchHelper;
    String oldName = "";
    CommonUtilsMethods commonUtilsMethods;


    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        sqLite = new SQLite(CreatePresentationActivity.this);
        uiInitialisation();

        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(CreatePresentationActivity.this, PresentationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });

        binding.playBtn.setOnClickListener(view -> {
            if (selectedSlideArrayList.size() > 0) {
                Intent intent = new Intent(CreatePresentationActivity.this, PlaySlidePreviewActivity.class);
                String data = new Gson().toJson(selectedSlideArrayList);
                Bundle bundle = new Bundle();
                bundle.putString("slideBundle", data);
                bundle.putString("position", String.valueOf(0));
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

        binding.clear.setOnClickListener(view -> binding.presentationNameEt.setText(""));

        binding.save.setOnClickListener(view -> {
            if (selectedSlideArrayList.size() > 0) {
                String name = binding.presentationNameEt.getText().toString();

                if (!name.isEmpty()) {
                    if (!oldName.isEmpty()) {
                        if (!oldName.equalsIgnoreCase(name)) {
                            if (!sqLite.presentationExists(name)) {
                                intentAction(oldName, name);
                            } else {
                               commonUtilsMethods.showToastMessage(CreatePresentationActivity.this ,getString(R.string.presentation_saved_already));
                            }
                        } else {
                            intentAction(oldName, name);
                        }
                    } else {
                        if (!sqLite.presentationExists(name)) {
                            intentAction("", name);
                        } else {
                           commonUtilsMethods.showToastMessage(CreatePresentationActivity.this ,getString(R.string.presentation_saved_already));
                        }
                    }
                } else {
                   commonUtilsMethods.showToastMessage(CreatePresentationActivity.this ,getString(R.string.enter_presentation_name));
                }
            }
        });

    }

    public void uiInitialisation() {
        try {
            Bundle bundle = getIntent().getBundleExtra("bundle");
            if (bundle != null) {
                String data = bundle.getString("slideBundle");
                oldName = bundle.getString("presentationName");
                binding.presentationNameEt.setText(oldName);
                JSONArray jsonArray = new JSONArray(data);
                Type type = new TypeToken<ArrayList<BrandModelClass.Product>>() {
                }.getType();
                savedPresentation = new Gson().fromJson(jsonArray.toString(), type);
            }

            JSONArray prodSlide = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
            JSONArray brandSlide = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE);

            for (int i = 0; i < brandSlide.length(); i++) {
                JSONObject brandObject = brandSlide.getJSONObject(i);
                String brandName = "";
                String brandCode = brandObject.getString("Product_Brd_Code");
                String priority = brandObject.getString("Priority");

                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                for (int j = 0; j < prodSlide.length(); j++) {
                    JSONObject productObject = prodSlide.getJSONObject(j);
                    if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
                        brandName = productObject.getString("Name");
                        String code = productObject.getString("Code");
                        String slideId = productObject.getString("SlideId");
                        String fileName = productObject.getString("FilePath");
                        String slidePriority = productObject.getString("Priority");
                        BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                        productArrayList.add(product);
                    }
                }
                boolean brandSelected = i == 0;
                if (!brandCodeList.contains(brandCode)) { //To avoid repeated of same brand
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
                    brandProductArrayList.add(brandModelClass);
                    brandCodeList.add(brandCode);
                }
            }

            //savedPresentation.size will be > 0 only when we lunch this activity by click on "Edit" in Presentation Activity.
            //savedPresentation.size will be 0 when we launch this activity by click on create presentation btn in Presentation Activity.
            if (savedPresentation.size() > 0) { //Changing image selection state if there are data in savedPresentation.same time load data of selected image to the SelectedSlideAdapter
                for (BrandModelClass.Product savedProduct : savedPresentation) {
                    for (BrandModelClass brandModelClass : brandProductArrayList) {
                        if (savedProduct.getBrandCode().equals(brandModelClass.brandCode)) {
                            for (BrandModelClass.Product product : brandModelClass.productArrayList) {
                                if (savedProduct.getSlideId().equals(product.getSlideId())) {
                                    product.setImageSelected(true);
                                    selectedSlideArrayList.add(product); //selectedSlideArrayList is the array we pass to SelectedSlideAdapter
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        populateBrandNameAdapter(brandProductArrayList);
        populateSelectedSlideAdapter(selectedSlideArrayList);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateBrandNameAdapter(ArrayList<BrandModelClass> arrayList) {

        brandNameAdapter = new BrandNameAdapter(CreatePresentationActivity.this, arrayList, (arrayList1, position) -> {
            populateSlideImageAdapter(arrayList1.get(position).getProductArrayList());
            brandNameAdapter.notifyDataSetChanged();
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreatePresentationActivity.this);
        binding.brandNameRecView.setLayoutManager(layoutManager);
        binding.brandNameRecView.setAdapter(brandNameAdapter);

        if (arrayList.size() > 0)
            populateSlideImageAdapter(arrayList.get(0).getProductArrayList());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateSlideImageAdapter(ArrayList<BrandModelClass.Product> arrayList) {

        imageSelectionInterface = (arrayList1, position) -> {
            brandNameAdapter.notifyDataSetChanged();
            slideImageAdapter.notifyItemChanged(position);

            for (BrandModelClass brandModelClass : brandProductArrayList) {
                for (BrandModelClass.Product product : brandModelClass.getProductArrayList()) {
                    if (product.isImageSelected()) {
                        if (selectedSlideArrayList.size() > 0) {
                            for (int i = 0; i < selectedSlideArrayList.size(); i++) {
                                if (!selectedSlideArrayList.get(i).getSlideId().equalsIgnoreCase(product.getSlideId())) {
                                    selectedSlideArrayList.add(selectedSlideArrayList.size(), product);
                                    break;
                                }
                            }
                        } else {
                            selectedSlideArrayList.add(selectedSlideArrayList.size(), product);
                        }
                    } else {
                        if (selectedSlideArrayList.size() > 0) {
                            for (int i = 0; i < selectedSlideArrayList.size(); i++) {
                                if (selectedSlideArrayList.get(i).getSlideId().equalsIgnoreCase(product.getSlideId())) {
                                    selectedSlideArrayList.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }


            int count = selectedSlideArrayList.size();
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (selectedSlideArrayList.get(i).getSlideId().equalsIgnoreCase(selectedSlideArrayList.get(j).getSlideId())) {
                        selectedSlideArrayList.remove(j--);
                        count--;
                    }
                }
            }

            populateSelectedSlideAdapter(selectedSlideArrayList);
            binding.playBtn.setEnabled(selectedSlideArrayList.size() > 0);
            binding.save.setEnabled(selectedSlideArrayList.size() > 0);
        };


        Collections.sort(arrayList, (product, t1) -> product.getPriority().compareTo(t1.getPriority()));

        slideImageAdapter = new SlideImageAdapter(CreatePresentationActivity.this, arrayList, imageSelectionInterface);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(CreatePresentationActivity.this, 2);
        binding.slideImageRecView.setLayoutManager(layoutManager);
        binding.slideImageRecView.setAdapter(slideImageAdapter);

        binding.playBtn.setEnabled(selectedSlideArrayList.size() > 0);
        binding.save.setEnabled(selectedSlideArrayList.size() > 0);

    }

    public void populateSelectedSlideAdapter(ArrayList<BrandModelClass.Product> arrayList) {

        /// Collections.sort(arrayList);
        selectedSlidesAdapter = new SelectedSlidesAdapter(CreatePresentationActivity.this, arrayList, imageSelectionInterface, viewHolder -> itemTouchHelper.startDrag(viewHolder));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreatePresentationActivity.this);
        binding.slidesRecView.setLayoutManager(layoutManager);

        ItemTouchHelperCallBack itemTouchHelperCallBack = new ItemTouchHelperCallBack(selectedSlidesAdapter);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(binding.slidesRecView);
        binding.slideImageRecView.setHasFixedSize(false);
        binding.slidesRecView.setAdapter(selectedSlidesAdapter);

        binding.selectedSlideCount.setText(String.valueOf(arrayList.size()));
    }

    public void intentAction(String oldName, String name) {
        try {
            BrandModelClass.Presentation presentation = new BrandModelClass.Presentation();
            presentation.setPresentationName(name);
            presentation.setProducts(selectedSlideArrayList);
            JSONObject jsonObject = new JSONObject(new Gson().toJson(presentation));

            sqLite.savePresentation(oldName, name, jsonObject.toString());
            Intent intent = new Intent(CreatePresentationActivity.this, PresentationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}