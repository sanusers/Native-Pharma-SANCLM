package saneforce.sanzen.activity.previewPresentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.presentation.PresentationAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.FragmentHomePreviewBinding;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class Customized extends Fragment {
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
