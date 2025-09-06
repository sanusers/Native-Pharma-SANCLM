package saneforce.sanzen.activity.reports.visitMonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.FragmentDoctorVisitReportBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DoctorFragment extends Fragment {

    public FragmentDoctorVisitReportBinding binding;

    TextView headerTxt, doctorVisitTxt, dateTxt, totalDr, totalDrCnt, visited, visitedCnt, missed, missedCnt, FWDays, FWDaysCnt, callAvg, callAvgCnt, callCvg, callCvgCnt;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doctor_visit_report, container, false);
        headerTxt = v.findViewById(R.id.headerTxt);
        doctorVisitTxt = v.findViewById(R.id.doctorVisitTxt);
        dateTxt = v.findViewById(R.id.dateTxt);
        totalDr = v.findViewById(R.id.totalDr);
        totalDrCnt = v.findViewById(R.id.totalDrCnt);
        visited = v.findViewById(R.id.visited);
        visitedCnt = v.findViewById(R.id.visitedCnt);
        missed = v.findViewById(R.id.missed);
        missedCnt = v.findViewById(R.id.missedCnt);
        FWDays = v.findViewById(R.id.FWDays);
        FWDaysCnt = v.findViewById(R.id.FWDaysCnt);
        callAvg = v.findViewById(R.id.callAvg);
        callAvgCnt = v.findViewById(R.id.callAvgCnt);
        callCvg = v.findViewById(R.id.callCvg);
        callCvgCnt = v.findViewById(R.id.callCvgCnt);

        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());


        doctorVisitTxt.setText(String.format(SharedPref.getDrCap(requireContext()))+"Visit");
        totalDr.setText(String.format(getString(R.string.total))+SharedPref.getDrCap(requireContext()));
        dateTxt.setText(TimeUtils.FORMAT_23);


        return v;
    }
}
