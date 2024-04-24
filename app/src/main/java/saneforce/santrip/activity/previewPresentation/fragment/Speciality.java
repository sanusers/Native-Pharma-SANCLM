package saneforce.santrip.activity.previewPresentation.fragment;

import static saneforce.santrip.activity.previewPresentation.PreviewActivity.CusType;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SelectedTab;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SpecialityCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SpecialityName;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.from_where;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.previewBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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

public class Speciality extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSpecialityPreviewBinding specialityPreviewBinding;
    public static ArrayList<BrandModelClass> SlideSpecialityList = new ArrayList<>();
    public static ArrayList<String> brandCodeList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static PreviewAdapter previewAdapter;
    SQLite sqLite;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    public static void getRequiredData(Context context, SQLite sqLite, String specialityName, MasterDataDao masterDataDao) {
        try {
            specialityPreviewBinding.tvSelectSpeciality.setText(specialityName);
            SlideSpecialityList.clear();
            brandCodeList.clear();
            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();
//            JSONArray prodSlide = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
//            JSONArray brandSlide = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE);

            for (int i = 0; i < brandSlide.length(); i++) {
                JSONObject brandObject = brandSlide.getJSONObject(i);
                String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "";
                String brandCode = brandObject.getString("Product_Brd_Code");
                String priority = brandObject.getString("Priority");

                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                for (int j = 0; j < prodSlide.length(); j++) {
                    JSONObject productObject = prodSlide.getJSONObject(j);
                    if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
                        brandName = productObject.getString("Name");
                        code = productObject.getString("Code");
                        slideId = productObject.getString("SlideId");
                        fileName = productObject.getString("FilePath");
                        slidePriority = productObject.getString("Priority");
                        BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                        productArrayList.add(product);
                    }
                }
                boolean brandSelected = i == 0;
                if (!brandCodeList.contains(brandCode) && !brandName.isEmpty()) {  //To avoid repeated of same brand
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
                    SlideSpecialityList.add(brandModelClass);
                    brandCodeList.add(brandCode);
                }
            }

            if (SlideSpecialityList.size() > 0) {
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

    public static void getSelectedSpec(Context context, SQLite sqLite, String selectedSpecialityCode, String SpecialityName, MasterDataDao masterDataDao) {
        try {
            specialityPreviewBinding.tvSelectSpeciality.setText(SpecialityName);
            SlideSpecialityList.clear();
            brandCodeList.clear();
            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();
//            JSONArray prodSlide = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
//            JSONArray brandSlide = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE);

            for (int i = 0; i < brandSlide.length(); i++) {
                JSONObject brandObject = brandSlide.getJSONObject(i);
                String brandName = "";
                String brandCode = brandObject.getString("Product_Brd_Code");
                String priority = brandObject.getString("Priority");

                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                for (int j = 0; j < prodSlide.length(); j++) {
                    JSONObject productObject = prodSlide.getJSONObject(j);
                    if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
                        if (productObject.getString("Speciality_Code").contains(selectedSpecialityCode)) {
                            brandName = productObject.getString("Name");
                            String code = productObject.getString("Code");
                            String slideId = productObject.getString("SlideId");
                            String fileName = productObject.getString("FilePath");
                            String slidePriority = productObject.getString("Priority");
                            BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                            productArrayList.add(product);
                        }
                    }
                }
                boolean brandSelected = i == 0;
                if (!brandCodeList.contains(brandCode) && !brandName.isEmpty()) { //To avoid repeated of same brand
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
                    SlideSpecialityList.add(brandModelClass);
                    brandCodeList.add(brandCode);
                }
            }

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(specialityPreviewBinding.rvBrandList.getWindowToken(), 0);
            if (SlideSpecialityList.size() > 0) {
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
        sqLite = new SQLite(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        if (from_where.equalsIgnoreCase("call")) {
            specialityPreviewBinding.tvSelectDoctor.setVisibility(View.GONE);
            specialityPreviewBinding.tvSelectSpeciality.setVisibility(View.VISIBLE);
            if (CusType.equalsIgnoreCase("1")) {
                getSelectedSpec(requireContext(), sqLite, SpecialityCode, SpecialityName, masterDataDao);
            } else {
                getRequiredData(requireContext(), sqLite, SpecialityName, masterDataDao);
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

}
