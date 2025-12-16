package saneforce.sanzen.activity.forms.birthdayAnniversary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import saneforce.sanzen.R;

public class AnniversaryAdapter extends BaseAdapter {

    ArrayList<AnniversaryModel> anniversaryList;
    Context context;
    LayoutInflater inflater;

    public AnniversaryAdapter(ArrayList<AnniversaryModel> anniversaryList, Context context) {
        this.anniversaryList = anniversaryList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
       // return anniversaryList.size();
        return (int) Math.ceil(anniversaryList.size() / 2.0);
    }


    @Override
    public Object getItem(int position) {
       // return anniversaryList.get(position);
        int leftIndex = position * 2;
        return anniversaryList.get(leftIndex);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AnniversaryAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_anniversary_adapter, parent, false);
            holder = new AnniversaryAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (AnniversaryAdapter.ViewHolder) convertView.getTag();
        }

        // Each row represents TWO doctors
        int leftIndex = position * 2;
        int rightIndex = leftIndex + 1;

        // ✅ LEFT card setup
        if (leftIndex < anniversaryList.size()) {
            AnniversaryModel leftModel = anniversaryList.get(leftIndex);
            holder.card_left.setVisibility(View.VISIBLE);
            holder.drImgWeds.setImageResource(R.drawable.tp_dr_icon);
            holder.textDoctorWeds.setText(leftModel.getDoctorName());
            holder.numberWeds.setText((leftIndex + 1) + ")");
            holder.textDow.setText(leftModel.getAnniversaryDate());
            holder.textPlaceWeds.setText(leftModel.getTerritoryWeds());
            holder.QualificationWeds.setText(leftModel.getQualificationWeds());
            holder.CategoryWeds.setText(leftModel.getCategoryWeds());
            holder.SpecialityWeds.setText(leftModel.getSpecialityWeds());
            holder.ClassWeds.setText(leftModel.getClassNameWeds());
        } else {
            holder.card_left.setVisibility(View.INVISIBLE);
        }

        // ✅ RIGHT card setup
        if (rightIndex < anniversaryList.size()) {
            AnniversaryModel rightModel = anniversaryList.get(rightIndex);
            holder.card_Right.setVisibility(View.VISIBLE);
            holder.drImgWeds2.setImageResource(R.drawable.tp_dr_icon);
            holder.textDoctorWeds2.setText(rightModel.getDoctorName());
            holder.numberWeds2.setText((rightIndex + 1) + ")");
            holder.textDow2.setText(rightModel.getAnniversaryDate());
            holder.textPlaceWeds2.setText(rightModel.getTerritoryWeds());
            holder.QualificationWeds2.setText(rightModel.getQualificationWeds());
            holder.CategoryWeds2.setText(rightModel.getCategoryWeds());
            holder.SpecialityWeds2.setText(rightModel.getSpecialityWeds());
            holder.ClassWeds2.setText(rightModel.getClassNameWeds());
        } else {
            holder.card_Right.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        anniversaryModel model = anniversaryList.get(position);
//
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.fragment_anniversary_adapter, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        Log.d("AnniversaryAdapter", "Binding: " + model.getDoctorName() + " - " + model.getAnniversaryDate());
//
//        holder.drImgWeds.setImageResource(R.drawable.tp_dr_icon);
//        holder.textDoctorWeds.setText(model.getDoctorName());
//        holder.numberWeds.setText((position + 1) + ")");
//        holder.textDow.setText(model.getAnniversaryDate());
//        holder.textPlaceWeds.setText(model.getTerritoryWeds());
//        holder.QualificationWeds.setText(model.getQualificationWeds());
//        holder.CategoryWeds.setText(model.getCategoryWeds());
//        holder.SpecialityWeds.setText(model.getSpecialityWeds());
//        holder.ClassWeds2.setText(model.getClassNameWeds());
//        holder.drImgWeds2.setImageResource(R.drawable.tp_dr_icon);
//        holder.textDoctorWeds2.setText(model.getDoctorName());
//        holder.numberWeds2.setText((position + 1) + ")");
//        holder.textDow2.setText(model.getAnniversaryDate());
//        holder.textPlaceWeds2.setText(model.getTerritoryWeds());
//        holder.QualificationWeds2.setText(model.getQualificationWeds());
//        holder.CategoryWeds2.setText(model.getCategoryWeds());
//        holder.SpecialityWeds2.setText(model.getSpecialityWeds());
//        holder.ClassWeds2.setText(model.getClassNameWeds());
//        return convertView;
//    }

    public static class ViewHolder {

        TextView textDoctorWeds, textDow, textPlaceWeds;
        TextView textDoctorWeds2, textDow2, textPlaceWeds2;

        TextView drqualWeds, drcatWeds, drspecWeds, drclassWeds, numberWeds;
        TextView drqualWeds2, drcatWeds2, drspecWeds2, drclassWeds2, numberWeds2;

        TextView QualificationWeds, CategoryWeds, SpecialityWeds, ClassWeds;
        TextView QualificationWeds2, CategoryWeds2, SpecialityWeds2, ClassWeds2;

        LinearLayout doctorNameLayoutWeds, layoutQualificationWeds, layoutCategoryWeds, layoutSpecialityWeds, layoutClassWeds;
        LinearLayout doctorNameLayoutWeds2, layoutQualificationWeds2, layoutCategoryWeds2, layoutSpecialityWeds2, layoutClassWeds2;

        View dividerWeds;
        View dividerWeds2;

        ImageView drImgWeds, imgAnniversaryWeds;
        ImageView drImgWeds2, imgAnniversaryWeds2;

        CardView card_left, card_Right;


        @SuppressLint("WrongViewCast")
        public ViewHolder( View  view) {
            card_left =  view.findViewById(R.id.card_left);
            textDoctorWeds =  view.findViewById(R.id.textDoctorWeds);
            textDow =  view.findViewById(R.id.textDow);
            textPlaceWeds =  view.findViewById(R.id.textPlaceWeds);
            drqualWeds =  view.findViewById(R.id.drqualWeds);
            drcatWeds =  view.findViewById(R.id.drcatWeds);
            drspecWeds =  view.findViewById(R.id.drspecWeds);
            drclassWeds =  view.findViewById(R.id.drclassWeds);
            numberWeds =  view.findViewById(R.id.numberWeds);

            QualificationWeds =  view.findViewById(R.id.QualificationWeds);
            CategoryWeds =  view.findViewById(R.id.CategoryWeds);
            SpecialityWeds =  view.findViewById(R.id.SpecialityWeds);
            ClassWeds =  view.findViewById(R.id.ClassWeds);
            dividerWeds =  view.findViewById(R.id.dividerViewWeds);
            drImgWeds =  view.findViewById(R.id.dr_imgWeds);
            imgAnniversaryWeds =  view.findViewById(R.id.imgAnniversaryWeds);

            doctorNameLayoutWeds =  view.findViewById(R.id.doctorNameLayoutWeds);
            layoutQualificationWeds =  view.findViewById(R.id.layoutQualificationWeds);
            layoutCategoryWeds =  view.findViewById(R.id.layoutCategoryWeds);
            layoutSpecialityWeds =  view.findViewById(R.id.layoutSpecialityWeds);
            layoutClassWeds =  view.findViewById(R.id.layoutClassWeds);

            card_Right =  view.findViewById(R.id.card_Right);
            textDoctorWeds2 =  view.findViewById(R.id.textDoctorWeds2);
            textDow2 =  view.findViewById(R.id.textDow2);
            textPlaceWeds2 =  view.findViewById(R.id.textPlaceWeds2);
            drqualWeds2 =  view.findViewById(R.id.drqualWeds2);
            drcatWeds2 =  view.findViewById(R.id.drcatWeds2);
            drspecWeds2 =  view.findViewById(R.id.drspecWeds2);
            drclassWeds2 =  view.findViewById(R.id.drclassWeds2);
            numberWeds2 =  view.findViewById(R.id.numberWeds2);

            QualificationWeds2 =  view.findViewById(R.id.QualificationWeds2);
            CategoryWeds2 =  view.findViewById(R.id.CategoryWeds2);
            SpecialityWeds2 =  view.findViewById(R.id.SpecialityWeds2);
            ClassWeds2 =  view.findViewById(R.id.ClassWeds2);
            dividerWeds2 =  view.findViewById(R.id.dividerViewWeds2);
            drImgWeds2 =  view.findViewById(R.id.dr_imgWeds2);
            imgAnniversaryWeds2 =  view.findViewById(R.id.imgAnniversaryWeds2);

            doctorNameLayoutWeds2 =  view.findViewById(R.id.doctorNameLayoutWeds2);
            layoutQualificationWeds2 =  view.findViewById(R.id.layoutQualificationWeds2);
            layoutCategoryWeds2 =  view.findViewById(R.id.layoutCategoryWeds2);
            layoutSpecialityWeds2 =  view.findViewById(R.id.layoutSpecialityWeds2);
            layoutClassWeds2 =  view.findViewById(R.id.layoutClassWeds2);
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
//public class anniversaryAdapter extends RecyclerView.Adapter<anniversaryAdapter.ViewHolder> {
//
//    ArrayList<anniversaryModel>anniversaryList;
//    Context context;
//
//    public anniversaryAdapter(ArrayList<anniversaryModel> anniversaryList, Context context) {
//        this.anniversaryList = anniversaryList;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.fragment_anniversary_adapter, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        anniversaryModel model = anniversaryList.get(position);
//        Log.d("AnniversaryAdapter", "Binding: " + model.getDoctorName() + " - " + model.getAnniversaryDate());
//        holder.drImgWeds.setImageResource(R.drawable.tp_dr_icon);
//        holder.textDoctorWeds.setText(model.getDoctorName());
//        holder.numberWeds.setText((position + 1) + ")");
//        holder.textDow.setText(model.getAnniversaryDate());
//        holder.textPlaceWeds.setText(model.getTerritoryWeds());
//        holder.QualificationWeds.setText((model.getQualificationWeds()));
//        holder.CategoryWeds.setText((model.getCategoryWeds()));
//        holder.SpecialityWeds.setText((model.getSpecialityWeds()));
//        holder.ClassWeds.setText((model.getClassNameWeds()));
//    }
//
//    @Override
//    public int getItemCount() {
//        return anniversaryList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textDoctorWeds, textDow, textPlaceWeds;
//        TextView  drqualWeds, drcatWeds, drspecWeds, drclassWeds, numberWeds;
//        TextView QualificationWeds, CategoryWeds, SpecialityWeds,ClassWeds;
//        LinearLayout doctorNameLayoutWeds,layoutQualificationWeds,layoutCategoryWeds,layoutSpecialityWeds,layoutClassWeds;
//        View dividerWeds;
//        ImageView drImgWeds,imgAnniversaryWeds;
//        @SuppressLint("WrongViewCast")
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textDoctorWeds = itemView.findViewById(R.id.textDoctorWeds);
//            textDow = itemView.findViewById(R.id.textDow);
//            textPlaceWeds = itemView.findViewById(R.id.textPlaceWeds);
//            drqualWeds = itemView.findViewById(R.id.drqualWeds);
//            drcatWeds = itemView.findViewById(R.id.drcatWeds);
//            drspecWeds = itemView.findViewById(R.id.drspecWeds);
//            drclassWeds = itemView.findViewById(R.id.drclassWeds);
//            numberWeds = itemView.findViewById(R.id.numberWeds);
//
//            QualificationWeds=itemView.findViewById(R.id.QualificationWeds);
//            CategoryWeds=itemView.findViewById(R.id.CategoryWeds);
//            SpecialityWeds=itemView.findViewById(R.id.SpecialityWeds);
//            ClassWeds=itemView.findViewById(R.id.ClassWeds);
//            dividerWeds = itemView.findViewById(R.id.dividerViewWeds);
//            drImgWeds = itemView.findViewById(R.id.dr_imgWeds);
//            imgAnniversaryWeds = itemView.findViewById(R.id.imgAnniversaryWeds);
//
//            doctorNameLayoutWeds = itemView.findViewById(R.id.doctorNameLayoutWeds);
//            layoutQualificationWeds = itemView.findViewById(R.id.layoutQualificationWeds);
//            layoutCategoryWeds = itemView.findViewById(R.id.layoutCategoryWeds);
//            layoutSpecialityWeds = itemView.findViewById(R.id.layoutSpecialityWeds);
//            layoutClassWeds = itemView.findViewById(R.id.layoutClassWeds);
//
//        }
//    }
//}
