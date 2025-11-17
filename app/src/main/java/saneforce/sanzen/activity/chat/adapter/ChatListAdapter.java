package saneforce.sanzen.activity.chat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.chat.model.ChatUserModel;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private final Context context;
    private List<ChatUserModel> chatUserModelList;
    private final UserClickListener userClickListener;
    private ArrayList<Integer> colors = new ArrayList<>();

    public interface UserClickListener {
        void onUserClick(ChatUserModel chatUserModel, int position);
    }

    public ChatListAdapter(Context context, List<ChatUserModel> chatUserModelList, UserClickListener userClickListener) {
        this.context = context;
        this.chatUserModelList = chatUserModelList;
        this.userClickListener = userClickListener;

        colors.add(ContextCompat.getColor(context, R.color.green_60));
        colors.add(ContextCompat.getColor(context, R.color.red_60));
        colors.add(ContextCompat.getColor(context, R.color.blue_60));
        colors.add(ContextCompat.getColor(context, R.color.yellow_60));
        colors.add(ContextCompat.getColor(context, R.color.pink_60));
        colors.add(ContextCompat.getColor(context, R.color.brown_60));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<ChatUserModel> filteredList) {
        this.chatUserModelList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatUserModel chatUserModel = chatUserModelList.get(position);
        if (chatUserModel.isSelected()) {
            holder.itemView.setSelected(true);
            holder.tvName.setTextColor(context.getColor(R.color.white));
            holder.tvDate.setTextColor(context.getColor(R.color.white));
            holder.tvMessage.setTextColor(context.getColor(R.color.white));
        } else {
            holder.itemView.setSelected(false);
            holder.tvName.setTextColor(context.getColor(R.color.dark_purple));
            holder.tvDate.setTextColor(context.getColor(R.color.black));
            holder.tvMessage.setTextColor(context.getColor(R.color.text_grey));
        }
        holder.tvName.setText(chatUserModel.getName());
        holder.tvProfile.setText(chatUserModel.getName().substring(0, 2));
        holder.imgProfile.setImageTintList(ColorStateList.valueOf(colors.get(position % 6)));
        holder.tvMessage.setText(chatUserModel.getMessage());
        holder.tvDate.setText(chatUserModel.getDate());
        holder.itemView.setOnClickListener(v -> {
            if (!chatUserModel.isSelected()) {
                chatUserModel.setSelected(true);
                holder.itemView.setSelected(true);
                holder.tvName.setTextColor(context.getColor(R.color.white));
                userClickListener.onUserClick(chatUserModel, holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatUserModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvProfile, tvMessage, tvDate;
        ImageView imgProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvProfile = itemView.findViewById(R.id.tv_profile);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvDate = itemView.findViewById(R.id.tv_date);
            imgProfile = itemView.findViewById(R.id.img_profile);
        }
    }

}
