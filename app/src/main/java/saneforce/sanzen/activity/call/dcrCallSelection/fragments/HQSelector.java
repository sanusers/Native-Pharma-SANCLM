package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.storage.SharedPref;

public class HQSelector {

    public interface OnHQChangeListener {
        void onHQChange();
    }

    public static void setupHQSelector(Fragment fragment, TextView tv_hqName, ImageView img_drop_down, MasterDataDao masterDataDao, LayoutInflater inflater, OnHQChangeListener hqChangeListener) {
        if (SharedPref.getSfType(fragment.requireContext()).equalsIgnoreCase("2")) {
            if (SharedPref.getOneBuild(fragment.requireContext()).equalsIgnoreCase("0")) {
                if ((WorkPlanFragment.mHQCode1 != null && !WorkPlanFragment.mHQCode1.isEmpty() && WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F")) && (WorkPlanFragment.mHQCode2 != null && !WorkPlanFragment.mHQCode2.isEmpty() && WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F"))) {
                    img_drop_down.setVisibility(View.VISIBLE);
                } else {
                    img_drop_down.setVisibility(View.GONE);
                }
                setupClickForOneBuild(fragment, tv_hqName, img_drop_down, masterDataDao, inflater, hqChangeListener);
            } else {
                if (!SharedPref.getMultiHQCode(fragment.requireContext()).isEmpty()) {
                    String[] hqCodes = SharedPref.getMultiHQCode(fragment.requireContext()).split(",");
                    if (hqCodes.length > 1) {
                        img_drop_down.setVisibility(View.VISIBLE);
                    } else {
                        img_drop_down.setVisibility(View.GONE);
                    }
                }
                setupClickForMultiHQ(fragment, tv_hqName, masterDataDao, inflater, hqChangeListener);
            }
        }
    }

    private static void setupClickForOneBuild(Fragment fragment, TextView tv_hqName, ImageView img_drop_down, MasterDataDao masterDataDao, LayoutInflater inflater, OnHQChangeListener hqChangeListener) {
        tv_hqName.setOnClickListener(view -> {
            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                ArrayList<String> list = new ArrayList<>();

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if ((WorkPlanFragment.mHQCode1 != null && WorkPlanFragment.mHQCode1.equalsIgnoreCase(jsonObject.optString("id")) && WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F")) || (WorkPlanFragment.mHQCode2 != null && WorkPlanFragment.mHQCode2.equalsIgnoreCase(jsonObject.optString("id")) && WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F"))) {
                            list.add(jsonObject.getString("name"));
                        }
                    }
                }

                showDialog(fragment, inflater, tv_hqName, jsonArray, list, hqChangeListener);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            UtilityClass.hideKeyboard(fragment.requireActivity());
        });
    }

    private static void setupClickForMultiHQ(Fragment fragment, TextView tv_hqName, MasterDataDao masterDataDao, LayoutInflater inflater, OnHQChangeListener hqChangeListener) {
        tv_hqName.setOnClickListener(view -> {
            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                ArrayList<String> list = new ArrayList<>();

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (SharedPref.getMultiHQCode(fragment.requireContext()).contains(jsonObject.optString("id"))) {
                            list.add(jsonObject.optString("name"));
                        }
                    }
                }

                showDialog(fragment, inflater, tv_hqName, jsonArray, list, hqChangeListener);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            UtilityClass.hideKeyboard(fragment.requireActivity());
        });
    }

    private static void showDialog(Fragment fragment, LayoutInflater inflater, TextView tv_hqName, JSONArray jsonArray, List<String> list, OnHQChangeListener hqChangeListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragment.requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_listview, null);
        alertDialog.setView(dialogView);

        TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
        ListView listView = dialogView.findViewById(R.id.listView);
        SearchView searchView = dialogView.findViewById(R.id.searchET);

        headerTxt.setText(fragment.getResources().getText(R.string.select_hq));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(fragment.requireContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        AlertDialog dialog = alertDialog.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedHq = listView.getItemAtPosition(position).toString();
            tv_hqName.setText(selectedHq);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.optString("name").equalsIgnoreCase(selectedHq)) {
                        DcrCallTabLayoutActivity.TodayPlanSfCode = jsonObject.optString("id");
                        DcrCallTabLayoutActivity.TodayPlanSfName = jsonObject.optString("name");
                        SharedPref.saveHq(fragment.requireContext(), DcrCallTabLayoutActivity.TodayPlanSfName, DcrCallTabLayoutActivity.TodayPlanSfCode);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hqChangeListener.onHQChange();
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }
}
