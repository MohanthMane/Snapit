package com.example.mohanth.openhouse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowImage extends AppCompatActivity {

    private ArrayList<String> res = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        res.add("None as of now");

        Bundle extras = getIntent().getExtras();

        if(extras!=null) {
            res = extras.getStringArrayList("RESULT");
        }

        ListView listView = findViewById(R.id.listview);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShowImage.this
                ,android.R.layout.simple_list_item_1,res);

        listView.setAdapter(arrayAdapter);

    }
}
