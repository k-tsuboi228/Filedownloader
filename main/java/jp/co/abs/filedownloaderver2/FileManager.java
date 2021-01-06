package jp.co.abs.filedownloaderver2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FileManager {
    private Context mContext;
    private String mFileFullPath;

    public FileManager(Context context) {
        mContext = context;
    }

    public void save(Bitmap bitmap, String albumName) {
        saveToSd(getSdStorageDir(albumName), bitmap);
    }

    private void saveToSd(File dir, Bitmap bitmap) {
        String fileName = getFileName();

        mFileFullPath = dir.getAbsolutePath() + "/" + fileName;
        try {
            FileOutputStream fos = new FileOutputStream(mFileFullPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("Error", "" + e.toString());
        } finally {
            addGallery(fileName);
        }
    }

    private void addGallery(String fileName) {
        try {
            ContentValues values = new ContentValues();
            ContentResolver contentResolver = mContext.getContentResolver();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put("_data", mFileFullPath);
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            Log.e("Error", "" + e);
        }
    }

    private String getFileName() {
        Date mDate = new Date();
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);

        return fileNameFormat.format(mDate) + ".jpg";
    }

    private File getSdStorageDir(String albumName) {
        String extStorageDir = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(extStorageDir, albumName);
        Log.d("dir", "" + dir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("Error", "Directory not created");
            }
        }
        return dir;
    }
}