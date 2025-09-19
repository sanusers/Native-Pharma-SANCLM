/*
 * Copyright 2015-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package saneforce.sanzen.AWS;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Shader;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//import saneforce.sanzen.commonClasses.Keys;
import saneforce.sanzen.commonClasses.SimpleDecrypt;
import saneforce.sanzen.storage.SharedPref;

/**
 * Handles basic helper functions used throughout the app.
 */
public class Util {
    private static final String TAG = Util.class.getSimpleName();

    private AmazonS3 sS3Client;
    private AWSCredentialsProvider sMobileClient;
    private TransferUtility transferUtility;

    /**
     * Gets an instance of AWSMobileClient which is
     * constructed using the given Context.
     *
     * @param context Android context
     * @return AWSMobileClient which is a credentials provider
     */
  /*  private AWSCredentialsProvider getCredProvider(Context context) {
        if (sMobileClient == null) {
            final CountDownLatch latch = new CountDownLatch(1);
            AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails result) {
                    latch.countDown();
                }

                @Override
                public void onError(Exception e) {
                        Log.d(TAG, "onError: "+e.getMessage());
                    latch.countDown();
                }
            });
            try {
                latch.await();
                sMobileClient = AWSMobileClient.getInstance();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sMobileClient;
    }*/
    private final String REGION = "eu-north-1";

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context Android context
     * @return A default S3 client.
     */
    public AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
       /*     try {
                String regionString = new AWSConfiguration(context)
                        .optJsonObject("S3TransferUtility")
                        .getString("Region");
                Region region = Region.getRegion(regionString);
                sS3Client = new AmazonS3Client(getCredProvider(context), region);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            String accessKey = "", secretKey = "";
            try {
                String[] keys = SharedPref.getKeys(context).split("\\^\\^");
                String aKey = keys[0], sKey = keys[1];
                accessKey = SimpleDecrypt.decrypt(aKey, "zenaccess_san");
                secretKey = SimpleDecrypt.decrypt(sKey, "zensecret_san");
            } catch (Exception e) {
                e.printStackTrace();
            }
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            sS3Client =  new AmazonS3Client(credentials);
            sS3Client.setRegion(Region.getRegion(Regions.fromName(REGION)));
        }
        return (AmazonS3Client) sS3Client;
    }

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context Android context
     * @return a TransferUtility instance
     */
    public TransferUtility getTransferUtility(Context context) {
        if (transferUtility == null) {
            transferUtility = TransferUtility.builder()
                    .context(context)
                    .s3Client(getS3Client(context))
                    .build();
        }

        return transferUtility;
    }



    /**
     * Copies the data from the passed in Uri, to a new file for use with the
     * Transfer Service
     *
     * @param context Android context
     * @param uri Content URI
     * @return New file copied from given URI
     * @throws IOException if data read/write fails
     */
    public File copyContentUriToFile(Context context, Uri uri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        File copiedData = new File(context.getDir("SampleImagesDir", Context.MODE_PRIVATE),
                UUID.randomUUID().toString());
        copiedData.createNewFile();

        FileOutputStream fos = new FileOutputStream(copiedData);
        byte[] buf = new byte[2046];
        int read = -1;
        while ((read = is.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }

        fos.flush();
        fos.close();

        return copiedData;
    }

    public String getFileExtension(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void createFile(Context context, Uri srcUri, File dstFile) {
        try {
            if (dstFile.exists ()) dstFile.delete ();
            dstFile.createNewFile();
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
