package saneforce.sanclm.commonClasses;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Parcelable;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import saneforce.sanclm.R;


public class CommonUtilsMethods {
    Context context;
    Activity activity;


    public CommonUtilsMethods(Activity activity) {
        this.activity = activity;
    }

    public CommonUtilsMethods(Context context) {
        this.context = context;
    }


    public void recycleTestWithoutDivider(RecyclerView rv_test) {
        try {
            if (rv_test.getItemDecorationCount() > 0) {
                for (int i = 0; i < rv_test.getItemDecorationCount(); i++) {
                    rv_test.removeItemDecorationAt(i);
                }
            }
            rv_test.setItemAnimator(new DefaultItemAnimator());
         //   rv_test.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            Parcelable recyclerViewState;
            recyclerViewState = rv_test.getLayoutManager().onSaveInstanceState();
            rv_test.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        } catch (Exception e) {
        }
    }

    public void displayPopupWindow(Activity activity, Context context, View view, String name) {
        PopupWindow popup = new PopupWindow(context);
        View layout = activity.getLayoutInflater().inflate(R.layout.popup_text, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        TextView tv_name = layout.findViewById(R.id.tv_name);
        tv_name.setText(name);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(view);

    }

    public void recycleTestWithDivider(RecyclerView rv_test) {
        try {
            if (rv_test.getItemDecorationCount() > 0) {
                for (int i = 0; i < rv_test.getItemDecorationCount(); i++) {
                    rv_test.removeItemDecorationAt(i);
                }
            }
            rv_test.setItemAnimator(new DefaultItemAnimator());
            rv_test.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            Parcelable recyclerViewState;
            recyclerViewState = rv_test.getLayoutManager().onSaveInstanceState();
            rv_test.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        } catch (Exception e) {
        }
    }

    public void setSpinText(Spinner spin, String text) {
        for (int i = 0; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().contains(text)) {
                spin.setSelection(i);
            }
        }
    }


    public void FullScreencall() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
