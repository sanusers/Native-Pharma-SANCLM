package saneforce.santrip.activity.homeScreen.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.adapters.OutBoxAdapter;
import saneforce.santrip.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.databinding.OutboxFragmentBinding;


public class OutboxFragment extends Fragment {

    OutboxFragmentBinding binding;
    OutBoxAdapter outBoxAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OutboxFragmentBinding.inflate(inflater,container,false);
        View v = binding.getRoot();

        List<GroupModelClass> listnew=new ArrayList<>();
        listnew.add(new GroupModelClass("September 13,1998",getData()));
        listnew.add(new GroupModelClass("September 14,1998",getData()));
        listnew.add(new GroupModelClass("September 1,1998",getData()));
        outBoxAdapter=new OutBoxAdapter(getActivity(),listnew);
        binding.Expandapleview.setGroupIndicator(null);
        binding.Expandapleview.setAdapter(outBoxAdapter);


        return v;
    }

    private List<CallsModalClass> getData() {
        List<CallsModalClass> list = new ArrayList<>();
        list.add(new CallsModalClass("Aravind Raj (Doctor)", "2023-08-19,00:0:49", "D"));
        list.add(new CallsModalClass("Tom Latham (Chemist)", "2023-08-19,00:0:49", "C"));
        list.add(new CallsModalClass("Dharany (CIP)", "2023-08-19,00:0:49", "CIP"));

        return list;
    }


}