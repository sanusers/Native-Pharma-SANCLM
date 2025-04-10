package saneforce.sanzen.commonClasses;

import android.content.Context;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import android.graphics.Canvas;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GifView extends View {

    private Movie gifMovie;
    private long movieStart;

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGifResource(int resId) {
        try (InputStream inputStream = getContext().getResources().openRawResource(resId)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byte[] gifBytes = byteArrayOutputStream.toByteArray();
            gifMovie = Movie.decodeByteArray(gifBytes, 0, gifBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (gifMovie != null) {
            int desiredWidth = MeasureSpec.getSize(widthMeasureSpec);
            int desiredHeight = MeasureSpec.getSize(heightMeasureSpec);

            setMeasuredDimension(desiredWidth, desiredHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (gifMovie == null) {
            return;
        }

        long now = SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }

//        int duration = gifMovie.duration();
//        if (duration == 0) {
//            duration = 3000;
//        }

        int elapsedTime = (int) ((now - movieStart) % gifMovie.duration());
        gifMovie.setTime(elapsedTime);

        int movieWidth = gifMovie.width();
        int movieHeight = gifMovie.height();

        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float scaleFactorX = viewWidth / movieWidth;
        float scaleFactorY = viewHeight / movieHeight;

        canvas.save();
        canvas.scale(scaleFactorX, scaleFactorY);
        gifMovie.draw(canvas, 0, 0);
        canvas.restore();
        invalidate();
    }
}
