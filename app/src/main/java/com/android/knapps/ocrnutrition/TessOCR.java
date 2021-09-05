package com.android.knapps.ocrnutrition;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TessOCR {

    private TessBaseAPI mTess;
    private String datapath;
    public Context context;


    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/ocrctz/";
    public static final String lang = "eng";

    private static final String TAG = "OCR";

    public TessOCR(Context context, String language) {
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
        mTess = new TessBaseAPI();
        this.context = context;
        datapath = Environment.getExternalStorageDirectory() + "/ocrctz";

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }
      /*
        File dir = new File(datapath + "/tessdata/");
        File file = new File(datapath + "/tessdata/" + "eng.traineddata");
        if (!file.exists())
        {
            Log.d("mylog", "in file doesn't exist");
            dir.mkdirs();
            copyFile(context);
        }
*/
        AssetManager assetManager = context.getAssets();

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

        mTess.setDebug(true);
        mTess.init(DATA_PATH, lang);

        /*
        mTess = new TessBaseAPI();
        String lang = "eng";

        String str1, str2;
        str1 = context.getFilesDir() + "/tessdata/";
        str2 = datapath + "/tessdata/";
        mTess.init(datapath, lang);


        mTess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_ONLY);*/
    }

    private void copyFile(Context context)
    {
        AssetManager assetManager = context.getAssets();
        try
        {
            InputStream in = assetManager.open("eng.traineddata");
            OutputStream out = new FileOutputStream(datapath + "tessdata/" + "eng.traineddata");
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);            }
        } catch (Exception e)
        {
            Log.d("mylog", "couldn't copy with the following error : "+e.toString());
        }
    }

  /*  public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap); return mTess.getUTF8Text();
    }
*//*
    public void onDestroy() {
        if (mTess != null) mTess.end();
    }*/

    public void stopRecognition() {
        mTess.stop();
    }

    public String getOCRResult(Bitmap bitmap)
    {
        mTess.setImage(bitmap);
        String detected = mTess.getUTF8Text();
        return detected;
    }

    public void onDestroy()
    {
        if (mTess != null)
            mTess.end();
    }
/*
    private void copyFile(Context context)
    {
        AssetManager assetManager = context.getAssets();
        try
        {   InputStream in = assetManager.open("eng.traineddata");
            OutputStream out = new FileOutputStream(datapath + "/tessdata/" + "eng.traineddata");
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);            }
        } catch (Exception e)
        {
            Log.d("mylog", "couldn't copy with the following error : "+e.toString());
        }
    }*/
}
