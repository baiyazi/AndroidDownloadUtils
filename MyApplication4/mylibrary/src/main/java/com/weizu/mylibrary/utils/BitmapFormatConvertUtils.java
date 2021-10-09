package com.weizu.mylibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

public class BitmapFormatConvertUtils {

    public static Drawable bitmap2Drawable(Bitmap bitmap){
        return new BitmapDrawable(null, bitmap);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] data){
        if(data.length != 0)
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        return null;
    }
}
