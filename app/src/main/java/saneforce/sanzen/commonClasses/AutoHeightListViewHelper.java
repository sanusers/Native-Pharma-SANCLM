package saneforce.sanzen.commonClasses;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AutoHeightListViewHelper {

    public static void setListViewHeight(final ListView listView) {

        if (listView == null) return;
        final ListAdapter adapter = listView.getAdapter();
        if (adapter == null) return;

        // Wait until ListView has real width
        listView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        calculateHeight(listView, adapter);
                    }
                }
        );
    }

    private static void calculateHeight(ListView listView, ListAdapter adapter) {

        int totalHeight = 0;

        int widthSpec = View.MeasureSpec.makeMeasureSpec(
                listView.getWidth(),
                View.MeasureSpec.EXACTLY
        );

        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);

            listItem.measure(
                    widthSpec,
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            totalHeight += listItem.getMeasuredHeight();
        }

        int dividerHeight = listView.getDividerHeight() * (adapter.getCount() - 1);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + dividerHeight;
        Log.d("Listview height", "calculateHeight: " + (totalHeight + dividerHeight));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}

