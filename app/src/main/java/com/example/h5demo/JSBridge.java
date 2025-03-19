package com.example.h5demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JSBridge {
    private Activity activity;
    public static final int PERMISSION_REQUEST_CODE = 100;
    public static final int PICK_IMAGE_REQUEST = 1;

    public JSBridge(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void chooseImage() {
        activity.runOnUiThread(() -> {
            if (checkPermission()) {
                openGallery();
            } else {
                requestPermission();
            }
        });
    }

    @JavascriptInterface
    public void dispatchMessage(String message) {
        Log.e("xxxxxxxxDispatchMessage", message);
        String callbackId = "";
        try {
            JSONObject jsonObject = new JSONObject(message);
            String apiName = jsonObject.getString("apiName");
            Object params = jsonObject.opt("params");
            callbackId = jsonObject.optString("callbackId");
            if ("closeWindow".equals(apiName)) {
                activity.finish();
            }
        } catch (Exception e) {

        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void handleImageResult(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

            // 调用JavaScript函数，传递base64图片数据
            if (activity instanceof WebViewActivity) {
                WebViewActivity webViewActivity = (WebViewActivity) activity;
                webViewActivity.getWebView().post(() -> {
                    // 移除可能的换行符和空格，确保base64字符串格式正确
                    String cleanBase64 = base64Image.replaceAll("\\s+", "");
                    String jsCode = String.format("javascript:onImageSelected('%s')", cleanBase64);
                    webViewActivity.getWebView().loadUrl(jsCode);
                    activity.runOnUiThread(() ->
                            Toast.makeText(activity, "图片加载成功", Toast.LENGTH_SHORT).show()
                    );
                });
            }

            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Failed to load image", Toast.LENGTH_SHORT).show()
            );
        }
    }
}