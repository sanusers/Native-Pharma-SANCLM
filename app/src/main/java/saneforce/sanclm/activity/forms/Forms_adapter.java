package saneforce.sanclm.activity.forms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.myresource.Resource_adapter;
import saneforce.sanclm.activity.myresource.Resourcemodel_class;

public class Forms_adapter extends RecyclerView.Adapter<Forms_adapter.ViewHolder> {

    ArrayList<Formsmodel_class> flisteduser;

    Context context;

    public Forms_adapter(ArrayList<Formsmodel_class> flisteduser, Context context) {
        this.flisteduser = flisteduser;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forms_adapt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Formsmodel_class frm_listed = flisteduser.get(position);

 /*list_show*/

    }

    @Override
    public int getItemCount() {
        return flisteduser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView flview_name;
       ImageView Frm_imgs;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flview_name=itemView.findViewById(R.id.flview_name);
            Frm_imgs=itemView.findViewById(R.id.Frm_imgs);
        }
    }
}
