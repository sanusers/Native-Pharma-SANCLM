package saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCaptureImageList;


public class AdapterCallCaptureImage extends RecyclerView.Adapter<AdapterCallCaptureImage.ViewHolder> {
    Context context;
    ArrayList<CallCaptureImageList> callCaptureImageLists;

    public AdapterCallCaptureImage(Context context, ArrayList<CallCaptureImageList> callCaptureImageLists) {
        this.context = context;
        this.callCaptureImageLists = callCaptureImageLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_capture_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_image_name.setText(callCaptureImageLists.get(position).getImg_name());
        holder.ed_img_desc.setText(callCaptureImageLists.get(position).getImg_description());
        holder.img_view.setImageBitmap(callCaptureImageLists.get(position).getImg_view());

        holder.img_del_img.setOnClickListener(view -> removeAt(holder.getAdapterPosition()));


        holder.tv_image_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                callCaptureImageLists.set(holder.getAdapterPosition(), new CallCaptureImageList(editable.toString(), callCaptureImageLists.get(holder.getAdapterPosition()).getImg_description(), callCaptureImageLists.get(holder.getAdapterPosition()).getImg_view()));
            }
        });

        holder.ed_img_desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                callCaptureImageLists.set(holder.getAdapterPosition(), new CallCaptureImageList(callCaptureImageLists.get(holder.getAdapterPosition()).getImg_name(), editable.toString(), callCaptureImageLists.get(holder.getAdapterPosition()).getImg_view()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return callCaptureImageLists.size();
    }

    public void removeAt(int position) {
        callCaptureImageLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, callCaptureImageLists.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_view, img_del_img;
        EditText tv_image_name;
        EditText ed_img_desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_view = itemView.findViewById(R.id.img_view);
            img_del_img = itemView.findViewById(R.id.img_del);
            tv_image_name = itemView.findViewById(R.id.tv_img_name);
            ed_img_desc = itemView.findViewById(R.id.ed_img_desc);
        }
    }
}
