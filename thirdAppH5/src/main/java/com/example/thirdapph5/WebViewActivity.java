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

/**
 * WebView容器Activity
 * 负责管理WebView的生命周期、权限请求和JavaScript桥接
 * 提供文件选择、地理位置等原生功能支持
 */
public class WebViewActivity extends AppCompatActivity {
    private static final String BRIDGE_NAME = "YMBridge";
    private CustomWebView webView;
    private JSBridge jsBridge;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    /**
     * 获取应用所需的权限列表
     * 根据Android系统版本返回相应的权限数组
     * @return 权限字符串数组
     */
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

    /**
     * Activity创建时的回调方法
     * 初始化WebView和JSBridge，设置页面布局
     * @param savedInstanceState 保存的实例状态
     */
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

    /**
     * 初始化JavaScript桥接
     * 创建JSBridge实例并注册到WebView中
     */
    private void initJSBridge() {
        jsBridge = new JSBridge(this);
        webView.removeJavascriptInterface(BRIDGE_NAME);
        webView.addJavascriptInterface(jsBridge, BRIDGE_NAME);
    }

    /**
     * 获取WebView实例
     * @return CustomWebView实例
     */
    public CustomWebView getWebView() {
        return webView;
    }

    /**
     * 处理Activity结果回调
     * 主要用于处理文件选择的结果
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回的数据
     */
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

    /**
     * 处理返回键事件
     * 如果WebView可以返回则返回上一页，否则退出Activity
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 检查并请求必要的权限
     * 包括相机、存储、位置等权限
     */
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

    /**
     * 处理权限请求的结果
     * @param requestCode 权限请求码
     * @param permissions 权限数组
     * @param grantResults 授权结果数组
     */
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

    /**
     * 显示权限说明对话框
     * 当用户拒绝权限时，提示用户前往设置页面手动授权
     */
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