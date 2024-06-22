package saneforce.sanzen.activity.previewPresentation.fragment;

import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.CusType;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.SelectedTab;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.SpecialityCode;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.SpecialityName;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.from_where;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.previewBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentSpecialityPreviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;


public class Speciality extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSpecialityPreviewBinding specialityPreviewBinding;
    public static ArrayList<BrandModelClass> SlideSpecialityList = new ArrayList<>();
    public static ArrayList<String> brandCodeList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static PreviewAdapter previewAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    public static void getRequiredData(Context context, String specialityName, MasterDataDao masterDataDao) {
        try {
            specialityPreviewBinding.tvSelectSpeciality.setText(specialityName);
            SlideSpecialityList.clear();
            brandCodeList.clear();
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
                                code = productObject.getString("Code");
                                slideId = productObject.getString("SlideId");
                                fileName = productObject.getString("FilePath");
                                slidePriority = productObject.getString("Priority");
                                if(priority.isEmpty()) priority = "500" + slidePriority;
                                BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, priority, false);
                                productArrayList.add(product);
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
                            code = productObject.getString("Code");
                            slideId = productObject.getString("SlideId");
                            fileName = productObject.getString("FilePath");
                            slidePriority = productObject.getString("Priority");
                            if(priority.isEmpty()) priority = "500" + slidePriority;
                            BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, priority, false);
                            productArrayList.add(product);
                        }
                    }
                }
                if(!brandName.isEmpty() && !productArrayList.isEmpty()) {
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, false, productArrayList);
                    SlideSpecialityList.add(brandModelClass);
                }
            }
            if(!SlideSpecialityList.isEmpty()) {
                BrandModelClass brandModelClass = SlideSpecialityList.get(0);
                brandModelClass.setBrandSelected(true);
                SlideSpecialityList.set(0, brandModelClass);
            }

//            for (int i = 0; i < brandSlide.length(); i++) {
//                JSONObject brandObject = brandSlide.getJSONObject(i);
//                String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "";
//                String brandCode = brandObject.getString("Product_Brd_Code");
//                String priority = brandObject.getString("Priority");
//
//                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
//                for (int j = 0; j < prodSlide.length(); j++) {
//                    JSONObject productObject = prodSlide.getJSONObject(j);
//                    if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
//                        brandName = productObject.getString("Name");
//                        code = productObject.getString("Code");
//                        slideId = productObject.getString("SlideId");
//                        fileName = productObject.getString("FilePath");
//                        slidePriority = productObject.getString("Priority");
//                        BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
//                        productArrayList.add(product);
//                    }
//                }
//                boolean brandSelected = i == 0;
//                if (!brandCodeList.contains(brandCode) && !brandName.isEmpty()) {  //To avoid repeated of same brand
//                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
//                    SlideSpecialityList.add(brandModelClass);
//                    brandCodeList.add(brandCode);
//                }
//            }

            if (!SlideSpecialityList.isEmpty()) {
                specialityPreviewBinding.constraintNoData.setVisibility(View.GONE);
                specialityPreviewBinding.constraintSortFilter.setVisibility(View.VISIBLE);
                specialityPreviewBinding.rvBrandList.setVisibility(View.VISIBLE);
                specialityPreviewBinding.tvInfo.setVisibility(View.VISIBLE);
                specialityPreviewBinding.viewDummy2.setVisibility(View.VISIBLE);
                previewAdapter = new PreviewAdapter(context, SlideSpecialityList);
                specialityPreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
                specialityPreviewBinding.rvBrandList.setAdapter(previewAdapter);
                Collections.sort(SlideSpecialityList, Comparator.comparing(BrandModelClass::getBrandName));
            } else {
                specialityPreviewBinding.constraintNoData.setVisibility(View.VISIBLE);
                specialityPreviewBinding.constraintSortFilter.setVisibility(View.GONE);
                specialityPreviewBinding.rvBrandList.setVisibility(View.GONE);
                specialityPreviewBinding.tvInfo.setVisibility(View.GONE);
                specialityPreviewBinding.viewDummy2.setVisibility(View.GONE);
            }

        } catch (Exception ignored) {

        }
    }

    public static void getSelectedSpec(Context context, String selectedSpecialityCode, String SpecialityName, MasterDataDao masterDataDao) {
        try {
            specialityPreviewBinding.tvSelectSpeciality.setText(SpecialityName);
            SlideSpecialityList.clear();
            brandCodeList.clear();
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
                            if(productObject != null && productObject.getString("Speciality_Code").contains(selectedSpecialityCode)) {
                                brandName = productObject.getString("Name");
                                code = productObject.getString("Code");
                                slideId = productObject.getString("SlideId");
                                fileName = productObject.getString("FilePath");
                                slidePriority = productObject.getString("Priority");
                                if(priority.isEmpty()) priority = "500" + slidePriority;
                                BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, priority, false);
                                productArrayList.add(product);
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
                        if(productObject != null && productObject.getString("Speciality_Code").contains(selectedSpecialityCode)) {
                            brandName = productObject.getString("Name");
                            code = productObject.getString("Code");
                            slideId = productObject.getString("SlideId");
                            fileName = productObject.getString("FilePath");
                            slidePriority = productObject.getString("Priority");
                            if(priority.isEmpty()) priority = "500" + slidePriority;
                            BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, priority, false);
                            productArrayList.add(product);
                        }
                    }
                }
                if(!brandName.isEmpty() && !productArrayList.isEmpty()) {
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, false, productArrayList);
                    SlideSpecialityList.add(brandModelClass);
                }
            }
            if(!SlideSpecialityList.isEmpty()) {
                BrandModelClass brandModelClass = SlideSpecialityList.get(0);
                brandModelClass.setBrandSelected(true);
                SlideSpecialityList.set(0, brandModelClass);
            }

