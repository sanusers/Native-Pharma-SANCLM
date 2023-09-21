package saneforce.sanclm.Activities.HomeScreen.Adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import saneforce.sanclm.R;
import saneforce.sanclm.Activities.HomeScreen.ModelClass.CallsModalClass;


public class Call_adapter extends RecyclerView.Adapter<Call_adapter.listDataViewholider> {
    Resources resources;
    List<CallsModalClass> list = new ArrayList<>();

    public Call_adapter(List<CallsModalClass> list) {
        this.list = list;

    }

    @NonNull
    @Override
    public listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calls_item_view, parent, false);
        return new listDataViewholider(view);

    }

    @Override
    public void onBindViewHolder(@NonNull listDataViewholider holder, int position) {

        CallsModalClass callslist = list.get(position);

        holder.DocName.setText(callslist.getDocName());
        holder.datetime.setText(callslist.getCallsDateTime());
        if (callslist.getDocNameID().equalsIgnoreCase("D")) {

            holder.imageView.setImageResource(R.drawable.doctor_img);
        } else if (callslist.getDocNameID().equalsIgnoreCase("C")) {

            holder.imageView.setImageResource(R.drawable.chemist_img);

        } else if (callslist.getDocNameID().equalsIgnoreCase("cip")) {

            holder.imageView.setImageResource(R.drawable.cip_img);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class listDataViewholider extends RecyclerView.ViewHolder {

        TextView DocName, datetime;
        CircleImageView imageView;


        public listDataViewholider(@NonNull View itemView) {
            super(itemView);


            DocName = itemView.findViewById(R.id.textViewLabel1);
            datetime = itemView.findViewById(R.id.textViewLabel2);
            imageView = itemView.findViewById(R.id.profile_icon);


        }
    }
}