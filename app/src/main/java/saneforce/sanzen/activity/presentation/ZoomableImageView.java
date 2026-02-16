
package saneforce.sanzen.activity.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];

    private float scale = 1f;      // User zoom level
    private float baseScale = 1f;  // Initial fit scale
    private final float minScale = 1f;
    private final float maxScale = 5f;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private float lastX, lastY;
    private boolean isDragging = false;

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        fitImageToView();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        post(this::fitImageToView);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        post(this::fitImageToView);
    }

    private void fitImageToView() {
        Drawable d = getDrawable();
        if (d == null) return;

        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float dw = d.getIntrinsicWidth();
        float dh = d.getIntrinsicHeight();

        if (dw == 0 || dh == 0 || viewWidth == 0 || viewHeight == 0) return;

        float scaleX = viewWidth / dw;
        float scaleY = viewHeight / dh;

        // FIT_CENTER behavior
        baseScale = Math.max(scaleX, scaleY);

        matrix.reset();
        matrix.postScale(baseScale, baseScale);
        matrix.postTranslate(
                (viewWidth - dw * baseScale) / 2f,
                (viewHeight - dh * baseScale) / 2f
        );

        scale = 1f;
        setImageMatrix(matrix);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        if (scale > minScale) {
            handleDrag(event);
        }

        return true;
    }

    private void handleDrag(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                isDragging = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isDragging) break;

                float dx = event.getX() - lastX;
                float dy = event.getY() - lastY;

                matrix.postTranslate(dx, dy);
                fixTranslation();
                setImageMatrix(matrix);

                lastX = event.getX();
                lastY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = scale * scaleFactor;

            if (newScale > maxScale) {
                scaleFactor = maxScale / scale;
                scale = maxScale;
            } else if (newScale < minScale) {
                scaleFactor = minScale / scale;
                scale = minScale;
            } else {
                scale = newScale;
            }

            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            fixTranslation();
            setImageMatrix(matrix);
            return true;
        }
    }

    // DOUBLE TAP → RESET ONLY
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            resetZoom();
            return true;
        }
    }

    private void resetZoom() {
        fitImageToView();
    }

    private void fixTranslation() {
        matrix.getValues(matrixValues);
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];
        float currentScale = matrixValues[Matrix.MSCALE_X];

        Drawable d = getDrawable();
        if (d == null) return;

        float width = d.getIntrinsicWidth() * currentScale;
        float height = d.getIntrinsicHeight() * currentScale;

        float viewWidth = getWidth();
        float viewHeight = getHeight();

        if (width < viewWidth) {
            transX = (viewWidth - width) / 2;
        } else {
            transX = Math.max(viewWidth - width, Math.min(transX, 0));
        }

        if (height < viewHeight) {
            transY = (viewHeight - height) / 2;
        } else {
            transY = Math.max(viewHeight - height, Math.min(transY, 0));
        }

        matrixValues[Matrix.MTRANS_X] = transX;
        matrixValues[Matrix.MTRANS_Y] = transY;
        matrix.setValues(matrixValues);
    }
}