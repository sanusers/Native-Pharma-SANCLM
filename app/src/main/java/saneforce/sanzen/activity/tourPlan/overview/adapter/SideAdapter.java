package saneforce.sanzen.activity.tourPlan.overview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.overview.TourPlanOverviewActivity;
import saneforce.sanzen.activity.tourPlan.overview.model.ContentModel;
import saneforce.sanzen.activity.tourPlan.overview.model.HeaderModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.storage.SharedPref;

public class SideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTENT = 1;
    private Context context;
    private List<Object> data;
    private TourPlanOverviewActivity.NavType navType;
    private OnAddClickListener addClickListener;
    private Boolean isDoc = false;
    private CommonUtilsMethods commonUtilsMethods;

    public interface OnAddClickListener {
        void onAddClick(ContentModel model, int position);
    }

    public SideAdapter(Context context, List<Object> data, TourPlanOverviewActivity.NavType navType, OnAddClickListener listener) {
        this.context = context;
        this.data = data;
        this.navType = navType;
        this.addClickListener = listener;
        commonUtilsMethods = new CommonUtilsMethods(context);
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
                ((ContentVH) holder).addBtn.setOnClickListener(v -> {
                    if (addClickListener != null) {
                        addClickListener.onAddClick(content, holder.getAdapterPosition());
                    }
                });
                if (navType == TourPlanOverviewActivity.NavType.DOCTOR || navType == TourPlanOverviewActivity.NavType.DOCTOR_CATEGORY || navType == TourPlanOverviewActivity.NavType.CHEMIST) {
                    ((ContentVH) holder).sideContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                } else {
                    ((ContentVH) holder).sideContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }
                ((ContentVH) holder).content.setOnClickListener(v -> commonUtilsMethods.displayPopupWindowEnd(context, v, content.getContent()));
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
                if (navType == TourPlanOverviewActivity.NavType.DOCTOR || navType == TourPlanOverviewActivity.NavType.DOCTOR_CATEGORY || navType == TourPlanOverviewActivity.NavType.CHEMIST) {
                    ((ContentVH) holder).sideContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                } else {
                    ((ContentVH) holder).sideContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }
                ((ContentVH) holder).subContent.setOnClickListener(v -> commonUtilsMethods.displayPopupWindowEnd(context, v, content.getContent()));
            } else {
                ((ContentVH) holder).addBtn.setVisibility(View.GONE);
                ((ContentVH) holder).subContent.setVisibility(View.GONE);
            }
            if (content.getSpannableString() != null && !content.getSpannableString().toString().isEmpty()) {
                ((ContentVH) holder).sideContent.setVisibility(View.VISIBLE);
                ((ContentVH) holder).sideContent.setText(content.getSpannableString());
                if (navType == TourPlanOverviewActivity.NavType.WORK_CATEGORY) {
                    ((ContentVH) holder).sideContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                } else {
                    ((ContentVH) holder).sideContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }
            } else if (content.getSideContent() != null && !content.getSideContent().isEmpty()) {
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
            if ((navType == TourPlanOverviewActivity.NavType.DOCTOR || navType == TourPlanOverviewActivity.NavType.DOCTOR_CATEGORY) && !content.isNoData()) {
                ((ContentVH) holder).addBtn.setVisibility(View.VISIBLE);
                ((ContentVH) holder).addBtn.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_background_green));
            } else if (navType == TourPlanOverviewActivity.NavType.CHEMIST && !content.isNoData()) {
                ((ContentVH) holder).addBtn.setVisibility(View.VISIBLE);
                ((ContentVH) holder).addBtn.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_background_blue));
            } else {
                ((ContentVH) holder).addBtn.setVisibility(View.GONE);
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
        ImageButton addBtn;

        public ContentVH(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_content);
            subContent = itemView.findViewById(R.id.tv_sub_content);
            sideContent = itemView.findViewById(R.id.tv_content_side);
            addBtn = itemView.findViewById(R.id.btn_add_tp);

        }
    }
}
