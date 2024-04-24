package saneforce.santrip.activity.previewPresentation.fragment;

import static saneforce.santrip.activity.previewPresentation.PreviewActivity.BrandCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SelectedTab;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SlideCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.from_where;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.previewBinding;

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

import saneforce.santrip.R;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.FragmentSpecialityPreviewBinding;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SQLite;

public class BrandMatrix extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSpecialityPreviewBinding brandMatrixBinding;
    public static ArrayList<BrandModelClass> SlideBrandMatrixList = new ArrayList<>();
    public static ArrayList<String> brandCodeList = new ArrayList<>();
    public static ArrayList<String> slideIdList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static PreviewAdapter previewAdapter;
//    SQLite sqLite;
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
//            JSONArray prodSlide = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
//            JSONArray brandSlide = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE);
            Log.v("Brand", mappedBrands + "---" + mappedSlides);
            for (int i = 0; i < brandSlide.length(); i++) {
                JSONObject brandObject = brandSlide.getJSONObject(i);
                String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "";
                String brandCode = brandObject.getString("Product_Brd_Code");
                String priority = brandObject.getString("Priority");

                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                for (int j = 0; j < prodSlide.length(); j++) {
                    JSONObject productObject = prodSlide.getJSONObject(j);

                    if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
                        if (mappedBrands.contains(productObject.getString("Code"))) {
                            String[] separated1 = mappedSlides.split(",");
                            String[] separated2 = productObject.getString("Product_Detail_Code").split(",");
                            for (String value : separated1) {
                                for (String s : separated2) {
                                    if (value.equalsIgnoreCase(s)) {
                                        if (!slideIdList.contains(productObject.getString("SlideId")) && !productObject.getString("SlideId").isEmpty()) {
                                            brandName = productObject.getString("Name");
                                            code = productObject.getString("Code");
                                            slideId = productObject.getString("SlideId");
                                            fileName = productObject.getString("FilePath");
                                            slidePriority = productObject.getString("Priority");
                                            BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                                            productArrayList.add(product);
                                            slideIdList.add(slideId);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                 /*   if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
                        if (mappedBrands.contains(productObject.getString("Code")) && mappedSlides.contains(productObject.getString("Product_Detail_Code"))) {
                            brandName = productObject.getString("Name");
                            code = productObject.getString("Code");
                            slideId = productObject.getString("SlideId");
                            fileName = productObject.getString("FilePath");
                            slidePriority = productObject.getString("Priority");
                            BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                            productArrayList.add(product);
                        }
                    }*/
                }
                boolean brandSelected = i == 0;
                if (!brandCodeList.contains(brandCode) && !brandName.isEmpty()) { //To avoid repeated of same brand
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
                    SlideBrandMatrixList.add(brandModelClass);
                    brandCodeList.add(brandCode);
                }
            }

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(brandMatrixBinding.rvBrandList.getWindowToken(), 0);
            if (SlideBrandMatrixList.size() > 0) {
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
//        sqLite = new SQLite(requireContext());
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
}
