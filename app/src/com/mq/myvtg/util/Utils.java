package com.mq.myvtg.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;

import android.util.StateSet;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mq.myvtg.R;
import com.mq.myvtg.base.BaseFrgmtActivity;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.model.ModelAppConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.List;

public final class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    // Dùng sau khi App Contacts trả về onActivityResult
    // Uri contactUri = data.getFilterData();
    public static ContactInfo getContact(Uri contactUri, Context context) {
        ContentResolver cr = context.getContentResolver();
        String id = null, name = null, phone = null, hasPhone = null, email = null;
        int idx;
        Cursor cursor = cr.query(contactUri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            id = cursor.getString(idx);
            idx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            name = cursor.getString(idx);
            idx = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            hasPhone = cursor.getString(idx);
            cursor.close();

            // Build the Entity URI.
            Uri.Builder b = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id).buildUpon();
            b.appendPath(ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);
            Uri contactUri1 = b.build();
            // Create the projection (SQL fields) and sort order.
            String[] projection = {
                    ContactsContract.Contacts.Entity.RAW_CONTACT_ID,
                    ContactsContract.Contacts.Entity.DATA1,
                    ContactsContract.Contacts.Entity.MIMETYPE};
            String sortOrder = ContactsContract.Contacts.Entity.RAW_CONTACT_ID + " ASC";
            cursor = cr.query(contactUri1, projection, null, null, sortOrder);
            if (cursor == null) {
                return new ContactInfo(id, name, null, null);
            }
            String mime;
            int mimeIdx = cursor.getColumnIndex(ContactsContract.Contacts.Entity.MIMETYPE);
            int dataIdx = cursor.getColumnIndex(ContactsContract.Contacts.Entity.DATA1);
            if (cursor.moveToFirst()) {
                do {
                    mime = cursor.getString(mimeIdx);
                    if (mime.equalsIgnoreCase(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                        email = cursor.getString(dataIdx);
                    }
                    if (mime.equalsIgnoreCase(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                        phone = cursor.getString(dataIdx);
                    }
                    // ...etc.
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return new ContactInfo(id, name, phone, email);
    }

    public static void openWeb(Context context, String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }

    public static void openAnotherApp(Context context, String uri, Runnable runIffailure) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(uri);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            if (runIffailure != null) {
                runIffailure.run();
            }
        }
    }

    public static boolean isLocationServiceEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static void gotoGooglePlay(Context context, String appId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId));
        context.startActivity(intent);
    }

    public static boolean appIsInstalled(Context context, String appId) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appId);
        return launchIntent != null;
    }

    public static String getTruePackageName(String rawName) {
        String[] listSplit = rawName.trim().split("&");
        if (listSplit != null && listSplit.length > 1) {
            return listSplit[0];
        }
        return rawName;
    }

    public static void openAnotherApp(final Context context, final String appId, String msg) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appId);
        if (launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            // package name was not found
            if (msg != null && msg.length() > 0) {
                new CustomDialog(context)
                        .setMess(msg)
                        .setButtonNegative(context.getString(R.string.label_btn_close), null)
                        .setButtonPositive(context.getString(R.string.label_ok), null)
                        .setListener(new CustomDialog.Listener() {
                            @Override
                            public void onOK(CustomDialog dlg) {
                                dlg.dismiss();
                                gotoGooglePlay(context, appId);
                            }
                        }).show();
            } else {
                gotoGooglePlay(context, appId);
            }
        }
    }

    public static String jsonStringFromAsset(Context context, String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtil.e(TAG, "jsonStringFromAsset " + LogUtil.getThrowableString(e));
            return null;
        }
        return json;
    }

    public static JSONObject jsonObjectFromAsset(Context context, String filename) {
        try {
            return new JSONObject(jsonStringFromAsset(context, filename));
        } catch (Exception e) {
            LogUtil.e(TAG, "jsonObjectFromAsset " + LogUtil.getThrowableString(e));
            return null;
        }
    }

    /**
     * This function to get locale match same current language.
     * The locale list defined in res/values/arrays.xml (array name: locale_supported)
     * The languages list defined in Const.java (LANGUAGES enum)
     * Note that to get right locale with a language, you must define pair locales and languages have same index on list defined
     * @param context
     * @return locale String with value format is en_EN
     */
    public static String getLocale(Context context) {
        List<String> localeSupported = Arrays.asList(context.getResources().getStringArray(R.array.locale_supported));
        String lang = getLanguage(context);
        List<String> languages = ((BaseFrgmtActivity)context).languages;

        if (languages != null) {
            return localeSupported.get(languages.indexOf(lang));
        }

        return localeSupported.get(1); // default is en_US values
    }

    public static String getLanguage(Context context) {
        return SharePref.getString(context, Const.KEY_CURRENT_LANG, Const.LANGUAGES.KHMER);
    }

    public static void saveCurrentUserName(Context context, String userName) {
        SharePref.putString(context, Const.KEY_CURRENT_USER_NAME, userName);
    }

    public static String getCurrentUserName(Context context) {
        if (context == null) return "";
        String usrn = SharePref.getString(context, Const.KEY_CURRENT_USER_NAME, "");
        if (usrn != null) usrn = usrn.replace("+", "").replace(" ", "").replace("-", "");
        return usrn;
    }

    public static boolean checkUsesPermission(Context context, String permission, int checkPermissionCode) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, checkPermissionCode);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, checkPermissionCode);
            }

            return false;
        } else {
            return true;
        }
    }

    public static void getAndSaveImage(final Context context, final ModelAppConfig.FunctItem item, final String localImgName) {
//        Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                try {
//                    File file = new File(context.getDir("icons", Context.MODE_PRIVATE), localImgName);
//                    FileOutputStream out = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//                    item.normalIconUrl = file.getAbsolutePath();
//
//                    out.flush();
//                    out.close();
//                } catch (IOException e) {
//                    LogUtil.d("IOException", e.getLocalizedMessage());
//                }
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//            }
//        };

//        Picasso.with(context)
//                .load(item.normalIconUrl)
//                .into(target);
        Glide.with(context)
                .load(item.normalIconUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        try {
                            File file = new File(context.getDir("icons", Context.MODE_PRIVATE), localImgName);
                            FileOutputStream out = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 90, out);
                            item.normalIconUrl = file.getAbsolutePath();

                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            LogUtil.d("IOException", e.getLocalizedMessage());
                        }
                    }
                });

    }

    public static StateListDrawable getAndSaveDrawable(final Context context, String normalStateUrl, String seletedStateUrl, final String normalName, final String selectedName) {
        final StateListDrawable stateListDrawable = new StateListDrawable();
        // final Picasso picasso = Picasso.with(context);
        // selected and checked state
//        picasso.load(seletedStateUrl).into(new Target() {
//            @Override
//            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                Drawable drawImage = new BitmapDrawable(context.getResources(), bitmap);
//                stateListDrawable.addState(new int[]{android.R.attr.state_selected}, drawImage);
//                stateListDrawable.addState(new int[]{android.R.attr.state_activated}, drawImage);
//
//                // store in internal memory
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/icons/" + selectedName);
//                        try {
//                            FileOutputStream out = context.openFileOutput(selectedName, Context.MODE_PRIVATE);
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//
//                            out.flush();
//                            out.close();
//                        } catch (IOException e) {
//                            LogUtil.d("IOException", e.getLocalizedMessage());
//                        }
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        });
        Glide.with(context).load(seletedStateUrl).
                asBitmap().
                into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawImage = new BitmapDrawable(context.getResources(), resource);
                        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, drawImage);
                        stateListDrawable.addState(new int[]{android.R.attr.state_activated}, drawImage);

                        // store in internal memory
                        new Thread(new Runnable() {

                            @Override
                            public void run() {

                                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/icons/" + selectedName);
                                try {
                                    FileOutputStream out = context.openFileOutput(selectedName, Context.MODE_PRIVATE);
                                    resource.compress(Bitmap.CompressFormat.PNG, 90, out);

                                    out.flush();
                                    out.close();
                                } catch (IOException e) {
                                    LogUtil.d("IOException", e.getLocalizedMessage());
                                }
                            }
                        }).start();
                    }
                });

