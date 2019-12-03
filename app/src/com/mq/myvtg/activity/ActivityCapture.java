package com.mq.myvtg.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.mq.myvtg.R;
import com.mq.myvtg.util.UIHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityCapture extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener {

    private static final String TAG = ActivityCapture.class.getSimpleName();

    private static final int RESULT_FRONT = 1;
    private static final int RESULT_BACK = 2;

    private Camera mCamera;
    private TextureView mTextureView;
    private ImageView btnTakePhoto;
    private boolean isAvailable = false;
    private ProgressBar mProgressBar;
    private RelativeLayout mBorder;
    private boolean isFrontCard = false;

    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIHelper.setOverlayStatusBar(getWindow(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);
        getSupportActionBar().hide();

        initUI();
    }

    private void initUI() {
        mTextureView = findViewById(R.id.tt_view);
        mTextureView.setSurfaceTextureListener(this);
        mBorder = findViewById(R.id.border);
        mProgressBar = findViewById(R.id.progress_loader);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnTakePhoto = findViewById(R.id.btn_capture);
        btnTakePhoto.setOnClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels - 100;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBorder.getLayoutParams();
        params.height = (int) (width / 1.59);
        params.width = width;
        mBorder.setLayoutParams(params);

        isFrontCard = getIntent().getBooleanExtra("camera", true);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        isAvailable = true;
        Log.d("TAG", "onSurfaceTextureAvailable: " + i + " - " + i1);
        openCamera();
    }

    private void openCamera() {
        if (checkPermissionCaptureCamera()) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

            int rotate = info.orientation;

            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            mCamera.setDisplayOrientation(rotate);

            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            int cameraWidth;
            int cameraHeight;
            if (sizes.get(sizes.size() - 1).width * sizes.get(sizes.size() - 1).height
                    < sizes.get(0).width * sizes.get(0).height) {
                cameraWidth = sizes.get(0).width;
                cameraHeight = sizes.get(0).height;
            } else {
                cameraWidth = sizes.get(sizes.size() - 1).width;
                cameraHeight = sizes.get(sizes.size() - 1).height;
            }
            Log.d(TAG, "openCamera: " + cameraWidth + " - " + cameraHeight);
            parameters.setPictureSize(cameraWidth, cameraHeight);

            parameters.setRotation(rotate);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(parameters);

            try {
                mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        if (mCamera != null) {
            mCamera.release();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAvailable) {
            if (checkPermissionCaptureCamera()) {
                openCamera();
            }
        }
    }

    private boolean checkPermissionCaptureCamera() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        List<String> listPers = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            listPers.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            listPers.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            listPers.add(Manifest.permission.CAMERA);
        }

        if (listPers.size() == 0) {
            return true;
        }

        String[] pers = new String[listPers.size()];
        int index = 0;
        for (String lisPer : listPers) {
            pers[index] = lisPer;
            index++;
        }
        ActivityCompat.requestPermissions(this, pers, 100);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    return;
                }
            }
            if (isAvailable) {
                openCamera();
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_capture) {
            if (mCamera == null) {
                return;
            }
            btnTakePhoto.setClickable(false);
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    camera.release();
                    savePicture(bytes);
                }
            });
        } else {
            setResult(0);
            finish();
        }
    }

    private void savePicture(byte[] bytes) {
        final String imgPath = Environment.getExternalStorageDirectory() + File.separator
                + new Date().getTime() + "IMG.jpg";
        try {
            FileOutputStream out = new FileOutputStream(new File(imgPath));
            out.write(bytes);
            out.close();

            Bitmap bm = BitmapFactory.decodeFile(new File(imgPath).getAbsolutePath());
            int rotate = getDirBitmap(imgPath);
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.postRotate(rotate);

            Bitmap rotatedBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotationMatrix, true);

            new SavePictureTask(getApplicationContext(), rotatedBm, new OnSaveListener() {
                @Override
                public void onSaved(String path) {
                    Intent intent = new Intent();
                    intent.putExtra("result", path);
                    if (isFrontCard) {
                        setResult(RESULT_FRONT, intent);
                    } else {
                        setResult(RESULT_BACK, intent);
                    }
                    deleteImg(imgPath);
                    finish();
                    mCamera.release();
                }
            }).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean deleteImg(String path) {
        File file = new File(path);
        return file.delete();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    class SavePictureTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private Bitmap bitmap;
        private OnSaveListener listener;

        SavePictureTask(Context context, Bitmap bitmap, OnSaveListener listener) {
            this.context = context;
            this.bitmap = bitmap;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                //Write file
                String filename = "bitmap.png";
                FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Cleanup
                stream.close();
                bitmap.recycle();

                //Pop intent
                return filename;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressBar.setVisibility(View.GONE);
            listener.onSaved(s);
            btnBack.setEnabled(true);
        }
    }

    interface OnSaveListener {
        void onSaved(String path);
    }

    public static int getDirBitmap(String path) {
        ExifInterface exif = null;
        int rotate = 0;
        try {
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }
}