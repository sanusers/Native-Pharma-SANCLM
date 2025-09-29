package saneforce.sanzen.activity.presentation;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ZoomableImageView extends androidx.appcompat.widget.AppCompatImageView {

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private float[] lastEvent;
    private float scale = 1.0f;
    private float minScale = 1.0f;
    private boolean isMatrixSet = false;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private int mode = NONE;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private ScaleGestureDetector scaleDetector;

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

  /*  @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (!isMatrixSet) {
            fitImageToScreen(w, h);
            isMatrixSet = true;
        }
    }

    private void fitImageToScreen(int viewWidth, int viewHeight) {
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();


        float scaleX = (float) viewWidth / drawableWidth;
        float scaleY = (float) 800 / drawableHeight;


        minScale = Math.min(scaleX, scaleY);


        float dx = (viewWidth - drawableWidth * minScale) * 0.5f;
        float dy = (800 - drawableHeight * minScale) * 0.5f;


        matrix.setScale(minScale, minScale);
        matrix.postTranslate(dx, dy);


        scale = minScale;
        savedMatrix.set(matrix);
        setImageMatrix(matrix);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        // Check the mode for dragging
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                // DRAG is only allowed if the image is scaled larger than the initial fit size (minScale)
                if (scale > minScale) {
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM; // Set mode to ZOOM when a second finger touches the screen
                savedMatrix.set(matrix); // Save matrix before zoom starts
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG && scale > minScale) {
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;

                    // Apply translation based on the difference from the initial touch point
                    matrix.set(savedMatrix);
                    matrix.postTranslate(dx, dy);

                    // You should also add logic here to clamp the translation
                    // (prevent dragging the image off-screen)
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                // Save the final matrix state after drag/zoom ends
                savedMatrix.set(matrix);
                break;
        }

        setImageMatrix(matrix);
        return true;
    }*/


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                if (scale > 1.0f) {
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG && scale > 1.0f) {
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.set(savedMatrix);
                    matrix.postTranslate(dx, dy);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }

        setImageMatrix(matrix);
        return true;
    }
    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(1.0f, Math.min(scale, 3.0f));
            matrix.setScale(scale, scale,   detector.getFocusX(), detector.getFocusY()/*getWidth() / 2f, getHeight() / 2f*/);
            setImageMatrix(matrix);
            return true;
        }
    }
}