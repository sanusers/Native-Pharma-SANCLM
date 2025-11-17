package saneforce.sanzen.activity.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.chat.model.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ChatMessage> messages;
    Context context;

    public ChatAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getMessageType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new LeftViewHolder(view);
        } else if (viewType == ChatMessage.TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new RightViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_date_separator, parent, false);
            return new DateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);

        if (holder instanceof LeftViewHolder) {
            ((LeftViewHolder) holder).msg.setText(msg.getMessage());
            ((LeftViewHolder) holder).time.setText(msg.getTime());
        } else if (holder instanceof RightViewHolder) {
            ((RightViewHolder) holder).msg.setText(msg.getMessage());
            ((RightViewHolder) holder).time.setText(msg.getTime());

        } else if (holder instanceof DateViewHolder) {
            ((DateViewHolder) holder).date.setText(msg.getDate());
            holder.itemView.setAlpha(0f);
            holder.itemView.animate()
                    .alpha(1f)
                    .setDuration(350)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView msg, time;

        LeftViewHolder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.tvMessageLeft);
            time = itemView.findViewById(R.id.tvTimeLeft);
        }
    }

    class RightViewHolder extends RecyclerView.ViewHolder {
        TextView msg, time;

        RightViewHolder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.tvMessageRight);
            time = itemView.findViewById(R.id.tvTimeRight);
        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tvDateSeparator);
        }
    }
}
