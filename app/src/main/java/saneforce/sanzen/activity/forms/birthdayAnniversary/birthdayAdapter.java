package saneforce.sanzen.activity.forms.birthdayAnniversary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import saneforce.sanzen.R;

public class birthdayAdapter extends BaseAdapter {

    ArrayList<birthdayModel> birthdayList;
     Context context;
    LayoutInflater inflater;

    public birthdayAdapter(ArrayList<birthdayModel> birthdayList, Context context) {
        this.birthdayList = birthdayList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
//        return birthdayList.size();
        return (int) Math.ceil(birthdayList.size() / 2.0);
    }

    @Override
    public Object getItem(int position) {
        //return birthdayList.get(position);
        int leftIndex = position * 2;
        return birthdayList.get(leftIndex);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        birthdayAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_birthday_adapter, parent, false);
            holder = new birthdayAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (birthdayAdapter.ViewHolder) convertView.getTag();
        }

        // Each row represents TWO doctors
        int leftIndex = position * 2;
        int rightIndex = leftIndex + 1;

        // ✅ LEFT card setup
        if (leftIndex < birthdayList.size()) {
          birthdayModel leftModel = birthdayList.get(leftIndex);
            holder.card_left.setVisibility(View.VISIBLE);
            holder.drImg.setImageResource(R.drawable.tp_dr_icon);
            holder.textDoctor.setText(leftModel.getDoctorName());
            holder.number.setText((leftIndex + 1) + ")");
            holder.textDob.setText(leftModel.getBirthDate());
            holder.textPlace.setText(leftModel.getTerritory());
            holder.Qualification.setText(leftModel.getQualification());
            holder.Category.setText(leftModel.getCategory());
            holder.Speciality.setText(leftModel.getSpeciality());
            holder.Class.setText(leftModel.getClassName());
        } else {
            holder.card_left.setVisibility(View.INVISIBLE);
        }

        // ✅ RIGHT card setup
        if (rightIndex < birthdayList.size()) {
            birthdayModel rightModel = birthdayList.get(rightIndex);
            holder.card_Right.setVisibility(View.VISIBLE);
            holder.drImg2.setImageResource(R.drawable.tp_dr_icon);
            holder.textDoctor2.setText(rightModel.getDoctorName());
            holder.number2.setText((rightIndex + 1) + ")");
            holder.textDob2.setText(rightModel.getBirthDate());
            holder.textPlace2.setText(rightModel.getTerritory());
            holder.Qualification2.setText(rightModel.getQualification());
            holder.Category2.setText(rightModel.getCategory());
            holder.Speciality2.setText(rightModel.getSpeciality());
            holder.Class2.setText(rightModel.getClassName());
        } else {
            holder.card_Right.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        birthdayModel model = birthdayList.get(position);
//
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.fragment_birthday_adapter, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        Log.d("BirthdayAdapter", "Binding: " + model.getDoctorName() + " - " + model.getBirthDate());
//
//        holder.drImg.setImageResource(R.drawable.tp_dr_icon);
//        holder.imgBirthday.setImageResource(R.drawable.birthday);
//        holder.textDoctor.setText(model.getDoctorName());
//        holder.number.setText((position + 1) + ")");
//        holder.textDob.setText(model.getBirthDate());
//        holder.textPlace.setText(model.getTerritory());
//        holder.Qualification.setText(model.getQualification());
//        holder.Category.setText(model.getCategory());
//        holder.Speciality.setText(model.getSpeciality());
//        holder.Class.setText(model.getClassName());
//
//        return convertView;
//    }

    public static class ViewHolder {
        TextView textDoctor, textDob, textPlace;
        TextView textDoctor2, textDob2, textPlace2;

        TextView drqual, drcat, drspec, drclass, number;
        TextView drqual2, drcat2, drspec2, drclass2, number2;

        TextView Qualification, Category, Speciality, Class;
        TextView Qualification2, Category2, Speciality2, Class2;

        LinearLayout doctorNameLayout, layoutQualification, layoutCategory, layoutSpeciality, layoutClass;
        LinearLayout doctorNameLayout2, layoutQualification2, layoutCategory2, layoutSpeciality2, layoutClass2;

        View divider;
        View divider2;

        ImageView drImg, imgBirthday;
        ImageView drImg2, imgBirthday2;

        CardView card_left, card_Right;

        @SuppressLint("WrongViewCast")
        public ViewHolder(View view) {
            card_left = view.findViewById(R.id.card_left);
            textDoctor = view.findViewById(R.id.textDoctor);
            textDob = view.findViewById(R.id.textDob);
            textPlace = view.findViewById(R.id.textPlace);
            drqual = view.findViewById(R.id.drqual);
            drcat = view.findViewById(R.id.drcat);
            drspec = view.findViewById(R.id.drspec);
            drclass = view.findViewById(R.id.drclass);
            number = view.findViewById(R.id.number);

            Qualification = view.findViewById(R.id.Qualification);
            Category = view.findViewById(R.id.Category);
            Speciality = view.findViewById(R.id.Speciality);
            Class = view.findViewById(R.id.Class);
            divider = view.findViewById(R.id.dividerView);
            drImg = view.findViewById(R.id.dr_img);
            imgBirthday = view.findViewById(R.id.imgBirthday);

            doctorNameLayout = view.findViewById(R.id.doctorNameLayout);
            layoutQualification = view.findViewById(R.id.layoutQualification);
            layoutCategory = view.findViewById(R.id.layoutCategory);
            layoutSpeciality = view.findViewById(R.id.layoutSpeciality);
            layoutClass = view.findViewById(R.id.layoutClass);

            card_Right = view.findViewById(R.id.card_Right);
            textDoctor2 = view.findViewById(R.id.textDoctor2);
            textDob2 = view.findViewById(R.id.textDob2);
            textPlace2 = view.findViewById(R.id.textPlace2);
            drqual2 = view.findViewById(R.id.drqual2);
            drcat2 = view.findViewById(R.id.drcat2);
            drspec2 = view.findViewById(R.id.drspec2);
            drclass2 = view.findViewById(R.id.drclass2);
            number2 = view.findViewById(R.id.number2);

            Qualification2 = view.findViewById(R.id.Qualification2);
            Category2 = view.findViewById(R.id.Category2);
            Speciality2 = view.findViewById(R.id.Speciality2);
            Class2 = view.findViewById(R.id.Class2);
            divider2 = view.findViewById(R.id.dividerView2);
            drImg2 = view.findViewById(R.id.dr_img2);
            imgBirthday2 = view.findViewById(R.id.imgBirthday2);

            doctorNameLayout2 = view.findViewById(R.id.doctorNameLayout2);
            layoutQualification2 = view.findViewById(R.id.layoutQualification2);
            layoutCategory2 = view.findViewById(R.id.layoutCategory2);
            layoutSpeciality2 = view.findViewById(R.id.layoutSpeciality2);
            layoutClass2 = view.findViewById(R.id.layoutClass2);
        }
    }
}

