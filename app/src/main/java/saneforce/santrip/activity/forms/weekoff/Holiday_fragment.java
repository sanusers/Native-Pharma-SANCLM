package saneforce.santrip.activity.forms.weekoff;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.utility.TimeUtils;

public class Holiday_fragment extends Fragment {

    RecyclerView weeklist;
    TextView holy_year;
    ConstraintLayout constraintNoData;


    ArrayList<fromsmodelclass> listvalue = new ArrayList<>();
    SQLite sqLite;
    holiday_adapter holidayadapter;
    String current_year;
    CommonUtilsMethods commonUtilsMethods;
    String[] colr = {"#000000", "#00C689", "#3DA5F4", "#F1536E", "#FEB91A", "#FF7F50", "#09F1E3", "#F65EF8", "#C1BF16", "#934CD3", "#B92727", "#6A28BA", "#85A523", "#DBDB32"};
    String[] backcolr = {"#000000", "#EAFAF1", "#E8F5FC", "#FCE8EC", "#FCF8E8", "#FCF6E8", "#E8F9FC", "#FCEDFB", "#FAFAED", "#EEE3F7", "#FEF8F8", "#F2ECF9", "#F5F9E9", "#FBFBE7"};

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.holiday_fragment, container, false);
        weeklist = v.findViewById(R.id.weeklist);
        holy_year = v.findViewById(R.id.holy_year);
        constraintNoData = v.findViewById(R.id.constraint_no_data);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        sqLite = new SQLite(getActivity());

        holiday_avalabledetails();
        return v;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void holiday_avalabledetails() {
        try {

            String dub_colrline = "", month_name1 = "", monthname = "", colorline = "", backgrd_clr = "", holiy = "";
            int datapos = 0, datapos1 = 0;
            JSONArray jsonstock = sqLite.getMasterSyncDataByKey(Constants.HOLIDAY);

            if (jsonstock.length() > 0) {
                weeklist.setVisibility(View.VISIBLE);
                constraintNoData.setVisibility(View.GONE);
                for (int i = 0; i < jsonstock.length(); i++) {

                    JSONObject jsonObject = jsonstock.getJSONObject(i);
                    String Holiday_Date = (jsonObject.getString("Holiday_Date"));

                    if (!month_name1.equals(jsonObject.getString("month_name"))) {

                        monthname = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_25, jsonObject.getString("Holiday_Date")));
                        current_year = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_26, jsonObject.getString("Holiday_Date")));
                        month_name1 = (jsonObject.getString("month_name"));
                        datapos++;
                        dub_colrline = colr[datapos];

                        colorline = colr[datapos];
                        backgrd_clr = backcolr[datapos];
                    } else {
                        colorline = colr[datapos];
                        backgrd_clr = backcolr[datapos];
                        dub_colrline = "#FFFFFF";
                        monthname = "";
                    }
                    if (holiy.equals("")) {
                        holiy = current_year;
                        holy_year.setText(current_year);
                    }

                    String Hday = (jsonObject.getString("Hday"));
                    String Holiday_Name = (jsonObject.getString("Holiday_Name"));
                    String day_name = (jsonObject.getString("day_name"));

                    fromsmodelclass list = new fromsmodelclass(monthname, Hday, Holiday_Name, day_name, Holiday_Date, colorline, dub_colrline, backgrd_clr);
                    listvalue.add(list);

                    holidayadapter = new holiday_adapter(listvalue, getActivity());
                    weeklist.setAdapter(holidayadapter);
                    weeklist.setLayoutManager(new LinearLayoutManager(getActivity()));
                    holidayadapter.notifyDataSetChanged();
                }
            } else {
                weeklist.setVisibility(View.GONE);
                constraintNoData.setVisibility(View.VISIBLE);
                holy_year.setText(CommonUtilsMethods.getCurrentInstance("yyyy"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}