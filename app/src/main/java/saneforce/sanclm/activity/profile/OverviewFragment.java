package saneforce.sanclm.activity.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.databinding.FragmentOverviewBinding;


public class OverviewFragment extends Fragment {
    FragmentOverviewBinding overviewBinding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        overviewBinding = FragmentOverviewBinding.inflate(inflater);
        View view = overviewBinding.getRoot();
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
        } catch (Exception e) {

        }



       /* try {
            jsonArray = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + DcrCallTabLayoutActivity.TodayPlanSfCode);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if (CustomerProfile.tv_custCode.equalsIgnoreCase(jsonObject.getString("Code"))) {
                    overviewBinding.tvMob.setText(jsonObject.getString("Code"));
                }
            }
        } catch (Exception e) {

        }*/
    }
}
