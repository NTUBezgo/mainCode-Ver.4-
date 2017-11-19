package com.ezgo.index;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CloudvisionActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyB45j2r8PKmHKNoMq-S8Lmz4sGY2IrHNBI";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private ImageView mMainImage;
    private ImageView mAnswer_image;
    //開始拍攝按鈕
    private Button photoChoice ;
    //動物介紹按鈕
    private Button introduction ;
    //暫存該題答案的動物名稱
    private String ansAnimal="";
    //
    private String animalPrompt="";

    //-----測試用影像辨識用
    private String message2 = "";
    private TextView CVtest;
    //-----
    private static int visionImg[] ={
            R.drawable.vision_blackbear,R.drawable.vision_deer,
            R.drawable.vision_kookaburra,R.drawable.vision_prairiedog,
            R.drawable.vision_hyena,R.drawable.vision_wolf
    };
    private String[][] animalSet = {{"elephent","wolf","dog","black bear","panda"} , {"大象","灰狼","狗","台灣黑熊","大象",}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudvision);
        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);
        mAnswer_image=(ImageView) findViewById(R.id.answer_image);

        Bundle bundle=getIntent().getExtras();
        animalPrompt=bundle.getString("vision");

        if(animalPrompt.equals("臺灣黑熊")){
            ansAnimal = "blackbear";
            mAnswer_image.setImageResource(visionImg[0]);
        }else if(animalPrompt.equals("山羌")){
            ansAnimal = "deer";
            mAnswer_image.setImageResource(visionImg[1]);
        }else if(animalPrompt.equals("笑翠鳥")){
            ansAnimal = "kookaburra";
            mAnswer_image.setImageResource(visionImg[2]);
        }else if(animalPrompt.equals("黑尾草原犬鼠")){
            ansAnimal = "prairiedog";
            mAnswer_image.setImageResource(visionImg[3]);
        }else if(animalPrompt.equals("斑點鬣狗")){
            ansAnimal = "hyena";
            mAnswer_image.setImageResource(visionImg[4]);
        }else if(animalPrompt.equals("北美灰狼")){
            ansAnimal = "wolf";
            mAnswer_image.setImageResource(visionImg[5]);
        }


        photoChoice = (Button) findViewById(R.id.btn_CV);
        introduction = (Button) findViewById(R.id.btn_NextPage);
        photoChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CloudvisionActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                                mHandler.sendEmptyMessageDelayed(0, 1000); //1秒跳轉
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                                mHandler.sendEmptyMessageDelayed(0, 1000); //1秒跳轉
                            }
                        });
                builder.create().show();
                mHandler.sendEmptyMessageDelayed(1, 7000); //1秒跳轉

            }
        });

        introduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //將正解傳入cloudvisionIntroductionActivity 來顯示動物介紹 並進入下一頁
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putString("from", ansAnimal);
                intent.putExtras(bundle);

                intent.setClass(CloudvisionActivity.this, CVintroActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0:
                    mImageDetails.setText(R.string.loading_message);
                    photoChoice.setVisibility(View.GONE);
                    break;
                case 1:
                    introduction.setVisibility(View.VISIBLE);
                    mImageDetails.setText("加分成功!");
                    break;
                default:
                    break;
            }
        }
    };

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);
                mMainImage.setImageBitmap(bitmap);


            } catch (IOException e) {
                //Log.d(TAG, "Image picking failed because " + e.getMessage());
                //Toast.makeText(this,"BZX", Toast.LENGTH_LONG).show();
            }
        } else {
            //Log.d(TAG, "Image picker gave us a null image.");
            //Toast.makeText(this,"ABC", Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading


        // Do the real work in an async task, because we need to use the network anyway
        AsyncTask<Object, Void, String> label_detection = new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        add(annotateImageRequest);
                        // Add the list of one thing to the request
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    //Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    //Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    //Log.d(TAG, "failed to make API request because of other IOException " +e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                showBtn();
                //mHandler.sendEmptyMessageDelayed(GOTO_LOADING_ACTIVITY, 3000); //1秒跳轉
                /*--------測試用
                AlertDialog ad;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.cloudcv, null);
                CVtest = (TextView) v.findViewById(R.id.CVtest);

                CVtest.setText(message2);

                dialogBuilder.setView(v);
                ad = dialogBuilder.show();
                --------*/

                mImageDetails.setText(result);
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message2 += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message2 += "\n";
                for (int i = 0; i < 5; i++) {
                    if (label.getDescription().indexOf(animalSet[0][i]) != -1 && label.getScore() >= 0.7) {
                        ansAnimal = animalSet[0][i];
                        message = "加分成功 是" + animalSet[0][i];
                        //Log.v("1","ans");
                        return message;
                    } else if (label.getDescription().indexOf(animalSet[0][i]) != -1 && label.getScore() < 0.7) {
                        message = ("加分失敗，您的照片很像" + animalSet[0][i] + "再拍一次吧！");
                        return message;
                    } else {
                        message = "您可能看錯動物囉";
                    }
                }
            }
        }else {
            message += "nothing";
        }
        return message;
    }

    protected void showBtn(){

        if(ansAnimal.isEmpty()){
            photoChoice.setVisibility(View.VISIBLE);
        }else{
            introduction.setVisibility(View.VISIBLE);
        }
    }

}
