package saneforce.sanclm.activity.forms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.myresource.MyResource_Activity;
import saneforce.sanclm.activity.myresource.Resource_adapter;
import saneforce.sanclm.activity.myresource.Resourcemodel_class;

public class Forms_activity extends AppCompatActivity {
    ArrayList<Formsmodel_class> frmlisted_data = new ArrayList<>();
    Forms_adapter frm_adapter;
    RecyclerView forms_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        forms_id = findViewById(R.id.forms_id);


//Leave details,Next Visit,Holiday / Weekly off,Tour Plan View

        frmlisted_data.clear();
        ArrayList<Integer> array_image = new ArrayList<Integer>();
        array_image.add(R.drawable.doctor_img);
        array_image.add(R.drawable.chemist_img);
        int listed = R.drawable.cip_img;

//        frmlisted_data.add(new Formsmodel_class("Leave details", ""));
//        frmlisted_data.add(new Formsmodel_class("Next Visit", ""));
//        frmlisted_data.add(new Formsmodel_class("Holiday / Weekly off", ""));
//        frmlisted_data.add(new Formsmodel_class("Tour Plan View", ""));


        frm_adapter = new Forms_adapter(frmlisted_data, Forms_activity.this);
        forms_id.setItemAnimator(new DefaultItemAnimator());
        forms_id.setLayoutManager(new GridLayoutManager(Forms_activity.this, 4, GridLayoutManager.VERTICAL, false));
        forms_id.setAdapter(frm_adapter);
    }
}