package saneforce.sanzen.activity.tourPlan.overview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.overview.TourPlanOverviewActivity;
import saneforce.sanzen.activity.tourPlan.overview.model.ContentModel;
import saneforce.sanzen.activity.tourPlan.overview.model.HeaderModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class SideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTENT = 1;
    private Context context;
    private List<Object> data;
    private TourPlanOverviewActivity.NavType navType;

    public SideAdapter(Context context, List<Object> data, TourPlanOverviewActivity.NavType navType) {
        this.context = context;
        this.data = data;
        this.navType = navType;
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = data.get(position);
        if (obj instanceof HeaderModel)
            return TYPE_HEADER;
        else
            return TYPE_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tp_overview_header, parent, false);
            return new HeaderVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tp_overview_content, parent, false);
            return new ContentVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object obj = data.get(position);
        if (holder instanceof HeaderVH) {
            HeaderModel header = (HeaderModel) obj;
            ((HeaderVH) holder).title.setText(header.getTitle());
            if (position == 0) {
                ((HeaderVH) holder).title.setTextColor(context.getColor(R.color.red_60));
            } else {
                ((HeaderVH) holder).title.setTextColor(context.getColor(R.color.black));
            }
        } else if (holder instanceof ContentVH) {
            ContentModel content = (ContentModel) obj;
            if (content.getContent() != null && !content.getContent().isEmpty()) {
                ((ContentVH) holder).content.setVisibility(View.VISIBLE);
                ((ContentVH) holder).content.setText(content.getContent());
            } else {
                ((ContentVH) holder).content.setVisibility(View.GONE);
            }
            if (content.getSubContent() != null && !content.getSubContent().isEmpty()) {
                ((ContentVH) holder).subContent.setVisibility(View.VISIBLE);
                if (navType == TourPlanOverviewActivity.NavType.CLUSTER) {
                    ((ContentVH) holder).subContent.setText(CommonUtilsMethods.applyOrdinalSuperscript(content.getSubContent()));
                    ((ContentVH) holder).subContent.setTextColor(context.getColor(R.color.green_60));
                } else {
                    ((ContentVH) holder).subContent.setText(content.getSubContent());
                    ((ContentVH) holder).subContent.setTextColor(context.getColor(R.color.text_grey));
                }
            } else {
                ((ContentVH) holder).subContent.setVisibility(View.GONE);
            }
            if (content.getSideContent() != null && !content.getSideContent().isEmpty()) {
                ((ContentVH) holder).sideContent.setVisibility(View.VISIBLE);
                ((ContentVH) holder).sideContent.setText(content.getSideContent());
                if (navType == TourPlanOverviewActivity.NavType.WORK_CATEGORY) {
                    ((ContentVH) holder).sideContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                } else {
                    ((ContentVH) holder).sideContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }
            } else {
                ((ContentVH) holder).sideContent.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class HeaderVH extends RecyclerView.ViewHolder {
        TextView title;

        public HeaderVH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_head);
        }
    }

    static class ContentVH extends RecyclerView.ViewHolder {
        TextView content, subContent, sideContent;

        public ContentVH(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_content);
            subContent = itemView.findViewById(R.id.tv_sub_content);
            sideContent = itemView.findViewById(R.id.tv_content_side);
        }
    }
}
