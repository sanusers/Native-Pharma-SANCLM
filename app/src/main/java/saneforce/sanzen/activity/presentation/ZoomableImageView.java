/*
package saneforce.sanzen.activity.presentation;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private float maxScale = 3f;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    private boolean isZooming = false;

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // NORMAL behavior on open
        setScaleType(ScaleType.FIT_XY);

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // Switch to matrix ONLY when user zooms
            if (!isZooming) {
                setScaleType(ScaleType.MATRIX);
                isZooming = true;
            }
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = scale * scaleFactor;

            if (newScale > maxScale) scaleFactor = maxScale / scale;
            if (newScale < 1f) scaleFactor = 1f / scale;

            scale *= scaleFactor;
            matrix.postScale(
                    scaleFactor,
                    scaleFactor,
                    detector.getFocusX(),
                    detector.getFocusY()
            );

            setImageMatrix(matrix);
            return true;
        }
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Reset to NORMAL view
            matrix.reset();
            scale = 1f;
            isZooming = false;
            setScaleType(ScaleType.FIT_XY);
            return true;
        }
    }
}*/

//final out with out drag

/*package saneforce.sanzen.activity.presentation;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private final Matrix matrix = new Matrix();

    private float scale = 1f;
    private final float maxScale = 3f;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private boolean isZooming = false;

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Default state: always fill screen
        setScaleType(ScaleType.FIT_XY);

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (!isZooming ) {

                setScaleType(ScaleType.MATRIX);
                matrix.reset();
                isZooming = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            // 🚫 Block zoom-out completely
            if (scaleFactor <= 1f) return true;

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
            setImageMatrix(matrix);

            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Optional: reset to FIT_XY on double tap
            resetToFitXY();
            return true;
        }
    }

    private void resetToFitXY() {
        scale = 1f;
        isZooming = false;
        matrix.reset();
        setImageMatrix(null);
        setScaleType(ScaleType.FIT_XY);
    }
}*/


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

    private float scale = 1f;
    private final float maxScale = 3f;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private boolean isZooming = false;

    // For panning
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

    // -------------------- DRAG / MOVE --------------------

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
                setImageMatrix(matrix);

                lastX = event.getX();
                lastY = event.getY();
                break;
        }
    }

    // -------------------- ZOOM --------------------

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (!isZooming) {
                setScaleType(ScaleType.MATRIX);
                matrix.reset();
                isZooming = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            // 🚫 Block zoom-out
            if (scaleFactor <= 1f) return true;

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
            setImageMatrix(matrix);
            return true;
        }
    }

    // -------------------- DOUBLE TAP RESET --------------------

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            resetToFitXY();
            return true;
        }
    }

    private void resetToFitXY() {
        scale = 1f;
        isZooming = false;
        matrix.reset();
        setImageMatrix(null);
        setScaleType(ScaleType.FIT_XY);
    }
}




