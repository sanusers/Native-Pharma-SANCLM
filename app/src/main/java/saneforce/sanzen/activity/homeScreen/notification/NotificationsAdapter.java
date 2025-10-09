package saneforce.sanzen.activity.homeScreen.notification;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ItemNotificationBinding;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataDao;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.utility.TimeUtils;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final Context context;
    private List<NotificationDataTable> notificationDataTableList;
    private DeleteClickListener deleteClickListener;

    public interface DeleteClickListener {
        void deleteClick(NotificationDataTable notificationDataTable);
    }

    public NotificationsAdapter(Context context, DeleteClickListener deleteClickListener) {
        this.context = context;
        this.deleteClickListener = deleteClickListener;
        notificationDataTableList = new ArrayList<>();
    }

    public void setNotifications(List<NotificationDataTable> notificationDataTableList) {
        this.notificationDataTableList = notificationDataTableList;
        notifyDataSetChanged();
    }

    public List<NotificationDataTable> getNotifications() {
        return notificationDataTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationDataTable notificationDataTable = notificationDataTableList.get(position);
        holder.binding.tvTitle.setText(notificationDataTable.getTitle());
        holder.binding.tvMessage.setText(notificationDataTable.getMessage());
        String dateTime = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_2, TimeUtils.FORMAT_39, notificationDataTable.getDateTime());
        holder.binding.tvDateTime.setText(dateTime);

        if(notificationDataTable.getIsRead() == 1) {
            holder.binding.llNotificationMain.setBackgroundColor(context.getColor(R.color.bg_light_cement));
        } else {
            holder.binding.llNotificationMain.setBackgroundColor(context.getColor(R.color.white));
        }

        holder.binding.ivDelete.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                deleteClickListener.deleteClick(notificationDataTable);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationDataTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        public ViewHolder(@NonNull ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
