package com.gjiazhe.springrecyclerview.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openVerticalSample(View view) {
        startActivity(new Intent(this, VerticalActivity.class));
    }

    public void openHorizontalSample(View view) {
        startActivity(new Intent(this, HorizontalActivity.class));
    }

    public void openStaggeredGridLayoutSample(View view) {
        startActivity(new Intent(this, StaggeredGridLayoutActivity.class));
    }
}
