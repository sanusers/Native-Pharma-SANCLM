package saneforce.sanzen.activity.previewPresentation.fragment;

import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.CusType;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.cus_code;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.presentation.adapter.PresentationAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.FragmentHomePreviewBinding;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;


public class CustomizedPresentationFragment extends Fragment {
    private FragmentHomePreviewBinding binding;
    PresentationAdapter presentationAdapter;
    public static ArrayList<BrandModelClass.Presentation> SlideCustomList = new ArrayList<>();
    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private PresentationDataDao presentationDataDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomDB = RoomDB.getDatabase(requireContext());
        presentationDataDao = roomDB.presentationDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SlideCustomList = presentationDataDao.getPresentations(CusType, cus_code);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomePreviewBinding.inflate(inflater);

        binding.tvAz.setOnClickListener(view -> {
            binding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_left_radius));
            binding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            binding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_right));
            binding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            presentationAdapter = new PresentationAdapter(requireContext(), SlideCustomList, "customized");
            binding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            binding.rvBrandList.setAdapter(presentationAdapter);
            Collections.sort(SlideCustomList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        });

        binding.tvZa.setOnClickListener(view -> {
            binding.tvZa.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_right_radius));
            binding.tvZa.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            binding.tvAz.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_left));
            binding.tvAz.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
            presentationAdapter = new PresentationAdapter(requireContext(), SlideCustomList, "customized");
            binding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            binding.rvBrandList.setAdapter(presentationAdapter);
            Collections.sort(SlideCustomList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
        });

        populateAdapter();

        return binding.getRoot();
    }

    public void populateAdapter() {
        if (!SlideCustomList.isEmpty()) {
            binding.constraintNoData.setVisibility(View.GONE);
            binding.rvBrandList.setVisibility(View.VISIBLE);
            presentationAdapter = new PresentationAdapter(requireContext(), SlideCustomList, "customized");
            binding.rvBrandList.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            binding.rvBrandList.setAdapter(presentationAdapter);
        } else {
            binding.constraintNoData.setVisibility(View.VISIBLE);
            binding.rvBrandList.setVisibility(View.GONE);
        }
    }

}