package saneforce.sanzen.activity.previewPresentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.presentation.ShowSideScreenListener;
import saneforce.sanzen.activity.presentation.presentation.adapter.PresentationAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentCustomPreviewBinding;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class CustomPreviewFragment extends Fragment {
    private FragmentCustomPreviewBinding binding;
    private PresentationAdapter commonPresentationAdapter;
    private PresentationAdapter drPresentationAdapter;
    private PresentationAdapter chmPresentationAdapter;
    private PresentationAdapter stkPresentationAdapter;
    private PresentationAdapter unDrPresentationAdapter;
    private PresentationAdapter cipPresentationAdapter;
    private PresentationAdapter hosPresentationAdapter;
    public static ArrayList<BrandModelClass.Presentation> commonSlideCustomizedList = new ArrayList<>();
    public static ArrayList<BrandModelClass.Presentation> drSlideCustomizedList = new ArrayList<>();
    public static ArrayList<BrandModelClass.Presentation> chmSlideCustomizedList = new ArrayList<>();
    public static ArrayList<BrandModelClass.Presentation> stkSlideCustomizedList = new ArrayList<>();
    public static ArrayList<BrandModelClass.Presentation> unDrSlideCustomizedList = new ArrayList<>();
    public static ArrayList<BrandModelClass.Presentation> cipSlideCustomizedList = new ArrayList<>();
    public static ArrayList<BrandModelClass.Presentation> hosSlideCustomizedList = new ArrayList<>();
    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private PresentationDataDao presentationDataDao;
    private boolean drNeed, chmNeed, stkNeed, unDrNeed, cipNeed, hosNeed;
    private String drCap, chmCap, stkCap, unDrCap, cipCap, hosCap;
    private ShowSideScreenListener showSideScreenListener;

    public CustomPreviewFragment() {
    }

    public CustomPreviewFragment(ShowSideScreenListener showSideScreenListener) {
        this.showSideScreenListener = showSideScreenListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRequiredData();
        roomDB = RoomDB.getDatabase(requireContext());
        presentationDataDao = roomDB.presentationDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        populateList();
    }

    private void getRequiredData() {
        drNeed = SharedPref.getDrNeed(requireContext()).equals("0");
        chmNeed = SharedPref.getChmNeed(requireContext()).equals("0");
        stkNeed = SharedPref.getStkNeed(requireContext()).equals("0");
        unDrNeed = SharedPref.getUnlNeed(requireContext()).equals("0");
        cipNeed = SharedPref.getCipNeed(requireContext()).equals("0");
        hosNeed = SharedPref.getHospNeed(requireContext()).equals("0");

        drCap = SharedPref.getDrCap(requireContext());
        chmCap = SharedPref.getChmCap(requireContext());
        stkCap = SharedPref.getStkCap(requireContext());
        unDrCap = SharedPref.getUNLcap(requireContext());
        cipCap = SharedPref.getCipCaption(requireContext());
        hosCap = SharedPref.getHospCaption(requireContext());
    }

    private void populateList() {
        commonSlideCustomizedList = presentationDataDao.getPresentations();
        Collections.sort(commonSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        if(drNeed) {
            drSlideCustomizedList = presentationDataDao.getPresentations("1");
            Collections.sort(drSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        }
        if(chmNeed) {
            chmSlideCustomizedList = presentationDataDao.getPresentations("2");
            Collections.sort(chmSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        }
        if(stkNeed) {
            stkSlideCustomizedList = presentationDataDao.getPresentations("3");
            Collections.sort(stkSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        }
        if(unDrNeed) {
            unDrSlideCustomizedList = presentationDataDao.getPresentations("4");
            Collections.sort(unDrSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        }
        if(cipNeed) {
            cipSlideCustomizedList = presentationDataDao.getPresentations("5");
            Collections.sort(cipSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        }
        if(hosNeed) {
            hosSlideCustomizedList = presentationDataDao.getPresentations("6");
            Collections.sort(hosSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomPreviewBinding.inflate(inflater);
        populateAdapter();
        setUpSort();
        return binding.getRoot();
    }

    private void populateAdapter() {
        if(!commonSlideCustomizedList.isEmpty()) {
            binding.sortFilterCommon.setVisibility(View.VISIBLE);
            binding.noDataCommon.setVisibility(View.GONE);
            binding.rvBrandListCommon.setVisibility(View.VISIBLE);
            commonPresentationAdapter = new PresentationAdapter(requireContext(), commonSlideCustomizedList, "customized");
            binding.rvBrandListCommon.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
            binding.rvBrandListCommon.setAdapter(commonPresentationAdapter);
        }else {
            binding.noDataCommon.setVisibility(View.VISIBLE);
            binding.rvBrandListCommon.setVisibility(View.GONE);
            binding.sortFilterCommon.setVisibility(View.GONE);
        }
        if(drNeed) {
            binding.tvDr.setText(drCap);
            binding.rlDr.setVisibility(View.VISIBLE);
            if(!drSlideCustomizedList.isEmpty()) {
                binding.sortFilterDr.setVisibility(View.VISIBLE);
                binding.noDataDr.setVisibility(View.GONE);
                binding.rvBrandListDr.setVisibility(View.VISIBLE);
                drPresentationAdapter = new PresentationAdapter(requireContext(), drSlideCustomizedList, "preview", presentationName -> {
                    showSideScreenListener.onClick(Constants.DOCTOR_MAS, presentationName);
                });
                binding.rvBrandListDr.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                binding.rvBrandListDr.setAdapter(drPresentationAdapter);
            }else {
                binding.noDataDr.setVisibility(View.VISIBLE);
                binding.rvBrandListDr.setVisibility(View.GONE);
                binding.sortFilterDr.setVisibility(View.GONE);
            }
        }else {
            binding.rlDr.setVisibility(View.GONE);
        }
        if(chmNeed) {
            binding.tvChm.setText(chmCap);
            binding.rlChm.setVisibility(View.VISIBLE);
            if(!chmSlideCustomizedList.isEmpty()) {
                binding.sortFilterChm.setVisibility(View.VISIBLE);
                binding.noDataChm.setVisibility(View.GONE);
                binding.rvBrandListChm.setVisibility(View.VISIBLE);
                chmPresentationAdapter = new PresentationAdapter(requireContext(), chmSlideCustomizedList, "preview", presentationName -> {
                    showSideScreenListener.onClick(Constants.CHEMIST_MAS, presentationName);
                });
                binding.rvBrandListChm.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                binding.rvBrandListChm.setAdapter(chmPresentationAdapter);
            }else {
                binding.noDataChm.setVisibility(View.VISIBLE);
                binding.rvBrandListChm.setVisibility(View.GONE);
                binding.sortFilterChm.setVisibility(View.GONE);
            }
        }else {
            binding.rlChm.setVisibility(View.GONE);
        }
        if(stkNeed) {
            binding.tvStk.setText(stkCap);
            binding.rlStk.setVisibility(View.VISIBLE);
            if(!stkSlideCustomizedList.isEmpty()) {
                binding.sortFilterStk.setVisibility(View.VISIBLE);
                binding.noDataStk.setVisibility(View.GONE);
                binding.rvBrandListStk.setVisibility(View.VISIBLE);
                stkPresentationAdapter = new PresentationAdapter(requireContext(), stkSlideCustomizedList, "preview", presentationName -> {
                    showSideScreenListener.onClick(Constants.STOCKIEST_MAS, presentationName);
                });
                binding.rvBrandListStk.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                binding.rvBrandListStk.setAdapter(stkPresentationAdapter);
            }else {
                binding.noDataStk.setVisibility(View.VISIBLE);
                binding.sortFilterStk.setVisibility(View.GONE);
                binding.rvBrandListStk.setVisibility(View.GONE);
            }
        }else {
            binding.rlStk.setVisibility(View.GONE);
        }
        if(unDrNeed) {
            binding.tvUnlDr.setText(unDrCap);
            binding.rlUnlDr.setVisibility(View.VISIBLE);
            if(!unDrSlideCustomizedList.isEmpty()) {
                binding.sortFilterUnlDr.setVisibility(View.VISIBLE);
                binding.noDataUnlDr.setVisibility(View.GONE);
                binding.rvBrandListUnlDr.setVisibility(View.VISIBLE);
                unDrPresentationAdapter = new PresentationAdapter(requireContext(), unDrSlideCustomizedList, "preview", presentationName -> {
                    showSideScreenListener.onClick(Constants.UNLISTED_DOCTOR_MAS, presentationName);
                });
                binding.rvBrandListUnlDr.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                binding.rvBrandListUnlDr.setAdapter(unDrPresentationAdapter);
            }else {
                binding.noDataUnlDr.setVisibility(View.VISIBLE);
                binding.rvBrandListUnlDr.setVisibility(View.GONE);
                binding.sortFilterUnlDr.setVisibility(View.GONE);
            }
        }else {
            binding.rlUnlDr.setVisibility(View.GONE);
        }
        if(cipNeed) {
            binding.tvCip.setText(cipCap);
            binding.rlCip.setVisibility(View.VISIBLE);
            if(!cipSlideCustomizedList.isEmpty()) {
                binding.sortFilterCip.setVisibility(View.VISIBLE);
                binding.noDataCip.setVisibility(View.GONE);
                binding.rvBrandListCip.setVisibility(View.VISIBLE);
                cipPresentationAdapter = new PresentationAdapter(requireContext(), cipSlideCustomizedList, "preview", presentationName -> {
                    showSideScreenListener.onClick(Constants.CIP, presentationName);
                });
                binding.rvBrandListCip.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                binding.rvBrandListCip.setAdapter(cipPresentationAdapter);
            }else {
                binding.noDataCip.setVisibility(View.VISIBLE);
                binding.rvBrandListCip.setVisibility(View.GONE);
                binding.sortFilterCip.setVisibility(View.GONE);
            }
        }else {
            binding.rlCip.setVisibility(View.GONE);
        }
        if(hosNeed) {
            binding.tvHos.setText(hosCap);
            binding.rlHos.setVisibility(View.VISIBLE);
            if(!hosSlideCustomizedList.isEmpty()) {
                binding.sortFilterHos.setVisibility(View.VISIBLE);
                binding.noDataHos.setVisibility(View.GONE);
                binding.rvBrandListHos.setVisibility(View.VISIBLE);
                hosPresentationAdapter = new PresentationAdapter(requireContext(), hosSlideCustomizedList, "preview", presentationName -> {
                    showSideScreenListener.onClick(Constants.HOSPITAL, presentationName);
                });
                binding.rvBrandListHos.setLayoutManager(new GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false));
                binding.rvBrandListHos.setAdapter(hosPresentationAdapter);
            }else {
                binding.noDataHos.setVisibility(View.VISIBLE);
                binding.rvBrandListHos.setVisibility(View.GONE);
                binding.sortFilterHos.setVisibility(View.GONE);
            }
        }else {
            binding.rlHos.setVisibility(View.GONE);
        }
    }

    private void setUpSort() {
        binding.tvAzCommon.setOnClickListener(view -> {
            sortChange(binding.tvAzCommon, binding.tvZaCommon, true);
            Collections.sort(commonSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
            commonPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvZaCommon.setOnClickListener(view -> {
            sortChange(binding.tvZaCommon, binding.tvAzCommon, false);
            Collections.sort(commonSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
            commonPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvAzDr.setOnClickListener(view -> {
            sortChange(binding.tvAzDr, binding.tvZaDr, true);
            Collections.sort(drSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
            drPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvZaDr.setOnClickListener(view -> {
            sortChange(binding.tvZaDr, binding.tvAzDr, false);
            Collections.sort(drSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
            drPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvAzChm.setOnClickListener(view -> {
            sortChange(binding.tvAzChm, binding.tvZaChm, true);
            Collections.sort(chmSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
            chmPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvZaChm.setOnClickListener(view -> {
            sortChange(binding.tvZaChm, binding.tvAzChm, false);
            Collections.sort(chmSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
            chmPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvAzStk.setOnClickListener(view -> {
            sortChange(binding.tvAzStk, binding.tvZaStk, true);
            Collections.sort(stkSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
            stkPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvZaStk.setOnClickListener(view -> {
            sortChange(binding.tvZaStk, binding.tvAzStk, false);
            Collections.sort(stkSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
            stkPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvAzUnlDr.setOnClickListener(view -> {
            sortChange(binding.tvAzUnlDr, binding.tvZaUnlDr, true);
            Collections.sort(unDrSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
            unDrPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvZaUnlDr.setOnClickListener(view -> {
            sortChange(binding.tvZaUnlDr, binding.tvAzUnlDr, false);
            Collections.sort(unDrSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
            unDrPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvAzCip.setOnClickListener(view -> {
            sortChange(binding.tvAzCip, binding.tvZaCip, true);
            Collections.sort(cipSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
            cipPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvZaCip.setOnClickListener(view -> {
            sortChange(binding.tvZaCip, binding.tvAzCip, false);
            Collections.sort(cipSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
            cipPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvAzHos.setOnClickListener(view -> {
            sortChange(binding.tvAzHos, binding.tvZaHos, true);
            Collections.sort(hosSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName));
            hosPresentationAdapter.notifyDataSetChanged();
        });

        binding.tvZaHos.setOnClickListener(view -> {
            sortChange(binding.tvZaHos, binding.tvAzHos, false);
            Collections.sort(hosSlideCustomizedList, Comparator.comparing(BrandModelClass.Presentation::getPresentationName).reversed());
            hosPresentationAdapter.notifyDataSetChanged();
        });

    }

    private void sortChange(TextView tv1, TextView tv2, boolean az) {
        if(az) {
            tv1.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_left_radius));
            tv1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            tv2.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_right));
            tv2.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
        }else {
            tv1.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_purple_right_radius));
            tv1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            tv2.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_left));
            tv2.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_purple));
        }
    }

}