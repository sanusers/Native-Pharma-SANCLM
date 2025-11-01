package saneforce.sanzen.activity.call.dcrCallSelection.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.storage.SharedPref;

public class HQSelector {

    public interface OnHQChangeListener {
        void onHQChange(String hqID, String hqName);
    }

    public static void setupHQSelector(@Nullable Fragment fragment, @NonNull Context context, TextView tv_hqName, ImageView img_drop_down, MasterDataDao masterDataDao, LayoutInflater inflater, OnHQChangeListener hqChangeListener) {
        if (SharedPref.getSfType(context).equalsIgnoreCase("2")) {
            if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                if ((WorkPlanFragment.mHQCode1 != null && !WorkPlanFragment.mHQCode1.isEmpty() && WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F")) && (WorkPlanFragment.mHQCode2 != null && !WorkPlanFragment.mHQCode2.isEmpty() && WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F"))) {
                    img_drop_down.setVisibility(View.VISIBLE);
                } else {
                    img_drop_down.setVisibility(View.GONE);
                }
                setupClickForOneBuild(fragment, context, tv_hqName, masterDataDao, inflater, hqChangeListener);
            } else {
                if (!SharedPref.getMultiHQCode(context).isEmpty()) {
                    String[] hqCodes = SharedPref.getMultiHQCode(context).split(",");
                    if (hqCodes.length > 1) {
                        img_drop_down.setVisibility(View.VISIBLE);
                    } else {
                        img_drop_down.setVisibility(View.GONE);
                    }
                }
                setupClickForMultiHQ(fragment, context, tv_hqName, masterDataDao, inflater, hqChangeListener);
            }
        }
    }

    private static void setupClickForOneBuild(@Nullable Fragment fragment, Context context, TextView tv_hqName, MasterDataDao masterDataDao, LayoutInflater inflater, OnHQChangeListener hqChangeListener) {
        tv_hqName.setOnClickListener(view -> {
            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                ArrayList<String> list = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if ((WorkPlanFragment.mHQCode1 != null && WorkPlanFragment.mHQCode1.equalsIgnoreCase(jsonObject.optString("id")) && WorkPlanFragment.mFwFlg1.equalsIgnoreCase("F"))
                                || (WorkPlanFragment.mHQCode2 != null && WorkPlanFragment.mHQCode2.equalsIgnoreCase(jsonObject.optString("id")) && WorkPlanFragment.mFwFlg2.equalsIgnoreCase("F"))) {
                            list.add(jsonObject.getString("name"));
                        }
                    }
                }
                showDialog(context, inflater, tv_hqName, jsonArray, list, hqChangeListener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideKeyboard(fragment, context, view);
        });
    }

    private static void setupClickForMultiHQ(@Nullable Fragment fragment, Context context, TextView tv_hqName, MasterDataDao masterDataDao, LayoutInflater inflater, OnHQChangeListener hqChangeListener) {
        tv_hqName.setOnClickListener(view -> {
            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                ArrayList<String> list = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (SharedPref.getMultiHQCode(context).contains(jsonObject.optString("id"))) {
                            list.add(jsonObject.optString("name"));
                        }
                    }
                }
                showDialog(context, inflater, tv_hqName, jsonArray, list, hqChangeListener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideKeyboard(fragment, context, view);
        });
    }

    public static void setupClickForHQ(@Nullable Fragment fragment, Context context, TextView tv_hqName, MasterDataDao masterDataDao, LayoutInflater inflater, OnHQChangeListener hqChangeListener) {
        tv_hqName.setOnClickListener(view -> {
            try {
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
                ArrayList<String> list = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        list.add(jsonObject.optString("name"));
                    }
                }
                showDialog(context, inflater, tv_hqName, jsonArray, list, hqChangeListener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideKeyboard(fragment, context, view);
        });
    }

    private static void showDialog(Context context, LayoutInflater inflater, TextView tv_hqName, JSONArray jsonArray, List<String> list, OnHQChangeListener hqChangeListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.dialog_listview, null);
        alertDialog.setView(dialogView);

        TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
        ListView listView = dialogView.findViewById(R.id.listView);
        SearchView searchView = dialogView.findViewById(R.id.searchET);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        headerTxt.setText(context.getString(R.string.select_hq));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new ArrayList<>(list));
        listView.setAdapter(adapter);
        AlertDialog dialog = alertDialog.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<String> filtered = new ArrayList<>();
                for (String item : list) {
                    if (item.toLowerCase().contains(s.toLowerCase())) {
                        filtered.add(item);
                    }
                }
                adapter.clear();
                adapter.addAll(filtered);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedHq = listView.getItemAtPosition(position).toString();
            tv_hqName.setText(selectedHq);
            String hqID = "", hqName = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.optString("name").equalsIgnoreCase(selectedHq)) {
                        hqID = jsonObject.optString("id");
                        hqName = jsonObject.optString("name");
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hqChangeListener.onHQChange(hqID, hqName);
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }

    private static void hideKeyboard(@Nullable Fragment fragment, Context context, View view) {
        Activity activity = null;
        if (fragment != null) {
            activity = fragment.getActivity();
        } else if (context instanceof Activity) {
            activity = (Activity) context;
        }

        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}
