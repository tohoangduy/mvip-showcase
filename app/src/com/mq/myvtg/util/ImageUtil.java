package com.mq.myvtg.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();

    public static Bitmap decodeFileResize(String localImagePath, int maxW, int maxH) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(localImagePath, options);
        float ratio = ((float) options.outWidth) / options.outHeight;
        int reqWidth, reqHeight;
        if (ratio > 1) {
            reqWidth = maxW;
            reqHeight = (int) (reqWidth / ratio);
        } else {
            reqHeight = maxH;
            reqWidth = (int) (reqHeight * ratio);
        }
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(localImagePath, options);
        return Bitmap.createScaledBitmap(bm, reqWidth, reqHeight, false);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap getFromLocal(String filePath) {
        return BitmapFactory.decodeFile(filePath, null);
    }

    // this method should be called in background task
    public static Bitmap getFromNet(String url) {
        Bitmap bm;
        try {
            InputStream in = new URL(url).openStream();
            bm = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            LogUtil.logException(TAG, e);
            if (e instanceof MalformedURLException) {
                LogUtil.e(TAG, "image url: " + url);
            }
            bm = null;
        } catch (OutOfMemoryError e) {
            LogUtil.e(TAG, "OutOfMemoryError " + e.getMessage());
            bm = null;
        }
        return bm;
    }

    public static String resizeIfNeeded(String imagePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        if (options.outWidth <= maxWidth && options.outHeight <= maxHeight) {
            return imagePath;
        }
        Bitmap bmp = ImageUtil.decodeFileResize(imagePath, maxWidth, maxHeight);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imagePath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            LogUtil.logException("resizeIfNeeded failed", e);
        }

        return imagePath;
    }

    public static void getImageSize(String localFile, int[] size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(localFile, options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;
    }

    // Convert a view to bitmap
    public static Bitmap drawableFromView(Activity context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
