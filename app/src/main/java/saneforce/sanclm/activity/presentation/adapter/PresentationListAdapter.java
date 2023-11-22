package saneforce.sanclm.activity.presentation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.presentation.PresentationStoredModel;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class PresentationListAdapter extends RecyclerView.Adapter<PresentationListAdapter.ViewHolder> {
    Context context;
    ArrayList<PresentationStoredModel> presentationStoredModels;
    CommonUtilsMethods commonUtilsMethods;
    PopupWindow mypopupWindow;
    Menu menu;

    public PresentationListAdapter(Context context, ArrayList<PresentationStoredModel> presentationStoredModels) {
        this.context = context;
        this.presentationStoredModels = presentationStoredModels;
    }

    @NonNull
    @Override
    public PresentationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_presentation_list, parent, false);
        return new PresentationListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(presentationStoredModels.get(position).getPresentation_name());
        holder.tv_count.setText(presentationStoredModels.get(position).getCounts());

        holder.img_menu.setOnClickListener(view -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, view, Gravity.END);
            popup.inflate(R.menu.presentation_menu);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.v("fff", menuItem.toString());
                    if (menuItem.toString().equalsIgnoreCase("Play")) {

                    } else if (menuItem.toString().equalsIgnoreCase("Edit")) {

                    } else if (menuItem.toString().equalsIgnoreCase("Delete")) {
                        removeAt(position);
                    }
                    return true;
                }
            });
           /* setPopUpWindow(view);
            mypopupWindow.showAsDropDown(view, -153, 0);*/
         /*   PopupMenu popup = new PopupMenu(context, view);
            popup.getMenuInflater().inflate(R.menu.presentation_menu, popup.getMenu());*/
            popup.show();
        });
    }


    public void removeAt(int position) {
        presentationStoredModels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, presentationStoredModels.size());
    }

    private void setPopUpWindow(View view) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.pop_up_presentation, null);
        mypopupWindow = new PopupWindow(view, 200, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
    }


    @Override
    public int getItemCount() {
        return presentationStoredModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_menu;
        TextView tv_name, tv_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_menu = itemView.findViewById(R.id.img_menu);
            tv_count = itemView.findViewById(R.id.tv_asserts_count);
            tv_name = itemView.findViewById(R.id.tv_prs_name);
        }
    }
}