//        picasso.load(normalStateUrl)
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                        Drawable drawImage = new BitmapDrawable(context.getResources(), bitmap);
//                        stateListDrawable.addState(StateSet.WILD_CARD, drawImage);
//
//                        // store in internal memory
//                        new Thread(new Runnable() {
//
//                            @Override
//                            public void run() {
//
//                                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/icons/" + selectedName);
//                                try {
//                                    FileOutputStream out = context.openFileOutput(selectedName, Context.MODE_PRIVATE);
//                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//
//                                    out.flush();
//                                    out.close();
//                                } catch (IOException e) {
//                                    LogUtil.d("IOException", e.getLocalizedMessage());
//                                }
//                            }
//                        }).start();
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });
        Glide.with(context).load(normalStateUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawImage = new BitmapDrawable(context.getResources(), resource);
                        stateListDrawable.addState(StateSet.WILD_CARD, drawImage);

                        // store in internal memory
                        new Thread(new Runnable() {

                            @Override
                            public void run() {

                                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/icons/" + selectedName);
                                try {
                                    FileOutputStream out = context.openFileOutput(selectedName, Context.MODE_PRIVATE);
                                    resource.compress(Bitmap.CompressFormat.PNG, 90, out);

                                    out.flush();
                                    out.close();
                                } catch (IOException e) {
                                    LogUtil.d("IOException", e.getLocalizedMessage());
                                }
                            }
                        }).start();
                    }
                });

        return stateListDrawable;
    }

    public static StateListDrawable getDrawable(final Context context, ModelAppConfig.FunctItem item) {
        final StateListDrawable stateListDrawable = new StateListDrawable();
        // final Picasso picasso = Picasso.with(context);

//        Picasso.with(context)
//                .load(new File(item.selectedIconUrl))
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        Drawable drawImage = new BitmapDrawable(context.getResources(), bitmap);
//                        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, drawImage);
//                        stateListDrawable.addState(new int[]{android.R.attr.state_activated}, drawImage);
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });
        Glide.with(context)
                .load(new File(item.selectedIconUrl))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawImage = new BitmapDrawable(context.getResources(), resource);
                        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, drawImage);
                        stateListDrawable.addState(new int[]{android.R.attr.state_activated}, drawImage);
                    }
                });
