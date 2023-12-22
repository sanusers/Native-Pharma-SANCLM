package saneforce.sanclm.activity;


import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanclm.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity{

    ActivityMainBinding binding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }



}