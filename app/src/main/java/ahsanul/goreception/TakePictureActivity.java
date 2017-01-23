package ahsanul.goreception;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ahsanul.goreception.CameraService.CameraPreview;

public class TakePictureActivity extends AppCompatActivity {

    private Button takePictureButton;
    private ImageView imageView;

    private android.hardware.Camera mCamera;
    private CameraPreview mPreview;
    Uri file;
   JSONArray resultjson;
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_take_picture);
            mCamera = getCameraInstance();


            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(getBaseContext(), mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            Button captureButton = (Button) findViewById(R.id.button_capture);
            captureButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            mCamera.takePicture(null, null, mPicture);
                            Log.d("gotiC",mCamera.toString());
                            Log.d("gotiP",mPicture.toString());
                            finish();
                            Intent intent = new Intent(getBaseContext(), SearchHostActivity.class);
                            intent.putExtra("User_id",getIntent().getStringExtra("User_id"));
                            intent.putExtra("business_id",getIntent().getStringExtra("business_id"));
                            intent.putExtra("SToken",getIntent().getStringExtra("SToken"));
                            intent.putExtra("Fullname",getIntent().getStringExtra("Fullname"));
                            try{
                                resultjson = new JSONArray(getIntent().getStringExtra("Fields"));
                                int lengthField = resultjson.length();
                                for (int i = 0; i < lengthField; i++) {
                                    intent.putExtra(resultjson.getJSONObject(i).getString("name"),getIntent().getStringExtra(resultjson.getJSONObject(i).getString("name")));
                                    Log.d("walah",resultjson.getJSONObject(i).getString("name")+" "+getIntent().getStringExtra(resultjson.getJSONObject(i).getString("name")));}
                            }catch(Exception e){
                                Log.d("walah",e.getMessage());
                            }
                            intent.putExtra("Fields",resultjson.toString());
                            startActivity(intent);
                            Log.d("CameraDemo", "Pic saved");
                        }
                    }
            );
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(1);
            // attempt to get a QRScanActivity instance
        }
        catch (Exception e){
            // QRScanActivity is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
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
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            Log.d("gotiP",pictureFile.toString());
            try{
               // Uri uri = pictureFile;

              //  Bitmap photo = getBitmapFromUri(uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);


            }catch (Exception e){

            }
            if (pictureFile == null){
                Exception e=null;
                Log.d("test", "Error creating media file, check storage permissions: " +
                        e.getMessage());
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);

                Log.d("gotiP",fos.toString());

                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("test", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("test", "Error accessing file: " + e.getMessage());
            }
        }
    };

    private Bitmap getBitmapFromUri(Uri uri) throws IOException{
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri,"r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
        }


