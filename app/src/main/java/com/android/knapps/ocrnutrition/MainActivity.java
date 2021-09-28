package com.android.knapps.ocrnutrition;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private final int PERMISSIONS_WRITE_STORAGE = 101;
    private final int PERMISSIONS_CAMERA = 102;
    String valor = null;
    ImageView iv = null;
    TextView txt = null;
    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private Uri picUri;
    TextRecognizer recognizer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.imageView);
        txt = findViewById(R.id.texto);
        Button captureBtn = (Button) findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        askForWriteStoragePermission();


    }

    private void loadWithOCR(){
     //   mTessOCR = new TessOCR(this,"eng");
        // mTessOCR.doOCR(bitmap);

       /* Drawable drawable = ContextCompat.getDrawable(this, R.drawable.etiqueta3);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }*/


     //  Picasso.get().load(R.drawable.etiqueta1).into(iv);


    //   Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.etiqueta1);

       // doOCR(convertColorIntoBlackAndWhiteImage(bitmap));

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE) {
            // get the Uri for the captured image


            //    Uri file = Uri.fromFile(getOutputMediaFile());

           // picUri = Uri.fromFile(getOutputMediaFile());


      //      File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");

            //Uri of camera image
   //         picUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
          /*  Bitmap bitmap = null;
            try {
                bitmap = this.getBitmapFromUri(picUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
*/

            performCrop();
        }
        // handle result of pick image chooser
      /*  if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE )
        {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri))
            {

            }
            else
            {
                // no permissions required or already grunted, can start crop image activity
                performCrop();
                //     CropImage.
            }
        }*/

/*
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        Bitmap bitmap = this.getBitmapFromUri(resultUri);
                        doOCR(convertColorIntoBlackAndWhiteImage(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
*/
     /*   if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE){
            data.toString();
        }
*/
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Bitmap bitmap = null;
            try {
                bitmap = this.getBitmapFromUri(resultUri);
           //     bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.etiqueta1);
                doOCR(convertColorIntoBlackAndWhiteImage(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }


    }


    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not



      //      CropImage.activity(Uri.fromFile(new File(picUri.getPath()))).start(this);
       //     CropImage.startPickImageActivity(this);

            Uri destinationUri = Uri.fromFile(new File(this.getCacheDir(), "IMG_" + System.currentTimeMillis()));

            UCrop.of(picUri, destinationUri)
                    .withMaxResultSize(1080, 768) // any resolution you want
                    .start(this);

        /*    UCrop.of(null, destinationUri)
                    .withAspectRatio(16, 9)
                    .withMaxResultSize(200, 200)
                    .start(this);
*/

        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    Uri outFileUri = null;

    public void onClick(View v) {
        if (v.getId() == R.id.capture_btn) {
            try {



                // ESTO ES LA POSTA

           /*     Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                picUri  = Uri.parse("file:///sdcard/photo.jpg");
                captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, picUri);

                startActivityForResult(captureIntent, CAMERA_CAPTURE);

*/




          //      doOCR(convertColorIntoBlackAndWhiteImage(bitmap));





            } catch (ActivityNotFoundException anfe) {
                Toast toast = Toast.makeText(this, "This device doesn't support the crop action!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
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
       // return blackAndWhiteBitmap.copy(Bitmap.Config.ARGB_8888, true);
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


            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSIONS_CAMERA);


            }/*else {//Ya tiene el permiso...
                this.loadWithOCR();
            }*/
        } else {
            this.loadWithOCR();
        }


    }

    ProgressDialog mProgressDialog = null;
    Activity ocrView= null;

    private void doOCR(final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "Processing",
                    "Doing OCR...", true);
        } else {
            mProgressDialog.show();
        }

        final Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.etiqueta1);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // ...
                                txt.setText(visionText.getText());
                                iv.setImageBitmap(bitmap1);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        e.printStackTrace();
                                    }
                                });


        /*new Thread(new Runnable() {
            public void run() {

                final String srcText = mTessOCR.getOCRResult(bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (srcText != null && !srcText.equals("")) {
                            txt.setText(srcText);
                            iv.setImageBitmap(bitmap);
                        }
                        mProgressDialog.dismiss();
                    }
                });
            }
        }).start();*/
    }

/*    private void doOCR (final Bitmap bitmap) {
        new Thread(new Runnable() {
            public void run() {
            final String srcText = mTessOCR.getOCRResult(bitmap);
                valor = srcText;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (srcText != null && !srcText.equals("")) {
                            txt.setText(srcText);
                        }
                    }
                });
            }
        }).start();
    }*/
}