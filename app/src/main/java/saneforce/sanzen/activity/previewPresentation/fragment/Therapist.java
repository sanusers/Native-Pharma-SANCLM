package saneforce.sanzen.activity.previewPresentation.fragment;

import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.CusType;
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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentTherapistBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class Therapist extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static FragmentTherapistBinding fragmentTherapistBinding;
    public static ArrayList<BrandModelClass> SlideTherapistList = new ArrayList<>();
    private static final ArrayList<String> brandCodeList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private static PreviewAdapter previewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTherapistBinding = FragmentTherapistBinding.inflate(inflater);
        RoomDB roomDB = RoomDB.getDatabase(requireContext());
        MasterDataDao masterDataDao = roomDB.masterDataDao();
        CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        if (from_where.equalsIgnoreCase("call")) {
            if (CusType.equalsIgnoreCase("1")) {
                getSelectedTherapist(requireContext(), SpecialityCode, SpecialityName, masterDataDao);
            } else {
                getRequiredData(requireContext(), "All", masterDataDao);
            }
        } else {
            fragmentTherapistBinding.constraintNoData.setVisibility(View.VISIBLE);
            fragmentTherapistBinding.rvTherapistList.setVisibility(View.GONE);
            fragmentTherapistBinding.tvInfo.setVisibility(View.GONE);
            fragmentTherapistBinding.viewDummy2.setVisibility(View.GONE);
        }

        getRequiredData(requireContext(), "All", masterDataDao);

        fragmentTherapistBinding.tvSelectTheraptic.setOnClickListener(v1 -> {
            previewBinding.fragmentSelectTherapistSide.setVisibility(View.VISIBLE);
        });

        fragmentTherapistBinding.tvAz.setOnClickListener(v13 -> {
            fragmentTherapistBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_left_radius));
            fragmentTherapistBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            fragmentTherapistBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_right));
            fragmentTherapistBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            previewAdapter = new PreviewAdapter(requireContext(), SlideTherapistList);
            fragmentTherapistBinding.rvTherapistList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            fragmentTherapistBinding.rvTherapistList.setAdapter(previewAdapter);
            Collections.sort(SlideTherapistList, Comparator.comparing(BrandModelClass::getBrandName));
        });

        fragmentTherapistBinding.tvZa.setOnClickListener(v12 -> {
            fragmentTherapistBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_right_radius));
            fragmentTherapistBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            fragmentTherapistBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_left));
            fragmentTherapistBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            previewAdapter = new PreviewAdapter(requireContext(), SlideTherapistList);
            fragmentTherapistBinding.rvTherapistList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            fragmentTherapistBinding.rvTherapistList.setAdapter(previewAdapter);
            Collections.sort(SlideTherapistList, Collections.reverseOrder(new BrandMatrix.SortByName()));
        });

        return fragmentTherapistBinding.getRoot();
    }

    public static void getRequiredData(Context context,String therapticName, MasterDataDao masterDataDao) {
        try {
            fragmentTherapistBinding.tvSelectTheraptic.setText(therapticName);
            SlideTherapistList.clear();
            brandCodeList.clear();
            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();
            JSONArray therapticSlide = masterDataDao.getMasterDataTableOrNew(Constants.THERAPTIC_SLIDE).getMasterSyncDataJsonArray();
            List<String> therapticCodes = new ArrayList<>();
            for (int i=0; i<therapticSlide.length(); i++){
                JSONObject jsonObject = therapticSlide.getJSONObject(i);
                therapticCodes.add(jsonObject.getString("Code"));
            }

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
                        String categoryCode = productObject.getString("Category_Code");
                        String[] codes = categoryCode.split(",");
                        for (String categoryGrpCode: codes){
                            if(therapticCodes.contains(categoryGrpCode)){
                                Log.e("TAG", "getRequiredData: \nBrand: " +brandCode + "\nCategory: " + categoryGrpCode);
                                BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                                productArrayList.add(product);
                                break;
                            }
                        }
                    }
                }
                boolean brandSelected = i == 0;
                if (!brandCodeList.contains(brandCode) && !brandName.isEmpty() && !productArrayList.isEmpty()) {  //To avoid repeated of same brand
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
                    SlideTherapistList.add(brandModelClass);
                    brandCodeList.add(brandCode);
                }
            }

            if (!SlideTherapistList.isEmpty()) {
                fragmentTherapistBinding.constraintNoData.setVisibility(View.GONE);
                fragmentTherapistBinding.constraintSortFilter.setVisibility(View.VISIBLE);
                fragmentTherapistBinding.rvTherapistList.setVisibility(View.VISIBLE);
                if(from_where.equalsIgnoreCase("call")) {
                    fragmentTherapistBinding.tvInfo.setVisibility(View.VISIBLE);
                    fragmentTherapistBinding.viewDummy2.setVisibility(View.VISIBLE);
                }
                previewAdapter = new PreviewAdapter(context, SlideTherapistList);
                fragmentTherapistBinding.rvTherapistList.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
                fragmentTherapistBinding.rvTherapistList.setAdapter(previewAdapter);
                Collections.sort(SlideTherapistList, Comparator.comparing(BrandModelClass::getBrandName));
            } else {
                fragmentTherapistBinding.constraintNoData.setVisibility(View.VISIBLE);
                fragmentTherapistBinding.constraintSortFilter.setVisibility(View.GONE);
                fragmentTherapistBinding.rvTherapistList.setVisibility(View.GONE);
                fragmentTherapistBinding.tvInfo.setVisibility(View.GONE);
                fragmentTherapistBinding.viewDummy2.setVisibility(View.GONE);
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }


    public static void getSelectedTherapist(Context context, String therapticCode, String therapticName, MasterDataDao masterDataDao) {
        try {
            fragmentTherapistBinding.tvSelectTheraptic.setText(therapticName);
            SlideTherapistList.clear();
            brandCodeList.clear();
            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();

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
                        String categoryCode = productObject.getString("Category_Code");
                        Log.e("TAG", "getRequiredData: " + categoryCode);
                        String[] codes = categoryCode.split(",");
                        for (String categoryGrpCode: codes){
                            if(therapticCode.equalsIgnoreCase(categoryGrpCode)){
                                Log.e("TAG", "getRequiredData: \nBrand: " +brandCode + "\nCategory: " + categoryGrpCode);
                                BrandModelClass.Product product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                                productArrayList.add(product);
                                break;
                            }
                        }
                    }
                }
                boolean brandSelected = i == 0;
                if (!brandCodeList.contains(brandCode) && !brandName.isEmpty() && !productArrayList.isEmpty()) { //To avoid repeated of same brand
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
                    SlideTherapistList.add(brandModelClass);
                    brandCodeList.add(brandCode);
                }
            }

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(fragmentTherapistBinding.rvTherapistList.getWindowToken(), 0);
            if (!SlideTherapistList.isEmpty()) {
                fragmentTherapistBinding.constraintNoData.setVisibility(View.GONE);
                fragmentTherapistBinding.constraintSortFilter.setVisibility(View.VISIBLE);
                fragmentTherapistBinding.rvTherapistList.setVisibility(View.VISIBLE);
                if (from_where.equalsIgnoreCase("call")) {
                    fragmentTherapistBinding.tvInfo.setVisibility(View.VISIBLE);
                    fragmentTherapistBinding.viewDummy2.setVisibility(View.VISIBLE);
                }
                previewAdapter = new PreviewAdapter(context, SlideTherapistList);
                fragmentTherapistBinding.rvTherapistList.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
                fragmentTherapistBinding.rvTherapistList.setAdapter(previewAdapter);
                Collections.sort(SlideTherapistList, Comparator.comparing(BrandModelClass::getBrandName));
            } else {
                fragmentTherapistBinding.constraintNoData.setVisibility(View.VISIBLE);
                fragmentTherapistBinding.constraintSortFilter.setVisibility(View.GONE);
                fragmentTherapistBinding.rvTherapistList.setVisibility(View.GONE);
                fragmentTherapistBinding.tvInfo.setVisibility(View.GONE);
                fragmentTherapistBinding.viewDummy2.setVisibility(View.GONE);
            }

        } catch (Exception ignored) {

        }
    }
    
}