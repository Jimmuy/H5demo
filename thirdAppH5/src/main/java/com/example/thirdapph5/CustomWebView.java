package com.example.thirdapph5;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.net.Uri;

/**
 * 自定义WebView组件，继承自WebView
 * 提供了文件选择、地理位置、SSL证书处理等功能的封装
 * 支持JavaScript交互和各种Web特性
 */
public class CustomWebView extends WebView {
    private static final String DEFAULT_URL = "https://www.baidu.com";
    private String currentUrl = DEFAULT_URL;
    private ValueCallback<Uri[]> filePathCallback;
    public static final int FILE_CHOOSER_RESULT_CODE = 1;

    /**
     * 单参数构造方法
     * @param context 上下文环境
     */
    public CustomWebView(Context context) {
        this(context, null);
    }

    /**
     * 双参数构造方法
     * @param context 上下文环境
     * @param attrs 属性集合
     */
    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化WebView配置
     * 启用JavaScript、DOM存储、文件访问等功能
     * 配置缓存、地理位置和安全设置
     */
    private void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(getContext().getFilesDir().getPath());
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(getContext().getFilesDir().getAbsolutePath());
        // 设置自定义UserAgent
        String originalUserAgent = settings.getUserAgentString();
        settings.setUserAgentString(originalUserAgent + "ThirdApp");
        
        // 配置WebViewClient，处理页面加载、错误和SSL证书等事件
        setWebViewClient(new WebViewClient() {
            /**
             * 页面加载完成的回调方法
             * @param view WebView实例
             * @param url 加载完成的页面URL
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 页面加载完成后的回调
            }
            
            /**
             * 页面加载错误的回调方法
             * @param view WebView实例
             * @param errorCode 错误代码
             * @param description 错误描述
             * @param failingUrl 失败的URL
             */
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 加载错误时的回调
            }

            /**
             * SSL证书错误的回调方法
             * @param view WebView实例
             * @param handler SSL错误处理器
             * @param error SSL错误信息
             */
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.e("SSL error",error.toString());
                handler.proceed();
            }
        });

        // 配置WebChromeClient，处理文件选择和地理位置权限等功能
        setWebChromeClient(new WebChromeClient() {

            /**
             * 处理文件选择功能
             * @param webView WebView实例
             * @param filePathCallback 文件选择回调接口
             * @param fileChooserParams 文件选择参数
             * @return 是否成功处理文件选择请求
             */
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                CustomWebView.this.filePathCallback = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    ((Activity) getContext()).startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
                } catch (ActivityNotFoundException e) {
                    filePathCallback.onReceiveValue(null);
                    return false;
                }
                return true;
            }

            /**
             * 处理地理位置权限请求
             * @param origin 请求地理位置的网页来源
             * @param callback 权限回调接口
             */
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        loadUrl(currentUrl);
    }

    /**
     * 设置并加载指定URL
     * @param url 要加载的URL地址
     */
    public void setUrl(String url) {
        if (url != null && !url.isEmpty()) {
            currentUrl = url;
            loadUrl(currentUrl);
        }
    }

    /**
     * 获取文件选择回调接口
     * @return 文件选择回调接口
     */
    public ValueCallback<Uri[]> getFilePathCallback() {
        return filePathCallback;
    }

    /**
     * 设置文件选择回调接口
     * @param callback 文件选择回调接口
     */
    public void setFilePathCallback(ValueCallback<Uri[]> callback) {
        this.filePathCallback = callback;
    }
}