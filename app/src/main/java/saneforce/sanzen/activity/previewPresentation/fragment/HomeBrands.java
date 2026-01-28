package saneforce.sanzen.activity.previewPresentation.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.sanzen.activity.previewPresentation.adapter.SlideWiseAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentHomePreviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class HomeBrands extends Fragment {
    FragmentHomePreviewBinding homePreviewBinding;
    public static ArrayList<BrandModelClass> SlideHomeBrandList = new ArrayList<>();
    LinkedHashSet<String> brandCodeList = new LinkedHashSet<>();
    RecyclerView.Adapter previewAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private boolean isSlideWiseEnabled = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homePreviewBinding = FragmentHomePreviewBinding.inflate(inflater);
        View view = homePreviewBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        getRequiredData();

        if (SharedPref.getSlideWiseDetailingNeed(requireContext()).equalsIgnoreCase("0")) {
            homePreviewBinding.tabBrandSlide.setVisibility(View.VISIBLE);
        } else {
            homePreviewBinding.tabBrandSlide.setVisibility(View.GONE);
        }

        homePreviewBinding.brandWise.setOnClickListener(view1 -> {
            isSlideWiseEnabled = false;
            homePreviewBinding.brandWise.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_left_radius));
            homePreviewBinding.brandWise.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            homePreviewBinding.slideWise.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_right));
            homePreviewBinding.slideWise.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            previewAdapter = new PreviewAdapter(requireContext(), SlideHomeBrandList);
            homePreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            homePreviewBinding.rvBrandList.setAdapter(previewAdapter);
        });

        homePreviewBinding.slideWise.setOnClickListener(view1 -> {
            isSlideWiseEnabled = true;
            homePreviewBinding.slideWise.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_right_radius));
            homePreviewBinding.slideWise.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            homePreviewBinding.brandWise.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_left));
            homePreviewBinding.brandWise.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            previewAdapter = new SlideWiseAdapter(requireContext(), SlideHomeBrandList);
            homePreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            homePreviewBinding.rvBrandList.setAdapter(previewAdapter);
        });

        homePreviewBinding.tvAz.setOnClickListener(view1 -> {
            homePreviewBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_left_radius));
            homePreviewBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            homePreviewBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_right));
            homePreviewBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            Collections.sort(SlideHomeBrandList, Comparator.comparing(BrandModelClass::getBrandName));
            if (isSlideWiseEnabled) {
                previewAdapter = new SlideWiseAdapter(requireContext(), SlideHomeBrandList);
            } else {
                previewAdapter = new PreviewAdapter(requireContext(), SlideHomeBrandList);
            }
            homePreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            homePreviewBinding.rvBrandList.setAdapter(previewAdapter);
        });

        homePreviewBinding.tvZa.setOnClickListener(view1 -> {
            homePreviewBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_right_radius));
            homePreviewBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            homePreviewBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_left));
            homePreviewBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            Collections.sort(SlideHomeBrandList, Collections.reverseOrder(new BrandMatrix.SortByName()));
            if (isSlideWiseEnabled) {
                previewAdapter = new SlideWiseAdapter(requireContext(), SlideHomeBrandList);
            } else {
                previewAdapter = new PreviewAdapter(requireContext(), SlideHomeBrandList);
            }
            homePreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            homePreviewBinding.rvBrandList.setAdapter(previewAdapter);
        });

        return view;
    }

    private void getRequiredData() {
        try {
            SlideHomeBrandList.clear();
            brandCodeList.clear();
            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();
            LinkedHashMap<String, LinkedHashMap<String, String>> brandToProductWithPriority = new LinkedHashMap<>();
            HashMap<String, LinkedHashMap<String, JSONObject>> brandToProducts = new HashMap<>();

            for (int i = 0; i < prodSlide.length(); i++) {
                JSONObject productObject = prodSlide.getJSONObject(i);
                String id = productObject.optString("SlideId");
                String code = productObject.optString("Code");
                if (brandToProducts.containsKey(code)) {
                    brandToProducts.get(code).put(id, productObject);
                } else {
                    LinkedHashMap<String, JSONObject> productData = new LinkedHashMap<>();
                    productData.put(id, productObject);
                    brandToProducts.put(code, productData);
                }
            }

            for (int i = 0; i < brandSlide.length(); i++) {
                JSONObject brandObject = brandSlide.getJSONObject(i);
                String brandCode = brandObject.optString("Product_Brd_Code");
                String priority = brandObject.optString("Priority");
                String id = brandObject.optString("ID");
                if (brandToProductWithPriority.containsKey(brandCode)) {
                    brandToProductWithPriority.get(brandCode).put(id, priority);
                } else {
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
                if (productWithPriority != null) {
                    for (String productID : productWithPriority.keySet()) {
                        if (products != null && products.containsKey(productID)) {
                            JSONObject productObject = products.get(productID);
                            if (productObject != null) {
                                brandName = productObject.optString("Name");
                                BrandModelClass.Product product = getProductData(productObject, priority);
                                if (product != null) {
                                    productArrayList.add(product);
                                }
                            }
                        }
                    }
                    if (!productWithPriority.isEmpty() && products != null) {
                        for (String productID : productWithPriority.keySet()) {
                            products.remove(productID);
                        }
                    }
                }
                if (products != null && !products.isEmpty()) {
                    for (String productID : products.keySet()) {
                        JSONObject productObject = products.get(productID);
                        if (productObject != null) {
                            brandName = productObject.optString("Name");
                            BrandModelClass.Product product = getProductData(productObject, priority);
                            if (product != null) {
                                productArrayList.add(product);
                            }
                        }
                    }
                }
                if (!brandName.isEmpty() && !productArrayList.isEmpty()) {
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, false, productArrayList);
                    SlideHomeBrandList.add(brandModelClass);
                }
            }
            if (!SlideHomeBrandList.isEmpty()) {
                BrandModelClass brandModelClass = SlideHomeBrandList.get(0);
                brandModelClass.setBrandSelected(true);
                SlideHomeBrandList.set(0, brandModelClass);
            }

            if (!SlideHomeBrandList.isEmpty()) {
                if (SlideHomeBrandList.size() > 1) {
                    homePreviewBinding.constraintSortFilter.setVisibility(View.VISIBLE);
                }
                homePreviewBinding.constraintNoData.setVisibility(View.GONE);
                homePreviewBinding.rvBrandList.setVisibility(View.VISIBLE);
                previewAdapter = new PreviewAdapter(requireContext(), SlideHomeBrandList);
                homePreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                homePreviewBinding.rvBrandList.setAdapter(previewAdapter);
            } else {
                homePreviewBinding.constraintSortFilter.setVisibility(View.GONE);
                homePreviewBinding.constraintNoData.setVisibility(View.VISIBLE);
                homePreviewBinding.rvBrandList.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e("HomeBrands", "getRequiredData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BrandModelClass.Product getProductData(JSONObject productObject, String priority) {
        try {
            String brandName = productObject.optString("Name");
            String code = productObject.optString("Code");
            String slideId = productObject.optString("SlideId");
            String fileName = productObject.optString("FilePath");
            String slidePriority = productObject.optString("Priority");
            String productDetailCode = productObject.optString("Product_Detail_Code");
            String mandatorySlide = productObject.optString("Mandatory_slide");
            if (priority.isEmpty()) priority = "500" + slidePriority;
            return new BrandModelClass.Product(code, brandName, slideId, fileName, priority, false, productDetailCode,mandatorySlide);
        } catch (Exception e) {
            Log.e("GetProductData", "getProductData: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
