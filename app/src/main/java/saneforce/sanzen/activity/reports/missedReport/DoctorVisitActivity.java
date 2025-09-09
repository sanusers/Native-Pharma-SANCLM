package saneforce.sanzen.activity.reports.missedReport;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.DCRFillteredModelClass;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.FillteredAdapter;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityDoctorVisitBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class DoctorVisitActivity extends AppCompatActivity {
    ActivityDoctorVisitBinding binding;
    DoctorVisitAdapter adapter;
    final List<DoctorVisitItem> doctorList = new ArrayList<>();
    Dialog dialogFilter;
    //AdapterDCRCallSelection adapterDCRCallSelection;
    RecyclerView rv_list;
    //ArrayList<CustList> custListArrayList = new ArrayList<>();
    ArrayList<DoctorVisitItem> FilltercustArraList = new ArrayList<>();
    ArrayList<DoctorVisitItem> filteredNames = new ArrayList<>();
    ImageView img_close, img_del;
    TextView tv_hqName, tv_add_condition;
    Button btn_apply, btn_clear;
    //EditText ed_search;
   // String specialityCode = "", categoryCode = "", territoryCode = "", classCode = "";
    String specialityName = "", categoryName = "", territoryName = "", className = "";
    TextView tvSpec, tvCate, tvTerritory, tvClass;
    JSONObject jsonObject;
    ArrayList<DCRFillteredModelClass> filterSelectionList = new ArrayList<>();
    ApiInterface apiInterface;
    ConstraintLayout constraintLayout;
    ListView lv_spec, lv_cate, lv_terr, lv_class;
    private MasterDataDao masterDataDao;
    private RoomDB roomDB;
    public void afterTextChanged(Editable editable) {
        filter(editable.toString());
    }



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorVisitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String drCaption = SharedPref.getDrCap(this);
        binding.toolbarTitle.setText(" Missed " + drCaption);
        adapter = new DoctorVisitAdapter(this, doctorList);
        binding.recyclerDoctorVisit.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDoctorVisit.setAdapter(adapter);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        binding.imageBack.setOnClickListener(v -> finish());
        binding.missedfilter.setOnClickListener(v -> {
            customisedMissedFilter();
        });
        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());

                if (charSequence.length() > 0) {
                    binding.searchClearIcon.setVisibility(View.VISIBLE);
                } else {
                    binding.searchClearIcon.setVisibility(View.GONE);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.searchClearIcon.setOnClickListener(v -> {
            binding.searchET.setText("");
            binding.searchClearIcon.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(binding.searchET.getWindowToken(), 0);
            }
        });


            String sfcode =getIntent().getStringExtra("sfcode" );
            String date =getIntent().getStringExtra("date" );
    //        String doctorArrayString = getIntent().getStringExtra("doctor_array");
            DoctorVisitDao visitDao = roomDB.doctorVisitDao();
            String doctorArrayString =visitDao.getVisitValues(sfcode, date);
            if (doctorArrayString != null) {
                try {
                    JSONArray jsonArray = new JSONArray(doctorArrayString);

                    // Parse each JSON object into your DoctorVisitItem model
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                    // Example assuming DoctorVisitItem has a constructor or setters:
                    DoctorVisitItem item = new DoctorVisitItem(obj.optString("ListedDr_Name"), obj.optString("territory_Name"), obj.optString("ListedDrCode"), obj.optString("Doc_QuaName"), obj.optString("Doc_Cat_SName"), obj.optString("Doc_Special_SName"), obj.optString("Doc_ClsSName"));

                    doctorList.add(item);
                }
                adapter.updateData(doctorList);

                binding.missedtittle.setText("Missed: " + doctorList.size());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void customisedMissedFilter() {
        dialogFilter = new Dialog(this);
        dialogFilter.setContentView(R.layout.doctorvisit_filter);
        Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilter.setCancelable(false);
        dialogFilter.show();
        //ed_search = dialogFilter.findViewById(R.id.search_cust);
        img_close = dialogFilter.findViewById(R.id.img_close);
        img_del = dialogFilter.findViewById(R.id.img_del);
        btn_apply = dialogFilter.findViewById(R.id.btn_apply);
        btn_clear = dialogFilter.findViewById(R.id.btn_clear);
        tv_add_condition = dialogFilter.findViewById(R.id.btn_add_condition);
        tvSpec = dialogFilter.findViewById(R.id.constraint_speciality);
        tvTerritory = dialogFilter.findViewById(R.id.constraint_territory);
        tvCate = dialogFilter.findViewById(R.id.constraint_category);
        tvClass = dialogFilter.findViewById(R.id.constraint_class);

        lv_spec = dialogFilter.findViewById(R.id.lv_spec);
        lv_cate = dialogFilter.findViewById(R.id.lv_category);
        lv_terr = dialogFilter.findViewById(R.id.lv_territory);
        lv_class = dialogFilter.findViewById(R.id.lv_class);
        tvSpec.setVisibility(View.VISIBLE);
        tvCate.setVisibility(View.VISIBLE);
        tv_add_condition.setVisibility(View.VISIBLE);
        img_del.setVisibility(View.GONE);

        if (!territoryName.isEmpty()) {
            tvTerritory.setVisibility(View.VISIBLE);
            img_del.setVisibility(View.VISIBLE);
        } else {
            tvTerritory.setVisibility(View.GONE);
            img_del.setVisibility(View.GONE);
        }

        if (!className.isEmpty()) {
            if (territoryName.isEmpty()) {
                tvTerritory.setVisibility(View.INVISIBLE);
            } else {
                tv_add_condition.setVisibility(View.GONE);
            }
            tvClass.setVisibility(View.VISIBLE);
        } else {
            tvClass.setVisibility(View.GONE);
        }

        constraintLayout = dialogFilter.findViewById(R.id.constraint_btns);
        img_close.setOnClickListener(view12 -> dialogFilter.dismiss());

        btn_clear.setOnClickListener(view15 -> {
//            specialityCode = "";
//            territoryCode = "";
//            categoryCode = "";
//            classCode = "";
            specialityName = "";
            territoryName = "";
            categoryName = "";
            className = "";
            tvSpec.setText("");
            tvTerritory.setText("");
            tvCate.setText("");
            tvClass.setText("");
            tvSpec.setHint(R.string.speciality);
            tvTerritory.setHint(R.string.territory);
            tvCate.setHint(R.string.category);
            tvClass.setHint(R.string.class_filter);
        });

        tvSpec.setText(specialityName);
        tvTerritory.setText(territoryName);
        tvCate.setText(categoryName);
        tvClass.setText(className);

        tv_add_condition.setOnClickListener(view13 -> {
            if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                tvClass.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.GONE);
            } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE) {
                tvTerritory.setVisibility(View.VISIBLE);
                img_del.setVisibility(View.VISIBLE);
            }
        });

        img_del.setOnClickListener(view14 -> {
            if (!className.isEmpty()) {
                //classCode = "";
                className = "";
            } else if (!territoryName.isEmpty()) {
               // territoryCode = "";
                territoryName = "";
            }
            if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvClass.getVisibility() == View.INVISIBLE) {
                tvTerritory.setVisibility(View.GONE);
                img_del.setVisibility(View.GONE);
                tvTerritory.setHint(R.string.territory);
            } else if (tvSpec.getVisibility() == View.VISIBLE && tvCate.getVisibility() == View.VISIBLE && tvTerritory.getVisibility() == View.VISIBLE) {
                tvClass.setVisibility(View.INVISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
                tvClass.setHint(R.string.class_filter);
            }
        });

        tvSpec.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if (lv_spec.getVisibility() == View.VISIBLE) {
               // ed_search.setVisibility(View.GONE);
               // lv_spec.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilteredList( "Speciality");

                FillteredAdapter arrayAdapter = new FillteredAdapter(DoctorVisitActivity.this, filterSelectionList, clickedItem -> {
                   // specialityCode = clickedItem.getCode();
                    specialityName = clickedItem.getName();
                    tvSpec.setText(clickedItem.getName());
                    lv_spec.setVisibility(View.GONE);
//                    ed_search.setVisibility(View.GONE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_spec.setAdapter(arrayAdapter);
                lv_spec.setVisibility(View.VISIBLE);
//                ed_search.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvCate.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_spec.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if (lv_cate.getVisibility() == View.VISIBLE) {
                lv_cate.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilteredList( "Category");
                FillteredAdapter arrayAdapter = new FillteredAdapter(DoctorVisitActivity.this, filterSelectionList, clickedItem -> {
                    //categoryCode = clickedItem.getCode();
                    categoryName = clickedItem.getName();
                    tvCate.setText(clickedItem.getName());
                    lv_cate.setVisibility(View.GONE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_cate.setAdapter(arrayAdapter);
                lv_cate.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvTerritory.setOnClickListener(view -> {
            lv_class.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_spec.setVisibility(View.GONE);
            if (lv_terr.getVisibility() == View.VISIBLE) {
                lv_terr.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilteredList( "Territory");
                FillteredAdapter arrayAdapter = new FillteredAdapter(DoctorVisitActivity.this, filterSelectionList, clickedItem -> {
                   // territoryCode = clickedItem.getCode();
                    territoryName = clickedItem.getName();
                    tvTerritory.setText(clickedItem.getName());
                    lv_terr.setVisibility(View.GONE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_terr.setAdapter(arrayAdapter);
                lv_terr.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        tvClass.setOnClickListener(view -> {
            lv_spec.setVisibility(View.GONE);
            lv_cate.setVisibility(View.GONE);
            lv_terr.setVisibility(View.GONE);
            if (lv_class.getVisibility() == View.VISIBLE) {
                lv_class.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.VISIBLE);
            } else {
                getFilteredList( "Class");
                FillteredAdapter arrayAdapter = new FillteredAdapter(DoctorVisitActivity.this, filterSelectionList, clickedItem -> {
                    //classCode = clickedItem.getCode();
                    className = clickedItem.getName();
                    tvClass.setText(clickedItem.getName());
                    lv_class.setVisibility(View.GONE);
                    tv_add_condition.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                });
                lv_class.setAdapter(arrayAdapter);
                lv_class.setVisibility(View.VISIBLE);
                tv_add_condition.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        btn_apply.setOnClickListener(view1 -> {
            Filtered();
        });

    }

    private void getFilteredList(String requiredList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (requiredList.equalsIgnoreCase("Speciality")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Category")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Territory")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLUSTER +SharedPref.getSfCode(this)).getMasterSyncDataJsonArray();
            } else if (requiredList.equalsIgnoreCase("Class")) {
                jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CLASS).getMasterSyncDataJsonArray();
            }

            filterSelectionList.clear();
            Log.v("getFilteredList", "jsonArray length: " + jsonArray.length());


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);


                String name = jsonObject.optString("Name", "");
                String code = jsonObject.optString("Code", "");

                if (!name.isEmpty() && !code.isEmpty()) {
                    filterSelectionList.add(new DCRFillteredModelClass(name, code));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void filter(String text) {
        filteredNames = new ArrayList<>();
        List<DoctorVisitItem> baseList;
        if (!FilltercustArraList.isEmpty()) {
            baseList = FilltercustArraList;
        } else {
            baseList = doctorList;
        }
        for (DoctorVisitItem s : baseList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())
                    || s.getCategory().toLowerCase().contains(text.toLowerCase())
                    || s.getTerritory().toLowerCase().contains(text.toLowerCase())
                    || s.getClassName().toLowerCase().contains(text.toLowerCase())
                    || s.getSpeciality().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        adapter.filterList(filteredNames);
    }

//    private void filter(String text) {
//        filteredNames = new ArrayList<>();
//        for (DoctorVisitItem s : doctorList) {
//            if (s.getName().toLowerCase().contains(text.toLowerCase())
//                    || s.getCategory().toLowerCase().contains(text.toLowerCase())
//                    || s.getTerritory().toLowerCase().contains(text.toLowerCase())
//                    || s.getClassName().toLowerCase().contains(text.toLowerCase())
//                    || s.getSpeciality().toLowerCase().contains(text.toLowerCase())) {
//                filteredNames.add(s);
//            }
//        }
//        adapter.filterList(filteredNames);
//    }

    public void Filtered () {
        ArrayList<DoctorVisitItem> filterCusList = new ArrayList<>();
        if (!binding.searchET.getText().toString().isEmpty() && !filteredNames.isEmpty()) {
            filterCusList.addAll(filteredNames);
        } else {
            filterCusList.addAll(doctorList);
        }



//        if (filteredNames != null && !filteredNames.isEmpty()) {
//            filterCusList.addAll(filteredNames);
//        } else {
//            filterCusList.addAll(doctorList);
//        }
        FilltercustArraList.clear();

        if (specialityName.equalsIgnoreCase("") && categoryName.equalsIgnoreCase("") && territoryName.equalsIgnoreCase("") && className.equalsIgnoreCase("")) {
                FilltercustArraList.addAll(filterCusList);
                binding.tvFilterCount.setText("0");
                //Collections.sort(FilltercustArraList, Comparator.comparing(CustList::isClusterAvailable));
            } else {
                for (DoctorVisitItem mList : filterCusList) {
                    if (mList.getSpeciality().equalsIgnoreCase(specialityName)
                            && mList.getTerritory().equalsIgnoreCase(territoryName)
                            && mList.getCategory().equalsIgnoreCase(categoryName)
                            && mList.getClassName().equalsIgnoreCase(className)) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getSpeciality().equalsIgnoreCase(specialityName)
                            && mList.getTerritory().equalsIgnoreCase(territoryName)
                            && mList.getCategory().equalsIgnoreCase(categoryName)
                            && className.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getSpeciality().equalsIgnoreCase(specialityName)
                            && mList.getTerritory().equalsIgnoreCase(territoryName)
                            && mList.getClassName().equalsIgnoreCase(className)
                            && categoryName.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getSpeciality().equalsIgnoreCase(specialityName)
                            && mList.getCategory().equalsIgnoreCase(categoryName)
                            && mList.getClassName().equalsIgnoreCase(className)
                            && territoryName.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getTerritory().equalsIgnoreCase(territoryName)
                            && mList.getCategory().equalsIgnoreCase(categoryName)
                            && mList.getClassName().equalsIgnoreCase(className)
                            && specialityName.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getSpeciality().equalsIgnoreCase(specialityName)
                            && mList.getTerritory().equalsIgnoreCase(territoryName)
                            && categoryName.isEmpty()
                            && className.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getSpeciality().equalsIgnoreCase(specialityName)
                            && mList.getCategory().equalsIgnoreCase(categoryName)
                            && territoryName.isEmpty()
                            && className.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getSpeciality().equalsIgnoreCase(specialityName)
                            && mList.getClassName().equalsIgnoreCase(className)
                            && territoryName.isEmpty()
                            && categoryName.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getTerritory().equalsIgnoreCase(territoryName)
                            && mList.getCategory().equalsIgnoreCase(categoryName)
                            && specialityName.isEmpty()
                            && className.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getTerritory().equalsIgnoreCase(territoryName)
                            && mList.getClassName().equalsIgnoreCase(className)
                            && specialityName.isEmpty()
                            && categoryName.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else if (mList.getCategory().equalsIgnoreCase(categoryName)
                            && mList.getClassName().equalsIgnoreCase(className)
                            && specialityName.isEmpty()
                            && territoryName.isEmpty()) {
                        FilltercustArraList.add(mList);
                    } else {
                        if (mList.getSpeciality().equalsIgnoreCase(specialityName)
                                && territoryName.isEmpty()
                                && categoryName.isEmpty()
                                && className.isEmpty()) {
                            FilltercustArraList.add(mList);
                        } else if (mList.getCategory().equalsIgnoreCase(categoryName)
                                && specialityName.isEmpty()
                                && territoryName.isEmpty()
                                && className.isEmpty()) {
                            FilltercustArraList.add(mList);
                        } else if (mList.getTerritory().equalsIgnoreCase(territoryName)
                                && specialityName.isEmpty()
                                && categoryName.isEmpty()
                                && className.isEmpty()) {
                            FilltercustArraList.add(mList);
                        } else if (mList.getClassName().equalsIgnoreCase(className)
                                && specialityName.isEmpty()
                                && territoryName.isEmpty()
                                && categoryName.isEmpty()) {
                            FilltercustArraList.add(mList);
                        }
                    }
                }

            binding.tvFilterCount.setText(String.valueOf(FilltercustArraList.size()));

            }

            if (FilltercustArraList.isEmpty()) {
                binding.noDoctor.setText(String.format("%s %s %s", getString(R.string.no), SharedPref.getDrCap(this), getString(R.string.found)));
                binding.noDoctor.setVisibility(View.VISIBLE);
                binding.recyclerDoctorVisit.setVisibility(View.GONE);
            } else {
                binding.noDoctor.setVisibility(View.GONE);
               binding.recyclerDoctorVisit.setVisibility(View.VISIBLE);
                adapter.filterList(FilltercustArraList);
            }
        dialogFilter.dismiss();
        }
    }

