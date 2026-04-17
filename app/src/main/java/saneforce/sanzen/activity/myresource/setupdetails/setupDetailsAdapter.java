package saneforce.sanzen.activity.myresource.setupdetails;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.storage.SharedPref;

public class setupDetailsAdapter extends RecyclerView.Adapter<setupDetailsAdapter.ViewHolder> {

    Context context;
    ArrayList<callstatus_model> listeduser;

    public setupDetailsAdapter(Context context, ArrayList<callstatus_model> listeduser) {
        this.context = context;
        this.listeduser = listeduser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setup_details_adapter, parent, false);
        return new ViewHolder(view);
    }

    //    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final callstatus_model app_adapt = listeduser.get(position);
//
//        holder.CustName.setText(app_adapt.getCustName());
//
//        if (app_adapt.getChkflk().equals("0")) {
//            holder.cs_line.setVisibility(View.VISIBLE);
//        } else {
//            holder.cs_line.setVisibility(View.GONE);
//        }
//        holder.img_promoted.setClickable(false);
//        holder.img_promoted.setFocusable(false);
//        holder.img_promoted.setChecked(app_adapt.isPromoted());
//
//        if (app_adapt.isPromoted()) {
//            holder.img_promoted.setTrackTintList(
//                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.Green_45)));
//            holder.img_promoted.setThumbTintList(
//                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
//        } else {
//            holder.img_promoted.setTrackTintList(
//                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_grey)));
//            holder.img_promoted.setThumbTintList(
//                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
//        }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final callstatus_model app_adapt = listeduser.get(position);

        holder.CustName.setText(app_adapt.getCustName());

        if (app_adapt.getChkflk().equals("0") || app_adapt.getChkflk().equals("2")) {
            holder.cs_line.setVisibility(View.VISIBLE);
        } else {
            holder.cs_line.setVisibility(View.GONE);
        }
//        if (app_adapt.getChkflk().equals("0")) {
//            holder.cs_line.setVisibility(View.VISIBLE);
//        } else {
//            holder.cs_line.setVisibility(View.GONE);
//        }

//        if (app_adapt.getChkflk().equals("2")) {
//            holder.img_promoted.setVisibility(View.INVISIBLE);
//            holder.tv_date_range.setVisibility(View.VISIBLE);
//            holder.tv_date_range.setText("From "+  " " + app_adapt.getTown_code() +  "  To "+ " " + app_adapt.getTown_name());
//            holder.tv_date_range.setText( app_adapt.getMnth() + " Meters");
//           holder.tv_date_range.setText( app_adapt.getMonth_name() + " days");
        if (app_adapt.getChkflk().equals("2")) {
            holder.img_promoted.setVisibility(View.INVISIBLE);
            holder.tv_date_range.setVisibility(View.VISIBLE);

            String spKey = app_adapt.getSpKey();
            if (spKey.equals("GENERIC_NUM")) {
                holder.tv_date_range.setText(app_adapt.getDcr_flag());
            } else if (spKey.equals(SharedPref.DIS_RAD)) {
                holder.tv_date_range.setText(app_adapt.getMnth() + " " + context.getString(R.string.meters));
            } else if (spKey.equals(SharedPref.SEQ_DCR_LOCK_DAYS)) {
                holder.tv_date_range.setText(app_adapt.getMonth_name() + " " + context.getString(R.string.days));
            } else if (spKey.equals(SharedPref.RESET_PASSWORD_DAYS)) {
                holder.tv_date_range.setText(app_adapt.getWorkType() + " " + context.getString(R.string.days));
            } else if (spKey.equals(SharedPref.DETAILING_IDLE_DURATION)) {
                holder.tv_date_range.setText(app_adapt.getDcrname()+ " " + context.getString(R.string.minutes));

            } else {
                holder.tv_date_range.setText(context.getString(R.string.from)+ " " + app_adapt.getTown_code() + "  " + context.getString(R.string.to) + " "  + app_adapt.getTown_name());
            }
        } else {

            holder.img_promoted.setVisibility(View.VISIBLE);
            holder.tv_date_range.setVisibility(View.GONE);
            holder.img_promoted.setClickable(false);
            holder.img_promoted.setFocusable(false);
            holder.img_promoted.setChecked(app_adapt.isPromoted());

            if (app_adapt.isPromoted()) {
                holder.img_promoted.setTrackTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.Green_45)));
                holder.img_promoted.setThumbTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            } else {
                holder.img_promoted.setTrackTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_grey)));
                holder.img_promoted.setThumbTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            }
        }

//        holder.img_promoted.setOnCheckedChangeListener(null);
//        holder.img_promoted.setChecked(app_adapt.isPromoted());
//
//        holder.img_promoted.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            app_adapt.setPromoted(isChecked);
//            context.getSharedPreferences(SharedPref.SP_NAME, Context.MODE_PRIVATE)
//                    .edit()
//                    .putString(app_adapt.getSpKey(), isChecked ? "1" : "0")
//                    .apply();
//        });
    }

    @Override
    public int getItemCount() {
        return listeduser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView CustName;
        TextView tv_date_range;
        SwitchCompat img_promoted;
        LinearLayout cs_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CustName = itemView.findViewById(R.id.CustName);
            img_promoted = itemView.findViewById(R.id.img_promoted);
            cs_line = itemView.findViewById(R.id.cs_line);
            tv_date_range = itemView.findViewById(R.id.tv_date_range);
        }
    }
}

//package saneforce.sanzen.activity.myresource.setupdetails;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.SwitchCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//import saneforce.sanzen.R;
//import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
//import saneforce.sanzen.storage.SharedPref;
//
//public class setupDetailsAdapter extends RecyclerView.Adapter<setupDetailsAdapter.ViewHolder> {
//
//    Context context;
//    ArrayList<callstatus_model> listeduser;
//
//    public setupDetailsAdapter(Context context, ArrayList<callstatus_model> listeduser) {
//        this.context = context;
//        this.listeduser = listeduser;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setup_details_adapter, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final callstatus_model app_adapt = listeduser.get(position);
//
//        holder.CustName.setText(app_adapt.getCustName());
//
//        holder.img_promoted.setOnCheckedChangeListener(null);
//        holder.img_promoted.setChecked(app_adapt.isPromoted());
//
//        holder.img_promoted.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            app_adapt.setPromoted(isChecked);
//            context.getSharedPreferences(SharedPref.SP_NAME, Context.MODE_PRIVATE)
//                    .edit()
//                    .putString(SharedPref.LOCATION_TRACK, isChecked ? "1" : "0")
//                    .apply();
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return listeduser.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView CustName;
//        SwitchCompat img_promoted;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            CustName = itemView.findViewById(R.id.CustName);
//            img_promoted = itemView.findViewById(R.id.img_promoted);
//        }
//    }
//}