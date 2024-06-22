package saneforce.sanzen.activity.previewPresentation.fragment;

import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.BrandCode;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.SelectedTab;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.SlideCode;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.from_where;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.previewBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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


public class BrandMatrix extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSpecialityPreviewBinding brandMatrixBinding;
    public static ArrayList<BrandModelClass> SlideBrandMatrixList = new ArrayList<>();
    public static ArrayList<String> brandCodeList = new ArrayList<>();
    public static ArrayList<String> slideIdList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static PreviewAdapter previewAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    public static void getSelectedMatrix(Context context, String mappedBrands, String mappedSlides, MasterDataDao masterDataDao) {
        try {
            SlideBrandMatrixList.clear();
            brandCodeList.clear();
            slideIdList.clear();
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
                if(mappedBrands.contains(brandCode)) {
                    ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                    String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "", priority = "";
                    LinkedHashMap<String, String> productWithPriority = brandToProductWithPriority.get(brandCode);
                    HashMap<String, JSONObject> products = brandToProducts.get(brandCode);
                    if(productWithPriority != null) {
                        for (String productID : productWithPriority.keySet()) {
                            if(products != null && products.containsKey(productID)) {
                                JSONObject productObject = products.get(productID);
                                if(productObject != null) {
                                    String[] separated1 = mappedSlides.split(",");
                                    String[] separated2 = productObject.getString("Product_Detail_Code").split(",");
                                    Log.d("TAG", "getSelectedMatrix: " + mappedSlides + "\n" + productObject.getString("Product_Detail_Code"));
                                    for (String value : separated1) {
                                        for (String s : separated2) {
                                            if(value.equalsIgnoreCase(s)) {
                                                brandName = productObject.getString("Name");
                                                BrandModelClass.Product product = getProductData(productObject, priority);
                                                if(product != null) {
                                                    productArrayList.add(product);
                                                }
                                                break;
                                            }
                                        }
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
                                String[] separated1 = mappedSlides.split(",");
                                String[] separated2 = productObject.getString("Product_Detail_Code").split(",");
                                for (String value : separated1) {
                                    for (String s : separated2) {
                                        if(value.equalsIgnoreCase(s)) {
                                            brandName = productObject.getString("Name");
                                            BrandModelClass.Product product = getProductData(productObject, priority);
                                            if(product != null) {
                                                productArrayList.add(product);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(!brandName.isEmpty()) {
                        BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, false, productArrayList);
                        SlideBrandMatrixList.add(brandModelClass);
                    }
                }
            }
            if(!SlideBrandMatrixList.isEmpty()) {
                BrandModelClass brandModelClass = SlideBrandMatrixList.get(0);
                brandModelClass.setBrandSelected(true);
                SlideBrandMatrixList.set(0, brandModelClass);
            }

            Log.v("Brand", mappedBrands + "---" + mappedSlides);
//            for (int i = 0; i < brandSlide.length(); i++) {
//                JSONObject brandObject = brandSlide.getJSONObject(i);
//                String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "";
//                String brandCode = brandObject.getString("Product_Brd_Code");
//                String priority = brandObject.getString("Priority");
//
//                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
//                for (int j = 0; j < prodSlide.length(); j++) {
//                    JSONObject productObject = prodSlide.getJSONObject(j);
//
//                    if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
//                        if (mappedBrands.contains(productObject.getString("Code"))) {
//                            String[] separated1 = mappedSlides.split(",");
//                            String[] separated2 = productObject.getString("Product_Detail_Code").split(",");
//                            for (String value : separated1) {
//                                for (String s : separated2) {
//                                    if (value.equalsIgnoreCase(s)) {
//                                        if (!slideIdList.contains(productObject.getString("SlideId")) && !productObject.getString("SlideId").isEmpty()) {
//                                            brandName = productObject.getString("Name");
//                                            code = productObject.getString("Code");
//                                            slideId = productObject.getString("SlideId");
//                                            fileName = productObject.getString("FilePath");
//                                            slidePriority = productObject.getString("Priority");
//                                            BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
//                                            productArrayList.add(product);
//                                            slideIdList.add(slideId);
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                 /*   if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
//                        if (mappedBrands.contains(productObject.getString("Code")) && mappedSlides.contains(productObject.getString("Product_Detail_Code"))) {
//                            brandName = productObject.getString("Name");
//                            code = productObject.getString("Code");
//                            slideId = productObject.getString("SlideId");
//                            fileName = productObject.getString("FilePath");
//                            slidePriority = productObject.getString("Priority");
//                            BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
//                            productArrayList.add(product);
//                        }
//                    }*/
//                }
//                boolean brandSelected = i == 0;
//                if (!brandCodeList.contains(brandCode) && !brandName.isEmpty()) { //To avoid repeated of same brand
//                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
//                    SlideBrandMatrixList.add(brandModelClass);
//                    brandCodeList.add(brandCode);
//                }
//            }

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(brandMatrixBinding.rvBrandList.getWindowToken(), 0);
            if (!SlideBrandMatrixList.isEmpty()) {
                brandMatrixBinding.constraintNoData.setVisibility(View.GONE);
                brandMatrixBinding.rvBrandList.setVisibility(View.VISIBLE);
                brandMatrixBinding.constraintSortFilter.setVisibility(View.VISIBLE);
                if (from_where.equalsIgnoreCase("call")) {
                    brandMatrixBinding.tvInfo.setVisibility(View.VISIBLE);
                    brandMatrixBinding.viewDummy2.setVisibility(View.VISIBLE);
                }
                previewAdapter = new PreviewAdapter(context, SlideBrandMatrixList);
                brandMatrixBinding.rvBrandList.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
                brandMatrixBinding.rvBrandList.setAdapter(previewAdapter);
                Collections.sort(SlideBrandMatrixList, Comparator.comparing(BrandModelClass::getBrandName));
            } else {
                brandMatrixBinding.constraintNoData.setVisibility(View.VISIBLE);
                brandMatrixBinding.constraintSortFilter.setVisibility(View.GONE);
                brandMatrixBinding.rvBrandList.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.v("Brand", "----" + e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        brandMatrixBinding = FragmentSpecialityPreviewBinding.inflate(inflater);
        View v = brandMatrixBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        brandMatrixBinding.tvSelectSpeciality.setVisibility(View.GONE);
        if (from_where.equalsIgnoreCase("call")) {
            brandMatrixBinding.tvSelectDoctor.setVisibility(View.GONE);
            getSelectedMatrix(requireContext(), BrandCode, SlideCode, masterDataDao);
        } else {
            brandMatrixBinding.constraintNoData.setVisibility(View.VISIBLE);
            brandMatrixBinding.rvBrandList.setVisibility(View.GONE);
            brandMatrixBinding.tvSelectDoctor.setVisibility(View.VISIBLE);
            brandMatrixBinding.tvInfo.setVisibility(View.GONE);
            brandMatrixBinding.viewDummy2.setVisibility(View.GONE);
        }


        brandMatrixBinding.tvSelectDoctor.setOnClickListener(v1 -> {
            SelectedTab = "Matrix";
            previewBinding.fragmentSelectDrSide.setVisibility(View.VISIBLE);
        });


        brandMatrixBinding.tvAz.setOnClickListener(v13 -> {
            brandMatrixBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_left_radius));
            brandMatrixBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            brandMatrixBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_right));
            brandMatrixBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            previewAdapter = new PreviewAdapter(requireContext(), SlideBrandMatrixList);
            brandMatrixBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            brandMatrixBinding.rvBrandList.setAdapter(previewAdapter);
            Collections.sort(SlideBrandMatrixList, Comparator.comparing(BrandModelClass::getBrandName));
        });

        brandMatrixBinding.tvZa.setOnClickListener(v12 -> {
            brandMatrixBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_right_radius));
            brandMatrixBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            brandMatrixBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_left));
            brandMatrixBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            previewAdapter = new PreviewAdapter(requireContext(), SlideBrandMatrixList);
            brandMatrixBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            brandMatrixBinding.rvBrandList.setAdapter(previewAdapter);
            Collections.sort(SlideBrandMatrixList, Collections.reverseOrder(new SortByName()));
        });

        return v;
    }

    static class SortByName implements Comparator<BrandModelClass> {
        @Override
        public int compare(BrandModelClass a, BrandModelClass b) {
            return a.getBrandName().compareTo(b.getBrandName());
        }
    }

    private static BrandModelClass.Product getProductData(JSONObject productObject, String priority) {
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
