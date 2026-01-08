package saneforce.sanzen.activity.presentation;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];

    private float scale = 1f;
    private final float maxScale = 3f;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private boolean isZooming = false;

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

        if (isZooming) {
            handleDrag(event);
        }
        return true;
    }


    private void handleDrag(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - lastX;
                float dy = event.getY() - lastY;

                matrix.postTranslate(dx, dy);
                fixTranslation();
                setImageMatrix(matrix);

                lastX = event.getX();
                lastY = event.getY();
                break;
        }
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (!isZooming) {
                setScaleType(ScaleType.MATRIX);
                matrix.reset();
                centerImage();
                isZooming = true;
            }
            return true;
        }

    /*    @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = scale * scaleFactor;

            if (newScale > maxScale) {
                scaleFactor = maxScale / scale;
            }

            scale *= scaleFactor;

            matrix.postScale(
                    scaleFactor,
                    scaleFactor,
                    detector.getFocusX(),
                    detector.getFocusY()
            );

            fixTranslation();
            setImageMatrix(matrix);
            return true;
        }*/
    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scaleFactor = detector.getScaleFactor();

        if (scaleFactor <= 1f) {
            return true;
        }

        float newScale = scale * scaleFactor;

        if (newScale > maxScale) {
            scaleFactor = maxScale / scale;
        }

        scale *= scaleFactor;

        matrix.postScale(
                scaleFactor,
                scaleFactor,
                detector.getFocusX(),
                detector.getFocusY()
        );

        fixTranslation();
        setImageMatrix(matrix);
        return true;
    }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            resetToFit();
            return true;
        }
    }

    private void resetToFit() {
        scale = 1f;
        isZooming = false;
        matrix.reset();
        setImageMatrix(null);
        setScaleType(ScaleType.FIT_XY);
    }


    private void fixTranslation() {
        if (getDrawable() == null) return;

        matrix.getValues(matrixValues);

        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];
        float scaleX = matrixValues[Matrix.MSCALE_X];
        float scaleY = matrixValues[Matrix.MSCALE_Y];

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        int imgWidth = getDrawable().getIntrinsicWidth();
        int imgHeight = getDrawable().getIntrinsicHeight();

        float scaledWidth = imgWidth * scaleX;
        float scaledHeight = imgHeight * scaleY;

        float minX = Math.min(0, viewWidth - scaledWidth);
        float minY = Math.min(0, viewHeight - scaledHeight);

        float clampedX = Math.max(minX, Math.min(transX, 0));
        float clampedY = Math.max(minY, Math.min(transY, 0));

        matrixValues[Matrix.MTRANS_X] = clampedX;
        matrixValues[Matrix.MTRANS_Y] = clampedY;

        matrix.setValues(matrixValues);
    }

    private void centerImage() {
        if (getDrawable() == null) return;

        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float imgWidth = getDrawable().getIntrinsicWidth();
        float imgHeight = getDrawable().getIntrinsicHeight();

        float dx = (viewWidth - imgWidth) / 2f;
        float dy = (viewHeight - imgHeight) / 2f;

        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);
    }
}



