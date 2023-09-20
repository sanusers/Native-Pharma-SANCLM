package saneforce.sanclm.activity.HomeScreen.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.HomeScreen.Adapters.Call_adapter;


public class CallsFragment extends Fragment {

    Call_adapter adapter;
    RecyclerView recyclerView;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calls_fragment, container, false);

        recyclerView=v.findViewById(R.id.recyelerview);
        adapter=new Call_adapter();
        LinearLayoutManager   manager = new LinearLayoutManager(getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        return v;
    }



    }
