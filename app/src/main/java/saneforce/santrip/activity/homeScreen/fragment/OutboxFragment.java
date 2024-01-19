package saneforce.santrip.activity.homeScreen.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import saneforce.santrip.activity.homeScreen.adapters.OutBoxAdapter;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.databinding.OutboxFragmentBinding;
import saneforce.santrip.storage.SQLite;


public class OutboxFragment extends Fragment {

    OutboxFragmentBinding binding;
    public static List<GroupModelClass> listOfflineCalls;
    OutBoxAdapter outBoxAdapter;
    SQLite sqLite;
    String callCount = "";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OutboxFragmentBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        sqLite = new SQLite(requireContext());
        callCount = String.valueOf(sqLite.getCountOffline());
        Log.v("outBoxCall", listOfflineCalls.size() + "----" + callCount + "----"  + listOfflineCalls.get(0).getGroupName() + "---" + listOfflineCalls.get(0).getChildItems().get(0).getDocName());
        outBoxAdapter = new OutBoxAdapter(getActivity(), listOfflineCalls);
        binding.Expandapleview.setGroupIndicator(null);
        binding.Expandapleview.setAdapter(outBoxAdapter);

        binding.txtCallcount.setText(callCount);

        return v;
    }

}