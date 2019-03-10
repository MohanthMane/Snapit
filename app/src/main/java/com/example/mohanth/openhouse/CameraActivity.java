package com.example.mohanth.openhouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.Random;

import static android.os.SystemClock.sleep;

public class CameraActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private ImageView captureButton;
    private ImageView toggle;
    private ImageView playButton;
    private FirebaseVisionImage image;
    private int count;
    private String randomObject;
    private int points;
    private TextView score;
    private boolean playPressed;
    private objectsHolder objects;
    private TextView question;
    private ArrayList<String> results = new ArrayList<String>();


    private AlertDialog alertDialog;

    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, byte[] bytes) {

                    count++;

                    // decoding the byte array to bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                    // converting to Firebase image
                    image = FirebaseVisionImage.fromBitmap(bitmap);

                    // setting the minimum confidence level to 1 percent
                    FirebaseVisionOnDeviceImageLabelerOptions options = new
                            FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                            .setConfidenceThreshold(0.4f)
                            .build();

                    // Creating the labeler object
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                         .getOnDeviceImageLabeler(options);

                    // passing the captured image to the labeler
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

                                    if(playPressed) {

                                        boolean detected = process();

                                        if (detected) {
                                            points++;
                                            score.setText("Score : " + points);
                                            setRandomQuestion();
                                            Toast.makeText(CameraActivity.this, "Good job",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            setRandomQuestion();
                                        }

                                        if (count == 10) {
                                            resetGame();
                                        }
                                        // An intent that shows all the tags identified by the labeler
    //                                    Intent intent = new Intent(CameraActivity.this,ShowImage.class);
    //                                    intent.putStringArrayListExtra("RESULT",results);
    //                                    startActivity(intent);
                                }
                                }
                            })

                            // In case of any failure... possibly never happens
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CameraActivity.this
                                            ,"could not recognize",Toast.LENGTH_LONG).show();
                                }
                            });

                }
            });
        }
    };

    private void resetGame() {
        alertDialog.setMessage("Score : " + points);
        alertDialog.show();
        count = 0;
        points = 0;
        playButton.setAlpha(1f);
        score.setText("Score : "+ points);
        question.setText("press play button");
    }

    private boolean process() {
        for (int i = 0; i < results.size(); i++) {
            if(results.get(i).toLowerCase().equals(randomObject.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Instantiating all the objects
        count = 0;
        points = 0;
        playPressed = false;
        cameraKitView = (CameraKitView) findViewById(R.id.camera);
        captureButton = (ImageView) findViewById(R.id.capture);
        toggle = (ImageView) findViewById(R.id.toggle);
        playButton = (ImageView) findViewById(R.id.play);
        score = (TextView) findViewById(R.id.score);
        question = (TextView) findViewById(R.id.displayQuestion);
        objects = new objectsHolder();
        alertDialog = new AlertDialog.Builder(CameraActivity.this).create();


        alertDialog.setTitle("Game over!");
        alertDialog.setMessage("Score : " + points);
        alertDialog.setCancelable(true);

        // Switching the face of the camera
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.toggleFacing();
            }
        });

        // Game Starts now
        playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPressed = true;
                        if(count==0) {
                            playButton.setAlpha(0f);
                            setRandomQuestion();
                        }
                    }
                });

        // Captures the photo
        captureButton.setOnClickListener(photoOnClickListener);

    }

    private void setRandomQuestion() {
        Random rnd = new Random();
        int idx = (int) rnd.nextInt(objects.objectNames.size())+1;
        Log.e("Selected index",Integer.toString(idx));
        randomObject = objects.objectNames.get(idx);
        question.setText((count+1) + " : " + randomObject);
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