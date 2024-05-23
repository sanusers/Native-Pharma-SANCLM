package saneforce.sanzen.activity.myresource.Categoryview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.sanzen.databinding.ActivityCateChemistviewBinding;


public class Cate_Chemistview extends Fragment {


    ActivityCateChemistviewBinding catechm;
    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        catechm = ActivityCateChemistviewBinding.inflate(inflater);
        View v = catechm.getRoot();

        return v;
    }
}