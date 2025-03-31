package com.example.h5demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thirdapph5.WebViewActivity;
import com.google.android.material.card.MaterialCardView;

/**
 * 网格布局Activity，用于显示功能菜单按钮，处理按钮点击事件并跳转到对应的H5页面
 */
public class GridActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        initButtons();
    }

    /**
     * 初始化所有功能按钮并设置点击事件监听器
     */
    private void initButtons() {
        // 定义所有功能按钮的ID数组
        int[] buttonIds = {
                R.id.btn_todo,            // 待办事项
                R.id.btn_store_select,    // 门店选择
                R.id.btn_records,         // 记录
                R.id.btn_eval_draft,      // 评估草稿
                R.id.btn_statistics,      // 统计
                R.id.btn_message,         // 消息（巡检）
                R.id.btn_message2,        // 消息
                R.id.btn_video_settings,  // 视频设置
                R.id.btn_store_list,      // 门店列表
                R.id.btn_store_manage     // 门店管理
        };

        for (int id : buttonIds) {
            MaterialCardView cardView = findViewById(id);
            cardView.setOnClickListener(this);
        }
    }

    /**
     * 处理按钮点击事件，根据不同的按钮ID构建对应的H5页面URL并跳转
     *
     * @param view 被点击的视图对象
     */
    @Override
    public void onClick(View view) {
        // 设置H5页面的基础URL（开发环境）
        String url = "https://pb.hik-cloud.com/lite-miniapp-h5/index.html#";
        String pathName = "myTodo";
        // 生产环境URL
//        String url = "https://pb.hik-cloud.com/lite-miniapp-h5/index.html#";
        int id = view.getId();
        String token = "804ea7b7-b1bb-4b1e-9718-b9dd829c72b8";
        // 根据按钮ID拼接不同的页面路径和参数
        if (id == R.id.btn_todo) {
            url += "/todo" + "?token=" + token;
            pathName = "myTodo";
        } else if (id == R.id.btn_store_select) {
            url += "/inspect/onlineEval/storeSelect" + "?token=" + token;
            pathName = "storeSelect";
        } else if (id == R.id.btn_records) {
            url += "/records" + "?token=" + token;
            pathName = "records";
        } else if (id == R.id.btn_eval_draft) {
            url += "/inspect/evalDraft" + "?token=" + token;
            pathName = "evalDraft";
        } else if (id == R.id.btn_statistics) {
            url += "/inspect/statistics" + "?token=" + token;
            pathName = "statistics";
        } else if (id == R.id.btn_message) {
            url += "/messageList?type=patrol&token=" + token;
            pathName = "messageList";
        } else if (id == R.id.btn_message2) {
            url += "/message" + "?token=" + token;
            pathName = "message";
        } else if (id == R.id.btn_video_settings) {
            url += "/mine/videoSettings" + "?token=" + token;
            pathName = "videoSettings";
        } else if (id == R.id.btn_store_list) {
            url += "/storeList" + "?token=" + token;
            pathName = "storeList";
        } else if (id == R.id.btn_store_manage) {
            url += "/store/storeManage" + "?token=" + token;
            pathName = "storeManage";
        }
        // 添加通用参数：状态栏高度、第三方应用标识和类型（Android为3，iOS为2）
        url += "&statusBarHeight=0&fromThirdApp=true&thirdType=3";
        // 除了待办事项页面外，其他页面都添加firstPage参数
        if (id != R.id.btn_todo) {
            url += "&firstPage=1";
        }
        android.util.Log.d("GridActivity", "Opening URL: " + url);

        // 创建Intent并携带URL参数跳转到WebView页面
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", "https://pb.hik-cloud.com/safe-center/index.html#/login/single?client_id=155e8cf61ed84578998fc6e7b2ba309e&response_type=code&state=STATE&menu_uri=3&redirect_uri=http%3A%2F%2F10.11.66.11%3A3000%2Flite-miniapp-h5%2Findex.html%23%2Flogin%3FstatusBarHeight%3D0%26fromThirdApp%3Dtrue%26thirdType%3D3");
        startActivity(intent);
    }
}