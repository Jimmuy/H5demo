package com.example.h5demo;

import static com.example.h5demo.JSBridge.PICK_IMAGE_REQUEST;

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
    private static  final String  BRIDGE_NAME= "YMBridge";
    private CustomWebView webView;
    private JSBridge jsBridge;
    public static final int FILE_CHOOSER_RESULT_CODE = 1;

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
//        checkAndRequestPermissions();
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
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
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
            for (String permission : getRequiredPermissions()) {
                // 跳过后台定位权限的检查，稍后单独处理
                if (permission.equals(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    continue;
                }
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        // 用户之前拒绝过这个权限，显示解释
                        new AlertDialog.Builder(this)
                            .setTitle("权限申请")
                            .setMessage("需要" + getPermissionName(permission) + "权限来保证应用正常运行")
                            .setPositiveButton("确定", (dialog, which) -> {
                                requestPermissions(new String[]{permission}, PERMISSIONS_REQUEST_CODE);
                            })
                            .setNegativeButton("取消", null)
                            .show();
                    } else {
                        requestPermissions(new String[]{permission}, PERMISSIONS_REQUEST_CODE);
                    }
                    return; // 一次只请求一个权限
                }
            }
            
            // 所有基本权限都已获取
        }
    }

    private String getPermissionName(String permission) {
        switch (permission) {
            case android.Manifest.permission.CAMERA:
                return "相机";
            case android.Manifest.permission.READ_EXTERNAL_STORAGE:
            case android.Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case android.Manifest.permission.READ_MEDIA_IMAGES:
                return "存储";
            case android.Manifest.permission.ACCESS_FINE_LOCATION:
            case android.Manifest.permission.ACCESS_COARSE_LOCATION:
            case android.Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                return "位置";
            default:
                return "";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //回调给JS去查找是不是自己的requestCode对应再处理权限请求结果
        jsBridge.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被授予，继续检查其他权限
//                    checkAndRequestPermissions();
                } else {
                    // 权限被拒绝
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        // 用户选择了"不再询问"，显示引导对话框
                        showSettingsDialog(getPermissionName(permissions[0]));
                    } else {
                        // 用户拒绝了权限但没有选择"不再询问"
                        Toast.makeText(this, "需要" + getPermissionName(permissions[0]) + "权限来保证应用正常运行", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void showSettingsDialog(String permissionName) {
        new AlertDialog.Builder(this)
            .setTitle("权限申请")
            .setMessage("需要" + permissionName + "权限来保证应用正常运行，请前往设置页面手动开启权限")
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