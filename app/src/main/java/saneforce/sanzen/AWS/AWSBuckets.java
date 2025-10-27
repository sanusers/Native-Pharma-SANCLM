package saneforce.sanzen.AWS;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.storage.SharedPref;

public class AWSBuckets {
    private static final String TAG = "Upload Task";
    TransferUtility transferUtility;
    Util util;
    Context context;
    String filename, filestored_name;
    File file;
    int pos;
    S3DownloadFiles S3DownloadFiles;
    CommonUtilsMethods commonUtilsMethods;



    public AWSBuckets(Context context, String Filename, File File, String filestored_name) {  // for upload
        this.context = context;
        this.filename = Filename;
        this.file = File;
        this.filestored_name = filestored_name;
        util = new Util();
        transferUtility = util.getTransferUtility(context);
        commonUtilsMethods =  new CommonUtilsMethods(context);
        new AWSbucketsclass().execute();
    }

    // download
    public AWSBuckets(Context context, String Filename, File File, int filepos, String filestored_name,S3DownloadFiles s3Download_Files) {  // for download/ retrival
        this.context = context;
        this.filename = Filename;
        this.file = File;
        this.pos = filepos;
        this.filestored_name = filestored_name;
        this.S3DownloadFiles = s3Download_Files;
        util = new Util();
        transferUtility = util.getTransferUtility(context);
        commonUtilsMethods = new CommonUtilsMethods(context);
        new AWSbucketsDownload().execute();
    }


    private class AWSbucketsDownload extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                TransferObserver downloadObserver = transferUtility.download("san-one","uploads/"+SharedPref.getDivisionSname(context)+SharedPref.getDivisionCode(context).replace(",","/")+"Event_Capture"+"/"+ filestored_name+filename, file);
                downloadObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED == state) {
                            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                            System.out.println("CHk_Data-->>" + bmp);
                            S3DownloadFiles.fileDataAdd(pos, bmp);
                        } else if (TransferState.FAILED == state) {
                            S3DownloadFiles.onFailure(pos);
                            Log.d("S3 Transfer" , "onStateChanged: "+"S3 Transfer state FAILED");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        ex.printStackTrace();
                    }
                });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Download Error Exception " + e.getMessage());
                return false;
            }
        }
    }

    private class AWSbucketsclass extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                TransferObserver image_upload = transferUtility.upload("san-one","uploads/"+SharedPref.getDivisionSname(context)+SharedPref.getDivisionCode(context).replace(",","/")+"Event_Capture"+"/"+ filestored_name+filename, file);
                if (image_upload == null) {
                    Log.e("AWSUpload", "TransferObserver is null - upload() may have failed silently.");
                    return false;
                }
                image_upload.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
//                        if (TransferState.COMPLETED == state) {
//                            commonUtilsMethods.showToastMessage(context,"Upload Successful!");
//
//
//
//                        } else if (TransferState.FAILED == state) {
//                            commonUtilsMethods.showToastMessage(context,"Upload Failed");
//                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
//                        commonUtilsMethods.showToastMessage(context,"Error");
                        ex.printStackTrace();
                    }
                });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Download Error Exception " + e.getMessage());
                return false;
            }
        }

    }
}


