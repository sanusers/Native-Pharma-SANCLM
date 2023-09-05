package saneforce.sanclm.activity.HomeScreen.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import saneforce.sanclm.R;


public class Call_adapter extends RecyclerView.Adapter<Call_adapter.listDataViewholider> {


    public Call_adapter() {
    }

    @NonNull
    @Override
    public listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calls_item_view, parent, false);
        return new listDataViewholider(view);

    }

    @Override
    public void onBindViewHolder(@NonNull listDataViewholider holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class listDataViewholider extends RecyclerView.ViewHolder {
        public listDataViewholider(@NonNull View itemView) {
            super(itemView);
        }
    }
}