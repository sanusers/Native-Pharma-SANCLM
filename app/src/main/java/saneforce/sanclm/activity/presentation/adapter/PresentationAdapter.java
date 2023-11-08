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
import android.widget.LinearLayout;
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

public class PresentationAdapter extends RecyclerView.Adapter<PresentationAdapter.MyViewHolder> {
    Context context;
    ArrayList<PresentationStoredModel> presentationStoredModels;
    CommonUtilsMethods commonUtilsMethods;
    PopupWindow mypopupWindow;
    Menu menu;

    public PresentationAdapter (Context context, ArrayList<PresentationStoredModel> presentationStoredModels) {
        this.context = context;
        this.presentationStoredModels = presentationStoredModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.presentation_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        PresentationStoredModel presentationStoredModel = presentationStoredModels.get(position);
        holder.name.setText(presentationStoredModel.getPresentation_name());
        holder.count.setText(presentationStoredModel.getCounts());

        holder.menu.setOnClickListener(view -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, view, Gravity.END);
            popup.inflate(R.menu.presentation_menu);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.v("test", menuItem.toString());
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout menu;
        TextView name, count;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.verticalDot);
            count = itemView.findViewById(R.id.presentationName);
            name = itemView.findViewById(R.id.assertCount);
        }
    }
}
