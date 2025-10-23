package saneforce.sanzen.activity.homeScreen.fragment.worktype;

import android.content.Context;
import android.graphics.Shader;
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
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.homeScreen.modelClass.MultiHQClusterItem;
import saneforce.sanzen.activity.homeScreen.modelClass.MultiHQExpandItem;
import saneforce.sanzen.activity.homeScreen.modelClass.Multicheckclass_clust;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.storage.SharedPref;

public class MultiHQClusterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PARENT = 0;
    private static final int VIEW_TYPE_CHILD = 1;

    private final Context context;
    private final List<MultiHQExpandItem> originalList;
    private final List<Object> displayList = new ArrayList<>();
    private final ClusterSelectListener clusterSelectListener;

    public interface ClusterSelectListener {
        void onClusterSelected(String hqCode, MultiHQClusterItem multiHQClusterItem);
        void onClusterUnSelected(String hqCode, MultiHQClusterItem multiHQClusterItem);
    }

    public MultiHQClusterAdapter(Context context, List<MultiHQExpandItem> parentList, ClusterSelectListener clusterSelectListener) {
        this.context = context;
        this.originalList = parentList;
        this.clusterSelectListener = clusterSelectListener;
        updateDisplayList();
    }

    private void updateDisplayList() {
        displayList.clear();
        for (MultiHQExpandItem parent : originalList) {
            displayList.add(parent);
            if(parent.isExpanded()) {
//                displayList.addAll(parent.getClusterList());
                List<MultiHQClusterItem> clusters = parent.getClusterList();
                if (clusters != null && !clusters.isEmpty()) {
                    displayList.addAll(clusters);
                } else {
                    if (UtilityClass.isNetworkAvailable(context)) {
                        displayList.add(new MultiHQClusterItem("No " + SharedPref.getClusterCap(context) + " available.", true));
                    } else {
                        displayList.add(new MultiHQClusterItem("No network available. Kindly sync " + SharedPref.getClusterCap(context), true));
                    }
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position) instanceof MultiHQExpandItem ? VIEW_TYPE_PARENT : VIEW_TYPE_CHILD;
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
            return new ParentViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_view_text, parent, false);
            return new ChildViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ParentViewHolder) {
            MultiHQExpandItem parentItem = (MultiHQExpandItem) displayList.get(position);
            ((ParentViewHolder) holder).bind(parentItem);
        }else {
            MultiHQClusterItem childItem = (MultiHQClusterItem) displayList.get(position);
            ((ChildViewHolder) holder).bind(childItem);
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

        void bind(MultiHQExpandItem item) {
            title.setText(item.getName());
            itemView.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    item.setExpanded(!item.isExpanded());
                    updateDisplayList();
                    notifyDataSetChanged();
                }
            });
            if(item.isExpanded()) {
                arrow.setImageResource(R.drawable.up_arrow);
            } else {
                arrow.setImageResource(R.drawable.down_arrow);
            }
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        CheckBox checkBox;

        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.itemTitle);
            checkBox = itemView.findViewById(R.id.ch_mutiple);
        }

        void bind(MultiHQClusterItem item) {
            text.setText(item.getName());
            if (item.isPlaceholder()) {
                checkBox.setVisibility(View.INVISIBLE);
            } else {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(item.isChecked());
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    item.setChecked(isChecked);
                    if (isChecked) {
                        clusterSelectListener.onClusterSelected(item.getHqCode(), item);
                    } else {
                        clusterSelectListener.onClusterUnSelected(item.getHqCode(), item);
                    }
                });
                if (item.isChecked()) {
                    clusterSelectListener.onClusterSelected(item.getHqCode(), item);
                } else {
                    clusterSelectListener.onClusterUnSelected(item.getHqCode(), item);
                }
            }
        }
    }

    public void filter(String query) {
        displayList.clear();
        if(query == null || query.trim().isEmpty()) {
            updateDisplayList();
        }else {
            String lower = query.toLowerCase();
            for (MultiHQExpandItem parent : originalList) {
                ArrayList<MultiHQClusterItem> filteredChildren = new ArrayList<>();
                for (MultiHQClusterItem child : parent.getClusterList()) {
                    if(child.getName().toLowerCase().contains(lower)) {
                        filteredChildren.add(child);
                    }
                }
                if(!filteredChildren.isEmpty()) {
                    MultiHQExpandItem tempParent = new MultiHQExpandItem(parent.getName(), parent.getCode(), filteredChildren, true);
                    displayList.add(tempParent);
                    displayList.addAll(filteredChildren);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<MultiHQClusterItem> getAllSelectedItems() {
        List<MultiHQClusterItem> selected = new ArrayList<>();
        for (MultiHQExpandItem parent : originalList) {
            for (MultiHQClusterItem child : parent.getClusterList()) {
                if(child.isChecked()) {
                    selected.add(child);
                }
            }
        }
        return selected;
    }
}
