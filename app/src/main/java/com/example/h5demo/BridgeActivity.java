package com.example.h5demo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.button.MaterialButton;

public class BridgeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge);

        MaterialButton btnImage = findViewById(R.id.btn_image);
        btnImage.setOnClickListener(v -> {
            Intent intent = new Intent(BridgeActivity.this, WebViewActivity.class);
            intent.putExtra("url", "file:///android_asset/bridge.html");
            startActivity(intent);
        });
    }
}