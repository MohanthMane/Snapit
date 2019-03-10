package com.example.mohanth.openhouse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import java.util.ArrayList;
import java.util.List;

public class DetectionActivity extends AppCompatActivity {

    private ImageView capture;
    private ImageView flipcamera;
    private FirebaseVisionImage image;
    private CameraKitView cameraKitView;
    private ArrayList<String> results = new ArrayList<String>();


    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, byte[] bytes) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    image = FirebaseVisionImage.fromBitmap(bitmap);


                    FirebaseVisionOnDeviceImageLabelerOptions options = new
                            FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                            .setConfidenceThreshold(0.6f)
                            .build();

                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                            .getOnDeviceImageLabeler(options);



                    labeler.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                                    results.clear();

                                    // Storing results in an arraylist
                                    for (FirebaseVisionImageLabel label : firebaseVisionImageLabels) {
                                        String text = label.getText();
                                        String objectID = label.getEntityId();
                                        float confidence = label.getConfidence();
                                        results.add(text);

                                    }


                                        // An intent that shows all the tags identified by the labeler
                                         Intent intent = new Intent(DetectionActivity.this,ShowImage.class);
                                         intent.putStringArrayListExtra("RESULT",results);
                                         startActivity(intent);

                                }
                            })

                            // In case of any failure... possibly never happens
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DetectionActivity.this
                                            ,"could not recognize",Toast.LENGTH_LONG).show();
                                }
                            });


                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        capture = (ImageView) findViewById(R.id.capture);
        flipcamera = (ImageView) findViewById(R.id.toggle);
        cameraKitView = (CameraKitView) findViewById(R.id.camera);

        flipcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.toggleFacing();
            }
        });

        capture.setOnClickListener(photoOnClickListener);
    }


    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraKitView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraKitView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


}
