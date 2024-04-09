package saneforce.santrip.activity.presentation.createPresentation.selectedSlide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import saneforce.santrip.R;
import saneforce.santrip.activity.presentation.SupportClass;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.activity.presentation.createPresentation.slide.ImageSelectionInterface;

public class SelectedSlidesAdapter extends RecyclerView.Adapter<SelectedSlidesAdapter.MyViewHolder> implements ItemTouchHelperCallBack.ItemTouchHelperContract {

    Context context;
    ArrayList<BrandModelClass.Product> arrayList;
    ImageSelectionInterface imageSelectionInterface;
    ItemDragListener itemDragListener;

    public SelectedSlidesAdapter (Context context, ArrayList<BrandModelClass.Product> arrayList, ImageSelectionInterface imageSelectionInterface, ItemDragListener itemDragListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.imageSelectionInterface = imageSelectionInterface;
        this.itemDragListener = itemDragListener;;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.presentation_selected_slides_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        BrandModelClass.Product product = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.setIsRecyclable(false);

        holder.brandName.setText(product.getBrandName());
        holder.fileName.setText(product.getSlideName());
        SupportClass.setThumbnail(context, product.getSlideName(), holder.slideImage);

        arrayList.get(holder.getAbsoluteAdapterPosition()).setDraggedPosition(holder.getAbsoluteAdapterPosition() + 1);

        holder.deleteIcon.setOnClickListener(view -> {
            arrayList.get(holder.getAbsoluteAdapterPosition()).setImageSelected(false);
            imageSelectionInterface.imageSelection(arrayList,holder.getAbsoluteAdapterPosition());
        });

        holder.dragIcon.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                itemDragListener.requestDrag(holder);
            }
            return false;
        });

        holder.dragIcon.setOnLongClickListener(view -> {
            itemDragListener.requestDrag(holder);
            return false;
        });
    }

    @Override
    public int getItemCount () {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView brandName,fileName;
        ImageView slideImage;
        LinearLayout deleteIcon,dragIcon;
        View rowView;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.brandName);
            fileName = itemView.findViewById(R.id.fileName);
            slideImage = itemView.findViewById(R.id.slideImage);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            dragIcon = itemView.findViewById(R.id.dragIcon);
            rowView = itemView;
        }

    }


    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        arrayList.get(fromPosition).setDraggedPosition(toPosition);
        arrayList.get(toPosition).setDraggedPosition(fromPosition);

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(arrayList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(arrayList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(context.getResources().getColor(R.color.green_2));
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }


}
