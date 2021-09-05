package com.android.knapps.ocrnutrition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSIONS_WRITE_STORAGE = 101;
    private TessOCR mTessOCR = null;
    String valor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loadWithOCR();
        askForWriteStoragePermission();

    }

    private void loadWithOCR(){
        mTessOCR = new TessOCR(this,"eng");
        // mTessOCR.doOCR(bitmap);

       /* Drawable drawable = ContextCompat.getDrawable(this, R.drawable.etiqueta3);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }*/

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.etiqueta1);

    /*    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);*/
        doOCR(convertColorIntoBlackAndWhiteImage(bitmap));
    }


    private Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);

        Bitmap blackAndWhiteBitmap = orginalBitmap.copy(
                Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setColorFilter(colorMatrixFilter);

        Canvas canvas = new Canvas(blackAndWhiteBitmap);
        canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

        return blackAndWhiteBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //   mCameraPermissionGranted = false;


        if (requestCode == PERMISSIONS_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.loadWithOCR();
            }
        }/*else{
                Intent intent = getIntent();
                finish();
                startActivity(intent);
        }*/


    }

    private void askForWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_WRITE_STORAGE);


            } else {//Ya tiene el permiso...
                this.loadWithOCR();
            }
        } else {
            this.loadWithOCR();
        }


    }

    ProgressDialog mProgressDialog = null;
    Activity ocrView= null;
    private void doOCR (final Bitmap bitmap) {

      /*  if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(ocrView, "Processing",
                    "Doing OCR...", true);
        } else {
            mProgressDialog.show();
        }*/
        new Thread(new Runnable() {
            public void run() {


                final String srcText = mTessOCR.getOCRResult(bitmap);
                valor = srcText;
           /*     ocrView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (srcText != null && !srcText.equals("")) {
                            //srcText contiene el texto reconocido
                        }
                        mTessOCR.onDestroy();
                        mProgressDialog.dismiss();
                    }
                });*/
            }
        }).start();
    }
}