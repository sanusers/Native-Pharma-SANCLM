package saneforce.sanzen.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import saneforce.sanzen.R;
import saneforce.sanzen.databinding.ViewLoadingDotsBinding;

public class LoadingDotsView extends LinearLayout {

    private ViewLoadingDotsBinding binding;
    private Animation pulse1, pulse2, pulse3;

    public LoadingDotsView(Context context) {
        super(context);
        init(context);
    }

    public LoadingDotsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingDotsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        binding = ViewLoadingDotsBinding.inflate(LayoutInflater.from(context), this);
        setupAnimations(context);
    }

    private void setupAnimations(Context context) {
        pulse1 = AnimationUtils.loadAnimation(context, R.anim.pulse);
        pulse2 = AnimationUtils.loadAnimation(context, R.anim.pulse);
        pulse3 = AnimationUtils.loadAnimation(context, R.anim.pulse);

        pulse1.setStartOffset(0);
        pulse2.setStartOffset(200);
        pulse3.setStartOffset(400);
    }

    public void startLoading() {
        setVisibility(VISIBLE);
        if (binding == null) return;
        binding.dot1.startAnimation(pulse1);
        binding.dot2.startAnimation(pulse2);
        binding.dot3.startAnimation(pulse3);
    }

    public void stopLoading() {
        if (binding == null) return;
        binding.dot1.clearAnimation();
        binding.dot2.clearAnimation();
        binding.dot3.clearAnimation();
        setVisibility(GONE);
    }

    public void setLoadingText(String text) {
        binding.tvLoadingText.setText(text);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        binding = null;
    }
}
