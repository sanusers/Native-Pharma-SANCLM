package saneforce.sanzen.activity.presentation.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.sanzen.activity.presentation.presentation.adapter.PresentationAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.FragmentCommonPresentationBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class CommonPresentationFragment extends Fragment {
    private FragmentCommonPresentationBinding binding;
    private ArrayList<BrandModelClass.Presentation> savedPresentation = new ArrayList<>();
    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private PresentationDataDao presentationDataDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(requireActivity());
        roomDB = RoomDB.getDatabase(requireContext());
        presentationDataDao = roomDB.presentationDataDao();
        masterDataDao = roomDB.masterDataDao();
        savedPresentation = presentationDataDao.getPresentations();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCommonPresentationBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.getRoot().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        binding.createPresentationBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                startActivity(new Intent(requireContext(), CreatePresentationActivity.class));
            }
        });

        populateAdapter();
    }

    public void populateAdapter() {
        if (!savedPresentation.isEmpty()) {
            binding.constraintNoData.setVisibility(View.GONE);
            binding.presentationRecView.setVisibility(View.VISIBLE);
            PresentationAdapter presentationAdapter = new PresentationAdapter(requireContext(), savedPresentation, "presentation");
            binding.presentationRecView.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            binding.presentationRecView.setAdapter(presentationAdapter);
        } else {
            binding.constraintNoData.setVisibility(View.VISIBLE);
            binding.presentationRecView.setVisibility(View.GONE);
        }
    }

}