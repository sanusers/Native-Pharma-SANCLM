package saneforce.sanclm.activity.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.sanclm.R;


public class OverviewFragment extends Fragment {
    TextView tv_name, tv_address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_overview, container, false);
        tv_name = v.findViewById(R.id.tv_name);
        tv_address = v.findViewById(R.id.tv_address);
        tv_name.setText(CustomerProfile.tv_custName);
        tv_address.setText(CustomerProfile.tv_cust_area);
        return v;
    }
}
