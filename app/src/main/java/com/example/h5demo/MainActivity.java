package com.example.h5demo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String defaultUrl = "http://10.11.66.116:3001/lite-miniapp-h5/index.html#/workbench?" +
            "token=08ab312e-d378-4175-9c6b-29ab2589b18b&statusBarHeight=38&firstPage=1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton btnOpenWebview = findViewById(R.id.btn_open_webview);

        TextInputEditText inputEditText = findViewById(R.id.et_url);
        inputEditText.setText(defaultUrl);
        btnOpenWebview.setOnClickListener(v -> {
            String url = Objects.requireNonNull(inputEditText.getText()).toString().trim();
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });

        MaterialButton btnGrid = findViewById(R.id.btn_grid);
        btnGrid.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GridActivity.class);
            startActivity(intent);
        });

        MaterialButton btnJsBridge = findViewById(R.id.btn_jsbridge);
        btnJsBridge.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BridgeActivity.class);
            startActivity(intent);
        });
    }
}