package saneforce.sanclm.activity.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.databinding.FragmentOverviewBinding;
import saneforce.sanclm.storage.SQLite;


public class OverviewFragment extends Fragment {
    FragmentOverviewBinding overviewBinding;
    SQLite sqLite;
    JSONArray jsonArray;
    JSONObject jsonObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        overviewBinding = FragmentOverviewBinding.inflate(inflater);
        View view = overviewBinding.getRoot();
        SetProfileData();
        return view;
    }

    private void SetProfileData() {

        overviewBinding.tvName.setText(DCRCallActivity.CallActivityCustDetails.get(0).getName());
        overviewBinding.tvDob.setText(DCRCallActivity.CallActivityCustDetails.get(0).getDob());
        overviewBinding.tvWedDate.setText(DCRCallActivity.CallActivityCustDetails.get(0).getWedding_date());
        overviewBinding.tvMob.setText(DCRCallActivity.CallActivityCustDetails.get(0).getMobile());
        overviewBinding.tvEmail.setText(DCRCallActivity.CallActivityCustDetails.get(0).getEmail());
        overviewBinding.tvQualify.setText(DCRCallActivity.CallActivityCustDetails.get(0).getQualification());
        overviewBinding.tvCategory.setText(DCRCallActivity.CallActivityCustDetails.get(0).getCategory());
        overviewBinding.tvSpeciality.setText(DCRCallActivity.CallActivityCustDetails.get(0).getSpecialist());
        overviewBinding.tvTerritory.setText(DCRCallActivity.CallActivityCustDetails.get(0).getTown_name());
        overviewBinding.tvAddress.setText(DCRCallActivity.CallActivityCustDetails.get(0).getAddress());



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
