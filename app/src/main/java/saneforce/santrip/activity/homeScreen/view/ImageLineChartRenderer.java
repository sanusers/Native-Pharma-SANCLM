package saneforce.santrip.activity.homeScreen.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class ImageLineChartRenderer extends LineChartRenderer {

    private final LineChart lineChart;
    private final Bitmap image;

    public ImageLineChartRenderer(LineChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler, Bitmap image) {
        super(chart, animator, viewPortHandler);
        this.lineChart = chart;
        this.image = image;
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);

        Highlight[] highlighted = lineChart.getHighlighted();
        if (highlighted == null) return;

        float phaseY = mAnimator.getPhaseY();

        float[] imageBuffer = new float[2];
        imageBuffer[0] = 0;
        imageBuffer[1] = 0;
        LineData lineData = mChart.getLineData();
        List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();

        Bitmap[] scaledBitmaps = new Bitmap[dataSets.size()];
        float[] scaledBitmapOffsets = new float[dataSets.size()];
        for (int i = 0; i < dataSets.size(); i++) {
            float imageSize = dataSets.get(i).getCircleRadius() * 5;
            scaledBitmapOffsets[i] = imageSize / 2f;
            scaledBitmaps[i] = scaleImage((int) imageSize);
        }

        for (Highlight high : highlighted) {
            int dataSetIndex = high.getDataSetIndex();
            ILineDataSet set = lineData.getDataSetByIndex(dataSetIndex);
            Transformer trans = lineChart.getTransformer(set.getAxisDependency());

            if (set == null || !set.isHighlightEnabled())
                continue;

            Entry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            imageBuffer[0] = e.getX();
            imageBuffer[1] = e.getY() * phaseY;
            trans.pointValuesToPixel(imageBuffer);

            c.drawBitmap(scaledBitmaps[dataSetIndex],
                    imageBuffer[0] - scaledBitmapOffsets[dataSetIndex],
                    imageBuffer[1] - scaledBitmapOffsets[dataSetIndex],
                    mRenderPaint);
        }
    }

    private Bitmap scaleImage(int radius) {
        return Bitmap.createScaledBitmap(image, radius, radius, false);
    }
}