//            for (int i = 0; i < brandSlide.length(); i++) {
//                JSONObject brandObject = brandSlide.getJSONObject(i);
//                String brandName = "";
//                String brandCode = brandObject.getString("Product_Brd_Code");
//                String priority = brandObject.getString("Priority");
//
//                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
//                for (int j = 0; j < prodSlide.length(); j++) {
//                    JSONObject productObject = prodSlide.getJSONObject(j);
//                    if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
//                        if (productObject.getString("Speciality_Code").contains(selectedSpecialityCode)) {
//                            brandName = productObject.getString("Name");
//                            String code = productObject.getString("Code");
//                            String slideId = productObject.getString("SlideId");
//                            String fileName = productObject.getString("FilePath");
//                            String slidePriority = productObject.getString("Priority");
//                            BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
//                            productArrayList.add(product);
//                        }
//                    }
//                }
//                boolean brandSelected = i == 0;
//                if (!brandCodeList.contains(brandCode) && !brandName.isEmpty()) { //To avoid repeated of same brand
//                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
//                    SlideSpecialityList.add(brandModelClass);
//                    brandCodeList.add(brandCode);
//                }
//            }

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(specialityPreviewBinding.rvBrandList.getWindowToken(), 0);
            if (!SlideSpecialityList.isEmpty()) {
                specialityPreviewBinding.constraintNoData.setVisibility(View.GONE);
                specialityPreviewBinding.constraintSortFilter.setVisibility(View.VISIBLE);
                specialityPreviewBinding.rvBrandList.setVisibility(View.VISIBLE);
                if (from_where.equalsIgnoreCase("call")) {
                    specialityPreviewBinding.tvInfo.setVisibility(View.VISIBLE);
                    specialityPreviewBinding.viewDummy2.setVisibility(View.VISIBLE);
                }
                previewAdapter = new PreviewAdapter(context, SlideSpecialityList);
                specialityPreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
                specialityPreviewBinding.rvBrandList.setAdapter(previewAdapter);
                Collections.sort(SlideSpecialityList, Comparator.comparing(BrandModelClass::getBrandName));
            } else {
                specialityPreviewBinding.constraintNoData.setVisibility(View.VISIBLE);
                specialityPreviewBinding.constraintSortFilter.setVisibility(View.GONE);
                specialityPreviewBinding.rvBrandList.setVisibility(View.GONE);
                specialityPreviewBinding.tvInfo.setVisibility(View.GONE);
                specialityPreviewBinding.viewDummy2.setVisibility(View.GONE);
            }

        } catch (Exception ignored) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        specialityPreviewBinding = FragmentSpecialityPreviewBinding.inflate(inflater);
        View v = specialityPreviewBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        if (from_where.equalsIgnoreCase("call")) {
            specialityPreviewBinding.tvSelectDoctor.setVisibility(View.GONE);
            specialityPreviewBinding.tvSelectSpeciality.setVisibility(View.VISIBLE);
            if (CusType.equalsIgnoreCase("1")) {
                getSelectedSpec(requireContext(), SpecialityCode, SpecialityName, masterDataDao);
            } else {
                getRequiredData(requireContext(), SpecialityName, masterDataDao);
            }
        } else {
            specialityPreviewBinding.constraintNoData.setVisibility(View.VISIBLE);
            specialityPreviewBinding.tvSelectSpeciality.setVisibility(View.GONE);
            specialityPreviewBinding.rvBrandList.setVisibility(View.GONE);
            specialityPreviewBinding.tvSelectDoctor.setVisibility(View.VISIBLE);
            specialityPreviewBinding.tvInfo.setVisibility(View.GONE);
            specialityPreviewBinding.viewDummy2.setVisibility(View.GONE);
        }

        specialityPreviewBinding.tvSelectSpeciality.setOnClickListener(v15 -> {
            previewBinding.btnFinishDet.setVisibility(View.GONE);
            previewBinding.fragmentSelectSpecialistSide.setVisibility(View.VISIBLE);
        });


        specialityPreviewBinding.tvSelectDoctor.setOnClickListener(v1 -> {
            SelectedTab = "Spec";
            previewBinding.fragmentSelectDrSide.setVisibility(View.VISIBLE);
        });

        specialityPreviewBinding.tvAz.setOnClickListener(v13 -> {
            specialityPreviewBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_left_radius));
            specialityPreviewBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            specialityPreviewBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_right));
            specialityPreviewBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            previewAdapter = new PreviewAdapter(requireContext(), SlideSpecialityList);
            specialityPreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            specialityPreviewBinding.rvBrandList.setAdapter(previewAdapter);
            Collections.sort(SlideSpecialityList, Comparator.comparing(BrandModelClass::getBrandName));
        });

        specialityPreviewBinding.tvZa.setOnClickListener(v12 -> {
            specialityPreviewBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_right_radius));
            specialityPreviewBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            specialityPreviewBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_left));
            specialityPreviewBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            previewAdapter = new PreviewAdapter(requireContext(), SlideSpecialityList);
            specialityPreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            specialityPreviewBinding.rvBrandList.setAdapter(previewAdapter);
            Collections.sort(SlideSpecialityList, Collections.reverseOrder(new BrandMatrix.SortByName()));
        });

        return v;
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
