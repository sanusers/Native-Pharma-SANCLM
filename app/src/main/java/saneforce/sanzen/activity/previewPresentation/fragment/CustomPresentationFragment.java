package saneforce.sanzen.activity.previewPresentation.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.FragmentCustomPresentationBinding;
import saneforce.sanzen.databinding.FragmentCustomerPresentationBinding;
import saneforce.sanzen.databinding.FragmentHomePreviewBinding;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class CustomPresentationFragment extends Fragment {
    private FragmentCustomPresentationBinding binding;
    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private PresentationDataDao presentationDataDao;

    public CustomPresentationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomDB = RoomDB.getDatabase(requireContext());
        presentationDataDao = roomDB.presentationDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomPresentationBinding.inflate(inflater);

        return binding.getRoot();
    }
}