package saneforce.sanzen.activity.presentation.createPresentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;
import java.util.LinkedHashMap;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.brand.BrandNameAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.selectedSlide.ItemTouchHelperCallBack;
import saneforce.sanzen.activity.presentation.createPresentation.selectedSlide.SelectedSlidesAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.slide.ImageSelectionInterface;
import saneforce.sanzen.activity.presentation.createPresentation.slide.SlideImageAdapter;
import saneforce.sanzen.activity.presentation.playPreview.PlaySlidePreviewActivity;
import saneforce.sanzen.activity.presentation.presentation.PresentationActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityCreatePresentationBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;


public class CreatePresentationActivity extends AppCompatActivity {
    ActivityCreatePresentationBinding binding;
    BrandNameAdapter brandNameAdapter;
    SlideImageAdapter slideImageAdapter;
    SelectedSlidesAdapter selectedSlidesAdapter;
    ArrayList<BrandModelClass> brandProductArrayList = new ArrayList<>();
    ArrayList<BrandModelClass.Product> selectedSlideArrayList = new ArrayList<>();
    ArrayList<BrandModelClass.Product> savedPresentation = new ArrayList<>();
    ArrayList<String> brandCodeList = new ArrayList<>();
    ImageSelectionInterface imageSelectionInterface;
    ItemTouchHelper itemTouchHelper;
    String oldName = "";
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private PresentationDataDao presentationDataDao;
    private MasterDataDao masterDataDao;

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
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        presentationDataDao = roomDB.presentationDataDao();
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
            if (!selectedSlideArrayList.isEmpty()) {
                String name = binding.presentationNameEt.getText().toString().trim();

                if (!name.isEmpty()) {
                    if (!oldName.isEmpty()) {
                        if (!oldName.equalsIgnoreCase(name)) {
                            if (!presentationDataDao.presentationExists(name)) {
                                intentAction(oldName, name);
                            } else {
                               commonUtilsMethods.showToastMessage(CreatePresentationActivity.this ,getString(R.string.presentation_saved_already));
                            }
                        } else {
                            intentAction(oldName, name);
                        }
                    } else {
                        if (!presentationDataDao.presentationExists(name)) {
                            intentAction("", name);
                        } else {
                           commonUtilsMethods.showToastMessage(CreatePresentationActivity.this ,getString(R.string.presentation_saved_already));
                        }
                    }
                } else {
                   commonUtilsMethods.showToastMessage(CreatePresentationActivity.this ,getString(R.string.enter_presentation_name));
                }
            }
            UtilityClass.hideKeyboard(this);
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

            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();

            LinkedHashMap<String, LinkedHashMap<String, String>> brandToProductWithPriority = new LinkedHashMap<>();
            HashMap<String, LinkedHashMap<String, JSONObject>> brandToProducts = new HashMap<>();

            for (int i = 0; i < prodSlide.length(); i++) {
                JSONObject productObject = prodSlide.getJSONObject(i);
                String id = productObject.getString("SlideId");
                String code = productObject.getString("Code");
                if(brandToProducts.containsKey(code)){
                    brandToProducts.get(code).put(id, productObject);
                }else {
                    LinkedHashMap<String, JSONObject> productData = new LinkedHashMap<>();
                    productData.put(id, productObject);
                    brandToProducts.put(code, productData);
                }
            }

            for(int i = 0; i < brandSlide.length(); i++) {
                JSONObject brandObject = brandSlide.getJSONObject(i);
                String brandCode = brandObject.getString("Product_Brd_Code");
                String priority = brandObject.getString("Priority");
                String id = brandObject.getString("ID");
                if(brandToProductWithPriority.containsKey(brandCode)){
                    brandToProductWithPriority.get(brandCode).put(id, priority);
                }else{
                    LinkedHashMap<String, String> productsList = new LinkedHashMap<>();
                    productsList.put(id, priority);
                    brandToProductWithPriority.put(brandCode, productsList);
                }
            }

