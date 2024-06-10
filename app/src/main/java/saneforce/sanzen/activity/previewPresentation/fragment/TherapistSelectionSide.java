package saneforce.sanzen.activity.previewPresentation.fragment;

import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.previewBinding;
import static saneforce.sanzen.activity.previewPresentation.fragment.Therapist.getRequiredData;
import static saneforce.sanzen.activity.previewPresentation.fragment.Therapist.getSelectedTherapist;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentSelectFbSideBinding;
import saneforce.sanzen.response.SetupResponse;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class TherapistSelectionSide extends Fragment {
    private FragmentSelectFbSideBinding selectTherapistSideBinding;
    private final ArrayList<String> list_name = new ArrayList<>();
    private final ArrayList<String> list_code = new ArrayList<>();
    private ArrayAdapter<String> dataAdapter;
    private MasterDataDao masterDataDao;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        selectTherapistSideBinding = FragmentSelectFbSideBinding.inflate(inflater);
        View v = selectTherapistSideBinding.getRoot();
        RoomDB roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupAdapter();

        selectTherapistSideBinding.tvTagHeader.setText(requireContext().getString(R.string.therapist_selection));
        selectTherapistSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectTherapistSideBinding.imgClose.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(selectTherapistSideBinding.imgClose.getWindowToken(), 0);
            selectTherapistSideBinding.searchList.setText("");
            previewBinding.btnFinishDet.setVisibility(View.VISIBLE);
            previewBinding.fragmentSelectTherapistSide.setVisibility(View.GONE);
        });

        selectTherapistSideBinding.searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dataAdapter.getFilter().filter(editable.toString());
            }
        });

        selectTherapistSideBinding.selectListView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectTherapistSideBinding.searchList.setText("");
            if (list_name.get(i).equalsIgnoreCase("All") && list_code.get(i).isEmpty()) {
                getRequiredData(requireContext(), list_name.get(i), masterDataDao);
            } else {
                getSelectedTherapist(requireContext(), list_code.get(i), list_name.get(i), masterDataDao);
            }
            previewBinding.btnFinishDet.setVisibility(View.VISIBLE);
            previewBinding.fragmentSelectTherapistSide.setVisibility(View.GONE);
        });
        return selectTherapistSideBinding.getRoot();
    }

    private void SetupAdapter() {
        list_code.clear();
        list_name.clear();
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.THERAPTIC_SLIDE).getMasterSyncDataJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list_name.add(jsonObject.getString("Name"));
                list_code.add(jsonObject.getString("Code"));
            }
        } catch (Exception e) {
            Log.e("TherapistSelectionSide", "SetupAdapter: " + e.getMessage());
            e.printStackTrace();
        }
        dataAdapter = new ArrayAdapter<>(requireActivity(), R.layout.listview_items, list_name);
        selectTherapistSideBinding.selectListView.setAdapter(dataAdapter);
    }
}