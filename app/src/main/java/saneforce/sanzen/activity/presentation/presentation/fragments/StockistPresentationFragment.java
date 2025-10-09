package saneforce.sanzen.activity.presentation.presentation.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import saneforce.sanzen.activity.presentation.customerSelection.CustomerSelectionActivity;
import saneforce.sanzen.activity.presentation.presentation.ShowSideScreenListener;
import saneforce.sanzen.activity.presentation.presentation.adapter.PresentationAdapter;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.FragmentCustomerPresentationBinding;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class StockistPresentationFragment extends Fragment {
    private FragmentCustomerPresentationBinding binding;
    private ArrayList<BrandModelClass.Presentation> savedPresentation = new ArrayList<>();
    private RoomDB roomDB;
    private PresentationDataDao presentationDataDao;
    private final ShowSideScreenListener showSideScreenListener;

    public StockistPresentationFragment(ShowSideScreenListener showSideScreenListener) {
        this.showSideScreenListener = showSideScreenListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(requireContext());
        presentationDataDao = roomDB.presentationDataDao();
        savedPresentation = presentationDataDao.getPresentations("3");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomerPresentationBinding.inflate(inflater);
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
                Intent intent = new Intent(requireContext(), CustomerSelectionActivity.class);
                intent.putExtra(CustomerSelectionActivity.CUSTOMER_TYPE, Constants.STOCKIEST);
//            intent.putExtra(CustomerSelectionActivity.CUSTOMER_TYPE, Constants.STOCKIEST_MAS);
                startActivity(intent);
            }
        });

        populateAdapter();
    }

    public void populateAdapter() {
        if(!savedPresentation.isEmpty()) {
            binding.constraintNoData.setVisibility(View.GONE);
            binding.presentationRecView.setVisibility(View.VISIBLE);
            PresentationAdapter presentationAdapter = new PresentationAdapter(requireContext(), savedPresentation, "custom", SharedPref.getStkCap(requireContext()), showCustomersClickListener);
            binding.presentationRecView.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            binding.presentationRecView.setAdapter(presentationAdapter);
        }else {
            binding.constraintNoData.setVisibility(View.VISIBLE);
            binding.presentationRecView.setVisibility(View.GONE);
        }
    }

    private final PresentationAdapter.ShowCustomersClickListener showCustomersClickListener = new PresentationAdapter.ShowCustomersClickListener() {
        @Override
        public void onClick(String presentationName) {
            showSideScreenListener.onClick(Constants.STOCKIEST, presentationName);
//            showSideScreenListener.onClick(Constants.STOCKIEST_MAS, presentationName);
        }
    };

}