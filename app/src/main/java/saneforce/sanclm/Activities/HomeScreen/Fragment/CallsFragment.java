package saneforce.sanclm.Activities.HomeScreen.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanclm.Activities.Call.DcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.R;
import saneforce.sanclm.Activities.HomeScreen.Adapters.Call_adapter;
import saneforce.sanclm.Activities.HomeScreen.ModelClass.CallsModalClass;


public class CallsFragment extends Fragment {

    Call_adapter adapter;

    TextView tv_add_call;
  Resources resources;

    RecyclerView recyclerView;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calls_fragment, container, false);

        recyclerView=v.findViewById(R.id.recyelerview);
        tv_add_call=v.findViewById(R.id.tv_add_call);
        adapter=new Call_adapter(getData());
        LinearLayoutManager   manager = new LinearLayoutManager(getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        tv_add_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
            }
        });
        return v;
    }




    private List<CallsModalClass> getData()
    {
        List<CallsModalClass> list = new ArrayList<>();
        list.add(new CallsModalClass("Aravind Raj",
                "2023-08-19,00:0:49",
                "D"));
        list.add(new CallsModalClass("Tom Latham",
                "2023-08-19,00:0:49",
                "C"));
        list.add(new CallsModalClass("Dharany",
                "2023-08-19,00:0:49",
                "CIP"));

        return list;
    }

    }
