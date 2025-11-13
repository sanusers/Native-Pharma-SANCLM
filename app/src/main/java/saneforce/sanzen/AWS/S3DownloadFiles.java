package saneforce.sanzen.AWS;

import android.graphics.Bitmap;

public interface S3DownloadFiles {
    void fileDataAdd(int pos, Bitmap bitmap);
    void onFailure(int pos);
}
