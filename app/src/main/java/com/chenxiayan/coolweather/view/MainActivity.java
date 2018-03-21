package com.chenxiayan.coolweather.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chenxiayan.coolweather.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /***
         * 已经初始选择了地方 就不用选择省市了
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather1",null) != null){
            Intent intent = new Intent(this,weatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