            for (String brandCode : brandToProductWithPriority.keySet()) {
                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "", priority = "";
                LinkedHashMap<String, String> productWithPriority = brandToProductWithPriority.get(brandCode);
                HashMap<String, JSONObject> products = brandToProducts.get(brandCode);
                if(productWithPriority != null) {
                    for (String productID : productWithPriority.keySet()) {
                        if(products != null && products.containsKey(productID)) {
                            JSONObject productObject = products.get(productID);
                            if(productObject != null) {
                                brandName = productObject.getString("Name");
                                BrandModelClass.Product product = getProductData(productObject, priority);
                                if(product != null) {
                                    productArrayList.add(product);
                                }
                            }
                        }
                    }
                    if(!productWithPriority.isEmpty() && products != null) {
                        for (String productID : productWithPriority.keySet()) {
                            products.remove(productID);
                        }
                    }
                }
                if(products != null && !products.isEmpty()) {
                    for (String productID : products.keySet()) {
                        JSONObject productObject = products.get(productID);
                        if(productObject != null) {
                            brandName = productObject.getString("Name");
                            BrandModelClass.Product product = getProductData(productObject, priority);
                            if(product != null) {
                                productArrayList.add(product);
                            }
                        }
                    }
                }
                if(!brandName.isEmpty() && !productArrayList.isEmpty()) {
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, false, productArrayList);
                    brandProductArrayList.add(brandModelClass);
                }
            }
            if(!brandProductArrayList.isEmpty()) {
                BrandModelClass brandModelClass = brandProductArrayList.get(0);
                brandModelClass.setBrandSelected(true);
                brandProductArrayList.set(0, brandModelClass);
            }

            if (!savedPresentation.isEmpty()) { //Changing image selection state if there are data in savedPresentation.same time load data of selected image to the SelectedSlideAdapter
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

        if (!arrayList.isEmpty())
            populateSlideImageAdapter(arrayList.get(0).getProductArrayList());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateSlideImageAdapter(ArrayList<BrandModelClass.Product> arrayList) {

        imageSelectionInterface = (arrayList1, position) -> {
            brandNameAdapter.notifyDataSetChanged();
            slideImageAdapter.notifyDataSetChanged();

            for (BrandModelClass brandModelClass : brandProductArrayList) {
                for (BrandModelClass.Product product : brandModelClass.getProductArrayList()) {
                    if (product.isImageSelected()) {
                        if (!selectedSlideArrayList.isEmpty()) {
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
                        if (!selectedSlideArrayList.isEmpty()) {
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
            binding.playBtn.setEnabled(!selectedSlideArrayList.isEmpty());
            binding.save.setEnabled(!selectedSlideArrayList.isEmpty());
        };


        slideImageAdapter = new SlideImageAdapter(CreatePresentationActivity.this, arrayList, imageSelectionInterface);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(CreatePresentationActivity.this, 2);
        binding.slideImageRecView.setLayoutManager(layoutManager);
        binding.slideImageRecView.setAdapter(slideImageAdapter);

        binding.playBtn.setEnabled(!selectedSlideArrayList.isEmpty());
        binding.save.setEnabled(!selectedSlideArrayList.isEmpty());

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

            presentationDataDao.savePresentation(oldName, name, jsonObject.toString());
            Intent intent = new Intent(CreatePresentationActivity.this, PresentationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BrandModelClass.Product getProductData(JSONObject productObject, String priority) {
        try {
            String brandName = productObject.getString("Name");
            String code = productObject.getString("Code");
            String slideId = productObject.getString("SlideId");
            String fileName = productObject.getString("FilePath");
            String slidePriority = productObject.getString("Priority");
            if(priority.isEmpty()) priority = "500" + slidePriority;
            return new BrandModelClass.Product(code, brandName, slideId, fileName, priority, false);
        } catch (Exception e) {
            Log.e("GetProductData", "getProductData: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}