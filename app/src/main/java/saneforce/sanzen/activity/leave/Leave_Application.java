package saneforce.sanzen.activity.leave;

import static saneforce.sanzen.commonClasses.UtilityClass.hideKeyboard;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.ViewModel.LeaveViewModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityLeaveApplicationBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class Leave_Application extends AppCompatActivity {
    public static ArrayList<Leave_modelclass> List_LeaveDates = new ArrayList<>();
    public static ArrayList<Leave_modelclass> Chart_list = new ArrayList<>();
    public static ArrayList<String> ltypecount = new ArrayList<>();
    public static ListView dailog_list;
    public static ActivityLeaveApplicationBinding leavebinding;
    TextView headtext_id;
    EditText et_Custsearch;
    ApiInterface apiInterface;
    ImageView close_sideview;
    String navigateFrom = "";
    ArrayAdapter<String> adapter;
    ArrayList<String> leave_type = new ArrayList<>();
    ArrayList<String> leave_typeid = new ArrayList<>();
    ArrayList<String> leave_typename = new ArrayList<>();
    ArrayList<String> listdate = new ArrayList<>();
    String Ltype_id, L_typename, L_count = "", Lshortname, avilable, leavety;
    int totalval = 0, val = 0;
    String l_address = "", l_reason = "", imageName = "", leaveAvailable;
    CommonUtilsMethods commonUtilsMethods;
    boolean isLeaveEntitlementRequested;
    private RoomDB roomDB;
    private static MasterDataDao masterDataDao;
    private String attachmentFilePath;
    LeaveViewModel leaveViewModel;
    Uri uri;
    int StorageFlag = 0;

    //To Hide the bottomNavigation When popup
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            leavebinding.chartLayout.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leavebinding = ActivityLeaveApplicationBinding.inflate(getLayoutInflater());
        setContentView(leavebinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        isLeaveEntitlementRequested = SharedPref.getLeaveEntitlementNeed(this).equals("0");
        dailog_list = findViewById(R.id.cutumdailog_list);
        close_sideview = findViewById(R.id.close_sideview);
        headtext_id = findViewById(R.id.headtext_id);
        et_Custsearch = findViewById(R.id.et_Custsearch);
        dailog_list.setVisibility(View.VISIBLE);
        leaveViewModel = new LeaveViewModel(this);
        if (isLeaveEntitlementRequested) {
            leaveViewModel.updateLeaveStatusMasterSync();
        }
        //setVisibility();
        setMaxLength();
        onClickListener();

        leavebinding.edReason.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(leavebinding.edReason, 300)});
        leavebinding.edAddress.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(leavebinding.edAddress, 300)});

