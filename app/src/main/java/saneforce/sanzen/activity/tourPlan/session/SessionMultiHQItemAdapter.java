package saneforce.sanzen.activity.tourPlan.session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.model.MultiHQHeaderModelClass;
import saneforce.sanzen.activity.tourPlan.model.MultiHQItemModelClass;
import saneforce.sanzen.commonClasses.Constants;

public class SessionMultiHQItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PARENT = 0;
    private static final int VIEW_TYPE_CHILD = 1;

    private Context context;
    private List<MultiHQHeaderModelClass> originalList;
    private final List<Object> displayList = new ArrayList<>();
    private ItemSelectListener itemSelectListener;

    public interface ItemSelectListener {
        void onItemClicked(String hqCode, MultiHQItemModelClass multiHQItemModelClass);
    }

    public SessionMultiHQItemAdapter() {
    }

    public SessionMultiHQItemAdapter(Context context, List<MultiHQHeaderModelClass> parentList, ItemSelectListener itemSelectListener) {
        this.context = context;
        this.originalList = parentList;
        this.itemSelectListener = itemSelectListener;
        updateDisplayList();
    }

    private void updateDisplayList() {
        displayList.clear();
        if(originalList != null) {
            for (MultiHQHeaderModelClass parent : originalList) {
                displayList.add(parent);
                if(parent.isExpanded()) {
                    displayList.addAll(parent.getItemsList());
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position) instanceof MultiHQHeaderModelClass ? VIEW_TYPE_PARENT : VIEW_TYPE_CHILD;
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_PARENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.multi_hq_header, parent, false);
            return new SessionMultiHQItemAdapter.ParentViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.tp_session_listview_item, parent, false);
            return new SessionMultiHQItemAdapter.ChildViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SessionMultiHQItemAdapter.ParentViewHolder) {
            MultiHQHeaderModelClass parentItem = (MultiHQHeaderModelClass) displayList.get(position);
            ((SessionMultiHQItemAdapter.ParentViewHolder) holder).bind(parentItem);
        }else {
            MultiHQItemModelClass childItem = (MultiHQItemModelClass) displayList.get(position);
            ((SessionMultiHQItemAdapter.ChildViewHolder) holder).bind(holder, childItem);
        }
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView arrow;

        ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            arrow = itemView.findViewById(R.id.img_arrow);
        }

        void bind(MultiHQHeaderModelClass item) {
            title.setText(item.getName());
            itemView.setOnClickListener(v -> {
                item.setExpanded(!item.isExpanded());
                updateDisplayList();
                notifyDataSetChanged();
            });
            if(item.isExpanded()) {
                arrow.setImageResource(R.drawable.up_arrow);
            }else {
                arrow.setImageResource(R.drawable.down_arrow);
            }
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView textName, text2;
        CheckBox checkBox;

        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.tp_item_text);
            text2 = itemView.findViewById(R.id.tp_item_text2);
            checkBox = itemView.findViewById(R.id.tp_item_checkbox);
        }

        void bind(RecyclerView.ViewHolder holder, MultiHQItemModelClass item) {
            textName.setText(item.getName());
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(item.isChecked());

            if(item.getClusterCode() != null && !item.getClusterCode().isEmpty()) {
                text2.setVisibility(View.VISIBLE);
                text2.setText(item.getClusterName());
            } else {
                text2.setVisibility(View.GONE);
                text2.setText("");
            }

            itemView.setOnClickListener(view -> {
                int position = holder.getAbsoluteAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                int independentPos = -1;
                for (int i = 0; i < displayList.size(); i++) {
                    if ((displayList.get(i) instanceof MultiHQItemModelClass) && ((MultiHQItemModelClass) displayList.get(i)).getName().equalsIgnoreCase(Constants.INDEPENDENT)) {
                        independentPos = i;
                        break;
                    }
                }

                boolean isNowChecked = !item.isChecked();
                item.setChecked(isNowChecked);
                checkBox.setChecked(isNowChecked);
                notifyItemChanged(position);

                if (isNowChecked) {
                    if (item.getName().equalsIgnoreCase(Constants.INDEPENDENT)) {
                        for (int i = 0; i < displayList.size(); i++) {
                            if (i != position && (displayList.get(i) instanceof MultiHQItemModelClass) && (((MultiHQItemModelClass) displayList.get(i)).isChecked())) {
                                ((MultiHQItemModelClass) displayList.get(i)).setChecked(false);
                                notifyItemChanged(i); // update only changed rows
                            }
                        }
                    } else if (independentPos != -1 && (displayList.get(independentPos) instanceof MultiHQItemModelClass) && (((MultiHQItemModelClass) displayList.get(independentPos)).isChecked())) {
                        ((MultiHQItemModelClass) displayList.get(independentPos)).setChecked(false);
                        notifyItemChanged(independentPos);
                    }
                } else {
//                    checkBox.setChecked(false);
//                    itemSelectListener.onItemUnSelected(item.getHqCode(), item);
                }
                itemSelectListener.onItemClicked(item.getHqCode(), item);

            });
        }
    }

    public void filter(String query) {
        displayList.clear();
        if(query == null || query.trim().isEmpty()) {
            updateDisplayList();
        }else {
            String lower = query.toLowerCase();
            for (MultiHQHeaderModelClass parent : originalList) {
                ArrayList<MultiHQItemModelClass> filteredChildren = new ArrayList<>();
                for (MultiHQItemModelClass child : parent.getItemsList()) {
                    if(child.getName().toLowerCase().contains(lower)) {
                        filteredChildren.add(child);
                    }
                }
                if(!filteredChildren.isEmpty()) {
                    MultiHQHeaderModelClass tempParent = new MultiHQHeaderModelClass(parent.getName(), parent.getCode(), filteredChildren, true);
                    displayList.add(tempParent);
                    displayList.addAll(filteredChildren);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<MultiHQItemModelClass> getAllSelectedItems() {
        List<MultiHQItemModelClass> selected = new ArrayList<>();
        for (MultiHQHeaderModelClass parent : originalList) {
            for (MultiHQItemModelClass child : parent.getItemsList()) {
                if(child.isChecked()) {
                    selected.add(child);
                }
            }
        }
        return selected;
    }

}
