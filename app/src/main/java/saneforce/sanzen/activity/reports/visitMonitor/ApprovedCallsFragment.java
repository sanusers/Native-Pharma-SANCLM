package saneforce.sanzen.activity.reports.visitMonitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import saneforce.sanzen.R;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class ApprovedCallsFragment extends Fragment {
    private RoomDB roomDB;
    MasterDataDao masterDataDao;

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_approved_calls, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();


        return v;

    }
