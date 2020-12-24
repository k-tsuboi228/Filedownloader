package jp.co.abs.filedownloaderver2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final int RESULT_PICK_IMAGEFILE = 1000;
    private static final int REQUEST_OPEN_TREE = 42;
    private EditText mEditText;
    private ImageView mImageView;

    private DownloadImageFileTask.DownloadEndListener downloadEndListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button pickButton = findViewById(R.id.ShowGallery);
        Button urlButton = findViewById(R.id.urlButton);
        Button googleButton = findViewById(R.id.googleButton);
        Button clearButton = findViewById(R.id.Clear);
        Button FolderButton = findViewById(R.id.Folder);

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFilenameFromGallery();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText != null) {
                    mEditText.setText(null);
                }
                if (mImageView != null) {
                    mImageView.setImageBitmap(null);
                }
            }
        });

        FolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, REQUEST_OPEN_TREE);
            }
        });

        urlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText = findViewById(R.id.urlText);
                String urlText = mEditText.getText().toString();

                downloadEndListener = new DownloadImageFileTask.DownloadEndListener() {
                    @Override
                    public void onDownloadEnd(Bitmap image) {
                        if (image != null) {
                            ImageView imageView = (ImageView) MainActivity.this.findViewById(R.id.urlView);
                            imageView.setImageBitmap(image);
                            saveImage();
                            Toast toast = Toast.makeText(MainActivity.this, "ダウンロードが完了しました。", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this, "画像取得に失敗しました。", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                };

                DownloadImageFileTask task = new DownloadImageFileTask();
                task.setDownloadEndListener(downloadEndListener);
                task.execute(urlText);
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://images.google.co.jp/imghp?q=";

                Intent intent = new Intent(MainActivity.this, GoogleActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void pickFilenameFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_PICK_IMAGEFILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK && null != data) {
            mImageView = (ImageView) findViewById(R.id.urlView);
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Toast.makeText(this, "ダウンロードが完了しました。", Toast.LENGTH_LONG).show();
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mImageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveImage() {
        mImageView = (ImageView) findViewById(R.id.urlView);
        Bitmap viewImage = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

        FileManager fileManager = new FileManager(this);
        try {
            String albumName = "Tsuboi_pic";
            fileManager.save(viewImage, albumName);
        } catch (Error e) {
            Log.e("MainActivity", "onCreate:" + e);
        }
    }
}