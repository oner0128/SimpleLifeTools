package com.hrong.simplelifetools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.afollestad.materialcamera.MaterialCamera;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        new MaterialCamera(this).stillShot()
                .start(8989);
    }
}
