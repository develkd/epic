package de.master.kd.epic.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pentax on 03.07.17.
 */

public class PictureService {

    private final static int widthIcon = 42;
    private final static int heightIcon = 32;

    private PictureService() {

    }

    public static Bitmap createMarkerIcon(Bitmap src) {
        return resizeBitmap(src,90,  widthIcon, heightIcon);
    }


    public static Bitmap createBitmap(final Context context, final View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    public static Bitmap fitBitmapIn(final Bitmap srcBitmap) {
        return resizeBitmap(srcBitmap, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
    }

    private static Bitmap resizeBitmap(final Bitmap srcBitmap, int degree, int width, int height) {
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Matrix m = new Matrix();
        m.setScale((float) width / srcBitmap.getWidth(), (float) height / srcBitmap.getHeight());
        Canvas canvas = new Canvas(output);
        canvas.rotate(degree,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap(srcBitmap, m, new Paint());
        return output;
    }


    public static Bitmap loadImage(Context context,String path) {
        Bitmap b =  FileHandlingService.getImageBitmap(context, path);
        if(null != b){
            b = resizeBitmap(b, 90, widthIcon, heightIcon);
        }
        return b;
    }

}
