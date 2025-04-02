/**
 * JavaScript桥接器类
 * 负责处理Web页面与原生Android之间的通信
 * 提供权限管理、消息分发、JavaScript回调等核心功能
 */
package com.example.thirdapph5;

import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaScript桥接器类
 * 管理Web与Android原生代码之间的交互
 */
public class JSBridge {
    private final WebViewActivity activity;
    public static final int PERMISSION_REQUEST_CODE = 100;
    public static final int PICK_IMAGE_REQUEST = 1;
    private final Map<String, Integer> PermissionMap = new HashMap<>();
    private int nextIntValue = 100;

    /**
     * 将权限字符串转换为对应的整数值
     * @param str 权限字符串
     * @return 对应的整数值，如果不存在则创建新的值
     */
    public int convertStringToInt(String str) {
        //KEY 是permisson value是对应申请权限的requestCode
        // 如果字符串已经存在于映射中，直接返回对应的整数值
        if (PermissionMap.containsKey(str)) {
            return PermissionMap.get(str);
        }

        // 如果字符串不存在于映射中，分配一个新的整数值
        PermissionMap.put(str, nextIntValue);
        nextIntValue++;

        return PermissionMap.get(str);
    }

    /**
     * 构造函数
     * @param activity WebView所在的Activity实例
     */
    public JSBridge(WebViewActivity activity) {
        this.activity = activity;
    }

    /**
     * JavaScript接口方法，用于处理来自Web页面的消息
     * @param message JSON格式的消息字符串，包含apiName、params和callbackId
     */
    @JavascriptInterface
    public void dispatchMessage(String message) {
        String callbackId;
        try {
            JSONObject jsonObject = new JSONObject(message);
            String apiName = jsonObject.getString("apiName");
            Object params = jsonObject.opt("params");
            callbackId = jsonObject.optString("callbackId");
            switch (apiName) {
                case "closeWindow":
                    activity.finish();
                    break;
                case "requestPermission":
                    requestPermission(params, callbackId);
                    break;
                case "exitApp":
                    activity.finish();
                    break;
                default:
                    Log.e("DispatchMsg not impl", message);
                    break;
            }
        } catch (Exception e) {
            Log.e("dispatchMsg---Exception", e.toString());
        }
    }

    /**
     * 处理权限请求
     * @param params 权限参数
     * @param callbackId 回调ID，用于向JavaScript返回结果
     */
    private void requestPermission(Object params, String callbackId) {
        if (params != null) {
            String permission = params.toString();
            activity.runOnUiThread(() -> {
                boolean isGet = checkPermission(permission);
                if (isGet) {
                    //有权限直接回调给js
                    replyJs(callbackId, 200, true);
                } else {
                    //没有权限请求原生权限
                    requestPermission(permission, convertStringToInt(callbackId));
                }

            });

        }
    }

    /**
     * 向JavaScript回调结果
     * @param callbackId 回调ID
     * @param code 状态码
     * @param data 返回数据
     */
    private void replyJs(String callbackId, int code, Object data) {
        if (callbackId.isEmpty()) {
            return;
        }
        JSONObject replyValue = new JSONObject();
        try {
            replyValue.put("code", code);
            if (data != null) {
                replyValue.put("body", data);
            }
            String replyString = replyValue.toString();
            activity.runOnUiThread(() -> {
                StringBuffer buffer = new StringBuffer();
                buffer.append("handleYMAppBridgeCallback(");
                buffer.append("'").append(callbackId).append("'");
                buffer.append(",");
                buffer.append(replyString);
                buffer.append(")");
                activity.getWebView().evaluateJavascript(buffer.toString(), null);
            });
        } catch (JSONException e) {
            Log.e("replyJs---Exception", e.toString());
        }
    }

    /**
     * 检查是否已获得指定权限
     * @param permission 权限名称
     * @return 是否已获得权限
     */
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求指定权限
     * @param permission 权限名称
     * @param requestCode 请求码
     */
    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }
}