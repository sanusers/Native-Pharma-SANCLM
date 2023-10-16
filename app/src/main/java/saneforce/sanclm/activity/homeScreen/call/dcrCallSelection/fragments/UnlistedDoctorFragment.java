package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.R;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;


public class UnlistedDoctorFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;
    EditText ed_search;
    Dialog dialogFilter;
    ImageButton iv_filter;
    ImageView img_close;
    Button btn_apply;
    SQLite sqLite;
    String SfCode, SfType;
    JSONArray jsonArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_unlisted_doctor, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);
        ed_search = v.findViewById(R.id.search_cust);
        iv_filter = v.findViewById(R.id.iv_filter);
        SfCode = SharedPref.getSfCode(getContext());
        SfType = SharedPref.getSfType(getContext());
        sqLite = new SQLite(getContext());

        custListArrayList.clear();
        try {
            if (SfType.equalsIgnoreCase("1")) {
                jsonArray = sqLite.getMasterSyncDataByKey("Unlisted_Doctor_" + SfCode);
            }

            Log.v("jsonArray", "--" + jsonArray.length() + "---" + jsonArray);
            if (jsonArray.length() == 0) {
                if (!jsonArray.toString().equalsIgnoreCase(Constants.NO_DATA_AVAILABLE)) {
                    Toast.makeText(getActivity(), "Kindly Select Again!", Toast.LENGTH_SHORT).show();
                    //  MasterSyncActivity.callList(sqLite, apiInterface, getApplicationContext(), "Doctor", "getdoctors", SfCode, SharedPref.getDivisionCode(TagCustSelectionList.this), selectedHqCode, SfType, SharedPref.getDesignationName(TagCustSelectionList.this), SharedPref.getStateCode(TagCustSelectionList.this), SharedPref.getSubdivCode(TagCustSelectionList.this));
                } else {
                    Toast.makeText(getActivity(), Constants.NO_DATA_AVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                custListArrayList.add(new CustList(jsonObject.getString("Name"), jsonObject.getString("Code"), "4", jsonObject.getString("CategoryName"), jsonObject.getString("SpecialtyName"),
                        jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), jsonObject.getString("GEOTagCnt"), jsonObject.getString("MaxGeoMap"), String.valueOf(i)));
            }
        } catch (Exception e) {
            Log.v("jsonarray", "-error--" + e);
        }

        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(), getContext(), custListArrayList);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);

        iv_filter.setOnClickListener(view -> {

            dialogFilter = new Dialog(getActivity());
            dialogFilter.setContentView(R.layout.popup_dcr_filter);
            dialogFilter.setCancelable(false);
            dialogFilter.show();

            img_close = dialogFilter.findViewById(R.id.img_close);
            btn_apply = dialogFilter.findViewById(R.id.btn_apply);

            img_close.setOnClickListener(view12 -> dialogFilter.dismiss());

            btn_apply.setOnClickListener(view1 -> dialogFilter.dismiss());
        });

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        return v;
    }

    private void filter(String text) {
        ArrayList<CustList> filterdNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) ||
                    s.getTown_name().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        adapterDCRCallSelection.filterList(filterdNames);
    }
}