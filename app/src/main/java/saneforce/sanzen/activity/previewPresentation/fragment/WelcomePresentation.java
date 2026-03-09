package saneforce.sanzen.activity.previewPresentation.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentWelcomePresentationBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class WelcomePresentation extends Fragment {
    FragmentWelcomePresentationBinding welcomePresentationBinding;
    public static ArrayList<BrandModelClass> SlideWelcomeList = new ArrayList<>();
    PreviewAdapter previewAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private MasterDataDao masterDataDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        welcomePresentationBinding = FragmentWelcomePresentationBinding.inflate(inflater);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        RoomDB roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        getRequiredData();
        return welcomePresentationBinding.getRoot();
    }

    private void getRequiredData() {
        try {
            SlideWelcomeList.clear();
            JSONArray welcomeSlide = masterDataDao.getMasterDataTableOrNew(Constants.WELCOME_SLIDE).getMasterSyncDataJsonArray();
            ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();

            for (int i = 0; i < welcomeSlide.length(); i++) {
                JSONObject productObject = welcomeSlide.optJSONObject(i);
                if (productObject != null) {
                    BrandModelClass.Product product = getProductData(productObject);
                    if (product != null) {
                        productArrayList.add(product);
                    }
                }
            }

            if (!productArrayList.isEmpty()) {
                BrandModelClass brandModelClass = new BrandModelClass("Welcome", "", "0", 0, true, productArrayList);
                SlideWelcomeList.add(brandModelClass);
            }

            if (!SlideWelcomeList.isEmpty()) {
                welcomePresentationBinding.constraintNoData.setVisibility(View.GONE);
                welcomePresentationBinding.rvWelcomePresentation.setVisibility(View.VISIBLE);
                previewAdapter = new PreviewAdapter(requireContext(), SlideWelcomeList);
                welcomePresentationBinding.rvWelcomePresentation.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                welcomePresentationBinding.rvWelcomePresentation.setAdapter(previewAdapter);
            } else {
                welcomePresentationBinding.constraintNoData.setVisibility(View.VISIBLE);
                welcomePresentationBinding.rvWelcomePresentation.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e("HomeBrands", "getRequiredData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BrandModelClass.Product getProductData(JSONObject productObject) {
        try {
            String brandName = "Welcome";
            String code = "";
            String slideId = "";
            String presentationName = productObject.optString("Subject");
            String fileName = productObject.optString("Name");
            fileName = fileName.substring(fileName.indexOf('/') + 1);
            String slidePriority = productObject.getString("orderby");
            return new BrandModelClass.Product(presentationName, code, brandName, slideId, fileName, slidePriority, false,"");
        } catch (Exception e) {
            Log.e("GetProductData", "getProductData: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}