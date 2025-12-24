package saneforce.sanzen.activity.previewPresentation.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.sanzen.activity.presentation.createPresentation.brand.BrandNameAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.selectedSlide.ItemTouchHelperCallBack;
import saneforce.sanzen.activity.presentation.createPresentation.selectedSlide.SelectedSlidesAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.slide.ImageSelectionInterface;
import saneforce.sanzen.activity.presentation.createPresentation.slide.SlideImageAdapter;
import saneforce.sanzen.activity.presentation.playPreview.PlaySlidePreviewActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentCustomPresentationBinding;
import saneforce.sanzen.databinding.FragmentCustomerPresentationBinding;
import saneforce.sanzen.databinding.FragmentHomePreviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class CustomPresentationFragment extends Fragment {
    private FragmentCustomPresentationBinding binding;
    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private BrandNameAdapter brandNameAdapter;
    private SlideImageAdapter slideImageAdapter;
    private SelectedSlidesAdapter selectedSlidesAdapter;
    private final ArrayList<BrandModelClass> brandProductArrayList = new ArrayList<>();
    public static ArrayList<BrandModelClass.Product> selectedSlideArrayList = new ArrayList<>();
    private final ArrayList<BrandModelClass.Product> savedPresentation = new ArrayList<>();
    private ImageSelectionInterface imageSelectionInterface;
    private ItemTouchHelper itemTouchHelper;

    public CustomPresentationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        selectedSlideArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomPresentationBinding.inflate(inflater);
        uiInitialisation();

        binding.playBtn.setOnClickListener(view -> {
            if (!selectedSlideArrayList.isEmpty()) {
                Intent intent = new Intent(requireContext(), PlaySlideDetailing.class);
                String data = new Gson().toJson(selectedSlideArrayList);
                Bundle bundle = new Bundle();
                bundle.putString("slideBundle", data);
                bundle.putString("position", String.valueOf(0));
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });
        binding.clearBtn.setOnClickListener(view -> {
            if (!selectedSlideArrayList.isEmpty()) {
                selectedSlideArrayList.clear();
                savedPresentation.clear();
                brandProductArrayList.clear();
                uiInitialisation();
            }
        });
        return binding.getRoot();
    }

    public void uiInitialisation() {
        try {
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
                        if (savedProduct.getBrandCode().equals(brandModelClass.getBrandCode())) {
                            for (BrandModelClass.Product product : brandModelClass.getProductArrayList()) {
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
        populateSelectedSlideAdapter(selectedSlideArrayList, -1);
    }

    private BrandModelClass.Product getProductData(JSONObject productObject, String priority) {
        try {
            String brandName = productObject.getString("Name");
            String code = productObject.getString("Code");
            String slideId = productObject.getString("SlideId");
            String fileName = productObject.getString("FilePath");
            String slidePriority = productObject.getString("Priority");
            String productDetailCode = productObject.getString("Product_Detail_Code");
            if(priority.isEmpty()) priority = "500" + slidePriority;
            return new BrandModelClass.Product(code, brandName, slideId, fileName, priority, false, productDetailCode);
        } catch (Exception e) {
            Log.e("GetProductData", "getProductData: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateBrandNameAdapter(ArrayList<BrandModelClass> arrayList) {
        brandNameAdapter = new BrandNameAdapter(requireContext(), arrayList, (arrayList1, position) -> {
            populateSlideImageAdapter(arrayList1.get(position).getProductArrayList());
            brandNameAdapter.notifyDataSetChanged();
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
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

            populateSelectedSlideAdapter(selectedSlideArrayList, position);
            binding.playBtn.setEnabled(!selectedSlideArrayList.isEmpty());
            binding.clearBtn.setEnabled(!selectedSlideArrayList.isEmpty());
        };
        slideImageAdapter = new SlideImageAdapter(requireContext(), arrayList, imageSelectionInterface);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.slideImageRecView.setLayoutManager(layoutManager);
        binding.slideImageRecView.setAdapter(slideImageAdapter);
        binding.playBtn.setEnabled(!selectedSlideArrayList.isEmpty());
        binding.clearBtn.setEnabled(!selectedSlideArrayList.isEmpty());
    }

    public void populateSelectedSlideAdapter(ArrayList<BrandModelClass.Product> arrayList, int position) {
        selectedSlidesAdapter = new SelectedSlidesAdapter(requireContext(), arrayList, imageSelectionInterface, viewHolder -> itemTouchHelper.startDrag(viewHolder));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.slidesRecView.setLayoutManager(layoutManager);

        ItemTouchHelperCallBack itemTouchHelperCallBack = new ItemTouchHelperCallBack(selectedSlidesAdapter);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(binding.slidesRecView);
        binding.slideImageRecView.setHasFixedSize(false);
        binding.slidesRecView.setAdapter(selectedSlidesAdapter);
        binding.selectedSlideCount.setText(String.valueOf(arrayList.size()));
        if (position >= arrayList.size()) {
            binding.slidesRecView.scrollToPosition(arrayList.size() - 1);
        } else if (position >= 0) {
            binding.slidesRecView.scrollToPosition(position);
        }
    }

}