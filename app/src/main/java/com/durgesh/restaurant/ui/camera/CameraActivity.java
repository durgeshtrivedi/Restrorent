package com.durgesh.restaurant.ui.camera;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

import com.durgesh.restaurant.R;
import com.durgesh.restaurant.models.googlePlaces.ImageCompareResponse;
import com.durgesh.restaurant.models.googlePlaces.Ingredients;
import com.durgesh.restaurant.network.ApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.durgesh.restaurant.app.constant.RWebConstants.BAD_GATEWAY;
import static com.durgesh.restaurant.app.constant.RWebConstants.NOT_FOUND;


public class CameraActivity extends AppCompatActivity implements Camera.PictureCallback {

    protected static final String EXTRA_IMAGE_PATH = "com.durgesh.restaurent.ui.EXTRA_IMAGE_PATH";
    private static final int REQUEST_CODE = 1;
    private static final int CAMERA = 3;
    private Camera mCamera;
    private com.durgesh.restaurant.network.ApiHelper service;
    private static final String TAG = "CameraActivity";

    @BindView(R.id.camera_preview)
    protected CameraPreview cameraPreview;

    @BindView(R.id.btn_capture_image)
    protected Button mBtnCaptureImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = Camera.open();
        mBtnCaptureImage.setEnabled(true);


        if (ApiClient.getClient(this) != null) {
            service = ApiClient.getClient(this).create(com.durgesh.restaurant.network.ApiHelper.class);
        }

        setResult(RESULT_CANCELED);
        if (CameraHelper.cameraAvailable(mCamera)) {
            initCameraPreview();
        } else {
            finish();
        }
    }

    private void initCameraPreview() {
        cameraPreview.init(mCamera);
    }

    @OnClick(R.id.btn_capture_image)
    public void onCaptureClick() {
        mCamera.takePicture(shutterCallback, null, this);
        mBtnCaptureImage.setEnabled(false);
    }

    private final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        String path = savePictureToFileSystem(data);
        setResult(path);
        // finish();
    }

    private String savePictureToFileSystem(byte[] data) {
        File file = getOutputMediaFile();
        saveToFile(data, file);
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//       Bitmap.createScaledBitmap(bitmap,640,480,false);


        Log.v("**File_bitmap", bitmap + "");
        Log.v("**Bitmap size", bitmap.getByteCount() + "");
        uploadImage(bitmap);
        return file.getAbsolutePath();
    }

    private void setResult(String path) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IMAGE_PATH, path);
        setResult(RESULT_OK, intent);
//        mCamera.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }


    }

    public File getOutputMediaFile() {

        File file = null;
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SnapX360");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        file = new File(mediaStorageDir.getPath() + File.separator + "SnapX360_IMG_" + new Date().getTime() + ".jpeg");
        Log.v("**File_path", file.getAbsolutePath() + "");

        return file;
    }

    private void uploadImage(Bitmap bitmap) {

        final ProgressDialog pd = new ProgressDialog(CameraActivity.this);
        pd.setMessage("Fetching Details...");
        pd.show();

        //Upload Image API call
        String myBase64Image = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
        Log.v("**Encoded image", "" + myBase64Image);

        Log.v("Encoded Image size", "" + bitmap.getByteCount());
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("name", "data:image/jpeg;base64," + myBase64Image);

        Log.v("Picture height ", "" + mCamera.getParameters().getPictureSize().height);
        Log.v("Picture width", "" + mCamera.getParameters().getPictureSize().height);

        final RequestBody requestBody = bodyBuilder.build();

        if (service != null) {
            Call<ImageCompareResponse> call = service.uploadImage(requestBody);

            call.enqueue(new Callback<ImageCompareResponse>() {
                @Override
                public void onResponse(Call<ImageCompareResponse> call, Response<ImageCompareResponse> response) {
                    Log.v("Response code", "" + response.code());
                    pd.cancel();
                    if (response.code() == 200) {
                        //RToast.showToast(CameraActivity.this, "Image uploaded successfully");
                        ImageCompareResponse imageCompareResponse = response.body();
                        if (imageCompareResponse != null) {


                            Log.d(TAG, String.valueOf(imageCompareResponse.getCode()));

                            if (imageCompareResponse.getCode()) {

                                Log.d(TAG, imageCompareResponse.getName());
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(CameraActivity.this);
                                DecimalFormat df = new DecimalFormat("0.00");
                                df.setMaximumFractionDigits(2);
                                String probability = df.format(imageCompareResponse.getValue());

                                //To show percentage
//                                builder1.setTitle("The dish is " + imageCompareResponse.getName() + " " + (Double.parseDouble(probability) * 100) + "%");
                                builder1.setTitle("The dish is " + imageCompareResponse.getName());

                                List<Ingredients> ingredientsList = imageCompareResponse.getIngredients();
                                String message = "";
                                for (Ingredients ingredient : ingredientsList) {
                                    if (ingredient.getValue() > 0.7) {
                                        String percentage = df.format(ingredient.getValue() * 100);
                                        message = message.concat(ingredient.getName() + " - " + percentage + "%" + "\n");

                                    }
                                }
//                                builder1.setMessage("The ingredients are : \n " + message);
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                finish();
                                            }
                                        });
                                builder1.setNegativeButton(
                                        "Take new",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                mCamera.startPreview();
                                                mBtnCaptureImage.setEnabled(true);
                                            }
                                        });


                                AlertDialog alert = builder1.create();
                                alert.show();

                            } else {
                                Log.d(TAG, getString(R.string.not_a_deal_image));
                                com.durgesh.restaurant.app.constant.RToast.showToast(CameraActivity.this, getString(R.string.not_a_food_or_listed_item));
                                finish();
                            }
                        } else {
                            Log.d(TAG, getString(R.string.not_a_food_image));
                            com.durgesh.restaurant.app.constant.RToast.showToast(CameraActivity.this, getString(R.string.not_a_food_or_listed_item));
                            finish();
                        }
                    } else if (response.code() == BAD_GATEWAY) {
                        com.durgesh.restaurant.app.constant.RToast.showToast(CameraActivity.this, getString(R.string.please_try_after_sometime));
                    } else if (response.code() == NOT_FOUND) {
                        com.durgesh.restaurant.app.constant.RToast.showToast(CameraActivity.this, getString(R.string.please_try_after_sometime));
                    }
                }

                @Override
                public void onFailure(Call<ImageCompareResponse> call, Throwable t) {
                    pd.dismiss();
                    com.durgesh.restaurant.app.constant.RToast.showToast(CameraActivity.this, "Image uploading failed");
                    finish();
                }
            });
        } else {
            com.durgesh.restaurant.app.constant.RToast.showLongToast(this, getString(R.string.check_nw_connectivity));
        }

    }

    private String encodeToBase64(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, byteArrayOS);
        Log.v("After compression", "" + bitmap.getByteCount());
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }


    public static boolean saveToFile(byte[] bytes, File file) {
        boolean saved = false;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            saved = true;
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return saved;
    }

}