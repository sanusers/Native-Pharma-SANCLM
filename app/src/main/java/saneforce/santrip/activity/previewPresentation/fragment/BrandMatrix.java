package saneforce.santrip.activity.previewPresentation.fragment;

import static saneforce.santrip.activity.previewPresentation.PreviewActivity.BrandCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SelectedTab;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SlideCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SpecialityCode;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.from_where;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.previewBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.FragmentSpecialityPreviewBinding;
import saneforce.santrip.storage.SQLite;

public class BrandMatrix extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentSpecialityPreviewBinding brandMatrixBinding;
    SQLite sqLite;
    public static ArrayList<BrandModelClass> brandProductArrayList = new ArrayList<>();
    public static ArrayList<String> brandCodeList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static PreviewAdapter previewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        brandMatrixBinding = FragmentSpecialityPreviewBinding.inflate(inflater);
        View v = brandMatrixBinding.getRoot();
        sqLite = new SQLite(requireContext());

        if (from_where.equalsIgnoreCase("call")) {
            brandMatrixBinding.tvSelectDoctor.setVisibility(View.GONE);
            Log.v("gfddf",BrandCode + "----" + SlideCode);
            getSelectedMatrix(requireContext(), sqLite, BrandCode, SlideCode);
        } else {
            brandMatrixBinding.tvSelectDoctor.setVisibility(View.VISIBLE);
        }


        brandMatrixBinding.tvSelectDoctor.setOnClickListener(v1 -> {
            SelectedTab = "Matrix";
            previewBinding.fragmentSelectDrSide.setVisibility(View.VISIBLE);
        });

        return v;
    }

    public static void getSelectedMatrix(Context context, SQLite sqLite, String mappedBrands, String mappedSlides) {
        try {
            brandProductArrayList.clear();
            brandCodeList.clear();
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
                        //   Log.v("chek", productObject.getString("Speciality_Code") + "---" + selectedSpecialityCode + "----" +productObject.getString("Speciality_Code").contains(selectedSpecialityCode));
                        if (mappedBrands.contains(productObject.getString("Code")) && mappedSlides.contains(productObject.getString("Product_Detail_Code"))) {
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
                    brandProductArrayList.add(brandModelClass);
                    brandCodeList.add(brandCode);
                }
            }

            previewAdapter = new PreviewAdapter(context, brandProductArrayList);
            brandMatrixBinding.rvBrandList.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
            brandMatrixBinding.rvBrandList.setAdapter(previewAdapter);


        } catch (Exception ignored) {

        }
    }
}
