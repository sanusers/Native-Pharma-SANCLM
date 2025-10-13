package saneforce.sanzen.commonClasses;

import android.util.Log;
import android.view.View;

public abstract class SafeClickListener implements View.OnClickListener {
    private static final long MIN_CLICK_INTERVAL = 5000; // in ms
    private long lastClickTime;

    public abstract void onSafeClick(View view);

    @Override
    public final void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < MIN_CLICK_INTERVAL) {
            return;
        }
        Log.i("SAFE click", "onClick: " + currentTime);
        lastClickTime = currentTime;
        onSafeClick(view);
    }
}

