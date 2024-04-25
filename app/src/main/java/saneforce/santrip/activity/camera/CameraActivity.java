package saneforce.santrip.activity.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import saneforce.santrip.R;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.GPSTrack;
import saneforce.santrip.databinding.ActivityCameraBinding;

public class CameraActivity extends AppCompatActivity implements ImageReader.OnImageAvailableListener {

    private ActivityCameraBinding activityCameraBinding;
    private static final int REQUEST_CODE = 1001;
    private String frontCameraId, backCameraId;
    private CameraDevice cameraDevice;
    private ImageReader imageReader;
    private CameraCaptureSession cameraCaptureSession;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private boolean isImageCaptured = false;
    private int width, height;
    private CameraManager cameraManager;
    private boolean isBackCameraOpen = true;
    private boolean isFlashEnabled = false;
    private CommonUtilsMethods commonUtilsMethods;
    private GPSTrack gpsTrack;
    private double latitude, longitude;
    private String date, time;
    private String address;
    private final String TAG = "Camera Activity";
    private String filePath, cameraMode, from;
    private boolean lTagEnabled = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            activityCameraBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCameraBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(activityCameraBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        gpsTrack = new GPSTrack(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            filePath = extras.getString("FILE_PATH");
            lTagEnabled = extras.getBoolean("L_FLAG", false);
            cameraMode = extras.getString("CAMERA_MODE", "BACK");
            from = extras.getString("FROM");
        }

        if(cameraMode != null) {
            switch (cameraMode){
                case "BACK":
                case "FRONT":
                    activityCameraBinding.switchCamera.setVisibility(View.GONE);
                    break;
                case "ALL":
                    activityCameraBinding.switchCamera.setVisibility(View.VISIBLE);
                    break;
            }
        }

        if(lTagEnabled) activityCameraBinding.locationLayout.setVisibility(View.VISIBLE);
        else activityCameraBinding.locationLayout.setVisibility(View.GONE);

        setData();

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        imageReader = ImageReader.newInstance(displayMetrics.widthPixels, displayMetrics.heightPixels, ImageFormat.JPEG, 10);
        imageReader.setOnImageAvailableListener(this, backgroundHandler);
        getFrontAndBackCameraId();
        activityCameraBinding.cameraPreview.getHolder().setFixedSize(width, height);
        activityCameraBinding.cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                openCamera();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                closeCamera();
            }
        });

        activityCameraBinding.close.setOnClickListener(view -> {
            isImageCaptured = false;
            finish();
        });

        activityCameraBinding.flash.setOnClickListener(view -> {
            isFlashEnabled = !isFlashEnabled;
            openCamera();
        });

        activityCameraBinding.capture.setOnClickListener(view -> {
            if(isImageCaptured) {
                isImageCaptured = false;
                openCamera();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activityCameraBinding.capture.setTooltipText("Capture");
                }
                activityCameraBinding.capturedImage.setImageBitmap(null);
                activityCameraBinding.capturedImage.setVisibility(View.GONE);
                activityCameraBinding.save.setVisibility(View.GONE);
                activityCameraBinding.cameraPreview.setVisibility(View.VISIBLE);
                activityCameraBinding.capture.setImageResource(R.drawable.ic_camera);
//                commonUtilsMethods.showToastMessage(this, "Now you can capture image...");
            }else {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activityCameraBinding.capture.setTooltipText("Retake");
                }
                captureImage();
            }
        });

        activityCameraBinding.save.setOnClickListener(view -> {
            activityCameraBinding.options.setVisibility(View.GONE);
            new Handler().postDelayed(() -> {
                captureImageWithLocation();
                isImageCaptured = false;
                setResult(RESULT_OK);
                finish();
            }, 1000);
        });

        activityCameraBinding.switchCamera.setOnClickListener(view -> {
            Log.d(TAG, "onCreate: camera switch : " + isBackCameraOpen);
            isBackCameraOpen = !isBackCameraOpen;
            openCamera();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, true);
            }
        }else {
            CommonUtilsMethods.RequestGPSPermission(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBackgroundThread();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }else {
                commonUtilsMethods.showToastMessage(this, "Camera permission needed!");
            }
        }
    }

    private void openCamera() {
        closeCamera();
        if(backCameraId != null && frontCameraId != null) {
            switch (cameraMode){
                case "BACK":
                    activityCameraBinding.switchCamera.setVisibility(View.GONE);
                    openCamera(backCameraId);
                    break;
                case "FRONT":
                    activityCameraBinding.switchCamera.setVisibility(View.GONE);
                    openCamera(frontCameraId);
                    break;
                case "ALL":
                    activityCameraBinding.switchCamera.setVisibility(View.VISIBLE);
                    if(isBackCameraOpen) {
                        openCamera(backCameraId);
                        break;
                    }
                    openCamera(frontCameraId);
                    break;
                default:
                    openCamera(backCameraId);
                    break;
            }
        }else if(backCameraId != null) {
            activityCameraBinding.switchCamera.setVisibility(View.GONE);
            openCamera(backCameraId);
        }else if(frontCameraId != null) {
            activityCameraBinding.switchCamera.setVisibility(View.GONE);
            openCamera(frontCameraId);
        }else {
            commonUtilsMethods.showToastMessage(this, "No Camera Detected");
            finish();
        }
    }

    private void setData(){
        if(currentLoc()) {
            latitude = gpsTrack.getLatitude();
            longitude = gpsTrack.getLongitude();
            address = CommonUtilsMethods.gettingAddress(this, latitude, longitude, false);
            activityCameraBinding.latitude.setText(String.valueOf(latitude));
            activityCameraBinding.longitude.setText(String.valueOf(longitude));
            activityCameraBinding.address.setText(address);
            date = CommonUtilsMethods.getCurrentInstance("dd-MMM-yyyy");
            time = CommonUtilsMethods.getCurrentInstance("hh:mm aa");
            activityCameraBinding.date.setText(date);
            activityCameraBinding.time.setText(time);
        }
    }

    private void captureImageWithLocation() {
        activityCameraBinding.getRoot().setDrawingCacheEnabled(true);
        activityCameraBinding.getRoot().setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        activityCameraBinding.getRoot().buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        bitmap = Bitmap.createBitmap(activityCameraBinding.getRoot().getDrawingCache());
        activityCameraBinding.getRoot().setDrawingCacheEnabled(false);
        activityCameraBinding.capturedImage.setImageBitmap(bitmap);
        activityCameraBinding.capturedImage.setVisibility(View.VISIBLE);
        activityCameraBinding.cameraPreview.setVisibility(View.GONE);
        activityCameraBinding.locationLayout.setVisibility(View.GONE);
        saveImage(bitmap);
    }

    private void saveImage(Bitmap bitmap) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            commonUtilsMethods.showToastMessage(this, "Image has been saved");
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        Log.d(TAG, "startBackgroundThread: ");
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        Log.d(TAG, "stopBackgroundThread: ");
        if(backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFrontAndBackCameraId() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if(lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    if(frontCameraId == null) frontCameraId = cameraId;
                }else if(lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    if(backCameraId == null) backCameraId = cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean isFlashSupported(CameraCharacteristics cameraCharacteristics) {
        int[] availableModes = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        if(availableModes != null) {
            for (int mode : availableModes) {
                if(mode == CameraCharacteristics.CONTROL_AE_MODE_ON_ALWAYS_FLASH)
                    return true;
            }
        }
        return Boolean.TRUE.equals(cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE));
    }

    private void openCamera(String cameraId) {
        try {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
                return;
            }
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    createCameraPreview(cameraId);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice device) {
                    cameraDevice.close();
                }

                @Override
                public void onError(@NonNull CameraDevice device, int i) {
                    cameraDevice.close();
                    cameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview(String cameraId) {
        try {
            Surface surface = activityCameraBinding.cameraPreview.getHolder().getSurface();
            final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

//            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
//            boolean isFlashAvailable = isFlashSupported(cameraCharacteristics);
//            if(isFlashAvailable) {
//                activityCameraBinding.flash.setVisibility(View.VISIBLE);
//                if(isFlashEnabled) {
//                    captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
//                    activityCameraBinding.flash.setImageResource(R.drawable.baseline_flash_off_24);
//                }else {
//                    captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
//                    activityCameraBinding.flash.setImageResource(R.drawable.baseline_flash_on_24);
//                }
//            }else {
//                activityCameraBinding.flash.setVisibility(View.GONE);
//            }
            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if(cameraDevice == null) return;
                    cameraCaptureSession = session;
                    try {
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Log.e(TAG, "Create Capture : onConfigureFailed");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureImage() {
        if(cameraDevice == null || imageReader == null) {
            Log.e(TAG, "captureImage: " + cameraDevice + " , " + imageReader);
            return;
        }
        try {
            CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            cameraCaptureSession.stopRepeating();
            cameraCaptureSession.capture(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if(cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if(cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    @Override
    public void onImageAvailable(ImageReader imageReader) {
        try (Image image = imageReader.acquireLatestImage()) {
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.capacity()];
            byteBuffer.get(bytes);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            final Bitmap bitmapImage = bitmap;
            runOnUiThread(() -> {
                setData();
                activityCameraBinding.capturedImage.setImageBitmap(bitmapImage);
                activityCameraBinding.capturedImage.setVisibility(View.VISIBLE);
                activityCameraBinding.cameraPreview.setVisibility(View.GONE);
                Log.d(TAG, "onImageAvailable: " + isImageCaptured);
                activityCameraBinding.switchCamera.setVisibility(View.GONE);
                activityCameraBinding.save.setVisibility(View.VISIBLE);
                activityCameraBinding.capture.setImageResource(R.drawable.baseline_autorenew_24);
                isImageCaptured = true;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean currentLoc() {
        boolean val = false;
        gpsTrack = new GPSTrack(this);
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new android.app.AlertDialog.Builder(this).setTitle("Alert")
                        .setCancelable(false).setMessage("Activate the Gps to proceed further")
                        .setPositiveButton("Yes", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
            }else {
                val = true;
            }
        } catch (Exception e) {
            commonUtilsMethods.showToastMessage(this, getString(R.string.loc_not_detect));
        }
        return val;
    }

}
