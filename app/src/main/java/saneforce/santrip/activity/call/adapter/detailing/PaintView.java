package saneforce.santrip.activity.call.adapter.detailing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import saneforce.santrip.activity.call.pojo.detailing.ModelPaint;


public class PaintView extends View
{
    Path paths;
    Paint paints;
    Region r;
    private float mScaleFactor = 1.f;
    private float mScaleFactor1 = 1.f;
    private ScaleGestureDetector mScaleDetector;
    boolean isScale=false,isDrag=false;
    boolean isDoublePinch=false;
    int width,height;
    float checkPrevScale,checkPresentScale=1.f;
    float storePrevScale;
    int prev_X, prev_Y, current_X, current_Y,endX,endY,startX,startY;
    int angle;
    private final RectF uneven_rect = new RectF();
    ArrayList<Path> shapePath=new ArrayList<>();
    ArrayList<Paint> shapePaint=new ArrayList<>();
    ArrayList<Path> linePath=new ArrayList<>();
    ArrayList<Paint> linePaint=new ArrayList<>();
    ArrayList<Integer> indexArrow=new ArrayList<>();
    ArrayList<ModelPaint> indexesArrowPath=new ArrayList<>();
    ArrayList<Path> SketchAllPath=new ArrayList<>();
    ArrayList<Paint> SketchAllPaint=new ArrayList<>();
    int selectedPathIndex;
    boolean isArrow=false;
    boolean isErase=false;
    int firstArrow,secondArrow,lineArrow;
    Paint SketchPaint;
    Path SketchPath;
    static int paintColor=Color.BLACK;
    boolean canload_bitmap;
    Bitmap second_bitmap,bitmap;
    Paint erasePaint;
    int prevAngle;
    int checkPrevAngle=0;
    int checkArrow;

