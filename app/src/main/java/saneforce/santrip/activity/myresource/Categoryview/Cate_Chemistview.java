package saneforce.santrip.activity.myresource.Categoryview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.santrip.databinding.ActivityCateChemistviewBinding;
import saneforce.santrip.storage.SQLite;

public class Cate_Chemistview extends Fragment {
    SQLite sqLite;

    ActivityCateChemistviewBinding catechm;
    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        catechm = ActivityCateChemistviewBinding.inflate(inflater);
        View v = catechm.getRoot();
        sqLite = new SQLite(getActivity());




        return v;
    }
}