//package saneforce.sanzen.activity.forms.birthdayAnniversary;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Color;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//import saneforce.sanzen.R;
//
//public class birthdayAdapter extends RecyclerView.Adapter<birthdayAdapter.ViewHolder> {
//
//    ArrayList<birthdayModel> birthdayList;
//    Context context;
//
//    public birthdayAdapter(ArrayList<birthdayModel> birthdayList, Context context) {
//        this.birthdayList = birthdayList;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.fragment_birthday_adapter, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        birthdayModel model = birthdayList.get(position);
//        Log.d("BirthdayAdapter", "Binding: " + model.getDoctorName() + " - " + model.getBirthDate());
//        holder.drImg.setImageResource(R.drawable.tp_dr_icon);
//        holder.imgBirthday.setImageResource(R.drawable.birthday);
//        holder.textDoctor.setText(model.getDoctorName());
//        holder.number.setText((position + 1) + ")");
//        holder.textDob.setText(model.getBirthDate());
//        holder.textPlace.setText(model.getTerritory());
//        holder.Qualification.setText((model.getQualification()));
//        holder.Category.setText((model.getCategory()));
//        holder.Speciality.setText((model.getSpeciality()));
//        holder.Class.setText((model.getClassName()));
//
//
////        holder.doctorNameLayout.setText((model.doctorNameLayout));
////        holder.Qualification.setText((model.getQualification));
////        holder.Qualification.setText((model.getQualification));
////        holder.Qualification.setText((model.getQualification));
//
//
////        try {
////            holder.back_clr.setBackgroundColor(Color.parseColor(model.getColor()));
////            holder.viewclr_1.setBackgroundColor(Color.parseColor(model.getColor()));
////            holder.line_1.setBackgroundColor(Color.parseColor(model.getColor()));
////            holder.line_2.setBackgroundColor(Color.parseColor(model.getColor()));
////            holder.line_3.setBackgroundColor(Color.parseColor(model.getColor()));
////        } catch (Exception e) {
////            // fallback color if parsing fails
////            holder.back_clr.setBackgroundColor(Color.parseColor("#E8F9FC"));
////        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return birthdayList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textDoctor, textDob, textPlace;
//        TextView  drqual, drcat, drspec, drclass, number;
//        TextView Qualification, Category, Speciality,Class;
//        LinearLayout doctorNameLayout,layoutQualification,layoutCategory,layoutSpeciality,layoutClass;
//        View divider;
//        ImageView drImg,imgBirthday;
//        @SuppressLint("WrongViewCast")
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textDoctor = itemView.findViewById(R.id.textDoctor);
//            textDob = itemView.findViewById(R.id.textDob);
//            textPlace = itemView.findViewById(R.id.textPlace);
//            drqual = itemView.findViewById(R.id.drqual);
//            drcat = itemView.findViewById(R.id.drcat);
//            drspec = itemView.findViewById(R.id.drspec);
//            drclass = itemView.findViewById(R.id.drclass);
//            number = itemView.findViewById(R.id.number);
//
//            Qualification=itemView.findViewById(R.id.Qualification);
//            Category=itemView.findViewById(R.id.Category);
//            Speciality=itemView.findViewById(R.id.Speciality);
//            Class=itemView.findViewById(R.id.Class);
//            divider = itemView.findViewById(R.id.dividerView);
//            drImg = itemView.findViewById(R.id.dr_img);
//            imgBirthday = itemView.findViewById(R.id.imgBirthday);
//
//
//            doctorNameLayout = itemView.findViewById(R.id.doctorNameLayout);
//            layoutQualification = itemView.findViewById(R.id.layoutQualification);
//            layoutCategory = itemView.findViewById(R.id.layoutCategory);
//            layoutSpeciality = itemView.findViewById(R.id.layoutSpeciality);
//            layoutClass = itemView.findViewById(R.id.layoutClass);
//
//        }
//    }
//}
