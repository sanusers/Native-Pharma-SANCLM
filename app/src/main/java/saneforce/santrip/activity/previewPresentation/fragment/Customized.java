package saneforce.santrip.activity.previewPresentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.activity.presentation.presentation.PresentationAdapter;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.FragmentHomePreviewBinding;
import saneforce.santrip.storage.SQLite;

public class Customized extends Fragment {
    FragmentHomePreviewBinding customizedBinding;
    SQLite sqLite;
    PresentationAdapter presentationAdapter;

    public static ArrayList<BrandModelClass.Presentation> SlideCustomizedList = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customizedBinding = FragmentHomePreviewBinding.inflate(inflater);
        View v = customizedBinding.getRoot();
        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SlideCustomizedList = sqLite.getPresentationData();

        populateAdapter();

        return v;
    }

    public void populateAdapter() {
        if (SlideCustomizedList.size() > 0) {
            customizedBinding.constraintNoData.setVisibility(View.GONE);
            customizedBinding.rvBrandList.setVisibility(View.VISIBLE);
            presentationAdapter = new PresentationAdapter(requireContext(), SlideCustomizedList, "customized");
            customizedBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            customizedBinding.rvBrandList.setAdapter(presentationAdapter);
        } else {
            customizedBinding.constraintNoData.setVisibility(View.VISIBLE);
            customizedBinding.rvBrandList.setVisibility(View.GONE);
        }
    }
}
