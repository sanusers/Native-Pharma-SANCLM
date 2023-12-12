package saneforce.sanclm.activity.reports.dayReport.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanclm.activity.reports.ReportFragContainerActivity;
import saneforce.sanclm.activity.reports.dayReport.DataViewModel;
import saneforce.sanclm.activity.reports.dayReport.DayReportModel;
import saneforce.sanclm.activity.reports.dayReport.adapter.DayReportDetailAdapter;
import saneforce.sanclm.databinding.FragmentDayReportDetailBinding;


public class DayReportDetailFragment extends Fragment {

    FragmentDayReportDetailBinding binding;
    DayReportDetailAdapter adapter;
    DayReportModel dayReportModel;
    DataViewModel dataViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDayReportDetailBinding.inflate(inflater,container,false);
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        initialisation();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");

        adapter = new DayReportDetailAdapter(arrayList,getContext());
        binding.dayReportDetailRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.dayReportDetailRecView.setAdapter(adapter);

        return binding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    public void initialisation(){
            Type type = new TypeToken<DayReportModel>(){}.getType();
            dayReportModel = new Gson().fromJson(dataViewModel.getDetailedData().getValue(), type);

            ReportFragContainerActivity activity = (ReportFragContainerActivity) getActivity();
            Objects.requireNonNull(activity).title.setText("Day Report");

            int allCount = Integer.parseInt(dayReportModel.getDrs()) + Integer.parseInt(dayReportModel.getChm()) + Integer.parseInt(dayReportModel.getStk()) +
                    Integer.parseInt(dayReportModel.getHos());
            binding.name.setText(dayReportModel.getTerrWrk());
            binding.allCount.setText(String.valueOf(allCount));
            binding.drCount.setText(dayReportModel.getDrs());
            binding.cheCount.setText(dayReportModel.getChm());
            binding.stkCount.setText(dayReportModel.getStk());
            binding.unDrCount.setText(dayReportModel.getUdr());
            binding.cipCount.setText(dayReportModel.getCip());
            binding.hospCount.setText(dayReportModel.getHos());


    }

}