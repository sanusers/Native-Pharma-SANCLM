package saneforce.sanzen.activity.call.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.FragmentOverviewBinding;


public class OverviewFragment extends Fragment {
    FragmentOverviewBinding overviewBinding;
    CommonUtilsMethods commonUtilsMethods;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        overviewBinding = FragmentOverviewBinding.inflate(inflater);
        View view = overviewBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        hideUnwantedView();
        SetProfileData();
        return view;
    }

    private void SetProfileData() {
        try {
            overviewBinding.tvName.setText(DCRCallActivity.CallActivityCustDetails.get(0).getName());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getDob().isEmpty())
                overviewBinding.tvDob.setText(DCRCallActivity.CallActivityCustDetails.get(0).getDob());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getWedding_date().isEmpty())
                overviewBinding.tvWedDate.setText(DCRCallActivity.CallActivityCustDetails.get(0).getWedding_date());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getMobile().isEmpty())
                overviewBinding.tvMob.setText(DCRCallActivity.CallActivityCustDetails.get(0).getMobile());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getEmail().isEmpty())
                overviewBinding.tvEmail.setText(DCRCallActivity.CallActivityCustDetails.get(0).getEmail());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getQualification().isEmpty())
                overviewBinding.tvQualify.setText(DCRCallActivity.CallActivityCustDetails.get(0).getQualification());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getCategory().isEmpty())
                overviewBinding.tvCategory.setText(DCRCallActivity.CallActivityCustDetails.get(0).getCategory());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getSpecialist().isEmpty())
                overviewBinding.tvSpeciality.setText(DCRCallActivity.CallActivityCustDetails.get(0).getSpecialist());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getTown_name().isEmpty())
                overviewBinding.tvTerritory.setText(DCRCallActivity.CallActivityCustDetails.get(0).getTown_name());
            if (!DCRCallActivity.CallActivityCustDetails.get(0).getAddress().isEmpty())
                overviewBinding.tvAddress.setText(DCRCallActivity.CallActivityCustDetails.get(0).getAddress());
        } catch (Exception ignored) {

        }
    }

    private void hideUnwantedView() {
        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()){
            case "1":
            case "4":
                break;
            case "2":
                overviewBinding.tvTagDob.setVisibility(View.GONE);
                overviewBinding.tvDob.setVisibility(View.GONE);
                overviewBinding.tvTagWedDate.setVisibility(View.GONE);
                overviewBinding.tvWedDate.setVisibility(View.GONE);
                overviewBinding.tvTagQualify.setVisibility(View.GONE);
                overviewBinding.tvQualify.setVisibility(View.GONE);
                overviewBinding.tvTagSpeciality.setVisibility(View.GONE);
                overviewBinding.tvSpeciality.setVisibility(View.GONE);
                break;
            case "3":
                overviewBinding.tvTagDob.setVisibility(View.GONE);
                overviewBinding.tvDob.setVisibility(View.GONE);
                overviewBinding.tvTagWedDate.setVisibility(View.GONE);
                overviewBinding.tvWedDate.setVisibility(View.GONE);
                overviewBinding.tvTagQualify.setVisibility(View.GONE);
                overviewBinding.tvQualify.setVisibility(View.GONE);
                overviewBinding.tvTagSpeciality.setVisibility(View.GONE);
                overviewBinding.tvSpeciality.setVisibility(View.GONE);
                overviewBinding.tvTagCategory.setVisibility(View.GONE);
                overviewBinding.tvCategory.setVisibility(View.GONE);
                overviewBinding.tvTagEmail.setVisibility(View.GONE);
                overviewBinding.tvEmail.setVisibility(View.GONE);
                break;
        }
    }
}
