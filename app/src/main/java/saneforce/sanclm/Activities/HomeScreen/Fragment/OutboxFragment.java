package saneforce.sanclm.Activities.HomeScreen.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanclm.R;
import saneforce.sanclm.Activities.HomeScreen.Adapters.OutBoxAdapter;
import saneforce.sanclm.Activities.HomeScreen.ModelClass.CallsModalClass;
import saneforce.sanclm.Activities.HomeScreen.ModelClass.GroupModelClass;


public class OutboxFragment extends Fragment {

    OutBoxAdapter outBoxAdapter;
    ExpandableListView expandableListView;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.outbox_fragment, container, false);

        expandableListView=v.findViewById(R.id.Expandapleview);

        List<GroupModelClass> listnew=new ArrayList<>();
        listnew.add(new GroupModelClass("September 13,2000",getData()));
        listnew.add(new GroupModelClass("September 14,1998",getData()));
        listnew.add(new GroupModelClass("September 15,1998",getData()));
        outBoxAdapter=new OutBoxAdapter(getActivity(),listnew);
        expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(outBoxAdapter);




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
