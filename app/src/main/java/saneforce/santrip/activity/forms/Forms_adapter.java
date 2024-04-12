package saneforce.santrip.activity.forms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.forms.weekoff.weekoff_viewscreen;

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
        holder. flview_name.setText(frm_listed.getForms_name());
        holder.Frm_imgs.setImageResource(frm_listed.getViewlist());

        holder.list_resource.setOnClickListener(v -> {
            if(frm_listed.getForms_name().equals("Holiday / Weekly off")){
                Intent l = new Intent(context, weekoff_viewscreen.class);
                context.startActivity(l);
            }
        });

 /*list_show*/

    }

    @Override
    public int getItemCount() {
        return flisteduser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView flview_name;
        ImageView Frm_imgs;
        RelativeLayout list_resource;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flview_name = itemView.findViewById(R.id.flview_name);
            Frm_imgs = itemView.findViewById(R.id.Frm_imgs);
            list_resource = itemView.findViewById(R.id.list_resource);
        }
    }
}
