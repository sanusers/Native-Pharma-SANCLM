package saneforce.sanzen.activity.previewPresentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.presentation.PresentationAdapter;
import saneforce.sanzen.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.FragmentHomePreviewBinding;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class MyPresentation extends Fragment {
    FragmentHomePreviewBinding customizedBinding;
    PresentationAdapter presentationAdapter;

    public static ArrayList<BrandModelClass.Presentation> SlideCustomizedList = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private PresentationDataDao presentationDataDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customizedBinding = FragmentHomePreviewBinding.inflate(inflater);
        View v = customizedBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        presentationDataDao = roomDB.presentationDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SlideCustomizedList = presentationDataDao.getPresentations();

        customizedBinding.tvAz.setOnClickListener(view -> {
            customizedBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_left_radius));
            customizedBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            customizedBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_right));
            customizedBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            presentationAdapter = new PresentationAdapter(requireContext(), SlideCustomizedList, "customized");
            customizedBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            customizedBinding.rvBrandList.setAdapter(presentationAdapter);
            Collections.sort(SlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        });

        customizedBinding.tvZa.setOnClickListener(view -> {
            customizedBinding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_right_radius));
            customizedBinding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            customizedBinding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_left));
            customizedBinding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            presentationAdapter = new PresentationAdapter(requireContext(), SlideCustomizedList, "customized");
            customizedBinding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            customizedBinding.rvBrandList.setAdapter(presentationAdapter);
            Collections.sort(SlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
        });

        populateAdapter();

        return v;
    }

    public void populateAdapter() {
        if (!SlideCustomizedList.isEmpty()) {
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
