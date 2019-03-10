package com.example.mohanth.openhouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button gameMode;
    private Button multiplayer;
    private Button detectionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameMode = (Button) findViewById(R.id.game);
        multiplayer = (Button) findViewById(R.id.multiplayer);
        detectionMode = (Button) findViewById(R.id.detect);


        gameMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                startActivity(intent);
            }
        });

        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Coming Soon!"
                        ,Toast.LENGTH_LONG).show();
            }
        });


        detectionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DetectionActivity.class);
                startActivity(intent);
            }
        });

    }
}
