package saneforce.sanclm.activity.HomeScreen.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.HomeScreen.Adapters.OutBoxAdapter;


public class OutboxFragment extends Fragment {

    OutBoxAdapter outBoxAdapter;
    ExpandableListView expandableListView;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.outbox_fragment, container, false);

        expandableListView=v.findViewById(R.id.Expandapleview);

        outBoxAdapter=new OutBoxAdapter(getActivity());
        expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(outBoxAdapter);





        return v;
    }


}
