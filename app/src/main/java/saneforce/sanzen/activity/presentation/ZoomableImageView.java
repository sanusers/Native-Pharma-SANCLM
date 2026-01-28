
package saneforce.sanzen.activity.presentation;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];

    private float scale = 1f;
    private final float minScale = 1f;
    private final float maxScale = 5f;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private float lastX, lastY;

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.FIT_XY);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        // Allow dragging if we are zoomed in
        if (scale > minScale) {
            handleDrag(event);
        }
        return true;
    }

    private void handleDrag(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                lastX = event.getX();
                lastY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
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
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            if (getScaleType() != ScaleType.MATRIX) {
                setupMatrix();
                setScaleType(ScaleType.MATRIX);
            }
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float nextScale = scale * scaleFactor;

            if (nextScale > maxScale) {
                scaleFactor = maxScale / scale;
                scale = maxScale;
            } else if (nextScale < minScale) {
                scaleFactor = minScale / scale;
                scale = minScale;
            } else {
                scale = nextScale;
            }

            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            fixTranslation();
            setImageMatrix(matrix);

            if (scale <= minScale) {
                resetToFit();
            }
            return true;
        }
    }

    private void setupMatrix() {
        // Initialize matrix based on current FIT_CENTER position
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();

        float scaleW = viewWidth / drawableWidth;
        float scaleH = viewHeight / drawableHeight;
        float initScale = Math.min(scaleW, scaleH);

        matrix.reset();
        matrix.postScale(initScale, initScale);
        matrix.postTranslate((viewWidth - drawableWidth * initScale) / 2f,
                (viewHeight - drawableHeight * initScale) / 2f);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (scale > minScale) resetToFit();
            else {
                // Optional: Zoom in on double tap
                scale = 2f;
                setupMatrix();
                matrix.postScale(2f, 2f, e.getX(), e.getY());
                setScaleType(ScaleType.MATRIX);
                fixTranslation();
                setImageMatrix(matrix);
            }
            return true;
        }
    }

    private void resetToFit() {
        scale = 1f;
        matrix.reset();
        setImageMatrix(null);
        setScaleType(ScaleType.FIT_XY);
    }

    private void fixTranslation() {
        matrix.getValues(matrixValues);
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];

        Drawable d = getDrawable();
        if (d == null) return;

        float width = d.getIntrinsicWidth() * matrixValues[Matrix.MSCALE_X];
        float height = d.getIntrinsicHeight() * matrixValues[Matrix.MSCALE_Y];

        float viewWidth = getWidth();
        float viewHeight = getHeight();

        // Fix X translation
        if (width < viewWidth) {
            transX = (viewWidth - width) / 2;
        } else {
            transX = Math.min(0, Math.max(transX, viewWidth - width));
        }

        // Fix Y translation
        if (height < viewHeight) {
            transY = (viewHeight - height) / 2;
        } else {
            transY = Math.min(0, Math.max(transY, viewHeight - height));
        }

        matrixValues[Matrix.MTRANS_X] = transX;
        matrixValues[Matrix.MTRANS_Y] = transY;
        matrix.setValues(matrixValues);
    }
}
