package com.company.njupt.lianliankan;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    //游戏难度 1简单 2普通 3困难
    private int level = 1;
    //背景音乐
    private boolean bgm = true;
    //提示音
    private boolean beep = true;
    //震动
    private boolean vibrate = true;

    private Button startBtn;
    private Button levelBtn;
    private Button bgmBtn;
    private Button beepBtn;
    private Button vibrateBtn;
    private Button rankBtn;
    private Button exitBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = findViewById(R.id.startBtn);
        levelBtn = findViewById(R.id.levelBtn);
        bgmBtn = findViewById(R.id.bgmBtn);
        beepBtn = findViewById(R.id.beepBtn);
        vibrateBtn = findViewById(R.id.vibrateBtn);
        rankBtn = findViewById(R.id.rankBtn);
        exitBtn = findViewById(R.id.exitBtn);
        //设置背景音乐
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.bg);
        mp.setLooping(true);
        mp.start();
        //若无排行榜数据文件 则创建
        try {
            File file = new File(getFilesDir().getPath() + "//myrank.txt");
            if(!file.exists()){
                /*
                Toast.makeText(MainActivity.this,
                        "无排行榜文件",
                        Toast.LENGTH_SHORT).show();
                */
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //为按钮绑定点击函数
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, GameActivity.class);  //从MainActivity跳转到GameActivity
                //放入数据
                intent.putExtra("level", level);
                intent.putExtra("bgm", bgm);
                intent.putExtra("beep", beep);
                intent.putExtra("vibrate", vibrate);
                startActivity(intent);  //开始跳转
            }
        });
        levelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = (String) levelBtn.getText();
                if (str.equals("难度：简单"))
                {
                    str = "难度：普通";
                    level = 2;
                }
                else if (str.equals("难度：普通"))
                {
                    str = "难度：困难";
                    level = 3;
                }
                else if (str.equals("难度：困难"))
                {
                    str = "难度：简单";
                    level = 1;
                }
                levelBtn.setText(str);
            }
        });
        bgmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = (String) bgmBtn.getText();
                if (str.equals("音乐：打开"))
                {
                    str = "音乐：关闭";
                    bgm = false;
                    mp.pause();
                }
                else if (str.equals("音乐：关闭"))
                {
                    str = "音乐：打开";
                    bgm = true;
                    mp.start();
                }
                bgmBtn.setText(str);
            }
        });
        beepBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = (String) beepBtn.getText();
                if (str.equals("提示音：打开"))
                {
                    str = "提示音：关闭";
                    beep = false;
                }
                else if (str.equals("提示音：关闭"))
                {
                    str = "提示音：打开";
                    beep = true;
                }
                beepBtn.setText(str);
            }
        });
        vibrateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = (String) vibrateBtn.getText();
                if (str.equals("震动：打开"))
                {
                    str = "震动：关闭";
                    vibrate = false;
                }
                else if (str.equals("震动：关闭"))
                {
                    str = "震动：打开";
                    vibrate = true;
                }
                vibrateBtn.setText(str);
            }
        });
        rankBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RankActivity.class);  //从MainActivity跳转到RankActivity
                startActivity(intent);
            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }
}