//        l_sideview.closeDrawer(Gravity.RIGHT);
        leavebinding.leavebackArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                //  onBackPressed();
                getOnBackPressedDispatcher().onBackPressed();
          /*  Intent l = new Intent(Leave_Application.this, HomeDashBoard.class);
            startActivity(l);*/
            }
        });

        String colorText = "<font color=\"#85929e\">" + "Leave Date From" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";
        String colorText1 = "<font color=\"#85929e\">" + "Leave Date To" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";
        String colorText2 = "<font color=\"#85929e\">" + "Leave Type" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";

        leavebinding.HeadFromdate.setText(Html.fromHtml(colorText));
        leavebinding.HeadTodate.setText(Html.fromHtml(colorText1));
        leavebinding.HeadLtype.setText(Html.fromHtml(colorText2));

        close_sideview.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                leavebinding.leaveSide.closeDrawer(GravityCompat.END);
                hideKeyboard(Leave_Application.this);
            }
        });
        leave_applydates();


        leavebinding.LeaveType.setText("Leave Type");
        leavebinding.etFromDate.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Intent tp = new Intent(Leave_Application.this, CalendarActivity.class);
                tp.putExtra("selectefromdDate", "1");
                startActivity(tp);
            }
        });


        leavebinding.etToDate.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (!leavebinding.etFromDate.getText().toString().equals("")) {
                    Intent tp = new Intent(Leave_Application.this, CalendarActivity.class);
                    tp.putExtra("selectefromdDate", "2");
                    startActivity(tp);
                } else {
                    commonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_from_date));
                }
            }
        });

        leavebinding.LeaveType.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (leavebinding.etFromDate.getText().toString().equals("")) {
                    commonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_from_date));
                } else if (leavebinding.etToDate.getText().toString().equals("")) {
                    commonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_to_date));
                } else {
                    if (UtilityClass.isNetworkAvailable(Leave_Application.this)) {
                        showalert_leavetype();
                    } else {
                        commonUtilsMethods.showToastMessage(Leave_Application.this, "Please Check Your Internet Connection");
                    }
                }
                closeKeyboard();
            }
        });

        leavebinding.tlAttachment.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (leavebinding.etFromDate.getText().toString().equals("")) {
                    commonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_from_date));
                } else if (leavebinding.etToDate.getText().toString().equals("")) {
                    commonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_to_date));
                } else {
//                    if (!CheckStoragePermission()) {
//                        RequestStoragePermission();
//                    } else {
                    Open_Storage();
//                    }
                }
                closeKeyboard();
            }
        });

        leavebinding.submitLeave.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (leavebinding.etFromDate.getText().toString().equals("")) {
                    CommonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_from_date));
                } else if (leavebinding.etToDate.getText().toString().equals("")) {
                    CommonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_to_date));
                } else if (leavebinding.LeaveType.getText().toString().equals("")) {
                    CommonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.select_leave_type));
                } else if (leavebinding.edReason.getText().toString().isEmpty() || leavebinding.edReason.getText().toString().equalsIgnoreCase("")) {
                    CommonUtilsMethods.showToastMessage(Leave_Application.this, getString(R.string.enter_reason_for_leave));
                } else if (leavebinding.tlAttachment.getVisibility() == View.VISIBLE && leavebinding.txtAttachement.getText().toString().isEmpty()) {
                    CommonUtilsMethods.showToastMessage(Leave_Application.this, "Select Attachment");
                } else {
                    if (UtilityClass.isNetworkAvailable(Leave_Application.this)) {
                        if (leavebinding.tlAttachment.getVisibility() == View.VISIBLE && !leavebinding.txtAttachement.getText().toString().isEmpty()) {
                            JSONObject jsonImage = CommonUtilsMethods.CommonObjectParameter(Leave_Application.this);
                            try {
                                jsonImage.put("tableName", "uploadphoto");
                                jsonImage.put("sfcode", SharedPref.getSfCode(Leave_Application.this));
                                jsonImage.put("division_code", SharedPref.getDivisionCode(Leave_Application.this));
                                File file = null;
                                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                                    file = new File(Leave_Application.this.getExternalFilesDir(null) + "/LeaveAttachment/");
                                } else {
                                    Log.e("File Creation", "captureFile: No media mounted");
                                }
                                if (file != null && !file.exists()) {
                                    if (!file.mkdirs()) {
                                        Log.e("File Creation", "Directory Creation Failed.");
                                    }
                                }
                                File destinationFile = new File(file, leavebinding.txtAttachement.getText().toString());
                                try {
                                    if (!destinationFile.createNewFile()) {
                                        Log.e("File Creation", "Destination File Creation Failed.");
                                    }
                                    attachmentFilePath = destinationFile.getAbsolutePath();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            saveAttachment(attachmentFilePath, jsonImage.toString());
                        } else {
                            Submit();
                        }
                    }
                }
            }
        });

        AvailableLeave(Leave_Application.this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean DontAskAgain = false;

        if (requestCode == 101) {
            for (String allowedPermissions : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Leave_Application.this, allowedPermissions)) {
                    StorageFlag++;
                } else if (PermissionChecker.checkCallingOrSelfPermission(Leave_Application.this, allowedPermissions) != PermissionChecker.PERMISSION_GRANTED) {
                    DontAskAgain = true;
                    StorageFlag++;
                    break;
                } else {
                    Open_Storage();
                }
            }
            if ((DontAskAgain) && (StorageFlag > 1)) {
                CommonUtilsMethods.RequestGPSPermission(Leave_Application.this, "Files");
            }
        }
    }

    @SuppressLint({"MissingSuperCall", "Range"})
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 7) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                try {
                    uri = data.getData();
                    try {
                        final int takeFlags = data.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String filename = getFileNameFromUri(uri);
                    if (filename == null) {
                        commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.please_select_correct_path));
                        return;
                    }
                    if (filename.endsWith(".zip")) {
                        commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.zip_not_supported));
                        return;
                    }
                    leavebinding.txtAttachement.setText(filename);
                    commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.file_accepted));
                    copyFileToAppDir(uri);
