package saneforce.sanzen.activity.previewPresentation.fragment;

import android.os.Bundle;
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

import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentHomePreviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;



public class HomeBrands extends Fragment {
    FragmentHomePreviewBinding homePreviewBinding;
    public static ArrayList<BrandModelClass> SlideHomeBrandList = new ArrayList<>();
    ArrayList<String> brandCodeList = new ArrayList<>();
    PreviewAdapter previewAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homePreviewBinding = FragmentHomePreviewBinding.inflate(inflater);
        View v = homePreviewBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        getRequiredData();

        return v;
    }

    private void getRequiredData() {
        try {
            SlideHomeBrandList.clear();
            brandCodeList.clear();
            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();

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
                    SlideHomeBrandList.add(brandModelClass);
                    brandCodeList.add(brandCode);
                }
            }

            if (SlideHomeBrandList.size() > 0) {
                homePreviewBinding.constraintNoData.setVisibility(View.GONE);
                homePreviewBinding.rvBrandList.setVisibility(View.VISIBLE);
                previewAdapter = new PreviewAdapter(requireContext(), SlideHomeBrandList);
                homePreviewBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                homePreviewBinding.rvBrandList.setAdapter(previewAdapter);
            } else {
                homePreviewBinding.constraintNoData.setVisibility(View.VISIBLE);
                homePreviewBinding.rvBrandList.setVisibility(View.GONE);
            }

        } catch (Exception ignored) {

        }
    }
}
