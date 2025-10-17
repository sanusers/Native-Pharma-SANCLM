package saneforce.sanzen.activity.call.fragments.signature;

import static saneforce.sanzen.activity.call.DCRCallActivity.SfCode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.commonClasses.CommonSharedPreference;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class SignatureCanvas extends View {

    Paint paint;
    Path path;
    int count = 0;
    Bitmap signatureBitmap;
    CommonSharedPreference commonSharedPreference;
    String imageName;
    SignatureCanvas signatureCanvas;
    String destinationFilePath;
    private boolean isSigned = false;


    public SignatureCanvas(Context context) {
        super(context);
        init();
    }

    public SignatureCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SignatureCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonSharedPreference = new CommonSharedPreference(context);
        init();
    }



    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
        if (signatureBitmap != null) {
            canvas.drawBitmap(signatureBitmap, 0, 0,paint);
        }
        canvas.drawPath(path, paint);

    }

    public void clearCanvas() {
        if (signatureBitmap != null) {
            signatureBitmap = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                ++count;
                try{
                    commonSharedPreference = new CommonSharedPreference(getContext());
                    commonSharedPreference.setValueToPreference("signCount",String.valueOf(count));
                }catch (Exception e){

                }
                path.lineTo(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }
    public Bitmap setSignatureBitmap(Bitmap bitmap) {
        return bitmap;
    }
    public Path getSignaturePath(){
        return path;
    }
    public void setSignaturePath(Path newPath) {
        this.path = newPath;
        invalidate();
    }


    public String getSignature() {

//        imageName = DCRCallActivity.CallActivityCustDetails.get(0).getCode();
        imageName = DCRCallActivity.CallActivityCustDetails.get(0).getName();
        if(imageName != null) {
            this.imageName = "Sign" + SfCode + "_" + imageName + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "") + CommonUtilsMethods.getCurrentInstance("HHmmss") + ".jpeg";

            File file = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                file = new File(getContext().getExternalFilesDir(null) + "/Signature/");
            } else {
                Log.d("Storage", "getSignaturePath: Storage not mounted");
            }
            if (file != null && !file.exists() && !file.mkdirs()) {
                Log.e("SignatureFlow", "Directory Created.");
            } else if (file != null) {
                Log.e("SignatureFlow", "Directory Creation failed or already exists.");
            } else {
                Log.e("SignatureFlow", "File object is null.");
            }

            File destinationFile = new File(file, imageName);
            destinationFilePath = destinationFile.getAbsolutePath();
            return destinationFilePath;
        }
        return destinationFilePath;
    }

    public String saveSignature() {
        if(path.isEmpty()){
            Log.d("CanvasPath", "saveSignature: "+"Nothing is drawn in the Canvas");
            return null;
        }else {
            String filePath = getSignature();
            if (filePath == null) {
                Log.e("SignatureFlow", "Failed to get a valid file path.");
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            this.draw(canvas);

            FileOutputStream fileOutputStream = null;
            try {
                File destinationFile = new File(filePath);
                fileOutputStream = new FileOutputStream(destinationFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                Log.d("SignatureFlow", "Signature saved to: " + filePath);
                return filePath;
            } catch (FileNotFoundException e) {
                Log.e("SignatureFlow", "Error saving signature (FileNotFound): " + e.getMessage());
                return null;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Log.e("SignatureFlow", "Error closing output stream: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        this.signatureBitmap = bitmap;
        invalidate();
    }

}