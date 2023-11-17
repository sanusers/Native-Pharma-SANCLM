package saneforce.sanclm.activity.presentation.createPresentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import saneforce.sanclm.activity.presentation.PresentationActivity;
import saneforce.sanclm.activity.presentation.createPresentation.brand.BrandNameAdapter;
import saneforce.sanclm.activity.presentation.createPresentation.brand.BrandNameInterFace;
import saneforce.sanclm.activity.presentation.createPresentation.selectedSlide.ItemTouchHelperCallBack;
import saneforce.sanclm.activity.presentation.createPresentation.selectedSlide.SelectedSlidesAdapter;
import saneforce.sanclm.activity.presentation.createPresentation.selectedSlide.ItemDragListener;
import saneforce.sanclm.activity.presentation.createPresentation.slide.ImageSelectionInterface;
import saneforce.sanclm.activity.presentation.createPresentation.slide.SlideImageAdapter;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.ActivityCreatePresentationBinding;
import saneforce.sanclm.storage.SQLite;


public class CreatePresentationActivity extends AppCompatActivity {
    ActivityCreatePresentationBinding binding;
    BrandNameAdapter brandNameAdapter;
    SlideImageAdapter slideImageAdapter;
    SelectedSlidesAdapter selectedSlidesAdapter;
    SQLite sqLite;
    ArrayList<BrandModelClass> brandProductArrayList = new ArrayList<>();
    ImageSelectionInterface imageSelectionInterface;

    ItemTouchHelper itemTouchHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sqLite = new SQLite(CreatePresentationActivity.this);
        uiInitialisation();

        binding.backArrow.setOnClickListener(view -> startActivity(new Intent(CreatePresentationActivity.this, PresentationActivity.class)));
    }

    public void uiInitialisation(){
        try {
            JSONArray prodSlide = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
            JSONArray brandSlide = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE);

            for (int i=0;i<brandSlide.length();i++){
                JSONObject brandObject = brandSlide.getJSONObject(i);
                String brandName = "";
                String brandCode = brandObject.getString("Product_Brd_Code");
                String priority = brandObject.getString("Priority");

                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                for (int j=0;j<prodSlide.length();j++){
                    JSONObject productObject = prodSlide.getJSONObject(j);
                    if (productObject.getString("Code").equalsIgnoreCase(brandObject.getString("Product_Brd_Code"))){
                        brandName = productObject.getString("Name");
                        String code = productObject.getString("Code");
                        String slideId = productObject.getString("SlideId");
                        String fileName = productObject.getString("FilePath");
                        BrandModelClass.Product product = new BrandModelClass.Product(code,brandName,slideId,fileName,false);
                        productArrayList.add(product);
                    }
                }
                boolean brandSelected = i == 0;
                BrandModelClass brandModelClass = new BrandModelClass(brandName,brandCode,priority,0,brandSelected,productArrayList);
                brandProductArrayList.add(brandModelClass);
            }

        } catch (JSONException e){

        }
        populateBrandNameAdapter(brandProductArrayList);

    }

    public void getFromFilePath(String imageName){
        File file = new File(getApplicationContext().getExternalFilesDir(null)+ "/Slides/",imageName);
        if (file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
    }

    public void populateBrandNameAdapter(ArrayList<BrandModelClass> arrayList){
        brandNameAdapter = new BrandNameAdapter(CreatePresentationActivity.this, arrayList, new BrandNameInterFace() {
            @Override
            public void onBrandClick (ArrayList<BrandModelClass> arrayList,int position) {
                for (BrandModelClass brandModelClass : arrayList){
                    brandModelClass.setBrandSelected(false);
                }
                arrayList.get(position).setBrandSelected(true);
                brandNameAdapter.notifyDataSetChanged();
                populateSlideImageAdapter(arrayList.get(position).getProductArrayList());
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreatePresentationActivity.this);
        binding.brandNameRecView.setLayoutManager(layoutManager);
        binding.brandNameRecView.setAdapter(brandNameAdapter);
        brandNameAdapter.notifyDataSetChanged();

        if (arrayList.size() > 0)
            populateSlideImageAdapter(arrayList.get(0).getProductArrayList());
    }

    public void populateSlideImageAdapter(ArrayList<BrandModelClass.Product> arrayList){

        imageSelectionInterface = new ImageSelectionInterface() {
            @Override
            public void imageSelection (ArrayList<BrandModelClass.Product> arrayList, int position) {
                brandNameAdapter.notifyDataSetChanged();
                slideImageAdapter.notifyDataSetChanged();

                ArrayList<BrandModelClass.Product> selectedProductArrayList = new ArrayList<>();
                for (BrandModelClass brandModelClass : brandProductArrayList){
                    for (BrandModelClass.Product product : brandModelClass.getProductArrayList()){
                        if (product.isImageSelected()){
                            selectedProductArrayList.add(product);
                        }
                    }
                }
//                Log.e("test","productArrayList size : " + selectedProductArrayList.size());
                populateSlideAdapter(selectedProductArrayList);
            }
        };

        slideImageAdapter = new SlideImageAdapter(CreatePresentationActivity.this, arrayList, imageSelectionInterface);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(CreatePresentationActivity.this,2);
        binding.slideImageRecView.setLayoutManager(layoutManager);
        binding.slideImageRecView.setAdapter(slideImageAdapter);
        slideImageAdapter.notifyDataSetChanged();
    }

    public void populateSlideAdapter(ArrayList<BrandModelClass.Product> arrayList){
        selectedSlidesAdapter = new SelectedSlidesAdapter(CreatePresentationActivity.this, arrayList, imageSelectionInterface, new ItemDragListener() {
            @Override
            public void requestDrag (RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreatePresentationActivity.this);
        binding.slidesRecView.setLayoutManager(layoutManager);

        ItemTouchHelperCallBack itemTouchHelperCallBack = new ItemTouchHelperCallBack(selectedSlidesAdapter);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(binding.slidesRecView);
        binding.slideImageRecView.setHasFixedSize(false);
        binding.slidesRecView.setAdapter(selectedSlidesAdapter);
        selectedSlidesAdapter.notifyDataSetChanged();

        binding.selectedSlideCount.setText(String.valueOf(arrayList.size()));
    }


}