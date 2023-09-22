package saneforce.sanclm.activity.map.custSelection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.map.MapsActivity;

public class TagCustSelectionList extends AppCompatActivity {
    ImageView img_back;
    RecyclerView rv_cust_list;
    CustListAdapter custListAdapter;
    CommonUtilsMethods commonUtilsMethods;
    EditText editTextSearch_cust;
    ArrayList<CustList> custListArrayList = new ArrayList<>();

    @Override
    public void onBackPressed() {
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dcr_selection);

        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.FullScreencall();

        img_back = findViewById(R.id.iv_back);
        rv_cust_list = findViewById(R.id.rv_cust_list);
        editTextSearch_cust = findViewById(R.id.search_cust);

        DummyAdapter();

        editTextSearch_cust.addTextChangedListener(new TextWatcher() {
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

        img_back.setOnClickListener(view -> {
            Intent intent = new Intent(TagCustSelectionList.this, MapsActivity.class);
            intent.putExtra("from", "cust_sel_list");
            startActivity(intent);
        });
    }

    private void DummyAdapter() {
        custListArrayList.clear();
        custListArrayList.add(new CustList("Mohammed Ameer BashaKhan", "Category", "Cardio Surgion", "Madurai", "0/1"));
        custListArrayList.add(new CustList("Baskar Kumar Reddy", "Category", "Neurolgist", "Chennai", "1/1"));
        custListArrayList.add(new CustList("Aasik", "Category", "MBBS", "Trichy", "0/1"));
        custListArrayList.add(new CustList("Umar Kathab Manzoor Ali", "Category", "Ortho Specialist", "Kanyakumari", "0/1"));
        custListArrayList.add(new CustList("Venkatesh", "Category", "Dermotologist", "Sivagangai", "1/1"));
        custListArrayList.add(new CustList("Akash", "Category", "MBBS", "Madurai", "0/1"));
        custListArrayList.add(new CustList("Aravindh", "Category", "Ortho", "Vellore", "1/1"));
        custListArrayList.add(new CustList("Surya Vignesh Kumar ", "Category", "Dermotologist", "Kerala", "0/1"));
        custListArrayList.add(new CustList("Jahir Basha", "Category", "Gynocologist", "Andhra", "1/1"));
        custListArrayList.add(new CustList("Vamshi Kannan", "Category", "Neurolgist", "Jammu", "0/1"));
        custListArrayList.add(new CustList("Madhan", "Category", "Cardiogilist", "Kanyakumari", "1/1"));

        custListAdapter = new CustListAdapter(TagCustSelectionList.this,TagCustSelectionList.this, custListArrayList);
        rv_cust_list.setItemAnimator(new DefaultItemAnimator());
        rv_cust_list.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        rv_cust_list.setAdapter(custListAdapter);
    }

    private void filter(String text) {
        ArrayList<CustList> filterdNames = new ArrayList<>();
        for (CustList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        custListAdapter.filterList(filterdNames);
    }
}