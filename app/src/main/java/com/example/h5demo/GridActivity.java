package com.example.h5demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class GridActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        initButtons();
    }

    private void initButtons() {
        int[] buttonIds = {
            R.id.btn_todo,
            R.id.btn_store_select,
            R.id.btn_records,
            R.id.btn_eval_draft,
            R.id.btn_statistics,
            R.id.btn_message,
            R.id.btn_message2,
            R.id.btn_video_settings,
            R.id.btn_store_list,
            R.id.btn_store_manage
        };

        for (int id : buttonIds) {
            MaterialButton button = findViewById(id);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        String url = "http://10.11.66.11:3000/lite-miniapp-h5/index.html#";
//        String url = "https://pb.hik-cloud.com/lite-miniapp-h5/index.html#";
        int id = view.getId();
        String token = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.token_input)).getText().toString();

        if (id == R.id.btn_todo) {
            url += "/todo"+"?token=" + token;
        } else if (id == R.id.btn_store_select) {
            url += "/inspect/onlineEval/storeSelect"+"?token=" + token;
        } else if (id == R.id.btn_records) {
            url += "/records"+"?token=" + token;
        } else if (id == R.id.btn_eval_draft) {
            url += "/inspect/evalDraft"+"?token=" + token;
        } else if (id == R.id.btn_statistics) {
            url += "/inspect/statistics"+"?token=" + token;
        } else if (id == R.id.btn_message) {
            url += "/messageList?type=patrol&token=" + token;
        } else if (id == R.id.btn_message2) {
            url += "/message"+"?token=" + token;
        } else if (id == R.id.btn_video_settings) {
            url += "/mine/videoSettings"+"?token=" + token;
        } else if (id == R.id.btn_store_list) {
            url += "/storeList"+"?token=" + token;
        } else if (id == R.id.btn_store_manage) {
            url += "/store/storeManage"+"?token=" + token;
        }
        url	+=  "&statusBarHeight=0&fromThirdApp=true&thirdType=3";//android传3ios传2
        if (id!=R.id.btn_todo){
            url+="&firstPage=1";
        }
        android.util.Log.d("GridActivity", "Opening URL: " + url);
        
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}