//                    String fullPath = getPathFromURI(Leave_Application.this, uri);
//                    String[] parts = fullPath.split("/");
//                    String filenmae = parts[parts.length - 1];
//
//                    if (filenmae.endsWith(".zip")) {
//                        commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.zip_not_supported));
//                    } else {
//                        FilnameTet.setText(String.valueOf(filenmae));
//                        commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.file_accepted));
////                        File dir1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), "SAN_Images");
////                        if(!dir1.exists()) {
////                            dir1.mkdirs();
////                        }
//                        File file = null;
//                        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//                            file = new File(getApplicationContext().getExternalFilesDir(null) + "/ActivityUpload/");
//                        } else {
//                            Log.e("File Creation", "captureFile: No media mounted");
//                        }
//                        if (file != null && !file.exists()) {
//                            if (!file.mkdirs()) {
//                                Log.e("File Creation", "Directory Creation Failed.");
//                            }
//                        }
//                        copyFileOrDirectory(String.valueOf(fullPath), String.valueOf(file));
//                    }
                } catch (Exception ex) {
                    Log.v("Error", ex.toString());
                    commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.please_select_correct_path));
                    ex.printStackTrace();
                }
            } else {
                commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.no_file_selected));
            }
            commonFun();
        }
    }

    private void copyFileToAppDir(Uri sourceUri) {
        try {
            File destDir = new File(getApplicationContext().getExternalFilesDir(null), "ActivityUpload");
            if (!destDir.exists()) destDir.mkdirs();
            String fileName = getFileNameFromUri(sourceUri);
            if (fileName == null) fileName = "file_" + System.currentTimeMillis();
            File destFile = new File(destDir, fileName);
            try (InputStream inputStream = getContentResolver().openInputStream(sourceUri);
                 OutputStream outputStream = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            Log.d("FileCopy", "Saved to: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        try {
            if ("content".equals(uri.getScheme())) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index != -1) {
                            result = cursor.getString(index);
                        }
                    }
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getPathFromURI(final Context Context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(Context, uri)) {
            if (isExternalStorageDocument(uri)) {
                Log.v("bv1", "------" + uri);
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                Log.v("bv2", "------" + uri);
                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                    return getDataColumn(Context, contentUri, null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Log.v("bv3", "------" + uri);
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(Context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.v("bv4", "------" + uri);
            return getDataColumn(Context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.v("bv5", "------" + uri);
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context Context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = Context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void commonFun() {
        Leave_Application.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

//    public boolean CheckStoragePermission() {
//        if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
//            int image = ContextCompat.checkSelfPermission(Leave_Application.this, READ_MEDIA_IMAGES);
//            int video = ContextCompat.checkSelfPermission(Leave_Application.this, READ_MEDIA_VIDEO);
//            int audio = ContextCompat.checkSelfPermission(Leave_Application.this, READ_MEDIA_AUDIO);
//            return image == PackageManager.PERMISSION_GRANTED && video == PackageManager.PERMISSION_GRANTED && audio == PackageManager.PERMISSION_GRANTED;
//        }else {
//            int Write = ContextCompat.checkSelfPermission(Leave_Application.this, WRITE_EXTERNAL_STORAGE);
//            int Read = ContextCompat.checkSelfPermission(Leave_Application.this, READ_EXTERNAL_STORAGE);
//            return Write == PackageManager.PERMISSION_GRANTED && Read == PackageManager.PERMISSION_GRANTED;
//        }
//    }

//    private void RequestStoragePermission() {
//        if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
//            if((ActivityCompat.checkSelfPermission(Leave_Application.this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) || ActivityCompat.checkSelfPermission(Leave_Application.this, READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Leave_Application.this, READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(Leave_Application.this, new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO}, 101);
//            }
//        }else {
//            if(ActivityCompat.checkSelfPermission(Leave_Application.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Leave_Application.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(Leave_Application.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 101);
//            }
//        }
//    }

    private void Open_Storage() {
        Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, 7);
    }

    public static void copyFileOrDirectory(String srcDir, String dstDir) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());
            Log.d("string", src.getName());
            if (src.isDirectory()) {
                String[] files = src.list();
                if (files != null) {
                    for (String file : files) {
                        String src1 = (new File(src, file).getPath());
                        String dst1 = dst.getPath();
                        copyFileOrDirectory(src1, dst1);
                    }
                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                FileUtils.copy(in,out);
//            }
            destination.transferFrom(source, 0, source.size());
            // destination.write(source, 0, source.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFile(String fileName) {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(this.getExternalFilesDir(null) + "/LeaveAttachment/" + fileName);
        } else {
            Log.e("File Deletion", "captureFile: No media mounted");
        }
        if (file != null && !file.exists()) {
            Log.w("File Deletion", "No File Found" + file.getAbsolutePath());
        } else if (file != null && file.exists()) {
            if (file.delete()) {
                Log.d("FileDeleter", "File deleted: " + file.getAbsolutePath());
            } else {
                Log.e("FileDeleter", "File not deleted: " + file.getAbsolutePath());
            }
        }
    }

    public void leave_applydates() {
        ltypecount.clear();
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE).getMasterSyncDataJsonArray();
            String days = "";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dates = jsonObject.getString("Created_Date");
                    JSONObject jsonval = new JSONObject(dates);
                    if (days.equals("")) {
                        days = (jsonval.getString("date"));
                        String dval = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_22, TimeUtils.FORMAT_4, days);
                        ltypecount.add(dval);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("WrongConstant")
    public void showalert_leavetype() {
        et_Custsearch.getText().clear();
        leavebinding.leaveSide.setVisibility(View.VISIBLE);
        leavebinding.leaveSide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        leavebinding.leaveSide.openDrawer(Gravity.END);

        leave_type.clear();
        leave_typeid.clear();
        leave_typename.clear();

        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE).getMasterSyncDataJsonArray();
            Log.d("L-type", String.valueOf(jsonArray));
            String days = "";
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String Leavename = (jsonObject.getString("Leave_Name"));//Leave_Name
                    String L_code = (jsonObject.getString("Leave_code"));
                    String L_Sname = (jsonObject.getString("Leave_SName"));


                    String dates = jsonObject.getString("Created_Date");

                    JSONObject jsonval = new JSONObject(dates);
                    if (days.equals("")) {
                        days = (jsonval.getString("date"));

                        String dval = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_22, TimeUtils.FORMAT_4, days);

                        ltypecount.add(dval);
                    }
                    leave_type.add(L_Sname);
                    leave_typeid.add(L_code);
                    leave_typename.add(Leavename);
                }
            }

            adapter = new ArrayAdapter<>(Leave_Application.this, R.layout.cutoum_layout, (List) leave_typename);
            dailog_list.setAdapter(adapter);

            et_Custsearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            dailog_list.setOnItemClickListener((arg0, arg1, position, arg3) -> {
                // TODO Auto-generated method stub
                avilable = "";
                leavety = "";
                if (UtilityClass.isNetworkAvailable(this)) {
                    String selectedFromList = dailog_list.getItemAtPosition(position).toString();
                    for (int i = 0; i < leave_typename.size(); i++) {
                        if (selectedFromList.equals(leave_typename.get(i))) {
                            leavebinding.LeaveType.setText(selectedFromList);
                            Ltype_id = leave_typeid.get(i);
                            L_typename = leave_typename.get(i);
                            Lshortname = leave_type.get(i);
                            try {
                                JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray();
                                for (int d = 0; d < jsonArray1.length(); d++) {
                                    JSONObject jsonobj1 = jsonArray1.getJSONObject(d);
                                    if (Ltype_id.equals(jsonobj1.getString("Leave_code"))) {
                                        avilable = (jsonobj1.getString("Avail"));
                                        leavety = (jsonobj1.getString("Leave_Type_Code"));
                                        break;
                                    }
                                }
                                Log.e("dates12", String.valueOf(ltypecount));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            if(avilable == null || avilable.isEmpty()) {
//                                listdate.clear();
//                                Leave_Application.leavebinding.lDays.setText("");
//                                commonUtilsMethods.showToastMessage(this, "No Leave Available!");
//                                leavebinding.submitLeave.setEnabled(false);
//                                leavebinding.etFromDate.setText("");
//                                leavebinding.etToDate.setText("");
//                                leavebinding.LeaveType.setText("");
//                                leavebinding.balanceDays.setText("");
//                                List_LeaveDates.clear();
//                                List_LeaveDates = new ArrayList<>();
//
//                                Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
//                                LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
//                                Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
//                                Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
//                                Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
//                                l_details.notifyDataSetChanged();
//                            } else {
                            leave_avalabledetails();
//                            }

                        }
                    }
                    leavebinding.leaveSide.closeDrawer(Gravity.END);
                } else {
                    commonUtilsMethods.showToastMessage(this, getString(R.string.please_check_your_internet_connection));
                }
                closeKeyboard();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leave_avalabledetails() {
        try {
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                navigateFrom = getIntent().getExtras().getString("Origin");
            }


            String f_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, leavebinding.etFromDate.getText().toString()));
            String t_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, leavebinding.etToDate.getText().toString()));
            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
            jsonObject.put("tableName", "getlvlvalid");
            jsonObject.put("sfcode", SharedPref.getSfCode(this));
            jsonObject.put("Fdt", f_date);
            jsonObject.put("Tdt", t_date);
            jsonObject.put("LTy", Lshortname);
            jsonObject.put("Leave_type_code", Ltype_id);
            jsonObject.put("division_code", SharedPref.getDivisionCode(this));
            jsonObject.put("Rsf", SharedPref.getHqCode(this));
            Log.d("JSonobj", String.valueOf(jsonObject));

            Map<String, String> qry = new HashMap<>();
            qry.put("axn", "get/leave");
            Call<JsonElement> call = null;
            call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(getApplicationContext()), qry, jsonObject.toString());
            if (call != null) {
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                        if (response.isSuccessful()) {
                            Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                            try {
                                JSONArray jsonArray = new JSONArray(response.body().toString());
                                Log.d("responce1", String.valueOf(jsonArray));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String Leavename = (jsonObject.getString("Flg"));//Leave_Name
                                    String msg = (jsonObject.getString("Msg"));
//                                    System.out.println("leaveMessage--->" + msg);
                                    if ((jsonObject.getString("Msg").equals(""))) {
                                        Leavedetails(jsonObject);
                                    } else {
                                        List_LeaveDates.clear();
                                        leavebinding.etFromDate.setText("");
                                        leavebinding.etToDate.setText("");
                                        leavebinding.LeaveType.setText("");
                                        leavebinding.balanceDays.setText("");
                                        leavebinding.lDays.setText("");
                                        List_LeaveDates = new ArrayList<>();

                                        Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
                                        LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
                                        Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
                                        Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
                                        Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
                                        l_details.notifyDataSetChanged();

                                        commonUtilsMethods.showToastMessage(Leave_Application.this, msg);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        List_LeaveDates.clear();
                        leavebinding.etFromDate.setText("");
                        leavebinding.etToDate.setText("");
                        leavebinding.LeaveType.setText("");
                        leavebinding.balanceDays.setText("");
                        List_LeaveDates.clear();

                        Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
                        LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
                        Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
                        Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
                        Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
                        l_details.notifyDataSetChanged();

                        commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.poor_connection));
                    }

                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void Leavedetails(JSONObject data) {
        listdate.clear();
        List_LeaveDates.clear();
        DateFormat mFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        try {
            String F_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_19, leavebinding.etFromDate.getText().toString()));
            String T_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_19, leavebinding.etToDate.getText().toString()));

            Date fromDate = mFormat.parse(F_date);
            Date toDate = mFormat.parse(T_date);
            List<Date> datesInRange = getDatesInRange(fromDate, toDate);
            for (Date date : datesInRange) {
                listdate.add((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_20, TimeUtils.FORMAT_12, String.valueOf(date))));
            }

            Leave_Application.leavebinding.lDays.setText(listdate.size() + " days " + L_typename);
            L_count = String.valueOf(listdate.size());
//            if (isLeaveEntitlementRequested) {
//                totalval = Integer.parseInt(avilable);
//                val = Integer.parseInt(L_count);
//                Log.d("rem", totalval + "---" + val);
//                int bal = totalval - val;
//
//                if (bal < 0) {
//                    commonUtilsMethods.showToastMessage(this, "Kindly Sync Leave Available!");
//                    leavebinding.submitLeave.setEnabled(false);
//                } else {
////                    if(bal == 0) {
////
////                }else {
////                    if(leavety.equals("LOP")) {
////                        leavebinding.balanceDays.setText("");
////                    }else {
//                    String balval = String.valueOf(bal);
//                    leavebinding.balanceDays.setText(balval + " " + "days remaining");
////                    }
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
            listdate.clear();
            List_LeaveDates.clear();
            leavebinding.balanceDays.setText("");
            Leave_Application.leavebinding.lDays.setText("");
            commonUtilsMethods.showToastMessage(this, "No Leave Available!");
            leavebinding.submitLeave.setEnabled(false);
            return;
        }


        try {
            JSONArray jsonarr = new JSONArray();
            for (int s = 0; s < listdate.size(); s++) {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("Date", listdate.get(s));//cip_head
                jsonarr.put(jsonobj);
            }
            if (jsonarr.length() > 0) {
                for (int j = 0; j < jsonarr.length(); j++) {
                    JSONObject jsonObject = jsonarr.getJSONObject(j);
                    String listed_date = jsonObject.getString("Date");
                    Leave_modelclass model = new Leave_modelclass(listed_date, "5");//,ref_source_id
                    List_LeaveDates.add(model);
                }
                Leavedetails_adapter l_details = new Leavedetails_adapter(this, List_LeaveDates);
                LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
                Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
                Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
                Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
                l_details.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String attachmentStr = data.optString("attchment");
            int attachment = attachmentStr.isEmpty() ? 0 : Integer.parseInt(attachmentStr);
            if (attachment == 0) {
                leavebinding.tlAttachment.setVisibility(View.VISIBLE);
            } else {
                leavebinding.tlAttachment.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Date> getDatesInRange(Date fromDate, Date toDate) {
        List<Date> dateList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        while (calendar.getTime().before(toDate) || calendar.getTime().equals(toDate)) {
            dateList.add(calendar.getTime());

            // Increment the date by one day
            calendar.add(Calendar.DATE, 1);
        }

        return dateList;
    }

    public static void AvailableLeave(Context context) {
        try {
            Chart_list.clear();
            leavebinding.submitLeave.setEnabled(true);
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray();
            JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE).getMasterSyncDataJsonArray();
            String lstatus = (jsonArray.get(0).toString());
            if (lstatus.equals(Constants.NO_DATA_AVAILABLE)) {
                leavebinding.chartLayout.setVisibility(View.GONE);
                leavebinding.mtcard.setVisibility(View.VISIBLE);
            } else {
//                Log.d("Leave_data", jsonArray + "--" + jsonArray1);
                HashMap<String, LeaveStatusModelClass> leaveStatusMap = new HashMap<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    LeaveStatusModelClass leaveStatusModelClass = new LeaveStatusModelClass(jsonObject.optString("Leave_Type_Code"), jsonObject.optString("Elig"), jsonObject.optString("Taken"), jsonObject.optString("Avail"), jsonObject.optString("Leave_code"));
                    leaveStatusMap.put(leaveStatusModelClass.getLeaveCode(), leaveStatusModelClass);
                }

                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject jsonObject = jsonArray1.optJSONObject(i);
                    if (leaveStatusMap.containsKey(jsonObject.optString("Leave_code"))) {
                        LeaveStatusModelClass leaveStatusModelClass = leaveStatusMap.get(jsonObject.optString("Leave_code"));
                        if (leaveStatusModelClass != null) {
                            Leave_modelclass leave = new Leave_modelclass(jsonObject.optString("Leave_Name"), leaveStatusModelClass.getEligible(), leaveStatusModelClass.getTaken(), leaveStatusModelClass.getAvailable(), leaveStatusModelClass.getLeaveTypeCode());
                            Chart_list.add(leave);
                        }
                    } else {
                        Leave_modelclass leave = new Leave_modelclass(jsonObject.optString("Leave_Name"), "0", "0", "0", jsonObject.optString("Leave_SName"));
                        Chart_list.add(leave);
                    }
                }

                Piechart_adapter chart_details = new Piechart_adapter(context, Chart_list);
                leavebinding.RLPiechart.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                leavebinding.RLPiechart.setItemAnimator(new DefaultItemAnimator());
                leavebinding.RLPiechart.setAdapter(chart_details);
                chart_details.notifyDataSetChanged();

//                for (int i = 0; i<jsonArray.length(); i++) {
//                    for (int i1 = 0; i1<jsonArray1.length(); i1++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i1);
//                        String Ltype = (jsonObject1.getString("Leave_Name"));
//                        if(jsonObject.getString("Leave_code").equals(jsonObject1.getString("Leave_code"))) {
//                            Leave_modelclass tackleave = new Leave_modelclass(jsonObject1.getString("Leave_Name"), (jsonObject.getString("Elig")),
//                                                                              (jsonObject.getString("Taken")), (jsonObject.getString("Avail")),
//                                                                              jsonObject.getString("Leave_Type_Code"));
//
////                            leave_modelclass tackleave=new leave_modelclass(jsonObject1.getString("Leave_Name"),"6","5","10");
//                            Chart_list.add(tackleave);
//
//
////                        Collections.reverse(Chart_list);
//                            Piechart_adapter chart_details = new Piechart_adapter(context, Chart_list);
//                            leavebinding.RLPiechart.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                            leavebinding.RLPiechart.setItemAnimator(new DefaultItemAnimator());
//                            leavebinding.RLPiechart.setAdapter(chart_details);
//                            chart_details.notifyDataSetChanged();
//
//                        }
//                    }
//                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(txt, MultipartBody.FORM);
    }

    public MultipartBody.Part convertImg(String tag, String path) {
        Log.d("path", tag + "-" + path);
        MultipartBody.Part yy = null;
        try {
            File file;
            if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(this).compressToFile(new File(path));
                Log.d("path", tag + "-" + path);
            } else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

            Log.d("path", String.valueOf(yy));
        } catch (Exception ignored) {
        }
        return yy;
    }

    private void saveAttachment(String filePath, String jsonValues) {
        try {
            ApiInterface apiInterface = RetrofitClient.getRetrofit(this, SharedPref.getTagApiImageUrl(this));
            MultipartBody.Part img = convertImg("EventImg", filePath);
            HashMap<String, RequestBody> values = field(jsonValues);
            Call<JsonObject> saveImgDcr = apiInterface.SaveImg(values, img);
            saveImgDcr.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            Submit();
                        } catch (Exception e) {
                            Log.v("SendOutboxCall", "-error---" + e);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.poor_connection));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Submit() {
        if (isNetworkConnected()) {
            String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
            String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
            String replacedUrl = pathUrl.replaceAll("\\?.*", "/");
            Log.e("test", "login url : " + baseUrl + replacedUrl);
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl + replacedUrl);
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                navigateFrom = getIntent().getExtras().getString("Origin");
            }

            String f_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, leavebinding.etFromDate.getText().toString()));
            String t_date = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_21, leavebinding.etToDate.getText().toString()));

            l_address = leavebinding.edAddress.getText().toString();
            l_reason = leavebinding.edReason.getText().toString();

            try {
                JSONObject jsonobj = CommonUtilsMethods.CommonObjectParameter(this);
                jsonobj.put("tableName", "saveleave");
                jsonobj.put("sfcode", SharedPref.getSfCode(this));
                jsonobj.put("FDate", f_date);
                jsonobj.put("TDate", t_date);
                jsonobj.put("LeaveType", Lshortname);
                jsonobj.put("NOD", L_count);
                jsonobj.put("LvOnAdd", l_address);
                jsonobj.put("LvRem", l_reason);
                jsonobj.put("division_code", SharedPref.getDivisionCode(this));
                jsonobj.put("Rsf", SharedPref.getHqCode(this));
                jsonobj.put("leave_typ_code", Ltype_id);

                Log.d("save_obj", String.valueOf(jsonobj));
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "save/leavemodule");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonobj.toString());

                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                Log.e("test", "response : " + " : " + Objects.requireNonNull(response.body()).toString());
                                try {
                                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
                                    JSONArray wtJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
                                    String FWIndicator = "", WTName = "";
                                    for (int i = 0; i < wtJsonArray.length(); i++) {
                                        JSONObject jsonObject = wtJsonArray.optJSONObject(i);
                                        if (jsonObject.optString("FWFlg").equalsIgnoreCase("L")) {
                                            FWIndicator = jsonObject.optString("FWFlg");
                                            WTName = jsonObject.optString("Name");
                                            break;
                                        }
                                    }

                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeUtils.FORMAT_21);
                                    LocalDate fromDate = LocalDate.parse(f_date, formatter);
                                    LocalDate toDate = LocalDate.parse(t_date, formatter);

                                    for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("CustCode", "");
                                        jsonObject.put("CustType", "0");
                                        jsonObject.put("Dcr_dt", date.toString());
                                        jsonObject.put("month_name", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_9, date.toString()));
                                        jsonObject.put("Mnth", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_31, date.toString()));
                                        jsonObject.put("Yr", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_10, date.toString()));
                                        jsonObject.put("vtm", CommonUtilsMethods.getCurrentInstance("hh:mm aa"));
                                        jsonObject.put("CustName", "");
                                        jsonObject.put("town_code", "");
                                        jsonObject.put("FW_Indicator", FWIndicator);
                                        jsonObject.put("WorkType_Name", WTName);
                                        jsonObject.put("town_name", "");
                                        jsonObject.put("Dcr_flag", "0");
                                        jsonObject.put("SF_Code", "");
                                        jsonObject.put("Trans_SlNo", "");
                                        jsonObject.put("AMSLNo", "");
                                        jsonObject.put("day_status", "1");
                                        jsonArray.put(jsonObject);
                                    }
                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, jsonArray.toString(), 2));

                                    JSONArray leaveArray = masterDataDao.getMasterDataTableOrNew(Constants.LEAVE_STATUS).getMasterSyncDataJsonArray();

                                    for (int i = 0; i < leaveArray.length(); i++) {
                                        JSONObject obj = leaveArray.getJSONObject(i);

                                        if (obj.optString("Leave_Type_Code").equalsIgnoreCase(Lshortname)) {

                                            int taken = obj.optInt("Taken", 0);
                                            int avail = obj.optInt("Avail", 0);
                                            int days = Integer.parseInt(L_count);

                                            obj.put("Taken", taken + days);
                                            obj.put("Avail", Math.max(avail - days, 0));
                                        }
                                    }

                                    masterDataDao.saveMasterSyncData(
                                            new MasterDataTable(Constants.LEAVE_STATUS, leaveArray.toString(), 2)
                                    );


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                commonUtilsMethods.showToastMessage(Leave_Application.this, "Leave Submitted Successfully");
//                                if (isLeaveEntitlementRequested) {
//                                    leaveViewModel.updateLeaveStatusMasterSync();
//                                }
                                finish();

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.poor_connection));
                        }
                    });
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            leavebinding.etFromDate.setText("");
            leavebinding.etToDate.setText("");
            leavebinding.LeaveType.setText("");
            leavebinding.edAddress.getText().clear();
            leavebinding.edReason.getText().clear();
            leavebinding.balanceDays.setText("");
            List_LeaveDates.clear();

            Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
            LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
            Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
            Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
            Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
            l_details.notifyDataSetChanged();

            commonUtilsMethods.showToastMessage(Leave_Application.this, Leave_Application.this.getString(R.string.poor_connection));
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Leave_Application.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

