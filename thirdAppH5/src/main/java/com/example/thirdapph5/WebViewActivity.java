package com.example.thirdapph5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.widget.Toast;
import android.app.AlertDialog;
import android.provider.Settings;

public class WebViewActivity extends AppCompatActivity {
    private static final String BRIDGE_NAME = "YMBridge";
    private CustomWebView webView;
    private JSBridge jsBridge;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private String[] getRequiredPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return new String[] {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        } else {
            return new String[] {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webview);
        initJSBridge();

        // 从Intent中获取URL
        String url = getIntent().getStringExtra("url");
        webView.setUrl(url != null ? url : "https://www.baidu.com");
    }

    private void initJSBridge() {
        jsBridge = new JSBridge(this);
        webView.removeJavascriptInterface(BRIDGE_NAME);
        webView.addJavascriptInterface(jsBridge, BRIDGE_NAME);
    }

    public CustomWebView getWebView() {
        return webView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CustomWebView.FILE_CHOOSER_RESULT_CODE) {
            ValueCallback<Uri[]> callback = webView.getFilePathCallback();
            if (callback != null) {
                Uri[] results = null;
                if (resultCode == RESULT_OK && data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
                callback.onReceiveValue(results);
                webView.setFilePathCallback(null); // 直接设置为null，避免重复调用
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void checkAndRequestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String[] permissions = getRequiredPermissions();
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                showPermissionDialog();
            }
        }
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("权限申请")
                .setMessage("为了保证应用正常运行，请授予必要权限")
                .setPositiveButton("去设置", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
    }
}