//
//        Picasso.with(context)
//                .load(new File(item.normalIconUrl))
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        Drawable drawImage = new BitmapDrawable(context.getResources(), bitmap);
//                        stateListDrawable.addState(StateSet.WILD_CARD, drawImage);
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });
        Glide.with(context)
                .load(new File(item.normalIconUrl))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawImage = new BitmapDrawable(context.getResources(), resource);
                        stateListDrawable.addState(StateSet.WILD_CARD, drawImage);
                    }
                });

        return stateListDrawable;
    }

    public static void setTintedDrawable(Resources res, ImageView imageView) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                imageView.getDrawable().setColorFilter(res.getColor(R.color.icon_color_n), PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {

        }

    }

    public static void setTintedDrawable(Resources res, ImageView imageView, boolean isCheckBuildVersion) {
        if (isCheckBuildVersion && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            imageView.getDrawable().setColorFilter(res.getColor(R.color.icon_color_n), PorterDuff.Mode.SRC_IN);
        } else if (!isCheckBuildVersion) {
            imageView.getDrawable().setColorFilter(res.getColor(R.color.icon_color_n), PorterDuff.Mode.SRC_IN);
        }
    }

    public static void setTintedDrawable(Resources res, ImageView imageView, int drawableResId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = res.getDrawable(drawableResId);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTintList(drawable, res.getColorStateList(R.color.color_state_list));
            imageView.setImageDrawable(drawable);
        }
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }
}