//    private void setVisibility() {
//        if (isLeaveEntitlementRequested) {
//            leavebinding.chartLayout.setVisibility(View.VISIBLE);
//        } else {
//            leavebinding.chartLayout.setVisibility(View.GONE);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        timeZoneVerification();
    }

    private void timeZoneVerification() {
        boolean isAutoTimeZoneEnabled = commonUtilsMethods.isAutoTimeEnabled(this) && commonUtilsMethods.isTimeZoneAutomatic(this);
        if (!isAutoTimeZoneEnabled) {
            CommonUtilsMethods.showCustomDialog(this);
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setMaxLength() {

        leavebinding.edAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String str = s.toString();
                if (str.length() > 300) {
                    String truncated = str.substring(0, 300);
                    leavebinding.edAddress.setText(truncated);
                    leavebinding.edAddress.setSelection(truncated.length());
                    closeKeyboard();
                    commonUtilsMethods.showToastMessage(Leave_Application.this, "Maximum address length reached. Please keep it under 300 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        leavebinding.edReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String str = s.toString();
                if (str.length() > 300) {
                    String truncated = str.substring(0, 300);
                    leavebinding.edReason.setText(truncated);
                    leavebinding.edReason.setSelection(truncated.length());
                    commonUtilsMethods.showToastMessage(Leave_Application.this, "Maximum Reason length reached. Please keep it under 300 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void onClickListener() {
        leavebinding.leaveStatusSync.setOnClickListener(view -> {
            List_LeaveDates.clear();
            leavebinding.etFromDate.setText("");
            leavebinding.etToDate.setText("");
            leavebinding.LeaveType.setText("");
            leavebinding.balanceDays.setText("");
            leavebinding.lDays.setText("");
            List_LeaveDates = new ArrayList<>();

            Leavedetails_adapter l_details = new Leavedetails_adapter(Leave_Application.this, List_LeaveDates);
            LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(Leave_Application.this);
            Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
            Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
            Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
            l_details.notifyDataSetChanged();
            leavebinding.progressBar.setVisibility(View.VISIBLE);
            leaveViewModel.updateLeaveStatusMasterSync();
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    leavebinding.progressBar.setVisibility(View.GONE);
//                    AvailableLeave();
//                }
//            };

//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(runnable, 500);
        });
    }
}