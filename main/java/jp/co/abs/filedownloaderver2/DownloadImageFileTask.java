package jp.co.abs.filedownloaderver2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;

public final class DownloadImageFileTask extends AsyncTask<String, Void, Bitmap> {

    private DownloadEndListener mDownloadEndListener;

    public DownloadImageFileTask() {
        super();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap image = null;
        BitmapFactory.Options options;
        try {

            URL url = new URL(strings[0]);
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            InputStream is = (InputStream) url.getContent();
            image = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        super.onPostExecute(image);

        if (mDownloadEndListener != null) {
            mDownloadEndListener.onDownloadEnd(image);
        }
    }

    public interface DownloadEndListener {
        void onDownloadEnd(Bitmap image);
    }

    public void setDownloadEndListener(DownloadEndListener downloadEndListener) {
        mDownloadEndListener = downloadEndListener;
    }
}