    public PaintView(Context context)
    {
        super(context);
        init();
        initSketch();
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public PaintView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
        initSketch();
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
        initSketch();
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void initSketch()
    {
        if(!isErase)
        {
             SketchPath=new Path();
             SketchPaint=new Paint();
             SketchPaint.setColor(paintColor);
             SketchPaint.setStrokeWidth(5);
             SketchPaint.setStyle(Paint.Style.STROKE);
             SketchPaint.setAntiAlias(true);
        }
        else
        {
            SketchPath=new Path();
            SketchPaint = new Paint();
            SketchPaint.setAntiAlias(true);
            SketchPaint.setColor(paintColor);
            SketchPaint.setStyle(Paint.Style.STROKE);
            SketchPaint.setStrokeJoin(Paint.Join.ROUND);
            SketchPaint.setStrokeCap(Paint.Cap.ROUND);
            SketchPaint.setStrokeWidth(20);
            SketchPaint.setAlpha(0);
            SketchPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
       /* SketchPath=new Path();
        */
    }

    public void init()
    {
        paths=new Path();
        paints=new Paint();
        paints.setColor(paintColor);
        paints.setStrokeWidth(5);
        paints.setStyle(Paint.Style.STROKE);
        paints.setAntiAlias(true);
    }

    public void drawShapePath(Canvas canvas)
    {
        int k=0;
        for(Path p2:SketchAllPath)
        {
            canvas.drawPath(p2, SketchAllPaint.get(k));
            k++;
        }
        int i = 0;
        for (Path p : shapePath)
        {
            canvas.drawPath(p, shapePaint.get(i));
            i++;
        }
        int j=0;
        for(Path p1:linePath)
        {
            canvas.drawPath(p1, linePaint.get(j));
            j++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
       // canvas.drawLine(871,871,-0,0,SketchPaint);
        //getArrowAngle(50,50,50,20);
       /*
        canvas.drawLine(50,50,100,70,SketchPaint);*/
    /*    getArrowAngle(50,50,100,50);
        canvas.drawLine(50,50,100,50,SketchPaint);
        float centerx=width/2;
        float centery=height/2;
        float x = 100 - centerx;
        float y =  centery - 70;
        double angleB = ComputeAngle(x, y);
        x = 50 - centerx;
        y = centery - 50;
        double angleA = ComputeAngle(x,y);
        float rotation = (int)(angleA - angleB);
        angle= (int) rotation;
        Log.v("line_center_pt",angle+"");*/
       /* paths.moveTo(50, 50);
        paths.lineTo(100, 50);
        paths.lineTo(100);
        paths.lineTo(80);
        paths.close();
        canvas.drawPath(paths, paints);
        RectF rectF = new RectF();
        paths.computeBounds(rectF, true);
        r = new Region();
        r.setPath(paths, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));*/
       /* if(isArrow){
            isDrag=false;
            getArrowAngle(prev_X,prev_Y,endX,endY);
            rotateArrow();
            //getNewPosition(event.getX(), event.getY());
            //moveLine();
        }*/
        if(isScale || isDrag)
        {
           /* Matrix scaleMatrix = new Matrix();
            RectF rectF = new RectF();
            paths.computeBounds(rectF, true);
            scaleMatrix.setScale(mScaleFactor, mScaleFactor, rectF.centerX(), rectF.centerY());
            //scaleMatrix.setScale(sX, sY);
            paths.transform(scaleMatrix);*/
            RectF rectF = new RectF();
            paths.computeBounds(rectF, true);
            r = new Region();
            r.setPath(paths, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        }
        else
        {
           /* paths.moveTo(50, 50);
            paths.lineTo(200, 50);
            paths.lineTo(200, 200);
            paths.lineTo(50, 200);
            paths.close();
            RectF rectF = new RectF();
            paths.computeBounds(rectF, true);
            r = new Region();
            r.setPath(paths, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));*/
        }
       /* if(canload_bitmap)
        canvas.drawBitmap(second_bitmap,0,0,paints);*/
        Log.v("canvas_drawwPath","here");
        drawShapePath(canvas);
        canvas.drawPath(paths, paints);
        canvas.drawPath(SketchPath, SketchPaint);
       /* if(isScale) {
            canvas.save();
            //Log.d("DEBUG", "X: "+mPosX+" Y: "+mPosY);
            canvas.translate(50, 50);
            canvas.scale(mScaleFactor, mScaleFactor);
        }*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width=MeasureSpec.getSize(widthMeasureSpec);
        height=MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        mScaleDetector.onTouchEvent(event);
        Point point = new Point();
       /* Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();
        //points.add(point);
        invalidate();
        Log.d("paintVieww", "point: " + point);
        if(r.contains((int)point.x,(int) point.y)) {
            Log.d("paintVieww", "Touch IN");
            isDoublePinch = true;
        }
        else {
            Log.d("paintVieww", "Touch OUT");
        }*/
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                Log.v("Action_down_ins","double_point");
                initiatePrevXY(event.getX(),event.getY());
                ComputeAngle(prev_X,prev_Y);
                Log.v("scaleFactor_value_112",checkSelectedpath(prev_X,prev_Y)+"");
                prevAngle=getArrowAngle(prev_X,prev_Y,prev_X+10,prev_Y+10);
                if(checkSelectedpath(prev_X,prev_Y))
                    isDrag=true;
                else
                {
                    SketchPath.moveTo(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isScale || !isDoublePinch)
                {
                    if(isArrow)
                    {
                       isDrag=false;
                       endX= (int) event.getX();
                       endY= (int) event.getY();
                       int angleval;
                       ComputeAngle(endX,endY);
                       getNewPosition(event.getX(), event.getY());
                       int presentAngle=getArrowAngle(startX,startY,event.getX(),event.getY());
                       if(prevAngle>presentAngle)
                       {
                           angleval=prevAngle-presentAngle;
                       }
                       else
                           angleval=presentAngle-prevAngle;
                       if(angleval>15)
                       {
                           angle=2;
                       }
                       else
                           angle=angleval;
                        float centerx=width/2;
                        float centery=height/2;
                        float x = event.getX() - centerx;
                        float y =  centery - event.getY();
                        double angleB = ComputeAngle(x, y);
                        x = prev_X - centerx;
                        y = centery - prev_Y;
                        double angleA = ComputeAngle(x,y);
                        float rotation = (int)(angleA - angleB);
                        Log.v("angllee_11B",angleB+" angle_A "+angleA+" rotation "+rotation);
                        int newangle= (int) angleB;
                        newangle=360-newangle;
                        if(checkPrevAngle!=0)
                        {
                            angle=newangle-checkPrevAngle;
                            Log.v("angle_move_11",angle+" newAngle "+newangle+" prevangle "+checkPrevAngle+" angleB "+angleB);
                            checkPrevAngle=newangle;
                        }
                        else
                        {
                            angle=newangle;
                            checkPrevAngle=angle;
                            Log.v("angle_move_22",angle+"");
                        }
                       // angle= (int) rotation;
                        rotateArrow();
                        moveLine();
                        /*int presentAngle=getArrowAngle(prev_X,prev_Y,event.getX(),event.getY());
                        int angleval=presentAngle-prevAngle;
                        if(angleval>0){
                            angle=angleval;
                        }
                        else{
                            angle=1;
                        }
                        Log.v("rotateArroww_finder", String.valueOf(angle)+"prev_X "+prevAngle+" present "+presentAngle);
                        prevAngle=presentAngle;*/
                        //rotateArrow();
                       /* float centerx=width/2;
                        float centery=height/2;
                        float x = event.getX() - centerx;
                        float y =  centery - event.getY();
                            double angleB = ComputeAngle(x, y);
                             x = prev_X - centerx;
                             y = centery - prev_Y;
                            double angleA = ComputeAngle(x,y);
                             float rotation = (int)(angleA - angleB);
                             angle= (int) rotation;
                        getNewPosition(event.getX(), event.getY());
                        //moveLine();
                        rotateArrow();*/
                       /*
                        getArrowAngle(prev_X,prev_Y,event.getX(),event.getY());
                        */
                        Log.v("getArrowAngle",angle+" rot ");
                        //getNewPosition(event.getX(), event.getY());
                    }
                   else if (isDrag)
                        getNewPosition(event.getX(), event.getY());
                    else
                    {
                        paintSmoothly(event.getX(),event.getY(),event);
                       // SketchPath.lineTo(event.getX(), event.getY());
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isDrag=false;
                isArrow=false;
               // rotateAtUp();
                SketchAllPath.add(SketchPath);
                SketchAllPaint.add(SketchPaint);
                initSketch();
                invalidate();
                break;
        }
        /*int x, y;
        x = (int) event.getX();
        y = (int) event.getY();
        Path tempPath = new Path(); // Create temp Path
        tempPath.moveTo(x,y); // Move cursor to point
        RectF rectangle = new RectF(x-1, y-1, x+1, y+1); // create rectangle with size 2xp
        tempPath.addRect(rectangle, Path.Direction.CW); // add rect to temp path
        tempPath.op(paths, Path.Op.DIFFERENCE); // get difference with our PathToCheck
        if (tempPath.isEmpty()) // if out path cover temp path we get empty path in result
        {
            Log.d("paintVieww", "Path contains this point");
            return true;
        }
        else
        {
            Log.d("paintVieww", "Path don't contains this point");
            return false;
        }*/
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            super.onScaleBegin(detector);
            Log.v("scaleFactor_value_111",checkSelectedpath(prev_X,prev_Y)+"");
            if(!isArrow)
            {
                if (checkSelectedpath(prev_X, prev_Y))
                {
                    isDoublePinch = true;
                }
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector)
        {
            super.onScaleEnd(detector);
            Log.v("The_end_scaling","are_enddd");
            isDoublePinch = false;
        }

        @SuppressLint("SuspiciousIndentation")
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            mScaleFactor = detector.getScaleFactor();
            if(isDoublePinch)
            {
                isScale = true;
               // Log.v("scaleFactor_value_11",mScaleFactor+" original "+detector.getScaleFactor());
                checkPresentScale=mScaleFactor;
                // Don't let the object get too small or too large.
                mScaleFactor = Math.max(0.3f, Math.min(mScaleFactor, 1.3f));
                Log.v("scaleFactor_value",checkPresentScale+" yyy "+checkPrevScale);
                storePrevScale=mScaleFactor;
                if(mScaleFactor>1)
                {
                    mScaleFactor1=1.10f;
                }
                else
                    mScaleFactor1=0.90f;
             /*   if(mScaleFactor>0.){
                    mScaleFactor1=1.01f;
                }
                else
                    mScaleFactor1=0.1f;*/
              // mScaleFactor=1.01f;
                Log.v("scaleFactor_value_sss",checkPresentScale+" gh "+mScaleFactor1);
                Matrix scaleMatrix = new Matrix();
                RectF rectF = new RectF();
                shapePath.get(selectedPathIndex).computeBounds(rectF, true);
                scaleMatrix.postScale(mScaleFactor, mScaleFactor, rectF.centerX(), rectF.centerY());
                //scaleMatrix.postTranslate(detector.getFocusX() + detector.getFocusShiftX(), detector.getFocusY() + detector.getFocusShiftY());
                //scaleMatrix.setScale(sX, sY);
                checkPrevScale=checkPresentScale;
                shapePath.get(selectedPathIndex).transform(scaleMatrix);
                invalidate();
            }
            return true;
        }
    }

    public void initiatePrevXY(float _initX, float _initY)
    {
        prev_X = (int) _initX;
        prev_Y = (int) _initY;
        startX = (int) _initX;
        startY = (int) _initY;
        invalidate();
    }

    public void getNewPosition(float newX,float newY)
    {
        current_X = (int) newX;
        current_Y = (int) newY;
        int _MoveX = current_X - prev_X;
        int _MoveY = current_Y - prev_Y;
        prev_X = current_X;
        prev_Y = current_Y;
        transformPath(_MoveX,_MoveY);
    }

    public void transformPath(int _x, int _y)
    {
        try
        {
            RectF rectF=new RectF();
            Matrix transform_matrix = new Matrix();
            transform_matrix.setTranslate(_x, _y);
            shapePath.get(selectedPathIndex).computeBounds(rectF, true);
            shapePath.get(selectedPathIndex).transform(transform_matrix);
            invalidate();
        }
        catch (Exception e)
        {
            System.out.println("error at image annotation" + e);
        }
    }

    public boolean checkSelectedpath(float evex,float evey)
    {
        Point point = new Point();
        point.x = (int) evex;
        point.y = (int) evey;
        for(int i=0;i<shapePath.size();i++)
        {
            RectF rectF = new RectF();
            shapePath.get(i).computeBounds(rectF, true);
            r = new Region();
            r.setPath(shapePath.get(i), new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            if(r.contains((int)point.x,(int) point.y))
            {
                selectedPathIndex=i;
                if(indexArrow.contains(i))
                {
                    isArrow = true;
                    for (int k = 0; k < indexesArrowPath.size(); k++)
                    {
                        ModelPaint pp = indexesArrowPath.get(k);
                        if (pp.getStartArrow() == i)
                        {
                            firstArrow = pp.getStartArrow();
                            lineArrow = pp.getPathLine();
                            secondArrow = pp.getEndArrow();
                            checkArrow=1;
                        }
                        else if (pp.getEndArrow() == i)
                        {
                            secondArrow = pp.getEndArrow();
                            lineArrow = pp.getPathLine();
                            firstArrow = pp.getStartArrow();
                            checkArrow=2;
                        }
                    }
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    public void addRectangle()
    {
        isErase=false;
        paths.addRect(50, 50, 200, 200, Path.Direction.CCW);
        shapePath.add(paths);
        shapePaint.add(paints);
        init();
        invalidate();
    }

    public void addLine()
    {
        paths.moveTo(50,50);
        paths.lineTo(500,50);
        shapePath.add(paths);
        shapePaint.add(paints);
        Matrix matrix = new Matrix();
        RectF bounds = new RectF();
        matrix.postRotate(45, 500, 50);
        shapePath.get(0).transform(matrix);
    }

    public void addCircle()
    {
        isErase=false;
        paths.addCircle(200,200,100,Path.Direction.CCW);
        shapePath.add(paths);
        shapePaint.add(paints);
        init();
        invalidate();
    }

    public void addArrows()
    {
        int v1 = 30,v2 = 30, v3 = 30, v4 = 100;
        int x=50,y=50;
        int _length=50;
       /* paths.moveTo(x, y);
        paths.lineTo(x + (v1 / 2), y);
        paths.lineTo(x + (v1 / 2), y - _length);
        paths.lineTo((x + (v1 / 2)) + (v2 / 2), (y - _length) + (v3 / 2));
        paths.lineTo(((x + (v1 / 4))), ((y - _length) + (v3 / 2)) - (v4 / 2));
        paths.lineTo(x - (v2 / 2), ((y - _length) + (v3 / 2)));
        paths.lineTo(x, (y - _length));
        paths.lineTo(x, y);
        paths.close();*/
      /*  paths.moveTo(50,50);
        paths.lineTo(90,65);
        paths.lineTo(50,80);
        paths.cubicTo(50,80,70,65,50,50);
        paths.close();
*/
        paths.moveTo(50,50);
        paths.lineTo(35,75);
        paths.lineTo(65,75);
        paths.lineTo(50,50);
        paths.close();
        shapePath.add(paths);
        shapePaint.add(paints);
        indexArrow.add(shapePath.size()-1);
        Matrix matrix = new Matrix();
        RectF bounds = new RectF();
        shapePath.get(0).computeBounds(bounds, false); // fills rect with bounds
        PointF center = new PointF((bounds.left + bounds.right) / 2, (bounds.top + bounds.bottom) / 2);
        matrix.setRotate(315, center.x, center.y);
        shapePath.get(0).transform(matrix);
        shapePath.get(0).computeBounds(bounds, true);
      /*  paths.moveTo(200,50);
        paths.lineTo(240,65);
        paths.lineTo(200,80);
        paths.cubicTo(200,80,220,65,200,50);
        paths.close();
        shapePath.add(paths);*/
      /*  */
        isArrow=true;
        init();
        invalidate();
    }

    public void addArrow()
    {
        /*paths.moveTo(100,25);
        paths.lineTo(125,50);
        paths.lineTo(100,75);
        paths.lineTo(100,25);
        paths.close();
        shapePath.add(paths);
        indexArrow.add(shapePath.size()-1);
        paths=new Path();
        paths.moveTo(50,50);
        paths.lineTo(100,50);
        linePath.add(paths);
        paths=new Path();*/
        isErase=false;
        init();
        int shape1,shape2,line1;
      /*  paths.moveTo(50,50);
        paths.lineTo(90,65);
        paths.lineTo(50,80);
        paths.cubicTo(50,80,70,65,50,50);
        paths.close();*/
        paths.moveTo(50,50);
        paths.lineTo(35,75);
        paths.lineTo(65,75);
        paths.lineTo(50,50);
        paths.close();
        shapePath.add(paths);
        paints.setStyle(Paint.Style.FILL);
        shapePaint.add(paints);
        indexArrow.add(shapePath.size()-1);
        shape1=shapePath.size()-1;
        PointF pt1=getMidPoint(shapePath.size()-1);
        init();
        /*paths.moveTo(200,50);
        paths.lineTo(240,65);
        paths.lineTo(200,80);
        paths.cubicTo(200,80,220,65,200,50);
        paths.close();*/
        paths.moveTo(50,200);
        paths.lineTo(35,215);
        paths.lineTo(65,215);
        paths.lineTo(50,200);
        paths.close();
        shapePath.add(paths);
        paints.setStyle(Paint.Style.FILL);
        shapePaint.add(paints);
        indexArrow.add(shapePath.size()-1);
        shape2=shapePath.size()-1;
        PointF pt2=getMidPoint(shapePath.size()-1);
        init();
        paths.moveTo(pt1.x,pt1.y);
        paths.lineTo(pt2.x,pt2.y);
        linePath.add(paths);
        linePaint.add(paints);
        init();
        line1=linePath.size()-1;
        indexesArrowPath.add(new ModelPaint(shape1,shape2,line1));
        invalidate();
    }

    public void rotateAtUp()
    {
        paths=new Path();
        Log.v("start_canva_pt",current_X+" yval "+current_X+" end_angle "+Math.cos(checkPrevAngle)+" yval "+Math.sin(checkPrevAngle));
        paths.moveTo(current_X,current_X);
        paths.lineTo((int) Math.cos(checkPrevAngle), (int) Math.sin(checkPrevAngle));
        paths.close();
        shapePath.set(selectedPathIndex,paths);
        paths=new Path();
        invalidate();
    }

    public void rotateArrow()
    {
        Matrix matrix = new Matrix();
        RectF bounds = new RectF();
        shapePath.get(selectedPathIndex).computeBounds(bounds, true);
        PointF secc;
        if(checkArrow==1)
            secc=getMidPoint(firstArrow);
        else
            secc=getMidPoint(secondArrow);
        matrix.postRotate(angle, secc.x, secc.y);
        shapePath.get(selectedPathIndex).transform(matrix);
       /* matrix.setRotate(angle, endX, endY);
        //matrix.postRotate(angle);
        shapePath.get(selectedPathIndex).transform(matrix);
        shapePath.get(selectedPathIndex).computeBounds(bounds, true);*/
        invalidate();
    }

    public int getArrowAngle(float _startx, float _starty, float _endx, float _endy)
    {
        try
        {
            final double RADS_TO_DEGREES = 360 / (Math.PI*2);
            double result = Math.atan2(_endx,_endy) * RADS_TO_DEGREES;
            angle = (int) (Math.atan2(_endy - _starty, _endx - _startx) * 180 / Math.PI + 180);
            Log.v("checking_angle_val",angle+" res "+result);
            if (angle >= 90)
            {
                angle = angle - 90;
            }
            else if (angle < 90)
            {
                angle = angle + 270;
            }
            Log.v("checking_angle_val_fina",angle+"");
        }
        catch (Exception e)
        {
            System.out.println("error at image annotation" + e);
        }
        return angle;
    }

    public void moveLine()
    {
       /* RectF bounds = new RectF();
        shapePath.get(firstArrow).computeBounds(bounds, false); // fills rect with bounds
        PointF centerval = new PointF((bounds.left + bounds.right) / 2,
                (bounds.top + bounds.bottom) / 2);
        RectF bounds1 = new RectF();
        shapePath.get(secondArrow).computeBounds(bounds1, false); // fills rect with bounds
        PointF center = new PointF((bounds1.left + bounds1.right) / 2,
                (bounds1.top + bounds1.bottom) / 2);*/
        PointF firstc=getMidPoint(firstArrow);
        PointF secc=getMidPoint(secondArrow);
        paths.moveTo(firstc.x,firstc.y);
        paths.lineTo(secc.x,secc.y);
        linePath.set(lineArrow,paths);
        paths=new Path();
        Log.v("length_line_patjh",linePath.size()+"");
        invalidate();
    }

    public PointF getMidPoint(int ind)
    {
        RectF bounds = new RectF();
        shapePath.get(ind).computeBounds(bounds, false); // fills rect with bounds
        PointF center = new PointF((bounds.left + bounds.right) / 2, (bounds.top + bounds.bottom) / 2);
        return center;
    }

    public void changePaintColor(int x)
    {
        isErase=false;
        switch (x)
        {
            case 1:
                paintColor=Color.BLACK;
                break;
            case 2:
                paintColor=Color.RED;
                break;
            case 3:
                paintColor=Color.GREEN;
                break;
        }
        //paintColor=Color.parseColor("#636161");
       /* paints.setColor(Color.RED);
        SketchPaint.setColor(Color.RED);*/
        initSketch();
        invalidate();
    }

    private void paintSmoothly(float eventX, float eventY, MotionEvent event)
    {
        try
        {
            resetUnevenRect(eventX, eventY);
            int historySize = event.getHistorySize();
            for (int i = 0; i < historySize; i++)
            {
                 float historicalX = event.getHistoricalX(i);
                 float historicalY = event.getHistoricalY(i);
                 expandUnevenRect(historicalX, historicalY);
                 SketchPath.lineTo(historicalX, historicalY);
            }
            SketchPath.lineTo(eventX, eventY);
        }
        catch (Exception e)
        {
            System.out.println("error at image annotation" + e);
        }
    }

    private void expandUnevenRect(float historicalX, float historicalY)
    {
        try
        {
            if (historicalX < uneven_rect.left)
            {
                uneven_rect.left = historicalX;
            }
            else if (historicalX > uneven_rect.right)
            {
                uneven_rect.right = historicalX;
            }
            if (historicalY < uneven_rect.top)
            {
                uneven_rect.top = historicalY;
            }
            else if (historicalY > uneven_rect.bottom)
            {
                uneven_rect.bottom = historicalY;
            }
        }
        catch (Exception e)
        {
            System.out.println("error at image annotation" + e);
        }
    }

    private void resetUnevenRect(float eventX, float eventY)
    {
        try
        {
            uneven_rect.left = Math.min(prev_X, eventX);
            uneven_rect.right = Math.max(prev_X, eventX);
            uneven_rect.top = Math.min(prev_Y, eventY);
            uneven_rect.bottom = Math.max(prev_Y, eventY);
        }
        catch (Exception e)
        {
            System.out.println("error at image annotation" + e);
        }
    }

    public void loadNewBitmap(String _path)
    {
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            second_bitmap = BitmapFactory.decodeFile(_path, options);
            canload_bitmap = true;
            invalidate();
        }
        catch (Exception e)
        {
            System.out.println("error at image annotation" + e);
        }
    }

    public void erase()
    {
        try
        {
            isErase=true;
            initSketch();
            SketchPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
           // invalidate();
        }
        catch (Exception e)
        {
            System.out.println("error at image annotation" + e);
        }
    }

    double ComputeAngle(float x, float y)
    {
        final double RADS_TO_DEGREES = 360 / (Math.PI*2);
        double result = Math.atan2(y,x) * RADS_TO_DEGREES;
        Log.v("getArrowAngle_111",result+"");
        if (result < 0)
        {
            result = 360 + result;
        }
        Log.v("getArrowAngl_222",result+"");
        return result;
    }
}
