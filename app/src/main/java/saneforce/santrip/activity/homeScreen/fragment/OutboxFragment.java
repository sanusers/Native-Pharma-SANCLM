package saneforce.santrip.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.activity.homeScreen.adapters.OutBoxHeaderAdapter;
import saneforce.santrip.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.databinding.OutboxFragmentBinding;
import saneforce.santrip.storage.SQLite;


public class OutboxFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static OutboxFragmentBinding outBoxBinding;
    public static ArrayList<GroupModelClass> listDates = new ArrayList<>();
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SQLite sqLite;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        outBoxBinding = OutboxFragmentBinding.inflate(inflater, container, false);
        View v = outBoxBinding.getRoot();
        sqLite = new SQLite(requireContext());
        listDates = sqLite.getOutBoxDate();

        outBoxHeaderAdapter = new OutBoxHeaderAdapter(requireContext(), listDates);
        mLayoutManager = new LinearLayoutManager(requireContext());
        outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);

        outBoxBinding.clearCalls.setOnClickListener(v1 -> {
            sqLite.deleteOfflineCalls();
            listDates.clear();
            outBoxHeaderAdapter = new OutBoxHeaderAdapter(requireContext(), listDates);
            outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
            outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        });

        return v;
    }

}